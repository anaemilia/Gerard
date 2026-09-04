package gerard.infraestrutura.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividadeWeb;
import gerard.pesquisador.auditoria.EscritorJsonSimples;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/** Servidor local da prova funcional; HTTP e arquivos ficam na infraestrutura. */
public final class ServidorPrototipoWeb {
    private final ServicoSorteioAtividadeWeb sorteios =
            new ServicoSorteioAtividadeWeb();
    private final Path raizWeb;
    private final AtomicLong gestosRecebidos = new AtomicLong();

    private ServidorPrototipoWeb(Path raizWeb) {
        this.raizWeb = raizWeb.toAbsolutePath().normalize();
    }

    public static void main(String[] args) throws Exception {
        int porta = args.length > 0
                ? Integer.parseInt(args[0])
                : Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Path distribuicao = Paths.get("web-poc", "dist");
        ServidorPrototipoWeb aplicacao = new ServidorPrototipoWeb(
                Files.isDirectory(distribuicao) ? distribuicao : Paths.get("web-poc"));
        HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        servidor.createContext("/api/situacao", aplicacao::situacao);
        servidor.createContext("/api/acoes/posicionar", aplicacao::posicionar);
        servidor.createContext("/api/acoes/escolher-operacao", aplicacao::escolherOperacao);
        servidor.createContext("/api/gestos", aplicacao::registrarGesto);
        servidor.createContext("/api/reiniciar", aplicacao::reiniciar);
        servidor.createContext("/api/sorteios/medidas", aplicacao::sortearMedidas);
        servidor.createContext("/api/sorteios/relacoes", aplicacao::sortearRelacoes);
        servidor.createContext("/api/classificacao/categoria", aplicacao::escolherCategoria);
        servidor.createContext("/api/classificacao/confirmacao", aplicacao::confirmarCategoria);
        servidor.createContext("/", aplicacao::arquivoEstatico);
        servidor.setExecutor(null);
        servidor.start();
        System.out.println("Gérard web funcional em http://localhost:" + porta);
    }

    private void situacao(HttpExchange troca) throws IOException {
        if (!"GET".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        // Carga inicial entra pelo fluxo de classificação (sorteios), nunca
        // pelo widget fixo legado (atividade) — diagrama só depois de a
        // categoria ser acertada, ver ServicoSorteioAtividadeWeb.estadoInicial.
        responder(troca, 200, sorteios.estadoInicial());
    }

    @SuppressWarnings("unchecked")
    private void posicionar(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Object analisado = AnalisadorJsonSimples.analisar(corpo);
            Object bruto = ((Map<String, Object>) analisado).get("valor");
            String papelId = String.valueOf(((Map<String, Object>) analisado).get("papel_id"));
            int valor = ((Number) bruto).intValue();
            responder(troca, 200, sorteios.proporValor(papelId, valor));
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void escolherOperacao(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> analisado = (Map<String, Object>) AnalisadorJsonSimples.analisar(corpo);
            String seletor = String.valueOf(analisado.get("seletor"));
            String operacao = String.valueOf(analisado.get("operacao"));
            if (!sorteios.possuiAtividadeEscolhaOperacaoAtiva()) {
                responder(troca, 422, erro("a situação atual não possui escolha de operação implementada"));
                return;
            }
            responder(troca, 200, sorteios.escolherOperacao(seletor, operacao));
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    private void reiniciar(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            responder(troca, 200, sorteios.reiniciarAtividadeAtual());
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    private void sortearMedidas(HttpExchange troca) throws IOException {
        sortear(troca, true);
    }

    private void sortearRelacoes(HttpExchange troca) throws IOException {
        sortear(troca, false);
    }

    private void sortear(HttpExchange troca, boolean medidas) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            responder(troca, 200, medidas
                    ? sorteios.sortearMedidas() : sorteios.sortearRelacoes());
        } catch (RuntimeException erro) {
            responder(troca, 422, erro(erro.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void escolherCategoria(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido")); return;
        }
        try {
            Map<String, Object> corpo = (Map<String, Object>) AnalisadorJsonSimples
                    .analisar(new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            responder(troca, 200, sorteios.escolherCategoria(String.valueOf(corpo.get("categoria"))));
        } catch (RuntimeException ex) { responder(troca, 400, erro(ex.getMessage())); }
    }

    @SuppressWarnings("unchecked")
    private void confirmarCategoria(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido")); return;
        }
        try {
            Map<String, Object> corpo = (Map<String, Object>) AnalisadorJsonSimples
                    .analisar(new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            responder(troca, 200, sorteios.confirmarCategoria(
                    Boolean.TRUE.equals(corpo.get("concordou"))));
        } catch (RuntimeException ex) { responder(troca, 400, erro(ex.getMessage())); }
    }

    @SuppressWarnings("unchecked")
    private void registrarGesto(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> gesto = (Map<String, Object>) AnalisadorJsonSimples.analisar(corpo);
            if (!"gerard.gesto-web.v1".equals(gesto.get("schema"))
                    || gesto.get("gesture_id") == null
                    || gesto.get("destino_geometrico") == null) {
                throw new IllegalArgumentException("registro físico de gesto incompleto");
            }
            Map<String, Object> confirmacao = new java.util.LinkedHashMap<String, Object>();
            confirmacao.put("gesture_id", gesto.get("gesture_id"));
            confirmacao.put("recebido", Boolean.TRUE);
            confirmacao.put("sequencia", Long.valueOf(gestosRecebidos.incrementAndGet()));
            responder(troca, 202, confirmacao);
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    private void arquivoEstatico(HttpExchange troca) throws IOException {
        String requisitado = troca.getRequestURI().getPath();
        if ("/".equals(requisitado)) requisitado = "/index.html";
        Path arquivo = raizWeb.resolve(requisitado.substring(1)).normalize();
        if (!arquivo.startsWith(raizWeb) || !Files.isRegularFile(arquivo)) {
            troca.sendResponseHeaders(404, -1);
            troca.close();
            return;
        }
        byte[] bytes = Files.readAllBytes(arquivo);
        troca.getResponseHeaders().set("Content-Type", tipoConteudo(arquivo));
        troca.sendResponseHeaders(200, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    private static void responder(HttpExchange troca, int status,
            Map<String, Object> corpo) throws IOException {
        byte[] bytes = EscritorJsonSimples.escrever(corpo).getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        troca.sendResponseHeaders(status, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    private static Map<String, Object> erro(String mensagem) {
        Map<String, Object> erro = new java.util.LinkedHashMap<String, Object>();
        erro.put("erro", mensagem == null ? "Requisição inválida" : mensagem);
        return erro;
    }

    private static String tipoConteudo(Path arquivo) {
        String nome = arquivo.getFileName().toString();
        if (nome.endsWith(".css")) return "text/css; charset=utf-8";
        if (nome.endsWith(".js")) return "text/javascript; charset=utf-8";
        return "text/html; charset=utf-8";
    }
}
