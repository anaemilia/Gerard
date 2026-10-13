package gerard.infraestrutura.web;

import gerard.agente.conhecimento.AnalisadorJsonSimples;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Operação e segurança da API, pelo HTTP real: X-Request-Id, log estruturado sem segredo, métricas, CORS explícito, os dois
 * perfis (visitante sem credencial, pesquisador com Bearer) e a depreciação dos caminhos fora de /api/v1.
 */
public class TesteOperacaoWeb {
    static int falhas;
    static final PrintStream SAIDA = System.out;
    static final String BASE = "http://localhost:18088";
    static final String TOKEN = "segredo-de-teste-do-pesquisador";
    static final String ORIGEM_PERMITIDA = "http://app.exemplo";

    static final class Resposta {
        int status;
        Map<String, String> cabecalhos = new java.util.HashMap<String, String>();
        Map<String, Object> corpo;

        String cab(String nome) {
            return cabecalhos.get(nome.toLowerCase());
        }
    }

    static Resposta chamar(String metodo, String caminho) throws Exception {
        return chamar(metodo, caminho, null);
    }

    @SuppressWarnings("unchecked")
    static Resposta chamar(String metodo, String caminho, String corpo, String... cabecalhos) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(BASE + caminho).openConnection();
        c.setRequestMethod(metodo);
        for (int i = 0; i < cabecalhos.length; i += 2) {
            c.setRequestProperty(cabecalhos[i], cabecalhos[i + 1]);
        }
        if (corpo != null) {
            c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "application/json");
            try (OutputStream out = c.getOutputStream()) {
                out.write(corpo.getBytes(StandardCharsets.UTF_8));
            }
        }
        Resposta r = new Resposta();
        r.status = c.getResponseCode();
        for (Map.Entry<String, java.util.List<String>> e : c.getHeaderFields().entrySet()) {
            if (e.getKey() != null) {
                r.cabecalhos.put(e.getKey().toLowerCase(), e.getValue().get(0));
            }
        }
        java.io.InputStream in = r.status < 400 ? c.getInputStream() : c.getErrorStream();
        String json = in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        if (!json.isEmpty() && json.trim().startsWith("{")) {
            r.corpo = (Map<String, Object>) AnalisadorJsonSimples.analisar(json);
        }
        return r;
    }

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
        }
        SAIDA.println((ok ? "[OK] " : "[FALHA] ") + msg);
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true"); // o HttpURLConnection barra Origin
        System.setProperty("gerard.cors.origens", ORIGEM_PERMITIDA);
        System.setProperty("gerard.pesquisador.token", TOKEN);
        PrintStream original = System.out;
        ByteArrayOutputStream capturado = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturado, true, "UTF-8"));
        ServidorPrototipoWeb.main(new String[] {"18088"});

        // --- X-Request-Id ------------------------------------------------------------------------------------------
        Resposta sem = chamar("GET", "/api/v1");
        conferir(sem.cab("X-Request-Id") != null && sem.cab("X-Request-Id").length() >= 32,
                "sem X-Request-Id na ida, o servidor gera um e devolve");
        Resposta com = chamar("GET", "/api/v1", null, "X-Request-Id", "meu-id-123");
        conferir("meu-id-123".equals(com.cab("X-Request-Id")), "um X-Request-Id sensato é devolvido como veio");
        Resposta ruim = chamar("GET", "/api/v1", null, "X-Request-Id", "tem espaço e <script>");
        conferir(ruim.cab("X-Request-Id") != null && !ruim.cab("X-Request-Id").contains("<"),
                "um X-Request-Id com lixo é trocado por um gerado");
        Resposta erro = chamar("GET", "/api/v1/nada", null, "X-Request-Id", "id-do-erro");
        conferir(erro.status == 404 && "id-do-erro".equals(String.valueOf(erro.corpo.get("request_id"))),
                "o problema+json traz o request_id da chamada");
        conferir("nosniff".equals(sem.cab("X-Content-Type-Options")), "X-Content-Type-Options: nosniff");

        // --- CORS explícito ---------------------------------------------------------------------------------------
        Resposta daOrigem = chamar("GET", "/api/v1", null, "Origin", ORIGEM_PERMITIDA);
        conferir(ORIGEM_PERMITIDA.equals(daOrigem.cab("Access-Control-Allow-Origin"))
                        && "Origin".equals(daOrigem.cab("Vary")),
                "origem da lista: Access-Control-Allow-Origin igual à origem, com Vary: Origin");
        Resposta deFora = chamar("GET", "/api/v1", null, "Origin", "http://qualquer.outro");
        conferir(deFora.cab("Access-Control-Allow-Origin") == null, "origem fora da lista: nenhum cabeçalho CORS");
        conferir(sem.cab("Access-Control-Allow-Origin") == null, "sem Origin: nenhum cabeçalho CORS");
        Resposta pre = chamar("OPTIONS", "/api/v1/tentativas", null, "Origin", ORIGEM_PERMITIDA,
                "Access-Control-Request-Method", "POST");
        conferir(pre.status == 204 && pre.cab("Access-Control-Allow-Methods") != null
                        && pre.cab("Access-Control-Allow-Headers").contains("If-Match"),
                "preflight da origem permitida: 204 com métodos e cabeçalhos (inclui If-Match)");
        conferir(chamar("OPTIONS", "/api/v1/tentativas", null, "Origin", "http://qualquer.outro").status == 403,
                "preflight de origem não permitida: 403");

        // --- Os dois perfis ---------------------------------------------------------------------------------------
        Resposta semCredencial = chamar("GET", "/api/v1/pesquisa/log");
        conferir(semCredencial.status == 401 && String.valueOf(semCredencial.cab("WWW-Authenticate")).startsWith("Bearer"),
                "pesquisador sem credencial: 401 com WWW-Authenticate: Bearer");
        conferir(chamar("GET", "/api/v1/pesquisa/log", null, "Authorization", "Bearer errado").status == 401,
                "token errado: 401");
        conferir(chamar("GET", "/api/v1/pesquisa/log", null, "Authorization", "Bearer " + TOKEN).status == 200,
                "Authorization: Bearer com o token certo: 200");
        Resposta legado = chamar("GET", "/api/v1/pesquisa/log", null, "X-Curadoria-Token", TOKEN);
        conferir(legado.status == 200 && "true".equals(legado.cab("Deprecation")),
                "o cabeçalho antigo X-Curadoria-Token ainda vale, marcado como depreciado");
        conferir(chamar("GET", "/api/v1/metricas").status == 401, "métricas sem credencial: 401");
        conferir(chamar("POST", "/api/v1/curadoria/situacoes", "x").status == 401
                        && chamar("POST", "/api/v1/curadoria/situacoes", "x", "Authorization", "Bearer " + TOKEN).status != 401,
                "a curadoria é do pesquisador: sem token 401, com Bearer a rota atende");
        conferir(chamar("POST", "/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}").status == 201,
                "o visitante não precisa de credencial para a própria tentativa");

        // --- Depreciação dos caminhos antigos ---------------------------------------------------------------------
        Resposta antigo = chamar("GET", "/api/pesquisa/log", null, "Authorization", "Bearer " + TOKEN);
        conferir(antigo.status == 200 && "true".equals(antigo.cab("Deprecation")) && antigo.cab("Sunset") != null
                        && String.valueOf(antigo.cab("Link")).contains("successor-version")
                        && String.valueOf(antigo.cab("Link")).contains("/api/v1/pesquisa/log"),
                "caminho antigo: Deprecation, Sunset e Link rel=successor-version para o novo");
        Resposta novo = chamar("GET", "/api/v1/pesquisa/log", null, "Authorization", "Bearer " + TOKEN);
        conferir(novo.cab("Deprecation") == null && novo.cab("Sunset") == null, "caminho novo: sem marca de depreciação");

        // --- Métricas e log estruturado ---------------------------------------------------------------------------
        Resposta metricas = chamar("GET", "/api/v1/metricas", null, "Authorization", "Bearer " + TOKEN);
        @SuppressWarnings("unchecked")
        Map<String, Object> rotas = (Map<String, Object>) metricas.corpo.get("rotas");
        conferir(metricas.status == 200 && rotas.containsKey("POST /api/v1/tentativas") && rotas.containsKey("GET /api/v1"),
                "as métricas contam por modelo de rota (POST /api/v1/tentativas, GET /api/v1)");
        boolean semExplosao = true;
        for (String chave : rotas.keySet()) {
            semExplosao &= !chave.matches(".*[0-9a-f]{8}-[0-9a-f]{4}-.*");
        }
        conferir(semExplosao, "nenhuma métrica é por id de tentativa");

        System.out.flush();
        System.setOut(original);
        String log = new String(capturado.toByteArray(), StandardCharsets.UTF_8);
        conferir(log.contains("\"request_id\":\"meu-id-123\"") && log.contains("\"status\":404")
                        && log.contains("\"caminho\":\"/api/v1/tentativas\""),
                "o log tem uma linha JSON por requisição (request_id, status, caminho como modelo)");
        conferir(!log.contains(TOKEN) && !log.contains("gerard_sessao"),
                "o log nunca traz token nem cookie de sessão");

        System.out.println(falhas == 0 ? "APROVADO: operação e segurança da API" : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
