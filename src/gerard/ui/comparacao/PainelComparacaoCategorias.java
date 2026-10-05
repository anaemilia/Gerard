package gerard.ui.comparacao;

import gerard.aplicacao.EstadoNumericoComparacaoCategorias;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;
import gerard.ui.UITemaGerard;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Tela "Comparar categorias" (Arquivo > Comparar categorias): mesma solução numérica em Composição,
 * Transformação e Comparação de medidas, com a representação da situação-problema (editável por duplo
 * clique) e a representação da categoria (estática). O desenho de cada categoria é do renderizador dela
 * (RenderizadorMiniCategoria) e as decisões por categoria são de CategoriasComparaveis; o registro da
 * ação é entregue por quem abre a tela.
 */
public final class PainelComparacaoCategorias extends JPanel {
    private final ServicoLocalizacao localizacao;
    private final RegistroAcaoComparacao registro;

    // Terceiro tom de cinza (mais claro que UITemaGerard.COR_TEXTO/UITemaGerard.COR_TEXTO_SECUNDARIO,
    // ainda legível) usado só para distinguir o "total" de parcela1/parcela2
    // no rastreio de valores entre colunas — ver comentário acima de total.setForeground.
    final Color COR_CINZA_TOTAL_COMPARACAO = gerard.ui.CoresRepresentacaoGerard.CONTROLE_SINAL;
    final EstadoNumericoComparacaoCategorias modelo =
            new EstadoNumericoComparacaoCategorias(4, 7);
    final JSpinner spinnerA = new JSpinner(new SpinnerNumberModel(4, 0, 999, 1));
    final JSpinner spinnerB = new JSpinner(new SpinnerNumberModel(7, 0, 999, 1));
    final JPanel grade = new JPanel(new GridBagLayout());
    boolean atualizandoControles;

    public PainelComparacaoCategorias(ServicoLocalizacao localizacao, RegistroAcaoComparacao registro) {
        super(new BorderLayout(8, 8));
        this.localizacao = localizacao;
        this.registro = registro;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UITemaGerard.COR_FUNDO_CONTEUDO);

        JPanel topo = new JPanel(new BorderLayout(12, 6));
        topo.setOpaque(false);

        JPanel destaque = new JPanel();
        destaque.setLayout(new BoxLayout(destaque, BoxLayout.Y_AXIS));
        destaque.setOpaque(false);
        destaque.setBorder(new gerard.ui.cartao.BordaCartaoGerard(
                UITemaGerard.COR_DESTAQUE, UITemaGerard.COR_PRIMARIA));

        JLabel tituloDestaque = new JLabel(localizacao.texto("ui.compare.title"), SwingConstants.CENTER);
        tituloDestaque.setFont(new Font("Arial", Font.BOLD, 18));
        tituloDestaque.setForeground(UITemaGerard.COR_PRIMARIA);
        tituloDestaque.setAlignmentX(Component.CENTER_ALIGNMENT);
        destaque.add(tituloDestaque);

        final JLabel formulaDestaque = new JLabel("4 + 7 = 11", SwingConstants.CENTER);
        formulaDestaque.setFont(new Font("Arial", Font.BOLD, 20));
        formulaDestaque.setAlignmentX(Component.CENTER_ALIGNMENT);
        destaque.add(formulaDestaque);

        destaque.add(Box.createVerticalStrut(4));
        JLabel explicacao = new JLabel("<html><div style='text-align:center; width:720px;'>" + localizacao.texto("ui.compare.explanation") + "</div></html>", SwingConstants.CENTER);
        explicacao.setFont(new Font("Arial", Font.BOLD, 18));
        explicacao.setForeground(UITemaGerard.COR_TEXTO);
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
        ((JSpinner.DefaultEditor) spinnerA.getEditor()).getTextField().setForeground(UITemaGerard.COR_TEXTO);
        ((JSpinner.DefaultEditor) spinnerB.getEditor()).getTextField().setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
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
    final TipoSituacaoAditiva[] categorias = gerard.aplicacao.CategoriasComparaveis.todas()
            .toArray(new TipoSituacaoAditiva[0]);

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
        String chave = gerard.aplicacao.CategoriasComparaveis.chaveTextoDoProblema(categoria);
        String valorA = "<span style='color:#332E28;font-weight:bold'>" + modelo.getPrimeiraParcela() + "</span>";
        String valorB = "<span style='color:#746E62;font-weight:bold'>" + modelo.getSegundaParcela() + "</span>";
        String valorTotal = "<span style='color:#968E80;font-weight:bold'>" + modelo.getTotal() + "</span>";
        return "<html><div style='padding:5px 7px'><b>" + romano + ". " + titulo + "</b><br><br>"
                + localizacao.formatar(chave, valorA, valorB, valorTotal)
                + "</div></html>";
    }

    void registrarAcaoComparacao(String evento, String detalhes) {
        registro.registrar(evento, detalhes);
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
            g2.setColor(UITemaGerard.COR_TEXTO); g2.drawString(a, x, y); x += fm.stringWidth(a);
            g2.setColor(Color.DARK_GRAY); g2.drawString(op1, x, y); x += fm.stringWidth(op1);
            g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO); g2.drawString(b, x, y); x += fm.stringWidth(b);
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

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.4f));
            alvos.clear();
            // A representação da situação-problema é uma instância preenchida e interativa.
            // A representação da categoria é uma referência estrutural: vazia, estática
            // e sem participação na sincronização ou no log de modelagem. Quem desenha cada
            // categoria é o renderizador dela (gerard.ui.comparacao); a tela só o pergunta.
            gerard.ui.comparacao.QuadroMiniCategoria quadro =
                    new gerard.ui.comparacao.QuadroMiniCategoria(getWidth(), getHeight(), alvos);
            g2.setFont(quadro.fonteDosValores());
            gerard.ui.comparacao.RenderizadorMiniCategoria.para(categoria)
                    .desenhar(g2, quadro, formal ? null : modelo);
            g2.dispose();
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
            return gerard.aplicacao.CategoriasComparaveis.papelNoIndice(categoria, indice);
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
