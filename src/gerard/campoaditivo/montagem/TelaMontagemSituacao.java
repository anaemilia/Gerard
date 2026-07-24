package gerard.campoaditivo.montagem;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.i18n.ServicoLocalizacao;
import gerard.idioma.IdiomaInterface;
import gerard.pesquisador.log.LoggerInteracaoGerard;
import gerard.ui.UITemaGerard;
import gerard.ui.conclusao.TipConclusaoModelagem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

/**
 * Aba em que o usuário constrói uma situação-problema a partir de um
 * diagrama preenchido e de blocos textuais semanticamente próximos.
 */
public final class TelaMontagemSituacao extends JPanel {
    // Paleta única em gerard.ui.UITemaGerard — mudar o tema é editar só aquele
    // arquivo. Os nomes locais são mantidos para não alterar cada uso abaixo.
    private static final Color FUNDO = UITemaGerard.COR_FUNDO_CONTEUDO;
    private static final Color SUPERFICIE = UITemaGerard.COR_SUPERFICIE;
    private static final Color TEXTO = UITemaGerard.COR_TEXTO;
    private static final Color TEXTO_SECUNDARIO = UITemaGerard.COR_TEXTO_SECUNDARIO;
    private static final Color BORDA = UITemaGerard.COR_BORDA;
    private static final Color PRIMARIA = UITemaGerard.COR_PRIMARIA;
    private static final Color SUCESSO = UITemaGerard.COR_SUCESSO;
    private static final Color AVISO = new Color(161, 98, 7);
    private static final Color ERRO_SUAVE = new Color(191, 109, 122);
    private static final Color FUNDO_CORRETO = new Color(239, 246, 255);
    private static final Color FUNDO_INCORRETO = new Color(255, 245, 246);

    private final RepositorioSituacoesAditivas repositorio;
    private final GeradorBlocosMontagem gerador = new GeradorBlocosMontagem();
    private final Random random = new Random();
    private final ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
    private final LoggerInteracaoGerard logger = LoggerInteracaoGerard.getInstancia();
    private final AvaliadorMontagemPassoAPasso avaliadorPassoAPasso = new AvaliadorMontagemPassoAPasso();
    private final List<StatusVerificacaoBlocoMontagem> estadosMontagem = new ArrayList<StatusVerificacaoBlocoMontagem>();

    private final JComboBox<IdiomaInterface> comboIdioma;
    private final JButton botaoSortear;
    private final JButton botaoAdicionar;
    private final JButton botaoRemover;
    private final JButton botaoSubir;
    private final JButton botaoDescer;
    private final JButton botaoRecomecar;
    private final JButton botaoValidar;
    private final JLabel rotuloTitulo;
    private final JLabel rotuloInstrucao;
    private final JLabel rotuloSituacao;
    private final JLabel rotuloFeedback;
    private final TipConclusaoModelagem tipParabens = new TipConclusaoModelagem();
    private final JLabel tituloDisponiveis;
    private final JLabel tituloMontagem;
    private final JLabel tituloDiagrama;
    private final PainelDiagramaPreenchido painelDiagrama;
    private final DefaultListModel<BlocoTextoMontagem> modeloDisponiveis = new DefaultListModel<BlocoTextoMontagem>();
    private final DefaultListModel<BlocoTextoMontagem> modeloMontagem = new DefaultListModel<BlocoTextoMontagem>();
    private final JList<BlocoTextoMontagem> listaDisponiveis = new JList<BlocoTextoMontagem>(modeloDisponiveis);
    private final JList<BlocoTextoMontagem> listaMontagem = new JList<BlocoTextoMontagem>(modeloMontagem);

    private ConjuntoBlocosMontagem atividadeAtual;
    private int quantidadeMovimentos;
    private int quantidadeValidacoes;
    private boolean atualizandoIdioma;
    private boolean abaAtiva;
    private boolean recarregarAoAtivar = true;
    private String idUltimaSituacao = "";
    private TipoSituacaoAditiva tipoUltimaSituacao;
    private LoggerInteracaoGerard.ContextoInteracao contextoAbaAnterior;
    private LoggerInteracaoGerard.ContextoInteracao contextoMontagem;

