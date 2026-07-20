package gerard.pesquisador.tentativa;

import gerard.i18n.ServicoLocalizacao;
import gerard.pesquisador.log.LoggerInteracaoGerard;
import gerard.ui.UITemaGerard;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/** Janela opcional de explicitação da modelagem. Não altera o diagrama. */
public final class TelaArtefatoExplicativo extends JDialog {
    private static final Color COR_FUNDO = UITemaGerard.COR_FUNDO;
    private static final Color COR_SUPERFICIE = UITemaGerard.COR_SUPERFICIE;
    private static final Color COR_SUPERFICIE_SUAVE = UITemaGerard.COR_SUPERFICIE_SUAVE;
    private static final Color COR_PRIMARIA = UITemaGerard.COR_PRIMARIA;
    private static final Color COR_PRIMARIA_ESCURA = UITemaGerard.COR_PRIMARIA_ESCURA;
    private static final Color COR_TEXTO = UITemaGerard.COR_TEXTO;
    private static final Color COR_TEXTO_SECUNDARIO = UITemaGerard.COR_TEXTO_SECUNDARIO;
    private static final Color COR_BORDA = UITemaGerard.COR_BORDA;
    private static final Color COR_DESTAQUE = UITemaGerard.COR_DESTAQUE;
    private static final Color COR_CAMPO_DESABILITADO = UITemaGerard.COR_CAMPO_DESABILITADO;

    private final ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
    private final List<LinhaResposta> linhas = new ArrayList<LinhaResposta>();
    private final JTextArea explicacaoGeral = new JTextArea(3, 60);
    private final JLabel contadorExplicacaoGeral = new JLabel("0/500");
    private final JComboBox<InvarianteItem> invarianteCatalogo = new JComboBox<InvarianteItem>();
    private final JCheckBox inserirNovaForma = new JCheckBox();
    private final JComboBox<String> simbolos = new JComboBox<String>(new String[] {
        "A", "B", "I", "F", "T", "M1", "M2", "M₁", "M₂", "R", "n", "Todo", "Parte₁", "Parte₂",
        "Card(", ")", "=", "+", "−", "∪", "∩", "∅", "∈", "ℤ", "T⁻¹", "desde que", "Se", "então"
    });
    private final JTextField formaConstruida = new JTextField();
    private final JTextArea observacaoInvariante = new JTextArea(2, 60);
    private final List<String> blocosForma = new ArrayList<String>();
    private boolean salvo;

