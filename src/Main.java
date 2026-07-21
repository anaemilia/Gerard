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
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import gerard.agente.monitor.AgenteMonitor;
import gerard.agente.monitor.IndicadorAgenteMonitor;
import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.ConectorVereditoModelador;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.conclusao.AtualizacaoConclusaoModelagem;
import gerard.campoaditivo.conclusao.ControladorConclusaoModelagem;
import gerard.campoaditivo.conclusao.EstadoPosicionamentoModelagem;
import gerard.campoaditivo.conclusao.PoliticaPreenchimentoIncognita;
import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.campoaditivo.semantica.PoliticaValoresAditivos;
import gerard.campoaditivo.sincronizacao.texto.ElementoSemanticoTexto;
import gerard.campoaditivo.sincronizacao.texto.MapeadorPapelSemanticoTexto;
import gerard.campoaditivo.sincronizacao.texto.SincronizadorElementosSemanticosTexto;
import gerard.campoaditivo.sincronizacao.texto.SincronizadorElementosSemanticosTextoAditivo;
import gerard.campoaditivo.servico.ControladorContextoSituacao;
import gerard.campoaditivo.curadoria.TelaCuradoriaSituacoes;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.montagem.TelaMontagemSituacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
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
import gerard.campoaditivo.representacao.SeletorRepresentacaoComplementar;
import gerard.campoaditivo.representacao.TipoRepresentacaoComplementar;
import gerard.campoaditivo.transformacao.processo.PoliticaSinalTransformacaoComplementar;
import gerard.campoaditivo.transformacao.processo.ControleSinalProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.EstadoProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.LayoutUnidadesProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.PlanoUnidadesProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.SincronizadorUnidadesProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.RenderizadorProcessoTransformacao;
import gerard.idioma.IdiomaInterface;
import gerard.idioma.IdiomaSituacao;
import gerard.idioma.CadastroIdiomasSituacao;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.SubtipoVergnaud;
import gerard.interpretacao.simbolo.SimboloDesconhecido;
import gerard.semantica.numero.ConversorTextoParaInteiroSemantico;
import gerard.semantica.quantidade.ServicoQuantidadeContextual;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.modelo.NumeroEncontrado;
import gerard.i18n.ServicoLocalizacao;
import gerard.Scaffolding.ScaffoldingNumeroRelativo;
import gerard.Scaffolding.proximidade.EstadoRealceAlvo;
import gerard.Scaffolding.proximidade.EstiloRealceAlvo;
import gerard.estilointeracao.EstiloInteracao;
import gerard.Scaffolding.proximidade.ScaffoldingProximidade;
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
import gerard.Scaffolding.feedbackerro.ScaffoldingFeedbackMultissensorialErro;
import gerard.Scaffolding.feedbackerro.ScaffoldingFeedbackProxyPosicionamento;
import gerard.Scaffolding.feedbackerro.ControladorAnotacaoTemporaria;
import gerard.Scaffolding.conclusao.AplicadorDestaqueConclusaoDiagrama;
import gerard.Scaffolding.ajudacontextual.ScaffoldingAjudaContextual;
import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import gerard.Scaffolding.reacao.ScaffoldingReacaoRepresentacoes;
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
import gerard.aplicacao.AcaoAtividade;
import gerard.aplicacao.ControladorEstadoAtividade;
import gerard.aplicacao.ContextoCarregamentoAtividade;
import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.pesquisador.tentativa.ItemExplicacaoModelagem;
import gerard.pesquisador.tentativa.TelaArtefatoExplicativo;
import gerard.ui.menu.ConfiguradorOpcaoComparacaoCategorias;
import gerard.ui.janela.ConfiguradorJanelaPrincipal;
import gerard.ui.janela.DimensionadorJanelaComparacaoCategorias;
import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.FragmentoAnotacao;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;
import gerard.interacao.arraste.ControladorLimiarArrasteEstrutural;
import gerard.interacao.arraste.PoliticaGestoEstrutural;
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
        aplicarTemaSwingPadrao();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main janela = new Main();
                janela.setVisible(true);
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
        abas.addTab(ServicoLocalizacao.getInstancia().texto("ui.tab.assembly"), telaMontagem);
        abas.addTab(ServicoLocalizacao.getInstancia().texto("ui.tab.curation"), telaCuradoria);
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

                ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
                abas.setTitleAt(0, loc.texto("ui.tab.gerard"));
                abas.setTitleAt(1, loc.texto("ui.tab.assembly"));
                abas.setTitleAt(2, loc.texto("ui.tab.curation"));
                componenteAnterior = selecionado;
            }
        });
        add(abas);
        ConfiguradorJanelaPrincipal.aplicar(this);
    }

    static class TelaGerard extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

        String textoProblema = "";
        SituacaoProblemaAditiva situacaoProblemaAtual;

        IdiomaInterface idiomaSelecionado = IdiomaInterface.PORTUGUES;
        TipoSituacaoAditiva tipoSituacaoSelecionada = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
        boolean categoriaSelecionadaParaAtividade = false;
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
        final gerard.campoaditivo.sincronizacao.SimuladorEstadoComplementarVenn
                simuladorEstadoComplementarVenn =
                new gerard.campoaditivo.sincronizacao.SimuladorEstadoComplementarVenn(
                        politicaSinalTransformacaoComplementar);
        final SincronizadorUnidadesProcessoTransformacao
                sincronizadorUnidadesProcessoTransformacao =
                new SincronizadorUnidadesProcessoTransformacao();
        final ConversorTextoParaInteiroSemantico conversorTextoParaInteiroSemantico =
                new ConversorTextoParaInteiroSemantico();
        final ServicoQuantidadeContextual servicoQuantidadeContextual =
                new ServicoQuantidadeContextual();
        PlanoUnidadesProcessoTransformacao planoUnidadesProcessoAtual;
        CenaDiagramaVenn cenaDiagramaVennAtual;
        Rectangle ultimaAreaDiagramaVenn = null;
        String assinaturaDiagramaVennSincronizado = "";
        JButton botaoIdioma;
        JButton botaoTipo;
        JButton botaoSobre;
        JButton botaoReportarBug;
        JButton botaoSortear;
        IndicadorAgenteMonitor indicadorAgenteMonitor;
        JButton botaoTeste;
        JButton botaoRestaurar;
        JButton botaoCorrigirCuradoria;
        JButton botaoIdiomaSituacao;
        JButton botaoRestaurarDiagrama;
        JButton botaoVisaoPesquisador;
        JButton botaoArtefatoExplicativo;
        JButton botaoAjudaTexto;
        JButton botaoAjudaVergnaud;
        JButton botaoAjudaComplementar;
        JPopupMenu menuAjudaContextualAtivo;
        ScaffoldingAjudaContextual.Area areaAjudaContextualAtiva;
        JPanel menuIdioma;
        JPanel menuTipo;
        JPanel submenuTipoMedidas;
        JPanel submenuTipoTransformacoes;
        JPanel submenuTipoRelacoes;
        JPanel menuTeste;
        JPanel menuSobre;
        JPanel submenuInterpretacaoLinguistica;
        JPanel submenuRegistroGerard;
        JPanel submenuGerardVergnaud;
        JPanel submenuGerardRecife;
        javax.swing.Timer timerOcultarMenus;

        final String URL_GERARD_VERGNAUD = "https://pt.wikipedia.org/wiki/G%C3%A9rard_Vergnaud";
        final String URL_VIDEO_GERARD_VERGNAUD = "https://www.youtube.com/watch?v=pU7um4GX5XQ";

        ConstrutorResultadoCurado construtorResultadoCurado = new ConstrutorResultadoCurado();
        ResultadoInterpretacao resultadoInterpretacao;
        boolean textoProblemaEhMensagemSistema = false;
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        ScaffoldingNumeroRelativo scaffoldingNumeroRelativo = new ScaffoldingNumeroRelativo();
        ScaffoldingProximidade scaffoldingProximidade = new ScaffoldingProximidade();
        ScaffoldingQuestionamento scaffoldingQuestionamento = new ScaffoldingQuestionamento();
        // Extração nomeada do veredito certo/errado já existente (ver
        // gerard-ajuda-adaptativa/references/agente-monitor.md e
        // instrucao-indicador-agente-monitor.pdf). Não substitui
        // scaffoldingQuestionamento, delega para ele.
        final AgenteMonitor agenteMonitor = new AgenteMonitor(scaffoldingQuestionamento);
        // Modelo do Usuário (ver gerard-modelo-usuario/SKILL.md) e Agente
        // Modelador (agente-modelador.md): ainda sem Agente ZDP, então só a
        // ação 1 (armazenar caso) está conectada — ver ConectorVereditoModelador.
        final AgenteModelador agenteModelador = new AgenteModelador(new RepositorioModeloUsuario());
        final ConectorVereditoModelador conectorVereditoModelador = new ConectorVereditoModelador(agenteModelador);
        ScaffoldingFeedbackMultissensorialErro scaffoldingFeedbackMultissensorialErro = new ScaffoldingFeedbackMultissensorialErro();
        ControladorAnotacaoTemporaria controladorAnotacaoTemporaria = new ControladorAnotacaoTemporaria();
        CatalogoPapeisSemanticosAditivos catalogoPapeisSemanticos = new CatalogoPapeisSemanticosAditivos();
        PoliticaValoresAditivos politicaValoresAditivos = new PoliticaValoresAditivos(catalogoPapeisSemanticos);
        ScaffoldingAjudaContextual scaffoldingAjudaContextual = new ScaffoldingAjudaContextual();
        ScaffoldingGraficoInteiros scaffoldingGraficoInteiros = new ScaffoldingGraficoInteiros();
        ScaffoldingReacaoRepresentacoes scaffoldingReacaoRepresentacoes = new ScaffoldingReacaoRepresentacoes();
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
        final ControladorLimiarArrasteEstrutural controladorLimiarArrasteEstrutural =
                new ControladorLimiarArrasteEstrutural();
        final PoliticaGestoEstrutural politicaGestoEstrutural =
                new PoliticaGestoEstrutural();
        final ControladorConclusaoModelagem controladorConclusaoModelagem =
                new ControladorConclusaoModelagem();
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
        ControladorContextoSituacao controladorContextoSituacao = new ControladorContextoSituacao(loggerInteracaoGerard);
        ControladorEstadoAtividade controladorEstadoAtividade = new ControladorEstadoAtividade();
        FachadaCarregamentoAtividade fachadaCarregamentoAtividade =
                new FachadaCarregamentoAtividade(repositorioSituacoesAditivas, catalogoDefinicoesAditivas, construtorResultadoCurado);
        TelaCuradoriaSituacoes telaCuradoriaSituacoes;
        EstiloInteracao modoFeedbackTeste = EstiloInteracao.PROXIMIDADE;
        int quantidadePassosTransformacaoComposta = 1;
        java.util.List<Integer> transformacoesComSinalTransformacaoComposta = new ArrayList<Integer>();
        java.util.List<Integer> estadosIntermediariosTransformacaoComposta = new ArrayList<Integer>();

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
        final Color COR_BOTAO_SECUNDARIO = gerard.ui.UITemaGerard.COR_FUNDO_CONTEUDO;
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
        // Extraído para gerard.ui.enunciado.AreaTituloCategoriaEnunciado (Fase 1 da refatoração).
        final gerard.ui.enunciado.AreaTituloCategoriaEnunciado areaTituloCategoria =
                new gerard.ui.enunciado.AreaTituloCategoriaEnunciado();

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
        ArrayList<QuadradinhoVenn> quadradinhosCorrespondentesComparacao = new ArrayList<QuadradinhoVenn>();
        ArrayList<ElementoVergnaud> elementosVergnaud = new ArrayList<ElementoVergnaud>();
        ArrayList<ConectorVergnaud> conectoresVergnaud = new ArrayList<ConectorVergnaud>();
        final EstadoSemanticoCompartilhado estadoSemanticoCompartilhado = new EstadoSemanticoCompartilhado();
        final SincronizadorElementosSemanticosTexto sincronizadorElementosSemanticosTexto =
                new SincronizadorElementosSemanticosTextoAditivo();
        boolean aplicandoEstadoSemanticoCompartilhado = false;
        int indiceCirculoVennOrigemArraste = -1;
        int[] indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};

        ItemTextoArrastavel itemSelecionado = null;
        ItemTextoArrastavel itemFocado = null;
        ElementoTextoMovel elementoTextoSelecionado = null;
        ElementoTextoMovel elementoTextoFocado = null;
        boolean layoutTextoInicializado = false;
        int larguraUltimoLayoutTexto = -1;
        QuadradinhoVenn quadradinhoVennSelecionado = null;
        QuadradinhoVenn quadradinhoVennFocado = null;
        ElementoVergnaud elementoVergnaudSelecionado = null;
        ConectorVergnaud conectorVergnaudSelecionado = null;
        ElementoVergnaud alvoRealcadoPorProximidade = null;
        final int DISTANCIA_REALCE_ALVO = 48;

        int deslocamentoX;
        int deslocamentoY;
        int deslocamentoVennX;
        int deslocamentoVennY;
        int mouseAnteriorX;
        int mouseAnteriorY;
        boolean arrastandoControleComparacao = false;
        double proporcaoControleComparacao = -1.0;
        int ultimoValorInteiroControleComparacao = -1;

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
        boolean mostrarQuestionamentoPersistente = false;
        String textoQuestionamentoPersistente = "";
        ItemTextoArrastavel itemQuestionadoPersistente = null;
        ItemTextoArrastavel itemGraficoInteiros = null;
        ElementoVergnaud numeroRelativoGraficoInteiros = null;
        boolean sincronizacaoEstadoFinalHabilitada = false;
        ItemTextoArrastavel itemIncognitaEstadoFinal = null;
        ElementoVergnaud elementoEstadoFinalSincronizado = null;

        public TelaGerard() {
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
                    repaint();
                }
            });

            criarBotaoIdioma();
            criarBotaoTipo();
            criarBotaoSobre();
            criarBotaoSortear();
            criarBotaoReportarBug();
            criarIndicadorAgenteMonitor();
            criarBotaoTeste();
            criarBotaoVisaoPesquisador();
            criarBotaoRestaurar();
            criarBotaoCorrigirCuradoria();
            criarBotaoIdiomaSituacao();
            criarBotaoArtefatoExplicativo();
            criarBotaoRestaurarDiagrama();
            criarBotoesAjudaContextual();
            criarMenusInternos();
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
                    if (botaoSortear != null && botaoSortear.isEnabled()) {
                        botaoSortear.doClick();
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
                            seloConclusaoModelagem.mostrarAbaixoDoDiagrama(
                                    areaDiagrama, areaPermitida, getWidth(), getHeight());
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
                    if (botaoSortear != null && botaoSortear.isEnabled()) {
                        botaoSortear.doClick();
                    }
                }
            });
            seloConclusaoModelagem.definirAcaoHover(new Runnable() {
                public void run() {
                    mostrarTipConclusaoModelagem();
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
            Rectangle areaPermitida = obterAreaVisivelDiagramasVergnaud();
            Rectangle areaDiagrama = obterAreaVisualDiagramaVergnaudAtual();
            setComponentZOrder(tipConclusaoModelagem, 0);
            tipConclusaoModelagem.mostrarAbaixoDoDiagrama(
                    areaDiagrama, areaPermitida, getWidth(), getHeight());
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

        private void criarBotaoIdioma() {
            botaoIdioma = new JButton(idiomaSelecionado.getRotuloBotao());
            botaoIdioma.setBounds(18, 9, 120, 28);
            botaoIdioma.setFocusable(false);
            botaoIdioma.setFont(gerard.ui.UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
            estilizarBotaoPrincipal(botaoIdioma);
            botaoIdioma.setToolTipText(localizacao.texto("ui.tooltip.language"));
            botaoIdioma.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarMenuIdioma();
                }
            });
            botaoIdioma.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarMenuIdioma();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }
            });
            add(botaoIdioma);
        }

        private void criarBotaoTipo() {
            botaoTipo = new JButton(localizacao.texto("ui.button.category"));
            botaoTipo.setBounds(150, 9, 150, 28);
            botaoTipo.setFocusable(false);
            botaoTipo.setFont(gerard.ui.UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
            estilizarBotaoPrincipal(botaoTipo);
            botaoTipo.setToolTipText(localizacao.texto("ui.tooltip.type"));
            botaoTipo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarMenuTipo();
                }
            });
            botaoTipo.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarMenuTipo();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }
            });
            add(botaoTipo);
        }

        private void criarBotaoSobre() {
            botaoSobre = new JButton(localizacao.texto("ui.button.about"));
            botaoSobre.setBounds(312, 9, 120, 28);
            botaoSobre.setFocusable(false);
            botaoSobre.setFont(gerard.ui.UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
            estilizarBotaoPrincipal(botaoSobre);
            botaoSobre.setToolTipText(localizacao.texto("ui.tooltip.about"));
            botaoSobre.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarMenuSobre();
                }
            });
            botaoSobre.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarMenuSobre();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }
            });
            add(botaoSobre);
        }

        private void criarBotaoSortear() {
            botaoSortear = new JButton(localizacao.texto("ui.button.random"));
            botaoSortear.setBounds(444, 9, 120, 28);
            botaoSortear.setFocusable(false);
            botaoSortear.setFont(gerard.ui.UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
            estilizarBotaoPrincipal(botaoSortear);
            botaoSortear.setToolTipText(localizacao.texto("ui.tooltip.random"));
            botaoSortear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    iniciarNovaAtividade(AcaoAtividade.SORTEAR);
                }
            });
            add(botaoSortear);
        }

        /**
         * Indicador ambiente experimental (ver
         * instrucao-indicador-agente-monitor.pdf) — pulsa quando o
         * AgenteMonitor produz um veredito. Componente isolado; remover esta
         * chamada e o campo indicadorAgenteMonitor desliga o experimento sem
         * afetar o resto da tela.
         */
        private void criarIndicadorAgenteMonitor() {
            indicadorAgenteMonitor = new IndicadorAgenteMonitor();
            indicadorAgenteMonitor.setBounds(1127, 16, 14, 14);
            agenteMonitor.adicionarOuvinte(indicadorAgenteMonitor);
            add(indicadorAgenteMonitor);
        }

        private void criarBotaoReportarBug() {
            botaoReportarBug = new JButton(localizacao.texto("ui.button.reportBug"));
            botaoReportarBug.setBounds(995, 9, 122, 28);
            botaoReportarBug.setFocusable(false);
            botaoReportarBug.setFont(gerard.ui.UITemaGerard.FONTE_ITEM_MENU.deriveFont(Font.PLAIN, 11.0f));
            botaoReportarBug.setForeground(COR_TEXTO_SECUNDARIO);
            botaoReportarBug.setBackground(COR_SUPERFICIE);
            botaoReportarBug.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            botaoReportarBug.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botaoReportarBug.setToolTipText(localizacao.texto("ui.tooltip.reportBug"));
            botaoReportarBug.getAccessibleContext().setAccessibleName(localizacao.texto("ui.button.reportBug"));
            botaoReportarBug.getAccessibleContext().setAccessibleDescription(localizacao.texto("ui.tooltip.reportBug"));
            botaoReportarBug.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    botaoReportarBug.setBackground(COR_SUPERFICIE_SUAVE);
                }
                public void mouseExited(MouseEvent e) {
                    botaoReportarBug.setBackground(COR_SUPERFICIE);
                }
            });
            botaoReportarBug.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ocultarMenus();
                    mostrarDialogoRelatoBug();
                }
            });
            add(botaoReportarBug);
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

            if (deveExibirDiagramaComplementar()) {
                if (ehDiagramaVennComposicaoMedidas()) {
                    representacoes.add(localizacao.texto("ui.collections.title"));
                } else if (ehGraficoBarrasComparacao()) {
                    representacoes.add(localizacao.texto("ui.comparisonBars.title"));
                } else {
                    representacoes.add(localizacao.texto("ui.vann.title"));
                }
            }

            if (scaffoldingGraficoInteiros != null
                    && scaffoldingGraficoInteiros.isVisivel()) {
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

        private void criarBotaoTeste() {
            botaoTeste = new JButton();
            botaoTeste.setBounds(576, 9, 205, 28);
            botaoTeste.setFocusable(false);
            botaoTeste.setFont(gerard.ui.UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
            estilizarBotaoPrincipal(botaoTeste);
            atualizarTextoBotaoTeste();
            botaoTeste.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarMenuTeste();
                }
            });
            botaoTeste.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarMenuTeste();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }
            });
            add(botaoTeste);
        }

        private void criarBotaoVisaoPesquisador() {
            botaoVisaoPesquisador = new JButton(localizacao.texto("pesq.button.open"));
            botaoVisaoPesquisador.setBounds(793, 9, 190, 28);
            botaoVisaoPesquisador.setFocusable(false);
            botaoVisaoPesquisador.setFont(gerard.ui.UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
            estilizarBotaoPrincipal(botaoVisaoPesquisador);
            botaoVisaoPesquisador.setToolTipText(localizacao.texto("pesq.tooltip.open"));
            botaoVisaoPesquisador.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Window janela = SwingUtilities.getWindowAncestor(TelaGerard.this);
                    TelaVisaoPesquisador.mostrar(janela);
                    requestFocusInWindow();
                }
            });
            add(botaoVisaoPesquisador);
        }

        private void criarBotaoRestaurar() {
            botaoRestaurar = new JButton(criarIconeRestaurar());
            botaoRestaurar.setBounds(27, 70, 26, 26);
            configurarBotaoAcaoContextual(botaoRestaurar, localizacao.texto("ui.tooltip.restore.elements"));
            botaoRestaurar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    registrarLogUsuario(
                            "Restaurar elementos fora do diagrama",
                            "-",
                            "Botão Restaurar",
                            "Botão Restaurar do enunciado",
                            "Repor elementos móveis que ficaram fora do modelo",
                            "OBJ8",
                            "A restauração permite retomar a modelagem sem alterar o problema.",
                            "RESTAURAR_TEXTO",
                            ""
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
            botaoCorrigirCuradoria.setBounds(27, 101, 26, 26);
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
            botaoIdiomaSituacao.setBounds(27, 132, 26, 26);
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
            textoProblema = normalizarTextoProblemaParaRenderizacao(versao.getEnunciado());
            resultadoInterpretacao = construtorResultadoCurado.construir(versao, textoProblema);
            transformacoesComSinalTransformacaoComposta = extrairTransformacoesComSinalTransformacaoComposta();
            quantidadePassosTransformacaoComposta = calcularQuantidadePassosTransformacaoComposta();
            estadosIntermediariosTransformacaoComposta = calcularEstadosIntermediariosTransformacaoComposta();
            elementoTextoSelecionado = null;
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
            botaoArtefatoExplicativo.setBounds(27, 163, 26, 26);
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
            boolean salvo = TelaArtefatoExplicativo.mostrar(this, itens, contexto);
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
                    registrarLogUsuario(
                            "Restaurar a área do diagrama",
                            "-",
                            "Botão Restaurar",
                            "Botão Restaurar da área do diagrama",
                            "Limpar a modelagem e permitir nova tentativa",
                            "OBJ8",
                            "O sujeito pode recomeçar a construção do diagrama.",
                            "RESTAURAR_DIAGRAMA",
                            ""
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
                botaoAjudaTexto.setBounds(Math.max(18, getWidth() - 53), 63, 26, 26);
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

            final JTextArea resposta = new JTextArea(localizacao.texto("ui.help.choose"), 4, 34);
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

            painel.add(criarOpcaoAjudaContextual(
                    area, ScaffoldingAjudaContextual.Intencao.DUVIDA, resposta, popup));
            painel.add(Box.createVerticalStrut(3));
            painel.add(criarOpcaoAjudaContextual(
                    area, ScaffoldingAjudaContextual.Intencao.CONTINUAR, resposta, popup));
            painel.add(Box.createVerticalStrut(3));
            painel.add(criarOpcaoAjudaContextual(
                    area, ScaffoldingAjudaContextual.Intencao.PROXIMO_PASSO, resposta, popup));
            painel.add(Box.createVerticalStrut(8));
            resposta.setAlignmentX(Component.LEFT_ALIGNMENT);
            painel.add(resposta);

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

        private void fecharMenuAjudaContextual() {
            if (menuAjudaContextualAtivo != null) {
                menuAjudaContextualAtivo.setVisible(false);
                menuAjudaContextualAtivo = null;
            }
            areaAjudaContextualAtiva = null;
        }


        private void criarMenusInternos() {
            if (menuIdioma != null) {
                remove(menuIdioma);
            }
            if (menuTipo != null) {
                remove(menuTipo);
            }
            if (submenuTipoMedidas != null) {
                remove(submenuTipoMedidas);
            }
            if (submenuTipoTransformacoes != null) {
                remove(submenuTipoTransformacoes);
            }
            if (submenuTipoRelacoes != null) {
                remove(submenuTipoRelacoes);
            }
            if (menuTeste != null) {
                remove(menuTeste);
            }
            if (menuSobre != null) {
                remove(menuSobre);
            }
            if (submenuInterpretacaoLinguistica != null) {
                remove(submenuInterpretacaoLinguistica);
            }
            if (submenuRegistroGerard != null) {
                remove(submenuRegistroGerard);
            }
            if (submenuGerardVergnaud != null) {
                remove(submenuGerardVergnaud);
            }
            if (submenuGerardRecife != null) {
                remove(submenuGerardRecife);
            }

            menuIdioma = criarPainelMenu(18, 39, 150, IdiomaInterface.values().length * 29 + 8);

            for (final IdiomaInterface idioma : IdiomaInterface.values()) {
                JButton opcao = criarBotaoOpcao(localizacao.nomeIdioma(idioma));
                opcao.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        idiomaSelecionado = idioma;
                        ocultarMenus();
                        controladorEstadoAtividade.registrar(AcaoAtividade.TROCAR_IDIOMA);
                        aplicarIdiomaSelecionadoMantendoEstadoTela();
                    }
                });
                menuIdioma.add(opcao);
            }

            menuTipo = criarPainelMenu(150, 39, 245, 4 * 33 + 10);
            submenuTipoMedidas = criarPainelMenu(395, 39, 340, 4 * 33 + 10);
            submenuTipoTransformacoes = criarPainelMenu(395, 72, 370, 3 * 33 + 10);
            submenuTipoRelacoes = criarPainelMenu(395, 105, 320, 2 * 33 + 10);

            adicionarGrupoCategoria(menuTipo, localizacao.texto("ui.menu.category.measures"), submenuTipoMedidas);
            adicionarOpcaoCategoriaEmConstrucao(menuTipo, localizacao.texto("ui.menu.category.transformations"));
            adicionarOpcaoCategoriaEmConstrucao(menuTipo, localizacao.texto("ui.menu.category.relations"));
            final JButton opcaoComparacaoCategorias = criarBotaoOpcaoCategoria(
                    localizacao.texto("ui.menu.category.compare"), false);
            ConfiguradorOpcaoComparacaoCategorias.configurar(
                    opcaoComparacaoCategorias,
                    new Runnable() {
                        public void run() {
                            ocultarMenus();
                            abrirTelaComparacaoCategorias();
                        }
                    });
            menuTipo.add(opcaoComparacaoCategorias);

            adicionarOpcaoCategoria(submenuTipoMedidas, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
            adicionarOpcaoCategoria(submenuTipoMedidas, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
            adicionarOpcaoCategoria(submenuTipoMedidas, TipoSituacaoAditiva.COMPARACAO_MEDIDAS);

            JButton opcaoComposicaoMedidasNovaTela = criarBotaoOpcaoCategoria(
                    "Composição de medidas (nova tela)", false);
            opcaoComposicaoMedidasNovaTela.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ocultarMenus();
                    new JanelaComposicaoMedidasDesktop().setVisible(true);
                }
            });
            submenuTipoMedidas.add(opcaoComposicaoMedidasNovaTela);

            adicionarOpcaoCategoria(submenuTipoTransformacoes, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS);
            adicionarOpcaoCategoria(submenuTipoTransformacoes, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES);
            adicionarOpcaoCategoria(submenuTipoTransformacoes, TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS);

            adicionarOpcaoCategoria(submenuTipoRelacoes, TipoSituacaoAditiva.TRANSFORMACAO_RELACAO);
            adicionarOpcaoCategoria(submenuTipoRelacoes, TipoSituacaoAditiva.COMPOSICAO_RELACOES);

            menuTeste = criarPainelMenu(576, 39, 330, EstiloInteracao.values().length * 29 + 8);
            for (final EstiloInteracao modo : EstiloInteracao.values()) {
                JButton opcao = criarBotaoOpcao(localizacao.texto(modo.getChave()));
                if (modo == modoFeedbackTeste) {
                    opcao.setText("✓ " + localizacao.texto(modo.getChave()));
                    opcao.setFont(new Font("Arial", Font.BOLD, 11));
                }
                opcao.setToolTipText(localizacao.texto("ui.test.description." + modo.name()));
                opcao.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        controladorEstadoAtividade.registrar(AcaoAtividade.TROCAR_ESTILO_INTERACAO);
                        modoFeedbackTeste = modo;
                        limparRealceAlvoProximidade();
                        ocultarMenus();
                        atualizarTextoBotaoTeste();
                        criarMenusInternos();
                        repaint();
                    }
                });
                menuTeste.add(opcao);
            }

            menuSobre = criarPainelMenu(312, 39, 245, 127);
            JButton opcaoInterpretacao = criarBotaoOpcao(localizacao.texto("ui.menu.linguisticInterpretation"));
            opcaoInterpretacao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarSubmenuInterpretacaoLinguistica();
                }
            });
            opcaoInterpretacao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarSubmenuInterpretacaoLinguistica();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }
            });
            menuSobre.add(opcaoInterpretacao);

            JButton opcaoRegistro = criarBotaoOpcao(localizacao.texto("ui.menu.gerardRegistration"));
            opcaoRegistro.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarSubmenuRegistroGerard();
                }
            });
            opcaoRegistro.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarSubmenuRegistroGerard();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }
            });
            menuSobre.add(opcaoRegistro);

            JButton opcaoGerardVergnaud = criarBotaoOpcao(localizacao.texto("ui.menu.gerardVergnaud"));
            opcaoGerardVergnaud.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarSubmenuGerardVergnaud();
                }
            });
            opcaoGerardVergnaud.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarSubmenuGerardVergnaud();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }
            });
            menuSobre.add(opcaoGerardVergnaud);

            submenuInterpretacaoLinguistica = criarMenuInterpretacaoLinguistica(557, 39, 620, 170);
            submenuRegistroGerard = criarMenuRegistroGerard(557, 70, 430, 145);
            submenuGerardVergnaud = criarMenuGerardVergnaud(557, 132, 250, 330);
            submenuGerardRecife = criarMenuGerardRecife(807, 132, 420, 300);

            add(menuIdioma);
            add(menuTipo);
            add(submenuTipoMedidas);
            add(submenuTipoTransformacoes);
            add(submenuTipoRelacoes);
            add(menuTeste);
            add(menuSobre);
            add(submenuInterpretacaoLinguistica);
            add(submenuRegistroGerard);
            add(submenuGerardVergnaud);
            add(submenuGerardRecife);
            setComponentZOrder(menuIdioma, 0);
            setComponentZOrder(menuTipo, 0);
            setComponentZOrder(submenuTipoMedidas, 0);
            setComponentZOrder(submenuTipoTransformacoes, 0);
            setComponentZOrder(submenuTipoRelacoes, 0);
            setComponentZOrder(menuTeste, 0);
            setComponentZOrder(menuSobre, 0);
            setComponentZOrder(submenuInterpretacaoLinguistica, 0);
            setComponentZOrder(submenuRegistroGerard, 0);
            setComponentZOrder(submenuGerardVergnaud, 0);
            setComponentZOrder(submenuGerardRecife, 0);
            menuIdioma.setVisible(false);
            menuTipo.setVisible(false);
            submenuTipoMedidas.setVisible(false);
            submenuTipoTransformacoes.setVisible(false);
            submenuTipoRelacoes.setVisible(false);
            menuTeste.setVisible(false);
            menuSobre.setVisible(false);
            submenuInterpretacaoLinguistica.setVisible(false);
            submenuRegistroGerard.setVisible(false);
            submenuGerardVergnaud.setVisible(false);
            submenuGerardRecife.setVisible(false);
            revalidate();
            repaint();
        }


        private void adicionarGrupoCategoria(JPanel menuPai, String texto, final JPanel submenuAlvo) {
            final JButton opcao = criarBotaoOpcaoCategoria(texto, false);
            opcao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarSubmenuCategoria(submenuAlvo);
                }
            });
            opcao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarSubmenuCategoria(submenuAlvo);
                }
            });
            menuPai.add(opcao);
        }

        private void adicionarOpcaoCategoriaEmConstrucao(JPanel menuPai, String texto) {
            final String dica = localizacao.texto("ui.menu.underConstruction");
            final JPanel envoltorio = new JPanel(new BorderLayout());
            envoltorio.setOpaque(false);
            envoltorio.setToolTipText(dica);

            final JButton opcao = criarBotaoOpcaoCategoria(texto, false);
            opcao.setEnabled(false);
            opcao.setToolTipText(dica);
            opcao.setCursor(Cursor.getDefaultCursor());
            envoltorio.add(opcao, BorderLayout.CENTER);

            // O envoltório mantém o tooltip disponível mesmo com o botão
            // semanticamente desabilitado e também recolhe submenus abertos.
            envoltorio.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarSubmenuCategoria(null);
                }
            });
            menuPai.add(envoltorio);
        }

        private void adicionarOpcaoCategoria(JPanel submenu, final TipoSituacaoAditiva tipo) {
            JButton opcao = criarBotaoOpcaoCategoria(tipo.getRotuloBotao(), false);
            opcao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    registrarLogUsuario(
                            "Selecionar a legenda correspondente à operação",
                            "-",
                            "Menu/barra de legendas",
                            "Botão " + tipo.getRotuloBotao(),
                            "Representar a estrutura escolhida para o problema",
                            "OBJ8",
                            "O sujeito deve selecionar a legenda adequada ao tipo de problema.",
                            "MENU_CATEGORIA",
                            "categoriaSelecionada=" + tipo.name()
                    );
                    tipoSituacaoSelecionada = tipo;
                    categoriaSelecionadaParaAtividade = true;
                    if (botaoSortear != null) {
                        botaoSortear.setEnabled(true);
                    }
                    ocultarMenus();
                    iniciarNovaAtividade(AcaoAtividade.SELECIONAR_CATEGORIA);
                }
            });
            submenu.add(opcao);
        }

        private void mostrarSubmenuCategoria(JPanel submenuAlvo) {
            cancelarOcultacaoMenus();
            if (submenuTipoMedidas != null) submenuTipoMedidas.setVisible(submenuAlvo == submenuTipoMedidas);
            if (submenuTipoTransformacoes != null) submenuTipoTransformacoes.setVisible(submenuAlvo == submenuTipoTransformacoes);
            if (submenuTipoRelacoes != null) submenuTipoRelacoes.setVisible(submenuAlvo == submenuTipoRelacoes);
            if (submenuAlvo != null) {
                setComponentZOrder(submenuAlvo, 0);
            }
            repaint();
        }

        private JPanel criarPainelMenu(int x, int y, int largura, int altura) {
            JPanel painel = new JPanel();
            painel.setLayout(new GridLayout(0, 1, 0, 0));
            painel.setBounds(x, y, largura, altura);
            painel.setBackground(COR_SUPERFICIE);
            painel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)
            ));
            painel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    cancelarOcultacaoMenus();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }
            });
            return painel;
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

        private JPanel criarMenuInterpretacaoLinguistica(int x, int y, int largura, int altura) {
            JPanel painel = criarPainelMenu(x, y, largura, altura);
            painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
            painel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

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

            ajustarMenuAoConteudo(painel, x, y);
            return painel;
        }

        private JPanel criarMenuRegistroGerard(int x, int y, int largura, int altura) {
            JPanel painel = criarPainelMenu(x, y, largura, altura);
            painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
            painel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            painel.add(criarLabelMenu(localizacao.texto("ui.menu.gerardRegistration"), true));
            painel.add(Box.createVerticalStrut(5));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.registeredProgram"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.process"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.title"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.creationDate"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.authors"), false));
            painel.add(criarLabelMenu(localizacao.texto("ui.about.language"), false));

            ajustarMenuAoConteudo(painel, x, y);
            return painel;
        }

        private JPanel criarMenuGerardVergnaud(int x, int y, int largura, int altura) {
            JPanel painel = criarPainelMenu(x, y, largura, altura);
            painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
            painel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            int larguraConteudo = largura - 34;

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
            painel.add(Box.createVerticalStrut(8));

            JButton opcaoEmRecife = criarBotaoOpcaoSubmenu(localizacao.texto("ui.menu.emRecife"));
            opcaoEmRecife.setAlignmentX(Component.LEFT_ALIGNMENT);
            int alturaOpcaoEmRecife = opcaoEmRecife.getPreferredSize().height;
            Dimension tamanhoOpcaoEmRecife = new Dimension(larguraConteudo, alturaOpcaoEmRecife);
            opcaoEmRecife.setPreferredSize(tamanhoOpcaoEmRecife);
            opcaoEmRecife.setMinimumSize(tamanhoOpcaoEmRecife);
            opcaoEmRecife.setMaximumSize(tamanhoOpcaoEmRecife);
            opcaoEmRecife.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mostrarSubmenuGerardRecife();
                }
            });
            opcaoEmRecife.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    mostrarSubmenuGerardRecife();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }
            });
            painel.add(opcaoEmRecife);

            ajustarMenuAoConteudoLimitado(painel, x, y, largura);
            return painel;
        }

        private JPanel criarMenuGerardRecife(int x, int y, int largura, int altura) {
            JPanel painel = criarPainelMenu(x, y, largura, altura);
            painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
            painel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            int larguraConteudo = largura - 34;

            JLabel titulo = criarLabelMenu(localizacao.texto("ui.menu.emRecife"), true);
            titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            titulo.setMaximumSize(new Dimension(larguraConteudo, titulo.getPreferredSize().height));
            painel.add(titulo);
            painel.add(Box.createVerticalStrut(8));

            JPanel painelImagens = criarPainelDuasImagensMenu(
                    "/gerard/imagens/em_recife_rostos.png",
                    "/gerard/imagens/em_recife_adicional.png",
                    larguraConteudo,
                    220
            );
            painel.add(painelImagens);

            ajustarMenuAoConteudoLimitado(painel, x, y, largura);
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
                public void mouseEntered(MouseEvent e) {
                    cancelarOcultacaoMenus();
                }
                public void mouseExited(MouseEvent e) {
                    agendarOcultacaoMenus();
                }

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

        private void ajustarMenuAoConteudo(JPanel painel, int x, int y) {
            Dimension preferido = painel.getPreferredSize();
            int larguraAjustada = Math.max(120, preferido.width + 2);
            int alturaAjustada = Math.max(28, preferido.height + 2);
            painel.setPreferredSize(new Dimension(larguraAjustada, alturaAjustada));
            painel.setBounds(x, y, larguraAjustada, alturaAjustada);
        }

        private void ajustarMenuAoConteudoLimitado(JPanel painel, int x, int y, int larguraMaxima) {
            Dimension preferido = painel.getPreferredSize();
            int larguraAjustada = Math.max(120, larguraMaxima);
            int alturaAjustada = Math.max(28, preferido.height + 2);
            painel.setPreferredSize(new Dimension(larguraAjustada, alturaAjustada));
            painel.setBounds(x, y, larguraAjustada, alturaAjustada);
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

        private JButton criarBotaoOpcaoCategoria(String texto, boolean destaque) {
            JButton botao = criarBotaoOpcao(texto);
            botao.setFont(gerard.ui.UITemaGerard.FONTE_ITEM_MENU);
            botao.setMargin(new Insets(2, 8, 2, 8));
            botao.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            return botao;
        }

        private JButton criarBotaoOpcaoSubmenu(String texto) {
            JButton botao = criarBotaoOpcao(texto);
            // O texto permanece alinhado à esquerda, como os demais itens do menu,
            // enquanto a seta é desenhada na extremidade direita da linha.
            // Assim, o indicador não fica colado ao rótulo nem desloca o texto.
            botao.setIcon(null);
            botao.setMargin(new Insets(2, 6, 2, 6));
            botao.setBorder(new javax.swing.border.AbstractBorder() {
                public Insets getBorderInsets(Component componente) {
                    return new Insets(6, 10, 6, 24);
                }

                public Insets getBorderInsets(Component componente, Insets insets) {
                    insets.top = 6;
                    insets.left = 10;
                    insets.bottom = 6;
                    insets.right = 24;
                    return insets;
                }

                public void paintBorder(Component componente, Graphics grafico,
                        int x, int y, int largura, int altura) {
                    Graphics2D g2 = (Graphics2D) grafico.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(componente != null && componente.isEnabled()
                                ? COR_TEXTO_SECUNDARIO
                                : UIManager.getColor("Label.disabledForeground"));
                        g2.setStroke(new BasicStroke(1.35f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND));

                        int centroY = y + altura / 2;
                        int pontaX = x + largura - 11;
                        g2.drawLine(pontaX - 4, centroY - 4, pontaX, centroY);
                        g2.drawLine(pontaX, centroY, pontaX - 4, centroY + 4);
                    } finally {
                        g2.dispose();
                    }
                }
            });
            botao.setHorizontalAlignment(SwingConstants.LEFT);
            botao.setHorizontalTextPosition(SwingConstants.LEFT);
            return botao;
        }

        private Icon criarIconeSetaSubmenu() {
            return new Icon() {
                public int getIconWidth() {
                    return 7;
                }

                public int getIconHeight() {
                    return 10;
                }

                public void paintIcon(Component componente, Graphics grafico, int x, int y) {
                    Graphics2D g2 = (Graphics2D) grafico.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(componente != null && componente.isEnabled()
                                ? COR_TEXTO_SECUNDARIO
                                : UIManager.getColor("Label.disabledForeground"));
                        g2.setStroke(new BasicStroke(1.35f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int meioY = y + getIconHeight() / 2;
                        g2.drawLine(x + 1, y + 2, x + 5, meioY);
                        g2.drawLine(x + 5, meioY, x + 1, y + getIconHeight() - 2);
                    } finally {
                        g2.dispose();
                    }
                }
            };
        }

        private JButton criarBotaoOpcao(String texto) {
            JButton botao = new JButton(texto);
            botao.setHorizontalAlignment(SwingConstants.LEFT);
            botao.setFocusable(false);
            botao.setFont(gerard.ui.UITemaGerard.FONTE_ITEM_MENU);
            botao.setMargin(new Insets(2, 6, 2, 6));
            botao.setBackground(COR_SUPERFICIE);
            botao.setForeground(COR_TEXTO);
            botao.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (botao.isEnabled()) {
                        botao.setBackground(COR_DESTAQUE);
                    }
                }
                public void mouseExited(MouseEvent e) {
                    botao.setBackground(COR_SUPERFICIE);
                }
            });
            return botao;
        }

        private void mostrarMenuIdioma() {
            cancelarOcultacaoMenus();
            if (menuTipo != null) {
                menuTipo.setVisible(false);
            }
            if (submenuTipoMedidas != null) {
                submenuTipoMedidas.setVisible(false);
            }
            if (submenuTipoTransformacoes != null) {
                submenuTipoTransformacoes.setVisible(false);
            }
            if (submenuTipoRelacoes != null) {
                submenuTipoRelacoes.setVisible(false);
            }
            if (menuTeste != null) {
                menuTeste.setVisible(false);
            }
            if (menuSobre != null) {
                menuSobre.setVisible(false);
            }
            if (submenuInterpretacaoLinguistica != null) {
                submenuInterpretacaoLinguistica.setVisible(false);
            }
            if (submenuRegistroGerard != null) {
                submenuRegistroGerard.setVisible(false);
            }
            if (submenuGerardVergnaud != null) {
                submenuGerardVergnaud.setVisible(false);
            }
            if (submenuGerardRecife != null) {
                submenuGerardRecife.setVisible(false);
            }
            if (menuIdioma != null) {
                menuIdioma.setVisible(true);
                setComponentZOrder(menuIdioma, 0);
                repaint();
            }
        }

        private void mostrarMenuTipo() {
            cancelarOcultacaoMenus();
            if (menuIdioma != null) {
                menuIdioma.setVisible(false);
            }
            if (menuTeste != null) {
                menuTeste.setVisible(false);
            }
            if (menuSobre != null) {
                menuSobre.setVisible(false);
            }
            if (submenuInterpretacaoLinguistica != null) {
                submenuInterpretacaoLinguistica.setVisible(false);
            }
            if (submenuRegistroGerard != null) {
                submenuRegistroGerard.setVisible(false);
            }
            if (submenuGerardVergnaud != null) {
                submenuGerardVergnaud.setVisible(false);
            }
            if (submenuGerardRecife != null) {
                submenuGerardRecife.setVisible(false);
            }
            if (submenuTipoMedidas != null) submenuTipoMedidas.setVisible(false);
            if (submenuTipoTransformacoes != null) submenuTipoTransformacoes.setVisible(false);
            if (submenuTipoRelacoes != null) submenuTipoRelacoes.setVisible(false);
            if (menuTipo != null) {
                menuTipo.setVisible(true);
                setComponentZOrder(menuTipo, 0);
                repaint();
            }
        }

        private void mostrarMenuTeste() {
            cancelarOcultacaoMenus();
            if (menuIdioma != null) {
                menuIdioma.setVisible(false);
            }
            if (menuTipo != null) {
                menuTipo.setVisible(false);
            }
            ocultarSubmenusCategoria();
            if (menuTeste != null) {
                menuTeste.setVisible(false);
            }
            if (menuSobre != null) {
                menuSobre.setVisible(false);
            }
            if (submenuInterpretacaoLinguistica != null) {
                submenuInterpretacaoLinguistica.setVisible(false);
            }
            if (submenuRegistroGerard != null) {
                submenuRegistroGerard.setVisible(false);
            }
            if (submenuGerardVergnaud != null) {
                submenuGerardVergnaud.setVisible(false);
            }
            if (submenuGerardRecife != null) {
                submenuGerardRecife.setVisible(false);
            }
            if (menuTeste != null) {
                menuTeste.setVisible(true);
                setComponentZOrder(menuTeste, 0);
                repaint();
            }
        }

        private void mostrarMenuSobre() {
            cancelarOcultacaoMenus();
            ocultarSubmenusCategoria();
            if (menuIdioma != null) {
                menuIdioma.setVisible(false);
            }
            if (menuTipo != null) {
                menuTipo.setVisible(false);
            }
            if (menuTeste != null) {
                menuTeste.setVisible(false);
            }
            if (submenuInterpretacaoLinguistica != null) {
                submenuInterpretacaoLinguistica.setVisible(false);
            }
            if (submenuRegistroGerard != null) {
                submenuRegistroGerard.setVisible(false);
            }
            if (submenuGerardVergnaud != null) {
                submenuGerardVergnaud.setVisible(false);
            }
            if (submenuGerardRecife != null) {
                submenuGerardRecife.setVisible(false);
            }
            if (menuSobre != null) {
                menuSobre.setVisible(true);
                setComponentZOrder(menuSobre, 0);
                repaint();
            }
        }

        private void mostrarSubmenuInterpretacaoLinguistica() {
            cancelarOcultacaoMenus();
            if (menuIdioma != null) {
                menuIdioma.setVisible(false);
            }
            if (menuTipo != null) {
                menuTipo.setVisible(false);
            }
            if (menuTeste != null) {
                menuTeste.setVisible(false);
            }
            if (menuSobre != null) {
                menuSobre.setVisible(true);
                setComponentZOrder(menuSobre, 0);
            }
            if (submenuRegistroGerard != null) {
                submenuRegistroGerard.setVisible(false);
            }
            if (submenuGerardVergnaud != null) {
                submenuGerardVergnaud.setVisible(false);
            }
            if (submenuGerardRecife != null) {
                submenuGerardRecife.setVisible(false);
            }
            if (submenuInterpretacaoLinguistica != null) {
                submenuInterpretacaoLinguistica.setVisible(true);
                setComponentZOrder(submenuInterpretacaoLinguistica, 0);
                repaint();
            }
        }

        private void mostrarSubmenuRegistroGerard() {
            cancelarOcultacaoMenus();
            if (menuIdioma != null) {
                menuIdioma.setVisible(false);
            }
            if (menuTipo != null) {
                menuTipo.setVisible(false);
            }
            if (menuTeste != null) {
                menuTeste.setVisible(false);
            }
            if (menuSobre != null) {
                menuSobre.setVisible(true);
                setComponentZOrder(menuSobre, 0);
            }
            if (submenuInterpretacaoLinguistica != null) {
                submenuInterpretacaoLinguistica.setVisible(false);
            }
            if (submenuGerardVergnaud != null) {
                submenuGerardVergnaud.setVisible(false);
            }
            if (submenuGerardRecife != null) {
                submenuGerardRecife.setVisible(false);
            }
            if (submenuRegistroGerard != null) {
                submenuRegistroGerard.setVisible(true);
                setComponentZOrder(submenuRegistroGerard, 0);
                repaint();
            }
        }

        private void mostrarSubmenuGerardVergnaud() {
            cancelarOcultacaoMenus();
            if (menuIdioma != null) {
                menuIdioma.setVisible(false);
            }
            if (menuTipo != null) {
                menuTipo.setVisible(false);
            }
            if (menuTeste != null) {
                menuTeste.setVisible(false);
            }
            if (menuSobre != null) {
                menuSobre.setVisible(true);
                setComponentZOrder(menuSobre, 0);
            }
            if (submenuInterpretacaoLinguistica != null) {
                submenuInterpretacaoLinguistica.setVisible(false);
            }
            if (submenuRegistroGerard != null) {
                submenuRegistroGerard.setVisible(false);
            }
            if (submenuGerardRecife != null) {
                submenuGerardRecife.setVisible(false);
            }
            if (submenuGerardVergnaud != null) {
                submenuGerardVergnaud.setVisible(true);
                setComponentZOrder(submenuGerardVergnaud, 0);
                repaint();
            }
        }

        private void mostrarSubmenuGerardRecife() {
            cancelarOcultacaoMenus();
            if (menuIdioma != null) {
                menuIdioma.setVisible(false);
            }
            if (menuTipo != null) {
                menuTipo.setVisible(false);
            }
            if (menuTeste != null) {
                menuTeste.setVisible(false);
            }
            if (menuSobre != null) {
                menuSobre.setVisible(true);
                setComponentZOrder(menuSobre, 0);
            }
            if (submenuInterpretacaoLinguistica != null) {
                submenuInterpretacaoLinguistica.setVisible(false);
            }
            if (submenuRegistroGerard != null) {
                submenuRegistroGerard.setVisible(false);
            }
            if (submenuGerardVergnaud != null) {
                submenuGerardVergnaud.setVisible(true);
                setComponentZOrder(submenuGerardVergnaud, 0);
            }
            if (submenuGerardRecife != null) {
                submenuGerardRecife.setVisible(true);
                setComponentZOrder(submenuGerardRecife, 0);
                repaint();
            }
        }

        private void ocultarSubmenusCategoria() {
            if (submenuTipoMedidas != null) {
                submenuTipoMedidas.setVisible(false);
            }
            if (submenuTipoTransformacoes != null) {
                submenuTipoTransformacoes.setVisible(false);
            }
            if (submenuTipoRelacoes != null) {
                submenuTipoRelacoes.setVisible(false);
            }
        }

        private void ocultarMenus() {
            if (menuIdioma != null) {
                menuIdioma.setVisible(false);
            }
            if (menuTipo != null) {
                menuTipo.setVisible(false);
            }
            ocultarSubmenusCategoria();
            if (menuTeste != null) {
                menuTeste.setVisible(false);
            }
            if (menuSobre != null) {
                menuSobre.setVisible(false);
            }
            if (submenuInterpretacaoLinguistica != null) {
                submenuInterpretacaoLinguistica.setVisible(false);
            }
            if (submenuRegistroGerard != null) {
                submenuRegistroGerard.setVisible(false);
            }
            if (submenuGerardVergnaud != null) {
                submenuGerardVergnaud.setVisible(false);
            }
            if (submenuGerardRecife != null) {
                submenuGerardRecife.setVisible(false);
            }
            repaint();
        }

        private void cancelarOcultacaoMenus() {
            if (timerOcultarMenus != null && timerOcultarMenus.isRunning()) {
                timerOcultarMenus.stop();
            }
        }

        private void agendarOcultacaoMenus() {
            cancelarOcultacaoMenus();
            timerOcultarMenus = new javax.swing.Timer(180, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!cursorSobre(botaoIdioma) && !cursorSobre(botaoTipo) && !cursorSobre(botaoTeste) && !cursorSobre(botaoSobre) && !cursorSobre(botaoReportarBug) &&
                        !cursorSobre(menuIdioma) && !cursorSobre(menuTipo) && !cursorSobre(submenuTipoMedidas) &&
                        !cursorSobre(submenuTipoTransformacoes) && !cursorSobre(submenuTipoRelacoes) &&
                        !cursorSobre(menuTeste) && !cursorSobre(menuSobre) &&
                        !cursorSobre(submenuInterpretacaoLinguistica) && !cursorSobre(submenuRegistroGerard) &&
                        !cursorSobre(submenuGerardVergnaud) && !cursorSobre(submenuGerardRecife)) {
                        ocultarMenus();
                    }
                }
            });
            timerOcultarMenus.setRepeats(false);
            timerOcultarMenus.start();
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
            atualizarTextosFixosDaInterface();
            repaint();
        }

        private void trocarIdiomaDaSituacaoSemLog(SituacaoProblemaAditiva versao) {
            cancelarEfeitosArraste();
            situacaoProblemaAtual = versao;
            textoProblemaEhMensagemSistema = false;
            textoProblema = normalizarTextoProblemaParaRenderizacao(versao.getEnunciado());
            resultadoInterpretacao = construtorResultadoCurado.construir(versao, textoProblema);
            definicaoDiagramaAtual = SemanticaCuradaSituacao.aplicarRotulos(
                    catalogoDefinicoesAditivas.obter(tipoSituacaoSelecionada),
                    situacaoProblemaAtual,
                    localizacao
            );
            transformacoesComSinalTransformacaoComposta = extrairTransformacoesComSinalTransformacaoComposta();
            quantidadePassosTransformacaoComposta = calcularQuantidadePassosTransformacaoComposta();
            estadosIntermediariosTransformacaoComposta = calcularEstadosIntermediariosTransformacaoComposta();
            elementoTextoSelecionado = null;
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
            situacaoProblemaAtual = null;
            textoProblema = "";
            textoProblemaEhMensagemSistema = false;
            resultadoInterpretacao = null;
            definicaoDiagramaAtual = null;
            cenaDiagramaAtual = null;
            cenaDiagramaVennAtual = null;
            ultimaAreaDiagramaVenn = null;
            assinaturaDiagramaVennSincronizado = "";

            itensArrastaveis.clear();
            marcadoresFixosTexto.clear();
            elementosTexto.clear();
            elementosVergnaud.clear();
            conectoresVergnaud.clear();
            circulosVenn.clear();
            quadradinhosVenn.clear();
            quadradinhosCorrespondentesComparacao.clear();

            itemSelecionado = null;
            itemFocado = null;
            elementoTextoSelecionado = null;
            elementoTextoFocado = null;
            sessaoArrasteTextoParaDiagrama.limpar();
            quadradinhoVennSelecionado = null;
            quadradinhoVennFocado = null;
            elementoVergnaudSelecionado = null;
            conectorVergnaudSelecionado = null;
            limparRealceAlvoProximidade();
            limparQuestionamentoPersistente();
            limparGraficoInteiros();
            desabilitarSincronizacaoEstadoFinal();
            estadoSemanticoCompartilhado.limpar(tipoSituacaoSelecionada);
            layoutTextoInicializado = false;
            larguraUltimoLayoutTexto = -1;

            ocultarControlesDaAtividadeSemCategoria();
            if (botaoSortear != null) {
                botaoSortear.setEnabled(false);
            }
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
            textoProblema = normalizarTextoProblemaParaRenderizacao(
                    situacaoCuradaDisponivel ? situacao.getEnunciado() : textoAusenciaSituacaoCurada());
            controladorContextoSituacao.registrarNovaSituacao(
                    situacaoProblemaAtual,
                    situacaoCuradaDisponivel ? tipoSituacaoSelecionada.name() : "SEM_SITUACAO_CURADA",
                    textoProblema);
            definicaoDiagramaAtual = SemanticaCuradaSituacao.aplicarRotulos(
                    contexto.getDefinicao(), situacaoProblemaAtual, localizacao);
            resultadoInterpretacao = situacaoCuradaDisponivel ? construtorResultadoCurado.construir(situacao, textoProblema) : null;
            transformacoesComSinalTransformacaoComposta = extrairTransformacoesComSinalTransformacaoComposta();
            quantidadePassosTransformacaoComposta = calcularQuantidadePassosTransformacaoComposta();
            estadosIntermediariosTransformacaoComposta = calcularEstadosIntermediariosTransformacaoComposta();

            itensArrastaveis.clear();
            marcadoresFixosTexto.clear();
            elementosTexto.clear();
            itemSelecionado = null;
            itemFocado = null;
            elementoTextoSelecionado = null;
            elementoTextoFocado = null;
            quadradinhoVennSelecionado = null;
            quadradinhoVennFocado = null;
            elementoVergnaudSelecionado = null;
            conectorVergnaudSelecionado = null;
            limparRealceAlvoProximidade();
            mostrarAnotacaoMouseOver = false;
            limparQuestionamentoPersistente();
            limparGraficoInteiros();
            desabilitarSincronizacaoEstadoFinal();
            estadoSemanticoCompartilhado.limpar(tipoSituacaoSelecionada);
            indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};

            inicializarElementosTexto();
            inicializarDiagramaVergnaud();
            inicializarDiagramaVenn();
            criarMenusInternos();
            atualizarTextosFixosDaInterface();

            Window janela = SwingUtilities.getWindowAncestor(this);
            if (janela instanceof JFrame) {
                ((JFrame) janela).setTitle(localizacao.texto("ui.app.title"));
            }

            repaint();
        }


        private void aplicarIdiomaSelecionadoMantendoEstadoTela() {
            cancelarEfeitosArraste();
            if (!categoriaSelecionadaParaAtividade) {
                localizacao.definirIdioma(idiomaSelecionado);
                criarMenusInternos();
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
                textoProblema = normalizarTextoProblemaParaRenderizacao(situacaoTraduzida.getEnunciado());
                resultadoInterpretacao = contexto.getInterpretacao();
                transformacoesComSinalTransformacaoComposta = extrairTransformacoesComSinalTransformacaoComposta();
                quantidadePassosTransformacaoComposta = calcularQuantidadePassosTransformacaoComposta();
                estadosIntermediariosTransformacaoComposta = calcularEstadosIntermediariosTransformacaoComposta();
                elementoTextoSelecionado = null;
                elementoTextoFocado = null;
                inicializarElementosTexto();
            } else {
                // A ausência de uma versão textual não autoriza restaurar a modelagem.
                // O diagrama permanece exatamente no estado construído pelo usuário.
                textoProblemaEhMensagemSistema = true;
                textoProblema = normalizarTextoProblemaParaRenderizacao(textoAusenciaSituacaoCurada());
                resultadoInterpretacao = null;
                elementosTexto.clear();
                elementoTextoSelecionado = null;
                elementoTextoFocado = null;
                inicializarElementosTexto();
            }

            definicaoDiagramaAtual = SemanticaCuradaSituacao.aplicarRotulos(
                    contexto.getDefinicao(), situacaoProblemaAtual, localizacao);
            atualizarRotulosDiagramaVergnaudSemReposicionar();
            atualizarRotulosDiagramaVennSemLimparQuadradinhos();
            criarMenusInternos();
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
            CenaDiagramaAditivo cenaAtualizada;
            if (usaDiagramasEncadeadosTransformacaoComposta()) {
                cenaAtualizada = criarCenaTransformacaoComposta(area);
            } else if (usaDiagramasComposicaoTransformacaoMedidas()) {
                cenaAtualizada = criarCenaComposicaoTransformacaoMedidas(area);
            } else {
                cenaAtualizada = geradorCenaDiagrama.gerar(tipoSituacaoSelecionada, area, definicaoDiagramaAtual, extrairValoresDoTexto());
            }

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
                    ehProcessoTransformacaoMedidas()
                    ? sincronizadorUnidadesProcessoTransformacao
                            .criarPlano(snapshotTabuleiro, situacaoProblemaAtual)
                    : null;
            planoUnidadesProcessoAtual = planoUnidadesProcesso;
            TipoSituacaoAditiva tipoVenn = usaCenaVergnaudComposta() ? TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS : tipoSituacaoSelecionada;
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
                    circulo.formaRetangular = (tipoVenn == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                        || tipoVenn == TipoSituacaoAditiva.COMPARACAO_MEDIDAS
                        || tipoVenn == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS)
                        && !usaCenaVergnaudComposta();
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

            switch (tipoSituacaoSelecionada) {
                case COMPOSICAO_MEDIDAS:
                    return CategoriaProblema.COMPOSICAO_MEDIDAS;
                case TRANSFORMACAO_MEDIDAS:
                    return CategoriaProblema.TRANSFORMACAO_MEDIDAS;
                case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
                    return CategoriaProblema.COMPOSICAO_TRANSFORMACAO_MEDIDAS;
                case COMPARACAO_MEDIDAS:
                    return CategoriaProblema.COMPARACAO_MEDIDAS;
                case COMPOSICAO_TRANSFORMACOES:
                    return CategoriaProblema.COMPOSICAO_TRANSFORMACOES;
                case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                    return CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS;
                case TRANSFORMACAO_RELACAO:
                    return CategoriaProblema.TRANSFORMACAO_RELACAO;
                case COMPOSICAO_RELACOES:
                    return CategoriaProblema.COMPOSICAO_RELACOES;
                default:
                    return CategoriaProblema.INDEFINIDA;
            }
        }

        private String normalizarTextoProblemaParaRenderizacao(String textoOriginal) {
            if (textoOriginal == null || textoOriginal.trim().length() == 0) {
                return textoOriginal == null ? "" : textoOriginal;
            }

            int quantidadeNumerosArabicos = contarNumerosArabicos(textoOriginal);
            if (quantidadeNumerosArabicos >= 2) {
                return textoOriginal;
            }

            int faltamConverter = 2 - quantidadeNumerosArabicos;
            String[] tokens = textoOriginal.split(" ");

            for (int i = 0; i < tokens.length && faltamConverter > 0; i++) {
                String convertido = converterTokenNumeralParaAlgarismo(tokens[i]);
                if (!convertido.equals(tokens[i])) {
                    tokens[i] = convertido;
                    faltamConverter--;
                }
            }

            StringBuilder textoNormalizado = new StringBuilder();
            for (int i = 0; i < tokens.length; i++) {
                if (i > 0) {
                    textoNormalizado.append(' ');
                }
                textoNormalizado.append(tokens[i]);
            }

            return textoNormalizado.toString();
        }

        private int contarNumerosArabicos(String texto) {
            java.util.regex.Pattern padrao = java.util.regex.Pattern.compile("\\d+");
            java.util.regex.Matcher matcher = padrao.matcher(texto == null ? "" : texto);
            int contador = 0;

            while (matcher.find()) {
                contador++;
            }

            return contador;
        }

        private String converterTokenNumeralParaAlgarismo(String token) {
            if (token == null || token.length() == 0) {
                return token == null ? "" : token;
            }

            int inicio = 0;
            int fim = token.length();

            while (inicio < fim && !Character.isLetter(token.charAt(inicio))) {
                inicio++;
            }

            while (fim > inicio && !Character.isLetter(token.charAt(fim - 1))) {
                fim--;
            }

            if (inicio >= fim) {
                return token;
            }

            String prefixo = token.substring(0, inicio);
            String nucleo = token.substring(inicio, fim);
            String sufixo = token.substring(fim);

            String valor = obterAlgarismoParaPalavraNumeral(nucleo);
            if (valor == null) {
                return token;
            }

            return prefixo + valor + sufixo;
        }

        private String obterAlgarismoParaPalavraNumeral(String palavra) {
            if (palavra == null) {
                return null;
            }

            String normalizada = Normalizer.normalize(palavra, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}+", "")
                    .toLowerCase();

            Map<String, String> mapa = new LinkedHashMap<String, String>();
            mapa.put("zero", "0");
            mapa.put("um", "1");
            mapa.put("uma", "1");
            mapa.put("dois", "2");
            mapa.put("duas", "2");
            mapa.put("tres", "3");
            mapa.put("quatro", "4");
            mapa.put("cinco", "5");
            mapa.put("seis", "6");
            mapa.put("sete", "7");
            mapa.put("oito", "8");
            mapa.put("nove", "9");
            mapa.put("dez", "10");
            mapa.put("onze", "11");
            mapa.put("doze", "12");
            mapa.put("treze", "13");
            mapa.put("catorze", "14");
            mapa.put("quatorze", "14");
            mapa.put("quinze", "15");
            mapa.put("dezesseis", "16");
            mapa.put("dezessete", "17");
            mapa.put("dezoito", "18");
            mapa.put("dezenove", "19");
            mapa.put("vinte", "20");
            mapa.put("trinta", "30");
            mapa.put("quarenta", "40");
            mapa.put("cinquenta", "50");
            mapa.put("sessenta", "60");
            mapa.put("setenta", "70");
            mapa.put("oitenta", "80");
            mapa.put("noventa", "90");
            mapa.put("cem", "100");
            mapa.put("cento", "100");

            return mapa.get(normalizada);
        }

        private boolean usaDiagramasEncadeadosTransformacaoComposta() {
            return tipoSituacaoSelecionada == TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS;
        }

        private boolean usaDiagramasComposicaoTransformacaoMedidas() {
            return tipoSituacaoSelecionada == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS;
        }

        private boolean usaCenaVergnaudComposta() {
            return usaDiagramasEncadeadosTransformacaoComposta() || usaDiagramasComposicaoTransformacaoMedidas();
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

        private int calcularQuantidadePassosTransformacaoComposta() {
            if (usaDiagramasComposicaoTransformacaoMedidas()) {
                return 2;
            }
            if (!usaDiagramasEncadeadosTransformacaoComposta()) {
                return 1;
            }

            if (!transformacoesComSinalTransformacaoComposta.isEmpty()) {
                return Math.max(2, transformacoesComSinalTransformacaoComposta.size());
            }

            int[] numeros = extrairTodosNumerosDoTexto();
            if (numeros.length <= 1) {
                return 2;
            }

            return Math.max(2, numeros.length - 1);
        }

        private java.util.List<Integer> calcularEstadosIntermediariosTransformacaoComposta() {
            java.util.List<Integer> estados = new ArrayList<Integer>();

            if (!usaDiagramasEncadeadosTransformacaoComposta()) {
                return estados;
            }

            int[] numeros = extrairTodosNumerosDoTexto();
            if (numeros.length == 0) {
                return estados;
            }

            int acumulado = numeros[0];
            java.util.List<Integer> transformacoes = transformacoesComSinalTransformacaoComposta;
            if (transformacoes == null || transformacoes.isEmpty()) {
                transformacoes = extrairTransformacoesComSinalTransformacaoComposta();
            }

            for (int i = 0; i < transformacoes.size(); i++) {
                acumulado += transformacoes.get(i);
                estados.add(acumulado);
            }

            return estados;
        }

        private java.util.List<Integer> extrairTransformacoesComSinalTransformacaoComposta() {
            java.util.List<Integer> transformacoes = new ArrayList<Integer>();

            if (!usaDiagramasEncadeadosTransformacaoComposta()) {
                return transformacoes;
            }

            String texto = textoProblema == null ? "" : textoProblema;
            String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}+", "")
                    .toLowerCase();

            java.util.regex.Pattern padrao = java.util.regex.Pattern.compile("\\d+");
            java.util.regex.Matcher matcher = padrao.matcher(textoNormalizado);
            java.util.List<Integer> valores = new ArrayList<Integer>();
            java.util.List<Integer> inicios = new ArrayList<Integer>();
            java.util.List<Integer> fins = new ArrayList<Integer>();

            while (matcher.find()) {
                valores.add(Integer.parseInt(matcher.group()));
                inicios.add(matcher.start());
                fins.add(matcher.end());
            }

            if (valores.size() <= 1) {
                return transformacoes;
            }

            int sinalAnterior = detectarSinalPadraoTransformacaoComposta(textoNormalizado);

            for (int i = 1; i < valores.size(); i++) {
                int inicioJanela = i == 1 ? Math.max(0, inicios.get(i) - 28) : Math.max(0, fins.get(i - 1));
                int fimJanela = Math.min(textoNormalizado.length(), fins.get(i) + 24);
                String contexto = textoNormalizado.substring(inicioJanela, fimJanela);
                int sinal = inferirSinalPassoTransformacao(contexto, sinalAnterior);
                transformacoes.add(valores.get(i) * sinal);
                sinalAnterior = sinal;
            }

            return transformacoes;
        }

        private int detectarSinalPadraoTransformacaoComposta(String texto) {
            if (texto == null) {
                return 1;
            }

            String[] negativas = new String[] {"comeu", "perdeu", "gastou", "tirou", "retirou", "deu", "emprestou", "tomou", "vendeu", "foi vendido", "foram vendidos", "sobraram", "sobrou", "restaram", "restou", "diminuiu", "removeu", "usou"};
            for (String termo : negativas) {
                if (texto.contains(termo)) {
                    return -1;
                }
            }

            String[] positivas = new String[] {"ganhou", "recebeu", "comprou", "juntou", "acrescentou", "colocou", "foram postas", "foi posta", "mais", "chegaram", "adicionou", "aumentou"};
            for (String termo : positivas) {
                if (texto.contains(termo)) {
                    return 1;
                }
            }

            return 1;
        }

        private int inferirSinalPassoTransformacao(String contexto, int sinalPadrao) {
            if (contexto == null || contexto.trim().length() == 0) {
                return sinalPadrao;
            }

            String[] negativas = new String[] {"comeu", "perdeu", "gastou", "tirou", "retirou", "deu", "emprestou", "tomou", "vendeu", "foi vendido", "foram vendidos", "diminuiu", "removeu", "usou"};
            for (String termo : negativas) {
                if (contexto.contains(termo)) {
                    return -1;
                }
            }

            String[] positivas = new String[] {"ganhou", "recebeu", "comprou", "juntou", "acrescentou", "colocou", "foram postas", "foi posta", "mais", "chegaram", "adicionou", "aumentou"};
            for (String termo : positivas) {
                if (contexto.contains(termo)) {
                    return 1;
                }
            }

            return sinalPadrao;
        }

        private String montarResumoPassosTransformacaoComposta() {
            if (!usaDiagramasEncadeadosTransformacaoComposta()) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= quantidadePassosTransformacaoComposta; i++) {
                if (i > 1) {
                    sb.append(" • ");
                }
                sb.append(localizacao.formatar("ui.step.label", i));
            }
            return sb.toString();
        }

        private void atualizarTextosFixosDaInterface() {
            atualizarTextosTipConclusaoModelagem();
            if (botaoIdioma != null) {
                botaoIdioma.setText(idiomaSelecionado.getRotuloBotao());
                botaoIdioma.setToolTipText(localizacao.texto("ui.tooltip.language"));
            }
            if (botaoTipo != null) {
                botaoTipo.setText(localizacao.texto("ui.button.category"));
                botaoTipo.setToolTipText(localizacao.texto("ui.tooltip.type"));
            }
            if (botaoSobre != null) {
                botaoSobre.setText(localizacao.texto("ui.button.about"));
                botaoSobre.setToolTipText(localizacao.texto("ui.tooltip.about"));
            }
            if (botaoSortear != null) {
                botaoSortear.setText(localizacao.texto("ui.button.random"));
                botaoSortear.setToolTipText(localizacao.texto("ui.tooltip.random"));
                botaoSortear.setEnabled(categoriaSelecionadaParaAtividade);
            }
            if (botaoReportarBug != null) {
                botaoReportarBug.setText(localizacao.texto("ui.button.reportBug"));
                botaoReportarBug.setToolTipText(localizacao.texto("ui.tooltip.reportBug"));
                botaoReportarBug.getAccessibleContext().setAccessibleName(localizacao.texto("ui.button.reportBug"));
                botaoReportarBug.getAccessibleContext().setAccessibleDescription(localizacao.texto("ui.tooltip.reportBug"));
            }
            if (botaoVisaoPesquisador != null) {
                botaoVisaoPesquisador.setText(localizacao.texto("pesq.button.open"));
                botaoVisaoPesquisador.setToolTipText(localizacao.texto("pesq.tooltip.open"));
            }
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
            atualizarTextosBotoesAjudaContextual();
            fecharMenuAjudaContextual();
            atualizarTextoBotaoTeste();
        }

        private void atualizarTextoBotaoTeste() {
            if (botaoTeste == null) {
                return;
            }
            String modo = localizacao.texto(modoFeedbackTeste.getChave());
            botaoTeste.setText(localizacao.formatar("ui.button.test", modo));
            botaoTeste.setToolTipText(localizacao.texto("ui.tooltip.test") + ": " + modo);
        }

        private void estilizarBotaoPrincipal(final JButton botao) {
            botao.setFocusPainted(false);
            botao.setForeground(COR_TEXTO);
            botao.setBackground(COR_BOTAO_SECUNDARIO);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA_BOTAO),
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
            ));
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.setOpaque(true);
            botao.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    botao.setBackground(COR_DESTAQUE);
                }
                public void mouseExited(MouseEvent e) {
                    botao.setBackground(COR_BOTAO_SECUNDARIO);
                }
            });
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
            desenharGraficoInteiros(g2);
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

        private void desenharTextoProblema(Graphics2D g2) {
            marcadoresFixosTexto.clear();

            if (!categoriaSelecionadaParaAtividade) {
                // Mantém a divisão visual da interface desde a inicialização.
                // Somente o conteúdo educativo permanece ausente até que o
                // usuário escolha uma categoria.
                desenharCard(g2, 15, 55, getWidth() - 30, 135, 18);
                ocultarControlesDaAtividadeSemCategoria();
                return;
            }

            // Reserva uma faixa à esquerda para as ações contextuais.
            int margemX = 94;
            // A categoria funciona como rótulo de contexto acima do enunciado.
            // Diagramas compostos reservam uma segunda linha para o resumo dos passos.
            int yInicial = (usaDiagramasEncadeadosTransformacaoComposta()
                    || usaDiagramasComposicaoTransformacaoMedidas()) ? 113 : 101;
            int larguraMaxima = getWidth() - margemX - 30;

            desenharCard(g2, 15, 55, getWidth() - 30, 135, 18);
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

                if (elemento != elementoTextoSelecionado) {
                    desenharElementoTextoMovel(g2, fm, elemento);
                }
                marcarElementoSemanticoDoTexto(fm, elemento);
            }

            desenharTextoCategoriaNaAreaDoEnunciado(g2, fm, margemX);
            desenharMarcadoresFixosDoTexto(g2);
        }

        private void reposicionarBotaoCorrigirCuradoria(FontMetrics fm, int margemX, int larguraMaxima) {
            if (botaoCorrigirCuradoria == null) {
                return;
            }
            boolean exibir = situacaoProblemaAtual != null && !textoProblemaEhMensagemSistema;
            botaoCorrigirCuradoria.setVisible(exibir);
            botaoCorrigirCuradoria.setEnabled(exibir);
            if (exibir) {
                botaoCorrigirCuradoria.setBounds(27, 101, 26, 26);
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
                botaoRestaurar.setBounds(27, 70, 26, 26);
            }
        }

        private void restaurarElementosForaDoDiagrama() {
            itemSelecionado = null;
            elementoTextoSelecionado = null;
            quadradinhoVennSelecionado = null;
            elementoVergnaudSelecionado = null;
            conectorVergnaudSelecionado = null;
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
                    if (item == itemGraficoInteiros) {
                        limparGraficoInteiros();
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
            itemSelecionado = null;
            itemFocado = null;
            itemGraficoInteiros = null;
            numeroRelativoGraficoInteiros = null;
            elementoTextoSelecionado = null;
            elementoTextoFocado = null;
            quadradinhoVennSelecionado = null;
            quadradinhoVennFocado = null;
            elementoVergnaudSelecionado = null;
            conectorVergnaudSelecionado = null;
            alvoRealcadoPorProximidade = null;

            itensArrastaveis.clear();
            marcadoresFixosTexto.clear();
            elementosTexto.clear();
            elementosVergnaud.clear();
            conectoresVergnaud.clear();
            circulosVenn.clear();
            quadradinhosVenn.clear();

            limparRealceAlvoProximidade();
            limparQuestionamentoPersistente();
            limparGraficoInteiros();
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
            int indice = elementosVergnaud.indexOf(elemento);
            if (indice < 0) return false;
            return politicaPreenchimentoIncognita.ehPapelDaIncognita(
                    obterPapelElementoParaConclusao(indice),
                    obterPapelIncognitaAtual());
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
                int indice = elementosVergnaud.indexOf(alvo);
                if (indice >= 0 && politicaPreenchimentoIncognita
                        .ehPapelDaIncognita(
                                obterPapelElementoParaConclusao(indice),
                                papelIncognita)) {
                    return true;
                }
            }
            return false;
        }

        private boolean devePreservarMarcadorIncognita(
                ElementoVergnaud elemento) {
            if (elemento == null) return false;
            int indice = elementosVergnaud.indexOf(elemento);
            if (indice < 0) return false;
            ItemTextoArrastavel incognita =
                    encontrarIncognitaOriginalSobreElemento(elemento);
            boolean preenchida = (incognita != null
                    && incognita.isPreenchidoPeloProtocoloMouseTexto())
                    || incognitaPreenchidaPeloProtocoloMouseTexto();
            return politicaPreenchimentoIncognita.devePreservarMarcador(
                    obterPapelElementoParaConclusao(indice),
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
            return scaffoldingQuestionamento.obterChavePapelDoElemento(
                    tipoSituacaoSelecionada,
                    indiceElemento,
                    usaDiagramasEncadeadosTransformacaoComposta(),
                    quantidadePassosTransformacaoComposta);
        }

        private boolean papelValidoParaConclusao(String papel) {
            return papel != null && papel.trim().length() > 0
                    && !"papel.valor".equals(papel.trim());
        }

        private java.util.List<String> capturarPapeisEsperadosConclusao() {
            java.util.List<String> papeis = new ArrayList<String>();
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                String papel = obterPapelElementoParaConclusao(i);
                if (papelValidoParaConclusao(papel)) {
                    papeis.add(papel.trim());
                }
            }
            return papeis;
        }

        private java.util.List<EstadoPosicionamentoModelagem> capturarPosicionamentosConclusao() {
            java.util.List<EstadoPosicionamentoModelagem> estados =
                    new ArrayList<EstadoPosicionamentoModelagem>();
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                ElementoVergnaud alvo = elementosVergnaud.get(i);
                String papelAlvo = obterPapelElementoParaConclusao(i);
                if (!papelValidoParaConclusao(papelAlvo)) {
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
                            item.isPreenchidoPeloProtocoloMouseTexto()));
                } else {
                    String valorDigitado = alvo.textoEditavel == null
                            ? "" : alvo.textoEditavel.trim();
                    estados.add(new EstadoPosicionamentoModelagem(
                            papelAlvo,
                            papelAlvo,
                            valorDigitado,
                            valorDigitado.length() > 0));
                }
            }
            return estados;
        }

        private void verificarConclusaoModelagem() {
            AtualizacaoConclusaoModelagem atualizacao = controladorConclusaoModelagem.atualizar(
                    capturarPapeisEsperadosConclusao(),
                    capturarPosicionamentosConclusao());
            boolean concluida = controladorConclusaoModelagem.isConcluida();
            if (!concluida) {
                aplicadorDestaqueConclusaoDiagrama.aplicar(
                        false, elementosVergnaud, conectoresVergnaud, itensArrastaveis,
                        quadradinhosVenn);
            }

            if (!concluida) {
                sequenciadorFeedbackConclusao.cancelar();
            } else if (atualizacao == AtualizacaoConclusaoModelagem.CONCLUIDA_AGORA
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

        private void desenharTextoCategoriaNaAreaDoEnunciado(Graphics2D g2, FontMetrics fm, int margemX) {
            if (definicaoDiagramaAtual == null) {
                areaTituloCategoria.limpar();
                return;
            }

            String textoCategoria = definicaoDiagramaAtual.getTitulo();
            String descricaoCategoria = cenaDiagramaAtual != null ? cenaDiagramaAtual.getDescricao() : "";
            String descricaoSubtipo = obterDescricaoSubtipoAtual();

            String linhaResumoAbaixoDoTitulo = null;
            if (usaDiagramasEncadeadosTransformacaoComposta()) {
                linhaResumoAbaixoDoTitulo =
                        localizacao.formatar("ui.compound.steps", montarResumoPassosTransformacaoComposta());
            } else if (usaDiagramasComposicaoTransformacaoMedidas()) {
                linhaResumoAbaixoDoTitulo = localizacao.texto("ui.compositionTransformation.steps");
            }

            areaTituloCategoria.desenhar(g2, margemX, textoCategoria, descricaoCategoria,
                    descricaoSubtipo, COR_TEXTO_SECUNDARIO, linhaResumoAbaixoDoTitulo);
        }

        private boolean pontoNoTituloCategoriaEnunciado(int x, int y) {
            return areaTituloCategoria.contemPonto(x, y);
        }

        private String obterDescricaoSubtipoAtual() {
            if (resultadoInterpretacao == null || resultadoInterpretacao.getSubtipoVergnaud() == null) {
                return "";
            }
            return resultadoInterpretacao.getSubtipoFormatado();
        }

        private int obterIndiceFiguraIncognitaAtual() {
            if (usaDiagramasComposicaoTransformacaoMedidas()) {
                return 5;
            }
            if (usaDiagramasEncadeadosTransformacaoComposta()) {
                return quantidadePassosTransformacaoComposta * 3 - 1;
            }
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
                                obterChavePapelExataPorValor("?"),
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
            if (!textoProblemaEhMensagemSistema && (elemento == elementoTextoSelecionado || elemento == elementoTextoFocado)) {
                Stroke original = g2.getStroke();

                if (elemento == elementoTextoSelecionado) {
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
            adicionarMarcadorComPapel(fm, palavra, xPalavra, yBase, indiceInicio, indiceFim, valor, editavel, obterChavePapelExataPorIndice(marcadoresFixosTexto.size()));
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
                return obterChavePapelExataPorValor("?");
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
            g2.setColor(COR_FUNDO);
            g2.fillRect(0, 210, getWidth(), getHeight() - 210);

            g2.setColor(COR_BORDA);
            g2.drawLine(0, 210, getWidth(), 210);

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
                g2.drawLine(xDivisor, 222, xDivisor, getHeight() - 14);

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
                g2.drawLine(xDivisorDiagramas, 222, xDivisorDiagramas, getHeight() - 14);
            }

            Rectangle limiteVergnaud = obterAreaVisivelDiagramasVergnaud();
            reposicionarBotaoRestaurarDiagrama(limiteVergnaud);
            reposicionarBotaoAjudaVergnaud(limiteVergnaud);
            if (!deveExibirDiagramaComplementar() && botaoAjudaComplementar != null) {
                botaoAjudaComplementar.setVisible(false);
            }
            desenharCard(g2, limiteVergnaud.x, limiteVergnaud.y, limiteVergnaud.width, limiteVergnaud.height, 18);

            if (cenaDiagramaAtual == null || elementosVergnaud.isEmpty()) {
                inicializarDiagramaVergnaud();
            }

            if (usaDiagramasEncadeadosTransformacaoComposta()) {
                desenharRotulosPassosNoDiagrama(g2);
            } else if (usaDiagramasComposicaoTransformacaoMedidas()) {
                desenharRotulosComposicaoTransformacaoNoDiagrama(g2);
            }

            for (ConectorVergnaud conector : conectoresVergnaud) {
                if (conector != conectorVergnaudSelecionado) {
                    conector.desenhar(g2);
                }
            }
            for (ElementoVergnaud elemento : elementosVergnaud) {
                if (elemento != elementoVergnaudSelecionado) {
                    elemento.desenhar(g2);
                }
            }
        }

        private void desenharRotulosPassosNoDiagrama(Graphics2D g2) {
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.setColor(COR_TEXTO_SECUNDARIO);

            for (int passo = 0; passo < quantidadePassosTransformacaoComposta; passo++) {
                Rectangle subarea = obterSubareaPassoTransformacaoComposta(passo);
                String rotulo = localizacao.formatar("ui.step.label", passo + 1);
                g2.drawString(rotulo, subarea.x + 12, subarea.y + 16);
            }
        }

        private void desenharRotulosComposicaoTransformacaoNoDiagrama(Graphics2D g2) {
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.setColor(COR_TEXTO_SECUNDARIO);

            Rectangle subareaComposicao = obterSubareaPassoTransformacaoComposta(0);
            Rectangle subareaTransformacao = obterSubareaPassoTransformacaoComposta(1);
            g2.drawString(localizacao.texto("ui.diagram.label.composition"), subareaComposicao.x + 12, subareaComposicao.y + 16);
            g2.drawString(localizacao.texto("ui.diagram.label.transformation"), subareaTransformacao.x + 12, subareaTransformacao.y + 16);
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

        private void desenharGraficoInteiros(Graphics2D g2) {
            scaffoldingGraficoInteiros.desenhar(
                    g2,
                    getWidth(),
                    getHeight(),
                    obterAreaVisivelDiagramasVergnaud()
            );
        }

        private void desenharElementos(Graphics2D g2) {
            for (int i = 0; i < itensArrastaveis.size(); i++) {
                ItemTextoArrastavel item = itensArrastaveis.get(i);
                if (item != itemSelecionado) {
                    item.desenhar(g2);
                }
            }

            if (itemFocado != null && itemFocado != itemSelecionado) {
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

            if (elementoTextoSelecionado != null) {
                final ElementoTextoMovel elemento = elementoTextoSelecionado;
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

            if (quadradinhoVennSelecionado != null) {
                final QuadradinhoVenn quadradinho = quadradinhoVennSelecionado;
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

            if (itemSelecionado != null) {
                final ItemTextoArrastavel item = itemSelecionado;
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
            if (proxyEmFeedback != null && proxyEmFeedback != itemSelecionado) {
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

            if (elementoVergnaudSelecionado != null) {
                final ElementoVergnaud elemento = elementoVergnaudSelecionado;
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        int margemSuperior = elemento.rotulosAcima ? 42 : 8;
                        int margemInferior = elemento.rotulosAcima ? 8 : 42;
                        return new Rectangle(
                                elemento.x - 8,
                                elemento.y - margemSuperior,
                                Math.max(1, elemento.largura + 16),
                                Math.max(1, elemento.altura + margemSuperior + margemInferior));
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        elemento.desenhar(grafico);
                    }
                });
            }

            if (conectorVergnaudSelecionado != null) {
                final ConectorVergnaud conector = conectorVergnaudSelecionado;
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        return obterLimitesVisuaisConector(conector);
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        conector.desenhar(grafico);
                    }
                });
            }

            if (scaffoldingGraficoInteiros.estaArrastandoPontoControle()) {
                renderizadorPickup.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
                    public Rectangle obterLimitesVisuais() {
                        return scaffoldingGraficoInteiros.obterAreaVisualPontoControle();
                    }
                    public void desenharConteudo(Graphics2D grafico) {
                        scaffoldingGraficoInteiros.desenharPontoControleEmPrimeiroPlano(grafico);
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
            if (itemSelecionado != null) {
                desenharIndicacaoEstiloDuranteArraste(g2, itemSelecionado);
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
            Stroke original = g2.getStroke();
            Color corOriginal = g2.getColor();
            int itemCx = item.x + item.largura / 2;
            int itemCy = item.y + item.altura / 2;
            int alvoCx = alvo.x + alvo.largura / 2;
            int alvoCy = alvo.y + alvo.altura / 2;

            if (modoFeedbackTeste == EstiloInteracao.PROXIMIDADE && proximo) {
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[]{5.0f, 5.0f}, 0.0f));
                g2.setColor(COR_PRIMARIA);
                g2.drawLine(itemCx, itemCy, alvoCx, alvoCy);
                g2.drawRoundRect(alvo.x - 4, alvo.y - 4, alvo.largura + 8, alvo.altura + 8, 10, 10);
            } else if (modoFeedbackTeste == EstiloInteracao.DROP_TARGET_HIGHLIGHTING && dentro) {
                Composite compostoOriginal = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.16f));
                g2.setColor(COR_PRIMARIA);
                g2.fillRoundRect(alvo.x + 2, alvo.y + 2, alvo.largura - 4, alvo.altura - 4, 10, 10);
                g2.setComposite(compostoOriginal);
                g2.setStroke(new BasicStroke(2.4f));
                g2.setColor(COR_PRIMARIA);
                int px = alvo.x + Math.max(5, (alvo.largura - item.largura) / 2);
                int py = alvo.y + Math.max(5, (alvo.altura - item.altura) / 2);
                g2.drawRoundRect(px, py, Math.max(4, Math.min(item.largura, alvo.largura - 10)),
                        Math.max(4, Math.min(item.altura, alvo.altura - 10)), 8, 8);
            } else if (modoFeedbackTeste == EstiloInteracao.DRAG_OVER_FEEDBACK && (proximo || dentro)) {
                g2.setStroke(new BasicStroke(dentro ? 3.0f : 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COR_PRIMARIA);
                g2.drawLine(itemCx, itemCy, alvoCx, alvoCy);
                g2.drawRoundRect(alvo.x - (dentro ? 5 : 3), alvo.y - (dentro ? 5 : 3),
                        alvo.largura + (dentro ? 10 : 6), alvo.altura + (dentro ? 10 : 6), 10, 10);
                g2.fillOval(alvoCx - (dentro ? 5 : 3), alvoCy - (dentro ? 5 : 3),
                        dentro ? 10 : 6, dentro ? 10 : 6);
            } else if (modoFeedbackTeste == EstiloInteracao.AFFORDANCE) {
                // Indica disponibilidade por cantos de enquadramento. Evita o sinal "+",
                // que pode ser confundido com o sinal positivo da representação formal.
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COR_PRIMARIA);
                desenharCantosDeEnquadramento(g2, alvo);
            } else if (modoFeedbackTeste == EstiloInteracao.SNAP_TO_TARGET && proximo) {
                g2.setStroke(new BasicStroke(2.0f));
                g2.setColor(COR_PRIMARIA);
                desenharSetaCurta(g2, itemCx, itemCy, alvoCx, alvoCy);
            }

            g2.setStroke(original);
            g2.setColor(corOriginal);
        }

        private void desenharCantosDeEnquadramento(Graphics2D g2, ElementoVergnaud alvo) {
            int margem = 5;
            int comprimento = Math.max(6, Math.min(10, Math.min(alvo.largura, alvo.altura) / 4));
            int esquerda = alvo.x + margem;
            int direita = alvo.x + alvo.largura - margem;
            int topo = alvo.y + margem;
            int base = alvo.y + alvo.altura - margem;

            // Canto superior esquerdo.
            g2.drawLine(esquerda, topo, esquerda + comprimento, topo);
            g2.drawLine(esquerda, topo, esquerda, topo + comprimento);
            // Canto superior direito.
            g2.drawLine(direita - comprimento, topo, direita, topo);
            g2.drawLine(direita, topo, direita, topo + comprimento);
            // Canto inferior esquerdo.
            g2.drawLine(esquerda, base, esquerda + comprimento, base);
            g2.drawLine(esquerda, base - comprimento, esquerda, base);
            // Canto inferior direito.
            g2.drawLine(direita - comprimento, base, direita, base);
            g2.drawLine(direita, base - comprimento, direita, base);
        }

        private void desenharSetaCurta(Graphics2D g2, int x1, int y1, int x2, int y2) {
            g2.drawLine(x1, y1, x2, y2);
            double angulo = Math.atan2(y2 - y1, x2 - x1);
            int tamanho = 9;
            int ax1 = x2 - (int) Math.round(tamanho * Math.cos(angulo - Math.PI / 6));
            int ay1 = y2 - (int) Math.round(tamanho * Math.sin(angulo - Math.PI / 6));
            int ax2 = x2 - (int) Math.round(tamanho * Math.cos(angulo + Math.PI / 6));
            int ay2 = y2 - (int) Math.round(tamanho * Math.sin(angulo + Math.PI / 6));
            g2.drawLine(x2, y2, ax1, ay1);
            g2.drawLine(x2, y2, ax2, ay2);
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

            if (!usarQuestionamentoPersistente && !usarLimiteQuantidadePersistente
                    && !mostrarAnotacaoMouseOver) {
                return;
            }

            String mensagem = usarQuestionamentoPersistente
                    ? textoQuestionamentoPersistente
                    : (usarLimiteQuantidadePersistente
                            ? textoLimiteQuantidadeQuestionado
                            : textoAnotacaoMouseOver);

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
                        ? controleSinalProcessoTransformacao.obterAreaAdicionar(
                                circulosVenn,
                                obterEstadoVisualProcessoTransformacao())
                        : controleAdicionarQuadradinhoVenn.obterArea(
                                representacaoLimite,
                                obterAreaDiagramaAditivo());
                baseX = areaControle.x + areaControle.width;
                baseY = Math.max(50, areaControle.y + areaControle.height / 2);
            }

            int x = baseX + 14;
            int y = baseY - altura - 10;

            if (x + largura > getWidth() - 8) {
                x = getWidth() - largura - 8;
            }

            if (x < 8) {
                x = 8;
            }

            if (y < 50) {
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
            if (!aplicandoEstadoSemanticoCompartilhado) {
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
                    ehProcessoTransformacaoMedidas()
                    ? sincronizadorUnidadesProcessoTransformacao
                            .criarPlano(snapshotTabuleiro, situacaoProblemaAtual)
                    : null;
            planoUnidadesProcessoAtual = planoUnidadesProcesso;
            TipoSituacaoAditiva tipoVenn = usaCenaVergnaudComposta() ? TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS : tipoSituacaoSelecionada;
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
                circulo.formaRetangular = ehProcessoTransformacaoMedidas()
                        || ((tipoVenn == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                                || tipoVenn == TipoSituacaoAditiva.COMPARACAO_MEDIDAS)
                                && !usaCenaVergnaudComposta());
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
            int absoluto = Math.abs(valorRelativo);

            if (maximo > 0) {
                proporcaoControleComparacao = Math.max(0.0, Math.min(1.0, absoluto / (double) maximo));
                ultimoValorInteiroControleComparacao = Math.max(0, Math.min(maximo, absoluto));
            } else {
                proporcaoControleComparacao = 0.0;
                ultimoValorInteiroControleComparacao = 0;
            }

            if (elementosVergnaud != null && elementosVergnaud.size() >= 2) {
                ElementoVergnaud numeroRelativo = elementosVergnaud.get(1);
                if (ehElementoNumeroRelativo(numeroRelativo)) {
                    String textoRelativo = formatarValorRelativoParaDiagrama(valorRelativo);
                    String base = scaffoldingNumeroRelativo.removerSinal(textoRelativo);
                    String sinal = obterSinalAtual(textoRelativo);
                    ItemTextoArrastavel item = encontrarItemSobreElemento(numeroRelativo);
                    registrarEscolhaGraficoInteiros(item, numeroRelativo, base, sinal);
                }
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
            return estadoSemanticoCompartilhado.atualizar(
                    tipoSituacaoSelecionada, valores, conhecidos,
                    indicePapelAlterado, origem,
                    obterIndiceIncognitaProtegidaNoEstadoCompartilhado(),
                    incognitaPreenchidaPeloProtocoloMouseTexto());
        }

        private void atualizarIndicesEstadoCompartilhado(int indiceAlteradoReal) {
            if (usaDiagramasComposicaoTransformacaoMedidas()
                    && elementosVergnaud != null && elementosVergnaud.size() >= 6) {
                indicesElementosEstadoCompartilhado = new int[] {3, 4, 5};
                return;
            }
            if (usaDiagramasEncadeadosTransformacaoComposta()
                    && elementosVergnaud != null && elementosVergnaud.size() >= 3) {
                int indiceBase = indiceAlteradoReal;
                if (indiceBase < 0 && numeroRelativoGraficoInteiros != null) {
                    indiceBase = elementosVergnaud.indexOf(numeroRelativoGraficoInteiros);
                }
                if (indiceBase < 0) {
                    indiceBase = indicesElementosEstadoCompartilhado[0];
                }
                int inicio = Math.max(0, (indiceBase / 3) * 3);
                if (inicio + 2 >= elementosVergnaud.size()) {
                    inicio = 0;
                }
                indicesElementosEstadoCompartilhado = new int[] {inicio, inicio + 1, inicio + 2};
                return;
            }
            indicesElementosEstadoCompartilhado = new int[] {0, 1, 2};
        }

        private int converterIndiceRealParaPapel(int indiceReal) {
            return gerard.ui.enunciado.ConversorIndiceEstadoCompartilhado
                    .converterIndiceRealParaPapel(indicesElementosEstadoCompartilhado, indiceReal);
        }

        private EstadoSemanticoCompartilhado.Snapshot capturarEstadoCompartilhadoDoDiagramaComplementar(
                int indiceAlteradoVisual, EstadoSemanticoCompartilhado.Origem origem) {
            Integer[] valores = new Integer[] { null, null, null };
            boolean[] conhecidos = new boolean[] { false, false, false };
            EstadoSemanticoCompartilhado.Snapshot anterior =
                    estadoSemanticoCompartilhado.snapshot();
            MapeamentoPapeisRepresentacaoComplementar mapeamento =
                    obterMapeamentoPapeisComplementaresAtual();

            if (circulosVenn != null) {
                for (int indiceVisual = 0;
                        indiceVisual < 3 && indiceVisual < circulosVenn.size();
                        indiceVisual++) {
                    int indiceSemantico = mapeamento.paraIndiceSemantico(
                            indiceVisual);
                    if (indiceSemantico < 0) {
                        continue;
                    }
                    CirculoVenn no = circulosVenn.get(indiceVisual);
                    if (no.exibirQuadradinhos) {
                        int quantidade = contarQuadradinhosNoCirculo(no);
                        if (ehProcessoTransformacaoMedidas()
                                && planoUnidadesProcessoAtual != null) {
                            quantidade = planoUnidadesProcessoAtual
                                    .converterUnidadesParaValor(quantidade);
                        }
                        if (ehProcessoTransformacaoMedidas()
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
                        conhecidos[indiceSemantico] = Math.abs(quantidade) > 0
                                || indiceVisual == indiceAlteradoVisual
                                || (anterior != null
                                    && anterior.isConhecido(indiceSemantico));
                    } else {
                        Integer editado = converterTextoParaInteiro(
                                no.textoEditavel);
                        if (editado != null) {
                            valores[indiceSemantico] = editado;
                            conhecidos[indiceSemantico] = true;
                        } else if (no.valorReferencia != 0
                                || indiceVisual == indiceAlteradoVisual
                                || (anterior != null
                                    && anterior.isConhecido(indiceSemantico))) {
                            valores[indiceSemantico] = Integer.valueOf(
                                    no.valorReferencia);
                            conhecidos[indiceSemantico] = true;
                        }
                    }
                }
            }

            int indiceAlteradoSemantico = mapeamento.paraIndiceSemantico(
                    indiceAlteradoVisual);
            return estadoSemanticoCompartilhado.atualizar(
                    tipoSituacaoSelecionada, valores, conhecidos,
                    indiceAlteradoSemantico, origem,
                    obterIndiceIncognitaProtegidaNoEstadoCompartilhado(),
                    incognitaPreenchidaPeloProtocoloMouseTexto());
        }

        private void aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(
                EstadoSemanticoCompartilhado.Snapshot snapshot,
                boolean reconstruirComplementar) {
            if (snapshot == null || aplicandoEstadoSemanticoCompartilhado) {
                return;
            }
            aplicandoEstadoSemanticoCompartilhado = true;
            try {
                if (elementosVergnaud != null && elementosVergnaud.size() >= 3) {
                    for (int i = 0; i < 3; i++) {
                        if (!snapshot.isConhecido(i)) {
                            continue;
                        }
                        int indiceReal = indicesElementosEstadoCompartilhado[i];
                        if (indiceReal < 0 || indiceReal >= elementosVergnaud.size()) {
                            continue;
                        }
                        int valor = snapshot.valorOuZero(i);
                        ElementoVergnaud elemento = elementosVergnaud.get(indiceReal);
                        if (ehElementoNumeroRelativo(elemento)) {
                            definirValorNoElementoNumeroRelativo(elemento, valor, true);
                        } else {
                            definirValorNoElementoMedida(elemento,
                                    servicoQuantidadeContextual.formatarInteiroLegado(
                                            Math.max(0, valor),
                                            situacaoProblemaAtual, false));
                        }
                    }
                }
                sincronizarElementosSemanticosDoTexto(snapshot);
                if (reconstruirComplementar) {
                    sincronizarDiagramaVennComRepresentacoes(true);
                }
                sincronizarEixosComEstadoCompartilhado(snapshot);
            } finally {
                aplicandoEstadoSemanticoCompartilhado = false;
            }
        }

        private void sincronizarElementosSemanticosDoTexto(
                final EstadoSemanticoCompartilhado.Snapshot snapshot) {
            if (snapshot == null || textoProblemaEhMensagemSistema) {
                return;
            }

            MapeadorPapelSemanticoTexto mapeador =
                    new gerard.ui.enunciado.MapeadorPapelSemanticoTextoPadrao(
                            scaffoldingQuestionamento,
                            tipoSituacaoSelecionada,
                            usaDiagramasEncadeadosTransformacaoComposta(),
                            quantidadePassosTransformacaoComposta,
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
            String texto = formatarValorRelativoParaDiagrama(valor);
            registrarEscolhaGraficoInteiros(
                    encontrarItemSobreElemento(relacao), relacao,
                    scaffoldingNumeroRelativo.removerSinal(texto),
                    obterSinalAtual(texto));
            if (ehGraficoBarrasComparacao()) {
                int maximo = Math.max(1, obterValorMaximoEscalaComparacao());
                proporcaoControleComparacao = Math.max(0.0,
                        Math.min(1.0, Math.abs(valor) / (double) maximo));
                ultimoValorInteiroControleComparacao = Math.abs(valor);
            }
        }

        private void sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                ElementoVergnaud elemento,
                EstadoSemanticoCompartilhado.Origem origem) {
            int indice = elemento == null ? -1 : elementosVergnaud.indexOf(elemento);
            EstadoSemanticoCompartilhado.Snapshot snapshot =
                    capturarEstadoCompartilhadoDoVergnaud(indice, origem);
            aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(snapshot, true);
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
            Integer referidoCurado = null;
            Integer valorRelativoCurado = null;
            Integer referendoCurado = null;

            if (situacaoProblemaAtual != null) {
                SemanticaCuradaSituacao.PapelCurado papelReferido = SemanticaCuradaSituacao.buscar(
                        situacaoProblemaAtual, localizacao, "papel.referido");
                SemanticaCuradaSituacao.PapelCurado papelReferendo = SemanticaCuradaSituacao.buscar(
                        situacaoProblemaAtual, localizacao, "papel.referendo");
                SemanticaCuradaSituacao.PapelCurado papelValorRelativo = SemanticaCuradaSituacao.buscar(
                        situacaoProblemaAtual, localizacao, "papel.diferenca");
                // Nunca usar o valor curado de um papel que é a incógnita: ele
                // não pode vazar para uma representação visível antes de o
                // aluno resolvê-lo (ver gerard-consistencia-estado).
                if (papelReferido != null && !papelReferido.isDesconhecido()) {
                    referidoCurado = converterTextoParaInteiro(papelReferido.getValor());
                }
                if (papelReferendo != null && !papelReferendo.isDesconhecido()) {
                    referendoCurado = converterTextoParaInteiro(papelReferendo.getValor());
                }
                if (papelValorRelativo != null && !papelValorRelativo.isDesconhecido()) {
                    valorRelativoCurado = converterTextoParaInteiro(papelValorRelativo.getValor());
                }
            }

            Integer referido = null;
            Integer valorRelativo = null;
            Integer referendo = null;
            boolean existeModelagemDoUsuario = false;

            // No diagrama formal de comparação, a ordem visual existente é:
            // referido, valor relativo e referendo.
            if (elementosVergnaud != null && elementosVergnaud.size() >= 3) {
                Integer referidoModelado = obterValorNumericoDoElemento(elementosVergnaud.get(0));
                Integer relativoModelado = obterValorNumericoDoElemento(elementosVergnaud.get(1));
                Integer referendoModelado = obterValorNumericoDoElemento(elementosVergnaud.get(2));
                existeModelagemDoUsuario = referidoModelado != null || relativoModelado != null || referendoModelado != null;
                if (referidoModelado != null) referido = referidoModelado;
                if (relativoModelado != null) valorRelativo = relativoModelado;
                if (referendoModelado != null) referendo = referendoModelado;
            }

            // Antes de o usuário posicionar números no diagrama, o gráfico de
            // barras deve permanecer vazio, sem antecipar as quantidades da
            // curadoria na representação manipulável.
            if (!existeModelagemDoUsuario) {
                return new int[] {0, 0, 0};
            }

            // As barras de referido e referendo só devem refletir valores
            // explicitamente posicionados pelo usuário no diagrama de Vergnaud.
            // A curadoria pode abastecer o valor relativo, mas não deve
            // preencher automaticamente a barra do outro papel semântico.
            if (valorRelativo == null) {
                valorRelativo = valorRelativoCurado;
            }
            if (valorRelativo == null && referido != null && referendo != null) {
                valorRelativo = Integer.valueOf(referendo.intValue() - referido.intValue());
            }

            return new int[] {
                Math.max(0, referido == null ? 0 : referido.intValue()),
                valorRelativo == null ? 0 : valorRelativo.intValue(),
                Math.max(0, referendo == null ? 0 : referendo.intValue())
            };
        }

        private Integer converterValorRelativoCurado(String valor, String sinal) {
            Integer numero = converterTextoParaInteiro(valor);
            if (numero == null) {
                return null;
            }
            int absoluto = Math.abs(numero.intValue());
            String s = normalizarChaveComparacao(sinal);
            if (numero.intValue() < 0 || s.contains("negativo") || s.contains("negative") || s.contains("negatif")) {
                return Integer.valueOf(-absoluto);
            }
            return Integer.valueOf(absoluto);
        }

        private String normalizarChaveComparacao(String texto) {
            return gerard.ui.venn.UtilitariosComparacaoBarras.normalizarChaveComparacao(texto);
        }

        private boolean papelComparacaoDesconhecido(String papel) {
            if (situacaoProblemaAtual == null) return false;
            String desconhecido = normalizarChaveComparacao(situacaoProblemaAtual.getTermoDesconhecido());
            String p = normalizarChaveComparacao(papel);
            if ("valorrelativo".equals(p)) {
                return desconhecido.contains("valorrelativo") || desconhecido.contains("diferenca");
            }
            return desconhecido.contains(p);
        }

        private boolean papelComparacaoResolvidoNoDiagrama(String papel) {
            if (elementosVergnaud == null || elementosVergnaud.size() < 3) return false;
            String p = normalizarChaveComparacao(papel);
            int indice = "referido".equals(p) ? 0 : ("valorrelativo".equals(p) ? 1 : 2);
            return obterValorNumericoDoElemento(elementosVergnaud.get(indice)) != null;
        }

        private int obterValorElementoModeladoOuZero(int indiceElemento) {
            if (elementosVergnaud != null && indiceElemento >= 0 && indiceElemento < elementosVergnaud.size()) {
                Integer valor = obterValorNumericoDoElemento(elementosVergnaud.get(indiceElemento));
                if (valor != null) {
                    return valor.intValue();
                }
            }
            return 0;
        }

        private int obterValorElementoOuOriginal(int indiceElemento, int[] originais, int indiceOriginal) {
            if (indiceElemento >= 0 && indiceElemento < elementosVergnaud.size()) {
                Integer valor = obterValorNumericoDoElemento(elementosVergnaud.get(indiceElemento));
                if (valor != null) {
                    return valor.intValue();
                }
            }
            if (originais != null && indiceOriginal >= 0 && indiceOriginal < originais.length) {
                return originais[indiceOriginal];
            }
            return 0;
        }

        private static final int LARGURA_BASE_TELA = 1240;
        private static final int ALTURA_BASE_TELA = 760;
        private static final int X_BASE_VERGNAUD = 25;
        private static final int Y_BASE_VERGNAUD = 215;
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
            if (usaCenaVergnaudComposta()) {
                return new Rectangle(limite.x + 10, limite.y + 28, limite.width - 20, limite.height - 38);
            }
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

        private boolean deveExibirDiagramaComplementar() {
            return seletorRepresentacaoComplementar.deveExibir(
                    categoriaSelecionadaParaAtividade,
                    tipoSituacaoSelecionada);
        }

        private boolean ehProcessoTransformacaoMedidas() {
            return seletorRepresentacaoComplementar.selecionar(
                    tipoSituacaoSelecionada,
                    usaCenaVergnaudComposta())
                    == TipoRepresentacaoComplementar.PROCESSO_TRANSFORMACAO;
        }

        private int obterXDivisorDiagramas() {
            Rectangle areaVergnaud = obterAreaVisivelDiagramasVergnaud();
            return areaVergnaud.x + areaVergnaud.width + (ESPACO_BASE_ENTRE_DIAGRAMAS / 2);
        }

        private boolean pontoNoDiagramaVergnaud(int x, int y) {
            return categoriaSelecionadaParaAtividade
                    && obterAreaVisivelDiagramasVergnaud().contains(x, y);
        }

        private Rectangle criarZonaSemanticaElemento(Rectangle limite, TipoSituacaoAditiva tipo, int indiceElemento, int largura, int altura) {
            Rectangle zonaBase = obterZonaBaseElementoPorCategoria(limite, tipo, indiceElemento);
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

        private Rectangle criarZonaSemanticaConector(Rectangle limite, TipoSituacaoAditiva tipo, int indiceConector) {
            return obterZonaBaseConectorPorCategoria(limite, tipo, indiceConector);
        }

        private Rectangle obterZonaBaseElementoPorCategoria(Rectangle limite, TipoSituacaoAditiva tipo, int indiceElemento) {
            int x = limite.x;
            int y = limite.y;
            int w = limite.width;
            int h = limite.height;

            switch (tipo) {
                case COMPOSICAO_MEDIDAS:
                    if (indiceElemento == 0) return new Rectangle(x + 25, y + 20, 150, h / 2 - 20);
                    if (indiceElemento == 1) return new Rectangle(x + 25, y + h / 2, 150, h / 2 - 20);
                    return new Rectangle(x + 155, y + 60, 170, h - 120);
                case TRANSFORMACAO_MEDIDAS:
                    if (indiceElemento == 0) return new Rectangle(x + 10, y + h / 2 - 70, 150, 140);
                    if (indiceElemento == 1) return new Rectangle(x + w / 2 - 90, y + 10, 180, 140);
                    return new Rectangle(x + w - 170, y + h / 2 - 70, 160, 140);
                case COMPARACAO_MEDIDAS:
                    if (indiceElemento == 0) return new Rectangle(x + 35, y + h / 2, 150, h / 2 - 20);
                    if (indiceElemento == 1) return new Rectangle(x + 150, y + h / 2 - 80, 160, 160);
                    return new Rectangle(x + 35, y + 20, 150, h / 2 - 20);
                case COMPOSICAO_TRANSFORMACOES:
                    if (indiceElemento == 0) return new Rectangle(x + 105, y + 10, 170, 130);
                    if (indiceElemento == 1) return new Rectangle(x + 290, y + 10, 170, 130);
                    if (indiceElemento == 2) return new Rectangle(x + w / 2 - 110, y + h - 130, 220, 120);
                    if (indiceElemento == 3) return new Rectangle(x + 10, y + h / 2 - 70, 120, 140);
                    if (indiceElemento == 4) return new Rectangle(x + w / 2 - 70, y + h / 2 - 70, 140, 140);
                    return new Rectangle(x + w - 130, y + h / 2 - 70, 120, 140);
                case TRANSFORMACAO_RELACAO:
                    if (indiceElemento == 0) return new Rectangle(x + 10, y + h / 2 - 80, 170, 160);
                    if (indiceElemento == 1) return new Rectangle(x + w / 2 - 90, y + 10, 180, 140);
                    return new Rectangle(x + w - 180, y + h / 2 - 80, 170, 160);
                case COMPOSICAO_RELACOES:
                    if (indiceElemento == 0) return new Rectangle(x + 40, y + 20, 160, h / 2 - 20);
                    if (indiceElemento == 1) return new Rectangle(x + 40, y + h / 2, 160, h / 2 - 20);
                    return new Rectangle(x + 220, y + 60, 180, h - 120);
                default:
                    return new Rectangle(x, y, w, h);
            }
        }

        private Rectangle obterZonaBaseConectorPorCategoria(Rectangle limite, TipoSituacaoAditiva tipo, int indiceConector) {
            int x = limite.x;
            int y = limite.y;
            int w = limite.width;
            int h = limite.height;

            switch (tipo) {
                case COMPOSICAO_MEDIDAS:
                    return new Rectangle(x + 85, y + 30, 100, h - 60);
                case TRANSFORMACAO_MEDIDAS:
                case TRANSFORMACAO_RELACAO:
                    return new Rectangle(x + 120, y + h / 2 - 70, w - 240, 140);
                case COMPARACAO_MEDIDAS:
                    return new Rectangle(x + 60, y + 45, 90, h - 90);
                case COMPOSICAO_TRANSFORMACOES:
                    if (indiceConector == 0) return new Rectangle(x + 90, y + h / 2 - 70, w / 2 - 80, 120);
                    if (indiceConector == 1) return new Rectangle(x + w / 2, y + h / 2 - 70, w / 2 - 90, 120);
                    return new Rectangle(x + 55, y + h / 2, w - 110, h / 2 - 10);
                case COMPOSICAO_RELACOES:
                    return new Rectangle(x + 120, y + 30, 110, h - 60);
                default:
                    return new Rectangle(x, y, w, h);
            }
        }

        private void inicializarDiagramaVergnaud() {
            Rectangle area = obterAreaConteudoDiagramaVergnaud();
            elementosVergnaud.clear();
            conectoresVergnaud.clear();
            desabilitarSincronizacaoEstadoFinal();

            if (usaDiagramasEncadeadosTransformacaoComposta()) {
                cenaDiagramaAtual = criarCenaTransformacaoComposta(area);
            } else if (usaDiagramasComposicaoTransformacaoMedidas()) {
                cenaDiagramaAtual = criarCenaComposicaoTransformacaoMedidas(area);
            } else {
                cenaDiagramaAtual = geradorCenaDiagrama.gerar(tipoSituacaoSelecionada, area, definicaoDiagramaAtual, extrairValoresDoTexto());
            }

            if (cenaDiagramaAtual == null) {
                return;
            }

            Rectangle caixaCena = calcularCaixaCena(cenaDiagramaAtual);
            int deslocamentoCentroX = 0;
            int deslocamentoCentroY = 0;

            if (!usaCenaVergnaudComposta() && caixaCena != null) {
                deslocamentoCentroX = area.x + (area.width - caixaCena.width) / 2 - caixaCena.x;
                deslocamentoCentroY = area.y + (area.height - caixaCena.height) / 2 - caixaCena.y;
            }
            deslocamentoCentroXAplicadoDiagramaVergnaud = deslocamentoCentroX;
            deslocamentoCentroYAplicadoDiagramaVergnaud = deslocamentoCentroY;

            for (int i = 0; i < cenaDiagramaAtual.getConectores().size(); i++) {
                ConectorDiagrama conector = cenaDiagramaAtual.getConectores().get(i);
                Rectangle zona = usaDiagramasEncadeadosTransformacaoComposta()
                        ? criarZonaSemanticaConectorTransformacaoComposta(i)
                        : (usaDiagramasComposicaoTransformacaoMedidas()
                                ? criarZonaSemanticaConectorComposicaoTransformacao(i)
                                : criarZonaSemanticaConector(area, tipoSituacaoSelecionada, i));
                if (!usaCenaVergnaudComposta()) {
                    zona = deslocarZonaSemantica(zona, deslocamentoCentroX, deslocamentoCentroY, area);
                }
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
                Rectangle zona = usaDiagramasEncadeadosTransformacaoComposta()
                        ? criarZonaSemanticaElementoTransformacaoComposta(i, figura.getLargura(), figura.getAltura())
                        : (usaDiagramasComposicaoTransformacaoMedidas()
                                ? criarZonaSemanticaElementoComposicaoTransformacao(i, figura.getLargura(), figura.getAltura())
                                : criarZonaSemanticaElemento(area, tipoSituacaoSelecionada, i, figura.getLargura(), figura.getAltura()));
                if (!usaCenaVergnaudComposta()) {
                    zona = deslocarZonaSemantica(zona, deslocamentoCentroX, deslocamentoCentroY, area);
                }
                elementosVergnaud.add(new ElementoVergnaud(
                        figura.getX() + deslocamentoCentroX,
                        figura.getY() + deslocamentoCentroY,
                        figura.getLargura(),
                        figura.getAltura(),
                        figura.getTipo(),
                        figura.getRotulo(),
                        zona,
                        i == indiceIncognita
                ));
            }

            if (usaDiagramasEncadeadosTransformacaoComposta()) {
                adicionarItensAutomaticosTransformacaoComposta();
            }

            // O diagrama deve iniciar vazio. A interrogação pertence ao enunciado
            // e só pode aparecer dentro de uma figura após o arraste do usuário.
            removerInterrogacoesPreenchidasAutomaticamenteNoDiagrama();
            aplicarSubtitulosPersonagensNoDiagramaVergnaud();
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
            if (cenaDiagramaAtual == null || usaCenaVergnaudComposta()
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
                elemento.subtitulo = obterSubtituloPersonagemParaElemento(elemento, i);
                elemento.rotulosAcima = deveExibirRotulosAcimaNoDiagrama(elemento);
            }
        }

        private boolean deveExibirRotulosAcimaNoDiagrama(ElementoVergnaud elemento) {
            if (elemento == null) {
                return false;
            }
            if (tipoSituacaoSelecionada == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
                return true;
            }
            if (tipoSituacaoSelecionada == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
                String chave = normalizarChaveComparacao(elemento.rotulo);
                return chave.contains("referendo");
            }
            return false;
        }

        private String obterSubtituloPersonagemParaElemento(ElementoVergnaud elemento, int indice) {
            String porRotulo = obterSubtituloPersonagemParaRotulo(elemento == null ? "" : elemento.rotulo);
            if (porRotulo != null && porRotulo.trim().length() > 0) {
                return porRotulo.trim();
            }
            return obterSubtituloPersonagemPorIndice(indice);
        }

        private String obterSubtituloPersonagemPorIndice(int indice) {
            if (situacaoProblemaAtual == null) {
                return "";
            }
            switch (tipoSituacaoSelecionada) {
                case COMPOSICAO_MEDIDAS:
                    if (indice == 0) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem1());
                    if (indice == 1) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem2());
                    if (indice == 2) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem3());
                    break;
                case TRANSFORMACAO_MEDIDAS:
                    if (indice == 0) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem1());
                    if (indice == 1) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem2());
                    if (indice == 2) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem3());
                    break;
                case COMPARACAO_MEDIDAS:
                    if (indice == 0 || indice == 2) {
                        // fallback apenas se os rótulos não vierem corretamente
                        return indice == 0
                                ? valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem1())
                                : valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem2());
                    }
                    break;
                case COMPOSICAO_RELACOES:
                    if (indice == 0) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem1());
                    if (indice == 1) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem2());
                    if (indice == 2) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem3());
                    break;
                case TRANSFORMACAO_RELACAO:
                    if (indice == 0) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem1());
                    if (indice == 1) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem2());
                    if (indice == 2) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem3());
                    break;
                case COMPOSICAO_TRANSFORMACOES:
                    if (indice == 0 || indice == 3) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem1());
                    if (indice == 1 || indice == 4) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem2());
                    if (indice == 2 || indice == 5) return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem3());
                    break;
                default:
                    break;
            }
            return "";
        }

        private String obterSubtituloPersonagemParaRotulo(String rotulo) {
            if (situacaoProblemaAtual == null) {
                return "";
            }
            String chave = normalizarChaveComparacao(rotulo);
            if (chave.length() == 0) {
                return "";
            }

            if (tipoSituacaoSelecionada == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
                if (chave.contains("referido")) {
                    return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem1());
                }
                if (chave.contains("referendo")) {
                    return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem2());
                }
                return "";
            }

            if (chave.contains("parte1") || chave.contains("quantidade1") || chave.contains("estadoinicial") || chave.contains("referido")) {
                return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem1());
            }
            if (chave.contains("parte2") || chave.contains("quantidade2") || chave.contains("transformacao") || chave.contains("valorrelativo") || chave.contains("referendo")) {
                return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem2());
            }
            if (chave.contains("todo") || chave.contains("resultado") || chave.contains("estadofinal")) {
                return valorSeguroPersonagem(situacaoProblemaAtual.getPersonagem3());
            }
            return "";
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

        private int obterEspacamentoPassosTransformacaoComposta() {
            return 22;
        }

        private int obterAlturaPassoTransformacaoComposta(Rectangle area, int passos) {
            int espacamento = obterEspacamentoPassosTransformacaoComposta();
            int alturaDisponivelPorPasso = (area.height - (espacamento * (passos - 1))) / Math.max(1, passos);

            if (passos == 2) {
                return Math.max(175, Math.min(220, alturaDisponivelPorPasso));
            }

            return Math.max(135, alturaDisponivelPorPasso);
        }

        private int obterTopoInicialPassosTransformacaoComposta(Rectangle area, int passos, int alturaPasso) {
            int espacamento = obterEspacamentoPassosTransformacaoComposta();
            int alturaTotalBloco = passos * alturaPasso + (espacamento * (passos - 1));
            int topoCentralizado = area.y + Math.max(0, (area.height - alturaTotalBloco) / 2);
            if (passos > 1) {
                topoCentralizado -= 20;
            }
            return Math.max(area.y, topoCentralizado);
        }

        private CenaDiagramaAditivo criarCenaComposicaoTransformacaoMedidas(Rectangle area) {
            java.util.List<FiguraDiagrama> figuras = new ArrayList<FiguraDiagrama>();
            java.util.List<ConectorDiagrama> conectores = new ArrayList<ConectorDiagrama>();

            DefinicaoDiagramaAditivo definicaoComposicao = catalogoDefinicoesAditivas.obter(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
            DefinicaoDiagramaAditivo definicaoTransformacao = catalogoDefinicoesAditivas.obter(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);

            int[] numeros = extrairTodosNumerosDoTexto();
            int parte1 = numeros.length > 0 ? numeros[0] : 0;
            int parte2 = numeros.length > 1 ? numeros[1] : 0;
            int totalInicial = parte1 + parte2;
            int transformacao = calcularTotalTransformacaoComposicaoTransformacao(numeros);
            int estadoFinal = totalInicial - transformacao;

            adicionarFigurasComposicaoCompacta(
                    obterSubareaPassoTransformacaoComposta(0),
                    definicaoComposicao,
                    new int[] { parte1, parte2, totalInicial },
                    figuras,
                    conectores
            );
            adicionarFigurasTransformacaoCompacta(
                    obterSubareaPassoTransformacaoComposta(1),
                    definicaoTransformacao,
                    new int[] { totalInicial, transformacao, estadoFinal },
                    figuras,
                    conectores
            );

            return new CenaDiagramaAditivo(
                    definicaoDiagramaAtual.getTitulo(),
                    localizacao.texto("diag.desc.composicao_transformacao_medidas"),
                    figuras,
                    conectores
            );
        }

        private int calcularTotalTransformacaoComposicaoTransformacao(int[] numeros) {
            if (numeros == null || numeros.length <= 2) {
                return 0;
            }

            int total = 0;
            for (int i = 2; i < numeros.length; i++) {
                total += numeros[i];
            }
            return total;
        }

        private CenaDiagramaAditivo criarCenaTransformacaoComposta(Rectangle area) {
            java.util.List<FiguraDiagrama> figuras = new ArrayList<FiguraDiagrama>();
            java.util.List<ConectorDiagrama> conectores = new ArrayList<ConectorDiagrama>();
            int[] numeros = extrairTodosNumerosDoTexto();
            int passos = quantidadePassosTransformacaoComposta;
            int espacamento = obterEspacamentoPassosTransformacaoComposta();
            int alturaPasso = obterAlturaPassoTransformacaoComposta(area, passos);
            int yInicial = obterTopoInicialPassosTransformacaoComposta(area, passos, alturaPasso);
            DefinicaoDiagramaAditivo definicaoBase = catalogoDefinicoesAditivas.obter(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);

            for (int passo = 0; passo < passos; passo++) {
                int subY = yInicial + passo * (alturaPasso + espacamento);
                int alturaReal = Math.min(alturaPasso, area.y + area.height - subY);
                Rectangle subarea = new Rectangle(area.x, subY, area.width, alturaReal);

                int valorInicial = 0;
                if (passo == 0) {
                    valorInicial = numeros.length > 0 ? numeros[0] : 0;
                } else if (passo - 1 < estadosIntermediariosTransformacaoComposta.size()) {
                    valorInicial = estadosIntermediariosTransformacaoComposta.get(passo - 1);
                }

                int valorTransformacao = passo < transformacoesComSinalTransformacaoComposta.size() ? Math.abs(transformacoesComSinalTransformacaoComposta.get(passo)) : (numeros.length > passo + 1 ? numeros[passo + 1] : 0);
                int valorFinal = passo < estadosIntermediariosTransformacaoComposta.size() ? estadosIntermediariosTransformacaoComposta.get(passo) : 0;
                adicionarFigurasTransformacaoCompacta(subarea, definicaoBase, new int[] { valorInicial, valorTransformacao, valorFinal }, figuras, conectores);
            }

            return new CenaDiagramaAditivo(
                    definicaoDiagramaAtual.getTitulo(),
                    localizacao.texto("diag.desc.transformacao_composta_dois_passos"),
                    figuras,
                    conectores
            );
        }

        private int obterDeslocamentoVerticalDiagramasCompostos() {
            return 18;
        }

        private void adicionarFigurasComposicaoCompacta(
                Rectangle area,
                DefinicaoDiagramaAditivo definicao,
                int[] valores,
                java.util.List<FiguraDiagrama> figuras,
                java.util.List<ConectorDiagrama> conectores
        ) {
            int deslocamentoVertical = obterDeslocamentoVerticalDiagramasCompostos();
            int parte1X = area.x + 58;
            int parte1Y = area.y + 54 - deslocamentoVertical;
            int parte2X = area.x + 58;
            int parte2Y = area.y + 132 - deslocamentoVertical;
            int todoX = area.x + 225;
            int todoY = area.y + 93 - deslocamentoVertical;

            FiguraDiagrama parte1 = new FiguraDiagrama(TipoFiguraDiagrama.RETANGULO_ARREDONDADO, parte1X, parte1Y, 42, 42, definicao.getRotulo1(), valores[0], true);
            FiguraDiagrama parte2 = new FiguraDiagrama(TipoFiguraDiagrama.RETANGULO_ARREDONDADO, parte2X, parte2Y, 42, 42, definicao.getRotulo2(), valores[1], true);
            FiguraDiagrama todo = new FiguraDiagrama(TipoFiguraDiagrama.RETANGULO_ARREDONDADO, todoX, todoY, 42, 42, definicao.getRotulo3(), valores[2], true);

            figuras.add(parte1);
            figuras.add(parte2);
            figuras.add(todo);
            conectores.add(new ConectorDiagrama(TipoConectorDiagrama.CHAVE_VERTICAL, parte1X + 66, parte1Y - 6, parte2X + 66, parte2Y + 48, ""));
        }

        private void adicionarFigurasTransformacaoCompacta(
                Rectangle area,
                DefinicaoDiagramaAditivo definicao,
                int[] valores,
                java.util.List<FiguraDiagrama> figuras,
                java.util.List<ConectorDiagrama> conectores
        ) {
            int deslocamentoVertical = obterDeslocamentoVerticalDiagramasCompostos();
            int medidaY = area.y + area.height - 58 - deslocamentoVertical;
            int transformacaoY = Math.max(area.y + 28, medidaY - 72);
            int inicialX = area.x + 38;
            int transformacaoX = area.x + 202;
            int finalX = area.x + 374;

            FiguraDiagrama inicial = new FiguraDiagrama(TipoFiguraDiagrama.RETANGULO_ARREDONDADO, inicialX, medidaY, 42, 42, definicao.getRotulo1(), valores[0], true);
            FiguraDiagrama transformacao = new FiguraDiagrama(TipoFiguraDiagrama.ELIPSE, transformacaoX, transformacaoY, 52, 52, definicao.getRotulo2(), valores[1], true);
            FiguraDiagrama fim = new FiguraDiagrama(TipoFiguraDiagrama.RETANGULO_ARREDONDADO, finalX, medidaY, 42, 42, definicao.getRotulo3(), valores[2], true);

            figuras.add(inicial);
            figuras.add(transformacao);
            figuras.add(fim);
            conectores.add(new ConectorDiagrama(TipoConectorDiagrama.SETA, inicialX + 74, medidaY + 21, finalX - 32, medidaY + 21, ""));
        }

        private Rectangle obterSubareaPassoTransformacaoComposta(int passo) {
            Rectangle area = obterAreaConteudoDiagramaVergnaud();
            int passos = Math.max(1, quantidadePassosTransformacaoComposta);
            int espacamento = obterEspacamentoPassosTransformacaoComposta();
            int alturaPasso = obterAlturaPassoTransformacaoComposta(area, passos);
            int yInicial = obterTopoInicialPassosTransformacaoComposta(area, passos, alturaPasso);
            int y = yInicial + passo * (alturaPasso + espacamento);
            int alturaReal = Math.min(alturaPasso, area.y + area.height - y);
            return new Rectangle(area.x, y, area.width, alturaReal);
        }

        private Rectangle criarZonaSemanticaElementoTransformacaoComposta(int indiceElemento, int largura, int altura) {
            int passo = indiceElemento / 3;
            int indiceLocal = indiceElemento % 3;
            Rectangle subarea = obterSubareaPassoTransformacaoComposta(passo);
            return criarZonaSemanticaElemento(subarea, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, indiceLocal, largura, altura);
        }

        private Rectangle criarZonaSemanticaConectorTransformacaoComposta(int indiceConector) {
            int passo = indiceConector;
            Rectangle subarea = obterSubareaPassoTransformacaoComposta(passo);
            return criarZonaSemanticaConector(subarea, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, 0);
        }

        private Rectangle criarZonaSemanticaElementoComposicaoTransformacao(int indiceElemento, int largura, int altura) {
            if (indiceElemento < 3) {
                Rectangle subarea = obterSubareaPassoTransformacaoComposta(0);
                return criarZonaSemanticaElemento(subarea, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, indiceElemento, largura, altura);
            }

            Rectangle subarea = obterSubareaPassoTransformacaoComposta(1);
            return criarZonaSemanticaElemento(subarea, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, indiceElemento - 3, largura, altura);
        }

        private Rectangle criarZonaSemanticaConectorComposicaoTransformacao(int indiceConector) {
            if (indiceConector == 0) {
                Rectangle subarea = obterSubareaPassoTransformacaoComposta(0);
                return criarZonaSemanticaConector(subarea, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, 0);
            }

            Rectangle subarea = obterSubareaPassoTransformacaoComposta(1);
            return criarZonaSemanticaConector(subarea, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, 0);
        }

        private void adicionarItensAutomaticosTransformacaoComposta() {
            // Nesta versão, os diagramas compostos em múltiplos passos permanecem sem preenchimento automático.
            // A centralização visual é preservada, mas os espaços internos ficam vazios.
            return;
        }

        private void adicionarItemAutomaticoCentralizado(int indiceElemento, String valor, String chavePapel) {
            if (indiceElemento < 0 || indiceElemento >= elementosVergnaud.size()) {
                return;
            }

            ElementoVergnaud alvo = elementosVergnaud.get(indiceElemento);
            Font fonte = new Font("Arial", Font.BOLD, 20);
            FontMetrics fm = getFontMetrics(fonte);
            int largura = fm.stringWidth(valor) + 8;
            int altura = fm.getHeight() - 5;
            ItemTextoArrastavel item = new ItemTextoArrastavel(
                    alvo.x + (alvo.largura - largura) / 2,
                    alvo.y + (alvo.altura - altura) / 2,
                    largura,
                    altura,
                    valor,
                    false,
                    valor,
                    chavePapel
            );
            itensArrastaveis.add(item);
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

            desenharCard(g2, area.x, area.y, area.width, area.height, 18);
            reposicionarBotaoAjudaComplementar(area);

            if (processoTransformacao) {
                renderizadorProcessoTransformacao.desenharCabecalho(
                        g2, area, localizacao);
            } else if (!comparacaoMedidas) {
                String chaveTituloDiagrama = composicaoMedidas
                        ? "ui.collections.title"
                        : "ui.vann.title";
                g2.setColor(COR_TEXTO);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString(localizacao.texto(chaveTituloDiagrama), area.x + 18, area.y + 28);
            }

            boolean precisaReconstruirEstruturaVenn = cenaDiagramaVennAtual == null || ultimaAreaDiagramaVenn == null || !ultimaAreaDiagramaVenn.equals(area);
            if (precisaReconstruirEstruturaVenn || (quadradinhoVennSelecionado == null && itemSelecionado == null && elementoTextoSelecionado == null)) {
                sincronizarDiagramaVennComRepresentacoes(precisaReconstruirEstruturaVenn);
            }

            if (cenaDiagramaVennAtual != null) {
                for (ConectorDiagramaVenn conector : cenaDiagramaVennAtual.getConectores()) {
                    desenharSetaVenn(g2, conector.getX1(), conector.getY1(), conector.getX2(), conector.getY2());
                }
            }

            EstadoProcessoTransformacao estadoProcesso =
                    processoTransformacao
                    ? EstadoProcessoTransformacao.aPartir(
                            estadoSemanticoCompartilhado.snapshot())
                    : null;
            for (int i = 0; i < circulosVenn.size(); i++) {
                if (processoTransformacao) {
                    renderizadorProcessoTransformacao.desenharZona(
                            g2, circulosVenn.get(i), i,
                            estadoProcesso, localizacao,
                            planoUnidadesProcessoAtual);
                } else {
                    desenharCirculoVenn(g2, circulosVenn.get(i),
                            composicaoMedidas, comparacaoMedidas);
                }
            }
            if (processoTransformacao) {
                renderizadorProcessoTransformacao.desenharEstrutura(
                        g2, circulosVenn, estadoProcesso,
                        planoUnidadesProcessoAtual);
            }

            atualizarQuadradinhosCorrespondentesComparacao(comparacaoMedidas);
            for (int i = 0; i < quadradinhosVenn.size(); i++) {
                QuadradinhoVenn quadradinho = quadradinhosVenn.get(i);
                if (quadradinho != quadradinhoVennSelecionado) {
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
                String chave = scaffoldingQuestionamento.obterChavePapelDoElemento(
                        tipoSituacaoSelecionada,
                        indiceReal,
                        usaDiagramasEncadeadosTransformacaoComposta(),
                        quantidadePassosTransformacaoComposta);
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
                    ehProcessoTransformacaoMedidas(),
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
                    ehProcessoTransformacaoMedidas(),
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
                    controleSinalProcessoTransformacao.desenharAdicionar(
                            g2, circulosVenn,
                            obterEstadoVisualProcessoTransformacao(),
                            focado, habilitado);
                } else {
                    controleAdicionarQuadradinhoVenn.desenhar(
                            g2, representacao, area, focado, habilitado);
                }
            }
        }

        private RepresentacaoComUnidadesAdicionaveis
                encontrarRepresentacaoPeloControleAdicionarQuadradinho(int x, int y) {
            if (!deveExibirDiagramaComplementar()) {
                return null;
            }
            Rectangle area = obterAreaDiagramaAditivo();
            java.util.List<RepresentacaoComUnidadesAdicionaveis> representacoes =
                    obterRepresentacoesComUnidadesAdicionaveis();
            for (int i = representacoes.size() - 1; i >= 0; i--) {
                RepresentacaoComUnidadesAdicionaveis representacao = representacoes.get(i);
                boolean contem = ehAgrupamentoTransformacaoComSinal(
                        representacao.obterAgrupamento())
                        ? controleSinalProcessoTransformacao.contemAdicionar(
                                circulosVenn,
                                obterEstadoVisualProcessoTransformacao(), x, y)
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
                    controleSinalProcessoTransformacao.desenharRemover(
                            g2, circulosVenn,
                            obterEstadoVisualProcessoTransformacao(),
                            focado, habilitado);
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
            if (!deveExibirDiagramaComplementar()) {
                return null;
            }
            Rectangle area = obterAreaDiagramaAditivo();
            java.util.List<RepresentacaoComUnidadesRemoviveis> representacoes =
                    obterRepresentacoesComUnidadesRemoviveis();
            for (int i = representacoes.size() - 1; i >= 0; i--) {
                RepresentacaoComUnidadesRemoviveis representacao = representacoes.get(i);
                boolean contem = ehAgrupamentoTransformacaoComSinal(
                        representacao.obterAgrupamento())
                        ? controleSinalProcessoTransformacao.contemRemover(
                                circulosVenn,
                                obterEstadoVisualProcessoTransformacao(), x, y)
                        : controleRemoverQuadradinhoVenn.contem(
                                representacao, area, x, y);
                if (contem) {
                    return representacao;
                }
            }
            return null;
        }

        private boolean ehAgrupamentoTransformacaoComSinal(CirculoVenn agrupamento) {
            if (!ehProcessoTransformacaoMedidas() || agrupamento == null) {
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
                                : Math.abs(valorAssinadoProposto);
                    } else if (ehProcessoTransformacaoMedidas()
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
                    conhecidos[indiceSemantico] = Math.abs(quantidade) > 0
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
                    Math.abs(novoValor),
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
                chaves[indiceSemantico] =
                        scaffoldingQuestionamento.obterChavePapelDoElemento(
                                tipoSituacaoSelecionada,
                                indiceReal,
                                usaDiagramasEncadeadosTransformacaoComposta(),
                                quantidadePassosTransformacaoComposta);
                valores[indiceSemantico] = obterValorCuradoPorIndiceEChave(
                        indiceReal, chaves[indiceSemantico]);
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

        private Integer obterValorCuradoPorIndiceEChave(int indiceReal, String chave) {
            if (situacaoProblemaAtual == null) {
                return null;
            }

            if (tipoSituacaoSelecionada == TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS) {
                Integer inicial = converterTextoParaInteiro(situacaoProblemaAtual.getEstadoInicial());
                Integer t1 = converterValorRelativoCurado(
                        situacaoProblemaAtual.getQuantidade1(), "positivo");
                Integer t2 = converterValorRelativoCurado(
                        situacaoProblemaAtual.getQuantidade2(), "positivo");
                Integer finalCurado = converterTextoParaInteiro(
                        primeiroNaoVazio(situacaoProblemaAtual.getResultado(),
                                situacaoProblemaAtual.getEstadoFinal()));
                if (indiceReal == 0) return inicial;
                if (indiceReal == 1) return t1;
                if (indiceReal == 2 || indiceReal == 3) {
                    if (inicial != null && t1 != null) {
                        return Integer.valueOf(inicial.intValue() + t1.intValue());
                    }
                    if (finalCurado != null && t2 != null) {
                        return Integer.valueOf(finalCurado.intValue() - t2.intValue());
                    }
                    return null;
                }
                if (indiceReal == 4) return t2;
                if (indiceReal == 5) return finalCurado;
            }

            SemanticaCuradaSituacao.PapelCurado papel = SemanticaCuradaSituacao.buscar(
                    situacaoProblemaAtual, localizacao, chave);
            Integer valorMapeado = converterTextoParaInteiro(
                    papel == null ? "" : papel.getValor());
            if (valorMapeado != null) {
                return valorMapeado;
            }

            if ("papel.parte1".equals(chave) || "papel.transformacao1".equals(chave)
                    || "papel.relacao1".equals(chave)) {
                return converterTextoParaInteiro(situacaoProblemaAtual.getQuantidade1());
            }
            if ("papel.parte2".equals(chave) || "papel.transformacao2".equals(chave)
                    || "papel.relacao2".equals(chave)) {
                return converterTextoParaInteiro(situacaoProblemaAtual.getQuantidade2());
            }
            if ("papel.todo".equals(chave) || "papel.transformacaoFinal".equals(chave)) {
                return converterTextoParaInteiro(situacaoProblemaAtual.getResultado());
            }
            if ("papel.estadoInicial".equals(chave) || "papel.relacaoInicial".equals(chave)) {
                return converterTextoParaInteiro(situacaoProblemaAtual.getEstadoInicial());
            }
            if (chave != null && chave.startsWith("papel.transformacao")) {
                return converterValorRelativoCurado(
                        situacaoProblemaAtual.getTransformacao(),
                        situacaoProblemaAtual.getSinalTransformacao());
            }
            if ("papel.estadoFinal".equals(chave)) {
                return converterTextoParaInteiro(situacaoProblemaAtual.getEstadoFinal());
            }
            if ("papel.relacaoFinal".equals(chave)) {
                return converterTextoParaInteiro(
                        tipoSituacaoSelecionada == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                                ? situacaoProblemaAtual.getEstadoFinal()
                                : situacaoProblemaAtual.getResultado());
            }
            if ("papel.referido".equals(chave)) {
                return converterTextoParaInteiro(primeiroNaoVazio(
                        situacaoProblemaAtual.getReferido(),
                        situacaoProblemaAtual.getQuantidade2()));
            }
            if ("papel.referendo".equals(chave)) {
                return converterTextoParaInteiro(primeiroNaoVazio(
                        situacaoProblemaAtual.getReferendo(),
                        situacaoProblemaAtual.getQuantidade1()));
            }
            if ("papel.diferenca".equals(chave)) {
                Integer relativo = converterValorRelativoCurado(
                        situacaoProblemaAtual.getValorRelativo(),
                        situacaoProblemaAtual.getSinalValorRelativo());
                return relativo != null ? relativo : converterTextoParaInteiro(
                        situacaoProblemaAtual.getResultado());
            }
            return null;
        }

        private String primeiroNaoVazio(String primeiro, String segundo) {
            String a = primeiro == null ? "" : primeiro.trim();
            if (a.length() > 0) {
                return a;
            }
            return segundo == null ? "" : segundo.trim();
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
            return !usaCenaVergnaudComposta() && tipoSituacaoSelecionada == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS;
        }

        private boolean ehGraficoBarrasComparacao() {
            return !usaCenaVergnaudComposta() && tipoSituacaoSelecionada == TipoSituacaoAditiva.COMPARACAO_MEDIDAS;
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

            EstadoVisualUnidadeVenn estado = quadradinho == quadradinhoVennSelecionado
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

                String valor = valorAtual > 0 ? "+" + valorAtual : String.valueOf(valorAtual);
                g2.setColor(COR_TEXTO);
                g2.setFont(new Font("Arial", Font.BOLD, 26));
                FontMetrics fmValor = g2.getFontMetrics();
                int xValor = circulo.x + (circulo.largura - fmValor.stringWidth(valor)) / 2;
                int yValor = circulo.y + (circulo.altura - fmValor.getHeight()) / 2 + fmValor.getAscent();
                g2.drawString(valor, xValor, yValor);
            }
            g2.setStroke(original);
        }

        private String textoValorRelativoComparacao(int valor) {
            if (papelComparacaoDesconhecido("valor_relativo")
                    && !papelComparacaoResolvidoNoDiagrama("valor_relativo")) {
                return "?";
            }
            if (valor > 0) return "+" + valor;
            return String.valueOf(valor);
        }

        private String textoMedidaComparacao(String papel, int quantidade) {
            if (papelComparacaoDesconhecido(papel)
                    && !papelComparacaoResolvidoNoDiagrama(papel)) {
                return "?";
            }
            return String.valueOf(quantidade);
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
            String textoReferido = textoMedidaComparacao("referido", quantidadeReferido);
            String textoReferendo = textoMedidaComparacao("referendo", quantidadeReferendo);
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
            SemanticaCuradaSituacao.PapelCurado papel = SemanticaCuradaSituacao.buscar(
                    situacaoProblemaAtual, localizacao, chave);
            return papel == null ? "" : papel.getParticipante();
        }

        private int obterValorAtualControleComparacao() {
            int maximo = obterValorMaximoEscalaComparacao();
            if (maximo <= 0) {
                if (circulosVenn.size() < 2) {
                    return 0;
                }
                return Math.max(0, Math.abs(contarQuadradinhosNoCirculo(circulosVenn.get(1)) - contarQuadradinhosNoCirculo(circulosVenn.get(0))));
            }
            inicializarProporcaoControleComparacaoSeNecessario();
            return (int) Math.round(proporcaoControleComparacao * maximo);
        }

        private int obterValorMaximoEscalaComparacao() {
            int maximo = 0;
            if (situacaoProblemaAtual != null) {
                SemanticaCuradaSituacao.PapelCurado papelValorRelativo = SemanticaCuradaSituacao.buscar(
                        situacaoProblemaAtual, localizacao, "papel.diferenca");
                // Idem: se este papel for a incógnita, seu valor curado não
                // pode influenciar a escala visível (vazaria a resposta nos
                // números do eixo antes do aluno resolver).
                if (papelValorRelativo != null && !papelValorRelativo.isDesconhecido()) {
                    Integer valorCurado = converterTextoParaInteiro(papelValorRelativo.getValor());
                    if (valorCurado != null) {
                        maximo = Math.max(maximo, Math.abs(valorCurado.intValue()));
                    }
                }
            }
            if (elementosVergnaud != null && elementosVergnaud.size() >= 2) {
                Integer valorModelado = obterValorNumericoDoElemento(elementosVergnaud.get(1));
                if (valorModelado != null) {
                    maximo = Math.max(maximo, Math.abs(valorModelado.intValue()));
                }
            }
            if (circulosVenn.size() >= 3) {
                maximo = Math.max(maximo, Math.abs(circulosVenn.get(2).valorReferencia));
            }
            return maximo;
        }

        private int obterValorRelativoAssinadoComparacao() {
            if (elementosVergnaud != null && elementosVergnaud.size() >= 2) {
                Integer valorModelado = obterValorNumericoDoElemento(elementosVergnaud.get(1));
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
            int atual = obterValorRelativoAssinadoComparacao();
            return atual < 0 ? -Math.abs(modulo) : Math.abs(modulo);
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
            int atual = Math.max(0, Math.abs(contarQuadradinhosNoCirculo(circulosVenn.get(1)) - contarQuadradinhosNoCirculo(circulosVenn.get(0))));
            proporcaoControleComparacao = Math.max(0.0, Math.min(1.0, atual / (double) maximo));
            ultimoValorInteiroControleComparacao = Math.max(0, Math.min(maximo, atual));
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
            String papelDesconhecido = situacaoProblemaAtual != null ? situacaoProblemaAtual.getTermoDesconhecido() : "";

            if ("referido".equalsIgnoreCase(papelDesconhecido)) {
                normalizarQuantidadeQuadradinhosNaBarra(referido, Math.max(0, quantidadeReferendo - valorControle));
            } else {
                normalizarQuantidadeQuadradinhosNaBarra(referendo, Math.max(0, quantidadeReferido + valorControle));
            }

            if (elementosVergnaud != null && elementosVergnaud.size() >= 2) {
                definirValorNoElementoNumeroRelativo(
                        elementosVergnaud.get(1), valorControle, true);
                sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                        elementosVergnaud.get(1),
                        EstadoSemanticoCompartilhado.Origem.EIXO_VERTICAL);
            }
        }

        private void sincronizarDiagramaVergnaudAPartirDoControleComparacao(int valorControle) {
            if (elementosVergnaud == null || elementosVergnaud.size() < 3 || circulosVenn.size() < 2) {
                return;
            }

            CirculoVenn referido = circulosVenn.get(0);
            CirculoVenn referendo = circulosVenn.get(1);
            int quantidadeReferido = contarQuadradinhosNoCirculo(referido);
            int quantidadeReferendo = contarQuadradinhosNoCirculo(referendo);

            definirValorNoElementoMedida(elementosVergnaud.get(0), Integer.toString(Math.max(0, quantidadeReferido)));
            definirValorNoElementoNumeroRelativo(elementosVergnaud.get(1), valorControle, true);
            definirValorNoElementoMedida(elementosVergnaud.get(2), Integer.toString(Math.max(0, quantidadeReferendo)));
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

        private String criarEquacaoComparacao(String referido, String relativo,
                                               String referendo, int valorRelativo) {
            if (papelComparacaoDesconhecido("valor_relativo")
                    && !papelComparacaoResolvidoNoDiagrama("valor_relativo")) {
                return referendo + " - " + referido + " = ?";
            }
            String operador = valorRelativo >= 0 ? " + " : " - ";
            String modulo = String.valueOf(Math.abs(valorRelativo));
            return referido + operador + modulo + " = " + referendo;
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
            int totalComposicao = calcularTotalComposicaoMedidas(quantidade1, quantidade2);

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

        private int calcularTotalComposicaoMedidas(int quantidade1, int quantidade2) {
            return Math.max(0, quantidade1) + Math.max(0, quantidade2);
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
            int indiceAlterado = -1;
            if (quadradinhoVennSelecionado != null) {
                for (int i = 0; i < circulosVenn.size(); i++) {
                    if (circulosVenn.get(i).contem(
                            quadradinhoVennSelecionado.centroX(),
                            quadradinhoVennSelecionado.centroY())) {
                        indiceAlterado = i;
                        break;
                    }
                }
            }
            if (indiceAlterado < 0) {
                indiceAlterado = indiceCirculoVennOrigemArraste;
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
            return x >= 15 && x <= getWidth() - 15 && y >= 55 && y <= 190;
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
                elementoTextoSelecionado = null;
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
            itemSelecionado = novo;
            itemFocado = novo;
            elementoTextoSelecionado = null;

            deslocamentoX = mouseX - novo.x;
            deslocamentoY = mouseY - novo.y;
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
            return itemSelecionado != null
                    || elementoTextoSelecionado != null
                    || quadradinhoVennSelecionado != null
                    || elementoVergnaudSelecionado != null
                    || conectorVergnaudSelecionado != null
                    || arrastandoControleComparacao
                    || scaffoldingGraficoInteiros.estaArrastando();
        }

        private boolean pontoSobreElementoArrastavel(int x, int y) {
            boolean representacoesLiberadas = interacaoRepresentacoesLiberadaPelaModelagem();
            if (representacoesLiberadas
                    && scaffoldingGraficoInteiros.contemPontoControle(x, y)) {
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
                    || encontrarElementoVergnaud(x, y) != null
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
            controladorLimiarArrasteEstrutural.cancelar();
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
            itemSelecionado = null;
            elementoVergnaudSelecionado = null;
            conectorVergnaudSelecionado = null;
            elementoTextoSelecionado = null;
            limparRealceAlvoProximidade();
            mostrarAnotacaoMouseOver = false;
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

            Rectangle origemPontoGraficoInteiros =
                    scaffoldingGraficoInteiros.obterAreaVisualPontoControle();
            Rectangle origemPainelGraficoInteiros =
                    scaffoldingGraficoInteiros.obterAreaVisualPainel();
            ScaffoldingGraficoInteiros.NaturezaInteracao naturezaInteracaoEixo =
                    scaffoldingGraficoInteiros.identificarNaturezaInteracao(
                            x, y, getWidth(), getHeight(),
                            obterAreaVisivelDiagramasVergnaud());
            if (naturezaInteracaoEixo
                    == ScaffoldingGraficoInteiros.NaturezaInteracao.VALOR_SEMANTICO
                    && !interacaoRepresentacoesLiberadaPelaModelagem()) {
                informarBloqueioInteracaoRepresentacao(
                        x, y, "Valor semântico no eixo x dos inteiros");
                return;
            }
            if (scaffoldingGraficoInteiros.processarPressionamento(
                    x,
                    y,
                    getWidth(),
                    getHeight(),
                    obterAreaVisivelDiagramasVergnaud())) {
                if (scaffoldingGraficoInteiros.estaArrastandoPontoControle()) {
                    iniciarFantasmaRetangular(origemPontoGraficoInteiros, 14, true);
                    iniciarArrasteElastico(x, y);
                    definirCursorMaoFechada();
                } else if (scaffoldingGraficoInteiros.estaArrastandoPainel()) {
                    iniciarFantasmaRetangular(origemPainelGraficoInteiros, 16, false);
                    iniciarArrasteElastico(x, y);
                    definirCursorMaoFechada();
                }
                if (scaffoldingGraficoInteiros.foiOcultadoPorInteracao()) {
                    registrarAcaoGranular("SELECIONAR", "Ocultar eixo X",
                            "Alterar visibilidade de apoio visual", "BOTAO_VISIBILIDADE_EIXO_X",
                            "ocultar_eixo_x", "estado_anterior=visivel;estado_atual=oculto",
                            "O eixo X deixou de ser exibido.");
                    itemGraficoInteiros = null;
                    numeroRelativoGraficoInteiros = null;
                } else {
                    sincronizarNumeroRelativoComGraficoSeNecessario();
                }
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
                itemSelecionado = novo;
                itemFocado = novo;
                registrarAcaoGranular("SELECIONAR", "Selecionar elemento semântico do enunciado",
                        "Texto do problema", "Marcador textual",
                        "Escolher o elemento para conduzi-lo ao diagrama",
                        "valor=" + marcador.valor + "; papel="
                                + (marcador.chavePapel != null ? marcador.chavePapel : ""),
                        "Elemento semântico selecionado para manipulação.");

                deslocamentoX = x - novo.x;
                deslocamentoY = y - novo.y;
                iniciarRastreamentoGranular(x, y, novo.valor, "Marcador textual convertido em item", true);
                atualizarRealceAlvoProximidade(novo);
                iniciarFantasmaItem(novo);
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();

                repaint();
                return;
            }


            ElementoTextoMovel elementoTexto = encontrarElementoTextoMovel(x, y);

            if (elementoTexto != null && estaNaAreaDoTexto(x, y)) {
                elementoTextoSelecionado = elementoTexto;
                elementoTextoFocado = elementoTexto;
                itemFocado = null;
                quadradinhoVennFocado = null;
                deslocamentoX = x - elementoTexto.x;
                deslocamentoY = y - elementoTexto.y;
                iniciarRastreamentoGranular(x, y, elementoTexto.valor, "Texto do enunciado", true);
                registrarAcaoGranular("SELECIONAR", "Selecionar elemento textual", "Texto do problema", "Elemento textual", "Escolher texto para manipulação", "texto=" + elementoTexto.valor, "Seleção visual do texto.");
                iniciarFantasmaElementoTexto(elementoTextoSelecionado);
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();
                repaint();
                return;
            }

            quadradinhoVennSelecionado = encontrarQuadradinhoVenn(x, y);

            if (quadradinhoVennSelecionado != null
                    && !interacaoRepresentacoesLiberadaPelaModelagem()) {
                quadradinhoVennSelecionado = null;
                informarBloqueioInteracaoRepresentacao(x, y, "Unidade da representação complementar");
                return;
            }

            if (quadradinhoVennSelecionado != null) {
                indiceCirculoVennOrigemArraste = -1;
                for (int i = 0; i < circulosVenn.size(); i++) {
                    if (circulosVenn.get(i).contem(
                            quadradinhoVennSelecionado.centroX(),
                            quadradinhoVennSelecionado.centroY())) {
                        indiceCirculoVennOrigemArraste = i;
                        break;
                    }
                }
                quadradinhoVennFocado = quadradinhoVennSelecionado;
                itemFocado = null;
                deslocamentoVennX = x - quadradinhoVennSelecionado.x;
                deslocamentoVennY = y - quadradinhoVennSelecionado.y;
                iniciarRastreamentoGranular(x, y, "Elemento do diagrama", "Quadrado do diagrama", false);
                registrarAcaoGranular("SELECIONAR", "Selecionar elemento do diagrama", "Diagrama", "Quadrado", "Escolher objeto para manipulação", "", "Elemento selecionado.");
                iniciarFantasmaQuadradinho(quadradinhoVennSelecionado);
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();
                repaint();
                return;
            }

            itemSelecionado = encontrarItemArrastavel(x, y);

            if (itemSelecionado != null) {
                scaffoldingFeedbackMultissensorialErro.pararTremor();
                itemFocado = itemSelecionado;
                deslocamentoX = x - itemSelecionado.x;
                deslocamentoY = y - itemSelecionado.y;
                iniciarRastreamentoGranular(x, y, itemSelecionado.valor, "Item arrastável", true);
                registrarAcaoGranular("SELECIONAR", "Selecionar item arrastável", "Área de trabalho", "Item arrastável", "Escolher valor para posicionamento", "valor=" + itemSelecionado.valor, "Item selecionado.");
                iniciarFantasmaItem(itemSelecionado);
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();
                repaint();
                return;
            }

            elementoVergnaudSelecionado = encontrarElementoVergnaud(x, y);

            if (elementoVergnaudSelecionado != null) {
                if (politicaGestoEstrutural.ehPressionamentoDeDuploClique(e.getClickCount())) {
                    elementoVergnaudSelecionado = null;
                    return;
                }
                controladorLimiarArrasteEstrutural.iniciar(x, y);
                deslocamentoX = x - elementoVergnaudSelecionado.x;
                deslocamentoY = y - elementoVergnaudSelecionado.y;
                itemFocado = null;
                quadradinhoVennFocado = null;
                iniciarRastreamentoGranular(x, y, "Elemento de Vergnaud", "Elemento do diagrama", false);
                registrarAcaoGranular("SELECIONAR", "Selecionar elemento de Vergnaud", "Diagrama", "Elemento do modelo", "Escolher elemento para reposicionamento", "", "Elemento selecionado.");
                iniciarFantasmaElementoVergnaud(elementoVergnaudSelecionado);
                iniciarArrasteElastico(x, y);
                definirCursorMaoFechada();
                repaint();
                return;
            }

            conectorVergnaudSelecionado = encontrarConectorVergnaud(x, y);

            if (conectorVergnaudSelecionado != null) {
                if (politicaGestoEstrutural.ehPressionamentoDeDuploClique(e.getClickCount())) {
                    conectorVergnaudSelecionado = null;
                    return;
                }
                controladorLimiarArrasteEstrutural.iniciar(x, y);
                itemFocado = null;
                quadradinhoVennFocado = null;
                mouseAnteriorX = x;
                mouseAnteriorY = y;
                iniciarRastreamentoGranular(x, y, "Conector de Vergnaud", "Conector do diagrama", false);
                registrarAcaoGranular("SELECIONAR", "Selecionar conector", "Diagrama", "Conector", "Escolher conector para reposicionamento", "", "Conector selecionado.");
                iniciarFantasmaConector(conectorVergnaudSelecionado);
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

            if (itemSelecionado != null || elementoVergnaudSelecionado != null) {
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
            if (scaffoldingGraficoInteiros.estaArrastando()) {
                scaffoldingGraficoInteiros.arrastarPara(x, y, getWidth(), getHeight());
                sincronizarNumeroRelativoComGraficoSeNecessario();
                repaint();
                return;
            }

            if (arrastandoControleComparacao) {
                aplicarControleComparacaoPeloMouse(y);
                repaint();
                return;
            }

            if (quadradinhoVennSelecionado != null) {
                quadradinhoVennSelecionado.x = x - deslocamentoVennX;
                quadradinhoVennSelecionado.y = y - deslocamentoVennY;
                repaint();
                return;
            }

            if (elementoTextoSelecionado != null) {
                if (podeEnviarParaDiagrama(elementoTextoSelecionado) && !estaNaAreaDoTexto(x, y)) {
                    converterElementoTextoEmItemDiagrama(elementoTextoSelecionado, x, y);
                    repaint();
                    return;
                }

                int novoX = x - deslocamentoX;
                int novoY = y - deslocamentoY;

                if (!ehNumeroOuInterrogacaoDoTexto(elementoTextoSelecionado)) {
                    int limiteEsquerdo = 20;
                    int limiteDireito = getWidth() - 25 - elementoTextoSelecionado.largura;

                    // A coordenada y do ElementoTextoMovel corresponde à linha de base do texto.
                    // Por isso, os limites verticais consideram a altura do texto para impedir
                    // que palavras sem número ultrapassem a área visual do enunciado.
                    int limiteSuperior = 58 + elementoTextoSelecionado.altura;
                    int limiteInferior = 184;

                    if (novoX < limiteEsquerdo) {
                        novoX = limiteEsquerdo;
                    }

                    if (novoX > limiteDireito) {
                        novoX = limiteDireito;
                    }

                    if (novoY < limiteSuperior) {
                        novoY = limiteSuperior;
                    }

                    if (novoY > limiteInferior) {
                        novoY = limiteInferior;
                    }
                }

                elementoTextoSelecionado.x = novoX;
                elementoTextoSelecionado.y = novoY;
                repaint();
                return;
            }

            if (itemSelecionado != null) {
                itemSelecionado.x = x - deslocamentoX;
                itemSelecionado.y = y - deslocamentoY;
                atualizarRealceAlvoProximidade(itemSelecionado);
                atualizarQuestionamentoPersistenteDuranteMovimento(itemSelecionado);
                atualizarGraficoInteirosDuranteMovimento(itemSelecionado);
                repaint();
                return;
            }

            if (elementoVergnaudSelecionado != null) {
                if (!controladorLimiarArrasteEstrutural.deveMovimentar(x, y)) {
                    return;
                }
                Rectangle limite = obterAreaConteudoDiagramaVergnaud();
                elementoVergnaudSelecionado.moverPara(x - deslocamentoX, y - deslocamentoY, limite);
                repaint();
                return;
            }

            if (conectorVergnaudSelecionado != null) {
                if (!controladorLimiarArrasteEstrutural.deveMovimentar(x, y)) {
                    return;
                }
                int dx = x - mouseAnteriorX;
                int dy = y - mouseAnteriorY;
                conectorVergnaudSelecionado.mover(dx, dy, obterAreaConteudoDiagramaVergnaud());
                mouseAnteriorX = x;
                mouseAnteriorY = y;
                repaint();
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (controladorArrasteElastico.estaAtivo()) {
                controladorArrasteElastico.concluir(e.getX(), e.getY());
            }
            controladorLimiarArrasteEstrutural.finalizar();
            boolean houveMovimentoQuadradinhoVenn = quadradinhoVennSelecionado != null;
            int indiceDestinoQuadradinhoVenn = -1;
            if (quadradinhoVennSelecionado != null) {
                for (int i = 0; i < circulosVenn.size(); i++) {
                    if (circulosVenn.get(i).contem(
                            quadradinhoVennSelecionado.centroX(),
                            quadradinhoVennSelecionado.centroY())) {
                        indiceDestinoQuadradinhoVenn = i;
                        break;
                    }
                }
            }
            finalizarRastreamentoGranular(e.getX(), e.getY());
            if (scaffoldingGraficoInteiros.estaArrastando()) {
                sincronizarNumeroRelativoComGraficoSeNecessario();
                scaffoldingGraficoInteiros.finalizarArraste();
                marcadorOrigemArraste.limpar();
                atualizarCursorDepoisDoPickup(e.getX(), e.getY());
                repaint();
                return;
            }

            if (arrastandoControleComparacao) {
                arrastandoControleComparacao = false;
                marcadorOrigemArraste.limpar();
                atualizarCursorDepoisDoPickup(e.getX(), e.getY());
                repaint();
                return;
            }

            ItemTextoArrastavel itemSolto = itemSelecionado;
            if (itemSelecionado != null) {
                ElementoVergnaud alvo = obterAlvoCorretoParaItem(itemSelecionado);
                boolean proximo = alvo != null && itemEstaProximoDoElemento(itemSelecionado, alvo);
                if (alvo != null && scaffoldingProximidade.deveCentralizarAoSoltar(modoFeedbackTeste, proximo)) {
                    centralizarItemNoElemento(itemSelecionado, alvo);
                }
            }

            ResultadoQuestionamento resultadoPosicionamento =
                    avaliarQuestionamentoPosicionamento(itemSolto);
            boolean posicionamentoIncorreto = resultadoPosicionamento.isAplicavel()
                    && !resultadoPosicionamento.isCorreto();
            finalizarProxyTextoSolto(itemSolto, !posicionamentoIncorreto);
            itemSelecionado = null;
            limparRealceAlvoProximidade();
            quadradinhoVennSelecionado = null;
            elementoVergnaudSelecionado = null;
            conectorVergnaudSelecionado = null;
            elementoTextoSelecionado = null;

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
            registrarLogSolturaItem(itemSolto);
            if (!posicionamentoIncorreto) {
                atualizarHabilitacaoSincronizacaoEstadoFinal(itemSolto);
                processarSolturaEmNumeroRelativo(itemSolto);
                atualizarRepresentacoesReativasAposAlteracaoDoItem(itemSolto);
            }
            if (houveMovimentoQuadradinhoVenn) {
                int indiceAlterado = indiceDestinoQuadradinhoVenn >= 0
                        ? indiceDestinoQuadradinhoVenn
                        : indiceCirculoVennOrigemArraste;
                sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(
                        indiceAlterado,
                        EstadoSemanticoCompartilhado.Origem.ARRASTE);
                indiceCirculoVennOrigemArraste = -1;
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
        }

        private void registrarLogComputador(String tarefa,
                String instrumentoOrganizacao,
                String instrumentoArtefato,
                String funcaoDoArtefato,
                String regras,
                String origemEvento,
                String detalhes) {
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

        private void registrarLogSolturaItem(ItemTextoArrastavel item) {
            if (item == null) {
                return;
            }

            ElementoVergnaud elemento = encontrarElementoVergnaudPorItem(item);
            String artefato = elemento != null ? descreverElementoVergnaudParaLog(elemento) : "Fora do diagrama";
            String ce = "-";
            String regras = "A ação não foi avaliada como acerto ou erro matemático.";

            if (elemento != null && scaffoldingNumeroRelativo.ehNumeroOuInterrogacao(item.valor)) {
                ResultadoQuestionamento resultado = avaliarQuestionamentoPosicionamento(item);
                if (resultado.isAplicavel()) {
                    ce = resultado.isCorreto() ? "C" : "E";
                    regras = resultado.isCorreto()
                            ? "O valor foi associado ao elemento do modelo que representa sua função no problema."
                            : "O valor deve ser associado ao elemento do modelo que representa sua função no problema.";
                }
            }

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
            int indice = elementosVergnaud.indexOf(elemento);
            String chave = scaffoldingQuestionamento.obterChavePapelDoElemento(
                    tipoSituacaoSelecionada,
                    indice,
                    usaDiagramasEncadeadosTransformacaoComposta(),
                    quantidadePassosTransformacaoComposta
            );
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
            if (ehElementoNumeroRelativo(elemento)) {
                return "OBJ4";
            }
            String descricao = descreverElementoVergnaudParaLog(elemento).toLowerCase();
            if (descricao.indexOf("estado inicial") >= 0) {
                return "OBJ1";
            }
            if (descricao.indexOf("estado final") >= 0) {
                return "OBJ2";
            }
            if (descricao.indexOf("referente") >= 0) {
                return "OBJ3";
            }
            if (descricao.indexOf("referido") >= 0) {
                return "OBJ5";
            }
            if (descricao.indexOf("todo") >= 0) {
                return "OBJ6";
            }
            if (descricao.indexOf("parte") >= 0) {
                return "OBJ7";
            }
            return "OBJ8";
        }

        private void processarSolturaEmNumeroRelativo(ItemTextoArrastavel item) {
            if (item == null || !item.estaNoDiagrama()) {
                limparGraficoInteirosSeForItemAtivo(item);
                return;
            }
            ElementoVergnaud numeroRelativo = encontrarNumeroRelativoPorItem(item);
            if (numeroRelativo == null) {
                limparGraficoInteirosSeForItemAtivo(item);
                return;
            }
            if (!scaffoldingNumeroRelativo.ehNumeroOuInterrogacao(item.valor)) {
                limparGraficoInteirosSeForItemAtivo(item);
                return;
            }
            if (deveBloquearMenuNumeroRelativoPorQuestionamento(item, numeroRelativo)) {
                limparGraficoInteiros();
                return;
            }
            if (scaffoldingNumeroRelativo.temSinal(item.valor)) {
                String base = scaffoldingNumeroRelativo.removerSinal(item.valor);
                String sinal = obterSinalAtual(item.valor);
                mostrarGraficoInteirosNumeroRelativo(item, numeroRelativo, base);
                registrarEscolhaGraficoInteiros(item, numeroRelativo, base, sinal);
                atualizarEstadoFinalAPartirDoNumeroRelativo(numeroRelativo, calcularValorRelativo(base, sinal));
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

        private ResultadoQuestionamento avaliarQuestionamentoPosicionamento(ItemTextoArrastavel item) {
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

            int indiceAlvo = elementosVergnaud.indexOf(elementoAlvo);
            String chavePapelNumeral = chavePapelElementoTexto;
            String chavePapelAlvo = scaffoldingQuestionamento.obterChavePapelDoElemento(
                    tipoSituacaoSelecionada,
                    indiceAlvo,
                    usaDiagramasEncadeadosTransformacaoComposta(),
                    quantidadePassosTransformacaoComposta
            );
            String papelDoElementoNoDiagrama = localizacao.texto(chavePapelAlvo);

            ResultadoQuestionamento resultado = agenteMonitor.avaliarPosicionamento(
                    chavePapelNumeral,
                    chavePapelAlvo,
                    papelDoElementoNoDiagrama,
                    localizacao.descricaoTipo(tipoSituacaoSelecionada)
            );
            if (resultado != null && resultado.isAplicavel()) {
                conectorVereditoModelador.registrarVeredito(
                        loggerInteracaoGerard.getUsuarioAtual(), tipoSituacaoSelecionada, chavePapelAlvo);
            }
            return resultado;
        }

        private boolean processarQuestionamentoPosicionamento(ItemTextoArrastavel item) {
            ResultadoQuestionamento resultado = avaliarQuestionamentoPosicionamento(item);

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
            ResultadoQuestionamento resultado = avaliarQuestionamentoPosicionamento(item);
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

            ResultadoQuestionamento resultado = avaliarQuestionamentoPosicionamento(item);
            if (!resultado.isAplicavel() || resultado.isCorreto()) {
                limparQuestionamentoPersistente();
                return;
            }

            textoQuestionamentoPersistente = resultado.getMensagem();
            mostrarQuestionamentoPersistente = true;
        }

        private void mostrarGraficoInteirosNumeroRelativo(ItemTextoArrastavel item, ElementoVergnaud numeroRelativo, String valorBase) {
            if (numeroRelativo == null) {
                return;
            }
            itemGraficoInteiros = item;
            numeroRelativoGraficoInteiros = numeroRelativo;
            scaffoldingGraficoInteiros.mostrar(
                    retanguloDoElemento(numeroRelativo),
                    valorBase
            );
        }

        private void registrarEscolhaGraficoInteiros(ItemTextoArrastavel item, ElementoVergnaud numeroRelativo, String valorBase, String sinal) {
            if (numeroRelativo == null) {
                return;
            }
            if (!scaffoldingGraficoInteiros.isVisivel()) {
                mostrarGraficoInteirosNumeroRelativo(item, numeroRelativo, valorBase);
            } else {
                scaffoldingGraficoInteiros.atualizarCirculo(retanguloDoElemento(numeroRelativo));
            }
            itemGraficoInteiros = item;
            numeroRelativoGraficoInteiros = numeroRelativo;
            scaffoldingGraficoInteiros.registrarEscolha(valorBase, sinal);
        }

        private void sincronizarNumeroRelativoComGraficoSeNecessario() {
            if (!scaffoldingGraficoInteiros.houveAlteracaoValorPorInteracao()) {
                return;
            }
            if (numeroRelativoGraficoInteiros == null) {
                scaffoldingGraficoInteiros.limparAlteracaoValorPorInteracao();
                return;
            }

            int valor = scaffoldingGraficoInteiros.getValorNavegavel();
            Integer valorAnterior = obterValorNumericoDoElemento(
                    numeroRelativoGraficoInteiros);
            if (!valorRelativoPreservaQuantidadesNaoNegativas(
                    numeroRelativoGraficoInteiros, valor)) {
                int seguro = valorAnterior == null ? Math.abs(valor) : valorAnterior.intValue();
                aplicarValorRelativoNoDiagrama(
                        numeroRelativoGraficoInteiros,
                        itemGraficoInteiros,
                        seguro,
                        true
                );
                informarBloqueioQuantidadeNegativa();
                scaffoldingGraficoInteiros.limparAlteracaoValorPorInteracao();
                repaint();
                return;
            }
            registrarLogUsuario(
                    "Navegar no eixo x dos inteiros",
                    "-",
                    "Eixo x navegável",
                    "Ponto de controle do eixo",
                    "Quantificar o número relativo e manter consistência entre representações",
                    "OBJ4",
                    "Ao alterar o eixo, o círculo da relação e os valores dependentes do diagrama devem ser atualizados.",
                    "EIXO_X",
                    "valorRelativo=" + valor
            );
            aplicarValorRelativoNoDiagrama(
                    numeroRelativoGraficoInteiros,
                    itemGraficoInteiros,
                    valor,
                    true
            );
            reagirConsistenciaAPartirDoElemento(
                    numeroRelativoGraficoInteiros,
                    ScaffoldingReacaoRepresentacoes.OrigemAlteracao.EIXO
            );
            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                    numeroRelativoGraficoInteiros,
                    EstadoSemanticoCompartilhado.Origem.EIXO_X);
            scaffoldingGraficoInteiros.atualizarCirculo(retanguloDoElemento(numeroRelativoGraficoInteiros));
            scaffoldingGraficoInteiros.limparAlteracaoValorPorInteracao();
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

            ResultadoQuestionamento resultado = avaliarQuestionamentoPosicionamento(item);
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
            if (elemento == null || !ehElementoMedidaRetangular(elemento)) {
                return false;
            }
            int indice = elementosVergnaud.indexOf(elemento);
            if (indice < 0) {
                return false;
            }
            String chavePapel = scaffoldingQuestionamento.obterChavePapelDoElemento(
                    tipoSituacaoSelecionada,
                    indice,
                    usaDiagramasEncadeadosTransformacaoComposta(),
                    quantidadePassosTransformacaoComposta
            );
            return elemento.incognitaPrincipal || "papel.estadoFinal".equals(chavePapel);
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
            atualizarEstadoFinalAPartirDoNumeroRelativo(numeroRelativo, valorRelativo.intValue());
        }

        private void atualizarRepresentacoesReativasAposAlteracaoDoItem(ItemTextoArrastavel item) {
            if (item == null) {
                return;
            }

            ElementoVergnaud elemento = encontrarElementoVergnaudPorItem(item);
            if (elemento == null) {
                return;
            }

            ScaffoldingReacaoRepresentacoes.OrigemAlteracao origem = ehElementoNumeroRelativo(elemento)
                    ? ScaffoldingReacaoRepresentacoes.OrigemAlteracao.NUMERO_RELATIVO
                    : ScaffoldingReacaoRepresentacoes.OrigemAlteracao.ARRASTE;
            reagirConsistenciaAPartirDoElemento(elemento, origem);
            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                    elemento, EstadoSemanticoCompartilhado.Origem.ARRASTE);
            verificarConclusaoModelagem();
        }

        private void atualizarRepresentacoesReativasAposAlteracaoDoElemento(ElementoVergnaud elemento) {
            if (elemento == null) {
                return;
            }

            ScaffoldingReacaoRepresentacoes.OrigemAlteracao origem = ehElementoNumeroRelativo(elemento)
                    ? ScaffoldingReacaoRepresentacoes.OrigemAlteracao.NUMERO_RELATIVO
                    : ScaffoldingReacaoRepresentacoes.OrigemAlteracao.TEXTO;
            reagirConsistenciaAPartirDoElemento(elemento, origem);
            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                    elemento, EstadoSemanticoCompartilhado.Origem.EDICAO_TEXTO);
            verificarConclusaoModelagem();
        }

        private void atualizarGraficoAPartirDoNumeroRelativo(ElementoVergnaud numeroRelativo) {
            if (numeroRelativo == null || !ehElementoNumeroRelativo(numeroRelativo)) {
                return;
            }

            Integer valorRelativo = obterValorNumericoDoElemento(numeroRelativo);
            if (valorRelativo == null) {
                return;
            }

            String textoRelativo = formatarValorRelativoParaDiagrama(valorRelativo.intValue());
            String base = scaffoldingNumeroRelativo.removerSinal(textoRelativo);
            String sinal = obterSinalAtual(textoRelativo);
            ItemTextoArrastavel item = encontrarItemSobreElemento(numeroRelativo);
            registrarEscolhaGraficoInteiros(item, numeroRelativo, base, sinal);
        }

        private void reagirConsistenciaAPartirDoElemento(ElementoVergnaud elemento, ScaffoldingReacaoRepresentacoes.OrigemAlteracao origem) {
            if (elemento == null) {
                return;
            }

            int indice = elementosVergnaud.indexOf(elemento);
            if (indice < 0) {
                return;
            }

            if (ehElementoNumeroRelativo(elemento)) {
                atualizarGraficoAPartirDoNumeroRelativo(elemento);
                Integer valorRelativo = obterValorNumericoDoElemento(elemento);
                if (valorRelativo != null) {
                    reagirConsistenciaDaTransformacao(indice, origem, valorRelativo);
                }
                return;
            }

            if (!ehElementoMedidaRetangular(elemento)) {
                return;
            }

            // Quando a medida alterada vem antes de um circulo, ela funciona como
            // estado inicial daquela transformacao.
            if (indice + 2 < elementosVergnaud.size()
                    && ehElementoNumeroRelativo(elementosVergnaud.get(indice + 1))
                    && ehElementoMedidaRetangular(elementosVergnaud.get(indice + 2))) {
                reagirConsistenciaDaTransformacao(
                        indice + 1,
                        ScaffoldingReacaoRepresentacoes.OrigemAlteracao.ESTADO_INICIAL,
                        null
                );
            }

            // Quando a medida alterada vem depois de um circulo, ela funciona como
            // estado final daquela transformacao; nesse caso a relacao deve ser
            // recalculada para manter a consistencia.
            if (indice - 2 >= 0
                    && ehElementoNumeroRelativo(elementosVergnaud.get(indice - 1))
                    && ehElementoMedidaRetangular(elementosVergnaud.get(indice - 2))) {
                reagirConsistenciaDaTransformacao(
                        indice - 1,
                        ScaffoldingReacaoRepresentacoes.OrigemAlteracao.ESTADO_FINAL,
                        null
                );
            }

            // Na categoria composicao seguida de transformacao, o todo da composicao
            // alimenta o estado inicial da transformacao seguinte. Se esse todo mudar,
            // a transformacao tambem deve reagir.
            if (usaDiagramasComposicaoTransformacaoMedidas()
                    && indice == 2
                    && elementosVergnaud.size() > 5
                    && ehElementoNumeroRelativo(elementosVergnaud.get(4))) {
                reagirConsistenciaDaTransformacao(
                        4,
                        ScaffoldingReacaoRepresentacoes.OrigemAlteracao.ESTADO_INICIAL,
                        null
                );
            }
        }

        private void reagirConsistenciaDaTransformacao(int indiceRelacao,
                ScaffoldingReacaoRepresentacoes.OrigemAlteracao origem,
                Integer valorRelativoForcado) {
            if (indiceRelacao < 1 || indiceRelacao + 1 >= elementosVergnaud.size()) {
                return;
            }

            ElementoVergnaud relacao = elementosVergnaud.get(indiceRelacao);
            ElementoVergnaud estadoInicial = elementosVergnaud.get(indiceRelacao - 1);
            ElementoVergnaud estadoFinal = elementosVergnaud.get(indiceRelacao + 1);

            if (!ehElementoNumeroRelativo(relacao)
                    || !ehElementoMedidaRetangular(estadoInicial)
                    || !ehElementoMedidaRetangular(estadoFinal)) {
                return;
            }

            Integer valorRelativo = valorRelativoForcado != null
                    ? valorRelativoForcado
                    : obterValorNumericoDoElemento(relacao);
            Integer valorInicial = obterValorEstadoInicialParaRelacao(indiceRelacao);
            Integer valorFinal = obterValorNumericoDoElemento(estadoFinal);

            if (origem == ScaffoldingReacaoRepresentacoes.OrigemAlteracao.ESTADO_FINAL) {
                Integer novaRelacao = scaffoldingReacaoRepresentacoes.calcularRelacao(valorInicial, valorFinal);
                if (novaRelacao != null) {
                    definirValorNoElementoNumeroRelativo(relacao, novaRelacao.intValue(), true);
                }
                return;
            }

            if (origem == ScaffoldingReacaoRepresentacoes.OrigemAlteracao.NUMERO_RELATIVO
                    || origem == ScaffoldingReacaoRepresentacoes.OrigemAlteracao.EIXO
                    || origem == ScaffoldingReacaoRepresentacoes.OrigemAlteracao.ESTADO_INICIAL
                    || origem == ScaffoldingReacaoRepresentacoes.OrigemAlteracao.ARRASTE
                    || origem == ScaffoldingReacaoRepresentacoes.OrigemAlteracao.TEXTO) {
                Integer novoFinal = scaffoldingReacaoRepresentacoes.calcularEstadoFinal(valorInicial, valorRelativo);
                if (novoFinal != null
                        && politicaValoresAditivos.quantidadeEhNaoNegativa(novoFinal)) {
                    String valorFormatado = servicoQuantidadeContextual
                            .formatarInteiroLegado(novoFinal.intValue(),
                                    situacaoProblemaAtual, false);
                    definirValorNoElementoMedida(estadoFinal, valorFormatado);
                    propagarTextoEntrePassosTransformacaoComposta(
                            estadoFinal, valorFormatado);
                }
                return;
            }

            Integer novoFinal = scaffoldingReacaoRepresentacoes.calcularEstadoFinal(valorInicial, valorRelativo);
            if (novoFinal != null
                    && politicaValoresAditivos.quantidadeEhNaoNegativa(novoFinal)) {
                String valorFormatado = servicoQuantidadeContextual
                        .formatarInteiroLegado(novoFinal.intValue(),
                                situacaoProblemaAtual, false);
                definirValorNoElementoMedida(estadoFinal, valorFormatado);
                propagarTextoEntrePassosTransformacaoComposta(
                        estadoFinal, valorFormatado);
                return;
            }

            Integer novaRelacao = scaffoldingReacaoRepresentacoes.calcularRelacao(valorInicial, valorFinal);
            if (novaRelacao != null) {
                definirValorNoElementoNumeroRelativo(relacao, novaRelacao.intValue(), true);
            }
        }

        private Integer obterValorEstadoInicialParaRelacao(int indiceRelacao) {
            if (indiceRelacao < 1 || indiceRelacao >= elementosVergnaud.size()) {
                return null;
            }

            Integer valorInicial = obterValorNumericoDoElemento(elementosVergnaud.get(indiceRelacao - 1));

            // Na categoria "Composicao seguida de transformacao", o estado inicial
            // da transformacao pode ser o todo obtido no primeiro diagrama.
            if (valorInicial == null
                    && usaDiagramasComposicaoTransformacaoMedidas()
                    && indiceRelacao == 4
                    && elementosVergnaud.size() > 2) {
                valorInicial = obterValorNumericoDoElemento(elementosVergnaud.get(2));
            }

            return valorInicial;
        }

        private void aplicarValorRelativoNoDiagrama(ElementoVergnaud numeroRelativo,
                ItemTextoArrastavel itemPreferencial,
                int valorRelativo,
                boolean atualizarGrafico) {
            if (numeroRelativo == null
                    || devePreservarMarcadorIncognita(numeroRelativo)) {
                return;
            }

            String textoRelativo = formatarValorRelativoParaDiagrama(valorRelativo);
            ItemTextoArrastavel item = itemPreferencial != null ? itemPreferencial : encontrarItemSobreElemento(numeroRelativo);

            if (item != null) {
                item.valor = textoRelativo;
                ajustarTamanhoDoItem(item);
                centralizarItemNoNumeroRelativoSeNecessario(item);
                numeroRelativo.textoEditavel = "";
            } else {
                numeroRelativo.textoEditavel = textoRelativo;
                propagarTextoEntrePassosTransformacaoComposta(numeroRelativo, textoRelativo);
            }

            if (atualizarGrafico) {
                registrarEscolhaGraficoInteiros(
                        item,
                        numeroRelativo,
                        scaffoldingReacaoRepresentacoes.valorAbsolutoComoTexto(valorRelativo),
                        scaffoldingReacaoRepresentacoes.sinalDe(valorRelativo)
                );
            }
        }

        private void definirValorNoElementoNumeroRelativo(ElementoVergnaud numeroRelativo, int valorRelativo, boolean atualizarGrafico) {
            aplicarValorRelativoNoDiagrama(numeroRelativo, null, valorRelativo, atualizarGrafico);
        }

        private ElementoVergnaud encontrarNumeroRelativoParaSincronizacaoEstadoFinal() {
            if (numeroRelativoGraficoInteiros != null && elementosVergnaud.contains(numeroRelativoGraficoInteiros)) {
                return numeroRelativoGraficoInteiros;
            }
            for (int i = 0; i < elementosVergnaud.size(); i++) {
                ElementoVergnaud elemento = elementosVergnaud.get(i);
                if (ehElementoNumeroRelativo(elemento) && obterValorNumericoDoElemento(elemento) != null) {
                    return elemento;
                }
            }
            return null;
        }

        private void atualizarEstadoFinalAPartirDoNumeroRelativo(ElementoVergnaud numeroRelativo, int valorRelativo) {
            if (numeroRelativo == null) {
                return;
            }
            int indiceRelacao = elementosVergnaud.indexOf(numeroRelativo);
            reagirConsistenciaDaTransformacao(
                    indiceRelacao,
                    ScaffoldingReacaoRepresentacoes.OrigemAlteracao.NUMERO_RELATIVO,
                    Integer.valueOf(valorRelativo)
            );
        }

        private boolean ehElementoMedidaRetangular(ElementoVergnaud elemento) {
            return elemento != null
                    && (elemento.tipo == TipoFiguraDiagrama.RETANGULO
                    || elemento.tipo == TipoFiguraDiagrama.RETANGULO_ARREDONDADO);
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

        private String formatarValorRelativoParaDiagrama(int valor) {
            return servicoQuantidadeContextual.formatarInteiroLegado(
                    valor, situacaoProblemaAtual, true);
        }

        private void atualizarGraficoInteirosDuranteMovimento(ItemTextoArrastavel item) {
            if (item == null || item != itemGraficoInteiros) {
                return;
            }
            ElementoVergnaud numeroRelativoAtual = encontrarNumeroRelativoPorItem(item);
            if (numeroRelativoAtual == null || numeroRelativoAtual != numeroRelativoGraficoInteiros) {
                limparGraficoInteiros();
                return;
            }
            scaffoldingGraficoInteiros.atualizarCirculo(retanguloDoElemento(numeroRelativoAtual));
        }

        private void limparGraficoInteirosSeForItemAtivo(ItemTextoArrastavel item) {
            if (item != null && item == itemGraficoInteiros) {
                limparGraficoInteiros();
            }
        }

        private void limparGraficoInteiros() {
            itemGraficoInteiros = null;
            numeroRelativoGraficoInteiros = null;
            scaffoldingGraficoInteiros.ocultar();
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
            return elemento != null && elemento.tipo == TipoFiguraDiagrama.ELIPSE;
        }

        private int calcularValorRelativo(String base, String sinal) {
            return scaffoldingReacaoRepresentacoes.calcularValorRelativo(base, sinal);
        }

        private boolean primeiraQuantidadeDaRelacaoEhDesconhecida() {
            if (situacaoProblemaAtual == null) {
                return false;
            }
            String desconhecido = normalizarChaveComparacao(
                    situacaoProblemaAtual.getTermoDesconhecido());
            return desconhecido.contains("referido")
                    || desconhecido.contains("estado_inicial");
        }

        private ScaffoldingReacaoRepresentacoes.ResultadoQuantidadeDependente
                calcularQuantidadeDependenteDoValorRelativo(
                        ElementoVergnaud relacao, int valorRelativo) {
            if (relacao == null || elementosVergnaud == null) {
                return ScaffoldingReacaoRepresentacoes.ResultadoQuantidadeDependente.ausente();
            }
            int indiceRelacao = elementosVergnaud.indexOf(relacao);
            if (indiceRelacao < 1 || indiceRelacao + 1 >= elementosVergnaud.size()) {
                return ScaffoldingReacaoRepresentacoes.ResultadoQuantidadeDependente.ausente();
            }

            Integer primeiraQuantidade = obterValorEstadoInicialParaRelacao(indiceRelacao);
            Integer terceiraQuantidade = obterValorNumericoDoElemento(
                    elementosVergnaud.get(indiceRelacao + 1));

            return scaffoldingReacaoRepresentacoes.calcularQuantidadeDependente(
                    indiceRelacao,
                    primeiraQuantidade,
                    terceiraQuantidade,
                    primeiraQuantidadeDaRelacaoEhDesconhecida(),
                    valorRelativo
            );
        }

        private boolean valorRelativoPreservaQuantidadesNaoNegativas(
                ElementoVergnaud relacao, int valorRelativo) {
            ScaffoldingReacaoRepresentacoes.ResultadoQuantidadeDependente resultado =
                    calcularQuantidadeDependenteDoValorRelativo(
                            relacao, valorRelativo);
            if (!resultado.foiCalculado()) {
                return true;
            }
            return politicaValoresAditivos.valorEhValidoParaElemento(
                    tipoSituacaoSelecionada,
                    resultado.getIndiceDependente(),
                    usaDiagramasEncadeadosTransformacaoComposta(),
                    quantidadePassosTransformacaoComposta,
                    resultado.getValor()
            );
        }

        private void informarBloqueioQuantidadeNegativa() {
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

            if (itemGraficoInteiros != null) {
                scaffoldingFeedbackMultissensorialErro.sinalizarErro(itemGraficoInteiros, new Runnable() {
                    public void run() {
                        repaint();
                    }
                });
            }

            Rectangle ancora = numeroRelativoGraficoInteiros != null
                    ? retanguloDoElemento(numeroRelativoGraficoInteiros)
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

        private void restaurarValorRelativoPositivoSeguro(
                ElementoVergnaud relacao, ItemTextoArrastavel item, String base) {
            int valorSeguro = Math.abs(calcularValorRelativo(base, "+"));
            aplicarValorRelativoNoDiagrama(relacao, item, valorSeguro, true);
            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                    relacao, EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        }

        private void solicitarSinalNumeroRelativoParaTexto(final ElementoVergnaud elemento) {
            final String base = scaffoldingNumeroRelativo.removerSinal(elemento.textoEditavel);
            mostrarGraficoInteirosNumeroRelativo(null, elemento, base);
            scaffoldingNumeroRelativo.mostrarMenuEscolhaSinal(
                    this,
                    new Rectangle(elemento.x, elemento.y, elemento.largura, elemento.altura),
                    base,
                    new ScaffoldingNumeroRelativo.AcaoSinalNumeroRelativo() {
                        public void sinalEscolhido(String sinal) {
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
                            propagarTextoEntrePassosTransformacaoComposta(elemento, elemento.textoEditavel);
                            registrarEscolhaGraficoInteiros(null, elemento, base, sinal);
                            reagirConsistenciaAPartirDoElemento(
                                    elemento,
                                    ScaffoldingReacaoRepresentacoes.OrigemAlteracao.NUMERO_RELATIVO
                            );
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
            mostrarGraficoInteirosNumeroRelativo(item, numeroRelativo, base);
            final ElementoVergnaud numeroRelativoFinal = numeroRelativo;
            scaffoldingNumeroRelativo.mostrarMenuEscolhaSinal(
                    this,
                    new Rectangle(numeroRelativo.x, numeroRelativo.y, numeroRelativo.largura, numeroRelativo.altura),
                    base,
                    new ScaffoldingNumeroRelativo.AcaoSinalNumeroRelativo() {
                        public void sinalEscolhido(String sinal) {
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
                            ajustarTamanhoDoItem(item);
                            centralizarItemNoNumeroRelativoSeNecessario(item);
                            registrarEscolhaGraficoInteiros(item, numeroRelativoFinal, base, sinal);
                            reagirConsistenciaAPartirDoElemento(
                                    numeroRelativoFinal,
                                    ScaffoldingReacaoRepresentacoes.OrigemAlteracao.NUMERO_RELATIVO
                            );
                            sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                                    numeroRelativoFinal,
                                    EstadoSemanticoCompartilhado.Origem.ARRASTE);
                            verificarConclusaoModelagem();
                            repaint();
                        }
                    }
            );
        }

        private void centralizarItemNoNumeroRelativoSeNecessario(ItemTextoArrastavel item) {
            ElementoVergnaud numeroRelativo = encontrarNumeroRelativoPorItem(item);
            if (item == null || numeroRelativo == null) {
                return;
            }
            int centroX = item.x + item.largura / 2;
            int centroY = item.y + item.altura / 2;
            if (numeroRelativo.contem(centroX, centroY)) {
                expandirElementoParaCaberItem(numeroRelativo, item);
                item.x = numeroRelativo.x + (numeroRelativo.largura - item.largura) / 2;
                item.y = numeroRelativo.y + (numeroRelativo.altura - item.altura) / 2;
            }
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
                propagarTextoEntrePassosTransformacaoComposta(elemento, elemento.textoEditavel);
                if (ehElementoNumeroRelativo(elemento) && scaffoldingNumeroRelativo.ehNumeroOuInterrogacao(elemento.textoEditavel)) {
                    solicitarSinalNumeroRelativoParaTexto(elemento);
                } else {
                    atualizarRepresentacoesReativasAposAlteracaoDoElemento(elemento);
                }
                repaint();
            }
        }

        private void propagarTextoEntrePassosTransformacaoComposta(ElementoVergnaud elemento, String texto) {
            if (!usaDiagramasEncadeadosTransformacaoComposta() || elemento == null) {
                return;
            }

            int indice = elementosVergnaud.indexOf(elemento);
            if (indice < 0) {
                return;
            }

            int indiceLocalNoPasso = indice % 3;
            int indicePassoSeguinte = indice + 1;

            if (indiceLocalNoPasso == 2 && indicePassoSeguinte < elementosVergnaud.size()) {
                ElementoVergnaud proximoEstadoInicial = elementosVergnaud.get(indicePassoSeguinte);
                if (!devePreservarMarcadorIncognita(proximoEstadoInicial)) {
                    proximoEstadoInicial.textoEditavel = texto != null ? texto : "";
                }
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
            String texto = solicitarTextoEditavel(formatarValorRelativoParaDiagrama(valorAtual));
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
            if (elementosVergnaud == null || elementosVergnaud.size() < 3) {
                return;
            }

            ElementoVergnaud referido = elementosVergnaud.get(0);
            ElementoVergnaud relacao = elementosVergnaud.get(1);
            ElementoVergnaud referendo = elementosVergnaud.get(2);
            if (!valorRelativoPreservaQuantidadesNaoNegativas(relacao, valorRelativo)) {
                informarBloqueioQuantidadeNegativa();
                sincronizarTodasAsRepresentacoesAPartirDoVergnaud(
                        relacao, EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
                repaint();
                return;
            }
            definirValorNoElementoNumeroRelativo(relacao, valorRelativo, true);

            Integer valorReferido = obterValorNumericoDoElemento(referido);
            Integer valorReferendo = obterValorNumericoDoElemento(referendo);
            String desconhecido = situacaoProblemaAtual == null
                    ? ""
                    : normalizarChaveComparacao(situacaoProblemaAtual.getTermoDesconhecido());

            if (desconhecido.contains("referido") && valorReferendo != null) {
                definirValorNoElementoMedida(referido,
                        Integer.toString(valorReferendo.intValue() - valorRelativo));
            } else if (valorReferido != null) {
                definirValorNoElementoMedida(referendo,
                        Integer.toString(valorReferido.intValue() + valorRelativo));
            } else if (valorReferendo != null) {
                definirValorNoElementoMedida(referido,
                        Integer.toString(valorReferendo.intValue() - valorRelativo));
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
                    String ceIncognita = "-";
                    String regrasIncognita = "A ação não foi avaliada como acerto ou erro matemático.";
                    ResultadoQuestionamento resultadoIncognita = avaliarQuestionamentoPosicionamento(item);
                    if (resultadoIncognita.isAplicavel()) {
                        ceIncognita = resultadoIncognita.isCorreto() ? "C" : "E";
                        regrasIncognita = resultadoIncognita.isCorreto()
                                ? "O valor foi associado ao elemento do modelo que representa sua função no problema."
                                : "O valor deve ser associado ao elemento do modelo que representa sua função no problema.";
                    }
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
                    ElementoVergnaud numeroRelativo = encontrarNumeroRelativoPorItem(item);
                    boolean preenchimentoDeInterrogacao = SimboloDesconhecido.eh(item.origemValor)
                            || SimboloDesconhecido.eh(scaffoldingNumeroRelativo.removerSinal(item.valor));
                    if (numeroRelativo != null) {
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

            item.largura = fm.stringWidth(item.valor) + 8;
            item.altura = fm.getHeight() - 5;
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {
            mostrarAnotacaoMouseOver = false;
            agrupamentoAdicionarQuadradinhoFocado = null;
            agrupamentoRemoverQuadradinhoFocado = null;
            scaffoldingGraficoInteiros.limparFocoBotaoEsconder();
            limparRealceAlvoProximidade();
            setCursor(Cursor.getDefaultCursor());
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
            mouseOverX = e.getX();
            mouseOverY = e.getY();
            boolean interacaoRepresentacoesLiberada =
                    interacaoRepresentacoesLiberadaPelaModelagem();
            if (interacaoRepresentacoesLiberada) {
                scaffoldingGraficoInteiros.atualizarFocoBotaoEsconder(e.getX(), e.getY());
            } else {
                scaffoldingGraficoInteiros.limparFocoBotaoEsconder();
                Rectangle areaEixo = scaffoldingGraficoInteiros.obterAreaVisualPainel();
                if (areaEixo.contains(e.getX(), e.getY())) {
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

            if (scaffoldingGraficoInteiros.contemBotaoEsconder(e.getX(), e.getY())) {
                elementoTextoFocado = null;
                itemFocado = null;
                mostrarAnotacaoMouseOver = true;
                textoAnotacaoMouseOver = scaffoldingGraficoInteiros.obterDicaBotaoEsconder();
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
                return;
            }

            if (scaffoldingGraficoInteiros.contemPontoControle(e.getX(), e.getY())) {
                elementoTextoFocado = null;
                itemFocado = null;
                mostrarAnotacaoMouseOver = true;
                textoAnotacaoMouseOver = scaffoldingGraficoInteiros.obterDicaPontoControle();
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

            ElementoTextoMovel elementoTexto = encontrarElementoTextoMovel(e.getX(), e.getY());

            if (elementoTexto != null && estaNaAreaDoTexto(e.getX(), e.getY())) {
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

                if (pontoNoTituloCategoriaEnunciado(e.getX(), e.getY())) {
                    mostrarAnotacaoMouseOver = true;
                    textoAnotacaoMouseOver = areaTituloCategoria.getDescricao();
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
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
                        ItemTextoArrastavel item = encontrarItemArrastavel(e.getX(), e.getY());
                        if (item != null && item.estaNoDiagrama()) {
                            itemFocado = item;
                            mostrarAnotacaoMouseOver = true;
                            if (mostrarQuestionamentoPersistente && item == itemQuestionadoPersistente) {
                                textoAnotacaoMouseOver = textoQuestionamentoPersistente;
                            } else {
                                textoAnotacaoMouseOver = criarMensagemPapelItemArrastavel(item);
                            }
                            definirCursorMaoAberta();
                        } else if (encontrarElementoVergnaud(e.getX(), e.getY()) != null || encontrarConectorVergnaud(e.getX(), e.getY()) != null) {
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
                String chaveIncognita = aplicarFallbackCuradoItemDesconhecido(
                        item.chavePapel == null ? "papel.valor" : item.chavePapel);
                if (chavePapelEspecifica(chaveIncognita)) {
                    return chaveIncognita;
                }
                String chavePorInterrogacao = obterChavePapelExataPorValor("?");
                if (chavePapelEspecifica(chavePorInterrogacao)) {
                    return chavePorInterrogacao;
                }
            }

            if (chavePapelEspecifica(item.chavePapel)) {
                return item.chavePapel;
            }

            String chavePorValor = obterChavePapelExataPorValor(item.origemValor);
            if (chavePapelEspecifica(chavePorValor)) {
                return chavePorValor;
            }

            return item.chavePapel != null ? item.chavePapel : "papel.valor";
        }

        private boolean chavePapelEspecifica(String chavePapel) {
            if (chavePapel == null) {
                return false;
            }

            return "papel.parte1".equals(chavePapel) ||
                   "papel.parte2".equals(chavePapel) ||
                   "papel.todo".equals(chavePapel) ||
                   "papel.estadoInicial".equals(chavePapel) ||
                   "papel.estadoFinal".equals(chavePapel) ||
                   "papel.referendo".equals(chavePapel) ||
                   "papel.referente".equals(chavePapel) ||
                   "papel.referido".equals(chavePapel) ||
                   "papel.diferenca".equals(chavePapel) ||
                   "papel.transformacao".equals(chavePapel) ||
                   chavePapel.startsWith("papel.transformacao") ||
                   "papel.transformacaoFinal".equals(chavePapel) ||
                   "papel.relacaoInicial".equals(chavePapel) ||
                   "papel.relacaoFinal".equals(chavePapel) ||
                   "papel.relacao1".equals(chavePapel) ||
                   "papel.relacao2".equals(chavePapel);
        }

        private int obterIndiceElementoVergnaudPorPapel(String chavePapel) {
            return scaffoldingQuestionamento.obterIndiceElementoPorPapel(
                    chavePapel,
                    tipoSituacaoSelecionada,
                    usaDiagramasEncadeadosTransformacaoComposta(),
                    quantidadePassosTransformacaoComposta
            );
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
            expandirElementoParaCaberItem(alvo, item);
            item.x = alvo.x + (alvo.largura - item.largura) / 2;
            item.y = alvo.y + (alvo.altura - item.altura) / 2;
        }

        /**
         * Valores mais longos que o número inteiro típico (ex.: "15,00" em
         * quantidades monetárias) podem ficar mais largos que o elemento do
         * diagrama onde o item é centralizado. Sem isso, o item ultrapassa a
         * borda do elemento em vez de ficar contido nela.
         */
        private void expandirElementoParaCaberItem(ElementoVergnaud alvo, ItemTextoArrastavel item) {
            if (item.largura <= alvo.largura) {
                return;
            }
            int centroX = alvo.x + alvo.largura / 2;
            alvo.largura = item.largura;
            alvo.x = centroX - alvo.largura / 2;
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
            chavePapel = aplicarFallbackCuradoItemDesconhecido(chavePapel);
            String papel = localizacao.texto(chavePapel);
            return localizacao.formatar("ui.hover.role", papel);
        }

        private String criarMensagemPapelItemArrastavel(ItemTextoArrastavel item) {
            String chavePapel = item.chavePapel;

            if (chavePapel == null || chavePapel.length() == 0) {
                chavePapel = obterChavePapelCanonicoPorValor(item.origemValor);
            } else {
                chavePapel = converterParaPapelCanonico(chavePapel);
            }

            chavePapel = aplicarFallbackCuradoItemDesconhecido(chavePapel);
            String papel = localizacao.texto(chavePapel);
            return localizacao.formatar("ui.hover.role", papel);
        }

        /**
         * A interrogação representa o item desconhecido, mas não constitui um
         * papel semântico próprio. Quando a associação direta resultar em
         * "papel.valor", recupera-se da curadoria o papel efetivamente ocupado
         * pela incógnita (estado inicial, transformação, estado final, parte 1,
         * parte 2, referido, referendo, valor relativo ou todo).
         */
        private String aplicarFallbackCuradoItemDesconhecido(String chavePapel) {
            if (!"papel.valor".equals(chavePapel)) {
                return chavePapel;
            }
            if (resultadoInterpretacao != null && resultadoInterpretacao.getPapeis() != null) {
                java.util.List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();
                for (int i = 0; i < papeis.size(); i++) {
                    PapelElementoInterpretado papel = papeis.get(i);
                    if (papel != null && !papel.isConhecido()
                            && papel.getChavePapel() != null
                            && papel.getChavePapel().trim().length() > 0) {
                        return converterParaPapelCanonico(papel.getChavePapel());
                    }
                }
            }
            return chavePapel;
        }


        private String obterChavePapelExataDoElemento(ElementoTextoMovel elemento) {
            if (elemento != null && elemento.possuiVinculoSemantico()
                    && elemento.chavePapelSemantico != null) {
                return elemento.chavePapelSemantico;
            }
            if (ehInterrogacaoDoTexto(elemento)) {
                return obterChavePapelExataPorValor("?");
            }
            int indice = obterIndiceSimboloArrastavel(elemento);
            if (usaDiagramasComposicaoTransformacaoMedidas()) {
                return obterChavePapelExataComposicaoTransformacaoPorIndice(indice);
            }
            if (usaDiagramasEncadeadosTransformacaoComposta()) {
                if (indice == 0) {
                    return "papel.estadoInicial";
                }
                if (indice > 0 && indice <= quantidadePassosTransformacaoComposta) {
                    return "papel.transformacao" + indice;
                }
                return "papel.estadoFinal";
            }
            return obterChavePapelExataPorIndice(indice);
        }

        private String obterChavePapelExataComposicaoTransformacaoPorIndice(int indice) {
            if (indice == 0) {
                return "papel.parte1";
            }
            if (indice == 1) {
                return "papel.parte2";
            }
            if (indice == 2 || indice == 3) {
                return "papel.transformacao";
            }
            if (indice >= 4) {
                return "papel.estadoFinal";
            }
            return "papel.valor";
        }

        private String obterChavePapelExataPorValor(String valor) {
            if (resultadoInterpretacao == null || valor == null) {
                return "papel.valor";
            }

            java.util.List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();

            if (SimboloDesconhecido.eh(valor)) {
                for (int i = 0; i < papeis.size(); i++) {
                    PapelElementoInterpretado papel = papeis.get(i);
                    if (!papel.isConhecido()) {
                        return papel.getChavePapel();
                    }
                }
            }

            for (int i = 0; i < papeis.size(); i++) {
                PapelElementoInterpretado papel = papeis.get(i);
                if (valor.equals(papel.getElemento())) {
                    return papel.getChavePapel();
                }
            }

            return "papel.valor";
        }

        private String obterChavePapelExataPorIndice(int indice) {
            if (resultadoInterpretacao == null || indice < 0) {
                return "papel.valor";
            }

            java.util.List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();

            if (indice >= 0 && indice < papeis.size()) {
                return papeis.get(indice).getChavePapel();
            }

            return "papel.valor";
        }

        private String obterChavePapelCanonicoDoElemento(ElementoTextoMovel elemento) {
            if (elemento != null && elemento.possuiVinculoSemantico()
                    && elemento.chavePapelSemantico != null) {
                return converterParaPapelCanonico(elemento.chavePapelSemantico);
            }
            if (ehInterrogacaoDoTexto(elemento)) {
                return converterParaPapelCanonico(obterChavePapelExataPorValor("?"));
            }
            int indice = obterIndiceSimboloArrastavel(elemento);
            if (usaDiagramasComposicaoTransformacaoMedidas()) {
                return converterParaPapelCanonico(obterChavePapelExataComposicaoTransformacaoPorIndice(indice));
            }
            if (usaDiagramasEncadeadosTransformacaoComposta()) {
                if (indice == 0) {
                    return "papel.estadoInicial";
                }
                if (indice > 0 && indice <= quantidadePassosTransformacaoComposta) {
                    return "papel.transformacao";
                }
                return "papel.estadoFinal";
            }
            return obterChavePapelCanonicoPorIndice(indice);
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

        private String obterChavePapelCanonicoPorValor(String valor) {
            if (resultadoInterpretacao == null || valor == null) {
                return "papel.valor";
            }

            java.util.List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();

            for (int i = 0; i < papeis.size(); i++) {
                PapelElementoInterpretado papel = papeis.get(i);

                if (valor.equals(papel.getElemento())) {
                    return converterParaPapelCanonico(papel.getChavePapel());
                }
            }

            if (SimboloDesconhecido.eh(valor)) {
                for (int i = 0; i < papeis.size(); i++) {
                    PapelElementoInterpretado papel = papeis.get(i);

                    if (!papel.isConhecido()) {
                        return converterParaPapelCanonico(papel.getChavePapel());
                    }
                }
            }

            return "papel.valor";
        }

        private String obterChavePapelCanonicoPorIndice(int indice) {
            if (resultadoInterpretacao == null || indice < 0) {
                return "papel.valor";
            }

            java.util.List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();

            if (indice >= 0 && indice < papeis.size()) {
                return converterParaPapelCanonico(papeis.get(indice).getChavePapel());
            }

            return "papel.valor";
        }

        private String converterParaPapelCanonico(String chavePapel) {
            if (chavePapel == null) {
                return "papel.valor";
            }

            if (chavePapel.indexOf("parte") >= 0) {
                return "papel.parte";
            }

            if (chavePapel.indexOf("todo") >= 0) {
                return "papel.todo";
            }

            if (chavePapel.indexOf("estadoInicial") >= 0) {
                return "papel.estadoInicial";
            }

            if (chavePapel.indexOf("estadoFinal") >= 0) {
                return "papel.estadoFinal";
            }

            if (chavePapel.indexOf("transformacao") >= 0) {
                return "papel.transformacao";
            }

            if (chavePapel.indexOf("referendo") >= 0 || chavePapel.indexOf("referente") >= 0) {
                return "papel.referendo";
            }

            if (chavePapel.indexOf("referido") >= 0) {
                return "papel.referido";
            }

            if (chavePapel.indexOf("diferenca") >= 0) {
                return "papel.diferenca";
            }

            if (chavePapel.indexOf("relacao") >= 0) {
                return "papel.relacao";
            }

            return "papel.valor";
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
                    quadradinhoVennSelecionado = null;
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
                    if (itemFocado == itemGraficoInteiros) {
                        limparGraficoInteiros();
                    }
                    if (itemFocado == itemIncognitaEstadoFinal) {
                        desabilitarSincronizacaoEstadoFinal();
                    }
                    ItemTextoArrastavel itemRemovido = itemFocado;
                    itensArrastaveis.remove(itemFocado);
                    itemFocado = null;
                    itemSelecionado = null;
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

        private final class ModeloNumericoComparacao {
            int parcela1 = 4;
            int parcela2 = 7;
            final java.util.List<Runnable> ouvintes = new ArrayList<Runnable>();

            int total() { return parcela1 + parcela2; }

            void definir(int a, int b) {
                parcela1 = Math.max(0, a);
                parcela2 = Math.max(0, b);
                notificar();
            }

            void definirPapel(TipoSituacaoAditiva categoria, int indice, int valor) {
                valor = Math.max(0, valor);
                if (categoria == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
                    if (indice == 0) parcela1 = valor;
                    else if (indice == 1) parcela2 = valor;
                    else parcela2 = Math.max(0, valor - parcela1);
                } else if (categoria == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
                    if (indice == 0) parcela1 = valor;
                    else if (indice == 1) parcela2 = valor;
                    else parcela2 = Math.max(0, valor - parcela1);
                } else {
                    // Na comparação, M1 é o referente, R a relação e M2 o referido.
                    if (indice == 0) parcela1 = valor;
                    else if (indice == 1) parcela2 = valor;
                    else parcela2 = Math.max(0, valor - parcela1);
                }
                notificar();
            }

            void adicionarOuvinte(Runnable r) { ouvintes.add(r); }

            void notificar() {
                for (Runnable r : new ArrayList<Runnable>(ouvintes)) r.run();
            }
        }

        private final class PainelComparacaoCategorias extends JPanel {
            final ModeloNumericoComparacao modelo = new ModeloNumericoComparacao();
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
                tituloDestaque.setForeground(new Color(26, 71, 126));
                tituloDestaque.setAlignmentX(Component.CENTER_ALIGNMENT);
                destaque.add(tituloDestaque);

                final JLabel formulaDestaque = new JLabel("4 + 7 = 11", SwingConstants.CENTER);
                formulaDestaque.setFont(new Font("Arial", Font.BOLD, 20));
                formulaDestaque.setAlignmentX(Component.CENTER_ALIGNMENT);
                destaque.add(formulaDestaque);

                destaque.add(Box.createVerticalStrut(4));
                JLabel explicacao = new JLabel("<html><div style='text-align:center; width:720px;'>" + localizacao.texto("ui.compare.explanation") + "</div></html>", SwingConstants.CENTER);
                explicacao.setFont(new Font("Arial", Font.BOLD, 18));
                explicacao.setForeground(new Color(122, 32, 32));
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
                ((JSpinner.DefaultEditor) spinnerA.getEditor()).getTextField().setForeground(new Color(32, 137, 126));
                ((JSpinner.DefaultEditor) spinnerB.getEditor()).getTextField().setForeground(new Color(216, 119, 6));
                valores.add(spinnerA);
                valores.add(new JLabel("+"));
                valores.add(spinnerB);
                valores.add(new JLabel("="));
                final JLabel total = new JLabel(String.valueOf(modelo.total()));
                total.setFont(new Font("Arial", Font.BOLD, 14));
                total.setForeground(new Color(11, 87, 208));
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
                        modelo.definir(((Number) spinnerA.getValue()).intValue(),
                                ((Number) spinnerB.getValue()).intValue());
                        total.setText(String.valueOf(modelo.total()));
                        formulaDestaque.setText(modelo.parcela1 + " + " + modelo.parcela2 + " = " + modelo.total());
                        registrarAcaoComparacao("ALTERAR_VALORES_COMUNS", "a=" + modelo.parcela1 + ";b=" + modelo.parcela2 + ";total=" + modelo.total());
                    }
                };
                spinnerA.addChangeListener(listener);
                spinnerB.addChangeListener(listener);
                modelo.adicionarOuvinte(new Runnable() {
                    public void run() {
                        atualizandoControles = true;
                        try {
                            spinnerA.setValue(modelo.parcela1);
                            spinnerB.setValue(modelo.parcela2);
                            total.setText(String.valueOf(modelo.total()));
                            formulaDestaque.setText(modelo.parcela1 + " + " + modelo.parcela2 + " = " + modelo.total());
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
                JLabel l = new JLabel(textoSituacao(categoria, linha));
                l.setVerticalAlignment(SwingConstants.TOP);
                l.setFont(new Font("Arial", Font.PLAIN, 14));
                l.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.DARK_GRAY));
                l.setOpaque(true); l.setBackground(Color.WHITE);
                l.setPreferredSize(new Dimension(350, 170));
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
                String valorA = "<span style='color:#20897E;font-weight:bold'>" + modelo.parcela1 + "</span>";
                String valorB = "<span style='color:#D87706;font-weight:bold'>" + modelo.parcela2 + "</span>";
                String valorTotal = "<span style='color:#0B57D0;font-weight:bold'>" + modelo.total() + "</span>";
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
                    g2.setFont(new Font("Arial", Font.BOLD, 17));
                    String a = String.valueOf(modelo.parcela1);
                    String op1 = " + ";
                    String b = String.valueOf(modelo.parcela2);
                    String op2 = " = ";
                    String t = String.valueOf(modelo.total());
                    FontMetrics fm = g2.getFontMetrics();
                    int largura = fm.stringWidth(a + op1 + b + op2 + t);
                    int x = Math.max(6, (getWidth() - largura) / 2);
                    int y = Math.max(30, getHeight() / 2);
                    g2.setColor(new Color(32, 137, 126)); g2.drawString(a, x, y); x += fm.stringWidth(a);
                    g2.setColor(Color.DARK_GRAY); g2.drawString(op1, x, y); x += fm.stringWidth(op1);
                    g2.setColor(new Color(216, 119, 6)); g2.drawString(b, x, y); x += fm.stringWidth(b);
                    g2.setColor(Color.DARK_GRAY); g2.drawString(op2, x, y); x += fm.stringWidth(op2);
                    g2.setColor(new Color(11, 87, 208)); g2.drawString(t, x, y);
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

                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.setFont(new Font("Arial", Font.BOLD, 15));
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
                    Rectangle r = new Rectangle(x-22, y-22, 44, 44); alvos.add(r);
                    g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
                    if (circulo) g2.fillOval(r.x,r.y,r.width,r.height); else g2.fillRect(r.x,r.y,r.width,r.height);
                    g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
                    if (circulo) g2.drawOval(r.x,r.y,r.width,r.height); else g2.drawRect(r.x,r.y,r.width,r.height);
                    String t=String.valueOf(valor); FontMetrics fm=g2.getFontMetrics();
                    g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO);
                    g2.drawString(t, x-fm.stringWidth(t)/2, y+fm.getAscent()/2-2);
                }


                void desenharFormaVazia(Graphics2D g2, int x, int y, boolean circulo) {
                    int tamanho = 44;
                    g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
                    if (circulo) g2.fillOval(x - tamanho/2, y - tamanho/2, tamanho, tamanho);
                    else g2.fillRect(x - tamanho/2, y - tamanho/2, tamanho, tamanho);
                    g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
                    if (circulo) g2.drawOval(x - tamanho/2, y - tamanho/2, tamanho, tamanho);
                    else g2.drawRect(x - tamanho/2, y - tamanho/2, tamanho, tamanho);
                }

                void desenharComposicaoVazia(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int x1=w/4, x2=w/4, xt=3*w/4;
                    desenharFormaVazia(g2,x1,h/3,false);
                    desenharFormaVazia(g2,x2,2*h/3,false);
                    desenharFormaVazia(g2,xt,h/2,false);
                    int bx=w/2-12; g2.drawArc(bx,h/3-18,30,h/3+36,270,180);
                }

                void desenharTransformacaoVazia(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int y=2*h/3;
                    desenharFormaVazia(g2,w/5,y,false);
                    desenharFormaVazia(g2,w/2,h/3,true);
                    desenharFormaVazia(g2,4*w/5,y,false);
                    g2.drawLine(w/5+25,y,4*w/5-25,y);
                    g2.drawLine(4*w/5-25,y,4*w/5-35,y-6); g2.drawLine(4*w/5-25,y,4*w/5-35,y+6);
                }

                void desenharComparacaoVazia(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int x=w/2;
                    desenharFormaVazia(g2,x,h/5,false);
                    desenharFormaVazia(g2,x,h*4/5,false);
                    desenharFormaVazia(g2,x+w/4,h/2,true);
                    g2.drawLine(x,h/5+24,x,h*4/5-24);
                    g2.drawLine(x,h*4/5-24,x-6,h*4/5-34); g2.drawLine(x,h*4/5-24,x+6,h*4/5-34);
                }

                void desenharComposicao(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int x1=w/4, x2=w/4, xt=3*w/4;
                    desenharValor(g2,x1,h/3,modelo.parcela1,false);
                    desenharValor(g2,x2,2*h/3,modelo.parcela2,false);
                    desenharValor(g2,xt,h/2,modelo.total(),false);
                    int bx=w/2-12; g2.drawArc(bx,h/3-18,30,h/3+36,270,180);
                }

                void desenharTransformacao(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int y=2*h/3;
                    desenharValor(g2,w/5,y,modelo.parcela1,false);
                    desenharValor(g2,w/2,h/3,modelo.parcela2,true);
                    desenharValor(g2,4*w/5,y,modelo.total(),false);
                    g2.drawLine(w/5+25,y,4*w/5-25,y);
                    g2.drawLine(4*w/5-25,y,4*w/5-35,y-6); g2.drawLine(4*w/5-25,y,4*w/5-35,y+6);
                }

                void desenharComparacao(Graphics2D g2) {
                    int w=getWidth(), h=getHeight(); int x=w/2;
                    desenharValor(g2,x,h/5,modelo.total(),false);
                    desenharValor(g2,x,h*4/5,modelo.parcela1,false);
                    desenharValor(g2,x+w/4,h/2,modelo.parcela2,true);
                    g2.drawLine(x,h/5+24,x,h*4/5-24);
                    g2.drawLine(x,h*4/5-24,x-6,h*4/5-34); g2.drawLine(x,h*4/5-24,x+6,h*4/5-34);
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
                        String entrada = solicitarValorInteiro(
                                i == 0 ? modelo.parcela1 : i == 1 ? modelo.parcela2 : modelo.total());
                        if (entrada == null) return;
                        try {
                            int valor=Integer.parseInt(entrada.trim());
                            modelo.definirPapel(categoria,i,valor);
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
            }
        }

    }
}
