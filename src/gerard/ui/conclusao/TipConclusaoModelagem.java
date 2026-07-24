package gerard.ui.conclusao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/** Tip não modal com escolha Sim/Não para avançar após concluir a modelagem. */
public final class TipConclusaoModelagem extends JPanel {
    public interface OuvinteEscolha {
        void aoEscolherSim();
        void aoEscolherNao();
    }

    // Neutro de propósito: o azul de sucesso é reservado aos números do
    // diagrama (ver ElementoVergnaud.desenhar) — este tip pergunta sobre a
    // próxima tarefa, não representa um valor correto.
    private static final Color FUNDO = gerard.ui.UITemaGerard.COR_SUPERFICIE;
    private static final Color BORDA = gerard.ui.UITemaGerard.COR_BORDA;
    private static final Color TEXTO = gerard.ui.UITemaGerard.COR_TEXTO;

    private final JLabel mensagem = new JLabel();
    private final JRadioButton opcaoSim = new JRadioButton();
    private final JRadioButton opcaoNao = new JRadioButton();
    private final ButtonGroup grupo = new ButtonGroup();
    private OuvinteEscolha ouvinte;

    public TipConclusaoModelagem() {
        setLayout(new BorderLayout(8, 6));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 9, 12));
        mensagem.setFont(new Font("Arial", Font.BOLD, 13));
        mensagem.setForeground(TEXTO);
        add(mensagem, BorderLayout.CENTER);

        JPanel opcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        opcoes.setOpaque(false);
        configurarOpcao(opcaoSim);
        configurarOpcao(opcaoNao);
        grupo.add(opcaoSim);
        grupo.add(opcaoNao);
        opcoes.add(opcaoSim);
        opcoes.add(opcaoNao);
        add(opcoes, BorderLayout.SOUTH);

        opcaoSim.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ocultar();
                if (ouvinte != null) ouvinte.aoEscolherSim();
            }
        });
        opcaoNao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ocultar();
                if (ouvinte != null) ouvinte.aoEscolherNao();
            }
        });
        setVisible(false);
    }

    private void configurarOpcao(JRadioButton opcao) {
        opcao.setOpaque(false);
        opcao.setForeground(TEXTO);
        opcao.setFont(new Font("Arial", Font.PLAIN, 13));
        opcao.setFocusPainted(false);
    }

    public void definirOuvinte(OuvinteEscolha ouvinte) {
        this.ouvinte = ouvinte;
    }

    public void atualizarTextos(String textoMensagem, String textoSim, String textoNao) {
        mensagem.setText(textoMensagem == null ? "" : textoMensagem);
        opcaoSim.setText(textoSim == null ? "" : textoSim);
        opcaoNao.setText(textoNao == null ? "" : textoNao);
        revalidate();
        repaint();
    }

    public void mostrar(Rectangle areaReferencia, int larguraPai, int alturaPai) {
        mostrarAbaixoDoDiagrama(areaReferencia, null, larguraPai, alturaPai);
    }

    /**
     * Posiciona o tip abaixo da área visual ocupada pelo diagrama de Vergnaud.
     * A área permitida corresponde ao painel do diagrama e evita que o tip
     * invada o texto do problema ou ultrapasse as bordas laterais.
     */
    public void mostrarAbaixoDoDiagrama(Rectangle areaDiagrama,
                                        Rectangle areaPermitida,
                                        int larguraPai,
                                        int alturaPai) {
        grupo.clearSelection();
        Dimension preferido = getPreferredSize();
        int largura = Math.max(360, preferido.width + 8);
        int altura = Math.max(72, preferido.height + 4);

        Rectangle permitida = areaPermitida == null
                ? new Rectangle(12, 55, Math.max(1, larguraPai - 24), Math.max(1, alturaPai - 67))
                : new Rectangle(areaPermitida);
        int centroX = areaDiagrama == null
                ? permitida.x + permitida.width / 2
                : areaDiagrama.x + areaDiagrama.width / 2;
        int minimoX = Math.max(12, permitida.x + 10);
        int maximoX = Math.min(larguraPai - largura - 12,
                permitida.x + permitida.width - largura - 10);
        int x = Math.max(minimoX, Math.min(maximoX, centroX - largura / 2));

        int baseDiagrama = areaDiagrama == null
                ? permitida.y + permitida.height / 2
                : areaDiagrama.y + areaDiagrama.height;
        int yDesejado = baseDiagrama + 14;
        int maximoY = Math.min(alturaPai - altura - 12,
                permitida.y + permitida.height - altura - 10);
        int y = Math.max(permitida.y + 10, Math.min(maximoY, yDesejado));

        setBounds(x, y, largura, altura);
        setVisible(true);
        repaint();
    }

    /**
     * Posiciona o tip num ponto livre (x, y) em vez de abaixo de uma área de
     * referência — usado quando o tip deve ficar ao lado do conteúdo, colado
     * a ele, em vez de sempre abaixo do diagrama inteiro.
     */
    public void mostrarEm(int x, int y, int larguraPai, int alturaPai) {
        grupo.clearSelection();
        Dimension preferido = getPreferredSize();
        int largura = Math.max(360, preferido.width + 8);
        int altura = Math.max(72, preferido.height + 4);
        int xClamped = Math.max(8, Math.min(Math.max(8, larguraPai - largura - 8), x));
        int yClamped = Math.max(8, Math.min(Math.max(8, alturaPai - altura - 8), y));
        setBounds(xClamped, yClamped, largura, altura);
        setVisible(true);
        repaint();
    }

    public void ocultar() {
        setVisible(false);
        grupo.clearSelection();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(FUNDO);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.setColor(BORDA);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }
}
