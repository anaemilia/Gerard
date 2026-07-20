package gerard.campoaditivo.curadoria;

import gerard.interpretacao.simbolo.SimboloDesconhecido;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.curadoria.sinal.ControladorSinaisCuradoria;
import gerard.campoaditivo.curadoria.sinal.ModoPersistenciaSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.PainelValorComSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.PapelSinalCuradoria;
import gerard.campoaditivo.servico.ClassificadorTipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.campoaditivo.servico.RegistroErrosCuradoria;
import gerard.campoaditivo.servico.ValidadorVinculosTraducoes;
import gerard.i18n.ServicoLocalizacao;
import gerard.idioma.IdiomaInterface;
import gerard.idioma.IdiomaSituacao;
import gerard.idioma.UnicodeTexto;
import gerard.idioma.CadastroIdiomasSituacao;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Insets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.Icon;
import javax.swing.DefaultCellEditor;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class TelaCuradoriaSituacoes extends JPanel {
    private final ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
    private final RepositorioSituacoesAditivas repositorio;
    private final Runnable aoSalvarCuradoria;
    private final ModeloTabelaSituacoes modelo;
    private final JTable tabela;
    private final JLabel status;
    private final ClassificadorTipoSituacaoAditiva classificador = new ClassificadorTipoSituacaoAditiva();
    private final CadastroIdiomasSituacao cadastroIdiomas = new CadastroIdiomasSituacao();

    public TelaCuradoriaSituacoes(RepositorioSituacoesAditivas repositorio, Runnable aoSalvarCuradoria) {
        this.repositorio = repositorio == null ? new RepositorioSituacoesAditivas() : repositorio;
        this.aoSalvarCuradoria = aoSalvarCuradoria;
        this.modelo = new ModeloTabelaSituacoes(this.repositorio.listarTodas());
        this.tabela = new JTable(modelo);
        this.status = new JLabel(" ");
        construirInterface();
        atualizarStatus();
    }

    private void construirInterface() {
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(246, 247, 248));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topo = new JPanel(new BorderLayout(8, 8));
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Curadoria humana das situações-problema");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(31, 41, 51));
        topo.add(titulo, BorderLayout.NORTH);

        JTextArea explicacao = new JTextArea(
                "Selecione uma situação-problema para abrir sua curadoria específica. " +
                "Ao fechar essa tela, o estado atual dos campos é refletido na tabela e os metadados curados " +
                "são salvos automaticamente como fonte principal do Gerard.");
        explicacao.setEditable(false);
        explicacao.setFocusable(false);
        explicacao.setLineWrap(true);
        explicacao.setWrapStyleWord(true);
        explicacao.setOpaque(false);
        explicacao.setFont(new Font("Arial", Font.PLAIN, 12));
        explicacao.setForeground(new Color(82, 97, 107));
        topo.add(explicacao, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botoes.setOpaque(false);
        JButton validar = new JButton("Validar seleção");
        JButton recarregar = new JButton("Recarregar");
        JButton nova = new JButton("Nova linha");
        JButton remover = new JButton("Remover linha");

        validar.addActionListener(e -> validarSelecionadas());
        recarregar.addActionListener(e -> recarregar());
        nova.addActionListener(e -> novaLinha());
        remover.addActionListener(e -> removerLinhasSelecionadas());

        botoes.add(validar);
        botoes.add(recarregar);
        botoes.add(nova);
        botoes.add(remover);
        topo.add(botoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        configurarTabela();
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(new Color(213, 218, 224)));
        add(rolagem, BorderLayout.CENTER);

        status.setFont(new Font("Arial", Font.PLAIN, 12));
        status.setForeground(new Color(82, 97, 107));
        status.setHorizontalAlignment(SwingConstants.LEFT);
        add(status, BorderLayout.SOUTH);
    }

    private void configurarTabela() {
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JComboBox<TipoSituacaoAditiva> comboTipo = new JComboBox<TipoSituacaoAditiva>(TipoSituacaoAditiva.values());
        tabela.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(comboTipo));


        int[] larguras = new int[] {250, 620, 80, 110, 180};
        for (int i = 0; i < larguras.length && i < tabela.getColumnModel().getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }

        tabela.setDefaultRenderer(Object.class, new RendererCuradoria());
        modelo.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                atualizarStatus();
            }
        });

        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int linhaView = tabela.rowAtPoint(e.getPoint());
                int colunaView = tabela.columnAtPoint(e.getPoint());
                if (linhaView < 0 || colunaView < 0) {
                    return;
                }
                int colunaModelo = tabela.convertColumnIndexToModel(colunaView);
                if (colunaModelo == 1 && e.getClickCount() == 1) {
                    abrirTelaCuradoriaSituacao(tabela.convertRowIndexToModel(linhaView));
                }
            }
        });
    }

    public void abrirCuradoriaDaSituacao(SituacaoProblemaAditiva situacao) {
        if (situacao == null) {
            return;
        }
        int linha = localizarLinhaDaSituacao(situacao);
        if (linha < 0) {
            repositorio.recarregar();
            modelo.substituir(repositorio.listarTodas());
            linha = localizarLinhaDaSituacao(situacao);
        }
        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "A situação-problema renderizada não foi localizada na curadoria.",
                    "Curadoria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int linhaView = tabela.convertRowIndexToView(linha);
        if (linhaView >= 0) {
            tabela.getSelectionModel().setSelectionInterval(linhaView, linhaView);
            tabela.scrollRectToVisible(tabela.getCellRect(linhaView, 1, true));
        }
        abrirTelaCuradoriaSituacao(linha);
    }

    private int localizarLinhaDaSituacao(SituacaoProblemaAditiva situacao) {
        String id = situacao.getId() == null ? "" : situacao.getId().trim();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao linha = modelo.getLinha(i);
            String idLinha = linha.id == null ? "" : linha.id.trim();
            if (!id.isEmpty() && id.equals(idLinha)) {
                return i;
            }
        }
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao linha = modelo.getLinha(i);
            if (linha.tipo == situacao.getTipo()
                    && linha.codigoIdioma.equalsIgnoreCase(situacao.getCodigoIdioma())
                    && textosIguais(linha.enunciado, situacao.getEnunciado())) {
                return i;
            }
        }
        return -1;
    }

    private boolean textosIguais(String a, String b) {
        return ValidadorTraducaoCurada.textosIguais(a, b);
    }

    private void abrirTelaCuradoriaSituacao(final int linhaModelo) {
        if (linhaModelo < 0 || linhaModelo >= modelo.getRowCount()) {
            return;
        }
        final LinhaSituacao linha = modelo.getLinha(linhaModelo);
        final boolean versaoTraducaoSomenteTexto = "traducao".equalsIgnoreCase(linha.tipoVersao);
        if (versaoTraducaoSomenteTexto) {
            LinhaSituacao originalVinculada = localizarOriginalDoGrupo(linha.situacaoGrupoId);
            if (originalVinculada == null && linha.versaoOrigemId != null) {
                LinhaSituacao candidataOriginal = localizarLinhaPorId(linha.versaoOrigemId);
                if (candidataOriginal != null && "original".equalsIgnoreCase(candidataOriginal.tipoVersao)) {
                    originalVinculada = candidataOriginal;
                }
            }
            if (originalVinculada != null) {
                copiarMetadadosConceituais(originalVinculada, linha);
            }
        }
        final LinhaSituacao estadoAnterior = copiarLinha(linha);
        Window dono = SwingUtilities.getWindowAncestor(this);
        final JDialog dialogo = new JDialog(dono, "Curadoria da situação-problema", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialogo.setLayout(new BorderLayout(10, 10));

        JPanel conteudo = new JPanel(new BorderLayout(8, 8));
        conteudo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        conteudo.setBackground(new Color(246, 247, 248));

        final JCheckBox campoValidada = new JCheckBox("", Boolean.TRUE.equals(linha.validada));
        campoValidada.setOpaque(false);
        campoValidada.setFont(new Font("Arial", Font.BOLD, 12));
        campoValidada.setToolTipText("Valida somente a versão linguística atualmente aberta nesta tela.");
        final JLabel rotuloValidacaoAtual = new JLabel();
        rotuloValidacaoAtual.setFont(new Font("Arial", Font.BOLD, 12));
        final JLabel indicadorValidacao = new JLabel();
        indicadorValidacao.setFont(new Font("Arial", Font.PLAIN, 11));
        indicadorValidacao.setToolTipText("Estado atual da validação desta versão linguística.");
        Runnable atualizarIndicadorValidacao = () -> {
            IdiomaSituacao idiomaAtual = cadastroIdiomas.obter(linha.codigoIdioma);
            String nomeIdiomaAtual = idiomaAtual == null ? linha.codigoIdioma : idiomaAtual.getNome();
            String tipoAtual = "original".equalsIgnoreCase(linha.tipoVersao) ? "original" : "tradução";
            rotuloValidacaoAtual.setText("Validar esta versão: " + nomeIdiomaAtual + " — " + tipoAtual);
            if (campoValidada.isSelected()) {
                indicadorValidacao.setText("✓ Validada");
                indicadorValidacao.setForeground(new Color(46, 125, 50));
            } else {
                indicadorValidacao.setText("○ Não validada");
                indicadorValidacao.setForeground(new Color(97, 97, 97));
            }
        };
        campoValidada.addActionListener(e -> atualizarIndicadorValidacao.run());
        atualizarIndicadorValidacao.run();

        JLabel rotuloEnunciado = new JLabel("Situação-problema");
        rotuloEnunciado.setFont(new Font("Arial", Font.BOLD, 13));
        JPanel cabecalhoEnunciado = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cabecalhoEnunciado.setOpaque(false);
        cabecalhoEnunciado.add(rotuloEnunciado);
        final JTextArea areaEnunciado = new JTextArea(linha.enunciado == null ? "" : linha.enunciado, 3, 58);
        areaEnunciado.setLineWrap(true);
        areaEnunciado.setWrapStyleWord(true);
        areaEnunciado.setFont(UnicodeTexto.fonteCompativel(areaEnunciado, Font.PLAIN, 13));
        areaEnunciado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 218, 224)),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));

        JPanel topoDialogo = new JPanel(new BorderLayout(4, 4));
        topoDialogo.setOpaque(false);
        topoDialogo.add(cabecalhoEnunciado, BorderLayout.NORTH);
        topoDialogo.add(areaEnunciado, BorderLayout.CENTER);

        final JButton botaoAdicionarTraducao = new JButton(new IconeTraducao());
        botaoAdicionarTraducao.setPreferredSize(new Dimension(28, 28));
        botaoAdicionarTraducao.setFocusable(true);
        botaoAdicionarTraducao.setToolTipText("Inserir tradução vinculada a esta situação-problema");
        botaoAdicionarTraducao.getAccessibleContext().setAccessibleName("Inserir tradução");
        botaoAdicionarTraducao.getAccessibleContext().setAccessibleDescription("Cria uma nova versão linguística recuperando a curadoria da situação original.");
        final JComboBox<IdiomaSituacao> campoIdiomaTraducao = new JComboBox<IdiomaSituacao>();
        atualizarComboIdiomas(campoIdiomaTraducao, linha.codigoIdioma);
        campoIdiomaTraducao.setPreferredSize(new Dimension(190, 28));
        campoIdiomaTraducao.setToolTipText("Escolha o idioma da nova tradução");
        // O escopo linguístico é fixo: português, inglês, francês e espanhol.
        final JTextArea campoTextoTraducao = new JTextArea(2, 42);
        campoTextoTraducao.setLineWrap(true);
        campoTextoTraducao.setWrapStyleWord(true);
        campoTextoTraducao.setFont(UnicodeTexto.fonteCompativel(campoTextoTraducao, Font.PLAIN, 12));
        campoTextoTraducao.setToolTipText("Digite ou revise o enunciado traduzido");
        final JCheckBox campoTraducaoValidada = new JCheckBox("Tradução validada");
        campoTraducaoValidada.setOpaque(false);
        campoTraducaoValidada.setToolTipText("Marque somente após revisar linguisticamente a tradução selecionada.");
        final JLabel indicadorValidacaoTraducao = new JLabel("○ Não validada");
        indicadorValidacaoTraducao.setFont(new Font("Arial", Font.PLAIN, 11));
        Runnable atualizarIndicadorTraducao = () -> {
            if (campoTraducaoValidada.isSelected()) {
                indicadorValidacaoTraducao.setText("✓ Tradução validada");
                indicadorValidacaoTraducao.setForeground(new Color(46, 125, 50));
            } else {
                indicadorValidacaoTraducao.setText("○ Tradução não validada");
                indicadorValidacaoTraducao.setForeground(new Color(97, 97, 97));
            }
        };
        final String[] codigoTraducaoEmEdicao = new String[] { "" };
        final boolean[] carregandoTraducao = new boolean[] { false };
        // O listener do idioma é criado antes dos campos semânticos. Esta
        // referência permite atualizar dinamicamente o modo de edição assim
        // que o formulário já estiver completamente construído.
        final Runnable[] atualizarModoEdicaoPorIdioma = new Runnable[1];

        campoTraducaoValidada.addActionListener(e -> {
            if (carregandoTraducao[0]) {
                atualizarIndicadorTraducao.run();
                return;
            }
            String codigoSelecionado = codigoTraducaoEmEdicao[0];
            String textoAtual = UnicodeTexto.normalizarNfc(campoTextoTraducao.getText() == null
                    ? "" : campoTextoTraducao.getText().trim());
            if (campoTraducaoValidada.isSelected()
                    && !validarAlgarismosDaTraducao(dialogo, linha, textoAtual, true)) {
                carregandoTraducao[0] = true;
                try {
                    campoTraducaoValidada.setSelected(false);
                } finally {
                    carregandoTraducao[0] = false;
                }
            }
            atualizarIndicadorTraducao.run();
            if (!codigoSelecionado.isEmpty() && !textoAtual.isEmpty()) {
                salvarRascunhoTraducaoSilenciosamente(dialogo, linha, codigoSelecionado,
                        textoAtual, campoTraducaoValidada.isSelected());
            }
        });

        campoIdiomaTraducao.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.DESELECTED) {
                if (carregandoTraducao[0]) return;
                String codigoAnterior = codigoTraducaoEmEdicao[0];
                String textoAnterior = UnicodeTexto.normalizarNfc(campoTextoTraducao.getText() == null
                        ? "" : campoTextoTraducao.getText().trim());
                if (!codigoAnterior.isEmpty() && !textoAnterior.isEmpty()) {
                    salvarRascunhoTraducaoSilenciosamente(dialogo, linha, codigoAnterior,
                            textoAnterior, campoTraducaoValidada.isSelected());
                }
                return;
            }
            if (e.getStateChange() != java.awt.event.ItemEvent.SELECTED) return;
            carregandoTraducao[0] = true;
            try {
                IdiomaSituacao i = (IdiomaSituacao) e.getItem();
                if (i != null) UnicodeTexto.aplicarDirecao(campoTextoTraducao, i.getDirecao());
                String codigo = i == null ? "" : IdiomaSituacao.normalizarCodigo(i.getCodigo());
                codigoTraducaoEmEdicao[0] = codigo;
                LinhaSituacao existente = localizarVersaoDoGrupoPorIdioma(linha.situacaoGrupoId, codigo);
                if (existente != null && existente != linha) {
                    campoTextoTraducao.setText(existente.enunciado == null ? "" : existente.enunciado);
                    campoTraducaoValidada.setSelected(Boolean.TRUE.equals(existente.validada));
                } else {
                    campoTextoTraducao.setText("");
                    campoTraducaoValidada.setSelected(false);
                }
                atualizarIndicadorTraducao.run();
            } finally {
                carregandoTraducao[0] = false;
            }
            if (atualizarModoEdicaoPorIdioma[0] != null) {
                atualizarModoEdicaoPorIdioma[0].run();
            }
        });
        {
            IdiomaSituacao i = (IdiomaSituacao) campoIdiomaTraducao.getSelectedItem();
            if (i != null) {
                codigoTraducaoEmEdicao[0] = IdiomaSituacao.normalizarCodigo(i.getCodigo());
                UnicodeTexto.aplicarDirecao(campoTextoTraducao, i.getDirecao());
                LinhaSituacao existente = localizarVersaoDoGrupoPorIdioma(linha.situacaoGrupoId,
                        codigoTraducaoEmEdicao[0]);
                if (existente != null && existente != linha) {
                    campoTextoTraducao.setText(existente.enunciado == null ? "" : existente.enunciado);
                    campoTraducaoValidada.setSelected(Boolean.TRUE.equals(existente.validada));
                }
            }
        }
        atualizarIndicadorTraducao.run();
        JScrollPane rolagemTraducao = new JScrollPane(campoTextoTraducao);
        rolagemTraducao.setBorder(BorderFactory.createLineBorder(new Color(213, 218, 224)));
        JPanel painelTraducao = new JPanel(new BorderLayout(6, 0));
        painelTraducao.setOpaque(false);
        JPanel controlesTraducao = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        controlesTraducao.setOpaque(false);
        controlesTraducao.add(botaoAdicionarTraducao);
        controlesTraducao.add(campoIdiomaTraducao);
        JPanel estadoTraducao = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        estadoTraducao.setOpaque(false);
        estadoTraducao.add(campoTraducaoValidada);
        estadoTraducao.add(indicadorValidacaoTraducao);
        painelTraducao.add(controlesTraducao, BorderLayout.WEST);
        painelTraducao.add(rolagemTraducao, BorderLayout.CENTER);
        painelTraducao.add(estadoTraducao, BorderLayout.SOUTH);
        topoDialogo.add(painelTraducao, BorderLayout.SOUTH);
        conteudo.add(topoDialogo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        final JTextField campoId = campoTexto(linha.id);
        final JComboBox<IdiomaSituacao> campoIdiomaVersao = new JComboBox<IdiomaSituacao>();
        atualizarComboIdiomas(campoIdiomaVersao, linha.codigoIdioma);
        campoIdiomaVersao.setToolTipText("Idioma desta versão linguística");
        campoIdiomaVersao.addActionListener(e -> {
            IdiomaSituacao i = (IdiomaSituacao) campoIdiomaVersao.getSelectedItem();
            if (i != null) UnicodeTexto.aplicarDirecao(areaEnunciado, i.getDirecao());
            if (atualizarModoEdicaoPorIdioma[0] != null) atualizarModoEdicaoPorIdioma[0].run();
        });
        { IdiomaSituacao i=(IdiomaSituacao)campoIdiomaVersao.getSelectedItem(); if(i!=null) UnicodeTexto.aplicarDirecao(areaEnunciado, i.getDirecao()); }
        final JTextField campoSituacaoGrupoId = campoTexto(linha.situacaoGrupoId);
        campoSituacaoGrupoId.setEditable(false);
        campoSituacaoGrupoId.setToolTipText("Preenchido automaticamente a partir da versão original vinculada.");
        final JComboBox<String> campoTipoVersao = comboTipoVersao(linha.tipoVersao);
        final JComboBox<String> campoVersaoOrigemId = comboVersoesOriginais(linha.versaoOrigemId, linha.id);
        configurarVinculoRelacional(campoTipoVersao, campoVersaoOrigemId, campoSituacaoGrupoId, campoId);
        Runnable atualizarRotuloVersaoAtual = () -> {
            IdiomaSituacao idiomaSelecionado = (IdiomaSituacao) campoIdiomaVersao.getSelectedItem();
            String nome = idiomaSelecionado == null ? linha.codigoIdioma : idiomaSelecionado.getNome();
            String tipo = "original".equalsIgnoreCase(String.valueOf(campoTipoVersao.getSelectedItem())) ? "original" : "tradução";
            rotuloValidacaoAtual.setText("Validar esta versão: " + nome + " — " + tipo);
        };
        campoIdiomaVersao.addActionListener(e -> atualizarRotuloVersaoAtual.run());
        campoTipoVersao.addActionListener(e -> atualizarRotuloVersaoAtual.run());
        atualizarRotuloVersaoAtual.run();
        final JTextField campoFonte = campoTexto(linha.fonte);
        // Mantido somente como suporte ao formato legado dos dados. O subtipo
        // não é exibido nem editado na curadoria, pois sua informação já é
        // determinada pelos campos estruturados (categoria, papéis, sinais e
        // termo desconhecido).
        final JTextField campoSubtipo = campoTexto(linha.subtipo);
        final JTextField campoPersonagem1 = campoTexto(linha.personagem1);
        final JTextField campoPersonagem2 = campoTexto(linha.personagem2);
        final JTextField campoPersonagem3 = campoTexto(linha.personagem3);
        campoPersonagem1.setToolTipText("Nome de pessoa, grupo, animal ou objeto envolvido na situação-problema.");
        campoPersonagem2.setToolTipText("Segundo nome de pessoa, grupo, animal ou objeto envolvido, quando houver.");
        campoPersonagem3.setToolTipText("Terceiro nome de pessoa, grupo, animal ou objeto envolvido, quando houver.");
        final TipoSituacaoAditiva tipoSemantico = linha.tipo;
        final boolean termoDesconhecidoEstavaVazioAoAbrir =
                linha.termoDesconhecido == null
                || linha.termoDesconhecido.trim().isEmpty();
        // Compatibilidade com registros curados em que o próprio campo do papel
        // contém "?", mas termo_desconhecido ainda está vazio. A curadoria
        // continua sendo a fonte da verdade; apenas harmonizamos suas duas formas
        // explícitas de registrar a incógnita.
        harmonizarTermoDesconhecidoComValores(linha);
        final boolean composicaoTransformacoes =
                tipoSemantico == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES;
        final JTextField campoEstadoInicial = campoTexto(linha.estadoInicial);
        final JTextField campoTransformacao = campoTexto(linha.transformacao);
        final JTextField campoEstadoFinal = campoTexto(linha.estadoFinal);
        final JTextField campoQuantidade1 = campoTexto(linha.quantidade1);
        final JTextField campoQuantidade2 = campoTexto(linha.quantidade2);
        final JTextField campoResultado = campoTexto(linha.resultado);
        final JTextField campoReferido = campoTexto(linha.referido);
        final JTextField campoReferendo = campoTexto(linha.referendo);
        final JTextField campoValorRelativo = campoTexto(linha.valorRelativo);

        // A escolha do sinal é uma responsabilidade coesa da camada de curadoria.
        // A tela apenas registra os campos aplicáveis à categoria atual.
        final ControladorSinaisCuradoria controladorSinais =
                new ControladorSinaisCuradoria(tipoSemantico, localizacao);
        final PainelValorComSinalCuradoria painelSinalTransformacao =
                controladorSinais.registrarSeAplicavel(
                        PapelSinalCuradoria.TRANSFORMACAO,
                        ModoPersistenciaSinalCuradoria.METADADO_SEPARADO,
                        campoTransformacao, linha.sinalTransformacao);
        // A composição de transformações já possuía seletores de sinal no
        // formato legado. Eles são preservados como opcionais; a nova exigência
        // obrigatória vale somente para os campos com metadado próprio:
        // sinal_transformacao e sinal_valor_relativo.
        final PainelValorComSinalCuradoria painelSinalTransformacao1 =
                composicaoTransformacoes
                ? controladorSinais.registrar(
                        PapelSinalCuradoria.TRANSFORMACAO_1,
                        ModoPersistenciaSinalCuradoria.EMBUTIDO_NO_VALOR,
                        campoQuantidade1, "") : null;
        final PainelValorComSinalCuradoria painelSinalTransformacao2 =
                composicaoTransformacoes
                ? controladorSinais.registrar(
                        PapelSinalCuradoria.TRANSFORMACAO_2,
                        ModoPersistenciaSinalCuradoria.EMBUTIDO_NO_VALOR,
                        campoQuantidade2, "") : null;
        final PainelValorComSinalCuradoria painelSinalTransformacaoResultante =
                composicaoTransformacoes
                ? controladorSinais.registrar(
                        PapelSinalCuradoria.TRANSFORMACAO_RESULTANTE,
                        ModoPersistenciaSinalCuradoria.EMBUTIDO_NO_VALOR,
                        campoResultado, "") : null;
        final PainelValorComSinalCuradoria painelSinalValorRelativo =
                controladorSinais.registrarSeAplicavel(
                        PapelSinalCuradoria.VALOR_RELATIVO,
                        ModoPersistenciaSinalCuradoria.METADADO_SEPARADO,
                        campoValorRelativo, linha.sinalValorRelativo);
        final JComboBox<String> campoTermoDesconhecido = comboTermoDesconhecido(tipoSemantico, linha.termoDesconhecido);
        final AvisoTermoDesconhecidoVazio avisoTermoDesconhecido =
                new AvisoTermoDesconhecidoVazio(
                        campoTermoDesconhecido,
                        termoDesconhecidoEstavaVazioAoAbrir,
                        termoDesconhecidoEstavaVazioAoAbrir
                                ? linha.termoDesconhecido : "",
                        localizacao);
        final JTextField campoRepresentacao = campoTexto(linha.representacaoVisual);
        final JTextField campoObservacoes = campoTexto(linha.observacoes);
        // Trechos de texto natural associados a cada papel semântico da
        // categoria (nesta ordem), usados para construir o enunciado a partir
        // do diagrama preenchido. Toda categoria simples tem exatamente três
        // papéis (CategoriaSimples em CatalogoEsquemasCategoriasAditivas); as
        // duas categorias compostas (COMPOSICAO_TRANSFORMACAO_MEDIDAS e
        // TRANSFORMACAO_COMPOSTA_DOIS_PASSOS) são duas categorias simples
        // encadeadas, por isso reaproveitam o padrão duas vezes (seis campos).
        final boolean categoriaComposta =
                tipoSemantico == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS
                || tipoSemantico == TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS;
        final JTextField campoFragmentoTexto1 = campoTexto(linha.fragmentoTexto1);
        final JTextField campoFragmentoTexto2 = campoTexto(linha.fragmentoTexto2);
        final JTextField campoFragmentoTexto3 = campoTexto(linha.fragmentoTexto3);
        final JTextField campoFragmentoTexto4 = campoTexto(linha.fragmentoTexto4);
        final JTextField campoFragmentoTexto5 = campoTexto(linha.fragmentoTexto5);
        final JTextField campoFragmentoTexto6 = campoTexto(linha.fragmentoTexto6);

        int y = 0;
        final JLabel avisoSemanticaOriginal = new JLabel(localizacao.texto("curadoria.semantica.somenteOriginal"));
        avisoSemanticaOriginal.setFont(new Font("Arial", Font.PLAIN, 11));
        avisoSemanticaOriginal.setForeground(new Color(82, 97, 107));
        avisoSemanticaOriginal.setVisible(versaoTraducaoSomenteTexto);
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(avisoSemanticaOriginal, gbc);
        gbc.gridwidth = 1;
        // Os identificadores técnicos permanecem no modelo e no arquivo de dados,
        // pois são necessários para vincular versões e traduções. Eles não são
        // apresentados na curadoria: não constituem informação pedagógica para o
        // usuário e sua exposição introduzia ruído e confusão na leitura da tela.
        y = adicionarCampo(formulario, gbc, y, "idioma da versão", campoIdiomaVersao);
        y = adicionarCampo(formulario, gbc, y, "tipo_versão", campoTipoVersao);
        y = adicionarCampo(formulario, gbc, y, "fonte", campoFonte);
        y = adicionarCampo(formulario, gbc, y, "personagem_1", campoPersonagem1);
        y = adicionarCampo(formulario, gbc, y, "personagem_2", campoPersonagem2);
        y = adicionarCampo(formulario, gbc, y, "personagem_3", campoPersonagem3);
        // A parte semântica é montada a partir da categoria. Nenhum campo de
        // outra estrutura aditiva é apresentado ao pesquisador.
        if (tipoSemantico == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            y = adicionarCampo(formulario, gbc, y, "parte_1", campoQuantidade1);
            y = adicionarCampo(formulario, gbc, y, "parte_2", campoQuantidade2);
            y = adicionarCampo(formulario, gbc, y, "todo", campoResultado);
        } else if (tipoSemantico == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            y = adicionarCampo(formulario, gbc, y, "estado_inicial", campoEstadoInicial);
            y = adicionarCampo(formulario, gbc, y, "transformacao", painelSinalTransformacao);
            y = adicionarCampo(formulario, gbc, y, "estado_final", campoEstadoFinal);
        } else if (tipoSemantico == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS) {
            y = adicionarCampo(formulario, gbc, y, "parte_1", campoQuantidade1);
            y = adicionarCampo(formulario, gbc, y, "parte_2", campoQuantidade2);
            y = adicionarCampo(formulario, gbc, y, "todo", campoResultado);
            y = adicionarCampo(formulario, gbc, y, "estado_inicial", campoEstadoInicial);
            y = adicionarCampo(formulario, gbc, y, "transformacao", painelSinalTransformacao);
            y = adicionarCampo(formulario, gbc, y, "estado_final", campoEstadoFinal);
        } else if (tipoSemantico == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            y = adicionarCampo(formulario, gbc, y, "referendo", campoReferendo);
            y = adicionarCampo(formulario, gbc, y, "valor_relativo", painelSinalValorRelativo);
            y = adicionarCampo(formulario, gbc, y, "referido", campoReferido);
        } else if (tipoSemantico == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            y = adicionarCampo(formulario, gbc, y, "transformacao_1", painelSinalTransformacao1);
            y = adicionarCampo(formulario, gbc, y, "transformacao_2", painelSinalTransformacao2);
            y = adicionarCampo(formulario, gbc, y, "transformacao_resultante", painelSinalTransformacaoResultante);
        } else if (tipoSemantico == TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS) {
            y = adicionarCampo(formulario, gbc, y, "estado_inicial", campoEstadoInicial);
            y = adicionarCampo(formulario, gbc, y, "transformacao_1", campoQuantidade1);
            y = adicionarCampo(formulario, gbc, y, "transformacao_2", campoQuantidade2);
            y = adicionarCampo(formulario, gbc, y, "estado_final", campoResultado);
        } else if (tipoSemantico == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            y = adicionarCampo(formulario, gbc, y, "relacao_inicial", campoEstadoInicial);
            y = adicionarCampo(formulario, gbc, y, "transformacao", painelSinalTransformacao);
            y = adicionarCampo(formulario, gbc, y, "relacao_final", campoEstadoFinal);
        } else if (tipoSemantico == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            y = adicionarCampo(formulario, gbc, y, "relacao_1", campoQuantidade1);
            y = adicionarCampo(formulario, gbc, y, "relacao_2", campoQuantidade2);
            y = adicionarCampo(formulario, gbc, y, "relacao_resultante", campoResultado);
        }
        y = adicionarCampo(formulario, gbc, y, "termo_desconhecido", campoTermoDesconhecido);
        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(avisoTermoDesconhecido.getComponente(), gbc);
        y = adicionarCampo(formulario, gbc, y, "representacao_visual", campoRepresentacao);
        y = adicionarCampo(formulario, gbc, y, "observacoes", campoObservacoes);
        y = adicionarCampo(formulario, gbc, y, "trecho_texto_1", campoFragmentoTexto1);
        y = adicionarCampo(formulario, gbc, y, "trecho_texto_2", campoFragmentoTexto2);
        y = adicionarCampo(formulario, gbc, y, "trecho_texto_3", campoFragmentoTexto3);
        if (categoriaComposta) {
            y = adicionarCampo(formulario, gbc, y, "trecho_texto_4", campoFragmentoTexto4);
            y = adicionarCampo(formulario, gbc, y, "trecho_texto_5", campoFragmentoTexto5);
            y = adicionarCampo(formulario, gbc, y, "trecho_texto_6", campoFragmentoTexto6);
        }

        JPanel painelValidacaoVersao = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelValidacaoVersao.setOpaque(false);
        painelValidacaoVersao.add(campoValidada);
        painelValidacaoVersao.add(rotuloValidacaoAtual);
        painelValidacaoVersao.add(indicadorValidacao);
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(painelValidacaoVersao, gbc);
        gbc.gridwidth = 1;

        // A seleção no editor de tradução muda dinamicamente o modo da tela.
        // Ao escolher um idioma diferente do idioma da versão aberta, apenas a
        // tradução e sua validação permanecem editáveis. Os dados da versão
        // original ficam visíveis, porém protegidos contra alterações.
        campoTipoVersao.setEnabled(false);
        atualizarModoEdicaoPorIdioma[0] = () -> {
            IdiomaSituacao idiomaDaVersao = (IdiomaSituacao) campoIdiomaVersao.getSelectedItem();
            String codigoDaVersao = idiomaDaVersao == null
                    ? IdiomaSituacao.normalizarCodigo(linha.codigoIdioma)
                    : IdiomaSituacao.normalizarCodigo(idiomaDaVersao.getCodigo());
            String codigoSelecionado = IdiomaSituacao.normalizarCodigo(codigoTraducaoEmEdicao[0]);
            boolean outroIdiomaSelecionado = !codigoSelecionado.isEmpty()
                    && !codigoSelecionado.equalsIgnoreCase(codigoDaVersao);
            boolean semanticaHerdada = versaoTraducaoSomenteTexto || outroIdiomaSelecionado;
            String dicaHerdado = localizacao.texto("curadoria.semantica.herdadaTooltip");

            // Se o editor superior está apontando para outro idioma, a versão
            // atualmente aberta também fica protegida. Assim, somente o texto
            // traduzido e o respectivo marcador de validação ficam ativos.
            areaEnunciado.setEditable(!outroIdiomaSelecionado);
            areaEnunciado.setFocusable(!outroIdiomaSelecionado);
            areaEnunciado.setBackground(outroIdiomaSelecionado
                    ? new Color(238, 241, 244) : UIManager.getColor("TextArea.background"));
            areaEnunciado.setForeground(outroIdiomaSelecionado
                    ? new Color(82, 97, 107) : UIManager.getColor("TextArea.foreground"));
            campoValidada.setEnabled(!outroIdiomaSelecionado);
            campoIdiomaVersao.setEnabled(!semanticaHerdada);
            avisoSemanticaOriginal.setVisible(semanticaHerdada);

            configurarCampoHerdado(campoFonte, dicaHerdado, semanticaHerdada);
            configurarCampoHerdado(campoSubtipo, dicaHerdado, semanticaHerdada);
            configurarCampoHerdado(campoPersonagem1, dicaHerdado, semanticaHerdada);
            configurarCampoHerdado(campoPersonagem2, dicaHerdado, semanticaHerdada);
            configurarCampoHerdado(campoPersonagem3, dicaHerdado, semanticaHerdada);
            configurarCampoHerdado(campoEstadoInicial, dicaHerdado, semanticaHerdada);
            configurarCampoHerdado(campoEstadoFinal, dicaHerdado, semanticaHerdada);
            configurarCampoHerdado(campoReferido, dicaHerdado, semanticaHerdada);
            configurarCampoHerdado(campoReferendo, dicaHerdado, semanticaHerdada);
            controladorSinais.definirSemanticaHerdada(semanticaHerdada, dicaHerdado);
            if (painelSinalTransformacao == null) {
                configurarCampoHerdado(campoTransformacao, dicaHerdado, semanticaHerdada);
            }
            if (painelSinalTransformacao1 == null) {
                configurarCampoHerdado(campoQuantidade1, dicaHerdado, semanticaHerdada);
            }
            if (painelSinalTransformacao2 == null) {
                configurarCampoHerdado(campoQuantidade2, dicaHerdado, semanticaHerdada);
            }
            if (painelSinalTransformacaoResultante == null) {
                configurarCampoHerdado(campoResultado, dicaHerdado, semanticaHerdada);
            }
            if (painelSinalValorRelativo == null) {
                configurarCampoHerdado(campoValorRelativo, dicaHerdado, semanticaHerdada);
            }
            configurarComboHerdado(campoTermoDesconhecido, dicaHerdado, semanticaHerdada);
            avisoTermoDesconhecido.definirSemanticaHerdada(semanticaHerdada);
            configurarCampoHerdado(campoRepresentacao, dicaHerdado, semanticaHerdada);
            configurarCampoHerdado(campoObservacoes, dicaHerdado, semanticaHerdada);

            formulario.revalidate();
            formulario.repaint();
        };
        atualizarModoEdicaoPorIdioma[0].run();

        formulario.setBorder(BorderFactory.createLineBorder(new Color(213, 218, 224)));
        conteudo.add(formulario, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.setOpaque(false);
        JLabel avisoSalvamento = new JLabel("As alterações serão salvas automaticamente ao fechar.");
        avisoSalvamento.setFont(new Font("Arial", Font.PLAIN, 11));
        avisoSalvamento.setForeground(new Color(82, 97, 107));
        JButton fechar = new JButton("Salvar e fechar");

        final Runnable fecharESalvar = new Runnable() {
            private boolean executando;

            public void run() {
                if (executando) {
                    return;
                }
                executando = true;
                try {
                    if (!versaoTraducaoSomenteTexto
                            && !controladorSinais.validarAntesDeSalvar(dialogo)) {
                        executando = false;
                        fechar.setEnabled(true);
                        avisoSalvamento.setText(localizacao.texto(
                                "curadoria.sinal.revisarAntesSalvar"));
                        return;
                    }
                    controladorSinais.normalizarCamposExibidos();
                    aplicarCamposDaCuradoriaDetalhada(linha, campoValidada, areaEnunciado, campoId, campoIdiomaVersao, campoSituacaoGrupoId, campoTipoVersao, campoVersaoOrigemId, campoFonte, campoSubtipo, campoPersonagem1, campoPersonagem2, campoPersonagem3,
                            campoEstadoInicial, campoTransformacao, campoEstadoFinal, campoQuantidade1, campoQuantidade2,
                            campoResultado, campoReferido, campoReferendo, campoValorRelativo, controladorSinais, campoTermoDesconhecido, campoRepresentacao, campoObservacoes,
                            campoFragmentoTexto1, campoFragmentoTexto2, campoFragmentoTexto3, campoFragmentoTexto4, campoFragmentoTexto5, campoFragmentoTexto6);
                    modelo.atualizarLinha(linhaModelo);

                    // A área de tradução é um editor independente do formulário principal.
                    // No fechamento, a tradução selecionada é incorporada ao modelo e toda a
                    // curadoria é persistida uma única vez. A versão anterior fazia uma gravação
                    // intermediária e outra gravação ao final, o que podia bloquear a EDT e
                    // deixar tanto o botão quanto o X aparentemente sem resposta.
                    String codigoTraducaoAtual = codigoTraducaoEmEdicao[0];
                    String textoTraducaoAtual = UnicodeTexto.normalizarNfc(
                            campoTextoTraducao.getText() == null ? "" : campoTextoTraducao.getText().trim());
                    boolean traducaoDesvalidadaPorNumeros = false;
                    if (!codigoTraducaoAtual.isEmpty() && !textoTraducaoAtual.isEmpty()) {
                        LinhaSituacao original = "original".equals(linha.tipoVersao)
                                ? linha : localizarOriginalDoGrupo(linha.situacaoGrupoId);
                        boolean validada = campoTraducaoValidada.isSelected();
                        if (validada && !validarAlgarismosDaTraducao(null, original, textoTraducaoAtual, false)) {
                            validada = false;
                            traducaoDesvalidadaPorNumeros = true;
                            campoTraducaoValidada.setSelected(false);
                            atualizarIndicadorTraducao.run();
                        }
                        LinhaSituacao traducao = obterOuCriarTraducaoUnica(original,
                                codigoTraducaoAtual, textoTraducaoAtual, validada, null);
                        if (traducao == null) {
                            executando = false;
                            fechar.setEnabled(true);
                            avisoSalvamento.setText("Não foi possível preparar a tradução para salvar.");
                            return;
                        }
                        modelo.fireTableDataChanged();
                    }

                    if (!validarAntesDeSalvarCuradoriaDetalhada(dialogo, linha, estadoAnterior, linhaModelo)) {
                        executando = false;
                        fechar.setEnabled(true);
                        avisoSalvamento.setText("Revise os dados indicados para concluir o salvamento.");
                        return;
                    }
                    modelo.atualizarLinha(linhaModelo);
                    avisoSalvamento.setText("Salvando...");
                    fechar.setEnabled(false);
                    salvarCuradoriaSemMensagem();
                    dialogo.dispose();
                    if (traducaoDesvalidadaPorNumeros) {
                        JOptionPane.showMessageDialog(TelaCuradoriaSituacoes.this,
                                "A tradução foi salva como não validada porque seus valores numéricos não correspondem aos valores da situação original.",
                                "Tradução salva como não validada", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    executando = false;
                    fechar.setEnabled(true);
                    avisoSalvamento.setText("Falha no salvamento. Corrija o problema e tente novamente.");
                    RegistroErrosCuradoria.registrar("ERRO_SALVAMENTO", linha.id, linha.situacaoGrupoId,
                            ex.getMessage(), "SALVAMENTO_NAO_REALIZADO");
                    JOptionPane.showMessageDialog(dialogo, "Erro ao salvar automaticamente a curadoria: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        botaoAdicionarTraducao.addActionListener(e -> {
            if (!versaoTraducaoSomenteTexto
                    && !controladorSinais.validarAntesDeSalvar(dialogo)) {
                return;
            }
            controladorSinais.normalizarCamposExibidos();
            aplicarCamposDaCuradoriaDetalhada(linha, campoValidada, areaEnunciado, campoId, campoIdiomaVersao, campoSituacaoGrupoId, campoTipoVersao, campoVersaoOrigemId, campoFonte, campoSubtipo, campoPersonagem1, campoPersonagem2, campoPersonagem3,
                    campoEstadoInicial, campoTransformacao, campoEstadoFinal, campoQuantidade1, campoQuantidade2,
                    campoResultado, campoReferido, campoReferendo, campoValorRelativo, controladorSinais, campoTermoDesconhecido, campoRepresentacao, campoObservacoes,
                    campoFragmentoTexto1, campoFragmentoTexto2, campoFragmentoTexto3, campoFragmentoTexto4, campoFragmentoTexto5, campoFragmentoTexto6);
            IdiomaSituacao idiomaSelecionadoTraducao = (IdiomaSituacao) campoIdiomaTraducao.getSelectedItem();
            String idiomaDestino = idiomaSelecionadoTraducao == null ? "" : idiomaSelecionadoTraducao.getCodigo();
            String textoTraduzido = UnicodeTexto.normalizarNfc(campoTextoTraducao.getText() == null ? "" : campoTextoTraducao.getText().trim());
            if (!UnicodeTexto.fonteExibeTudo(campoTextoTraducao, textoTraduzido)) {
                int opcaoFonte = JOptionPane.showConfirmDialog(dialogo,
                        "A fonte disponível neste computador pode não exibir todos os caracteres desta tradução.\nDeseja continuar mesmo assim?",
                        "Compatibilidade da fonte", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (opcaoFonte != JOptionPane.YES_OPTION) return;
            }
            if (idiomaDestino.isEmpty() || textoTraduzido.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Escolha o idioma e informe o texto da tradução.", "Tradução", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LinhaSituacao original = "original".equals(linha.tipoVersao) ? linha : localizarOriginalDoGrupo(linha.situacaoGrupoId);
            if (original == null) {
                JOptionPane.showMessageDialog(dialogo, "Não foi encontrada a versão original vinculada.", "Tradução", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (idiomaDestino.equalsIgnoreCase(original.codigoIdioma)) {
                JOptionPane.showMessageDialog(dialogo, "O idioma escolhido já corresponde à versão original.", "Tradução", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!confirmarEstadoValidacaoTraducaoAntesDeInserir(dialogo, idiomaSelecionadoTraducao,
                    campoTraducaoValidada, atualizarIndicadorTraducao)) {
                return;
            }
            if (campoTraducaoValidada.isSelected()
                    && !validarAlgarismosDaTraducao(dialogo, original, textoTraduzido, true)) {
                campoTraducaoValidada.setSelected(false);
                atualizarIndicadorTraducao.run();
                return;
            }
            boolean jaExistia = localizarVersaoDoGrupoPorIdioma(original.situacaoGrupoId, idiomaDestino) != null;
            LinhaSituacao traducao = obterOuCriarTraducaoUnica(original, idiomaDestino, textoTraduzido,
                    campoTraducaoValidada.isSelected(), dialogo);
            if (traducao == null) return;
            modelo.fireTableDataChanged();
            atualizarIndicadorTraducao.run();
            persistirCuradoriaImediatamenteSemRecarregar(dialogo,
                    jaExistia ? "Não foi possível salvar imediatamente a tradução atualizada."
                              : "Não foi possível salvar imediatamente a nova tradução.");
            JOptionPane.showMessageDialog(dialogo,
                    (jaExistia ? "A tradução existente foi atualizada" : "Tradução inserida e vinculada à situação original")
                    + ". Estado: " + (traducao.validada ? "validada" : "não validada") + ".",
                    "Tradução", JOptionPane.INFORMATION_MESSAGE);
        });

        fechar.addActionListener(e -> fecharESalvar.run());
        dialogo.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                fecharESalvar.run();
            }
        });
        botoes.add(avisoSalvamento);
        botoes.add(fechar);
        conteudo.add(botoes, BorderLayout.SOUTH);

        dialogo.add(conteudo, BorderLayout.CENTER);
        dialogo.setMinimumSize(new Dimension(650, 520));
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }



    /**
     * Cria ou atualiza silenciosamente a versão linguística em edição. É usado
     * ao marcar a validação e antes de trocar o idioma do combobox, evitando que
     * texto e estado fiquem apenas nos componentes visuais da janela.
     */
    private boolean salvarRascunhoTraducaoSilenciosamente(Component pai, LinhaSituacao linhaAtual,
            String codigoIdioma, String textoTraduzido, boolean validada) {
        String codigo = IdiomaSituacao.normalizarCodigo(codigoIdioma);
        String texto = UnicodeTexto.normalizarNfc(textoTraduzido == null ? "" : textoTraduzido.trim());
        if (codigo.isEmpty() || texto.isEmpty()) return true;

        LinhaSituacao original = "original".equals(linhaAtual.tipoVersao)
                ? linhaAtual : localizarOriginalDoGrupo(linhaAtual.situacaoGrupoId);
        if (original == null || codigo.equalsIgnoreCase(IdiomaSituacao.normalizarCodigo(original.codigoIdioma))) {
            return false;
        }
        if (validada && !validarAlgarismosDaTraducao(pai, original, texto, true)) {
            validada = false;
        }

        LinhaSituacao traducao = obterOuCriarTraducaoUnica(original, codigo, texto, validada, pai);
        if (traducao == null) return false;
        modelo.fireTableDataChanged();
        return persistirCuradoriaImediatamenteSemRecarregar(pai,
                "Não foi possível salvar automaticamente a tradução antes da troca de idioma.");
    }

    /**
     * Persiste alterações linguísticas imediatamente no arquivo do usuário,
     * sem recarregar o repositório enquanto a janela de curadoria está aberta.
     * Isso garante continuidade entre versões do Gérard e evita que o estado
     * validado dependa apenas do fechamento posterior da janela.
     */
    private boolean persistirCuradoriaImediatamenteSemRecarregar(Component pai, String mensagemErro) {
        try {
            consolidarDuplicidadesLinguisticas(false, pai);
            List<SituacaoProblemaAditiva> situacoes = modelo.paraSituacoes();
            ValidadorVinculosTraducoes.validarOuFalhar(situacoes);
            RepositorioSituacoesAditivas.salvarCuradoria(situacoes);
            atualizarStatus();
            return true;
        } catch (Exception ex) {
            RegistroErrosCuradoria.registrar("ERRO_PERSISTENCIA_IMEDIATA", "", "",
                    ex.getMessage(), "ALTERACAO_MANTIDA_APENAS_NA_TELA");
            JOptionPane.showMessageDialog(pai,
                    mensagemErro + "\nA alteração continua na tela e será tentada novamente ao fechar.\nDetalhes: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    /**
     * Único ponto de criação/atualização de uma tradução. Tanto a aba de
     * curadoria quanto o botão contextual chegam a esta mesma tela e usam este
     * método, evitando dois fluxos independentes de persistência.
     */
    private LinhaSituacao obterOuCriarTraducaoUnica(LinhaSituacao original,
            String codigoIdioma, String enunciado, boolean validada, Component pai) {
        if (original == null) return null;
        String grupo = original.situacaoGrupoId == null || original.situacaoGrupoId.trim().isEmpty()
                ? original.id : original.situacaoGrupoId.trim();
        original.situacaoGrupoId = grupo;
        String codigo = IdiomaSituacao.normalizarCodigo(codigoIdioma);

        consolidarDuplicidadesDoGrupoIdioma(grupo, codigo, original, pai, false);
        LinhaSituacao traducao = localizarVersaoDoGrupoPorIdioma(grupo, codigo);
        if (traducao == original) traducao = null;
        if (traducao == null) {
            traducao = criarTraducaoAPartirDaOriginal(original, codigo, enunciado);
            modelo.adicionarLinha(traducao);
        }
        traducao.enunciado = UnicodeTexto.normalizarNfc(enunciado == null ? "" : enunciado.trim());
        traducao.situacaoGrupoId = grupo;
        traducao.tipoVersao = "traducao";
        traducao.versaoOrigemId = original.id;
        traducao.codigoIdioma = codigo;
        traducao.validada = validada;
        copiarMetadadosConceituais(original, traducao);
        return traducao;
    }

    /** Consolida dados antigos que contenham mais de uma versão por grupo/idioma. */
    private int consolidarDuplicidadesLinguisticas(boolean informarUsuario, Component pai) {
        Map<String, List<LinhaSituacao>> grupos = new LinkedHashMap<String, List<LinhaSituacao>>();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao l = modelo.getLinha(i);
            if (l == null) continue;
            String grupo = l.situacaoGrupoId == null ? "" : l.situacaoGrupoId.trim();
            String idioma = IdiomaSituacao.normalizarCodigo(l.codigoIdioma).toLowerCase(java.util.Locale.ROOT);
            if (grupo.isEmpty() || idioma.isEmpty()) continue;
            String chave = grupo + "\u0000" + idioma;
            List<LinhaSituacao> lista = grupos.get(chave);
            if (lista == null) { lista = new ArrayList<LinhaSituacao>(); grupos.put(chave, lista); }
            lista.add(l);
        }
        int total = 0;
        for (List<LinhaSituacao> lista : grupos.values()) {
            if (lista.size() < 2) continue;
            LinhaSituacao primeira = lista.get(0);
            LinhaSituacao original = localizarOriginalDoGrupo(primeira.situacaoGrupoId);
            total += consolidarDuplicidadesDoGrupoIdioma(primeira.situacaoGrupoId,
                    primeira.codigoIdioma, original, pai, false);
        }
        if (total > 0) {
            modelo.fireTableDataChanged();
            RegistroErrosCuradoria.registrar("DUPLICIDADE_TRADUCAO", "", "",
                    total + " versão(ões) linguística(s) redundante(s) consolidada(s).",
                    "CONSOLIDACAO_AUTOMATICA");
            if (informarUsuario && pai != null) {
                JOptionPane.showMessageDialog(pai,
                        "Foram consolidadas automaticamente " + total
                        + " versão(ões) linguística(s) duplicada(s).\n"
                        + "O texto mais completo e o estado validado foram preservados.",
                        "Consolidação de traduções", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        return total;
    }

    private int consolidarDuplicidadesDoGrupoIdioma(String grupoId, String codigoIdioma,
            LinhaSituacao original, Component pai, boolean informarUsuario) {
        String grupo = grupoId == null ? "" : grupoId.trim();
        String codigo = IdiomaSituacao.normalizarCodigo(codigoIdioma);
        List<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao l = modelo.getLinha(i);
            if (l != null && grupo.equals(l.situacaoGrupoId == null ? "" : l.situacaoGrupoId.trim())
                    && codigo.equalsIgnoreCase(IdiomaSituacao.normalizarCodigo(l.codigoIdioma))) {
                indices.add(i);
            }
        }
        if (indices.size() < 2) return 0;

        int indiceMantido = indices.get(0);
        LinhaSituacao mantida = modelo.getLinha(indiceMantido);
        for (int idx : indices) {
            LinhaSituacao candidata = modelo.getLinha(idx);
            if (pontuarTraducao(candidata) > pontuarTraducao(mantida)) {
                mantida = candidata;
                indiceMantido = idx;
            }
        }
        boolean algumaValidada = false;
        String melhorTexto = mantida.enunciado == null ? "" : mantida.enunciado;
        for (int idx : indices) {
            LinhaSituacao candidata = modelo.getLinha(idx);
            algumaValidada |= Boolean.TRUE.equals(candidata.validada);
            String texto = candidata.enunciado == null ? "" : candidata.enunciado.trim();
            if (texto.length() > melhorTexto.trim().length()) melhorTexto = texto;
        }
        mantida.enunciado = UnicodeTexto.normalizarNfc(melhorTexto);
        mantida.validada = algumaValidada;
        mantida.codigoIdioma = codigo;
        mantida.situacaoGrupoId = grupo;
        if (original != null && mantida != original) {
            mantida.tipoVersao = "traducao";
            mantida.versaoOrigemId = original.id;
            copiarMetadadosConceituais(original, mantida);
        }

        int removidas = 0;
        for (int p = indices.size() - 1; p >= 0; p--) {
            int idx = indices.get(p);
            if (idx == indiceMantido) continue;
            modelo.removerLinha(idx);
            removidas++;
        }
        if (informarUsuario && removidas > 0 && pai != null) {
            JOptionPane.showMessageDialog(pai,
                    "Foram encontradas versões duplicadas para este idioma.\n"
                    + "Elas foram consolidadas em uma única tradução.",
                    "Consolidação de traduções", JOptionPane.INFORMATION_MESSAGE);
        }
        return removidas;
    }

    private int pontuarTraducao(LinhaSituacao l) {
        return ValidadorTraducaoCurada.pontuarTraducao(l);
    }

    /**
     * Regra simples e independente do idioma: os valores matemáticos conhecidos
     * devem aparecer na tradução como algarismos. Não tenta interpretar números
     * por extenso, evitando regras linguísticas específicas.
     */
    private boolean validarAlgarismosDaTraducao(Component pai, LinhaSituacao original,
            String textoTraduzido, boolean mostrarAviso) {
        if (original == null) return true;
        List<String> ausentes = localizarValoresNumericosAusentes(original, textoTraduzido);
        if (ausentes.isEmpty()) return true;
        if (mostrarAviso && pai != null) {
            StringBuilder detalhes = new StringBuilder();
            for (String item : ausentes) detalhes.append("• ").append(item).append('\n');
            JOptionPane.showMessageDialog(pai,
                    "A tradução não pode ser marcada como validada porque alguns valores curados "
                    + "não aparecem em algarismos no enunciado.\n\n"
                    + detalhes.toString().trim()
                    + "\n\nMantenha esses valores em algarismos, independentemente do idioma.",
                    "Valores numéricos ausentes", JOptionPane.WARNING_MESSAGE);
        }
        return false;
    }

    private List<String> localizarValoresNumericosAusentes(LinhaSituacao original, String texto) {
        return ValidadorTraducaoCurada.localizarValoresNumericosAusentes(original, texto);
    }

    private void adicionarSeAusente(List<String> ausentes, List<BigDecimal> numerosTexto,
            String rotulo, String valor, boolean papelDesconhecido) {
        ValidadorTraducaoCurada.adicionarSeAusente(ausentes, numerosTexto, rotulo, valor, papelDesconhecido);
    }

    private List<BigDecimal> extrairNumeros(String texto) {
        return ValidadorTraducaoCurada.extrairNumeros(texto);
    }

    private BigDecimal converterNumero(String valor) {
        return ValidadorTraducaoCurada.converterNumero(valor);
    }

    private String normalizarPapel(String valor) {
        return ValidadorTraducaoCurada.normalizarPapel(valor);
    }

    private boolean confirmarEstadoValidacaoTraducaoAntesDeInserir(Component pai,
            IdiomaSituacao idioma, JCheckBox campoTraducaoValidada, Runnable atualizarIndicador) {
        if (campoTraducaoValidada.isSelected()) {
            return true;
        }
        String nomeIdioma = idioma == null || idioma.getNome() == null || idioma.getNome().trim().isEmpty()
                ? (idioma == null ? "" : idioma.getCodigo()) : idioma.getNome();
        Object[] opcoes = new Object[] {
            localizacao.texto("curadoria.traducao.validarInserir"),
            localizacao.texto("curadoria.traducao.inserirSemValidar"),
            localizacao.texto("curadoria.traducao.voltar")
        };
        int escolha = JOptionPane.showOptionDialog(pai,
                localizacao.formatar("curadoria.traducao.avisoNaoValidada", nomeIdioma),
                localizacao.texto("curadoria.traducao.tituloValidacao"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opcoes, opcoes[0]);
        if (escolha == 0) {
            campoTraducaoValidada.setSelected(true);
            if (atualizarIndicador != null) {
                atualizarIndicador.run();
            }
            return true;
        }
        return escolha == 1;
    }

    private boolean confirmarValidacaoAntesDeFechar(Component pai, LinhaSituacao atual,
            JCheckBox campoValidada, JLabel indicadorValidacao) {
        List<LinhaSituacao> pendentes = new ArrayList<LinhaSituacao>();
        String grupo = atual.situacaoGrupoId == null ? "" : atual.situacaoGrupoId.trim();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao candidata = modelo.getLinha(i);
            if (candidata == null || Boolean.TRUE.equals(candidata.validada)) {
                continue;
            }
            String grupoCandidata = candidata.situacaoGrupoId == null ? "" : candidata.situacaoGrupoId.trim();
            if (grupo.equals(grupoCandidata)) {
                pendentes.add(candidata);
            }
        }
        if (pendentes.isEmpty()) {
            return true;
        }

        StringBuilder lista = new StringBuilder();
        for (LinhaSituacao pendente : pendentes) {
            if (lista.length() > 0) lista.append("\n");
            IdiomaSituacao idioma = cadastroIdiomas.obter(pendente.codigoIdioma);
            String nomeIdioma = idioma == null ? pendente.codigoIdioma : idioma.getNome();
            lista.append("• ").append(nomeIdioma == null || nomeIdioma.trim().isEmpty()
                    ? "Idioma não informado" : nomeIdioma);
            if (pendente.id != null && !pendente.id.trim().isEmpty()) {
                lista.append(" — ").append(pendente.id);
            }
            if (pendente == atual) lista.append(" (versão atual)");
        }

        boolean atualPendente = !Boolean.TRUE.equals(atual.validada);
        String mensagem = pendentes.size() == 1
                ? "Esta situação-problema ainda não foi validada."
                : "Existem versões linguísticas desta situação-problema que ainda não foram validadas.";
        JTextArea detalhes = new JTextArea(lista.toString(), Math.min(8, pendentes.size() + 1), 58);
        detalhes.setEditable(false);
        detalhes.setOpaque(false);
        detalhes.setLineWrap(true);
        detalhes.setWrapStyleWord(true);
        detalhes.setFont(UnicodeTexto.fonteCompativel(detalhes, Font.PLAIN, 12));

        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.add(new JLabel("<html>" + mensagem + "<br>Revise o estado antes de fechar a curadoria.</html>"), BorderLayout.NORTH);
        painel.add(detalhes, BorderLayout.CENTER);

        if (atualPendente) {
            Object[] opcoes = { "Validar esta versão e fechar", "Fechar sem validar", "Voltar à curadoria" };
            int escolha = JOptionPane.showOptionDialog(pai, painel, "Validação pendente",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opcoes, opcoes[2]);
            if (escolha == 0) {
                atual.validada = Boolean.TRUE;
                campoValidada.setSelected(true);
                indicadorValidacao.setText("✓ Validada");
                indicadorValidacao.setForeground(new Color(46, 125, 50));
                return true;
            }
            if (escolha == 1) {
                return true;
            }
            return false;
        }

        Object[] opcoes = { "Fechar sem validar as traduções", "Voltar à curadoria" };
        int escolha = JOptionPane.showOptionDialog(pai, painel, "Traduções pendentes de validação",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opcoes, opcoes[1]);
        return escolha == 0;
    }

    /**
     * termo_desconhecido é a única forma de marcar qual papel é a incógnita.
     * O curador sempre digita o valor numérico real, em todo campo — nunca
     * "?". Ver skill gerard-consistencia-estado.
     */
    private List<String> papeisComInterrogacaoDigitada(LinhaSituacao linha) {
        List<String> encontrados = new ArrayList<String>();
        if (SimboloDesconhecido.eh(linha.estadoInicial)) encontrados.add("estado_inicial");
        if (SimboloDesconhecido.eh(linha.transformacao)) encontrados.add("transformacao");
        if (SimboloDesconhecido.eh(linha.estadoFinal)) encontrados.add("estado_final");
        if (SimboloDesconhecido.eh(linha.quantidade1)) encontrados.add("quantidade_1");
        if (SimboloDesconhecido.eh(linha.quantidade2)) encontrados.add("quantidade_2");
        if (SimboloDesconhecido.eh(linha.resultado)) encontrados.add("resultado");
        if (SimboloDesconhecido.eh(linha.referido)) encontrados.add("referido");
        if (SimboloDesconhecido.eh(linha.referendo)) encontrados.add("referendo");
        if (SimboloDesconhecido.eh(linha.valorRelativo)) encontrados.add("valor_relativo");
        return encontrados;
    }

    private boolean validarAntesDeSalvarCuradoriaDetalhada(JDialog dialogo, LinhaSituacao linha,
            LinhaSituacao estadoAnterior, int linhaModelo) {
        List<String> papeisComInterrogacao = papeisComInterrogacaoDigitada(linha);
        if (!papeisComInterrogacao.isEmpty()) {
            JOptionPane.showMessageDialog(dialogo,
                    "Estes campos estão com \"?\" em vez de um número: "
                    + juntarMensagens(papeisComInterrogacao)
                    + "\n\nInforme o valor numérico correspondente em cada um. Para marcar qual papel é a "
                    + "incógnita do exercício, use o campo termo_desconhecido — nunca digite \"?\" diretamente "
                    + "no valor.",
                    "Valor numérico esperado", JOptionPane.WARNING_MESSAGE);
            RegistroErrosCuradoria.registrar("VALOR_SEMANTICO_COM_INTERROGACAO",
                    linha.id, linha.situacaoGrupoId,
                    "Campos com ?: " + papeisComInterrogacao, "VOLTAR_E_CORRIGIR");
            return false;
        }
        ResolvedorIncognitaCurada.Resultado resolucaoIncognita =
                resolverIncognitaCurada(linha);
        if (resolucaoIncognita.possuiConflito()
                || resolucaoIncognita.possuiMultiplasInterrogacoes()) {
            JOptionPane.showMessageDialog(dialogo,
                    "Os valores semânticos e o campo termo_desconhecido não estão consistentes.\n\n"
                    + resolucaoIncognita.mensagemInconsistencia()
                    + "\n\nMantenha somente um papel com ? e selecione o mesmo papel em termo_desconhecido.",
                    "Incógnita curada inconsistente", JOptionPane.WARNING_MESSAGE);
            RegistroErrosCuradoria.registrar("INCOGNITA_CURADA_INCONSISTENTE",
                    linha.id, linha.situacaoGrupoId,
                    resolucaoIncognita.mensagemInconsistencia(),
                    "VOLTAR_E_CORRIGIR");
            return false;
        }
        LinhaSituacao originalAtual = "original".equals(linha.tipoVersao)
                ? linha : localizarOriginalDoGrupo(linha.situacaoGrupoId);
        sincronizarGrupoDaOriginalComTraducoes(originalAtual);
        // Arquivos criados por versões anteriores podem conter traduções vinculadas,
        // porém com campos conceituais vazios ou antigos. Como a curadoria original é
        // a fonte de verdade, harmonizamos esses campos antes de validar e salvar.
        harmonizarMetadadosConceituaisComOriginais();
        List<SituacaoProblemaAditiva> situacoes = modelo.paraSituacoes();
        List<String> vinculos = ValidadorVinculosTraducoes.validarVinculosEstruturais(situacoes);
        if (!vinculos.isEmpty()) {
            String detalhes = juntarMensagens(vinculos);
            Object[] opcoes = { "Voltar e corrigir", "Cancelar alterações" };
            int escolha = mostrarAvisoValidacao(dialogo,
                    "Vínculo inválido entre versões",
                    "A curadoria não pode ser salva porque o vínculo entre as versões é inválido.\n"
                    + "Revise os dados indicados abaixo.", detalhes, opcoes);
            String decisao = escolha == 1 ? "CANCELAR_ALTERACOES" : "VOLTAR_E_CORRIGIR";
            RegistroErrosCuradoria.registrar("VINCULO_INVALIDO", linha.id, linha.situacaoGrupoId, detalhes, decisao);
            if (escolha == 1) {
                restaurarLinha(linha, estadoAnterior);
                modelo.atualizarLinha(linhaModelo);
                dialogo.dispose();
            }
            return false;
        }

        List<String> divergencias = ValidadorVinculosTraducoes.validarDivergenciasConceituais(situacoes);
        if (!divergencias.isEmpty()) {
            String detalhes = juntarMensagens(divergencias);
            Object[] opcoes = { "Voltar e corrigir", "Aplicar esta versão às vinculadas",
                "Usar a versão original como referência", "Cancelar alterações" };
            int escolha = mostrarAvisoValidacao(dialogo,
                    "Divergência entre traduções vinculadas",
                    "Foram encontradas diferenças em metadados conceituais de versões que representam\n"
                    + "a mesma situação-problema. O enunciado de cada idioma será preservado.",
                    detalhes, opcoes);
            if (escolha == 1) {
                aplicarMetadadosConceituaisAsVinculadas(linha);
                modelo.fireTableDataChanged();
                RegistroErrosCuradoria.registrar("DIVERGENCIA_CONCEITUAL", linha.id, linha.situacaoGrupoId,
                        detalhes, "APLICAR_VERSAO_ATUAL");
                return ValidadorVinculosTraducoes.validar(modelo.paraSituacoes()).isEmpty();
            }
            if (escolha == 2) {
                LinhaSituacao original = localizarOriginalDoGrupo(linha.situacaoGrupoId);
                if (original == null) {
                    JOptionPane.showMessageDialog(dialogo,
                            "Não foi encontrada uma versão original válida para este grupo.",
                            "Curadoria", JOptionPane.WARNING_MESSAGE);
                    RegistroErrosCuradoria.registrar("DIVERGENCIA_CONCEITUAL", linha.id, linha.situacaoGrupoId,
                            detalhes, "ORIGINAL_NAO_ENCONTRADA");
                    return false;
                }
                aplicarMetadadosConceituaisAsVinculadas(original);
                modelo.fireTableDataChanged();
                RegistroErrosCuradoria.registrar("DIVERGENCIA_CONCEITUAL", linha.id, linha.situacaoGrupoId,
                        detalhes, "USAR_VERSAO_ORIGINAL");
                return ValidadorVinculosTraducoes.validar(modelo.paraSituacoes()).isEmpty();
            }
            if (escolha == 3) {
                restaurarLinha(linha, estadoAnterior);
                modelo.atualizarLinha(linhaModelo);
                dialogo.dispose();
                RegistroErrosCuradoria.registrar("DIVERGENCIA_CONCEITUAL", linha.id, linha.situacaoGrupoId,
                        detalhes, "CANCELAR_ALTERACOES");
                return false;
            }
            RegistroErrosCuradoria.registrar("DIVERGENCIA_CONCEITUAL", linha.id, linha.situacaoGrupoId,
                    detalhes, "VOLTAR_E_CORRIGIR");
            return false;
        }
        return true;
    }

    private int mostrarAvisoValidacao(Component pai, String titulo, String mensagem,
            String detalhes, Object[] opcoes) {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        JLabel cabecalho = new JLabel("<html>" + mensagem.replace("\n", "<br>") + "</html>");
        JTextArea area = new JTextArea(detalhes, Math.min(12, Math.max(5, detalhes.split("\\n").length)), 76);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);
        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(new JScrollPane(area), BorderLayout.CENTER);
        return JOptionPane.showOptionDialog(pai, painel, titulo, JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, opcoes, opcoes[0]);
    }

    private String juntarMensagens(List<String> mensagens) {
        return ValidadorTraducaoCurada.juntarMensagens(mensagens);
    }

    private boolean existeIdiomaNoGrupo(String grupoId, String idioma) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao candidata = modelo.getLinha(i);
            if (candidata != null && grupoId != null && grupoId.equals(candidata.situacaoGrupoId)
                    && idioma.equalsIgnoreCase(candidata.codigoIdioma)) return true;
        }
        return false;
    }

    private LinhaSituacao localizarVersaoDoGrupoPorIdioma(String grupoId, String idioma) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao candidata = modelo.getLinha(i);
            if (candidata != null && grupoId != null && grupoId.equals(candidata.situacaoGrupoId)
                    && idioma.equalsIgnoreCase(candidata.codigoIdioma)) return candidata;
        }
        return null;
    }

    private LinhaSituacao criarTraducaoAPartirDaOriginal(LinhaSituacao original, String idioma, String enunciadoTraduzido) {
        LinhaSituacao traducao = copiarLinha(original);
        traducao.id = gerarIdUnicoTraducao(idioma, original.tipo, original.contexto, enunciadoTraduzido);
        traducao.situacaoGrupoId = original.situacaoGrupoId == null || original.situacaoGrupoId.trim().isEmpty() ? original.id : original.situacaoGrupoId;
        traducao.tipoVersao = "traducao";
        traducao.versaoOrigemId = original.id;
        traducao.codigoIdioma = idioma;
        traducao.enunciado = enunciadoTraduzido;
        traducao.validada = Boolean.FALSE;
        return traducao;
    }

    private String gerarIdUnicoTraducao(String idioma, TipoSituacaoAditiva tipo,
            String contexto, String enunciadoTraduzido) {
        String base = gerarIdPadrao(modelo.getRowCount() + 1, idioma, tipo, contexto, enunciadoTraduzido);
        String candidato = base;
        int sufixo = 2;
        while (localizarLinhaPorId(candidato) != null) {
            candidato = base + "_" + sufixo++;
        }
        return candidato;
    }

    private void sincronizarGrupoDaOriginalComTraducoes(LinhaSituacao original) {
        if (original == null || original.id == null) return;
        String grupo = original.situacaoGrupoId == null ? "" : original.situacaoGrupoId.trim();
        if (grupo.isEmpty()) {
            grupo = original.id.trim();
            original.situacaoGrupoId = grupo;
        }
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao candidata = modelo.getLinha(i);
            if (candidata == null || candidata == original) continue;
            if (original.id.equals(candidata.versaoOrigemId)) {
                candidata.situacaoGrupoId = grupo;
                candidata.tipoVersao = "traducao";
            }
        }
    }

    private void atualizarComboIdiomas(JComboBox<IdiomaSituacao> combo, String selecionarCodigo) {
        combo.removeAllItems();
        for (IdiomaSituacao idioma : cadastroIdiomas.listar()) combo.addItem(idioma);
        if (selecionarCodigo != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                IdiomaSituacao item = combo.getItemAt(i);
                if (item.getCodigo().equalsIgnoreCase(selecionarCodigo)) { combo.setSelectedIndex(i); break; }
            }
        }
    }

    // O cadastro é fechado: o Gérard trabalha apenas com pt-BR, en, fr e es.

    private static class IconeTraducao implements Icon {
        public int getIconWidth() { return 18; }
        public int getIconHeight() { return 18; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(45, 92, 155));
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.drawString("A", x + 1, y + 11);
            g2.drawLine(x + 8, y + 5, x + 16, y + 5);
            g2.drawLine(x + 13, y + 2, x + 16, y + 5);
            g2.drawLine(x + 13, y + 8, x + 16, y + 5);
            g2.drawLine(x + 16, y + 13, x + 8, y + 13);
            g2.drawLine(x + 11, y + 10, x + 8, y + 13);
            g2.drawLine(x + 11, y + 16, x + 8, y + 13);
            g2.dispose();
        }
    }

    private LinhaSituacao localizarOriginalDoGrupo(String grupoId) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao candidata = modelo.getLinha(i);
            if (candidata != null && grupoId != null && grupoId.equals(candidata.situacaoGrupoId)
                    && "original".equals(candidata.tipoVersao)) return candidata;
        }
        return null;
    }

    private void harmonizarMetadadosConceituaisComOriginais() {
        java.util.Map<String, LinhaSituacao> originaisPorGrupo = new java.util.LinkedHashMap<String, LinhaSituacao>();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao candidata = modelo.getLinha(i);
            if (candidata != null && "original".equals(candidata.tipoVersao)
                    && candidata.situacaoGrupoId != null && !candidata.situacaoGrupoId.trim().isEmpty()) {
                originaisPorGrupo.put(candidata.situacaoGrupoId, candidata);
            }
        }
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao destino = modelo.getLinha(i);
            if (destino == null || "original".equals(destino.tipoVersao)) continue;
            LinhaSituacao original = originaisPorGrupo.get(destino.situacaoGrupoId);
            if (original == null && destino.versaoOrigemId != null) {
                LinhaSituacao porId = localizarLinhaPorId(destino.versaoOrigemId);
                if (porId != null && "original".equals(porId.tipoVersao)) {
                    original = porId;
                    destino.situacaoGrupoId = porId.situacaoGrupoId;
                }
            }
            if (original != null) {
                destino.tipoVersao = "traducao";
                destino.versaoOrigemId = original.id;
                copiarMetadadosConceituais(original, destino);
            }
        }
        modelo.fireTableDataChanged();
    }

    private void aplicarMetadadosConceituaisAsVinculadas(LinhaSituacao referencia) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao destino = modelo.getLinha(i);
            if (destino != null && referencia.situacaoGrupoId != null
                    && referencia.situacaoGrupoId.equals(destino.situacaoGrupoId)) {
                copiarMetadadosConceituais(referencia, destino);
            }
        }
    }

    private void copiarMetadadosConceituais(LinhaSituacao origem, LinhaSituacao destino) {
        destino.tipo = origem.tipo;
        destino.subtipo = origem.subtipo;
        destino.personagem1 = origem.personagem1;
        destino.personagem2 = origem.personagem2;
        destino.personagem3 = origem.personagem3;
        destino.estadoInicial = origem.estadoInicial;
        destino.transformacao = origem.transformacao;
        destino.sinalTransformacao = origem.sinalTransformacao;
        destino.estadoFinal = origem.estadoFinal;
        destino.quantidade1 = origem.quantidade1;
        destino.quantidade2 = origem.quantidade2;
        destino.resultado = origem.resultado;
        destino.referido = origem.referido;
        destino.referendo = origem.referendo;
        destino.valorRelativo = origem.valorRelativo;
        destino.sinalValorRelativo = origem.sinalValorRelativo;
        destino.termoDesconhecido = origem.termoDesconhecido;
        destino.representacaoVisual = origem.representacaoVisual;
        destino.observacoes = origem.observacoes;
    }

    private LinhaSituacao copiarLinha(LinhaSituacao origem) {
        LinhaSituacao copia = new LinhaSituacao();
        copia.id = origem.id; copia.situacaoGrupoId = origem.situacaoGrupoId;
        copia.tipoVersao = origem.tipoVersao; copia.versaoOrigemId = origem.versaoOrigemId;
        copia.validada = origem.validada; copia.codigoIdioma = origem.codigoIdioma; copia.tipo = origem.tipo;
        copia.contexto = origem.contexto; copia.enunciado = origem.enunciado; copia.fonte = origem.fonte;
        copia.subtipo = origem.subtipo; copia.personagem1 = origem.personagem1;
        copia.personagem2 = origem.personagem2; copia.personagem3 = origem.personagem3;
        copia.estadoInicial = origem.estadoInicial;
        copia.transformacao = origem.transformacao; copia.sinalTransformacao = origem.sinalTransformacao;
        copia.estadoFinal = origem.estadoFinal; copia.quantidade1 = origem.quantidade1;
        copia.quantidade2 = origem.quantidade2; copia.resultado = origem.resultado;
        copia.referido = origem.referido; copia.referendo = origem.referendo;
        copia.valorRelativo = origem.valorRelativo; copia.sinalValorRelativo = origem.sinalValorRelativo;
        copia.termoDesconhecido = origem.termoDesconhecido;
        copia.representacaoVisual = origem.representacaoVisual; copia.observacoes = origem.observacoes;
        copia.fragmentoTexto1 = origem.fragmentoTexto1; copia.fragmentoTexto2 = origem.fragmentoTexto2;
        copia.fragmentoTexto3 = origem.fragmentoTexto3; copia.fragmentoTexto4 = origem.fragmentoTexto4;
        copia.fragmentoTexto5 = origem.fragmentoTexto5; copia.fragmentoTexto6 = origem.fragmentoTexto6;
        return copia;
    }

    private void restaurarLinha(LinhaSituacao destino, LinhaSituacao origem) {
        destino.id = origem.id; destino.situacaoGrupoId = origem.situacaoGrupoId;
        destino.tipoVersao = origem.tipoVersao; destino.versaoOrigemId = origem.versaoOrigemId;
        destino.validada = origem.validada; destino.codigoIdioma = origem.codigoIdioma; destino.tipo = origem.tipo;
        destino.contexto = origem.contexto; destino.enunciado = origem.enunciado; destino.fonte = origem.fonte;
        destino.subtipo = origem.subtipo; destino.personagem1 = origem.personagem1;
        destino.personagem2 = origem.personagem2; destino.personagem3 = origem.personagem3;
        destino.estadoInicial = origem.estadoInicial;
        destino.transformacao = origem.transformacao; destino.sinalTransformacao = origem.sinalTransformacao;
        destino.estadoFinal = origem.estadoFinal; destino.quantidade1 = origem.quantidade1;
        destino.quantidade2 = origem.quantidade2; destino.resultado = origem.resultado;
        destino.referido = origem.referido; destino.referendo = origem.referendo;
        destino.valorRelativo = origem.valorRelativo; destino.sinalValorRelativo = origem.sinalValorRelativo;
        destino.termoDesconhecido = origem.termoDesconhecido;
        destino.representacaoVisual = origem.representacaoVisual; destino.observacoes = origem.observacoes;
        destino.fragmentoTexto1 = origem.fragmentoTexto1; destino.fragmentoTexto2 = origem.fragmentoTexto2;
        destino.fragmentoTexto3 = origem.fragmentoTexto3; destino.fragmentoTexto4 = origem.fragmentoTexto4;
        destino.fragmentoTexto5 = origem.fragmentoTexto5; destino.fragmentoTexto6 = origem.fragmentoTexto6;
    }

    private void configurarCampoHerdado(JTextField campo, String dica, boolean herdado) {
        if (campo == null) return;
        if (campo.getClientProperty("gerard.tooltipOriginal") == null) {
            campo.putClientProperty("gerard.tooltipOriginal", campo.getToolTipText() == null ? "" : campo.getToolTipText());
        }
        campo.setEditable(!herdado);
        campo.setFocusable(!herdado);
        campo.setBackground(herdado
                ? new Color(238, 241, 244) : UIManager.getColor("TextField.background"));
        campo.setForeground(herdado
                ? new Color(82, 97, 107) : UIManager.getColor("TextField.foreground"));
        Object tooltipOriginal = campo.getClientProperty("gerard.tooltipOriginal");
        campo.setToolTipText(herdado ? dica
                : (tooltipOriginal == null || tooltipOriginal.toString().isEmpty() ? null : tooltipOriginal.toString()));
    }

    private void configurarComboHerdado(JComboBox<?> combo, String dica, boolean herdado) {
        if (combo == null) return;
        if (combo.getClientProperty("gerard.tooltipOriginal") == null) {
            combo.putClientProperty("gerard.tooltipOriginal", combo.getToolTipText() == null ? "" : combo.getToolTipText());
        }
        combo.setEnabled(!herdado);
        Object tooltipOriginal = combo.getClientProperty("gerard.tooltipOriginal");
        combo.setToolTipText(herdado ? dica
                : (tooltipOriginal == null || tooltipOriginal.toString().isEmpty() ? null : tooltipOriginal.toString()));
    }

    private JTextField campoTexto(String valor) {
        JTextField campo = new JTextField(valor == null ? "" : valor);
        campo.setFont(UnicodeTexto.fonteCompativel(campo, Font.PLAIN, 12));
        return campo;
    }

    private JComboBox<String> comboTipoVersao(String valorAtual) {
        String[] opcoes = new String[] { "original", "traducao" };
        JComboBox<String> combo = new JComboBox<String>(opcoes);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        String valor = valorAtual == null ? "" : valorAtual.trim();
        if (valor.length() > 0 && !"original".equals(valor) && !"traducao".equals(valor)) {
            combo.addItem(valor);
        }
        combo.setSelectedItem(valor.length() == 0 ? "original" : valor);
        return combo;
    }

    private JComboBox<String> comboVersoesOriginais(String valorAtual, String idAtual) {
        JComboBox<String> combo = new JComboBox<String>();
        combo.addItem("");
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao candidata = modelo.getLinha(i);
            if (candidata == null || candidata.id == null || candidata.id.equals(idAtual)) continue;
            if ("original".equals(candidata.tipoVersao)) combo.addItem(candidata.id);
        }
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setSelectedItem(valorAtual == null ? "" : valorAtual.trim());
        return combo;
    }

    private void configurarVinculoRelacional(final JComboBox<String> tipo, final JComboBox<String> origem,
                                               final JTextField grupo, final JTextField id) {
        Runnable atualizar = new Runnable() {
            public void run() {
                boolean traducao = "traducao".equals(String.valueOf(tipo.getSelectedItem()));
                origem.setEnabled(traducao);
                if (!traducao) {
                    origem.setSelectedItem("");
                    // Preserve an existing conceptual group. Replacing it with the
                    // version id would orphan translations already linked to the group.
                    if (grupo.getText().trim().length() == 0 && id.getText().trim().length() > 0) {
                        grupo.setText(id.getText().trim());
                    }
                } else {
                    String origemId = String.valueOf(origem.getSelectedItem());
                    LinhaSituacao original = localizarLinhaPorId(origemId);
                    grupo.setText(original == null ? "" : original.situacaoGrupoId);
                }
            }
        };
        tipo.addActionListener(e -> atualizar.run());
        origem.addActionListener(e -> atualizar.run());
        id.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { atualizar.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { atualizar.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { atualizar.run(); }
        });
        atualizar.run();
    }

    private LinhaSituacao localizarLinhaPorId(String id) {
        if (id == null) return null;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            LinhaSituacao linha = modelo.getLinha(i);
            if (linha != null && id.equals(linha.id)) return linha;
        }
        return null;
    }

    private JComboBox<String> comboTermoDesconhecido(TipoSituacaoAditiva tipo, String valorAtual) {
        String[] opcoes;
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            opcoes = new String[] { "", "parte_1", "parte_2", "todo" };
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            opcoes = new String[] { "", "estado_inicial", "transformação", "estado_final" };
        } else if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS) {
            opcoes = new String[] { "", "parte_1", "parte_2", "todo", "estado_inicial", "transformação", "estado_final" };
        } else if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            opcoes = new String[] { "", "referendo", "valor_relativo", "referido" };
        } else if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            opcoes = new String[] { "", "transformacao_1", "transformacao_2", "transformacao_resultante" };
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS) {
            opcoes = new String[] { "", "estado_inicial", "transformacao_1", "transformacao_2", "estado_final" };
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            opcoes = new String[] { "", "relacao_inicial", "transformação", "relacao_final" };
        } else if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            opcoes = new String[] { "", "relacao_1", "relacao_2", "relacao_resultante" };
        } else {
            opcoes = new String[] { "" };
        }
        JComboBox<String> combo = new JComboBox<String>(opcoes);
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        String valor = valorAtual == null ? "" : valorAtual.trim();
        boolean encontrado = false;
        for (String opcao : opcoes) {
            if (normalizarPapel(opcao).equals(normalizarPapel(valor))) {
                valor = opcao;
                encontrado = true;
                break;
            }
        }
        if (!encontrado && valor.length() > 0) combo.addItem(valor);
        combo.setSelectedItem(valor);
        return combo;
    }

    private int adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, Component componente) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel label = new JLabel(rotulo);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        painel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(componente, gbc);
        return linha + 1;
    }

    private void aplicarCamposDaCuradoriaDetalhada(LinhaSituacao linha, JCheckBox campoValidada, JTextArea areaEnunciado,
            JTextField campoId, JComboBox<IdiomaSituacao> campoIdiomaVersao, JTextField campoSituacaoGrupoId, JComboBox<String> campoTipoVersao, JComboBox<String> campoVersaoOrigemId,
            JTextField campoFonte, JTextField campoSubtipo, JTextField campoPersonagem1, JTextField campoPersonagem2, JTextField campoPersonagem3,
            JTextField campoEstadoInicial, JTextField campoTransformacao, JTextField campoEstadoFinal, JTextField campoQuantidade1, JTextField campoQuantidade2,
            JTextField campoResultado, JTextField campoReferido, JTextField campoReferendo, JTextField campoValorRelativo,
            ControladorSinaisCuradoria controladorSinais, JComboBox<String> campoTermoDesconhecido, JTextField campoRepresentacao, JTextField campoObservacoes,
            JTextField campoFragmentoTexto1, JTextField campoFragmentoTexto2, JTextField campoFragmentoTexto3,
            JTextField campoFragmentoTexto4, JTextField campoFragmentoTexto5, JTextField campoFragmentoTexto6) {
        linha.validada = campoValidada.isSelected();
        linha.enunciado = UnicodeTexto.normalizarNfc(areaEnunciado.getText() == null ? "" : areaEnunciado.getText().trim());
        linha.id = campoId.getText().trim();
        IdiomaSituacao idiomaVersaoSelecionado = (IdiomaSituacao) campoIdiomaVersao.getSelectedItem();
        linha.codigoIdioma = idiomaVersaoSelecionado == null ? "und" : idiomaVersaoSelecionado.getCodigo();
        linha.situacaoGrupoId = campoSituacaoGrupoId.getText().trim();
        Object tipoVersaoSelecionado = campoTipoVersao.getSelectedItem();
        linha.tipoVersao = tipoVersaoSelecionado == null ? "" : tipoVersaoSelecionado.toString().trim();
        Object origemSelecionada = campoVersaoOrigemId.getSelectedItem();
        linha.versaoOrigemId = origemSelecionada == null ? "" : origemSelecionada.toString().trim();
        if ("original".equals(linha.tipoVersao)) {
            // A própria interface já limpa a seleção ao escolher "original".
            // Esta segunda proteção cobre alterações programáticas ou estados
            // legados sem apresentar uma mensagem desnecessária ao usuário.
            linha.versaoOrigemId = "";
        }
        linha.fonte = campoFonte.getText().trim();
        boolean versaoTraduzida = "traducao".equalsIgnoreCase(linha.tipoVersao);
        if (versaoTraduzida) {
            LinhaSituacao original = localizarOriginalDoGrupo(linha.situacaoGrupoId);
            if (original == null && linha.versaoOrigemId != null) {
                LinhaSituacao candidata = localizarLinhaPorId(linha.versaoOrigemId);
                if (candidata != null && "original".equalsIgnoreCase(candidata.tipoVersao)) {
                    original = candidata;
                }
            }
            if (original != null) {
                linha.versaoOrigemId = original.id;
                linha.situacaoGrupoId = original.situacaoGrupoId;
                copiarMetadadosConceituais(original, linha);
            }
        } else {
            linha.subtipo = campoSubtipo.getText().trim();
            linha.personagem1 = UnicodeTexto.normalizarNfc(campoPersonagem1.getText().trim());
            linha.personagem2 = UnicodeTexto.normalizarNfc(campoPersonagem2.getText().trim());
            linha.personagem3 = UnicodeTexto.normalizarNfc(campoPersonagem3.getText().trim());
            linha.estadoInicial = campoEstadoInicial.getText().trim();
            linha.transformacao = controladorSinais.obterValorParaPersistencia(
                    PapelSinalCuradoria.TRANSFORMACAO,
                    campoTransformacao.getText());
            linha.sinalTransformacao = controladorSinais.obterSinalCanonico(
                    PapelSinalCuradoria.TRANSFORMACAO);
            linha.estadoFinal = campoEstadoFinal.getText().trim();
            linha.quantidade1 = controladorSinais.obterValorParaPersistencia(
                    PapelSinalCuradoria.TRANSFORMACAO_1,
                    campoQuantidade1.getText());
            linha.quantidade2 = controladorSinais.obterValorParaPersistencia(
                    PapelSinalCuradoria.TRANSFORMACAO_2,
                    campoQuantidade2.getText());
            linha.resultado = controladorSinais.obterValorParaPersistencia(
                    PapelSinalCuradoria.TRANSFORMACAO_RESULTANTE,
                    campoResultado.getText());
            linha.referido = campoReferido.getText().trim();
            linha.referendo = campoReferendo.getText().trim();
            linha.valorRelativo = controladorSinais.obterValorParaPersistencia(
                    PapelSinalCuradoria.VALOR_RELATIVO,
                    campoValorRelativo.getText());
            linha.sinalValorRelativo = controladorSinais.obterSinalCanonico(
                    PapelSinalCuradoria.VALOR_RELATIVO);
            Object termoSelecionado = campoTermoDesconhecido.getSelectedItem();
            linha.termoDesconhecido = termoSelecionado == null ? "" : termoSelecionado.toString().trim();
            limparCamposSemanticosNaoAplicaveis(linha);
            harmonizarTermoDesconhecidoComValores(linha);
            linha.representacaoVisual = campoRepresentacao.getText().trim();
            linha.observacoes = campoObservacoes.getText().trim();
            linha.fragmentoTexto1 = campoFragmentoTexto1.getText().trim();
            linha.fragmentoTexto2 = campoFragmentoTexto2.getText().trim();
            linha.fragmentoTexto3 = campoFragmentoTexto3.getText().trim();
            linha.fragmentoTexto4 = campoFragmentoTexto4.getText().trim();
            linha.fragmentoTexto5 = campoFragmentoTexto5.getText().trim();
            linha.fragmentoTexto6 = campoFragmentoTexto6.getText().trim();
        }
        if (linha.id == null || linha.id.trim().isEmpty()) {
            linha.id = gerarIdPadrao(linhaModeloSeguro(linha), linha.codigoIdioma, linha.tipo, linha.contexto, linha.enunciado);
        }
        if (linha.situacaoGrupoId == null || linha.situacaoGrupoId.trim().isEmpty()) {
            linha.situacaoGrupoId = linha.id;
        }
        if (linha.tipoVersao == null || linha.tipoVersao.trim().isEmpty()) {
            linha.tipoVersao = "original";
        }
    }


    private void limparCamposSemanticosNaoAplicaveis(LinhaSituacao linha) {
        if (linha == null || linha.tipo == null) return;
        TipoSituacaoAditiva t = linha.tipo;
        if (t != TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                && t != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS
                && t != TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            linha.transformacao = "";
            linha.sinalTransformacao = "";
        }
        if (t != TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                && t != TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                && t != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS) {
            linha.estadoInicial = t == TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS ? linha.estadoInicial : "";
        }
        if (t != TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                && t != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS
                && t != TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            linha.estadoFinal = "";
        }
        boolean usaQuantidades = t == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                || t == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS
                || t == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES
                || t == TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS
                || t == TipoSituacaoAditiva.COMPOSICAO_RELACOES;
        if (!usaQuantidades) {
            linha.quantidade1 = "";
            linha.quantidade2 = "";
            linha.resultado = "";
        }
        if (t != TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            linha.referido = "";
            linha.referendo = "";
            linha.valorRelativo = "";
            linha.sinalValorRelativo = "";
        }
    }

    private ResolvedorIncognitaCurada.Resultado resolverIncognitaCurada(
            LinhaSituacao linha) {
        if (linha == null) {
            return new ResolvedorIncognitaCurada().resolver(null);
        }
        return new ResolvedorIncognitaCurada().resolver(
                linha.tipo, linha.termoDesconhecido,
                linha.estadoInicial, linha.transformacao, linha.estadoFinal,
                linha.quantidade1, linha.quantidade2, linha.resultado,
                linha.referido, linha.referendo, linha.valorRelativo);
    }

    /**
     * Quando há exatamente um campo curado com "?" e o combo ainda está vazio,
     * registra o papel correspondente em termo_desconhecido. Não infere pelo
     * enunciado e não sobrescreve uma escolha explícita divergente.
     */
    private void harmonizarTermoDesconhecidoComValores(LinhaSituacao linha) {
        if (linha == null || (linha.termoDesconhecido != null
                && !linha.termoDesconhecido.trim().isEmpty())) {
            return;
        }
        ResolvedorIncognitaCurada.Resultado resolucao =
                resolverIncognitaCurada(linha);
        if (resolucao.possuiUmaUnicaInterrogacao()
                && !resolucao.possuiConflito()) {
            linha.termoDesconhecido =
                    resolucao.getTermoCuradoriaEfetivo();
        }
    }

    private int linhaModeloSeguro(LinhaSituacao alvo) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (modelo.getLinha(i) == alvo) {
                return i + 1;
            }
        }
        return 1;
    }

    private void salvarEAplicar() {
        try {
            salvarCuradoriaSemMensagem();
            JOptionPane.showMessageDialog(this,
                    "Metadados curados salvos e aplicados.\nArquivo: " + RepositorioSituacoesAditivas.obterArquivoCuradoriaUsuario().getAbsolutePath(),
                    "Curadoria salva", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar curadoria: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarCuradoriaSemMensagem() throws Exception {
        List<SituacaoProblemaAditiva> situacoes = modelo.paraSituacoes();
        ValidadorVinculosTraducoes.validarOuFalhar(situacoes);
        RepositorioSituacoesAditivas.salvarCuradoria(situacoes);
        if (aoSalvarCuradoria != null) {
            aoSalvarCuradoria.run();
        }
        atualizarStatus();
    }

    private void validarSelecionadas() {
        int[] linhas = tabela.getSelectedRows();
        if (linhas.length == 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma ou mais situações para validar.",
                    "Curadoria", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (int linhaView : linhas) {
            int linha = tabela.convertRowIndexToModel(linhaView);
            modelo.setValueAt(Boolean.TRUE, linha, 2);
        }
        atualizarStatus();
    }

    private void recarregar() {
        repositorio.recarregar();
        modelo.substituir(repositorio.listarTodas());
        atualizarStatus();
    }

    private void novaLinha() {
        modelo.adicionarLinha();
        int ultima = modelo.getRowCount() - 1;
        tabela.getSelectionModel().setSelectionInterval(ultima, ultima);
        tabela.scrollRectToVisible(tabela.getCellRect(ultima, 0, true));
    }

    private void removerLinhasSelecionadas() {
        int[] linhas = tabela.getSelectedRows();
        if (linhas.length == 0) {
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Remover as linhas selecionadas da curadoria?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        for (int i = linhas.length - 1; i >= 0; i--) {
            modelo.removerLinha(tabela.convertRowIndexToModel(linhas[i]));
        }
        atualizarStatus();
    }

    private void gerarAuditoria() {
        try {
            File diretorio = RepositorioSituacoesAditivas.obterDiretorioCuradoriaUsuario();
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }
            File arquivo = RepositorioSituacoesAditivas.obterArquivoAuditoriaUsuario();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {
                bw.write("# auditoria_gerada_em\t" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                bw.newLine();
                bw.write("# id\tvalidada\tidioma\ttipo_curado\tsugestao_automatica\tstatus\tenunciado");
                bw.newLine();
                for (SituacaoProblemaAditiva s : modelo.paraSituacoes()) {
                    TipoSituacaoAditiva sugestao = classificador.corrigirTipo(s.getTipo(), s.getEnunciado());
                    String statusLinha = sugestao == s.getTipo() ? "OK" : "REVISAR_SUGESTAO_NAO_AUTORITATIVA";
                    bw.write(campo(s.getId()) + "\t" + s.isValidada() + "\t" + campo(s.getCodigoIdioma()) + "\t" + campo(s.getTipo().name())
                            + "\t" + campo(sugestao.name()) + "\t" + statusLinha + "\t" + campo(s.getEnunciado()));
                    bw.newLine();
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Auditoria gerada. A sugestão automática é apenas indicativa e não altera os metadados curados.\nArquivo: " + arquivo.getAbsolutePath(),
                    "Auditoria", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar auditoria: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarStatus() {
        int total = modelo.getRowCount();
        int validadas = modelo.contarValidadas();
        File arquivo = RepositorioSituacoesAditivas.obterArquivoCuradoriaUsuario();
        status.setText("Situações: " + total + " | Validadas: " + validadas + " | Arquivo curado: " + arquivo.getAbsolutePath());
    }

    private static String campo(String valor) {
        return ValidadorTraducaoCurada.campo(valor);
    }

    private static String gerarIdPadrao(int linha, String codigoIdioma, TipoSituacaoAditiva tipo, String contexto, String enunciado) {
        return ValidadorTraducaoCurada.gerarIdPadrao(linha, codigoIdioma, tipo, contexto, enunciado);
    }

    private static String normalizar(String texto) {
        return ValidadorTraducaoCurada.normalizar(texto);
    }

    static class LinhaSituacao {
        String id;
        String situacaoGrupoId;
        String tipoVersao;
        String versaoOrigemId;
        Boolean validada;
        String codigoIdioma;
        TipoSituacaoAditiva tipo;
        String contexto;
        String enunciado;
        String fonte;
        String subtipo;
        String personagem1;
        String personagem2;
        String personagem3;
        String estadoInicial;
        String transformacao;
        String sinalTransformacao;
        String estadoFinal;
        String quantidade1;
        String quantidade2;
        String resultado;
        String referido;
        String referendo;
        String valorRelativo;
        String sinalValorRelativo;
        String termoDesconhecido;
        String representacaoVisual;
        String observacoes;
        String fragmentoTexto1;
        String fragmentoTexto2;
        String fragmentoTexto3;
        String fragmentoTexto4;
        String fragmentoTexto5;
        String fragmentoTexto6;
    }

    static class ModeloTabelaSituacoes extends AbstractTableModel {
        private final String[] colunas = new String[] {
            "categoria_vergnaud", "enunciado", "validada", "idioma", "contexto"
        };
        private final List<LinhaSituacao> linhas = new ArrayList<LinhaSituacao>();

        ModeloTabelaSituacoes(List<SituacaoProblemaAditiva> situacoes) {
            substituir(situacoes);
        }

        void substituir(List<SituacaoProblemaAditiva> situacoes) {
            linhas.clear();
            if (situacoes != null) {
                int i = 1;
                for (SituacaoProblemaAditiva s : situacoes) {
                    LinhaSituacao l = new LinhaSituacao();
                    l.id = s.getId() == null || s.getId().trim().isEmpty()
                            ? gerarIdPadrao(i, s.getCodigoIdioma(), s.getTipo(), s.getContexto(), s.getEnunciado())
                            : s.getId();
                    l.situacaoGrupoId = s.getSituacaoGrupoId();
                    l.tipoVersao = s.getTipoVersao();
                    l.versaoOrigemId = s.getVersaoOrigemId();
                    l.validada = s.isValidada();
                    l.codigoIdioma = s.getCodigoIdioma();
                    l.tipo = s.getTipo();
                    l.contexto = s.getContexto();
                    l.enunciado = s.getEnunciado();
                    l.fonte = s.getFonte();
                    l.subtipo = s.getSubtipo();
                    l.personagem1 = s.getPersonagem1();
                    l.personagem2 = s.getPersonagem2();
                    l.personagem3 = s.getPersonagem3();
                    l.estadoInicial = s.getEstadoInicial();
                    l.transformacao = s.getTransformacao();
                    l.sinalTransformacao = s.getSinalTransformacao();
                    l.estadoFinal = s.getEstadoFinal();
                    l.quantidade1 = s.getQuantidade1();
                    l.quantidade2 = s.getQuantidade2();
                    l.resultado = s.getResultado();
                    l.referido = s.getReferido();
                    l.referendo = s.getReferendo();
                    l.valorRelativo = s.getValorRelativo();
                    l.sinalValorRelativo = s.getSinalValorRelativo();
                    l.termoDesconhecido = s.getTermoDesconhecido();
                    l.representacaoVisual = s.getRepresentacaoVisual();
                    l.observacoes = s.getObservacoes();
                    l.fragmentoTexto1 = s.getFragmentoTexto1();
                    l.fragmentoTexto2 = s.getFragmentoTexto2();
                    l.fragmentoTexto3 = s.getFragmentoTexto3();
                    l.fragmentoTexto4 = s.getFragmentoTexto4();
                    l.fragmentoTexto5 = s.getFragmentoTexto5();
                    l.fragmentoTexto6 = s.getFragmentoTexto6();
                    linhas.add(l);
                    i++;
                }
            }
            fireTableDataChanged();
        }

        void adicionarLinha() {
            LinhaSituacao l = new LinhaSituacao();
            l.id = "nova_situacao_" + (linhas.size() + 1);
            l.situacaoGrupoId = l.id;
            l.tipoVersao = "original";
            l.versaoOrigemId = "";
            l.validada = Boolean.FALSE;
            l.codigoIdioma = "pt-BR";
            l.tipo = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
            l.contexto = "";
            l.enunciado = "";
            l.fonte = "curadoria";
            l.subtipo = "";
            l.personagem1 = "";
            l.personagem2 = "";
            l.personagem3 = "";
            l.estadoInicial = "";
            l.transformacao = "";
            l.sinalTransformacao = "";
            l.estadoFinal = "";
            l.quantidade1 = "";
            l.quantidade2 = "";
            l.resultado = "";
            l.referido = "";
            l.referendo = "";
            l.valorRelativo = "";
            l.sinalValorRelativo = "";
            l.termoDesconhecido = "";
            l.representacaoVisual = "";
            l.observacoes = "";
            l.fragmentoTexto1 = "";
            l.fragmentoTexto2 = "";
            l.fragmentoTexto3 = "";
            l.fragmentoTexto4 = "";
            l.fragmentoTexto5 = "";
            l.fragmentoTexto6 = "";
            linhas.add(l);
            int i = linhas.size() - 1;
            fireTableRowsInserted(i, i);
        }

        void adicionarLinha(LinhaSituacao linha) {
            if (linha == null) return;
            linhas.add(linha);
            int i = linhas.size() - 1;
            fireTableRowsInserted(i, i);
        }

        void removerLinha(int linha) {
            if (linha >= 0 && linha < linhas.size()) {
                linhas.remove(linha);
                fireTableRowsDeleted(linha, linha);
            }
        }

        LinhaSituacao getLinha(int linha) {
            return linhas.get(linha);
        }

        void atualizarLinha(int linha) {
            if (linha >= 0 && linha < linhas.size()) {
                fireTableRowsUpdated(linha, linha);
            }
        }

        int contarValidadas() {
            int total = 0;
            for (LinhaSituacao l : linhas) {
                if (Boolean.TRUE.equals(l.validada)) {
                    total++;
                }
            }
            return total;
        }

        List<SituacaoProblemaAditiva> paraSituacoes() {
            List<SituacaoProblemaAditiva> situacoes = new ArrayList<SituacaoProblemaAditiva>();
            int i = 1;
            for (LinhaSituacao l : linhas) {
                String id = l.id == null || l.id.trim().isEmpty()
                        ? gerarIdPadrao(i, l.codigoIdioma, l.tipo, l.contexto, l.enunciado)
                        : l.id.trim();
                situacoes.add(new SituacaoProblemaAditiva(id, l.situacaoGrupoId, l.tipoVersao, l.versaoOrigemId, Boolean.TRUE.equals(l.validada), l.tipo, l.codigoIdioma, l.enunciado,
                        l.contexto, l.fonte, l.subtipo, l.estadoInicial, l.transformacao, l.sinalTransformacao, l.estadoFinal,
                        l.quantidade1, l.quantidade2, l.resultado, l.referido, l.referendo, l.valorRelativo, l.sinalValorRelativo,
                        l.termoDesconhecido, l.representacaoVisual, l.observacoes,
                        l.personagem1, l.personagem2, l.personagem3,
                        l.fragmentoTexto1, l.fragmentoTexto2, l.fragmentoTexto3,
                        l.fragmentoTexto4, l.fragmentoTexto5, l.fragmentoTexto6));
                i++;
            }
            return situacoes;
        }

        public int getRowCount() { return linhas.size(); }
        public int getColumnCount() { return colunas.length; }
        public String getColumnName(int column) { return colunas[column]; }
        public boolean isCellEditable(int rowIndex, int columnIndex) { return columnIndex != 1 && columnIndex != 3; }
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return TipoSituacaoAditiva.class;
            if (columnIndex == 2) return Boolean.class;
            if (columnIndex == 3) return String.class;
            return String.class;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            LinhaSituacao l = linhas.get(rowIndex);
            switch (columnIndex) {
                case 0: return l.tipo;
                case 1: return l.enunciado;
                case 2: return l.validada;
                case 3: return l.codigoIdioma;
                case 4: return l.contexto;
                default: return "";
            }
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            LinhaSituacao l = linhas.get(rowIndex);
            switch (columnIndex) {
                case 0: l.tipo = aValue instanceof TipoSituacaoAditiva ? (TipoSituacaoAditiva) aValue : TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS; break;
                case 1: l.enunciado = str(aValue); break;
                case 2: l.validada = Boolean.TRUE.equals(aValue); break;
                case 3: l.codigoIdioma = str(aValue); break;
                case 4: l.contexto = str(aValue); break;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        private String str(Object valor) {
            return valor == null ? "" : valor.toString();
        }
    }

    static class RendererCuradoria extends DefaultTableCellRenderer {
        private final Color fundoValidado = new Color(236, 253, 245);
        private final Color fundoNaoValidado = new Color(255, 251, 235);
        private final Color texto = new Color(31, 41, 51);

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int colModelo = table.convertColumnIndexToModel(column);
            Object validada = table.getValueAt(row, 2);
            if (!isSelected) {
                c.setBackground(Boolean.TRUE.equals(validada) ? fundoValidado : fundoNaoValidado);
                c.setForeground(colModelo == 0 ? corCategoria(value) : texto);
            } else if (colModelo == 0) {
                c.setForeground(Color.WHITE);
            }
            if (colModelo == 0) {
                setFont(getFont().deriveFont(Font.BOLD));
                setToolTipText("Categoria validada: " + (value == null ? "" : value.toString()));
            } else {
                setFont(getFont().deriveFont(Font.PLAIN));
                setToolTipText(colModelo == 1 && value != null ? value.toString() : null);
            }
            return c;
        }

        private Color corCategoria(Object valor) {
            if (!(valor instanceof TipoSituacaoAditiva)) {
                return new Color(55, 65, 81);
            }
            TipoSituacaoAditiva tipo = (TipoSituacaoAditiva) valor;
            switch (tipo) {
                case COMPOSICAO_MEDIDAS:
                    return new Color(15, 118, 110);
                case TRANSFORMACAO_MEDIDAS:
                    return new Color(37, 99, 235);
                case COMPARACAO_MEDIDAS:
                    return new Color(147, 51, 234);
                case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
                    return new Color(14, 116, 144);
                case COMPOSICAO_TRANSFORMACOES:
                    return new Color(194, 65, 12);
                case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                    return new Color(202, 138, 4);
                case TRANSFORMACAO_RELACAO:
                    return new Color(190, 18, 60);
                case COMPOSICAO_RELACOES:
                    return new Color(77, 124, 15);
                default:
                    return new Color(55, 65, 81);
            }
        }
    }
}
