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
 * Nível 3 de maturidade (hipermídia), pelo HTTP real. O teste só conhece a entrada, GET /api/v1, e tudo o mais ele
 * alcança seguindo links — com o recurso das tentativas RENOMEADO no servidor (propriedade
 * gerard.api.recurso.tentativas=percursos). Se a API exigisse que o cliente soubesse o caminho, isto quebraria.
 */
public class TesteHipermidiaWeb {
    static int falhas;
    static final String BASE = "http://localhost:18086";
    static String cookie;
    static String etag;

    static final class Resposta {
        int status;
        String tipoDeConteudo;
        Map<String, Object> corpo;
    }

    @SuppressWarnings("unchecked")
    static Resposta seguir(Map<String, Object> link, String corpo) throws Exception {
        String metodo = link.get("metodo") == null ? "GET" : String.valueOf(link.get("metodo"));
        HttpURLConnection c = (HttpURLConnection) new URL(BASE + link.get("href")).openConnection();
        c.setRequestMethod(metodo);
        if (cookie != null) {
            c.setRequestProperty("Cookie", cookie);
        }
        if (etag != null && !"GET".equals(metodo)) {
            c.setRequestProperty("If-Match", etag);
        }
        if (corpo != null && !"GET".equals(metodo)) {
            c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "application/json");
            try (OutputStream out = c.getOutputStream()) {
                out.write(corpo.getBytes(StandardCharsets.UTF_8));
            }
        }
        Resposta r = new Resposta();
        r.status = c.getResponseCode();
        r.tipoDeConteudo = c.getHeaderField("Content-Type");
        String novo = c.getHeaderField("ETag");
        if (novo != null) {
            etag = novo;
        }
        String setCookie = c.getHeaderField("Set-Cookie");
        if (setCookie != null && cookie == null) {
            cookie = setCookie.split(";")[0];
        }
        java.io.InputStream in = r.status < 400 ? c.getInputStream() : c.getErrorStream();
        String json = in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        r.corpo = json.isEmpty() || r.status == 304 ? null : (Map<String, Object>) AnalisadorJsonSimples.analisar(json);
        return r;
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> links(Map<String, Object> recurso) {
        return (Map<String, Object>) recurso.get("_links");
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> link(Map<String, Object> recurso, String rel) {
        return (Map<String, Object>) links(recurso).get(rel);
    }

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "[OK] " : "[FALHA] ") + msg);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        System.setProperty("gerard.api.recurso.tentativas", "percursos");
        ServidorPrototipoWeb.main(new String[] {"18086"});

        // --- A entrada ------------------------------------------------------------------------------------------
        Map<String, Object> entrada = new java.util.LinkedHashMap<String, Object>();
        entrada.put("href", "/api/v1");
        Resposta raiz = seguir(entrada, null);
        conferir(raiz.status == 200 && raiz.tipoDeConteudo.startsWith("application/hal+json"),
                "GET /api/v1 devolve a entrada em application/hal+json");
        String[] rels = {"self", "tentativas", "usuarios", "cadastrar_usuario", "sessao_usuario", "entrar", "relatos",
            "sincronizacoes", "curadoria_situacoes", "problemas"};
        boolean todos = true;
        for (String rel : rels) {
            todos &= link(raiz.corpo, rel) != null;
        }
        conferir(todos, "a entrada publica os " + rels.length + " links de onde tudo parte");
        conferir(String.valueOf(link(raiz.corpo, "tentativas").get("href")).equals("/api/v1/percursos"),
                "o recurso renomeado no servidor aparece no link, sem o cliente saber (/api/v1/percursos)");

        // --- Daí em diante, só links ----------------------------------------------------------------------------
        Resposta criada = seguir(link(raiz.corpo, "tentativas"), "{\"grupo_sorteio\":\"MEDIDAS\"}");
        conferir(criada.status == 201 && criada.tipoDeConteudo.startsWith("application/hal+json"),
                "seguir o link \"tentativas\" cria a tentativa (201, hal+json)");
        for (String rel : new String[] {"self", "tentativas", "idioma", "ajudas"}) {
            conferir(link(criada.corpo, rel) != null, "o estado da tentativa traz o link \"" + rel + "\"");
        }
        String categoria = String.valueOf(criada.corpo.get("situacao_id")).replaceAll("^PO_([A-Z_]+?)_[a-z].*$", "$1");
        Map<String, Object> escolha = null;
        for (Object o : (List<Object>) criada.corpo.get("acoes_disponiveis")) {
            Map<String, Object> acao = (Map<String, Object>) o;
            if ("ESCOLHER_CATEGORIA".equals(acao.get("id"))
                    && categoria.equals(((Map<String, Object>) acao.get("corpo")).get("categoria"))) {
                escolha = acao;
            }
        }
        Map<String, Object> linkEscolha = new java.util.LinkedHashMap<String, Object>();
        linkEscolha.put("href", escolha.get("href"));
        linkEscolha.put("metodo", escolha.get("metodo"));
        Resposta classificada = seguir(linkEscolha, "{\"categoria\":\"" + categoria + "\"}");
        conferir(classificada.status == 200 && Boolean.TRUE.equals(classificada.corpo.get("correta")),
                "seguir a ação publicada (acoes_disponiveis) classifica");
        Map<String, Object> estado = (Map<String, Object>) classificada.corpo.get("estado");

        // Cada figura com papel publica os recursos do papel; todos têm que responder (nunca 404 ou 405).
        List<Object> figuras = (List<Object>) ((Map<String, Object>) estado.get("cena")).get("figuras");
        int comLinks = 0;
        int semResposta = 0;
        List<String> relsDaFigura = new ArrayList<String>();
        for (Object o : figuras) {
            Map<String, Object> figura = (Map<String, Object>) o;
            if (figura.get("chave_papel_semantico") == null) {
                continue;
            }
            comLinks++;
            Map<String, Object> l = links(figura);
            for (String rel : new String[] {"posicionamento", "valor", "confirmacao_valor", "quadradinhos", "sinal",
                "eixo_visibilidade", "eixo_projecao"}) {
                Map<String, Object> alvo = (Map<String, Object>) l.get(rel);
                if (alvo == null) {
                    semResposta++;
                    continue;
                }
                int status = seguir(alvo, "{\"valor\":1,\"delta\":1,\"sinal\":\"+\",\"visivel\":true,\"confirmou\":true}").status;
                if (status == 404 || status == 405) {
                    semResposta++;
                    relsDaFigura.add(rel + "->" + status);
                }
            }
        }
        conferir(comLinks > 0 && semResposta == 0,
                "os links de papel das " + comLinks + " figuras respondem " + relsDaFigura);

        // Os links do próprio estado: ler a tentativa, o idioma e a ajuda.
        Resposta lida = seguir(link(estado, "self"), null);
        conferir(lida.status == 200, "seguir \"self\" lê a tentativa");
        conferir(seguir(link(lida.corpo, "idioma"), "{\"codigo\":\"pt-BR\"}").status == 200, "seguir \"idioma\" (PUT) grava o idioma");

        // --- A pessoa traz o link de edição ----------------------------------------------------------------------
        Resposta pessoa = seguir(link(raiz.corpo, "cadastrar_usuario"), "{\"nome\":\"Teste Links\",\"idade\":30,"
                + "\"sexo\":\"FEMININO\",\"midia_preferida\":\"GRAFICO\",\"nivel_escolaridade\":\"GRADUACAO\"}");
        conferir(pessoa.status == 201 && link(pessoa.corpo, "editar") != null,
                "criar uma pessoa devolve o link \"editar\" dela");
        Resposta editada = seguir(link(pessoa.corpo, "editar"), "{\"nome\":\"Teste Links 2\",\"idade\":31,"
                + "\"sexo\":\"FEMININO\",\"midia_preferida\":\"GRAFICO\",\"nivel_escolaridade\":\"GRADUACAO\"}");
        conferir(editada.status == 200, "seguir \"editar\" (PUT) edita a pessoa");

        // --- Nada que o servidor não reconheça cai em 404 vazio ---------------------------------------------------
        Map<String, Object> inexistente = new java.util.LinkedHashMap<String, Object>();
        inexistente.put("href", "/api/v1/nada-aqui");
        Resposta nada = seguir(inexistente, null);
        conferir(nada.status == 404 && nada.tipoDeConteudo.startsWith("application/problem+json"),
                "caminho desconhecido sob /api/v1: 404 em problem+json");

        System.out.println(falhas == 0 ? "APROVADO: a API se percorre só por links, a partir da entrada"
                : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
