import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.infraestrutura.web.ServidorPrototipoWeb;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dois visitantes simultâneos no mesmo servidor não podem interferir um no outro: a resposta de A é julgada contra a
 * situação de A, mesmo que B tenha sorteado outra depois. Passa pelo HTTP real, com um cookie por visitante.
 */
public class TesteIsolamentoSessoesWeb {
    static int falhas;
    static final String BASE = "http://localhost:18083";

    static final class Visitante {
        String cookie;
        String etag;

        Map<String, Object> post(String caminho, String corpo) throws Exception {
            HttpURLConnection c = (HttpURLConnection) new URL(BASE + caminho).openConnection();
            c.setRequestMethod("POST");
            c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "application/json");
            if (cookie != null) {
                c.setRequestProperty("Cookie", cookie);
            }
            if (etag != null && caminho.matches("/api/v1/tentativas/[^/]+/.*")) {
                c.setRequestProperty("If-Match", etag);
            }
            try (OutputStream out = c.getOutputStream()) {
                out.write(corpo.getBytes(StandardCharsets.UTF_8));
            }
            String novoEtag = c.getHeaderField("ETag");
            if (novoEtag != null) {
                etag = novoEtag;
            }
            String setCookie = c.getHeaderField("Set-Cookie");
            if (setCookie != null && cookie == null) {
                cookie = setCookie.split(";")[0];
            }
            java.io.InputStream in = c.getResponseCode() < 400 ? c.getInputStream() : c.getErrorStream();
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> mapa = (Map<String, Object>) AnalisadorJsonSimples.analisar(json);
            return mapa;
        }
    }

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "[OK] " : "[FALHA] ") + msg);
    }

    static String categoriaDe(Object situacaoId) {
        Matcher m = Pattern.compile("^PO_([A-Z_]+?)_[a-z]").matcher(String.valueOf(situacaoId));
        m.find();
        return m.group(1);
    }

    public static void main(String[] args) throws Exception {
        ServidorPrototipoWeb.main(new String[] {"18083"});
        Visitante a = new Visitante();
        Visitante b = new Visitante();
        int comparaveis = 0;
        int julgamentosCruzados = 0;
        for (int i = 0; i < 12 && comparaveis < 5; i++) {
            Map<String, Object> estadoA = a.post("/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}");
            Map<String, Object> estadoB = b.post("/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}");
            String catA = categoriaDe(estadoA.get("situacao_id"));
            String catB = categoriaDe(estadoB.get("situacao_id"));
            if (catA.equals(catB)) {
                continue;
            }
            comparaveis++;
            Map<String, Object> resultado = a.post("/api/v1/tentativas/" + estadoA.get("tentativa_id") + "/classificacao", "{\"categoria\":\"" + catA + "\"}");
            if (!Boolean.TRUE.equals(resultado.get("correta"))) {
                julgamentosCruzados++;
            }
        }
        System.out.println("cookieA=" + a.cookie + " cookieB=" + b.cookie); conferir(a.cookie != null && b.cookie != null && !a.cookie.equals(b.cookie),
                "cada visitante recebe um cookie de sessão próprio");
        conferir(comparaveis >= 3, "houve rodadas com situações de categorias diferentes (" + comparaveis + ")");
        conferir(julgamentosCruzados == 0,
                "a resposta de A é julgada contra a situação de A (cruzados: " + julgamentosCruzados + ")");

        Visitante semCookie = new Visitante();
        Map<String, Object> primeiro = semCookie.post("/api/v1/tentativas", "{\"grupo_sorteio\":\"MEDIDAS\"}");
        conferir(semCookie.cookie != null && primeiro.get("situacao_id") != null,
                "quem chega sem cookie recebe uma sessão nova");

        System.out.println(falhas == 0 ? "APROVADO: sessões isoladas por visitante" : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
