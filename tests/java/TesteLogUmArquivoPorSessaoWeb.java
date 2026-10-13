import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.infraestrutura.web.ServidorPrototipoWeb;
import gerard.pesquisador.log.LoggerInteracaoGerard;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * O log da pesquisa é um arquivo só (o do processo), mas cada linha carrega o usuário e o contexto do visitante que a
 * originou: duas pessoas simultâneas, cada uma com o seu usuário, não podem aparecer misturadas nas linhas um do outro.
 */
public class TesteLogUmArquivoPorSessaoWeb {
    static int falhas;
    static final String BASE = "http://localhost:18084";

    static final class Visitante {
        String cookie;

        @SuppressWarnings("unchecked")
        Map<String, Object> chamar(String metodo, String caminho, String corpo) throws Exception {
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
            String setCookie = c.getHeaderField("Set-Cookie");
            if (setCookie != null && cookie == null) {
                cookie = setCookie.split(";")[0];
            }
            java.io.InputStream in = c.getResponseCode() < 400 ? c.getInputStream() : c.getErrorStream();
            return (Map<String, Object>) AnalisadorJsonSimples.analisar(new String(in.readAllBytes(), StandardCharsets.UTF_8));
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

    @SuppressWarnings("unchecked")
    static String criarUsuario(Visitante v, String nome) throws Exception {
        Map<String, Object> perfil = v.chamar("POST", "/api/usuarios", "{\"nome\":\"" + nome
                + "\",\"idade\":30,\"sexo\":\"FEMININO\",\"midia_preferida\":\"GRAFICO\","
                + "\"nivel_escolaridade\":\"GRADUACAO\"}");
        return String.valueOf(perfil.get("usuario_id") != null ? perfil.get("usuario_id") : perfil.get("id"));
    }

    public static void main(String[] args) throws Exception {
        ServidorPrototipoWeb.main(new String[] {"18084"});
        Visitante a = new Visitante();
        Visitante b = new Visitante();
        String idA = criarUsuario(a, "Teste Log A");
        String idB = criarUsuario(b, "Teste Log B");
        a.chamar("POST", "/api/sessao/usuario", "{\"usuario_id\":\"" + idA + "\"}");
        b.chamar("POST", "/api/sessao/usuario", "{\"usuario_id\":\"" + idB + "\"}");

        String situacaoA = null;
        String situacaoB = null;
        for (int i = 0; i < 12; i++) {
            Map<String, Object> estadoA = a.chamar("POST", "/api/sorteios/medidas", "{}");
            Map<String, Object> estadoB = b.chamar("POST", "/api/sorteios/medidas", "{}");
            situacaoA = String.valueOf(estadoA.get("situacao_id"));
            situacaoB = String.valueOf(estadoB.get("situacao_id"));
            if (!categoriaDe(situacaoA).equals(categoriaDe(situacaoB))) {
                break;
            }
        }
        // As ações se alternam: A classifica depois de B ter sorteado.
        a.chamar("POST", "/api/classificacao/categoria", "{\"categoria\":\"" + categoriaDe(situacaoA) + "\"}");
        b.chamar("POST", "/api/classificacao/categoria", "{\"categoria\":\"" + categoriaDe(situacaoB) + "\"}");

        File arquivo = LoggerInteracaoGerard.getInstancia().getArquivoSessao();
        List<String[]> linhas = new ArrayList<String[]>();
        for (String linha : Files.readAllLines(arquivo.toPath(), StandardCharsets.UTF_8)) {
            linhas.add(linha.split("\t", -1));
        }
        int deA = 0;
        int deB = 0;
        int usuarioTrocado = 0;
        for (String[] l : linhas) {
            if (l.length < 6) {
                continue;
            }
            if (l[5].equals(situacaoA) && !l[2].equals("usuario_local")) {
                deA++;
                if (!l[2].equals(idA)) {
                    usuarioTrocado++;
                }
            }
            if (l[5].equals(situacaoB) && !l[2].equals("usuario_local")) {
                deB++;
                if (!l[2].equals(idB)) {
                    usuarioTrocado++;
                }
            }
        }
        conferir(deA > 0 && deB > 0, "há linhas das duas situações no mesmo arquivo (A=" + deA + ", B=" + deB + ")");
        conferir(usuarioTrocado == 0, "toda linha de uma situação traz o usuário do visitante dela (trocadas: "
                + usuarioTrocado + ")");
        System.out.println(falhas == 0 ? "APROVADO: um arquivo, contexto por visitante"
                : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