    public TelaMontagemSituacao(RepositorioSituacoesAditivas repositorio) {
        this.repositorio = repositorio;
        setLayout(new BorderLayout(12, 12));
        setBackground(FUNDO);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Cabeçalho aumentado para acompanhar a escala do diagrama nesta aba
        // (caixas e números do "Diagrama preenchido" foram ampliados — ver
        // RenderizadorDiagramaAditivoBase e PainelDiagramaPreenchido): sem
        // este ajuste, o texto de instrução ficava pequeno demais perto do
        // diagrama grande logo abaixo.
        rotuloTitulo = new JLabel();
        rotuloTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        rotuloTitulo.setForeground(TEXTO);
        rotuloInstrucao = new JLabel();
        rotuloInstrucao.setFont(new Font("Arial", Font.PLAIN, 16));
        rotuloInstrucao.setForeground(TEXTO_SECUNDARIO);
        rotuloSituacao = new JLabel(" ");
        rotuloSituacao.setFont(new Font("Arial", Font.BOLD, 15));
        rotuloSituacao.setForeground(TEXTO_SECUNDARIO);

        comboIdioma = new JComboBox<IdiomaInterface>(new DefaultComboBoxModel<IdiomaInterface>(new IdiomaInterface[] {
            IdiomaInterface.PORTUGUES, IdiomaInterface.INGLES, IdiomaInterface.FRANCES
        }));
        comboIdioma.setFont(UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
        comboIdioma.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof IdiomaInterface) {
                    IdiomaInterface idioma = (IdiomaInterface) value;
                    label.setText(idioma.getSigla() + " - " + idioma.getNome());
                }
                return label;
            }
        });
        botaoSortear = criarBotaoPrimario();

        JPanel cabecalho = new JPanel(new BorderLayout(12, 6));
        cabecalho.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setLayout(new javax.swing.BoxLayout(textos, javax.swing.BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(rotuloTitulo);
        textos.add(javax.swing.Box.createVerticalStrut(3));
        textos.add(rotuloInstrucao);
        textos.add(javax.swing.Box.createVerticalStrut(3));
        textos.add(rotuloSituacao);
        cabecalho.add(textos, BorderLayout.CENTER);

        JPanel comandosCabecalho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        comandosCabecalho.setOpaque(false);
        comandosCabecalho.add(comboIdioma);
        comandosCabecalho.add(botaoSortear);
        cabecalho.add(comandosCabecalho, BorderLayout.EAST);
        add(cabecalho, BorderLayout.NORTH);

        painelDiagrama = new PainelDiagramaPreenchido();
        JPanel cardDiagrama = criarCard(new BorderLayout(8, 8));
        tituloDiagrama = new JLabel();
        // Mesmo tamanho de criarTituloLista() — os três cartões (Diagrama
        // preenchido, Blocos disponíveis, Situação-problema em construção)
        // são títulos do mesmo nível hierárquico.
        tituloDiagrama.setFont(new Font("Arial", Font.BOLD, 17));
        tituloDiagrama.setForeground(TEXTO);
        cardDiagrama.add(tituloDiagrama, BorderLayout.NORTH);
        cardDiagrama.add(painelDiagrama, BorderLayout.CENTER);

        // Sobreposta ao próprio painel do diagrama (não empilhada por baixo
        // dele), na mesma posição em que "Próxima tarefa" aparece sob o
        // diagrama de Vergnaud: colada ao contorno real desenhado, não ao
        // fim da área alocada pelo layout.
        rotuloFeedback = new JLabel(" ");
        rotuloFeedback.setFont(new Font("Arial", Font.BOLD, 15));
        rotuloFeedback.setForeground(TEXTO_SECUNDARIO);
        painelDiagrama.anexarRotuloFeedback(rotuloFeedback);

        // Substitui a mensagem estática pelo convite interativo assim que a
        // construção é validada como compatível: Sim aciona o mesmo botão
        // "Novo diagrama", que sorteia outra situação (novo diagrama e novos
        // blocos textuais, potencialmente de uma construção equivalente).
        tipParabens.atualizarTextos(
                localizacao.texto("montagem.feedback.correct"),
                localizacao.texto("ui.completion.yes"),
                localizacao.texto("ui.completion.no"));
        tipParabens.definirOuvinte(new TipConclusaoModelagem.OuvinteEscolha() {
            public void aoEscolherSim() {
                if (botaoSortear != null && botaoSortear.isEnabled()) {
                    botaoSortear.doClick();
                }
            }
            public void aoEscolherNao() {
                tipParabens.ocultar();
            }
        });
        painelDiagrama.anexarTipParabens(tipParabens);

        tituloDisponiveis = criarTituloLista();
        tituloMontagem = criarTituloLista();
        configurarLista(listaDisponiveis);
        configurarLista(listaMontagem);

        JPanel cardDisponiveis = criarCard(new BorderLayout(6, 6));
        cardDisponiveis.add(tituloDisponiveis, BorderLayout.NORTH);
        cardDisponiveis.add(new JScrollPane(listaDisponiveis), BorderLayout.CENTER);

        JPanel cardMontagem = criarCard(new BorderLayout(6, 6));
        cardMontagem.add(tituloMontagem, BorderLayout.NORTH);
        cardMontagem.add(new JScrollPane(listaMontagem), BorderLayout.CENTER);

        botaoAdicionar = criarBotaoSecundario();
        botaoRemover = criarBotaoSecundario();
        botaoSubir = criarBotaoSecundario();
        botaoDescer = criarBotaoSecundario();

        JPanel controlesListas = new JPanel(new GridBagLayout());
        controlesListas.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridy = 0; controlesListas.add(botaoAdicionar, gbc);
        gbc.gridy = 1; controlesListas.add(botaoRemover, gbc);
        gbc.gridy = 2; controlesListas.add(botaoSubir, gbc);
        gbc.gridy = 3; controlesListas.add(botaoDescer, gbc);

        // GridBagLayout em vez de BorderLayout: com WEST/CENTER/EAST, o
        // BorderLayout dava todo o espaço sobrando ao CENTER (os botões),
        // deixando as duas listas com largura fixa e uma lacuna enorme entre
        // elas. Aqui as duas listas dividem proporcionalmente (weightx=1.0)
        // o espaço disponível, e a coluna de botões fica só com a largura
        // que o conteúdo pede (weightx=0.0).
        JPanel painelListas = new JPanel(new GridBagLayout());
        painelListas.setOpaque(false);
        GridBagConstraints gbcListas = new GridBagConstraints();
        gbcListas.gridy = 0;
        gbcListas.fill = GridBagConstraints.BOTH;
        gbcListas.weighty = 1.0;

        gbcListas.gridx = 0;
        gbcListas.weightx = 1.0;
        gbcListas.insets = new Insets(0, 0, 0, 8);
        painelListas.add(cardDisponiveis, gbcListas);

        gbcListas.gridx = 1;
        gbcListas.weightx = 0.0;
        gbcListas.fill = GridBagConstraints.VERTICAL;
        gbcListas.insets = new Insets(0, 0, 0, 0);
        painelListas.add(controlesListas, gbcListas);

        gbcListas.gridx = 2;
        gbcListas.weightx = 1.0;
        gbcListas.fill = GridBagConstraints.BOTH;
        gbcListas.insets = new Insets(0, 8, 0, 0);
        painelListas.add(cardMontagem, gbcListas);

        cardDisponiveis.setPreferredSize(new Dimension(300, 420));
        cardMontagem.setPreferredSize(new Dimension(300, 420));

        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cardDiagrama, painelListas);
        divisor.setResizeWeight(0.43);
        divisor.setBorder(null);
        divisor.setOpaque(false);
        divisor.setContinuousLayout(true);
        add(divisor, BorderLayout.CENTER);

        botaoRecomecar = criarBotaoSecundario();
        botaoValidar = criarBotaoPrimario();

        JPanel rodape = new JPanel(new BorderLayout(8, 0));
        rodape.setOpaque(false);
        JPanel botoesRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoesRodape.setOpaque(false);
        botoesRodape.add(botaoRecomecar);
        botoesRodape.add(botaoValidar);
        rodape.add(botoesRodape, BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);

        instalarTransferencia();
        instalarAcoes();
        atualizarTextosInterface();
        prepararEstadoInicial();
    }

    /** Ativa o contexto próprio da tarefa ao entrar nesta aba. */
    public void ativarAba() {
        if (abaAtiva) {
            return;
        }
        contextoAbaAnterior = logger.capturarContextoAtual();
        abaAtiva = true;
        atualizarTextosInterface();

        if (recarregarAoAtivar || atividadeAtual == null || contextoMontagem == null) {
            carregarNovaAtividade();
        } else {
            logger.restaurarContexto(contextoMontagem);
        }
    }

    /** Restaura a tentativa da aba anterior sem perder a tentativa de construção. */
    public void desativarAba() {
        if (!abaAtiva) {
            return;
        }
        contextoMontagem = logger.capturarContextoAtual();
        logger.restaurarContexto(contextoAbaAnterior);
        contextoAbaAnterior = null;
        abaAtiva = false;
    }

    public void recarregarSituacoesCuradas() {
        recarregarAoAtivar = true;
        contextoMontagem = null;
        if (abaAtiva) {
            carregarNovaAtividade();
        }
    }

    public void atualizarIdiomaInterface() {
        atualizarTextosInterface();
    }

    private void prepararEstadoInicial() {
        modeloDisponiveis.clear();
        modeloMontagem.clear();
        rotuloFeedback.setText(" ");
        painelDiagrama.definirSituacao(null, null);
        rotuloSituacao.setText(localizacao.texto("montagem.awaiting"));
        estadosMontagem.clear();
        listaMontagem.repaint();
        atualizarEstadoBotoes(false);
    }

    private void instalarAcoes() {
        comboIdioma.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!atualizandoIdioma) carregarNovaAtividade();
            }
        });
        botaoSortear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { carregarNovaAtividade(); }
        });
        botaoAdicionar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { moverSelecionado(listaDisponiveis, modeloDisponiveis, modeloMontagem, "BOTAO_ADICIONAR"); }
        });
        botaoRemover.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { moverSelecionado(listaMontagem, modeloMontagem, modeloDisponiveis, "BOTAO_REMOVER"); }
        });
        botaoSubir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { moverNaMontagem(-1); }
        });
        botaoDescer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { moverNaMontagem(1); }
        });
        botaoRecomecar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { recomecar(); }
        });
        botaoValidar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { validarMontagem(); }
        });

        listaDisponiveis.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) moverSelecionado(listaDisponiveis, modeloDisponiveis, modeloMontagem, "DUPLO_CLIQUE_ADICIONAR");
            }
        });
        listaMontagem.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) moverSelecionado(listaMontagem, modeloMontagem, modeloDisponiveis, "DUPLO_CLIQUE_REMOVER");
            }
        });
    }

    private void instalarTransferencia() {
        TransferHandler handler = new BlocoTransferHandler();
        if (!GraphicsEnvironment.isHeadless()) {
            listaDisponiveis.setDragEnabled(true);
            listaMontagem.setDragEnabled(true);
        }
        listaDisponiveis.setDropMode(javax.swing.DropMode.INSERT);
        listaMontagem.setDropMode(javax.swing.DropMode.INSERT);
        listaDisponiveis.setTransferHandler(handler);
        listaMontagem.setTransferHandler(handler);
    }

    private void configurarLista(JList<BlocoTextoMontagem> lista) {
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFixedCellHeight(-1);
        lista.setBackground(SUPERFICIE);
        lista.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        lista.setCellRenderer(new RenderizadorBloco());
    }

    private void carregarNovaAtividade() {
        recarregarAoAtivar = false;
        modeloDisponiveis.clear();
        modeloMontagem.clear();
        atividadeAtual = null;
        quantidadeMovimentos = 0;
        quantidadeValidacoes = 0;
        rotuloFeedback.setText(" ");
        estadosMontagem.clear();
        listaMontagem.repaint();
        painelDiagrama.definirSituacao(null, null);

        IdiomaInterface idioma = (IdiomaInterface) comboIdioma.getSelectedItem();
        List<SituacaoProblemaAditiva> candidatas = candidatas(idioma);
        if (candidatas.isEmpty()) {
            rotuloSituacao.setText(localizacao.texto("montagem.no.curated"));
            rotuloFeedback.setText(localizacao.texto("montagem.no.curated.help"));
            rotuloFeedback.setForeground(AVISO);
            painelDiagrama.repaint();
            atualizarEstadoBotoes(false);
            contextoMontagem = null;
            return;
        }

        SituacaoProblemaAditiva escolhida = escolherProximaSituacao(candidatas);
        idUltimaSituacao = escolhida.getId();
        tipoUltimaSituacao = escolhida.getTipo();
        atividadeAtual = gerador.gerar(escolhida);
        for (BlocoTextoMontagem bloco : atividadeAtual.getBlocosDisponiveis()) {
            modeloDisponiveis.addElement(bloco);
        }
        painelDiagrama.definirSituacao(escolhida, atividadeAtual.getValoresDiagrama());
        rotuloSituacao.setText(localizacao.formatar("montagem.exercise.identification",
                idioma == null ? "" : idioma.getSigla(), escolhida.getContexto()));
        atualizarEstadoBotoes(true);
        atualizarVerificacaoPassoAPasso();

        if (abaAtiva) {
            logger.novoProblema(escolhida.getTipo().name(), escolhida.getEnunciado(),
                    escolhida.getId(), escolhida.getSituacaoGrupoId(), escolhida.getCodigoIdioma());
            logger.registrarComputador(
                    "TAREFA_MATEMATICA_CONSTRUCAO_APRESENTAR",
                    "Diagrama preenchido e blocos textuais",
                    "Aba Construir situação-problema",
                    "Apresentar atividade de construção sem expor a solução textual",
                    "Blocos não compatíveis preservam contexto, personagens, valores e forma linguística aproximada; a variação principal está na relação semântica.",
                    "CONSTRUCAO_SITUACAO",
                    "blocos_corretos=" + atividadeAtual.getIdsCorretosOrdenados().size()
                            + ";blocos_disponiveis=" + atividadeAtual.getBlocosDisponiveis().size()
                            + ";criterio_distancia_semantica=controlada");
            contextoMontagem = logger.capturarContextoAtual();
        }
    }

    private List<SituacaoProblemaAditiva> candidatas(IdiomaInterface idioma) {
        Map<String, SituacaoProblemaAditiva> unicas = new LinkedHashMap<String, SituacaoProblemaAditiva>();
        for (SituacaoProblemaAditiva situacao : repositorio.listarValidadas()) {
            if (situacao.getIdioma() == idioma && gerador.podeGerar(situacao)) {
                unicas.put(chaveSituacao(situacao), situacao);
            }
        }

        // O catálogo de referência garante pelo menos três estruturas distintas
        // mesmo quando o corpus local ainda possui somente uma situação validada.
        for (SituacaoProblemaAditiva situacao : CatalogoAtividadesMontagemPadrao.listar(idioma)) {
            if (gerador.podeGerar(situacao)) {
                String chave = chaveSituacao(situacao);
                if (!unicas.containsKey(chave)) {
                    unicas.put(chave, situacao);
                }
            }
        }
        return new ArrayList<SituacaoProblemaAditiva>(unicas.values());
    }

    /**
     * Evita que o botão Novo diagrama apresente novamente a mesma atividade.
     * Quando possível, muda também a categoria, tornando a alteração do
     * diagrama imediatamente perceptível sem criar um padrão mecânico fixo.
     */
    private SituacaoProblemaAditiva escolherProximaSituacao(List<SituacaoProblemaAditiva> candidatas) {
        if (candidatas == null || candidatas.isEmpty()) {
            throw new IllegalArgumentException("É necessária ao menos uma situação candidata.");
        }
        if (candidatas.size() == 1) {
            return candidatas.get(0);
        }

        List<SituacaoProblemaAditiva> outraCategoria = new ArrayList<SituacaoProblemaAditiva>();
        List<SituacaoProblemaAditiva> outraSituacao = new ArrayList<SituacaoProblemaAditiva>();
        for (SituacaoProblemaAditiva candidata : candidatas) {
            if (!mesmoId(candidata, idUltimaSituacao)) {
                outraSituacao.add(candidata);
                if (tipoUltimaSituacao == null || candidata.getTipo() != tipoUltimaSituacao) {
                    outraCategoria.add(candidata);
                }
            }
        }
        List<SituacaoProblemaAditiva> opcoes = !outraCategoria.isEmpty() ? outraCategoria : outraSituacao;
        if (opcoes.isEmpty()) {
            opcoes = candidatas;
        }
        return opcoes.get(random.nextInt(opcoes.size()));
    }

    private boolean mesmoId(SituacaoProblemaAditiva situacao, String id) {
        return situacao != null && situacao.getId() != null && situacao.getId().equals(id == null ? "" : id);
    }

    private String chaveSituacao(SituacaoProblemaAditiva situacao) {
        if (situacao == null) return "";
        String texto = situacao.getEnunciado() == null ? "" : situacao.getEnunciado().trim().toLowerCase();
        texto = texto.replaceAll("\\s+", " ");
        if (texto.length() > 0) {
            return "texto:" + situacao.getTipo() + ":" + texto;
        }
        String id = situacao.getId() == null ? "" : situacao.getId().trim();
        return "id:" + id;
    }

    private void moverSelecionado(JList<BlocoTextoMontagem> origemLista,
                                   DefaultListModel<BlocoTextoMontagem> origem,
                                   DefaultListModel<BlocoTextoMontagem> destino,
                                   String origemEvento) {
        int indice = origemLista.getSelectedIndex();
        if (indice < 0) return;
        BlocoTextoMontagem bloco = origem.getElementAt(indice);
        origem.remove(indice);
        destino.addElement(bloco);
        quantidadeMovimentos++;
        registrarMovimento(bloco, origemEvento, destino == modeloMontagem ? "construcao" : "disponiveis");
        limparFeedbackAposEdicao();
        atualizarVerificacaoPassoAPasso();
    }

    private void moverNaMontagem(int delta) {
        int indice = listaMontagem.getSelectedIndex();
        int destino = indice + delta;
        if (indice < 0 || destino < 0 || destino >= modeloMontagem.size()) return;
        BlocoTextoMontagem bloco = modeloMontagem.getElementAt(indice);
        modeloMontagem.remove(indice);
        modeloMontagem.add(destino, bloco);
        listaMontagem.setSelectedIndex(destino);
        quantidadeMovimentos++;
        registrarMovimento(bloco, delta < 0 ? "REORDENAR_SUBIR" : "REORDENAR_DESCER", "construcao");
        limparFeedbackAposEdicao();
        atualizarVerificacaoPassoAPasso();
    }

    private void recomecar() {
        if (atividadeAtual == null) return;
        List<BlocoTextoMontagem> blocos = new ArrayList<BlocoTextoMontagem>();
        for (int i = 0; i < modeloDisponiveis.size(); i++) blocos.add(modeloDisponiveis.getElementAt(i));
        for (int i = 0; i < modeloMontagem.size(); i++) blocos.add(modeloMontagem.getElementAt(i));
        Collections.shuffle(blocos, random);
        modeloDisponiveis.clear();
        modeloMontagem.clear();
        for (BlocoTextoMontagem bloco : blocos) modeloDisponiveis.addElement(bloco);
        quantidadeMovimentos = 0;
        rotuloFeedback.setText(localizacao.texto("montagem.feedback.restarted"));
        rotuloFeedback.setForeground(TEXTO_SECUNDARIO);
        painelDiagrama.definirSucesso(false);
        atualizarVerificacaoPassoAPasso();
        logger.registrarUsuario(
                "TAREFA_MATEMATICA_CONSTRUCAO_RECOMECAR", "-",
                "Organização dos blocos", "Botão Recomeçar",
                "Retornar todos os blocos para a área de origem", "construcao",
                "Reinício da construção sem alterar a situação ou o diagrama.",
                "CONSTRUCAO_RECOMECAR", "blocos_retornados=" + blocos.size());
    }

    private void validarMontagem() {
        if (atividadeAtual == null) return;
        quantidadeValidacoes++;
        List<String> ids = new ArrayList<String>();
        boolean possuiNaoCompativel = false;
        for (int i = 0; i < modeloMontagem.size(); i++) {
            BlocoTextoMontagem bloco = modeloMontagem.getElementAt(i);
            ids.add(bloco.getId());
            if (!bloco.isCorreto()) possuiNaoCompativel = true;
        }
        boolean correta = ids.equals(atividadeAtual.getIdsCorretosOrdenados());
        if (correta) {
            rotuloFeedback.setText(localizacao.texto("montagem.feedback.correct"));
            rotuloFeedback.setForeground(SUCESSO);
        } else {
            rotuloFeedback.setText(localizacao.texto("montagem.feedback.review"));
            rotuloFeedback.setForeground(AVISO);
        }
        painelDiagrama.definirSucesso(correta);

        logger.registrarUsuario(
                "TAREFA_MATEMATICA_CONSTRUCAO_VALIDAR", correta ? "C" : "E",
                "Sequência de blocos", "Botão Validar",
                "Comparar a construção com a estrutura textual curada", "construcao",
                "A construção deve selecionar somente os blocos compatíveis com o diagrama e preservar a ordem do enunciado.",
                "CONSTRUCAO_VALIDACAO",
                "resultado=" + (correta ? "correto" : "revisar")
                        + ";validacao=" + quantidadeValidacoes
                        + ";movimentos=" + quantidadeMovimentos
                        + ";blocos_montados=" + modeloMontagem.size()
                        + ";incluiu_bloco_nao_compativel=" + possuiNaoCompativel
                        + ";ordem_ids=" + juntar(ids));
    }

    private void registrarMovimento(BlocoTextoMontagem bloco, String origemEvento, String destino) {
        logger.registrarAcaoGranularUsuario(
                "SELECIONAR",
                "TAREFA_MATEMATICA_CONSTRUCAO_BLOCO",
                "Organização por blocos",
                "Bloco textual",
                "Selecionar e posicionar trecho na construção",
                bloco == null ? "" : bloco.getId(),
                origemEvento,
                "destino=" + destino
                        + ";papel_semantico=" + (bloco == null ? "" : bloco.getPapelSemantico())
                        + ";categoria_semantica=" + (bloco == null || bloco.getCategoriaSemantica() == null ? "" : bloco.getCategoriaSemantica().name())
                        + ";bloco_compativel=" + (bloco != null && bloco.isCorreto()),
                "Bloco movido para " + destino);
    }

    private void limparFeedbackAposEdicao() {
        rotuloFeedback.setText(" ");
        rotuloFeedback.setForeground(TEXTO_SECUNDARIO);
        painelDiagrama.definirSucesso(false);
        atualizarVerificacaoPassoAPasso();
    }

    private void atualizarVerificacaoPassoAPasso() {
        estadosMontagem.clear();
        if (atividadeAtual == null) {
            listaMontagem.repaint();
            return;
        }
        List<BlocoTextoMontagem> montados = new ArrayList<BlocoTextoMontagem>();
        for (int i = 0; i < modeloMontagem.size(); i++) {
            montados.add(modeloMontagem.get(i));
        }
        estadosMontagem.addAll(avaliadorPassoAPasso.avaliar(montados, atividadeAtual.getIdsCorretosOrdenados()));
        listaMontagem.repaint();
    }

    private StatusVerificacaoBlocoMontagem statusMontagemNaPosicao(int indice) {
        if (indice < 0 || indice >= estadosMontagem.size()) {
            return StatusVerificacaoBlocoMontagem.NEUTRO;
        }
        return estadosMontagem.get(indice);
    }

    private String juntar(List<String> ids) {
        StringBuilder sb = new StringBuilder();
        for (String id : ids) {
            if (sb.length() > 0) sb.append(',');
            sb.append(id);
        }
        return sb.toString();
    }

    private void atualizarEstadoBotoes(boolean habilitado) {
        botaoAdicionar.setEnabled(habilitado);
        botaoRemover.setEnabled(habilitado);
        botaoSubir.setEnabled(habilitado);
        botaoDescer.setEnabled(habilitado);
        botaoRecomecar.setEnabled(habilitado);
        botaoValidar.setEnabled(habilitado);
    }

    private void atualizarTextosInterface() {
        rotuloTitulo.setText(localizacao.texto("montagem.title"));
        rotuloInstrucao.setText(localizacao.texto("montagem.instruction"));
        tituloDiagrama.setText(localizacao.texto("montagem.diagram.title"));
        botaoSortear.setText(localizacao.texto("montagem.button.new"));
        botaoAdicionar.setText(localizacao.texto("montagem.button.add"));
        botaoRemover.setText(localizacao.texto("montagem.button.remove"));
        botaoSubir.setText(localizacao.texto("montagem.button.up"));
        botaoDescer.setText(localizacao.texto("montagem.button.down"));
        botaoRecomecar.setText(localizacao.texto("montagem.button.restart"));
        botaoValidar.setText(localizacao.texto("montagem.button.validate"));
        tituloDisponiveis.setText(localizacao.texto("montagem.available"));
        tituloMontagem.setText(localizacao.texto("montagem.assembly"));
        tipParabens.atualizarTextos(
                localizacao.texto("montagem.feedback.correct"),
                localizacao.texto("ui.completion.yes"),
                localizacao.texto("ui.completion.no"));
        painelDiagrama.repaint();
    }

    private JPanel criarCard(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SUPERFICIE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return panel;
    }

    private JLabel criarTituloLista() {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.BOLD, 17));
        label.setForeground(TEXTO);
        return label;
    }

    private JButton criarBotaoPrimario() {
        JButton botao = new JButton();
        botao.setFont(UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
        botao.setForeground(Color.WHITE);
        botao.setBackground(PRIMARIA);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 13, 8, 13));
        return botao;
    }

    private JButton criarBotaoSecundario() {
        JButton botao = new JButton();
        botao.setFont(UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
        botao.setForeground(TEXTO);
        botao.setBackground(UITemaGerard.COR_FUNDO_CONTEUDO);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITemaGerard.COR_BORDA),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        return botao;
    }

    private final class RenderizadorBloco extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String texto = value instanceof BlocoTextoMontagem ? ((BlocoTextoMontagem) value).getTexto() : String.valueOf(value);
            label.setText("<html><div style='width:245px;padding:6px 4px;'>" + escaparHtml(texto) + "</div></html>");
            label.setFont(new Font("Arial", Font.PLAIN, 15));
            label.setVerticalAlignment(SwingConstants.TOP);
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA));
            if (list == listaMontagem) {
                StatusVerificacaoBlocoMontagem status = statusMontagemNaPosicao(index);
                if (status == StatusVerificacaoBlocoMontagem.CORRETO) {
                    label.setForeground(SUCESSO);
                    label.setBackground(FUNDO_CORRETO);
                } else if (status == StatusVerificacaoBlocoMontagem.INCORRETO) {
                    label.setForeground(ERRO_SUAVE);
                    label.setBackground(FUNDO_INCORRETO);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(TEXTO);
                }
                if (isSelected) {
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDA),
                            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
                }
            } else if (!isSelected) {
                label.setBackground(Color.WHITE);
                label.setForeground(TEXTO);
            }
            return label;
        }
    }

    private String escaparHtml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private final class BlocoTransferHandler extends TransferHandler {
        private final DataFlavor sabor;
        private JList<?> listaOrigem;
        private BlocoTextoMontagem bloco;

        BlocoTransferHandler() {
            try {
                sabor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + BlocoTextoMontagem.class.getName());
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        protected Transferable createTransferable(javax.swing.JComponent c) {
            if (!(c instanceof JList)) return null;
            listaOrigem = (JList<?>) c;
            Object selecionado = listaOrigem.getSelectedValue();
            if (!(selecionado instanceof BlocoTextoMontagem)) return null;
            bloco = (BlocoTextoMontagem) selecionado;
            final BlocoTextoMontagem transferido = bloco;
            return new Transferable() {
                public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[] { sabor }; }
                public boolean isDataFlavorSupported(DataFlavor flavor) { return sabor.equals(flavor); }
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                    if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
                    return transferido;
                }
            };
        }

        @Override
        public int getSourceActions(javax.swing.JComponent c) { return MOVE; }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDrop() && support.isDataFlavorSupported(sabor)
                    && support.getComponent() instanceof JList
                    && support.getComponent() != listaOrigem;
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) return false;
            try {
                BlocoTextoMontagem recebido = (BlocoTextoMontagem) support.getTransferable().getTransferData(sabor);
                JList<?> alvo = (JList<?>) support.getComponent();
                @SuppressWarnings("unchecked")
                DefaultListModel<BlocoTextoMontagem> modelo = (DefaultListModel<BlocoTextoMontagem>) alvo.getModel();
                JList.DropLocation local = (JList.DropLocation) support.getDropLocation();
                int indice = Math.max(0, Math.min(local.getIndex(), modelo.size()));
                modelo.add(indice, recebido);
                quantidadeMovimentos++;
                registrarMovimento(recebido, "ARRASTE_BLOCO", alvo == listaMontagem ? "montagem" : "disponiveis");
                limparFeedbackAposEdicao();
                atualizarVerificacaoPassoAPasso();
                return true;
            } catch (UnsupportedFlavorException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }

        @Override
        protected void exportDone(javax.swing.JComponent source, Transferable data, int action) {
            if (action == MOVE && bloco != null && source instanceof JList) {
                @SuppressWarnings("unchecked")
                DefaultListModel<BlocoTextoMontagem> modelo = (DefaultListModel<BlocoTextoMontagem>) ((JList<?>) source).getModel();
                modelo.removeElement(bloco);
            }
            atualizarVerificacaoPassoAPasso();
            bloco = null;
            listaOrigem = null;
        }
    }
}
