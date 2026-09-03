import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.InferenciaRegrasModelador;
import gerard.agente.modelador.RepositorioRegrasInferidas;
import gerard.agente.modelador.ConectorVereditoModelador;
import gerard.agente.modelador.RepositorioRegrasAdaptativasPublicadas;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.sincronizacao.ResolvedorRelacoesEstruturaisAditivas;
import gerard.campoaditivo.sincronizacao.representacoes.ProjetorValoresComparacaoComplementar;
import gerard.campoaditivo.conclusao.AvaliadorConclusaoModelagem;
import gerard.campoaditivo.conclusao.AtualizacaoConclusaoModelagem;
import gerard.campoaditivo.conclusao.ControladorConclusaoModelagem;
import gerard.campoaditivo.conclusao.EstadoPosicionamentoModelagem;
import gerard.campoaditivo.conclusao.PoliticaPreenchimentoIncognita;
import gerard.campoaditivo.conclusao.SeletorPapeisConclusaoModelagem;
import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.campoaditivo.semantica.PoliticaValoresAditivos;
import gerard.campoaditivo.sincronizacao.texto.ElementoSemanticoTexto;
import gerard.campoaditivo.sincronizacao.texto.MapeadorPapelSemanticoTexto;
import gerard.campoaditivo.sincronizacao.texto.SincronizadorElementosSemanticosTexto;
import gerard.campoaditivo.sincronizacao.texto.SincronizadorElementosSemanticosTextoAditivo;
import gerard.campoaditivo.servico.ControladorContextoSituacao;
import gerard.campoaditivo.curadoria.TelaCuradoriaSituacoes;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.curadoria.MaterializadorEnunciadoCurado;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.montagem.TelaMontagemSituacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.apresentacao.RenderizadorSwingDiagramaAditivo;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import gerard.campoaditivo.venn.modelo.NoDiagramaVenn;
import gerard.campoaditivo.venn.modelo.ConectorDiagramaVenn;
import gerard.campoaditivo.venn.servico.GeradorCenaDiagramaVenn;
import gerard.campoaditivo.venn.mapeamento.FabricaMapeamentosPapeisComplementares;
import gerard.campoaditivo.venn.mapeamento.MapeamentoPapeisRepresentacaoComplementar;
import gerard.campoaditivo.venn.apresentacao.EstadoVisualUnidadeVenn;
import gerard.campoaditivo.venn.apresentacao.FabricaRenderizadoresUnidadeVenn;
import gerard.campoaditivo.venn.apresentacao.RenderizadorUnidadeVenn;
import gerard.campoaditivo.venn.apresentacao.ProjetorValorPapelDiagramaComplementar;
import gerard.campoaditivo.representacao.SeletorRepresentacaoComplementar;
import gerard.campoaditivo.representacao.SelecionadorPlanoUnidadesTransformacao;
import gerard.campoaditivo.representacao.TipoRepresentacaoComplementar;
import gerard.campoaditivo.transformacao.processo.PoliticaSinalTransformacaoComplementar;
import gerard.campoaditivo.transformacao.processo.ControleSinalProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.EstadoProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.LayoutUnidadesProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.PlanoUnidadesProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.RenderizadorProcessoTransformacao;
import gerard.idioma.IdiomaInterface;
import gerard.idioma.IdiomaSituacao;
import gerard.idioma.CadastroIdiomasSituacao;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.interpretacao.modelo.ResolvedorPapelInterpretado;
import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.SubtipoVergnaud;
import gerard.interpretacao.simbolo.SimboloDesconhecido;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.RecalculoComparacaoMedidas;
import gerard.campoaditivo.diagrama.modelo.DecisaoExibicaoPaineisEixo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComparacao;
import gerard.interacao.eixo.PoliticaRestauracaoValorRelativo;
import gerard.aplicacao.EstadoNumericoComparacaoCategorias;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.dominio.campoaditivo.RegistroAcaoClassificacaoCategoria;
import gerard.dominio.campoaditivo.RegistroAcaoEscolhaSinalPapelQuantitativo;
import gerard.dominio.campoaditivo.TentativaClassificacaoCategoriaAditiva;
import gerard.dominio.campoaditivo.TentativaEscolhaSinalPapelQuantitativo;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.CatalogoObjetosLogAcaoInstrumental;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.aplicacao.adaptacao.ContextoRegistroAjuda;
import gerard.aplicacao.adaptacao.ExecutorAjudaIncognita;
import gerard.aplicacao.adaptacao.ProjetorContextoAdaptativoIncognita;
import gerard.aplicacao.adaptacao.ResultadoContextualizacaoIncognita;
import gerard.aplicacao.adaptacao.ResultadoExecucaoAjudaIncognita;
import gerard.dominio.campoaditivo.ajuda.FormatoAjudaNarrativaVisual;
import gerard.dominio.campoaditivo.ajuda.RepertorioAjudaVisual;
import gerard.semantica.numero.ConversorTextoParaInteiroSemantico;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.ValorNumerico;
import gerard.semantica.numero.OpcaoSinalNumeroInteiro;
import gerard.semantica.quantidade.ServicoQuantidadeContextual;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.modelo.NumeroEncontrado;
import gerard.i18n.ServicoLocalizacao;
import gerard.Scaffolding.ScaffoldingNumeroRelativo;
import gerard.Scaffolding.proximidade.EstadoRealceAlvo;
import gerard.Scaffolding.proximidade.EstiloRealceAlvo;
import gerard.estilointeracao.EstiloInteracao;
import gerard.Scaffolding.proximidade.ScaffoldingProximidade;
import gerard.Scaffolding.automatizacao.ScaffoldingAutomatizacaoPassos;
import gerard.Scaffolding.questionamento.ResultadoQuestionamento;
import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.Scaffolding.venn.ControleAdicionarQuadradinhoVenn;
import gerard.Scaffolding.venn.ControleRemoverQuadradinhoVenn;
import gerard.Scaffolding.venn.ScaffoldingLimiteQuantidadeVenn;
import gerard.Scaffolding.venn.CondicaoHabilitacaoAdicaoUnidades;
import gerard.Scaffolding.venn.CondicaoDiagramaVergnaudNaoVazio;
import gerard.Scaffolding.venn.EstadoModelagemVergnaud;
import gerard.campoaditivo.venn.interacao.OperacoesUnidadesVenn;
import gerard.campoaditivo.venn.interacao.RepresentacaoComUnidadesAdicionaveis;
import gerard.campoaditivo.venn.interacao.RepresentacaoComUnidadesRemoviveis;
import gerard.campoaditivo.venn.interacao.RepresentacaoVennEditavel;
import gerard.campoaditivo.venn.interacao.ResultadoOperacaoUnidade;
import gerard.campoaditivo.sincronizacao.representacoes.EstadoPrimeiroPosicionamento;
import gerard.campoaditivo.sincronizacao.representacoes.PoliticaInteracaoRepresentacoes;
import gerard.campoaditivo.sincronizacao.representacoes.CoordenadorSincronizacaoRepresentacoes;
import gerard.campoaditivo.sincronizacao.representacoes.DestinoSincronizacaoRepresentacoes;
import gerard.campoaditivo.sincronizacao.representacoes.CapturadorValoresRepresentacaoComplementar;
import gerard.campoaditivo.sincronizacao.representacoes.ValoresCapturadosRepresentacaoComplementar;
import gerard.Scaffolding.feedbackerro.ScaffoldingFeedbackMultissensorialErro;
import gerard.Scaffolding.feedbackerro.ScaffoldingFeedbackProxyPosicionamento;
import gerard.Scaffolding.feedbackerro.ControladorAnotacaoTemporaria;
import gerard.Scaffolding.conclusao.AplicadorDestaqueConclusaoDiagrama;
import gerard.Scaffolding.ajudacontextual.ScaffoldingAjudaContextual;
import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import gerard.Scaffolding.pickup.DesenhavelPickup;
import gerard.Scaffolding.pickup.FornecedorCursoresPickup;
import gerard.Scaffolding.pickup.FornecedorCursoresPickupSwing;
import gerard.Scaffolding.pickup.RenderizadorPickup;
import gerard.Scaffolding.pickup.RenderizadorPickupElevado;
import gerard.Scaffolding.arraste.ControladorArrasteElastico;
import gerard.Scaffolding.arraste.ControladorArrasteMolaMomento;
import gerard.Scaffolding.arraste.DesenhavelFantasmaOrigem;
import gerard.Scaffolding.arraste.MarcadorOrigemArraste;
import gerard.Scaffolding.arraste.MarcadorOrigemArrasteTracejado;
import gerard.Scaffolding.arraste.OuvinteArrasteElastico;
import gerard.pesquisador.TelaVisaoPesquisador;
import gerard.pesquisador.log.LoggerInteracaoGerard;
import gerard.pesquisador.log.LoggerGestosInteracaoGerard;
import gerard.pesquisador.log.RegistradorEventosAjudaLogGerard;
import gerard.aplicacao.AcaoAtividade;
import gerard.aplicacao.ControladorEstadoAtividade;
import gerard.aplicacao.ContextoCarregamentoAtividade;
import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas.Grupo;
import gerard.aplicacao.ResolvedorValorEsperadoIncognita;
import gerard.aplicacao.ServicoAvaliacaoAcaoIncognita;
import gerard.pesquisador.tentativa.ItemExplicacaoModelagem;
import gerard.pesquisador.tentativa.TelaArtefatoExplicativo;
import gerard.ui.menu.ConfiguradorOpcaoComparacaoCategorias;
import gerard.ui.vergnaud.SeletorIndicesEstadoCompartilhado;
import gerard.ui.vergnaud.AtualizacaoElementoVergnaud;
import gerard.ui.vergnaud.PlanejadorAplicacaoEstadoVergnaud;
import gerard.ui.vergnaud.ApresentadorItemVergnaud;
import gerard.ui.vergnaud.PaineisEixosRelacoes;
import gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno;
import gerard.ui.vergnaud.AdaptadorMovimentoConectorVergnaud;
import gerard.ui.vergnaud.AdaptadorInteracaoPaineisEixosRelacoes;
import gerard.ui.vergnaud.FonteGeometriaInteracaoPaineisEixosRelacoes;
import gerard.ui.enunciado.GeometriaAreaEnunciado;
import gerard.ui.ajuda.PainelAjudaNarrativaVisualCategoria;
import gerard.ui.swing.adaptacao.MaterializadorDecisaoAjudaSwing;
import gerard.ui.janela.ConfiguradorJanelaPrincipal;
import gerard.ui.janela.DimensionadorJanelaComparacaoCategorias;
import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.FragmentoAnotacao;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;
import gerard.interacao.arraste.HandlerInteracaoElementoTextoMovel;
import gerard.interacao.arraste.HandlerInteracaoArrasteIncremental;
import gerard.interacao.arraste.HandlerInteracaoItemTextoArrastavel;
import gerard.interacao.arraste.HandlerInteracaoQuadradinhoVenn;
import gerard.interacao.arraste.AlvoInteracaoPaineisEixosRelacoes;
import gerard.interacao.arraste.HandlerInteracaoPaineisEixosRelacoes;
import gerard.interacao.arraste.PoliticaGestoEstrutural;
import gerard.interacao.ContextoRegistroGesto;
import gerard.interacao.DestinoGeometricoGesto;
import gerard.interacao.PublicadorGestoInteracao;
import gerard.interacao.geometria.LimitesMovimento;
import gerard.interacao.texto.PoliticaElementoMatematicoTexto;
import gerard.interacao.texto.PoliticaUnicidadeElementoMatematicoTexto;
import gerard.interacao.texto.ResolvedorPickupElementoMatematicoTexto;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.Elemento;
import gerard.campoaditivo.diagrama.elementos.Seta;
import gerard.suporte.RegistroRelatoBug;
import gerard.suporte.PreparadorEmailRelatoBug;
import gerard.ui.conclusao.CalculadorAreaVisualDiagramaVergnaud;
import gerard.desktop.composicaomedidas.JanelaComposicaoMedidasDesktop;
import gerard.ui.conclusao.SeloConclusaoModelagem;
import gerard.ui.conclusao.SequenciadorFeedbackConclusao;
import gerard.ui.conclusao.TipConclusaoModelagem;

public class Main extends JFrame {

    /**
     * Bloqueio simples de uso para impedir alterações acidentais na curadoria.
     * Não representa mecanismo de segurança para dados sigilosos.
     */
    static final class ControleAcessoPesquisador {
        private static final String SENHA_FIXA = "gerard";
        private static boolean autorizadoNestaSessao;

        private ControleAcessoPesquisador() {
        }

        static boolean solicitarAutorizacao(Component componentePai) {
            if (autorizadoNestaSessao) {
                return true;
            }

            ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
            JPasswordField campoSenha = new JPasswordField(16);
            campoSenha.getAccessibleContext().setAccessibleName(localizacao.texto("ui.researcher.password"));
            campoSenha.getAccessibleContext().setAccessibleDescription(localizacao.texto("ui.researcher.password.prompt"));

            JPanel painel = new JPanel(new BorderLayout(0, 8));
            painel.add(new JLabel(localizacao.texto("ui.researcher.password.prompt")), BorderLayout.NORTH);
            painel.add(campoSenha, BorderLayout.CENTER);

            int resposta = JOptionPane.showConfirmDialog(
                    componentePai,
                    painel,
                    localizacao.texto("ui.researcher.access.title"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (resposta != JOptionPane.OK_OPTION) {
                return false;
            }

            char[] digitada = campoSenha.getPassword();
            boolean correta = SENHA_FIXA.equals(new String(digitada));
            java.util.Arrays.fill(digitada, '\0');

            if (!correta) {
                JOptionPane.showMessageDialog(
                        componentePai,
                        localizacao.texto("ui.researcher.password.invalid"),
                        localizacao.texto("ui.researcher.access.title"),
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            autorizadoNestaSessao = true;
            return true;
        }
    }

    public static void main(String[] args) {
        semearDadosPesquisadorSeNecessario();
        aplicarTemaSwingPadrao();
        prepararGatilhoMineracaoAutomatica();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main janela = new Main();
                janela.setVisible(true);
                dispararMineracaoSeNecessario();
            }
        });
    }

    // ---- Gatilho automático de mineração (PART + Apriori) sobre o conjunto
    // acumulado de todos os participantes — ver proposta aprovada em
    // 2026-08-04. Contador global (ContadorMineracao, incrementado em
    // AgenteModelador.armazenarCaso) decide quando disparar; roda ao abrir
    // e ao fechar o Gerard — nunca durante a interação pedagógica em
    // andamento — para não ser perceptível ao participante. Um guard em
    // memória evita que os dois gatilhos disparem mineração concorrente
    // sobre praticamente os mesmos dados.

    private static final int LIMIAR_MINERACAO_AUTOMATICA = 50;
    private static final int MINIMO_INSTANCIAS_PART_AUTOMATICO = 10;
    private static final int MINIMO_INSTANCIAS_APRIORI_AUTOMATICO = 10;
    private static final long TIMEOUT_MINERACAO_MS = 10_000L;

    private static final java.util.concurrent.atomic.AtomicBoolean mineracaoEmAndamento =
            new java.util.concurrent.atomic.AtomicBoolean(false);
    private static volatile Thread threadMineracao;
    private static volatile boolean mineracaoTimeoutExpirado;

    /**
     * Registra o gatilho de fechamento uma única vez, no início de main().
     * Roda numa thread própria da JVM (shutdown hook), separada da
     * interface — não trava nenhuma tela, que já está fechando/fechada
     * nesse ponto. EXIT_ON_CLOSE (já configurado no construtor de Main)
     * chama System.exit(), que espera o shutdown hook terminar antes de
     * encerrar o processo — por isso o timeout: sem ele, uma mineração
     * travada manteria o processo do Gerard rodando indefinidamente em
     * segundo plano, mesmo com a janela já fechada.
     */
    private static void prepararGatilhoMineracaoAutomatica() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                dispararMineracaoSeNecessario();
                Thread t = threadMineracao;
                if (t != null) {
                    try {
                        t.join(TIMEOUT_MINERACAO_MS);
                    } catch (InterruptedException ignored) {
                    }
                    if (t.isAlive()) {
                        // Timeout estourado: a thread de mineração ainda vai checar
                        // esta flag antes de gravar — se estourou, ela descarta o
                        // resultado em vez de arriscar uma escrita interrompida.
                        mineracaoTimeoutExpirado = true;
                    }
                }
            }
        }));
    }

    /**
     * Chamado ao abrir (logo após a janela ficar visível) e ao fechar (do
     * shutdown hook). A checagem do limiar é barata e roda direto; se
     * disparar, a mineração em si roda numa thread separada — nunca na
     * thread de eventos do Swing. compareAndSet garante que, se os dois
     * gatilhos chamarem isto quase juntos, só uma mineração é iniciada; o
     * outro chamador só vê a mesma thread já em andamento (via
     * threadMineracao) e, no caso do fechamento, espera por ela.
     */
    private static void dispararMineracaoSeNecessario() {
        final RepositorioModeloUsuario repositorio = new RepositorioModeloUsuario();
        final AgenteModelador agenteModelador = new AgenteModelador(repositorio);
        if (agenteModelador.contadorMineracaoAtual() < LIMIAR_MINERACAO_AUTOMATICA) {
            return;
        }
        if (!mineracaoEmAndamento.compareAndSet(false, true)) {
            return;
        }
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    executarMineracaoGlobalEPersistir(agenteModelador);
                } finally {
                    mineracaoEmAndamento.set(false);
                }
            }
        });
        t.setDaemon(true);
        threadMineracao = t;
        t.start();
    }

    private static void executarMineracaoGlobalEPersistir(AgenteModelador agenteModelador) {
        try {
            InferenciaRegrasModelador.Resultado resultado = agenteModelador.inferirRegrasGlobal(
                    MINIMO_INSTANCIAS_PART_AUTOMATICO, MINIMO_INSTANCIAS_APRIORI_AUTOMATICO);
            if (mineracaoTimeoutExpirado) {
                // Timeout já estourado enquanto o Weka rodava — não grava
                // resultado tardio, mesmo que tenha terminado a tempo do
                // cálculo em si.
                return;
            }
            new RepositorioRegrasInferidas().salvar("TODOS", resultado);
            agenteModelador.zerarContadorMineracao();
        } catch (Exception ex) {
            // Falha na mineração automática não deve derrubar o app nem o
            // encerramento — a próxima abertura/fechamento tenta de novo,
            // já que o contador só zera após sucesso.
        }
    }

    /**
     * Se ~/Gerard ainda não existe (instalação nova, sem nenhum dado prévio),
     * copia pra lá o conteúdo de Gerard_seed — uma pasta opcional ao lado do
     * Gerard.jar — antes de qualquer repositório (situações curadas, modelo
     * do usuário, log de interação) tentar ler de ~/Gerard. Isso precisa
     * rodar antes de "new Main()", já que os repositórios leem ~/Gerard na
     * própria construção. Nunca sobrescreve uma pasta ~/Gerard existente —
     * só semeia em instalação genuinamente nova. Sem Gerard_seed (a
     * distribuição pública não leva essa pasta — decisão da usuária,
     * 2026-07-28, dados de pesquisa não vão pro instalador público), o app
     * simplesmente segue com ~/Gerard vazio, como sempre funcionou.
     */
    private static void semearDadosPesquisadorSeNecessario() {
        java.io.File pastaGerard = new java.io.File(System.getProperty("user.home"), "Gerard");
        if (pastaGerard.exists()) {
            return;
        }
        try {
            java.io.File localizacaoJar = new java.io.File(Main.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            java.io.File dirApp = localizacaoJar.isFile() ? localizacaoJar.getParentFile() : localizacaoJar;
            java.io.File semente = new java.io.File(dirApp, "Gerard_seed");
            if (semente.isDirectory()) {
                copiarDiretorio(semente.toPath(), pastaGerard.toPath());
            }
        } catch (Exception ex) {
            // Falha ao semear não deve impedir o app de abrir — segue com ~/Gerard vazio.
        }
    }

    private static void copiarDiretorio(final java.nio.file.Path origem, final java.nio.file.Path destino) throws java.io.IOException {
        java.nio.file.Files.walk(origem).forEach(new java.util.function.Consumer<java.nio.file.Path>() {
            public void accept(java.nio.file.Path caminho) {
                try {
                    java.nio.file.Path relativo = origem.relativize(caminho);
                    java.nio.file.Path alvo = destino.resolve(relativo.toString());
                    if (java.nio.file.Files.isDirectory(caminho)) {
                        java.nio.file.Files.createDirectories(alvo);
                    } else {
                        java.nio.file.Files.createDirectories(alvo.getParent());
                        java.nio.file.Files.copy(caminho, alvo, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (java.io.IOException ex) {
                    throw new java.io.UncheckedIOException(ex);
                }
            }
        });
    }

    /**
     * O tema padrão do Swing (Metal/Ocean) pinta abas selecionadas e certos
     * elementos de diálogo em azul, fora da paleta neutra do app. Como isso é
     * pintura nativa do Look and Feel (não constantes do próprio código),
     * precisa ser sobrescrito via UIManager antes de qualquer componente ser
     * criado — cobre a aba selecionada e os JOptionPane usados nos diálogos
     * (ex.: "Acesso do pesquisador", edição de valores).
     */
    private static void aplicarTemaSwingPadrao() {
        UIManager.put("TabbedPane.selected", gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE);
        UIManager.put("TabbedPane.selectHighlight", gerard.ui.UITemaGerard.COR_SUPERFICIE);
        UIManager.put("TabbedPane.focus", gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
        UIManager.put("TabbedPane.contentAreaColor", gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE);

        UIManager.put("OptionPane.background", gerard.ui.UITemaGerard.COR_SUPERFICIE);
        UIManager.put("Panel.background", gerard.ui.UITemaGerard.COR_SUPERFICIE);
        UIManager.put("OptionPane.messageForeground", gerard.ui.UITemaGerard.COR_TEXTO);
        UIManager.put("TextField.selectionBackground", gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE);
        UIManager.put("TextField.selectionForeground", gerard.ui.UITemaGerard.COR_TEXTO);
        UIManager.put("Button.select", gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE);
        UIManager.put("Button.focus", gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
    }

    public Main() {
        setTitle(ServicoLocalizacao.getInstancia().texto("ui.app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final TelaGerard telaGerard = new TelaGerard();
        final TelaMontagemSituacao telaMontagem = new TelaMontagemSituacao(
                telaGerard.getRepositorioSituacoesAditivas());
        final TelaCuradoriaSituacoes telaCuradoria = new TelaCuradoriaSituacoes(telaGerard.getRepositorioSituacoesAditivas(), new Runnable() {
            public void run() {
                telaGerard.recarregarSituacoesCuradasSemPerderIdiomaAtual();
                telaMontagem.recarregarSituacoesCuradas();
            }
        });
        telaGerard.definirTelaCuradoriaSituacoes(telaCuradoria);
        final JTabbedPane abas = new JTabbedPane();
        abas.addTab(ServicoLocalizacao.getInstancia().texto("ui.tab.gerard"), telaGerard);
        abas.setToolTipTextAt(0, ServicoLocalizacao.getInstancia().texto("ui.tab.gerard.tooltip"));
        abas.addTab(ServicoLocalizacao.getInstancia().texto("ui.tab.assembly"), telaMontagem);
        abas.setToolTipTextAt(1, ServicoLocalizacao.getInstancia().texto("ui.tab.assembly.tooltip"));
        abas.addTab(ServicoLocalizacao.getInstancia().texto("ui.tab.curation"), telaCuradoria);
        abas.setToolTipTextAt(2, ServicoLocalizacao.getInstancia().texto("ui.tab.curation.tooltip"));
        abas.addChangeListener(new ChangeListener() {
            private boolean restaurandoSelecao;
            private Component componenteAnterior = telaGerard;

            public void stateChanged(ChangeEvent e) {
                if (restaurandoSelecao) {
                    return;
                }

                Component selecionado = abas.getSelectedComponent();
                if (selecionado == telaCuradoria
                        && !ControleAcessoPesquisador.solicitarAutorizacao(Main.this)) {
                    restaurandoSelecao = true;
                    try {
                        abas.setSelectedComponent(componenteAnterior == null ? telaGerard : componenteAnterior);
                    } finally {
                        restaurandoSelecao = false;
                    }
                    return;
                }

                if (componenteAnterior == telaMontagem && selecionado != telaMontagem) {
                    telaMontagem.desativarAba();
                }
                if (selecionado == telaMontagem && componenteAnterior != telaMontagem) {
                    telaMontagem.ativarAba();
                }

                // Categoria/Nova situação-problema (na JMenuBar) só fazem
                // sentido na aba Gerard — ver definirAbaGerardAtiva.
                telaGerard.definirAbaGerardAtiva(selecionado == telaGerard);

                ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
                abas.setTitleAt(0, loc.texto("ui.tab.gerard"));
                abas.setToolTipTextAt(0, loc.texto("ui.tab.gerard.tooltip"));
                abas.setTitleAt(1, loc.texto("ui.tab.assembly"));
                abas.setToolTipTextAt(1, loc.texto("ui.tab.assembly.tooltip"));
                abas.setTitleAt(2, loc.texto("ui.tab.curation"));
                abas.setToolTipTextAt(2, loc.texto("ui.tab.curation.tooltip"));
                componenteAnterior = selecionado;
            }
        });
        add(abas, BorderLayout.CENTER);
        setJMenuBar(telaGerard.menuBarPrincipal);
        ConfiguradorJanelaPrincipal.aplicar(this);
    }

    static class TelaGerard extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

        String textoProblema = "";
        SituacaoProblemaAditiva situacaoProblemaAtual;

        IdiomaInterface idiomaSelecionado = IdiomaInterface.PORTUGUES;
        TipoSituacaoAditiva tipoSituacaoSelecionada = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
        boolean categoriaSelecionadaParaAtividade = false;
        // Estado do modo "adivinhar categoria" (Sortear): a categoria certa
        // fica guardada aqui, escondida da UI, enquanto
        // categoriaSelecionadaParaAtividade permanece false — ver
        // iniciarQuizCategoria/clicarAtalhoCategoria.
        TipoSituacaoAditiva categoriaSorteioOculta;
        boolean aguardandoAdivinhacaoCategoria = false;
        // A tentativa é o menor agregado que conhece a situação classificada,
        // a escolha do participante e a sequência de rejeições. A Main apenas
        // conserva a referência do fluxo corrente e materializa seu desfecho.
        TentativaClassificacaoCategoriaAditiva tentativaClassificacaoCategoriaAtual;
        // Um proprietário por papel inteiro curado. A Main conserva somente
        // as referências da atividade corrente e encaminha cada seleção.
        Map<String, TentativaEscolhaSinalPapelQuantitativo>
                tentativasEscolhaSinalAtual = Collections.emptyMap();
        RepositorioSituacoesAditivas repositorioSituacoesAditivas = new RepositorioSituacoesAditivas();
        CadastroIdiomasSituacao cadastroIdiomasSituacao = new CadastroIdiomasSituacao();
        CatalogoDefinicoesAditivas catalogoDefinicoesAditivas = new CatalogoDefinicoesAditivas();
        DefinicaoDiagramaAditivo definicaoDiagramaAtual;
        GeradorCenaDiagramaAditivo geradorCenaDiagrama = new GeradorCenaDiagramaAditivo();
        RenderizadorSwingDiagramaAditivo renderizadorSwingDiagrama = new RenderizadorSwingDiagramaAditivo();
        CenaDiagramaAditivo cenaDiagramaAtual;
        // Guardam o deslocamento de centralização aplicado na última montagem
        // do diagrama de Vergnaud, para permitir apenas *reposicionar* (sem
        // recriar/perder o que já foi arrastado) quando a janela é
        // redimensionada. Ver reposicionarDiagramaVergnaudParaAreaAtual().
        int deslocamentoCentroXAplicadoDiagramaVergnaud;
        int deslocamentoCentroYAplicadoDiagramaVergnaud;
        GeradorCenaDiagramaVenn geradorCenaDiagramaVenn = new GeradorCenaDiagramaVenn();
        final SeletorRepresentacaoComplementar seletorRepresentacaoComplementar =
                new SeletorRepresentacaoComplementar();
        final RenderizadorProcessoTransformacao renderizadorProcessoTransformacao =
                new RenderizadorProcessoTransformacao();
        final ControleSinalProcessoTransformacao controleSinalProcessoTransformacao =
                new ControleSinalProcessoTransformacao();
        final LayoutUnidadesProcessoTransformacao layoutUnidadesProcessoTransformacao =
                new LayoutUnidadesProcessoTransformacao();
        final PoliticaSinalTransformacaoComplementar politicaSinalTransformacaoComplementar =
                new PoliticaSinalTransformacaoComplementar();
        final CapturadorValoresRepresentacaoComplementar
                capturadorValoresRepresentacaoComplementar =
                new CapturadorValoresRepresentacaoComplementar(
                        politicaSinalTransformacaoComplementar);
        final gerard.campoaditivo.sincronizacao.SimuladorEstadoComplementarVenn
                simuladorEstadoComplementarVenn =
                new gerard.campoaditivo.sincronizacao.SimuladorEstadoComplementarVenn(
                        politicaSinalTransformacaoComplementar);
        final SelecionadorPlanoUnidadesTransformacao
                selecionadorPlanoUnidadesTransformacao =
                new SelecionadorPlanoUnidadesTransformacao();
        // Composição de Transformações (2026-08-07) — mesma família visual
        // do Processo de Transformação acima, mas com três funis
        // independentes (Transformação 1, 2 e Final) num único canal, sem
        // caixas de estado. Ver gerard.campoaditivo.transformacao.composicao
        // e RELATORIO_PROCESSO_COMPOSICAO_TRANSFORMACOES_2026-08-07.md.
        final gerard.campoaditivo.transformacao.composicao.RenderizadorComposicaoTransformacoesProcesso
                renderizadorComposicaoTransformacoesProcesso =
                new gerard.campoaditivo.transformacao.composicao.RenderizadorComposicaoTransformacoesProcesso();
        final gerard.campoaditivo.transformacao.composicao.ControleSinalComposicaoTransformacoes
                controleSinalComposicaoTransformacoes =
                new gerard.campoaditivo.transformacao.composicao.ControleSinalComposicaoTransformacoes();
        final gerard.campoaditivo.transformacao.composicao.LayoutUnidadesComposicaoTransformacoes
                layoutUnidadesComposicaoTransformacoes =
                new gerard.campoaditivo.transformacao.composicao.LayoutUnidadesComposicaoTransformacoes();
        final ConversorTextoParaInteiroSemantico conversorTextoParaInteiroSemantico =
                new ConversorTextoParaInteiroSemantico();
        final ServicoQuantidadeContextual servicoQuantidadeContextual =
                new ServicoQuantidadeContextual();
        PlanoUnidadesProcessoTransformacao planoUnidadesProcessoAtual;
        CenaDiagramaVenn cenaDiagramaVennAtual;
        Rectangle ultimaAreaDiagramaVenn = null;
        String assinaturaDiagramaVennSincronizado = "";
        // Barra de menu clássica estilo Windows (Arquivo/Exibir/Ferramentas/
        // Ajuda), ver criarMenuPrincipal(). menuBarPrincipal fica com Main
        // (setJMenuBar), mas quem constrói/atualiza o conteúdo é TelaGerard,
        // já que os itens chamam métodos e leem estado privados dela.
        // itemNovaSituacao/menuCategoria continuam como campos porque
        // precisam ser habilitados/desabilitados de fora (troca de aba, em
        // Main) além de internamente (ver definirAbaGerardAtiva).
        JMenuBar menuBarPrincipal;
        JMenuItem itemNovaSituacao;
        JMenu menuCategoria;
        // Botões de ícone embutidos no cabeçalho desta aba (decisão da
        // usuária, 2026-07-28) — Sortear traz o Sortear pra fora do menu
        // Arquivo, que ela achou escondido demais; Comparar tem a mesma
        // função do item de menu Comparar categorias. Ver
        // criarBotoesCabecalhoEmbutidos.
        JButton botaoCompararCategorias;
        JButton botaoFerramentaSortearMedidas;
        JButton botaoFerramentaSortearRelacoes;
        boolean abaGerardAtiva = true;
        gerard.pesquisador.IndicadorPulsoAgente indicadorAgenteModelador;
        JPanel caixaIndicadorAgenteModelador;
        gerard.pesquisador.FaixaLateralAtividadeModelador faixaAtividadeModelador;
        JButton botaoAtalhoComposicao;
        JButton botaoAtalhoTransformacao;
        JButton botaoAtalhoComparacao;
        JButton botaoAtalhoComposicaoTransformacoes;
        JButton botaoAtalhoTransformacaoRelacao;
        JButton botaoAtalhoComposicaoRelacoes;
        JPanel separadorAtalhoCategoria;
        JButton botaoAtalhoProximoPasso;
        // Posição ao lado dos ícones de categoria — usada só depois que a
        // categoria já foi confirmada (ver reposicionarBotaoAtalhoProximoPasso).
        int xPadraoBotaoAtalhoProximoPasso;
        int yPadraoBotaoAtalhoProximoPasso;
        // Centro horizontal de cada grupo de 3 ícones, para desenhar os
        // rótulos "Medidas"/"Relações" acima deles (ver desenharFaixaAtalhoCategoria).
        int xCentroGrupoMedidas;
        int xCentroGrupoRelacoes;
        // Retângulo delimitador de cada grupo de 3 ícones (ver desenharFaixaAtalhoCategoria).
        Rectangle areaGrupoMedidas;
        Rectangle areaGrupoRelacoes;
        JButton botaoRestaurar;
        JButton botaoCorrigirCuradoria;
        JButton botaoIdiomaSituacao;
        JButton botaoRestaurarDiagrama;
        JButton botaoArtefatoExplicativo;
        JButton botaoAjudaTexto;
        JButton botaoAjudaVergnaud;
        JButton botaoAjudaComplementar;
        JPopupMenu menuAjudaContextualAtivo;
        ScaffoldingAjudaContextual.Area areaAjudaContextualAtiva;
        javax.swing.Timer timerOcultarTipConclusao;

        final String URL_GERARD_VERGNAUD = "https://pt.wikipedia.org/wiki/G%C3%A9rard_Vergnaud";
        final String URL_VIDEO_GERARD_VERGNAUD = "https://www.youtube.com/watch?v=pU7um4GX5XQ";
        final String URL_VERGNAUD_BRASIL = "https://vergnaudbrasil.com/";
        final String URL_COMUNIDADE_GERARD = "https://github.com/anaemilia/Gerard/discussions";
        final String URL_SITE_GERARD = "https://anaemilia.github.io/Gerard/";

        ConstrutorResultadoCurado construtorResultadoCurado = new ConstrutorResultadoCurado();
        MaterializadorEnunciadoCurado materializadorEnunciadoCurado =
                new MaterializadorEnunciadoCurado();
        ResultadoInterpretacao resultadoInterpretacao;
        boolean textoProblemaEhMensagemSistema = false;
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        ScaffoldingNumeroRelativo scaffoldingNumeroRelativo = new ScaffoldingNumeroRelativo();
        ScaffoldingProximidade scaffoldingProximidade = new ScaffoldingProximidade();
        ScaffoldingQuestionamento scaffoldingQuestionamento = new ScaffoldingQuestionamento();
        // Fluxo de tentativas rejeitadas (REFERENCE.md §4.8;
        // TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md): contagem,
        // action_id por submissão e rejection_sequence_id por sequência
        // (PapelQuantitativo.registrarTentativa), nunca armazenamento de
        // valor real — isso continua exclusivamente com
        // estadoSemanticoCompartilhado. Recriado sempre que o papel da
        // incógnita atual muda — ver garantirTentativasIncognitaAtual.
        // Segunda referência desta classe a gerard.dominio.campoaditivo,
        // além de OrigemAcao (ver javadoc de registrarLogPorOrigem, que
        // precisa ser atualizado para não dizer mais "único ponto").
        private gerard.dominio.campoaditivo.PapelQuantitativo tentativasIncognitaAtual;
        private String papelDaTentativaAtual;
        // O Modelador escreve o Modelo do Usuário e publica regras; a sessão
        // congela uma fotografia no login e entrega apenas a projeção pedida
        // por cada proprietário semântico. O diálogo de usuário compartilha o
        // mesmo repositório, não a fotografia mutável.
        final gerard.agente.modelousuario.RepositorioModeloUsuario repositorioModeloUsuario =
                new gerard.agente.modelousuario.RepositorioModeloUsuario();
        final RepositorioRegrasAdaptativasPublicadas repositorioRegrasPublicadas =
                new RepositorioRegrasAdaptativasPublicadas();
        final gerard.adaptacao.sessao.SessaoAdaptativaUsuario sessaoAdaptativaUsuario =
                new gerard.adaptacao.sessao.SessaoAdaptativaUsuario(
                        repositorioModeloUsuario, repositorioRegrasPublicadas);
        final ProjetorContextoAdaptativoIncognita projetorContextoIncognita =
                new ProjetorContextoAdaptativoIncognita(sessaoAdaptativaUsuario);
        private ResultadoContextualizacaoIncognita contextoIncognitaAtual =
                projetorContextoIncognita.projetarPara(null);
        final AgenteModelador agenteModelador = new AgenteModelador(
                repositorioModeloUsuario, repositorioRegrasPublicadas);
        final ConectorVereditoModelador conectorVereditoModelador = new ConectorVereditoModelador(agenteModelador);
        // Unidade de análise A-B-C-D para os registros de pesquisa.
        gerard.pesquisador.analiseunidade.AnalysisUnitAuditService analysisUnitAuditService;
        ScaffoldingFeedbackMultissensorialErro scaffoldingFeedbackMultissensorialErro = new ScaffoldingFeedbackMultissensorialErro();
        ControladorAnotacaoTemporaria controladorAnotacaoTemporaria = new ControladorAnotacaoTemporaria();
        CatalogoPapeisSemanticosAditivos catalogoPapeisSemanticos = new CatalogoPapeisSemanticosAditivos();
        PoliticaValoresAditivos politicaValoresAditivos = new PoliticaValoresAditivos(catalogoPapeisSemanticos);
        ScaffoldingAjudaContextual scaffoldingAjudaContextual = new ScaffoldingAjudaContextual();
        PoliticaRestauracaoValorRelativo politicaRestauracaoValorRelativo =
                new PoliticaRestauracaoValorRelativo();
        /**
         * Item 4 do levantamento de pendências (2026-08-11): material
         * concreto próprio de TRANSFORMACAO_RELACAO/COMPOSICAO_RELACOES —
         * um eixo dos inteiros por papel, todos visíveis e manipuláveis ao
         * mesmo tempo, ativados pelo mesmo gatilho de todo outro material
         * concreto do app (deveExibirDiagramaComplementar).
         */
        final PaineisEixosRelacoes paineisEixosRelacoes = new PaineisEixosRelacoes();
        final AdaptadorInteracaoPaineisEixosRelacoes
                adaptadorInteracaoPaineisEixosRelacoes =
                new AdaptadorInteracaoPaineisEixosRelacoes(
                        paineisEixosRelacoes,
                        new FonteGeometriaInteracaoPaineisEixosRelacoes() {
                            @Override
                            public int obterLarguraTela() {
                                return TelaGerard.this.getWidth();
                            }

                            @Override
                            public int obterAlturaTela() {
                                return TelaGerard.this.getHeight();
                            }

                            @Override
                            public Rectangle obterAreaDiagrama() {
                                return obterAreaVisivelDiagramasVergnaud();
                            }
                        });
        /**
         * Item 22 (2026-08-18): seletor soma/subtração mostrado ao aluno
         * perto da seta do diagrama, nas 3 categorias de Relações — parte
         * avaliada da resposta, não decorativo. Ver Javadoc de
         * SeletorOperacaoRelacaoAluno.
         */
        final SeletorOperacaoRelacaoAluno seletorOperacaoRelacaoAluno = new SeletorOperacaoRelacaoAluno();
        /**
         * Item 30 (2026-08-23): segunda operação, exclusiva de Composição de
         * Transformações — estado_inicial [op] transformação_resultante =
         * estado_final. Instância independente da acima (que avalia
         * transformação_1 [op] transformação_2); ver Javadoc de
         * SeletorOperacaoRelacaoAluno.TipoOperacaoSeletor.
         */
        final SeletorOperacaoRelacaoAluno seletorOperacaoEstadoTransformacaoAluno = new SeletorOperacaoRelacaoAluno();
        final FornecedorCursoresPickup fornecedorCursoresPickup = new FornecedorCursoresPickupSwing();
        final RenderizadorPickup renderizadorPickup = new RenderizadorPickupElevado();
        final ControladorArrasteElastico controladorArrasteElastico =
                new ControladorArrasteMolaMomento();
        final SessaoArrasteTextoParaDiagrama sessaoArrasteTextoParaDiagrama =
                new SessaoArrasteTextoParaDiagrama();
        final ScaffoldingFeedbackProxyPosicionamento scaffoldingFeedbackProxyPosicionamento =
                new ScaffoldingFeedbackProxyPosicionamento(
                        scaffoldingFeedbackMultissensorialErro,
                        sessaoArrasteTextoParaDiagrama);
        final PoliticaElementoMatematicoTexto politicaElementoMatematicoTexto =
                new PoliticaElementoMatematicoTexto();
        final ResolvedorPickupElementoMatematicoTexto resolvedorPickupElementoMatematicoTexto =
                new ResolvedorPickupElementoMatematicoTexto(politicaElementoMatematicoTexto);
        final PoliticaUnicidadeElementoMatematicoTexto politicaUnicidadeElementoMatematicoTexto =
                new PoliticaUnicidadeElementoMatematicoTexto();
        final PoliticaGestoEstrutural politicaGestoEstrutural =
                new PoliticaGestoEstrutural();
        final ControladorConclusaoModelagem controladorConclusaoModelagem =
                new ControladorConclusaoModelagem();
        final SeletorPapeisConclusaoModelagem seletorPapeisConclusaoModelagem =
                new SeletorPapeisConclusaoModelagem();
        final CatalogoObjetosLogAcaoInstrumental catalogoObjetosLogAcaoInstrumental =
                new CatalogoObjetosLogAcaoInstrumental();
        final PoliticaPreenchimentoIncognita politicaPreenchimentoIncognita =
                new PoliticaPreenchimentoIncognita();
        final AplicadorDestaqueConclusaoDiagrama aplicadorDestaqueConclusaoDiagrama =
                new AplicadorDestaqueConclusaoDiagrama();
        final SeloConclusaoModelagem seloConclusaoModelagem =
                new SeloConclusaoModelagem();
        final TipConclusaoModelagem tipConclusaoModelagem =
                new TipConclusaoModelagem();
        final SequenciadorFeedbackConclusao sequenciadorFeedbackConclusao =
                new SequenciadorFeedbackConclusao();
        final CalculadorAreaVisualDiagramaVergnaud calculadorAreaVisualDiagramaVergnaud =
                new CalculadorAreaVisualDiagramaVergnaud();
        final MarcadorOrigemArraste marcadorOrigemArraste =
                new MarcadorOrigemArrasteTracejado();
        LoggerInteracaoGerard loggerInteracaoGerard = LoggerInteracaoGerard.getInstancia();
        final RegistradorEventosAjudaLogGerard registradorEventosAjuda =
                new RegistradorEventosAjudaLogGerard(loggerInteracaoGerard);
        final MaterializadorDecisaoAjudaSwing materializadorDecisaoAjuda =
                new MaterializadorDecisaoAjudaSwing(
                        this, localizacao, new Runnable() {
                            public void run() {
                                revalidate();
                                repaint();
                            }
                        });
        final ExecutorAjudaIncognita executorAjudaIncognita =
                new ExecutorAjudaIncognita(
                        materializadorDecisaoAjuda, registradorEventosAjuda);
        final PublicadorGestoInteracao publicadorGestosInteracao =
                LoggerGestosInteracaoGerard.paraSessao(loggerInteracaoGerard);
        ControladorContextoSituacao controladorContextoSituacao = new ControladorContextoSituacao(loggerInteracaoGerard);
        ControladorEstadoAtividade controladorEstadoAtividade = new ControladorEstadoAtividade();
        FachadaCarregamentoAtividade fachadaCarregamentoAtividade =
                new FachadaCarregamentoAtividade(repositorioSituacoesAditivas, catalogoDefinicoesAditivas, construtorResultadoCurado);
        TelaCuradoriaSituacoes telaCuradoriaSituacoes;
        // Fixado em atração magnética (decisão da usuária em 2026-07-28) —
        // era o único ponto de reatribuição além deste default (o botão
        // "Teste: ..." que permitia trocar o estilo foi removido junto).
        EstiloInteracao modoFeedbackTeste = EstiloInteracao.SNAP_TO_TARGET;

        // Paleta única em gerard.ui.UITemaGerard — mudar o tema é editar só aquele
        // arquivo. Os nomes locais são mantidos para não alterar cada uso abaixo.
        final Color COR_FUNDO = gerard.ui.UITemaGerard.COR_FUNDO_CONTEUDO;
        final Color COR_SUPERFICIE = gerard.ui.UITemaGerard.COR_SUPERFICIE;
        final Color COR_SUPERFICIE_SUAVE = gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE;
        final Color COR_PRIMARIA = gerard.ui.UITemaGerard.COR_PRIMARIA;
        final Color COR_PRIMARIA_ESCURA = gerard.ui.UITemaGerard.COR_PRIMARIA_ESCURA;
        final Color COR_TEXTO = gerard.ui.UITemaGerard.COR_TEXTO;
        final Color COR_TEXTO_SECUNDARIO = gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO;
        final Color COR_BORDA = gerard.ui.UITemaGerard.COR_BORDA;
        final Color COR_DESTAQUE = gerard.ui.UITemaGerard.COR_DESTAQUE;
        final Color COR_AVISO = new Color(224, 160, 58);               // #E0A03A (sinal de revisão, mantido)
        final Color COR_BORDA_BOTAO = gerard.ui.UITemaGerard.COR_BORDA;
        final Color COR_MARCADOR_NUMERO = gerard.ui.UITemaGerard.COR_SUPERFICIE;
        final Color COR_BORDA_MARCADOR = gerard.ui.UITemaGerard.COR_BORDA;
        final Color COR_QUESTIONAMENTO = gerard.ui.UITemaGerard.COR_DESTAQUE;
        final Color COR_SUCESSO = gerard.ui.UITemaGerard.COR_SUCESSO;
        final Color COR_SUCESSO_FUNDO = gerard.ui.UITemaGerard.COR_SUCESSO_FUNDO;
        final Color COR_SUCESSO_TEXTO = gerard.ui.UITemaGerard.COR_SUCESSO_TEXTO;
        final Color COR_ERRO = gerard.ui.UITemaGerard.COR_ERRO;
        final Color COR_ERRO_FUNDO = gerard.ui.UITemaGerard.COR_ERRO_FUNDO;
        final Color COR_ERRO_TEXTO = gerard.ui.UITemaGerard.COR_ERRO_TEXTO;

        ArrayList<MarcadorTexto> marcadoresFixosTexto = new ArrayList<MarcadorTexto>();
        ArrayList<ElementoTextoMovel> elementosTexto = new ArrayList<ElementoTextoMovel>();
        ArrayList<ItemTextoArrastavel> itensArrastaveis = new ArrayList<ItemTextoArrastavel>();

        ArrayList<CirculoVenn> circulosVenn = new ArrayList<CirculoVenn>();
        ArrayList<QuadradinhoVenn> quadradinhosVenn = new ArrayList<QuadradinhoVenn>();
        final ControleAdicionarQuadradinhoVenn controleAdicionarQuadradinhoVenn =
                new ControleAdicionarQuadradinhoVenn();
        final ControleRemoverQuadradinhoVenn controleRemoverQuadradinhoVenn =
                new ControleRemoverQuadradinhoVenn();
        final ScaffoldingLimiteQuantidadeVenn scaffoldingLimiteQuantidadeVenn =
                new ScaffoldingLimiteQuantidadeVenn();
        final CondicaoHabilitacaoAdicaoUnidades condicaoEdicaoAposInicioVergnaud =
                new CondicaoDiagramaVergnaudNaoVazio(
                        new EstadoModelagemVergnaud() {
                            @Override
                            public boolean possuiConteudoSemantico() {
                                return diagramaVergnaudPossuiConteudoSemantico();
                            }
                        },
                        "ui.tooltip.venn.positionFirst");
        final PoliticaInteracaoRepresentacoes politicaInteracaoRepresentacoes =
                new PoliticaInteracaoRepresentacoes(
                        new EstadoPrimeiroPosicionamento() {
                            @Override
                            public boolean possuiConteudoSemanticoNoVergnaud() {
                                return diagramaVergnaudPossuiConteudoSemantico();
                            }
                        },
                        "ui.tooltip.representation.positionFirst");
        final FabricaMapeamentosPapeisComplementares fabricaMapeamentosPapeisComplementares =
                new FabricaMapeamentosPapeisComplementares();
        CirculoVenn agrupamentoAdicionarQuadradinhoFocado = null;
        CirculoVenn agrupamentoRemoverQuadradinhoFocado = null;
        final OperacoesUnidadesVenn operacoesUnidadesVenn = new OperacoesUnidadesVenn() {
            public int contarUnidades(CirculoVenn agrupamento) {
                return contarQuadradinhosNoAgrupamento(agrupamento);
            }

            public Integer obterLimiteSemantico(CirculoVenn agrupamento) {
                int indice = circulosVenn.indexOf(agrupamento);
                if (indice < 0) {
                    return null;
                }
                Integer limite = obterLimiteSemanticoCuradoDoAgrupamento(indice);
                if (ehProcessoTransformacaoMedidas()) {
                    limite = politicaSinalTransformacaoComplementar
                            .normalizarLimiteParaUnidades(
                                    limite,
                                    obterIndiceSemanticoDoAgrupamento(indice));
                }
                return limite;
            }

            public boolean podeAlterarQuantidade(CirculoVenn agrupamento, int variacao) {
                return podeAlterarQuantidadeNoEstadoAtual(agrupamento, variacao);
            }

            public void adicionarUnidade(CirculoVenn agrupamento) {
                adicionarQuadradinhoAoAgrupamentoInterno(agrupamento);
            }

            public void removerUnidade(CirculoVenn agrupamento) {
                removerQuadradinhoDoAgrupamentoInterno(agrupamento);
            }
        };
        CirculoVenn agrupamentoLimiteQuantidadeQuestionado = null;
        boolean mostrarLimiteQuantidadeQuestionado = false;
        String textoLimiteQuantidadeQuestionado = "";
        /**
         * Aviso persistente (2026-08-18) de sinal divergente do curado no
         * número relativo — mesma família do aviso de limite de quantidade
         * acima, mas independente dele: "deixe na tela até que seja
         * corrigido" (a usuária achou o tooltip anterior, de 2600ms,
         * sumindo rápido demais). itemSinalDivergentePersistente é usado
         * quando o menu foi aberto a partir de um item já solto no
         * diagrama; elementoSinalDivergentePersistente, quando foi aberto a
         * partir do próprio círculo/retângulo (sem item associado) — nunca
         * os dois ao mesmo tempo.
         */
        ItemTextoArrastavel itemSinalDivergentePersistente = null;
        ElementoVergnaud elementoSinalDivergentePersistente = null;
        boolean mostrarSinalDivergentePersistente = false;
        String textoSinalDivergentePersistente = "";
        ArrayList<QuadradinhoVenn> quadradinhosCorrespondentesComparacao = new ArrayList<QuadradinhoVenn>();
        ArrayList<ElementoVergnaud> elementosVergnaud = new ArrayList<ElementoVergnaud>();
        ArrayList<ConectorVergnaud> conectoresVergnaud = new ArrayList<ConectorVergnaud>();
         final EstadoSemanticoCompartilhado estadoSemanticoCompartilhado = new EstadoSemanticoCompartilhado();
        final ResolvedorRelacoesEstruturaisAditivas resolvedorRelacoesEstruturais =
                new ResolvedorRelacoesEstruturaisAditivas();
         final ResolvedorValorEsperadoIncognita resolvedorValorEsperadoIncognita =
                 new ResolvedorValorEsperadoIncognita();
         final ServicoAvaliacaoAcaoIncognita servicoAvaliacaoAcaoIncognita =
                 new ServicoAvaliacaoAcaoIncognita();
        final ProjetorValorPapelDiagramaComplementar projetorValorPapelDiagramaComplementar =
                new ProjetorValorPapelDiagramaComplementar();
        final ProjetorValoresComparacaoComplementar projetorValoresComparacaoComplementar =
                new ProjetorValoresComparacaoComplementar();
        final SincronizadorElementosSemanticosTexto sincronizadorElementosSemanticosTexto =
                new SincronizadorElementosSemanticosTextoAditivo();
        final CoordenadorSincronizacaoRepresentacoes coordenadorSincronizacaoRepresentacoes =
                new CoordenadorSincronizacaoRepresentacoes();
        final SeletorIndicesEstadoCompartilhado seletorIndicesEstadoCompartilhado =
                new SeletorIndicesEstadoCompartilhado();
        final PlanejadorAplicacaoEstadoVergnaud planejadorAplicacaoEstadoVergnaud =
                new PlanejadorAplicacaoEstadoVergnaud();
        final ApresentadorItemVergnaud apresentadorItemVergnaud =
                new ApresentadorItemVergnaud();
        int[] indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};

        final HandlerInteracaoItemTextoArrastavel handlerItemTextoArrastavel =
                new HandlerInteracaoItemTextoArrastavel();
        final HandlerInteracaoElementoTextoMovel handlerElementoTextoMovel =
                new HandlerInteracaoElementoTextoMovel();
        final HandlerInteracaoArrasteIncremental<AdaptadorMovimentoConectorVergnaud>
                handlerConectorVergnaud =
                new HandlerInteracaoArrasteIncremental<AdaptadorMovimentoConectorVergnaud>();
        final HandlerInteracaoQuadradinhoVenn handlerQuadradinhoVenn =
                new HandlerInteracaoQuadradinhoVenn();
        final HandlerInteracaoPaineisEixosRelacoes
                handlerPaineisEixosRelacoes =
                new HandlerInteracaoPaineisEixosRelacoes();
        final GeometriaAreaEnunciado geometriaAreaEnunciado;
        // Posicao do item no instante do pickup (rodada 4, 2026-07-31) —
        // ver mouseReleased: um release na MESMA posicao do pickup nao e
        // um arrasto real (é um clique parado — inclusive cada clique de
        // um duplo-clique sobre o item, usado pra abrir o dialogo de
        // edicao), so o handler move as coordenadas do item. Distingue
        // "soltura real do usuario" de "clique sem deslocamento".
        ItemTextoArrastavel itemFocado = null;
        ElementoTextoMovel elementoTextoFocado = null;
        boolean layoutTextoInicializado = false;
        int larguraUltimoLayoutTexto = -1;
        QuadradinhoVenn quadradinhoVennFocado = null;
        ElementoVergnaud alvoRealcadoPorProximidade = null;
        final int DISTANCIA_REALCE_ALVO = 48;

        boolean arrastandoControleComparacao = false;
        double proporcaoControleComparacao = -1.0;
        int ultimoValorInteiroControleComparacao = -1;
        // Throttling do log CONSISTENCIA_AUTOMATICA durante o arraste
        // contínuo do controle das barras de Comparação — decisão da
        // usuária, 2026-08-16 (item 3 do levantamento de pendências de
        // 2026-08-11, ver TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md).
        // Guarda só o snapshot mais recente enquanto arrastandoControleComparacao
        // é verdadeiro; o log em si só é escrito quando o gesto termina (ver
        // registrarLogConsistenciaAutomaticaSeHouve/
        // flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao). Não
        // afeta a propagação de estado, que continua acontecendo a cada
        // passo do arrasto.
        EstadoSemanticoCompartilhado.Snapshot logConsistenciaAutomaticaPendenteArrasteComparacao;
        EstadoSemanticoCompartilhado.Origem origemLogConsistenciaAutomaticaPendenteArrasteComparacao;

        boolean rastreamentoCaminhoAtivo = false;
        int rastreamentoInicioX;
        int rastreamentoInicioY;
        int rastreamentoUltimoX;
        int rastreamentoUltimoY;
        String rastreamentoUltimaOrientacao = "";
        int rastreamentoMudancasOrientacao = 0;
        int rastreamentoAmostras = 0;
        String rastreamentoObjeto = "";
        String rastreamentoArtefato = "";
        boolean rastreamentoEhTexto = false;

        double faseAnimacao = 0.0;

        boolean mostrarAnotacaoMouseOver = false;
        String textoAnotacaoMouseOver = "";
        int mouseOverX = 0;
        int mouseOverY = 0;
        // Por padrão, desenharAnotacaoMouseOver tenta posicionar a caixa
        // ACIMA da âncora (mouseOverX/Y), só caindo para abaixo se isso
        // ficaria muito perto do topo da tela (y<50). Essa heurística
        // pressupõe âncoras no meio/embaixo da tela (elementos do diagrama),
        // não uma fileira de botões Swing logo abaixo do cabeçalho: ali,
        // "acima da âncora" cai por cima dos próprios botões da faixa de
        // atalhos, que são componentes Swing pintados por cima do canvas
        // (a caixa é desenhada dentro de paintComponent, então fica atrás
        // de qualquer JButton que ela geometricamente cruze). Esta flag
        // força a colocação abaixo da âncora incondicionalmente — usada só
        // pelo hover dos ícones de atalho de categoria (ver
        // criarBotaoAtalhoCategoria), para garantir que a caixa apareça
        // sempre abaixo da faixa inteira, nunca atrás de um botão vizinho.
        boolean forcarAnotacaoMouseOverAbaixo = false;
        boolean mostrarQuestionamentoPersistente = false;
        String textoQuestionamentoPersistente = "";
        ItemTextoArrastavel itemQuestionadoPersistente = null;
        // AG_AE (automatização de passos, item 7 do levantamento de
        // 2026-08-07) — dica de posicionamento sob demanda, um papel por
        // vez: mesmo padrão de anotação persistente de
        // itemQuestionadoPersistente acima, mas ancorada num
        // ElementoVergnaud (o alvo correto), não num ItemTextoArrastavel.
        boolean mostrarDicaPosicionamentoPersistente = false;
        String papelDicaPosicionamentoAtual = null;
        ElementoVergnaud elementoDicaPosicionamentoPersistente = null;
        // Correlação ação:evento (REFERENCE.md §4.8, 1:N) e
        // "qual o próximo papel resolvido/não resolvido" são regras
        // semânticas, não de interface (gerard-domain-model-first,
        // gerard-knowledge-locality-principle) — vivem em
        // AvaliadorConclusaoModelagem (já existente, reaproveitado) e
        // ScaffoldingAutomatizacaoPassos (política pedagógica dedicada),
        // não como um Map solto aqui.
        final AvaliadorConclusaoModelagem avaliadorConclusaoModelagem =
                new AvaliadorConclusaoModelagem();
        final ScaffoldingAutomatizacaoPassos scaffoldingAutomatizacaoPassos =
                new ScaffoldingAutomatizacaoPassos();
        JButton botaoVerDicaPosicionamento;
        boolean sincronizacaoEstadoFinalHabilitada = false;
        ItemTextoArrastavel itemIncognitaEstadoFinal = null;
        ElementoVergnaud elementoEstadoFinalSincronizado = null;

        public TelaGerard() {
            geometriaAreaEnunciado = new GeometriaAreaEnunciado(
                    ALTURA_PAINEL_ATALHOS_CATEGORIA);
            setBackground(COR_FUNDO);
            setFocusable(true);
            setLayout(null);
            addMouseListener(this);
            addMouseMotionListener(this);
            addKeyListener(this);
            addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent evento) {
                    reposicionarDiagramaVergnaudParaAreaAtual();
                    if (faixaAtividadeModelador != null) {
                        faixaAtividadeModelador.reposicionar(getWidth(), getHeight());
                    }
                    reposicionarPainelAtalhoCategoria();
                    repaint();
                }
            });

            criarPainelAtalhoCategoria();
            criarIndicadorAgenteModelador();
            criarFaixaAtividadeModelador();
            criarBotaoRestaurar();
            criarBotaoCorrigirCuradoria();
            criarBotaoIdiomaSituacao();
            criarBotaoArtefatoExplicativo();
            criarBotaoRestaurarDiagrama();
            criarBotoesAjudaContextual();
            criarMenuPrincipal();
            criarBotoesCabecalhoEmbutidos();
            configurarFeedbackConclusaoModelagem();
            inicializarTelaSemCategoria();

            Timer timer = new Timer(80, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    faseAnimacao += 0.045;
                    repaint();
                }
            });
            timer.start();
        }

        private void atualizarContextoAdaptativoIncognitaAtual() {
            SituacaoProblemaAditiva situacaoContextualizavel =
                    categoriaSelecionadaParaAtividade ? situacaoProblemaAtual : null;
            if (situacaoContextualizavel == null) {
                contextoIncognitaAtual = projetorContextoIncognita.projetarPara(null);
                return;
            }
            garantirTentativasIncognitaAtual(obterPapelIncognitaAtual());
            contextoIncognitaAtual = projetorContextoIncognita.projetarPara(
                    situacaoContextualizavel, tentativasIncognitaAtual);
        }

        private void configurarFeedbackConclusaoModelagem() {
            tipConclusaoModelagem.definirOuvinte(new TipConclusaoModelagem.OuvinteEscolha() {
                public void aoEscolherSim() {
                    sequenciadorFeedbackConclusao.encerrar();
                    registrarLogUsuario(
                            "Concluir modelagem e avançar", "C",
                            "Diagrama de Vergnaud", "Tip de conclusão",
                            "Escolher a continuidade da atividade", "OBJ_CONCLUSAO",
                            "A modelagem concluída permite iniciar uma nova situação-problema.",
                            "CONCLUSAO_MODELAGEM", "escolha=sim");
                    if (itemNovaSituacao != null && itemNovaSituacao.isEnabled()) {
                        itemNovaSituacao.doClick();
                    }
                }

                public void aoEscolherNao() {
                    sequenciadorFeedbackConclusao.encerrar();
                    registrarLogUsuario(
                            "Concluir modelagem e permanecer", "C",
                            "Diagrama de Vergnaud", "Tip de conclusão",
                            "Escolher a continuidade da atividade", "OBJ_CONCLUSAO",
                            "A escolha Não mantém a situação atual e o destaque de conclusão.",
                            "CONCLUSAO_MODELAGEM", "escolha=nao");
                    repaint();
                }
            });
            sequenciadorFeedbackConclusao.definirOuvinte(
                    new SequenciadorFeedbackConclusao.Ouvinte() {
                public void aoMostrarConfirmacaoVisual() {
                    // Aguarda a pausa (ver SequenciadorFeedbackConclusao) sem
                    // alterar nada visualmente ainda — o diagrama só passa a
                    // azul depois do atraso, em aoSolicitarDecisao().
                }

                public void aoSolicitarDecisao() {
                    if (!controladorConclusaoModelagem.isConcluida()) {
                        sequenciadorFeedbackConclusao.cancelar();
                        return;
                    }
                    controladorConclusaoModelagem.registrarTipApresentado();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            aplicadorDestaqueConclusaoDiagrama.aplicar(
                                    true, elementosVergnaud, conectoresVergnaud, itensArrastaveis,
                                    quadradinhosVenn);
                            atualizarTextosTipConclusaoModelagem();
                            Rectangle areaPermitida = obterAreaVisivelDiagramasVergnaud();
                            Rectangle areaDiagrama = obterAreaVisualDiagramaVergnaudAtual();
                            tipConclusaoModelagem.ocultar();
                            setComponentZOrder(seloConclusaoModelagem, 0);
                            seloConclusaoModelagem.mostrarAoLadoDireitoDoDiagrama(
                                    areaDiagrama, areaPermitida, getWidth(), getHeight());
                            registrarFeedbackExibido("AG_EMS",
                                    gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding.VISUAL,
                                    "selo de conclusão da modelagem, após o atraso padrão");
                            repaint();
                        }
                    });
                }

                public void aoCancelar() {
                    aplicadorDestaqueConclusaoDiagrama.aplicar(
                            false, elementosVergnaud, conectoresVergnaud, itensArrastaveis,
                            quadradinhosVenn);
                    seloConclusaoModelagem.ocultar();
                    tipConclusaoModelagem.ocultar();
                    repaint();
                }
            });
            seloConclusaoModelagem.definirAcaoClique(new Runnable() {
                public void run() {
                    sequenciadorFeedbackConclusao.encerrar();
                    registrarLogUsuario(
                            "Concluir modelagem e avançar", "C",
                            "Diagrama de Vergnaud", "Próxima tarefa",
                            "Escolher a continuidade da atividade", "OBJ_CONCLUSAO",
                            "A modelagem concluída permite iniciar uma nova situação-problema.",
                            "CONCLUSAO_MODELAGEM", "escolha=proxima_tarefa");
                    if (itemNovaSituacao != null && itemNovaSituacao.isEnabled()) {
                        itemNovaSituacao.doClick();
                    }
                }
            });
            seloConclusaoModelagem.definirAcaoHover(new Runnable() {
                public void run() {
                    mostrarTipConclusaoModelagem();
                }
            });
            // Ao retirar o mouse do tip (sem escolher Sim/Não), ele some e
            // volta a mostrar só o link "Próxima tarefa" (seloConclusaoModelagem
            // nunca é ocultado, então reaparece sozinho ao ocultar o tip por
            // cima dele). Pequeno atraso (ver agendarOcultacaoTipConclusao)
            // evita fechar o tip ao passar o mouse entre a mensagem e os
            // radiobuttons Sim/Não internos.
            tipConclusaoModelagem.addMouseListener(new MouseAdapter() {
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoTipConclusao();
                }
                public void mouseEntered(MouseEvent e) {
                    cancelarOcultacaoTipConclusao();
                }
            });
            atualizarTextosTipConclusaoModelagem();
            add(seloConclusaoModelagem);
            add(tipConclusaoModelagem);
            setComponentZOrder(seloConclusaoModelagem, 0);
            setComponentZOrder(tipConclusaoModelagem, 0);
        }

        /**
         * Exibe o tip "Podemos passar para a próxima tarefa?" com as opções
         * Sim/Não ao passar o mouse sobre o link "Próxima tarefa", em vez de
         * avançar direto no clique sem confirmação.
         */
        private void mostrarTipConclusaoModelagem() {
            if (!controladorConclusaoModelagem.isConcluida() || tipConclusaoModelagem.isVisible()) {
                return;
            }
            setComponentZOrder(tipConclusaoModelagem, 0);
            tipConclusaoModelagem.mostrarAcimaDoElemento(
                    seloConclusaoModelagem.getBounds(), getWidth(), getHeight());
            repaint();
        }

        /** Área visual atual ocupada pelo diagrama de Vergnaud concluído. */
        private Rectangle obterAreaVisualDiagramaVergnaudAtual() {
            return calculadorAreaVisualDiagramaVergnaud.calcular(
                    elementosVergnaud, conectoresVergnaud,
                    obterAreaConteudoDiagramaVergnaud());
        }

        private void atualizarTextosTipConclusaoModelagem() {
            seloConclusaoModelagem.atualizarTexto(
                    localizacao.texto("ui.completion.next"));
            tipConclusaoModelagem.atualizarTextos(
                    localizacao.texto("ui.completion.congratulations"),
                    localizacao.texto("ui.completion.yes"),
                    localizacao.texto("ui.completion.no"));
        }


        private static final int LARGURA_ICONE_CATEGORIA = 92;
        private static final int ALTURA_ICONE_CATEGORIA = 84;
        private static final int LARGURA_SEPARADOR_ATALHO = 32;
        private static final int LARGURA_BOTAO_PROXIMO_PASSO = 54;
        private static final int ALTURA_BOTAO_PROXIMO_PASSO = 54;
        /** Botão de sorteio (Medidas/Relações), colado ao lado de cada grupo — mesmo tamanho dos botões de ícone do cabeçalho (criarBotaoIconeCabecalho). */
        private static final int LARGURA_BOTAO_SORTEIO = 34;
        /** Espaço entre o último ícone de um grupo (ou o botão de sorteio) e o próximo elemento — fora da caixa delimitadora do grupo (areaGrupoMedidas/Relacoes), pra não parecer uma 4ª opção de resposta do quiz. */
        private static final int GAP_BOTAO_SORTEIO = 16;

        /**
         * Faixa de atalhos de categoria, entre o cabeçalho e a área do
         * enunciado — painel adicional, não substitui o menu "Categoria" já
         * existente. Um separador vertical distingue as 3 primeiras
         * categorias (medidas — Composição/Transformação/Comparação) das 3
         * seguintes (Composição de transformações/Transformação de
         * relação/Composição de relações), decisão da usuária em
         * 2026-07-27. "Qual o próximo passo?" é um ícone "?" desabilitado
         * (decisão da usuária em 2026-07-27: sem rótulo de texto, mesmo
         * princípio de comunicabilidade situada já aplicado aos 3 botões
         * "?" de ajuda contextual — ver gerard-scaffolding-interacao; o tooltip
         * carrega a própria expressão "Qual o próximo passo?", já que é o
         * scaffolding de automatização de passos, ainda não desenhado — ver
         * gerard-scaffolding-interacao). Sem setas de voltar/avançar
         * (decisão da usuária em 2026-07-25: removidas para dar mais espaço
         * aos ícones — não existe histórico de situações no Gérard hoje de
         * qualquer forma, só "Sortear"). Os ícones de categoria chamam
         * exatamente selecionarCategoria(tipo) — o mesmo método usado pelo
         * menu "Categoria" — para garantir comportamento idêntico, não uma
         * segunda implementação.
         *
         * O grupo inteiro é centralizado horizontalmente na tela (não fixo
         * à esquerda) — ver reposicionarPainelAtalhoCategoria, chamado aqui
         * uma vez e depois a cada resize da janela, igual ao padrão já
         * usado por faixaAtividadeModelador/botaoAjudaTexto.
         */
        private void criarPainelAtalhoCategoria() {
            botaoAtalhoComposicao = criarBotaoAtalhoCategoria(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, criarIconeCategoriaComposicao());
            add(botaoAtalhoComposicao);
            botaoAtalhoTransformacao = criarBotaoAtalhoCategoria(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, criarIconeCategoriaTransformacao());
            add(botaoAtalhoTransformacao);
            botaoAtalhoComparacao = criarBotaoAtalhoCategoria(TipoSituacaoAditiva.COMPARACAO_MEDIDAS, criarIconeCategoriaComparacao());
            add(botaoAtalhoComparacao);

            // Botão de sorteio do grupo Medidas, colado ao lado dos 3 ícones
            // acima (mas fora da caixa delimitadora deles — ver
            // areaGrupoMedidas — pra não parecer uma 4ª opção de resposta do
            // quiz de adivinhação). Movido para cá em 2026-08-07, a pedido
            // da usuária, de dentro do cabeçalho (criarBotoesCabecalhoEmbutidos)
            // — ver sortearSituacaoMedidas.
            botaoFerramentaSortearMedidas = criarBotaoIconeCabecalho(criarIconeFerramentaSortear());
            botaoFerramentaSortearMedidas.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sortearSituacaoMedidas();
                }
            });
            botaoFerramentaSortearMedidas.setToolTipText(localizacao.texto("ui.tooltip.random.measures"));
            add(botaoFerramentaSortearMedidas);

            separadorAtalhoCategoria = criarSeparadorAtalhoCategoria();
            add(separadorAtalhoCategoria);

            // Categoria completa (20 situações curadas,
            // RenderizadorComposicaoTransformacoes), reincluída no sorteio/
            // quiz em 2026-08-07 (ver CATEGORIAS_SORTEIO_LIVRE) — o atalho
            // volta a usar a descrição padrão do diagrama (mesmo padrão dos
            // 3 ícones de Medidas), no lugar do aviso "temporariamente
            // desabilitada" que fazia sentido só enquanto o sorteio era
            // restrito ao grupo Medidas.
            botaoAtalhoComposicaoTransformacoes = criarBotaoAtalhoCategoria(
                    TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, criarIconeCategoriaComposicaoTransformacoes());
            add(botaoAtalhoComposicaoTransformacoes);
            // Mesma situação da anterior: categoria completa (8 situações
            // curadas, renderizador próprio — RenderizadorTransformacaoRelacao).
            botaoAtalhoTransformacaoRelacao = criarBotaoAtalhoCategoria(
                    TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, criarIconeCategoriaTransformacaoRelacao());
            add(botaoAtalhoTransformacaoRelacao);
            // Irmã de TRANSFORMACAO_RELACAO no mesmo grupo "Relações"
            // (completa em código — RenderizadorComposicaoRelacoes, 8
            // situações curadas).
            botaoAtalhoComposicaoRelacoes = criarBotaoAtalhoCategoria(
                    TipoSituacaoAditiva.COMPOSICAO_RELACOES, criarIconeCategoriaComposicaoRelacoes());
            add(botaoAtalhoComposicaoRelacoes);

            // Botão de sorteio do grupo Relações — ver o comentário análogo
            // acima de botaoFerramentaSortearMedidas.
            botaoFerramentaSortearRelacoes = criarBotaoIconeCabecalho(criarIconeFerramentaSortear());
            botaoFerramentaSortearRelacoes.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sortearSituacaoRelacoes();
                }
            });
            botaoFerramentaSortearRelacoes.setToolTipText(localizacao.texto("ui.tooltip.random.relations"));
            add(botaoFerramentaSortearRelacoes);

            botaoAtalhoProximoPasso = new JButton(criarIconeInterrogacaoAtalho());
            botaoAtalhoProximoPasso.setFocusable(false);
            botaoAtalhoProximoPasso.setEnabled(false);
            botaoAtalhoProximoPasso.setOpaque(false);
            botaoAtalhoProximoPasso.setContentAreaFilled(false);
            botaoAtalhoProximoPasso.setBorderPainted(false);
            // Sem o quadrado ao redor (decisão da usuária, 2026-07-28): só o
            // círculo com "?" desenhado pelo ícone fica visível. O tip de
            // anotação ao passar o mouse continua funcionando normalmente —
            // eventos de mouse ainda chegam a um JButton desabilitado, só a
            // ação/foco é que ficam bloqueados.
            botaoAtalhoProximoPasso.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarAnotacaoMouseOver = true;
                    forcarAnotacaoMouseOverAbaixo = true;
                    textoAnotacaoMouseOver = localizacao.texto("ui.hint.nextStep.tooltip");
                    mouseOverX = botaoAtalhoProximoPasso.getX();
                    mouseOverY = botaoAtalhoProximoPasso.getY() + botaoAtalhoProximoPasso.getHeight();
                    repaint();
                }
                public void mouseExited(MouseEvent e) {
                    mostrarAnotacaoMouseOver = false;
                    forcarAnotacaoMouseOverAbaixo = false;
                    repaint();
                }
            });
            add(botaoAtalhoProximoPasso);

            reposicionarPainelAtalhoCategoria();
        }

        /** Divisor vertical fino entre os dois grupos de categoria — cor estrutural neutra, sem significado próprio. */
        private JPanel criarSeparadorAtalhoCategoria() {
            JPanel separador = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
                    int x = getWidth() / 2;
                    g2.drawLine(x, 6, x, getHeight() - 6);
                    g2.dispose();
                }
            };
            separador.setOpaque(false);
            return separador;
        }

        /**
         * Ícone "?" para o atalho de "próximo passo" — mesmo estilo fino e
         * neutro dos glifos de categoria (prepararTracoIconeCategoria), não
         * o ícone escuro/preenchido de criarIconeInterrogacaoContextual (que
         * é para os botões de ajuda já habilitados e situados junto ao
         * conteúdo que explicam — este aqui é um placeholder desabilitado).
         */
        private Icon criarIconeInterrogacaoAtalho() {
            return new Icon() {
                public int getIconWidth() { return LARGURA_BOTAO_PROXIMO_PASSO; }
                public int getIconHeight() { return ALTURA_BOTAO_PROXIMO_PASSO; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        int diametro = 34;
                        int cx = x + LARGURA_BOTAO_PROXIMO_PASSO / 2 - diametro / 2;
                        int cy = y + ALTURA_BOTAO_PROXIMO_PASSO / 2 - diametro / 2;
                        g2.draw(new java.awt.geom.Ellipse2D.Float(cx, cy, diametro, diametro));
                        g2.setFont(new Font("Arial", Font.PLAIN, 16));
                        FontMetrics fm = g2.getFontMetrics();
                        String simbolo = "?";
                        int tx = x + LARGURA_BOTAO_PROXIMO_PASSO / 2 - fm.stringWidth(simbolo) / 2;
                        int ty = y + ALTURA_BOTAO_PROXIMO_PASSO / 2 + (fm.getAscent() - fm.getDescent()) / 2;
                        g2.drawString(simbolo, tx, ty);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /**
         * Centraliza horizontalmente o grupo (6 ícones + "próximo passo")
         * dentro da largura atual da tela. Chamado na criação (onde
         * getWidth() ainda pode ser 0, corrigido no primeiro resize real) e
         * a cada componentResized, junto com faixaAtividadeModelador.reposicionar.
         */
        private void reposicionarPainelAtalhoCategoria() {
            if (botaoAtalhoComposicao == null) {
                return;
            }
            int gapEntreIcones = 24;
            int gapAntesBotao = 32;
            int larguraTotal = LARGURA_ICONE_CATEGORIA * 6 + gapEntreIcones * 4
                    + LARGURA_SEPARADOR_ATALHO
                    + GAP_BOTAO_SORTEIO * 4 + LARGURA_BOTAO_SORTEIO * 2
                    + gapAntesBotao + LARGURA_BOTAO_PROXIMO_PASSO;
            int larguraTela = getWidth() > 0 ? getWidth() : LARGURA_BASE_TELA;
            int x = Math.max(18, (larguraTela - larguraTotal) / 2);
            int centroFaixa = 45 + ALTURA_PAINEL_ATALHOS_CATEGORIA / 2;

            botaoAtalhoComposicao.setBounds(x, centroFaixa - ALTURA_ICONE_CATEGORIA / 2, LARGURA_ICONE_CATEGORIA, ALTURA_ICONE_CATEGORIA);
            x += LARGURA_ICONE_CATEGORIA + gapEntreIcones;
            botaoAtalhoTransformacao.setBounds(x, centroFaixa - ALTURA_ICONE_CATEGORIA / 2, LARGURA_ICONE_CATEGORIA, ALTURA_ICONE_CATEGORIA);
            x += LARGURA_ICONE_CATEGORIA + gapEntreIcones;
            botaoAtalhoComparacao.setBounds(x, centroFaixa - ALTURA_ICONE_CATEGORIA / 2, LARGURA_ICONE_CATEGORIA, ALTURA_ICONE_CATEGORIA);
            x += LARGURA_ICONE_CATEGORIA;

            // Os dois botões de sorteio ficam colados um de cada lado do
            // separador central, em vez de um "no meio" (entre o grupo e o
            // separador) e o outro "na ponta" (sozinho depois do último
            // ícone, com um vão grande até o botão de próximo passo) — essa
            // segunda posição para Relações parecia solta/desalinhada
            // (relatado pela usuária, 2026-08-07). Espelhados assim, os dois
            // ficam visualmente simétricos em torno do separador, e nenhum
            // fica pendurado sozinho na borda do painel.
            x += GAP_BOTAO_SORTEIO;
            botaoFerramentaSortearMedidas.setBounds(x, centroFaixa - LARGURA_BOTAO_SORTEIO / 2, LARGURA_BOTAO_SORTEIO, LARGURA_BOTAO_SORTEIO);
            x += LARGURA_BOTAO_SORTEIO + GAP_BOTAO_SORTEIO;

            separadorAtalhoCategoria.setBounds(x, centroFaixa - ALTURA_ICONE_CATEGORIA / 2, LARGURA_SEPARADOR_ATALHO, ALTURA_ICONE_CATEGORIA);
            x += LARGURA_SEPARADOR_ATALHO;

            x += GAP_BOTAO_SORTEIO;
            botaoFerramentaSortearRelacoes.setBounds(x, centroFaixa - LARGURA_BOTAO_SORTEIO / 2, LARGURA_BOTAO_SORTEIO, LARGURA_BOTAO_SORTEIO);
            x += LARGURA_BOTAO_SORTEIO + GAP_BOTAO_SORTEIO;

            botaoAtalhoComposicaoTransformacoes.setBounds(x, centroFaixa - ALTURA_ICONE_CATEGORIA / 2, LARGURA_ICONE_CATEGORIA, ALTURA_ICONE_CATEGORIA);
            x += LARGURA_ICONE_CATEGORIA + gapEntreIcones;
            botaoAtalhoTransformacaoRelacao.setBounds(x, centroFaixa - ALTURA_ICONE_CATEGORIA / 2, LARGURA_ICONE_CATEGORIA, ALTURA_ICONE_CATEGORIA);
            x += LARGURA_ICONE_CATEGORIA + gapEntreIcones;
            botaoAtalhoComposicaoRelacoes.setBounds(x, centroFaixa - ALTURA_ICONE_CATEGORIA / 2, LARGURA_ICONE_CATEGORIA, ALTURA_ICONE_CATEGORIA);
            x += LARGURA_ICONE_CATEGORIA + gapAntesBotao;

            xCentroGrupoMedidas = (botaoAtalhoComposicao.getX() + botaoAtalhoComparacao.getX() + LARGURA_ICONE_CATEGORIA) / 2;
            xCentroGrupoRelacoes = (botaoAtalhoComposicaoTransformacoes.getX() + botaoAtalhoComposicaoRelacoes.getX() + LARGURA_ICONE_CATEGORIA) / 2;

            // Retângulo delimitador de cada grupo — padding menor em cima
            // (4px) pra não encostar no rótulo semi-negrito logo acima
            // (desenharRotulosGruposAtalhoCategoria), padding maior nos
            // outros lados (10px).
            int padLados = 10;
            int padTopo = 4;
            int padBaixo = 10;
            areaGrupoMedidas = new Rectangle(
                    botaoAtalhoComposicao.getX() - padLados,
                    botaoAtalhoComposicao.getY() - padTopo,
                    (botaoAtalhoComparacao.getX() + LARGURA_ICONE_CATEGORIA) - botaoAtalhoComposicao.getX() + padLados * 2,
                    ALTURA_ICONE_CATEGORIA + padTopo + padBaixo);
            areaGrupoRelacoes = new Rectangle(
                    botaoAtalhoComposicaoTransformacoes.getX() - padLados,
                    botaoAtalhoComposicaoTransformacoes.getY() - padTopo,
                    (botaoAtalhoComposicaoRelacoes.getX() + LARGURA_ICONE_CATEGORIA) - botaoAtalhoComposicaoTransformacoes.getX() + padLados * 2,
                    ALTURA_ICONE_CATEGORIA + padTopo + padBaixo);

            // Posição padrão (ao lado dos ícones de categoria) só é aplicada
            // de fato em reposicionarBotaoAtalhoProximoPasso — enquanto a
            // categoria não foi confirmada, o botão fica ao lado do texto em
            // vez de aqui (ver desenharTextoProblema/desenharTextoProblemaAdivinhacao).
            xPadraoBotaoAtalhoProximoPasso = x;
            yPadraoBotaoAtalhoProximoPasso = centroFaixa - ALTURA_BOTAO_PROXIMO_PASSO / 2;
        }

        /**
         * Posição do ícone "?" de próximo passo (decisão da usuária,
         * 2026-07-28): enquanto a categoria ainda não foi confirmada, fica ao
         * lado esquerdo do enunciado (mesma coluna dos botões contextuais do
         * texto, x=27) — chamando atenção pra tarefa pendente logo ao lado do
         * que o usuário está lendo. Assim que a categoria é confirmada, some
         * dali e volta a aparecer só na posição padrão, ao lado dos 6 ícones
         * de categoria (calculada em reposicionarPainelAtalhoCategoria).
         * Chamado a cada repintura do enunciado (desenharTextoProblema),
         * mesmo padrão já usado pelos outros reposicionarBotaoX.
         */
        private void reposicionarBotaoAtalhoProximoPasso() {
            if (botaoAtalhoProximoPasso == null) {
                return;
            }
            if (situacaoProblemaAtual == null) {
                botaoAtalhoProximoPasso.setVisible(false);
                return;
            }
            if (categoriaSelecionadaParaAtividade) {
                botaoAtalhoProximoPasso.setBounds(
                        xPadraoBotaoAtalhoProximoPasso, yPadraoBotaoAtalhoProximoPasso,
                        LARGURA_BOTAO_PROXIMO_PASSO, ALTURA_BOTAO_PROXIMO_PASSO);
            } else {
                int x = 27;
                int y = 63 + ALTURA_PAINEL_ATALHOS_CATEGORIA;
                botaoAtalhoProximoPasso.setBounds(x, y, LARGURA_BOTAO_PROXIMO_PASSO, ALTURA_BOTAO_PROXIMO_PASSO);
            }
            botaoAtalhoProximoPasso.setVisible(true);
        }

        /**
         * A chave de descrição rica ("diag.desc.<nome_enum_minúsculo>") é
         * derivada de tipo.name() em vez de recebida por parâmetro — bate
         * exatamente com as chaves já existentes em mensagens_pt.properties
         * para as 6 categorias que hoje têm atalho (confirmado uma a uma:
         * COMPOSICAO_MEDIDAS→diag.desc.composicao_medidas, etc.).
         */
        private JButton criarBotaoAtalhoCategoria(final TipoSituacaoAditiva tipo, Icon icone) {
            return criarBotaoAtalhoCategoria(tipo, icone, null);
        }

        /**
         * @param chaveAnotacaoPersonalizada se não-nula, substitui a descrição
         * padrão do diagrama ("diag.desc.*") no tip de mouse-over. Sem
         * chamador hoje (2026-08-07): usava
         * "ui.hint.categoryDisabled.tooltip" nos 3 ícones do grupo
         * "Relações" enquanto eles ficavam sempre desabilitados (sorteio
         * restrito a "Medidas", decisão de 2026-07-28, revertida — ver
         * CATEGORIAS_SORTEIO_LIVRE); mantido como mecanismo genérico caso
         * outro atalho precise de um aviso equivalente no futuro.
         */
        private JButton criarBotaoAtalhoCategoria(final TipoSituacaoAditiva tipo, Icon icone,
                final String chaveAnotacaoPersonalizada) {
            final JButton botao = new JButton(icone);
            botao.setFocusable(false);
            botao.setOpaque(true);
            botao.setBackground(COR_SUPERFICIE);
            botao.setBorder(BorderFactory.createLineBorder(COR_BORDA_BOTAO, 1));
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clicarAtalhoCategoria(tipo);
                }
            });
            botao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    botao.setBackground(COR_DESTAQUE);
                    mostrarAnotacaoMouseOver = true;
                    forcarAnotacaoMouseOverAbaixo = true;
                    textoAnotacaoMouseOver = localizacao.texto(chaveAnotacaoPersonalizada != null
                            ? chaveAnotacaoPersonalizada : "diag.desc." + tipo.name().toLowerCase());
                    mouseOverX = botao.getX();
                    mouseOverY = botao.getY() + botao.getHeight();
                    repaint();
                }
                public void mouseExited(MouseEvent e) {
                    botao.setBackground(COR_SUPERFICIE);
                    mostrarAnotacaoMouseOver = false;
                    forcarAnotacaoMouseOverAbaixo = false;
                    repaint();
                }
            });
            return botao;
        }

        private static final int CAIXA_ICONE_CATEGORIA = 16;

        /**
         * Réplica em escala de ícone de botão do mesmo vocabulário visual já
         * usado em PainelComparacaoCategorias.MiniRepresentacao.desenharComposicaoVazia
         * (duas caixas à esquerda, colchete, uma caixa à direita) — mesmas
         * cores (COR_SUPERFICIE/COR_BORDA) e proporções relativas daquele
         * método, mas redesenhado do zero: aquele pertence a uma classe
         * aninhada duas vezes dentro de TelaGerard e depende de estado de
         * instância dela (alvos, tamanhoCaixa()), então não dá para chamá-lo
         * diretamente daqui.
         */
        private Icon criarIconeCategoriaComposicao() {
            return new Icon() {
                public int getIconWidth() { return LARGURA_ICONE_CATEGORIA; }
                public int getIconHeight() { return ALTURA_ICONE_CATEGORIA; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        desenharFormaIconeCategoria(g2, x + 26, y + 22, false);
                        desenharFormaIconeCategoria(g2, x + 26, y + 58, false);
                        desenharFormaIconeCategoria(g2, x + 70, y + 40, false);

                        int cy = y + 40;
                        java.awt.geom.Path2D.Float chave = new java.awt.geom.Path2D.Float();
                        chave.moveTo(x + 44, y + 14);
                        chave.quadTo(x + 56, y + 22, x + 56, cy - 6);
                        chave.quadTo(x + 56, cy, x + 62, cy);
                        chave.quadTo(x + 56, cy, x + 56, cy + 6);
                        chave.quadTo(x + 56, y + 58, x + 44, y + 66);
                        g2.draw(chave);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /** Réplica em escala de ícone de botão de desenharTransformacaoVazia (caixa-seta-caixa, círculo acima) — ver criarIconeCategoriaComposicao. */
        private Icon criarIconeCategoriaTransformacao() {
            return new Icon() {
                public int getIconWidth() { return LARGURA_ICONE_CATEGORIA; }
                public int getIconHeight() { return ALTURA_ICONE_CATEGORIA; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        desenharFormaIconeCategoria(g2, x + 46, y + 16, true);
                        desenharFormaIconeCategoria(g2, x + 18, y + 52, false);
                        desenharFormaIconeCategoria(g2, x + 74, y + 52, false);

                        int cy = y + 52;
                        java.awt.geom.Path2D.Float seta = new java.awt.geom.Path2D.Float();
                        seta.moveTo(x + 26, cy);
                        seta.lineTo(x + 63, cy);
                        seta.moveTo(x + 66, cy);
                        seta.lineTo(x + 57, cy - 7);
                        seta.moveTo(x + 66, cy);
                        seta.lineTo(x + 57, cy + 7);
                        g2.draw(seta);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /** Réplica em escala de ícone de botão de desenharComparacaoVazia (caixa acima, seta para baixo, caixa abaixo, círculo ao lado) — ver criarIconeCategoriaComposicao. */
        private Icon criarIconeCategoriaComparacao() {
            return new Icon() {
                public int getIconWidth() { return LARGURA_ICONE_CATEGORIA; }
                public int getIconHeight() { return ALTURA_ICONE_CATEGORIA; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        desenharFormaIconeCategoria(g2, x + 40, y + 14, false);
                        desenharFormaIconeCategoria(g2, x + 40, y + 66, false);
                        desenharFormaIconeCategoria(g2, x + 72, y + 40, true);

                        int cx = x + 40;
                        java.awt.geom.Path2D.Float seta = new java.awt.geom.Path2D.Float();
                        seta.moveTo(cx, y + 23);
                        seta.lineTo(cx, y + 58);
                        seta.moveTo(cx, y + 20);
                        seta.lineTo(cx - 7, y + 29);
                        seta.moveTo(cx, y + 20);
                        seta.lineTo(cx + 7, y + 29);
                        g2.draw(seta);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /**
         * Glifo de composição de transformações (estado → transf.1 → estado
         * → transf.2 → estado, mais o arco por baixo ligando o primeiro e o
         * último estado, com a transformação resultante/composta perto do
         * arco) — mesma estrutura da figura de referência da usuária
         * (2026-07-26), sem os valores numéricos (que ali eram só exemplo:
         * +10/-6/+4), e confirmada linha a linha contra
         * `RenderizadorComposicaoTransformacoes.criarCena`: t1/t2 acima das
         * duas setas, "tr" (a transformação composta) perto do arco,
         * exatamente como desenhado aqui.
         *
         * A usuária confirmou que esta figura pertence à categoria canônica
         * `COMPOSICAO_TRANSFORMACOES`, que tem exatamente esta estrutura de arco.
         *
         * Categoria já existe (`TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES`,
         * com 20 situações curadas e renderizador próprio), mas o grupo
         * "Transformações" do menu "Categoria" a esconde atrás de "Em
         * construção" — decisão explícita da usuária em 2026-07-26: este
         * atalho abre a categoria mesmo assim.
         */
        private Icon criarIconeCategoriaComposicaoTransformacoes() {
            return new Icon() {
                public int getIconWidth() { return LARGURA_ICONE_CATEGORIA; }
                public int getIconHeight() { return ALTURA_ICONE_CATEGORIA; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        desenharFormaIconeCategoria(g2, x + 16, y + 30, false);
                        desenharFormaIconeCategoria(g2, x + 46, y + 30, false);
                        desenharFormaIconeCategoria(g2, x + 76, y + 30, false);
                        desenharFormaIconeCategoria(g2, x + 31, y + 12, true);
                        desenharFormaIconeCategoria(g2, x + 61, y + 12, true);
                        desenharFormaIconeCategoria(g2, x + 46, y + 70, true);

                        java.awt.geom.Path2D.Float setas = new java.awt.geom.Path2D.Float();
                        setas.moveTo(x + 24, y + 30);
                        setas.lineTo(x + 38, y + 30);
                        setas.moveTo(x + 38, y + 30);
                        setas.lineTo(x + 33, y + 26);
                        setas.moveTo(x + 38, y + 30);
                        setas.lineTo(x + 33, y + 34);

                        setas.moveTo(x + 54, y + 30);
                        setas.lineTo(x + 68, y + 30);
                        setas.moveTo(x + 68, y + 30);
                        setas.lineTo(x + 63, y + 26);
                        setas.moveTo(x + 68, y + 30);
                        setas.lineTo(x + 63, y + 34);
                        g2.draw(setas);

                        java.awt.geom.Path2D.Float arco = new java.awt.geom.Path2D.Float();
                        arco.moveTo(x + 16, y + 38);
                        arco.curveTo(x + 22, y + 66, x + 70, y + 66, x + 74, y + 38);
                        arco.moveTo(x + 74, y + 38);
                        arco.lineTo(x + 67, y + 35);
                        arco.moveTo(x + 74, y + 38);
                        arco.lineTo(x + 72, y + 45);
                        g2.draw(arco);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /**
         * Glifo de transformação de uma relação (estado inicial → transf. →
         * estado final, todos os três desenhados como círculo — mesma
         * convenção de RenderizadorTransformacaoRelacao, onde relacaoGrande
         * e transformacao usam TipoFiguraDiagrama.ELIPSE, diferente da
         * Transformação de medidas comum, onde só a transformação é
         * círculo). Categoria já existe
         * (`TipoSituacaoAditiva.TRANSFORMACAO_RELACAO`, com dados curados e
         * renderizador próprios), mas o grupo "Relações" do menu "Categoria"
         * a esconde atrás de "Em construção" — mesma decisão da usuária em
         * 2026-07-26 já aplicada à transformação composta acima: o atalho
         * abre a categoria mesmo assim.
         */
        private Icon criarIconeCategoriaTransformacaoRelacao() {
            return new Icon() {
                public int getIconWidth() { return LARGURA_ICONE_CATEGORIA; }
                public int getIconHeight() { return ALTURA_ICONE_CATEGORIA; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        desenharFormaIconeCategoria(g2, x + 46, y + 16, true);
                        desenharFormaIconeCategoria(g2, x + 18, y + 52, true);
                        desenharFormaIconeCategoria(g2, x + 74, y + 52, true);

                        int cy = y + 52;
                        java.awt.geom.Path2D.Float seta = new java.awt.geom.Path2D.Float();
                        seta.moveTo(x + 26, cy);
                        seta.lineTo(x + 63, cy);
                        seta.moveTo(x + 66, cy);
                        seta.lineTo(x + 57, cy - 7);
                        seta.moveTo(x + 66, cy);
                        seta.lineTo(x + 57, cy + 7);
                        g2.draw(seta);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /**
         * Glifo de composição de relações (duas relações à esquerda,
         * colchete, uma relação total à direita — todas círculo, mesma
         * convenção de RenderizadorComposicaoRelacoes, que usa relacaoGrande
         * = ELIPSE para os três elementos e chaveVertical como conector).
         * Mesma estrutura de criarIconeCategoriaComposicao (colchete), só
         * trocando quadrado por círculo nas três formas. Categoria já
         * existe (`TipoSituacaoAditiva.COMPOSICAO_RELACOES`, com dados
         * curados e renderizador próprios), mas o grupo "Relações" do menu
         * "Categoria" a esconde atrás de "Em construção" — mesma decisão da
         * usuária em 2026-07-26 já aplicada às duas categorias anteriores: o
         * atalho abre mesmo assim.
         */
        private Icon criarIconeCategoriaComposicaoRelacoes() {
            return new Icon() {
                public int getIconWidth() { return LARGURA_ICONE_CATEGORIA; }
                public int getIconHeight() { return ALTURA_ICONE_CATEGORIA; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        desenharFormaIconeCategoria(g2, x + 26, y + 22, true);
                        desenharFormaIconeCategoria(g2, x + 26, y + 58, true);
                        desenharFormaIconeCategoria(g2, x + 70, y + 40, true);

                        int cy = y + 40;
                        java.awt.geom.Path2D.Float chave = new java.awt.geom.Path2D.Float();
                        chave.moveTo(x + 44, y + 14);
                        chave.quadTo(x + 56, y + 22, x + 56, cy - 6);
                        chave.quadTo(x + 56, cy, x + 62, cy);
                        chave.quadTo(x + 56, cy, x + 56, cy + 6);
                        chave.quadTo(x + 56, y + 58, x + 44, y + 66);
                        g2.draw(chave);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /**
         * Uma forma (quadrado ou círculo) do glifo de categoria, centrada em
         * (cx,cy) — mesmas cores de desenharFormaVazia (preenchimento
         * COR_SUPERFICIE, contorno COR_BORDA): elemento estrutural sem
         * significado próprio, não um sinal de feedback (ver
         * gerard-identidade-visual).
         */
        private void desenharFormaIconeCategoria(Graphics2D g2, int cx, int cy, boolean circulo) {
            int metade = CAIXA_ICONE_CATEGORIA / 2;
            g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
            if (circulo) {
                g2.fillOval(cx - metade, cy - metade, CAIXA_ICONE_CATEGORIA, CAIXA_ICONE_CATEGORIA);
            } else {
                g2.fillRect(cx - metade, cy - metade, CAIXA_ICONE_CATEGORIA, CAIXA_ICONE_CATEGORIA);
            }
            g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
            if (circulo) {
                g2.drawOval(cx - metade, cy - metade, CAIXA_ICONE_CATEGORIA, CAIXA_ICONE_CATEGORIA);
            } else {
                g2.drawRect(cx - metade, cy - metade, CAIXA_ICONE_CATEGORIA, CAIXA_ICONE_CATEGORIA);
            }
        }

        /** Estilo de traço compartilhado pelos 3 glifos de categoria: fino, com pontas e junções arredondadas (suave, não "grosseiro"). */
        private Graphics2D prepararTracoIconeCategoria(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            return g2;
        }

        /** Indicador factual do único agente da arquitetura atual. */
        private void criarIndicadorAgenteModelador() {
            final Color corApagadoLed = gerard.ui.UITemaGerard.COR_ICONE_DESABILITADO;
            indicadorAgenteModelador = new gerard.pesquisador.IndicadorPulsoAgente(
                    new Color(0x4F, 0x6F, 0x64), corApagadoLed,
                    "Agente Modelador: pisca sempre que armazena um novo caso no Modelo do Usuário.");
            agenteModelador.adicionarOuvinte(new gerard.agente.modelador.OuvinteCasoAgenteModelador() {
                public void aoArmazenar(String idUsuario, gerard.agente.modelousuario.DiagnosticoTarefa diagnostico) {
                    indicadorAgenteModelador.pulsar("Última tarefa: " + diagnostico.getTarefa() + ".");
                }
            });

            caixaIndicadorAgenteModelador = new JPanel(null);
            caixaIndicadorAgenteModelador.setOpaque(true);
            caixaIndicadorAgenteModelador.setBackground(COR_SUPERFICIE);
            caixaIndicadorAgenteModelador.setBorder(
                    BorderFactory.createLineBorder(COR_BORDA_BOTAO, 1));
            caixaIndicadorAgenteModelador.setBounds(58, 8, 34, 34);
            indicadorAgenteModelador.setBounds(6, 6, 22, 22);
            caixaIndicadorAgenteModelador.add(indicadorAgenteModelador);
            add(caixaIndicadorAgenteModelador);
            setComponentZOrder(caixaIndicadorAgenteModelador, 0);
        }

        /** Faixa autenticada para observar os casos recebidos pelo Modelador. */
        private void criarFaixaAtividadeModelador() {
            faixaAtividadeModelador =
                    new gerard.pesquisador.FaixaLateralAtividadeModelador(
                            agenteModelador, this::autenticarPesquisador);
            faixaAtividadeModelador.reposicionar(getWidth(), getHeight());
            add(faixaAtividadeModelador);
            // Índice 0 = topo da pilha de pintura (ver Container#setComponentZOrder):
            // garante que a faixa fique acima de qualquer botão adicionado depois
            // dela quando expandida, mesmo sobrepondo a área de trabalho.
            setComponentZOrder(faixaAtividadeModelador, 0);
        }

        /** Nome do usuário cadastrado atual, se houver, senão o rótulo padrão do botão. */
        private String textoBotaoUsuario() {
            gerard.agente.modelousuario.ModeloUsuario modelo =
                    repositorioModeloUsuario.obter(loggerInteracaoGerard.getUsuarioAtual());
            if (modelo != null && modelo.getPerfilAluno().getNome() != null) {
                return modelo.getPerfilAluno().getNome();
            }
            return localizacao.texto("ui.button.user");
        }

        private void mostrarDialogoRelatoBug() {
            final JTextArea descricao = new JTextArea(7, 46);
            descricao.setLineWrap(true);
            descricao.setWrapStyleWord(true);
            descricao.setFont(new Font("Arial", Font.PLAIN, 14));
            descricao.setToolTipText(localizacao.texto("ui.bug.description.tooltip"));
            descricao.getAccessibleContext().setAccessibleName(localizacao.texto("ui.bug.description.label"));
            descricao.getAccessibleContext().setAccessibleDescription(localizacao.texto("ui.bug.description.tooltip"));

            JScrollPane rolagem = new JScrollPane(descricao);
            rolagem.setPreferredSize(new Dimension(520, 150));

            JLabel instrucao = new JLabel("<html>" + localizacao.texto("ui.bug.instruction") + "</html>");
            instrucao.setFont(gerard.ui.UITemaGerard.FONTE_DIALOGO);

            JPanel conteudo = new JPanel(new BorderLayout(0, 10));
            conteudo.setBorder(BorderFactory.createEmptyBorder(8, 8, 6, 8));
            conteudo.add(instrucao, BorderLayout.NORTH);
            conteudo.add(rolagem, BorderLayout.CENTER);

            Object[] opcoes = {
                localizacao.texto("ui.bug.submit"),
                localizacao.texto("analise.cancel")
            };
            int resposta = JOptionPane.showOptionDialog(
                    this,
                    conteudo,
                    localizacao.texto("ui.bug.title"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);

            if (resposta != JOptionPane.OK_OPTION) {
                requestFocusInWindow();
                return;
            }

            String relato = descricao.getText() == null ? "" : descricao.getText().trim();
            if (relato.length() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        localizacao.texto("ui.bug.required"),
                        localizacao.texto("ui.bug.title"),
                        JOptionPane.WARNING_MESSAGE);
                requestFocusInWindow();
                return;
            }

            try {
                String situacaoId = situacaoProblemaAtual == null ? "" : situacaoProblemaAtual.getId();
                String idiomaSituacao = situacaoProblemaAtual == null ? "" : situacaoProblemaAtual.getCodigoIdioma();
                String enunciadoAtual = situacaoProblemaAtual == null ? textoProblema : situacaoProblemaAtual.getEnunciado();
                String categoria = tipoSituacaoSelecionada == null ? "" : tipoSituacaoSelecionada.name();
                String representacoes = obterRepresentacoesAtuaisParaRelatoBug();
                String idiomaInterface = idiomaSelecionado == null ? "" : idiomaSelecionado.name();
                java.io.File arquivo = RegistroRelatoBug.registrar(
                        relato,
                        situacaoId,
                        categoria,
                        representacoes,
                        idiomaInterface,
                        idiomaSituacao,
                        enunciadoAtual);
                PreparadorEmailRelatoBug.MensagemPreparada email =
                        PreparadorEmailRelatoBug.preparar(
                                relato,
                                situacaoId,
                                categoria,
                                representacoes,
                                idiomaInterface,
                                idiomaSituacao,
                                enunciadoAtual);
                PreparadorEmailRelatoBug.ResultadoAbertura resultadoEmail =
                        PreparadorEmailRelatoBug.abrirMensagem(email);
                String chaveMensagemEmail;
                int tipoMensagemEmail;
                switch (resultadoEmail) {
                    case GMAIL_WEB:
                        chaveMensagemEmail = "ui.bug.gmailOpened";
                        tipoMensagemEmail = JOptionPane.INFORMATION_MESSAGE;
                        break;
                    case CLIENTE_PADRAO:
                        chaveMensagemEmail = "ui.bug.emailClientOpened";
                        tipoMensagemEmail = JOptionPane.INFORMATION_MESSAGE;
                        break;
                    default:
                        chaveMensagemEmail = "ui.bug.emailUnavailable";
                        tipoMensagemEmail = JOptionPane.WARNING_MESSAGE;
                        break;
                }
                JOptionPane.showMessageDialog(
                        this,
                        localizacao.formatar(
                                chaveMensagemEmail,
                                arquivo.getAbsolutePath(),
                                PreparadorEmailRelatoBug.DESTINATARIO),
                        localizacao.texto("ui.bug.title"),
                        tipoMensagemEmail);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        localizacao.formatar("ui.bug.error", ex.getMessage()),
                        localizacao.texto("ui.bug.title"),
                        JOptionPane.ERROR_MESSAGE);
            }
            requestFocusInWindow();
        }

        /**
         * Identifica as representações que estão efetivamente disponíveis no
         * momento do relato. O método centraliza essa classificação para que
         * novas representações possam ser acrescentadas sem alterar o formato
         * do registro ou do e-mail.
         */
        private String obterRepresentacoesAtuaisParaRelatoBug() {
            java.util.LinkedHashSet<String> representacoes =
                    new java.util.LinkedHashSet<String>();

            representacoes.add(localizacao.texto("ui.bug.representation.vergnaud"));

            if (deveExibirDiagramaComplementar() && !ehRepresentacaoComplementarGenerica()) {
                if (ehDiagramaVennComposicaoMedidas()) {
                    representacoes.add(localizacao.texto("ui.collections.title"));
                } else if (ehGraficoBarrasComparacao()) {
                    representacoes.add(localizacao.texto("ui.comparisonBars.title"));
                } else {
                    representacoes.add(localizacao.texto("ui.vann.title"));
                }
            }

            if (paineisEixosRelacoes.estaAtivo()) {
                representacoes.add(localizacao.texto("ui.bug.representation.integerAxis"));
            }

            // Campo livre da curadoria: funciona como extensão para uma
            // representação específica que ainda não tenha classificação
            // própria no código da interface.
            if (situacaoProblemaAtual != null) {
                String representacaoCurada = situacaoProblemaAtual.getRepresentacaoVisual();
                if (representacaoCurada != null
                        && representacaoCurada.trim().length() > 0) {
                    representacoes.add(representacaoCurada.trim());
                }
            }

            StringBuilder textoRepresentacoes = new StringBuilder();
            for (String representacao : representacoes) {
                if (representacao == null || representacao.trim().length() == 0) {
                    continue;
                }
                if (textoRepresentacoes.length() > 0) {
                    textoRepresentacoes.append("; ");
                }
                textoRepresentacoes.append(representacao.trim());
            }
            return textoRepresentacoes.toString();
        }

        // Senha fixa em código (decisão do usuário em 2026-07-23): é um portão
        // leve pra impedir abertura casual da Visão de Pesquisador, não uma
        // medida de segurança de verdade — qualquer um com acesso ao .class
        // consegue ler o valor. Trocar a senha exige recompilar o app.
        private static final String SENHA_VISAO_PESQUISADOR = "gerard";

        private boolean autenticarPesquisador() {
            JPasswordField campoSenha = new JPasswordField(18);
            int opcao = JOptionPane.showConfirmDialog(
                    this, campoSenha, localizacao.texto("pesq.password.title"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcao != JOptionPane.OK_OPTION) {
                return false;
            }
            String senhaDigitada = new String(campoSenha.getPassword());
            if (!SENHA_VISAO_PESQUISADOR.equals(senhaDigitada)) {
                JOptionPane.showMessageDialog(this, localizacao.texto("pesq.password.wrong"),
                        localizacao.texto("pesq.password.title"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        private void criarBotaoRestaurar() {
            botaoRestaurar = new JButton(criarIconeRestaurar());
            botaoRestaurar.setBounds(27, 70 + ALTURA_PAINEL_ATALHOS_CATEGORIA, 26, 26);
            configurarBotaoAcaoContextual(botaoRestaurar, localizacao.texto("ui.tooltip.restore.elements"));
            botaoRestaurar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    registrarAcaoRestauracao(
                            gerard.dominio.campoaditivo.TipoRestauracaoModelagem.ELEMENTOS_FORA_DO_DIAGRAMA,
                            "Restaurar elementos fora do diagrama",
                            "Botão Restaurar",
                            "Botão Restaurar do enunciado",
                            "Repor elementos móveis que ficaram fora do modelo",
                            "OBJ8",
                            "A restauração permite retomar a modelagem sem alterar o problema.",
                            "RESTAURAR_TEXTO"
                    );
                    restaurarElementosForaDoDiagrama();
                    requestFocusInWindow();
                }
            });
            add(botaoRestaurar);
        }

        /**
         * Aplica um padrão visual único às ações contextuais do enunciado.
         * Os ícones seguem affordances convencionais: seta circular para
         * restaurar/refazer o estado e lápis para editar/corrigir.
         */
        private void configurarBotaoAcaoContextual(final JButton botao, String descricao) {
            botao.setFocusable(true);
            botao.setFocusPainted(true);
            botao.setMargin(new Insets(0, 0, 0, 0));
            botao.setContentAreaFilled(true);
            botao.setOpaque(true);
            botao.setBackground(COR_SUPERFICIE);
            botao.setForeground(COR_TEXTO_SECUNDARIO);
            botao.setBorder(BorderFactory.createLineBorder(COR_BORDA_BOTAO));
            botao.setToolTipText(descricao);
            botao.getAccessibleContext().setAccessibleName(descricao);
            botao.getAccessibleContext().setAccessibleDescription(descricao);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    botao.setBackground(COR_SUPERFICIE_SUAVE);
                }
                public void mouseExited(MouseEvent e) {
                    botao.setBackground(COR_SUPERFICIE);
                }
            });
        }

        private Icon criarIconeRestaurar() {
            return new Icon() {
                public int getIconWidth() { return 16; }
                public int getIconHeight() { return 16; }
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(c.isEnabled() ? COR_TEXTO_SECUNDARIO : gerard.ui.UITemaGerard.COR_ICONE_DESABILITADO);
                        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.drawArc(x + 3, y + 3, 10, 10, 40, 285);
                        Path2D seta = new Path2D.Double();
                        seta.moveTo(x + 2.5, y + 7.0);
                        seta.lineTo(x + 3.7, y + 2.5);
                        seta.lineTo(x + 7.5, y + 4.7);
                        g2.draw(seta);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        private Icon criarIconeEditar() {
            return new Icon() {
                public int getIconWidth() { return 16; }
                public int getIconHeight() { return 16; }
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(c.isEnabled() ? COR_TEXTO_SECUNDARIO : gerard.ui.UITemaGerard.COR_ICONE_DESABILITADO);
                        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.drawLine(x + 4, y + 12, x + 11, y + 5);
                        g2.drawLine(x + 6, y + 14, x + 13, y + 7);
                        g2.drawLine(x + 4, y + 12, x + 3, y + 15);
                        g2.drawLine(x + 3, y + 15, x + 6, y + 14);
                        g2.drawLine(x + 11, y + 5, x + 13, y + 7);
                        g2.drawLine(x + 12, y + 4, x + 14, y + 6);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        private Icon criarIconeIdiomaSituacao() {
            return new Icon() {
                public int getIconWidth() { return 18; }
                public int getIconHeight() { return 16; }
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(c.isEnabled() ? COR_TEXTO_SECUNDARIO : gerard.ui.UITemaGerard.COR_ICONE_DESABILITADO);
                        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
                        g2.drawString("A", x + 1, y + 11);
                        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                        g2.drawString("文", x + 9, y + 11);
                        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.drawLine(x + 5, y + 14, x + 13, y + 14);
                        g2.drawLine(x + 11, y + 12, x + 13, y + 14);
                        g2.drawLine(x + 11, y + 16, x + 13, y + 14);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        public void definirTelaCuradoriaSituacoes(TelaCuradoriaSituacoes telaCuradoriaSituacoes) {
            this.telaCuradoriaSituacoes = telaCuradoriaSituacoes;
        }

        private void criarBotaoCorrigirCuradoria() {
            botaoCorrigirCuradoria = new JButton(criarIconeEditar());
            botaoCorrigirCuradoria.setBounds(27, 101 + ALTURA_PAINEL_ATALHOS_CATEGORIA, 26, 26);
            configurarBotaoAcaoContextual(botaoCorrigirCuradoria, localizacao.texto("ui.tooltip.correctCuration"));
            botaoCorrigirCuradoria.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    abrirCuradoriaDaSituacaoRenderizada();
                }
            });
            add(botaoCorrigirCuradoria);
        }

        private void criarBotaoIdiomaSituacao() {
            botaoIdiomaSituacao = new JButton(criarIconeIdiomaSituacao());
            botaoIdiomaSituacao.setBounds(27, 132 + ALTURA_PAINEL_ATALHOS_CATEGORIA, 26, 26);
            configurarBotaoAcaoContextual(botaoIdiomaSituacao, descricaoBotaoIdiomaSituacao());
            botaoIdiomaSituacao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarMenuIdiomaDaSituacao();
                }
            });
            add(botaoIdiomaSituacao);
        }

        private String descricaoBotaoIdiomaSituacao() {
            String idiomaAtual = situacaoProblemaAtual == null ? "" : nomeIdiomaSituacao(situacaoProblemaAtual.getCodigoIdioma());
            String base = localizacao.texto("ui.tooltip.problemLanguage");
            return idiomaAtual.length() == 0 ? base : base + ": " + idiomaAtual;
        }

        private String nomeIdiomaSituacao(String codigo) {
            IdiomaSituacao idioma = cadastroIdiomasSituacao.obter(codigo);
            return idioma == null ? (codigo == null ? "" : codigo) : idioma.getNome();
        }

        private void mostrarMenuIdiomaDaSituacao() {
            if (situacaoProblemaAtual == null || situacaoProblemaAtual.getSituacaoGrupoId() == null) {
                JOptionPane.showMessageDialog(this, localizacao.texto("ui.problemLanguage.unavailable"),
                        localizacao.texto("ui.problemLanguage.title"), JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            final String grupo = situacaoProblemaAtual.getSituacaoGrupoId();
            final java.util.Map<String, SituacaoProblemaAditiva> porCodigo = new LinkedHashMap<String, SituacaoProblemaAditiva>();
            for (SituacaoProblemaAditiva s : repositorioSituacoesAditivas.listarValidadas()) {
                if (grupo.equals(s.getSituacaoGrupoId()) && s.getEnunciado() != null && s.getEnunciado().trim().length() > 0) {
                    porCodigo.put(gerard.idioma.IdiomaSituacao.normalizarCodigo(s.getCodigoIdioma()), s);
                }
            }
            if (porCodigo.isEmpty()) {
                JOptionPane.showMessageDialog(this, localizacao.texto("ui.problemLanguage.unavailable"),
                        localizacao.texto("ui.problemLanguage.title"), JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JPopupMenu menu = new JPopupMenu();
            estilizarMenuPopup(menu);
            String codigoAtual = gerard.idioma.IdiomaSituacao.normalizarCodigo(situacaoProblemaAtual.getCodigoIdioma());
            for (IdiomaSituacao idioma : cadastroIdiomasSituacao.listar()) {
                final SituacaoProblemaAditiva versao = porCodigo.remove(idioma.getCodigo());
                if (versao == null) continue;
                JMenuItem item = new JMenuItem((idioma.getCodigo().equals(codigoAtual) ? "✓ " : "") + idioma.getNome());
                estilizarItemMenuPopup(item);
                item.setEnabled(!idioma.getCodigo().equals(codigoAtual));
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        trocarIdiomaDaSituacao(versao);
                    }
                });
                menu.add(item);
            }
            for (final SituacaoProblemaAditiva versao : porCodigo.values()) {
                String codigo = versao.getCodigoIdioma();
                JMenuItem item = new JMenuItem((gerard.idioma.IdiomaSituacao.normalizarCodigo(codigo).equals(codigoAtual) ? "✓ " : "") + nomeIdiomaSituacao(codigo));
                estilizarItemMenuPopup(item);
                item.setEnabled(!gerard.idioma.IdiomaSituacao.normalizarCodigo(codigo).equals(codigoAtual));
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) { trocarIdiomaDaSituacao(versao); }
                });
                menu.add(item);
            }
            menu.show(botaoIdiomaSituacao, botaoIdiomaSituacao.getWidth() + 3, 0);
        }

        private void trocarIdiomaDaSituacao(SituacaoProblemaAditiva versao) {
            if (!situacaoProblemaExibivel(versao)) return;
            cancelarEfeitosArraste();
            String anterior = situacaoProblemaAtual == null ? "" : situacaoProblemaAtual.getCodigoIdioma();
            situacaoProblemaAtual = versao;
            textoProblemaEhMensagemSistema = false;
            textoProblema = materializadorEnunciadoCurado.materializar(versao);
            resultadoInterpretacao = construtorResultadoCurado.construir(versao, textoProblema);
            atualizarContextoAdaptativoIncognitaAtual();
            handlerElementoTextoMovel.cancelar();
            elementoTextoFocado = null;
            inicializarElementosTexto();
            atualizarRotulosDiagramaVergnaudSemReposicionar();
            atualizarRotulosDiagramaVennSemLimparQuadradinhos();
            registrarAcaoGranular("SELECIONAR", "Alterar idioma da situação-problema",
                    "Escolha de versão linguística", "BOTAO_IDIOMA_SITUACAO", "trocar_idioma_situacao",
                    "idioma_anterior=" + anterior + "; idioma_novo=" + versao.getCodigoIdioma()
                            + "; situacao_grupo_id=" + versao.getSituacaoGrupoId(),
                    "O enunciado foi substituído por uma versão validada do mesmo grupo conceitual.");
            atualizarTextosFixosDaInterface();
            repaint();
            requestFocusInWindow();
        }

        private void abrirCuradoriaDaSituacaoRenderizada() {
            if (situacaoProblemaAtual == null || telaCuradoriaSituacoes == null) {
                return;
            }
            if (!ControleAcessoPesquisador.solicitarAutorizacao(this)) {
                registrarAcaoGranular("SELECIONAR", "Solicitar acesso à curadoria",
                        "Controle de acesso", "BOTAO_CURADORIA_INLINE", "acesso_curadoria_negado",
                        "situacao_id=" + situacaoProblemaAtual.getId(),
                        "A curadoria específica não foi aberta.");
                requestFocusInWindow();
                return;
            }
            registrarAcaoGranular("SELECIONAR", "Corrigir curadoria da situação renderizada",
                    "Abrir curadoria específica", "BOTAO_CURADORIA_INLINE", "abrir_curadoria_especifica",
                    "situacao_id=" + situacaoProblemaAtual.getId(),
                    "Tela vertical de curadoria aberta diretamente a partir do enunciado renderizado.");
            telaCuradoriaSituacoes.abrirCuradoriaDaSituacao(situacaoProblemaAtual);
            requestFocusInWindow();
        }

        private void criarBotaoArtefatoExplicativo() {
            botaoArtefatoExplicativo = new JButton("A");
            botaoArtefatoExplicativo.setBounds(27, 163 + ALTURA_PAINEL_ATALHOS_CATEGORIA, 26, 26);
            botaoArtefatoExplicativo.setFont(gerard.ui.UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
            configurarBotaoAcaoContextual(botaoArtefatoExplicativo, localizacao.texto("analise.button.tooltip"));
            botaoArtefatoExplicativo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    abrirArtefatoExplicativo();
                }
            });
            add(botaoArtefatoExplicativo);
        }

        private void abrirArtefatoExplicativo() {
            if (!autenticarPesquisador()) {
                requestFocusInWindow();
                return;
            }
            if (situacaoProblemaAtual == null || resultadoInterpretacao == null) {
                JOptionPane.showMessageDialog(this, localizacao.texto("analise.unavailable"),
                        localizacao.texto("analise.title"), JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (!existeAoMenosUmPosicionamentoNoDiagramaVergnaud()) {
                JOptionPane.showMessageDialog(this, localizacao.texto("analise.requires.positioning"),
                        localizacao.texto("analise.title"), JOptionPane.INFORMATION_MESSAGE);
                atualizarDisponibilidadeArtefatoExplicativo();
                return;
            }
            java.util.List<ItemExplicacaoModelagem> itens = new ArrayList<ItemExplicacaoModelagem>();
            for (PapelElementoInterpretado papel : resultadoInterpretacao.getPapeis()) {
                itens.add(new ItemExplicacaoModelagem(papel.getElemento(), papel.getChavePapel(), papel.isConhecido()));
            }
            TelaArtefatoExplicativo.ArtefatoContexto contexto =
                    new TelaArtefatoExplicativo.ArtefatoContexto(
                            loggerInteracaoGerard.getTentativaAtualId(),
                            loggerInteracaoGerard.getTentativaAtualNumeroSituacao(),
                            loggerInteracaoGerard.getUsuarioAtual(),
                            loggerInteracaoGerard.getProblemaAtual(),
                            situacaoProblemaAtual.getId(),
                            situacaoProblemaAtual.getSituacaoGrupoId(),
                            situacaoProblemaAtual.getCodigoIdioma(),
                            tipoSituacaoSelecionada.name(),
                            fotografarEstadoModelagem(),
                            situacaoProblemaAtual.getEnunciado(),
                            modoFeedbackTeste == null ? "" : modoFeedbackTeste.name(),
                            "Diagrama de Vergnaud",
                            fotografarImagemModelagem());
            boolean salvo = TelaArtefatoExplicativo.mostrar(this, itens, contexto, agenteModelador,
                    analysisUnitAuditService);
            if (salvo) {
                registrarAcaoGranular("TEXTO", "Explicitar decisões da modelagem",
                        "Análise qualitativa da tentativa", "ARTEFATO_EXPLICATIVO",
                        "Registrar justificativas, dificuldade e invariante operatório",
                        "tentativa_id=" + loggerInteracaoGerard.getTentativaAtualId(),
                        "Artefato explicativo salvo sem alterar a modelagem.");
            }
            requestFocusInWindow();
        }

        private boolean existeAoMenosUmPosicionamentoNoDiagramaVergnaud() {
            if (!categoriaSelecionadaParaAtividade || situacaoProblemaAtual == null) {
                return false;
            }
            for (ItemTextoArrastavel item : itensArrastaveis) {
                if (itemEstaSobreElementoDoDiagrama(item)) {
                    return true;
                }
            }
            return false;
        }

        private void atualizarDisponibilidadeArtefatoExplicativo() {
            if (botaoArtefatoExplicativo == null) {
                return;
            }
            boolean disponivel = existeAoMenosUmPosicionamentoNoDiagramaVergnaud();
            botaoArtefatoExplicativo.setVisible(disponivel);
            botaoArtefatoExplicativo.setEnabled(disponivel);
            if (analysisUnitAuditService != null) {
                analysisUnitAuditService.definirDisponibilidadeBotaoAtual(disponivel);
            }
        }

        private BufferedImage fotografarImagemModelagem() {
            Rectangle recorte = obterLimitesConteudoModelagem();
            Rectangle limitesTela = new Rectangle(0, 0, Math.max(1, getWidth()), Math.max(1, getHeight()));
            recorte = recorte.intersection(limitesTela);
            if (recorte.width <= 0 || recorte.height <= 0) {
                return null;
            }
            BufferedImage imagem = new BufferedImage(recorte.width, recorte.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = imagem.createGraphics();
            try {
                // Fundo branco apenas na dimensão exata do diagrama recortado.
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, recorte.width, recorte.height);
                g2.translate(-recorte.x, -recorte.y);
                paint(g2);
            } finally {
                g2.dispose();
            }
            return imagem;
        }

        /**
         * Calcula a menor área que contém somente a representação de Vergnaud
         * atualmente visível. Controles, cartões e grandes margens da tela não
         * fazem parte da fotografia usada no artefato explicativo.
         */
        private Rectangle obterLimitesConteudoModelagem() {
            Rectangle limites = null;

            for (ElementoVergnaud elemento : elementosVergnaud) {
                Rectangle r = new Rectangle(elemento.x, elemento.y,
                        Math.max(1, elemento.largura), Math.max(1, elemento.altura));
                limites = limites == null ? r : limites.union(r);
            }

            for (ConectorVergnaud conector : conectoresVergnaud) {
                Rectangle r = obterLimitesConector(conector);
                limites = limites == null ? r : limites.union(r);
            }

            if (limites == null) {
                limites = obterAreaConteudoDiagramaVergnaud();
            }

            // Margem pequena para preservar pontas de seta, chaves e espessuras.
            int margem = 18;
            limites.grow(margem, margem);
            return limites;
        }

        private Rectangle obterLimitesConector(ConectorVergnaud conector) {
            int minX = Math.min(conector.x1, conector.x2);
            int maxX = Math.max(conector.x1, conector.x2);
            int minY = Math.min(conector.y1, conector.y2);
            int maxY = Math.max(conector.y1, conector.y2);

            if (conector.tipo == TipoConectorDiagrama.SETA_CURVA) {
                int controleY = Math.max(conector.y1, conector.y2) + 96;
                minY = Math.min(minY, controleY);
                maxY = Math.max(maxY, controleY);
            }

            int margemX = (conector.tipo == TipoConectorDiagrama.CHAVE_VERTICAL) ? 28 : 14;
            int margemY = (conector.tipo == TipoConectorDiagrama.CHAVE_HORIZONTAL) ? 28 : 14;
            return new Rectangle(minX - margemX, minY - margemY,
                    Math.max(1, (maxX - minX) + (margemX * 2)),
                    Math.max(1, (maxY - minY) + (margemY * 2)));
        }

        private String fotografarEstadoModelagem() {
            StringBuilder sb = new StringBuilder();
            sb.append("itens=[");
            for (int i = 0; i < itensArrastaveis.size(); i++) {
                ItemTextoArrastavel item = itensArrastaveis.get(i);
                if (i > 0) sb.append(';');
                sb.append(item.valor).append('@').append(item.x).append(',').append(item.y)
                  .append(':').append(item.chavePapel == null ? "" : item.chavePapel);
            }
            sb.append("];vergnaud=[");
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                ElementoVergnaud el = elementosVergnaud.get(i);
                if (i > 0) sb.append(';');
                sb.append(el.rotulo).append('@').append(el.x).append(',').append(el.y)
                  .append('=').append(el.textoEditavel == null ? "" : el.textoEditavel);
            }
            sb.append("];venn=[");
            for (int i = 0; i < quadradinhosVenn.size(); i++) {
                QuadradinhoVenn q = quadradinhosVenn.get(i);
                if (i > 0) sb.append(';');
                sb.append(q.origem).append('@').append(q.x).append(',').append(q.y)
                  .append('=').append(q.textoEditavel == null ? "" : q.textoEditavel);
            }
            sb.append(']');
            return sb.toString();
        }

        private void criarBotaoRestaurarDiagrama() {
            botaoRestaurarDiagrama = new JButton(criarIconeRestaurar());
            botaoRestaurarDiagrama.setBounds(548, 225, 26, 26);
            configurarBotaoAcaoContextual(
                    botaoRestaurarDiagrama,
                    localizacao.texto("ui.tooltip.restore.diagram")
            );
            botaoRestaurarDiagrama.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    registrarAcaoRestauracao(
                            gerard.dominio.campoaditivo.TipoRestauracaoModelagem.DIAGRAMA_COMPLETO,
                            "Restaurar a área do diagrama",
                            "Botão Restaurar",
                            "Botão Restaurar da área do diagrama",
                            "Limpar a modelagem e permitir nova tentativa",
                            "OBJ8",
                            "O sujeito pode recomeçar a construção do diagrama.",
                            "RESTAURAR_DIAGRAMA"
                    );
                    restaurarModelagemDiagrama();
                    requestFocusInWindow();
                }
            });
            add(botaoRestaurarDiagrama);
        }


        private void criarBotoesAjudaContextual() {
            botaoAjudaTexto = criarBotaoAjudaContextual(ScaffoldingAjudaContextual.Area.TEXTO);
            botaoAjudaVergnaud = criarBotaoAjudaContextual(ScaffoldingAjudaContextual.Area.VERGNAUD);
            botaoAjudaComplementar = criarBotaoAjudaContextual(ScaffoldingAjudaContextual.Area.COMPLEMENTAR);
            add(botaoAjudaTexto);
            add(botaoAjudaVergnaud);
            add(botaoAjudaComplementar);
            atualizarTextosBotoesAjudaContextual();
            criarBotaoVerDicaPosicionamento();
        }

        /**
         * AG_AE — botão "Ver dica" (sob demanda, item 7 do levantamento de
         * 2026-08-07): mesmo padrão visual dos botões de ajuda contextual
         * acima, mas com texto (não só ícone) por ser uma affordance nova,
         * ainda não reconhecível por forma. Fica oculto sempre que não há
         * papel-dado pendente de posicionar (ver
         * reposicionarBotaoVerDicaPosicionamento).
         */
        private void criarBotaoVerDicaPosicionamento() {
            final JButton botao = new JButton();
            botao.setFocusable(true);
            botao.setFocusPainted(true);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.setFont(new Font("Arial", Font.PLAIN, 12));
            botao.setMargin(new Insets(2, 8, 2, 8));
            botao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarProximaDicaPosicionamento();
                }
            });
            botaoVerDicaPosicionamento = botao;
            add(botao);
            atualizarTextoBotaoVerDicaPosicionamento();
        }

        private void atualizarTextoBotaoVerDicaPosicionamento() {
            if (botaoVerDicaPosicionamento == null) {
                return;
            }
            String texto = localizacao.texto("ui.hint.stepPlacement.button");
            botaoVerDicaPosicionamento.setText(texto);
            botaoVerDicaPosicionamento.getAccessibleContext().setAccessibleName(texto);
            botaoVerDicaPosicionamento.setSize(botaoVerDicaPosicionamento.getPreferredSize());
        }

        /**
         * Visível só quando a categoria está selecionada, a modelagem
         * linguística está em curso (há elementos no diagrama) e ainda
         * existe ao menos um papel-dado não resolvido — a mesma condição
         * de obterProximoPapelNaoResolvidoParaDica(), sem gerar o evento
         * (só decide visibilidade, não conta como exibição de dica).
         *
         * Também fica visível quando todos os papéis-dado já foram
         * posicionados corretamente mas ainda falta responder (ou foi
         * respondido errado) algum seletor de soma/subtração ativo — ver
         * existeSeletorOperacaoPendenteParaDica(). Sem isso o botão
         * simplesmente sumia nesse momento e o aluno ficava sem saber que
         * a etapa pendente virou o seletor, não mais o diagrama (Item 36,
         * 2026-08-23: "e se o usuário não for notificado que tem que
         * escolher a operação e ficar esperando infinitamente?").
         */
        private void reposicionarBotaoVerDicaPosicionamento(Rectangle area) {
            if (botaoVerDicaPosicionamento == null || area == null) {
                return;
            }
            boolean exibir = categoriaSelecionadaParaAtividade
                    && !elementosVergnaud.isEmpty()
                    && (obterProximoPapelNaoResolvidoParaDica() != null
                            || existeSeletorOperacaoPendenteParaDica());
            botaoVerDicaPosicionamento.setVisible(exibir);
            botaoVerDicaPosicionamento.setEnabled(exibir);
            if (exibir) {
                Dimension tamanho = botaoVerDicaPosicionamento.getPreferredSize();
                botaoVerDicaPosicionamento.setBounds(
                        area.x + area.width - 38 - tamanho.width - 6,
                        area.y + 10, tamanho.width, 26);
            }
        }

        private JButton criarBotaoAjudaContextual(final ScaffoldingAjudaContextual.Area area) {
            final JButton botao = new JButton(criarIconeInterrogacaoContextual());
            botao.setBounds(0, 0, 26, 26);
            botao.setFocusable(true);
            botao.setFocusPainted(true);
            botao.setContentAreaFilled(false);
            botao.setOpaque(false);
            botao.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            botao.setMargin(new Insets(0, 0, 0, 0));
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarMenuAjudaContextual(area, botao);
                }
            });
            botao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarMenuAjudaContextual(area, botao);
                }
            });
            return botao;
        }

        private Icon criarIconeInterrogacaoContextual() {
            return new Icon() {
                public int getIconWidth() { return 20; }
                public int getIconHeight() { return 20; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(COR_SUPERFICIE);
                        g2.fillOval(x + 1, y + 1, 18, 18);
                        g2.setColor(COR_TEXTO_SECUNDARIO);
                        g2.setStroke(new BasicStroke(1.4f));
                        g2.drawOval(x + 1, y + 1, 18, 18);
                        g2.setFont(new Font("Arial", Font.BOLD, 14));
                        FontMetrics fm = g2.getFontMetrics();
                        String simbolo = "?";
                        int tx = x + 10 - fm.stringWidth(simbolo) / 2;
                        int ty = y + 10 + (fm.getAscent() - fm.getDescent()) / 2;
                        g2.drawString(simbolo, tx, ty);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        private void atualizarTextosBotoesAjudaContextual() {
            atualizarTextoBotaoAjudaContextual(botaoAjudaTexto, ScaffoldingAjudaContextual.Area.TEXTO);
            atualizarTextoBotaoAjudaContextual(botaoAjudaVergnaud, ScaffoldingAjudaContextual.Area.VERGNAUD);
            atualizarTextoBotaoAjudaContextual(botaoAjudaComplementar, ScaffoldingAjudaContextual.Area.COMPLEMENTAR);
        }

        private void atualizarTextoBotaoAjudaContextual(JButton botao, ScaffoldingAjudaContextual.Area area) {
            if (botao == null) {
                return;
            }
            String nomeArea = obterNomeAreaAjudaContextual(area);
            String descricao = localizacao.formatar("ui.help.tooltip", nomeArea);
            // O próprio mouseover abre o painel; um tooltip Swing simultâneo
            // encobriria o cabeçalho e duplicaria a orientação visual.
            botao.setToolTipText(null);
            botao.getAccessibleContext().setAccessibleName(localizacao.formatar("ui.help.header", nomeArea));
            botao.getAccessibleContext().setAccessibleDescription(descricao);
        }

        private String obterNomeAreaAjudaContextual(ScaffoldingAjudaContextual.Area area) {
            if (area == ScaffoldingAjudaContextual.Area.COMPLEMENTAR) {
                if (ehDiagramaVennComposicaoMedidas()) {
                    return localizacao.texto("ui.collections.title");
                }
                if (ehGraficoBarrasComparacao()) {
                    return localizacao.texto("ui.comparisonBars.title");
                }
                return localizacao.texto("ui.vann.title");
            }
            return localizacao.texto(scaffoldingAjudaContextual.obterChaveArea(area));
        }

        private void reposicionarBotaoAjudaTexto() {
            if (botaoAjudaTexto == null) {
                return;
            }
            boolean exibir = categoriaSelecionadaParaAtividade;
            botaoAjudaTexto.setVisible(exibir);
            botaoAjudaTexto.setEnabled(exibir);
            if (exibir) {
                botaoAjudaTexto.setBounds(Math.max(18, getWidth() - 53), 63 + ALTURA_PAINEL_ATALHOS_CATEGORIA, 26, 26);
            }
        }

        private void reposicionarBotaoAjudaVergnaud(Rectangle area) {
            if (botaoAjudaVergnaud == null || area == null) {
                return;
            }
            boolean exibir = categoriaSelecionadaParaAtividade;
            botaoAjudaVergnaud.setVisible(exibir);
            botaoAjudaVergnaud.setEnabled(exibir);
            if (exibir) {
                botaoAjudaVergnaud.setBounds(area.x + area.width - 38, area.y + 10, 26, 26);
            }
        }

        private void reposicionarBotaoAjudaComplementar(Rectangle area) {
            if (botaoAjudaComplementar == null || area == null) {
                return;
            }
            boolean exibir = categoriaSelecionadaParaAtividade && deveExibirDiagramaComplementar();
            botaoAjudaComplementar.setVisible(exibir);
            botaoAjudaComplementar.setEnabled(exibir);
            if (exibir) {
                botaoAjudaComplementar.setBounds(area.x + area.width - 38, area.y + 10, 26, 26);
            }
        }

        private void mostrarMenuAjudaContextual(final ScaffoldingAjudaContextual.Area area, final JButton botao) {
            if (area == null || botao == null || !botao.isShowing()) {
                return;
            }
            if (menuAjudaContextualAtivo != null
                    && menuAjudaContextualAtivo.isVisible()
                    && area == areaAjudaContextualAtiva
                    && menuAjudaContextualAtivo.getInvoker() == botao) {
                return;
            }

            fecharMenuAjudaContextual();
            areaAjudaContextualAtiva = area;
            final JPopupMenu popup = new JPopupMenu();
            popup.setBorder(BorderFactory.createLineBorder(COR_BORDA_BOTAO));

            JPanel painel = new JPanel();
            painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
            painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            painel.setBackground(COR_SUPERFICIE);

            JLabel cabecalho = new JLabel(localizacao.formatar(
                    "ui.help.header", obterNomeAreaAjudaContextual(area)));
            cabecalho.setFont(cabecalho.getFont().deriveFont(Font.BOLD, 13f));
            cabecalho.setForeground(COR_TEXTO);
            cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
            painel.add(cabecalho);
            painel.add(Box.createVerticalStrut(7));

            final JTextArea resposta = new JTextArea("", 4, 34);
            resposta.setLineWrap(true);
            resposta.setWrapStyleWord(true);
            resposta.setEditable(false);
            resposta.setFocusable(false);
            resposta.setOpaque(true);
            resposta.setBackground(COR_MARCADOR_NUMERO);
            resposta.setForeground(COR_TEXTO);
            resposta.setFont(new Font("Arial", Font.PLAIN, 12));
            resposta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(7, 8, 7, 8)));

            // Espaço que antes só mostrava o texto estático "Escolha uma
            // forma de apoio." agora abre com o checkbox de mídia preferida
            // (mesmas 3 opções do cadastro de usuário) — dá acesso rápido a
            // essa preferência sem sair do "E agora?"; some para dar lugar à
            // mensagem de ajuda assim que uma das 3 opções acima é clicada
            // (decisão da usuária, 2026-07-28, com captura de referência).
            final CardLayout layoutRespostaAjuda = new CardLayout();
            final JPanel painelRespostaAjuda = new JPanel(layoutRespostaAjuda);
            painelRespostaAjuda.setOpaque(false);
            painelRespostaAjuda.setAlignmentX(Component.LEFT_ALIGNMENT);
            painelRespostaAjuda.add(criarPainelMidiaAjudaContextual(), "escolha");
            painelRespostaAjuda.add(resposta, "resposta");
            layoutRespostaAjuda.show(painelRespostaAjuda, "escolha");

            painel.add(criarOpcaoAjudaContextual(
                    area, ScaffoldingAjudaContextual.Intencao.DUVIDA, resposta, painelRespostaAjuda, popup));
            painel.add(Box.createVerticalStrut(3));
            painel.add(criarOpcaoAjudaContextual(
                    area, ScaffoldingAjudaContextual.Intencao.CONTINUAR, resposta, painelRespostaAjuda, popup));
            painel.add(Box.createVerticalStrut(3));
            painel.add(criarOpcaoAjudaContextual(
                    area, ScaffoldingAjudaContextual.Intencao.PROXIMO_PASSO, resposta, painelRespostaAjuda, popup));
            painel.add(Box.createVerticalStrut(8));
            painel.add(painelRespostaAjuda);

            popup.add(painel);
            menuAjudaContextualAtivo = popup;
            popup.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
                public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
                public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
                    if (menuAjudaContextualAtivo == popup) {
                        menuAjudaContextualAtivo = null;
                        areaAjudaContextualAtiva = null;
                    }
                }
                public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
                    if (menuAjudaContextualAtivo == popup) {
                        menuAjudaContextualAtivo = null;
                        areaAjudaContextualAtiva = null;
                    }
                }
            });

            Dimension tamanho = popup.getPreferredSize();
            int deslocamentoXPopup = Math.min(0, botao.getWidth() - tamanho.width);
            popup.show(botao, deslocamentoXPopup, botao.getHeight() + 2);
        }

        private JButton criarOpcaoAjudaContextual(
                final ScaffoldingAjudaContextual.Area area,
                final ScaffoldingAjudaContextual.Intencao intencao,
                final JTextArea resposta,
                final JPanel painelRespostaAjuda,
                final JPopupMenu popup) {
            String textoOpcao = localizacao.texto(scaffoldingAjudaContextual.obterChaveOpcao(intencao));
            final JButton opcao = new JButton(textoOpcao);
            opcao.setHorizontalAlignment(SwingConstants.LEFT);
            opcao.setFont(new Font("Arial", Font.PLAIN, 12));
            opcao.setForeground(COR_TEXTO);
            opcao.setBackground(COR_SUPERFICIE);
            opcao.setOpaque(true);
            opcao.setFocusPainted(false);
            opcao.setBorder(BorderFactory.createEmptyBorder(6, 7, 6, 7));
            opcao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            opcao.setAlignmentX(Component.LEFT_ALIGNMENT);
            opcao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            opcao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    opcao.setBackground(COR_DESTAQUE);
                }
                public void mouseExited(MouseEvent e) {
                    opcao.setBackground(COR_SUPERFICIE);
                }
            });
            opcao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String mensagem = localizacao.texto(
                            scaffoldingAjudaContextual.obterChaveMensagem(area, intencao));
                    resposta.setText(mensagem);
                    resposta.setCaretPosition(0);
                    resposta.getAccessibleContext().setAccessibleDescription(mensagem);
                    ((CardLayout) painelRespostaAjuda.getLayout()).show(painelRespostaAjuda, "resposta");
                    registrarAcaoGranular(
                            "SELECIONAR",
                            "Solicitar ajuda contextual",
                            obterNomeAreaAjudaContextual(area),
                            "MENU_E_AGORA",
                            localizacao.texto(scaffoldingAjudaContextual.obterChaveOpcao(intencao)),
                            "area=" + area.name() + "; intencao=" + intencao.name(),
                            "A orientação contextual da área foi apresentada.");
                    popup.revalidate();
                    popup.repaint();
                }
            });
            return opcao;
        }

        /**
         * Preferência de materialização da ajuda. É uma escolha exclusiva:
         * não decide se haverá ajuda nem sua função pedagógica; apenas indica
         * como uma decisão já fundamentada deve ser apresentada.
         */
        private JPanel criarPainelMidiaAjudaContextual() {
            final String idUsuario = loggerInteracaoGerard.getUsuarioAtual();
            gerard.agente.modelousuario.ModeloUsuario modeloAtual = repositorioModeloUsuario.obter(idUsuario);
            gerard.agente.modelousuario.MidiaPreferida midiaAtual =
                    modeloAtual == null ? null : modeloAtual.getPerfilAprendizagem().getMidiaPreferida();
            if (midiaAtual == null) {
                midiaAtual = gerard.agente.modelousuario.MidiaPreferida.SOM;
            }

            JPanel painelMidia = new JPanel();
            painelMidia.setLayout(new BoxLayout(painelMidia, BoxLayout.Y_AXIS));
            painelMidia.setOpaque(true);
            painelMidia.setBackground(COR_MARCADOR_NUMERO);
            painelMidia.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(7, 8, 7, 8)));

            JLabel rotuloEscolha = new JLabel(localizacao.texto("ui.help.choose"));
            rotuloEscolha.setFont(new Font("Arial", Font.PLAIN, 12));
            rotuloEscolha.setForeground(COR_TEXTO);
            rotuloEscolha.setAlignmentX(Component.LEFT_ALIGNMENT);
            painelMidia.add(rotuloEscolha);
            painelMidia.add(Box.createVerticalStrut(5));

            final JRadioButton opcaoSom = new JRadioButton(
                    localizacao.texto("ui.userDialog.media.som"), midiaAtual == gerard.agente.modelousuario.MidiaPreferida.SOM);
            final JRadioButton opcaoGrafico = new JRadioButton(
                    localizacao.texto("ui.userDialog.media.grafico"), midiaAtual == gerard.agente.modelousuario.MidiaPreferida.GRAFICO);
            final JRadioButton opcaoLinguagemNatural = new JRadioButton(
                    localizacao.texto("ui.userDialog.media.linguagemNatural"),
                    midiaAtual == gerard.agente.modelousuario.MidiaPreferida.LINGUAGEM_NATURAL);
            final JRadioButton opcaoVideo = new JRadioButton(
                    localizacao.texto("ui.userDialog.media.video"), midiaAtual == gerard.agente.modelousuario.MidiaPreferida.VIDEO);
            final JRadioButton opcaoHistoriaEmQuadrinhos = new JRadioButton(
                    localizacao.texto("ui.userDialog.media.historiaEmQuadrinhos"),
                    midiaAtual == gerard.agente.modelousuario.MidiaPreferida.HISTORIA_EM_QUADRINHOS);
            final JRadioButton[] opcoes = {
                opcaoSom,
                opcaoGrafico,
                opcaoLinguagemNatural,
                opcaoVideo,
                opcaoHistoriaEmQuadrinhos
            };
            final gerard.agente.modelousuario.MidiaPreferida[] midiasDasOpcoes = {
                    gerard.agente.modelousuario.MidiaPreferida.SOM,
                    gerard.agente.modelousuario.MidiaPreferida.GRAFICO,
                    gerard.agente.modelousuario.MidiaPreferida.LINGUAGEM_NATURAL,
                    gerard.agente.modelousuario.MidiaPreferida.VIDEO,
                    gerard.agente.modelousuario.MidiaPreferida.HISTORIA_EM_QUADRINHOS};
            ButtonGroup grupoMidia = new ButtonGroup();
            for (JRadioButton opcao : opcoes) {
                grupoMidia.add(opcao);
                opcao.setOpaque(false);
                opcao.setFont(new Font("Arial", Font.PLAIN, 12));
                opcao.setForeground(COR_TEXTO);
                opcao.setFocusPainted(false);
                opcao.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            for (int i = 0; i < opcoes.length; i++) {
                final JRadioButton opcao = opcoes[i];
                final gerard.agente.modelousuario.MidiaPreferida midia = midiasDasOpcoes[i];
                opcao.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        repositorioModeloUsuario.atualizarMidiaPreferida(idUsuario, midia);
                        registrarAcaoGranular(
                                "SELECIONAR",
                                "Ajustar mídia preferida",
                                "Ajuda contextual",
                                "MENU_E_AGORA",
                                opcao.getText(),
                                "midia=" + midia.name(),
                                "A mídia preferida do usuário foi atualizada.");
                    }
                });
            }

            for (JRadioButton opcao : opcoes) {
                painelMidia.add(opcao);
            }
            return painelMidia;
        }

        private void fecharMenuAjudaContextual() {
            if (menuAjudaContextualAtivo != null) {
                menuAjudaContextualAtivo.setVisible(false);
                menuAjudaContextualAtivo = null;
            }
            areaAjudaContextualAtiva = null;
        }


        /**
         * Barra de menu clássica estilo Windows (Arquivo/Exibir/Ferramentas/
         * Ajuda) — substitui a antiga criarMenusInternos() (JPanels de popup
         * posicionados/mostrados/ocultados à mão). JMenuBar/JMenu/JMenuItem
         * do Swing já resolvem nativamente abrir ao passar o mouse, fechar
         * ao clicar fora, submenus aninhados e navegação por teclado — nada
         * disso precisa ser reimplementado aqui.
         *
         * Reconstrói tudo do zero a cada chamada (removeAll + reconstrói),
         * igual ao comportamento já existente de criarMenusInternos() —
         * mesmo padrão, só que aplicado a um JMenuBar real. Chamado uma vez
         * no construtor (primeira montagem) e depois via
         * atualizarTextosFixosDaInterface() sempre que idioma/categoria/
         * usuário/interpretação mudam.
         */
        private void criarMenuPrincipal() {
            if (menuBarPrincipal == null) {
                menuBarPrincipal = new JMenuBar();
            }
            menuBarPrincipal.removeAll();
            // Paleta neutra própria (não UIManager.put global): o L&F
            // nativo do Windows tende a ignorar overrides de UIManager para
            // cor de seleção/hover de menu, mas pintar diretamente as
            // propriedades do próprio componente funciona independente do
            // L&F — mesmo raciocínio já usado em estilizarMenuPopup/
            // estilizarItemMenuPopup, reaproveitados abaixo.
            menuBarPrincipal.setBackground(COR_SUPERFICIE);
            menuBarPrincipal.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA));

            menuBarPrincipal.add(criarMenuArquivo());
            menuBarPrincipal.add(criarMenuExibir());
            menuBarPrincipal.add(criarMenuFerramentas());
            menuBarPrincipal.add(criarMenuAjuda());
            // Comunidade, Site e Reportar bug ficam como ícones no canto
            // superior direito da barra, fora dos menus (decisão da usuária,
            // 2026-07-28).
            menuBarPrincipal.add(criarBotaoIconeMenuBar(
                    criarIconeComunidade(), localizacao.texto("ui.tooltip.comunidade"),
                    new Runnable() {
                        public void run() {
                            abrirLinkExterno(URL_COMUNIDADE_GERARD);
                        }
                    }));
            menuBarPrincipal.add(criarBotaoIconeMenuBar(
                    criarIconeSite(), localizacao.texto("ui.tooltip.site"),
                    new Runnable() {
                        public void run() {
                            abrirLinkExterno(URL_SITE_GERARD);
                        }
                    }));
            menuBarPrincipal.add(criarBotaoReportarBugMenuBar());
            // Estado logado/deslogado no canto direito da barra (decisão da
            // usuária, 2026-07-28, com captura de referência) — substitui o
            // antigo item "Usuário" dentro do menu Ferramentas.
            menuBarPrincipal.add(criarBotaoUsuarioMenuBar());

            atualizarEstadoItensMenuPorAba();
            menuBarPrincipal.revalidate();
            menuBarPrincipal.repaint();
        }

        /** Botão de ícone genérico direto na barra de menu (fora de qualquer JMenu) — mesmo padrão de criarBotaoReportarBugMenuBar. */
        private JButton criarBotaoIconeMenuBar(Icon icone, String tooltip, final Runnable acao) {
            JButton botao = new JButton(icone);
            botao.setFocusable(false);
            botao.setOpaque(false);
            botao.setContentAreaFilled(false);
            botao.setBorderPainted(false);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.setToolTipText(tooltip);
            botao.getAccessibleContext().setAccessibleDescription(tooltip);
            botao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    acao.run();
                }
            });
            return botao;
        }

        /** Duas silhuetas de pessoa (comunidade) — mesmo traço fino neutro dos outros ícones, ver prepararTracoIconeCategoria. */
        private Icon criarIconeComunidade() {
            final int tamanho = 22;
            return new Icon() {
                public int getIconWidth() { return tamanho; }
                public int getIconHeight() { return tamanho; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        desenharPessoaIconeComunidade(g2, x + 6, y + 4);
                        desenharPessoaIconeComunidade(g2, x + 13, y + 6);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        private void desenharPessoaIconeComunidade(Graphics2D g2, int cx, int topoCabeca) {
            int diametroCabeca = 6;
            g2.draw(new java.awt.geom.Ellipse2D.Float(cx - diametroCabeca / 2f, topoCabeca, diametroCabeca, diametroCabeca));
            java.awt.geom.Arc2D.Float corpo = new java.awt.geom.Arc2D.Float(
                    cx - 6, topoCabeca + diametroCabeca - 1, 12, 11, 15, 150, java.awt.geom.Arc2D.OPEN);
            g2.draw(corpo);
        }

        /** Globo (site) — círculo com meridiano e paralelo, mesmo traço fino neutro dos outros ícones. */
        private Icon criarIconeSite() {
            final int tamanho = 22;
            return new Icon() {
                public int getIconWidth() { return tamanho; }
                public int getIconHeight() { return tamanho; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        int diametro = tamanho - 4;
                        float ox = x + 2;
                        float oy = y + 2;
                        g2.draw(new java.awt.geom.Ellipse2D.Float(ox, oy, diametro, diametro));
                        g2.draw(new java.awt.geom.Line2D.Float(ox, oy + diametro / 2f, ox + diametro, oy + diametro / 2f));
                        g2.draw(new java.awt.geom.Ellipse2D.Float(ox + diametro * 0.28f, oy, diametro * 0.44f, diametro));
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /**
         * Estado logado (ícone de pessoa + nome + seta) ou deslogado
         * ("Entrar") no canto direito da barra — mesma ação de antes
         * (abre DialogoUsuario), só que sempre visível na barra em vez de
         * dentro do menu Ferramentas. Dimensionado pro tamanho natural da
         * barra (sem bounds fixos): fonte pequena, ícone de 16px, padding
         * enxuto — decisão da usuária, 2026-07-28.
         */
        private JButton criarBotaoUsuarioMenuBar() {
            boolean logado = usuarioLogado();
            final JButton botao = new JButton();
            botao.setFocusable(false);
            botao.setOpaque(true);
            botao.setBackground(COR_SUPERFICIE);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA_BOTAO, 1),
                    BorderFactory.createEmptyBorder(3, 10, 3, 10)));
            botao.setFont(new Font("Arial", Font.PLAIN, 12));
            botao.setForeground(COR_TEXTO);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.setIconTextGap(6);
            if (logado) {
                gerard.agente.modelousuario.ModeloUsuario meuPerfilBotao =
                        repositorioModeloUsuario.obter(loggerInteracaoGerard.getUsuarioAtual());
                botao.setIcon(criarIconePessoaMenuBar(
                        meuPerfilBotao == null ? null : meuPerfilBotao.getPerfilAluno().getFotoCaminho()));
                botao.setText(textoBotaoUsuario() + "  ▼");
            } else {
                botao.setIcon(null);
                botao.setText(localizacao.texto("ui.userDialog.enter"));
            }
            botao.setToolTipText(localizacao.texto("ui.tooltip.user"));
            botao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    botao.setBackground(COR_DESTAQUE);
                }
                public void mouseExited(MouseEvent e) {
                    botao.setBackground(COR_SUPERFICIE);
                }
            });
            botao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    java.awt.Window janela = SwingUtilities.getWindowAncestor(TelaGerard.this);
                    Frame proprietario = janela instanceof Frame ? (Frame) janela : null;
                    if (usuarioLogado()) {
                        gerard.agente.modelousuario.ModeloUsuario meuPerfil =
                                repositorioModeloUsuario.obter(loggerInteracaoGerard.getUsuarioAtual());
                        gerard.ui.usuario.DialogoUsuario dialogoEdicao = new gerard.ui.usuario.DialogoUsuario(
                                proprietario, repositorioModeloUsuario, meuPerfil);
                        dialogoEdicao.mostrarESelecionar();
                        criarMenuPrincipal();
                    } else {
                        gerard.ui.usuario.DialogoUsuario dialogo = new gerard.ui.usuario.DialogoUsuario(
                                proprietario, repositorioModeloUsuario);
                        String idEscolhido = dialogo.mostrarESelecionar();
                        if (idEscolhido != null) {
                            if (sessaoAdaptativaUsuario.fotografiaAtual().isPresent()
                                    && !idEscolhido.equals(sessaoAdaptativaUsuario
                                            .fotografiaAtual().get().getUsuarioId())) {
                                // Trocar o perfil ativo constitui uma nova
                                // sessão: descarta a fotografia anterior antes
                                // de carregar a do novo login.
                                sessaoAdaptativaUsuario.encerrarNoLogout();
                            }
                            sessaoAdaptativaUsuario.iniciarNoLogin(idEscolhido);
                            atualizarContextoAdaptativoIncognitaAtual();
                            loggerInteracaoGerard.definirUsuario(idEscolhido);
                            criarMenuPrincipal();
                        }
                    }
                    requestFocusInWindow();
                }
            });
            return botao;
        }

        private boolean usuarioLogado() {
            gerard.agente.modelousuario.ModeloUsuario modelo =
                    repositorioModeloUsuario.obter(loggerInteracaoGerard.getUsuarioAtual());
            return modelo != null && modelo.getPerfilAluno().getNome() != null;
        }

        /**
         * Foto do usuário recortada em círculo, se cadastrada (ver
         * PerfilAluno.getFotoCaminho()); sem foto, cai na silhueta genérica
         * (círculo + cabeça + ombros, mesmo traço fino neutro dos outros
         * ícones, ver prepararTracoIconeCategoria) — pedido da usuária,
         * 2026-07-30.
         */
        private Icon criarIconePessoaMenuBar(String caminhoFoto) {
            final int tamanho = 16;
            if (caminhoFoto != null) {
                try {
                    java.awt.Image imagemOriginal = javax.imageio.ImageIO.read(new java.io.File(caminhoFoto));
                    if (imagemOriginal != null) {
                        final BufferedImage circular = recortarImagemCircular(imagemOriginal, tamanho);
                        return new Icon() {
                            public int getIconWidth() { return tamanho; }
                            public int getIconHeight() { return tamanho; }

                            public void paintIcon(Component c, Graphics g, int x, int y) {
                                g.drawImage(circular, x, y, null);
                            }
                        };
                    }
                } catch (java.io.IOException ex) {
                    // Falha ao ler a foto cai na silhueta genérica abaixo.
                }
            }
            return new Icon() {
                public int getIconWidth() { return tamanho; }
                public int getIconHeight() { return tamanho; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        g2.draw(new java.awt.geom.Ellipse2D.Float(x + 1, y + 1, tamanho - 2, tamanho - 2));
                        int cabecaDiam = tamanho / 3;
                        g2.draw(new java.awt.geom.Ellipse2D.Float(
                                x + tamanho / 2f - cabecaDiam / 2f, y + tamanho * 0.28f, cabecaDiam, cabecaDiam));
                        java.awt.geom.Arc2D.Float ombros = new java.awt.geom.Arc2D.Float(
                                x + tamanho * 0.2f, y + tamanho * 0.55f, tamanho * 0.6f, tamanho * 0.55f,
                                20, 140, java.awt.geom.Arc2D.OPEN);
                        g2.draw(ombros);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /** Recorta origem num círculo de tamanho x tamanho, esticando pra preencher (mesmo comportamento de crop de rotuloPreviewFoto em DialogoUsuario). */
        private BufferedImage recortarImagemCircular(Image origem, int tamanho) {
            BufferedImage quadro = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = quadro.createGraphics();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, tamanho, tamanho));
                g2.drawImage(origem, 0, 0, tamanho, tamanho, null);
            } finally {
                g2.dispose();
            }
            return quadro;
        }

        private JButton criarBotaoReportarBugMenuBar() {
            JButton botao = new JButton(criarIconeReportarBug());
            botao.setFocusable(false);
            botao.setOpaque(false);
            botao.setContentAreaFilled(false);
            botao.setBorderPainted(false);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.setToolTipText(localizacao.texto("ui.tooltip.reportBug"));
            botao.getAccessibleContext().setAccessibleDescription(localizacao.texto("ui.tooltip.reportBug"));
            botao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarDialogoRelatoBug();
                }
            });
            return botao;
        }

        /** Silhueta simples de inseto (corpo, cabeça, antenas, 3 pares de pernas) — mesmo traço fino neutro dos outros ícones, ver prepararTracoIconeCategoria. */
        private Icon criarIconeReportarBug() {
            final int tamanho = 22;
            return new Icon() {
                public int getIconWidth() { return tamanho; }
                public int getIconHeight() { return tamanho; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        int corpoLargura = 10;
                        int corpoAltura = 12;
                        int cx = x + tamanho / 2;
                        int topoCorpo = y + 7;
                        g2.draw(new java.awt.geom.Ellipse2D.Float(cx - corpoLargura / 2f, topoCorpo, corpoLargura, corpoAltura));

                        int cabecaDiametro = 5;
                        g2.draw(new java.awt.geom.Ellipse2D.Float(cx - cabecaDiametro / 2f, topoCorpo - cabecaDiametro + 1, cabecaDiametro, cabecaDiametro));

                        g2.drawLine(cx - 1, topoCorpo - cabecaDiametro + 1, cx - 3, y + 1);
                        g2.drawLine(cx + 1, topoCorpo - cabecaDiametro + 1, cx + 3, y + 1);

                        int[] deslocamentosY = {topoCorpo + 2, topoCorpo + 6, topoCorpo + 10};
                        for (int dy : deslocamentosY) {
                            g2.drawLine(cx - corpoLargura / 2, dy, cx - corpoLargura / 2 - 4, dy - 2);
                            g2.drawLine(cx - corpoLargura / 2, dy, cx - corpoLargura / 2 - 4, dy + 2);
                            g2.drawLine(cx + corpoLargura / 2, dy, cx + corpoLargura / 2 + 4, dy - 2);
                            g2.drawLine(cx + corpoLargura / 2, dy, cx + corpoLargura / 2 + 4, dy + 2);
                        }
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /**
         * Botão de ícone embutido no cabeçalho da aba Diagramar (decisão da
         * usuária, 2026-07-28): "Comparar categorias" (mesma função do item
         * de menu Arquivo > Comparar categorias — reaproveita
         * ConfiguradorOpcaoComparacaoCategorias/abrirTelaComparacaoCategorias),
         * seguido do indicador factual do Modelador. Os dois botões de Sortear
         * (Medidas/Relações) que ficavam aqui, ao lado deste, se mudaram em
         * 2026-08-07 para dentro do próprio painel de ícones de categoria
         * (criarPainelAtalhoCategoria/reposicionarPainelAtalhoCategoria) —
         * a pedido da usuária, cada botão de sorteio agora fica colado no
         * grupo que ele sorteia, em vez dos dois juntos aqui no topo.
         */
        private void criarBotoesCabecalhoEmbutidos() {
            if (botaoCompararCategorias == null) {
                botaoCompararCategorias = criarBotaoIconeCabecalho(criarIconeCompararCategorias());
                ConfiguradorOpcaoComparacaoCategorias.configurar(
                        botaoCompararCategorias,
                        new Runnable() {
                            public void run() {
                                abrirTelaComparacaoCategorias();
                            }
                        });
                botaoCompararCategorias.setBounds(16, 8, 34, 34);
                add(botaoCompararCategorias);
                setComponentZOrder(botaoCompararCategorias, 0);
            }
            // ConfiguradorOpcaoComparacaoCategorias.configurar zera o tooltip
            // (opcao.setToolTipText(null)) — precisa ser setado depois dele.
            botaoCompararCategorias.setToolTipText(localizacao.texto("ui.menu.category.compare.tooltip"));
        }

        /**
         * Caixa quadrada com borda fina neutra ao redor do ícone (decisão da
         * usuária, 2026-07-28, com captura de referência) — mesmo estilo já
         * usado nos 6 ícones de atalho de categoria (criarBotaoAtalhoCategoria),
         * inclusive o destaque de fundo ao passar o mouse.
         */
        private JButton criarBotaoIconeCabecalho(Icon icone) {
            final JButton botao = new JButton(icone);
            botao.setFocusable(false);
            botao.setOpaque(true);
            botao.setBackground(COR_SUPERFICIE);
            botao.setBorder(BorderFactory.createLineBorder(COR_BORDA_BOTAO, 1));
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    botao.setBackground(COR_DESTAQUE);
                }
                public void mouseExited(MouseEvent e) {
                    botao.setBackground(COR_SUPERFICIE);
                }
            });
            return botao;
        }

        /** Duas caixas com seta dupla entre elas — mesmo traço fino neutro dos ícones de categoria, ver prepararTracoIconeCategoria. */
        private Icon criarIconeCompararCategorias() {
            final int tamanho = 30;
            return new Icon() {
                public int getIconWidth() { return tamanho; }
                public int getIconHeight() { return tamanho; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        int ladoCaixa = 9;
                        int yCaixa = y + 10;
                        g2.drawRoundRect(x + 3, yCaixa, ladoCaixa, ladoCaixa, 2, 2);
                        g2.drawRoundRect(x + tamanho - 3 - ladoCaixa, yCaixa, ladoCaixa, ladoCaixa, 2, 2);

                        int yFlecha = y + tamanho / 2;
                        int xEsq = x + 3 + ladoCaixa + 2;
                        int xDir = x + tamanho - 3 - ladoCaixa - 2;
                        g2.drawLine(xEsq, yFlecha, xDir, yFlecha);
                        g2.drawLine(xEsq, yFlecha, xEsq + 4, yFlecha - 3);
                        g2.drawLine(xEsq, yFlecha, xEsq + 4, yFlecha + 3);
                        g2.drawLine(xDir, yFlecha, xDir - 4, yFlecha - 3);
                        g2.drawLine(xDir, yFlecha, xDir - 4, yFlecha + 3);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        /**
         * Dado simples (face "3"), mesmo traço fino neutro dos ícones de
         * categoria — ver prepararTracoIconeCategoria. Ícone compartilhado
         * pelos dois botões de sorteio (Medidas/Relações, divididos em
         * 2026-08-07); desde que cada um passou a ficar colado no seu
         * próprio grupo de ícones (criarPainelAtalhoCategoria), a posição e
         * o rótulo "Medidas"/"Relações" logo acima já diferenciam os dois —
         * não precisa mais de um distintivo de letra no próprio ícone (tinha
         * quando os dois ainda ficavam juntos no cabeçalho).
         */
        private Icon criarIconeFerramentaSortear() {
            final int tamanho = 26;
            return new Icon() {
                public int getIconWidth() { return tamanho; }
                public int getIconHeight() { return tamanho; }

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = prepararTracoIconeCategoria(g);
                    try {
                        g2.drawRoundRect(x + 2, y + 2, tamanho - 4, tamanho - 4, 6, 6);
                        int raioPonto = 2;
                        desenharPontoIconeFerramentaSortear(g2, x + 8, y + 8, raioPonto);
                        desenharPontoIconeFerramentaSortear(g2, x + tamanho / 2, y + tamanho / 2, raioPonto);
                        desenharPontoIconeFerramentaSortear(g2, x + tamanho - 8, y + tamanho - 8, raioPonto);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        private void desenharPontoIconeFerramentaSortear(Graphics2D g2, int cx, int cy, int raio) {
            g2.fill(new java.awt.geom.Ellipse2D.Float(cx - raio, cy - raio, raio * 2, raio * 2));
        }

        private JMenu criarMenuArquivo() {
            JMenu menu = new JMenu(localizacao.texto("ui.menu.top.file"));
            menu.setMnemonic(KeyEvent.VK_A);
            estilizarItemMenuPopup(menu);
            estilizarMenuPopup(menu.getPopupMenu());

            itemNovaSituacao = new JMenuItem(localizacao.texto("ui.button.random"));
            estilizarItemMenuPopup(itemNovaSituacao);
            itemNovaSituacao.setToolTipText(localizacao.texto("ui.tooltip.random"));
            itemNovaSituacao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sortearNovaSituacao();
                }
            });
            menu.add(itemNovaSituacao);

            menuCategoria = criarMenuCategoria();
            menu.add(menuCategoria);

            menu.addSeparator();

            JMenuItem itemComparar = new JMenuItem(localizacao.texto("ui.menu.category.compare"));
            estilizarItemMenuPopup(itemComparar);
            itemComparar.setToolTipText(localizacao.texto("ui.menu.category.compare.tooltip"));
            ConfiguradorOpcaoComparacaoCategorias.configurar(
                    itemComparar,
                    new Runnable() {
                        public void run() {
                            abrirTelaComparacaoCategorias();
                        }
                    });
            menu.add(itemComparar);

            return menu;
        }

        private JMenu criarMenuCategoria() {
            JMenu menu = new JMenu(localizacao.texto("ui.button.category"));
            estilizarItemMenuPopup(menu);
            estilizarMenuPopup(menu.getPopupMenu());
            menu.setToolTipText(localizacao.texto("ui.tooltip.type"));

            JMenu menuMedidas = new JMenu(localizacao.texto("ui.menu.category.measures"));
            estilizarItemMenuPopup(menuMedidas);
            estilizarMenuPopup(menuMedidas.getPopupMenu());
            menuMedidas.add(criarItemCategoria(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS));
            menuMedidas.add(criarItemCategoria(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS));
            menuMedidas.add(criarItemCategoria(TipoSituacaoAditiva.COMPARACAO_MEDIDAS));
            menu.add(menuMedidas);

            // Grupo "Transformações compostas": misto de propósito.
            // A composição de transformações é a única categoria deste grupo
            // no modelo canônico de seis categorias.
            JMenu menuTransformacoes = new JMenu(localizacao.texto("ui.menu.category.transformations"));
            estilizarItemMenuPopup(menuTransformacoes);
            estilizarMenuPopup(menuTransformacoes.getPopupMenu());
            menuTransformacoes.add(criarItemCategoria(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES));
            menu.add(menuTransformacoes);

            // Grupo "Relações": os dois itens já são abertos pelos ícones de
            // atalho — grupo inteiro habilitado (não é mais "Em construção").
            JMenu menuRelacoes = new JMenu(localizacao.texto("ui.menu.category.relations"));
            estilizarItemMenuPopup(menuRelacoes);
            estilizarMenuPopup(menuRelacoes.getPopupMenu());
            menuRelacoes.add(criarItemCategoria(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO));
            menuRelacoes.add(criarItemCategoria(TipoSituacaoAditiva.COMPOSICAO_RELACOES));
            menu.add(menuRelacoes);

            return menu;
        }

        private JMenuItem criarItemCategoria(final TipoSituacaoAditiva tipo) {
            JMenuItem item = new JMenuItem(localizacao.rotuloBotaoTipo(tipo));
            estilizarItemMenuPopup(item);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selecionarCategoria(tipo);
                }
            });
            return item;
        }

        private JMenuItem criarItemCategoriaEmConstrucao(TipoSituacaoAditiva tipo) {
            JMenuItem item = new JMenuItem(localizacao.rotuloBotaoTipo(tipo));
            estilizarItemMenuPopup(item);
            item.setEnabled(false);
            item.setToolTipText(localizacao.texto("ui.menu.underConstruction"));
            return item;
        }

        /**
         * Corpo original do listener de adicionarOpcaoCategoria, extraído
         * para ser reaproveitado pelos ícones de atalho de categoria
         * (criarPainelAtalhoCategoria) — mesmo comportamento, um único
         * ponto de entrada para "selecionar esta categoria".
         */
        private void selecionarCategoria(TipoSituacaoAditiva tipo) {
            registrarLogUsuario(
                    "Selecionar a legenda correspondente à operação",
                    "-",
                    "Menu/barra de legendas",
                    "Botão " + localizacao.rotuloBotaoTipo(tipo),
                    "Representar a estrutura escolhida para o problema",
                    "OBJ8",
                    "O sujeito deve selecionar a legenda adequada ao tipo de problema.",
                    "MENU_CATEGORIA",
                    "categoriaSelecionada=" + tipo.name()
            );
            tipoSituacaoSelecionada = tipo;
            categoriaSelecionadaParaAtividade = true;
            // Navegação direta pelo menu Categoria cancela qualquer
            // adivinhação pendente do Sortear (usuária confirmou em
            // 2026-07-28: só o ícone de atalho valida a adivinhação).
            aguardandoAdivinhacaoCategoria = false;
            categoriaSorteioOculta = null;
            if (itemNovaSituacao != null) {
                itemNovaSituacao.setEnabled(true);
            }
            iniciarNovaAtividade(AcaoAtividade.SELECIONAR_CATEGORIA);
        }

        /**
         * Categorias do grupo "Medidas" que entram no sorteio restrito a
         * este grupo (botaoFerramentaSortearMedidas) — mesmos 3 tipos que já
         * formavam a metade "Medidas" de CATEGORIAS_SORTEIO_LIVRE.
         */
        /**
         * Categorias do grupo "Relações" que entram no sorteio restrito a
         * este grupo (botaoFerramentaSortearRelacoes) — mesmos 3 tipos que
         * já formavam a metade "Relações" de CATEGORIAS_SORTEIO_LIVRE
         * (inclui COMPOSICAO_TRANSFORMACOES, que vive no grupo de menu
         * "Transformações" mas sempre esteve agrupado com Relações nos
         * ícones de atalho — ver criarPainelAtalhoCategoria).
         */
        /**
         * União das duas listas acima — mantida só para o item de menu
         * Arquivo > Nova situação-problema (itemNovaSituacao), que continua
         * sorteando entre as 6 categorias (comportamento inalterado desde
         * 2026-08-07). Os dois botões de ícone do cabeçalho
         * (botaoFerramentaSortearMedidas/Relacoes, divididos em 2026-08-07
         * a pedido da usuária) usam as listas restritas acima, não esta.
         * O domínio possui exatamente as seis categorias destas duas listas.
         */
        private final java.util.Random sorteioCategoriaLivre = new java.util.Random();
        private final PoliticaSorteioSituacoesAditivas politicaSorteioSituacoes =
                new PoliticaSorteioSituacoesAditivas();

        /**
         * Ação de "Nova situação-problema" (Arquivo) — decisão da usuária em
         * 2026-07-28: fica habilitada desde o início, sem exigir que uma
         * categoria já tenha sido escolhida antes pelo menu Categoria. Cada
         * clique sorteia também a categoria (dentro de
         * CATEGORIAS_SORTEIO_LIVRE, as 6 categorias), não só a situação
         * dentro da categoria já fixada — diferente de
         * selecionarCategoria(tipo), que fixa uma categoria específica
         * escolhida manualmente.
         */
        private void sortearNovaSituacao() {
            sortearDentroDoGrupo(Grupo.TODAS, "Item de menu Nova situação-problema");
        }

        /**
         * Botão de ícone "Sortear Medidas" do cabeçalho — sorteia só entre
         * as 3 categorias de Medidas (CATEGORIAS_SORTEIO_MEDIDAS). Dividido
         * do antigo botão único "Sortear" em 2026-08-07, a pedido da
         * usuária, para permitir treinar um grupo por vez em vez de sempre
         * sortear entre as 6 categorias.
         */
        private void sortearSituacaoMedidas() {
            sortearDentroDoGrupo(Grupo.MEDIDAS, "Ícone Sortear Medidas");
        }

        /**
         * Botão de ícone "Sortear Relações" do cabeçalho — sorteia só entre
         * as 3 categorias de Relações (CATEGORIAS_SORTEIO_RELACOES). Ver
         * sortearSituacaoMedidas.
         */
        private void sortearSituacaoRelacoes() {
            sortearDentroDoGrupo(Grupo.RELACOES, "Ícone Sortear Relações");
        }

        /**
         * Corpo comum aos 3 pontos de entrada de sorteio acima — só o
         * grupo de categorias candidatas e a descrição do elemento clicado
         * (para o log de interação) mudam entre eles.
         */
        private void sortearDentroDoGrupo(Grupo grupo, String descricaoElemento) {
            TipoSituacaoAditiva tipoSorteado =
                    politicaSorteioSituacoes.sortearCategoria(grupo, sorteioCategoriaLivre);
            registrarLogUsuario(
                    "Sortear uma nova situação-problema, incluindo a categoria",
                    "-",
                    "Menu/barra de legendas",
                    descricaoElemento,
                    "Representar a estrutura escolhida para o problema",
                    "OBJ8",
                    "O sistema sorteia a categoria entre as disponíveis quando o sujeito pede uma nova situação sem fixar uma categoria específica.",
                    "SORTEAR_CATEGORIA",
                    "categoriaSorteada=" + tipoSorteado.name()
            );
            iniciarQuizCategoria(tipoSorteado);
        }

        /**
         * Carrega o enunciado de uma situação da categoria sorteada
         * (tipoSecreto) SEM revelar a categoria: categoriaSelecionadaParaAtividade
         * fica false, então todos os pontos que já ocultam diagramas/botões/
         * a sigla da categoria no cabeçalho nesse campo (o mesmo estado "sem
         * categoria" usado desde a inicialização do app) continuam ocultando
         * tudo que pertence à categoria — só o enunciado é exibido por cima
         * disso (ver desenharTextoProblemaAdivinhacao). A categoria real só
         * é revelada em confirmarCategoriaAdivinhada, quando o usuário acerta
         * o ícone de atalho correspondente.
         */
        private void iniciarQuizCategoria(TipoSituacaoAditiva tipoSecreto) {
            inicializarTelaSemCategoria();
            controladorEstadoAtividade.registrar(AcaoAtividade.SORTEAR);
            categoriaSorteioOculta = tipoSecreto;
            aguardandoAdivinhacaoCategoria = true;

            ContextoCarregamentoAtividade contexto = fachadaCarregamentoAtividade.carregarNova(
                    idiomaSelecionado, tipoSecreto);
            SituacaoProblemaAditiva situacao = contexto.getSituacao();
            boolean situacaoCuradaDisponivel = contexto.possuiSituacaoExibivel();
            situacaoProblemaAtual = situacaoCuradaDisponivel ? situacao : null;
            tentativaClassificacaoCategoriaAtual = situacaoCuradaDisponivel
                    ? new TentativaClassificacaoCategoriaAditiva(situacaoProblemaAtual)
                    : null;
            textoProblemaEhMensagemSistema = !situacaoCuradaDisponivel;
            textoProblema = situacaoCuradaDisponivel
                    ? materializadorEnunciadoCurado.materializar(situacao)
                    : textoAusenciaSituacaoCurada();

            atualizarHabilitacaoIconesAtalhoCategoria();
            repaint();
        }

        /**
         * Os 6 ícones de atalho de categoria só têm função enquanto há uma
         * situação-problema curada renderizada E a categoria ainda não foi
         * adivinhada (decisão da usuária, 2026-07-28) — diferente do menu
         * Categoria, que continua livre a qualquer momento. Depois da escolha
         * correta, os ícones perdem a função (mesmo com a situação ainda em
         * tela) e só voltam a habilitar num novo ciclo iniciado pelo Sortear
         * (iniciarQuizCategoria). Chamado nos pontos de transição de estado
         * (iniciarQuizCategoria, finalizarCarregamentoSituacao,
         * inicializarTelaSemCategoria) em vez de a cada repintura, pelo
         * mesmo motivo que os outros botões contextuais (reposicionarBotaoX)
         * já são atualizados nesses pontos.
         */
        private void atualizarHabilitacaoIconesAtalhoCategoria() {
            boolean habilitar = situacaoProblemaAtual != null && aguardandoAdivinhacaoCategoria;
            if (botaoAtalhoComposicao != null) botaoAtalhoComposicao.setEnabled(habilitar);
            if (botaoAtalhoTransformacao != null) botaoAtalhoTransformacao.setEnabled(habilitar);
            if (botaoAtalhoComparacao != null) botaoAtalhoComparacao.setEnabled(habilitar);
            // Os 3 ícones do grupo "Relações" voltaram a participar do quiz
            // de adivinhação em 2026-08-07 — reincluídas em
            // CATEGORIAS_SORTEIO_LIVRE, então voltam a ser respostas certas
            // possíveis, mesmo critério de habilitação das categorias de
            // Medidas. Antes (decisão de 2026-07-28) ficavam sempre
            // desabilitadas porque o sorteio era restrito a "Medidas" e
            // habilitá-las seria enganoso — essa restrição foi revertida.
            if (botaoAtalhoComposicaoTransformacoes != null) botaoAtalhoComposicaoTransformacoes.setEnabled(habilitar);
            if (botaoAtalhoTransformacaoRelacao != null) botaoAtalhoTransformacaoRelacao.setEnabled(habilitar);
            if (botaoAtalhoComposicaoRelacoes != null) botaoAtalhoComposicaoRelacoes.setEnabled(habilitar);
        }

        /**
         * Ponto de entrada único dos 6 ícones de atalho de categoria
         * (criarBotaoAtalhoCategoria). Fora do modo de adivinhação, se
         * comporta como sempre (seleção direta, selecionarCategoria). Durante
         * uma adivinhação pendente (Sortear), valida o clique contra a
         * categoria secreta em vez de carregar uma situação nova — usuária
         * confirmou em 2026-07-28 que só o ícone participa dessa validação
         * (o menu Categoria continua sendo navegação livre).
         *
         * Primeira ação avaliável do usuário, antes de qualquer
         * posicionamento no diagrama — o erro de categorização é
         * fundamental para a continuidade da modelagem (o diálogo em
         * mostrarQuestionamentoCategoriaErrada impede prosseguir até
         * acertar, e o erro pode se repetir várias vezes seguidas, como nos
         * diários de 2010. Captura a categoria real
         * antes de chamar confirmarCategoriaAdivinhada, que zera
         * categoriaSorteioOculta.
         */
        private void clicarAtalhoCategoria(TipoSituacaoAditiva tipo) {
            if (aguardandoAdivinhacaoCategoria) {
                if (tentativaClassificacaoCategoriaAtual == null) {
                    return;
                }
                ContextoAcaoInstrumental contextoInstrumental =
                        new ContextoAcaoInstrumental(
                                "Adivinhar a categoria da situação-problema sorteada",
                                "Selecionar um ícone de categoria",
                                "Faixa de ícones de categoria",
                                "Classificar a estrutura da situação-problema",
                                TentativaClassificacaoCategoriaAditiva.ALVO_ESCOLHA,
                                "SELECAO_CATEGORIA",
                                "categoria_escolhida=" + tipo.name(),
                                "Categoria selecionada para validação",
                                Collections.<String>emptyList());
                RegistroAcaoClassificacaoCategoria registro =
                        tentativaClassificacaoCategoriaAtual.avaliarEscolha(
                                tipo, contextoInstrumental);
                loggerInteracaoGerard.registrarAcaoInstrumentalUsuario(registro);
                conectorVereditoModelador.registrarAcaoInstrumental(
                        loggerInteracaoGerard.getUsuarioAtual(), registro,
                        gerard.agente.modelousuario.NivelSuporte.NENHUM,
                        registro.getActionId());
                if (registro.getDesfecho()
                        == RegistroAcaoClassificacaoCategoria.Desfecho.ACEITAR_CATEGORIA) {
                    confirmarCategoriaAdivinhada(tipo);
                } else if (registro.getDesfecho()
                        == RegistroAcaoClassificacaoCategoria.Desfecho
                                .REEXPLICAR_CATEGORIA_APOS_LIMITE) {
                    acionarTimeoutCategoria(registro.getCategoriaEsperada());
                } else {
                    mostrarQuestionamentoCategoriaErrada(tipo);
                }
                return;
            }
            selecionarCategoria(tipo);
        }

        /**
         * O usuário acertou o ícone da categoria sorteada. situacaoProblemaAtual/
         * textoProblema já foram carregados por iniciarQuizCategoria — só falta
         * montar a definição do diagrama e a interpretação curada (o mesmo par
         * de atribuições que aplicarIdiomaSelecionado faz para o fluxo normal)
         * e rodar a cauda comum de inicialização de diagrama/texto.
         */
        private void confirmarCategoriaAdivinhada(TipoSituacaoAditiva tipo) {
            tipoSituacaoSelecionada = tipo;
            categoriaSelecionadaParaAtividade = true;
            aguardandoAdivinhacaoCategoria = false;
            categoriaSorteioOculta = null;
            controladorContextoSituacao.registrarNovaSituacao(
                    situacaoProblemaAtual, tipo.name(), textoProblema);
            definicaoDiagramaAtual = SemanticaCuradaSituacao.aplicarRotulos(
                    catalogoDefinicoesAditivas.obter(tipo), situacaoProblemaAtual, localizacao);
            resultadoInterpretacao = construtorResultadoCurado.construir(situacaoProblemaAtual, textoProblema);
            if (itemNovaSituacao != null) {
                itemNovaSituacao.setEnabled(true);
            }
            finalizarCarregamentoSituacao();
        }

        private void mostrarQuestionamentoCategoriaErrada(TipoSituacaoAditiva tipo) {
            String pergunta = localizacao.texto("ui.question.category." + tipo.name().toLowerCase());
            mostrarDialogoConfirmacaoSimNao(pergunta, new Runnable() {
                public void run() {
                    avaliarRespostaConfirmacaoCategoriaErrada(true);
                }
            }, new Runnable() {
                public void run() {
                    avaliarRespostaConfirmacaoCategoriaErrada(false);
                }
            });
        }

        /**
         * Segundo tipo de erro consecutivo revelado pelo diálogo de
         * confirmação (relatado pela usuária, 2026-07-30): a pessoa pode
         * insistir dizendo "Sim", concordando que a definição da categoria
         * ERRADA (a que ela clicou) se aplica à situação-problema — sinal
         * diferente de simplesmente repetir o clique no ícone errado. "Sim"
         * = insiste no erro; "Não" = reconhece corretamente que a definição
         * errada não se aplica.
         */
        private void avaliarRespostaConfirmacaoCategoriaErrada(boolean concordou) {
            if (tentativaClassificacaoCategoriaAtual == null) {
                return;
            }
            ContextoAcaoInstrumental contextoInstrumental =
                    new ContextoAcaoInstrumental(
                            "Confirmar se a definição da categoria escolhida se aplica à situação-problema",
                            "Responder ao questionamento de categorização",
                            "Diálogo de confirmação",
                            "Reconhecer ou reafirmar uma classificação divergente",
                            TentativaClassificacaoCategoriaAditiva.ALVO_CONFIRMACAO,
                            "CONFIRMACAO_CATEGORIA_DIVERGENTE",
                            "concordou=" + concordou,
                            "Resposta à confirmação de categoria registrada",
                            Collections.<String>emptyList());
            RegistroAcaoClassificacaoCategoria registro =
                    tentativaClassificacaoCategoriaAtual
                            .avaliarConfirmacaoCategoriaDivergente(
                                    concordou, contextoInstrumental);
            loggerInteracaoGerard.registrarAcaoInstrumentalUsuario(registro);
            conectorVereditoModelador.registrarAcaoInstrumental(
                    loggerInteracaoGerard.getUsuarioAtual(), registro,
                    gerard.agente.modelousuario.NivelSuporte.PARCIAL,
                    registro.getActionId());
            // "Não" é uma ação correta, mas não encerra a sequência: a
            // categoria da situação ainda não foi acertada. Esse estado
            // permanece no agregado, não na interface.
            if (registro.atingiuLimite()) {
                acionarTimeoutCategoria(registro.getCategoriaEsperada());
            }
        }

        /**
         * Disparado pela tentativa semântica de classificação ao atingir o
         * limite de erros consecutivos (ícone errado +
         * confirmação "Sim", somados): para a adivinhação — em vez de
         * deixar a pessoa clicando infinitamente — e reexplica a categoria
         * curada da situação. Reflete a intervenção que a própria
         * usuária fazia manualmente como pesquisadora nos experimentos em
         * papel: parar a ação e explicar novamente cada categoria.
         */
        private void acionarTimeoutCategoria(TipoSituacaoAditiva categoriaReal) {
            aguardandoAdivinhacaoCategoria = false;
            categoriaSorteioOculta = null;
            atualizarHabilitacaoIconesAtalhoCategoria();
            registrarLogUsuario(
                    "Encerrar a adivinhação após erros consecutivos e reexplicar a categoria",
                    "-",
                    "Faixa de ícones de categoria",
                    "Diálogo de reexplicação",
                    "Retomar a compreensão da categoria antes de continuar a modelagem",
                    "OBJ8",
                    "Após erros consecutivos, o sistema para a adivinhação e reexplica a categoria da situação sorteada.",
                    "TIMEOUT_CATEGORIA",
                    "categoria=" + categoriaReal
            );
            mostrarExplicacaoCategorias(categoriaReal);
        }

        /**
         * Reexplica só a categoria real da situação sorteada (2026-08-17:
         * antes reexplicava sempre as 3 categorias de Medidas, hardcoded,
         * mesmo quando a situação era de Relações — bug relatado pela
         * usuária, "essa explicação aparece no terceiro erro da categoria
         * de relações", com captura de tela mostrando CM/TM/COP mesmo numa
         * situação de Relações. Decisão: "cada categoria deve vir apenas
         * com sua explicação curta" — uma linha só, a da categoria real
         * (`categoriaReal`, capturada por quem chama antes de
         * `categoriaSorteioOculta` ser zerada), qualquer uma das 6),
         * no formato da MidiaPreferida do usuário (Modelo do Usuário) —
         * primeira vez que esse campo passa a influenciar algo mostrado na
         * tela; até agora só era gravado (ver criarPainelMidiaAjudaContextual).
         * VIDEO e HISTORIA_EM_QUADRINHOS materializam o mesmo repertório
         * curado das categorias de Relações em sintaxes diferentes. A
         * categoria possui o conteúdo; Swing resolve GIFs ou storyboards.
         * GRAFICO e SOM ainda mostram o aviso de "em construção" e caem
         * para o mesmo texto de LINGUAGEM_NATURAL por baixo.
         */
        private void mostrarExplicacaoCategorias(TipoSituacaoAditiva categoriaReal) {
            String idUsuario = loggerInteracaoGerard.getUsuarioAtual();
            gerard.agente.modelousuario.ModeloUsuario modeloAtual = repositorioModeloUsuario.obter(idUsuario);
            gerard.agente.modelousuario.MidiaPreferida midia =
                    modeloAtual == null ? null : modeloAtual.getPerfilAprendizagem().getMidiaPreferida();
            if (midia == null) {
                midia = gerard.agente.modelousuario.MidiaPreferida.LINGUAGEM_NATURAL;
            }
            final RepertorioAjudaVisual repertorioAjudaVisual =
                    categoriaReal.selecionarRepertorioAjudaVisual();
            final FormatoAjudaNarrativaVisual formatoNarrativa =
                    midia == gerard.agente.modelousuario.MidiaPreferida.VIDEO
                            ? FormatoAjudaNarrativaVisual.ANIMACAO
                            : midia == gerard.agente.modelousuario.MidiaPreferida.HISTORIA_EM_QUADRINHOS
                                    ? FormatoAjudaNarrativaVisual.HISTORIA_EM_QUADRINHOS
                                    : null;
            final PainelAjudaNarrativaVisualCategoria painelNarrativa =
                    formatoNarrativa == null ? null
                            : PainelAjudaNarrativaVisualCategoria.criarSeDisponivel(
                                    repertorioAjudaVisual, formatoNarrativa);

            final JDialog dialogo = new JDialog(
                    SwingUtilities.getWindowAncestor(this),
                    localizacao.texto("ui.dialog.categoryExplanation.title"),
                    Dialog.ModalityType.APPLICATION_MODAL
            );
            dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialogo.setResizable(false);

            JPanel conteudo = new JPanel(new BorderLayout(0, 16));
            conteudo.setBorder(BorderFactory.createEmptyBorder(18, 20, 14, 20));
            conteudo.setBackground(COR_SUPERFICIE);

            JPanel corpo = new JPanel();
            corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
            corpo.setOpaque(false);

            JLabel intro = new JLabel("<html><body style='width: 320px'>"
                    + localizacao.texto("ui.dialog.categoryExplanation.intro") + "</body></html>");
            intro.setFont(new Font("Arial", Font.PLAIN, 15));
            intro.setForeground(COR_TEXTO);
            intro.setAlignmentX(Component.LEFT_ALIGNMENT);
            corpo.add(intro);
            corpo.add(Box.createVerticalStrut(12));

            boolean formatoPendente = midia != gerard.agente.modelousuario.MidiaPreferida.LINGUAGEM_NATURAL
                    && painelNarrativa == null;
            if (formatoPendente) {
                JLabel avisoConstrucao = new JLabel("<html><body style='width: 320px'><i>"
                        + localizacao.texto("ui.dialog.categoryExplanation.midiaPendente") + "</i></body></html>");
                avisoConstrucao.setFont(new Font("Arial", Font.PLAIN, 13));
                avisoConstrucao.setForeground(COR_TEXTO);
                avisoConstrucao.setAlignmentX(Component.LEFT_ALIGNMENT);
                corpo.add(avisoConstrucao);
                corpo.add(Box.createVerticalStrut(10));
            }

            if (painelNarrativa != null) {
                corpo.add(painelNarrativa);
                corpo.add(Box.createVerticalStrut(12));
            }

            corpo.add(criarLinhaExplicacaoCategoria(
                    categoriaReal, obterIconeParaCategoria(categoriaReal)));

            conteudo.add(corpo, BorderLayout.CENTER);

            final JButton fechar = new JButton(localizacao.texto("ui.dialog.categoryExplanation.close"));
            fechar.setOpaque(true);
            fechar.setBackground(COR_SUPERFICIE_SUAVE);
            fechar.setForeground(COR_TEXTO);
            fechar.setFocusPainted(false);
            fechar.setBorder(BorderFactory.createLineBorder(COR_BORDA_BOTAO, 1));
            ActionListener fecharDialogo = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialogo.dispose();
                }
            };
            fechar.addActionListener(fecharDialogo);
            JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            botoes.setOpaque(false);
            botoes.add(fechar);
            conteudo.add(botoes, BorderLayout.SOUTH);

            dialogo.getRootPane().setDefaultButton(fechar);
            dialogo.getRootPane().registerKeyboardAction(
                    fecharDialogo,
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            dialogo.setContentPane(conteudo);
            dialogo.pack();
            dialogo.setLocationRelativeTo(this);
            final String formatoExibido = painelNarrativa == null
                    ? "linguagem_natural"
                    : painelNarrativa.getFormato() == FormatoAjudaNarrativaVisual.ANIMACAO
                            ? "historinha_animada"
                            : "historia_em_quadrinhos";
            dialogo.addWindowListener(new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    registrarFeedbackExibido(
                            "AG_EME",
                            gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding.VISUAL,
                            "categoria=" + categoriaReal
                                    + "; formato=" + formatoExibido
                                    + "; gatilho=terceiro_erro_categoria"
                                    + (painelNarrativa == null ? ""
                                            : "; repertorio=" + painelNarrativa.getChaveRepertorio()
                                                    + "; quantidade_historinhas="
                                                    + painelNarrativa.getQuantidadeHistorias()));
                }
            });
            dialogo.setVisible(true);
        }

        /**
         * Mesmo ícone usado nos botões de atalho de categoria
         * (criarPainelAtalhoCategoria), reaproveitado aqui para não duplicar
         * o desenho — localidade do conhecimento: cada categoria só tem um
         * ícone, definido em um lugar só (criarIconeCategoria*).
         */
        private Icon obterIconeParaCategoria(TipoSituacaoAditiva tipo) {
            if (tipo == null) {
                return criarIconeCategoriaComposicao();
            }
            switch (tipo) {
                case COMPOSICAO_MEDIDAS:
                    return criarIconeCategoriaComposicao();
                case TRANSFORMACAO_MEDIDAS:
                    return criarIconeCategoriaTransformacao();
                case COMPARACAO_MEDIDAS:
                    return criarIconeCategoriaComparacao();
                case COMPOSICAO_TRANSFORMACOES:
                    return criarIconeCategoriaComposicaoTransformacoes();
                case TRANSFORMACAO_RELACAO:
                    return criarIconeCategoriaTransformacaoRelacao();
                case COMPOSICAO_RELACOES:
                    return criarIconeCategoriaComposicaoRelacoes();
                default:
                    return criarIconeCategoriaComposicao();
            }
        }

        /**
         * Uma linha da explicação por categoria: ícone (mesmo desenhado nos
         * botões de atalho) + rótulo + o texto de definição
         * (ui.question.category.*) — mesmo conteúdo para todo mundo, já que
         * só LINGUAGEM_NATURAL tem conteúdo pronto hoje (ver
         * mostrarExplicacaoCategorias).
         */
        private JPanel criarLinhaExplicacaoCategoria(TipoSituacaoAditiva tipo, Icon icone) {
            JPanel linha = new JPanel();
            linha.setLayout(new BoxLayout(linha, BoxLayout.Y_AXIS));
            linha.setOpaque(false);
            linha.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            cabecalho.setOpaque(false);
            cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
            cabecalho.add(new JLabel(icone));
            JLabel rotulo = new JLabel(localizacao.rotuloBotaoTipo(tipo));
            rotulo.setFont(new Font("Arial", Font.BOLD, 14));
            rotulo.setForeground(COR_TEXTO);
            cabecalho.add(rotulo);
            linha.add(cabecalho);

            JLabel definicao = new JLabel("<html><body style='width: 300px'>"
                    + localizacao.texto("ui.question.category." + tipo.name().toLowerCase()) + "</body></html>");
            definicao.setFont(new Font("Arial", Font.PLAIN, 13));
            definicao.setForeground(COR_TEXTO);
            definicao.setAlignmentX(Component.LEFT_ALIGNMENT);
            definicao.setBorder(BorderFactory.createEmptyBorder(2, 4, 0, 0));
            linha.add(definicao);
            return linha;
        }

        /**
         * Diálogo Sim/Não no padrão visual do Gérard (JDialog + JPanel/
         * JButton, mesma estrutura de solicitarNumeroInteiroParaInterrogacao),
         * em vez de JOptionPane.showConfirmDialog: este último usa o ícone e
         * os rótulos "Yes"/"No" nativos do Swing/SO, que não seguem o idioma
         * selecionado nem a paleta neutra do app (relatado pela usuária,
         * 2026-07-28, com captura de tela mostrando o diálogo nativo em
         * inglês). aoResponderSim/aoResponderNao são opcionais (podem ser
         * null) — quem não precisa avaliar a resposta (ex.: outros usos
         * futuros deste diálogo genérico) passa null nos dois.
         */
        private void mostrarDialogoConfirmacaoSimNao(String pergunta, final Runnable aoResponderSim,
                final Runnable aoResponderNao) {
            final JDialog dialogo = new JDialog(
                    SwingUtilities.getWindowAncestor(this),
                    localizacao.texto("ui.dialog.confirm"),
                    Dialog.ModalityType.APPLICATION_MODAL
            );
            dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialogo.setResizable(false);

            JPanel conteudo = new JPanel(new BorderLayout(0, 16));
            conteudo.setBorder(BorderFactory.createEmptyBorder(18, 20, 14, 20));
            conteudo.setBackground(COR_SUPERFICIE);

            JLabel mensagem = new JLabel("<html><body style='width: 280px'>" + pergunta + "</body></html>");
            // Fonte maior pra melhor leitura (decisão da usuária, 2026-07-28:
            // "mensagens muito importantes") — a largura do HTML continua
            // fixa em 280px, então o texto ganha altura (mais linhas), não
            // largura, mantendo o diálogo estreito.
            mensagem.setFont(new Font("Arial", Font.PLAIN, 17));
            mensagem.setForeground(COR_TEXTO);
            conteudo.add(mensagem, BorderLayout.CENTER);

            JButton nao = new JButton(localizacao.texto("ui.completion.no"));
            JButton sim = new JButton(localizacao.texto("ui.completion.yes"));
            // Estiliza direto no componente em vez de confiar no L&F nativo
            // do Windows para o destaque azul do botão padrão — o mesmo
            // motivo já documentado para os menus (estilizarItemMenuPopup):
            // o L&F nativo ignora boa parte do UIManager.put, mas pintar
            // direto no componente funciona (relatado pela usuária,
            // 2026-07-28, com captura mostrando o azul nativo do Windows).
            for (JButton botao : new JButton[] {nao, sim}) {
                botao.setOpaque(true);
                botao.setBackground(COR_SUPERFICIE_SUAVE);
                botao.setForeground(COR_TEXTO);
                botao.setFocusPainted(false);
                botao.setBorder(BorderFactory.createLineBorder(COR_BORDA_BOTAO, 1));
            }
            JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            botoes.setOpaque(false);
            botoes.add(nao);
            botoes.add(sim);
            conteudo.add(botoes, BorderLayout.SOUTH);

            ActionListener fechar = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialogo.dispose();
                }
            };
            sim.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialogo.dispose();
                    if (aoResponderSim != null) {
                        aoResponderSim.run();
                    }
                }
            });
            nao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialogo.dispose();
                    if (aoResponderNao != null) {
                        aoResponderNao.run();
                    }
                }
            });

            dialogo.getRootPane().setDefaultButton(sim);
            dialogo.getRootPane().registerKeyboardAction(
                    fechar,
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            dialogo.setContentPane(conteudo);
            dialogo.pack();
            dialogo.setLocationRelativeTo(this);
            dialogo.setVisible(true);
        }

        private JMenu criarMenuExibir() {
            JMenu menu = new JMenu(localizacao.texto("ui.menu.top.view"));
            menu.setMnemonic(KeyEvent.VK_X);
            estilizarItemMenuPopup(menu);
            estilizarMenuPopup(menu.getPopupMenu());

            JMenu menuIdiomaItem = new JMenu(localizacao.texto("ui.button.language.generic"));
            estilizarItemMenuPopup(menuIdiomaItem);
            estilizarMenuPopup(menuIdiomaItem.getPopupMenu());
            menuIdiomaItem.setToolTipText(localizacao.texto("ui.tooltip.language"));

            for (final IdiomaInterface idioma : IdiomaInterface.values()) {
                boolean atual = idioma == idiomaSelecionado;
                JMenuItem item = new JMenuItem((atual ? "✓ " : "") + localizacao.nomeIdioma(idioma));
                estilizarItemMenuPopup(item);
                item.setEnabled(!atual);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        idiomaSelecionado = idioma;
                        controladorEstadoAtividade.registrar(AcaoAtividade.TROCAR_IDIOMA);
                        aplicarIdiomaSelecionadoMantendoEstadoTela();
                    }
                });
                menuIdiomaItem.add(item);
            }
            menu.add(menuIdiomaItem);
            return menu;
        }

        private JMenu criarMenuFerramentas() {
            JMenu menu = new JMenu(localizacao.texto("ui.menu.top.tools"));
            menu.setMnemonic(KeyEvent.VK_F);
            estilizarItemMenuPopup(menu);
            estilizarMenuPopup(menu.getPopupMenu());

            // Item "Usuário" saiu daqui — agora é o widget logado/deslogado
            // no canto direito da barra (ver criarBotaoUsuarioMenuBar,
            // decisão da usuária, 2026-07-28).

            JMenuItem itemVisaoPesquisador = new JMenuItem(localizacao.texto("pesq.button.open"));
            estilizarItemMenuPopup(itemVisaoPesquisador);
            itemVisaoPesquisador.setToolTipText(localizacao.texto("pesq.tooltip.open"));
            itemVisaoPesquisador.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!autenticarPesquisador()) {
                        requestFocusInWindow();
                        return;
                    }
                    Window janela = SwingUtilities.getWindowAncestor(TelaGerard.this);
                    TelaVisaoPesquisador.mostrar(janela, agenteModelador, repositorioModeloUsuario);
                    requestFocusInWindow();
                }
            });
            menu.add(itemVisaoPesquisador);

            return menu;
        }

        private JMenu criarMenuAjuda() {
            JMenu menu = new JMenu(localizacao.texto("ui.menu.top.help"));
            menu.setMnemonic(KeyEvent.VK_J);
            estilizarItemMenuPopup(menu);
            estilizarMenuPopup(menu.getPopupMenu());

            JMenu menuSobreItem = new JMenu(localizacao.texto("ui.button.about"));
            estilizarItemMenuPopup(menuSobreItem);
            estilizarMenuPopup(menuSobreItem.getPopupMenu());
            menuSobreItem.setToolTipText(localizacao.texto("ui.tooltip.about"));

            JMenu itemInterpretacao = new JMenu(localizacao.texto("ui.menu.linguisticInterpretation"));
            estilizarItemMenuPopup(itemInterpretacao);
            estilizarMenuPopup(itemInterpretacao.getPopupMenu());
            itemInterpretacao.add(criarMenuInterpretacaoLinguistica());
            menuSobreItem.add(itemInterpretacao);

            JMenu itemRegistro = new JMenu(localizacao.texto("ui.menu.gerardRegistration"));
            estilizarItemMenuPopup(itemRegistro);
            estilizarMenuPopup(itemRegistro.getPopupMenu());
            itemRegistro.add(criarMenuRegistroGerard());
            menuSobreItem.add(itemRegistro);

            JMenu itemGerardVergnaud = new JMenu(localizacao.texto("ui.menu.gerardVergnaud"));
            estilizarItemMenuPopup(itemGerardVergnaud);
            estilizarMenuPopup(itemGerardVergnaud.getPopupMenu());
            itemGerardVergnaud.add(criarMenuGerardVergnaud());
            menuSobreItem.add(itemGerardVergnaud);

            menu.add(menuSobreItem);

            // Reportar bug saiu daqui — agora é um ícone ao lado do menu
            // Ajuda, direto na barra de menu (ver criarBotaoReportarBugMenuBar,
            // decisão da usuária, 2026-07-28).

            return menu;
        }

        /**
         * Habilita/desabilita os itens que só fazem sentido na aba "Gerard"
         * (esta TelaGerard) — Categoria e Nova situação-problema manipulam
         * tipoSituacaoSelecionada/iniciarNovaAtividade, que só têm efeito
         * visível nesta aba. "Nova situação-problema" não depende mais de
         * categoriaSelecionadaParaAtividade (decisão da usuária em
         * 2026-07-28): ele mesmo sorteia a categoria a cada clique — ver
         * sortearNovaSituacao() — então fica disponível desde o início,
         * mesmo sem passar pelo menu Categoria antes.
         */
        private void atualizarEstadoItensMenuPorAba() {
            // Menu Categoria sempre desabilitado, em qualquer aba (decisão da
            // usuária, 2026-07-28): era um atalho que pulava a adivinhação
            // pelos ícones — com ele fora, os ícones passam a ser o único
            // caminho pra escolher categoria.
            if (menuCategoria != null) {
                menuCategoria.setEnabled(false);
            }
            if (itemNovaSituacao != null) {
                itemNovaSituacao.setEnabled(abaGerardAtiva);
            }
            if (botaoFerramentaSortearMedidas != null) {
                botaoFerramentaSortearMedidas.setEnabled(abaGerardAtiva);
            }
            if (botaoFerramentaSortearRelacoes != null) {
                botaoFerramentaSortearRelacoes.setEnabled(abaGerardAtiva);
            }
        }

        /** Chamado por Main ao trocar de aba — ver atualizarEstadoItensMenuPorAba. */
        public void definirAbaGerardAtiva(boolean ativa) {
            abaGerardAtiva = ativa;
            atualizarEstadoItensMenuPorAba();
        }

        private void estilizarMenuPopup(JPopupMenu menu) {
            if (menu == null) {
                return;
            }
            menu.setBackground(COR_SUPERFICIE);
            menu.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)
            ));
        }

        private void estilizarItemMenuPopup(final JMenuItem item) {
            if (item == null) {
                return;
            }
            item.setFont(gerard.ui.UITemaGerard.FONTE_ITEM_MENU);
            item.setHorizontalAlignment(SwingConstants.LEFT);
            item.setHorizontalTextPosition(SwingConstants.LEFT);
            item.setMargin(new Insets(6, 10, 6, 10));
            item.setBackground(COR_SUPERFICIE);
            item.setForeground(COR_TEXTO);
            item.setOpaque(true);
            item.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            item.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (item.isEnabled()) {
                        item.setBackground(COR_DESTAQUE);
                    }
                }
                public void mouseExited(MouseEvent e) {
                    item.setBackground(COR_SUPERFICIE);
                }
            });
        }

        private JPanel criarMenuInterpretacaoLinguistica() {
            JPanel painel = new JPanel();
            painel.setOpaque(false);
            painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
            painel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            JLabel titulo = criarLabelMenu(localizacao.texto("ui.menu.linguisticInterpretation"), true);
            painel.add(titulo);
            painel.add(Box.createVerticalStrut(5));

            if (resultadoInterpretacao != null) {
                painel.add(criarLabelMenu(
                        localizacao.texto("ui.panel.detectedLanguage") + ": " + resultadoInterpretacao.getIdiomaDetectado().getDescricao() +
                        " | " + localizacao.texto("ui.panel.confidence") + ": " + localizacao.texto("ui.panel.humanCuration"),
                        false
                ));
                painel.add(criarLabelMenu(
                        localizacao.texto("ui.panel.probableCategory") + ": " + resultadoInterpretacao.getCategoriaProvavel().getDescricao() +
                        " (" + resultadoInterpretacao.getCategoriaProvavel().getSigla() + ")",
                        false
                ));
                painel.add(criarLabelMenu(localizacao.texto("ui.panel.subtype") + ": " + limitarTexto(resultadoInterpretacao.getSubtipoFormatado(), 92), false));
                painel.add(criarLabelMenu(localizacao.texto("ui.panel.numbers") + ": " + resultadoInterpretacao.getNumerosFormatados(), false));
                painel.add(criarLabelMenu(localizacao.texto("ui.panel.probableRelation") + ": " + resultadoInterpretacao.getRelacaoProvavel(), false));
                painel.add(criarLabelMenu(localizacao.texto("ui.panel.clues") + ": " + limitarTexto(resultadoInterpretacao.getPistasFormatadas(), 92), false));
                painel.add(criarLabelMenu(localizacao.texto("ui.panel.roles") + ": " + limitarTexto(resultadoInterpretacao.getPapeisFormatados(), 92), false));

                if (!resultadoInterpretacao.getAvisos().isEmpty()) {
                    painel.add(criarLabelMenu(localizacao.texto("ui.panel.warning") + ": " + limitarTexto(resultadoInterpretacao.getAvisos().get(0), 92), false));
                }
            }

            return painel;
        }

        private JPanel criarMenuRegistroGerard() {
            JPanel painel = new JPanel();
            painel.setOpaque(false);
            painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
            painel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            painel.add(criarLabelMenu(localizacao.texto("ui.menu.gerardRegistration"), true));
            painel.add(Box.createVerticalStrut(5));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.registeredProgram"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.process"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.title"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.creationDate"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.authors"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.language"), false));

            return painel;
        }

        private JPanel criarMenuGerardVergnaud() {
            JPanel painel = new JPanel();
            painel.setOpaque(false);
            painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
            painel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

            int larguraConteudo = 216;

            JLabel titulo = criarLabelMenu(localizacao.texto("ui.menu.gerardVergnaud"), true);
            titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            titulo.setMaximumSize(new Dimension(larguraConteudo, titulo.getPreferredSize().height));
            painel.add(titulo);
            painel.add(Box.createVerticalStrut(8));

            JLabel imagem = criarLabelImagemMenu("/gerard/imagens/gerard_vergnaud.png", larguraConteudo, 140);
            painel.add(criarPainelImagemCentralizada(imagem, larguraConteudo));
            painel.add(Box.createVerticalStrut(8));

            JTextArea descricao = criarAreaTextoMenuMultilinha(localizacao.texto("ui.about.gerardCaption"), larguraConteudo);
            painel.add(descricao);
            painel.add(Box.createVerticalStrut(4));

            JLabel linkWikipedia = criarLinkMenu(localizacao.texto("ui.about.wikipediaLink"), URL_GERARD_VERGNAUD);
            linkWikipedia.setAlignmentX(Component.LEFT_ALIGNMENT);
            linkWikipedia.setMaximumSize(new Dimension(larguraConteudo, linkWikipedia.getPreferredSize().height));
            painel.add(linkWikipedia);
            painel.add(Box.createVerticalStrut(3));

            JLabel linkVideo = criarLinkMenu(localizacao.texto("ui.about.youtubeLink"), URL_VIDEO_GERARD_VERGNAUD);
            linkVideo.setAlignmentX(Component.LEFT_ALIGNMENT);
            linkVideo.setMaximumSize(new Dimension(larguraConteudo, linkVideo.getPreferredSize().height));
            painel.add(linkVideo);
            painel.add(Box.createVerticalStrut(3));

            JLabel linkVergnaudBrasil = criarLinkMenu(localizacao.texto("ui.about.vergnaudBrasilLink"), URL_VERGNAUD_BRASIL);
            linkVergnaudBrasil.setAlignmentX(Component.LEFT_ALIGNMENT);
            linkVergnaudBrasil.setMaximumSize(new Dimension(larguraConteudo, linkVergnaudBrasil.getPreferredSize().height));
            painel.add(linkVergnaudBrasil);
            painel.add(Box.createVerticalStrut(8));

            // "Na UFPE em 2009" deixou de ser um submenu (JMenu aninhado
            // dentro deste painel) e passou a ser conteúdo direto, mais
            // abaixo (decisão da usuária, 2026-07-28): um JMenu aninhado
            // dentro de um JPanel comum (não diretamente dentro de um
            // JPopupMenu de outro JMenu) deixa o rastreamento de mouse do
            // Swing inconsistente — ao mover o cursor dos links acima em
            // direção a essa opção, o menu inteiro fechava antes do cursor
            // chegar lá. Achatar em vez de tentar corrigir o rastreamento de
            // mouse (frágil e não verificável sem testar interativamente).
            painel.add(Box.createVerticalStrut(4));
            JLabel tituloUfpe = criarLabelMenu(localizacao.texto("ui.menu.emRecife"), true);
            tituloUfpe.setAlignmentX(Component.LEFT_ALIGNMENT);
            tituloUfpe.setMaximumSize(new Dimension(larguraConteudo, tituloUfpe.getPreferredSize().height));
            painel.add(tituloUfpe);
            painel.add(Box.createVerticalStrut(8));

            JPanel painelImagensUfpe = criarPainelDuasImagensMenu(
                    "/gerard/imagens/em_recife_rostos.png",
                    "/gerard/imagens/em_recife_adicional.png",
                    larguraConteudo,
                    140
            );
            painel.add(painelImagensUfpe);

            return painel;
        }

        private JPanel criarPainelDuasImagensMenu(String caminhoImagem1, String caminhoImagem2, int larguraTotal, int alturaMaximaTotal) {
            int espacamento = 8;
            int larguraPorImagem = Math.max(80, (larguraTotal - espacamento) / 2);
            int alturaPorImagem = Math.max(80, alturaMaximaTotal - 10);

            JLabel imagem1 = criarLabelImagemMenu(caminhoImagem1, larguraPorImagem, alturaPorImagem);
            JLabel imagem2 = criarLabelImagemMenu(caminhoImagem2, larguraPorImagem, alturaPorImagem);

            JPanel painel = new JPanel(new GridLayout(1, 2, espacamento, 0));
            painel.setOpaque(false);
            painel.setAlignmentX(Component.LEFT_ALIGNMENT);
            painel.add(imagem1);
            painel.add(imagem2);

            int altura = Math.max(imagem1.getPreferredSize().height, imagem2.getPreferredSize().height);
            Dimension tamanho = new Dimension(larguraTotal, altura);
            painel.setPreferredSize(tamanho);
            painel.setMinimumSize(tamanho);
            painel.setMaximumSize(tamanho);
            return painel;
        }

        private JPanel criarPainelImagemCentralizada(JLabel imagem, int largura) {
            JPanel painelImagem = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            painelImagem.setOpaque(false);
            painelImagem.setAlignmentX(Component.LEFT_ALIGNMENT);
            painelImagem.add(imagem);
            Dimension preferidoImagem = imagem.getPreferredSize();
            painelImagem.setPreferredSize(new Dimension(largura, preferidoImagem.height));
            painelImagem.setMinimumSize(new Dimension(largura, preferidoImagem.height));
            painelImagem.setMaximumSize(new Dimension(largura, preferidoImagem.height));
            return painelImagem;
        }

        private JTextArea criarAreaTextoMenuMultilinha(String texto, int largura) {
            JTextArea area = new JTextArea(texto == null ? "" : texto);
            area.setEditable(false);
            area.setFocusable(false);
            area.setOpaque(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setFont(new Font("Arial", Font.PLAIN, 11));
            area.setForeground(COR_TEXTO_SECUNDARIO);
            area.setBorder(BorderFactory.createEmptyBorder(1, 0, 3, 0));
            area.setAlignmentX(Component.LEFT_ALIGNMENT);
            area.setSize(new Dimension(largura, Short.MAX_VALUE));
            Dimension preferido = area.getPreferredSize();
            int alturaPreferida = Math.max(preferido.height, area.getFontMetrics(area.getFont()).getHeight() + 4);
            Dimension tamanhoFinal = new Dimension(largura, alturaPreferida);
            area.setPreferredSize(tamanhoFinal);
            area.setMinimumSize(tamanhoFinal);
            area.setMaximumSize(tamanhoFinal);
            area.setSize(tamanhoFinal);
            return area;
        }

        private JLabel criarLabelImagemMenu(String caminhoRecurso, int larguraMaxima, int alturaMaxima) {
            java.net.URL recurso = Main.class.getResource(caminhoRecurso);
            if (recurso == null) {
                return criarLabelMenuMultilinha(localizacao.texto("ui.about.gerardCaption"), larguraMaxima);
            }

            ImageIcon iconeOriginal = new ImageIcon(recurso);
            if (iconeOriginal.getIconWidth() <= 0 || iconeOriginal.getIconHeight() <= 0) {
                return criarLabelMenuMultilinha(localizacao.texto("ui.about.gerardCaption"), larguraMaxima);
            }

            double escalaLargura = (double) larguraMaxima / (double) iconeOriginal.getIconWidth();
            double escalaAltura = (double) alturaMaxima / (double) iconeOriginal.getIconHeight();
            double escala = Math.min(1.0, Math.min(escalaLargura, escalaAltura));

            int largura = Math.max(1, (int) Math.round(iconeOriginal.getIconWidth() * escala));
            int altura = Math.max(1, (int) Math.round(iconeOriginal.getIconHeight() * escala));

            Image imagemEscalada = iconeOriginal.getImage().getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(imagemEscalada));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return label;
        }

        private JLabel criarLinkMenu(final String texto, final String url) {
            JLabel link = new JLabel("<html><a href=''>" + escaparHtml(texto) + "</a></html>");
            link.setFont(gerard.ui.UITemaGerard.FONTE_TEXTO_SUBMENU);
            link.setForeground(COR_PRIMARIA_ESCURA);
            link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            link.setToolTipText(url);
            link.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    abrirLinkExterno(url);
                }
            });
            return link;
        }

        private void abrirLinkExterno(String url) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    JOptionPane.showMessageDialog(this, url, localizacao.texto("ui.menu.gerardVergnaud"), JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, url, localizacao.texto("ui.menu.gerardVergnaud"), JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private JLabel criarLabelMenu(String texto, boolean titulo) {
            JLabel label = new JLabel(titulo ? texto : "<html>" + formatarTextoMenu(texto) + "</html>");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setFont(titulo ? gerard.ui.UITemaGerard.FONTE_TITULO_SUBMENU : gerard.ui.UITemaGerard.FONTE_TEXTO_SUBMENU);
            label.setForeground(titulo ? COR_TEXTO : COR_TEXTO_SECUNDARIO);
            return label;
        }

        private JLabel criarLabelMenuMultilinha(String texto, int largura) {
            JLabel label = new JLabel("<html><div style='width:" + largura + "px; white-space:normal; word-wrap:break-word;'>" + formatarTextoMenu(texto) + "</div></html>");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            label.setFont(gerard.ui.UITemaGerard.FONTE_TEXTO_SUBMENU);
            label.setForeground(COR_TEXTO_SECUNDARIO);
            label.setBorder(BorderFactory.createEmptyBorder(1, 0, 3, 0));
            return label;
        }

        private String formatarTextoMenu(String texto) {
            if (texto == null) {
                return "";
            }
            int indiceDoisPontos = texto.indexOf(':');
            if (indiceDoisPontos <= 0) {
                return escaparHtml(texto);
            }
            String prefixo = escaparHtml(texto.substring(0, indiceDoisPontos));
            String sufixo = escaparHtml(texto.substring(indiceDoisPontos + 1));
            return "<b>" + prefixo + ":</b>" + sufixo;
        }

        private String escaparHtml(String texto) {
            if (texto == null) {
                return "";
            }
            return texto.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }

        private String limitarTexto(String texto, int limite) {
            if (texto == null) {
                return "";
            }
            return texto.length() > limite ? texto.substring(0, limite - 3) + "..." : texto;
        }

        private void cancelarOcultacaoTipConclusao() {
            if (timerOcultarTipConclusao != null && timerOcultarTipConclusao.isRunning()) {
                timerOcultarTipConclusao.stop();
            }
        }

        private void agendarOcultacaoTipConclusao() {
            cancelarOcultacaoTipConclusao();
            timerOcultarTipConclusao = new javax.swing.Timer(180, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!cursorSobre(tipConclusaoModelagem)) {
                        tipConclusaoModelagem.ocultar();
                    }
                }
            });
            timerOcultarTipConclusao.setRepeats(false);
            timerOcultarTipConclusao.start();
        }

        private boolean cursorSobre(Component componente) {
            if (componente == null || !componente.isVisible()) {
                return false;
            }

            PointerInfo info = MouseInfo.getPointerInfo();
            if (info == null) {
                return false;
            }

            Point ponto = info.getLocation();
            SwingUtilities.convertPointFromScreen(ponto, componente);
            return componente.contains(ponto);
        }

        public RepositorioSituacoesAditivas getRepositorioSituacoesAditivas() {
            return repositorioSituacoesAditivas;
        }

        public void recarregarSituacoesCuradasSemPerderIdiomaAtual() {
            String grupoAtual = situacaoProblemaAtual == null ? "" : situacaoProblemaAtual.getSituacaoGrupoId();
            String codigoIdiomaAtual = situacaoProblemaAtual == null ? ""
                    : gerard.idioma.IdiomaSituacao.normalizarCodigo(situacaoProblemaAtual.getCodigoIdioma());
            String idAtual = situacaoProblemaAtual == null ? "" : situacaoProblemaAtual.getId();

            repositorioSituacoesAditivas.recarregar();
            cadastroIdiomasSituacao.recarregar();

            SituacaoProblemaAditiva recarregada = null;
            for (SituacaoProblemaAditiva candidata : repositorioSituacoesAditivas.listarTodas()) {
                if (candidata == null) continue;
                String grupo = candidata.getSituacaoGrupoId() == null ? "" : candidata.getSituacaoGrupoId();
                String codigo = gerard.idioma.IdiomaSituacao.normalizarCodigo(candidata.getCodigoIdioma());
                if (!grupoAtual.isEmpty() && grupoAtual.equals(grupo) && codigoIdiomaAtual.equals(codigo)) {
                    recarregada = candidata;
                    break;
                }
                if (recarregada == null && !idAtual.isEmpty() && idAtual.equals(candidata.getId())) {
                    recarregada = candidata;
                }
            }

            if (situacaoProblemaExibivel(recarregada)) {
                trocarIdiomaDaSituacaoSemLog(recarregada);
            } else {
                // Recarregar/fechar a curadoria não pode apagar a modelagem.
                // Atualiza apenas a camada textual, mantendo o estado dos diagramas.
                aplicarIdiomaSelecionadoMantendoEstadoTela();
            }
            // A curadoria normativa pode ter sido corrigida. Recria somente
            // os proprietários de sinal; a modelagem visual continua intacta.
            reiniciarTentativasEscolhaSinalAtual();
            atualizarTextosFixosDaInterface();
            repaint();
        }

        private void trocarIdiomaDaSituacaoSemLog(SituacaoProblemaAditiva versao) {
            cancelarEfeitosArraste();
            situacaoProblemaAtual = versao;
            textoProblemaEhMensagemSistema = false;
            textoProblema = materializadorEnunciadoCurado.materializar(versao);
            resultadoInterpretacao = construtorResultadoCurado.construir(versao, textoProblema);
            atualizarContextoAdaptativoIncognitaAtual();
            definicaoDiagramaAtual = SemanticaCuradaSituacao.aplicarRotulos(
                    catalogoDefinicoesAditivas.obter(tipoSituacaoSelecionada),
                    situacaoProblemaAtual,
                    localizacao
            );
            handlerElementoTextoMovel.cancelar();
            elementoTextoFocado = null;
            inicializarElementosTexto();
            atualizarRotulosDiagramaVergnaudSemReposicionar();
            atualizarRotulosDiagramaVennSemLimparQuadradinhos();
        }

        private boolean situacaoProblemaExibivel(SituacaoProblemaAditiva situacao) {
            return situacao != null
                    && situacao.isValidada()
                    && situacao.getEnunciado() != null
                    && situacao.getEnunciado().trim().length() > 0;
        }

        private String textoAusenciaSituacaoCurada() {
            return localizacao.texto("ui.problem.noCuratedProblem");
        }

        private void inicializarTelaSemCategoria() {
            cancelarEfeitosArraste();
            reiniciarConclusaoModelagem();
            controladorEstadoAtividade.registrar(AcaoAtividade.INICIALIZAR);
            categoriaSelecionadaParaAtividade = false;
            aguardandoAdivinhacaoCategoria = false;
            categoriaSorteioOculta = null;
            tentativaClassificacaoCategoriaAtual = null;
            tentativasEscolhaSinalAtual = Collections.emptyMap();
            situacaoProblemaAtual = null;
            textoProblema = "";
            textoProblemaEhMensagemSistema = false;
            resultadoInterpretacao = null;
            atualizarContextoAdaptativoIncognitaAtual();
            definicaoDiagramaAtual = null;
            cenaDiagramaAtual = null;
            cenaDiagramaVennAtual = null;
            ultimaAreaDiagramaVenn = null;
            assinaturaDiagramaVennSincronizado = "";

            itensArrastaveis.clear();
            marcadoresFixosTexto.clear();
            elementosTexto.clear();
            elementosVergnaud.clear();
            limparEstadoDicaPosicionamento();
            conectoresVergnaud.clear();
            circulosVenn.clear();
            quadradinhosVenn.clear();
            quadradinhosCorrespondentesComparacao.clear();

            handlerItemTextoArrastavel.cancelar();
            itemFocado = null;
            handlerElementoTextoMovel.cancelar();
            elementoTextoFocado = null;
            sessaoArrasteTextoParaDiagrama.limpar();
            handlerQuadradinhoVenn.cancelar();
            quadradinhoVennFocado = null;
            handlerConectorVergnaud.cancelar();
            limparRealceAlvoProximidade();
            limparQuestionamentoPersistente();
            limparSinalDivergentePersistente();
            desativarPaineisEixosRelacoes();
            seletorOperacaoRelacaoAluno.desativar();
            seletorOperacaoEstadoTransformacaoAluno.desativar();
            desabilitarSincronizacaoEstadoFinal();
            estadoSemanticoCompartilhado.limpar(tipoSituacaoSelecionada);
            layoutTextoInicializado = false;
            larguraUltimoLayoutTexto = -1;

            ocultarControlesDaAtividadeSemCategoria();
            // itemNovaSituacao NÃO é desabilitado aqui: desde 2026-07-28 ele
            // sorteia a própria categoria a cada clique (sortearNovaSituacao),
            // então continua disponível mesmo no estado "sem categoria".
            atualizarHabilitacaoIconesAtalhoCategoria();
            atualizarTextosFixosDaInterface();
            repaint();
        }

        private void ocultarControlesDaAtividadeSemCategoria() {
            if (botaoRestaurar != null) botaoRestaurar.setVisible(false);
            if (botaoCorrigirCuradoria != null) botaoCorrigirCuradoria.setVisible(false);
            if (botaoIdiomaSituacao != null) botaoIdiomaSituacao.setVisible(false);
            if (botaoArtefatoExplicativo != null) botaoArtefatoExplicativo.setVisible(false);
            if (botaoRestaurarDiagrama != null) botaoRestaurarDiagrama.setVisible(false);
            if (botaoAjudaTexto != null) botaoAjudaTexto.setVisible(false);
            if (botaoAjudaVergnaud != null) botaoAjudaVergnaud.setVisible(false);
            if (botaoAjudaComplementar != null) botaoAjudaComplementar.setVisible(false);
            if (botaoVerDicaPosicionamento != null) botaoVerDicaPosicionamento.setVisible(false);
            if (menuAjudaContextualAtivo != null) {
                menuAjudaContextualAtivo.setVisible(false);
                menuAjudaContextualAtivo = null;
            }
            areaAjudaContextualAtiva = null;
        }

        private void iniciarNovaAtividade(AcaoAtividade acao) {
            controladorEstadoAtividade.registrar(acao);
            if (!controladorEstadoAtividade.deveReiniciarModelagem(acao)) {
                throw new IllegalArgumentException("A ação informada não inicia uma nova atividade: " + acao);
            }
            if (!categoriaSelecionadaParaAtividade) {
                inicializarTelaSemCategoria();
                return;
            }
            aplicarIdiomaSelecionado();
        }

        /**
         * Carrega uma nova situação e restaura a modelagem.
         * Este fluxo é reservado à inicialização, ao botão Sortear e à
         * seleção de uma nova categoria no menu Tipo. Essas ações iniciam
         * uma nova atividade e, por isso, restauram a modelagem.
         * Nenhuma outra ação deve chamar este método.
         */
        private void aplicarIdiomaSelecionado() {
            cancelarEfeitosArraste();
            reiniciarConclusaoModelagem();
            localizacao.definirIdioma(idiomaSelecionado);
            if (!categoriaSelecionadaParaAtividade) {
                inicializarTelaSemCategoria();
                return;
            }
            ContextoCarregamentoAtividade contexto = fachadaCarregamentoAtividade.carregarNova(
                    idiomaSelecionado, tipoSituacaoSelecionada);
            SituacaoProblemaAditiva situacao = contexto.getSituacao();
            boolean situacaoCuradaDisponivel = contexto.possuiSituacaoExibivel();
            situacaoProblemaAtual = situacaoCuradaDisponivel ? situacao : null;
            textoProblemaEhMensagemSistema = !situacaoCuradaDisponivel;
            textoProblema = situacaoCuradaDisponivel
                    ? materializadorEnunciadoCurado.materializar(situacao)
                    : textoAusenciaSituacaoCurada();
            controladorContextoSituacao.registrarNovaSituacao(
                    situacaoProblemaAtual,
                    situacaoCuradaDisponivel ? tipoSituacaoSelecionada.name() : "SEM_SITUACAO_CURADA",
                    textoProblema);
            definicaoDiagramaAtual = SemanticaCuradaSituacao.aplicarRotulos(
                    contexto.getDefinicao(), situacaoProblemaAtual, localizacao);
            resultadoInterpretacao = situacaoCuradaDisponivel ? construtorResultadoCurado.construir(situacao, textoProblema) : null;
            finalizarCarregamentoSituacao();
        }

        /**
         * Cauda comum de carregamento, compartilhada por aplicarIdiomaSelecionado
         * (fluxo normal) e confirmarCategoriaAdivinhada (fluxo de adivinhação de
         * categoria pelo Sortear) — pressupõe que situacaoProblemaAtual,
         * textoProblema, definicaoDiagramaAtual e resultadoInterpretacao já
         * foram atribuídos pelo chamador.
         */
        private void finalizarCarregamentoSituacao() {
            reiniciarTentativasEscolhaSinalAtual();
            atualizarContextoAdaptativoIncognitaAtual();

            itensArrastaveis.clear();
            marcadoresFixosTexto.clear();
            elementosTexto.clear();
            handlerItemTextoArrastavel.cancelar();
            itemFocado = null;
            handlerElementoTextoMovel.cancelar();
            elementoTextoFocado = null;
            handlerQuadradinhoVenn.cancelar();
            quadradinhoVennFocado = null;
            handlerConectorVergnaud.cancelar();
            limparRealceAlvoProximidade();
            mostrarAnotacaoMouseOver = false;
            limparQuestionamentoPersistente();
            limparSinalDivergentePersistente();
            desabilitarSincronizacaoEstadoFinal();
            estadoSemanticoCompartilhado.limpar(tipoSituacaoSelecionada);
            indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};

            inicializarElementosTexto();
            inicializarDiagramaVergnaud();
            inicializarDiagramaVenn();
            atualizarHabilitacaoIconesAtalhoCategoria();
            atualizarTextosFixosDaInterface();

            Window janela = SwingUtilities.getWindowAncestor(this);
            if (janela instanceof JFrame) {
                ((JFrame) janela).setTitle(localizacao.texto("ui.app.title"));
            }

            repaint();
        }

        private void reiniciarTentativasEscolhaSinalAtual() {
            tentativasEscolhaSinalAtual =
                    SemanticaCuradaSituacao.criarTentativasEscolhaSinal(
                            situacaoProblemaAtual, localizacao);
        }


        private void aplicarIdiomaSelecionadoMantendoEstadoTela() {
            cancelarEfeitosArraste();
            if (!categoriaSelecionadaParaAtividade) {
                localizacao.definirIdioma(idiomaSelecionado);
                atualizarTextosFixosDaInterface();
                ocultarControlesDaAtividadeSemCategoria();
                repaint();
                return;
            }

            int[] valoresAtuais = obterValoresSincronizadosParaDiagramaVenn();

            localizacao.definirIdioma(idiomaSelecionado);

            ContextoCarregamentoAtividade contexto = fachadaCarregamentoAtividade.carregarCorrespondente(
                    situacaoProblemaAtual, idiomaSelecionado, tipoSituacaoSelecionada, valoresAtuais);
            SituacaoProblemaAditiva situacaoTraduzida = contexto.getSituacao();

            if (contexto.possuiSituacaoExibivel()) {
                situacaoProblemaAtual = situacaoTraduzida;
                textoProblemaEhMensagemSistema = false;
                textoProblema = materializadorEnunciadoCurado.materializar(situacaoTraduzida);
                resultadoInterpretacao = contexto.getInterpretacao();
                handlerElementoTextoMovel.cancelar();
                elementoTextoFocado = null;
                inicializarElementosTexto();
            } else {
                // A ausência de uma versão textual não autoriza restaurar a modelagem.
                // O diagrama permanece exatamente no estado construído pelo usuário.
                textoProblemaEhMensagemSistema = true;
                textoProblema = textoAusenciaSituacaoCurada();
                resultadoInterpretacao = null;
                elementosTexto.clear();
                handlerElementoTextoMovel.cancelar();
                elementoTextoFocado = null;
                inicializarElementosTexto();
            }

            atualizarContextoAdaptativoIncognitaAtual();
            definicaoDiagramaAtual = SemanticaCuradaSituacao.aplicarRotulos(
                    contexto.getDefinicao(), situacaoProblemaAtual, localizacao);
            atualizarRotulosDiagramaVergnaudSemReposicionar();
            atualizarRotulosDiagramaVennSemLimparQuadradinhos();
            atualizarTextosFixosDaInterface();

            Window janela = SwingUtilities.getWindowAncestor(this);
            if (janela instanceof JFrame) {
                ((JFrame) janela).setTitle(localizacao.texto("ui.app.title"));
            }

            repaint();
        }

        private void atualizarRotulosDiagramaVergnaudSemReposicionar() {
            if (definicaoDiagramaAtual == null) {
                definicaoDiagramaAtual = catalogoDefinicoesAditivas.obter(tipoSituacaoSelecionada);
            }
            if (elementosVergnaud == null || elementosVergnaud.isEmpty()) {
                return;
            }

            Rectangle area = obterAreaConteudoDiagramaVergnaud();
            CenaDiagramaAditivo cenaAtualizada = geradorCenaDiagrama.gerar(
                    tipoSituacaoSelecionada, area, definicaoDiagramaAtual,
                    extrairValoresDoTexto());

            if (cenaAtualizada == null) {
                return;
            }

            cenaDiagramaAtual = cenaAtualizada;

            int totalFiguras = Math.min(elementosVergnaud.size(), cenaAtualizada.getFiguras().size());
            for (int i = 0; i < totalFiguras; i++) {
                elementosVergnaud.get(i).rotulo = cenaAtualizada.getFiguras().get(i).getRotulo();
            }

            int totalConectores = Math.min(conectoresVergnaud.size(), cenaAtualizada.getConectores().size());
            for (int i = 0; i < totalConectores; i++) {
                conectoresVergnaud.get(i).legenda = cenaAtualizada.getConectores().get(i).getLegenda();
            }

            // A curadoria é a fonte dos personagens. Ao salvar alterações,
            // atualiza também os subtítulos dos nós já renderizados, sem
            // reposicionar nem apagar a modelagem construída pelo usuário.
            aplicarSubtitulosPersonagensNoDiagramaVergnaud();
        }


        private void atualizarRotulosDiagramaVennSemLimparQuadradinhos() {
            if (cenaDiagramaVennAtual == null || ultimaAreaDiagramaVenn == null) {
                return;
            }
            int[] valores = obterValoresSincronizadosParaDiagramaVenn();
            EstadoSemanticoCompartilhado.Snapshot snapshotTabuleiro =
                    estadoSemanticoCompartilhado.snapshot();
            PlanoUnidadesProcessoTransformacao planoUnidadesProcesso =
                    selecionadorPlanoUnidadesTransformacao.criarPlano(
                            obterTipoRepresentacaoComplementarAtual(),
                            snapshotTabuleiro, situacaoProblemaAtual);
            planoUnidadesProcessoAtual = planoUnidadesProcesso;
            TipoSituacaoAditiva tipoVenn = tipoSituacaoSelecionada;
            cenaDiagramaVennAtual = geradorCenaDiagramaVenn.gerar(tipoVenn, ultimaAreaDiagramaVenn, definicaoDiagramaAtual, valores);
            circulosVenn.clear();
            if (cenaDiagramaVennAtual != null) {
                for (NoDiagramaVenn no : cenaDiagramaVennAtual.getNos()) {
                    CirculoVenn circulo = new CirculoVenn(
                            no.getX(),
                            no.getY(),
                            no.getLargura(),
                            no.getAltura(),
                            no.getRotulo(),
                            no.getValorReferencia(),
                            no.isExibirQuadradinhos()
                    );
                    circulo.formaRetangular = no.getForma()
                            == NoDiagramaVenn.Forma.RETANGULO;
                    circulosVenn.add(circulo);
                }
            }
        }

        private CategoriaProblema categoriaProblemaSelecionada() {
            if (situacaoProblemaAtual != null && situacaoProblemaAtual.getTipo() != null) {
                return construtorResultadoCurado.categoriaDeTipo(situacaoProblemaAtual.getTipo());
            }
            if (tipoSituacaoSelecionada == null) {
                return CategoriaProblema.INDEFINIDA;
            }
            return construtorResultadoCurado.categoriaDeTipo(
                    tipoSituacaoSelecionada);
        }

        private int[] extrairTodosNumerosDoTexto() {
            java.util.List<NumeroEncontrado> numeros = obterNumerosInterpretados();
            int[] resposta = new int[numeros.size()];

            for (int i = 0; i < numeros.size(); i++) {
                resposta[i] = converterTextoNumeroParaInteiro(numeros.get(i).getValorCanonico());
            }

            return resposta;
        }

        private java.util.List<NumeroEncontrado> obterNumerosInterpretados() {
            if (textoProblemaEhMensagemSistema || resultadoInterpretacao == null) {
                return new ArrayList<NumeroEncontrado>();
            }
            return resultadoInterpretacao.getNumeros();
        }

        private int converterTextoNumeroParaInteiro(String texto) {
            if (texto == null) {
                return 0;
            }
            String limpo = texto.trim().replace(",", ".");
            try {
                if (limpo.indexOf('.') >= 0) {
                    return (int) Math.round(Double.parseDouble(limpo));
                }
                return Integer.parseInt(limpo);
            } catch (NumberFormatException ex) {
                return 0;
            }
        }

        private void atualizarTextosFixosDaInterface() {
            atualizarTextosTipConclusaoModelagem();
            // A JMenuBar inteira é reconstruída aqui — cobre idioma,
            // categoria, sobre, usuário, visão de pesquisador e reportar bug
            // de uma vez, no mesmo ponto em que os outros botões abaixo são
            // atualizados (chamado sempre que idioma/categoria/usuário/
            // interpretação mudam).
            criarMenuPrincipal();
            criarBotoesCabecalhoEmbutidos();
            if (botaoRestaurar != null) {
                String descricao = localizacao.texto("ui.tooltip.restore.elements");
                botaoRestaurar.setToolTipText(descricao);
                botaoRestaurar.getAccessibleContext().setAccessibleName(descricao);
                botaoRestaurar.getAccessibleContext().setAccessibleDescription(descricao);
            }
            if (botaoCorrigirCuradoria != null) {
                String descricao = localizacao.texto("ui.tooltip.correctCuration");
                botaoCorrigirCuradoria.setToolTipText(descricao);
                botaoCorrigirCuradoria.getAccessibleContext().setAccessibleName(descricao);
                botaoCorrigirCuradoria.getAccessibleContext().setAccessibleDescription(descricao);
            }
            if (botaoIdiomaSituacao != null) {
                String descricao = descricaoBotaoIdiomaSituacao();
                botaoIdiomaSituacao.setToolTipText(descricao);
                botaoIdiomaSituacao.getAccessibleContext().setAccessibleName(descricao);
                botaoIdiomaSituacao.getAccessibleContext().setAccessibleDescription(descricao);
            }
            if (botaoArtefatoExplicativo != null) {
                String descricao = localizacao.texto("analise.button.tooltip");
                botaoArtefatoExplicativo.setToolTipText(descricao);
                botaoArtefatoExplicativo.getAccessibleContext().setAccessibleName(descricao);
                botaoArtefatoExplicativo.getAccessibleContext().setAccessibleDescription(descricao);
            }
            if (botaoRestaurarDiagrama != null) {
                String descricao = localizacao.texto("ui.tooltip.restore.diagram");
                botaoRestaurarDiagrama.setToolTipText(descricao);
                botaoRestaurarDiagrama.getAccessibleContext().setAccessibleName(descricao);
                botaoRestaurarDiagrama.getAccessibleContext().setAccessibleDescription(descricao);
            }
            // Diferente dos 6 ícones de resposta do quiz (cujo tip é
            // recalculado a cada mouseEntered — ver criarBotaoAtalhoCategoria),
            // estes dois usam o tooltip padrão do Swing (setToolTipText),
            // por isso precisam ser atualizados aqui, junto com os outros
            // botões desta lista, quando o idioma muda.
            if (botaoFerramentaSortearMedidas != null) {
                String descricao = localizacao.texto("ui.tooltip.random.measures");
                botaoFerramentaSortearMedidas.setToolTipText(descricao);
                botaoFerramentaSortearMedidas.getAccessibleContext().setAccessibleName(descricao);
                botaoFerramentaSortearMedidas.getAccessibleContext().setAccessibleDescription(descricao);
            }
            if (botaoFerramentaSortearRelacoes != null) {
                String descricao = localizacao.texto("ui.tooltip.random.relations");
                botaoFerramentaSortearRelacoes.setToolTipText(descricao);
                botaoFerramentaSortearRelacoes.getAccessibleContext().setAccessibleName(descricao);
                botaoFerramentaSortearRelacoes.getAccessibleContext().setAccessibleDescription(descricao);
            }
            atualizarTextosBotoesAjudaContextual();
            atualizarTextoBotaoVerDicaPosicionamento();
            fecharMenuAjudaContextual();
        }

        private void desenharCard(Graphics2D g2, int x, int y, int largura, int altura, int raio) {
            Composite originalComposite = g2.getComposite();
            Stroke originalStroke = g2.getStroke();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
            g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO);
            g2.fillRoundRect(x + 1, y + 2, largura, altura, raio, raio);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2.setColor(COR_SUPERFICIE);
            g2.fillRoundRect(x, y, largura, altura, raio, raio);
            g2.setColor(COR_BORDA);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(x, y, largura, altura, raio, raio);
            g2.setComposite(originalComposite);
            g2.setStroke(originalStroke);
        }

        private void desenharLinhaTracejada(Graphics2D g2, int x, int y, int largura, int altura, int raio) {
            Stroke original = g2.getStroke();
            float[] tracejado = {3.0f, 3.0f};
            g2.setColor(gerard.ui.UITemaGerard.COR_TRACEJADO);
            g2.setStroke(new BasicStroke(0.9f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, tracejado, 0.0f));
            g2.drawRoundRect(x, y, largura, altura, raio, raio);
            g2.setStroke(original);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            desenharCabecalho(g2);
            desenharFaixaAtalhoCategoria(g2);
            desenharTextoProblema(g2);
            desenharAreaDiagrama(g2);
            desenharElementos(g2);
            if (deveExibirDiagramaComplementar()) {
                desenharDiagramaVenn(g2);
            }
            marcadorOrigemArraste.desenhar(g2);
            // O eixo dos inteiros e um painel flutuante de apoio e deve
            // permanecer a frente dos componentes estaticos da tela.
            // Mantemos apenas overlays transitorios (pickup, feedback e
            // anotacoes) acima dele.
            desenharPaineisEixoRelacoes(g2);
            desenharPickupEmPrimeiroPlano(g2);
            desenharFeedbackExplicitoProximidade(g2);
            desenharAnotacaoMouseOver(g2);
        }

        private void desenharCabecalho(Graphics2D g2) {
            g2.setColor(COR_SUPERFICIE);
            g2.fillRect(0, 0, getWidth(), 45);
            g2.setColor(COR_BORDA);
            g2.drawLine(0, 44, getWidth(), 44);

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(COR_TEXTO_SECUNDARIO);
            String status = categoriaSelecionadaParaAtividade
                    ? localizacao.formatar("ui.header.status", idiomaSelecionado.getNome(), tipoSituacaoSelecionada.getSigla())
                    : idiomaSelecionado.getNome();
            FontMetrics fmStatus = g2.getFontMetrics();
            int larguraStatus = fmStatus.stringWidth(status);
            int limiteDireitoStatus = getWidth() - 18;
            int xStatus = Math.max(800, limiteDireitoStatus - larguraStatus);
            g2.drawString(status, xStatus, 28);
        }

        /** Fundo da faixa de atalhos de categoria (ver criarPainelAtalhoCategoria). */
        private void desenharFaixaAtalhoCategoria(Graphics2D g2) {
            g2.setColor(COR_FUNDO);
            g2.fillRect(0, 45, getWidth(), ALTURA_PAINEL_ATALHOS_CATEGORIA);
            g2.setColor(COR_BORDA);
            g2.drawLine(0, 45 + ALTURA_PAINEL_ATALHOS_CATEGORIA - 1, getWidth(), 45 + ALTURA_PAINEL_ATALHOS_CATEGORIA - 1);
            desenharRotulosGruposAtalhoCategoria(g2);
        }

        /**
         * Rótulos "Medidas"/"Relações" (em tom de cinza, COR_TEXTO_SECUNDARIO)
         * acima de cada grupo de 3 ícones — decisão da usuária, 2026-07-28.
         * Reaproveita as mesmas chaves i18n já usadas no menu Categoria
         * (ui.menu.category.measures/relations), centralizados sobre
         * xCentroGrupoMedidas/xCentroGrupoRelacoes (calculados em
         * reposicionarPainelAtalhoCategoria).
         */
        private void desenharRotulosGruposAtalhoCategoria(Graphics2D g2) {
            if (botaoAtalhoComposicao == null) {
                return;
            }
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.setColor(COR_TEXTO_SECUNDARIO);
            FontMetrics fm = g2.getFontMetrics();
            int y = 45 + 18;

            String medidas = localizacao.texto("ui.menu.category.measures");
            g2.drawString(medidas, xCentroGrupoMedidas - fm.stringWidth(medidas) / 2, y);

            String relacoes = localizacao.texto("ui.menu.category.relations");
            g2.drawString(relacoes, xCentroGrupoRelacoes - fm.stringWidth(relacoes) / 2, y);

            // Retângulo delimitador agrupando os 3 ícones de cada grupo
            // (decisão da usuária, 2026-07-28) — mesma cor neutra de borda
            // já usada em desenharCard.
            g2.setColor(COR_BORDA);
            if (areaGrupoMedidas != null) {
                g2.drawRoundRect(areaGrupoMedidas.x, areaGrupoMedidas.y,
                        areaGrupoMedidas.width, areaGrupoMedidas.height, 10, 10);
            }
            if (areaGrupoRelacoes != null) {
                g2.drawRoundRect(areaGrupoRelacoes.x, areaGrupoRelacoes.y,
                        areaGrupoRelacoes.width, areaGrupoRelacoes.height, 10, 10);
            }
        }

        private void desenharTextoProblema(Graphics2D g2) {
            marcadoresFixosTexto.clear();
            reposicionarBotaoAtalhoProximoPasso();

            if (!categoriaSelecionadaParaAtividade) {
                if (aguardandoAdivinhacaoCategoria && textoProblema != null && textoProblema.trim().length() > 0) {
                    // Sorteio em andamento: mostra o texto puro, sem elementos
                    // arrastáveis (só criados em inicializarElementosTexto,
                    // chamado depois da confirmação em
                    // confirmarCategoriaAdivinhada) e sem os botões
                    // contextuais (ainda fazem referência a uma categoria que
                    // o usuário não confirmou ter identificado). Também cobre
                    // o caso em que a categoria sorteada não tem nenhuma
                    // situação curada disponível: iniciarQuizCategoria já
                    // preenche textoProblema com a mensagem de aviso
                    // ("Nenhuma situação-problema curada..."), então essa
                    // mensagem aparece aqui em vez da tela ficar muda —
                    // decisão da usuária, 2026-07-28.
                    desenharTextoProblemaAdivinhacao(g2);
                    ocultarControlesDaAtividadeSemCategoria();
                    return;
                }
                // Mantém a divisão visual da interface desde a inicialização.
                // Somente o conteúdo educativo permanece ausente até que o
                // usuário escolha uma categoria.
                desenharCardEnunciado(g2);
                ocultarControlesDaAtividadeSemCategoria();
                return;
            }

            // Reserva uma faixa à esquerda para as ações contextuais.
            int margemX = 94;
            // A categoria funciona como rótulo de contexto acima do enunciado.
            // Diagramas compostos reservam uma segunda linha para o resumo dos passos.
            int yInicial = 101;
            yInicial += ALTURA_PAINEL_ATALHOS_CATEGORIA;
            int larguraMaxima = getWidth() - margemX - 30;

            desenharCardEnunciado(g2);
            if (botaoIdiomaSituacao != null) botaoIdiomaSituacao.setVisible(situacaoProblemaAtual != null);
            atualizarDisponibilidadeArtefatoExplicativo();
            reposicionarBotaoAjudaTexto();

            g2.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();

            garantirLayoutElementosTexto(fm, margemX, yInicial, larguraMaxima);
            reposicionarBotaoCorrigirCuradoria(fm, margemX, larguraMaxima);
            reposicionarBotaoRestaurar(fm, margemX, larguraMaxima);

            for (int i = 0; i < elementosTexto.size(); i++) {
                ElementoTextoMovel elemento = elementosTexto.get(i);
                elemento.atualizarTamanho(fm);

                if (elemento != handlerElementoTextoMovel.obterElementoAtivo()) {
                    desenharElementoTextoMovel(g2, fm, elemento);
                }
                marcarElementoSemanticoDoTexto(fm, elemento);
            }

            desenharMarcadoresFixosDoTexto(g2);
        }

        private void desenharCardEnunciado(Graphics2D g2) {
            Rectangle area = geometriaAreaEnunciado.obterArea(getWidth());
            desenharCard(g2, area.x, area.y, area.width, area.height, 18);
        }

        /**
         * Enunciado como texto simples (sem elementos arrastáveis) durante a
         * adivinhação de categoria pelo Sortear — ver desenharTextoProblema.
         * Reaproveita o mesmo card de fundo e o utilitário de quebra de linha
         * já usado pelas anotações (quebrarTextoAnotacao).
         */
        private void desenharTextoProblemaAdivinhacao(Graphics2D g2) {
            desenharCardEnunciado(g2);

            int margemX = 94;
            int yInicial = 101 + ALTURA_PAINEL_ATALHOS_CATEGORIA;
            int larguraMaxima = getWidth() - margemX - 30;

            g2.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(COR_TEXTO);
            int y = yInicial;

            // Categoria sem situação curada: mostra o nome da categoria
            // sorteada (não é mais "trapaça" — não há nada pra adivinhar
            // nesse estado) seguido de uma mensagem curta de próximo passo,
            // em vez do texto genérico de aviso — decisão da usuária,
            // 2026-07-28.
            if (textoProblemaEhMensagemSistema && categoriaSorteioOculta != null) {
                g2.drawString(localizacao.rotuloBotaoTipo(categoriaSorteioOculta), margemX, y);
                y += fm.getHeight() + 6;
                java.util.List<String> linhasAcao = quebrarTextoAnotacao(
                        localizacao.texto("ui.problem.noCuratedProblem.tenteNovamente"), fm, larguraMaxima);
                for (String linha : linhasAcao) {
                    g2.drawString(linha, margemX, y);
                    y += fm.getHeight();
                }
                return;
            }

            java.util.List<String> linhas = quebrarTextoAnotacao(textoProblema, fm, larguraMaxima);
            for (String linha : linhas) {
                g2.drawString(linha, margemX, y);
                y += fm.getHeight();
            }
        }

        private void reposicionarBotaoCorrigirCuradoria(FontMetrics fm, int margemX, int larguraMaxima) {
            if (botaoCorrigirCuradoria == null) {
                return;
            }
            boolean exibir = situacaoProblemaAtual != null && !textoProblemaEhMensagemSistema;
            botaoCorrigirCuradoria.setVisible(exibir);
            botaoCorrigirCuradoria.setEnabled(exibir);
            if (exibir) {
                botaoCorrigirCuradoria.setBounds(27, 101 + ALTURA_PAINEL_ATALHOS_CATEGORIA, 26, 26);
            }
        }

        private void reposicionarBotaoRestaurar(FontMetrics fm, int margemX, int larguraMaxima) {
            if (botaoRestaurar == null) {
                return;
            }
            boolean exibir = situacaoProblemaAtual != null && !textoProblemaEhMensagemSistema;
            botaoRestaurar.setVisible(exibir);
            botaoRestaurar.setEnabled(exibir);
            if (exibir) {
                botaoRestaurar.setBounds(27, 70 + ALTURA_PAINEL_ATALHOS_CATEGORIA, 26, 26);
            }
        }

        private void restaurarElementosForaDoDiagrama() {
            handlerItemTextoArrastavel.cancelar();
            handlerElementoTextoMovel.cancelar();
            handlerQuadradinhoVenn.cancelar();
            handlerConectorVergnaud.cancelar();
            limparRealceAlvoProximidade();

            java.util.Iterator<ItemTextoArrastavel> iterador = itensArrastaveis.iterator();
            while (iterador.hasNext()) {
                ItemTextoArrastavel item = iterador.next();
                if (!itemEstaSobreElementoDoDiagrama(item)) {
                    if (item == itemFocado) {
                        itemFocado = null;
                    }
                    if (item == itemQuestionadoPersistente) {
                        limparQuestionamentoPersistente();
                    }
                    iterador.remove();
                }
            }

            if (!existeIncognitaEstadoFinalPosicionadaCorretamente()) {
                desabilitarSincronizacaoEstadoFinal();
            }

            elementoTextoFocado = null;
            layoutTextoInicializado = false;
            larguraUltimoLayoutTexto = -1;
            mostrarAnotacaoMouseOver = false;
            textoAnotacaoMouseOver = "";
            verificarConclusaoModelagem();
            repaint();
        }

        private void reposicionarBotaoRestaurarDiagrama(Rectangle limiteVergnaud) {
            if (botaoRestaurarDiagrama == null || limiteVergnaud == null) {
                return;
            }
            int larguraBotao = 26;
            int alturaBotao = 26;
            // Mantém o botão dentro do painel de Vergnaud, mas alinhado
            // exatamente à coluna dos botões contextuais do enunciado.
            int x = (botaoRestaurar != null)
                    ? botaoRestaurar.getX()
                    : limiteVergnaud.x + 3;
            int y = limiteVergnaud.y + 10;
            botaoRestaurarDiagrama.setBounds(x, y, larguraBotao, alturaBotao);
            botaoRestaurarDiagrama.setVisible(categoriaSelecionadaParaAtividade);
        }

        /**
         * Restauração explícita solicitada pelo usuário. Junto com Sortear,
         * é a única operação autorizada a limpar o estado dos diagramas.
         */
        private void restaurarModelagemDiagrama() {
            cancelarEfeitosArraste();
            reiniciarConclusaoModelagem();
            controladorEstadoAtividade.registrar(AcaoAtividade.RESTAURAR);
            handlerItemTextoArrastavel.cancelar();
            itemFocado = null;
            handlerElementoTextoMovel.cancelar();
            elementoTextoFocado = null;
            handlerQuadradinhoVenn.cancelar();
            quadradinhoVennFocado = null;
            handlerConectorVergnaud.cancelar();
            alvoRealcadoPorProximidade = null;
            desativarPaineisEixosRelacoes();
            seletorOperacaoRelacaoAluno.desativar();
            seletorOperacaoEstadoTransformacaoAluno.desativar();

            itensArrastaveis.clear();
            marcadoresFixosTexto.clear();
            elementosTexto.clear();
            elementosVergnaud.clear();
            limparEstadoDicaPosicionamento();
            conectoresVergnaud.clear();
            circulosVenn.clear();
            quadradinhosVenn.clear();

            limparRealceAlvoProximidade();
            limparQuestionamentoPersistente();
            limparSinalDivergentePersistente();
            desabilitarSincronizacaoEstadoFinal();
            mostrarAnotacaoMouseOver = false;
            textoAnotacaoMouseOver = "";

            layoutTextoInicializado = false;
            larguraUltimoLayoutTexto = -1;

            cenaDiagramaAtual = null;
            cenaDiagramaVennAtual = null;
            ultimaAreaDiagramaVenn = null;
            estadoSemanticoCompartilhado.limpar(tipoSituacaoSelecionada);
            indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};
            inicializarElementosTexto();
            inicializarDiagramaVergnaud();
            inicializarDiagramaVenn();

            repaint();
        }

        private String obterPapelIncognitaAtual() {
            if (resultadoInterpretacao == null
                    || resultadoInterpretacao.getPapeis() == null) {
                return "papel.valor";
            }
            for (PapelElementoInterpretado papel :
                    resultadoInterpretacao.getPapeis()) {
                if (papel != null && !papel.isConhecido()
                        && papel.getChavePapel() != null
                        && papel.getChavePapel().trim().length() > 0) {
                    return papel.getChavePapel().trim();
                }
            }
            return "papel.valor";
        }

        private boolean elementoEhPapelDaIncognita(ElementoVergnaud elemento) {
            if (elemento == null) return false;
            return politicaPreenchimentoIncognita.ehPapelDaIncognita(
                    obterPapelSemanticoDoElemento(elemento),
                    obterPapelIncognitaAtual());
        }

        private String obterPapelSemanticoDoElemento(ElementoVergnaud elemento) {
            if (elemento == null || elemento.chavePapelSemantico == null
                    || elemento.chavePapelSemantico.trim().isEmpty()) {
                return "papel.valor";
            }
            return elemento.chavePapelSemantico.trim();
        }

        private ElementoVergnaud encontrarElementoVergnaudPorPapel(
                String chavePapel) {
            if (chavePapel == null || elementosVergnaud == null) {
                return null;
            }
            for (ElementoVergnaud elemento : elementosVergnaud) {
                if (elemento != null
                        && chavePapel.equals(elemento.chavePapelSemantico)) {
                    return elemento;
                }
            }
            return null;
        }

        private ItemTextoArrastavel encontrarIncognitaOriginalSobreElemento(
                ElementoVergnaud elemento) {
            ItemTextoArrastavel item = encontrarItemSobreElemento(elemento);
            return item != null && item.representaIncognitaOriginal()
                    ? item : null;
        }

        private boolean incognitaPreenchidaPeloProtocoloMouseTexto() {
            String papelIncognita = obterPapelIncognitaAtual();
            for (ItemTextoArrastavel item : itensArrastaveis) {
                if (item == null || !item.estaNoDiagrama()
                        || !item.representaIncognitaOriginal()
                        || !item.isPreenchidoPeloProtocoloMouseTexto()) {
                    continue;
                }
                ElementoVergnaud alvo = encontrarElementoVergnaudPorItem(item);
                if (alvo == null) continue;
                if (politicaPreenchimentoIncognita
                        .ehPapelDaIncognita(
                                obterPapelSemanticoDoElemento(alvo),
                                papelIncognita)) {
                    return true;
                }
            }
            return false;
        }

        private boolean devePreservarMarcadorIncognita(
                ElementoVergnaud elemento) {
            if (elemento == null) return false;
            ItemTextoArrastavel incognita =
                    encontrarIncognitaOriginalSobreElemento(elemento);
            boolean preenchida = (incognita != null
                    && incognita.isPreenchidoPeloProtocoloMouseTexto())
                    || incognitaPreenchidaPeloProtocoloMouseTexto();
            return politicaPreenchimentoIncognita.devePreservarMarcador(
                    obterPapelSemanticoDoElemento(elemento),
                    obterPapelIncognitaAtual(), preenchida);
        }

        private int obterIndiceIncognitaProtegidaNoEstadoCompartilhado() {
            String papelIncognita = obterPapelIncognitaAtual();
            for (int i = 0; i < indicesElementosEstadoCompartilhado.length; i++) {
                int indiceReal = indicesElementosEstadoCompartilhado[i];
                if (indiceReal < 0 || indiceReal >= elementosVergnaud.size()) {
                    continue;
                }
                if (politicaPreenchimentoIncognita.ehPapelDaIncognita(
                        obterPapelElementoParaConclusao(indiceReal),
                        papelIncognita)) {
                    return i;
                }
            }
            return -1;
        }

        private void informarPosicionamentoIncognitaAntesDaEdicao(
                ElementoVergnaud elemento) {
            textoAnotacaoMouseOver = localizacao.texto(
                    "ui.unknown.placeBeforeEdit");
            mostrarAnotacaoMouseOver = true;
            mouseOverX = elemento.x + elemento.largura;
            mouseOverY = elemento.y + elemento.altura / 2;
            repaint();
        }

        private String obterPapelElementoParaConclusao(int indiceElemento) {
            return catalogoPapeisSemanticos.obterChavePapelDoElemento(
                    tipoSituacaoSelecionada, indiceElemento);
        }

        private java.util.List<String> capturarPapeisEsperadosConclusao() {
            java.util.List<String> papeisDaCena = new ArrayList<String>();
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                papeisDaCena.add(obterPapelElementoParaConclusao(i));
            }
            return seletorPapeisConclusaoModelagem.selecionar(
                    papeisDaCena, situacaoProblemaAtual, localizacao);
        }

        private java.util.List<EstadoPosicionamentoModelagem> capturarPosicionamentosConclusao() {
            java.util.List<EstadoPosicionamentoModelagem> estados =
                    new ArrayList<EstadoPosicionamentoModelagem>();
            java.util.List<String> papeisEsperados =
                    capturarPapeisEsperadosConclusao();
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                ElementoVergnaud alvo = elementosVergnaud.get(i);
                String papelAlvo = obterPapelElementoParaConclusao(i);
                if (!papeisEsperados.contains(papelAlvo)) {
                    continue;
                }

                ItemTextoArrastavel item = encontrarItemSobreElemento(alvo);
                if (item != null) {
                    estados.add(new EstadoPosicionamentoModelagem(
                            obterChavePapelExataDoItem(item),
                            papelAlvo,
                            item.valor,
                            item.estaNoDiagrama(),
                            item.representaIncognitaOriginal(),
                            item.isPreenchidoPeloProtocoloMouseTexto(),
                            item.representaIncognitaOriginal()
                                    ? valorDigitadoCorrespondeAoEsperado(papelAlvo, item.valor)
                                    : null,
                            calcularEstadoModificado(papelAlvo, item.valor)));
                } else {
                    String valorDigitado = alvo.textoEditavel == null
                            ? "" : alvo.textoEditavel.trim();
                    estados.add(new EstadoPosicionamentoModelagem(
                            papelAlvo,
                            papelAlvo,
                            valorDigitado,
                            valorDigitado.length() > 0,
                            false,
                            false,
                            null,
                            calcularEstadoModificado(papelAlvo, valorDigitado)));
                }
            }
            return estados;
        }

        /**
         * AG_AE — próximo papel-dado ainda não resolvido, em ordem canônica
         * (mesma ordem de elementosVergnaud/capturarPapeisEsperadosConclusao)
         * — nunca a incógnita atual (a dica indica onde uma frase-dado
         * pertence; o valor da incógnita nunca é antecipado, ver
         * gerard-consistencia-estado). null quando não há mais nenhum papel
         * pendente para dica (todos os papéis-dado já resolvidos, ou só
         * resta a incógnita).
         *
         * "Resolvido ou não" é regra semântica, não de interface
         * (gerard-domain-model-first) — delega a
         * AvaliadorConclusaoModelagem.obterProximoPapelNaoResolvido, a
         * mesma classe (e o mesmo critério de compatibilidade de papéis)
         * que já decide a conclusão da modelagem inteira; esta função só
         * acrescenta o filtro de representação — só ofereça dica quando
         * houver, de fato, uma frase para mostrar, senão o botão "Ver
         * dica" apareceria visível e, ao clicar, nada aconteceria
         * (situação digitada livremente, sem curadoria, ou papel sem
         * token semântico próprio) — e isso É uma questão de
         * representação/interface, não de domínio.
         */
        private String obterProximoPapelNaoResolvidoParaDica() {
            String papelIncognita = obterPapelIncognitaAtual();
            java.util.List<EstadoPosicionamentoModelagem> posicionamentos =
                    capturarPosicionamentosConclusao();
            for (String papel : capturarPapeisEsperadosConclusao()) {
                if (papel.equals(papelIncognita)) {
                    continue;
                }
                if (!avaliadorConclusaoModelagem.papelResolvido(papel, posicionamentos)
                        && obterFraseParaDicaPosicionamento(papel) != null) {
                    return papel;
                }
            }
            return null;
        }

        /**
         * AG_AE — texto exato do trecho do enunciado vinculado ao papel
         * indicado (a mesma frase/token que o participante vê e arrasta do
         * texto para o diagrama) — não reconstrói o texto a partir da
         * curadoria numérica, só localiza o elemento de texto já existente
         * cujo vínculo semântico é esse papel. null se não houver (situação
         * digitada livremente, sem curadoria, ou papel sem token próprio).
         */
        private String obterFraseParaDicaPosicionamento(String papel) {
            if (papel == null) {
                return null;
            }
            // Fonte primária: elementosTexto — os pedaços do enunciado
            // ainda não arrastados para o diagrama (é isso que o
            // participante precisa localizar e mover). Só considera
            // elementos com vínculo semântico explícito (não usa o
            // fallback posicional de obterChavePapelExataDoElemento, que
            // arriscaria apontar uma palavra qualquer do texto como se
            // fosse a frase do papel).
            for (ElementoTextoMovel elemento : elementosTexto) {
                if (elemento == null || !elemento.possuiVinculoSemantico()
                        || elemento.representaIncognitaOriginal()) {
                    continue;
                }
                if (papel.equals(elemento.chavePapelSemantico)
                        && elemento.valor != null && elemento.valor.trim().length() > 0) {
                    return elemento.valor;
                }
            }
            // Fallback: o item já foi arrastado (para o lugar certo ou
            // errado) — ainda mostra a dica com o texto que ele carrega.
            for (ItemTextoArrastavel item : itensArrastaveis) {
                if (item != null && !item.representaIncognitaOriginal()
                        && papel.equals(obterChavePapelExataDoItem(item))
                        && item.valor != null && item.valor.trim().length() > 0) {
                    return item.valor;
                }
            }
            return null;
        }

        /**
         * AG_AE — chamado sempre que elementosVergnaud é limpo para um novo
         * diagrama: nenhuma dica exibida (nem ação aberta) deve sobreviver
         * à troca de situação-problema, senão a âncora ficaria apontando
         * para um ElementoVergnaud de uma modelagem anterior.
         */
        private void limparEstadoDicaPosicionamento() {
            mostrarDicaPosicionamentoPersistente = false;
            elementoDicaPosicionamentoPersistente = null;
            papelDicaPosicionamentoAtual = null;
            scaffoldingAutomatizacaoPassos.limpar();
        }

        /**
         * AG_AE — botão "Ver dica" (sob demanda): revela, um papel por vez
         * e em ordem progressiva, qual frase pertence a cada papel-dado
         * ainda não posicionado corretamente. Nunca indica a incógnita.
         * Cada exibição vira um evento FEEDBACK_EXIBIDO real, correlacionado
         * por action_id enquanto o mesmo papel continuar pendente.
         */
        private void mostrarProximaDicaPosicionamento() {
            String papel = obterProximoPapelNaoResolvidoParaDica();
            if (papel == null) {
                mostrarDicaPosicionamentoPersistente = false;
                elementoDicaPosicionamentoPersistente = null;
                papelDicaPosicionamentoAtual = null;
                if (existeSeletorOperacaoPendenteParaDica()) {
                    mostrarDicaSeletorOperacaoPendente();
                }
                return;
            }
            int indice = obterIndiceElementoVergnaudPorPapel(papel);
            if (indice < 0 || indice >= elementosVergnaud.size()) {
                return;
            }
            String frase = obterFraseParaDicaPosicionamento(papel);
            if (frase == null) {
                return;
            }
            elementoDicaPosicionamentoPersistente = elementosVergnaud.get(indice);
            papelDicaPosicionamentoAtual = papel;
            mostrarDicaPosicionamentoPersistente = true;
            String actionId = scaffoldingAutomatizacaoPassos.obterOuIniciarAcao(papel);
            registrarFeedbackExibido("AG_AE",
                    gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding.VISUAL,
                    "dica de posicionamento sob demanda; papel=" + papel,
                    actionId);
            registrarAcaoGranular(
                    "SELECIONAR",
                    "Ver dica de posicionamento",
                    "Diagrama de Vergnaud",
                    "MODELAGEM_LINGUISTICA",
                    localizacao.texto("ui.hint.stepPlacement.button"),
                    "papel=" + papel + "; action_id=" + actionId,
                    "O participante solicitou a dica de posicionamento (AG_AE).");
            repaint();
        }

        /**
         * Item 36 (2026-08-23) — true quando não resta mais nenhum
         * papel-dado pendente de posicionar (obterProximoPapelNaoResolvidoParaDica()
         * == null) mas ainda existe um seletor de soma/subtração ativo que
         * o aluno não respondeu ou respondeu errado
         * (operacoesDeSomaSubtracaoRespondidasCorretamente() == false).
         * Existe para o botão "Ver dica" continuar útil depois que o
         * diagrama já está todo posicionado corretamente — antes deste
         * item, esse era exatamente o momento em que o botão sumia sem
         * avisar nada, deixando o aluno sem saber que a etapa pendente
         * virou o seletor.
         */
        private boolean existeSeletorOperacaoPendenteParaDica() {
            return obterProximoPapelNaoResolvidoParaDica() == null
                    && !operacoesDeSomaSubtracaoRespondidasCorretamente();
        }

        /**
         * "Estado modificado" (2026-08-06): true quando o valor atual do
         * papel diverge do curado do problema original — para QUALQUER
         * papel, não só a incógnita (ver getEstadoModificado em
         * EstadoPosicionamentoModelagem). Um papel-dado divergir não é em si
         * um erro: o usuário pode ter alterado o valor por outra
         * representação (ex.: eixo x) e estar operando legitimamente a
         * partir do novo valor — ver obterValorAlvoParaPapel, que já
         * recalcula o alvo da incógnita a partir disso. Este método só
         * detecta a divergência; não julga se ela é intencional.
         *
         * @return null quando não há curado disponível para conferir (mesmo
         *         critério de valorDigitadoCorrespondeAoEsperado).
         */
        private Boolean calcularEstadoModificado(String papel, String valorAtual) {
            if (situacaoProblemaAtual == null || papel == null) {
                return null;
            }
            SemanticaCuradaSituacao.PapelCurado curado =
                    SemanticaCuradaSituacao.buscar(
                            situacaoProblemaAtual, localizacao, papel);
            return curado == null ? null : curado.estadoModificadoPor(valorAtual);
        }

        /**
         * @return null quando não há valor curado disponível para conferir
         *         (ex.: problema digitado livremente, sem situação curada
         *         carregada, ou valor digitado ainda não numérico) — nesse
         *         caso quem chama não deve bloquear a conclusão por esse
         *         motivo; caso contrário, se o valor digitado bate com o
         *         valor curado do papel.
         */
        private Boolean valorDigitadoCorrespondeAoEsperado(String papel, String valorDigitado) {
            return servicoAvaliacaoAcaoIncognita.correspondeAoEsperado(
                    obterIncognitaSemanticaAtual(), situacaoProblemaAtual,
                    localizacao, papel, estadoSemanticoCompartilhado.snapshot(),
                    obterIndiceIncognitaProtegidaNoEstadoCompartilhado(),
                    valorDigitado);
        }

        private IncognitaQuantitativa obterIncognitaSemanticaAtual() {
            return contextoIncognitaAtual == null
                    || !contextoIncognitaAtual.getIncognita().isPresent()
                            ? null : contextoIncognitaAtual.getIncognita().get();
        }

        /**
         * Valor-alvo para conferir a incógnita — "estado modificado"
         * (2026-08-06): quando o papel indicado é a incógnita atual e a
         * arquitetura rica (Fase B1) já tem, no Snapshot corrente de
         * EstadoSemanticoCompartilhado, um valor calculado a partir dos
         * papéis-dado ATUAIS (Estado inicial/Transformação/etc.), esse valor
         * recalculado é o alvo — não o curado original do problema. Isso
         * cobre o caso em que o usuário alterou um papel-dado por outra
         * representação (ex.: arrastando o eixo x) e quer continuar operando
         * a partir desse novo valor, em vez de ser cobrado pela resposta do
         * problema original.
         *
         * Cai no curado (comportamento anterior, inalterado) quando: o papel
         * não é a incógnita atual; ou os papéis-dado ainda não estão todos
         * conhecidos no Snapshot (nesse caso não há recálculo válido ainda,
         * independente do tipo de situação — inclui os 2 tipos "Em
         * construção", hoje inalcançáveis pela UI). O valor do Snapshot usado
         * aqui vem da mesma resolução (rica, Fase B1, ou genérica de
         * fallback para os tipos ainda não cobertos por ela) que já preenche
         * a incógnita no "primeiro preenchimento" — não é um cálculo novo.
         */
        private Integer obterValorAlvoParaPapel(String papel) {
            return resolvedorValorEsperadoIncognita.resolver(
                    situacaoProblemaAtual, localizacao, papel,
                    obterPapelIncognitaAtual(),
                    estadoSemanticoCompartilhado.snapshot(),
                    obterIndiceIncognitaProtegidaNoEstadoCompartilhado());
        }

        /**
         * Encaminha a seleção ao proprietário do papel. A Main cria somente o
         * contexto observável e materializa o registro já avaliado; não lê o
         * valor curado nem compara sinais.
         *
         * @return o registro factual, ou null quando a situação/papel não
         *         possui critério curado aplicável (comportamento legado das
         *         situações digitadas livremente e das incógnitas sem valor).
         */
        private RegistroAcaoEscolhaSinalPapelQuantitativo
                avaliarEscolhaSinalNumeroRelativo(
                        String papel, String sinalEscolhido, String base) {
            TentativaEscolhaSinalPapelQuantitativo tentativa =
                    papel == null ? null : tentativasEscolhaSinalAtual.get(papel);
            if (tentativa == null) {
                return null;
            }

            ContextoAcaoInstrumental contextoInstrumental =
                    new ContextoAcaoInstrumental(
                            "Escolher sinal do número relativo",
                            "Selecionar uma opção de sinal",
                            "Menu de radio buttons",
                            "Representar perda ou ganho com sinal",
                            papel + ".sinal",
                            "MENU_SINAL",
                            "valor=" + base + "; sinal=" + sinalEscolhido,
                            "Sinal selecionado para o papel quantitativo",
                            participantesSemanticosDaSituacaoAtual());
            RegistroAcaoEscolhaSinalPapelQuantitativo registro =
                    tentativa.avaliarEscolha(
                            OpcaoSinalNumeroInteiro.doSimbolo(sinalEscolhido),
                            contextoInstrumental);

            loggerInteracaoGerard.registrarAcaoInstrumentalUsuario(registro);
            conectorVereditoModelador.registrarAcaoInstrumental(
                    loggerInteracaoGerard.getUsuarioAtual(), registro,
                    gerard.agente.modelousuario.NivelSuporte.NENHUM,
                    registro.getActionId());
            return registro;
        }

        /** Compatibilidade para uma seleção sem critério normativo curado. */
        private void registrarEscolhaSinalSemCriterio(String base, String sinal) {
            registrarLogUsuario(
                    "Escolher sinal do número relativo",
                    "C",
                    "Menu de radio buttons",
                    "Número relativo do diagrama",
                    "Representar perda ou ganho com sinal",
                    "OBJ4",
                    "O número relativo deve ser informado com sinal de mais ou de menos.",
                    "MENU_SINAL",
                    "valor=" + base + "; sinal=" + sinal
            );
        }

        /** Chave de papel transportada pela descrição semântica da cena. */
        private String obterChavePapelDoNumeroRelativo(ElementoVergnaud numeroRelativo) {
            if (numeroRelativo == null) {
                return null;
            }
            String chavePapel = numeroRelativo.chavePapelSemantico;
            return chavePapel == null || chavePapel.trim().isEmpty()
                    ? null
                    : chavePapel.trim();
        }

        /**
         * Verdadeiro só quando o item é a incógnita original, já foi
         * preenchida pelo protocolo mouse/texto, e o valor diverge do valor
         * curado da situação (quando há um valor curado disponível para
         * conferir). Usado para (1) mostrar a pergunta de confirmação ao
         * usuário e (2) adiar a propagação para as outras representações até
         * a incógnita ser confirmada correta — ver
         * atualizarRepresentacoesReativasAposAlteracaoDoItem.
         */
        private boolean incognitaAguardandoConfirmacaoDeValor(ItemTextoArrastavel item) {
            if (item == null || !item.representaIncognitaOriginal()
                    || !item.isPreenchidoPeloProtocoloMouseTexto()) {
                return false;
            }
            return Boolean.FALSE.equals(
                    valorDigitadoCorrespondeAoEsperado(obterPapelIncognitaAtual(), item.valor));
        }

        /**
         * Solicita ao agregado da tentativa uma única ação de restauração e
         * entrega o registro já constituído à infraestrutura. A tela conhece
         * o botão e materializa o efeito visual; não cria action_id, não
         * associa a restauração à sequência rejeitada e não reinventa a regra
         * de encerramento pertencente ao domínio.
         */
        private void registrarAcaoRestauracao(
                gerard.dominio.campoaditivo.TipoRestauracaoModelagem tipo,
                String tarefa,
                String instrumentoOrganizacao,
                String instrumentoArtefato,
                String funcaoDoArtefato,
                String objeto,
                String regras,
                String origemEvento) {
            gerard.dominio.campoaditivo.TentativaModelagemAditiva tentativa =
                    new gerard.dominio.campoaditivo.TentativaModelagemAditiva(
                            loggerInteracaoGerard.getTentativaAtualId());
            gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem registro =
                    tentativa.restaurar(tipo, OrigemAcao.ORIGEM_USUARIO,
                            tentativasIncognitaAtual);
            String detalhes = "tipo_restauracao=" + registro.getTipo().name()
                    + "; tentativa_id=" + registro.getTentativaId()
                    + "; papeis_participantes=" + registro.getPapeisParticipantes()
                    + "; sequencias_rejeicao_encerradas="
                    + registro.getSequenciasRejeicaoEncerradas();
            registrarLogUsuarioComIdentidade(
                    "SELECIONAR", tarefa, "-", instrumentoOrganizacao,
                    instrumentoArtefato, funcaoDoArtefato, objeto, regras,
                    origemEvento, detalhes, registro.getActionId(),
                    registro.getRejectionSequenceId());
        }

        /**
         * Garante que tentativasIncognitaAtual rastreia o papel indicado —
         * recria a instância quando o papel da incógnita atual mudou desde a
         * última chamada (nova situação, ou o sujeito avançou para outro
         * item). Usada só para contagem (registrarTentativa/restaurar);
         * nunca para armazenar um valor real — isso continua com
         * estadoSemanticoCompartilhado. Ver PapelQuantitativo.registrarTentativa
         * — REFERENCE.md §4.8.
         */
        /**
         * A chave inclui o id da situação-problema atual, não só o nome do
         * papel (2026-08-07) — corrige uma lacuna encontrada ao implementar
         * a visibilidade do diagrama complementar ligada a
         * estaBloqueadoPorLimiteTentativas(): duas situações-problema
         * diferentes podem ter incógnitas com o mesmo nome de papel (ex.:
         * "papel.todo" em duas situações de Composição distintas). Sem o id
         * da situação na chave, uma situação nova herdava o bloqueio de
         * tentativas da situação anterior — a primeira tentativa real do
         * participante na situação nova já nascia bloqueada, sem nunca ter
         * sido avaliada. Mesmo bug seria latente mesmo sem a mudança de
         * visibilidade do diagrama, só não tinha efeito visível ainda.
         */
        private void garantirTentativasIncognitaAtual(String papelAtual) {
            String chave = papelAtual == null || papelAtual.trim().length() == 0
                    ? "papel.valor" : papelAtual.trim();
            chave = chave + "@" + (situacaoProblemaAtual == null ? "" : situacaoProblemaAtual.getId());
            if (tentativasIncognitaAtual == null
                    || !chave.equals(papelDaTentativaAtual)) {
                tentativasIncognitaAtual = new gerard.dominio.campoaditivo.PapelQuantitativo(
                        chave, "Incógnita atual",
                        gerard.semantica.numero.DominioNumerico.INTEIROS,
                        new gerard.dominio.campoaditivo.DescritorRepresentacaoPapel(
                                gerard.dominio.campoaditivo.TipoRepresentacaoAbstrata.FIGURA_RETANGULAR,
                                "rotulo.papel.incognitaAtual"),
                        gerard.dominio.campoaditivo.evento.PublicadorEventoDominio.NENHUM);
                papelDaTentativaAtual = chave;
            }
        }

        /**
         * Aviso quando o limite de tentativas rejeitadas é atingido
         * (REFERENCE.md §4.8). O mecanismo (contagem, bloqueio, action_id)
         * foi implementado em 2026-08-07 sem conteúdo pedagógico decidido —
         * ver TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md. Em
         * 2026-08-07 (mesmo dia, pedido em seguida) a mensagem
         * (ui.notice.attemptLimitReached, mensagens_*.properties) ganhou uma
         * dica curta — revisar a relação entre as quantidades já conhecidas,
         * sem revelar o valor — seguindo a categoria "mensagem informativa"
         * de gerard-scaffolding-interacao (texto curto, sem mecanismo
         * estrutural dedicado). Este é um primeiro rascunho de conteúdo, não
         * uma validação pedagógica definitiva; a lógica de seleção do
         * repertório de Scaffolding em dois eixos continua não decidida.
         */
        private void mostrarAvisoLimiteTentativasAtingido(
                gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel resultado) {
            String nomePapel = localizacao.texto(obterPapelIncognitaAtual());
            String mensagem = localizacao.formatar("ui.notice.attemptLimitReached", nomePapel);
            JOptionPane.showMessageDialog(this, mensagem,
                    localizacao.texto("ui.dialog.confirm"), JOptionPane.INFORMATION_MESSAGE);
            registrarFeedbackExibido("AG_EMCME (mensagem)",
                    gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding.VISUAL,
                    "aviso do limite de tentativas, com dica de revisar a relação entre quantidades",
                    resultado == null ? null : resultado.getActionId(),
                    resultado == null ? null : resultado.getRejectionSequenceId());
        }

        /**
         * Traduz o evento FEEDBACK_EXIBIDO (REFERENCE.md §4.8,
         * TipoEventoPapel.FEEDBACK_EXIBIDO) para o log real de produção —
         * chamado no exato instante em que o critério de confirmação da
         * modalidade já foi satisfeito: "renderizado" para modalidades
         * passivas (VISUAL/SONORA), logo depois do componente ser de fato
         * mostrado; "affordance ativada" para interativas
         * (HAPTICA/MANIPULATIVA/GUIADA_POR_MOVIMENTO), no instante em que o
         * mecanismo de interação passa a estar disponível para o
         * participante operar — nunca quando ele de fato opera (isso é uma
         * ação própria dele, registrada separadamente). O evento nunca
         * afirma que o participante percebeu, entendeu ou prestou atenção
         * ao apoio (Seção 4.10).
         *
         * Não instancia gerard.dominio.campoaditivo.evento.EventoPapelQuantitativo
         * — essa classe continua isolada no pacote piloto por design (ver
         * seu javadoc, "não é referenciado por Main.java"); aqui só se usa
         * o mesmo vocabulário (estiloScaffolding, modalidade) para gerar uma
         * linha real no log de produção, o mesmo padrão já usado em toda
         * esta sessão (Main traduz o fato, nunca importa a classe do
         * piloto).
         */
        private void registrarFeedbackExibido(String estiloScaffolding,
                gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding modalidade,
                String detalhesExtra) {
            registrarFeedbackExibido(estiloScaffolding, modalidade, detalhesExtra, null);
        }

        /**
         * Variante com correlação explícita: eventos derivados de uma única
         * ação carregam o mesmo action_id. Na P3.1, os feedbacks apresentados
         * em consequência da terceira rejeição também carregam o
         * rejection_sequence_id daquela sequência. Uma nova solicitação do
         * participante constitui outra ação e não deve reutilizar action_id.
         * Os pontos antigos que não fornecem identidade continuam compatíveis.
         */
        private void registrarFeedbackExibido(String estiloScaffolding,
                gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding modalidade,
                String detalhesExtra, String actionId) {
            registrarFeedbackExibido(estiloScaffolding, modalidade, detalhesExtra,
                    actionId, null);
        }

        private void registrarFeedbackExibido(String estiloScaffolding,
                gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding modalidade,
                String detalhesExtra, String actionId, String rejectionSequenceId) {
            String criterio = modalidade.ehPassiva()
                    ? "renderizado (modalidade passiva)"
                    : "affordance ativada (modalidade interativa)";
            registrarLogComputadorComIdentidade(
                    "Exibir apoio pedagógico (Scaffolding)",
                    "Repertório de Scaffolding (REFERENCE.md §4.8)",
                    estiloScaffolding,
                    "Apresentar apoio pedagógico ao participante",
                    "Evento não afirma que o participante percebeu, entendeu ou "
                            + "prestou atenção ao apoio — só que ele foi apresentado.",
                    "FEEDBACK_EXIBIDO",
                    "estilo=" + estiloScaffolding + "; modalidade=" + modalidade
                            + "; criterio=" + criterio
                            + (detalhesExtra == null || detalhesExtra.length() == 0 ? "" : "; " + detalhesExtra)
                            + (actionId == null || actionId.length() == 0 ? "" : "; action_id=" + actionId),
                    actionId, rejectionSequenceId);
        }

        /**
         * Pergunta de confirmação quando o valor diverge do curado. Decisão
         * do usuário em 2026-07-22: a modelagem só volta a propagar/concluir
         * depois que o valor correto for inserido — "Sim" (o usuário insiste
         * que está certo) mostra uma dica curta da operação a fazer e ainda
         * assim não libera a propagação; "Não" só permite tentar de novo,
         * sem dica adicional.
         *
         * @return true quando não há divergência a conferir (sem valor
         *         curado disponível, ou o valor já bate); false sempre que
         *         houver divergência, independente da resposta do usuário.
         */
        /** Compatibilidade dos demais protocolos que também concluem uma incógnita. */
        private boolean confirmarValorIncognitaAceito(ItemTextoArrastavel item) {
            String papelAlvo = obterPapelIncognitaAtual();
            garantirTentativasIncognitaAtual(papelAlvo);
            gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel identidade =
                    tentativasIncognitaAtual.iniciarAcaoInstrumental(
                            gerard.dominio.campoaditivo.OrigemAcao.ORIGEM_USUARIO);
            return confirmarValorIncognitaAceito(item, identidade, false);
        }

        private boolean confirmarValorIncognitaAceito(ItemTextoArrastavel item,
                gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel identidadeAcao,
                boolean registrarAcaoDoProtocoloTexto) {
            if (registrarAcaoDoProtocoloTexto) {
                return confirmarValorIncognitaTexto(item, identidadeAcao);
            }
            if (item == null || !item.representaIncognitaOriginal()
                    || !item.isPreenchidoPeloProtocoloMouseTexto()) {
                return true;
            }
            String papelAlvo = obterPapelIncognitaAtual();
            garantirTentativasIncognitaAtual(papelAlvo);
            atualizarContextoAdaptativoIncognitaAtual();
            IncognitaQuantitativa incognita = obterIncognitaSemanticaAtual();
            if (incognita == null) {
                return true;
            }
            if (identidadeAcao == null) {
                identidadeAcao = tentativasIncognitaAtual.iniciarAcaoInstrumental(
                        gerard.dominio.campoaditivo.OrigemAcao.ORIGEM_USUARIO);
            }

            registrarPapeisDadoModificadosSeHouver();
            ContextoAcaoInstrumental contextoInstrumental =
                    new ContextoAcaoInstrumental(
                            "Quantificar a incógnita",
                            "Editor numérico",
                            "Item do diagrama",
                            "Informar valor numérico para o papel designado como incógnita",
                            papelAlvo,
                            "EDICAO_ITEM",
                            "valor=" + (item.valor == null ? "" : item.valor),
                            "Valor numérico informado para a incógnita",
                            participantesSemanticosDaSituacaoAtual());
            RegistroAcaoInstrumental registro = servicoAvaliacaoAcaoIncognita.avaliarAcao(
                    incognita, identidadeAcao,
                    gerard.dominio.atividade.TarefaInteracao.QUANTIFICAR,
                    situacaoProblemaAtual, localizacao, papelAlvo,
                    estadoSemanticoCompartilhado.snapshot(),
                    obterIndiceIncognitaProtegidaNoEstadoCompartilhado(),
                    item.valor, contextoInstrumental);
            loggerInteracaoGerard.registrarAcaoInstrumentalUsuario(registro);
            conectorVereditoModelador.registrarAcaoInstrumental(
                    loggerInteracaoGerard.getUsuarioAtual(), registro, null,
                    registro.getActionId());

            gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel resultadoTentativa =
                    registro.getResultadoTentativa().isPresent()
                            ? registro.getResultadoTentativa().get() : null;
            ResultadoExecucaoAjudaIncognita resultadoAjuda = null;
            if (registro.foiErrada() && resultadoTentativa != null
                    && registro.getDiagnostico().isPresent()) {
                resultadoAjuda = executorAjudaIncognita.executar(
                        contextoIncognitaAtual,
                        registro.getDiagnostico().get(),
                        resultadoTentativa.getRejeicoesConsecutivas(),
                        new ContextoRegistroAjuda(
                                registro.getActionId(),
                                registro.getRejectionSequenceId()));
            }
            if (!registro.possuiCriterioAplicavel() || registro.foiCorreta()) {
                return true;
            }
            if (resultadoAjuda != null && resultadoAjuda.foiMaterializada()) {
                return false;
            }
            if (resultadoTentativa != null && resultadoTentativa.isLimiteAtingidoAgora()) {
                // 3ª rejeição consecutiva do mesmo item: encerra a ação e
                // bloqueia novas tentativas (ver registrarTentativaIncognita)
                // — mostra o aviso mínimo em vez do diálogo normal de
                // confirmação/dica, já que novas tentativas ficam bloqueadas
                // até "restaurar" mesmo que o participante confirme.
                mostrarAvisoLimiteTentativasAtingido(resultadoTentativa);
                return false;
            }
            String nomePapel = localizacao.texto(papelAlvo);
            String pergunta = localizacao.formatar("ui.question.valueMismatch", nomePapel);
            int opcao = JOptionPane.showConfirmDialog(
                    this, pergunta, localizacao.texto("ui.dialog.confirm"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            registrarFeedbackExibido("AG_EMLQ",
                    gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding.VISUAL,
                    "pergunta de confirmação de valor divergente");
            if (opcao == JOptionPane.YES_OPTION) {
                mostrarDicaOperacaoIncognita();
            }
            return false;
        }

        /**
         * Primeiro fluxo adaptativo distribuído completo. A tela entrega os
         * fatos observáveis do protocolo TEXTO à incógnita; o proprietário
         * semântico avalia, diagnostica, atualiza a sequência e constitui um
         * único registro. A infraestrutura apenas persiste o registro,
         * encaminha o mesmo caso ao Modelador e materializa a ajuda que o
         * próprio proprietário selecionou a partir da fotografia do login e
         * das regras publicadas.
         */
        private boolean confirmarValorIncognitaTexto(
                ItemTextoArrastavel item,
                gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel identidadeAcao) {
            if (item == null || !item.representaIncognitaOriginal()
                    || !item.isPreenchidoPeloProtocoloMouseTexto()) {
                return true;
            }

            String papelAlvo = obterPapelIncognitaAtual();
            garantirTentativasIncognitaAtual(papelAlvo);
            atualizarContextoAdaptativoIncognitaAtual();
            IncognitaQuantitativa incognita = obterIncognitaSemanticaAtual();
            if (incognita == null) {
                return true;
            }
            if (identidadeAcao == null) {
                identidadeAcao = tentativasIncognitaAtual.iniciarAcaoInstrumental(
                        gerard.dominio.campoaditivo.OrigemAcao.ORIGEM_USUARIO);
            }

            registrarPapeisDadoModificadosSeHouver();
            ContextoAcaoInstrumental contextoInstrumental =
                    new ContextoAcaoInstrumental(
                            "Substituir incógnita por número",
                            "Caixa de texto editável",
                            "Item arrastável no diagrama",
                            "Informar valor numérico para o papel designado como incógnita",
                            papelAlvo,
                            "EDICAO_ITEM",
                            "valor=" + (item.valor == null ? "" : item.valor),
                            "Valor numérico informado para a incógnita",
                            participantesSemanticosDaSituacaoAtual());
            RegistroAcaoInstrumental registro = servicoAvaliacaoAcaoIncognita.avaliarAcao(
                    incognita, identidadeAcao,
                    gerard.dominio.atividade.TarefaInteracao.TEXTO,
                    situacaoProblemaAtual, localizacao, papelAlvo,
                    estadoSemanticoCompartilhado.snapshot(),
                    obterIndiceIncognitaProtegidaNoEstadoCompartilhado(),
                    item.valor, contextoInstrumental);
            gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel resultadoTentativa =
                    registro.getResultadoTentativa().isPresent()
                            ? registro.getResultadoTentativa().get() : null;

            loggerInteracaoGerard.registrarAcaoInstrumentalUsuario(registro);
            conectorVereditoModelador.registrarAcaoInstrumental(
                    loggerInteracaoGerard.getUsuarioAtual(), registro, null,
                    registro.getActionId());

            ResultadoExecucaoAjudaIncognita resultadoAjuda = null;
            if (registro.foiErrada() && resultadoTentativa != null
                    && registro.getDiagnostico().isPresent()) {
                resultadoAjuda = executorAjudaIncognita.executar(
                        contextoIncognitaAtual,
                        registro.getDiagnostico().get(),
                        resultadoTentativa.getRejeicoesConsecutivas(),
                        new ContextoRegistroAjuda(
                                registro.getActionId(),
                                registro.getRejectionSequenceId()));
            }

            if (!registro.possuiCriterioAplicavel() || registro.foiCorreta()) {
                return true;
            }
            if (resultadoAjuda != null && resultadoAjuda.foiMaterializada()) {
                return false;
            }
            if (resultadoTentativa != null && resultadoTentativa.isLimiteAtingidoAgora()) {
                mostrarAvisoLimiteTentativasAtingido(resultadoTentativa);
                return false;
            }

            String nomePapel = localizacao.texto(papelAlvo);
            String pergunta = localizacao.formatar("ui.question.valueMismatch", nomePapel);
            int opcao = JOptionPane.showConfirmDialog(
                    this, pergunta, localizacao.texto("ui.dialog.confirm"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            registrarFeedbackExibido("AG_EMLQ",
                    gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding.VISUAL,
                    "pergunta de confirmação de valor divergente",
                    registro.getActionId(), registro.getRejectionSequenceId());
            if (opcao == JOptionPane.YES_OPTION) {
                mostrarDicaOperacaoIncognita();
            }
            return false;
        }

        /**
         * Lista referências semânticas da situação, nunca novos registros de
         * ação. Os papéis vêm do mapeamento semântico da cena e não da posição
         * visual ocupada pelos elementos.
         */
        private java.util.List<String> participantesSemanticosDaSituacaoAtual() {
            java.util.LinkedHashSet<String> participantes =
                    new java.util.LinkedHashSet<String>();
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                String papel = obterPapelElementoParaConclusao(i);
                if (papel != null && papel.trim().length() > 0) {
                    participantes.add(papel.trim());
                }
            }
            return new java.util.ArrayList<String>(participantes);
        }

        /**
         * "Estado modificado" (2026-08-06) — registro de auditoria visível,
         * extensão de obterValorAlvoParaPapel: no momento em que o estudante
         * submete um valor para a incógnita, se algum papel-dado (não a
         * incógnita) diverge do curado do problema original, registra um log
         * de sistema identificando quais papéis e com que valores. Não
         * bloqueia nem altera o fluxo de confirmação — só torna visível, para
         * pesquisa, que a incógnita está sendo avaliada contra um contexto
         * que não é mais o problema original (o alvo já foi recalculado a
         * partir disso, não travado no curado — ver obterValorAlvoParaPapel).
         * Chamado só de dentro do bloco `correto != null` de
         * confirmarValorIncognitaAceito, mesma condição que já garante haver
         * curado disponível e uma submissão real de incógnita em andamento.
         */
        private void registrarPapeisDadoModificadosSeHouver() {
            StringBuilder detalhes = null;
            for (EstadoPosicionamentoModelagem estado : capturarPosicionamentosConclusao()) {
                if (estado.isIncognitaOriginal()
                        || !Boolean.TRUE.equals(estado.getEstadoModificado())) {
                    continue;
                }
                if (detalhes == null) {
                    detalhes = new StringBuilder();
                } else {
                    detalhes.append("; ");
                }
                detalhes.append(localizacao.texto(estado.getPapelAlvo()))
                        .append("=").append(estado.getValorMatematico());
            }
            if (detalhes != null) {
                registrarLogComputador(
                        "Detectar papel-dado modificado",
                        "Estado semântico compartilhado",
                        "Papel(éis)-dado divergente(s) do curado",
                        "Sinalizar, para pesquisa, que a incógnita está sendo avaliada "
                                + "contra papéis-dado alterados pelo usuário, não o problema original",
                        "Um papel-dado divergir do curado não é em si um erro — o alvo da "
                                + "incógnita já é recalculado a partir do valor atual.",
                        "ESTADO_MODIFICADO",
                        "papeis=" + detalhes);
            }
        }

        /**
         * AG_EME (levantamento de pendências de 2026-08-11, item 1; decisão
         * da usuária em 2026-08-16): não nomeia a operação nem os operandos,
         * mas agora explica conceitualmente o papel manipulado via protocolo
         * de mouse antes de orientar a escolher soma ou subtração — a
         * explicação vem do objeto rico (DescritorRepresentacaoPapel de
         * PapelQuantitativo/FabricaPapeis*, via
         * CatalogoExplicacoesConceituaisPapel), não de texto solto aqui.
         * Até 2026-08-16 mostrava só a instrução operacional, mesma frase
         * para todas as categorias, por decisão do usuário em 2026-07-22.
         */
        private void mostrarDicaOperacaoIncognita() {
            String chavePapelAtual = obterPapelIncognitaAtual();
            String nomePapel = localizacao.texto(chavePapelAtual);
            String chaveExplicacao = gerard.dominio.campoaditivo.CatalogoExplicacoesConceituaisPapel
                    .obterChaveExplicacao(chavePapelAtual);
            String explicacaoConceitual = localizacao.texto(chaveExplicacao);
            String mensagem = explicacaoConceitual + "\n\n"
                    + localizacao.formatar("ui.hint.chooseOperation", nomePapel);
            JOptionPane.showMessageDialog(this, mensagem,
                    localizacao.texto("ui.dialog.confirm"), JOptionPane.INFORMATION_MESSAGE);
            registrarFeedbackExibido("AG_EME",
                    gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding.VISUAL,
                    "explicação conceitual do papel (" + chavePapelAtual
                            + ") + dica de escolher soma ou subtração");
        }

        /**
         * Item 36 (2026-08-23) — "Ver dica" quando todos os papéis-dado já
         * estão posicionados corretamente, mas ainda falta responder (ou foi
         * respondido errado) algum seletor de soma/subtração — sem isso, o
         * aluno clicava em "Ver dica" e nada acontecia, sem saber que a
         * etapa pendente não era mais o diagrama, e sim o seletor. Mesmo
         * padrão de exibição (JOptionPane) já usado por
         * mostrarDicaOperacaoIncognita — não introduz um terceiro estilo de
         * dica. Não nomeia a categoria nem a operação certa (o seletor já
         * mostra sua própria explicação quando o aluno erra) — só aponta
         * onde olhar, mesma função de scaffolding (manutenção da direção)
         * do resto do botão "Ver dica".
         */
        private void mostrarDicaSeletorOperacaoPendente() {
            JOptionPane.showMessageDialog(this,
                    localizacao.texto("ui.hint.pendingOperationSelector"),
                    localizacao.texto("ui.dialog.confirm"), JOptionPane.INFORMATION_MESSAGE);
            registrarFeedbackExibido("AG_AE",
                    gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding.VISUAL,
                    "dica: falta responder o seletor de soma/subtracao pendente");
        }

        /**
         * "Só deixe azulzinho depois que for escolhida as operações
         * corretamente" — quando algum seletor de soma/subtração está ativo
         * para a situação atual, ele também precisa estar respondido
         * corretamente para a modelagem contar como concluída. Seletor
         * inativo (categoria sem operação, ou situação antiga sem operação
         * curada) não bloqueia nada — mesmo critério já usado por
         * estaAtivo()/respondeuCorretamente() no clique (mousePressed).
         * Cobre as 3 categorias que já tinham o seletor (Transformação de
         * Relação, Composição de Relações, Composição de Transformações)
         * mais a segunda operação de Composição de Transformações (Item 30),
         * sem precisar de nenhum código específico por categoria — os dois
         * widgets já sabem sozinhos quando estão ativos.
         */
        private boolean operacoesDeSomaSubtracaoRespondidasCorretamente() {
            if (seletorOperacaoRelacaoAluno.estaAtivo()
                    && !seletorOperacaoRelacaoAluno.respondeuCorretamente()) {
                return false;
            }
            if (seletorOperacaoEstadoTransformacaoAluno.estaAtivo()
                    && !seletorOperacaoEstadoTransformacaoAluno.respondeuCorretamente()) {
                return false;
            }
            return true;
        }

        private void verificarConclusaoModelagem() {
            AtualizacaoConclusaoModelagem atualizacaoConclusao =
                    controladorConclusaoModelagem.atualizar(
                    capturarPapeisEsperadosConclusao(),
                    capturarPosicionamentosConclusao(),
                    operacoesDeSomaSubtracaoRespondidasCorretamente());
            boolean concluida = controladorConclusaoModelagem.isConcluida();
            boolean acabouDeConcluirPlenamente = atualizacaoConclusao
                    == AtualizacaoConclusaoModelagem.CONCLUIDA_AGORA;
            if (!concluida) {
                aplicadorDestaqueConclusaoDiagrama.aplicar(
                        false, elementosVergnaud, conectoresVergnaud, itensArrastaveis,
                        quadradinhosVenn);
            }

            if (!concluida) {
                sequenciadorFeedbackConclusao.cancelar();
            } else if (acabouDeConcluirPlenamente
                    && controladorConclusaoModelagem.deveApresentarTip()) {
                sequenciadorFeedbackConclusao.iniciar();
                registrarLogComputador(
                        "Sinalizar conclusão da modelagem",
                        "Diagrama de Vergnaud",
                        "Destaque azul, selo discreto e tip de decisão",
                        "Reconhecer a associação correta de todos os elementos matemáticos",
                        "A confirmação visual antecede a pergunta sobre a próxima tarefa.",
                        "CONCLUSAO_MODELAGEM", "estado=concluida; sequencia=progressiva");
            }
            repaint();
        }

        private void suspenderConclusaoDuranteManipulacao() {
            if (!controladorConclusaoModelagem.isConcluida()) return;
            controladorConclusaoModelagem.reiniciar();
            aplicadorDestaqueConclusaoDiagrama.aplicar(
                    false, elementosVergnaud, conectoresVergnaud, itensArrastaveis,
                    quadradinhosVenn);
            sequenciadorFeedbackConclusao.cancelar();
        }

        private void reiniciarConclusaoModelagem() {
            controladorConclusaoModelagem.reiniciar();
            aplicadorDestaqueConclusaoDiagrama.aplicar(
                    false, elementosVergnaud, conectoresVergnaud, itensArrastaveis,
                    quadradinhosVenn);
            sequenciadorFeedbackConclusao.cancelar();
        }

        private boolean itemEstaSobreElementoDoDiagrama(ItemTextoArrastavel item) {
            if (item == null || !item.estaNoDiagrama()) {
                return false;
            }
            return encontrarElementoVergnaudPorItem(item) != null;
        }

        private void finalizarProxyTextoSolto(
                ItemTextoArrastavel itemSolto,
                boolean posicionamentoSemanticamenteCorreto) {
            if (itemSolto == null) {
                sessaoArrasteTextoParaDiagrama.limpar();
                return;
            }

            boolean solturaSobreDiagrama = itemEstaSobreElementoDoDiagrama(itemSolto);
            if (sessaoArrasteTextoParaDiagrama.devePersistirAoSoltar(
                    itemSolto, solturaSobreDiagrama,
                    posicionamentoSemanticamenteCorreto)) {
                if (!itensArrastaveis.contains(itemSolto)) {
                    itensArrastaveis.add(itemSolto);
                }
                sessaoArrasteTextoParaDiagrama.confirmarPersistencia(itemSolto);
                return;
            }

            if (sessaoArrasteTextoParaDiagrama.deveManterNoDiagramaAposErro(
                    itemSolto, solturaSobreDiagrama,
                    posicionamentoSemanticamenteCorreto)) {
                if (!itensArrastaveis.contains(itemSolto)) {
                    itensArrastaveis.add(itemSolto);
                }
                sessaoArrasteTextoParaDiagrama.confirmarPersistencia(itemSolto);
                return;
            }

            if (sessaoArrasteTextoParaDiagrama.deveDescartarAoSoltar(
                    itemSolto, solturaSobreDiagrama,
                    posicionamentoSemanticamenteCorreto)) {
                if (itemFocado == itemSolto) {
                    itemFocado = null;
                }
                sessaoArrasteTextoParaDiagrama.descartarProxy(itemSolto);
                return;
            }

            if (!itensArrastaveis.contains(itemSolto) && !solturaSobreDiagrama) {
                sessaoArrasteTextoParaDiagrama.limpar();
            }
        }

        private int obterIndiceFiguraIncognitaAtual() {
            if (resultadoInterpretacao == null || resultadoInterpretacao.getSubtipoVergnaud() == null) {
                return -1;
            }
            return resultadoInterpretacao.getSubtipoVergnaud().getIndiceFiguraIncognita();
        }

        private void inicializarElementosTexto() {
            elementosTexto.clear();

            String texto = textoProblema == null ? "" : textoProblema;
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\S+").matcher(texto);

            while (matcher.find()) {
                String palavra = matcher.group();
                int inicioPalavra = matcher.start();

                if (palavra.endsWith("?") && palavra.length() > 1) {
                    String antesDaInterrogacao = palavra.substring(0, palavra.length() - 1);

                    if (antesDaInterrogacao.length() > 0) {
                        elementosTexto.add(new ElementoTextoMovel(antesDaInterrogacao, inicioPalavra));
                    }

                    elementosTexto.add(new ElementoTextoMovel("?", inicioPalavra + palavra.length() - 1));
                } else {
                    elementosTexto.add(new ElementoTextoMovel(palavra, inicioPalavra));
                }
            }

            vincularPapeisSemanticosAosElementosTexto();
            layoutTextoInicializado = false;
            larguraUltimoLayoutTexto = -1;
        }

        private void vincularPapeisSemanticosAosElementosTexto() {
            if (textoProblemaEhMensagemSistema || resultadoInterpretacao == null) {
                return;
            }

            java.util.List<NumeroEncontrado> numeros = obterNumerosInterpretados();
            for (int i = 0; i < elementosTexto.size(); i++) {
                ElementoTextoMovel elemento = elementosTexto.get(i);
                int inicioElemento = elemento.posicaoInicialTexto;
                int fimElemento = inicioElemento + elemento.valorOriginal.length();

                for (int n = 0; n < numeros.size(); n++) {
                    NumeroEncontrado numero = numeros.get(n);
                    if (numero.getPosicaoInicial() >= inicioElemento
                            && numero.getPosicaoFinal() <= fimElemento) {
                        int inicioLocal = numero.getPosicaoInicial() - inicioElemento;
                        int fimLocal = numero.getPosicaoFinal() - inicioElemento;
                        elemento.vincularSemantica(
                                obterChavePapelDoNumero(n),
                                inicioLocal,
                                fimLocal,
                                numero.getValorCanonico());
                        break;
                    }
                }

                if (!elemento.possuiVinculoSemantico()) {
                    int indiceInterrogacao = elemento.valorOriginal.indexOf('?');
                    if (indiceInterrogacao >= 0) {
                        elemento.vincularSemantica(
                                ResolvedorPapelInterpretado.obterChavePapelExataPorValor(resultadoInterpretacao, "?"),
                                indiceInterrogacao,
                                indiceInterrogacao + 1,
                                "?");
                    }
                }
            }
        }

        private void garantirLayoutElementosTexto(FontMetrics fm, int margemX, int yInicial, int larguraMaxima) {
            if (layoutTextoInicializado && larguraUltimoLayoutTexto == getWidth()) {
                return;
            }

            boolean preservarDeslocamentos = larguraUltimoLayoutTexto >= 0;
            int x = margemX;
            int y = yInicial;
            int alturaLinha = fm.getHeight() + 8;

            for (int i = 0; i < elementosTexto.size(); i++) {
                ElementoTextoMovel elemento = elementosTexto.get(i);
                int deslocamentoAtualX = preservarDeslocamentos
                        ? elemento.x - elemento.xOriginal : 0;
                int deslocamentoAtualY = preservarDeslocamentos
                        ? elemento.y - elemento.yOriginal : 0;
                int larguraPalavra = fm.stringWidth(elemento.valor + " ");

                if (x + larguraPalavra > margemX + larguraMaxima) {
                    x = margemX;
                    y += alturaLinha;
                }

                elemento.xOriginal = x;
                elemento.yOriginal = y;
                elemento.x = x + deslocamentoAtualX;
                elemento.y = y + deslocamentoAtualY;
                elemento.atualizarTamanho(fm);

                x += larguraPalavra;
            }

            layoutTextoInicializado = true;
            larguraUltimoLayoutTexto = getWidth();
        }

        private void desenharElementoTextoMovel(Graphics2D g2, FontMetrics fm, ElementoTextoMovel elemento) {
            if (!textoProblemaEhMensagemSistema
                    && (elemento == handlerElementoTextoMovel.obterElementoAtivo()
                    || elemento == elementoTextoFocado)) {
                Stroke original = g2.getStroke();

                if (elemento == handlerElementoTextoMovel.obterElementoAtivo()) {
                    g2.setColor(COR_DESTAQUE);
                    g2.fillRoundRect(elemento.x - 4, elemento.y - fm.getAscent() + 1,
                            elemento.largura + 8, elemento.altura + 4, 8, 8);
                    g2.setColor(COR_PRIMARIA);
                    g2.setStroke(new BasicStroke(1.0f));
                } else {
                    g2.setColor(new Color(247, 246, 241));
                    g2.fillRoundRect(elemento.x - 4, elemento.y - fm.getAscent() + 1,
                            elemento.largura + 8, elemento.altura + 4, 8, 8);
                    g2.setColor(COR_BORDA);
                    g2.setStroke(new BasicStroke(0.8f));
                }

                g2.drawRoundRect(elemento.x - 4, elemento.y - fm.getAscent() + 1,
                        elemento.largura + 8, elemento.altura + 4, 8, 8);
                g2.setStroke(original);
            }

            desenharPalavra(g2, fm, elemento);
        }

        private void desenharPalavra(Graphics2D g2, FontMetrics fm,
                ElementoTextoMovel elemento) {
            String palavra = elemento.valor == null ? "" : elemento.valor;
            int xAtual = elemento.x;
            int inicioSemantico = elemento.getInicioSemanticoAtual();
            int fimSemantico = elemento.getFimSemanticoAtual();

            for (int i = 0; i < palavra.length(); i++) {
                String ch = palavra.substring(i, i + 1);
                boolean simboloSemantico = elemento.possuiVinculoSemantico()
                        && i >= inicioSemantico && i < fimSemantico;

                if (!textoProblemaEhMensagemSistema && simboloSemantico) {
                    desenharSimboloAnimado(g2, fm, ch, xAtual, elemento.y,
                            i, elemento.chavePapelSemantico);
                } else {
                    g2.setColor(COR_TEXTO);
                    g2.drawString(ch, xAtual, elemento.y);
                }

                xAtual += fm.stringWidth(ch);
            }

            g2.setColor(Color.BLACK);
            g2.drawString(" ", xAtual, elemento.y);
        }

        private void desenharSimboloAnimado(Graphics2D g2, FontMetrics fm,
                String valor, int x, int yBase, int indice,
                String chavePapel) {
            Graphics2D g = (Graphics2D) g2.create();

            int largura = fm.stringWidth(valor);
            int altura = fm.getHeight();

            double centroX = x + largura / 2.0;
            double centroY = yBase - fm.getAscent() + altura / 2.0;
            double angulo = Math.sin(faseAnimacao + indice) * 0.08;

            AffineTransform antigoTransform = g.getTransform();
            Composite antigoComposite = g.getComposite();
            g.rotate(angulo, centroX, centroY);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
            g.setColor(gerard.ui.UITemaGerard.COR_BORDA);
            g.drawString(valor, x + 1, yBase + 1);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.setColor(corTextoDoPapel(chavePapel));
            g.drawString(valor, x, yBase);
            g.setTransform(antigoTransform);
            g.setComposite(antigoComposite);
            g.dispose();
        }

        private void marcarElementoSemanticoDoTexto(FontMetrics fm,
                ElementoTextoMovel elemento) {
            if (textoProblemaEhMensagemSistema || elemento == null
                    || !elemento.possuiVinculoSemantico()) {
                return;
            }
            int inicio = elemento.getInicioSemanticoAtual();
            int fim = elemento.getFimSemanticoAtual();
            String valorAtual = elemento.getValorSemanticoAtual();
            adicionarMarcadorComPapel(
                    fm,
                    elemento.valor,
                    elemento.x,
                    elemento.y,
                    inicio,
                    fim,
                    valorAtual,
                    elemento.representaIncognitaOriginal(),
                    elemento.chavePapelSemantico);
        }

        private boolean ehPosicaoDeNumeralInterpretado(int posicaoGlobal) {
            if (textoProblemaEhMensagemSistema) {
                return false;
            }
            java.util.List<NumeroEncontrado> numeros = obterNumerosInterpretados();
            for (int i = 0; i < numeros.size(); i++) {
                NumeroEncontrado numero = numeros.get(i);
                if (posicaoGlobal >= numero.getPosicaoInicial() && posicaoGlobal < numero.getPosicaoFinal()) {
                    return true;
                }
            }
            return false;
        }

        private boolean ehNumeralInterpretado(int inicioGlobal, int fimGlobal, String valor) {
            java.util.List<NumeroEncontrado> numeros = obterNumerosInterpretados();
            for (int i = 0; i < numeros.size(); i++) {
                NumeroEncontrado numero = numeros.get(i);
                if (numero.getPosicaoInicial() == inicioGlobal && numero.getPosicaoFinal() == fimGlobal) {
                    return true;
                }
            }
            return false;
        }

        private boolean elementoContemNumeralInterpretado(ElementoTextoMovel elemento) {
            return !textoProblemaEhMensagemSistema
                    && elemento != null
                    && elemento.possuiVinculoSemantico()
                    && !elemento.representaIncognitaOriginal();
        }

        private void adicionarMarcador(
                FontMetrics fm,
                String palavra,
                int xPalavra,
                int yBase,
                int indice,
                String valor,
                boolean editavel
        ) {
            int fim = indice + Math.max(1, valor == null ? 1 : valor.length());
            adicionarMarcador(fm, palavra, xPalavra, yBase, indice, fim, valor, editavel);
        }

        private void adicionarMarcador(
                FontMetrics fm,
                String palavra,
                int xPalavra,
                int yBase,
                int indiceInicio,
                int indiceFim,
                String valor,
                boolean editavel
        ) {
            adicionarMarcadorComPapel(fm, palavra, xPalavra, yBase, indiceInicio, indiceFim, valor, editavel, ResolvedorPapelInterpretado.obterChavePapelExataPorIndice(resultadoInterpretacao, marcadoresFixosTexto.size()));
        }

        private void adicionarMarcadorComPapel(
                FontMetrics fm,
                String palavra,
                int xPalavra,
                int yBase,
                int indiceInicio,
                int indiceFim,
                String valor,
                boolean editavel,
                String chavePapel
        ) {
            int inicioSeguro = Math.max(0, Math.min(indiceInicio, palavra.length()));
            int fimSeguro = Math.max(inicioSeguro + 1, Math.min(indiceFim, palavra.length()));
            String antes = palavra.substring(0, inicioSeguro);
            String textoMarcado = palavra.substring(inicioSeguro, fimSeguro);

            int xChar = xPalavra + fm.stringWidth(antes);
            int larguraValor = Math.max(fm.stringWidth(textoMarcado), fm.stringWidth(valor == null ? "" : valor));
            int alturaTexto = fm.getHeight();

            int largura = larguraValor + 6;
            int altura = alturaTexto - 5;

            int xQuadrado = xChar - 3;
            int yQuadrado = yBase - fm.getAscent() + 3;

            marcadoresFixosTexto.add(
                    new MarcadorTexto(xQuadrado, yQuadrado, largura, altura, valor, editavel, chavePapel)
            );
        }

        private String obterChavePapelDoNumero(int indiceNumero) {
            if (resultadoInterpretacao == null || indiceNumero < 0) {
                return "papel.valor";
            }
            int indiceConhecido = 0;
            java.util.List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();
            for (int i = 0; i < papeis.size(); i++) {
                PapelElementoInterpretado papel = papeis.get(i);
                if (papel == null || !papel.isConhecido()) {
                    continue;
                }
                if (indiceConhecido == indiceNumero) {
                    return papel.getChavePapel();
                }
                indiceConhecido++;
            }
            return "papel.valor";
        }

        private String obterChavePapelPorPosicaoTexto(int posicaoGlobal, String valor) {
            if (SimboloDesconhecido.eh(valor)) {
                return ResolvedorPapelInterpretado.obterChavePapelExataPorValor(resultadoInterpretacao, "?");
            }
            java.util.List<NumeroEncontrado> numeros = obterNumerosInterpretados();
            for (int i = 0; i < numeros.size(); i++) {
                NumeroEncontrado numero = numeros.get(i);
                if (posicaoGlobal >= numero.getPosicaoInicial() && posicaoGlobal < numero.getPosicaoFinal()) {
                    return obterChavePapelDoNumero(i);
                }
            }
            return "papel.valor";
        }

        private Color corFundoDoPapel(String chave) {
            return COR_MARCADOR_NUMERO;
        }

        private Color corBordaDoPapel(String chave) {
            return COR_BORDA_MARCADOR;
        }

        private Color corTextoDoPapel(String chave) {
            return COR_TEXTO;
        }

        private void desenharMarcadoresFixosDoTexto(Graphics2D g2) {
            Stroke original = g2.getStroke();
            Composite compositeOriginal = g2.getComposite();

            float[] tracejado = {2.0f, 2.0f};
            g2.setStroke(new BasicStroke(
                    1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f,
                    tracejado,
                    0.0f
            ));

            for (int i = 0; i < marcadoresFixosTexto.size(); i++) {
                MarcadorTexto m = marcadoresFixosTexto.get(i);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.38f));
                g2.setColor(corFundoDoPapel(m.chavePapel));
                g2.fillRoundRect(m.x, m.y, m.largura, m.altura, 6, 6);
                g2.setComposite(compositeOriginal);
                g2.setColor(corBordaDoPapel(m.chavePapel));
                g2.drawRoundRect(m.x, m.y, m.largura, m.altura, 6, 6);
            }

            g2.setComposite(compositeOriginal);
            g2.setStroke(original);
        }

        private void desenharAreaDiagrama(Graphics2D g2) {
            int yTopoArea = 210 + ALTURA_PAINEL_ATALHOS_CATEGORIA;
            g2.setColor(COR_FUNDO);
            g2.fillRect(0, yTopoArea, getWidth(), getHeight() - yTopoArea);

            g2.setColor(COR_BORDA);
            g2.drawLine(0, yTopoArea, getWidth(), yTopoArea);

            if (!categoriaSelecionadaParaAtividade) {
                // Exibe os painéis vazios do diagrama de Vergnaud e da
                // representação complementar. Formas, títulos, valores e
                // demais conteúdos educativos só são criados após a escolha
                // da categoria.
                Rectangle[] areasIniciais = obterAreasDiagramasProporcionais();
                Rectangle areaVergnaudVazia = areasIniciais[0];
                Rectangle areaComplementarVazia = areasIniciais[1];

                int xDivisor = areaVergnaudVazia.x + areaVergnaudVazia.width
                        + (ESPACO_BASE_ENTRE_DIAGRAMAS / 2);
                g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
                g2.drawLine(xDivisor, 222 + ALTURA_PAINEL_ATALHOS_CATEGORIA, xDivisor, getHeight() - 14);

                desenharCard(g2,
                        areaVergnaudVazia.x,
                        areaVergnaudVazia.y,
                        areaVergnaudVazia.width,
                        areaVergnaudVazia.height,
                        18);
                desenharCard(g2,
                        areaComplementarVazia.x,
                        areaComplementarVazia.y,
                        areaComplementarVazia.width,
                        areaComplementarVazia.height,
                        18);

                ocultarControlesDaAtividadeSemCategoria();
                return;
            }
            if (deveExibirDiagramaComplementar()) {
                g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
                int xDivisorDiagramas = obterXDivisorDiagramas();
                g2.drawLine(xDivisorDiagramas, 222 + ALTURA_PAINEL_ATALHOS_CATEGORIA, xDivisorDiagramas, getHeight() - 14);
            }

            Rectangle limiteVergnaud = obterAreaVisivelDiagramasVergnaud();
            reposicionarBotaoRestaurarDiagrama(limiteVergnaud);
            reposicionarBotaoAjudaVergnaud(limiteVergnaud);
            reposicionarBotaoVerDicaPosicionamento(limiteVergnaud);
            if (!deveExibirDiagramaComplementar() && botaoAjudaComplementar != null) {
                botaoAjudaComplementar.setVisible(false);
            }
            desenharCard(g2, limiteVergnaud.x, limiteVergnaud.y, limiteVergnaud.width, limiteVergnaud.height, 18);

            if (cenaDiagramaAtual == null || elementosVergnaud.isEmpty()) {
                inicializarDiagramaVergnaud();
            }

            ConectorVergnaud conectorAtivo = obterConectorVergnaudAtivo();
            for (ConectorVergnaud conector : conectoresVergnaud) {
                if (conector != conectorAtivo) {
                    conector.desenhar(g2);
                }
            }
            for (ElementoVergnaud elemento : elementosVergnaud) {
                elemento.desenhar(g2);
            }
        }

        private ConectorVergnaud obterConectorVergnaudAtivo() {
            AdaptadorMovimentoConectorVergnaud adaptador =
                    handlerConectorVergnaud.obterAlvoAtivo();
            return adaptador == null ? null : adaptador.obterConector();
        }

        private LimitesMovimento obterLimitesMovimentoConectorVergnaud() {
            return AdaptadorMovimentoConectorVergnaud.traduzir(
                    obterAreaConteudoDiagramaVergnaud());
        }

        private void desenharPainelInterpretacaoLinguistica(Graphics2D g2) {
            if (resultadoInterpretacao == null) {
                return;
            }

            int x = 30;
            int y = 225;
            int largura = 640;
            int altura = 122;

            Composite compositeOriginal = g2.getComposite();
            Stroke strokeOriginal = g2.getStroke();

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.96f));
            g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
            g2.fillRoundRect(x, y, largura, altura, 12, 12);

            g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(x, y, largura, altura, 12, 12);

            g2.setComposite(compositeOriginal);

            g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.drawString(localizacao.texto("ui.panel.interpretation"), x + 12, y + 20);

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.drawString(
                    localizacao.texto("ui.panel.detectedLanguage") + ": " + resultadoInterpretacao.getIdiomaDetectado().getDescricao() +
                    " | " + localizacao.texto("ui.panel.probableCategory") + ": " + resultadoInterpretacao.getCategoriaProvavel().getDescricao() +
                    " (" + resultadoInterpretacao.getCategoriaProvavel().getSigla() + ")" +
                    " | " + localizacao.texto("ui.panel.confidence") + ": " + localizacao.texto("ui.panel.humanCuration"),
                    x + 12, y + 40
            );

            g2.drawString(localizacao.texto("ui.panel.numbers") + ": " + resultadoInterpretacao.getNumerosFormatados(), x + 12, y + 58);
            g2.drawString(localizacao.texto("ui.panel.probableRelation") + ": " + resultadoInterpretacao.getRelacaoProvavel(), x + 250, y + 58);

            String pistas = resultadoInterpretacao.getPistasFormatadas();
            if (pistas.length() > 76) {
                pistas = pistas.substring(0, 73) + "...";
            }
            g2.drawString(localizacao.texto("ui.panel.clues") + ": " + pistas, x + 12, y + 76);

            String papeis = resultadoInterpretacao.getPapeisFormatados();
            if (papeis.length() > 86) {
                papeis = papeis.substring(0, 83) + "...";
            }
            g2.drawString(localizacao.texto("ui.panel.roles") + ": " + papeis, x + 12, y + 94);

            if (!resultadoInterpretacao.getAvisos().isEmpty()) {
                String aviso = resultadoInterpretacao.getAvisos().get(0);
                if (aviso.length() > 78) {
                    aviso = aviso.substring(0, 75) + "...";
                }
                g2.setColor(new Color(132, 92, 47));
                g2.drawString(localizacao.texto("ui.panel.warning") + ": " + aviso, x + 12, y + 112);
            }

            g2.setStroke(strokeOriginal);
        }

        private void desenharPaineisEixoRelacoes(Graphics2D g2) {
            atualizarPaineisEixosRelacoesConformeVisibilidade();
            paineisEixosRelacoes.desenhar(
                    g2,
                    getWidth(),
                    getHeight(),
                    obterAreaVisivelDiagramasVergnaud()
            );
            paineisEixosRelacoes.desenharLupas(g2);
            seletorOperacaoRelacaoAluno.desenhar(g2, localizacao);
            // Ordem pedagógica (2026-08-23, pedido da usuária): "a primeira
            // operação é sempre a das transformações, a última é a final" —
            // em Composição de Transformações, o segundo seletor (estado x
            // transformação) só aparece depois que o primeiro (transformação
            // x transformação) estiver respondido corretamente. Nas outras
            // duas categorias (uma operação só) isto é irrelevante: o
            // segundo seletor nunca fica ativo nelas (ver
            // SeletorOperacaoRelacaoAluno.ativar), então a condição abaixo
            // não muda nada para elas.
            if (seletorOperacaoRelacaoAluno.respondeuCorretamente()) {
                seletorOperacaoEstadoTransformacaoAluno.desenhar(g2, localizacao);
            }
        }

        /**
         * Ativa/desativa os painéis de eixo das Relações a cada repaint.
         * Decisão revista da usuária, 2026-08-17 (depois de ver o
         * comportamento real): ao contrário de quadradinhos/barras/processo
         * (gatilho de {@link #deveExibirDiagramaComplementar()}, só depois
         * da 3ª tentativa rejeitada), os painéis de Relações não esperam
         * nenhuma tentativa rejeitada — aparecem sempre que a categoria
         * ativa for uma das duas de Relações, mesma disponibilidade do
         * mecanismo já existente de eixo único ("o eixo sempre aparece").
         * Decisão anterior (2026-08-16, "mesma regra" das 3 tentativas)
         * revogada nesta mesma sessão, ainda antes de qualquer validação
         * real ter passado por ela. Autocorretivo: não precisa de um ponto
         * de reset dedicado em cada lugar que hoje zera
         * handlerQuadradinhoVenn/etc. — some ou aparece sozinho se a
         * situação ou a categoria mudar.
         */
        private void atualizarPaineisEixosRelacoesConformeVisibilidade() {
            boolean deveExibir = devemExibirPaineisEixosRelacoes();
            if (deveExibir && !paineisEixosRelacoes.estaAtivo()) {
                ativarPaineisEixosRelacoes();
            } else if (!deveExibir && paineisEixosRelacoes.estaAtivo()) {
                desativarPaineisEixosRelacoes();
            }
        }

        private void desativarPaineisEixosRelacoes() {
            handlerPaineisEixosRelacoes.cancelar();
            paineisEixosRelacoes.desativar();
        }

        /**
         * Regra da usuária (2026-08-18, generalização do item 4): "todo
         * número relativo ou transformação carrega uma lupa. Essa é a
         * regra" — não importa a categoria, nem se veio de uma cena
         * simples ou composta/encadeada. O critério deixou de ser uma
         * lista fixa de TipoSituacaoAditiva e passou a ser estrutural:
         * existe pelo menos um elemento do diagrama atual que é um
         * número relativo ({@link #ehElementoNumeroRelativo}, mesmo
         * critério — TipoFiguraDiagrama.ELIPSE — já usado para decidir
         * onde o menu de sinal se aplica). Antes: só
         * TRANSFORMACAO_RELACAO/COMPOSICAO_RELACOES. Categorias sem
         * nenhum número relativo (ex.: Composição de Medidas, só
         * quadrados) continuam sem painel, pois a busca não encontra
         * nenhum elemento elipse.
         */
        private boolean devemExibirPaineisEixosRelacoes() {
            if (!categoriaSelecionadaParaAtividade || elementosVergnaud == null) {
                return false;
            }
            boolean[] flagsExibirLupa = new boolean[elementosVergnaud.size()];
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                ElementoVergnaud elemento = elementosVergnaud.get(i);
                flagsExibirLupa[i] = elemento != null && elemento.exibirLupa;
            }
            return DecisaoExibicaoPaineisEixo.existeAlgumComLupa(flagsExibirLupa);
        }

        /**
         * Cria um painel por papel de {@code elementosVergnaud}, com o
         * valor atual do elemento (mesmos helpers já usados pelo
         * mecanismo de escolha de sinal sob demanda —
         * {@code obterValorNumericoDoElemento},
         * {@code scaffoldingReacaoRepresentacoes}) e posição inicial
         * acima/abaixo dele, alternando por índice. A posição é só um
         * ponto de partida razoável — cada painel continua livremente
         * arrastável depois (mesma regra do painel único já existente).
         */
        private void ativarPaineisEixosRelacoes() {
            paineisEixosRelacoes.ativar(elementosVergnaud);
            int larguraTela = Math.max(getWidth(), 1);
            int alturaTela = Math.max(getHeight(), 1);
            for (PaineisEixosRelacoes.Painel painel : paineisEixosRelacoes.obterPaineis()) {
                prepararPainelEixoRelacao(painel, larguraTela, alturaTela);
            }
        }

        /**
         * Semeia valor e posição inicial de um painel — extraído para ser
         * reaproveitado tanto na ativação de todos os papéis quanto na
         * revelação individual de um só pela lupa (2026-08-17, ver Javadoc
         * de PaineisEixosRelacoes): mesma preparação, um só lugar.
         */
        private void prepararPainelEixoRelacao(
                PaineisEixosRelacoes.Painel painel, int larguraTela, int alturaTela) {
            atualizarValorPainelEixoRelacao(painel);
            ElementoVergnaud elemento = painel.elemento;
            int espacoAcima = Math.max(0, elemento.y);
            int espacoAbaixo = Math.max(0,
                    alturaTela - (elemento.y + elemento.altura));
            boolean acima = espacoAcima >= espacoAbaixo;
            int x = Math.max(8, Math.min(elemento.x, larguraTela - 200));
            int y = acima
                    ? Math.max(50, elemento.y - 110)
                    : Math.min(elemento.y + elemento.altura + 12, alturaTela - 108);
            painel.grafico.definirPosicaoInicial(x, y);
        }

        /**
         * Envia para o painel o valor atual do elemento correspondente
         * ({@code obterValorNumericoDoElemento}, mesmo helper já usado pelo
         * mecanismo de escolha de sinal sob demanda). Extraído de
         * {@link #ativarPaineisEixosRelacoes()} para ser reaproveitado
         * também por {@link #atualizarPaineisEixosRelacoesComValoresAtuais()}
         * — mesma lógica de leitura de valor, um só lugar (localidade do
         * conhecimento). Não mexe na posição do painel: {@code registrarEscolha}/
         * {@code mostrar} só criam a posição padrão a primeira vez
         * (painelFlutuante ainda nulo); com o painel já existente, a posição
         * arrastada pelo usuário — ou definida por definirPosicaoInicial —
         * permanece intacta.
         */
        private void atualizarValorPainelEixoRelacao(PaineisEixosRelacoes.Painel painel) {
            ElementoVergnaud elemento = painel.elemento;
            Rectangle geometria = new Rectangle(
                    elemento.x, elemento.y, elemento.largura, elemento.altura);
            Integer valor = obterValorNumericoDoElemento(elemento);
            if (valor != null) {
                painel.apresentador.registrarEscolha(
                        geometria,
                        servicoQuantidadeContextual.formatarMagnitudeNumeroRelativo(
                                valor.intValue(), situacaoProblemaAtual),
                        servicoQuantidadeContextual.sinalNumeroRelativo(
                                valor.intValue()));
            } else {
                painel.apresentador.mostrar(geometria, "");
            }
        }

        /**
         * Corrige o bug relatado pela usuária em 2026-08-17 ("eixos não
         * mudam com a mudança dos elementos no diagrama"): como o eixo
         * único antigo foi suprimido nas categorias de Relações (mecanismo
         * removido por completo em 2026-09-01, por ter se tornado
         * inalcançável em todas as 6 categorias), o único ponto que
         * atualizava o eixo quando o valor mudava por outro caminho (menu
         * de escolha de sinal, protocolo da incógnita) deixou de alcançar
         * os painéis novos. Chamado do mesmo ponto central por onde toda
         * outra representação já se mantém sincronizada
         * ({@link #aplicarEstadoCompartilhadoEmTodasAsRepresentacoes}), em
         * vez de duplicar gatilhos espalhados pelo código.
         *
         * Ignora o painel que estiver sendo arrastado no momento — ele já
         * escreve seu próprio valor de volta no elemento em tempo real
         * ({@link #sincronizarPainelEixoRelacaoSeNecessario(boolean)}),
         * então reaplicar o valor do elemento nele aqui seria na melhor das
         * hipóteses redundante e, durante o próprio arraste, uma fonte de
         * disputa com o gesto do usuário.
         */
        private void atualizarPaineisEixosRelacoesComValoresAtuais() {
            if (!paineisEixosRelacoes.estaAtivo()) {
                return;
            }
            for (PaineisEixosRelacoes.Painel painel : paineisEixosRelacoes.obterPaineis()) {
                if (painel.grafico.estaArrastando()) {
                    continue;
                }
                atualizarValorPainelEixoRelacao(painel);
            }
        }

        private void desenharElementos(Graphics2D g2) {
            for (int i = 0; i < itensArrastaveis.size(); i++) {
                ItemTextoArrastavel item = itensArrastaveis.get(i);
                if (item != handlerItemTextoArrastavel.obterItemAtivo()) {
                    item.desenhar(g2);
                }
            }

            if (itemFocado != null
                    && itemFocado != handlerItemTextoArrastavel.obterItemAtivo()) {
                g2.setColor(COR_TEXTO_SECUNDARIO);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRect(itemFocado.x - 3, itemFocado.y - 3,
                        itemFocado.largura + 6, itemFocado.altura + 6);
            }

        }

        private void desenharPickupEmPrimeiroPlano(final Graphics2D g2) {
            if (g2 == null) {
                return;
            }

            if (handlerElementoTextoMovel.estaAtivo()) {
                final ElementoTextoMovel elemento =
                        handlerElementoTextoMovel.obterElementoAtivo();
                final Font fonte = new Font("Arial", Font.BOLD, 20);
                final FontMetrics fm = getFontMetrics(fonte);
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        return new Rectangle(
                                elemento.x - 4,
                                elemento.y - fm.getAscent() + 1,
                                Math.max(1, elemento.largura + 8),
                                Math.max(1, elemento.altura + 4));
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        grafico.setFont(fonte);
                        desenharElementoTextoMovel(grafico, fm, elemento);
                    }
                });
            }

            if (handlerQuadradinhoVenn.estaAtivo()) {
                final QuadradinhoVenn quadradinho = handlerQuadradinhoVenn.obterQuadradinhoAtivo();
                final boolean composicao = ehDiagramaVennComposicaoMedidas();
                final boolean comparacao = ehGraficoBarrasComparacao();
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        return new Rectangle(quadradinho.x, quadradinho.y,
                                Math.max(1, quadradinho.tamanho),
                                Math.max(1, quadradinho.tamanho));
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        desenharQuadradinhoVenn(grafico, quadradinho,
                                composicao, comparacao);
                    }
                });
            }

            if (handlerItemTextoArrastavel.estaAtivo()) {
                final ItemTextoArrastavel item =
                        handlerItemTextoArrastavel.obterItemAtivo();
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        return new Rectangle(item.x - 4, item.y - 4,
                                Math.max(1, item.largura + 8),
                                Math.max(1, item.altura + 8));
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        item.desenhar(grafico);
                        Stroke anterior = grafico.getStroke();
                        grafico.setColor(COR_PRIMARIA);
                        grafico.setStroke(new BasicStroke(1.6f));
                        grafico.drawRect(item.x - 4, item.y - 4,
                                item.largura + 8, item.altura + 8);
                        grafico.setStroke(anterior);
                    }
                });
            }

            final ItemTextoArrastavel proxyEmFeedback =
                    scaffoldingFeedbackProxyPosicionamento.obterProxyEmFeedback();
            if (proxyEmFeedback != null
                    && proxyEmFeedback != handlerItemTextoArrastavel.obterItemAtivo()) {
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        return new Rectangle(proxyEmFeedback.x - 4, proxyEmFeedback.y - 4,
                                Math.max(1, proxyEmFeedback.largura + 8),
                                Math.max(1, proxyEmFeedback.altura + 8));
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        proxyEmFeedback.desenhar(grafico);
                    }
                });
            }

            final ConectorVergnaud conector = obterConectorVergnaudAtivo();
            if (conector != null) {
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        return obterLimitesVisuaisConector(conector);
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        conector.desenhar(grafico);
                    }
                });
            }

            final PaineisEixosRelacoes.Painel painelEixoRelacaoArrastando =
                    paineisEixosRelacoes.encontrarArrastando();
            if (painelEixoRelacaoArrastando != null
                    && painelEixoRelacaoArrastando.grafico.estaArrastandoPontoControle()) {
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        return painelEixoRelacaoArrastando.grafico.obterAreaVisualPontoControle();
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        painelEixoRelacaoArrastando.grafico.desenharPontoControleEmPrimeiroPlano(grafico);
                    }
                });
            }

            if (arrastandoControleComparacao) {
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        Rectangle area = obterRetanguloPontoControleComparacao();
                        return new Rectangle(
                                area.x + 5,
                                area.y + 5,
                                Math.max(1, area.width - 10),
                                Math.max(1, area.height - 10));
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        desenharPontoControleComparacaoEmPrimeiroPlano(grafico);
                    }
                });
            }
        }

        private Rectangle obterLimitesVisuaisConector(ConectorVergnaud conector) {
            if (conector == null) {
                return new Rectangle();
            }
            int minX = Math.min(conector.x1, conector.x2) - 18;
            int maxX = Math.max(conector.x1, conector.x2) + 18;
            int minY = Math.min(conector.y1, conector.y2) - 22;
            int maxY = Math.max(conector.y1, conector.y2) + 22;
            if (conector.tipo == TipoConectorDiagrama.SETA_CURVA) {
                maxY = Math.max(maxY, Math.max(conector.y1, conector.y2) + 116);
            }
            return new Rectangle(minX, minY,
                    Math.max(1, maxX - minX),
                    Math.max(1, maxY - minY));
        }

        private void desenharFeedbackExplicitoProximidade(Graphics2D g2) {
            if (handlerItemTextoArrastavel.estaAtivo()) {
                desenharIndicacaoEstiloDuranteArraste(g2,
                        handlerItemTextoArrastavel.obterItemAtivo());
            }
        }

        private void desenharIndicacaoEstiloDuranteArraste(Graphics2D g2, ItemTextoArrastavel item) {
            ElementoVergnaud alvo = obterAlvoCorretoParaItem(item);
            if (alvo == null) {
                return;
            }
            boolean proximo = itemEstaProximoDoElemento(item, alvo);
            boolean dentro = centroDoItemDentroDoElemento(item, alvo);
            desenharPistaVisualDoModo(g2, item, alvo, proximo, dentro);
        }

        private void desenharPistaVisualDoModo(Graphics2D g2, ItemTextoArrastavel item,
                                               ElementoVergnaud alvo, boolean proximo, boolean dentro) {
            // Extraído para EstrategiaEstiloInteracao.desenhar(...): a decisão de
            // estado (calcularEstado) já morava nas estratégias; a pista visual
            // ramificava de novo sobre o mesmo EstiloInteracao aqui, duplicando
            // o despacho por modo. Ver gerard.estilointeracao.estrategia.
            Stroke original = g2.getStroke();
            Color corOriginal = g2.getColor();
            scaffoldingProximidade.desenhar(modoFeedbackTeste, g2,
                    item.x, item.y, item.largura, item.altura,
                    alvo.x, alvo.y, alvo.largura, alvo.altura,
                    proximo, dentro);
            g2.setStroke(original);
            g2.setColor(corOriginal);
        }

        private void desenharRodapeInstrucao(Graphics2D g2) {
            // Instruções removidas da tela.
        }

        private void desenharAnotacaoMouseOver(Graphics2D g2) {
            boolean usarQuestionamentoPersistente = mostrarQuestionamentoPersistente
                    && itemQuestionadoPersistente != null
                    && itensArrastaveis.contains(itemQuestionadoPersistente)
                    && textoQuestionamentoPersistente != null
                    && textoQuestionamentoPersistente.trim().length() > 0;

            boolean usarLimiteQuantidadePersistente = !usarQuestionamentoPersistente
                    && mostrarLimiteQuantidadeQuestionado
                    && agrupamentoLimiteQuantidadeQuestionado != null
                    && circulosVenn.contains(agrupamentoLimiteQuantidadeQuestionado)
                    && textoLimiteQuantidadeQuestionado != null
                    && textoLimiteQuantidadeQuestionado.trim().length() > 0;

            // Aviso de sinal divergente do número relativo (2026-08-18) —
            // mesma família persistente, ancorado no item OU no elemento
            // (nunca os dois), conforme o menu de sinal foi aberto a partir
            // de um ou de outro.
            boolean usarSinalDivergentePersistente = !usarQuestionamentoPersistente
                    && !usarLimiteQuantidadePersistente
                    && mostrarSinalDivergentePersistente
                    && (itemSinalDivergentePersistente != null
                            ? itensArrastaveis.contains(itemSinalDivergentePersistente)
                            : (elementoSinalDivergentePersistente != null
                                    && elementosVergnaud.contains(elementoSinalDivergentePersistente)))
                    && textoSinalDivergentePersistente != null
                    && textoSinalDivergentePersistente.trim().length() > 0;

            // AG_AE — mesma família de anotação persistente das duas acima,
            // mas com uma checagem extra: se o papel foi resolvido (ou o
            // diagrama mudou) desde a última exibição, a dica se auto-fecha
            // aqui, no próximo repaint, em vez de exigir um gancho em cada
            // ponto onde um item pode ser solto sobre um elemento —
            // fechar aqui também encerra a ação correlacionada (mesmo
            // action_id) do §4.8.
            String fraseDicaPosicionamento = mostrarDicaPosicionamentoPersistente
                    && papelDicaPosicionamentoAtual != null
                    ? obterFraseParaDicaPosicionamento(papelDicaPosicionamentoAtual) : null;
            boolean usarDicaPosicionamentoPersistente = !usarQuestionamentoPersistente
                    && !usarLimiteQuantidadePersistente
                    && !usarSinalDivergentePersistente
                    && mostrarDicaPosicionamentoPersistente
                    && elementoDicaPosicionamentoPersistente != null
                    && elementosVergnaud.contains(elementoDicaPosicionamentoPersistente)
                    && papelDicaPosicionamentoAtual != null
                    && fraseDicaPosicionamento != null
                    && !avaliadorConclusaoModelagem.papelResolvido(
                            papelDicaPosicionamentoAtual, capturarPosicionamentosConclusao());
            if (mostrarDicaPosicionamentoPersistente && !usarDicaPosicionamentoPersistente) {
                scaffoldingAutomatizacaoPassos.encerrarAcao(papelDicaPosicionamentoAtual);
                mostrarDicaPosicionamentoPersistente = false;
                elementoDicaPosicionamentoPersistente = null;
                papelDicaPosicionamentoAtual = null;
            }

            if (!usarQuestionamentoPersistente && !usarLimiteQuantidadePersistente
                    && !usarSinalDivergentePersistente
                    && !usarDicaPosicionamentoPersistente && !mostrarAnotacaoMouseOver) {
                return;
            }

            String mensagem = usarQuestionamentoPersistente
                    ? textoQuestionamentoPersistente
                    : (usarLimiteQuantidadePersistente
                            ? textoLimiteQuantidadeQuestionado
                            : (usarSinalDivergentePersistente
                                    ? textoSinalDivergentePersistente
                                    : (usarDicaPosicionamentoPersistente
                                            ? localizacao.formatar("ui.hint.stepPlacement", fraseDicaPosicionamento)
                                            : textoAnotacaoMouseOver)));

            if (mensagem == null || mensagem.length() == 0) {
                return;
            }

            Font fonteAnotacao = new Font("Arial", Font.PLAIN, 13);
            Font fonteAnotacaoNegrito = fonteAnotacao.deriveFont(Font.BOLD);
            g2.setFont(fonteAnotacao);
            FontMetrics fm = g2.getFontMetrics(fonteAnotacao);

            int larguraMaxima = Math.min(430, Math.max(240, getWidth() - 40));
            java.util.List<java.util.List<FragmentoAnotacao>> linhas = quebrarTextoAnotacaoFormatado(
                    mensagem, fonteAnotacao, fonteAnotacaoNegrito, larguraMaxima - 18, g2);

            int larguraTexto = 0;
            for (java.util.List<FragmentoAnotacao> linha : linhas) {
                larguraTexto = Math.max(larguraTexto, medirLinhaAnotacao(linha, fonteAnotacao, fonteAnotacaoNegrito, g2));
            }

            int largura = larguraTexto + 18;
            int altura = linhas.size() * fm.getHeight() + 10;

            int baseX = mouseOverX;
            int baseY = mouseOverY;
            if (usarQuestionamentoPersistente) {
                baseX = itemQuestionadoPersistente.x + itemQuestionadoPersistente.largura;
                baseY = Math.max(50, itemQuestionadoPersistente.y + itemQuestionadoPersistente.altura / 2);
            } else if (usarLimiteQuantidadePersistente) {
                RepresentacaoComUnidadesAdicionaveis representacaoLimite =
                        criarRepresentacaoVennEditavel(
                                agrupamentoLimiteQuantidadeQuestionado);
                Rectangle areaControle = ehAgrupamentoTransformacaoComSinal(
                        representacaoLimite.obterAgrupamento())
                        ? obterAreaControleSinalAdicionar(
                                representacaoLimite.obterAgrupamento())
                        : controleAdicionarQuadradinhoVenn.obterArea(
                                representacaoLimite,
                                obterAreaDiagramaAditivo());
                baseX = areaControle.x + areaControle.width;
                baseY = Math.max(50, areaControle.y + areaControle.height / 2);
            } else if (usarSinalDivergentePersistente) {
                if (itemSinalDivergentePersistente != null) {
                    baseX = itemSinalDivergentePersistente.x + itemSinalDivergentePersistente.largura;
                    baseY = Math.max(50, itemSinalDivergentePersistente.y
                            + itemSinalDivergentePersistente.altura / 2);
                } else {
                    baseX = elementoSinalDivergentePersistente.x + elementoSinalDivergentePersistente.largura;
                    baseY = Math.max(50, elementoSinalDivergentePersistente.y
                            + elementoSinalDivergentePersistente.altura / 2);
                }
            } else if (usarDicaPosicionamentoPersistente) {
                baseX = elementoDicaPosicionamentoPersistente.x
                        + elementoDicaPosicionamentoPersistente.largura;
                baseY = Math.max(50, elementoDicaPosicionamentoPersistente.y
                        + elementoDicaPosicionamentoPersistente.altura / 2);
            }

            int x = baseX + 14;
            int y = forcarAnotacaoMouseOverAbaixo ? (baseY + 18) : (baseY - altura - 10);

            if (x + largura > getWidth() - 8) {
                x = getWidth() - largura - 8;
            }

            if (x < 8) {
                x = 8;
            }

            if (!forcarAnotacaoMouseOverAbaixo && y < 50) {
                y = baseY + 18;
            }

            if (y + altura > getHeight() - 8) {
                y = getHeight() - altura - 8;
            }

            Composite originalComposite = g2.getComposite();
            Stroke originalStroke = g2.getStroke();

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.97f));
            g2.setColor(COR_QUESTIONAMENTO);
            g2.fillRoundRect(x, y, largura, altura, 12, 12);

            g2.setColor(COR_AVISO);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(x, y, largura, altura, 10, 10);

            g2.setColor(COR_TEXTO);
            int yLinha = y + fm.getAscent() + 5;
            for (java.util.List<FragmentoAnotacao> linha : linhas) {
                desenharLinhaAnotacao(g2, linha, x + 9, yLinha, fonteAnotacao, fonteAnotacaoNegrito);
                yLinha += fm.getHeight();
            }

            g2.setComposite(originalComposite);
            g2.setStroke(originalStroke);
        }


        private java.util.List<java.util.List<FragmentoAnotacao>> quebrarTextoAnotacaoFormatado(
                String texto,
                Font fonteNormal,
                Font fonteNegrito,
                int larguraMaxima,
                Graphics2D g2
        ) {
            java.util.List<FragmentoAnotacao> tokens = tokenizarAnotacao(texto);
            java.util.List<java.util.List<FragmentoAnotacao>> linhas = new ArrayList<java.util.List<FragmentoAnotacao>>();
            java.util.List<FragmentoAnotacao> linhaAtual = new ArrayList<FragmentoAnotacao>();
            int larguraAtual = 0;

            for (FragmentoAnotacao tokenOriginal : tokens) {
                if (tokenOriginal == null || tokenOriginal.texto.length() == 0) {
                    continue;
                }

                FragmentoAnotacao token = tokenOriginal;
                int larguraToken = medirFragmentoAnotacao(token, fonteNormal, fonteNegrito, g2);

                if (!linhaAtual.isEmpty() && larguraAtual + larguraToken > larguraMaxima) {
                    linhas.add(linhaAtual);
                    linhaAtual = new ArrayList<FragmentoAnotacao>();
                    larguraAtual = 0;
                    token = new FragmentoAnotacao(removerEspacoInicial(token.texto), token.negrito);
                    larguraToken = medirFragmentoAnotacao(token, fonteNormal, fonteNegrito, g2);
                }

                linhaAtual.add(token);
                larguraAtual += larguraToken;
            }

            if (!linhaAtual.isEmpty()) {
                linhas.add(linhaAtual);
            }

            if (linhas.isEmpty()) {
                java.util.List<FragmentoAnotacao> linhaVazia = new ArrayList<FragmentoAnotacao>();
                linhaVazia.add(new FragmentoAnotacao("", false));
                linhas.add(linhaVazia);
            }

            return linhas;
        }

        private java.util.List<FragmentoAnotacao> tokenizarAnotacao(String texto) {
            java.util.List<FragmentoAnotacao> fragmentos = extrairFragmentosAnotacao(texto);
            java.util.List<FragmentoAnotacao> tokens = new ArrayList<FragmentoAnotacao>();

            for (FragmentoAnotacao fragmento : fragmentos) {
                if (fragmento == null || fragmento.texto == null) {
                    continue;
                }

                String textoFragmento = fragmento.texto.replace('\n', ' ');
                String[] palavras = textoFragmento.trim().length() == 0 ? new String[0] : textoFragmento.trim().split("\\s+");

                for (String palavra : palavras) {
                    if (palavra.length() == 0) {
                        continue;
                    }
                    boolean pontuacaoSemEspaco = palavra.matches("^[,.;:!?)]$");
                    boolean precisaEspaco = !tokens.isEmpty() && !pontuacaoSemEspaco;
                    String textoToken = (precisaEspaco ? " " : "") + palavra;
                    tokens.add(new FragmentoAnotacao(textoToken, fragmento.negrito));
                }
            }

            return tokens;
        }

        private java.util.List<FragmentoAnotacao> extrairFragmentosAnotacao(String texto) {
            java.util.List<FragmentoAnotacao> fragmentos = new ArrayList<FragmentoAnotacao>();
            String origem = texto == null ? "" : texto;
            int posicao = 0;

            while (posicao < origem.length()) {
                int inicioNegrito = origem.indexOf("<b>", posicao);
                if (inicioNegrito < 0) {
                    fragmentos.add(new FragmentoAnotacao(origem.substring(posicao), false));
                    break;
                }

                if (inicioNegrito > posicao) {
                    fragmentos.add(new FragmentoAnotacao(origem.substring(posicao, inicioNegrito), false));
                }

                int fimNegrito = origem.indexOf("</b>", inicioNegrito + 3);
                if (fimNegrito < 0) {
                    fragmentos.add(new FragmentoAnotacao(origem.substring(inicioNegrito + 3), true));
                    break;
                }

                fragmentos.add(new FragmentoAnotacao(origem.substring(inicioNegrito + 3, fimNegrito), true));
                posicao = fimNegrito + 4;
            }

            if (fragmentos.isEmpty()) {
                fragmentos.add(new FragmentoAnotacao(origem, false));
            }

            return fragmentos;
        }

        private String removerEspacoInicial(String texto) {
            if (texto == null) {
                return "";
            }
            int indice = 0;
            while (indice < texto.length() && Character.isWhitespace(texto.charAt(indice))) {
                indice++;
            }
            return texto.substring(indice);
        }

        private int medirLinhaAnotacao(java.util.List<FragmentoAnotacao> linha, Font fonteNormal, Font fonteNegrito, Graphics2D g2) {
            int largura = 0;
            for (FragmentoAnotacao fragmento : linha) {
                largura += medirFragmentoAnotacao(fragmento, fonteNormal, fonteNegrito, g2);
            }
            return largura;
        }

        private int medirFragmentoAnotacao(FragmentoAnotacao fragmento, Font fonteNormal, Font fonteNegrito, Graphics2D g2) {
            if (fragmento == null || fragmento.texto == null || fragmento.texto.length() == 0) {
                return 0;
            }
            Font fonte = fragmento.negrito ? fonteNegrito : fonteNormal;
            return g2.getFontMetrics(fonte).stringWidth(fragmento.texto);
        }

        private void desenharLinhaAnotacao(
                Graphics2D g2,
                java.util.List<FragmentoAnotacao> linha,
                int x,
                int y,
                Font fonteNormal,
                Font fonteNegrito
        ) {
            int xAtual = x;
            for (FragmentoAnotacao fragmento : linha) {
                if (fragmento == null || fragmento.texto == null) {
                    continue;
                }
                Font fonte = fragmento.negrito ? fonteNegrito : fonteNormal;
                g2.setFont(fonte);
                g2.drawString(fragmento.texto, xAtual, y);
                xAtual += g2.getFontMetrics(fonte).stringWidth(fragmento.texto);
            }
            g2.setFont(fonteNormal);
        }

        private java.util.List<String> quebrarTextoAnotacao(String texto, FontMetrics fm, int larguraMaxima) {
            java.util.List<String> linhas = new ArrayList<String>();

            if (texto == null || texto.trim().length() == 0) {
                linhas.add("");
                return linhas;
            }

            String textoNormalizado = texto.replace("\n", " ").trim();
            String[] palavras = textoNormalizado.split("\\s+");
            StringBuilder linhaAtual = new StringBuilder();

            for (String palavra : palavras) {
                String candidata = linhaAtual.length() == 0 ? palavra : linhaAtual.toString() + " " + palavra;

                if (fm.stringWidth(candidata) <= larguraMaxima) {
                    linhaAtual.setLength(0);
                    linhaAtual.append(candidata);
                } else {
                    if (linhaAtual.length() > 0) {
                        linhas.add(linhaAtual.toString());
                        linhaAtual.setLength(0);
                    }

                    if (fm.stringWidth(palavra) > larguraMaxima) {
                        quebrarPalavraLonga(palavra, fm, larguraMaxima, linhas, linhaAtual);
                    } else {
                        linhaAtual.append(palavra);
                    }
                }
            }

            if (linhaAtual.length() > 0) {
                linhas.add(linhaAtual.toString());
            }

            return linhas;
        }

        private void quebrarPalavraLonga(String palavra, FontMetrics fm, int larguraMaxima, java.util.List<String> linhas, StringBuilder linhaAtual) {
            StringBuilder pedaco = new StringBuilder();
            for (int i = 0; i < palavra.length(); i++) {
                String candidato = pedaco.toString() + palavra.charAt(i);
                if (fm.stringWidth(candidato) <= larguraMaxima) {
                    pedaco.append(palavra.charAt(i));
                } else {
                    if (pedaco.length() > 0) {
                        linhas.add(pedaco.toString());
                    }
                    pedaco.setLength(0);
                    pedaco.append(palavra.charAt(i));
                }
            }
            linhaAtual.append(pedaco.toString());
        }



        private void inicializarDiagramaVenn() {
            assinaturaDiagramaVennSincronizado = "";
            sincronizarDiagramaVennComRepresentacoes(true);
        }

        private void sincronizarDiagramaVennComRepresentacoes(boolean forcarReconstrucao) {
            if (forcarReconstrucao) {
                limparLimiteQuantidadeQuestionado();
            }
            if (forcarReconstrucao && ehGraficoBarrasComparacao()) {
                proporcaoControleComparacao = -1.0;
                ultimoValorInteiroControleComparacao = -1;
            }
            if (definicaoDiagramaAtual == null) {
                definicaoDiagramaAtual = catalogoDefinicoesAditivas.obter(tipoSituacaoSelecionada);
            }

            Rectangle areaAtual = obterAreaDiagramaAditivo();
            if (!coordenadorSincronizacaoRepresentacoes.estaSincronizando()) {
                capturarEstadoCompartilhadoDoVergnaud(-1,
                        EstadoSemanticoCompartilhado.Origem.VERGNAUD);
            }
            int[] valores = obterValoresSincronizadosParaDiagramaVenn();
            EstadoSemanticoCompartilhado.Snapshot snapshotTabuleiro =
                    estadoSemanticoCompartilhado.snapshot();
            if (ehGraficoBarrasComparacao()
                    && snapshotTabuleiro.isConhecido(0) && !snapshotTabuleiro.isConhecido(2)) {
                // Ao posicionar o referido, a barra do referendo começa com a
                // mesma quantidade de quadradinhos do referido — um ponto de
                // partida visual para o usuário ajustar com os controles
                // +/- até a quantidade correta. Só a contagem de quadradinhos
                // exibida muda; o papel semântico do referendo continua
                // desconhecido (o rótulo acima da barra permanece "?" e a
                // conclusão da modelagem não é antecipada).
                valores[2] = valores[0];
            }
            PlanoUnidadesProcessoTransformacao planoUnidadesProcesso =
                    selecionadorPlanoUnidadesTransformacao.criarPlano(
                            obterTipoRepresentacaoComplementarAtual(),
                            snapshotTabuleiro, situacaoProblemaAtual);
            planoUnidadesProcessoAtual = planoUnidadesProcesso;
            TipoSituacaoAditiva tipoVenn = tipoSituacaoSelecionada;
            String assinatura = criarAssinaturaDiagramaVenn(tipoVenn, areaAtual, valores);

            if (!forcarReconstrucao && assinatura.equals(assinaturaDiagramaVennSincronizado)) {
                return;
            }

            circulosVenn.clear();
            quadradinhosVenn.clear();
            ultimaAreaDiagramaVenn = new Rectangle(areaAtual);
            cenaDiagramaVennAtual = geradorCenaDiagramaVenn.gerar(tipoVenn, areaAtual, definicaoDiagramaAtual, valores);

            // Primeiro materializa todas as zonas. O processo de transformação
            // calcula canal e funis a partir do conjunto completo das três zonas.
            for (NoDiagramaVenn no : cenaDiagramaVennAtual.getNos()) {
                CirculoVenn circulo = new CirculoVenn(
                        no.getX(),
                        no.getY(),
                        no.getLargura(),
                        no.getAltura(),
                        no.getRotulo(),
                        no.getValorReferencia(),
                        no.isExibirQuadradinhos()
                );
                circulo.formaRetangular = no.getForma()
                        == NoDiagramaVenn.Forma.RETANGULO;
                circulosVenn.add(circulo);
            }
            // Depois distribui as unidades, garantindo que o layout especializado
            // enxergue estado inicial, transformação e estado final simultaneamente.
            for (int indiceVisual = 0;
                    indiceVisual < cenaDiagramaVennAtual.getNos().size();
                    indiceVisual++) {
                NoDiagramaVenn no = cenaDiagramaVennAtual.getNos().get(indiceVisual);
                if (!no.isExibirQuadradinhos()) {
                    continue;
                }
                CirculoVenn circulo = circulosVenn.get(indiceVisual);
                int quantidadeVisual;
                String origemVisual;
                if (planoUnidadesProcesso != null) {
                    quantidadeVisual = planoUnidadesProcesso
                            .isConhecido(indiceVisual)
                            ? planoUnidadesProcesso.getQuantidade(indiceVisual)
                            : 0;
                    origemVisual = planoUnidadesProcesso.getOrigem(indiceVisual);
                } else {
                    quantidadeVisual = ehGraficoBarrasComparacao()
                            ? limitarParaBarrinhasComparacao(no.getValorReferencia())
                            : limitarParaQuadradinhos(no.getValorReferencia());
                    origemVisual = no.getValorReferencia() < 0
                            ? "transformacao_negativa" : "situacao_problema";
                }
                adicionarQuadradinhosNoCirculo(
                        circulo, quantidadeVisual, origemVisual);
            }

            sincronizarValorRelativoComparacaoEmTodasAsDirecoes();
            assinaturaDiagramaVennSincronizado = assinatura;
        }

        private void sincronizarValorRelativoComparacaoEmTodasAsDirecoes() {
            if (!ehGraficoBarrasComparacao()) {
                return;
            }

            EstadoSemanticoCompartilhado.Snapshot snapshot = estadoSemanticoCompartilhado.snapshot();
            int valorRelativo = snapshot != null && snapshot.isConhecido(1)
                    ? snapshot.valorOuZero(1) : 0;
            int maximo = obterValorMaximoEscalaComparacao();
            int absoluto = relacaoEstruturalComparacao()
                    .calcularModuloDoValorRelativo(valorRelativo);

            if (maximo > 0) {
                proporcaoControleComparacao = Math.max(0.0, Math.min(1.0, absoluto / (double) maximo));
                ultimoValorInteiroControleComparacao = Math.max(0, Math.min(maximo, absoluto));
            } else {
                proporcaoControleComparacao = 0.0;
                ultimoValorInteiroControleComparacao = 0;
            }
        }

        private String criarAssinaturaDiagramaVenn(TipoSituacaoAditiva tipoVenn, Rectangle area, int[] valores) {
            return gerard.ui.venn.ConsultasDiagramaVenn
                    .criarAssinaturaDiagramaVenn(tipoVenn, area, valores);
        }

        private int[] obterValoresSincronizadosParaDiagramaVenn() {
            EstadoSemanticoCompartilhado.Snapshot snapshot = estadoSemanticoCompartilhado.snapshot();
            if (snapshot.getTipo() == tipoSituacaoSelecionada) {
                return new int[] {
                    snapshot.valorOuZero(0),
                    snapshot.valorOuZero(1),
                    snapshot.valorOuZero(2)
                };
            }
            return new int[] {0, 0, 0};
        }

        private EstadoSemanticoCompartilhado.Snapshot capturarEstadoCompartilhadoDoVergnaud(
                int indiceAlteradoReal, EstadoSemanticoCompartilhado.Origem origem) {
            atualizarIndicesEstadoCompartilhado(indiceAlteradoReal);
            Integer[] valores = new Integer[] { null, null, null };
            boolean[] conhecidos = new boolean[] { false, false, false };
            for (int i = 0; i < 3; i++) {
                int indiceReal = indicesElementosEstadoCompartilhado[i];
                if (elementosVergnaud != null && indiceReal >= 0
                        && indiceReal < elementosVergnaud.size()) {
                    Integer valor = obterValorNumericoDoElemento(elementosVergnaud.get(indiceReal));
                    valores[i] = valor;
                    conhecidos[i] = valor != null;
                }
            }
            int indicePapelAlterado = converterIndiceRealParaPapel(indiceAlteradoReal);
            EstadoSemanticoCompartilhado.Snapshot snapshot = estadoSemanticoCompartilhado.atualizar(
                    tipoSituacaoSelecionada, valores, conhecidos,
                    indicePapelAlterado, origem,
                    obterIndiceIncognitaProtegidaNoEstadoCompartilhado(),
                    incognitaPreenchidaPeloProtocoloMouseTexto());
            registrarLogConsistenciaAutomaticaSeHouve(snapshot, origem);
            return snapshot;
        }

        private void atualizarIndicesEstadoCompartilhado(int indiceAlteradoReal) {
            int quantidadeElementos = elementosVergnaud == null
                    ? 0 : elementosVergnaud.size();
            int indiceNumeroRelativo = -1;
            int indiceInicialAtual = indicesElementosEstadoCompartilhado == null
                    || indicesElementosEstadoCompartilhado.length == 0
                    ? 0 : indicesElementosEstadoCompartilhado[0];
            indicesElementosEstadoCompartilhado = seletorIndicesEstadoCompartilhado.selecionar(
                    false,
                    false,
                    quantidadeElementos,
                    indiceAlteradoReal,
                    indiceNumeroRelativo,
                    indiceInicialAtual);
        }

        private int converterIndiceRealParaPapel(int indiceReal) {
            return gerard.ui.enunciado.ConversorIndiceEstadoCompartilhado
                    .converterIndiceRealParaPapel(indicesElementosEstadoCompartilhado, indiceReal);
        }

        private EstadoSemanticoCompartilhado.Snapshot capturarEstadoCompartilhadoDoDiagramaComplementar(
                int indiceAlteradoVisual, EstadoSemanticoCompartilhado.Origem origem) {
            EstadoSemanticoCompartilhado.Snapshot anterior =
                    estadoSemanticoCompartilhado.snapshot();
            MapeamentoPapeisRepresentacaoComplementar mapeamento =
                    obterMapeamentoPapeisComplementaresAtual();
            final boolean processoTransformacao = ehProcessoTransformacaoMedidas()
                    || ehComposicaoTransformacoesProcesso();
            ValoresCapturadosRepresentacaoComplementar captura =
                    capturadorValoresRepresentacaoComplementar.capturar(
                            circulosVenn, mapeamento, tipoSituacaoSelecionada,
                            processoTransformacao, anterior, indiceAlteradoVisual,
                            true,
                            (indice, agrupamento) -> {
                                int quantidade = contarQuadradinhosNoCirculo(agrupamento);
                                if (processoTransformacao
                                        && planoUnidadesProcessoAtual != null) {
                                    quantidade = planoUnidadesProcessoAtual
                                            .converterUnidadesParaValor(quantidade);
                                }
                                return Integer.valueOf(quantidade);
                            },
                            this::converterTextoParaInteiro);
            EstadoSemanticoCompartilhado.Snapshot snapshot = estadoSemanticoCompartilhado.atualizar(
                    tipoSituacaoSelecionada, captura.getValores(), captura.getConhecidos(),
                    captura.getIndiceAlteradoSemantico(), origem,
                    obterIndiceIncognitaProtegidaNoEstadoCompartilhado(),
                    incognitaPreenchidaPeloProtocoloMouseTexto());
            registrarLogConsistenciaAutomaticaSeHouve(snapshot, origem);
            return snapshot;
        }

        /**
         * Traduz para o log de produção (origem SISTEMA) o fato que
         * EstadoSemanticoCompartilhado já determina internamente: qual papel,
         * se algum, foi resolvido/recalculado automaticamente na chamada de
         * atualizar() que gerou este snapshot
         * (Snapshot.getIndiceResolvidoAutomaticamente() — ver
         * EstadoSemanticoCompartilhado.definirSePermitido). Main não decide
         * nem recalcula nada aqui, só lê o fato já exposto pelo domínio — a
         * detecção (comparar o valor antes/depois da escrita) vive inteira
         * dentro do estado compartilhado, mais perto de onde a relação
         * estrutural é de fato resolvida (gerard-knowledge-locality-principle,
         * localidade relacional). Ver TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md.
         */
        private void registrarLogConsistenciaAutomaticaSeHouve(
                EstadoSemanticoCompartilhado.Snapshot snapshot,
                EstadoSemanticoCompartilhado.Origem origem) {
            if (snapshot == null) {
                return;
            }
            if (arrastandoControleComparacao) {
                // Arraste contínuo do controle das barras de Comparação:
                // adia o log até o fim do gesto (ver
                // flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao),
                // mantendo só o snapshot mais recente. A propagação de
                // estado que gerou este snapshot já aconteceu normalmente —
                // só o registro no log é que espera o soltar do mouse.
                logConsistenciaAutomaticaPendenteArrasteComparacao = snapshot;
                origemLogConsistenciaAutomaticaPendenteArrasteComparacao = origem;
                return;
            }
            registrarLogConsistenciaAutomaticaImediatamente(snapshot, origem);
        }

        private void registrarLogConsistenciaAutomaticaImediatamente(
                EstadoSemanticoCompartilhado.Snapshot snapshot,
                EstadoSemanticoCompartilhado.Origem origem) {
            int indiceResolvido = snapshot.getIndiceResolvidoAutomaticamente();
            if (indiceResolvido < 0) {
                return;
            }
            registrarLogComputador(
                    "Recomputo automático de consistência entre representações",
                    "Manutenção de consistência entre representações (REFERENCE.md §4.8)",
                    "Papel semântico índice " + indiceResolvido,
                    "Preencher ou recalcular automaticamente um papel a partir dos demais,"
                            + " mantendo a relação aditiva consistente entre as representações",
                    "Cálculo determinístico via RelacaoEstrutural*.calcularValorAusente/"
                            + "recalcularParaConsistencia (pacote piloto), delegado por"
                            + " EstadoSemanticoCompartilhado.resolverRelacaoAditiva",
                    "CONSISTENCIA_AUTOMATICA",
                    "origem=" + origem + "; papelResolvido=" + indiceResolvido
                            + "; valor=" + snapshot.valorOuZero(indiceResolvido));
        }

        /**
         * Escreve, se houver, o log CONSISTENCIA_AUTOMATICA represado durante
         * o arraste contínuo do controle das barras de Comparação — chamar
         * sempre que arrastandoControleComparacao voltar a false (soltura
         * normal em mouseReleased, ou reset defensivo no início de um novo
         * mousePressed, caso um arraste anterior tenha sido interrompido sem
         * passar por mouseReleased).
         */
        private void flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao() {
            if (logConsistenciaAutomaticaPendenteArrasteComparacao == null) {
                return;
            }
            EstadoSemanticoCompartilhado.Snapshot snapshot =
                    logConsistenciaAutomaticaPendenteArrasteComparacao;
            EstadoSemanticoCompartilhado.Origem origem =
                    origemLogConsistenciaAutomaticaPendenteArrasteComparacao;
            logConsistenciaAutomaticaPendenteArrasteComparacao = null;
            origemLogConsistenciaAutomaticaPendenteArrasteComparacao = null;
            registrarLogConsistenciaAutomaticaImediatamente(snapshot, origem);
        }

        private void aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(
                final EstadoSemanticoCompartilhado.Snapshot snapshot,
                boolean reconstruirComplementar) {
            coordenadorSincronizacaoRepresentacoes.sincronizar(
                    snapshot, reconstruirComplementar,
                    new DestinoSincronizacaoRepresentacoes() {
                        @Override
                        public void aplicarNoVergnaud(
                                EstadoSemanticoCompartilhado.Snapshot estado) {
                            aplicarEstadoCompartilhadoNoVergnaud(estado);
                        }

                        @Override
                        public void aplicarNoTexto(
                                EstadoSemanticoCompartilhado.Snapshot estado) {
                            sincronizarElementosSemanticosDoTexto(estado);
                        }

                        @Override
                        public void reconstruirRepresentacaoComplementar() {
                            sincronizarDiagramaVennComRepresentacoes(true);
                        }

                        @Override
                        public void aplicarNosEixos(
                                EstadoSemanticoCompartilhado.Snapshot estado) {
                            sincronizarEixosComEstadoCompartilhado(estado);
                        }
                    });
            // Ver Javadoc de atualizarPaineisEixosRelacoesComValoresAtuais():
            // fora do destino acima porque os painéis de Relações cobrem 3
            // papéis simultâneos (não um único índice como aplicarNosEixos),
            // e é um no-op fora das categorias de Relações.
            atualizarPaineisEixosRelacoesComValoresAtuais();
        }

        private void aplicarEstadoCompartilhadoNoVergnaud(
                EstadoSemanticoCompartilhado.Snapshot snapshot) {
            java.util.List<AtualizacaoElementoVergnaud> atualizacoes =
                    planejadorAplicacaoEstadoVergnaud.planejar(
                            snapshot, indicesElementosEstadoCompartilhado,
                            elementosVergnaud);
            for (AtualizacaoElementoVergnaud atualizacao : atualizacoes) {
                ElementoVergnaud elemento = elementosVergnaud.get(
                        atualizacao.getIndiceElemento());
                int valor = atualizacao.getValor();
                if (atualizacao.getNaturezaVisual()
                        == AtualizacaoElementoVergnaud.NaturezaVisual.NUMERO_RELATIVO) {
                    definirValorNoElementoNumeroRelativo(elemento, valor);
                } else {
                    definirValorNoElementoMedida(elemento,
                            servicoQuantidadeContextual.formatarMedidaParaDiagrama(
                                     Math.max(0, valor),
                                     situacaoProblemaAtual));
                }
            }
        }

        private void sincronizarElementosSemanticosDoTexto(
                final EstadoSemanticoCompartilhado.Snapshot snapshot) {
            if (snapshot == null || textoProblemaEhMensagemSistema) {
                return;
            }

            MapeadorPapelSemanticoTexto mapeador =
                    new gerard.ui.enunciado.MapeadorPapelSemanticoTextoPadrao(
                            tipoSituacaoSelecionada,
                            indicesElementosEstadoCompartilhado);

            sincronizadorElementosSemanticosTexto.sincronizar(
                    elementosTexto, snapshot, mapeador);

            java.util.List<ElementoSemanticoTexto> itensDoEnunciado =
                    gerard.ui.enunciado.SeletorItensTexto.itensAindaNoEnunciado(itensArrastaveis);
            sincronizadorElementosSemanticosTexto.sincronizar(
                    itensDoEnunciado, snapshot, mapeador);
            gerard.ui.enunciado.AjustadorDimensoesItensTexto.ajustar(
                    itensDoEnunciado, getFontMetrics(new Font("Arial", Font.BOLD, 20)));

            // Recalcula apenas o fluxo do enunciado. Os deslocamentos feitos
            // pelo usuário são preservados por xOriginal/yOriginal.
            layoutTextoInicializado = false;
        }


        private void sincronizarEixosComEstadoCompartilhado(
                EstadoSemanticoCompartilhado.Snapshot snapshot) {
            if (snapshot == null || !snapshot.isConhecido(1)
                    || elementosVergnaud == null || elementosVergnaud.size() < 2) {
                return;
            }
            int indiceRelacao = indicesElementosEstadoCompartilhado[1];
            if (indiceRelacao < 0 || indiceRelacao >= elementosVergnaud.size()) {
                return;
            }
            ElementoVergnaud relacao = elementosVergnaud.get(indiceRelacao);
            if (!ehElementoNumeroRelativo(relacao)) {
                return;
            }
            int valor = snapshot.valorOuZero(1);
            if (ehGraficoBarrasComparacao()) {
                int maximo = Math.max(1, obterValorMaximoEscalaComparacao());
                int modulo = relacaoEstruturalComparacao()
                        .calcularModuloDoValorRelativo(valor);
                proporcaoControleComparacao = Math.max(0.0,
                        Math.min(1.0, modulo / (double) maximo));
                ultimoValorInteiroControleComparacao = modulo;
            }
        }

        private void sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                ElementoVergnaud elemento,
                EstadoSemanticoCompartilhado.Origem origem) {
            int indice = obterIndiceVisualPorIdentidadeSemantica(elemento);
            EstadoSemanticoCompartilhado.Snapshot snapshot =
                    capturarEstadoCompartilhadoDoVergnaud(indice, origem);
            aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(snapshot, true);
        }

        private int obterIndiceVisualPorIdentidadeSemantica(
                ElementoVergnaud elemento) {
            if (elemento == null) {
                return -1;
            }
            return catalogoPapeisSemanticos.obterIndiceElementoPorPapel(
                    obterPapelSemanticoDoElemento(elemento),
                    tipoSituacaoSelecionada);
        }

        private void sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(
                int indiceAlterado,
                EstadoSemanticoCompartilhado.Origem origem) {
            EstadoSemanticoCompartilhado.Snapshot snapshot =
                    capturarEstadoCompartilhadoDoDiagramaComplementar(indiceAlterado, origem);
            aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(snapshot, true);
        }

        /**
         * A comparação não pode usar a ordem em que os números aparecem no
         * enunciado. Os valores são obtidos pelos papéis curados e devolvidos
         * na ordem: referido, valor relativo e referendo.
         */
        private int[] obterValoresSemanticosComparacao() {
            ElementoVergnaud elementoReferido = encontrarElementoVergnaudPorPapel(
                    "papel.referido");
            ElementoVergnaud elementoRelativo = encontrarElementoVergnaudPorPapel(
                    "papel.diferenca");
            ElementoVergnaud elementoReferendo = encontrarElementoVergnaudPorPapel(
                    "papel.referendo");
            return projetorValoresComparacaoComplementar.projetar(
                    situacaoProblemaAtual, localizacao,
                    obterValorNumericoDoElemento(elementoReferido),
                    obterValorNumericoDoElemento(elementoRelativo),
                    obterValorNumericoDoElemento(elementoReferendo));
        }

        private static final int LARGURA_BASE_TELA = 1240;
        private static final int ALTURA_BASE_TELA = 760;
        // Altura da faixa de atalhos de categoria (voltar/avançar, 3 ícones de
        // categoria, "Qual o próximo passo?") inserida entre o cabeçalho
        // (0-45) e a área do enunciado. Toda coordenada Y hardcoded que hoje
        // assume que o enunciado começa logo abaixo do cabeçalho soma esta
        // constante em vez de um novo número mágico — ver os pontos citados
        // em desenharTextoProblema, reposicionarBotaoAjudaTexto,
        // desenharAreaDiagrama e Y_BASE_VERGNAUD/VENN abaixo, além de
        // AreaTituloCategoriaEnunciado.desenhar (parâmetro yTitulo).
        // +20 em relação ao valor original (110) para abrir espaço aos
        // rótulos "Medidas"/"Relações" acima dos ícones (ver
        // desenharFaixaAtalhoCategoria) — decisão da usuária, 2026-07-28.
        private static final int ALTURA_PAINEL_ATALHOS_CATEGORIA = 130;
        private static final int X_BASE_VERGNAUD = 25;
        private static final int Y_BASE_VERGNAUD = 215 + ALTURA_PAINEL_ATALHOS_CATEGORIA;
        private static final int LARGURA_BASE_VERGNAUD = 655;
        private static final int ALTURA_BASE_VERGNAUD = 530;
        private static final int X_BASE_VENN = 715;
        private static final int Y_BASE_VENN = Y_BASE_VERGNAUD;
        private static final int LARGURA_BASE_VENN = 500;
        private static final int ALTURA_BASE_VENN = ALTURA_BASE_VERGNAUD;
        private static final int MARGEM_LATERAL_DIAGRAMAS = 15;
        private static final int ESPACO_BASE_ENTRE_DIAGRAMAS = X_BASE_VENN - (X_BASE_VERGNAUD + LARGURA_BASE_VERGNAUD);

        private Rectangle obterAreaDiagramaAditivo() {
            Rectangle[] areas = obterAreasDiagramasProporcionais();
            return areas[1];
        }

        private Rectangle obterAreaDiagramaCentralVergnaud() {
            return obterAreaVisivelDiagramasVergnaud();
        }

        private Rectangle obterAreaConteudoDiagramaVergnaud() {
            Rectangle limite = obterAreaVisivelDiagramasVergnaud();
            return new Rectangle(limite.x + 10, limite.y + 46, limite.width - 20, limite.height - 56);
        }

        private Rectangle obterAreaVisivelDiagramasVergnaud() {
            Rectangle[] areas = obterAreasDiagramasProporcionais();
            return areas[0];
        }

        private Rectangle[] obterAreasDiagramasProporcionais() {
            int larguraTela = getWidth() > 0 ? getWidth() : LARGURA_BASE_TELA;
            int alturaTela = getHeight() > 0 ? getHeight() : ALTURA_BASE_TELA;

            if (categoriaSelecionadaParaAtividade && !deveExibirDiagramaComplementar()) {
                int alturaComum = Math.max(300, alturaTela - Y_BASE_VERGNAUD - 16);
                Rectangle areaVergnaud = new Rectangle(
                        MARGEM_LATERAL_DIAGRAMAS,
                        Y_BASE_VERGNAUD,
                        Math.max(640, larguraTela - (MARGEM_LATERAL_DIAGRAMAS * 2)),
                        alturaComum);
                Rectangle areaOculta = new Rectangle(
                        areaVergnaud.x + areaVergnaud.width,
                        Y_BASE_VENN,
                        0,
                        alturaComum);
                return new Rectangle[] {areaVergnaud, areaOculta};
            }

            int espacoEntreDiagramas = ESPACO_BASE_ENTRE_DIAGRAMAS;
            int larguraDisponivel = Math.max(640, larguraTela - (MARGEM_LATERAL_DIAGRAMAS * 2));
            int larguraParaAreas = Math.max(600, larguraDisponivel - espacoEntreDiagramas);

            double proporcaoVergnaud = (double) LARGURA_BASE_VERGNAUD
                    / (double) (LARGURA_BASE_VERGNAUD + LARGURA_BASE_VENN);
            int larguraVergnaud = (int) Math.round(larguraParaAreas * proporcaoVergnaud);
            int larguraVenn = larguraParaAreas - larguraVergnaud;

            // Em telas estreitas, reduz os dois painéis proporcionalmente em vez
            // de preservar larguras mínimas que fariam o painel direito ultrapassar
            // a borda da janela.
            int larguraMinimaVergnaud = 320;
            int larguraMinimaVenn = 280;
            if (larguraVergnaud < larguraMinimaVergnaud) {
                larguraVergnaud = larguraMinimaVergnaud;
                larguraVenn = larguraParaAreas - larguraVergnaud;
            }
            if (larguraVenn < larguraMinimaVenn) {
                larguraVenn = larguraMinimaVenn;
                larguraVergnaud = larguraParaAreas - larguraVenn;
            }

            // Os dois painéis compartilham a mesma origem vertical e a mesma altura.
            // A altura é recalculada em cada pintura, mantendo o alinhamento durante
            // qualquer redimensionamento da janela.
            int alturaComum = Math.max(300, alturaTela - Y_BASE_VERGNAUD - 16);
            int alturaVergnaud = alturaComum;
            int alturaVenn = alturaComum;

            Rectangle areaVergnaud = new Rectangle(
                    MARGEM_LATERAL_DIAGRAMAS,
                    Y_BASE_VERGNAUD,
                    larguraVergnaud,
                    alturaVergnaud);
            Rectangle areaVenn = new Rectangle(
                    areaVergnaud.x + areaVergnaud.width + espacoEntreDiagramas,
                    Y_BASE_VENN,
                    larguraVenn,
                    alturaVenn);
            return new Rectangle[] {areaVergnaud, areaVenn};
        }

        /**
         * O material concreto (diagrama complementar — quadradinhos, barras,
         * processo) só aparece na última opção da escalada de Scaffolding
         * (3ª tentativa rejeitada consecutiva da incógnita atual — AG_EMCME),
         * não durante a modelagem normal (decisão de 2026-08-07, item 5 do
         * levantamento de pendências — TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md).
         * Antes disso o diagrama complementar sempre aparecia junto com o de
         * Vergnaud. `tentativasIncognitaAtual` é null antes da primeira
         * tentativa rejeitada de uma situação-problema — tratado como "não
         * bloqueado", igual a uma instância recém-criada.
         *
         * Entre 2026-08-07 e 2026-08-16 o diagrama complementar ficou visível
         * o tempo todo (constante temporária
         * EXIBIR_DIAGRAMA_COMPLEMENTAR_SEMPRE_PARA_TESTES = true), a pedido
         * da usuária, só para testar a interação com ele sem precisar errar 3
         * vezes a cada verificação. Encerrada essa fase de teste (decisão da
         * usuária, 2026-08-16, item 2 do levantamento de pendências de
         * 2026-08-11), a constante e o `||` que a usava foram removidos —
         * restaurando o comportamento definitivo abaixo sem mais nada a
         * desfazer, exatamente como a nota original previa.
         */
        private boolean deveExibirDiagramaComplementar() {
            boolean escaladaNoLimite = tentativasIncognitaAtual != null
                    && tentativasIncognitaAtual.estaBloqueadoPorLimiteTentativas();
            return seletorRepresentacaoComplementar.deveExibir(
                    categoriaSelecionadaParaAtividade,
                    tipoSituacaoSelecionada,
                    escaladaNoLimite);
        }

        private boolean ehProcessoTransformacaoMedidas() {
            return obterTipoRepresentacaoComplementarAtual()
                    == TipoRepresentacaoComplementar.PROCESSO_TRANSFORMACAO;
        }

        /** Composição de Transformações (2026-08-07) — três funis, um canal. */
        private boolean ehComposicaoTransformacoesProcesso() {
            return obterTipoRepresentacaoComplementarAtual()
                    == TipoRepresentacaoComplementar.PROCESSO_COMPOSICAO_TRANSFORMACOES;
        }

        private TipoRepresentacaoComplementar obterTipoRepresentacaoComplementarAtual() {
            return seletorRepresentacaoComplementar.selecionar(
                    tipoSituacaoSelecionada, false);
        }

        /**
         * Verdadeiro quando a categoria selecionada ainda cai no fallback
         * {@link TipoRepresentacaoComplementar#GENERICA} — círculos vazios
         * ligados por setas, sem quadradinhos nem qualquer conteúdo
         * manipulável. Hoje é o caso de TRANSFORMACAO_RELACAO e
         * COMPOSICAO_RELACOES (nunca tiveram representação complementar
         * própria — ver TAREFA_PENDENTE_REPRESENTACAO_COMPLEMENTAR_RELACOES.md).
         *
         * Usado só para suprimir o CONTEÚDO do painel complementar (o que é
         * desenhado dentro dele e os controles de clique). NÃO deve ser
         * combinado com {@link #deveExibirDiagramaComplementar()} nem com
         * {@link #obterAreasDiagramasProporcionais()} — o diagrama de
         * Vergnaud deve manter a mesma posição/tamanho de sempre, com ou
         * sem representação própria do lado (a pedido explícito da usuária,
         * 2026-08-08: "não mexa no diagrama de Vergnaud").
         */
        private boolean ehRepresentacaoComplementarGenerica() {
            return seletorRepresentacaoComplementar.selecionar(
                    tipoSituacaoSelecionada,
                    false)
                    == TipoRepresentacaoComplementar.GENERICA;
        }

        private int obterXDivisorDiagramas() {
            Rectangle areaVergnaud = obterAreaVisivelDiagramasVergnaud();
            return areaVergnaud.x + areaVergnaud.width + (ESPACO_BASE_ENTRE_DIAGRAMAS / 2);
        }

        private boolean pontoNoDiagramaVergnaud(int x, int y) {
            return categoriaSelecionadaParaAtividade
                    && obterAreaVisivelDiagramasVergnaud().contains(x, y);
        }

        private Rectangle criarZonaSemanticaElemento(Rectangle limite,
                int indiceElemento, int largura, int altura) {
            AreaDiagrama projetada = geradorCenaDiagrama
                    .projetarZonaFigura(cenaDiagramaAtual,
                            new AreaDiagrama(limite.x, limite.y,
                                    limite.width, limite.height),
                            indiceElemento);
            Rectangle zonaBase = new Rectangle(projetada.x, projetada.y,
                    projetada.largura, projetada.altura);
            int margemX = 12;
            int margemY = 12;
            int x = zonaBase.x + margemX;
            int y = zonaBase.y + margemY;
            int w = Math.max(largura + 18, zonaBase.width - (margemX * 2));
            int h = Math.max(altura + 18, zonaBase.height - (margemY * 2));
            int maxW = limite.x + limite.width - x;
            int maxH = limite.y + limite.height - y;
            return new Rectangle(x, y, Math.max(largura, Math.min(w, maxW)), Math.max(altura, Math.min(h, maxH)));
        }

        private Rectangle criarZonaSemanticaConector(Rectangle limite,
                int indiceConector) {
            AreaDiagrama projetada = geradorCenaDiagrama
                    .projetarZonaConector(cenaDiagramaAtual,
                            new AreaDiagrama(limite.x, limite.y,
                                    limite.width, limite.height),
                            indiceConector);
            return new Rectangle(projetada.x, projetada.y,
                    projetada.largura, projetada.altura);
        }

        private void inicializarDiagramaVergnaud() {
            Rectangle area = obterAreaConteudoDiagramaVergnaud();
            elementosVergnaud.clear();
            limparEstadoDicaPosicionamento();
            conectoresVergnaud.clear();
            desabilitarSincronizacaoEstadoFinal();
            // Os painéis (se houver) apontam para os ElementoVergnaud
            // antigos, prestes a serem descartados — desativa aqui
            // incondicionalmente (mesma categoria, novo sorteio, inclusive)
            // em vez de confiar só na autocorreção por repaint, que não
            // reativaria sozinha dentro da MESMA categoria de Relações.
            desativarPaineisEixosRelacoes();

            cenaDiagramaAtual = geradorCenaDiagrama.gerar(
                    tipoSituacaoSelecionada, area, definicaoDiagramaAtual,
                    extrairValoresDoTexto());

            if (cenaDiagramaAtual == null) {
                return;
            }

            Rectangle caixaCena = calcularCaixaCena(cenaDiagramaAtual);
            int deslocamentoCentroX = 0;
            int deslocamentoCentroY = 0;

            if (caixaCena != null) {
                deslocamentoCentroX = area.x + (area.width - caixaCena.width) / 2 - caixaCena.x;
                deslocamentoCentroY = area.y + (area.height - caixaCena.height) / 2 - caixaCena.y;
            }
            deslocamentoCentroXAplicadoDiagramaVergnaud = deslocamentoCentroX;
            deslocamentoCentroYAplicadoDiagramaVergnaud = deslocamentoCentroY;

            for (int i = 0; i < cenaDiagramaAtual.getConectores().size(); i++) {
                ConectorDiagrama conector = cenaDiagramaAtual.getConectores().get(i);
                Rectangle zona = criarZonaSemanticaConector(area, i);
                zona = deslocarZonaSemantica(
                        zona, deslocamentoCentroX, deslocamentoCentroY, area);
                if (conector.temAlvo()) {
                    conectoresVergnaud.add(new ConectorVergnaud(
                            conector.getTipo(),
                            conector.getX1() + deslocamentoCentroX,
                            conector.getY1() + deslocamentoCentroY,
                            conector.getX2() + deslocamentoCentroX,
                            conector.getY2() + deslocamentoCentroY,
                            conector.getLegenda(),
                            zona,
                            conector.getXAlvo() + deslocamentoCentroX,
                            conector.getYAlvo() + deslocamentoCentroY
                    ));
                } else {
                    conectoresVergnaud.add(new ConectorVergnaud(
                            conector.getTipo(),
                            conector.getX1() + deslocamentoCentroX,
                            conector.getY1() + deslocamentoCentroY,
                            conector.getX2() + deslocamentoCentroX,
                            conector.getY2() + deslocamentoCentroY,
                            conector.getLegenda(),
                            zona
                    ));
                }
            }

            int indiceIncognita = obterIndiceFiguraIncognitaAtual();
            for (int i = 0; i < cenaDiagramaAtual.getFiguras().size(); i++) {
                FiguraDiagrama figura = cenaDiagramaAtual.getFiguras().get(i);
                Rectangle zona = criarZonaSemanticaElemento(
                        area, i,
                        figura.getLargura(), figura.getAltura());
                zona = deslocarZonaSemantica(
                        zona, deslocamentoCentroX, deslocamentoCentroY, area);
                elementosVergnaud.add(new ElementoVergnaud(
                        figura.getX() + deslocamentoCentroX,
                        figura.getY() + deslocamentoCentroY,
                        figura.getLargura(),
                        figura.getAltura(),
                        figura.getTipo(),
                        figura.getRotulo(),
                        zona,
                        i == indiceIncognita,
                        figura.isExibirLupa(),
                        figura.getChavePapelSemantico()
                ));
            }

            // O diagrama deve iniciar vazio. A interrogação pertence ao enunciado
            // e só pode aparecer dentro de uma figura após o arraste do usuário.
            removerInterrogacoesPreenchidasAutomaticamenteNoDiagrama();
            aplicarSubtitulosPersonagensNoDiagramaVergnaud();
            seletorOperacaoRelacaoAluno.ativar(
                    tipoSituacaoSelecionada, situacaoProblemaAtual, elementosVergnaud,
                    conectoresVergnaud, SeletorOperacaoRelacaoAluno.TipoOperacaoSeletor.ENTRE_TRANSFORMACOES,
                    localizacao);
            seletorOperacaoEstadoTransformacaoAluno.ativar(
                    tipoSituacaoSelecionada, situacaoProblemaAtual, elementosVergnaud,
                    conectoresVergnaud, SeletorOperacaoRelacaoAluno.TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO,
                    localizacao);
        }

        /**
         * Reposiciona (translada) o diagrama de Vergnaud já montado para a
         * área atual do painel, sem recriar elementos/itens — preserva tudo
         * que o usuário já arrastou/preencheu. Chamado quando a janela é
         * redimensionada. Diferente de inicializarDiagramaVergnaud(), que
         * reconstrói o diagrama do zero e o deixaria vazio novamente.
         *
         * Cenas compostas (encadeadas ou de composição+transformação) usam
         * zonas calculadas diretamente a partir da área, em vez de um único
         * deslocamento de centralização — para essas, a translação simples
         * não é suficiente, então o método não faz nada nesses casos.
         */
        private void reposicionarDiagramaVergnaudParaAreaAtual() {
            if (cenaDiagramaAtual == null
                    || elementosVergnaud == null || elementosVergnaud.isEmpty()) {
                return;
            }

            Rectangle caixaCena = calcularCaixaCena(cenaDiagramaAtual);
            if (caixaCena == null) {
                return;
            }

            Rectangle area = obterAreaConteudoDiagramaVergnaud();
            int novoDeslocamentoCentroX = area.x + (area.width - caixaCena.width) / 2 - caixaCena.x;
            int novoDeslocamentoCentroY = area.y + (area.height - caixaCena.height) / 2 - caixaCena.y;

            int dx = novoDeslocamentoCentroX - deslocamentoCentroXAplicadoDiagramaVergnaud;
            int dy = novoDeslocamentoCentroY - deslocamentoCentroYAplicadoDiagramaVergnaud;
            if (dx == 0 && dy == 0) {
                return;
            }

            for (ElementoVergnaud elemento : elementosVergnaud) {
                if (elemento == null) continue;
                elemento.x += dx;
                elemento.y += dy;
                if (elemento.zonaPermitida != null) {
                    elemento.zonaPermitida = new Rectangle(
                            elemento.zonaPermitida.x + dx, elemento.zonaPermitida.y + dy,
                            elemento.zonaPermitida.width, elemento.zonaPermitida.height);
                }
            }
            for (ConectorVergnaud conector : conectoresVergnaud) {
                if (conector == null) continue;
                conector.x1 += dx;
                conector.y1 += dy;
                conector.x2 += dx;
                conector.y2 += dy;
                if (conector.temAlvo()) {
                    conector.xAlvo += dx;
                    conector.yAlvo += dy;
                }
                if (conector.zonaPermitida != null) {
                    conector.zonaPermitida = new Rectangle(
                            conector.zonaPermitida.x + dx, conector.zonaPermitida.y + dy,
                            conector.zonaPermitida.width, conector.zonaPermitida.height);
                }
            }
            if (itensArrastaveis != null) {
                for (ItemTextoArrastavel item : itensArrastaveis) {
                    // Só os itens já soltos dentro do diagrama acompanham o
                    // deslocamento; os que ainda estão no enunciado têm sua
                    // posição controlada pelo layout do texto, não pelo
                    // diagrama.
                    if (item != null && item.estaNoDiagrama()) {
                        item.x += dx;
                        item.y += dy;
                    }
                }
            }
            seletorOperacaoRelacaoAluno.reposicionar(dx, dy);
            seletorOperacaoEstadoTransformacaoAluno.reposicionar(dx, dy);

            deslocamentoCentroXAplicadoDiagramaVergnaud = novoDeslocamentoCentroX;
            deslocamentoCentroYAplicadoDiagramaVergnaud = novoDeslocamentoCentroY;
        }

        private void aplicarSubtitulosPersonagensNoDiagramaVergnaud() {
            if (situacaoProblemaAtual == null || elementosVergnaud == null || elementosVergnaud.isEmpty()) {
                return;
            }
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                ElementoVergnaud elemento = elementosVergnaud.get(i);
                if (elemento == null) {
                    continue;
                }
                elemento.subtitulo = obterSubtituloPersonagemParaElemento(elemento);
                if (cenaDiagramaAtual != null && i < cenaDiagramaAtual.getFiguras().size()) {
                    elemento.rotulosAcima = cenaDiagramaAtual.getFiguras().get(i)
                            .getPosicaoRotulo()
                            == gerard.campoaditivo.diagrama.modelo.PosicaoRotuloFigura.ACIMA;
                }
            }
        }

        private String obterSubtituloPersonagemParaElemento(ElementoVergnaud elemento) {
            if (situacaoProblemaAtual == null || elemento == null) {
                return "";
            }
            return valorSeguroPersonagem(SemanticaCuradaSituacao.buscarParticipante(
                    situacaoProblemaAtual, localizacao,
                    elemento.chavePapelSemantico));
        }

        private String valorSeguroPersonagem(String personagem) {
            return personagem == null ? "" : personagem.trim();
        }

        private void removerInterrogacoesPreenchidasAutomaticamenteNoDiagrama() {
            java.util.Iterator<ItemTextoArrastavel> it = itensArrastaveis.iterator();
            while (it.hasNext()) {
                ItemTextoArrastavel item = it.next();
                if (item != null && item.estaNoDiagrama()
                        && (SimboloDesconhecido.eh(item.valor) || SimboloDesconhecido.eh(item.origemValor))) {
                    it.remove();
                }
            }
            for (ElementoVergnaud elemento : elementosVergnaud) {
                if (elemento != null && SimboloDesconhecido.eh(elemento.textoEditavel)) {
                    elemento.textoEditavel = "";
                }
            }
        }

        private Rectangle calcularCaixaCena(CenaDiagramaAditivo cena) {
            Rectangle caixa = null;

            for (FiguraDiagrama figura : cena.getFiguras()) {
                Rectangle r = new Rectangle(figura.getX(), figura.getY(), figura.getLargura(), figura.getAltura());
                caixa = unirRetangulos(caixa, r);
            }

            for (ConectorDiagrama conector : cena.getConectores()) {
                int x = Math.min(conector.getX1(), conector.getX2());
                int y = Math.min(conector.getY1(), conector.getY2());
                int largura = Math.max(1, Math.abs(conector.getX2() - conector.getX1()));
                int altura = Math.max(1, Math.abs(conector.getY2() - conector.getY1()));
                Rectangle r = new Rectangle(x, y, largura, altura);
                caixa = unirRetangulos(caixa, r);
            }

            return caixa;
        }

        private Rectangle unirRetangulos(Rectangle atual, Rectangle novo) {
            if (atual == null) {
                return new Rectangle(novo);
            }
            Rectangle unido = new Rectangle(atual);
            unido.add(novo);
            return unido;
        }

        private Rectangle deslocarZonaSemantica(Rectangle zona, int dx, int dy, Rectangle limite) {
            Rectangle deslocada = new Rectangle(zona.x + dx, zona.y + dy, zona.width, zona.height);

            if (deslocada.width > limite.width) {
                deslocada.width = limite.width;
            }
            if (deslocada.height > limite.height) {
                deslocada.height = limite.height;
            }

            if (deslocada.x < limite.x) {
                deslocada.x = limite.x;
            }
            if (deslocada.y < limite.y) {
                deslocada.y = limite.y;
            }
            if (deslocada.x + deslocada.width > limite.x + limite.width) {
                deslocada.x = limite.x + limite.width - deslocada.width;
            }
            if (deslocada.y + deslocada.height > limite.y + limite.height) {
                deslocada.y = limite.y + limite.height - deslocada.height;
            }

            return deslocada;
        }

        private ElementoVergnaud encontrarElementoVergnaud(int x, int y) {
            for (int i = elementosVergnaud.size() - 1; i >= 0; i--) {
                ElementoVergnaud elemento = elementosVergnaud.get(i);
                if (elemento.contem(x, y)) {
                    return elemento;
                }
            }
            return null;
        }

        private ConectorVergnaud encontrarConectorVergnaud(int x, int y) {
            for (int i = conectoresVergnaud.size() - 1; i >= 0; i--) {
                ConectorVergnaud conector = conectoresVergnaud.get(i);
                if (conector.contem(x, y)) {
                    return conector;
                }
            }
            return null;
        }

        private int limitarParaQuadradinhos(int valor) {
            return gerard.ui.venn.UtilitariosComparacaoBarras.limitarParaQuadradinhos(valor);
        }

        private int limitarParaBarrinhasComparacao(int valor) {
            return gerard.ui.venn.UtilitariosComparacaoBarras.limitarParaBarrinhasComparacao(valor);
        }

        private int[] extrairValoresDoTexto() {
            int[] valores = new int[3];
            for (int i = 0; i < valores.length; i++) {
                valores[i] = extrairNumeroDoTexto(i);
            }
            return valores;
        }

        private int extrairNumeroDoTexto(int indice) {
            // Usa os papéis (alinhados por índice semântico: 0=inicial,
            // 1=transformação, 2=final) em vez da lista compactada de
            // números encontrados no texto — essa lista pula papéis
            // desconhecidos, o que desalinha o índice quando um papel
            // anterior (ex.: a transformação) é a incógnita.
            if (textoProblemaEhMensagemSistema || resultadoInterpretacao == null) {
                return 0;
            }
            java.util.List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();
            if (indice >= 0 && indice < papeis.size()) {
                return converterTextoNumeroParaInteiro(papeis.get(indice).getElemento());
            }
            return 0;
        }

        private void adicionarQuadradinhosNoCirculo(CirculoVenn circulo, int quantidade, String origem) {
            if (circulo == null || quantidade <= 0) {
                return;
            }

            int tamanho = 12;
            int espacamento = 18;

            if (ehProcessoTransformacaoMedidas()
                    && circulo.formaRetangular && circulo.exibirQuadradinhos) {
                java.util.List<Rectangle> posicoes =
                        layoutUnidadesProcessoTransformacao.calcular(
                                circulosVenn,
                                circulosVenn.indexOf(circulo),
                                quantidade);
                for (Rectangle posicao : posicoes) {
                    quadradinhosVenn.add(new QuadradinhoVenn(
                            posicao.x, posicao.y, posicao.width, origem));
                }
                return;
            }

            if (ehComposicaoTransformacoesProcesso()
                    && circulo.formaRetangular && circulo.exibirQuadradinhos) {
                java.util.List<Rectangle> posicoes =
                        layoutUnidadesComposicaoTransformacoes.calcular(
                                circulosVenn,
                                circulosVenn.indexOf(circulo),
                                quantidade);
                for (Rectangle posicao : posicoes) {
                    quadradinhosVenn.add(new QuadradinhoVenn(
                            posicao.x, posicao.y, posicao.width, origem));
                }
                return;
            }

            if (ehGraficoBarrasComparacao() && circulo.formaRetangular && circulo.exibirQuadradinhos) {
                // Os quadradinhos ficam compactados de baixo para cima. A
                // distância padrão não depende da quantidade momentânea da
                // barra, evitando que poucos elementos sejam espalhados por
                // toda a altura disponível.
                int alturaUtil = Math.max(24, circulo.altura - 16);
                int alturaNecessaria = tamanho + Math.max(0, quantidade - 1) * espacamento;
                if (alturaNecessaria > alturaUtil && quantidade > 1) {
                    espacamento = Math.max(6, (alturaUtil - tamanho) / (quantidade - 1));
                    tamanho = Math.max(5, Math.min(12, espacamento - 3));
                }

                int x = circulo.x + (circulo.largura - tamanho) / 2;
                int baseY = circulo.y + circulo.altura - tamanho - 7;

                for (int i = 0; i < quantidade; i++) {
                    int y = baseY - i * espacamento;
                    quadradinhosVenn.add(new QuadradinhoVenn(x, y, tamanho, origem));
                }
                return;
            }

            int margem = 14;
            int larguraUtil = Math.max(12, circulo.largura - 2 * margem);
            int alturaUtil = Math.max(12, circulo.altura - 2 * margem);

            // A grade se adapta à proporção da região e à quantidade, mantendo
            // todas as unidades dentro do diagrama sem impor limite arbitrário.
            double proporcao = larguraUtil / (double) Math.max(1, alturaUtil);
            int colunas = Math.max(1, (int) Math.ceil(Math.sqrt(quantidade * proporcao)));
            colunas = Math.min(colunas, quantidade);
            int linhas = (int) Math.ceil(quantidade / (double) colunas);

            int passoX = colunas <= 1 ? larguraUtil : Math.max(4, larguraUtil / colunas);
            int passoY = linhas <= 1 ? alturaUtil : Math.max(4, alturaUtil / linhas);
            tamanho = Math.max(3, Math.min(12, Math.min(passoX - 3, passoY - 3)));
            passoX = colunas <= 1 ? tamanho : Math.max(tamanho + 2, larguraUtil / colunas);
            passoY = linhas <= 1 ? tamanho : Math.max(tamanho + 2, alturaUtil / linhas);

            int larguraGrade = (colunas - 1) * passoX + tamanho;
            int alturaGrade = (linhas - 1) * passoY + tamanho;
            int inicioX = circulo.x + Math.max(4, (circulo.largura - larguraGrade) / 2);
            int inicioY = circulo.y + Math.max(4, (circulo.altura - alturaGrade) / 2);

            for (int i = 0; i < quantidade; i++) {
                int coluna = i % colunas;
                int linha = i / colunas;
                int x = inicioX + coluna * passoX;
                int y = inicioY + linha * passoY;
                quadradinhosVenn.add(new QuadradinhoVenn(x, y, tamanho, origem));
            }
        }

        private void desenharDiagramaVenn(Graphics2D g2) {
            if (!deveExibirDiagramaComplementar()) {
                return;
            }
            Rectangle area = obterAreaDiagramaAditivo();
            boolean composicaoMedidas = ehDiagramaVennComposicaoMedidas();
            boolean comparacaoMedidas = ehGraficoBarrasComparacao();
            boolean processoTransformacao = ehProcessoTransformacaoMedidas();
            boolean composicaoTransformacoesProcesso = ehComposicaoTransformacoesProcesso();
            /*
             * TRANSFORMACAO_RELACAO/COMPOSICAO_RELACOES caem no fallback
             * GENERICA (círculos vazios + setas, sem nenhum conteúdo
             * manipulável) — a pedido da usuária (2026-08-08), o painel
             * complementar deixa de desenhar qualquer coisa para esses
             * tipos, mas SEM alterar `deveExibirDiagramaComplementar()`
             * nem a área reservada para este painel: o diagrama de
             * Vergnaud deve continuar na mesma posição de sempre (ver
             * TAREFA_PENDENTE_REPRESENTACAO_COMPLEMENTAR_RELACOES.md).
             * O estado (sincronizarDiagramaVennComRepresentacoes) continua
             * sendo recalculado normalmente abaixo, só o desenho/controles
             * são suprimidos.
             */
            boolean representacaoGenerica = ehRepresentacaoComplementarGenerica();

            if (!representacaoGenerica) {
                desenharCard(g2, area.x, area.y, area.width, area.height, 18);
                reposicionarBotaoAjudaComplementar(area);
            } else if (botaoAjudaComplementar != null) {
                botaoAjudaComplementar.setVisible(false);
                botaoAjudaComplementar.setEnabled(false);
            }

            if (processoTransformacao) {
                renderizadorProcessoTransformacao.desenharCabecalho(
                        g2, area, localizacao);
            } else if (composicaoTransformacoesProcesso) {
                renderizadorComposicaoTransformacoesProcesso.desenharCabecalho(
                        g2, area, localizacao);
            } else if (!comparacaoMedidas && !representacaoGenerica) {
                String chaveTituloDiagrama = composicaoMedidas
                        ? "ui.collections.title"
                        : "ui.vann.title";
                g2.setColor(COR_TEXTO);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString(localizacao.texto(chaveTituloDiagrama), area.x + 18, area.y + 28);
            }

            boolean precisaReconstruirEstruturaVenn = cenaDiagramaVennAtual == null || ultimaAreaDiagramaVenn == null || !ultimaAreaDiagramaVenn.equals(area);
            if (precisaReconstruirEstruturaVenn
                    || (!handlerQuadradinhoVenn.estaAtivo()
                    && !handlerItemTextoArrastavel.estaAtivo()
                    && !handlerElementoTextoMovel.estaAtivo())) {
                sincronizarDiagramaVennComRepresentacoes(precisaReconstruirEstruturaVenn);
            }

            if (!representacaoGenerica && cenaDiagramaVennAtual != null) {
                for (ConectorDiagramaVenn conector : cenaDiagramaVennAtual.getConectores()) {
                    desenharSetaVenn(g2, conector.getX1(), conector.getY1(), conector.getX2(), conector.getY2());
                }
            }

            EstadoProcessoTransformacao estadoProcesso =
                    processoTransformacao
                    ? EstadoProcessoTransformacao.aPartir(
                            estadoSemanticoCompartilhado.snapshot())
                    : null;
            gerard.campoaditivo.transformacao.composicao.EstadoComposicaoTransformacoes
                    estadoComposicaoTransformacoes = composicaoTransformacoesProcesso
                    ? gerard.campoaditivo.transformacao.composicao.EstadoComposicaoTransformacoes.aPartir(
                            estadoSemanticoCompartilhado.snapshot())
                    : null;
            for (int i = 0; i < circulosVenn.size(); i++) {
                if (processoTransformacao) {
                    renderizadorProcessoTransformacao.desenharZona(
                            g2, circulosVenn.get(i), i,
                            estadoProcesso, localizacao,
                            planoUnidadesProcessoAtual);
                } else if (composicaoTransformacoesProcesso) {
                    renderizadorComposicaoTransformacoesProcesso.desenharZona(
                            g2, circulosVenn.get(i), i,
                            estadoComposicaoTransformacoes, localizacao,
                            planoUnidadesProcessoAtual);
                } else if (!representacaoGenerica) {
                    desenharCirculoVenn(g2, circulosVenn.get(i),
                            composicaoMedidas, comparacaoMedidas);
                }
            }
            if (processoTransformacao) {
                renderizadorProcessoTransformacao.desenharEstrutura(
                        g2, circulosVenn, estadoProcesso,
                        planoUnidadesProcessoAtual);
            } else if (composicaoTransformacoesProcesso) {
                renderizadorComposicaoTransformacoesProcesso.desenharEstrutura(
                        g2, circulosVenn, estadoComposicaoTransformacoes,
                        planoUnidadesProcessoAtual, localizacao);
            }

            if (!representacaoGenerica) {
                atualizarQuadradinhosCorrespondentesComparacao(comparacaoMedidas);
                for (int i = 0; i < quadradinhosVenn.size(); i++) {
                    QuadradinhoVenn quadradinho = quadradinhosVenn.get(i);
                    if (quadradinho != handlerQuadradinhoVenn.obterQuadradinhoAtivo()) {
                        desenharQuadradinhoVenn(g2, quadradinho, composicaoMedidas, comparacaoMedidas);
                    }
                }

                if (composicaoMedidas) {
                    desenharContagensComposicaoMedidasVenn(g2, area);
                } else if (comparacaoMedidas) {
                    desenharResumoComparacaoMedidas(g2, area);
                }

                desenharControlesAdicionarQuadradinhoVenn(g2, area);
                desenharControlesRemoverQuadradinhoVenn(g2, area);
            }
        }

        private java.util.List<RepresentacaoComUnidadesAdicionaveis>
                obterRepresentacoesComUnidadesAdicionaveis() {
            java.util.List<RepresentacaoComUnidadesAdicionaveis> representacoes =
                    new ArrayList<RepresentacaoComUnidadesAdicionaveis>();
            for (CirculoVenn agrupamento : circulosVenn) {
                if (agrupamento != null && agrupamento.exibirQuadradinhos) {
                    representacoes.add(criarRepresentacaoVennEditavel(agrupamento));
                }
            }
            return representacoes;
        }

        private java.util.List<RepresentacaoComUnidadesRemoviveis>
                obterRepresentacoesComUnidadesRemoviveis() {
            java.util.List<RepresentacaoComUnidadesRemoviveis> representacoes =
                    new ArrayList<RepresentacaoComUnidadesRemoviveis>();
            for (CirculoVenn agrupamento : circulosVenn) {
                if (agrupamento != null && agrupamento.exibirQuadradinhos) {
                    representacoes.add(criarRepresentacaoVennEditavel(agrupamento));
                }
            }
            return representacoes;
        }

        private RepresentacaoVennEditavel criarRepresentacaoVennEditavel(
                CirculoVenn agrupamento) {
            int indiceAgrupamento = circulosVenn.indexOf(agrupamento);
            String papelSemantico = "";
            if (indiceAgrupamento >= 0) {
                int indiceReal = obterIndiceRealDoAgrupamento(indiceAgrupamento);
                String chave = catalogoPapeisSemanticos
                        .obterChavePapelDoElemento(
                                tipoSituacaoSelecionada, indiceReal);
                papelSemantico = localizacao.texto(chave);
            }
            return new RepresentacaoVennEditavel(
                    agrupamento, papelSemantico, operacoesUnidadesVenn);
        }

        private boolean diagramaVergnaudPossuiConteudoSemantico() {
            if (!categoriaSelecionadaParaAtividade
                    || elementosVergnaud == null
                    || elementosVergnaud.isEmpty()) {
                return false;
            }

            for (ElementoVergnaud elemento : elementosVergnaud) {
                if (elemento == null) {
                    continue;
                }
                ItemTextoArrastavel item = encontrarItemSobreElemento(elemento);
                if (item != null) {
                    return true;
                }
                String texto = elemento.textoEditavel == null
                        ? "" : elemento.textoEditavel.trim();
                if (texto.length() > 0) {
                    return true;
                }
            }
            return false;
        }

        private boolean interacaoRepresentacoesLiberadaPelaModelagem() {
            return politicaInteracaoRepresentacoes.estaLiberada();
        }

        private boolean adicaoDeUnidadesLiberadaPelaModelagem() {
            return condicaoEdicaoAposInicioVergnaud.estaSatisfeita()
                    && interacaoRepresentacoesLiberadaPelaModelagem();
        }

        private String obterMensagemBloqueioInteracaoRepresentacoes() {
            return localizacao.texto(
                    politicaInteracaoRepresentacoes.obterChaveMensagemBloqueio());
        }

        private void informarBloqueioInteracaoRepresentacao(int x, int y, String artefato) {
            mostrarAnotacaoMouseOver = true;
            textoAnotacaoMouseOver = obterMensagemBloqueioInteracaoRepresentacoes();
            mouseOverX = x;
            mouseOverY = y;
            setCursor(Cursor.getDefaultCursor());
            registrarAcaoGranular(
                    "SELECIONAR",
                    "Tentar manipular representação antes do primeiro posicionamento",
                    "Representações sincronizadas",
                    artefato == null ? "Representação" : artefato,
                    "Aguardar o primeiro preenchimento semântico no diagrama de Vergnaud",
                    "bloqueio=vergnaud_sem_conteudo_semantico",
                    "A representação permaneceu inalterada.");
            repaint();
        }

        private boolean podeAlterarQuantidadeNoEstadoAtual(
                CirculoVenn agrupamento, int variacao) {
            if (agrupamento == null || !agrupamento.exibirQuadradinhos
                    || variacao == 0) {
                return false;
            }
            int indiceVisual = circulosVenn.indexOf(agrupamento);
            if (indiceVisual < 0) {
                return false;
            }
            int quantidadeAtual = contarQuadradinhosNoAgrupamento(agrupamento);
            int quantidadeProposta = quantidadeAtual + variacao;
            if (quantidadeProposta < 0) {
                return false;
            }

            EstadoSemanticoCompartilhado.Snapshot simulado =
                    simularEstadoCompartilhadoAposAlteracaoQuantidade(
                            indiceVisual, quantidadeProposta);
            return simulado != null
                    && estadoSimuladoRespeitaLimitesDasQuantidades(simulado);
        }

        private EstadoSemanticoCompartilhado.Snapshot
                simularEstadoCompartilhadoAposAlteracaoQuantidade(
                        int indiceAlteradoVisual, int quantidadeProposta) {
            return simuladorEstadoComplementarVenn.simular(
                    circulosVenn,
                    obterMapeamentoPapeisComplementaresAtual(),
                    tipoSituacaoSelecionada,
                    ehProcessoTransformacaoMedidas() || ehComposicaoTransformacoesProcesso(),
                    estadoSemanticoCompartilhado.snapshot(),
                    indiceAlteradoVisual,
                    quantidadeProposta,
                    this::contarQuadradinhosNoCirculo,
                    this::converterTextoParaInteiro);
        }

        private boolean estadoSimuladoRespeitaLimitesDasQuantidades(
                EstadoSemanticoCompartilhado.Snapshot snapshot) {
            return simuladorEstadoComplementarVenn.respeitaLimites(
                    snapshot,
                    circulosVenn,
                    obterMapeamentoPapeisComplementaresAtual(),
                    tipoSituacaoSelecionada,
                    ehProcessoTransformacaoMedidas() || ehComposicaoTransformacoesProcesso(),
                    this::obterLimiteSemanticoCuradoDoAgrupamento);
        }

        private String obterMensagemBloqueioAdicaoUnidades() {
            return localizacao.texto(
                    condicaoEdicaoAposInicioVergnaud.obterChaveMensagemBloqueio());
        }

        private EstadoProcessoTransformacao obterEstadoVisualProcessoTransformacao() {
            return EstadoProcessoTransformacao.aPartir(
                    estadoSemanticoCompartilhado.snapshot());
        }

        private gerard.campoaditivo.transformacao.composicao.EstadoComposicaoTransformacoes
                obterEstadoVisualComposicaoTransformacoes() {
            return gerard.campoaditivo.transformacao.composicao.EstadoComposicaoTransformacoes.aPartir(
                    estadoSemanticoCompartilhado.snapshot());
        }

        /**
         * Ponto único de despacho entre o widget de Transformação de Medidas
         * (um funil implícito) e o de Composição de Transformações (três
         * funis, indexados por agrupamento) para os controles de sinal
         * +/- — evita duplicar a ramificação em cada um dos pontos de
         * desenho/hit-test abaixo. Ver
         * RELATORIO_PROCESSO_COMPOSICAO_TRANSFORMACOES_2026-08-07.md.
         */
        private Rectangle obterAreaControleSinalAdicionar(CirculoVenn agrupamento) {
            if (ehComposicaoTransformacoesProcesso()) {
                return controleSinalComposicaoTransformacoes.obterAreaAdicionar(
                        circulosVenn, obterEstadoVisualComposicaoTransformacoes(),
                        circulosVenn.indexOf(agrupamento));
            }
            return controleSinalProcessoTransformacao.obterAreaAdicionar(
                    circulosVenn, obterEstadoVisualProcessoTransformacao());
        }

        private boolean controleSinalContemAdicionar(CirculoVenn agrupamento, int x, int y) {
            if (ehComposicaoTransformacoesProcesso()) {
                return controleSinalComposicaoTransformacoes.contemAdicionar(
                        circulosVenn, obterEstadoVisualComposicaoTransformacoes(),
                        circulosVenn.indexOf(agrupamento), x, y);
            }
            return controleSinalProcessoTransformacao.contemAdicionar(
                    circulosVenn, obterEstadoVisualProcessoTransformacao(), x, y);
        }

        private boolean controleSinalContemRemover(CirculoVenn agrupamento, int x, int y) {
            if (ehComposicaoTransformacoesProcesso()) {
                return controleSinalComposicaoTransformacoes.contemRemover(
                        circulosVenn, obterEstadoVisualComposicaoTransformacoes(),
                        circulosVenn.indexOf(agrupamento), x, y);
            }
            return controleSinalProcessoTransformacao.contemRemover(
                    circulosVenn, obterEstadoVisualProcessoTransformacao(), x, y);
        }

        private void desenharControleSinalAdicionar(Graphics2D g2,
                CirculoVenn agrupamento, boolean focado, boolean habilitado) {
            if (ehComposicaoTransformacoesProcesso()) {
                controleSinalComposicaoTransformacoes.desenharAdicionar(
                        g2, circulosVenn, obterEstadoVisualComposicaoTransformacoes(),
                        circulosVenn.indexOf(agrupamento), focado, habilitado);
                return;
            }
            controleSinalProcessoTransformacao.desenharAdicionar(
                    g2, circulosVenn, obterEstadoVisualProcessoTransformacao(),
                    focado, habilitado);
        }

        private void desenharControleSinalRemover(Graphics2D g2,
                CirculoVenn agrupamento, boolean focado, boolean habilitado) {
            if (ehComposicaoTransformacoesProcesso()) {
                controleSinalComposicaoTransformacoes.desenharRemover(
                        g2, circulosVenn, obterEstadoVisualComposicaoTransformacoes(),
                        circulosVenn.indexOf(agrupamento), focado, habilitado);
                return;
            }
            controleSinalProcessoTransformacao.desenharRemover(
                    g2, circulosVenn, obterEstadoVisualProcessoTransformacao(),
                    focado, habilitado);
        }

        private void desenharControlesAdicionarQuadradinhoVenn(Graphics2D g2, Rectangle area) {
            if (g2 == null || area == null) {
                return;
            }
            boolean modelagemIniciada = adicaoDeUnidadesLiberadaPelaModelagem();
            for (RepresentacaoComUnidadesAdicionaveis representacao :
                    obterRepresentacoesComUnidadesAdicionaveis()) {
                boolean habilitado = modelagemIniciada
                        && (ehAgrupamentoTransformacaoComSinal(
                                representacao.obterAgrupamento())
                                ? podeIncrementarValorAssinadoTransformacao(
                                        representacao.obterAgrupamento())
                                : representacao.podeAdicionarUnidade());
                boolean focado = habilitado
                        && representacao.obterAgrupamento()
                        == agrupamentoAdicionarQuadradinhoFocado;
                if (ehAgrupamentoTransformacaoComSinal(
                        representacao.obterAgrupamento())) {
                    desenharControleSinalAdicionar(g2,
                            representacao.obterAgrupamento(), focado, habilitado);
                } else {
                    controleAdicionarQuadradinhoVenn.desenhar(
                            g2, representacao, area, focado, habilitado);
                }
            }
        }

        private RepresentacaoComUnidadesAdicionaveis
                encontrarRepresentacaoPeloControleAdicionarQuadradinho(int x, int y) {
            if (!deveExibirDiagramaComplementar() || ehRepresentacaoComplementarGenerica()) {
                return null;
            }
            Rectangle area = obterAreaDiagramaAditivo();
            java.util.List<RepresentacaoComUnidadesAdicionaveis> representacoes =
                    obterRepresentacoesComUnidadesAdicionaveis();
            for (int i = representacoes.size() - 1; i >= 0; i--) {
                RepresentacaoComUnidadesAdicionaveis representacao = representacoes.get(i);
                boolean contem = ehAgrupamentoTransformacaoComSinal(
                        representacao.obterAgrupamento())
                        ? controleSinalContemAdicionar(
                                representacao.obterAgrupamento(), x, y)
                        : controleAdicionarQuadradinhoVenn.contem(
                                representacao, area, x, y);
                if (contem) {
                    return representacao;
                }
            }
            return null;
        }

        private void desenharControlesRemoverQuadradinhoVenn(Graphics2D g2, Rectangle area) {
            if (g2 == null || area == null) {
                return;
            }
            boolean modelagemIniciada = adicaoDeUnidadesLiberadaPelaModelagem();
            for (RepresentacaoComUnidadesRemoviveis representacao :
                    obterRepresentacoesComUnidadesRemoviveis()) {
                boolean habilitado = modelagemIniciada
                        && (ehAgrupamentoTransformacaoComSinal(
                                representacao.obterAgrupamento())
                                ? podeDecrementarValorAssinadoTransformacao(
                                        representacao.obterAgrupamento())
                                : representacao.podeRemoverUnidade());
                boolean focado = habilitado
                        && representacao.obterAgrupamento()
                        == agrupamentoRemoverQuadradinhoFocado;
                if (ehAgrupamentoTransformacaoComSinal(
                        representacao.obterAgrupamento())) {
                    desenharControleSinalRemover(g2,
                            representacao.obterAgrupamento(), focado, habilitado);
                } else {
                    controleRemoverQuadradinhoVenn.desenhar(
                            g2, representacao, area, focado, habilitado);
                }
            }
        }

        private int contarQuadradinhosNoAgrupamento(CirculoVenn agrupamento) {
            if (agrupamento == null) {
                return 0;
            }
            return contarQuadradinhosNoCirculo(agrupamento);
        }

        private RepresentacaoComUnidadesRemoviveis
                encontrarRepresentacaoPeloControleRemoverQuadradinho(int x, int y) {
            if (!deveExibirDiagramaComplementar() || ehRepresentacaoComplementarGenerica()) {
                return null;
            }
            Rectangle area = obterAreaDiagramaAditivo();
            java.util.List<RepresentacaoComUnidadesRemoviveis> representacoes =
                    obterRepresentacoesComUnidadesRemoviveis();
            for (int i = representacoes.size() - 1; i >= 0; i--) {
                RepresentacaoComUnidadesRemoviveis representacao = representacoes.get(i);
                boolean contem = ehAgrupamentoTransformacaoComSinal(
                        representacao.obterAgrupamento())
                        ? controleSinalContemRemover(
                                representacao.obterAgrupamento(), x, y)
                        : controleRemoverQuadradinhoVenn.contem(
                                representacao, area, x, y);
                if (contem) {
                    return representacao;
                }
            }
            return null;
        }

        private boolean ehAgrupamentoTransformacaoComSinal(CirculoVenn agrupamento) {
            if (!(ehProcessoTransformacaoMedidas() || ehComposicaoTransformacoesProcesso())
                    || agrupamento == null) {
                return false;
            }
            int indiceAgrupamento = circulosVenn.indexOf(agrupamento);
            if (indiceAgrupamento < 0) {
                return false;
            }
            int indiceSemantico = obterIndiceSemanticoDoAgrupamento(indiceAgrupamento);
            return politicaSinalTransformacaoComplementar
                    .permiteValorAssinado(tipoSituacaoSelecionada, indiceSemantico);
        }

        private int obterValorAssinadoAtualDoAgrupamento(CirculoVenn agrupamento) {
            if (agrupamento == null) {
                return 0;
            }
            int indiceAgrupamento = circulosVenn.indexOf(agrupamento);
            if (indiceAgrupamento < 0) {
                return 0;
            }
            int indiceSemantico = obterIndiceSemanticoDoAgrupamento(indiceAgrupamento);
            EstadoSemanticoCompartilhado.Snapshot snapshot =
                    estadoSemanticoCompartilhado.snapshot();
            if (snapshot != null && snapshot.isConhecido(indiceSemantico)) {
                return snapshot.valorOuZero(indiceSemantico);
            }
            return agrupamento.valorReferencia;
        }

        private EstadoSemanticoCompartilhado.Snapshot
                simularEstadoCompartilhadoAposAlteracaoValorAssinado(
                        int indiceAlteradoVisual, int valorAssinadoProposto) {
            if (indiceAlteradoVisual < 0 || indiceAlteradoVisual >= circulosVenn.size()) {
                return null;
            }

            Integer[] valores = new Integer[] { null, null, null };
            boolean[] conhecidos = new boolean[] { false, false, false };
            EstadoSemanticoCompartilhado.Snapshot anterior =
                    estadoSemanticoCompartilhado.snapshot();
            MapeamentoPapeisRepresentacaoComplementar mapeamento =
                    obterMapeamentoPapeisComplementaresAtual();

            for (int indiceVisual = 0;
                    indiceVisual < 3 && indiceVisual < circulosVenn.size();
                    indiceVisual++) {
                int indiceSemantico = mapeamento.paraIndiceSemantico(indiceVisual);
                if (indiceSemantico < 0) {
                    continue;
                }
                CirculoVenn no = circulosVenn.get(indiceVisual);
                if (no.exibirQuadradinhos) {
                    int quantidade = contarQuadradinhosNoCirculo(no);
                    if (indiceVisual == indiceAlteradoVisual) {
                        quantidade = ehAgrupamentoTransformacaoComSinal(no)
                                ? valorAssinadoProposto
                                : politicaSinalTransformacaoComplementar
                                        .magnitudeParaUnidades(valorAssinadoProposto);
                    } else if ((ehProcessoTransformacaoMedidas()
                                    || ehComposicaoTransformacoesProcesso())
                            && politicaSinalTransformacaoComplementar
                                    .permiteValorAssinado(tipoSituacaoSelecionada, indiceSemantico)) {
                        Integer valorAnterior = anterior != null
                                && anterior.isConhecido(indiceSemantico)
                                ? Integer.valueOf(anterior.valorOuZero(indiceSemantico))
                                : null;
                        quantidade = politicaSinalTransformacaoComplementar
                                .aplicarSinal(quantidade,
                                        no.valorReferencia, valorAnterior);
                    }
                    valores[indiceSemantico] = Integer.valueOf(quantidade);
                    conhecidos[indiceSemantico] = politicaSinalTransformacaoComplementar
                            .magnitudeParaUnidades(quantidade) > 0
                            || indiceVisual == indiceAlteradoVisual
                            || (anterior != null
                                && anterior.isConhecido(indiceSemantico));
                } else {
                    Integer editado = converterTextoParaInteiro(no.textoEditavel);
                    if (editado != null) {
                        valores[indiceSemantico] = editado;
                        conhecidos[indiceSemantico] = true;
                    } else if (no.valorReferencia != 0
                            || (anterior != null
                                && anterior.isConhecido(indiceSemantico))) {
                        valores[indiceSemantico] = Integer.valueOf(no.valorReferencia);
                        conhecidos[indiceSemantico] = true;
                    }
                }
            }

            EstadoSemanticoCompartilhado simulacao =
                    new EstadoSemanticoCompartilhado();
            return simulacao.atualizar(
                    tipoSituacaoSelecionada, valores, conhecidos,
                    mapeamento.paraIndiceSemantico(indiceAlteradoVisual),
                    EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        }

        private boolean podeIncrementarValorAssinadoTransformacao(
                CirculoVenn agrupamento) {
            if (!ehAgrupamentoTransformacaoComSinal(agrupamento)) {
                return false;
            }
            int indiceAgrupamento = circulosVenn.indexOf(agrupamento);
            int novoValor = obterValorAssinadoAtualDoAgrupamento(agrupamento) + 1;
            EstadoSemanticoCompartilhado.Snapshot simulado =
                    simularEstadoCompartilhadoAposAlteracaoValorAssinado(
                            indiceAgrupamento, novoValor);
            return simulado != null
                    && estadoSimuladoRespeitaLimitesDasQuantidades(simulado);
        }

        private boolean podeDecrementarValorAssinadoTransformacao(
                CirculoVenn agrupamento) {
            if (!ehAgrupamentoTransformacaoComSinal(agrupamento)) {
                return false;
            }
            int indiceAgrupamento = circulosVenn.indexOf(agrupamento);
            int novoValor = obterValorAssinadoAtualDoAgrupamento(agrupamento) - 1;
            EstadoSemanticoCompartilhado.Snapshot simulado =
                    simularEstadoCompartilhadoAposAlteracaoValorAssinado(
                            indiceAgrupamento, novoValor);
            return simulado != null
                    && estadoSimuladoRespeitaLimitesDasQuantidades(simulado);
        }

        private void relayoutQuadradinhosDoAgrupamento(
                CirculoVenn agrupamento, int quantidade, String origemPadrao) {
            if (agrupamento == null || !agrupamento.exibirQuadradinhos) {
                return;
            }
            ArrayList<QuadradinhoVenn> existentes =
                    obterQuadradinhosDoAgrupamento(agrupamento);
            int inicioLayoutTemporario = quadradinhosVenn.size();
            adicionarQuadradinhosNoCirculo(
                    agrupamento,
                    Math.max(0, quantidade),
                    origemPadrao);

            ArrayList<QuadradinhoVenn> layout = new ArrayList<QuadradinhoVenn>();
            while (quadradinhosVenn.size() > inicioLayoutTemporario) {
                layout.add(quadradinhosVenn.remove(inicioLayoutTemporario));
            }

            quadradinhosVenn.removeAll(existentes);
            int quantidadePreservada = Math.min(existentes.size(), layout.size());
            for (int i = 0; i < quantidadePreservada; i++) {
                QuadradinhoVenn atual = existentes.get(i);
                QuadradinhoVenn posicao = layout.get(i);
                atual.x = posicao.x;
                atual.y = posicao.y;
                atual.tamanho = posicao.tamanho;
                atual.origem = origemPadrao;
                quadradinhosVenn.add(atual);
            }
            for (int i = quantidadePreservada; i < layout.size(); i++) {
                QuadradinhoVenn posicao = layout.get(i);
                quadradinhosVenn.add(new QuadradinhoVenn(
                        posicao.x, posicao.y, posicao.tamanho, origemPadrao));
            }
        }

        private void alterarValorAssinadoTransformacao(
                CirculoVenn agrupamento, int delta,
                String descricao, String resultadoDescricao) {
            if (!ehAgrupamentoTransformacaoComSinal(agrupamento) || delta == 0) {
                return;
            }
            int indiceAgrupamento = circulosVenn.indexOf(agrupamento);
            if (indiceAgrupamento < 0) {
                return;
            }
            int valorAnterior = obterValorAssinadoAtualDoAgrupamento(agrupamento);
            int novoValor = valorAnterior + delta;
            EstadoSemanticoCompartilhado.Snapshot simulado =
                    simularEstadoCompartilhadoAposAlteracaoValorAssinado(
                            indiceAgrupamento, novoValor);
            if (simulado == null
                    || !estadoSimuladoRespeitaLimitesDasQuantidades(simulado)) {
                return;
            }
            agrupamento.valorReferencia = novoValor;
            relayoutQuadradinhosDoAgrupamento(
                    agrupamento,
                    politicaSinalTransformacaoComplementar
                            .magnitudeParaUnidades(novoValor),
                    novoValor < 0 ? "transformacao_negativa" : "usuario");

            registrarAcaoGranular(
                    "QUANTIFICAR",
                    descricao,
                    "Diagrama complementar",
                    "Controle da transformação",
                    "Ajustar valor assinado da transformação",
                    "agrupamento=" + indiceAgrupamento
                            + "; valor_anterior=" + valorAnterior
                            + "; valor_novo=" + novoValor,
                    resultadoDescricao);

            sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(
                    indiceAgrupamento,
                    EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);
            repaint();
        }

        private void adicionarQuadradinhoAoAgrupamentoInterno(CirculoVenn agrupamento) {
            if (agrupamento == null || !agrupamento.exibirQuadradinhos) {
                return;
            }

            int indiceAgrupamento = circulosVenn.indexOf(agrupamento);
            if (indiceAgrupamento < 0) {
                return;
            }

            ArrayList<QuadradinhoVenn> existentes = obterQuadradinhosDoAgrupamento(agrupamento);
            Integer limiteCurado = obterLimiteSemanticoCuradoDoAgrupamento(indiceAgrupamento);
            limparLimiteQuantidadeQuestionado();
            String origemNovo = agrupamento.valorReferencia < 0
                    ? "transformacao_negativa" : "usuario";

            int inicioLayoutTemporario = quadradinhosVenn.size();
            adicionarQuadradinhosNoCirculo(
                    agrupamento,
                    existentes.size() + 1,
                    origemNovo);

            ArrayList<QuadradinhoVenn> layout = new ArrayList<QuadradinhoVenn>();
            while (quadradinhosVenn.size() > inicioLayoutTemporario) {
                layout.add(quadradinhosVenn.remove(inicioLayoutTemporario));
            }

            quadradinhosVenn.removeAll(existentes);
            for (int i = 0; i < existentes.size(); i++) {
                QuadradinhoVenn atual = existentes.get(i);
                QuadradinhoVenn posicao = layout.get(i);
                atual.x = posicao.x;
                atual.y = posicao.y;
                atual.tamanho = posicao.tamanho;
                quadradinhosVenn.add(atual);
            }

            QuadradinhoVenn posicaoNova = layout.get(layout.size() - 1);
            QuadradinhoVenn novo = new QuadradinhoVenn(
                    posicaoNova.x,
                    posicaoNova.y,
                    posicaoNova.tamanho,
                    origemNovo);
            quadradinhosVenn.add(novo);

            registrarAcaoGranular(
                    "QUANTIFICAR",
                    "Adicionar unidade ao agrupamento",
                    "Diagrama complementar",
                    "Controle de adição do agrupamento",
                    "Criar novo quadradinho",
                    "agrupamento=" + indiceAgrupamento
                            + "; quantidade=" + (existentes.size() + 1)
                            + "; limite_curado=" + (limiteCurado == null ? "indefinido" : limiteCurado),
                    "Uma unidade foi acrescentada ao agrupamento selecionado.");

            sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(
                    indiceAgrupamento,
                    EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);

            repaint();
        }

        private ArrayList<QuadradinhoVenn> obterQuadradinhosDoAgrupamento(
                CirculoVenn agrupamento) {
            return gerard.ui.venn.ConsultasDiagramaVenn
                    .obterQuadradinhosDoAgrupamento(quadradinhosVenn, agrupamento);
        }

        private void registrarLimiteQuantidadeAtingido(CirculoVenn agrupamento,
                java.util.List<QuadradinhoVenn> quadradinhos) {
            if (agrupamento == null) {
                return;
            }
            agrupamentoLimiteQuantidadeQuestionado = agrupamento;
            textoLimiteQuantidadeQuestionado = localizacao.texto(
                    ehAgrupamentoTransformacaoComSinal(agrupamento)
                            ? "ui.tooltip.venn.integerLimitReached"
                            : "ui.tooltip.venn.semanticLimitReached");
            mostrarLimiteQuantidadeQuestionado = true;
            mostrarAnotacaoMouseOver = false;
            scaffoldingFeedbackMultissensorialErro.sinalizarErro(
                    agrupamento, quadradinhos, new Runnable() {
                public void run() {
                    repaint();
                }
            });
            registrarAcaoGranular(
                    "FEEDBACK",
                    "Informar limite semântico da coleção",
                    "Diagrama complementar",
                    "Controle de adição do agrupamento",
                    "Impedir cardinalidade superior ao valor curado",
                    "mensagem=" + textoLimiteQuantidadeQuestionado,
                    "A quantidade visual foi mantida no valor semântico curado.");
            repaint();
        }

        private void limparLimiteQuantidadeQuestionado() {
            if (mostrarLimiteQuantidadeQuestionado
                    || agrupamentoLimiteQuantidadeQuestionado != null) {
                scaffoldingFeedbackMultissensorialErro.pararTremor();
            }
            mostrarLimiteQuantidadeQuestionado = false;
            textoLimiteQuantidadeQuestionado = "";
            agrupamentoLimiteQuantidadeQuestionado = null;
        }

        private Integer obterLimiteSemanticoCuradoDoAgrupamento(int indiceAgrupamento) {
            if (situacaoProblemaAtual == null || indiceAgrupamento < 0
                    || indiceAgrupamento >= 3) {
                return null;
            }

            String[] chaves = new String[3];
            Integer[] valores = new Integer[3];
            for (int indiceVisual = 0; indiceVisual < 3; indiceVisual++) {
                int indiceSemantico = obterIndiceSemanticoDoAgrupamento(
                        indiceVisual);
                int indiceReal = obterIndiceRealDoAgrupamento(indiceVisual);
                if (indiceSemantico < 0 || indiceReal < 0) {
                    continue;
                }
                chaves[indiceSemantico] = catalogoPapeisSemanticos
                        .obterChavePapelDoElemento(
                                tipoSituacaoSelecionada, indiceReal);
                valores[indiceSemantico] = obterValorCuradoPorChave(
                        chaves[indiceSemantico]);
            }
            int indiceSemanticoAlvo = obterIndiceSemanticoDoAgrupamento(
                    indiceAgrupamento);
            return scaffoldingLimiteQuantidadeVenn.resolverLimite(
                    chaves, valores, indiceSemanticoAlvo);
        }

        private MapeamentoPapeisRepresentacaoComplementar
                obterMapeamentoPapeisComplementaresAtual() {
            return fabricaMapeamentosPapeisComplementares.obter(
                    tipoSituacaoSelecionada);
        }

        private int obterIndiceSemanticoDoAgrupamento(int indiceAgrupamento) {
            return obterMapeamentoPapeisComplementaresAtual()
                    .paraIndiceSemantico(indiceAgrupamento);
        }

        private int obterIndiceRealDoAgrupamento(int indiceAgrupamento) {
            int indiceSemantico = obterIndiceSemanticoDoAgrupamento(
                    indiceAgrupamento);
            if (indiceSemantico < 0) {
                return -1;
            }
            if (indicesElementosEstadoCompartilhado != null
                    && indiceSemantico < indicesElementosEstadoCompartilhado.length) {
                return indicesElementosEstadoCompartilhado[indiceSemantico];
            }
            return indiceSemantico;
        }

        private Integer obterValorCuradoPorChave(String chave) {
            if (situacaoProblemaAtual == null) {
                return null;
            }

            return SemanticaCuradaSituacao.buscarValorInteiroVisivel(
                    situacaoProblemaAtual, localizacao, chave);
        }

        private void removerQuadradinhoDoAgrupamentoInterno(CirculoVenn agrupamento) {
            if (agrupamento == null || !agrupamento.exibirQuadradinhos) {
                return;
            }

            int indiceAgrupamento = circulosVenn.indexOf(agrupamento);
            if (indiceAgrupamento < 0) {
                return;
            }

            limparLimiteQuantidadeQuestionado();
            ArrayList<QuadradinhoVenn> existentes = obterQuadradinhosDoAgrupamento(agrupamento);
            if (existentes.isEmpty()) {
                agrupamentoRemoverQuadradinhoFocado = null;
                return;
            }

            // Remove a unidade visual mais recente no ordenamento da coleção.
            // Os textos dos quadradinhos remanescentes são preservados.
            QuadradinhoVenn removido = existentes.remove(existentes.size() - 1);
            quadradinhosVenn.remove(removido);

            if (!existentes.isEmpty()) {
                String origemLayout = agrupamento.valorReferencia < 0
                        ? "transformacao_negativa" : "usuario";
                int inicioLayoutTemporario = quadradinhosVenn.size();
                adicionarQuadradinhosNoCirculo(
                        agrupamento,
                        existentes.size(),
                        origemLayout);

                ArrayList<QuadradinhoVenn> layout = new ArrayList<QuadradinhoVenn>();
                while (quadradinhosVenn.size() > inicioLayoutTemporario) {
                    layout.add(quadradinhosVenn.remove(inicioLayoutTemporario));
                }

                quadradinhosVenn.removeAll(existentes);
                for (int i = 0; i < existentes.size(); i++) {
                    QuadradinhoVenn atual = existentes.get(i);
                    QuadradinhoVenn posicao = layout.get(i);
                    atual.x = posicao.x;
                    atual.y = posicao.y;
                    atual.tamanho = posicao.tamanho;
                    quadradinhosVenn.add(atual);
                }
            }

            registrarAcaoGranular(
                    "QUANTIFICAR",
                    "Remover unidade do agrupamento",
                    "Diagrama complementar",
                    "Controle de remoção do agrupamento",
                    "Apagar um quadradinho",
                    "agrupamento=" + indiceAgrupamento
                            + "; quantidade=" + existentes.size(),
                    "Uma unidade foi removida do agrupamento selecionado.");

            sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(
                    indiceAgrupamento,
                    EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);
            repaint();
        }

        private void desenharDescricaoDiagramaQuebrada(Graphics2D g2, String texto,
                                                        int x, int y, int larguraMaxima) {
            FontMetrics fm = g2.getFontMetrics();
            java.util.List<String> linhas = quebrarTextoAnotacao(texto, fm, larguraMaxima);
            int maximoLinhas = 2;
            int alturaLinha = Math.max(13, fm.getHeight());

            for (int i = 0; i < linhas.size() && i < maximoLinhas; i++) {
                String linha = linhas.get(i);
                if (i == maximoLinhas - 1 && linhas.size() > maximoLinhas) {
                    linha = ajustarLinhaComReticencias(linha, fm, larguraMaxima);
                }
                g2.drawString(linha, x, y + i * alturaLinha);
            }
        }

        private String ajustarLinhaComReticencias(String texto, FontMetrics fm, int larguraMaxima) {
            String sufixo = "...";
            String base = texto == null ? "" : texto.trim();
            while (base.length() > 0 && fm.stringWidth(base + sufixo) > larguraMaxima) {
                base = base.substring(0, base.length() - 1).trim();
            }
            return base + sufixo;
        }

        private boolean ehDiagramaVennComposicaoMedidas() {
            return CenaDiagramaVenn.naturezaPara(tipoSituacaoSelecionada)
                    == CenaDiagramaVenn.Natureza.COLECOES;
        }

        private boolean ehGraficoBarrasComparacao() {
            return CenaDiagramaVenn.naturezaPara(tipoSituacaoSelecionada)
                    == CenaDiagramaVenn.Natureza.BARRAS_COMPARACAO;
        }

        private void desenharCirculoVenn(Graphics2D g2, CirculoVenn circulo, boolean composicaoMedidas, boolean comparacaoMedidas) {
            if (comparacaoMedidas) {
                desenharBarraComparacao(g2, circulo);
                return;
            }
            if (!composicaoMedidas) {
                circulo.desenhar(g2);
                return;
            }

            Stroke original = g2.getStroke();
            g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
            g2.fillRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 16, 16);
            g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 16, 16);

            if (circulo.textoEditavel != null && circulo.textoEditavel.trim().length() > 0) {
                g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
                g2.setFont(new Font("Arial", Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                int tx = circulo.x + (circulo.largura - fm.stringWidth(circulo.textoEditavel)) / 2;
                int ty = circulo.y + (circulo.altura - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(circulo.textoEditavel, tx, ty);
            }

            g2.setStroke(original);
        }

        private void atualizarQuadradinhosCorrespondentesComparacao(boolean comparacaoMedidas) {
            quadradinhosCorrespondentesComparacao.clear();
            if (!comparacaoMedidas || circulosVenn.size() < 2) {
                return;
            }

            CirculoVenn referido = circulosVenn.get(0);
            CirculoVenn referendo = circulosVenn.get(1);
            ArrayList<QuadradinhoVenn> unidadesReferido = quadradinhosDaBarraOrdenadosDeBaixoParaCima(referido);
            ArrayList<QuadradinhoVenn> unidadesReferendo = quadradinhosDaBarraOrdenadosDeBaixoParaCima(referendo);
            int quantidadeComum = Math.min(unidadesReferido.size(), unidadesReferendo.size());

            for (int i = 0; i < quantidadeComum; i++) {
                quadradinhosCorrespondentesComparacao.add(unidadesReferido.get(i));
                quadradinhosCorrespondentesComparacao.add(unidadesReferendo.get(i));
            }
        }

        private ArrayList<QuadradinhoVenn> quadradinhosDaBarraOrdenadosDeBaixoParaCima(CirculoVenn barra) {
            return gerard.ui.venn.ConsultasDiagramaVenn
                    .quadradinhosDaBarraOrdenadosDeBaixoParaCima(quadradinhosVenn, barra);
        }

        private void desenharQuadradinhoVenn(Graphics2D g2, QuadradinhoVenn quadradinho, boolean composicaoMedidas, boolean comparacaoMedidas) {
            if (quadradinho == null) {
                return;
            }

            RenderizadorUnidadeVenn renderizador;
            if (comparacaoMedidas) {
                boolean quantidadeCorrespondente = quadradinhosCorrespondentesComparacao.contains(quadradinho);
                renderizador = FabricaRenderizadoresUnidadeVenn.paraComparacao(quantidadeCorrespondente);
            } else if (composicaoMedidas) {
                renderizador = FabricaRenderizadoresUnidadeVenn.paraComposicao();
            } else {
                renderizador = FabricaRenderizadoresUnidadeVenn.paraOrigem(quadradinho);
            }

            EstadoVisualUnidadeVenn estado = quadradinho == handlerQuadradinhoVenn.obterQuadradinhoAtivo()
                    ? EstadoVisualUnidadeVenn.ARRASTADA
                    : quadradinho == quadradinhoVennFocado
                            ? EstadoVisualUnidadeVenn.FOCADA
                            : EstadoVisualUnidadeVenn.NORMAL;
            renderizador.desenhar(g2, quadradinho, estado);
        }

        private void desenharBarraComparacao(Graphics2D g2, CirculoVenn circulo) {
            Stroke original = g2.getStroke();
            if (circulo.exibirQuadradinhos) {
                g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
                g2.fillRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 12, 12);
                g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 12, 12);
            } else {
                int valorAtual = obterValorRelativoAssinadoComparacao();
                g2.setColor(COR_TEXTO_SECUNDARIO);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fmRotulo = g2.getFontMetrics();
                int xRotulo = circulo.x + (circulo.largura - fmRotulo.stringWidth(circulo.rotulo)) / 2;
                g2.drawString(circulo.rotulo, xRotulo, circulo.y - 8);

                g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
                g2.fillRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 12, 12);
                g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 12, 12);

                String valor = servicoQuantidadeContextual
                        .formatarNumeroRelativoParaDiagrama(
                                valorAtual, situacaoProblemaAtual);
                g2.setColor(COR_TEXTO);
                g2.setFont(new Font("Arial", Font.BOLD, 26));
                FontMetrics fmValor = g2.getFontMetrics();
                int xValor = circulo.x + (circulo.largura - fmValor.stringWidth(valor)) / 2;
                int yValor = circulo.y + (circulo.altura - fmValor.getHeight()) / 2 + fmValor.getAscent();
                g2.drawString(valor, xValor, yValor);
            }
            g2.setStroke(original);
        }

        private void desenharResumoComparacaoMedidas(Graphics2D g2, Rectangle area) {
            if (circulosVenn.size() < 3) {
                return;
            }

            CirculoVenn referido = circulosVenn.get(0);
            CirculoVenn referendo = circulosVenn.get(1);

            int quantidadeReferido = contarQuadradinhosNoCirculo(referido);
            int quantidadeReferendo = contarQuadradinhosNoCirculo(referendo);

            g2.setColor(COR_TEXTO);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fmNumero = g2.getFontMetrics();
            EstadoSemanticoCompartilhado.Snapshot estado =
                    estadoSemanticoCompartilhado.snapshot();
            MapeamentoPapeisRepresentacaoComplementar mapeamento =
                    obterMapeamentoPapeisComplementaresAtual();
            String textoReferido = projetorValorPapelDiagramaComplementar.projetar(
                    estado, tipoSituacaoSelecionada,
                    mapeamento.paraIndiceSemantico(0), quantidadeReferido);
            String textoReferendo = projetorValorPapelDiagramaComplementar.projetar(
                    estado, tipoSituacaoSelecionada,
                    mapeamento.paraIndiceSemantico(1), quantidadeReferendo);
            g2.drawString(textoReferido,
                    referido.x + (referido.largura - fmNumero.stringWidth(textoReferido)) / 2,
                    referido.y - 10);
            g2.drawString(textoReferendo,
                    referendo.x + (referendo.largura - fmNumero.stringWidth(textoReferendo)) / 2,
                    referendo.y - 10);

            g2.setColor(COR_TEXTO);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fmRotulo = g2.getFontMetrics();
            int yRotulo = referido.y + referido.altura + 22;
            g2.drawString(referido.rotulo,
                    referido.x + (referido.largura - fmRotulo.stringWidth(referido.rotulo)) / 2,
                    yRotulo);
            g2.drawString(referendo.rotulo,
                    referendo.x + (referendo.largura - fmRotulo.stringWidth(referendo.rotulo)) / 2,
                    yRotulo);

            desenharPersonagemComparacao(g2, obterPersonagemComparacao(1), referido, yRotulo + 18);
            desenharPersonagemComparacao(g2, obterPersonagemComparacao(2), referendo, yRotulo + 18);

            desenharSegmentoValorRelativoComparacao(g2, referido, referendo, obterValorMaximoEscalaComparacao(), obterValorAtualControleComparacao());
        }

        private void desenharPersonagemComparacao(Graphics2D g2, String personagem, CirculoVenn barra, int yBase) {
            if (personagem == null || personagem.trim().length() == 0) {
                return;
            }
            String texto = personagem.trim();
            g2.setColor(COR_TEXTO_SECUNDARIO);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int x = barra.x + (barra.largura - fm.stringWidth(texto)) / 2;
            g2.drawString(texto, x, yBase);
        }

        private String obterPersonagemComparacao(int indice) {
            if (situacaoProblemaAtual == null) {
                return "";
            }
            String chave = indice == 1 ? "papel.referido" : (indice == 2 ? "papel.referendo" : "papel.diferenca");
            return SemanticaCuradaSituacao.buscarParticipante(
                    situacaoProblemaAtual, localizacao, chave);
        }

        private int obterValorAtualControleComparacao() {
            int maximo = obterValorMaximoEscalaComparacao();
            if (maximo <= 0) {
                if (circulosVenn.size() < 2) {
                    return 0;
                }
                return relacaoEstruturalComparacao().calcularModuloValorRelativo(
                        contarQuadradinhosNoCirculo(circulosVenn.get(0)),
                        contarQuadradinhosNoCirculo(circulosVenn.get(1)));
            }
            inicializarProporcaoControleComparacaoSeNecessario();
            return (int) Math.round(proporcaoControleComparacao * maximo);
        }

        private int obterValorMaximoEscalaComparacao() {
            int maximo = 0;
            if (situacaoProblemaAtual != null) {
                // Idem: se este papel for a incógnita, seu valor curado não
                // pode influenciar a escala visível (vazaria a resposta nos
                // números do eixo antes do aluno resolver).
                Integer valorCurado = SemanticaCuradaSituacao.buscarValorInteiroVisivel(
                        situacaoProblemaAtual, localizacao, "papel.diferenca");
                if (valorCurado != null) {
                    maximo = Math.max(maximo, relacaoEstruturalComparacao()
                            .calcularModuloDoValorRelativo(valorCurado.intValue()));
                }
            }
            ElementoVergnaud elementoValorRelativo = encontrarElementoVergnaudPorPapel(
                    "papel.diferenca");
            if (elementoValorRelativo != null) {
                Integer valorModelado = obterValorNumericoDoElemento(elementoValorRelativo);
                if (valorModelado != null) {
                    maximo = Math.max(maximo, relacaoEstruturalComparacao()
                            .calcularModuloDoValorRelativo(valorModelado.intValue()));
                }
            }
            if (circulosVenn.size() >= 3) {
                maximo = Math.max(maximo, relacaoEstruturalComparacao()
                        .calcularModuloDoValorRelativo(
                                circulosVenn.get(2).valorReferencia));
            }
            return maximo;
        }

        private int obterValorRelativoAssinadoComparacao() {
            ElementoVergnaud elementoValorRelativo = encontrarElementoVergnaudPorPapel(
                    "papel.diferenca");
            if (elementoValorRelativo != null) {
                Integer valorModelado = obterValorNumericoDoElemento(elementoValorRelativo);
                if (valorModelado != null) {
                    return valorModelado.intValue();
                }
            }
            if (circulosVenn.size() >= 3) {
                return circulosVenn.get(2).valorReferencia;
            }
            return obterValorAtualControleComparacao();
        }

        private int aplicarSinalAtualAoModuloComparacao(int modulo) {
            return relacaoEstruturalComparacao().aplicarSinalDoValorAtual(
                    obterValorRelativoAssinadoComparacao(), modulo);
        }

        private void inicializarProporcaoControleComparacaoSeNecessario() {
            if (proporcaoControleComparacao >= 0.0) {
                return;
            }
            int maximo = obterValorMaximoEscalaComparacao();
            if (maximo <= 0 || circulosVenn.size() < 2) {
                proporcaoControleComparacao = 0.0;
                return;
            }
            int atual = relacaoEstruturalComparacao().calcularModuloValorRelativo(
                    contarQuadradinhosNoCirculo(circulosVenn.get(0)),
                    contarQuadradinhosNoCirculo(circulosVenn.get(1)));
            proporcaoControleComparacao = Math.max(0.0, Math.min(1.0, atual / (double) maximo));
            ultimoValorInteiroControleComparacao = Math.max(0, Math.min(maximo, atual));
        }

        private RelacaoEstruturalComparacao relacaoEstruturalComparacao() {
            return RelacaoEstruturalComparacao.comparacaoDeMedidas();
        }

        private int obterBaseFixaEixoComparacao(CirculoVenn referido, CirculoVenn referendo) {
            return gerard.ui.venn.UtilitariosComparacaoBarras
                    .obterBaseFixaEixoComparacao(referido, referendo);
        }

        private int obterAlturaFixaEixoComparacao(CirculoVenn referido,
                                                   CirculoVenn referendo,
                                                   int valorMaximo) {
            return gerard.ui.venn.UtilitariosComparacaoBarras
                    .obterAlturaFixaEixoComparacao(referido, referendo, valorMaximo);
        }

        private void desenharSegmentoValorRelativoComparacao(Graphics2D g2,
                                                              CirculoVenn referido,
                                                              CirculoVenn referendo,
                                                              int valorRelativoEsperado,
                                                              int valorControle) {
            if (valorRelativoEsperado < 0) {
                valorRelativoEsperado = 0;
            }
            inicializarProporcaoControleComparacaoSeNecessario();
            int xEixo = obterXEixoComparacao(referendo);
            int yBase = obterBaseFixaEixoComparacao(referido, referendo);
            int alturaEixo = obterAlturaFixaEixoComparacao(referido, referendo, valorRelativoEsperado);
            int yTopo = yBase - alturaEixo;

            Stroke original = g2.getStroke();
            g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(xEixo, yTopo, xEixo, yBase);
            g2.drawLine(xEixo - 5, yTopo, xEixo + 5, yTopo);
            g2.drawLine(xEixo - 5, yBase, xEixo + 5, yBase);

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            FontMetrics fm = g2.getFontMetrics();
            for (int i = 0; i <= valorRelativoEsperado; i++) {
                int yTick = yBase - (int) Math.round((i / (double) Math.max(1, valorRelativoEsperado)) * alturaEixo);
                g2.drawLine(xEixo - 4, yTick, xEixo + 4, yTick);
                String t = String.valueOf(i);
                g2.drawString(t, xEixo - 8 - fm.stringWidth(t), yTick + fm.getAscent() / 2 - 1);
            }

            double proporcao = Math.max(0.0, Math.min(1.0, proporcaoControleComparacao));
            int yControle = yBase - (int) Math.round(proporcao * alturaEixo);
            desenharMarcadorControleComparacao(g2, xEixo, yControle);
            g2.setStroke(original);
        }


        private int obterXEixoComparacao(CirculoVenn referendo) {
            CirculoVenn valorRelativo = circulosVenn.size() >= 3 ? circulosVenn.get(2) : null;
            return gerard.ui.venn.UtilitariosComparacaoBarras
                    .obterXEixoComparacao(referendo, valorRelativo);
        }

        private void desenharMarcadorControleComparacao(Graphics2D g2, int x, int y) {
            g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
            g2.fillOval(x - 7, y - 7, 14, 14);
            g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
            g2.fillOval(x - 3, y - 3, 6, 6);
            g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawOval(x - 7, y - 7, 14, 14);
        }

        private void desenharPontoControleComparacaoEmPrimeiroPlano(Graphics2D g2) {
            Rectangle area = obterRetanguloPontoControleComparacao();
            if (area.width <= 0 || area.height <= 0) {
                return;
            }
            desenharMarcadorControleComparacao(g2,
                    area.x + area.width / 2,
                    area.y + area.height / 2);
        }

        private Rectangle obterRetanguloPontoControleComparacao() {
            if (!ehGraficoBarrasComparacao() || circulosVenn.size() < 2) {
                return new Rectangle(0, 0, 0, 0);
            }
            inicializarProporcaoControleComparacaoSeNecessario();
            CirculoVenn referido = circulosVenn.get(0);
            CirculoVenn referendo = circulosVenn.get(1);
            int valorMaximo = obterValorMaximoEscalaComparacao();
            int xEixo = obterXEixoComparacao(referendo);
            int yBase = obterBaseFixaEixoComparacao(referido, referendo);
            int alturaEixo = obterAlturaFixaEixoComparacao(referido, referendo, valorMaximo);
            int yControle = yBase - (int) Math.round(Math.max(0.0, Math.min(1.0, proporcaoControleComparacao)) * alturaEixo);
            return new Rectangle(xEixo - 12, yControle - 12, 24, 24);
        }

        private boolean contemPontoControleComparacao(int x, int y) {
            return obterRetanguloPontoControleComparacao().contains(x, y);
        }

        private boolean contemEscalaComparacao(int x, int y) {
            if (!ehGraficoBarrasComparacao() || circulosVenn.size() < 2) {
                return false;
            }
            CirculoVenn referido = circulosVenn.get(0);
            CirculoVenn referendo = circulosVenn.get(1);
            int valorMaximo = obterValorMaximoEscalaComparacao();
            int xEixo = obterXEixoComparacao(referendo);
            int yBase = obterBaseFixaEixoComparacao(referido, referendo);
            int yTopo = yBase - obterAlturaFixaEixoComparacao(referido, referendo, valorMaximo);
            Rectangle area = new Rectangle(xEixo - 16, yTopo - 8, 32, (yBase - yTopo) + 16);
            return area.contains(x, y);
        }

        private void aplicarControleComparacaoPeloMouse(int yMouse) {
            if (!ehGraficoBarrasComparacao() || circulosVenn.size() < 3) {
                return;
            }
            CirculoVenn referido = circulosVenn.get(0);
            CirculoVenn referendo = circulosVenn.get(1);
            int valorMaximo = obterValorMaximoEscalaComparacao();
            int yBase = obterBaseFixaEixoComparacao(referido, referendo);
            int alturaEixo = obterAlturaFixaEixoComparacao(referido, referendo, valorMaximo);
            int yTopo = yBase - alturaEixo;
            if (alturaEixo <= 0) {
                proporcaoControleComparacao = 0.0;
                atualizarBarrasComparacaoAPartirDoControle(0);
                return;
            }
            double proporcao = (yBase - yMouse) / (double) alturaEixo;
            if (proporcao < 0.0) {
                proporcao = 0.0;
            }
            if (proporcao > 1.0) {
                proporcao = 1.0;
            }
            proporcaoControleComparacao = proporcao;
            int valor = (int) Math.round(proporcao * valorMaximo);
            if (valor != ultimoValorInteiroControleComparacao) {
                atualizarBarrasComparacaoAPartirDoControle(aplicarSinalAtualAoModuloComparacao(valor));
                ultimoValorInteiroControleComparacao = valor;
            }
        }

        private void atualizarBarrasComparacaoAPartirDoControle(int valorControle) {
            if (circulosVenn.size() < 3) {
                return;
            }
            CirculoVenn referido = circulosVenn.get(0);
            CirculoVenn referendo = circulosVenn.get(1);
            int quantidadeReferido = contarQuadradinhosNoCirculo(referido);
            int quantidadeReferendo = contarQuadradinhosNoCirculo(referendo);
            RecalculoComparacaoMedidas.Resultado resultado =
                    RecalculoComparacaoMedidas.decidir(
                            "papel.referido".equals(obterPapelIncognitaAtual()),
                            Integer.valueOf(quantidadeReferido),
                            Integer.valueOf(quantidadeReferendo), valorControle);
            if (resultado.getPapel() == RecalculoComparacaoMedidas.PapelAlvo.REFERIDO) {
                normalizarQuantidadeQuadradinhosNaBarra(
                        referido, Math.max(0, resultado.getValor()));
            } else if (resultado.getPapel()
                    == RecalculoComparacaoMedidas.PapelAlvo.REFERENDO) {
                normalizarQuantidadeQuadradinhosNaBarra(
                        referendo, Math.max(0, resultado.getValor()));
            }

            ElementoVergnaud diferenca = encontrarElementoVergnaudPorPapel(
                    "papel.diferenca");
            if (diferenca != null) {
                definirValorNoElementoNumeroRelativo(diferenca, valorControle);
                // Durante o arrasto (chamado a cada movimento do mouse), só
                // checa em silêncio se a incógnita ainda diverge do curado —
                // sem diálogo, que só deve aparecer ao soltar (ver
                // gerard-scaffolding-interacao, "erro só ao soltar a peça").
                if (!incognitaAguardandoConfirmacaoDeValor(encontrarItemSobreElemento(diferenca))) {
                    sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                            diferenca, EstadoSemanticoCompartilhado.Origem.EIXO_VERTICAL);
                }
            }
        }

        private void sincronizarDiagramaVergnaudAPartirDoControleComparacao(int valorControle) {
            if (circulosVenn.size() < 2) {
                return;
            }

            CirculoVenn referido = circulosVenn.get(0);
            CirculoVenn referendo = circulosVenn.get(1);
            int quantidadeReferido = contarQuadradinhosNoCirculo(referido);
            int quantidadeReferendo = contarQuadradinhosNoCirculo(referendo);

            ElementoVergnaud elementoReferido = encontrarElementoVergnaudPorPapel(
                    "papel.referido");
            ElementoVergnaud elementoRelativo = encontrarElementoVergnaudPorPapel(
                    "papel.diferenca");
            ElementoVergnaud elementoReferendo = encontrarElementoVergnaudPorPapel(
                    "papel.referendo");
            if (elementoReferido == null || elementoRelativo == null
                    || elementoReferendo == null) {
                return;
            }
            definirValorNoElementoMedida(elementoReferido,
                    Integer.toString(Math.max(0, quantidadeReferido)));
            definirValorNoElementoNumeroRelativo(elementoRelativo,
                    valorControle);
            definirValorNoElementoMedida(elementoReferendo,
                    Integer.toString(Math.max(0, quantidadeReferendo)));
        }

        private void normalizarQuantidadeQuadradinhosNaBarra(CirculoVenn barra, int quantidadeDesejada) {
            java.util.Iterator<QuadradinhoVenn> it = quadradinhosVenn.iterator();
            while (it.hasNext()) {
                QuadradinhoVenn q = it.next();
                if (barra.contem(q.centroX(), q.centroY())) {
                    it.remove();
                }
            }
            adicionarQuadradinhosNoCirculo(barra, Math.max(0, quantidadeDesejada), "situacao_problema");
        }

        private int topoConteudoBarraComparacao(CirculoVenn barra) {
            return gerard.ui.venn.ConsultasDiagramaVenn
                    .topoConteudoBarraComparacao(quadradinhosVenn, barra);
        }

        private void desenharContagensComposicaoMedidasVenn(Graphics2D g2, Rectangle area) {
            if (circulosVenn.size() < 3) {
                return;
            }

            CirculoVenn parcela1 = circulosVenn.get(0);
            CirculoVenn parcela2 = circulosVenn.get(1);
            CirculoVenn resultado = circulosVenn.get(2);

            int quantidade1 = contarQuadradinhosNoCirculo(parcela1);
            int quantidade2 = contarQuadradinhosNoCirculo(parcela2);
            int quantidadeResultado = contarQuadradinhosNoCirculo(resultado);
            int totalComposicao = gerard.dominio.campoaditivo.RelacaoEstruturalComposicao
                    .composicaoDeMedidas().calcularTodo(
                            Math.max(0, quantidade1), Math.max(0, quantidade2));

            g2.setColor(COR_TEXTO_SECUNDARIO);
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            FontMetrics fm = g2.getFontMetrics();

            desenharNumeroComposicaoVenn(g2, String.valueOf(quantidade1), parcela1.x + parcela1.largura + 22, parcela1.y + parcela1.altura / 2 + fm.getAscent() / 2);
            desenharNumeroComposicaoVenn(g2, String.valueOf(quantidade2), parcela2.x + parcela2.largura + 22, parcela2.y + parcela2.altura / 2 + fm.getAscent() / 2);
            // O número junto à coleção resultado representa a quantidade real
            // de quadradinhos dentro dela. O ponto de ancoragem é ajustado
            // para permanecer confortavelmente dentro do card do diagrama.
            int ancoraResultado = Math.min(area.x + area.width - 26, resultado.x + resultado.largura + 18);
            desenharNumeroComposicaoVenn(g2, String.valueOf(quantidadeResultado), ancoraResultado, resultado.y + resultado.altura / 2 + fm.getAscent() / 2);

            String equacao = quantidade1 + " + " + quantidade2 + " = " + totalComposicao;
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fmEquacao = g2.getFontMetrics();
            int xEquacao = area.x + Math.max(18, (area.width - fmEquacao.stringWidth(equacao)) / 2);
            int yEquacao = area.y + area.height - 18;
            g2.drawString(equacao, xEquacao, yEquacao);
        }

        private void desenharNumeroComposicaoVenn(Graphics2D g2, String texto, int x, int y) {
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, x - fm.stringWidth(texto) / 2, y);
        }

        private void desenharSetaVenn(Graphics2D g2, int x1, int y1, int x2, int y2) {
            Stroke original = g2.getStroke();

            g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawLine(x1, y1, x2, y2);

            double angulo = Math.atan2(y2 - y1, x2 - x1);
            int tamanho = 10;

            int xA = (int) (x2 - tamanho * Math.cos(angulo - Math.PI / 6));
            int yA = (int) (y2 - tamanho * Math.sin(angulo - Math.PI / 6));

            int xB = (int) (x2 - tamanho * Math.cos(angulo + Math.PI / 6));
            int yB = (int) (y2 - tamanho * Math.sin(angulo + Math.PI / 6));

            g2.drawLine(x2, y2, xA, yA);
            g2.drawLine(x2, y2, xB, yB);

            g2.setStroke(original);
        }

        private void sincronizarVergnaudAPartirDosQuadradinhosVenn() {
            QuadradinhoVenn quadradinhoAtivo = handlerQuadradinhoVenn.obterQuadradinhoAtivo();
            int indiceAlterado = -1;
            if (quadradinhoAtivo != null) {
                for (int i = 0; i < circulosVenn.size(); i++) {
                    if (circulosVenn.get(i).contem(
                            quadradinhoAtivo.centroX(),
                            quadradinhoAtivo.centroY())) {
                        indiceAlterado = i;
                        break;
                    }
                }
            }
            sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(
                    indiceAlterado,
                    EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);
        }

        private int contarQuadradinhosNoCirculo(CirculoVenn circulo) {
            return gerard.ui.venn.ConsultasDiagramaVenn
                    .contarQuadradinhosNoCirculo(quadradinhosVenn, circulo);
        }

        private QuadradinhoVenn encontrarQuadradinhoVenn(int x, int y) {
            return gerard.ui.venn.ConsultasDiagramaVenn
                    .encontrarQuadradinhoVenn(quadradinhosVenn, x, y);
        }

        private CirculoVenn encontrarCirculoVenn(int x, int y) {
            return gerard.ui.venn.ConsultasDiagramaVenn
                    .encontrarCirculoVenn(circulosVenn, x, y);
        }

        private ItemTextoArrastavel encontrarItemArrastavel(int x, int y) {
            for (int i = itensArrastaveis.size() - 1; i >= 0; i--) {
                ItemTextoArrastavel item = itensArrastaveis.get(i);

                if (item.contem(x, y)) {
                    return item;
                }
            }

            return null;
        }

        private ElementoTextoMovel encontrarElementoTextoMovel(int x, int y) {
            if (textoProblemaEhMensagemSistema) {
                return null;
            }
            for (int i = elementosTexto.size() - 1; i >= 0; i--) {
                ElementoTextoMovel elemento = elementosTexto.get(i);

                if (elemento.contem(x, y)) {
                    return elemento;
                }
            }

            return null;
        }

        private boolean estaNaAreaDoTexto(int x, int y) {
            return geometriaAreaEnunciado.contem(x, y, getWidth());
        }

        private boolean ehNumeroDoTexto(ElementoTextoMovel elemento) {
            return elementoContemNumeralInterpretado(elemento);
        }

        private boolean ehInterrogacaoDoTexto(ElementoTextoMovel elemento) {
            return !textoProblemaEhMensagemSistema
                    && elemento != null
                    && elemento.possuiVinculoSemantico()
                    && elemento.representaIncognitaOriginal();
        }

        private boolean ehNumeroOuInterrogacaoDoTexto(ElementoTextoMovel elemento) {
            return !textoProblemaEhMensagemSistema
                    && elemento != null
                    && elemento.possuiVinculoSemantico();
        }

        private boolean podeEnviarParaDiagrama(ElementoTextoMovel elemento) {
            return ehNumeroOuInterrogacaoDoTexto(elemento);
        }

        private void converterElementoTextoEmItemDiagrama(ElementoTextoMovel elemento, int mouseX, int mouseY) {
            String valor = extrairValorArrastavel(elemento);

            if (valor.length() == 0 || existeItemPosicionadoNoDiagrama(
                    elemento.getValorSemanticoOriginal(),
                    obterChavePapelExataDoElemento(elemento))) {
                textoAnotacaoMouseOver = localizacao.texto("ui.tooltip.alreadyPositioned");
                mostrarAnotacaoMouseOver = true;
                mouseOverX = mouseX;
                mouseOverY = mouseY;
                handlerElementoTextoMovel.cancelar();
                return;
            }

            ItemTextoArrastavel novo = new ItemTextoArrastavel(
                    mouseX - elemento.largura / 2,
                    mouseY - elemento.altura / 2,
                    elemento.largura + 6,
                    elemento.altura,
                    valor,
                    elemento.representaIncognitaOriginal(),
                    elemento.getValorSemanticoOriginal(),
                    obterChavePapelExataDoElemento(elemento)
            );

            itensArrastaveis.add(novo);
            handlerElementoTextoMovel.cancelar();
            handlerItemTextoArrastavel.iniciar(novo, mouseX, mouseY);
            itemFocado = novo;
            atualizarRealceAlvoProximidade(novo);
        }

        private String extrairValorArrastavel(ElementoTextoMovel elemento) {
            if (elemento == null || elemento.valor == null) {
                return "";
            }

            if (elemento.possuiVinculoSemantico()) {
                return elemento.getValorSemanticoAtual();
            }

            java.util.regex.Pattern padrao = java.util.regex.Pattern.compile(
                    "[0-9]+|" + SimboloDesconhecido.regexClasse());
            java.util.regex.Matcher matcher = padrao.matcher(elemento.valor);

            if (matcher.find()) {
                return matcher.group();
            }

            return "";
        }

        private MarcadorTexto encontrarMarcadorFixoTexto(int x, int y) {
            Font fonte = new Font("Arial", Font.BOLD, 20);
            return resolvedorPickupElementoMatematicoTexto.encontrar(
                    x, y, marcadoresFixosTexto, elementosTexto, getFontMetrics(fonte));
        }


        private boolean existeItemPosicionadoNoDiagrama(
                String origemValor, String chavePapel) {
            for (int i = 0; i < itensArrastaveis.size(); i++) {
                ItemTextoArrastavel item = itensArrastaveis.get(i);
                if (!item.estaNoDiagrama()) {
                    continue;
                }
                if (chavePapel != null && chavePapel.trim().length() > 0
                        && chavePapel.equals(item.chavePapel)) {
                    return true;
                }
                if (origemValor != null && origemValor.equals(item.origemValor)) {
                    return true;
                }
            }

            return false;
        }

        private void definirCursorMaoAberta() {
            setCursor(fornecedorCursoresPickup.obterMaoAberta());
        }

        private void definirCursorMaoFechada() {
            setCursor(fornecedorCursoresPickup.obterMaoFechada());
        }

        private boolean existePickupAtivo() {
            return handlerItemTextoArrastavel.estaAtivo()
                    || handlerElementoTextoMovel.estaAtivo()
                    || handlerQuadradinhoVenn.estaAtivo()
                    || handlerConectorVergnaud.estaAtivo()
                    || arrastandoControleComparacao
                    || handlerPaineisEixosRelacoes.estaAtivo();
        }

        private boolean pontoSobreElementoArrastavel(int x, int y) {
            boolean representacoesLiberadas = interacaoRepresentacoesLiberadaPelaModelagem();
            if (representacoesLiberadas
                    && paineisEixosRelacoes.contemPontoControle(x, y)) {
                return true;
            }
            if (representacoesLiberadas && ehGraficoBarrasComparacao()
                    && (contemPontoControleComparacao(x, y)
                    || contemEscalaComparacao(x, y))) {
                return true;
            }
            if (encontrarElementoTextoMovel(x, y) != null && estaNaAreaDoTexto(x, y)) {
                return true;
            }
            if ((representacoesLiberadas && encontrarQuadradinhoVenn(x, y) != null)
                    || encontrarItemArrastavel(x, y) != null
                    || encontrarConectorVergnaud(x, y) != null
                    || encontrarMarcadorFixoTexto(x, y) != null) {
                return true;
            }
            return false;
        }

        private void atualizarCursorDepoisDoPickup(int x, int y) {
            if (pontoSobreElementoArrastavel(x, y)) {
                definirCursorMaoAberta();
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }

        private void iniciarArrasteElastico(int x, int y) {
            controladorArrasteElastico.iniciar(x, y, new OuvinteArrasteElastico() {
                @Override
                public void aoAtualizarPosicao(int posicaoX, int posicaoY) {
                    processarMovimentoArraste(posicaoX, posicaoY);
                }
            });
        }

        private void cancelarEfeitosArraste() {
            controladorArrasteElastico.cancelar();
            handlerConectorVergnaud.finalizarLimiar();
            handlerPaineisEixosRelacoes.cancelar();
            marcadorOrigemArraste.limpar();
            scaffoldingFeedbackProxyPosicionamento.cancelar();
            sessaoArrasteTextoParaDiagrama.limpar();
        }

        private void iniciarFantasmaRetangular(final Rectangle area,
                                                final int arco,
                                                final boolean elipse) {
            if (area == null || area.width <= 0 || area.height <= 0) {
                marcadorOrigemArraste.limpar();
                return;
            }
            final Rectangle origem = new Rectangle(area);
            marcadorOrigemArraste.iniciar(new DesenhavelFantasmaOrigem() {
                @Override
                public Rectangle obterLimitesOrigem() {
                    return new Rectangle(origem);
                }

                @Override
                public void desenharContorno(Graphics2D grafico) {
                    if (elipse) {
                        grafico.drawOval(origem.x, origem.y,
                                origem.width, origem.height);
                    } else if (arco > 0) {
                        grafico.drawRoundRect(origem.x, origem.y,
                                origem.width, origem.height, arco, arco);
                    } else {
                        grafico.drawRect(origem.x, origem.y,
                                origem.width, origem.height);
                    }
                }
            });
        }

        private void iniciarFantasmaElementoTexto(ElementoTextoMovel elemento) {
            if (elemento == null) {
                marcadorOrigemArraste.limpar();
                return;
            }
            Font fonte = new Font("Arial", Font.BOLD, 20);
            FontMetrics fm = getFontMetrics(fonte);
            iniciarFantasmaRetangular(new Rectangle(
                    elemento.x - 4,
                    elemento.y - fm.getAscent() + 1,
                    Math.max(1, elemento.largura + 8),
                    Math.max(1, elemento.altura + 4)), 8, false);
        }

        private void iniciarFantasmaItem(ItemTextoArrastavel item) {
            if (item == null) {
                marcadorOrigemArraste.limpar();
                return;
            }
            iniciarFantasmaRetangular(new Rectangle(
                    item.x - 4, item.y - 4,
                    Math.max(1, item.largura + 8),
                    Math.max(1, item.altura + 8)), 8, false);
        }

        private void iniciarFantasmaQuadradinho(QuadradinhoVenn quadradinho) {
            if (quadradinho == null) {
                marcadorOrigemArraste.limpar();
                return;
            }
            iniciarFantasmaRetangular(new Rectangle(
                    quadradinho.x, quadradinho.y,
                    Math.max(1, quadradinho.tamanho),
                    Math.max(1, quadradinho.tamanho)), 4, false);
        }

        private void iniciarFantasmaElementoVergnaud(ElementoVergnaud elemento) {
            if (elemento == null) {
                marcadorOrigemArraste.limpar();
                return;
            }
            boolean elipse = elemento.tipo == TipoFiguraDiagrama.ELIPSE;
            int arco = elemento.tipo == TipoFiguraDiagrama.RETANGULO_ARREDONDADO
                    ? 20 : 0;
            iniciarFantasmaRetangular(new Rectangle(
                    elemento.x, elemento.y,
                    Math.max(1, elemento.largura),
                    Math.max(1, elemento.altura)), arco, elipse);
        }

        private void iniciarFantasmaConector(final ConectorVergnaud conector) {
            if (conector == null) {
                marcadorOrigemArraste.limpar();
                return;
            }
            final int x1 = conector.x1;
            final int y1 = conector.y1;
            final int x2 = conector.x2;
            final int y2 = conector.y2;
            final TipoConectorDiagrama tipo = conector.tipo;
            final Rectangle limites = obterLimitesVisuaisConector(conector);
            marcadorOrigemArraste.iniciar(new DesenhavelFantasmaOrigem() {
                @Override
                public Rectangle obterLimitesOrigem() {
                    return new Rectangle(limites);
                }

                @Override
                public void desenharContorno(Graphics2D grafico) {
                    if (tipo == TipoConectorDiagrama.SETA_CURVA) {
                        int controleX = (x1 + x2) / 2;
                        int controleY = Math.max(y1, y2) + 96;
                        grafico.draw(new QuadCurve2D.Double(
                                x1, y1, controleX, controleY, x2, y2));
                    } else {
                        grafico.drawLine(x1, y1, x2, y2);
                    }
                }
            });
        }

        public void mousePressed(MouseEvent e) {
            requestFocusInWindow();

            int x = e.getX();
            int y = e.getY();

            cancelarEfeitosArraste();
            handlerItemTextoArrastavel.cancelar();
            handlerElementoTextoMovel.cancelar();
            handlerConectorVergnaud.cancelar();
            limparRealceAlvoProximidade();
            mostrarAnotacaoMouseOver = false;
            flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao();
            arrastandoControleComparacao = false;

            RepresentacaoComUnidadesRemoviveis representacaoRemover =
                    encontrarRepresentacaoPeloControleRemoverQuadradinho(x, y);
            if (representacaoRemover != null) {
                if (!adicaoDeUnidadesLiberadaPelaModelagem()) {
                    agrupamentoRemoverQuadradinhoFocado = null;
                    mostrarAnotacaoMouseOver = true;
                    textoAnotacaoMouseOver = obterMensagemBloqueioAdicaoUnidades();
                    mouseOverX = x;
                    mouseOverY = y;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                    return;
                }
                boolean remocaoLiberada = ehAgrupamentoTransformacaoComSinal(
                        representacaoRemover.obterAgrupamento())
                        ? podeDecrementarValorAssinadoTransformacao(
                                representacaoRemover.obterAgrupamento())
                        : representacaoRemover.podeRemoverUnidade();
                if (!remocaoLiberada) {
                    agrupamentoRemoverQuadradinhoFocado = null;
                    mostrarAnotacaoMouseOver = true;
                    textoAnotacaoMouseOver = localizacao.texto(
                            ehAgrupamentoTransformacaoComSinal(
                                    representacaoRemover.obterAgrupamento())
                                    ? "ui.tooltip.venn.integerLimitReached"
                                    : "ui.tooltip.venn.minimumReached");
                    mouseOverX = x;
                    mouseOverY = y;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                    return;
                }
                if (ehAgrupamentoTransformacaoComSinal(
                        representacaoRemover.obterAgrupamento())) {
                    alterarValorAssinadoTransformacao(
                            representacaoRemover.obterAgrupamento(),
                            -1,
                            "Decrementar transformação no tabuleiro",
                            "A transformação foi reduzida em uma unidade com consistência entre as representações.");
                } else {
                    representacaoRemover.removerUnidade();
                }
                agrupamentoRemoverQuadradinhoFocado =
                        (ehAgrupamentoTransformacaoComSinal(
                                representacaoRemover.obterAgrupamento())
                                ? podeDecrementarValorAssinadoTransformacao(
                                        representacaoRemover.obterAgrupamento())
                                : representacaoRemover.podeRemoverUnidade())
                                ? representacaoRemover.obterAgrupamento() : null;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                verificarConclusaoModelagem();
                return;
            }

            RepresentacaoComUnidadesAdicionaveis representacaoAdicionar =
                    encontrarRepresentacaoPeloControleAdicionarQuadradinho(x, y);
            if (representacaoAdicionar != null) {
                if (!adicaoDeUnidadesLiberadaPelaModelagem()) {
                    agrupamentoAdicionarQuadradinhoFocado = null;
                    mostrarAnotacaoMouseOver = true;
                    textoAnotacaoMouseOver = obterMensagemBloqueioAdicaoUnidades();
                    mouseOverX = x;
                    mouseOverY = y;
                    setCursor(Cursor.getDefaultCursor());
                    registrarAcaoGranular(
                            "SELECIONAR",
                            "Tentar adicionar unidade antes da modelagem",
                            "Diagrama complementar",
                            "Controle de adição do agrupamento",
                            "Aguardar posicionamento no diagrama de Vergnaud",
                            "bloqueio=modelagem_vergnaud_incompleta",
                            "Nenhuma unidade foi criada.");
                    repaint();
                    return;
                }
                boolean adicaoLiberada = ehAgrupamentoTransformacaoComSinal(
                        representacaoAdicionar.obterAgrupamento())
                        ? podeIncrementarValorAssinadoTransformacao(
                                representacaoAdicionar.obterAgrupamento())
                        : representacaoAdicionar.podeAdicionarUnidade();
                if (!adicaoLiberada) {
                    agrupamentoAdicionarQuadradinhoFocado = null;
                    registrarLimiteQuantidadeAtingido(
                            representacaoAdicionar.obterAgrupamento(),
                            obterQuadradinhosDoAgrupamento(
                                    representacaoAdicionar.obterAgrupamento()));
                    setCursor(Cursor.getDefaultCursor());
                    return;
                }
                if (ehAgrupamentoTransformacaoComSinal(
                        representacaoAdicionar.obterAgrupamento())) {
                    alterarValorAssinadoTransformacao(
                            representacaoAdicionar.obterAgrupamento(),
                            1,
                            "Incrementar transformação no tabuleiro",
                            "A transformação foi aumentada em uma unidade com consistência entre as representações.");
                } else {
                    ResultadoOperacaoUnidade resultado = representacaoAdicionar.adicionarUnidade();
                    if (resultado.isLimiteAtingido()) {
                        registrarLimiteQuantidadeAtingido(
                                representacaoAdicionar.obterAgrupamento(),
                                obterQuadradinhosDoAgrupamento(
                                        representacaoAdicionar.obterAgrupamento()));
                    }
                }
                agrupamentoAdicionarQuadradinhoFocado =
                        representacaoAdicionar.obterAgrupamento();
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                verificarConclusaoModelagem();
                return;
            }

            if (ehGraficoBarrasComparacao()
                    && (contemPontoControleComparacao(x, y) || contemEscalaComparacao(x, y))
                    && !interacaoRepresentacoesLiberadaPelaModelagem()) {
                informarBloqueioInteracaoRepresentacao(x, y, "Controle do gráfico de barras");
                return;
            }

            if (ehGraficoBarrasComparacao() && (contemPontoControleComparacao(x, y) || contemEscalaComparacao(x, y))) {
                Rectangle origemControleComparacao = obterRetanguloPontoControleComparacao();
                iniciarFantasmaRetangular(origemControleComparacao, 18, true);
                arrastandoControleComparacao = true;
                definirCursorMaoFechada();
                aplicarControleComparacaoPeloMouse(y);
                iniciarArrasteElastico(x, y);
                repaint();
                return;
            }

            PaineisEixosRelacoes.Painel painelRecemRevelado =
                    paineisEixosRelacoes.processarPressionamentoLupa(x, y);
            if (painelRecemRevelado != null) {
                prepararPainelEixoRelacao(painelRecemRevelado, getWidth(), getHeight());
                registrarLogUsuario(
                        "Revelar o eixo de um papel de Relações pela lupa",
                        "-",
                        "Lupa perto do elemento",
                        "Papel do diagrama de Relações",
                        "Ampliar a visão do número relativo e do sinal daquele papel, sob demanda",
                        "OBJ4",
                        "O eixo de cada papel fica escondido até a lupa ser clicada, para reduzir poluição visual.",
                        "LUPA_EIXO_RELACAO",
                        ""
                );
                itemFocado = null;
                quadradinhoVennFocado = null;
                repaint();
                return;
            }

            if (seletorOperacaoRelacaoAluno.processarPressionamento(x, y)) {
                boolean correta = seletorOperacaoRelacaoAluno.respondeuCorretamente();
                if (!correta) {
                    scaffoldingFeedbackMultissensorialErro.emitirApenasSom();
                }
                registrarLogUsuario(
                        "Escolher a operação (soma/subtração) da situação de Relações",
                        "-",
                        "Seletor de operação perto da seta do diagrama",
                        "Operação entre os papéis de Relações",
                        seletorOperacaoRelacaoAluno.obterEscolhaAluno().name(),
                        "OBJ4",
                        correta
                                ? "O aluno escolheu a operação (soma/subtração) que combina os dois papéis curados."
                                : "O aluno escolheu uma operação diferente da curada — explicação exibida perto do seletor.",
                        "OPERACAO_RELACAO_ALUNO",
                        correta ? "CORRETO" : "INCORRETO"
                );
                itemFocado = null;
                quadradinhoVennFocado = null;
                // Reavalia a conclusão: "só deixe azulzinho depois que for
                // escolhida as operações corretamente" — a escolha da
                // operação pode ser o último requisito pendente (papéis já
                // posicionados antes). verificarConclusaoModelagem() já
                // chama repaint() ao final.
                verificarConclusaoModelagem();
                return;
            }

            // Mesma ordem pedagógica do desenho (ver comentário em
            // paintComponent): o clique no segundo seletor só é processado
            // depois do primeiro estar correto — evita registrar clique numa
            // área que, àquela altura, nem está sendo desenhada.
            if (seletorOperacaoRelacaoAluno.respondeuCorretamente()
                    && seletorOperacaoEstadoTransformacaoAluno.processarPressionamento(x, y)) {
                boolean correta = seletorOperacaoEstadoTransformacaoAluno.respondeuCorretamente();
                if (!correta) {
                    scaffoldingFeedbackMultissensorialErro.emitirApenasSom();
                }
                registrarLogUsuario(
                        "Escolher a operação (soma/subtração) entre estado inicial e transformação",
                        "-",
                        "Seletor de operação à esquerda do círculo inferior do diagrama",
                        "Operação entre estado inicial e transformação resultante",
                        seletorOperacaoEstadoTransformacaoAluno.obterEscolhaAluno().name(),
                        "OBJ4",
                        correta
                                ? "O aluno escolheu a operação (soma/subtração) que combina estado inicial e transformação resultante."
                                : "O aluno escolheu uma operação diferente da curada — explicação exibida perto do seletor.",
                        "OPERACAO_ESTADO_TRANSFORMACAO_ALUNO",
                        correta ? "CORRETO" : "INCORRETO"
                );
                itemFocado = null;
                quadradinhoVennFocado = null;
                // Ver comentário equivalente no bloco de
                // seletorOperacaoRelacaoAluno acima.
                verificarConclusaoModelagem();
                return;
            }

            HandlerInteracaoPaineisEixosRelacoes.ResultadoPressionamento
                    resultadoPaineisRelacoes =
                    handlerPaineisEixosRelacoes.iniciar(
                            adaptadorInteracaoPaineisEixosRelacoes,
                            x, y,
                            interacaoRepresentacoesLiberadaPelaModelagem());
            if (resultadoPaineisRelacoes.foiBloqueado()) {
                informarBloqueioInteracaoRepresentacao(
                        x, y, "Valor semântico no painel de eixo das Relações");
                return;
            }
            if (resultadoPaineisRelacoes.foiConsumido()) {
                if (resultadoPaineisRelacoes.getModoManipulacao()
                        == AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.PONTO_CONTROLE) {
                    iniciarFantasmaRetangular(
                            adaptadorInteracaoPaineisEixosRelacoes
                                    .obterAreaVisualPontoControle(),
                            14, true);
                    iniciarArrasteElastico(x, y);
                    definirCursorMaoFechada();
                } else if (resultadoPaineisRelacoes.getModoManipulacao()
                        == AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.PAINEL) {
                    iniciarFantasmaRetangular(
                            adaptadorInteracaoPaineisEixosRelacoes
                                    .obterAreaVisualPainel(),
                            16, false);
                    iniciarArrasteElastico(x, y);
                    definirCursorMaoFechada();
                }
                sincronizarPainelEixoRelacaoSeNecessario(false);
                itemFocado = null;
                quadradinhoVennFocado = null;
                repaint();
                return;
            }

            MarcadorTexto marcador = encontrarMarcadorFixoTexto(x, y);

            if (marcador != null) {
                if (politicaUnicidadeElementoMatematicoTexto.jaEstaNoDiagrama(
                        marcador, itensArrastaveis)) {
                    textoAnotacaoMouseOver = localizacao.texto("ui.tooltip.alreadyPositioned");
                    mostrarAnotacaoMouseOver = true;
                    mouseOverX = x;
                    mouseOverY = y;
                    repaint();
                    return;
                }

                ItemTextoArrastavel novo = sessaoArrasteTextoParaDiagrama.iniciarPorMarcador(marcador);
                if (novo == null) {
                    repaint();
                    return;
                }

                registrarLogUsuario(
                        "Selecionar valor do enunciado",
                        "-",
                        "Texto do problema",
                        "Marcador textual",
                        "Identificar dado numérico ou incógnita no enunciado",
                        "OBJ8",
                        "A seleção organiza os dados do problema antes do posicionamento no modelo.",
                        "SELECAO_TEXTO",
                        "valor=" + marcador.valor + "; papel=" + (marcador.chavePapel != null ? marcador.chavePapel : "")
                );
                // Selecionar não tem certo/errado porque ainda não há
                // papel-alvo. O fato neutro é encaminhado ao Modelador.
                conectorVereditoModelador.registrarAcaoNeutra(
                        loggerInteracaoGerard.getUsuarioAtual(), tipoSituacaoSelecionada, "SELECIONAR");
                handlerItemTextoArrastavel.iniciar(novo, x, y);
                itemFocado = novo;
                registrarAcaoGranular("SELECIONAR", "Selecionar elemento semântico do enunciado",
                        "Texto do problema", "Marcador textual",
                        "Escolher o elemento para conduzi-lo ao diagrama",
                        "valor=" + marcador.valor + "; papel="
                                + (marcador.chavePapel != null ? marcador.chavePapel : ""),
                        "Elemento semântico selecionado para manipulação.");

                atualizarRealceAlvoProximidade(novo);
                iniciarFantasmaItem(novo);
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();

                repaint();
                return;
            }


            ElementoTextoMovel elementoTexto = encontrarElementoTextoMovel(x, y);

            if (handlerElementoTextoMovel.iniciar(
                    estaNaAreaDoTexto(x, y) ? elementoTexto : null, x, y)) {
                elementoTextoFocado = elementoTexto;
                itemFocado = null;
                quadradinhoVennFocado = null;
                iniciarRastreamentoGranular(x, y, elementoTexto.valor, "Texto do enunciado", true);
                registrarAcaoGranular("SELECIONAR", "Selecionar elemento textual", "Texto do problema", "Elemento textual", "Escolher texto para manipulação", "texto=" + elementoTexto.valor, "Seleção visual do texto.");
                iniciarFantasmaElementoTexto(
                        handlerElementoTextoMovel.obterElementoAtivo());
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();
                repaint();
                return;
            }

            QuadradinhoVenn quadradinhoVennCandidato = encontrarQuadradinhoVenn(x, y);
            handlerQuadradinhoVenn.cancelar();

            if (quadradinhoVennCandidato != null
                    && !interacaoRepresentacoesLiberadaPelaModelagem()) {
                informarBloqueioInteracaoRepresentacao(x, y, "Unidade da representação complementar");
                return;
            }

            if (quadradinhoVennCandidato != null) {
                handlerQuadradinhoVenn.iniciar(quadradinhoVennCandidato, x, y, circulosVenn);
                quadradinhoVennFocado = quadradinhoVennCandidato;
                itemFocado = null;
                iniciarRastreamentoGranular(x, y, "Elemento do diagrama", "Quadrado do diagrama", false);
                registrarAcaoGranular("SELECIONAR", "Selecionar elemento do diagrama", "Diagrama", "Quadrado", "Escolher objeto para manipulação", "", "Elemento selecionado.");
                iniciarFantasmaQuadradinho(quadradinhoVennCandidato);
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();
                repaint();
                return;
            }

            ItemTextoArrastavel itemEncontrado = encontrarItemArrastavel(x, y);

            if (handlerItemTextoArrastavel.iniciar(itemEncontrado, x, y)) {
                ItemTextoArrastavel itemSelecionado =
                        handlerItemTextoArrastavel.obterItemAtivo();
                scaffoldingFeedbackMultissensorialErro.pararTremor();
                itemFocado = itemSelecionado;
                registrarAcaoGranular("SELECIONAR", "Selecionar item arrastável", "Área de trabalho", "Item arrastável", "Escolher valor para posicionamento", "valor=" + itemSelecionado.valor, "Item selecionado.");
                iniciarFantasmaItem(itemSelecionado);
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();
                repaint();
                return;
            }

            // Elementos semânticos (círculos/retângulos do diagrama de
            // Vergnaud) deixaram de ser arrastáveis (2026-08-18): "isso foi
            // um requisito muito antigo que, agora, não faz mais sentido" —
            // decisão da usuária, aplicada uniformemente a todas as
            // categorias, já que este mousePressed é compartilhado por
            // todas elas. A edição de texto por duplo clique continua
            // funcionando (mouseClicked → editarTextoElementoVergnaud), que
            // sempre foi um caminho independente deste aqui. Conectores
            // (setas) continuam arrastáveis normalmente, abaixo — não foram
            // mencionados no pedido.
            ConectorVergnaud conectorVergnaudCandidato = encontrarConectorVergnaud(x, y);

            if (conectorVergnaudCandidato != null) {
                if (politicaGestoEstrutural.ehPressionamentoDeDuploClique(e.getClickCount())) {
                    return;
                }
                handlerConectorVergnaud.iniciar(new AdaptadorMovimentoConectorVergnaud(conectorVergnaudCandidato), x, y);
                itemFocado = null;
                quadradinhoVennFocado = null;
                iniciarRastreamentoGranular(x, y, "Conector de Vergnaud", "Conector do diagrama", false);
                registrarAcaoGranular("SELECIONAR", "Selecionar conector", "Diagrama", "Conector", "Escolher conector para reposicionamento", "", "Conector selecionado.");
                iniciarFantasmaConector(conectorVergnaudCandidato);
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();
                repaint();
                return;
            }

            itemFocado = null;
            quadradinhoVennFocado = null;
            repaint();
        }

        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if (handlerItemTextoArrastavel.estaAtivo()
                    || handlerConectorVergnaud.estaAtivo()) {
                suspenderConclusaoDuranteManipulacao();
            }
            atualizarRastreamentoGranular(x, y);
            mostrarAnotacaoMouseOver = false;
            if (existePickupAtivo()) {
                definirCursorMaoFechada();
            }

            if (controladorArrasteElastico.estaAtivo()) {
                controladorArrasteElastico.atualizarAlvo(x, y);
            } else {
                processarMovimentoArraste(x, y);
            }
        }

        private void processarMovimentoArraste(int x, int y) {
            if (handlerPaineisEixosRelacoes.mover(x, y)) {
                sincronizarPainelEixoRelacaoSeNecessario(false);
                repaint();
                return;
            }

            if (arrastandoControleComparacao) {
                aplicarControleComparacaoPeloMouse(y);
                repaint();
                return;
            }

            if (handlerQuadradinhoVenn.estaAtivo()) {
                handlerQuadradinhoVenn.mover(x, y);
                repaint();
                return;
            }

            ElementoTextoMovel elementoTextoAtivo =
                    handlerElementoTextoMovel.obterElementoAtivo();
            if (elementoTextoAtivo != null) {
                if (podeEnviarParaDiagrama(elementoTextoAtivo)
                        && !estaNaAreaDoTexto(x, y)) {
                    converterElementoTextoEmItemDiagrama(
                            elementoTextoAtivo, x, y);
                    repaint();
                    return;
                }

                if (ehNumeroOuInterrogacaoDoTexto(elementoTextoAtivo)) {
                    handlerElementoTextoMovel.moverLivrePara(x, y);
                } else {
                    LimitesMovimento limites = geometriaAreaEnunciado
                            .obterLimitesMovimento(
                                    elementoTextoAtivo, getWidth());
                    handlerElementoTextoMovel.moverDentroDosLimites(
                            x, y, limites);
                }
                repaint();
                return;
            }

            ItemTextoArrastavel itemMovido =
                    handlerItemTextoArrastavel.moverPara(x, y);
            if (itemMovido != null) {
                atualizarRealceAlvoProximidade(itemMovido);
                atualizarQuestionamentoPersistenteDuranteMovimento(itemMovido);
                repaint();
                return;
            }

            if (handlerConectorVergnaud.estaAtivo()) {
                boolean moveu = handlerConectorVergnaud.mover(
                        x, y, obterLimitesMovimentoConectorVergnaud());
                if (moveu) {
                    repaint();
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (controladorArrasteElastico.estaAtivo()) {
                controladorArrasteElastico.concluir(e.getX(), e.getY());
            }
            handlerConectorVergnaud.finalizarLimiar();
            HandlerInteracaoQuadradinhoVenn.ResultadoSoltura resultadoSolturaQuadradinho =
                    handlerQuadradinhoVenn.concluir(circulosVenn);
            finalizarRastreamentoGranular(e.getX(), e.getY());
            if (handlerPaineisEixosRelacoes.estaAtivo()) {
                sincronizarPainelEixoRelacaoSeNecessario(true);
                handlerPaineisEixosRelacoes.concluir();
                marcadorOrigemArraste.limpar();
                atualizarCursorDepoisDoPickup(e.getX(), e.getY());
                repaint();
                return;
            }

            if (arrastandoControleComparacao) {
                arrastandoControleComparacao = false;
                flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao();
                marcadorOrigemArraste.limpar();
                // Ao soltar o controle do gráfico de barras (comparação de
                // medidas): mesma checagem/pergunta de confirmação do valor
                // da incógnita usada nos outros protocolos de preenchimento —
                // esse caminho escreve direto no diagrama e, sem isso,
                // contornava a checagem inteira (ver
                // incognitaAguardandoConfirmacaoDeValor em
                // atualizarBarrasComparacaoAPartirDoControle, que só bloqueia
                // em silêncio durante o arrasto).
                ElementoVergnaud diferenca = encontrarElementoVergnaudPorPapel(
                        "papel.diferenca");
                if (diferenca != null) {
                    ItemTextoArrastavel itemDiferenca = encontrarItemSobreElemento(diferenca);
                    if (confirmarValorIncognitaAceito(itemDiferenca)) {
                        sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                                diferenca, EstadoSemanticoCompartilhado.Origem.EIXO_VERTICAL);
                    }
                    verificarConclusaoModelagem();
                }
                atualizarCursorDepoisDoPickup(e.getX(), e.getY());
                repaint();
                return;
            }

            handlerElementoTextoMovel.concluir();
            HandlerInteracaoItemTextoArrastavel.ResultadoSoltura solturaItem =
                    handlerItemTextoArrastavel.concluir(e.getX(), e.getY());
            ItemTextoArrastavel itemSolto = solturaItem.getItem();
            ElementoVergnaud destinoGeometrico =
                    encontrarElementoVergnaudPorItem(itemSolto);
            registrarGestoItemSolto(solturaItem, destinoGeometrico);
            // Clique parado, inclusive no duplo-clique, é reavaliação de
            // consistência e não um novo arraste/posicionamento do usuário.
            boolean itemRealmenteMoveu = solturaItem.houveMovimento();
            if (itemSolto != null) {
                ElementoVergnaud alvo = obterAlvoCorretoParaItem(itemSolto);
                boolean proximo = alvo != null && itemEstaProximoDoElemento(itemSolto, alvo);
                if (alvo != null && scaffoldingProximidade.deveCentralizarAoSoltar(modoFeedbackTeste, proximo)) {
                    centralizarItemNoElemento(itemSolto, alvo);
                }
            }

            gerard.pesquisador.auditoria.OrigemAvaliacao origemSoltura = itemRealmenteMoveu
                    ? gerard.pesquisador.auditoria.OrigemAvaliacao.SOLTURA_USUARIO
                    : gerard.pesquisador.auditoria.OrigemAvaliacao.REAVALIACAO_CONSISTENCIA;
            ResultadoQuestionamento resultadoPosicionamento =
                    avaliarQuestionamentoPosicionamento(itemSolto, origemSoltura);
            boolean posicionamentoIncorreto = resultadoPosicionamento.isAplicavel()
                    && !resultadoPosicionamento.isCorreto();
            finalizarProxyTextoSolto(itemSolto, !posicionamentoIncorreto);
            limparRealceAlvoProximidade();
            handlerConectorVergnaud.cancelar();

            if (posicionamentoIncorreto) {
                // O item permanece no diagrama e reutiliza o fluxo consolidado
                // de tip persistente, som sutil e tremor leve. Assim, o usuário
                // pode arrastá-lo para outro papel sem precisar voltar ao texto.
                processarQuestionamentoPosicionamento(itemSolto);
            } else if (itemSolto == itemQuestionadoPersistente
                    && scaffoldingQuestionamento
                            .deveLimparQuestionamentoPersistente(
                                    resultadoPosicionamento)) {
                // Ao corrigir o posicionamento, o tip do erro anterior não
                // pode permanecer associado ao item já compatível.
                limparQuestionamentoPersistente();
            }
            registrarLogSolturaItem(
                    itemSolto, itemRealmenteMoveu, resultadoPosicionamento);
            if (!posicionamentoIncorreto) {
                atualizarHabilitacaoSincronizacaoEstadoFinal(itemSolto);
                processarSolturaEmNumeroRelativo(itemSolto);
                atualizarRepresentacoesReativasAposAlteracaoDoItem(itemSolto);
            }
            if (resultadoSolturaQuadradinho.houveMovimento()) {
                sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(
                        resultadoSolturaQuadradinho.obterIndiceParaSincronizacao(),
                        EstadoSemanticoCompartilhado.Origem.ARRASTE);
            }
            marcadorOrigemArraste.limpar();
            atualizarCursorDepoisDoPickup(e.getX(), e.getY());
            verificarConclusaoModelagem();
            repaint();
        }

        public void mouseClicked(MouseEvent e) {
            if (encontrarRepresentacaoPeloControleAdicionarQuadradinho(
                    e.getX(), e.getY()) != null
                    || encontrarRepresentacaoPeloControleRemoverQuadradinho(
                            e.getX(), e.getY()) != null) {
                return;
            }
            if (e.getClickCount() == 2) {
                ItemTextoArrastavel item = encontrarItemArrastavel(e.getX(), e.getY());

                if (item != null && item.editavel && item.estaNoDiagrama()) {
                    editarNumeroNatural(item);
                    return;
                }

                QuadradinhoVenn quadradinho = encontrarQuadradinhoVenn(e.getX(), e.getY());
                if (quadradinho != null) {
                    editarTextoQuadradinhoVenn(quadradinho);
                    return;
                }

                ElementoVergnaud elemento = encontrarElementoVergnaud(e.getX(), e.getY());
                if (elemento != null) {
                    editarTextoElementoVergnaud(elemento);
                    return;
                }

                CirculoVenn circulo = encontrarCirculoVenn(e.getX(), e.getY());
                if (circulo != null) {
                    editarTextoCirculoVenn(circulo);
                    return;
                }
            }
        }


        private void registrarAcaoGranular(String tipo, String tarefa, String organizacao,
                String artefato, String funcao, String detalhes, String mudanca) {
            loggerInteracaoGerard.registrarAcaoGranularUsuario(tipo, tarefa, organizacao, artefato,
                    funcao, "OBJ_INTERACAO", "ACAO_GRANULAR_" + tipo, detalhes, mudanca);
        }

        private void iniciarRastreamentoGranular(int x, int y, String objeto, String artefato, boolean ehTexto) {
            rastreamentoCaminhoAtivo = true;
            rastreamentoInicioX = rastreamentoUltimoX = x;
            rastreamentoInicioY = rastreamentoUltimoY = y;
            rastreamentoUltimaOrientacao = "";
            rastreamentoMudancasOrientacao = 0;
            rastreamentoAmostras = 0;
            rastreamentoObjeto = objeto == null ? "" : objeto;
            rastreamentoArtefato = artefato == null ? "" : artefato;
            rastreamentoEhTexto = ehTexto;
        }

        private void atualizarRastreamentoGranular(int x, int y) {
            if (!rastreamentoCaminhoAtivo) return;
            int dx = x - rastreamentoUltimoX;
            int dy = y - rastreamentoUltimoY;
            if (dx == 0 && dy == 0) return;
            rastreamentoAmostras++;
            String orientacao;
            if (Math.abs(dx) >= Math.abs(dy)) orientacao = dx >= 0 ? "DIREITA" : "ESQUERDA";
            else orientacao = dy >= 0 ? "BAIXO" : "CIMA";
            if (!orientacao.equals(rastreamentoUltimaOrientacao)) {
                rastreamentoMudancasOrientacao++;
                registrarAcaoGranular("ORIENTACAO", "Orientar deslocamento", "Espaço bidimensional",
                        rastreamentoArtefato, "Escolher a direção do movimento",
                        "objeto=" + rastreamentoObjeto + "; direcao=" + orientacao + "; dx=" + dx + "; dy=" + dy,
                        "Direção observável alterada para " + orientacao + ".");
                rastreamentoUltimaOrientacao = orientacao;
            }
            rastreamentoUltimoX = x;
            rastreamentoUltimoY = y;
        }

        private void finalizarRastreamentoGranular(int x, int y) {
            if (!rastreamentoCaminhoAtivo) return;
            int dx = x - rastreamentoInicioX;
            int dy = y - rastreamentoInicioY;
            double distancia = Math.sqrt((double) dx * dx + (double) dy * dy);
            boolean houveDeslocamento = x != rastreamentoInicioX || y != rastreamentoInicioY;
            if (rastreamentoAmostras > 0 || houveDeslocamento) {
                int amostrasEfetivas = rastreamentoAmostras > 0 ? rastreamentoAmostras : 1;
                registrarAcaoGranular("CAMINHO", "Executar caminho de arraste", "Espaço bidimensional",
                        rastreamentoArtefato, "Executar série contínua de orientação e posicionamento",
                        "objeto=" + rastreamentoObjeto + "; inicio=(" + rastreamentoInicioX + "," + rastreamentoInicioY + ")" +
                        "; fim=(" + x + "," + y + "); amostras=" + amostrasEfetivas +
                        "; mudancas_orientacao=" + rastreamentoMudancasOrientacao + "; distancia=" + Math.round(distancia),
                        "Trajetória concluída entre o ponto inicial e o ponto final.");
                registrarAcaoGranular("POSICIONAR", "Posicionar elemento", "Espaço bidimensional",
                        rastreamentoArtefato, "Definir a posição final do elemento",
                        "objeto=" + rastreamentoObjeto + "; x=" + x + "; y=" + y,
                        "Posição final alterada para (" + x + ", " + y + ").");
                if (rastreamentoEhTexto) {
                    registrarAcaoGranular("TEXTO", "Movimentar texto", "Texto do problema",
                            rastreamentoArtefato, "Modificar a posição espacial de um texto",
                            "texto=" + rastreamentoObjeto + "; x=" + x + "; y=" + y,
                            "Texto movimentado para nova posição.");
                }
            }
            rastreamentoCaminhoAtivo = false;
        }

        private void registrarLogUsuario(String tarefa,
                String ce,
                String instrumentoOrganizacao,
                String instrumentoArtefato,
                String funcaoDoArtefato,
                String objeto,
                String regras,
                String origemEvento,
                String detalhes) {
            registrarLogPorOrigem(OrigemAcao.ORIGEM_USUARIO, tarefa, ce, instrumentoOrganizacao,
                    instrumentoArtefato, funcaoDoArtefato, objeto, regras, origemEvento, detalhes);
        }

        private void registrarLogComputador(String tarefa,
                String instrumentoOrganizacao,
                String instrumentoArtefato,
                String funcaoDoArtefato,
                String regras,
                String origemEvento,
                String detalhes) {
            registrarLogPorOrigem(OrigemAcao.ORIGEM_SISTEMA, tarefa, null, instrumentoOrganizacao,
                    instrumentoArtefato, funcaoDoArtefato, null, regras, origemEvento, detalhes);
        }

        private void registrarLogUsuarioComIdentidade(String tarefa,
                String ce,
                String instrumentoOrganizacao,
                String instrumentoArtefato,
                String funcaoDoArtefato,
                String objeto,
                String regras,
                String origemEvento,
                String detalhes,
                String actionId,
                String rejectionSequenceId) {
            registrarLogUsuarioComIdentidade("TEXTO", tarefa, ce,
                    instrumentoOrganizacao, instrumentoArtefato,
                    funcaoDoArtefato, objeto, regras, origemEvento, detalhes,
                    actionId, rejectionSequenceId);
        }

        private void registrarLogUsuarioComIdentidade(String tipoAcao,
                String tarefa,
                String ce,
                String instrumentoOrganizacao,
                String instrumentoArtefato,
                String funcaoDoArtefato,
                String objeto,
                String regras,
                String origemEvento,
                String detalhes,
                String actionId,
                String rejectionSequenceId) {
            loggerInteracaoGerard.registrarUsuarioComIdentidade(tipoAcao, tarefa, ce,
                    instrumentoOrganizacao, instrumentoArtefato,
                    funcaoDoArtefato, objeto, regras, origemEvento, detalhes,
                    actionId, rejectionSequenceId);
        }

        private void registrarLogComputadorComIdentidade(String tarefa,
                String instrumentoOrganizacao,
                String instrumentoArtefato,
                String funcaoDoArtefato,
                String regras,
                String origemEvento,
                String detalhes,
                String actionId,
                String rejectionSequenceId) {
            loggerInteracaoGerard.registrarComputadorComIdentidade(tarefa,
                    instrumentoOrganizacao, instrumentoArtefato,
                    funcaoDoArtefato, regras, origemEvento, detalhes,
                    actionId, rejectionSequenceId);
        }

        /**
         * Ponto único de despacho do log de interação, tipado pela origem da
         * ação (gerard.dominio.campoaditivo.OrigemAcao) — no lugar da
         * distinção puramente por nome de método que existia antes
         * (registrarLogUsuario/registrarLogComputador cada uma chamando
         * diretamente o método correspondente de LoggerInteracaoGerard, sem
         * nenhum valor tipado carregando essa decisão).
         *
         * Referencia o pacote piloto gerard.dominio.campoaditivo (tipo
         * OrigemAcao) — não é mais o único ponto desde 2026-08-07:
         * tentativasIncognitaAtual (fluxo de tentativas rejeitadas,
         * REFERENCE.md §4.8) também referencia PapelQuantitativo/
         * DiagnosticoErroPapel/TipoErroPapel/ContextoAcao/DominioNumerico,
         * mas só para contagem — nunca para armazenar um valor real, que
         * continua exclusivo de estadoSemanticoCompartilhado. Ver o
         * javadoc de PapelQuantitativo para o estado geral da fronteira.
         *
         * ce e objeto só fazem sentido semântico para ORIGEM_USUARIO (o
         * registro de computador não avalia acerto/erro nem aponta um
         * objeto do sujeito); por isso os dois wrappers acima passam null
         * quando a origem é ORIGEM_SISTEMA, e esses valores são descartados
         * aqui — LoggerInteracaoGerard.registrarComputador nunca os recebeu.
         *
         * ORIGEM_INFERENCIA e ORIGEM_PESQUISADOR não têm, hoje, nenhum
         * ponto de chamada em Main.java — caem no mesmo ramo de
         * ORIGEM_SISTEMA por não haver, ainda, um registro de log
         * específico para essas origens.
         */
        private void registrarLogPorOrigem(OrigemAcao origem,
                String tarefa,
                String ce,
                String instrumentoOrganizacao,
                String instrumentoArtefato,
                String funcaoDoArtefato,
                String objeto,
                String regras,
                String origemEvento,
                String detalhes) {
            if (origem == OrigemAcao.ORIGEM_USUARIO) {
                loggerInteracaoGerard.registrarUsuario(
                        tarefa,
                        ce,
                        instrumentoOrganizacao,
                        instrumentoArtefato,
                        funcaoDoArtefato,
                        objeto,
                        regras,
                        origemEvento,
                        detalhes
                );
            } else {
                loggerInteracaoGerard.registrarComputador(
                        tarefa,
                        instrumentoOrganizacao,
                        instrumentoArtefato,
                        funcaoDoArtefato,
                        regras,
                        origemEvento,
                        detalhes
                );
            }
        }

        private void registrarGestoItemSolto(
                HandlerInteracaoItemTextoArrastavel.ResultadoSoltura soltura,
                ElementoVergnaud destinoGeometrico) {
            if (soltura == null || soltura.getItem() == null
                    || soltura.getGestoConcluido() == null) {
                return;
            }
            ContextoRegistroGesto contexto = new ContextoRegistroGesto(
                    loggerInteracaoGerard.getSessaoId(),
                    loggerInteracaoGerard.getUsuarioAtual(),
                    loggerInteracaoGerard.getProblemaAtual(),
                    loggerInteracaoGerard.getTentativaAtualId());
            DestinoGeometricoGesto destino = destinoGeometrico == null
                    ? DestinoGeometricoGesto.FORA_DE_ELEMENTO_DO_DIAGRAMA
                    : DestinoGeometricoGesto.SOBRE_ELEMENTO_DO_DIAGRAMA;
            publicadorGestosInteracao.publicar(
                    soltura.getItem().produzirRegistroGestoArraste(
                            soltura.getGestoConcluido(), contexto, destino));
        }

        private void registrarLogSolturaItem(ItemTextoArrastavel item,
                boolean houveMovimento,
                ResultadoQuestionamento resultadoPosicionamento) {
            if (item == null || !houveMovimento
                    || resultadoPosicionamento == null
                    || !resultadoPosicionamento.isAplicavel()) {
                return;
            }

            ElementoVergnaud elemento = encontrarElementoVergnaudPorItem(item);
            if (elemento == null) {
                return;
            }
            String artefato = descreverElementoVergnaudParaLog(elemento);
            String ce = resultadoPosicionamento.isCorreto() ? "C" : "E";
            String regras = resultadoPosicionamento.isCorreto()
                    ? "O valor foi associado ao elemento do modelo que representa sua função no problema."
                    : "O valor deve ser associado ao elemento do modelo que representa sua função no problema.";

            registrarLogUsuario(
                    "Associar valor do enunciado a um elemento do modelo",
                    ce,
                    "Valor do enunciado e elemento do modelo",
                    artefato,
                    "Preencher o diagrama/modelo com os valores do problema",
                    obterObjetoParaLog(elemento),
                    regras,
                    "SOLTURA",
                    "valor=" + item.valor + "; papel=" + (item.chavePapel != null ? item.chavePapel : "")
            );
        }

        private String descreverElementoVergnaudParaLog(ElementoVergnaud elemento) {
            if (elemento == null) {
                return "";
            }
            String chave = obterPapelSemanticoDoElemento(elemento);
            String papel = localizacao.texto(chave);
            if (papel == null || papel.trim().length() == 0 || papel.startsWith("ui.") || papel.startsWith("papel.")) {
                papel = elemento.rotulo != null && elemento.rotulo.trim().length() > 0 ? elemento.rotulo : elemento.tipo.name();
            }
            return papel;
        }

        private String obterObjetoParaLog(ElementoVergnaud elemento) {
            if (elemento == null) {
                return "";
            }
            return catalogoObjetosLogAcaoInstrumental.obterCodigo(
                    obterPapelSemanticoDoElemento(elemento));
        }

        private void processarSolturaEmNumeroRelativo(ItemTextoArrastavel item) {
            if (item == null || !item.estaNoDiagrama()) {
                return;
            }
            ElementoVergnaud numeroRelativo = encontrarNumeroRelativoPorItem(item);
            if (numeroRelativo == null) {
                return;
            }
            if (!scaffoldingNumeroRelativo.ehNumeroOuInterrogacao(item.valor)) {
                return;
            }
            if (deveBloquearMenuNumeroRelativoPorQuestionamento(item, numeroRelativo)) {
                return;
            }
            if (scaffoldingNumeroRelativo.temSinal(item.valor)) {
                sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                        numeroRelativo, EstadoSemanticoCompartilhado.Origem.ARRASTE);
                return;
            }
            solicitarSinalNumeroRelativoParaItem(item, numeroRelativo, false);
        }

        private ElementoVergnaud encontrarNumeroRelativoPorItem(ItemTextoArrastavel item) {
            if (item == null) {
                return null;
            }
            int centroX = item.x + item.largura / 2;
            int centroY = item.y + item.altura / 2;
            return encontrarNumeroRelativo(centroX, centroY);
        }

        private ElementoVergnaud encontrarElementoVergnaudPorItem(ItemTextoArrastavel item) {
            if (item == null) {
                return null;
            }
            int centroX = item.x + item.largura / 2;
            int centroY = item.y + item.altura / 2;
            return encontrarElementoVergnaud(centroX, centroY);
        }

        /**
         * Reavaliações internas consultam a compatibilidade sem produzir uma
         * nova ação. Somente a soltura canônica constitui o registro factual
         * POSICIONAR encaminhado ao Modelador.
         */
        private ResultadoQuestionamento avaliarQuestionamentoPosicionamento(
                ItemTextoArrastavel item, gerard.pesquisador.auditoria.OrigemAvaliacao origem) {
            if (item == null || !item.estaNoDiagrama()) {
                return ResultadoQuestionamento.naoAplicavel();
            }

            String chavePapelElementoTexto = obterChavePapelExataDoItem(item);
            String valorParaValidacao = item.origemValor != null
                    && item.origemValor.trim().length() > 0
                    ? item.origemValor : item.valor;
            if (!politicaElementoMatematicoTexto.deveValidar(
                    valorParaValidacao, chavePapelElementoTexto)) {
                return ResultadoQuestionamento.naoAplicavel();
            }

            ElementoVergnaud elementoAlvo = encontrarElementoVergnaudPorItem(item);
            if (elementoAlvo == null) {
                return ResultadoQuestionamento.naoAplicavel();
            }

            String chavePapelNumeral = chavePapelElementoTexto;
            String chavePapelAlvo = obterPapelSemanticoDoElemento(elementoAlvo);
            String papelDoElementoNoDiagrama = localizacao.texto(chavePapelAlvo);

            ResultadoQuestionamento resultado = scaffoldingQuestionamento.avaliarPosicionamento(
                    chavePapelNumeral,
                    chavePapelAlvo,
                    papelDoElementoNoDiagrama,
                    localizacao.descricaoTipo(tipoSituacaoSelecionada)
            );
            if (resultado != null && resultado.isAplicavel()
                    && origem.isCanonica() && tipoSituacaoSelecionada != null) {
                ContextoAcaoInstrumental contextoInstrumental =
                        new ContextoAcaoInstrumental(
                                "Posicionar elemento semântico do enunciado",
                                "Arrastar e soltar",
                                "Texto e diagrama de Vergnaud",
                                "Associar o elemento textual ao papel quantitativo correspondente",
                                chavePapelAlvo,
                                origem.name(),
                                "origem=" + chavePapelNumeral
                                        + "; destino=" + chavePapelAlvo,
                                "Elemento posicionado sobre um papel do diagrama",
                                participantesSemanticosDaSituacaoAtual());
                gerard.dominio.campoaditivo.RegistroAcaoPosicionamentoPapelQuantitativo
                        registro = catalogoPapeisSemanticos
                                .obterDescritor(chavePapelNumeral)
                                .avaliarPosicionamento(
                                        catalogoPapeisSemanticos
                                                .obterDescritor(chavePapelAlvo),
                                        tipoSituacaoSelecionada,
                                        contextoInstrumental);
                loggerInteracaoGerard.registrarAcaoInstrumentalUsuario(registro);
                conectorVereditoModelador.registrarAcaoInstrumental(
                        loggerInteracaoGerard.getUsuarioAtual(),
                        registro,
                        gerard.agente.modelousuario.NivelSuporte.NENHUM,
                        registro.getActionId());
            }
            return resultado;
        }

        private boolean processarQuestionamentoPosicionamento(ItemTextoArrastavel item) {
            ResultadoQuestionamento resultado = avaliarQuestionamentoPosicionamento(item,
                    gerard.pesquisador.auditoria.OrigemAvaliacao.REAVALIACAO_CONSISTENCIA);

            if (resultado.isAplicavel() && !resultado.isCorreto()) {
                registrarQuestionamentoPersistente(item, resultado);
                scaffoldingFeedbackMultissensorialErro.sinalizarErro(item, new Runnable() {
                    public void run() {
                        repaint();
                    }
                });
                return true;
            }

            if (item == itemQuestionadoPersistente) {
                limparQuestionamentoPersistente();
            }
            return false;
        }

        private boolean deveBloquearMenuNumeroRelativoPorQuestionamento(ItemTextoArrastavel item, ElementoVergnaud numeroRelativo) {
            ResultadoQuestionamento resultado = avaliarQuestionamentoPosicionamento(item,
                    gerard.pesquisador.auditoria.OrigemAvaliacao.SINCRONIZACAO_REPRESENTACOES);
            ElementoVergnaud elementoAlvo = encontrarElementoVergnaudPorItem(item);
            boolean itemSobreNumeroRelativo = numeroRelativo != null && numeroRelativo == elementoAlvo;

            if (scaffoldingQuestionamento.deveBloquearMenuNumeroRelativo(resultado, itemSobreNumeroRelativo)) {
                registrarQuestionamentoPersistente(item, resultado);
                scaffoldingFeedbackMultissensorialErro.sinalizarErro(item, new Runnable() {
                    public void run() {
                        repaint();
                    }
                });
                return true;
            }

            return false;
        }

        private void registrarQuestionamentoMouseOver(ItemTextoArrastavel item, ResultadoQuestionamento resultado) {
            if (item == null || resultado == null) {
                return;
            }
            textoAnotacaoMouseOver = resultado.getMensagem();
            mostrarAnotacaoMouseOver = true;
            mouseOverX = item.x + item.largura / 2;
            mouseOverY = Math.max(50, item.y);
        }

        private void registrarQuestionamentoPersistente(ItemTextoArrastavel item, ResultadoQuestionamento resultado) {
            if (item == null || resultado == null || resultado.getMensagem() == null || resultado.getMensagem().trim().length() == 0) {
                return;
            }
            itemQuestionadoPersistente = item;
            textoQuestionamentoPersistente = resultado.getMensagem();
            mostrarQuestionamentoPersistente = true;
            textoAnotacaoMouseOver = resultado.getMensagem();
            mostrarAnotacaoMouseOver = false;
        }

        private void limparQuestionamentoPersistente() {
            scaffoldingFeedbackMultissensorialErro.pararTremor();
            mostrarQuestionamentoPersistente = false;
            textoQuestionamentoPersistente = "";
            itemQuestionadoPersistente = null;
        }

        private void atualizarQuestionamentoPersistenteDuranteMovimento(ItemTextoArrastavel item) {
            if (item == null || item != itemQuestionadoPersistente) {
                return;
            }

            ResultadoQuestionamento resultado = avaliarQuestionamentoPosicionamento(item,
                    gerard.pesquisador.auditoria.OrigemAvaliacao.SINCRONIZACAO_REPRESENTACOES);
            if (!resultado.isAplicavel() || resultado.isCorreto()) {
                limparQuestionamentoPersistente();
                return;
            }

            textoQuestionamentoPersistente = resultado.getMensagem();
            mostrarQuestionamentoPersistente = true;
        }

        /**
         * @param confirmarAoFinalizar false durante o arrasto (chamadas
         *        contínuas a cada movimento do mouse): só bloqueia a
         *        propagação em silêncio, sem diálogo — erro só deve aparecer
         *        ao soltar, nunca durante o arrasto (mesmo princípio já
         *        aplicado ao gráfico de barras da comparação). true ao
         *        soltar/finalizar a interação: mostra a mesma pergunta de
         *        confirmação e dica usadas nos outros protocolos.
         */
        private void sincronizarPainelEixoRelacaoSeNecessario(boolean confirmarAoFinalizar) {
            PaineisEixosRelacoes.Painel painel = paineisEixosRelacoes.encontrarComAlteracaoPorInteracao();
            if (painel == null) {
                return;
            }
            ElementoVergnaud numeroRelativo = painel.elemento;
            int valor = painel.grafico.getValorNavegavel();
            Integer valorAnterior = obterValorNumericoDoElemento(numeroRelativo);
            if (!valorRelativoPreservaQuantidadesNaoNegativas(numeroRelativo, valor)) {
                int seguro = politicaRestauracaoValorRelativo
                        .escolherValorSeguro(valorAnterior, valor);
                aplicarValorRelativoNoDiagrama(numeroRelativo, null, seguro);
                painel.apresentador.registrarEscolha(
                        retanguloDoElemento(numeroRelativo),
                        servicoQuantidadeContextual.formatarMagnitudeNumeroRelativo(
                                seguro, situacaoProblemaAtual),
                        servicoQuantidadeContextual.sinalNumeroRelativo(seguro));
                informarBloqueioQuantidadeNegativa(null, numeroRelativo);
                painel.grafico.limparAlteracaoValorPorInteracao();
                repaint();
                return;
            }
            registrarLogUsuario(
                    "Navegar no eixo x das Relações (painel próprio do papel)",
                    "-",
                    "Eixo x navegável",
                    "Ponto de controle do eixo",
                    "Quantificar o papel da relação e manter consistência entre representações",
                    "OBJ4",
                    "Ao alterar o eixo, o círculo da relação e os valores dependentes do diagrama devem ser atualizados.",
                    "EIXO_X_RELACOES",
                    "valorRelativo=" + valor
            );
            aplicarValorRelativoNoDiagrama(numeroRelativo, null, valor);
            ItemTextoArrastavel itemIncognita = encontrarItemSobreElemento(numeroRelativo);
            boolean liberadoParaPropagar = confirmarAoFinalizar
                    ? confirmarValorIncognitaAceito(itemIncognita)
                    : !incognitaAguardandoConfirmacaoDeValor(itemIncognita);
            if (liberadoParaPropagar) {
                sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                        numeroRelativo,
                        EstadoSemanticoCompartilhado.Origem.EIXO_X);
            }
            if (confirmarAoFinalizar) {
                verificarConclusaoModelagem();
            }
            painel.apresentador.atualizarGeometria(retanguloDoElemento(numeroRelativo));
            painel.grafico.limparAlteracaoValorPorInteracao();
        }

        private void atualizarHabilitacaoSincronizacaoEstadoFinal(ItemTextoArrastavel item) {
            if (item == null || !item.estaNoDiagrama()) {
                return;
            }
            if (!SimboloDesconhecido.eh(item.origemValor) && !SimboloDesconhecido.eh(item.valor)) {
                return;
            }

            ElementoVergnaud alvo = encontrarElementoVergnaudPorItem(item);
            if (!ehElementoEstadoFinalIncognito(alvo)) {
                return;
            }

            ResultadoQuestionamento resultado = avaliarQuestionamentoPosicionamento(item,
                    gerard.pesquisador.auditoria.OrigemAvaliacao.REAVALIACAO_CONSISTENCIA);
            if (resultado.isAplicavel() && !resultado.isCorreto()) {
                return;
            }

            sincronizacaoEstadoFinalHabilitada = true;
            itemIncognitaEstadoFinal = item;
            elementoEstadoFinalSincronizado = alvo;
            recalcularEstadoFinalSeNumeroRelativoJaDefinido();
        }

        private boolean existeIncognitaEstadoFinalPosicionadaCorretamente() {
            if (!sincronizacaoEstadoFinalHabilitada) {
                return false;
            }
            if (itemIncognitaEstadoFinal == null || !itensArrastaveis.contains(itemIncognitaEstadoFinal)) {
                return false;
            }
            if (elementoEstadoFinalSincronizado == null || !elementosVergnaud.contains(elementoEstadoFinalSincronizado)) {
                return false;
            }
            ElementoVergnaud alvoAtual = encontrarElementoVergnaudPorItem(itemIncognitaEstadoFinal);
            return alvoAtual != null && alvoAtual == elementoEstadoFinalSincronizado && ehElementoEstadoFinalIncognito(alvoAtual);
        }

        private void desabilitarSincronizacaoEstadoFinal() {
            sincronizacaoEstadoFinalHabilitada = false;
            itemIncognitaEstadoFinal = null;
            elementoEstadoFinalSincronizado = null;
        }

        private boolean ehElementoEstadoFinalIncognito(ElementoVergnaud elemento) {
            return elemento != null
                    && (elemento.incognitaPrincipal
                    || "papel.estadoFinal".equals(elemento.chavePapelSemantico));
        }

        private void recalcularEstadoFinalSeNumeroRelativoJaDefinido() {
            ElementoVergnaud numeroRelativo = encontrarNumeroRelativoParaSincronizacaoEstadoFinal();
            if (numeroRelativo == null) {
                return;
            }
            Integer valorRelativo = obterValorNumericoDoElemento(numeroRelativo);
            if (valorRelativo == null) {
                return;
            }
            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                    numeroRelativo, EstadoSemanticoCompartilhado.Origem.ARRASTE);
        }

        private void atualizarRepresentacoesReativasAposAlteracaoDoItem(ItemTextoArrastavel item) {
            if (item == null) {
                return;
            }

            ElementoVergnaud elemento = encontrarElementoVergnaudPorItem(item);
            if (elemento == null) {
                return;
            }

            // Enquanto o valor da incógnita divergir do valor curado, a
            // mudança fica só no próprio item: as outras representações
            // (texto, Venn, eixo) não devem refletir uma resposta ainda não
            // confirmada como correta — ver incognitaAguardandoConfirmacaoDeValor.
            if (!incognitaAguardandoConfirmacaoDeValor(item)) {
                sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                        elemento, EstadoSemanticoCompartilhado.Origem.ARRASTE);
            }
            verificarConclusaoModelagem();
        }

        private void atualizarRepresentacoesReativasAposAlteracaoDoElemento(ElementoVergnaud elemento) {
            if (elemento == null) {
                return;
            }

            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                    elemento, EstadoSemanticoCompartilhado.Origem.EDICAO_TEXTO);
            verificarConclusaoModelagem();
        }

        private void aplicarValorRelativoNoDiagrama(ElementoVergnaud numeroRelativo,
                ItemTextoArrastavel itemPreferencial,
                int valorRelativo) {
            if (numeroRelativo == null
                    || devePreservarMarcadorIncognita(numeroRelativo)) {
                return;
            }

            String textoRelativo = servicoQuantidadeContextual
                    .formatarNumeroRelativoParaDiagrama(
                            valorRelativo, situacaoProblemaAtual);
            ItemTextoArrastavel item = itemPreferencial != null ? itemPreferencial : encontrarItemSobreElemento(numeroRelativo);

            if (item != null) {
                item.valor = textoRelativo;
                ajustarTamanhoDoItem(item);
                centralizarItemNoNumeroRelativoSeNecessario(item);
                numeroRelativo.textoEditavel = "";
            } else {
                numeroRelativo.textoEditavel = textoRelativo;
            }
        }

        private void definirValorNoElementoNumeroRelativo(ElementoVergnaud numeroRelativo, int valorRelativo) {
            aplicarValorRelativoNoDiagrama(numeroRelativo, null, valorRelativo);
        }

        private ElementoVergnaud encontrarNumeroRelativoParaSincronizacaoEstadoFinal() {
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                ElementoVergnaud elemento = elementosVergnaud.get(i);
                if (ehElementoNumeroRelativo(elemento) && obterValorNumericoDoElemento(elemento) != null) {
                    return elemento;
                }
            }
            return null;
        }

        private Integer obterValorNumericoDoElemento(ElementoVergnaud elemento) {
            if (elemento == null) {
                return null;
            }

            Integer valorTextoEditavel = converterTextoParaInteiro(elemento.textoEditavel);
            if (valorTextoEditavel != null) {
                return valorTextoEditavel;
            }

            ItemTextoArrastavel item = encontrarItemSobreElemento(elemento);
            if (item != null) {
                return converterTextoParaInteiro(item.valor);
            }

            return null;
        }

        private Integer converterTextoParaInteiro(String texto) {
            return conversorTextoParaInteiroSemantico.converter(
                    texto, situacaoProblemaAtual);
        }

        private ItemTextoArrastavel encontrarItemSobreElemento(ElementoVergnaud elemento) {
            if (elemento == null) {
                return null;
            }

            for (int i = itensArrastaveis.size() - 1; i >= 0; i--) {
                ItemTextoArrastavel item = itensArrastaveis.get(i);
                if (item == null || !item.estaNoDiagrama()) {
                    continue;
                }
                int centroX = item.x + item.largura / 2;
                int centroY = item.y + item.altura / 2;
                if (elemento.contem(centroX, centroY)) {
                    return item;
                }
            }

            return null;
        }

        private void definirValorNoElementoMedida(ElementoVergnaud elemento, String valor) {
            if (elemento == null || devePreservarMarcadorIncognita(elemento)) {
                return;
            }

            ItemTextoArrastavel item = encontrarItemSobreElemento(elemento);
            if (item != null) {
                item.valor = valor;
                ajustarTamanhoDoItem(item);
                centralizarItemNoElemento(item, elemento);
                elemento.textoEditavel = "";
            } else {
                elemento.textoEditavel = valor != null ? valor : "";
            }
        }

        private Rectangle retanguloDoElemento(ElementoVergnaud elemento) {
            if (elemento == null) {
                return null;
            }
            return new Rectangle(elemento.x, elemento.y, elemento.largura, elemento.altura);
        }

        private String obterSinalAtual(String valor) {
            if (valor != null && valor.trim().startsWith("-")) {
                return "-";
            }
            return "+";
        }

        private ElementoVergnaud encontrarNumeroRelativo(int x, int y) {
            for (int i = elementosVergnaud.size() - 1; i >= 0; i--) {
                ElementoVergnaud elemento = elementosVergnaud.get(i);
                if (ehElementoNumeroRelativo(elemento) && elemento.contem(x, y)) {
                    return elemento;
                }
            }
            return null;
        }

        private boolean ehElementoNumeroRelativo(ElementoVergnaud elemento) {
            return elemento != null && elemento.exibirLupa;
        }

        private int calcularValorRelativo(String base, String sinal) {
            return servicoQuantidadeContextual.converterNumeroRelativoLegado(
                    base, sinal, situacaoProblemaAtual);
        }

        private boolean valorRelativoPreservaQuantidadesNaoNegativas(
                ElementoVergnaud relacao, int valorRelativo) {
            int indiceVisual = obterIndiceVisualPorIdentidadeSemantica(relacao);
            int indicePapel = converterIndiceRealParaPapel(indiceVisual);
            if (indicePapel < 0 || indicePapel > 2) {
                return true;
            }
            EstadoSemanticoCompartilhado.Snapshot snapshot =
                    estadoSemanticoCompartilhado.snapshot();
            ValorNumerico[] valores = new ValorNumerico[] {
                snapshot.getValorNumerico(0), snapshot.getValorNumerico(1),
                snapshot.getValorNumerico(2)
            };
            return resolvedorRelacoesEstruturais.tentativaPreservaDominios(
                    tipoSituacaoSelecionada, valores, indicePapel,
                    Integer.valueOf(valorRelativo));
        }

        private void informarBloqueioQuantidadeNegativa() {
            informarBloqueioQuantidadeNegativa(null, null);
        }

        /**
         * Mesma mensagem/anotação de bloqueio, com âncora explícita — usada
         * por {@link #sincronizarPainelEixoRelacaoSeNecessario(boolean)}
         * (painéis persistentes de Relações), que tem seu próprio par
         * item/elemento. O overload sem parâmetros acima é usado onde não
         * há item/elemento específico para ancorar a anotação.
         */
        private void informarBloqueioQuantidadeNegativa(
                final ItemTextoArrastavel itemAncora, ElementoVergnaud elementoAncora) {
            final String mensagem = localizacao.texto("ui.tooltip.negativeQuantity");
            registrarAcaoGranular(
                    "QUANTIFICAR",
                    "Bloquear quantidade negativa",
                    "Sincronização entre representações",
                    "Quantidade de medida",
                    "Preservar quantidades cardinais não negativas",
                    mensagem,
                    "Quantidade não pode ser negativa em nenhuma categoria."
            );

            if (itemAncora != null) {
                scaffoldingFeedbackMultissensorialErro.sinalizarErro(itemAncora, new Runnable() {
                    public void run() {
                        repaint();
                    }
                });
            }

            Rectangle ancora = elementoAncora != null
                    ? retanguloDoElemento(elementoAncora)
                    : null;
            if (ancora != null) {
                mouseOverX = ancora.x + ancora.width;
                mouseOverY = Math.max(50, ancora.y + ancora.height / 2);
            }

            controladorAnotacaoTemporaria.mostrar(
                    2600,
                    new Runnable() {
                        public void run() {
                            textoAnotacaoMouseOver = mensagem;
                            mostrarAnotacaoMouseOver = true;
                            repaint();
                        }
                    },
                    new Runnable() {
                        public void run() {
                            if (!mostrarQuestionamentoPersistente
                                    && !mostrarLimiteQuantidadeQuestionado) {
                                mostrarAnotacaoMouseOver = false;
                                textoAnotacaoMouseOver = "";
                                repaint();
                            }
                        }
                    }
            );
        }

        /**
         * Aviso não bloqueante (2026-08-18, revisto no mesmo dia para
         * persistente) quando o sinal escolhido pelo usuário no menu de
         * número relativo diverge do sinal curado da situação: tremor + som
         * (ScaffoldingFeedbackMultissensorialErro) e um aviso com pergunta,
         * convidando a reconferir — sem desfazer a escolha nem bloquear o
         * fluxo, ao contrário de informarBloqueioQuantidadeNegativa.
         * Primeira versão usava um tooltip de 2600ms (controladorAnotacaoTemporaria);
         * a usuária achou que sumia rápido demais — "deixe na tela até que
         * seja corrigido" — então agora usa o mesmo mecanismo persistente de
         * mostrarLimiteQuantidadeQuestionado (desenharAnotacaoMouseOver),
         * limpo só quando o menu for reaberto para o mesmo item/elemento
         * (ver limparSinalDivergentePersistente, chamado no início de cada
         * sinalEscolhido). Antes desta correção, a comparação de sinal
         * calculava a divergência só para o log de pesquisa (C/E), sem
         * nenhum feedback visível ao participante — bug relatado pela
         * usuária ao testar Composição de relações: sinal "+3" aceito sem
         * aviso quando o curado era "-3".
         */
        private void informarSuspeitaSinalIncorretoNumeroRelativo(
                final ItemTextoArrastavel itemAncora, ElementoVergnaud elementoAncora,
                String sinalEscolhido) {
            final String mensagem = localizacao.formatar(
                    "ui.tooltip.relativeSign.confirm", sinalEscolhido);
            registrarAcaoGranular(
                    "SINALIZAR",
                    "Questionar sinal divergente do número relativo",
                    "Sincronização entre representações",
                    "Número relativo do diagrama",
                    "Convidar o participante a reconferir o sinal escolhido",
                    mensagem,
                    "O sinal escolhido diverge do valor curado da situação; o aviso não bloqueia, só convida a reconferir, "
                            + "e permanece na tela até o menu ser reaberto para o mesmo item/elemento."
            );

            if (itemAncora != null) {
                scaffoldingFeedbackMultissensorialErro.sinalizarErro(itemAncora, new Runnable() {
                    public void run() {
                        repaint();
                    }
                });
            } else if (elementoAncora != null) {
                scaffoldingFeedbackMultissensorialErro.sinalizarErro(elementoAncora, new Runnable() {
                    public void run() {
                        repaint();
                    }
                });
            }

            itemSinalDivergentePersistente = itemAncora;
            elementoSinalDivergentePersistente = itemAncora == null ? elementoAncora : null;
            textoSinalDivergentePersistente = mensagem;
            mostrarSinalDivergentePersistente = true;
            mostrarAnotacaoMouseOver = false;
            repaint();
        }

        /**
         * Ver informarSuspeitaSinalIncorretoNumeroRelativo. Chamado no
         * início de cada sinalEscolhido (antes da avaliação do proprietário)
         * para que reabrir o menu do mesmo item/elemento sempre substitua o
         * aviso anterior — seja porque o novo sinal já está certo (some), seja
         * porque ainda está errado (informarSuspeitaSinalIncorretoNumeroRelativo
         * o reexibe já com a mensagem atual).
         */
        private void limparSinalDivergentePersistente() {
            if (mostrarSinalDivergentePersistente
                    || itemSinalDivergentePersistente != null
                    || elementoSinalDivergentePersistente != null) {
                scaffoldingFeedbackMultissensorialErro.pararTremor();
            }
            mostrarSinalDivergentePersistente = false;
            textoSinalDivergentePersistente = "";
            itemSinalDivergentePersistente = null;
            elementoSinalDivergentePersistente = null;
        }

        private void restaurarValorRelativoPositivoSeguro(
                ElementoVergnaud relacao, ItemTextoArrastavel item, String base) {
            int valorSeguro = politicaRestauracaoValorRelativo
                    .restaurarComoPositivo(calcularValorRelativo(base, "+"));
            aplicarValorRelativoNoDiagrama(relacao, item, valorSeguro);
            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                    relacao, EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        }

        private void solicitarSinalNumeroRelativoParaTexto(final ElementoVergnaud elemento) {
            final String base = scaffoldingNumeroRelativo.removerSinal(elemento.textoEditavel);
            scaffoldingNumeroRelativo.mostrarMenuEscolhaSinal(
                    this,
                    new Rectangle(elemento.x, elemento.y, elemento.largura, elemento.altura),
                    base,
                    new ScaffoldingNumeroRelativo.AcaoSinalNumeroRelativo() {
                        public void sinalEscolhido(String sinal) {
                            limparSinalDivergentePersistente();
                            int valorRelativoCandidato = calcularValorRelativo(base, sinal);
                            if (!valorRelativoPreservaQuantidadesNaoNegativas(
                                    elemento, valorRelativoCandidato)) {
                                restaurarValorRelativoPositivoSeguro(
                                        elemento, null, base);
                                informarBloqueioQuantidadeNegativa();
                                repaint();
                                return;
                            }
                            elemento.textoEditavel = scaffoldingNumeroRelativo.aplicarSinal(base, sinal);
                            String chavePapelSinal = obterChavePapelDoNumeroRelativo(elemento);
                            RegistroAcaoEscolhaSinalPapelQuantitativo registroSinal =
                                    avaliarEscolhaSinalNumeroRelativo(
                                            chavePapelSinal, sinal, base);
                            if (registroSinal != null && registroSinal.foiErrada()) {
                                informarSuspeitaSinalIncorretoNumeroRelativo(null, elemento, sinal);
                            }
                            if (registroSinal == null) {
                                registrarEscolhaSinalSemCriterio(base, sinal);
                            }
                            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                                    elemento, EstadoSemanticoCompartilhado.Origem.EDICAO_TEXTO);
                            verificarConclusaoModelagem();
                            repaint();
                        }
                    }
            );
        }

        private void solicitarSinalNumeroRelativoParaItem(final ItemTextoArrastavel item, ElementoVergnaud numeroRelativo, boolean permitirTrocaDeSinal) {
            solicitarSinalNumeroRelativoParaItem(item, numeroRelativo, permitirTrocaDeSinal, false);
        }

        private void solicitarSinalNumeroRelativoParaItem(final ItemTextoArrastavel item, ElementoVergnaud numeroRelativo, boolean permitirTrocaDeSinal, final boolean ocultarSinalPositivo) {
            if (item == null || numeroRelativo == null) {
                return;
            }
            if (!permitirTrocaDeSinal && scaffoldingNumeroRelativo.temSinal(item.valor)) {
                return;
            }
            if (deveBloquearMenuNumeroRelativoPorQuestionamento(item, numeroRelativo)) {
                return;
            }
            final String base = scaffoldingNumeroRelativo.removerSinal(item.valor);
            final ElementoVergnaud numeroRelativoFinal = numeroRelativo;
            scaffoldingNumeroRelativo.mostrarMenuEscolhaSinal(
                    this,
                    new Rectangle(numeroRelativo.x, numeroRelativo.y, numeroRelativo.largura, numeroRelativo.altura),
                    base,
                    new ScaffoldingNumeroRelativo.AcaoSinalNumeroRelativo() {
                        public void sinalEscolhido(String sinal) {
                            limparSinalDivergentePersistente();
                            int valorRelativoCandidato = calcularValorRelativo(base, sinal);
                            if (!valorRelativoPreservaQuantidadesNaoNegativas(
                                    numeroRelativoFinal, valorRelativoCandidato)) {
                                restaurarValorRelativoPositivoSeguro(
                                        numeroRelativoFinal, item, base);
                                informarBloqueioQuantidadeNegativa();
                                repaint();
                                return;
                            }
                            // No circulo da relacao o sinal precisa permanecer explicito,
                            // inclusive quando o positivo for escolhido apos preenchimento.
                            item.valor = scaffoldingNumeroRelativo.aplicarSinal(base, sinal);
                            if (ocultarSinalPositivo
                                    && item.representaIncognitaOriginal()) {
                                item.registrarPreenchimentoPeloProtocoloMouseTexto();
                            }
                            String chavePapelSinal = obterChavePapelDoNumeroRelativo(numeroRelativoFinal);
                            RegistroAcaoEscolhaSinalPapelQuantitativo registroSinal =
                                    avaliarEscolhaSinalNumeroRelativo(
                                            chavePapelSinal, sinal, base);
                            if (registroSinal != null && registroSinal.foiErrada()) {
                                informarSuspeitaSinalIncorretoNumeroRelativo(item, numeroRelativoFinal, sinal);
                            }
                            if (registroSinal == null) {
                                registrarEscolhaSinalSemCriterio(base, sinal);
                            }
                            ajustarTamanhoDoItem(item);
                            centralizarItemNoNumeroRelativoSeNecessario(item);
                            // Pergunta de confirmação (nudge) se o valor divergir do
                            // curado; "Não" só adia a propagação às outras
                            // representações — o item mantém o valor digitado e o
                            // usuário pode reabrir o menu de sinal para tentar de
                            // novo (ver confirmarValorIncognitaAceito).
                            if (confirmarValorIncognitaAceito(item)) {
                                sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                                        numeroRelativoFinal,
                                        EstadoSemanticoCompartilhado.Origem.ARRASTE);
                            }
                            verificarConclusaoModelagem();
                            repaint();
                        }
                    }
            );
        }

        private void centralizarItemNoNumeroRelativoSeNecessario(ItemTextoArrastavel item) {
            ElementoVergnaud numeroRelativo = encontrarNumeroRelativoPorItem(item);
            apresentadorItemVergnaud.centralizarSeCentroEstiverContido(
                    item, numeroRelativo);
        }

        private void editarTextoElementoVergnaud(ElementoVergnaud elemento) {
            if (elemento != null && elementoEhPapelDaIncognita(elemento)
                    && encontrarIncognitaOriginalSobreElemento(elemento) == null) {
                informarPosicionamentoIncognitaAntesDaEdicao(elemento);
                return;
            }
            String texto = solicitarTextoEditavel(elemento.textoEditavel);
            if (texto != null) {
                registrarLogUsuario(
                        "Editar texto em elemento do diagrama",
                        "-",
                        "Caixa de texto editável",
                        descreverElementoVergnaudParaLog(elemento),
                        "Inserir ou modificar valor diretamente no modelo",
                        obterObjetoParaLog(elemento),
                        "A entrada de texto também deve manter a consistência entre as representações.",
                        "EDICAO_TEXTO_DIAGRAMA",
                        "valor=" + texto
                );
                registrarAcaoGranular("TEXTO", "Modificar texto em elemento do diagrama", "Caixa de texto editável",
                        descreverElementoVergnaudParaLog(elemento), "Inserir ou modificar texto", "valor=" + texto,
                        "Conteúdo textual do elemento modificado.");
                if (texto.matches("[+-]?[0-9]+([,.][0-9]+)?")) {
                    registrarAcaoGranular("QUANTIFICAR", "Informar valor numérico no diagrama", "Caixa de texto editável",
                            descreverElementoVergnaudParaLog(elemento), "Especificar valor numérico", "valor=" + texto,
                            "Valor numérico do elemento alterado.");
                }
                elemento.textoEditavel = scaffoldingNumeroRelativo.removerSinal(texto);
                elemento.preenchidoExplicitamentePeloUsuario =
                        elemento.textoEditavel != null
                        && elemento.textoEditavel.trim().length() > 0;
                if (ehElementoNumeroRelativo(elemento) && scaffoldingNumeroRelativo.ehNumeroOuInterrogacao(elemento.textoEditavel)) {
                    solicitarSinalNumeroRelativoParaTexto(elemento);
                } else {
                    atualizarRepresentacoesReativasAposAlteracaoDoElemento(elemento);
                }
                repaint();
            }
        }

        private void editarTextoCirculoVenn(CirculoVenn circulo) {
            if (ehCartaoValorRelativoComparacao(circulo)) {
                editarValorRelativoNoGraficoComparacao(circulo);
                return;
            }

            String texto = solicitarTextoEditavel(circulo.textoEditavel);
            if (texto != null) {
                registrarAcaoGranular("TEXTO", "Modificar texto do círculo", "Diagrama de Venn", "Círculo",
                        "Inserir ou modificar texto", "valor=" + texto, "Conteúdo textual do círculo modificado.");
                if (texto.matches("[+-]?[0-9]+([,.][0-9]+)?")) {
                    registrarAcaoGranular("QUANTIFICAR", "Informar valor numérico no círculo", "Diagrama de Venn", "Círculo",
                            "Especificar valor numérico", "valor=" + texto, "Valor numérico do círculo alterado.");
                }
                circulo.textoEditavel = texto;
                Integer valor = converterTextoParaInteiro(texto);
                if (valor != null) {
                    circulo.valorReferencia = valor.intValue();
                    int indiceAlterado = circulosVenn.indexOf(circulo);
                    sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(
                            indiceAlterado,
                            EstadoSemanticoCompartilhado.Origem.EDICAO_TEXTO);
                }
                repaint();
            }
        }

        private boolean ehCartaoValorRelativoComparacao(CirculoVenn circulo) {
            return ehGraficoBarrasComparacao()
                    && circulo != null
                    && circulosVenn.size() >= 3
                    && circulo == circulosVenn.get(2);
        }

        private void editarValorRelativoNoGraficoComparacao(CirculoVenn circulo) {
            int valorAtual = obterValorRelativoAssinadoComparacao();
            String texto = solicitarTextoEditavel(servicoQuantidadeContextual
                    .formatarNumeroRelativoParaDiagrama(
                            valorAtual, situacaoProblemaAtual));
            if (texto == null) {
                return;
            }

            Integer novoValor = converterTextoParaInteiro(texto);
            if (novoValor == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Informe um número inteiro válido.",
                        localizacao.texto("ui.dialog.editDiagramTitle"),
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            registrarAcaoGranular("QUANTIFICAR", "Editar valor relativo no gráfico de barras",
                    "Gráfico de comparação", "Cartão do valor relativo",
                    "Atualizar a relação e manter consistência entre representações",
                    "valor=" + novoValor, "Valor relativo atualizado bidirecionalmente.");

            circulo.valorReferencia = novoValor.intValue();
            aplicarEdicaoValorRelativoComparacao(novoValor.intValue());
        }

        private void aplicarEdicaoValorRelativoComparacao(int valorRelativo) {
            ElementoVergnaud referido = encontrarElementoVergnaudPorPapel(
                    "papel.referido");
            ElementoVergnaud relacao = encontrarElementoVergnaudPorPapel(
                    "papel.diferenca");
            ElementoVergnaud referendo = encontrarElementoVergnaudPorPapel(
                    "papel.referendo");
            if (referido == null || relacao == null || referendo == null) {
                return;
            }
            if (!valorRelativoPreservaQuantidadesNaoNegativas(relacao, valorRelativo)) {
                informarBloqueioQuantidadeNegativa();
                sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                        relacao, EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
                repaint();
                return;
            }
            definirValorNoElementoNumeroRelativo(relacao, valorRelativo);

            Integer valorReferido = obterValorNumericoDoElemento(referido);
            Integer valorReferendo = obterValorNumericoDoElemento(referendo);
            RecalculoComparacaoMedidas.Resultado resultadoRecalculo =
                    RecalculoComparacaoMedidas.decidir(
                            "papel.referido".equals(obterPapelIncognitaAtual()),
                            valorReferido, valorReferendo, valorRelativo);
            if (resultadoRecalculo.getPapel() == RecalculoComparacaoMedidas.PapelAlvo.REFERIDO) {
                definirValorNoElementoMedida(referido,
                        Integer.toString(resultadoRecalculo.getValor()));
            } else if (resultadoRecalculo.getPapel() == RecalculoComparacaoMedidas.PapelAlvo.REFERENDO) {
                definirValorNoElementoMedida(referendo,
                        Integer.toString(resultadoRecalculo.getValor()));
            }

            proporcaoControleComparacao = -1.0;
            ultimoValorInteiroControleComparacao = -1;
            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                    relacao, EstadoSemanticoCompartilhado.Origem.EDICAO_TEXTO);
            verificarConclusaoModelagem();
            repaint();
        }

        private void editarTextoQuadradinhoVenn(QuadradinhoVenn quadradinho) {
            String texto = solicitarTextoEditavel(quadradinho.textoEditavel);
            if (texto != null) {
                registrarAcaoGranular("TEXTO", "Modificar texto do quadrado", "Diagrama de Venn", "Quadrado",
                        "Inserir ou modificar texto", "valor=" + texto, "Conteúdo textual do quadrado modificado.");
                if (texto.matches("[+-]?[0-9]+([,.][0-9]+)?")) {
                    registrarAcaoGranular("QUANTIFICAR", "Informar valor numérico no quadrado", "Diagrama de Venn", "Quadrado",
                            "Especificar valor numérico", "valor=" + texto, "Valor numérico do quadrado alterado.");
                }
                quadradinho.textoEditavel = texto;
                repaint();
            }
        }

        private String solicitarTextoEditavel(String valorAtual) {
            JTextField campo = new JTextField(valorAtual == null ? "" : valorAtual, 22);
            int opcao = JOptionPane.showConfirmDialog(
                    this,
                    campo,
                    localizacao.texto("ui.dialog.editDiagramTitle"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcao != JOptionPane.OK_OPTION) {
                return null;
            }

            return campo.getText().trim();
        }

        private void editarNumeroNatural(ItemTextoArrastavel item) {
            while (true) {
                String entrada = solicitarNumeroInteiroParaInterrogacao(
                        SimboloDesconhecido.eh(item.valor) ? "" : item.valor
                );

                if (entrada == null) {
                    return;
                }

                entrada = entrada.trim();

                if (entrada.matches("[0-9]+")) {
                    ElementoVergnaud numeroRelativo = encontrarNumeroRelativoPorItem(item);
                    boolean preenchimentoDeInterrogacao = SimboloDesconhecido.eh(item.origemValor)
                            || SimboloDesconhecido.eh(scaffoldingNumeroRelativo.removerSinal(item.valor));
                    gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel identidadeAcao = null;
                    if (preenchimentoDeInterrogacao && numeroRelativo == null) {
                        String papelIncognita = obterPapelIncognitaAtual();
                        garantirTentativasIncognitaAtual(papelIncognita);
                        identidadeAcao = tentativasIncognitaAtual.iniciarAcaoInstrumental(
                                gerard.dominio.campoaditivo.OrigemAcao.ORIGEM_USUARIO);
                    }
                    // Correção rodada 3 (2026-07-31): a checagem de posição
                    // abaixo e a checagem de valor em
                    // confirmarValorIncognitaAceito (mais adiante, só no
                    // ramo de preenchimento de incógnita) são duas
                    // perguntas do MESMO gesto do usuário ("digitar e
                    // confirmar um número"). O gesto conserva gesture_id e
                    // todos os eventos da ação conservam o action_id emitido
                    // pelo proprietário semântico; são identidades distintas.
                    // A posição já foi validada quando o item foi
                    // solto/arrastado até aqui (evento SOLTURA_USUARIO
                    // anterior); reconferir agora é reavaliação de
                    // consistência é reativa: não constitui uma segunda
                    // ação instrumental nem um novo gesto canônico.
                    String ceIncognita = "-";
                    String regrasIncognita = "A ação não foi avaliada como acerto ou erro matemático.";
                    ResultadoQuestionamento resultadoIncognita = avaliarQuestionamentoPosicionamento(item,
                            gerard.pesquisador.auditoria.OrigemAvaliacao.REAVALIACAO_CONSISTENCIA);
                    if (resultadoIncognita.isAplicavel()) {
                        ceIncognita = resultadoIncognita.isCorreto() ? "C" : "E";
                        regrasIncognita = resultadoIncognita.isCorreto()
                                ? "O valor foi associado ao elemento do modelo que representa sua função no problema."
                                : "O valor deve ser associado ao elemento do modelo que representa sua função no problema.";
                    }
                    if (identidadeAcao == null) {
                        registrarLogUsuario(
                                "Substituir incógnita por número",
                                ceIncognita,
                                "Caixa de texto editável",
                                "Item arrastável no diagrama",
                                "Informar valor numérico para elemento previamente marcado como incógnita",
                                "OBJ8",
                                regrasIncognita,
                                "EDICAO_ITEM",
                                "valor=" + entrada
                        );
                        registrarAcaoGranular("TEXTO", "Substituir texto da incógnita", "Caixa de texto editável",
                                "Item arrastável", "Modificar texto", "valor=" + entrada, "Texto da incógnita modificado.");
                        registrarAcaoGranular("QUANTIFICAR", "Especificar valor da incógnita", "Caixa de texto editável",
                                "Item arrastável", "Especificar valor numérico", "valor=" + entrada, "Valor numérico informado.");
                    }
                    if (numeroRelativo != null) {
                        // Fluxo de sinal, não de confirmação de valor.
                        item.valor = scaffoldingNumeroRelativo.removerSinal(entrada);
                        ajustarTamanhoDoItem(item);
                        if (!deveBloquearMenuNumeroRelativoPorQuestionamento(item, numeroRelativo)) {
                            solicitarSinalNumeroRelativoParaItem(item, numeroRelativo, true, preenchimentoDeInterrogacao);
                        }
                    } else {
                        if (preenchimentoDeInterrogacao) {
                            item.registrarPreenchimentoPeloProtocoloMouseTexto();
                        }
                        item.valor = scaffoldingNumeroRelativo.removerSinalPositivo(entrada);
                        ajustarTamanhoDoItem(item);
                        if (preenchimentoDeInterrogacao
                                && !confirmarValorIncognitaAceito(item, identidadeAcao, true)) {
                            // Usuário respondeu "Não" à pergunta de confirmação:
                            // volta a pedir o valor em vez de propagar um valor
                            // que o próprio usuário disse não ter certeza.
                            repaint();
                            continue;
                        }
                        atualizarRepresentacoesReativasAposAlteracaoDoItem(item);
                    }
                    repaint();
                    return;
                }

            }
        }

        private String solicitarNumeroInteiroParaInterrogacao(String valorAtual) {
            final JDialog dialogo = new JDialog(
                    SwingUtilities.getWindowAncestor(this),
                    localizacao.texto("ui.dialog.insertValueTitle"),
                    Dialog.ModalityType.APPLICATION_MODAL
            );
            dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialogo.setResizable(false);

            JPanel conteudo = new JPanel(new BorderLayout(0, 12));
            conteudo.setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));

            JLabel instrucao = new JLabel(localizacao.texto("ui.dialog.replaceQuestion"));
            instrucao.setFont(instrucao.getFont().deriveFont(Font.BOLD));
            conteudo.add(instrucao, BorderLayout.NORTH);

            JPanel centro = new JPanel(new BorderLayout(0, 5));
            JTextField campo = new JTextField(valorAtual == null ? "" : valorAtual, 18);
            campo.getAccessibleContext().setAccessibleName(localizacao.texto("ui.dialog.insertValueTitle"));
            campo.getAccessibleContext().setAccessibleDescription(localizacao.texto("ui.dialog.replaceQuestion"));
            JLabel aviso = new JLabel(" ");
            aviso.setForeground(COR_ERRO);
            centro.add(campo, BorderLayout.NORTH);
            centro.add(aviso, BorderLayout.SOUTH);
            conteudo.add(centro, BorderLayout.CENTER);

            JButton cancelar = new JButton(localizacao.texto("analise.cancel"));
            JButton confirmar = new JButton(localizacao.texto("ui.dialog.confirm"));
            JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            botoes.add(cancelar);
            botoes.add(confirmar);
            conteudo.add(botoes, BorderLayout.SOUTH);

            final String[] resultado = new String[1];
            Runnable confirmarAcao = new Runnable() {
                public void run() {
                    String valor = campo.getText() == null ? "" : campo.getText().trim();
                    if (!valor.matches("[0-9]+")) {
                        aviso.setText(localizacao.texto("ui.dialog.invalidValue"));
                        campo.requestFocusInWindow();
                        campo.selectAll();
                        dialogo.pack();
                        return;
                    }
                    resultado[0] = valor;
                    dialogo.dispose();
                }
            };

            confirmar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    confirmarAcao.run();
                }
            });
            cancelar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialogo.dispose();
                }
            });
            campo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    confirmarAcao.run();
                }
            });

            dialogo.getRootPane().setDefaultButton(confirmar);
            dialogo.getRootPane().registerKeyboardAction(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            dialogo.dispose();
                        }
                    },
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            dialogo.setContentPane(conteudo);
            dialogo.pack();
            dialogo.setMinimumSize(new Dimension(390, dialogo.getHeight()));
            dialogo.setLocationRelativeTo(this);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    campo.requestFocusInWindow();
                    campo.selectAll();
                }
            });
            dialogo.setVisible(true);
            return resultado[0];
        }

        private void ajustarTamanhoDoItem(ItemTextoArrastavel item) {
            Font fonte = new Font("Arial", Font.BOLD, 20);
            FontMetrics fm = getFontMetrics(fonte);
            apresentadorItemVergnaud.atualizarEDimensionar(
                    item, item == null ? null : item.valor, fm);
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {
            mostrarAnotacaoMouseOver = false;
            agrupamentoAdicionarQuadradinhoFocado = null;
            agrupamentoRemoverQuadradinhoFocado = null;
            paineisEixosRelacoes.limparFocoBotaoEsconder();
            limparRealceAlvoProximidade();
            setCursor(Cursor.getDefaultCursor());
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
            mouseOverX = e.getX();
            mouseOverY = e.getY();
            forcarAnotacaoMouseOverAbaixo = false;
            boolean interacaoRepresentacoesLiberada =
                    interacaoRepresentacoesLiberadaPelaModelagem();
            if (interacaoRepresentacoesLiberada) {
                paineisEixosRelacoes.atualizarFocoBotaoEsconder(e.getX(), e.getY());
            } else {
                paineisEixosRelacoes.limparFocoBotaoEsconder();
                if (paineisEixosRelacoes.contemAlgumPainel(e.getX(), e.getY())) {
                    elementoTextoFocado = null;
                    itemFocado = null;
                    quadradinhoVennFocado = null;
                    mostrarAnotacaoMouseOver = true;
                    textoAnotacaoMouseOver = obterMensagemBloqueioInteracaoRepresentacoes();
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                    return;
                }
            }

            RepresentacaoComUnidadesRemoviveis representacaoRemover =
                    encontrarRepresentacaoPeloControleRemoverQuadradinho(
                            e.getX(), e.getY());
            boolean modelagemIniciada = adicaoDeUnidadesLiberadaPelaModelagem();
            boolean remocaoLiberada = representacaoRemover != null
                    && modelagemIniciada
                    && (ehAgrupamentoTransformacaoComSinal(
                            representacaoRemover.obterAgrupamento())
                            ? podeDecrementarValorAssinadoTransformacao(
                                    representacaoRemover.obterAgrupamento())
                            : representacaoRemover.podeRemoverUnidade());
            agrupamentoRemoverQuadradinhoFocado = remocaoLiberada
                    ? representacaoRemover.obterAgrupamento() : null;
            if (representacaoRemover != null) {
                agrupamentoAdicionarQuadradinhoFocado = null;
                elementoTextoFocado = null;
                itemFocado = null;
                quadradinhoVennFocado = null;
                mostrarAnotacaoMouseOver = true;
                boolean removerEhValorInteiro = ehAgrupamentoTransformacaoComSinal(
                        representacaoRemover.obterAgrupamento());
                textoAnotacaoMouseOver = !modelagemIniciada
                        ? obterMensagemBloqueioAdicaoUnidades()
                        : (remocaoLiberada
                                ? localizacao.texto(removerEhValorInteiro
                                        ? "ui.tooltip.venn.decreaseIntegerValue"
                                        : "ui.tooltip.venn.removeSquare")
                                : localizacao.texto(removerEhValorInteiro
                                        ? "ui.tooltip.venn.integerLimitReached"
                                        : "ui.tooltip.venn.minimumReached"));
                setCursor(remocaoLiberada
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
                repaint();
                return;
            }

            RepresentacaoComUnidadesAdicionaveis representacaoAdicionar =
                    encontrarRepresentacaoPeloControleAdicionarQuadradinho(
                            e.getX(), e.getY());
            boolean adicaoLiberada = representacaoAdicionar != null
                    && modelagemIniciada
                    && (ehAgrupamentoTransformacaoComSinal(
                            representacaoAdicionar.obterAgrupamento())
                            ? podeIncrementarValorAssinadoTransformacao(
                                    representacaoAdicionar.obterAgrupamento())
                            : representacaoAdicionar.podeAdicionarUnidade());
            agrupamentoAdicionarQuadradinhoFocado = adicaoLiberada
                    ? representacaoAdicionar.obterAgrupamento() : null;
            if (representacaoAdicionar != null) {
                elementoTextoFocado = null;
                itemFocado = null;
                quadradinhoVennFocado = null;
                mostrarAnotacaoMouseOver = true;
                boolean adicionarEhValorInteiro = ehAgrupamentoTransformacaoComSinal(
                        representacaoAdicionar.obterAgrupamento());
                textoAnotacaoMouseOver = !modelagemIniciada
                        ? obterMensagemBloqueioAdicaoUnidades()
                        : (adicaoLiberada
                                ? localizacao.texto(adicionarEhValorInteiro
                                        ? "ui.tooltip.venn.increaseIntegerValue"
                                        : "ui.tooltip.venn.addSquare")
                                : localizacao.texto(adicionarEhValorInteiro
                                        ? "ui.tooltip.venn.integerLimitReached"
                                        : "ui.tooltip.venn.semanticLimitReached"));
                setCursor(adicaoLiberada
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
                repaint();
                return;
            }

            if (paineisEixosRelacoes.contemLupa(e.getX(), e.getY())) {
                elementoTextoFocado = null;
                itemFocado = null;
                mostrarAnotacaoMouseOver = true;
                textoAnotacaoMouseOver = paineisEixosRelacoes.obterDicaLupa();
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
                return;
            }

            if (paineisEixosRelacoes.contemBotaoEsconder(e.getX(), e.getY())) {
                elementoTextoFocado = null;
                itemFocado = null;
                mostrarAnotacaoMouseOver = true;
                textoAnotacaoMouseOver = paineisEixosRelacoes.obterDicaBotaoEsconder();
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
                return;
            }

            if (paineisEixosRelacoes.contemPontoControle(e.getX(), e.getY())) {
                elementoTextoFocado = null;
                itemFocado = null;
                mostrarAnotacaoMouseOver = true;
                textoAnotacaoMouseOver = paineisEixosRelacoes.obterDicaPontoControle();
                definirCursorMaoAberta();
                repaint();
                return;
            }

            if (ehGraficoBarrasComparacao() && (contemPontoControleComparacao(e.getX(), e.getY()) || contemEscalaComparacao(e.getX(), e.getY()))) {
                elementoTextoFocado = null;
                itemFocado = null;
                mostrarAnotacaoMouseOver = true;
                if (!interacaoRepresentacoesLiberada) {
                    textoAnotacaoMouseOver = obterMensagemBloqueioInteracaoRepresentacoes();
                    setCursor(Cursor.getDefaultCursor());
                } else {
                    textoAnotacaoMouseOver = localizacao.texto("ui.tooltip.integerAxisBluePoint");
                    definirCursorMaoAberta();
                }
                repaint();
                return;
            }

            ElementoTextoMovel candidatoTexto =
                    encontrarElementoTextoMovel(e.getX(), e.getY());
            ElementoTextoMovel elementoTexto =
                    handlerElementoTextoMovel.identificarFoco(
                            candidatoTexto,
                            candidatoTexto != null
                                    && estaNaAreaDoTexto(
                                            e.getX(), e.getY()));

            if (elementoTexto != null) {
                elementoTextoFocado = elementoTexto;
                mostrarAnotacaoMouseOver = true;

                if (ehNumeroOuInterrogacaoDoTexto(elementoTexto)) {
                    textoAnotacaoMouseOver = criarMensagemPapelElementoTexto(elementoTexto);
                } else {
                    textoAnotacaoMouseOver = localizacao.texto("ui.tooltip.textMoveOnly");
                }

                definirCursorMaoAberta();
            } else {
                elementoTextoFocado = null;

                {
                    QuadradinhoVenn quadradinho = encontrarQuadradinhoVenn(e.getX(), e.getY());
                    if (quadradinho != null) {
                        itemFocado = null;
                        mostrarAnotacaoMouseOver = true;
                        if (!interacaoRepresentacoesLiberada) {
                            quadradinhoVennFocado = null;
                            textoAnotacaoMouseOver = obterMensagemBloqueioInteracaoRepresentacoes();
                            setCursor(Cursor.getDefaultCursor());
                        } else {
                            quadradinhoVennFocado = quadradinho;
                            textoAnotacaoMouseOver = localizacao.texto(ehDiagramaVennComposicaoMedidas() ? "ui.tooltip.collectionSquareDrag" : (ehGraficoBarrasComparacao() ? "ui.tooltip.comparisonBarDrag" : "ui.tooltip.vennSquareDrag"));
                            definirCursorMaoAberta();
                        }
                    } else {
                        quadradinhoVennFocado = null;
                        ItemTextoArrastavel item =
                                handlerItemTextoArrastavel.identificarFoco(
                                        encontrarItemArrastavel(e.getX(), e.getY()));
                        if (item != null) {
                            itemFocado = item;
                            mostrarAnotacaoMouseOver = true;
                            if (mostrarQuestionamentoPersistente && item == itemQuestionadoPersistente) {
                                textoAnotacaoMouseOver = textoQuestionamentoPersistente;
                            } else {
                                textoAnotacaoMouseOver = criarMensagemPapelItemArrastavel(item);
                            }
                            definirCursorMaoAberta();
                        } else if (encontrarConectorVergnaud(e.getX(), e.getY()) != null) {
                        mostrarAnotacaoMouseOver = false;
                        textoAnotacaoMouseOver = "";
                        definirCursorMaoAberta();
                        } else {
                            mostrarAnotacaoMouseOver = false;
                            textoAnotacaoMouseOver = "";
                            setCursor(Cursor.getDefaultCursor());
                        }
                    }
                }
            }

            repaint();
        }


        private void atualizarRealceAlvoProximidade(ItemTextoArrastavel item) {
            limparRealceAlvoProximidade();

            if (item == null || !item.estaNoDiagrama()) {
                return;
            }

            ElementoVergnaud alvo = obterAlvoCorretoParaItem(item);
            if (alvo == null) {
                return;
            }

            boolean proximo = itemEstaProximoDoElemento(item, alvo);
            boolean dentro = centroDoItemDentroDoElemento(item, alvo);

            EstadoRealceAlvo estado = scaffoldingProximidade.calcularEstadoAlvo(modoFeedbackTeste, proximo, dentro);
            if (estado != EstadoRealceAlvo.NENHUM) {
                realcarAlvo(alvo, estado);
            }
            if (scaffoldingProximidade.deveAplicarAtracaoMagnetica(modoFeedbackTeste, proximo)) {
                aplicarAtracaoMagnetica(item, alvo);
            }
        }

        private void realcarAlvo(ElementoVergnaud alvo, EstadoRealceAlvo estado) {
            alvo.estadoRealce = estado;
            alvoRealcadoPorProximidade = alvo;
        }

        private void limparRealceAlvoProximidade() {
            if (alvoRealcadoPorProximidade != null) {
                alvoRealcadoPorProximidade.estadoRealce = EstadoRealceAlvo.NENHUM;
                alvoRealcadoPorProximidade = null;
            }
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                elementosVergnaud.get(i).estadoRealce = EstadoRealceAlvo.NENHUM;
            }
        }

        private ElementoVergnaud obterAlvoCorretoParaItem(ItemTextoArrastavel item) {
            String chavePapel = obterChavePapelExataDoItem(item);
            int indice = obterIndiceElementoVergnaudPorPapel(chavePapel);

            if (indice >= 0 && indice < elementosVergnaud.size()) {
                return elementosVergnaud.get(indice);
            }

            return null;
        }

        private String obterChavePapelExataDoItem(ItemTextoArrastavel item) {
            if (item == null) {
                return "papel.valor";
            }

            boolean incognita = SimboloDesconhecido.eh(item.origemValor)
                    || SimboloDesconhecido.eh(item.valor);
            if (incognita) {
                String chaveIncognita = ResolvedorPapelInterpretado.aplicarFallbackCuradoItemDesconhecido(
                        resultadoInterpretacao, item.chavePapel == null ? "papel.valor" : item.chavePapel);
                if (chavePapelEspecifica(chaveIncognita)) {
                    return chaveIncognita;
                }
                String chavePorInterrogacao = ResolvedorPapelInterpretado.obterChavePapelExataPorValor(resultadoInterpretacao, "?");
                if (chavePapelEspecifica(chavePorInterrogacao)) {
                    return chavePorInterrogacao;
                }
            }

            if (chavePapelEspecifica(item.chavePapel)) {
                return item.chavePapel;
            }

            String chavePorValor = ResolvedorPapelInterpretado.obterChavePapelExataPorValor(resultadoInterpretacao, item.origemValor);
            if (chavePapelEspecifica(chavePorValor)) {
                return chavePorValor;
            }

            return item.chavePapel != null ? item.chavePapel : "papel.valor";
        }

        private boolean chavePapelEspecifica(String chavePapel) {
            return catalogoPapeisSemanticos.chavePapelEspecifica(chavePapel);
        }

        private int obterIndiceElementoVergnaudPorPapel(String chavePapel) {
            return catalogoPapeisSemanticos.obterIndiceElementoPorPapel(
                    chavePapel, tipoSituacaoSelecionada);
        }

        private boolean centroDoItemDentroDoElemento(ItemTextoArrastavel item, ElementoVergnaud elemento) {
            int centroItemX = item.x + item.largura / 2;
            int centroItemY = item.y + item.altura / 2;
            return elemento.contem(centroItemX, centroItemY);
        }

        private void aplicarAtracaoMagnetica(ItemTextoArrastavel item, ElementoVergnaud alvo) {
            int destinoX = alvo.x + (alvo.largura - item.largura) / 2;
            int destinoY = alvo.y + (alvo.altura - item.altura) / 2;
            item.x = item.x + Math.round((destinoX - item.x) * 0.35f);
            item.y = item.y + Math.round((destinoY - item.y) * 0.35f);
        }

        private void centralizarItemNoElemento(ItemTextoArrastavel item, ElementoVergnaud alvo) {
            apresentadorItemVergnaud.centralizar(item, alvo);
        }

        private boolean itemEstaProximoDoElemento(ItemTextoArrastavel item, ElementoVergnaud elemento) {
            int centroItemX = item.x + item.largura / 2;
            int centroItemY = item.y + item.altura / 2;

            if (elemento.contem(centroItemX, centroItemY)) {
                return true;
            }

            double distancia = distanciaPontoRetangulo(
                    centroItemX,
                    centroItemY,
                    elemento.x,
                    elemento.y,
                    elemento.largura,
                    elemento.altura
            );

            return distancia <= DISTANCIA_REALCE_ALVO;
        }

        private double distanciaPontoRetangulo(int px, int py, int x, int y, int largura, int altura) {
            int dx = 0;
            if (px < x) {
                dx = x - px;
            } else if (px > x + largura) {
                dx = px - (x + largura);
            }

            int dy = 0;
            if (py < y) {
                dy = y - py;
            } else if (py > y + altura) {
                dy = py - (y + altura);
            }

            return Math.sqrt(dx * dx + dy * dy);
        }

        private String criarMensagemPapelElementoTexto(ElementoTextoMovel elemento) {
            String chavePapel = obterChavePapelCanonicoDoElemento(elemento);
            chavePapel = ResolvedorPapelInterpretado.aplicarFallbackCuradoItemDesconhecido(resultadoInterpretacao, chavePapel);
            String papel = localizacao.texto(chavePapel);
            return localizacao.formatar("ui.hover.role", papel);
        }

        private String criarMensagemPapelItemArrastavel(ItemTextoArrastavel item) {
            String chavePapel = item.chavePapel;

            if (chavePapel == null || chavePapel.length() == 0) {
                chavePapel = ResolvedorPapelInterpretado.obterChavePapelCanonicoPorValor(resultadoInterpretacao, item.origemValor);
            } else {
                chavePapel = ResolvedorPapelInterpretado.converterParaPapelCanonico(chavePapel);
            }

            chavePapel = ResolvedorPapelInterpretado.aplicarFallbackCuradoItemDesconhecido(resultadoInterpretacao, chavePapel);
            String papel = localizacao.texto(chavePapel);
            return localizacao.formatar("ui.hover.role", papel);
        }

        /**
         * A interrogação representa o item desconhecido, mas não constitui um
         * papel semântico próprio. Quando a associação direta resultar em
         * "papel.valor", recupera-se da curadoria o papel efetivamente ocupado
         * pela incógnita (estado inicial, transformação, estado final, parte 1,
         * parte 2, referido, referendo, valor relativo ou todo).
         *
         * Lógica movida em 2026-09-01 para
         * {@link gerard.interpretacao.modelo.ResolvedorPapelInterpretado}
         * (não dependia de Swing nem de outro estado de instância além de
         * {@code resultadoInterpretacao}) — ver
         * LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md. Este método
         * preserva o nome e a assinatura para todos os pontos de chamada
         * existentes.
         */
        private String obterChavePapelExataDoElemento(ElementoTextoMovel elemento) {
            if (elemento != null && elemento.possuiVinculoSemantico()
                    && elemento.chavePapelSemantico != null) {
                return elemento.chavePapelSemantico;
            }
            if (ehInterrogacaoDoTexto(elemento)) {
                return ResolvedorPapelInterpretado.obterChavePapelExataPorValor(resultadoInterpretacao, "?");
            }
            int indice = obterIndiceSimboloArrastavel(elemento);
            return ResolvedorPapelInterpretado.obterChavePapelExataPorIndice(resultadoInterpretacao, indice);
        }

        private String obterChavePapelCanonicoDoElemento(ElementoTextoMovel elemento) {
            if (elemento != null && elemento.possuiVinculoSemantico()
                    && elemento.chavePapelSemantico != null) {
                return ResolvedorPapelInterpretado.converterParaPapelCanonico(elemento.chavePapelSemantico);
            }
            if (ehInterrogacaoDoTexto(elemento)) {
                return ResolvedorPapelInterpretado.converterParaPapelCanonico(
                        ResolvedorPapelInterpretado.obterChavePapelExataPorValor(resultadoInterpretacao, "?"));
            }
            int indice = obterIndiceSimboloArrastavel(elemento);
            return ResolvedorPapelInterpretado.obterChavePapelCanonicoPorIndice(resultadoInterpretacao, indice);
        }

        private int obterIndiceSimboloArrastavel(ElementoTextoMovel alvo) {
            int indice = 0;

            for (int i = 0; i < elementosTexto.size(); i++) {
                ElementoTextoMovel elemento = elementosTexto.get(i);

                if (ehNumeroOuInterrogacaoDoTexto(elemento)) {
                    if (elemento == alvo) {
                        return indice;
                    }
                    indice++;
                }
            }

            return -1;
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (quadradinhoVennFocado != null) {
                    int indiceAlterado = -1;
                    for (int i = 0; i < circulosVenn.size(); i++) {
                        if (circulosVenn.get(i).contem(
                                quadradinhoVennFocado.centroX(),
                                quadradinhoVennFocado.centroY())) {
                            indiceAlterado = i;
                            break;
                        }
                    }
                    quadradinhosVenn.remove(quadradinhoVennFocado);
                    quadradinhoVennFocado = null;
                    handlerQuadradinhoVenn.cancelar();
                    mostrarAnotacaoMouseOver = false;
                    sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(
                            indiceAlterado,
                            EstadoSemanticoCompartilhado.Origem.EXCLUSAO);
                    repaint();
                    return;
                }

                if (itemFocado != null) {
                    if (itemFocado == itemQuestionadoPersistente) {
                        limparQuestionamentoPersistente();
                    }
                    if (itemFocado == itemIncognitaEstadoFinal) {
                        desabilitarSincronizacaoEstadoFinal();
                    }
                    ItemTextoArrastavel itemRemovido = itemFocado;
                    itensArrastaveis.remove(itemFocado);
                    itemFocado = null;
                    handlerItemTextoArrastavel.cancelar();
                    mostrarAnotacaoMouseOver = false;
                    atualizarRepresentacoesReativasAposAlteracaoDoItem(itemRemovido);
                    verificarConclusaoModelagem();
                    repaint();
                }
            }
        }

        public void keyReleased(KeyEvent e) {}

        public void keyTyped(KeyEvent e) {}

        private void abrirTelaComparacaoCategorias() {
            Window proprietaria = SwingUtilities.getWindowAncestor(this);
            final JDialog dialogo = new JDialog(
                    proprietaria instanceof Frame ? (Frame) proprietaria : null,
                    localizacao.texto("ui.compare.title"),
                    Dialog.ModalityType.MODELESS
            );
            dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialogo.setContentPane(new PainelComparacaoCategorias());
            DimensionadorJanelaComparacaoCategorias.aplicar(
                    dialogo, proprietaria);
            dialogo.setVisible(true);
        }

        private final class PainelComparacaoCategorias extends JPanel {
            // Terceiro tom de cinza (mais claro que COR_TEXTO/COR_TEXTO_SECUNDARIO,
            // ainda legível) usado só para distinguir o "total" de parcela1/parcela2
            // no rastreio de valores entre colunas — ver comentário acima de total.setForeground.
            final Color COR_CINZA_TOTAL_COMPARACAO = new Color(150, 142, 128);
            final EstadoNumericoComparacaoCategorias modelo =
                    new EstadoNumericoComparacaoCategorias(4, 7);
            final JSpinner spinnerA = new JSpinner(new SpinnerNumberModel(4, 0, 999, 1));
            final JSpinner spinnerB = new JSpinner(new SpinnerNumberModel(7, 0, 999, 1));
            final JPanel grade = new JPanel(new GridBagLayout());
            boolean atualizandoControles;

            PainelComparacaoCategorias() {
                super(new BorderLayout(8, 8));
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                setBackground(COR_FUNDO);

                JPanel topo = new JPanel(new BorderLayout(12, 6));
                topo.setOpaque(false);

                JPanel destaque = new JPanel();
                destaque.setLayout(new BoxLayout(destaque, BoxLayout.Y_AXIS));
                destaque.setOpaque(false);
                destaque.setBorder(new gerard.ui.cartao.BordaCartaoGerard(
                        COR_DESTAQUE, COR_PRIMARIA));

                JLabel tituloDestaque = new JLabel(localizacao.texto("ui.compare.title"), SwingConstants.CENTER);
                tituloDestaque.setFont(new Font("Arial", Font.BOLD, 18));
                tituloDestaque.setForeground(COR_PRIMARIA);
                tituloDestaque.setAlignmentX(Component.CENTER_ALIGNMENT);
                destaque.add(tituloDestaque);

                final JLabel formulaDestaque = new JLabel("4 + 7 = 11", SwingConstants.CENTER);
                formulaDestaque.setFont(new Font("Arial", Font.BOLD, 20));
                formulaDestaque.setAlignmentX(Component.CENTER_ALIGNMENT);
                destaque.add(formulaDestaque);

                destaque.add(Box.createVerticalStrut(4));
                JLabel explicacao = new JLabel("<html><div style='text-align:center; width:720px;'>" + localizacao.texto("ui.compare.explanation") + "</div></html>", SwingConstants.CENTER);
                explicacao.setFont(new Font("Arial", Font.BOLD, 18));
                explicacao.setForeground(COR_TEXTO);
                explicacao.setBorder(BorderFactory.createEmptyBorder(6, 6, 2, 6));
                explicacao.setAlignmentX(Component.CENTER_ALIGNMENT);
                destaque.add(explicacao);
                topo.add(destaque, BorderLayout.CENTER);

                JPanel valores = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
                valores.setOpaque(false);
                JLabel rotuloValoresComuns = new JLabel(localizacao.texto("ui.compare.commonValues"));
                rotuloValoresComuns.setFont(new Font("Arial", Font.BOLD, 13));
                valores.add(rotuloValoresComuns);
                spinnerA.setFont(new Font("Arial", Font.BOLD, 14));
                spinnerB.setFont(new Font("Arial", Font.BOLD, 14));
                ((JSpinner.DefaultEditor) spinnerA.getEditor()).getTextField().setForeground(COR_TEXTO);
                ((JSpinner.DefaultEditor) spinnerB.getEditor()).getTextField().setForeground(COR_TEXTO_SECUNDARIO);
                valores.add(spinnerA);
                valores.add(new JLabel("+"));
                valores.add(spinnerB);
                valores.add(new JLabel("="));
                final JLabel total = new JLabel(String.valueOf(modelo.getTotal()));
                total.setFont(new Font("Arial", Font.BOLD, 14));
                // Tons de cinza, não cores: o rastreio de valores entre colunas
                // não deve competir com o azul de COR_SUCESSO nem introduzir
                // outras cores de significado — só a variação de tom distingue
                // parcela1/parcela2/total.
                total.setForeground(COR_CINZA_TOTAL_COMPARACAO);
                valores.add(total);
                topo.add(valores, BorderLayout.EAST);
                add(topo, BorderLayout.NORTH);

                grade.setBackground(Color.WHITE);
                montarGrade();
                JScrollPane rolagem = new JScrollPane(grade,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                rolagem.getVerticalScrollBar().setUnitIncrement(18);
                add(rolagem, BorderLayout.CENTER);

                ChangeListener listener = new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        if (atualizandoControles) return;
                        modelo.definirParcelas(((Number) spinnerA.getValue()).intValue(),
                                ((Number) spinnerB.getValue()).intValue());
                        total.setText(String.valueOf(modelo.getTotal()));
                        formulaDestaque.setText(modelo.getPrimeiraParcela() + " + " + modelo.getSegundaParcela() + " = " + modelo.getTotal());
                        registrarAcaoComparacao("ALTERAR_VALORES_COMUNS", "a=" + modelo.getPrimeiraParcela() + ";b=" + modelo.getSegundaParcela() + ";total=" + modelo.getTotal());
                    }
                };
                spinnerA.addChangeListener(listener);
                spinnerB.addChangeListener(listener);
                modelo.adicionarOuvinte(new Runnable() {
                    public void run() {
                        atualizandoControles = true;
                        try {
                            spinnerA.setValue(modelo.getPrimeiraParcela());
                            spinnerB.setValue(modelo.getSegundaParcela());
                            total.setText(String.valueOf(modelo.getTotal()));
                            formulaDestaque.setText(modelo.getPrimeiraParcela() + " + " + modelo.getSegundaParcela() + " = " + modelo.getTotal());
                            grade.repaint();
                            atualizarTextosSituacoes();
                        } finally {
                            atualizandoControles = false;
                        }
                    }
                });
            }

            final java.util.List<JLabel> rotulosSituacao = new ArrayList<JLabel>();
            final TipoSituacaoAditiva[] categorias = new TipoSituacaoAditiva[] {
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS
            };

            void montarGrade() {
                grade.removeAll();
                rotulosSituacao.clear();
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.BOTH;
                c.weighty = 0;
                c.insets = new Insets(0, 0, 0, 0);
                String[] cab = new String[] {
                        localizacao.texto("ui.compare.col.problem"),
                        localizacao.texto("ui.compare.col.solution"),
                        localizacao.texto("ui.compare.col.problemRepresentation"),
                        localizacao.texto("ui.compare.col.categoryRepresentation")
                };
                double[] pesos = new double[] {0.35, 0.11, 0.27, 0.27};
                for (int col = 0; col < 4; col++) {
                    c.gridx = col; c.gridy = 0; c.weightx = pesos[col];
                    JLabel h = new JLabel("<html><center>" + cab[col] + "</center></html>", SwingConstants.CENTER);
                    h.setFont(new Font("Arial", Font.BOLD, 13));
                    h.setOpaque(true); h.setBackground(gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE);
                    h.setBorder(BorderFactory.createMatteBorder(1, 1, 1, col == 3 ? 1 : 0, Color.DARK_GRAY));
                    h.setPreferredSize(new Dimension(100, 44));
                    grade.add(h, c);
                }
                for (int linha = 0; linha < categorias.length; linha++) {
                    TipoSituacaoAditiva categoria = categorias[linha];
                    c.gridy = linha + 1; c.weighty = 1.0;
                    c.gridx = 0; c.weightx = pesos[0];
                    JLabel situacao = criarRotuloSituacao(categoria, linha);
                    rotulosSituacao.add(situacao);
                    grade.add(situacao, c);

                    c.gridx = 1; c.weightx = pesos[1];
                    grade.add(new PainelSolucaoNumerica(categoria), c);

                    c.gridx = 2; c.weightx = pesos[2];
                    grade.add(new MiniRepresentacao(categoria, false), c);

                    c.gridx = 3; c.weightx = pesos[3];
                    grade.add(new MiniRepresentacao(categoria, true), c);
                }
                grade.revalidate();
            }

            JLabel criarRotuloSituacao(TipoSituacaoAditiva categoria, int linha) {
                final JLabel l = new JLabel(textoSituacao(categoria, linha));
                l.setVerticalAlignment(SwingConstants.TOP);
                l.setFont(new Font("Arial", Font.PLAIN, 17));
                l.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.DARK_GRAY));
                l.setOpaque(true); l.setBackground(Color.WHITE);
                l.setPreferredSize(new Dimension(350, 170));
                // Reajusta a fonte ao tamanho real da célula (a linha cresce com
                // GridBagLayout ao redimensionar o diálogo — ver
                // DimensionadorJanelaComparacaoCategorias).
                l.addComponentListener(new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        int tamanho = Math.max(14, Math.min(24, l.getHeight() / 9));
                        Font atual = l.getFont();
                        if (atual.getSize() != tamanho) {
                            l.setFont(atual.deriveFont((float) tamanho));
                        }
                    }
                });
                return l;
            }

            void atualizarTextosSituacoes() {
                for (int i = 0; i < rotulosSituacao.size(); i++) {
                    rotulosSituacao.get(i).setText(textoSituacao(categorias[i], i));
                }
            }

            String textoSituacao(TipoSituacaoAditiva categoria, int linha) {
                String romano = linha == 0 ? "I" : linha == 1 ? "II" : "III";
                String titulo = localizacao.descricaoTipo(categoria);
                String chave = categoria == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                        ? "ui.compare.problem.composition"
                        : categoria == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                        ? "ui.compare.problem.transformation"
                        : "ui.compare.problem.comparison";
                String valorA = "<span style='color:#332E28;font-weight:bold'>" + modelo.getPrimeiraParcela() + "</span>";
                String valorB = "<span style='color:#746E62;font-weight:bold'>" + modelo.getSegundaParcela() + "</span>";
                String valorTotal = "<span style='color:#968E80;font-weight:bold'>" + modelo.getTotal() + "</span>";
                return "<html><div style='padding:5px 7px'><b>" + romano + ". " + titulo + "</b><br><br>"
                        + localizacao.formatar(chave, valorA, valorB, valorTotal)
                        + "</div></html>";
            }

            void registrarAcaoComparacao(String evento, String detalhes) {
                registrarLogUsuario(
                        "Comparar estruturas com a mesma solução numérica",
                        "-",
                        "Tela Comparação entre categorias",
                        evento,
                        "Manter consistência entre solução e representações",
                        "OBJ8",
                        "O sujeito interage com representações equivalentes numericamente e distintas semanticamente.",
                        evento,
                        "modoAtividade=COMPARACAO_CATEGORIAS;" + detalhes
                );
            }

            final class PainelSolucaoNumerica extends JPanel {
                final TipoSituacaoAditiva categoria;
                PainelSolucaoNumerica(TipoSituacaoAditiva categoria) {
                    this.categoria = categoria;
                    setBackground(Color.WHITE);
                    setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.DARK_GRAY));
                    setPreferredSize(new Dimension(105, 170));
                    modelo.adicionarOuvinte(new Runnable() { public void run() { repaint(); } });
                }
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    int tamanhoFonte = Math.max(14, Math.min(28, getHeight() / 8));
                    g2.setFont(new Font("Arial", Font.BOLD, tamanhoFonte));
                    String a = String.valueOf(modelo.getPrimeiraParcela());
                    String op1 = " + ";
                    String b = String.valueOf(modelo.getSegundaParcela());
                    String op2 = " = ";
                    String t = String.valueOf(modelo.getTotal());
                    FontMetrics fm = g2.getFontMetrics();
                    int largura = fm.stringWidth(a + op1 + b + op2 + t);
                    int x = Math.max(6, (getWidth() - largura) / 2);
                    int y = Math.max(30, getHeight() / 2);
                    g2.setColor(COR_TEXTO); g2.drawString(a, x, y); x += fm.stringWidth(a);
                    g2.setColor(Color.DARK_GRAY); g2.drawString(op1, x, y); x += fm.stringWidth(op1);
                    g2.setColor(COR_TEXTO_SECUNDARIO); g2.drawString(b, x, y); x += fm.stringWidth(b);
                    g2.setColor(Color.DARK_GRAY); g2.drawString(op2, x, y); x += fm.stringWidth(op2);
                    g2.setColor(COR_CINZA_TOTAL_COMPARACAO); g2.drawString(t, x, y);
                    g2.dispose();
                }
            }

            final class MiniRepresentacao extends JPanel implements MouseListener {
                final TipoSituacaoAditiva categoria;
                final boolean formal;
                final java.util.List<Rectangle> alvos = new ArrayList<Rectangle>();
                MiniRepresentacao(TipoSituacaoAditiva categoria, boolean formal) {
                    this.categoria = categoria; this.formal = formal;
                    setBackground(Color.WHITE);
                    setBorder(BorderFactory.createMatteBorder(0, 1, 1, formal ? 1 : 0, Color.DARK_GRAY));
                    setPreferredSize(new Dimension(265, 170));
                    if (formal) {
                        setToolTipText(null);
                    } else {
                        setToolTipText(localizacao.texto("ui.compare.interactionHint"));
                        addMouseListener(this);
                        modelo.adicionarOuvinte(new Runnable() { public void run() { repaint(); } });
                    }
                    revalidate();
                    repaint();
                }

                /** Tamanho das caixas/círculos proporcional ao espaço real do painel. */
                int tamanhoCaixa() {
                    return Math.max(30, Math.min(70, Math.min(getWidth(), getHeight()) / 4));
                }

                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.setFont(new Font("Arial", Font.BOLD, Math.max(13, Math.min(26, tamanhoCaixa() / 3))));
                    alvos.clear();
                    // A representação da situação-problema é uma instância preenchida e interativa.
                    // A representação da categoria é uma referência estrutural: vazia, estática
                    // e sem participação na sincronização ou no log de modelagem.
                    if (formal) {
                        if (categoria == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) desenharComposicaoVazia(g2);
                        else if (categoria == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) desenharTransformacaoVazia(g2);
                        else desenharComparacaoVazia(g2);
                    } else {
                        if (categoria == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) desenharComposicao(g2);
                        else if (categoria == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) desenharTransformacao(g2);
                        else desenharComparacao(g2);
                    }
                    g2.dispose();
                }

                void desenharValor(Graphics2D g2, int x, int y, int valor, boolean circulo) {
                    int tamanho = tamanhoCaixa();
                    Rectangle r = new Rectangle(x - tamanho / 2, y - tamanho / 2, tamanho, tamanho); alvos.add(r);
                    g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
                    if (circulo) g2.fillOval(r.x,r.y,r.width,r.height); else g2.fillRect(r.x,r.y,r.width,r.height);
                    g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
                    if (circulo) g2.drawOval(r.x,r.y,r.width,r.height); else g2.drawRect(r.x,r.y,r.width,r.height);
                    String t=String.valueOf(valor); FontMetrics fm=g2.getFontMetrics();
                    g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO);
                    g2.drawString(t, x-fm.stringWidth(t)/2, y+fm.getAscent()/2-2);
                }


                void desenharFormaVazia(Graphics2D g2, int x, int y, boolean circulo) {
                    int tamanho = tamanhoCaixa();
                    g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
                    if (circulo) g2.fillOval(x - tamanho/2, y - tamanho/2, tamanho, tamanho);
                    else g2.fillRect(x - tamanho/2, y - tamanho/2, tamanho, tamanho);
                    g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
                    if (circulo) g2.drawOval(x - tamanho/2, y - tamanho/2, tamanho, tamanho);
                    else g2.drawRect(x - tamanho/2, y - tamanho/2, tamanho, tamanho);
                }

                /** Fator de escala dos pequenos deslocamentos (folgas, pontas de seta)
                 *  ao redor das caixas — mantém a mesma proporção visual de quando
                 *  a caixa tinha 44px fixos, agora que tamanhoCaixa() varia. */
                double escalaTraco() {
                    return tamanhoCaixa() / 44.0;
                }

                void desenharComposicaoVazia(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int x1=w/4, x2=w/4, xt=3*w/4;
                    desenharFormaVazia(g2,x1,h/3,false);
                    desenharFormaVazia(g2,x2,2*h/3,false);
                    desenharFormaVazia(g2,xt,h/2,false);
                    double r = escalaTraco();
                    int bx=w/2-(int)Math.round(12*r);
                    g2.drawArc(bx,h/3-(int)Math.round(18*r),(int)Math.round(30*r),h/3+(int)Math.round(36*r),270,180);
                }

                void desenharTransformacaoVazia(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int y=2*h/3;
                    desenharFormaVazia(g2,w/5,y,false);
                    desenharFormaVazia(g2,w/2,h/3,true);
                    desenharFormaVazia(g2,4*w/5,y,false);
                    double r = escalaTraco();
                    int gap=(int)Math.round(25*r), ponta=(int)Math.round(35*r), asa=(int)Math.round(6*r);
                    g2.drawLine(w/5+gap,y,4*w/5-gap,y);
                    g2.drawLine(4*w/5-gap,y,4*w/5-ponta,y-asa); g2.drawLine(4*w/5-gap,y,4*w/5-ponta,y+asa);
                }

                void desenharComparacaoVazia(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int x=w/2;
                    desenharFormaVazia(g2,x,h/5,false);
                    desenharFormaVazia(g2,x,h*4/5,false);
                    desenharFormaVazia(g2,x+w/4,h/2,true);
                    double r = escalaTraco();
                    int gap=(int)Math.round(24*r), ponta=(int)Math.round(34*r), asa=(int)Math.round(6*r);
                    g2.drawLine(x,h/5+gap,x,h*4/5-gap);
                    // Seta sai do quadrado de baixo para o de cima (2026-08-07,
                    // mesma correção do ícone de atalho em criarIconeCategoriaComparacao).
                    g2.drawLine(x,h/5+gap,x-asa,h/5+ponta); g2.drawLine(x,h/5+gap,x+asa,h/5+ponta);
                }

                void desenharComposicao(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int x1=w/4, x2=w/4, xt=3*w/4;
                    desenharValor(g2,x1,h/3,modelo.getPrimeiraParcela(),false);
                    desenharValor(g2,x2,2*h/3,modelo.getSegundaParcela(),false);
                    desenharValor(g2,xt,h/2,modelo.getTotal(),false);
                    double r = escalaTraco();
                    int bx=w/2-(int)Math.round(12*r);
                    g2.drawArc(bx,h/3-(int)Math.round(18*r),(int)Math.round(30*r),h/3+(int)Math.round(36*r),270,180);
                }

                void desenharTransformacao(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int y=2*h/3;
                    desenharValor(g2,w/5,y,modelo.getPrimeiraParcela(),false);
                    desenharValor(g2,w/2,h/3,modelo.getSegundaParcela(),true);
                    desenharValor(g2,4*w/5,y,modelo.getTotal(),false);
                    double r = escalaTraco();
                    int gap=(int)Math.round(25*r), ponta=(int)Math.round(35*r), asa=(int)Math.round(6*r);
                    g2.drawLine(w/5+gap,y,4*w/5-gap,y);
                    g2.drawLine(4*w/5-gap,y,4*w/5-ponta,y-asa); g2.drawLine(4*w/5-gap,y,4*w/5-ponta,y+asa);
                }

                void desenharComparacao(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int x=w/2;
                    desenharValor(g2,x,h/5,modelo.getTotal(),false);
                    desenharValor(g2,x,h*4/5,modelo.getPrimeiraParcela(),false);
                    desenharValor(g2,x+w/4,h/2,modelo.getSegundaParcela(),true);
                    double r = escalaTraco();
                    int gap=(int)Math.round(24*r), ponta=(int)Math.round(34*r), asa=(int)Math.round(6*r);
                    g2.drawLine(x,h/5+gap,x,h*4/5-gap);
                    // Seta sai do quadrado de baixo para o de cima (2026-08-07,
                    // mesma correção do ícone de atalho em criarIconeCategoriaComparacao).
                    g2.drawLine(x,h/5+gap,x-asa,h/5+ponta); g2.drawLine(x,h/5+gap,x+asa,h/5+ponta);
                }

                private String solicitarValorInteiro(int valorAtual) {
                    final JTextField campo = new JTextField(String.valueOf(valorAtual), 18);
                    campo.setFont(new Font("Arial", Font.PLAIN, 14));
                    campo.setPreferredSize(new Dimension(220, 30));
                    campo.selectAll();

                    JLabel mensagem = new JLabel(localizacao.texto("ui.compare.editValue"));
                    mensagem.setFont(gerard.ui.UITemaGerard.FONTE_DIALOGO);

                    JPanel conteudo = new JPanel();
                    conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
                    conteudo.setBorder(BorderFactory.createEmptyBorder(8, 8, 6, 8));
                    mensagem.setAlignmentX(Component.LEFT_ALIGNMENT);
                    campo.setAlignmentX(Component.LEFT_ALIGNMENT);
                    conteudo.add(mensagem);
                    conteudo.add(Box.createVerticalStrut(8));
                    conteudo.add(campo);

                    Object[] opcoes = {
                        localizacao.texto("ui.dialog.confirm"),
                        localizacao.texto("analise.cancel")
                    };
                    int resposta = JOptionPane.showOptionDialog(
                            this,
                            conteudo,
                            localizacao.texto("ui.dialog.insertValueTitle"),
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            opcoes,
                            opcoes[0]);
                    return resposta == JOptionPane.OK_OPTION ? campo.getText() : null;
                }

                public void mouseClicked(MouseEvent e) {
                    if (formal || e.getClickCount() < 2) return;
                    for (int i=0;i<alvos.size();i++) if (alvos.get(i).contains(e.getPoint())) {
                        EstadoNumericoComparacaoCategorias.Papel papel =
                                papelRepresentadoNoIndice(i);
                        String entrada = solicitarValorInteiro(valorAtual(papel));
                        if (entrada == null) return;
                        try {
                            int valor=Integer.parseInt(entrada.trim());
                            modelo.definir(papel, valor);
                            registrarAcaoComparacao("EDITAR_REPRESENTACAO", "categoria="+categoria.name()+";representacao="+(formal?"CATEGORIA":"SITUACAO")+";indice="+i+";valor="+valor);
                        } catch(NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, localizacao.texto("ui.compare.invalidValue"));
                        }
                        return;
                    }
                }
                public void mousePressed(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}

                private EstadoNumericoComparacaoCategorias.Papel
                        papelRepresentadoNoIndice(int indice) {
                    if (categoria == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
                        if (indice == 0) return EstadoNumericoComparacaoCategorias.Papel.TOTAL;
                        if (indice == 1) return EstadoNumericoComparacaoCategorias.Papel.PRIMEIRA_PARCELA;
                        return EstadoNumericoComparacaoCategorias.Papel.SEGUNDA_PARCELA;
                    }
                    if (indice == 0) return EstadoNumericoComparacaoCategorias.Papel.PRIMEIRA_PARCELA;
                    if (indice == 1) return EstadoNumericoComparacaoCategorias.Papel.SEGUNDA_PARCELA;
                    return EstadoNumericoComparacaoCategorias.Papel.TOTAL;
                }

                private int valorAtual(
                        EstadoNumericoComparacaoCategorias.Papel papel) {
                    if (papel == EstadoNumericoComparacaoCategorias.Papel.PRIMEIRA_PARCELA) {
                        return modelo.getPrimeiraParcela();
                    }
                    if (papel == EstadoNumericoComparacaoCategorias.Papel.SEGUNDA_PARCELA) {
                        return modelo.getSegundaParcela();
                    }
                    return modelo.getTotal();
                }
            }
        }

    }
}
