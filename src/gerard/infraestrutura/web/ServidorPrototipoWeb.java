package gerard.infraestrutura.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividadeWeb;
import gerard.adaptacao.sessao.SessaoAdaptativaUsuario;
import gerard.agente.modelador.RepositorioRegrasAdaptativasPublicadas;
import gerard.agente.modelousuario.Genero;
import gerard.agente.modelousuario.MidiaPreferida;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelEscolaridade;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.pesquisador.auditoria.EscritorJsonSimples;
import gerard.suporte.PreparadorEmailRelatoBug;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** Servidor local da prova funcional; HTTP e arquivos ficam na infraestrutura. */
public final class ServidorPrototipoWeb {
    private final ServicoSorteioAtividadeWeb sorteios =
            new ServicoSorteioAtividadeWeb();
    private final Path raizWeb;
    private final AtomicLong gestosRecebidos = new AtomicLong();
    private final RepositorioModeloUsuario usuarios = new RepositorioModeloUsuario();
    private final SessaoAdaptativaUsuario sessaoUsuario = new SessaoAdaptativaUsuario(
            usuarios, new RepositorioRegrasAdaptativasPublicadas());

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
        servidor.createContext("/api/acoes/quadradinho", aplicacao::ajustarQuadradinho);
        servidor.createContext("/api/acoes/posicionar-conhecido", aplicacao::posicionarConhecido);
        servidor.createContext("/api/acoes/engatar-incognita", aplicacao::engatarIncognita);
        servidor.createContext("/api/acoes/escolher-sinal", aplicacao::escolherSinal);
        servidor.createContext("/api/acoes/revelar-eixo", aplicacao::revelarEixo);
        servidor.createContext("/api/acoes/ocultar-eixo", aplicacao::ocultarEixo);
        servidor.createContext("/api/acoes/ajuda-contextual", aplicacao::ajudaContextual);
        servidor.createContext("/api/gestos", aplicacao::registrarGesto);
        servidor.createContext("/api/reiniciar", aplicacao::reiniciar);
        servidor.createContext("/api/sorteios/medidas", aplicacao::sortearMedidas);
        servidor.createContext("/api/sorteios/relacoes", aplicacao::sortearRelacoes);
        servidor.createContext("/api/classificacao/categoria", aplicacao::escolherCategoria);
        servidor.createContext("/api/classificacao/confirmacao", aplicacao::confirmarCategoria);
        servidor.createContext("/api/usuarios", aplicacao::usuarios);
        servidor.createContext("/api/sessao/usuario", aplicacao::entrarUsuario);
        servidor.createContext("/api/acoes/relato-bug", aplicacao::relatoBug);
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
    private void usuarios(HttpExchange troca) throws IOException {
        if ("GET".equals(troca.getRequestMethod())) {
            List<Object> perfis = new ArrayList<Object>();
            for (ModeloUsuario modelo : usuarios.listarPerfisCadastrados()) {
                perfis.add(perfilJson(modelo));
            }
            Map<String, Object> resposta = new LinkedHashMap<String, Object>();
            resposta.put("schema", "gerard.usuarios-web.v1");
            resposta.put("usuarios", perfis);
            responder(troca, 200, resposta);
            return;
        }
        if ("PUT".equals(troca.getRequestMethod())) {
            Path fotoTemporariaEdicao = null;
            try {
                Map<String, Object> corpo = (Map<String, Object>) AnalisadorJsonSimples.analisar(
                        new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                String id = textoObrigatorio(corpo, "usuario_id");
                String nome = textoObrigatorio(corpo, "nome");
                int idade = ((Number) corpo.get("idade")).intValue();
                if (idade < 1 || idade > 120) throw new IllegalArgumentException("idade inválida");
                Genero sexo = Genero.valueOf(textoObrigatorio(corpo, "sexo"));
                MidiaPreferida midia = MidiaPreferida.valueOf(textoObrigatorio(corpo, "midia_preferida"));
                NivelEscolaridade escolaridade = NivelEscolaridade.valueOf(
                        textoObrigatorio(corpo, "nivel_escolaridade"));
                fotoTemporariaEdicao = decodificarFotoTemporaria(corpo.get("foto_data_url"));
                usuarios.atualizarPerfil(id, nome, Integer.valueOf(idade), sexo, midia,
                        escolaridade, fotoTemporariaEdicao == null ? null : fotoTemporariaEdicao.toFile());
                responder(troca, 200, perfilJson(usuarios.obter(id)));
            } catch (RuntimeException erro) {
                responder(troca, 400, erro(erro.getMessage()));
            } finally {
                if (fotoTemporariaEdicao != null) Files.deleteIfExists(fotoTemporariaEdicao);
            }
            return;
        }
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        Path fotoTemporaria = null;
        try {
            Map<String, Object> corpo = (Map<String, Object>) AnalisadorJsonSimples.analisar(
                    new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            String nome = textoObrigatorio(corpo, "nome");
            int idade = ((Number) corpo.get("idade")).intValue();
            if (idade < 1 || idade > 120) throw new IllegalArgumentException("idade inválida");
            Genero sexo = Genero.valueOf(textoObrigatorio(corpo, "sexo"));
            MidiaPreferida midia = MidiaPreferida.valueOf(textoObrigatorio(corpo, "midia_preferida"));
            NivelEscolaridade escolaridade = NivelEscolaridade.valueOf(
                    textoObrigatorio(corpo, "nivel_escolaridade"));
            fotoTemporaria = decodificarFotoTemporaria(corpo.get("foto_data_url"));
            String id = usuarios.cadastrarPerfil(nome, Integer.valueOf(idade), sexo, midia,
                    escolaridade, fotoTemporaria == null ? null : fotoTemporaria.toFile());
            responder(troca, 201, perfilJson(usuarios.obter(id)));
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        } finally {
            if (fotoTemporaria != null) Files.deleteIfExists(fotoTemporaria);
        }
    }

    private static Path decodificarFotoTemporaria(Object foto) throws IOException {
        if (!(foto instanceof String) || ((String) foto).isBlank()) return null;
        String dataUrl = (String) foto;
        int virgula = dataUrl.indexOf(',');
        if (virgula < 0 || !dataUrl.startsWith("data:image/")) {
            throw new IllegalArgumentException("foto inválida");
        }
        byte[] bytes = Base64.getDecoder().decode(dataUrl.substring(virgula + 1));
        if (bytes.length > 5 * 1024 * 1024) throw new IllegalArgumentException("a foto excede 5 MB");
        Path fotoTemporaria = Files.createTempFile("gerard-perfil-", ".img");
        Files.write(fotoTemporaria, bytes);
        return fotoTemporaria;
    }

    @SuppressWarnings("unchecked")
    private void entrarUsuario(HttpExchange troca) throws IOException {
        if ("GET".equals(troca.getRequestMethod())) {
            Optional<String> idAtual = sessaoUsuario.fotografiaAtual().map(f -> f.getUsuarioId());
            Map<String, Object> resposta = new LinkedHashMap<String, Object>();
            resposta.put("schema", "gerard.sessao-usuario-web.v1");
            resposta.put("usuario_id", idAtual.orElse(null));
            resposta.put("perfil", idAtual.map(id -> perfilJson(usuarios.obter(id))).orElse(null));
            responder(troca, 200, resposta);
            return;
        }
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            Map<String, Object> corpo = (Map<String, Object>) AnalisadorJsonSimples.analisar(
                    new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            String id = textoObrigatorio(corpo, "usuario_id");
            if (sessaoUsuario.fotografiaAtual().isPresent()
                    && !id.equals(sessaoUsuario.fotografiaAtual().get().getUsuarioId())) {
                sessaoUsuario.encerrarNoLogout();
            }
            String versao = sessaoUsuario.iniciarNoLogin(id).getVersaoModelo();
            Map<String, Object> resposta = new LinkedHashMap<String, Object>();
            resposta.put("schema", "gerard.sessao-usuario-web.v1");
            resposta.put("usuario_id", id);
            resposta.put("versao_modelo", versao);
            responder(troca, 200, resposta);
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void relatoBug(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            Map<String, Object> corpo = (Map<String, Object>) AnalisadorJsonSimples.analisar(
                    new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            String descricao = textoObrigatorio(corpo, "descricao");
            String situacaoId = textoOpcional(corpo, "situacao_id");
            String categoria = textoOpcional(corpo, "categoria");
            String representacoes = textoOpcional(corpo, "representacoes");
            String idiomaInterface = textoOpcional(corpo, "idioma_interface");
            String idiomaSituacao = textoOpcional(corpo, "idioma_situacao");
            String enunciado = textoOpcional(corpo, "enunciado");
            PreparadorEmailRelatoBug.MensagemPreparada mensagem = PreparadorEmailRelatoBug.preparar(
                    descricao, situacaoId, categoria, representacoes,
                    idiomaInterface, idiomaSituacao, enunciado);
            Map<String, Object> resposta = new LinkedHashMap<String, Object>();
            resposta.put("uri_gmail", mensagem.getUriGmail().toString());
            resposta.put("uri_mailto", mensagem.getUriMailto().toString());
            responder(troca, 200, resposta);
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    private static String textoOpcional(Map<String, Object> corpo, String campo) {
        Object valor = corpo.get(campo);
        return valor == null ? "" : String.valueOf(valor);
    }

    private static Map<String, Object> perfilJson(ModeloUsuario modelo) {
        Map<String, Object> perfil = new LinkedHashMap<String, Object>();
        perfil.put("id", modelo.getPerfilAluno().getId());
        perfil.put("nome", modelo.getPerfilAluno().getNome());
        perfil.put("idade", modelo.getPerfilAluno().getIdade());
        perfil.put("sexo", modelo.getPerfilAluno().getSexo() == null ? null : modelo.getPerfilAluno().getSexo().name());
        perfil.put("midia_preferida", modelo.getPerfilAprendizagem().getMidiaPreferida() == null ? null : modelo.getPerfilAprendizagem().getMidiaPreferida().name());
        perfil.put("nivel_escolaridade", modelo.getPerfilAprendizagem().getNivelEscolaridade() == null ? null : modelo.getPerfilAprendizagem().getNivelEscolaridade().name());
        perfil.put("possui_foto", modelo.getPerfilAluno().getFotoCaminho() != null);
        return perfil;
    }

    private static String textoObrigatorio(Map<String, Object> corpo, String campo) {
        Object valor = corpo.get(campo);
        String texto = valor == null ? "" : String.valueOf(valor).trim();
        if (texto.isEmpty()) throw new IllegalArgumentException(campo + " é obrigatório");
        return texto;
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

    @SuppressWarnings("unchecked")
    private void ajustarQuadradinho(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> analisado = (Map<String, Object>) AnalisadorJsonSimples.analisar(corpo);
            String papelId = String.valueOf(analisado.get("papel_id"));
            int delta = ((Number) analisado.get("delta")).intValue();
            responder(troca, 200, sorteios.ajustarQuadradinho(papelId, delta));
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void posicionarConhecido(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> analisado = (Map<String, Object>) AnalisadorJsonSimples.analisar(corpo);
            String papelId = String.valueOf(analisado.get("papel_id"));
            Object origemBruta = analisado.get("origem_papel_id");
            String origemPapelId = origemBruta == null ? null : String.valueOf(origemBruta);
            responder(troca, 200, sorteios.posicionarValorConhecido(papelId, origemPapelId));
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void engatarIncognita(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> analisado = (Map<String, Object>) AnalisadorJsonSimples.analisar(corpo);
            String papelId = String.valueOf(analisado.get("papel_id"));
            Object origemBruta = analisado.get("origem_papel_id");
            String origemPapelId = origemBruta == null ? null : String.valueOf(origemBruta);
            responder(troca, 200, sorteios.engatarIncognita(papelId, origemPapelId));
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void escolherSinal(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> analisado = (Map<String, Object>) AnalisadorJsonSimples.analisar(corpo);
            String papelId = String.valueOf(analisado.get("papel_id"));
            String sinal = String.valueOf(analisado.get("sinal"));
            responder(troca, 200, sorteios.escolherSinalNumeroRelativo(papelId, sinal));
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void revelarEixo(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> analisado = (Map<String, Object>) AnalisadorJsonSimples.analisar(corpo);
            String papelId = String.valueOf(analisado.get("papel_id"));
            responder(troca, 200, sorteios.revelarEixo(papelId));
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void ocultarEixo(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> analisado = (Map<String, Object>) AnalisadorJsonSimples.analisar(corpo);
            String papelId = String.valueOf(analisado.get("papel_id"));
            responder(troca, 200, sorteios.ocultarEixo(papelId));
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void ajudaContextual(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> analisado = (Map<String, Object>) AnalisadorJsonSimples.analisar(corpo);
            String area = String.valueOf(analisado.get("area"));
            String intencao = String.valueOf(analisado.get("intencao"));
            responder(troca, 200, sorteios.ajudaContextual(area, intencao));
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
        // Protótipo em iteração ativa: sem isso o navegador pode continuar
        // servindo do cache um bundle JS/CSS antigo depois de um rebuild,
        // fazendo uma correção parecer não aplicada.
        troca.getResponseHeaders().set("Cache-Control", "no-store");
        troca.sendResponseHeaders(200, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    private static void responder(HttpExchange troca, int status,
            Map<String, Object> corpo) throws IOException {
        byte[] bytes = EscritorJsonSimples.escrever(corpo).getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        // Sem isso, GET /api/situacao pode ser servido do cache HTTP do
        // navegador num F5, mostrando estado antigo mesmo com o servidor já
        // tendo avançado — mesmo problema que motivou o no-store nos
        // estáticos, só que na consulta em si.
        troca.getResponseHeaders().set("Cache-Control", "no-store");
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
