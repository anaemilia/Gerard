package gerard.ui.conclusao;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Link de texto discreto exibido após a conclusão do diagrama ("Próxima tarefa"). */
public final class SeloConclusaoModelagem extends JPanel {
    // Neutro de propósito: o azul de sucesso fica restrito aos números do
    // diagrama — este link ("Próxima tarefa") não representa um valor.
    private static final Color TEXTO = gerard.ui.UITemaGerard.COR_TEXTO;

    private final JLabel mensagem = new JLabel();
    private final Timer timerEntrada;
    private float opacidade;
    private Runnable acaoClique;
    private Runnable acaoHover;

    public SeloConclusaoModelagem() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        mensagem.setFont(new Font("Arial", Font.BOLD, 13));
        mensagem.setForeground(TEXTO);
        add(mensagem, BorderLayout.CENTER);
        setVisible(false);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (isVisible() && acaoClique != null) {
                    acaoClique.run();
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (isVisible() && acaoHover != null) {
                    acaoHover.run();
                }
            }
        });

        timerEntrada = new Timer(32, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacidade = Math.min(1.0f, opacidade + 0.16f);
                repaint();
                if (opacidade >= 1.0f) {
                    timerEntrada.stop();
                }
            }
        });
    }

    /** Define a ação executada ao clicar no link "Próxima tarefa". */
    public void definirAcaoClique(Runnable acao) {
        this.acaoClique = acao;
    }

    /** Define a ação executada ao passar o mouse sobre o link "Próxima tarefa". */
    public void definirAcaoHover(Runnable acao) {
        this.acaoHover = acao;
    }

    public void atualizarTexto(String texto) {
        mensagem.setText(texto == null ? "" : texto);
        revalidate();
        repaint();
    }

    public void mostrarAbaixoDoDiagrama(Rectangle areaDiagrama,
            Rectangle areaPermitida, int larguraPai, int alturaPai) {
        Dimension preferido = getPreferredSize();
        int largura = Math.max(190, preferido.width + 10);
        int altura = Math.max(40, preferido.height + 2);
        Rectangle permitida = areaPermitida == null
                ? new Rectangle(12, 55, Math.max(1, larguraPai - 24),
                        Math.max(1, alturaPai - 67))
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
        opacidade = 0.0f;
        setVisible(true);
        timerEntrada.restart();
        repaint();
    }

    public void ocultar() {
        timerEntrada.stop();
        setVisible(false);
        opacidade = 0.0f;
    }

    float getOpacidadeParaTeste() {
        return opacidade;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER,
                Math.max(0.0f, Math.min(1.0f, opacidade))));
        super.paint(g2);
        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
