package gerard.infraestrutura.web;

import gerard.infraestrutura.web.scaffolding.AdaptadorAjudaContextualWeb;
import gerard.infraestrutura.scaffolding.AdaptadorQuestionamentoPosicionamento;
import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.idioma.IdiomaInterface;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.aplicacao.portabilidade.RotasTentativa;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividade;
import gerard.adaptacao.sessao.SessaoAdaptativaUsuario;
import gerard.agente.modelador.RepositorioRegrasAdaptativasPublicadas;
import gerard.agente.modelousuario.Genero;
import gerard.agente.modelousuario.MidiaPreferida;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelEscolaridade;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.pesquisador.auditoria.EscritorJsonSimples;
import gerard.pesquisador.log.AgendadorEnvioLogPesquisa;
import gerard.pesquisador.log.EventoLogGerard;
import gerard.pesquisador.log.RepositorioEventosLogPesquisa;
import gerard.pesquisador.log.RegistradorAtividadeWebLogGerard;
import gerard.suporte.PreparadorEmailRelatoBug;
import java.io.File;
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
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/** Servidor local da prova funcional; HTTP e arquivos ficam na infraestrutura. */
public final class ServidorPrototipoWeb {
    // Raiz de composição: a infraestrutura fornece as implementações das portas
    // da aplicação (registro, ajuda contextual e questionamento de posicionamento).
    // Catálogo curado compartilhado por todas as sessões (o upload de curadoria o substitui para todas); o estado
    // da tentativa e o usuário que entrou são de cada visitante (ver SessoesWeb).
    private final FachadaCarregamentoAtividade catalogo = new FachadaCarregamentoAtividade(
            new RepositorioSituacoesAditivas(), new CatalogoDefinicoesAditivas(), new ConstrutorResultadoCurado());
    private final SessoesWeb sessoes;
    private final Path raizWeb;
    private final RepositorioModeloUsuario usuarios = new RepositorioModeloUsuario();
    private final RepositorioEventosLogPesquisa logPesquisa = new RepositorioEventosLogPesquisa(
            new File(diretorioGerard(), "pesquisa_log.db").getAbsolutePath());
    private final AgendadorEnvioLogPesquisa agendadorLogPesquisa =
            new AgendadorEnvioLogPesquisa(logPesquisa, "gerard-web");
    /**
     * Marca a última requisição de interação (ver responder()) — base dos
     * dois gatilhos de fim de sessão pedidos pela pesquisadora, além do
     * envio periódico: inatividade prolongada (verificarInatividade) e
     * fechamento de aba (POST /api/pesquisa/log/sincronizar via
     * navigator.sendBeacon no cliente, ver web-poc/src/main.tsx). Mesma
     * limitação de AgendadorEnvioLogPesquisa continua valendo: o log é um
     * arquivo único por processo, não por visitante — os dois gatilhos só
     * antecipam o mesmo ciclo que já roda no relógio.
     */
    private final AtomicLong ultimaAtividadeEmMs = new AtomicLong(System.currentTimeMillis());

    private ServidorPrototipoWeb(Path raizWeb) {
        this.raizWeb = raizWeb.toAbsolutePath().normalize();
        // O logger e o índice de tentativas sobem no boot, não na primeira visita.
        gerard.pesquisador.log.LoggerInteracaoGerard.getInstancia().aquecerIndiceDeTentativas();
        long ociosidadeMinutos = Long.parseLong(System.getenv().getOrDefault("SESSAO_OCIOSIDADE_MINUTOS", "120"));
        int limiteSessoes = Integer.parseInt(System.getenv().getOrDefault("SESSAO_LIMITE", "500"));
        this.sessoes = new SessoesWeb(this::novaSessao, ociosidadeMinutos * 60_000L, limiteSessoes);
        new java.util.Timer("expiracao-sessoes-web", true).scheduleAtFixedRate(new java.util.TimerTask() {
            public void run() {
                sessoes.expirar();
            }
        }, 60_000L, 60_000L);
        long intervaloMinutos = Long.parseLong(
                System.getenv().getOrDefault("PESQUISA_LOG_INTERVALO_MINUTOS", "5"));
        agendadorLogPesquisa.iniciar(intervaloMinutos);
        iniciarVerificacaoInatividade();
    }

    // Raiz de composição: a infraestrutura fornece as implementações das portas da aplicação (registro, ajuda
    // contextual e questionamento de posicionamento) para a tentativa de cada visitante.
    private SessaoWeb novaSessao() {
        RegistradorAtividadeWebLogGerard registro = new RegistradorAtividadeWebLogGerard();
        ServicoSorteioAtividade sorteios = new ServicoSorteioAtividade(new PoliticaSorteioSituacoesAditivas(),
                catalogo, new java.util.Random(), IdiomaInterface.PORTUGUES, registro,
                new AdaptadorAjudaContextualWeb(), new AdaptadorQuestionamentoPosicionamento());
        return new SessaoWeb(SessoesWeb.novoId(), sorteios,
                new SessaoAdaptativaUsuario(usuarios, new RepositorioRegrasAdaptativasPublicadas()), registro);
    }

