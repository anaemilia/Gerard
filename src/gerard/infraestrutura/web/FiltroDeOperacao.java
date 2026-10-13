package gerard.infraestrutura.web;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import gerard.pesquisador.auditoria.EscritorJsonSimples;

import gerard.aplicacao.portabilidade.RotasTentativa;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * O que toda requisição à API recebe, sem que cada rota precise lembrar:
 * <ul>
 * <li><b>Identificação:</b> {@code X-Request-Id} (o que o cliente mandou, se for um id sensato; senão um novo) volta na
 * resposta e entra no log, ligando a chamada do cliente à linha do servidor.</li>
 * <li><b>Log estruturado:</b> uma linha JSON por requisição na saída padrão (carimbo, id, método, caminho com os ids
 * trocados por {id}, status, duração). Nunca cookie, token nem corpo.</li>
 * <li><b>Métricas:</b> contagem, duração e status por rota (modelo do caminho), lidas em GET /api/v1/metricas.</li>
 * <li><b>CORS explícito:</b> só as origens de {@code CORS_ORIGENS} (separadas por vírgula) recebem os cabeçalhos CORS;
 * sem lista, a API é só de mesma origem e nenhum cabeçalho CORS é enviado. O preflight (OPTIONS) de origem não
 * permitida é 403.</li>
 * <li><b>{@code X-Content-Type-Options: nosniff}.</b></li>
 * </ul>
 */
final class FiltroDeOperacao extends Filter {
    private static final Pattern ID_SEGURO = Pattern.compile("[A-Za-z0-9._-]{1,64}");

    /** Contadores de uma rota. */
    private static final class Metrica {
        long total;
        long somaMs;
        long maximoMs;
        final Map<String, Long> porStatus = new TreeMap<String, Long>();

        synchronized void registrar(int status, long ms) {
            total++;
            somaMs += ms;
            maximoMs = Math.max(maximoMs, ms);
            String classe = (status / 100) + "xx";
            Long atual = porStatus.get(classe);
            porStatus.put(classe, Long.valueOf(atual == null ? 1 : atual + 1));
        }

        synchronized Map<String, Object> instantaneo() {
            Map<String, Object> mapa = new LinkedHashMap<String, Object>();
            mapa.put("total", Long.valueOf(total));
            mapa.put("media_ms", Long.valueOf(total == 0 ? 0 : somaMs / total));
            mapa.put("maximo_ms", Long.valueOf(maximoMs));
            mapa.put("por_status", new LinkedHashMap<String, Object>(porStatus));
            return mapa;
        }
    }

    private final Map<String, Metrica> metricas = new ConcurrentHashMap<String, Metrica>();
    private final Map<HttpExchange, String> idsDasRequisicoes =
            Collections.synchronizedMap(new java.util.WeakHashMap<HttpExchange, String>());
    private final Set<String> origensPermitidas;
    private final long inicioEmMs = System.currentTimeMillis();

    FiltroDeOperacao() {
        String lista = System.getProperty("gerard.cors.origens", System.getenv("CORS_ORIGENS"));
        this.origensPermitidas = new HashSet<String>();
        if (lista != null) {
            for (String origem : lista.split(",")) {
                if (!origem.trim().isEmpty()) {
                    origensPermitidas.add(origem.trim());
                }
            }
        }
    }

    @Override
    public String description() {
        return "request id, log estruturado, métricas e CORS";
    }

    /** O X-Request-Id desta requisição (para quem monta a resposta de erro). */
    String idDaRequisicao(HttpExchange troca) {
        return idsDasRequisicoes.get(troca);
    }

