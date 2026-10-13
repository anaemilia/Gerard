import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Carga multiusuário: N visitantes simultâneos, cada um com o seu cookie de sessão, percorrendo o fluxo real
 * (sorteio, classificação até acertar, soltura dos números conhecidos, reinício) com pausas de "pensar". Confere
 * também o isolamento: o estado devolvido a cada visitante tem que ser o da situação que ele mesmo sorteou.
 *
 * Uso: java CargaMultiusuarioGerard.java [--usuarios=100] [--segundos=60] [--pensar-min-ms=1000]
 *      [--pensar-max-ms=3000] [--base-url=http://127.0.0.1:8080]
 */
public final class CargaMultiusuarioGerard {
    static final List<String> CATEGORIAS = List.of("COMPOSICAO_MEDIDAS", "TRANSFORMACAO_MEDIDAS",
            "COMPARACAO_MEDIDAS");
    static final Pattern SITUACAO = Pattern.compile("\"situacao_id\":\"([^\"]+)\"");
    static final Pattern TENTATIVA = Pattern.compile("\"tentativa_id\":\"([^\"]+)\"");
    static final Pattern CORRETA = Pattern.compile("\"correta\":(true|false)");

    static final List<Long> latencias = new CopyOnWriteArrayList<Long>();
    static final ConcurrentHashMap<String, List<Long>> porRota = new ConcurrentHashMap<String, List<Long>>();
    static final AtomicInteger erros = new AtomicInteger();
    static final AtomicInteger cruzados = new AtomicInteger();
    static final AtomicInteger sessoesConcluidas = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        int usuarios = 100;
        int segundos = 60;
        long pensarMin = 1000;
        long pensarMax = 3000;
        String base = "http://127.0.0.1:8080"; // IP: o CookieManager do JDK não reenvia cookie para "localhost"
        for (String a : args) {
            if (a.startsWith("--usuarios=")) usuarios = Integer.parseInt(a.substring(11));
            else if (a.startsWith("--segundos=")) segundos = Integer.parseInt(a.substring(11));
            else if (a.startsWith("--pensar-min-ms=")) pensarMin = Long.parseLong(a.substring(16));
            else if (a.startsWith("--pensar-max-ms=")) pensarMax = Long.parseLong(a.substring(16));
            else if (a.startsWith("--base-url=")) base = a.substring(11);
        }
        final long fim = System.currentTimeMillis() + segundos * 1000L;
        final String baseFinal = base;
        final long pMin = pensarMin;
        final long pMax = pensarMax;
        CountDownLatch pronto = new CountDownLatch(usuarios);
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < usuarios; i++) {
            Thread t = new Thread(() -> {
                try {
                    visitante(baseFinal, fim, pMin, pMax);
                } catch (Exception e) {
                    erros.incrementAndGet();
                    System.err.println("visitante falhou: " + e);
                } finally {
                    pronto.countDown();
                }
            });
            threads.add(t);
            t.start();
        }
        pronto.await();
        List<Long> ordenadas = new ArrayList<Long>(latencias);
        Collections.sort(ordenadas);
        System.out.println("usuarios=" + usuarios + " segundos=" + segundos + " chamadas=" + ordenadas.size()
                + " erros=" + erros.get() + " julgamentos_cruzados=" + cruzados.get()
                + " sessoes_concluidas=" + sessoesConcluidas.get());
        if (!ordenadas.isEmpty()) {
            System.out.println("latencia_ms p50=" + pct(ordenadas, 50) + " p95=" + pct(ordenadas, 95)
                    + " p99=" + pct(ordenadas, 99) + " max=" + ordenadas.get(ordenadas.size() - 1)
                    + " vazao=" + String.format("%.1f", ordenadas.size() / (double) segundos) + " req/s");
        }
        for (String rota : new java.util.TreeSet<String>(porRota.keySet())) {
            List<Long> l = new ArrayList<Long>(porRota.get(rota));
            Collections.sort(l);
            System.out.println(String.format("  %-34s n=%-5d p50=%-5d p95=%-5d max=%d", rota, l.size(), pct(l, 50),
                    pct(l, 95), l.get(l.size() - 1)));
        }
        System.exit(erros.get() == 0 && cruzados.get() == 0 ? 0 : 1);
    }

    static long pct(List<Long> ordenada, int p) {
        return ordenada.get(Math.min(ordenada.size() - 1, (int) Math.ceil(ordenada.size() * p / 100.0) - 1));
    }

    static String chamar(HttpClient http, String base, String metodo, String caminho, String corpo) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(base + caminho)).timeout(Duration.ofSeconds(30));
        if ("GET".equals(metodo)) {
            b.GET();
        } else {
            b.header("Content-Type", "application/json").method(metodo, BodyPublishers.ofString(corpo == null ? "{}" : corpo));
        }
        long t0 = System.nanoTime();
        HttpResponse<String> r = http.send(b.build(), BodyHandlers.ofString());
        long ms = (System.nanoTime() - t0) / 1_000_000;
        latencias.add(ms);
        porRota.computeIfAbsent(metodo + " " + caminho, k -> new CopyOnWriteArrayList<Long>()).add(ms);
        if (r.statusCode() >= 500) {
            erros.incrementAndGet();
        }
        return r.body();
    }

    static void pensar(long min, long max) throws InterruptedException {
        Thread.sleep(min + ThreadLocalRandom.current().nextLong(Math.max(1, max - min)));
    }

    static void visitante(String base, long fim, long pMin, long pMax) throws Exception {
        HttpClient http = HttpClient.newBuilder().cookieHandler(new CookieManager())
                .connectTimeout(Duration.ofSeconds(5)).build();
        Thread.sleep(ThreadLocalRandom.current().nextLong(2000));
        chamar(http, base, "GET", "/api/v1/sessao/usuario", null);
        while (System.currentTimeMillis() < fim) {
            String estado = chamar(http, base, "POST", "/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}");
            Matcher idT = TENTATIVA.matcher(estado);
            String tentativa = idT.find() ? idT.group(1) : null;
            Matcher m = SITUACAO.matcher(estado);
            if (!m.find()) {
                erros.incrementAndGet();
                continue;
            }
            String minha = m.group(1);
            pensar(pMin, pMax);
            boolean acertou = false;
            Matcher tipo = Pattern.compile("^PO_([A-Z_]+?)_[a-z]").matcher(minha);
            tipo.find();
            List<String> ordem = new ArrayList<String>(CATEGORIAS);
            Collections.shuffle(ordem);
            ordem.remove(tipo.group(1));
            ordem.add(0, tipo.group(1)); // como quem conhece o domínio: acerta de primeira
            for (String categoria : ordem) {
                String r = chamar(http, base, "POST", "/api/v1/tentativas/" + tentativa + "/classificacao",
                        "{\"categoria\":\"" + categoria + "\"}");
                Matcher c = CORRETA.matcher(r);
                if (c.find() && "true".equals(c.group(1))) {
                    acertou = true;
                    if (!minha.contains("_" + categoria + "_") && !minha.startsWith("PO_" + categoria + "_")) {
                        cruzados.incrementAndGet();
                    }
                    Matcher s = SITUACAO.matcher(r);
                    if (s.find() && !s.group(1).equals(minha)) {
                        cruzados.incrementAndGet();
                    }
                    break;
                }
                pensar(pMin, pMax);
                chamar(http, base, "POST", "/api/v1/tentativas/" + tentativa + "/classificacao/confirmacao", "{\"concordou\":false}");
            }
            if (!acertou) {
                continue;
            }
            pensar(pMin, pMax);
            String estadoModelagem = chamar(http, base, "POST", "/api/v1/tentativas/" + tentativa + "/reinicio", "{}");
            Matcher s = SITUACAO.matcher(estadoModelagem);
            if (s.find() && !s.group(1).equals(minha)) {
                cruzados.incrementAndGet();
            }
            sessoesConcluidas.incrementAndGet();
            pensar(pMin, pMax);
        }
    }
}