    private void iniciarVerificacaoInatividade() {
        long limiarMinutos = Long.parseLong(
                System.getenv().getOrDefault("PESQUISA_LOG_LIMIAR_INATIVIDADE_MINUTOS", "10"));
        final long limiarMs = limiarMinutos * 60_000L;
        new java.util.Timer("verificacao-inatividade-log-pesquisa", true).scheduleAtFixedRate(
                new java.util.TimerTask() {
                    public void run() {
                        if (System.currentTimeMillis() - ultimaAtividadeEmMs.get() >= limiarMs) {
                            try {
                                agendadorLogPesquisa.executarCiclo();
                            } catch (Exception ex) {
                                System.err.println(
                                        "Falha na sincronizacao do log de pesquisa por inatividade: "
                                                + ex.getMessage());
                            }
                        }
                    }
                }, 60_000L, 60_000L);
    }

    private static File diretorioGerard() {
        File diretorio = new File(System.getProperty("user.home"), "Gerard");
        diretorio.mkdirs();
        return diretorio;
    }

    public static void main(String[] args) throws Exception {
        int porta = args.length > 0
                ? Integer.parseInt(args[0])
                : Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        Path distribuicao = Paths.get("web-poc", "dist");
        ServidorPrototipoWeb aplicacao = new ServidorPrototipoWeb(
                Files.isDirectory(distribuicao) ? distribuicao : Paths.get("web-poc"));
        HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        servidor.createContext(RotasTentativa.BASE, new RecursosTentativa(aplicacao.sessoes, aplicacao::responder));
        servidor.createContext(Problema.BASE.substring(0, Problema.BASE.length() - 1), aplicacao::problemas);
        servidor.createContext("/api/v1/usuarios", aplicacao::usuarios);
        servidor.createContext("/api/v1/sessao/usuario", aplicacao::entrarUsuario);
        servidor.createContext("/api/v1/relatos", aplicacao::relatoBug);
        servidor.createContext("/api/curadoria/situacoes", aplicacao::situacoesCuradoria);
        servidor.createContext("/api/pesquisa/log", aplicacao::logPesquisa);
        servidor.createContext("/api/v1/pesquisa/sincronizacoes", aplicacao::sincronizarLogSessao);
        servidor.createContext("/", aplicacao::arquivoEstatico);
        int threadsHttp = Integer.parseInt(System.getenv().getOrDefault("HTTP_THREADS", "64"));
        servidor.setExecutor(Executors.newFixedThreadPool(threadsHttp));
        servidor.start();
        System.out.println("Gérard web funcional em http://localhost:" + porta
                + " (" + threadsHttp + " threads HTTP)");
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
                // A pessoa é o recurso: /api/v1/usuarios/{id}.
                String caminhoUsuario = troca.getRequestURI().getPath();
                String id = caminhoUsuario.length() > "/api/v1/usuarios/".length()
                        && caminhoUsuario.startsWith("/api/v1/usuarios/")
                        ? caminhoUsuario.substring("/api/v1/usuarios/".length()) : null;
                if (id == null || id.isEmpty() || id.contains("/")) {
                    responder(troca, 404, erro("Usuário inexistente: informe /api/v1/usuarios/{id}"));
                    return;
                }
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
            Optional<String> idAtual = sessoes.de(troca).usuario().fotografiaAtual().map(f -> f.getUsuarioId());
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
            if (sessoes.de(troca).usuario().fotografiaAtual().isPresent()
                    && !id.equals(sessoes.de(troca).usuario().fotografiaAtual().get().getUsuarioId())) {
                sessoes.de(troca).usuario().encerrarNoLogout();
            }
            String versao = sessoes.de(troca).usuario().iniciarNoLogin(id).getVersaoModelo();
            sessoes.de(troca).registro().definirUsuario(id);
            Map<String, Object> resposta = new LinkedHashMap<String, Object>();
            resposta.put("schema", "gerard.sessao-usuario-web.v1");
            resposta.put("usuario_id", id);
            resposta.put("versao_modelo", versao);
            responder(troca, 200, resposta);
        } catch (RuntimeException erro) {
            responder(troca, 400, erro(erro.getMessage()));
        }
    }

    /**
     * Upload de curadoria (pesquisadora): entrega ao servidor web o conteúdo
     * do arquivo vivo de situações curadas (RepositorioSituacoesAditivas —
     * mesmo formato/cabeçalho do .tsv em disco), substituindo em memória o
     * que veio empacotado no deploy. Não persiste em disco — Render tem
     * disco efêmero; vale até o próximo restart/redeploy, quando volta ao
     * recurso empacotado e precisa ser reenviado. Protegido por token
     * simples (variável de ambiente CURADORIA_TOKEN) em vez de um papel de
     * usuário — RepositorioModeloUsuario não distingue pesquisadora de
     * aluno, e inventar esse conceito só para este endpoint seria mais
     * acoplamento do que o problema pede.
     */
    private void situacoesCuradoria(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        String tokenEsperado = System.getenv("CURADORIA_TOKEN");
        if (tokenEsperado == null || tokenEsperado.trim().length() == 0) {
            responder(troca, 503, erro("Upload de curadoria não está configurado neste servidor"));
            return;
        }
        String tokenRecebido = troca.getRequestHeaders().getFirst("X-Curadoria-Token");
        if (tokenRecebido == null || !tokenEsperado.equals(tokenRecebido)) {
            responder(troca, 401, erro("Token de curadoria inválido"));
            return;
        }
        String conteudo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (conteudo.trim().length() == 0) {
            responder(troca, 400, erro("Conteúdo vazio"));
            return;
        }
        boolean aceito = catalogo.substituirSituacoesCuradas(conteudo);
        if (!aceito) {
            responder(troca, 422, erro("Conteúdo inválido: nenhuma situação foi carregada"));
            return;
        }
        Map<String, Object> resposta = new LinkedHashMap<String, Object>();
        resposta.put("aceito", Boolean.TRUE);
        resposta.put("total_situacoes", Integer.valueOf(catalogo.contarSituacoesCuradas()));
        resposta.put("total_validadas", Integer.valueOf(catalogo.contarSituacoesValidadas()));
        responder(troca, 200, resposta);
    }

    /**
     * Backend central do log de pesquisa (POST recebe TSV/JSON de qualquer
     * instância do Gérard e traduz para o banco; GET consulta o banco e
     * devolve JSON) — ver RepositorioEventosLogPesquisa. O lado que envia
     * nunca fala com o banco diretamente, só TSV ou JSON, como pedido
     * explicitamente pela pesquisadora. Mesmo token de curadoria (é a mesma
     * pessoa administrando as duas coisas; inventar um segundo segredo só
     * para isso seria mais acoplamento do que o problema pede).
     */
    @SuppressWarnings("unchecked")
    private void logPesquisa(HttpExchange troca) throws IOException {
        String tokenEsperado = System.getenv("CURADORIA_TOKEN");
        if (tokenEsperado == null || tokenEsperado.trim().length() == 0) {
            responder(troca, 503, erro("Log de pesquisa não está configurado neste servidor"));
            return;
        }
        String tokenRecebido = troca.getRequestHeaders().getFirst("X-Curadoria-Token");
        if (tokenRecebido == null || !tokenEsperado.equals(tokenRecebido)) {
            responder(troca, 401, erro("Token inválido"));
            return;
        }

        if ("GET".equals(troca.getRequestMethod())) {
            Map<String, String> parametros = analisarQuery(troca.getRequestURI().getQuery());
            int limite = 500;
            try { limite = Integer.parseInt(parametros.getOrDefault("limite", "500")); } catch (NumberFormatException ignorado) { }
            List<Map<String, Object>> linhas = logPesquisa.consultar(
                    parametros.get("origem"), parametros.get("sessao"), parametros.get("usuario"), limite);
            Map<String, Object> resposta = new LinkedHashMap<String, Object>();
            resposta.put("eventos", linhas);
            resposta.put("total_no_banco", Integer.valueOf(logPesquisa.contar()));
            responder(troca, 200, resposta);
            return;
        }

        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        String origemInstancia = troca.getRequestHeaders().getFirst("X-Origem-Instancia");
        if (origemInstancia == null || origemInstancia.trim().length() == 0) {
            origemInstancia = "desconhecida";
        }
        String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (corpo.trim().length() == 0) {
            responder(troca, 400, erro("Conteúdo vazio"));
            return;
        }
        List<EventoLogGerard> eventos = new ArrayList<EventoLogGerard>();
        String corpoAparado = corpo.trim();
        if (corpoAparado.startsWith("[")) {
            Object analisado = AnalisadorJsonSimples.analisar(corpoAparado);
            if (analisado instanceof List) {
                for (Object item : (List<Object>) analisado) {
                    if (item instanceof Map) {
                        EventoLogGerard evento =
                                EventoLogGerard.deMapa((Map<String, Object>) item);
                        if (evento != null) eventos.add(evento);
                    }
                }
            }
        } else {
            for (String linha : corpo.split("\n", -1)) {
                EventoLogGerard evento = EventoLogGerard.deTsv(linha.trim());
                if (evento != null) eventos.add(evento);
            }
        }
        int gravados = logPesquisa.inserirTodos(origemInstancia, eventos);
        Map<String, Object> resposta = new LinkedHashMap<String, Object>();
        resposta.put("aceito", Boolean.TRUE);
        resposta.put("eventos_gravados", Integer.valueOf(gravados));
        responder(troca, 200, resposta);
    }

    /**
     * Gatilho best-effort de fechamento de aba (navigator.sendBeacon no
     * cliente, ver web-poc/src/main.tsx). Sem token: não expõe nem aceita
     * dados do log, só antecipa o mesmo ciclo que o AgendadorEnvioLogPesquisa
     * já roda periodicamente.
     */
    private void sincronizarLogSessao(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            responder(troca, 405, erro("Método não permitido"));
            return;
        }
        try {
            agendadorLogPesquisa.executarCiclo();
        } catch (Exception ex) {
            System.err.println("Falha ao sincronizar log de pesquisa no fechamento da aba: " + ex.getMessage());
        }
        Map<String, Object> resposta = new LinkedHashMap<String, Object>();
        resposta.put("aceito", Boolean.TRUE);
        responder(troca, 200, resposta);
    }

    private static Map<String, String> analisarQuery(String query) {
        Map<String, String> parametros = new LinkedHashMap<String, String>();
        if (query == null || query.trim().length() == 0) return parametros;
        for (String par : query.split("&")) {
            int igual = par.indexOf('=');
            if (igual < 0) continue;
            try {
                String chave = java.net.URLDecoder.decode(par.substring(0, igual), "UTF-8");
                String valor = java.net.URLDecoder.decode(par.substring(igual + 1), "UTF-8");
                parametros.put(chave, valor);
            } catch (java.io.UnsupportedEncodingException ignorado) { }
        }
        return parametros;
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

    private void responder(HttpExchange troca, int status,
            Map<String, Object> corpo) throws IOException {
        ultimaAtividadeEmMs.set(System.currentTimeMillis());
        // Handlers que rejeitam cedo (token inválido, método não permitido)
        // respondem sem ter lido o corpo da requisição. Sem drenar o que
        // sobrou, o HttpServer embutido não consegue reaproveitar a conexão
        // keep-alive de forma limpa e derruba conexões concorrentes sob
        // carga — achado no teste de macaco contra /api/pesquisa/log com
        // muitos clientes simultâneos. Ler de novo depois que um handler já
        // consumiu o corpo é inofensivo (stream já em EOF).
        try {
            troca.getRequestBody().readAllBytes();
        } catch (IOException ignorado) {
        }
        Map<String, Object> corpoFinal = corpo;
        boolean ehProblema = status >= 400;
        if (ehProblema && !corpo.containsKey("type")) {
            // Erros montados com erro("..."): viram problem+json com o tipo genérico (RFC 9457, about:blank).
            corpoFinal = new LinkedHashMap<String, Object>();
            corpoFinal.put("type", "about:blank");
            corpoFinal.put("title", tituloDoStatus(status));
            corpoFinal.put("status", Integer.valueOf(status));
            corpoFinal.put("detail", corpo.get("erro"));
            corpoFinal.put("instance", troca.getRequestURI().getPath());
        }
        byte[] bytes = EscritorJsonSimples.escrever(corpoFinal).getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set("Content-Type",
                ehProblema ? "application/problem+json; charset=utf-8" : "application/json; charset=utf-8");
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

    private static String tituloDoStatus(int status) {
        switch (status) {
            case 400: return "Requisição inválida";
            case 401: return "Não autorizado";
            case 404: return "Não encontrado";
            case 405: return "Método não permitido";
            case 422: return "Dados inválidos";
            case 503: return "Serviço indisponível";
            default: return "Erro";
        }
    }

    /** GET /api/v1/problemas/{slug}: descreve o tipo de problema que as respostas de erro citam em "type". */
    private void problemas(HttpExchange troca) throws IOException {
        if (!"GET".equals(troca.getRequestMethod())) {
            troca.getResponseHeaders().set("Allow", "GET");
            responder(troca, 405, Problema.METODO_NAO_PERMITIDO.corpo(null, troca.getRequestURI().getPath()));
            return;
        }
        String caminho = troca.getRequestURI().getPath();
        Problema tipo = caminho.startsWith(Problema.BASE) ? Problema.porSlug(caminho.substring(Problema.BASE.length())) : null;
        if (tipo == null) {
            responder(troca, 404, Problema.RECURSO_INEXISTENTE.corpo(null, caminho));
            return;
        }
        responder(troca, 200, tipo.descricaoDoTipo());
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