    @Override
    public void doFilter(HttpExchange troca, Chain cadeia) throws IOException {
        long inicio = System.nanoTime();
        String recebido = troca.getRequestHeaders().getFirst("X-Request-Id");
        String id = recebido != null && ID_SEGURO.matcher(recebido).matches() ? recebido : UUID.randomUUID().toString();
        idsDasRequisicoes.put(troca, id);
        troca.getResponseHeaders().set("X-Request-Id", id);
        troca.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        String origem = troca.getRequestHeaders().getFirst("Origin");
        if (origem != null && origensPermitidas.contains(origem)) {
            troca.getResponseHeaders().set("Access-Control-Allow-Origin", origem);
            troca.getResponseHeaders().add("Vary", "Origin");
            troca.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
            troca.getResponseHeaders().set("Access-Control-Expose-Headers",
                    "ETag, Location, Retry-After, Allow, X-Request-Id, Deprecation, Sunset, Link");
        }
        try {
            if ("OPTIONS".equals(troca.getRequestMethod()) && origem != null) {
                preflight(troca, origem);
            } else {
                cadeia.doFilter(troca);
            }
        } finally {
            long ms = (System.nanoTime() - inicio) / 1_000_000L;
            int status = troca.getResponseCode();
            String caminho = modeloDoCaminho(troca.getRequestURI().getPath());
            Metrica metrica = metricas.computeIfAbsent(troca.getRequestMethod() + " " + caminho, k -> new Metrica());
            metrica.registrar(status, ms);
            Map<String, Object> linha = new LinkedHashMap<String, Object>();
            linha.put("ts", java.time.Instant.now().toString());
            linha.put("request_id", id);
            linha.put("metodo", troca.getRequestMethod());
            linha.put("caminho", caminho);
            linha.put("status", Integer.valueOf(status));
            linha.put("ms", Long.valueOf(ms));
            System.out.println(EscritorJsonSimples.escrever(linha));
        }
    }

    private void preflight(HttpExchange troca, String origem) throws IOException {
        if (!origensPermitidas.contains(origem)) {
            troca.sendResponseHeaders(403, -1);
            troca.close();
            return;
        }
        troca.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        troca.getResponseHeaders().set("Access-Control-Allow-Headers",
                "Content-Type, If-Match, If-None-Match, Authorization, X-Request-Id");
        troca.getResponseHeaders().set("Access-Control-Max-Age", "600");
        troca.sendResponseHeaders(204, -1);
        troca.close();
    }

    /**
     * O caminho como modelo, para o log e as métricas não terem uma entrada por valor: o id da tentativa vira {id}, o
     * papel {papel}, a pessoa {id}, o problema {slug}; o que não é da API é "(estatico)" e o que a API não reconhece
     * é "(desconhecido)".
     */
    static String modeloDoCaminho(String caminho) {
        String base = RotasTentativa.BASE;
        if (caminho.equals(base)) {
            return base;
        }
        if (caminho.startsWith(base + "/")) {
            String[] partes = caminho.substring(base.length() + 1).split("/", -1);
            partes[0] = "{id}";
            if (partes.length > 2 && "papeis".equals(partes[1])) {
                partes[2] = "{papel}";
            }
            return base + "/" + String.join("/", partes);
        }
        if (caminho.startsWith("/api/v1/usuarios/")) {
            return "/api/v1/usuarios/{id}";
        }
        if (caminho.startsWith(Problema.BASE) && caminho.length() > Problema.BASE.length()) {
            return Problema.BASE + "{slug}";
        }
        for (String conhecido : CAMINHOS_FIXOS) {
            if (caminho.equals(conhecido)) {
                return caminho;
            }
        }
        return caminho.startsWith("/api/") ? "(desconhecido)" : "(estatico)";
    }

    private static final String[] CAMINHOS_FIXOS = {"/api/v1", "/api/v1/", "/api/v1/usuarios", "/api/v1/sessao/usuario",
        "/api/v1/relatos", "/api/v1/pesquisa/log", "/api/v1/pesquisa/sincronizacoes", "/api/v1/curadoria/situacoes",
        "/api/v1/metricas", "/api/v1/openapi.json", "/api/pesquisa/log", "/api/curadoria/situacoes"};

    Map<String, Object> metricas() {
        Map<String, Object> raiz = new LinkedHashMap<String, Object>();
        raiz.put("schema", "gerard.metricas.v1");
        raiz.put("segundos_no_ar", Long.valueOf((System.currentTimeMillis() - inicioEmMs) / 1000));
        Map<String, Object> rotas = new TreeMap<String, Object>();
        for (Map.Entry<String, Metrica> e : metricas.entrySet()) {
            rotas.put(e.getKey(), e.getValue().instantaneo());
        }
        raiz.put("rotas", new LinkedHashMap<String, Object>(rotas));
        return raiz;
    }
}
