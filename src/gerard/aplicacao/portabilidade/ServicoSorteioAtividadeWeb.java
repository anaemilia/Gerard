package gerard.aplicacao.portabilidade;

import gerard.aplicacao.ContextoCarregamentoAtividade;
import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas.Grupo;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.DecisaoExibicaoPaineisEixo;
import gerard.campoaditivo.diagrama.modelo.DirecaoDeslocamentoDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.modelo.PosicaoRotuloFigura;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.servico.PosicaoSeletorOperacaoDiagrama;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.idioma.IdiomaInterface;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoClassificacaoCategoria;
import gerard.dominio.campoaditivo.TentativaClassificacaoCategoriaAditiva;
import gerard.Scaffolding.ajudacontextual.ScaffoldingAjudaContextual;
import gerard.campoaditivo.servico.ControladorContextoSituacao;
import gerard.pesquisador.log.LoggerInteracaoGerard;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Coordena o sorteio web sem conhecer HTTP ou qualquer tecnologia de tela. */
public final class ServicoSorteioAtividadeWeb {
    public static final String SCHEMA = "gerard.atividade-web.estado.v1";

    private final PoliticaSorteioSituacoesAditivas politica;
    private final FachadaCarregamentoAtividade carregamento;
    private final Random aleatorio;
    private final IdiomaInterface idioma;
    private ContextoCarregamentoAtividade contextoAtual;
    private Grupo grupoAtual;
    private TentativaClassificacaoCategoriaAditiva tentativaClassificacao;
    private TipoSituacaoAditiva categoriaSelecionada;
    private String questionamento;
    private ServicoAtividadeWeb atividadeModelagem;
    private ServicoAtividadeWebEscolhaOperacao atividadeEscolhaOperacao;
    private final ScaffoldingAjudaContextual scaffoldingAjudaContextual = new ScaffoldingAjudaContextual();
    private final ControladorContextoSituacao controladorContextoSituacao =
            new ControladorContextoSituacao(LoggerInteracaoGerard.getInstancia());

    public ServicoSorteioAtividadeWeb() {
        this(new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(
                        new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(),
                        new ConstrutorResultadoCurado()),
                new Random(), IdiomaInterface.PORTUGUES);
    }

    public ServicoSorteioAtividadeWeb(PoliticaSorteioSituacoesAditivas politica,
            FachadaCarregamentoAtividade carregamento, Random aleatorio,
            IdiomaInterface idioma) {
        if (politica == null || carregamento == null || aleatorio == null || idioma == null) {
            throw new IllegalArgumentException("dependências do sorteio são obrigatórias");
        }
        this.politica = politica;
        this.carregamento = carregamento;
        this.aleatorio = aleatorio;
        this.idioma = idioma;
    }

    /**
     * Estado a exibir na carga inicial da página — nunca um diagrama pronto:
     * carregar a página inteira (F5 incluído) sorteia sempre uma situação
     * nova (grupo aleatório) e devolve o estado de classificação recém-criado,
     * que por si só já esconde a cena até a categoria ser acertada (ver
     * projetarEstado/revelar). Uma carga de página é sempre tratada como um
     * recomeço — nunca reaproveita progresso de classificação/diagrama de
     * uma chamada anterior a este método. GET /api/situacao (que chama este
     * método) só é disparado uma vez, no mount da SPA — as demais interações
     * do fluxo recebem o estado atualizado na própria resposta do POST, sem
     * passar por aqui de novo.
     */
    public synchronized Map<String, Object> estadoInicial() {
        return sortear(aleatorio.nextBoolean() ? Grupo.MEDIDAS : Grupo.RELACOES);
    }

    public synchronized Map<String, Object> sortearMedidas() {
        return sortear(Grupo.MEDIDAS);
    }

    public synchronized Map<String, Object> sortearRelacoes() {
        return sortear(Grupo.RELACOES);
    }

    private Map<String, Object> sortear(Grupo grupo) {
        TipoSituacaoAditiva categoria = politica.sortearCategoria(grupo, aleatorio);
        ContextoCarregamentoAtividade contexto = carregamento.carregarNova(idioma, categoria);
        if (!contexto.possuiSituacaoExibivel()) {
            throw new IllegalStateException("não há situação curada exibível para " + categoria.name());
        }
        contextoAtual = contexto;
        grupoAtual = grupo;
        tentativaClassificacao = new TentativaClassificacaoCategoriaAditiva(
                contexto.getSituacao());
        categoriaSelecionada = null;
        questionamento = null;
        atividadeModelagem = null;
        atividadeEscolhaOperacao = null;
        return projetarEstado();
    }

    public synchronized Map<String, Object> escolherCategoria(String categoria) {
        exigirClassificacaoAtiva();
        TipoSituacaoAditiva escolhida = TipoSituacaoAditiva.valueOf(categoria);
        RegistroAcaoClassificacaoCategoria registro = tentativaClassificacao
                .avaliarEscolha(escolhida, contextoEscolha(escolhida));
        if (registro.foiCorreta()) {
            categoriaSelecionada = escolhida;
            // Mesmo ponto do desktop (confirmarCategoriaAdivinhada,
            // Main.java): o contexto do log granular só é atualizado quando
            // a categoria é confirmada, não a cada tentativa de
            // classificação — comportamento existente, não uma escolha nova.
            controladorContextoSituacao.registrarNovaSituacao(
                    contextoAtual.getSituacao(), escolhida.name(),
                    contextoAtual.getEnunciadoExibido());
            if (escolhida == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
                atividadeModelagem = new ServicoAtividadeWebComposicao(
                        "tentativa.web." + contextoAtual.getSituacao().getId(),
                        contextoAtual.getSituacao());
            } else if (escolhida == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
                atividadeModelagem = new ServicoAtividadeWebTransformacaoMedidas(
                        "tentativa.web." + contextoAtual.getSituacao().getId(),
                        contextoAtual.getSituacao());
            } else if (escolhida == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
                atividadeModelagem = new ServicoAtividadeWebComparacaoMedidas(
                        "tentativa.web." + contextoAtual.getSituacao().getId(),
                        contextoAtual.getSituacao());
            } else if (escolhida == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
                // RelacaoEstruturalTransformacaoDeRelacao (básica, só dado
                // tabular) é o caminho CANÔNICO real — confirmado em
                // CatalogoRelacoesEstruturaisAditivas, o resolvedor usado por
                // EstadoSemanticoCompartilhado.resolverRelacaoAditiva em todo
                // o desktop, que nunca referencia a variante "Orientada".
                // A variante Orientada/rica só existe dentro de
                // ConversorSituacaoProblemaRica, uma ponte de CURADORIA
                // (gera SituacaoProblema validado para ferramentas de
                // pesquisador) — não o caminho de resolução em tempo real.
                // ServicoAtividadeWebTransformacaoRelacaoRica usava esse
                // mecanismo secundário por engano; removido em 2026-09-04
                // (ver LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md-style
                // achado: fonte de verdade divergente da canônica).
                atividadeModelagem = new ServicoAtividadeWebTransformacaoRelacao(
                        "tentativa.web." + contextoAtual.getSituacao().getId(),
                        contextoAtual.getSituacao());
            } else if (escolhida == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
                atividadeEscolhaOperacao =
                        new ServicoAtividadeWebComposicaoTransformacoes(
                                "tentativa.web." + contextoAtual.getSituacao().getId(),
                                contextoAtual.getSituacao());
            } else if (escolhida == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
                atividadeEscolhaOperacao =
                        new ServicoAtividadeWebComposicaoRelacoes(
                                "tentativa.web." + contextoAtual.getSituacao().getId(),
                                contextoAtual.getSituacao());
            }
        }
        questionamento = tentativaClassificacao.aguardaConfirmacao()
                ? "A definição da categoria " + escolhida.name()
                        + " se aplica a esta situação?"
                : null;
        return resultadoClassificacao(registro);
    }