    private TelaArtefatoExplicativo(Window owner, List<ItemExplicacaoModelagem> itens,
            final ArtefatoContexto contexto) {
        super(owner, loc().texto("analise.title"), ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(COR_FUNDO);
        int quantidadeElementosSemanticos = itens == null ? 0 : itens.size();
        // A quantidade de linhas é calculada antes da montagem. Categorias com mais
        // elementos recebem todas as linhas e a rolagem absorve o crescimento sem cortes.
        int alturaInicial = Math.min(820, Math.max(650, 500 + quantidadeElementosSemanticos * 82));
        setMinimumSize(new Dimension(1020, alturaInicial));

        JPanel cabecalho = new JPanel(new GridBagLayout());
        cabecalho.setBackground(COR_SUPERFICIE);
        GridBagConstraints h = new GridBagConstraints();
        h.gridx = 0; h.gridy = 0; h.weightx = 1; h.fill = GridBagConstraints.HORIZONTAL;
        h.insets = new Insets(10, 14, 3, 14);
        JLabel situacao = new JLabel("<html><b>" + loc.texto("analise.situation") + ":</b> "
                + html(contexto.enunciado) + "</html>");
        situacao.setForeground(COR_TEXTO);
        cabecalho.add(situacao, h);
        h.gridy++;
        JLabel metadados = new JLabel("<html><b>" + loc.texto("analise.category") + ":</b> "
                + html(contexto.categoria) + " &nbsp;&nbsp; <b>" + loc.texto("analise.attempt") + ":</b> "
                + html(String.valueOf(contexto.tentativaNumero)) + " &nbsp;&nbsp; <b>" + loc.texto("analise.language") + ":</b> "
                + html(contexto.idioma) + "</html>");
        metadados.setForeground(COR_TEXTO);
        cabecalho.add(metadados, h);
        h.gridy++; h.insets = new Insets(7, 14, 9, 14);
        JLabel instrucao = new JLabel("<html>" + loc.texto("analise.instruction") + "</html>");
        instrucao.setForeground(COR_TEXTO_SECUNDARIO);
        cabecalho.add(instrucao, h);
        add(cabecalho, BorderLayout.NORTH);

        // O cabeçalho permanece fora da rolagem. A borda inferior reforçada
        // comunica visualmente a separação entre o contexto fixo da tentativa
        // e o conteúdo rolável, sem competir com os quadros internos.
        cabecalho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, COR_PRIMARIA),
                BorderFactory.createEmptyBorder(2, 2, 4, 2)));

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(COR_FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tarefa matemática: quadro delimitado, com tabela visualmente separada.
        JPanel matematica = new JPanel(new GridBagLayout());
        matematica.setBackground(COR_SUPERFICIE);
        matematica.setBorder(bordaSecao(loc.texto("analise.mathTask")));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 5, 4, 5);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 4; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        JLabel ajudaMatematica = new JLabel("<html>" + loc.texto("analise.mathTask.help") + "</html>");
        ajudaMatematica.setForeground(COR_TEXTO);
        matematica.add(ajudaMatematica, c);

        c.gridy++; c.gridwidth = 1;
        adicionarCabecalhoTabela(matematica, c, 0, loc.texto("analise.element"));
        adicionarCabecalhoTabela(matematica, c, 1, loc.texto("analise.semanticRole"));
        adicionarCabecalhoTabela(matematica, c, 2, loc.texto("analise.difficultyAndReason"));

        if (itens != null) for (ItemExplicacaoModelagem item : itens) {
            c.gridy++;
            LinhaResposta linha = new LinhaResposta(item);
            linhas.add(linha);
            c.gridx = 0; c.weightx = 0; c.fill = GridBagConstraints.BOTH;
            linha.elemento.setHorizontalAlignment(JLabel.CENTER);
            linha.elemento.setOpaque(true);
            linha.elemento.setBackground(COR_SUPERFICIE);
            linha.elemento.setForeground(COR_TEXTO);
            linha.elemento.setBorder(bordaCelula());
            matematica.add(linha.elemento, c);
            c.gridx = 1;
            linha.papel.setOpaque(true);
            linha.papel.setBackground(COR_SUPERFICIE_SUAVE);
            linha.papel.setForeground(COR_TEXTO);
            linha.papel.setBorder(bordaCelula());
            matematica.add(linha.papel, c);
            c.gridx = 2; c.weightx = 1;
            linha.respostaCompacta.setBackground(COR_SUPERFICIE);
            linha.respostaCompacta.setBorder(bordaCelula());
            matematica.add(linha.respostaCompacta, c);
        }

        c.gridy++; c.gridx = 0; c.gridwidth = 3; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        JPanel cabecalhoGeral = new JPanel(new BorderLayout());
        cabecalhoGeral.setBackground(COR_SUPERFICIE);
        cabecalhoGeral.add(rotulo(loc.texto("analise.general")), BorderLayout.WEST);
        cabecalhoGeral.add(contadorExplicacaoGeral, BorderLayout.EAST);
        matematica.add(cabecalhoGeral, c);
        c.gridy++; c.fill = GridBagConstraints.BOTH; c.weighty = 0;
        explicacaoGeral.setLineWrap(true);
        explicacaoGeral.setWrapStyleWord(true);
        contadorExplicacaoGeral.setForeground(COR_TEXTO_SECUNDARIO);
        aplicarLimite(explicacaoGeral, 500, contadorExplicacaoGeral);
        estilizarAreaTexto(explicacaoGeral, false);
        JScrollPane scrollGeral = new JScrollPane(explicacaoGeral);
        estilizarScrollCampo(scrollGeral);
        scrollGeral.setPreferredSize(new Dimension(780, 62));
        matematica.add(scrollGeral, c);
        conteudo.add(matematica);
        conteudo.add(Box.createVerticalStrut(10));

        // Tarefa de interação: quadro delimitado e somente leitura.
        JPanel interacao = new JPanel(new BorderLayout(4, 6));
        interacao.setBackground(COR_SUPERFICIE);
        interacao.setBorder(bordaSecao(loc.texto("analise.interactionTask")));
        JLabel ajudaInteracao = new JLabel("<html>" + loc.texto("analise.interactionTask.help") + "</html>");
        ajudaInteracao.setForeground(COR_TEXTO);
        interacao.add(ajudaInteracao, BorderLayout.NORTH);
        interacao.add(criarResumoInteracao(itens, contexto), BorderLayout.CENTER);
        conteudo.add(interacao);
        conteudo.add(Box.createVerticalStrut(10));

        // Fotografia visual do estado atual da modelagem. Apenas exibição; não altera o diagrama.
        if (contexto.fotografiaVisual != null) {
            JPanel fotografia = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            fotografia.setBackground(COR_SUPERFICIE);
            fotografia.setBorder(bordaSecao(loc.texto("analise.snapshot.title")));
            Image imagemEscalada = escalarImagem(contexto.fotografiaVisual, 860, 280);
            JLabel imagem = new JLabel(new ImageIcon(imagemEscalada));
            imagem.setHorizontalAlignment(JLabel.CENTER);
            imagem.setVerticalAlignment(JLabel.CENTER);
            imagem.setToolTipText(loc.texto("analise.snapshot.tooltip"));
            // Sem JScrollPane ou área fixa: o quadro assume apenas o tamanho do diagrama.
            fotografia.add(imagem);
            conteudo.add(fotografia);
            conteudo.add(Box.createVerticalStrut(10));
        }

        // Preenchimento do pesquisador: catálogo simbólico e construtor controlado.
        JPanel pesquisador = new JPanel(new GridBagLayout());
        pesquisador.setBackground(COR_SUPERFICIE);
        pesquisador.setBorder(bordaSecao(loc.texto("analise.researcher")));
        GridBagConstraints p = new GridBagConstraints();
        p.gridx = 0; p.gridy = 0; p.weightx = 1; p.fill = GridBagConstraints.HORIZONTAL;
        p.insets = new Insets(3, 4, 3, 4);

        JLabel rotuloInvariante = rotulo(loc.texto("analise.invariant"));
        rotuloInvariante.setToolTipText(loc.texto("analise.invariant.tooltip"));
        pesquisador.add(rotuloInvariante, p);

        carregarInvariantes(contexto.categoria);
        p.gridy++;
        pesquisador.add(invarianteCatalogo, p);

        inserirNovaForma.setText(loc.texto("analise.invariant.new"));
        inserirNovaForma.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { atualizarConstrutorSimbolico(); }
        });
        p.gridy++;
        pesquisador.add(inserirNovaForma, p);

        JPanel construtor = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        construtor.setBackground(COR_SUPERFICIE);
        JButton adicionar = new JButton(loc.texto("analise.invariant.addSymbol"));
        JButton remover = new JButton(loc.texto("analise.invariant.removeLast"));
        JButton limpar = new JButton(loc.texto("analise.invariant.clear"));
        estilizarBotaoSecundario(adicionar);
        estilizarBotaoSecundario(remover);
        estilizarBotaoSecundario(limpar);
        adicionar.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
            Object v = simbolos.getSelectedItem();
            if (v != null) blocosForma.add(String.valueOf(v));
            atualizarPrevisualizacao();
        }});
        remover.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
            if (!blocosForma.isEmpty()) blocosForma.remove(blocosForma.size() - 1);
            atualizarPrevisualizacao();
        }});
        limpar.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
            blocosForma.clear(); atualizarPrevisualizacao();
        }});
        construtor.add(simbolos); construtor.add(adicionar); construtor.add(remover); construtor.add(limpar);
        p.gridy++;
        pesquisador.add(construtor, p);

        p.gridy++;
        pesquisador.add(rotulo(loc.texto("analise.invariant.preview")), p);
        formaConstruida.setEditable(false);
        estilizarCampoTexto(formaConstruida, true);
        p.gridy++;
        pesquisador.add(formaConstruida, p);

        p.gridy++;
        pesquisador.add(rotulo(loc.texto("analise.invariant.observation")), p);
        observacaoInvariante.setLineWrap(true);
        observacaoInvariante.setWrapStyleWord(true);
        estilizarAreaTexto(observacaoInvariante, false);
        aplicarLimite(observacaoInvariante, 300, null);
        JScrollPane scrollObservacao = new JScrollPane(observacaoInvariante);
        estilizarScrollCampo(scrollObservacao);
        scrollObservacao.setPreferredSize(new Dimension(780, 50));
        p.gridy++; p.fill = GridBagConstraints.BOTH; p.weighty = 1;
        pesquisador.add(scrollObservacao, p);
        estilizarCombo(invarianteCatalogo);
        estilizarCombo(simbolos);
        inserirNovaForma.setBackground(COR_SUPERFICIE);
        inserirNovaForma.setForeground(COR_TEXTO);
        atualizarConstrutorSimbolico();
        conteudo.add(pesquisador);

        JScrollPane scroll = new JScrollPane(conteudo);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COR_FUNDO);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rodape.setBackground(COR_SUPERFICIE);
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COR_BORDA));
        JButton cancelar = new JButton(loc.texto("analise.cancel"));
        cancelar.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { dispose(); }});
        JButton salvar = new JButton(loc.texto("analise.save"));
        estilizarBotaoSecundario(cancelar);
        estilizarBotaoPrimario(salvar);
        salvar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    salvarNoLog(contexto);
                    salvo = true;
                    JOptionPane.showMessageDialog(TelaArtefatoExplicativo.this,
                            loc.texto("analise.saved"), loc.texto("analise.title"), JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TelaArtefatoExplicativo.this,
                            loc.formatar("analise.error", ex.getMessage()), loc.texto("analise.title"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        rodape.add(cancelar);
        rodape.add(salvar);
        add(rodape, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel criarResumoInteracao(List<ItemExplicacaoModelagem> itens, ArtefatoContexto contexto) {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_SUPERFICIE_SUAVE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST; c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1; c.insets = new Insets(2, 2, 2, 2);
        Map<String, String> resumo = LoggerInteracaoGerard.getInstancia().resumoProtocolosTentativaAtual();
        for (ItemExplicacaoModelagem item : itens) {
            String protocolos = resumo.get(item.getElemento());
            if (protocolos == null || protocolos.trim().length() == 0) protocolos = loc.texto("analise.noProtocol");
            painel.add(new JLabel("<html><b>" + html(item.getElemento()) + ":</b> " + html(protocolos) + "</html>"), c);
            c.gridy++;
        }
        painel.add(new JLabel("<html><b>" + loc.texto("analise.styleUsed") + ":</b> "
                + html(contexto.estiloInteracao) + "</html>"), c);
        c.gridy++;
        painel.add(new JLabel("<html><b>" + loc.texto("analise.representation") + ":</b> "
                + html(contexto.representacao) + "</html>"), c);
        return painel;
    }

    private void salvarNoLog(ArtefatoContexto contexto) {
        LoggerInteracaoGerard logger = LoggerInteracaoGerard.getInstancia();
        String geral = explicacaoGeral.getText();
        InvarianteItem selecionado = (InvarianteItem) invarianteCatalogo.getSelectedItem();
        String origemInvariante = inserirNovaForma.isSelected() ? "PESQUISADOR" : "CATALOGO";
        String codigoInvariante = inserirNovaForma.isSelected() ? "PERSONALIZADO" : (selecionado == null ? "NAO_IDENTIFICADO" : selecionado.codigo);
        String simbolicoInvariante = inserirNovaForma.isSelected() ? formaConstruida.getText() : (selecionado == null ? "" : selecionado.expressao);
        String observacao = observacaoInvariante.getText();
        // O invariante pertence à tentativa, não apenas ao evento de salvamento.
        // Por isso, ele é repetido em todas as ações já registradas e também
        // passa a acompanhar qualquer ação posterior da mesma tentativa.
        logger.associarInvarianteATentativaAtual(origemInvariante, codigoInvariante,
                simbolicoInvariante, observacao);
        for (LinhaResposta linha : linhas) {
            RespostaElementoModelagem r = linha.resposta();
            logger.registrarExplicacaoMatematica(r.getElemento(), r.getPapelSemantico(),
                    r.getExplicacao(), r.getDificuldade(), geral, origemInvariante, codigoInvariante, simbolicoInvariante, observacao,
                    contexto.fotografiaModelagem);
        }
    }

    private void carregarInvariantes(String categoria) {
        invarianteCatalogo.removeAllItems();
        // Catálogo simbólico ampliado. As quatro formas publicadas literalmente
        // no artigo de Mauricio Braga e as formalizações derivadas dos demais
        // invariantes ficam disponíveis em todas as categorias. As expressões
        // são independentes do idioma da interface.
        invarianteCatalogo.addItem(new InvarianteItem("COMP_PARTES_UNIAO",
                "Todo = Parte₁ ∪ Parte₂"));
        invarianteCatalogo.addItem(new InvarianteItem("COMP_PARTES_SOMA",
                "T = P₁ + P₂"));
        invarianteCatalogo.addItem(new InvarianteItem("COMP_CARDINAL",
                "n = Card(A)"));
        invarianteCatalogo.addItem(new InvarianteItem("COMP_CARDINAL_TODO",
                "Card(Todo) = Card(Parte₁) + Card(Parte₂)"));
        invarianteCatalogo.addItem(new InvarianteItem("COMP_ARTIGO_01",
                "Card(A ∪ B) = Card(A) + Card(B), desde que A ∩ B = ∅"));

        invarianteCatalogo.addItem(new InvarianteItem("TRANS_FUNCAO",
                "F = T(I)"));
        invarianteCatalogo.addItem(new InvarianteItem("TRANS_ADITIVA",
                "F = I + T"));
        invarianteCatalogo.addItem(new InvarianteItem("TRANS_ESTADO_INICIAL_NUMERICO",
                "I ∈ ℤ"));
        invarianteCatalogo.addItem(new InvarianteItem("TRANS_ESTADO_FINAL_NUMERICO",
                "F ∈ ℤ"));
        invarianteCatalogo.addItem(new InvarianteItem("TRANS_NUMERICA",
                "T ∈ ℤ"));
        invarianteCatalogo.addItem(new InvarianteItem("TRANS_ARTIGO_01",
                "Se F = I + T então T = F − I"));
        invarianteCatalogo.addItem(new InvarianteItem("TRANS_DIFERENCA",
                "T = F − I"));
        invarianteCatalogo.addItem(new InvarianteItem("TRANS_ARTIGO_02",
                "Se F = T(I) então I = T⁻¹(F)"));
        invarianteCatalogo.addItem(new InvarianteItem("TRANS_INVERSA",
                "I = T⁻¹(F)"));
        invarianteCatalogo.addItem(new InvarianteItem("TRANS_INVERSA_ADITIVA",
                "I = F − T"));

        invarianteCatalogo.addItem(new InvarianteItem("COMPAR_ARTIGO_01",
                "M₂ = M₁ + R"));
        invarianteCatalogo.addItem(new InvarianteItem("COMPAR_REFERENTE_NUMERICO",
                "M₁ ∈ ℤ"));
        invarianteCatalogo.addItem(new InvarianteItem("COMPAR_RELACAO",
                "R = M₂ − M₁"));
        invarianteCatalogo.addItem(new InvarianteItem("NAO_IDENTIFICADO", "—"));
    }

    private void atualizarConstrutorSimbolico() {
        boolean ativo = inserirNovaForma.isSelected();
        simbolos.setEnabled(ativo);
        Component pai = simbolos.getParent();
        if (pai != null) {
            for (Component c : ((JPanel) pai).getComponents()) c.setEnabled(ativo);
        }
        formaConstruida.setEnabled(ativo);
        invarianteCatalogo.setEnabled(!ativo);
        if (!ativo) { blocosForma.clear(); formaConstruida.setText(""); }
    }

    private void atualizarPrevisualizacao() {
        StringBuilder sb = new StringBuilder();
        for (String bloco : blocosForma) {
            if (sb.length() > 0 && precisaEspaco(sb.charAt(sb.length()-1), bloco)) sb.append(' ');
            sb.append(bloco);
        }
        formaConstruida.setText(sb.toString());
    }

    private boolean precisaEspaco(char anterior, String atual) {
        if (atual == null || atual.length() == 0) return false;
        char primeiro = atual.charAt(0);
        return anterior != '(' && primeiro != ')' && primeiro != ',';
    }

    private static final class InvarianteItem {
        final String codigo; final String expressao;
        InvarianteItem(String codigo, String expressao) { this.codigo = codigo; this.expressao = expressao; }
        public String toString() { return expressao; }
    }

    public static boolean mostrar(Component pai, List<ItemExplicacaoModelagem> itens, ArtefatoContexto contexto) {
        Window owner = SwingUtilities.getWindowAncestor(pai);
        TelaArtefatoExplicativo dialogo = new TelaArtefatoExplicativo(owner, itens, contexto);
        dialogo.setVisible(true);
        return dialogo.salvo;
    }

    private javax.swing.border.Border bordaSecao(String titulo) {
        TitledBorder tituloBorda = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                titulo, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), COR_PRIMARIA_ESCURA);
        return BorderFactory.createCompoundBorder(
                tituloBorda,
                BorderFactory.createEmptyBorder(8, 10, 10, 10));
    }

    private javax.swing.border.Border bordaCelula() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(5, 7, 5, 7));
    }

    private void adicionarCabecalhoTabela(JPanel painel, GridBagConstraints c, int x, String texto) {
        c.gridx = x;
        JLabel cabecalho = rotulo(texto);
        cabecalho.setOpaque(true);
        cabecalho.setBackground(COR_DESTAQUE);
        cabecalho.setForeground(COR_PRIMARIA_ESCURA);
        cabecalho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(6, 7, 6, 7)));
        painel.add(cabecalho, c);
    }

    private void adicionarCabecalho(JPanel painel, GridBagConstraints c, int x, String texto) {
        c.gridx = x;
        painel.add(rotulo(texto), c);
    }

    private JLabel tituloSecao(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(l.getFont().deriveFont(Font.BOLD, l.getFont().getSize2D() + 1f));
        l.setBorder(BorderFactory.createEmptyBorder(6, 0, 2, 0));
        return l;
    }

    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        l.setForeground(COR_TEXTO);
        return l;
    }

    private void estilizarAreaTexto(JTextArea campo, boolean somenteLeitura) {
        campo.setBackground(somenteLeitura ? COR_SUPERFICIE_SUAVE : COR_SUPERFICIE);
        campo.setForeground(COR_TEXTO);
        campo.setDisabledTextColor(COR_TEXTO_SECUNDARIO);
        campo.setCaretColor(COR_PRIMARIA_ESCURA);
        campo.setBorder(BorderFactory.createEmptyBorder(6, 7, 6, 7));
        campo.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void estilizarCampoTexto(JTextField campo, boolean somenteLeitura) {
        campo.setBackground(somenteLeitura ? COR_SUPERFICIE_SUAVE : COR_SUPERFICIE);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(COR_PRIMARIA_ESCURA);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
    }

    private void estilizarScrollCampo(JScrollPane scroll) {
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        scroll.getViewport().setBackground(COR_SUPERFICIE);
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setBackground(COR_SUPERFICIE);
        combo.setForeground(COR_TEXTO);
        combo.setBorder(BorderFactory.createLineBorder(COR_BORDA));
    }

    private void estilizarBotaoPrimario(JButton botao) {
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setFocusPainted(false);
        botao.setBackground(COR_PRIMARIA);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_PRIMARIA_ESCURA, 1, true),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotaoSecundario(JButton botao) {
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setFocusPainted(false);
        botao.setBackground(COR_SUPERFICIE);
        botao.setForeground(COR_PRIMARIA_ESCURA);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static Image escalarImagem(BufferedImage origem, int larguraMaxima, int alturaMaxima) {
        if (origem == null) return null;
        double escala = Math.min(1.0d, Math.min(
                (double) larguraMaxima / Math.max(1, origem.getWidth()),
                (double) alturaMaxima / Math.max(1, origem.getHeight())));
        int largura = Math.max(1, (int) Math.round(origem.getWidth() * escala));
        int altura = Math.max(1, (int) Math.round(origem.getHeight() * escala));
        return origem.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
    }

    private static ServicoLocalizacao loc() { return ServicoLocalizacao.getInstancia(); }
    private static String html(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }

    private final class LinhaResposta {
        final ItemExplicacaoModelagem item;
        final JLabel elemento;
        final JLabel papel;
        final JPanel respostaCompacta = new JPanel(new BorderLayout(2, 3));
        final JTextArea explicacao = new JTextArea(2, 36);
        final JLabel contador = new JLabel("0/300");
        final JPanel dificuldade = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        final JRadioButton facil = new JRadioButton(loc.texto("analise.easy"));
        final JRadioButton intermediaria = new JRadioButton(loc.texto("analise.intermediate"));
        final JRadioButton dificil = new JRadioButton(loc.texto("analise.hard"));

        LinhaResposta(ItemExplicacaoModelagem item) {
            this.item = item;
            elemento = new JLabel(item.getElemento());
            String papelLocalizado = loc.texto(item.getChavePapel());
            papel = new JLabel(papelLocalizado);
            papel.setToolTipText(loc.formatar("analise.role.tooltip", papelLocalizado));

            ButtonGroup grupo = new ButtonGroup();
            grupo.add(facil); grupo.add(intermediaria); grupo.add(dificil);
            dificuldade.setBackground(COR_SUPERFICIE);
            JLabel rotuloDificuldade = new JLabel(loc.texto("analise.difficulty") + ":");
            rotuloDificuldade.setForeground(COR_TEXTO);
            dificuldade.add(rotuloDificuldade);
            for (JRadioButton opcao : new JRadioButton[] { facil, intermediaria, dificil }) {
                opcao.setBackground(COR_SUPERFICIE);
                opcao.setForeground(COR_TEXTO);
            }
            dificuldade.add(facil); dificuldade.add(intermediaria); dificuldade.add(dificil);

            explicacao.setLineWrap(true);
            explicacao.setWrapStyleWord(true);
            estilizarAreaTexto(explicacao, false);
            explicacao.setEnabled(false);
            explicacao.setBackground(COR_CAMPO_DESABILITADO);
            explicacao.setToolTipText(loc.texto("analise.reason.tooltip"));
            contador.setForeground(COR_TEXTO_SECUNDARIO);
            aplicarLimite(explicacao, 300, contador);

            ActionListener habilitarExplicacao = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    explicacao.setEnabled(true);
                    explicacao.setBackground(COR_SUPERFICIE);
                    explicacao.requestFocusInWindow();
                }
            };
            facil.addActionListener(habilitarExplicacao);
            intermediaria.addActionListener(habilitarExplicacao);
            dificil.addActionListener(habilitarExplicacao);

            JPanel linhaRotulo = new JPanel(new BorderLayout());
            linhaRotulo.setBackground(COR_SUPERFICIE);
            JLabel justificativa = new JLabel(loc.texto("analise.reason") + ":");
            justificativa.setForeground(COR_TEXTO);
            linhaRotulo.add(justificativa, BorderLayout.WEST);
            linhaRotulo.add(contador, BorderLayout.EAST);
            JPanel topo = new JPanel(new BorderLayout(2, 1));
            topo.setBackground(COR_SUPERFICIE);
            respostaCompacta.setBackground(COR_SUPERFICIE);
            topo.add(dificuldade, BorderLayout.NORTH);
            topo.add(linhaRotulo, BorderLayout.SOUTH);
            respostaCompacta.add(topo, BorderLayout.NORTH);
            JScrollPane rolagemExplicacao = new JScrollPane(explicacao);
            estilizarScrollCampo(rolagemExplicacao);
            rolagemExplicacao.setPreferredSize(new Dimension(500, 48));
            respostaCompacta.add(rolagemExplicacao, BorderLayout.CENTER);
        }

        RespostaElementoModelagem resposta() {
            String nivel = facil.isSelected() ? "FACIL"
                    : (intermediaria.isSelected() ? "INTERMEDIARIA"
                    : (dificil.isSelected() ? "DIFICIL" : ""));
            return new RespostaElementoModelagem(item.getElemento(), item.getChavePapel(), explicacao.getText(), nivel);
        }
    }

    private static void aplicarLimite(final JTextArea campo, final int limite, final JLabel contador) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs)
                    throws BadLocationException {
                if (texto == null) return;
                int disponivel = limite - fb.getDocument().getLength();
                if (disponivel <= 0) return;
                super.insertString(fb, offset, texto.substring(0, Math.min(disponivel, texto.length())), attrs);
            }
            public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs)
                    throws BadLocationException {
                if (texto == null) texto = "";
                int disponivel = limite - (fb.getDocument().getLength() - length);
                if (disponivel < 0) disponivel = 0;
                super.replace(fb, offset, length, texto.substring(0, Math.min(disponivel, texto.length())), attrs);
            }
        });
        if (contador != null) {
            campo.getDocument().addDocumentListener(new DocumentListener() {
                private void atualizar() { contador.setText(campo.getDocument().getLength() + "/" + limite); }
                public void insertUpdate(DocumentEvent e) { atualizar(); }
                public void removeUpdate(DocumentEvent e) { atualizar(); }
                public void changedUpdate(DocumentEvent e) { atualizar(); }
            });
            contador.setText("0/" + limite);
        }
    }

    public static final class ArtefatoContexto {
        public final String tentativaId, usuarioId, problemaId, situacaoId, situacaoGrupoId;
        public final int tentativaNumero;
        public final String idioma, categoria, fotografiaModelagem, enunciado, estiloInteracao, representacao;
        public final BufferedImage fotografiaVisual;
        public ArtefatoContexto(String tentativaId, int tentativaNumero, String usuarioId, String problemaId,
                String situacaoId, String situacaoGrupoId, String idioma, String categoria,
                String fotografiaModelagem) {
            this(tentativaId, tentativaNumero, usuarioId, problemaId, situacaoId, situacaoGrupoId, idioma, categoria,
                    fotografiaModelagem, "", "", "", null);
        }
        public ArtefatoContexto(String tentativaId, int tentativaNumero, String usuarioId, String problemaId,
                String situacaoId, String situacaoGrupoId, String idioma, String categoria,
                String fotografiaModelagem, String enunciado, String estiloInteracao, String representacao) {
            this(tentativaId, tentativaNumero, usuarioId, problemaId, situacaoId, situacaoGrupoId, idioma, categoria,
                    fotografiaModelagem, enunciado, estiloInteracao, representacao, null);
        }
        public ArtefatoContexto(String tentativaId, int tentativaNumero, String usuarioId, String problemaId,
                String situacaoId, String situacaoGrupoId, String idioma, String categoria,
                String fotografiaModelagem, String enunciado, String estiloInteracao, String representacao,
                BufferedImage fotografiaVisual) {
            this.tentativaId = v(tentativaId); this.tentativaNumero = tentativaNumero; this.usuarioId = v(usuarioId); this.problemaId = v(problemaId);
            this.situacaoId = v(situacaoId); this.situacaoGrupoId = v(situacaoGrupoId);
            this.idioma = v(idioma); this.categoria = v(categoria); this.fotografiaModelagem = v(fotografiaModelagem);
            this.enunciado = v(enunciado); this.estiloInteracao = v(estiloInteracao); this.representacao = v(representacao);
            this.fotografiaVisual = fotografiaVisual;
        }
        private static String v(String s) { return s == null ? "" : s; }
    }
}
