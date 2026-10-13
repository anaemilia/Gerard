import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.infraestrutura.web.ServidorPrototipoWeb;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Níveis 1 e 2 de maturidade, pelo HTTP real: a tentativa é um recurso com identidade e cada linha desta tabela fixa a
 * semântica de uma rota — verbo, status, cabeçalhos e o tipo do problema quando falha (application/problem+json).
 */
public class TesteRecursosTentativaWeb {
    static int falhas;
    static final String BASE = "http://localhost:18085";

    static final class Resposta {
        int status;
        String tipoDeConteudo;
        Map<String, String> cabecalhos = new java.util.HashMap<String, String>();
        Map<String, Object> corpo;

        String tipoDoProblema() {
            return corpo == null ? null : String.valueOf(corpo.get("type"));
        }
    }

    static final class Visitante {
        String cookie;
        String etag;
        static final String AUTO = "\u0000auto";

        /** ifMatch: AUTO usa o último ETag lido; null não envia; outro valor é enviado como está. */
        @SuppressWarnings("unchecked")
        Resposta chamar(String metodo, String caminho, String corpo, String ifMatch, String tipoDeConteudo,
                String ifNoneMatch) throws Exception {
            HttpURLConnection c = (HttpURLConnection) new URL(BASE + caminho).openConnection();
            c.setRequestMethod(metodo);
            if (cookie != null) {
                c.setRequestProperty("Cookie", cookie);
            }
            String enviar = AUTO.equals(ifMatch) ? etag : ifMatch;
            if (enviar != null) {
                c.setRequestProperty("If-Match", enviar);
            }
            if (ifNoneMatch != null) {
                c.setRequestProperty("If-None-Match", ifNoneMatch);
            }
            if (corpo != null) {
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type", tipoDeConteudo);
                try (OutputStream out = c.getOutputStream()) {
                    out.write(corpo.getBytes(StandardCharsets.UTF_8));
                }
            }
            Resposta r = new Resposta();
            r.status = c.getResponseCode();
            r.tipoDeConteudo = c.getHeaderField("Content-Type");
            for (String nome : new String[] {"Location", "ETag", "Allow", "Retry-After"}) {
                String valor = c.getHeaderField(nome);
                if (valor != null) {
                    r.cabecalhos.put(nome, valor);
                }
            }
            if (r.cabecalhos.containsKey("ETag") && caminho.startsWith("/api/v1/tentativas")) {
                etag = r.cabecalhos.get("ETag");
            }
            String setCookie = c.getHeaderField("Set-Cookie");
            if (setCookie != null && cookie == null) {
                cookie = setCookie.split(";")[0];
            }
            java.io.InputStream in = r.status < 400 ? c.getInputStream() : c.getErrorStream();
            String json = in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8);
            r.corpo = json.isEmpty() ? null : (Map<String, Object>) AnalisadorJsonSimples.analisar(json);
            return r;
        }

