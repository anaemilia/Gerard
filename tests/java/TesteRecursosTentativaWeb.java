import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.infraestrutura.web.ServidorPrototipoWeb;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Nível 1 de maturidade: a tentativa é um recurso com identidade. Confere, pelo HTTP real, que (1) criar uma tentativa
 * devolve o id e o Location; (2) toda ação publicada aponta para uma URI de /api/v1/tentativas/{id}/... e a URI responde;
 * (3) um id que não é o da tentativa em curso é 404; (4) as rotas antigas, com verbo na URI, deixaram de existir;
 * (5) método errado num recurso é 405.
 */
public class TesteRecursosTentativaWeb {
    static int falhas;
    static final String BASE = "http://localhost:18085";

    static final class Resposta {
        int status;
        String location;
        Map<String, Object> corpo;
    }

    static String cookie;

    @SuppressWarnings("unchecked")
    static Resposta chamar(String metodo, String caminho, String corpo) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(BASE + caminho).openConnection();
        c.setRequestMethod(metodo);
        if (cookie != null) {
            c.setRequestProperty("Cookie", cookie);
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
        r.location = c.getHeaderField("Location");
        String setCookie = c.getHeaderField("Set-Cookie");
        if (setCookie != null && cookie == null) {
            cookie = setCookie.split(";")[0];
        }
        java.io.InputStream in = r.status < 400 ? c.getInputStream() : c.getErrorStream();
        String json = in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        r.corpo = json.isEmpty() ? null : (Map<String, Object>) AnalisadorJsonSimples.analisar(json);
        return r;
    }

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "[OK] " : "[FALHA] ") + msg);
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> acoes(Map<String, Object> estado) {
        List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();
        for (Object o : (List<Object>) estado.get("acoes_disponiveis")) {
            lista.add((Map<String, Object>) o);
        }
        return lista;
    }

    public static void main(String[] args) throws Exception {
        ServidorPrototipoWeb.main(new String[] {"18085"});

        Resposta criada = chamar("POST", "/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}");
        String id = String.valueOf(criada.corpo.get("tentativa_id"));
        conferir(criada.status == 200 && id.length() > 8, "criar uma tentativa devolve o id (" + id + ")");
        conferir(("/api/v1/tentativas/" + id).equals(criada.location), "Location aponta para a tentativa nova");

        String prefixo = "/api/v1/tentativas/" + id;
        int semRecurso = 0;
        for (Map<String, Object> acao : acoes(criada.corpo)) {
            String href = String.valueOf(acao.get("href"));
            if (!href.equals("/api/v1/tentativas") && !href.startsWith(prefixo + "/")) {
                semRecurso++;
            }
        }
        conferir(semRecurso == 0, "toda ação publicada aponta para a coleção ou para um recurso da tentativa");

        // Segue os hrefs publicados: a escolha de categoria correta tem que responder no recurso da tentativa.
        String categoria = String.valueOf(criada.corpo.get("situacao_id")).replaceAll("^PO_([A-Z_]+?)_[a-z].*$", "$1");
        Resposta classificada = chamar("POST", prefixo + "/classificacao", "{\"categoria\":\"" + categoria + "\"}");
        conferir(classificada.status == 200 && Boolean.TRUE.equals(classificada.corpo.get("correta")),
                "POST em .../classificacao julga a categoria (" + categoria + ")");
        @SuppressWarnings("unchecked")
        Map<String, Object> estado = (Map<String, Object>) classificada.corpo.get("estado");
        int figuras = 0;
        int respondem = 0;
        for (Map<String, Object> acao : acoes(estado)) {
            String id1 = String.valueOf(acao.get("id"));
            if ("POSICIONAR_CONHECIDO".equals(id1) || "ENGATAR_INCOGNITA".equals(id1)) {
                figuras++;
                String href = String.valueOf(acao.get("href"));
                Resposta r = chamar("POST", href, "{}");
                if (r.status == 200 && r.corpo != null && r.corpo.containsKey("estado")) {
                    respondem++;
                }
            }
        }
        conferir(figuras > 0 && figuras == respondem,
                "as ações de posicionar publicadas respondem nos seus recursos (" + respondem + "/" + figuras + ")");

        conferir(chamar("POST", prefixo + "/reinicio", "{}").status == 200, "POST .../reinicio reinicia a tentativa");
        conferir(chamar("POST", "/api/v1/tentativas/outra-tentativa/reinicio", "{}").status == 404,
                "um id que não é o da tentativa em curso é 404");
        conferir(chamar("GET", prefixo + "/reinicio", null).status == 405, "método errado num recurso é 405");
        conferir(chamar("POST", prefixo + "/inexistente", "{}").status == 404, "sub-recurso desconhecido é 404");

        // A tentativa antiga deixa de existir quando se sorteia outra.
        Resposta nova = chamar("POST", "/api/v1/tentativas", "{\"grupo_sorteio\":\"RELACOES\"}");
        conferir(!id.equals(String.valueOf(nova.corpo.get("tentativa_id"))), "um novo sorteio cria outra tentativa");
        conferir(chamar("POST", prefixo + "/reinicio", "{}").status == 404,
                "a tentativa substituída por outro sorteio é 404");

        // Todo sub-recurso de RotasTentativa é reconhecido (a resposta pode ser 200, 400 ou 422, nunca 404 ou 405).
        String novoPrefixo = "/api/v1/tentativas/" + nova.corpo.get("tentativa_id");
        String[][] recursos = {
            {"/classificacao/confirmacao", "{\"concordou\":false}"},
            {"/operacoes", "{\"seletor\":\"x\",\"operacao\":\"SOMA\"}"},
            {"/conclusao", "{\"escolha\":\"NAO\"}"},
            {"/ajudas", "{\"area\":\"TEXTO\",\"intencao\":\"DUVIDA\"}"},
            {"/idioma", "{\"codigo\":\"en\"}"},
            {"/papeis/papel.parte1/posicionamento", "{}"},
            {"/papeis/papel.parte1/valor", "{\"valor\":1}"},
            {"/papeis/papel.parte1/valor/confirmacao", "{\"confirmou\":true,\"valor\":1}"},
            {"/papeis/papel.parte1/quadradinhos", "{\"delta\":1}"},
            {"/papeis/papel.parte1/sinal", "{\"sinal\":\"+\"}"},
            {"/papeis/papel.parte1/eixo/visibilidade", "{\"visivel\":true}"},
            {"/papeis/papel.parte1/eixo/projecao", "{\"valor\":1}"}};
        int naoReconhecidos = 0;
        for (String[] r : recursos) {
            int status = chamar("POST", novoPrefixo + r[0], r[1]).status;
            if (status == 404 || status == 405) {
                naoReconhecidos++;
                System.out.println("   não reconhecido: " + r[0] + " -> " + status);
            }
        }
        conferir(naoReconhecidos == 0, "os " + recursos.length + " sub-recursos restantes são reconhecidos");

        // A pessoa é o recurso: /api/v1/usuarios/{id}.
        Resposta pessoa = chamar("POST", "/api/v1/usuarios", "{\"nome\":\"Teste Recursos\",\"idade\":30,"
                + "\"sexo\":\"FEMININO\",\"midia_preferida\":\"GRAFICO\",\"nivel_escolaridade\":\"GRADUACAO\"}");
        String idPessoa = String.valueOf(pessoa.corpo.get("id"));
        conferir(pessoa.status == 201 && idPessoa.length() > 3, "criar uma pessoa devolve 201 e o id");
        Resposta editada = chamar("PUT", "/api/v1/usuarios/" + idPessoa, "{\"nome\":\"Teste Recursos 2\",\"idade\":31,"
                + "\"sexo\":\"FEMININO\",\"midia_preferida\":\"GRAFICO\",\"nivel_escolaridade\":\"GRADUACAO\"}");
        conferir(editada.status == 200, "PUT /api/v1/usuarios/{id} edita a pessoa (" + editada.status + ")");
        conferir(chamar("PUT", "/api/v1/usuarios", "{}").status == 404, "PUT sem o id da pessoa na URI é 404");

        for (String antiga : new String[] {"/api/sorteios/medidas", "/api/sorteios/relacoes", "/api/reiniciar",
                "/api/classificacao/categoria", "/api/classificacao/confirmacao", "/api/acoes/posicionar",
                "/api/acoes/soltar-numero", "/api/acoes/escolher-operacao", "/api/acoes/conclusao",
                "/api/acoes/quadradinho", "/api/acoes/escolher-sinal", "/api/acoes/revelar-eixo",
                "/api/acoes/ocultar-eixo", "/api/acoes/projetar-eixo", "/api/acoes/ajuda-contextual",
                "/api/acoes/responder-confirmacao-valor", "/api/situacao/idioma", "/api/gestos"}) {
            int status = chamar("POST", antiga, "{}").status;
            conferir(status == 404, "a rota com verbo " + antiga + " deixou de existir (" + status + ")");
        }
        conferir(chamar("GET", "/api/situacao", null).status == 404, "GET /api/situacao (que sorteava) deixou de existir");

        System.out.println(falhas == 0 ? "APROVADO: a tentativa é um recurso com identidade"
                : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
