package gerard.infraestrutura.web;

import gerard.agente.conhecimento.AnalisadorJsonSimples;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * O contrato (OpenAPI 3.1) e o servidor não podem divergir: o documento servido é o do repositório
 * ({@code documentacao/api/openapi.json}) — mudar a API exige mudar o contrato, de propósito — e cada caminho e método
 * documentado existe no servidor. {@code -Dgerard.openapi.gravar=true} regrava o arquivo do repositório.
 */
public class TesteContratoOpenApiWeb {
    static int falhas;
    static final String BASE = "http://localhost:18087";
    static String cookie;
    static String etag;

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "[OK] " : "[FALHA] ") + msg);
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> chamar(String metodo, String caminho, String corpo, int[] status) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(BASE + caminho).openConnection();
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
        status[0] = c.getResponseCode();
        String novo = c.getHeaderField("ETag");
        if (novo != null) {
            etag = novo;
        }
        String setCookie = c.getHeaderField("Set-Cookie");
        if (setCookie != null && cookie == null) {
            cookie = setCookie.split(";")[0];
        }
        java.io.InputStream in = status[0] < 400 ? c.getInputStream() : c.getErrorStream();
        String json = in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        return json.isEmpty() || status[0] == 304 ? null : (Map<String, Object>) AnalisadorJsonSimples.analisar(json);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        ServidorPrototipoWeb.main(new String[] {"18087"});
        int[] s = new int[1];

        Map<String, Object> servido = chamar("GET", "/api/v1/openapi.json", null, s);
        conferir(s[0] == 200 && "3.1.0".equals(servido.get("openapi")), "GET /api/v1/openapi.json serve um OpenAPI 3.1");
        Map<String, Object> caminhos = (Map<String, Object>) servido.get("paths");
        conferir(caminhos.size() >= 20, "o contrato descreve " + caminhos.size() + " caminhos");

        // O documento servido é o do repositório.
        File arquivo = new File("documentacao/api/openapi.json");
        String gerado = DocumentoOpenApi.escrever(DocumentoOpenApi.gerar());
        if (Boolean.getBoolean("gerard.openapi.gravar") || !arquivo.exists()) {
            arquivo.getParentFile().mkdirs();
            Files.write(arquivo.toPath(), gerado.getBytes(StandardCharsets.UTF_8));
            System.out.println("   (contrato gravado em " + arquivo + ")");
        }
        String doRepositorio = new String(Files.readAllBytes(arquivo.toPath()), StandardCharsets.UTF_8);
        conferir(doRepositorio.replace("\r\n", "\n").equals(gerado),
                "documentacao/api/openapi.json é o contrato atual (mudou a API? regrave com -Dgerard.openapi.gravar=true)");
        conferir(DocumentoOpenApi.escrever(servido).equals(gerado), "o que o servidor serve, relido, é o que está no repositório");

        // Cada caminho e método documentado existe no servidor.
        int[] st = new int[1];
        Map<String, Object> estado = chamar("POST", "/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}", st);
        String id = String.valueOf(estado.get("tentativa_id"));
        int inexistentes = 0;
        int operacoes = 0;
        for (Map.Entry<String, Object> caminho : caminhos.entrySet()) {
            String real = caminho.getKey().replace("{id}", caminho.getKey().startsWith("/api/v1/usuarios") ? "x" : id)
                    .replace("{papel}", "papel.parte1").replace("{slug}", "versao-desatualizada");
            for (Map.Entry<String, Object> metodo : ((Map<String, Object>) caminho.getValue()).entrySet()) {
                operacoes++;
                String verbo = metodo.getKey().toUpperCase();
                if ("POST".equals(verbo) && real.equals("/api/v1/tentativas")) {
                    continue; // já exercitado acima; repetir trocaria a tentativa
                }
                chamar(verbo, real, "{}", st);
                // 404 só é legítimo para "pessoa x" e problema sem cadastro; 405 nunca.
                boolean legitimo404 = real.endsWith("/usuarios/x");
                if (st[0] == 405 || (st[0] == 404 && !legitimo404)) {
                    inexistentes++;
                    System.out.println("   documentado e não atendido: " + verbo + " " + real + " -> " + st[0]);
                }
            }
        }
        conferir(inexistentes == 0, "as " + operacoes + " operações documentadas existem no servidor");

        Map<String, Object> entrada = chamar("GET", "/api/v1", null, st);
        Map<String, Object> links = (Map<String, Object>) entrada.get("_links");
        conferir(links.containsKey("openapi") && links.containsKey("metricas"),
                "a entrada da API aponta para o contrato e para as métricas");

        System.out.println(falhas == 0 ? "APROVADO: o contrato e o servidor dizem a mesma coisa"
                : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