    public synchronized boolean possuiAtividadeModelagemAtiva() {
        return atividadeModelagem != null;
    }

    public synchronized boolean possuiAtividadeEscolhaOperacaoAtiva() {
        return atividadeEscolhaOperacao != null;
    }

    public synchronized Map<String, Object> proporValor(String papelId, int valor) {
        if (atividadeModelagem == null) {
            throw new IllegalStateException("a situação atual não possui modelagem web implementada");
        }
        Map<String, Object> resultado = atividadeModelagem.proporValor(papelId, valor);
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    /**
     * Posiciona um papel conhecido (nunca a incógnita) — a ação que soltar
     * qualquer elemento não-incógnita do enunciado sobre sua caixa dispara
     * (protocolo de mouse é posicionar). Vale para as 4 categorias com
     * modelagem de papel (Composição/Transformação/Comparação de Medidas,
     * Transformação de Relação); categorias de escolha de operação
     * (Composição de Transformações/Relações) não têm papel desconhecido
     * nesse sentido.
     */
    public synchronized Map<String, Object> posicionarValorConhecido(String papelId, String origemPapelId) {
        Map<String, Object> resultado;
        if (atividadeModelagem != null) {
            resultado = atividadeModelagem.posicionarValorConhecido(papelId, origemPapelId);
        } else if (atividadeEscolhaOperacao != null) {
            resultado = atividadeEscolhaOperacao.posicionarValorConhecido(papelId, origemPapelId);
        } else {
            throw new IllegalStateException(
                    "a situação atual não possui posicionamento de papel conhecido implementado");
        }
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    /**
     * Engata o "?" da incógnita na sua caixa (protocolo mouse-texto,
     * Main.java) — só existe onde há incógnita a digitar (ServicoAtividadeWeb);
     * categorias de escolha de operação não têm esse conceito.
     */
    public synchronized Map<String, Object> engatarIncognita(String papelId, String origemPapelId) {
        if (atividadeModelagem == null) {
            throw new IllegalStateException(
                    "a situação atual não possui incógnita a engatar");
        }
        Map<String, Object> resultado = atividadeModelagem.engatarIncognita(papelId, origemPapelId);
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    /**
     * Ajusta a contagem de quadradinhos do material concreto (AG_EMCME) —
     * só existe para Composição de Medidas hoje (piloto); outras categorias
     * ainda não têm representação complementar portada.
     */
    public synchronized Map<String, Object> ajustarQuadradinho(String papelId, int delta) {
        if (!(atividadeModelagem instanceof ServicoAtividadeWebComposicao)) {
            throw new IllegalStateException(
                    "a situação atual não possui material concreto implementado");
        }
        Map<String, Object> resultado = ((ServicoAtividadeWebComposicao) atividadeModelagem)
                .ajustarQuadradinho(papelId, delta);
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    /**
     * Escolhe positivo/negativo para o papel revelado que ficou "aguardando
     * escolha de sinal" (ver ServicoAtividadeWebComSinal) — capacidade
     * opcional presente só nas categorias com pelo menos um papel que
     * necessita representação de sinal (CatalogoNecessidadeRepresentacaoDeSinal),
     * mesmo padrão instanceof já usado por ajustarQuadradinho para material
     * concreto.
     */
    public synchronized Map<String, Object> escolherSinalNumeroRelativo(String papelId, String sinal) {
        Map<String, Object> resultado;
        if (atividadeModelagem instanceof ServicoAtividadeWebComSinal) {
            resultado = ((ServicoAtividadeWebComSinal) atividadeModelagem)
                    .escolherSinalNumeroRelativo(papelId, sinal);
        } else if (atividadeEscolhaOperacao instanceof ServicoAtividadeWebComSinal) {
            resultado = ((ServicoAtividadeWebComSinal) atividadeEscolhaOperacao)
                    .escolherSinalNumeroRelativo(papelId, sinal);
        } else {
            throw new IllegalStateException(
                    "a situação atual não possui escolha de sinal de número relativo implementada");
        }
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    public synchronized Map<String, Object> escolherOperacao(String seletor, String operacao) {
        if (atividadeEscolhaOperacao == null) {
            throw new IllegalStateException(
                    "a situação atual não possui escolha de operação implementada");
        }
        Map<String, Object> resultado = atividadeEscolhaOperacao.escolherOperacao(seletor, operacao);
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    public synchronized Map<String, Object> reiniciarAtividadeAtual() {
        if (atividadeModelagem == null && atividadeEscolhaOperacao == null) {
            throw new IllegalStateException("a situação atual não possui modelagem web implementada");
        }
        if (atividadeModelagem != null) {
            atividadeModelagem.reiniciar();
        }
        if (atividadeEscolhaOperacao != null) {
            atividadeEscolhaOperacao.reiniciar();
        }
        return projetarEstado();
    }

    public synchronized Map<String, Object> confirmarCategoria(boolean concordou) {
        exigirClassificacaoAtiva();
        RegistroAcaoClassificacaoCategoria registro = tentativaClassificacao
                .avaliarConfirmacaoCategoriaDivergente(concordou,
                        contextoConfirmacao(concordou));
        questionamento = null;
        return resultadoClassificacao(registro);
    }

    private Map<String, Object> resultadoClassificacao(
            RegistroAcaoClassificacaoCategoria registro) {
        Map<String, Object> resultado = mapa();
        resultado.put("schema", "gerard.atividade-web.resultado-classificacao.v1");
        resultado.put("action_id", registro.getActionId());
        resultado.put("correta", Boolean.valueOf(registro.foiCorreta()));
        resultado.put("diagnostico", registro.getTipoDiagnosticoFactual().length() == 0
                ? null : registro.getTipoDiagnosticoFactual());
        resultado.put("desfecho", registro.getDesfecho().name());
        resultado.put("rejeicoes_consecutivas",
                Integer.valueOf(registro.getRejeicoesConsecutivas()));
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    private void exigirClassificacaoAtiva() {
        if (contextoAtual == null || tentativaClassificacao == null) {
            throw new IllegalStateException("nenhuma situação sorteada aguarda classificação");
        }
    }

    private ContextoAcaoInstrumental contextoEscolha(TipoSituacaoAditiva escolhida) {
        return new ContextoAcaoInstrumental("Classificar situação-problema",
                "Selecionar um ícone de categoria", "Faixa de categorias web",
                "Classificar a estrutura da situação",
                TentativaClassificacaoCategoriaAditiva.ALVO_ESCOLHA,
                "SELECAO_CATEGORIA", "categoria_escolhida=" + escolhida.name(),
                "Categoria selecionada para validação", Collections.<String>emptyList());
    }

    private ContextoAcaoInstrumental contextoConfirmacao(boolean concordou) {
        return new ContextoAcaoInstrumental("Confirmar categoria escolhida",
                "Responder ao questionamento", "Diálogo web de confirmação",
                "Reconhecer ou reafirmar classificação divergente",
                TentativaClassificacaoCategoriaAditiva.ALVO_CONFIRMACAO,
                "CONFIRMACAO_CATEGORIA_DIVERGENTE", "concordou=" + concordou,
                "Resposta registrada", Collections.<String>emptyList());
    }

    private Map<String, Object> projetarEstado() {
        Map<String, Object> estado = projetar(contextoAtual, grupoAtual);
        estado.put("categoria_selecionada",
                categoriaSelecionada == null ? null : categoriaSelecionada.name());
        if (tentativaClassificacao.aguardaConfirmacao()) {
            estado.put("modo", "AGUARDANDO_CONFIRMACAO_CATEGORIA");
            estado.put("questionamento", questionamento);
            estado.put("acoes_disponiveis",
                    AcoesDisponiveisAtividadeWeb.confirmacaoCategoria());
        } else if (categoriaSelecionada != null) {
            estado.put("modo", "CATEGORIA_CLASSIFICADA");
            if (atividadeModelagem != null) {
                Map<String, Object> modelagem = atividadeModelagem.estadoAtual();
                estado.put("modelagem", modelagem);
                estado.put("concluida", modelagem.get("concluida"));
                estado.put("acoes_disponiveis", modelagem.get("acoes_disponiveis"));
            } else if (atividadeEscolhaOperacao != null) {
                Map<String, Object> modelagem = atividadeEscolhaOperacao.estadoAtual();
                estado.put("modelagem", modelagem);
                estado.put("concluida", modelagem.get("concluida"));
                estado.put("acoes_disponiveis", modelagem.get("acoes_disponiveis"));
            } else {
                estado.put("acoes_disponiveis", AcoesDisponiveisAtividadeWeb.sorteios());
            }
        } else if (tentativaClassificacao.estaEncerrada()) {
            estado.put("modo", "REEXPLICACAO_CATEGORIA");
            estado.put("categoria_revelada",
                    tentativaClassificacao.getCategoriaEsperada().name());
            estado.put("acoes_disponiveis", AcoesDisponiveisAtividadeWeb.sorteios());
        } else {
            estado.put("acoes_disponiveis",
                    AcoesDisponiveisAtividadeWeb.classificacaoCategoria());
        }
        // Mesmo texto do "?" ao lado do enunciado no desktop
        // (botaoAtalhoProximoPasso, Main.java), visível enquanto a categoria
        // ainda não foi confirmada (!categoriaSelecionadaParaAtividade).
        // Depois de confirmada, o desktop troca para o menu "E agora?"
        // (botaoAjudaTexto/Vergnaud/Complementar, ver ajuda_contextual
        // abaixo).
        estado.put("dica_proximo_passo", categoriaSelecionada == null
                ? AjudaContextualWeb.textoDicaProximoPasso()
                : null);
        boolean revelar = categoriaSelecionada != null
                || tentativaClassificacao.estaEncerrada();
        if (revelar) {
            estado.put("categoria", contextoAtual.getSituacao().getTipo().name());
            List<Object> acoesParaCena = listaDeAcoes(estado.get("acoes_disponiveis"));
            Map<String, Object> cenaProjetada = projetarCena(contextoAtual, acoesParaCena, estado.get("modelagem"));
            estado.put("cena", cenaProjetada);
            // Cena do material concreto (grupos de quadradinhos), gerada
            // pelo mesmo gerador de cena da cena abstrata — ver
            // GeradorCenaDiagramaAditivo.gerarMaterialConcreto. Omitida do
            // contrato (não posta como null) quando não disponível agora ou
            // quando a categoria ainda não tem essa cena implementada.
            Object cenaMaterialConcretoProjetada = projetarCenaMaterialConcreto(
                    contextoAtual, acoesParaCena, estado.get("modelagem"));
            if (cenaMaterialConcretoProjetada != null) {
                estado.put("cena_material_concreto", cenaMaterialConcretoProjetada);
            } else {
                estado.remove("cena_material_concreto");
            }
            // Aliases preservados para consumidores v1 anteriores à cena narrativa.
            estado.put("elementos_texto", cenaProjetada.get("elementos_texto"));
            estado.put("vocabulario_texto", cenaProjetada.get("vocabulario_texto"));
            estado.put("confirmacao_valor_papel", ConfirmacaoValorWeb.perguntaParaCena(cenaProjetada));
        } else {
            estado.remove("categoria");
            estado.remove("subtipo");
            estado.remove("termo_desconhecido");
            estado.remove("representacao_visual");
            estado.remove("operacao_relacao");
            estado.remove("operacao_estado_transformacao");
            estado.remove("papeis");
            estado.remove("diagrama");
            estado.remove("curadoria");
            estado.remove("elementos_texto");
            estado.remove("vocabulario_texto");
            estado.remove("confirmacao_valor_papel");
            estado.remove("cena_material_concreto");
        }
        // Menu "E agora?" (botaoAjudaTexto/Vergnaud/Complementar, Main.java)
        // só existe depois que a categoria foi de fato confirmada — mesma
        // condição de reposicionarBotaoAjudaTexto (categoriaSelecionadaParaAtividade).
        // REEXPLICACAO_CATEGORIA revela a cena mas não confirma categoria,
        // então não entra aqui.
        if (categoriaSelecionada != null) {
            estado.put("ajuda_contextual", projetarAjudaContextual(estado.get("modelagem")));
        } else {
            estado.remove("ajuda_contextual");
        }
        // A curadoria bruta contém a resposta da incógnita e nunca integra o
        // contrato público do participante, mesmo após a classificação.
        estado.remove("curadoria");
        return estado;
    }

    /**
     * Estrutura do menu "E agora?" por área — cabeçalho e rótulos das 3
     * opções (DUVIDA/CONTINUAR/PROXIMO_PASSO), sem a mensagem: igual ao
     * desktop (mostrarMenuAjudaContextual/criarOpcaoAjudaContextual,
     * Main.java), que só resolve a mensagem quando a opção é clicada (ver
     * ajudaContextual). TEXTO e VERGNAUD sempre presentes quando a categoria
     * está confirmada; COMPLEMENTAR só quando a modelagem atual expõe
     * material_concreto_disponivel=true (único caso real portado hoje —
     * quadradinhos de Composição de Medidas, liberados só após o erro
     * consecutivo, nunca por padrão).
     */
    private List<Object> projetarAjudaContextual(Object modelagem) {
        List<Object> areas = new ArrayList<Object>();
        areas.add(AjudaContextualWeb.projetarArea(scaffoldingAjudaContextual, ScaffoldingAjudaContextual.Area.TEXTO));
        areas.add(AjudaContextualWeb.projetarArea(scaffoldingAjudaContextual, ScaffoldingAjudaContextual.Area.VERGNAUD));
        if (modelagem instanceof Map
                && Boolean.TRUE.equals(((Map<?, ?>) modelagem).get("material_concreto_disponivel"))) {
            areas.add(AjudaContextualWeb.projetarArea(scaffoldingAjudaContextual, ScaffoldingAjudaContextual.Area.COMPLEMENTAR));
        }
        return areas;
    }

    /**
     * Ação de clicar numa opção do menu "E agora?" — resolve a mensagem real
     * (ui.help.<area>.<intencao>, mensagens_pt.properties) e grava o mesmo
     * fato granular do desktop (registrarAcaoGranular, Main.java:2874-2892),
     * agora via LoggerInteracaoGerard.getInstancia() — primeira vez que o
     * pacote portabilidade grava nesse log. O contexto (categoria/enunciado)
     * já foi carregado em escolherCategoria via controladorContextoSituacao.
     */
    public synchronized Map<String, Object> ajudaContextual(String areaTexto, String intencaoTexto) {
        ScaffoldingAjudaContextual.Area area;
        ScaffoldingAjudaContextual.Intencao intencao;
        try {
            area = ScaffoldingAjudaContextual.Area.valueOf(areaTexto);
            intencao = ScaffoldingAjudaContextual.Intencao.valueOf(intencaoTexto);
        } catch (RuntimeException erro) {
            throw new IllegalArgumentException("área ou intenção de ajuda contextual inválida: "
                    + areaTexto + "/" + intencaoTexto);
        }
        String nomeArea = AjudaContextualWeb.nomeArea(scaffoldingAjudaContextual, area);
        String rotuloOpcao = AjudaContextualWeb.rotuloOpcao(scaffoldingAjudaContextual, intencao);
        String mensagem = AjudaContextualWeb.mensagem(scaffoldingAjudaContextual, area, intencao);
        // Mesmos 9 argumentos que o wrapper privado registrarAcaoGranular
        // do desktop monta (Main.java:11063-11067) a partir dos 7 que
        // criarOpcaoAjudaContextual passa — "OBJ_INTERACAO" e
        // "ACAO_GRANULAR_SELECIONAR" são os dois fixos que o wrapper
        // acrescenta antes de chamar registrarAcaoGranularUsuario.
        LoggerInteracaoGerard.getInstancia().registrarAcaoGranularUsuario(
                "SELECIONAR",
                "Solicitar ajuda contextual",
                nomeArea,
                "MENU_E_AGORA",
                rotuloOpcao,
                "OBJ_INTERACAO",
                "ACAO_GRANULAR_SELECIONAR",
                "area=" + area.name() + "; intencao=" + intencao.name(),
                "A orientação contextual da área foi apresentada.");
        Map<String, Object> resultado = mapa();
        resultado.put("schema", "gerard.atividade-web.resultado-ajuda-contextual.v1");
        resultado.put("mensagem", mensagem);
        return resultado;
    }

    /**
     * Elementos do enunciado marcados/arrastáveis após a categoria ser
     * aceita — portação de inicializarElementosTexto/
     * vincularPapeisSemanticosAosElementosTexto (Main.java), via
     * SegmentadorTextoSemantico (portátil, sem Swing). Lista completa e
     * ordenada de tokens (não só os vinculados a um papel) para o cliente
     * poder reconstruir o enunciado inteiro juntando com espaço — o
     * tokenizador usa \S+, então isso corresponde exatamente ao texto
     * original.
     */
    private static List<Object> projetarElementosTexto(CenaDiagramaAditivo cena) {
        List<gerard.interpretacao.modelo.SegmentoTextoSemantico> segmentos = cena.getElementosTexto();
        List<Object> resultado = new ArrayList<Object>();
        for (gerard.interpretacao.modelo.SegmentoTextoSemantico segmento : segmentos) {
            Map<String, Object> item = projetarDescritorPalavra(
                    "texto." + resultado.size(), segmento.getValor(),
                    segmento.getTipoNarrativo(), segmento.isManipulavelNaNarrativa(),
                    destinoSaco(segmento));
            item.put("papel_id", segmento.possuiVinculoSemantico()
                    ? segmento.getChavePapelSemantico() : null);
            item.put("incognita", Boolean.valueOf(segmento.representaIncognitaOriginal()));
            resultado.add(item);
        }
        return resultado;
    }

    private static Map<String, Object> projetarVocabularioTexto(CenaDiagramaAditivo cena) {
        Map<String, Object> vocabulario = mapa();
        List<Object> organizadores = new ArrayList<Object>();
        int indice = 0;
        for (String expressao : cena.getVocabularioTexto().getCandidatosOrganizadoresInformacao()) {
            organizadores.add(projetarDescritorPalavra(
                    "vocabulario.organizador." + indice++, expressao,
                    gerard.interpretacao.modelo.TipoSegmentoNarrativo.CANDIDATO_ORGANIZADOR_INFORMACAO,
                    true, "ORGANIZADORES"));
        }
        vocabulario.put("candidatos_organizadores_informacao", organizadores);
        vocabulario.put("modelo_palavra_comum", projetarDescritorPalavra(
                "vocabulario.palavra-comum", "",
                gerard.interpretacao.modelo.TipoSegmentoNarrativo.COMUM, true, "COMUM"));
        return vocabulario;
    }

    private static Map<String, Object> projetarDescritorPalavra(
            String id, String valor,
            gerard.interpretacao.modelo.TipoSegmentoNarrativo tipo,
            boolean manipulavel, String sacoDestino) {
        Map<String, Object> item = mapa();
        item.put("id", id);
        item.put("valor", valor);
        item.put("tipo", tipo.name());
        item.put("manipulavel", Boolean.valueOf(manipulavel));
        item.put("papel_id", null);
        item.put("incognita", Boolean.FALSE);
        item.put("saco_destino", sacoDestino);
        return item;
    }

    private static String destinoSaco(
            gerard.interpretacao.modelo.SegmentoTextoSemantico segmento) {
        if (segmento.getTipoNarrativo()
                == gerard.interpretacao.modelo.TipoSegmentoNarrativo.CANDIDATO_ORGANIZADOR_INFORMACAO) {
            return "ORGANIZADORES";
        }
        if (segmento.getTipoNarrativo()
                == gerard.interpretacao.modelo.TipoSegmentoNarrativo.COMUM
                && segmento.isManipulavelNaNarrativa()) {
            return "COMUM";
        }
        return null;
    }

    private static Map<String, Object> projetarCena(
            ContextoCarregamentoAtividade contexto, List<Object> acoes, Object modelagem) {
        GeradorCenaDiagramaAditivo gerador = new GeradorCenaDiagramaAditivo();
        boolean estadoInicialDecomposto =
                !contexto.getSituacao().getEstadoInicialParte1().trim().isEmpty();
        CenaDiagramaAditivo cena = gerador.gerar(
                contexto.getSituacao().getTipo(), new AreaDiagrama(0, 0, 840, 480),
                contexto.getDefinicao(), new int[] {0, 0, 0}, estadoInicialDecomposto);
        boolean concluida = modelagem instanceof Map
                && Boolean.TRUE.equals(((Map<?, ?>) modelagem).get("concluida"));
        cena = gerador.comElementosTextoNarrativa(cena,
                contexto.getEnunciadoExibido(), contexto.getInterpretacao(), concluida);
        Map<String, Object> valoresPorChave = extrairValoresDePapeisProjetados(modelagem);
        Map<String, Object> resultado = mapa();
        resultado.put("elementos_texto", projetarElementosTexto(cena));
        resultado.put("vocabulario_texto", projetarVocabularioTexto(cena));
        resultado.put("permite_editar_narrativa", Boolean.valueOf(cena.isPermiteEditarNarrativa()));
        resultado.put("titulo", cena.getTitulo());
        resultado.put("descricao", cena.getDescricao());
        resultado.put("figuras", serializarFiguras(cena.getFiguras(), contexto, valoresPorChave, acoes));
        resultado.put("conectores", serializarConectores(cena.getConectores()));
        Map<String, Object> seletorOperacao = projetarSeletorOperacao(
                contexto.getSituacao().getTipo(), cena);
        if (seletorOperacao != null) {
            resultado.put("seletor_operacao", seletorOperacao);
        }
        // Deslocamento assimétrico do diagrama quando o material concreto
        // desta categoria está disponível ao lado dele — direção decidida
        // pelo gerador de cena (ver GeradorCenaDiagramaAditivo.
        // direcaoDeslocamentoParaMaterialConcreto), nunca uma posição fixa.
        boolean materialConcretoDisponivel = modelagem instanceof Map
                && Boolean.TRUE.equals(((Map<?, ?>) modelagem).get("material_concreto_disponivel"));
        DirecaoDeslocamentoDiagrama direcaoDeslocamento = materialConcretoDisponivel
                ? new GeradorCenaDiagramaAditivo().direcaoDeslocamentoParaMaterialConcreto(
                        contexto.getSituacao().getTipo())
                : DirecaoDeslocamentoDiagrama.SEM_DESLOCAMENTO;
        resultado.put("viewport", projetarViewport(cena, contexto, seletorOperacao, direcaoDeslocamento));
        // Decisão agregada da cena (não por figura): existe pelo menos um
        // papel com lupa, logo os painéis de eixo revelados por ela podem
        // ser oferecidos. Mesma regra usada pelo adaptador Swing (ver
        // DecisaoExibicaoPaineisEixo) — um único lugar decide isso, para
        // não obrigar um futuro consumidor web a recalculá-la sozinho.
        // Não decide qual mecanismo de eixo mostrar nem substitui
        // "lupa_habilitada" por figura, que segue como placeholder em
        // aberto (ver LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md).
        resultado.put("paineis_eixo_disponiveis",
                Boolean.valueOf(DecisaoExibicaoPaineisEixo.existeAlgumComLupa(
                        cena.getFiguras())));
        return resultado;
    }

    /**
     * Cena do material concreto (grupos de quadradinhos), ao lado da cena
     * abstrata — mesmo modelo de figuras/conectores, serializado pelo mesmo
     * par de métodos (serializarFiguras/serializarConectores), nunca um
     * componente de interface com sua própria lógica de contagem/posição
     * (ver GeradorCenaDiagramaAditivo.gerarMaterialConcreto). {@code null}
     * quando o material concreto não está disponível agora ou a categoria
     * ainda não tem essa cena implementada — o chamador (projetarSituacao)
     * simplesmente omite "cena_material_concreto" do contrato nesse caso.
     */
    private static Map<String, Object> projetarCenaMaterialConcreto(
            ContextoCarregamentoAtividade contexto, List<Object> acoes, Object modelagem) {
        boolean disponivel = modelagem instanceof Map
                && Boolean.TRUE.equals(((Map<?, ?>) modelagem).get("material_concreto_disponivel"));
        if (!disponivel) {
            return null;
        }
        Map<String, Object> valoresPorChave = extrairValoresDePapeisProjetados(modelagem);
        Object chaveAlvo = ((Map<?, ?>) modelagem).get("papel_desconhecido_original");
        CenaDiagramaAditivo cena = new GeradorCenaDiagramaAditivo().gerarMaterialConcreto(
                contexto.getSituacao().getTipo(), new AreaDiagrama(0, 0, 420, 340),
                contexto.getDefinicao(), new int[] {0, 0, 0},
                chaveAlvo == null ? null : chaveAlvo.toString());
        if (cena == null) {
            return null;
        }
        Map<String, Object> resultado = mapa();
        resultado.put("titulo", cena.getTitulo());
        resultado.put("descricao", cena.getDescricao());
        resultado.put("figuras", serializarFiguras(cena.getFiguras(), contexto, valoresPorChave, acoes));
        resultado.put("conectores", serializarConectores(cena.getConectores()));
        resultado.put("viewport", projetarViewport(cena, contexto, null, DirecaoDeslocamentoDiagrama.SEM_DESLOCAMENTO));
        return resultado;
    }

    private static List<Object> serializarFiguras(List<FiguraDiagrama> figurasFonte,
            ContextoCarregamentoAtividade contexto, Map<String, Object> valoresPorChave,
            List<Object> acoes) {
        List<Object> figuras = new ArrayList<Object>();
        int indice = 0;
        for (FiguraDiagrama figura : figurasFonte) {
            Map<String, Object> item = mapa();
            item.put("id", "figura." + indice++);
            item.put("tipo", figura.getTipo().name());
            item.put("x", Integer.valueOf(figura.getX()));
            item.put("y", Integer.valueOf(figura.getY()));
            item.put("largura", Integer.valueOf(figura.getLargura()));
            item.put("altura", Integer.valueOf(figura.getAltura()));
            item.put("rotulo", figura.getRotulo());
            item.put("posicao_rotulo", figura.getPosicaoRotulo().name());
            item.put("exibir_lupa", Boolean.valueOf(figura.isExibirLupa()));
            item.put("chave_papel_semantico", figura.getChavePapelSemantico());
            SemanticaCuradaSituacao.PapelCurado papel =
                    SemanticaCuradaSituacao.buscar(contexto.getSituacao(), null,
                            figura.getChavePapelSemantico());
            item.put("subtitulo", papel == null ? "" : papel.getParticipante());
            item.put("lupa_habilitada", Boolean.FALSE);
            // Valor atual do papel (null enquanto não posicionado) — sem
            // isso o cliente nunca saberia o que mostrar na caixa depois de
            // arrastar/confirmar um valor (achado ao testar o arraste ao
            // vivo: o valor era gravado no servidor, mas a caixa continuava
            // vazia porque a cena nunca carregava esse dado).
            Object papelProjetado = valoresPorChave.get(figura.getChavePapelSemantico());
            item.put("valor", extrairCampo(papelProjetado, "valor"));
            item.put("conhecido", extrairCampo(papelProjetado, "conhecido"));
            item.put("engatada", extrairCampo(papelProjetado, "engatada"));
            item.put("interacoes_permitidas", projetarInteracoesPermitidas(
                    acoes, figura.getChavePapelSemantico()));
            figuras.add(item);
        }
        return figuras;
    }

    private static List<Object> serializarConectores(List<ConectorDiagrama> conectoresFonte) {
        List<Object> conectores = new ArrayList<Object>();
        for (ConectorDiagrama conector : conectoresFonte) {
            Map<String, Object> item = mapa();
            item.put("tipo", conector.getTipo().name());
            item.put("x1", Integer.valueOf(conector.getX1()));
            item.put("y1", Integer.valueOf(conector.getY1()));
            item.put("x2", Integer.valueOf(conector.getX2()));
            item.put("y2", Integer.valueOf(conector.getY2()));
            item.put("legenda", conector.getLegenda());
            if (conector.temAlvo()) {
                item.put("x_alvo", Integer.valueOf(conector.getXAlvo()));
                item.put("y_alvo", Integer.valueOf(conector.getYAlvo()));
            }
            conectores.add(item);
        }
        return conectores;
    }

    /**
     * Varre o mapa de modelagem (formato varia por categoria: campos
     * nomeados como parte1/parte2/todo, estado_inicial/transformacao_1/...,
     * relacao_1/relacao_2/relacao_final, ou uma lista "papeis") e coleta,
     * por chave de papel, o mapa já projetado por projetarPapel
     * ({id, nome, conhecido, valor}) — sem precisar saber qual formato é
     * qual, já que todos os projetarPapel* das 6 categorias produzem o
     * mesmo formato de item.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> extrairValoresDePapeisProjetados(Object modelagem) {
        Map<String, Object> valores = new LinkedHashMap<String, Object>();
        if (!(modelagem instanceof Map)) {
            return valores;
        }
        for (Object valor : ((Map<String, Object>) modelagem).values()) {
            coletarPapelProjetado(valor, valores);
            if (valor instanceof List) {
                for (Object item : (List<Object>) valor) {
                    coletarPapelProjetado(item, valores);
                }
            }
        }
        return valores;
    }

    @SuppressWarnings("unchecked")
    private static void coletarPapelProjetado(Object valor, Map<String, Object> destino) {
        if (!(valor instanceof Map)) {
            return;
        }
        Map<String, Object> papel = (Map<String, Object>) valor;
        Object id = papel.get("id");
        if (id instanceof String && papel.containsKey("valor") && papel.containsKey("conhecido")) {
            destino.put((String) id, papel);
        }
    }

    @SuppressWarnings("unchecked")
    private static Object extrairCampo(Object papelProjetado, String campo) {
        return papelProjetado instanceof Map
                ? ((Map<String, Object>) papelProjetado).get(campo) : null;
    }

    /**
     * Centro(s) onde o seletor Soma/Subtração se posiciona sobre o diagrama
     * — sempre os dois pontos de Composição de Transformações (mesmo antes
     * da segunda etapa liberar, para o viewport já reservar o espaço e o
     * diagrama não pular de tamanho quando ela aparecer) ou o ponto único de
     * Composição de Relações. Deriva de figuras/conectores já presentes na
     * cena (PosicaoSeletorOperacaoDiagrama), nunca de coordenada própria —
     * mesma fonte usada pelo cálculo do viewport logo abaixo.
     */
    private static Map<String, Object> projetarSeletorOperacao(
            TipoSituacaoAditiva tipo, CenaDiagramaAditivo cena) {
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            FiguraDiagrama t1 = buscarFigura(cena, "papel.transformacao1");
            FiguraDiagrama t2 = buscarFigura(cena, "papel.transformacao2");
            FiguraDiagrama tr = buscarFigura(cena, "papel.transformacaoFinal");
            if (t1 == null || t2 == null || tr == null) {
                return null;
            }
            Map<String, Object> resultado = mapa();
            resultado.put("entre_transformacoes", projetarCentro(
                    PosicaoSeletorOperacaoDiagrama.entreTransformacoes(t1, t2)));
            resultado.put("entre_estado_transformacao", projetarCentro(
                    PosicaoSeletorOperacaoDiagrama.entreEstadoETransformacao(tr)));
            return resultado;
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            if (cena.getConectores().isEmpty()) {
                return null;
            }
            Map<String, Object> resultado = mapa();
            resultado.put("relacao", projetarCentro(
                    PosicaoSeletorOperacaoDiagrama.relacao(cena.getConectores().get(0))));
            return resultado;
        }
        return null;
    }

    /**
     * Mesma consulta usada por serializarFiguras para preencher "subtitulo"
     * — aqui só interessa se existe (não o texto), para saber se o cliente
     * vai desenhar rótulo abaixo da figura mesmo com posicao_rotulo=CENTRO
     * (ver geometriaSvg.ts, coordenadaYDoRotulo: `posicao_rotulo === "ABAIXO"
     * || subtitulo`).
     */
    private static boolean temParticipante(
            ContextoCarregamentoAtividade contexto, String chavePapelSemantico) {
        SemanticaCuradaSituacao.PapelCurado papel = SemanticaCuradaSituacao.buscar(
                contexto.getSituacao(), null, chavePapelSemantico);
        return papel != null && papel.getParticipante() != null
                && !papel.getParticipante().trim().isEmpty();
    }

    private static FiguraDiagrama buscarFigura(CenaDiagramaAditivo cena, String chave) {
        for (FiguraDiagrama figura : cena.getFiguras()) {
            if (chave.equals(figura.getChavePapelSemantico())) {
                return figura;
            }
        }
        return null;
    }

    private static Map<String, Object> projetarCentro(PosicaoSeletorOperacaoDiagrama.Centro centro) {
        Map<String, Object> item = mapa();
        item.put("cx", Integer.valueOf(centro.cx));
        item.put("cy", Integer.valueOf(centro.cy));
        return item;
    }

    // Espaço reservado abaixo/acima da figura para o rótulo (papel) e o
    // subtítulo (participante) que o cliente desenha fora da caixa quando
    // posicao_rotulo não é CENTRO — mesmas coordenadas de
    // coordenadaYDoRotulo/coordenadaYDoSubtitulo em geometriaSvg.ts
    // (ABAIXO: y+altura+40 de baseline; ACIMA: y-32 de baseline), com folga
    // para a altura do texto (13px, ver .scene-figure text em styles.css).
    // Sem isto, o viewport (calculado só a partir da geometria das figuras/
    // conectores) fica curto demais nos casos de cena compacta — a margem
    // proporcional (alturaConteudo/6) não cobre o rótulo, que é cortado pelo
    // SVG (ver GeradorCenaGerard.tsx, width/height explícitos = sem
    // letterbox "de graça" que escondia esse corte antes).
    private static final double ESPACO_ROTULO_FORA_DA_FIGURA = 46;

    private static Map<String, Object> projetarViewport(CenaDiagramaAditivo cena,
            ContextoCarregamentoAtividade contexto, Map<String, Object> seletorOperacao,
            DirecaoDeslocamentoDiagrama direcaoDeslocamento) {
        double minimoX = Double.POSITIVE_INFINITY;
        double minimoY = Double.POSITIVE_INFINITY;
        double maximoX = Double.NEGATIVE_INFINITY;
        double maximoY = Double.NEGATIVE_INFINITY;
        for (FiguraDiagrama figura : cena.getFiguras()) {
            minimoX = Math.min(minimoX, figura.getX());
            minimoY = Math.min(minimoY, figura.getY());
            maximoX = Math.max(maximoX, figura.getX() + figura.getLargura());
            maximoY = Math.max(maximoY, figura.getY() + figura.getAltura());
            if (figura.getPosicaoRotulo() == PosicaoRotuloFigura.ACIMA) {
                minimoY = Math.min(minimoY, figura.getY() - ESPACO_ROTULO_FORA_DA_FIGURA);
            } else if (figura.getPosicaoRotulo() == PosicaoRotuloFigura.ABAIXO
                    || temParticipante(contexto, figura.getChavePapelSemantico())) {
                maximoY = Math.max(maximoY,
                        figura.getY() + figura.getAltura() + ESPACO_ROTULO_FORA_DA_FIGURA);
            }
        }
        for (ConectorDiagrama conector : cena.getConectores()) {
            minimoX = Math.min(minimoX, Math.min(conector.getX1(), conector.getX2()));
            minimoY = Math.min(minimoY, Math.min(conector.getY1(), conector.getY2()));
            maximoX = Math.max(maximoX, Math.max(conector.getX1(), conector.getX2()));
            maximoY = Math.max(maximoY, Math.max(conector.getY1(), conector.getY2()));
            if (conector.temAlvo()) {
                minimoX = Math.min(minimoX, conector.getXAlvo());
                minimoY = Math.min(minimoY, conector.getYAlvo());
                maximoX = Math.max(maximoX, conector.getXAlvo());
                maximoY = Math.max(maximoY, conector.getYAlvo());
            }
        }
        // O seletor Soma/Subtração é desenhado pelo cliente por cima da cena
        // (ver PosicaoSeletorOperacaoDiagrama), fora das figuras/conectores
        // acima — sem isto, a área calculada não sobra espaço para ele e o
        // cliente teria que recalcular/expandir o viewport por conta própria.
        if (seletorOperacao != null) {
            int meiaLargura = PosicaoSeletorOperacaoDiagrama.meiaLargura();
            int raio = PosicaoSeletorOperacaoDiagrama.raioBotao();
            int alturaReservadaAbaixo = raio + 28 + 100; // rótulos (2 linhas) + caixa de explicação
            for (Object valor : seletorOperacao.values()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> centro = (Map<String, Object>) valor;
                int cx = ((Integer) centro.get("cx")).intValue();
                int cy = ((Integer) centro.get("cy")).intValue();
                minimoX = Math.min(minimoX, cx - meiaLargura);
                maximoX = Math.max(maximoX, cx + meiaLargura);
                minimoY = Math.min(minimoY, cy - raio);
                maximoY = Math.max(maximoY, cy + alturaReservadaAbaixo);
            }
        }
        if (!Double.isFinite(minimoX)) {
            minimoX = 0; minimoY = 0; maximoX = 840; maximoY = 480;
        }
        double larguraConteudo = Math.max(1, maximoX - minimoX);
        double alturaConteudo = Math.max(1, maximoY - minimoY);
        double margemX = larguraConteudo / 6.0;
        double margemY = alturaConteudo / 6.0;
        double margemEsquerda = margemX;
        double margemDireita = margemX;
        // Margem assimétrica: o SVG centraliza o conteúdo dentro do próprio
        // viewport (preserveAspectRatio="xMidYMid meet"), então só desloca
        // visualmente o diagrama dentro do painel alargando a margem de um
        // lado só — não adianta mover as figuras, o viewport recentraliza.
        if (direcaoDeslocamento == DirecaoDeslocamentoDiagrama.PARA_ESQUERDA) {
            margemDireita += margemX;
        } else if (direcaoDeslocamento == DirecaoDeslocamentoDiagrama.PARA_DIREITA) {
            margemEsquerda += margemX;
        }
        Map<String, Object> viewport = mapa();
        viewport.put("x", Double.valueOf(minimoX - margemEsquerda));
        viewport.put("y", Double.valueOf(minimoY - margemY));
        viewport.put("largura", Double.valueOf(larguraConteudo + margemEsquerda + margemDireita));
        viewport.put("altura", Double.valueOf(alturaConteudo + 2 * margemY));
        return viewport;
    }

    private static Map<String, Object> projetar(ContextoCarregamentoAtividade contexto,
            Grupo grupo) {
        SituacaoProblemaAditiva s = contexto.getSituacao();
        if (s.getTipo() == null) {
            throw new IllegalStateException("situação curada sem categoria");
        }
        ResultadoInterpretacao interpretacao = contexto.getInterpretacao();
        if (interpretacao == null || interpretacao.getPapeis().isEmpty()) {
            throw new IllegalStateException("situação curada sem estrutura semântica: " + s.getId());
        }

        Map<String, Object> estado = mapa();
        estado.put("schema", SCHEMA);
        estado.put("modo", "CLASSIFICACAO_CATEGORIA");
        estado.put("grupo_sorteio", grupo.name());
        estado.put("situacao_id", s.getId());
        estado.put("situacao_grupo_id", s.getSituacaoGrupoId());
        estado.put("situacao_validada", Boolean.valueOf(s.isValidada()));
        estado.put("idioma", s.getCodigoIdioma());
        estado.put("fonte", s.getFonte());
        estado.put("contexto", s.getContexto());
        estado.put("categoria", s.getTipo().name());
        estado.put("categoria_selecionada", null);
        estado.put("enunciado", contexto.getEnunciadoExibido());
        estado.put("subtipo", s.getSubtipo());
        estado.put("termo_desconhecido", s.getTermoDesconhecido());
        estado.put("representacao_visual", s.getRepresentacaoVisual());
        estado.put("operacao_relacao", vazioComoNulo(s.getOperacaoRelacao()));
        estado.put("operacao_estado_transformacao",
                vazioComoNulo(s.getOperacaoEstadoTransformacao()));
        estado.put("papeis", projetarPapeis(interpretacao));
        estado.put("diagrama", projetarDiagrama(contexto.getDefinicao()));
        estado.put("curadoria", projetarCuradoria(s));
        estado.put("concluida", Boolean.FALSE);
        estado.put("acoes_disponiveis", AcoesDisponiveisAtividadeWeb.classificacaoCategoria());
        return estado;
    }

    private static List<Object> projetarPapeis(ResultadoInterpretacao interpretacao) {
        List<Object> papeis = new ArrayList<Object>();
        for (PapelElementoInterpretado papel : interpretacao.getPapeis()) {
            Map<String, Object> item = mapa();
            item.put("id", papel.getChavePapel());
            item.put("valor", papel.isConhecido() ? papel.getElemento() : null);
            item.put("conhecido", Boolean.valueOf(papel.isConhecido()));
            papeis.add(item);
        }
        return papeis;
    }

    private static Map<String, Object> projetarDiagrama(DefinicaoDiagramaAditivo d) {
        if (d == null) throw new IllegalStateException("definição de diagrama ausente");
        Map<String, Object> diagrama = mapa();
        diagrama.put("titulo", d.getTitulo());
        diagrama.put("rotulos", java.util.Arrays.asList(
                d.getRotulo1(), d.getRotulo2(), d.getRotulo3()));
        return diagrama;
    }

    private static Map<String, Object> projetarCuradoria(SituacaoProblemaAditiva s) {
        Map<String, Object> c = mapa();
        c.put("estado_inicial", vazioComoNulo(s.getEstadoInicial()));
        c.put("estado_intermediario", vazioComoNulo(s.getEstadoIntermediario()));
        c.put("transformacao", vazioComoNulo(s.getTransformacao()));
        c.put("sinal_transformacao", vazioComoNulo(s.getSinalTransformacao()));
        c.put("estado_final", vazioComoNulo(s.getEstadoFinal()));
        c.put("quantidade_1", vazioComoNulo(s.getQuantidade1()));
        c.put("quantidade_2", vazioComoNulo(s.getQuantidade2()));
        c.put("resultado", vazioComoNulo(s.getResultado()));
        c.put("referido", vazioComoNulo(s.getReferido()));
        c.put("referendo", vazioComoNulo(s.getReferendo()));
        c.put("valor_relativo", vazioComoNulo(s.getValorRelativo()));
        c.put("sinal_valor_relativo", vazioComoNulo(s.getSinalValorRelativo()));
        return c;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> listaDeAcoes(Object valor) {
        return valor instanceof List
                ? (List<Object>) valor : Collections.<Object>emptyList();
    }

    @SuppressWarnings("unchecked")
    private static List<Object> projetarInteracoesPermitidas(
            List<Object> acoes, String chavePapel) {
        List<Object> interacoes = new ArrayList<Object>();
        if (chavePapel == null || acoes == null) {
            return interacoes;
        }
        for (Object valor : acoes) {
            if (!(valor instanceof Map)) continue;
            Map<String, Object> acao = (Map<String, Object>) valor;
            String id = String.valueOf(acao.get("id"));
            String tipoInteracao;
            if ("PROPOR_VALOR_PAPEL".equals(id)) {
                tipoInteracao = "EDITAR_VALOR";
            } else if ("POSICIONAR_CONHECIDO".equals(id)) {
                tipoInteracao = "POSICIONAR_CONHECIDO";
            } else if ("ENGATAR_INCOGNITA".equals(id)) {
                tipoInteracao = "ENGATAR_INCOGNITA";
            } else if ("AJUSTAR_QUADRADINHO".equals(id)) {
                tipoInteracao = "AJUSTAR_QUADRADINHO";
            } else {
                continue;
            }
            Object corpoValor = acao.get("corpo");
            if (!(corpoValor instanceof Map)) continue;
            Map<String, Object> corpo = (Map<String, Object>) corpoValor;
            if (!chavePapel.equals(corpo.get("papel_id"))) continue;
            Map<String, Object> interacao = mapa();
            interacao.put("tipo", tipoInteracao);
            interacao.put("acao_id", id);
            interacao.put("fase_envio", "PROPOR_VALOR_PAPEL".equals(id) ? "CONFIRMACAO" : "IMEDIATA");
            interacao.put("papel_id", chavePapel);
            interacoes.add(interacao);
        }
        return interacoes;
    }

    private static String vazioComoNulo(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor;
    }

    private static Map<String, Object> mapa() {
        return new LinkedHashMap<String, Object>();
    }
}