        Resposta json(String metodo, String caminho, String corpo) throws Exception {
            return chamar(metodo, caminho, corpo, AUTO, "application/json", null);
        }
    }

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "[OK] " : "[FALHA] ") + msg);
    }

    static boolean problema(Resposta r, int status, String slug) {
        return r.status == status && r.tipoDeConteudo != null && r.tipoDeConteudo.startsWith("application/problem+json")
                && ("/api/v1/problemas/" + slug).equals(r.tipoDoProblema());
    }

    public static void main(String[] args) throws Exception {
        ServidorPrototipoWeb.main(new String[] {"18085"});
        Visitante v = new Visitante();

        // --- A coleção: criar uma tentativa ---------------------------------------------------------------------
        Resposta criada = v.json("POST", "/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}");
        String id = String.valueOf(criada.corpo.get("tentativa_id"));
        String t = "/api/v1/tentativas/" + id;
        conferir(criada.status == 201, "POST /tentativas cria: 201");
        conferir(t.equals(criada.cabecalhos.get("Location")), "POST /tentativas devolve Location da nova tentativa");
        conferir(criada.cabecalhos.containsKey("ETag"), "POST /tentativas devolve o ETag da versão inicial");
        conferir(problema(v.json("POST", "/api/v1/tentativas", "{\"grupo_sorteio\":\"XX\"}"), 422, "dados-invalidos"),
                "grupo_sorteio inválido: 422 dados-invalidos");
        conferir(problema(v.chamar("POST", "/api/v1/tentativas", "{nao-json", null, "application/json", null), 400,
                "corpo-invalido"), "JSON inválido: 400 corpo-invalido");
        conferir(problema(v.chamar("POST", "/api/v1/tentativas", "x", null, "text/plain", null), 415,
                "tipo-de-midia-nao-suportado"), "corpo que não é JSON: 415");
        Resposta getColecao = v.chamar("GET", "/api/v1/tentativas", null, null, null, null);
        conferir(problema(getColecao, 405, "metodo-nao-permitido") && "POST".equals(getColecao.cabecalhos.get("Allow")),
                "GET na coleção: 405 com Allow: POST");

        // Outro sorteio substitui a tentativa: a primeira passa a ser 410, não 404.
        String tAntiga = t;
        Resposta atual = v.json("POST", "/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}");
        conferir(problema(v.chamar("GET", tAntiga, null, null, null, null), 410, "tentativa-substituida"),
                "tentativa que outro sorteio substituiu: 410 tentativa-substituida");
        id = String.valueOf(atual.corpo.get("tentativa_id"));
        t = "/api/v1/tentativas/" + id;

        // --- GET lê e não altera ---------------------------------------------------------------------------------
        Resposta leitura1 = v.chamar("GET", t, null, null, null, null);
        String etagLido = leitura1.cabecalhos.get("ETag");
        Resposta leitura2 = v.chamar("GET", t, null, null, null, null);
        conferir(leitura1.status == 200 && etagLido != null && etagLido.equals(leitura2.cabecalhos.get("ETag")),
                "GET /tentativas/{id} lê e é seguro: dois GETs, mesmo ETag");
        conferir(v.chamar("GET", t, null, null, null, etagLido).status == 304, "If-None-Match com o ETag atual: 304");

        // --- Alterar exige If-Match ------------------------------------------------------------------------------
        String categoria = String.valueOf(atual.corpo.get("situacao_id")).replaceAll("^PO_([A-Z_]+?)_[a-z].*$", "$1");
        String corpoCategoria = "{\"categoria\":\"" + categoria + "\"}";
        conferir(problema(v.chamar("POST", t + "/classificacao", corpoCategoria, null, "application/json", null), 428,
                "precondicao-obrigatoria"), "alterar sem If-Match: 428");
        Resposta velha = v.chamar("POST", t + "/classificacao", corpoCategoria, "\"" + id + "-999\"", "application/json", null);
        conferir(problema(velha, 412, "versao-desatualizada") && velha.cabecalhos.containsKey("ETag"),
                "If-Match que não é o atual: 412, com o ETag verdadeiro no cabeçalho");
        conferir(problema(v.json("POST", t + "/classificacao", "{\"categoria\":\"NAO_EXISTE\"}"), 422, "dados-invalidos"),
                "categoria que não existe: 422");
        String etagAntes = v.etag;
        Resposta classificada = v.json("POST", t + "/classificacao", corpoCategoria);
        conferir(classificada.status == 200 && Boolean.TRUE.equals(classificada.corpo.get("correta")),
                "POST .../classificacao com If-Match certo: 200 e julga");
        conferir(!etagAntes.equals(classificada.cabecalhos.get("ETag")), "a alteração muda o ETag");
        conferir(problema(v.chamar("POST", t + "/classificacao", corpoCategoria, etagAntes, "application/json", null),
                412, "versao-desatualizada"), "repetir com o ETag antigo (outra aba): 412");

        // --- Ação incompatível com o estado ---------------------------------------------------------------------
        conferir(problema(v.json("POST", t + "/classificacao/confirmacao", "{\"concordou\":true}"), 409,
                "acao-incompativel-com-o-estado"), "confirmar o que não foi perguntado: 409");

        // --- PUT é idempotente ----------------------------------------------------------------------------------
        Resposta idioma1 = v.json("PUT", t + "/idioma", "{\"codigo\":\"pt-BR\"}");
        Resposta idioma2 = v.json("PUT", t + "/idioma", "{\"codigo\":\"pt-BR\"}");
        conferir(idioma1.status == 200 && idioma2.status == 200
                        && String.valueOf(idioma1.corpo.get("enunciado")).equals(String.valueOf(idioma2.corpo.get("enunciado"))),
                "PUT .../idioma repetido: mesmo resultado");
        Resposta postIdioma = v.json("POST", t + "/idioma", "{\"codigo\":\"pt-BR\"}");
        conferir(problema(postIdioma, 405, "metodo-nao-permitido") && "PUT".equals(postIdioma.cabecalhos.get("Allow")),
                "POST em recurso de PUT: 405 com Allow: PUT");
        int status = v.json("PUT", t + "/papeis/papel.parte1/eixo/visibilidade", "{\"visivel\":false}").status;
        conferir(status != 404 && status != 405, "PUT .../eixo/visibilidade é reconhecido (" + status + ")");

        // --- Cálculo puro: não exige nem avança a versão --------------------------------------------------------
        String etagPuro = v.etag;
        int statusProjecao = v.chamar("POST", t + "/papeis/papel.parte1/eixo/projecao", "{\"valor\":1}", null,
                "application/json", null).status;
        conferir(statusProjecao != 428 && statusProjecao != 405 && statusProjecao != 404,
                "POST .../eixo/projecao sem If-Match não é barrado (" + statusProjecao + ")");
        conferir(etagPuro.equals(v.chamar("GET", t, null, null, null, null).cabecalhos.get("ETag")),
                "a projeção no eixo não muda a versão da tentativa");

        // --- Reconhecimento dos demais sub-recursos e dos que não existem ---------------------------------------
        String[][] outros = {
            {"POST", "/operacoes", "{\"seletor\":\"x\",\"operacao\":\"SOMA\"}"},
            {"POST", "/conclusao", "{\"escolha\":\"NAO\"}"},
            {"POST", "/ajudas", "{\"area\":\"TEXTO\",\"intencao\":\"DUVIDA\"}"},
            {"POST", "/papeis/papel.parte1/posicionamento", "{}"},
            {"POST", "/papeis/papel.parte1/valor", "{\"valor\":1}"},
            {"POST", "/papeis/papel.parte1/valor/confirmacao", "{\"confirmou\":true,\"valor\":1}"},
            {"POST", "/papeis/papel.parte1/quadradinhos", "{\"delta\":1}"},
            {"POST", "/papeis/papel.parte1/sinal", "{\"sinal\":\"+\"}"},
            {"POST", "/reinicio", "{}"}};
        int naoReconhecidos = 0;
        for (String[] r : outros) {
            int s = v.json(r[0], t + r[1], r[2]).status;
            if (s == 404 || s == 405 || s == 428) {
                naoReconhecidos++;
                System.out.println("   não reconhecido: " + r[0] + " " + r[1] + " -> " + s);
            }
        }
        conferir(naoReconhecidos == 0, "os " + outros.length + " sub-recursos de ação são reconhecidos");
        conferir(problema(v.json("POST", t + "/inexistente", "{}"), 404, "recurso-inexistente"),
                "sub-recurso desconhecido: 404 recurso-inexistente");
        conferir(problema(v.chamar("GET", "/api/v1/tentativas/nunca-existiu", null, null, null, null), 404,
                "tentativa-inexistente"), "id que nunca existiu: 404 tentativa-inexistente");

        // --- O catálogo dos problemas é dereferenciável ---------------------------------------------------------
        Resposta tipo = v.chamar("GET", "/api/v1/problemas/versao-desatualizada", null, null, null, null);
        conferir(tipo.status == 200 && ((Number) tipo.corpo.get("status")).intValue() == 412,
                "GET /problemas/{slug} descreve o tipo citado em \"type\"");
        conferir(v.chamar("GET", "/api/v1/problemas/nao-existe", null, null, null, null).status == 404,
                "tipo de problema inexistente: 404");

        // --- Pessoa -----------------------------------------------------------------------------------------------
        Resposta pessoa = v.json("POST", "/api/v1/usuarios", "{\"nome\":\"Teste Recursos\",\"idade\":30,"
                + "\"sexo\":\"FEMININO\",\"midia_preferida\":\"GRAFICO\",\"nivel_escolaridade\":\"GRADUACAO\"}");
        String idPessoa = String.valueOf(pessoa.corpo.get("id"));
        conferir(pessoa.status == 201 && idPessoa.length() > 3, "criar uma pessoa: 201 e o id");
        conferir(v.json("PUT", "/api/v1/usuarios/" + idPessoa, "{\"nome\":\"Teste Recursos 2\",\"idade\":31,"
                + "\"sexo\":\"FEMININO\",\"midia_preferida\":\"GRAFICO\",\"nivel_escolaridade\":\"GRADUACAO\"}").status == 200,
                "PUT /usuarios/{id} edita a pessoa: 200");
        conferir(v.json("PUT", "/api/v1/usuarios", "{}").status == 404, "PUT sem o id da pessoa na URI: 404");

        // --- Rotas antigas, com verbo na URI, não existem mais -------------------------------------------------
        int antigasVivas = 0;
        for (String antiga : new String[] {"/api/sorteios/medidas", "/api/reiniciar", "/api/classificacao/categoria",
                "/api/acoes/posicionar", "/api/acoes/soltar-numero", "/api/acoes/escolher-operacao", "/api/acoes/conclusao",
                "/api/situacao", "/api/situacao/idioma", "/api/gestos"}) {
            if (v.json("POST", antiga, "{}").status != 404) {
                antigasVivas++;
            }
        }
        conferir(antigasVivas == 0, "as rotas com verbo na URI deixaram de existir");

        // --- Limite de requisições por sessão: 429 com Retry-After ---------------------------------------------
        Visitante apressado = new Visitante();
        Resposta base = apressado.json("POST", "/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}");
        String tApressado = "/api/v1/tentativas/" + base.corpo.get("tentativa_id");
        Resposta barrada = null;
        for (int i = 0; i < 150 && barrada == null; i++) {
            Resposta r = apressado.chamar("GET", tApressado, null, null, null, null);
            if (r.status == 429) {
                barrada = r;
            }
        }
        conferir(barrada != null && problema(barrada, 429, "muitas-requisicoes")
                        && barrada.cabecalhos.containsKey("Retry-After"),
                "rajada acima do limite da sessão: 429 com Retry-After");

        System.out.println(falhas == 0 ? "APROVADO: a tentativa é um recurso, com a semântica HTTP de cada rota"
                : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
