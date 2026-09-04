import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Suite de benchmark de latência/throughput para as rotas HTTP de
 * gerard.infraestrutura.web.ServidorPrototipoWeb, medindo USO REALISTA —
 * não carga sintética.
 *
 * Cada "sessão simulada" percorre, do início ao fim, o fluxo real de um
 * adulto (professor/pesquisador) resolvendo UMA situação-problema:
 *   GET /           (carrega a página)
 *   GET /api/situacao                     [pensar: ler enunciado]
 *   POST /api/sorteios/{medidas|relacoes} [pensar: ler novo enunciado / decidir categoria]
 *   (POST /api/classificacao/categoria → se errar: pensar, confirmacao, pensar, tenta de novo —
 *    o espaço de categorias do grupo (3) é embaralhado e todo esgotado até acertar, então a
 *    sessão SEMPRE termina classificando certo, como um usuário que conhece o domínio)
 *   [pensar: manipular o diagrama]
 *   POST /api/gestos → POST /api/acoes/posicionar (ou .../escolher-operacao)  [pensar: ver feedback]
 *   (repete posicionar/escolher-operação 1–2x, como alguém ajustando a resposta)
 *   25% de chance: [pensar] POST /api/reiniciar
 *
 * As pausas ("pensar") são sorteadas numa faixa configurável (padrão 2–8s) —
 * é isso que torna a sessão realista em vez de um martelamento de rota.
 *
 * Duas baterias são rodadas: sessões SEQUENCIAIS (um usuário de cada vez,
 * o caso normal, já que o servidor guarda uma única "situação" ativa em
 * memória — não é multiusuário) e um número pequeno de sessões em
 * PARALELO (2–3, "alguns usuários ao mesmo tempo"), nunca dezenas/centenas
 * de workers artificiais.
 *
 * O relatório separa claramente:
 *   (1) latência por rota — só o tempo de resposta do servidor, chamada a chamada;
 *   (2) tempo total por sessão — da 1ª à última chamada, INCLUINDO as pausas simuladas;
 *   (3) tempo de servidor por sessão — soma só das chamadas HTTP daquela sessão, sem pausas.
 * (2) e (3) nunca são combinados numa única média.
 *
 * Standalone de propósito: compila só com {@code javac BenchmarkApiGerard.java}
 * (java.net.http é JDK padrão, zero dependência nova). Sem URL base informada,
 * sobe o servidor real como processo separado numa porta dedicada.
 *
 * Uso:
 *   javac BenchmarkApiGerard.java
 *   java BenchmarkApiGerard [--sessions=40] [--parallel-sessions=3]
 *                           [--think-min-ms=2000] [--think-max-ms=8000]
 *                           [--base-url=http://localhost:8080]
 *   (equivalentes em variável de ambiente: GERARD_BENCH_BASE_URL,
 *    GERARD_BENCH_SESSIONS, GERARD_BENCH_PARALLEL_SESSIONS,
 *    GERARD_BENCH_THINK_MIN_MS, GERARD_BENCH_THINK_MAX_MS)
 */
public final class BenchmarkApiGerard {

    private static final List<String> GRUPO_MEDIDAS = Arrays.asList(
            "COMPOSICAO_MEDIDAS", "TRANSFORMACAO_MEDIDAS", "COMPARACAO_MEDIDAS");
    private static final List<String> GRUPO_RELACOES = Arrays.asList(
            "TRANSFORMACAO_RELACAO", "COMPOSICAO_RELACOES", "COMPOSICAO_TRANSFORMACOES");

    public static void main(String[] args) throws Exception {
        Config cfg = Config.ler(args);
        Process servidorProprio = null;
        try {
            if (cfg.baseUrlFornecida == null) {
                servidorProprio = subirServidor(cfg.porta, cfg.raizProjeto);
            }
            HttpClient http = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            aguardarServidorPronto(http, cfg.baseUrl());

            System.out.println("Base URL: " + cfg.baseUrl());
            System.out.println("Sessões sequenciais: " + cfg.sessoesSequenciais
                    + "  |  Sessões em paralelo: " + cfg.sessoesParalelas
                    + "  |  Think-time: " + cfg.thinkMinMs + "-" + cfg.thinkMaxMs + " ms");
            System.out.println();

            Sessao sessao = new Sessao(http, cfg.baseUrl());

            System.out.println("Rodando " + cfg.sessoesSequenciais + " sessões sequenciais...");
            List<SessaoResultado> sequenciais = new ArrayList<SessaoResultado>();
            for (int i = 0; i < cfg.sessoesSequenciais; i++) {
                sequenciais.add(executarSessao(sessao, i, cfg));
                if ((i + 1) % 10 == 0) System.out.println("  ... " + (i + 1) + "/" + cfg.sessoesSequenciais);
            }

            System.out.println("Rodando " + cfg.sessoesParalelas + " sessões em paralelo...");
            List<SessaoResultado> paralelas = executarSessoesParalelas(sessao, cfg);

            System.out.println();
            String relatorioMd = gerarRelatorioMarkdown(cfg, sequenciais, paralelas);
            System.out.println(relatorioMd);

            Path dirResultados = cfg.raizProjeto.resolve("scripts/benchmark/results");
            Files.createDirectories(dirResultados);
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                    .format(java.time.LocalDateTime.now());
            Path arquivoJson = dirResultados.resolve("benchmark-" + timestamp + ".json");
            Path arquivoMd = dirResultados.resolve("benchmark-" + timestamp + ".md");
            Files.write(arquivoJson, gerarJsonBruto(cfg, sequenciais, paralelas).getBytes(StandardCharsets.UTF_8));
            Files.write(arquivoMd, relatorioMd.getBytes(StandardCharsets.UTF_8));
            System.out.println("Dados brutos: " + arquivoJson);
            System.out.println("Relatório:    " + arquivoMd);
        } finally {
            if (servidorProprio != null) {
                servidorProprio.destroy();
                servidorProprio.waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    // ------------------------------------------------------------------
    // Configuração
    // ------------------------------------------------------------------

    private static final class Config {
        String baseUrlFornecida;
        int porta = 18099;
        int sessoesSequenciais = 40;
        int sessoesParalelas = 3;
        long thinkMinMs = 2000;
        long thinkMaxMs = 8000;
        Path raizProjeto = Paths.get(".").toAbsolutePath().normalize();

        String baseUrl() {
            return baseUrlFornecida != null ? baseUrlFornecida : ("http://localhost:" + porta);
        }

        static Config ler(String[] args) {
            Config c = new Config();
            String urlEnv = System.getenv("GERARD_BENCH_BASE_URL");
            if (urlEnv != null && urlEnv.trim().length() > 0) c.baseUrlFornecida = urlEnv.trim();
            c.sessoesSequenciais = lerInt("GERARD_BENCH_SESSIONS", c.sessoesSequenciais);
            c.sessoesParalelas = lerInt("GERARD_BENCH_PARALLEL_SESSIONS", c.sessoesParalelas);
            c.thinkMinMs = lerLong("GERARD_BENCH_THINK_MIN_MS", c.thinkMinMs);
            c.thinkMaxMs = lerLong("GERARD_BENCH_THINK_MAX_MS", c.thinkMaxMs);
            for (String arg : args) {
                if (arg.startsWith("--sessions=")) c.sessoesSequenciais = Integer.parseInt(arg.substring(11));
                else if (arg.startsWith("--parallel-sessions=")) c.sessoesParalelas = Integer.parseInt(arg.substring(20));
                else if (arg.startsWith("--think-min-ms=")) c.thinkMinMs = Long.parseLong(arg.substring(15));
                else if (arg.startsWith("--think-max-ms=")) c.thinkMaxMs = Long.parseLong(arg.substring(15));
                else if (arg.startsWith("--base-url=")) c.baseUrlFornecida = arg.substring(11);
            }
            return c;
        }

        private static int lerInt(String env, int padrao) {
            String v = System.getenv(env);
            return v != null && v.trim().length() > 0 ? Integer.parseInt(v.trim()) : padrao;
        }
        private static long lerLong(String env, long padrao) {
            String v = System.getenv(env);
            return v != null && v.trim().length() > 0 ? Long.parseLong(v.trim()) : padrao;
        }
    }

    // ------------------------------------------------------------------
    // Subida do servidor (processo separado) e espera de prontidão
    // ------------------------------------------------------------------

    private static Process subirServidor(int porta, Path raizProjeto) throws IOException {
        String classpath = raizProjeto.resolve("build/classes") + java.io.File.pathSeparator
                + raizProjeto.toString() + java.io.File.separator + "lib" + java.io.File.separator + "*";
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", classpath,
                "gerard.infraestrutura.web.ServidorPrototipoWeb", String.valueOf(porta));
        pb.directory(raizProjeto.toFile());
        pb.redirectErrorStream(true);
        Path log = raizProjeto.resolve("scripts/benchmark/results");
        Files.createDirectories(log);
        pb.redirectOutput(log.resolve("servidor-benchmark.log").toFile());
        System.out.println("Subindo servidor próprio na porta " + porta);
        return pb.start();
    }

    private static void aguardarServidorPronto(HttpClient http, String baseUrl) throws Exception {
        Exception ultimaFalha = null;
        for (int tentativa = 0; tentativa < 50; tentativa++) {
            try {
                HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/"))
                        .timeout(Duration.ofSeconds(2)).GET().build();
                HttpResponse<Void> resp = http.send(req, BodyHandlers.discarding());
                if (resp.statusCode() > 0) return;
            } catch (Exception ex) {
                ultimaFalha = ex;
            }
            Thread.sleep(200);
        }
        throw new IllegalStateException("Servidor não respondeu em " + baseUrl + " a tempo.", ultimaFalha);
    }

    // ------------------------------------------------------------------
    // Cliente HTTP fino
    // ------------------------------------------------------------------

    private static final class Chamada {
        final String rota;
        final int status;
        final String corpo;
        final long latenciaNanos;
        Chamada(String rota, int status, String corpo, long latenciaNanos) {
            this.rota = rota; this.status = status; this.corpo = corpo; this.latenciaNanos = latenciaNanos;
        }
        boolean ok() { return status >= 200 && status < 300; }
    }

    private static final class Sessao {
        final HttpClient http;
        final String baseUrl;
        Sessao(HttpClient http, String baseUrl) { this.http = http; this.baseUrl = baseUrl; }

        Chamada get(String caminho) { return enviar("GET", caminho, null); }
        Chamada post(String caminho, String corpoJson) { return enviar("POST", caminho, corpoJson == null ? "" : corpoJson); }

        private Chamada enviar(String metodo, String caminho, String corpo) {
            long inicio = System.nanoTime();
            try {
                HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(baseUrl + caminho))
                        .timeout(Duration.ofSeconds(10));
                if ("GET".equals(metodo)) {
                    b.GET();
                } else {
                    b.header("Content-Type", "application/json; charset=utf-8");
                    b.POST(BodyPublishers.ofString(corpo, StandardCharsets.UTF_8));
                }
                HttpResponse<String> resp = http.send(b.build(), BodyHandlers.ofString(StandardCharsets.UTF_8));
                long fim = System.nanoTime();
                return new Chamada(rotaCanonica(metodo, caminho), resp.statusCode(), resp.body(), fim - inicio);
            } catch (Exception ex) {
                long fim = System.nanoTime();
                return new Chamada(rotaCanonica(metodo, caminho), -1, "{\"erro\":\"" + ex.getClass().getSimpleName()
                        + ": " + String.valueOf(ex.getMessage()).replace("\"", "'") + "\"}", fim - inicio);
            }
        }

        private static String rotaCanonica(String metodo, String caminho) {
            return metodo + " " + ("/".equals(caminho) ? "/" : caminho);
        }
    }

    // ------------------------------------------------------------------
    // Sessão simulada — o coração do benchmark
    // ------------------------------------------------------------------

    private static final class SessaoResultado {
        final int id;
        final String modo; // "sequencial" | "paralelo"
        final List<Chamada> chamadas = new ArrayList<Chamada>();
        long wallClockNanos;
        int tentativasClassificacaoErradas;
        String falhaInesperada; // não nulo se a sessão abortou por resposta fora do esperado

        SessaoResultado(int id, String modo) { this.id = id; this.modo = modo; }

        long tempoServidorNanos() {
            long soma = 0;
            for (Chamada c : chamadas) soma += c.latenciaNanos;
            return soma;
        }
        int erros() {
            int n = 0;
            for (Chamada c : chamadas) if (!c.ok()) n++;
            return n;
        }
    }

    private static long pensar(long minMs, long maxMs) {
        long duracao = minMs >= maxMs ? minMs : ThreadLocalRandom.current().nextLong(minMs, maxMs + 1);
        try { Thread.sleep(duracao); } catch (InterruptedException ignorado) { Thread.currentThread().interrupt(); }
        return duracao;
    }

    /**
     * Executa uma sessão completa (uma situação-problema, do carregamento da
     * página ao fim) e devolve as chamadas feitas + o tempo total decorrido.
     * Qualquer resposta fora do esperado nesse fluxo determinístico indica
     * problema real (harness ou servidor) — a sessão registra e interrompe
     * ali, sem tentar "adivinhar" uma recuperação.
     */
    private static SessaoResultado executarSessao(Sessao s, int id, Config cfg) {
        return executarSessaoComModo(s, id, "sequencial", cfg);
    }

    private static SessaoResultado executarSessaoComModo(Sessao s, int id, String modo, Config cfg) {
        SessaoResultado r = new SessaoResultado(id, modo);
        long inicioParede = System.nanoTime();
        try {
            // 1) carga da página
            registrar(r, s.get("/"));
            registrar(r, s.get("/api/situacao"));
            pensar(cfg.thinkMinMs, cfg.thinkMaxMs); // ler enunciado

            // 2) sorteia um grupo (como clicar no dado de Medidas ou Relações)
            boolean medidas = ThreadLocalRandom.current().nextBoolean();
            Chamada sorteio = registrar(r, s.post(medidas ? "/api/sorteios/medidas" : "/api/sorteios/relacoes", ""));
            exigirOk(sorteio, "sortear " + (medidas ? "medidas" : "relações"));
            pensar(cfg.thinkMinMs, cfg.thinkMaxMs); // ler novo enunciado / decidir categoria

            // 3) classifica — esgota o grupo até acertar (usuário que conhece o domínio)
            List<String> candidatos = new ArrayList<String>(medidas ? GRUPO_MEDIDAS : GRUPO_RELACOES);
            Collections.shuffle(candidatos, new java.util.Random(ThreadLocalRandom.current().nextLong()));
            Map<String, Object> classificacaoCorreta = null;
            for (String candidato : candidatos) {
                Chamada c = registrar(r, s.post("/api/classificacao/categoria",
                        "{\"categoria\":\"" + candidato + "\"}"));
                exigirOk(c, "classificar categoria " + candidato);
                Map<String, Object> corpo = MiniJson.parseObjeto(c.corpo);
                if (Boolean.TRUE.equals(corpo.get("correta"))) {
                    classificacaoCorreta = corpo;
                    break;
                }
                r.tentativasClassificacaoErradas++;
                pensar(cfg.thinkMinMs, cfg.thinkMaxMs); // ler o questionamento da divergência
                Chamada conf = registrar(r, s.post("/api/classificacao/confirmacao", "{\"concordou\":false}"));
                exigirOk(conf, "confirmar categoria divergente (recusar)");
                pensar(cfg.thinkMinMs, cfg.thinkMaxMs); // decidir a próxima tentativa
            }
            if (classificacaoCorreta == null) {
                throw new IllegalStateException("esgotou as " + candidatos.size()
                        + " categorias do grupo sem acertar — a última resposta deveria ter sido correta=true");
            }

            // 4) manipula o diagrama: posicionar valor OU escolher operação, conforme a categoria
            @SuppressWarnings("unchecked")
            Map<String, Object> estado = (Map<String, Object>) classificacaoCorreta.get("estado");
            @SuppressWarnings("unchecked")
            List<Object> acoes = (List<Object>) estado.get("acoes_disponiveis");
            boolean temPosicionar = false;
            boolean temEscolherOperacao = false;
            for (Object o : acoes) {
                @SuppressWarnings("unchecked")
                Map<String, Object> acao = (Map<String, Object>) o;
                if ("PROPOR_VALOR_PAPEL".equals(acao.get("id"))) temPosicionar = true;
                if ("ESCOLHER_OPERACAO_RELACAO".equals(acao.get("id"))) temEscolherOperacao = true;
            }

            pensar(cfg.thinkMinMs, cfg.thinkMaxMs); // começar a manipular o diagrama

            if (temPosicionar) {
                @SuppressWarnings("unchecked")
                Map<String, Object> modelagem = (Map<String, Object>) estado.get("modelagem");
                String papelId = String.valueOf(modelagem.get("papel_desconhecido_original"));
                int tentativasPosicionar = 1 + ThreadLocalRandom.current().nextInt(2); // 1 ou 2
                for (int i = 0; i < tentativasPosicionar; i++) {
                    Chamada gesto = registrar(r, s.post("/api/gestos", gestoJson(id, i)));
                    exigirOk(gesto, "registrar gesto antes de posicionar");
                    int valor = 1 + ThreadLocalRandom.current().nextInt(40);
                    Chamada c = registrar(r, s.post("/api/acoes/posicionar",
                            "{\"papel_id\":\"" + papelId + "\",\"valor\":" + valor + "}"));
                    exigirOk(c, "posicionar valor");
                    pensar(cfg.thinkMinMs, cfg.thinkMaxMs); // ver o feedback antes de decidir a próxima tentativa
                }
            } else if (temEscolherOperacao) {
                String seletor = "ENTRE_TRANSFORMACOES";
                for (Object o : acoes) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> acao = (Map<String, Object>) o;
                    if ("ESCOLHER_OPERACAO_RELACAO".equals(acao.get("id"))) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> corpoAcao = (Map<String, Object>) acao.get("corpo");
                        if (corpoAcao != null && corpoAcao.get("seletor") != null) {
                            seletor = String.valueOf(corpoAcao.get("seletor"));
                        }
                    }
                }
                Chamada gesto = registrar(r, s.post("/api/gestos", gestoJson(id, 0)));
                exigirOk(gesto, "registrar gesto antes de escolher operação");
                String operacao = ThreadLocalRandom.current().nextBoolean() ? "SOMA" : "SUBTRACAO";
                Chamada c = registrar(r, s.post("/api/acoes/escolher-operacao",
                        "{\"seletor\":\"" + seletor + "\",\"operacao\":\"" + operacao + "\"}"));
                exigirOk(c, "escolher operação");
                pensar(cfg.thinkMinMs, cfg.thinkMaxMs); // ver o feedback
            }
            // TRANSFORMACAO_RELACAO sem dado "rica" curado (possuiSituacaoRicaValida()=false em
            // ServicoSorteioAtividadeWeb.escolherCategoria) classifica certo mas não ativa
            // nenhuma sub-atividade — nem posicionar, nem escolher-operação. Achado real do
            // benchmark, não bug do harness: nesse caso não há nada para reiniciar depois.
            boolean possuiSubAtividadeAtiva = temPosicionar || temEscolherOperacao;

            // 5) às vezes, o usuário reinicia a mesma situação para tentar de novo — só faz
            // sentido pedir isso quando existe de fato uma sub-atividade ativa (ver acima).
            if (possuiSubAtividadeAtiva && ThreadLocalRandom.current().nextInt(4) == 0) {
                pensar(cfg.thinkMinMs, cfg.thinkMaxMs);
                Chamada c = registrar(r, s.post("/api/reiniciar", ""));
                exigirOk(c, "reiniciar");
            }
        } catch (RuntimeException ex) {
            r.falhaInesperada = ex.getMessage();
        }
        r.wallClockNanos = System.nanoTime() - inicioParede;
        return r;
    }

    private static String gestoJson(int sessaoId, int seq) {
        return "{\"schema\":\"gerard.gesto-web.v1\",\"gesture_id\":\"bench-" + sessaoId + "-" + seq + "\","
                + "\"destino_geometrico\":{\"x\":0,\"y\":0}}";
    }

    private static Chamada registrar(SessaoResultado r, Chamada c) {
        r.chamadas.add(c);
        return c;
    }

    private static void exigirOk(Chamada c, String descricaoPasso) {
        if (!c.ok()) {
            throw new IllegalStateException("resposta inesperada (" + c.status + ") em '" + descricaoPasso
                    + "' [" + c.rota + "]: " + resumir(c.corpo));
        }
    }

    private static String resumir(String texto) {
        return texto == null ? "" : (texto.length() > 200 ? texto.substring(0, 200) + "..." : texto);
    }

    private static List<SessaoResultado> executarSessoesParalelas(Sessao s, Config cfg) {
        if (cfg.sessoesParalelas <= 0) return Collections.emptyList();
        ExecutorService pool = Executors.newFixedThreadPool(cfg.sessoesParalelas);
        List<Future<SessaoResultado>> futuros = new ArrayList<Future<SessaoResultado>>();
        for (int i = 0; i < cfg.sessoesParalelas; i++) {
            final int id = cfg.sessoesSequenciais + i;
            futuros.add(pool.submit(() -> executarSessaoComModo(s, id, "paralelo", cfg)));
        }
        List<SessaoResultado> resultados = new CopyOnWriteArrayList<SessaoResultado>();
        for (Future<SessaoResultado> f : futuros) {
            try { resultados.add(f.get()); } catch (Exception ex) { /* já registrado como falha na própria sessão */ }
        }
        pool.shutdown();
        return new ArrayList<SessaoResultado>(resultados);
    }

    // ------------------------------------------------------------------
    // Estatísticas
    // ------------------------------------------------------------------

    private static double[] estatisticasMs(List<Long> nanos) {
        // [min, media, p50, p95, p99, max] em milissegundos
        if (nanos.isEmpty()) return new double[] {0, 0, 0, 0, 0, 0};
        List<Long> ord = new ArrayList<Long>(nanos);
        Collections.sort(ord);
        double soma = 0;
        for (long v : ord) soma += v;
        double media = (soma / ord.size()) / 1e6;
        return new double[] {
            ord.get(0) / 1e6,
            media,
            percentil(ord, 50) / 1e6,
            percentil(ord, 95) / 1e6,
            percentil(ord, 99) / 1e6,
            ord.get(ord.size() - 1) / 1e6
        };
    }

    private static double[] estatisticasSegundos(List<Long> nanos) {
        double[] ms = estatisticasMs(nanos);
        double[] s = new double[ms.length];
        for (int i = 0; i < ms.length; i++) s[i] = ms[i] / 1000.0;
        return s;
    }

    private static long percentil(List<Long> ordenada, double p) {
        int idx = (int) Math.ceil(p / 100.0 * ordenada.size()) - 1;
        idx = Math.max(0, Math.min(ordenada.size() - 1, idx));
        return ordenada.get(idx);
    }

    // ------------------------------------------------------------------
    // Relatório
    // ------------------------------------------------------------------

    private static String gerarRelatorioMarkdown(Config cfg, List<SessaoResultado> sequenciais,
            List<SessaoResultado> paralelas) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Benchmark de uso realista — API Gérard web\n\n");
        sb.append("**Estes números representam uso realista simulado (sessões com pausas de leitura/raciocínio ")
          .append("entre passos), não a capacidade máxima teórica do servidor.** Não houve martelamento de ")
          .append("requisições simultâneas em massa — o servidor guarda uma única situação ativa em memória por ")
          .append("processo (não é multiusuário), então o cenário realista é sessões sequenciais, com só um número ")
          .append("pequeno delas em paralelo representando poucos usuários simultâneos.\n\n");
        sb.append("Base URL: `").append(cfg.baseUrl()).append("`  \n");
        sb.append("Sessões sequenciais: ").append(sequenciais.size())
          .append("  |  Sessões em paralelo: ").append(paralelas.size())
          .append("  |  Think-time simulado por pausa: ").append(cfg.thinkMinMs).append("–")
          .append(cfg.thinkMaxMs).append(" ms (uniforme)\n\n");

        int falhasSeq = contarFalhas(sequenciais);
        int falhasPar = contarFalhas(paralelas);
        sb.append("Sessões com falha inesperada (fora do fluxo determinístico previsto): ")
          .append(falhasSeq).append("/").append(sequenciais.size()).append(" sequenciais, ")
          .append(falhasPar).append("/").append(paralelas.size()).append(" paralelas.\n\n");
        sb.append("**Leitura diferente para cada bateria:** nas sessões *sequenciais*, qualquer falha aqui é ")
          .append("bug real (do harness ou do servidor), já que só existe uma sessão de cada vez e o fluxo é ")
          .append("determinístico. Nas sessões *em paralelo*, uma falha deste tipo normalmente significa outra ")
          .append("coisa: duas sessões disputando a MESMA \"tentativa de classificação\" global do servidor ao ")
          .append("mesmo tempo — uma responde a categoria enquanto a outra ainda está no meio do próprio fluxo, ")
          .append("e a sequência de uma corrompe a da outra. Isso não é um bug do harness — é a confirmação, sob ")
          .append("uso concorrente real (mesmo que leve, 2–3 sessões), de que o servidor guarda estado de ")
          .append("classificação global e não isola sessões por usuário.\n\n");
        if (falhasSeq + falhasPar > 0) {
            sb.append("### Detalhe das falhas\n\n");
            detalharFalhas(sb, sequenciais);
            detalharFalhas(sb, paralelas);
            sb.append("\n");
        }

        sb.append("## 1) Latência por rota — só tempo de resposta do servidor\n\n");
        sb.append("Cada chamada real feita durante as sessões, agrupada por rota; pausas simuladas (think-time) ")
          .append("NÃO entram aqui.\n\n");
        sb.append("### Sessões sequenciais\n\n");
        tabelaPorRota(sb, sequenciais);
        sb.append("\n### Sessões em paralelo\n\n");
        tabelaPorRota(sb, paralelas);

        sb.append("\n## 2) Tempo total por sessão — fluxo completo, incluindo think-time\n\n");
        sb.append("Da primeira chamada (`GET /`) até o fim da sessão. É a métrica mais próxima de ")
          .append("\"quanto tempo um usuário passa resolvendo uma situação\", dominada pelas pausas simuladas, ")
          .append("não pelo servidor.\n\n");
        tabelaSessao(sb, "sequencial", sequenciais, true);
        tabelaSessao(sb, "paralelo", paralelas, true);

        sb.append("\n## 3) Tempo de servidor por sessão — só a soma das chamadas HTTP, sem think-time\n\n");
        sb.append("A mesma sessão, mas contando só o tempo realmente gasto em rede/servidor — mostra que o ")
          .append("servidor é uma fração pequena do tempo total da sessão.\n\n");
        tabelaSessao(sb, "sequencial", sequenciais, false);
        tabelaSessao(sb, "paralelo", paralelas, false);

        return sb.toString();
    }

    private static int contarFalhas(List<SessaoResultado> sessoes) {
        int n = 0;
        for (SessaoResultado r : sessoes) if (r.falhaInesperada != null) n++;
        return n;
    }

    private static void detalharFalhas(StringBuilder sb, List<SessaoResultado> sessoes) {
        for (SessaoResultado r : sessoes) {
            if (r.falhaInesperada != null) {
                sb.append("- sessão ").append(r.id).append(" (").append(r.modo).append("): ")
                  .append(r.falhaInesperada).append("\n");
            }
        }
    }

    private static void tabelaPorRota(StringBuilder sb, List<SessaoResultado> sessoes) {
        Map<String, List<Long>> porRota = new LinkedHashMap<String, List<Long>>();
        Map<String, Integer> errosPorRota = new LinkedHashMap<String, Integer>();
        Map<String, Integer> totalPorRota = new LinkedHashMap<String, Integer>();
        for (SessaoResultado r : sessoes) {
            for (Chamada c : r.chamadas) {
                porRota.computeIfAbsent(c.rota, k -> new ArrayList<Long>()).add(c.latenciaNanos);
                totalPorRota.merge(c.rota, 1, Integer::sum);
                if (!c.ok()) errosPorRota.merge(c.rota, 1, Integer::sum);
            }
        }
        if (porRota.isEmpty()) {
            sb.append("_Nenhuma chamada registrada._\n");
            return;
        }
        sb.append("| Rota | N | Erros | Erro % | Min (ms) | Média (ms) | p50 (ms) | p95 (ms) | p99 (ms) | Máx (ms) |\n");
        sb.append("|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|\n");
        for (Map.Entry<String, List<Long>> e : porRota.entrySet()) {
            String rota = e.getKey();
            int total = totalPorRota.get(rota);
            int erros = errosPorRota.getOrDefault(rota, 0);
            double[] st = estatisticasMs(e.getValue());
            sb.append("| ").append(rota)
              .append(" | ").append(total)
              .append(" | ").append(erros)
              .append(" | ").append(String.format(java.util.Locale.ROOT, "%.1f", total > 0 ? 100.0 * erros / total : 0))
              .append(" | ").append(String.format(java.util.Locale.ROOT, "%.2f", st[0]))
              .append(" | ").append(String.format(java.util.Locale.ROOT, "%.2f", st[1]))
              .append(" | ").append(String.format(java.util.Locale.ROOT, "%.2f", st[2]))
              .append(" | ").append(String.format(java.util.Locale.ROOT, "%.2f", st[3]))
              .append(" | ").append(String.format(java.util.Locale.ROOT, "%.2f", st[4]))
              .append(" | ").append(String.format(java.util.Locale.ROOT, "%.2f", st[5]))
              .append(" |\n");
        }
    }

    private static void tabelaSessao(StringBuilder sb, String modo, List<SessaoResultado> sessoes,
            boolean incluirThinkTime) {
        sb.append("**").append(modo).append("** (N=").append(sessoes.size()).append(")");
        if (sessoes.isEmpty()) { sb.append(" — nenhuma sessão.\n"); return; }
        List<Long> valores = new ArrayList<Long>();
        for (SessaoResultado r : sessoes) {
            valores.add(incluirThinkTime ? r.wallClockNanos : r.tempoServidorNanos());
        }
        double[] st = incluirThinkTime ? estatisticasSegundos(valores) : estatisticasMs(valores);
        String unidade = incluirThinkTime ? "s" : "ms";
        sb.append(" — min ").append(String.format(java.util.Locale.ROOT, "%.2f", st[0])).append(unidade)
          .append(", média ").append(String.format(java.util.Locale.ROOT, "%.2f", st[1])).append(unidade)
          .append(", p50 ").append(String.format(java.util.Locale.ROOT, "%.2f", st[2])).append(unidade)
          .append(", p95 ").append(String.format(java.util.Locale.ROOT, "%.2f", st[3])).append(unidade)
          .append(", p99 ").append(String.format(java.util.Locale.ROOT, "%.2f", st[4])).append(unidade)
          .append(", máx ").append(String.format(java.util.Locale.ROOT, "%.2f", st[5])).append(unidade)
          .append("\n\n");
    }

    private static String gerarJsonBruto(Config cfg, List<SessaoResultado> sequenciais,
            List<SessaoResultado> paralelas) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"timestamp\":\"").append(Instant.now()).append("\",")
          .append("\"base_url\":\"").append(escapar(cfg.baseUrl())).append("\",")
          .append("\"think_min_ms\":").append(cfg.thinkMinMs).append(",")
          .append("\"think_max_ms\":").append(cfg.thinkMaxMs).append(",")
          .append("\"sessoes_sequenciais\":").append(sessoesParaJson(sequenciais)).append(",")
          .append("\"sessoes_paralelas\":").append(sessoesParaJson(paralelas))
          .append("}");
        return sb.toString();
    }

    private static String sessoesParaJson(List<SessaoResultado> sessoes) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < sessoes.size(); i++) {
            SessaoResultado r = sessoes.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(r.id).append(",")
              .append("\"modo\":\"").append(escapar(r.modo)).append("\",")
              .append("\"wall_clock_ms\":").append(String.format(java.util.Locale.ROOT, "%.3f", r.wallClockNanos / 1e6)).append(",")
              .append("\"tempo_servidor_ms\":").append(String.format(java.util.Locale.ROOT, "%.3f", r.tempoServidorNanos() / 1e6)).append(",")
              .append("\"chamadas\":").append(r.chamadas.size()).append(",")
              .append("\"erros\":").append(r.erros()).append(",")
              .append("\"tentativas_classificacao_erradas\":").append(r.tentativasClassificacaoErradas).append(",")
              .append("\"falha_inesperada\":").append(r.falhaInesperada == null ? "null"
                      : "\"" + escapar(r.falhaInesperada) + "\"").append(",")
              .append("\"chamadas_detalhe\":[");
            for (int j = 0; j < r.chamadas.size(); j++) {
                Chamada c = r.chamadas.get(j);
                if (j > 0) sb.append(",");
                sb.append("{\"rota\":\"").append(escapar(c.rota)).append("\",")
                  .append("\"status\":").append(c.status).append(",")
                  .append("\"latencia_ms\":").append(String.format(java.util.Locale.ROOT, "%.3f", c.latenciaNanos / 1e6))
                  .append("}");
            }
            sb.append("]}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }

    // ------------------------------------------------------------------
    // Parser JSON minimalista (só o suficiente para ler as respostas do
    // próprio servidor — sem dependência externa nem do classpath gerard.*)
    // ------------------------------------------------------------------

    private static final class MiniJson {
        private final String texto;
        private int pos;

        private MiniJson(String texto) { this.texto = texto; }

        @SuppressWarnings("unchecked")
        static Map<String, Object> parseObjeto(String json) {
            Object v = new MiniJson(json).parseValor();
            return v instanceof Map ? (Map<String, Object>) v : new LinkedHashMap<String, Object>();
        }

        private Object parseValor() {
            pularEspacos();
            char c = texto.charAt(pos);
            if (c == '{') return parseObjetoInterno();
            if (c == '[') return parseArrayInterno();
            if (c == '"') return parseStringInterno();
            if (c == 't') { pos += 4; return Boolean.TRUE; }
            if (c == 'f') { pos += 5; return Boolean.FALSE; }
            if (c == 'n') { pos += 4; return null; }
            return parseNumeroInterno();
        }

        private Map<String, Object> parseObjetoInterno() {
            Map<String, Object> mapa = new LinkedHashMap<String, Object>();
            pos++; // {
            pularEspacos();
            if (texto.charAt(pos) == '}') { pos++; return mapa; }
            while (true) {
                pularEspacos();
                String chave = parseStringInterno();
                pularEspacos();
                pos++; // :
                Object valor = parseValor();
                mapa.put(chave, valor);
                pularEspacos();
                char c = texto.charAt(pos);
                if (c == ',') { pos++; continue; }
                if (c == '}') { pos++; break; }
            }
            return mapa;
        }

        private List<Object> parseArrayInterno() {
            List<Object> lista = new ArrayList<Object>();
            pos++; // [
            pularEspacos();
            if (texto.charAt(pos) == ']') { pos++; return lista; }
            while (true) {
                lista.add(parseValor());
                pularEspacos();
                char c = texto.charAt(pos);
                if (c == ',') { pos++; continue; }
                if (c == ']') { pos++; break; }
            }
            return lista;
        }

        private String parseStringInterno() {
            pos++; // abre aspas
            StringBuilder sb = new StringBuilder();
            while (texto.charAt(pos) != '"') {
                char c = texto.charAt(pos);
                if (c == '\\') {
                    pos++;
                    char esc = texto.charAt(pos);
                    switch (esc) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'u':
                            String hex = texto.substring(pos + 1, pos + 5);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                            break;
                        default: sb.append(esc);
                    }
                } else {
                    sb.append(c);
                }
                pos++;
            }
            pos++; // fecha aspas
            return sb.toString();
        }

        private Double parseNumeroInterno() {
            int inicio = pos;
            while (pos < texto.length() && "+-0123456789.eE".indexOf(texto.charAt(pos)) >= 0) pos++;
            return Double.parseDouble(texto.substring(inicio, pos));
        }

        private void pularEspacos() {
            while (pos < texto.length() && Character.isWhitespace(texto.charAt(pos))) pos++;
        }
    }
}
