package gerard.ui.conclusao;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Selo vermelho (círculo vazio, sem marca de visto) exibido ao lado direito
 * do diagrama de Vergnaud já na primeira tentativa rejeitada da incógnita —
 * conceito antagônico ao de SeloConclusaoModelagem (círculo azul com marca de
 * visto, exibido só na conclusão correta): mesma geometria de círculo, cor de
 * erro do app (UITemaGerard.COR_ERRO) em vez da cor de sucesso, e sem a marca
 * interna — um "checkbox" desmarcado em vez de marcado. Puramente visual
 * (sem clique/hover/navegação, ao contrário do selo de conclusão, que avança
 * para a próxima tarefa).
 */
public final class SeloErroModelagem extends JPanel {
    private static final Color COR_SELO = gerard.ui.UITemaGerard.COR_ERRO;
    private static final int DIAMETRO = 44;

    private final Timer timerEntrada;
    private float opacidade;

    public SeloErroModelagem() {
        setOpaque(false);
        setPreferredSize(new Dimension(DIAMETRO + 8, DIAMETRO + 8));
        setVisible(false);

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

    /** Posiciona o selo ao lado direito do diagrama de Vergnaud, verticalmente centralizado nele. */
    public void mostrarAoLadoDireitoDoDiagrama(Rectangle areaDiagrama,
            Rectangle areaPermitida, int larguraPai, int alturaPai) {
        int largura = DIAMETRO + 8;
        int altura = DIAMETRO + 8;
        Rectangle bounds = PosicionadorSeloDiagrama.calcular(
                areaDiagrama, areaPermitida, larguraPai, alturaPai, largura, altura);
        setBounds(bounds);
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
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(COR_SELO);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int raio = DIAMETRO / 2;

            g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Ellipse2D.Float(cx - raio, cy - raio, DIAMETRO, DIAMETRO));
            int alcance = Math.max(8, raio / 2);
            g2.setStroke(new BasicStroke(4.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - alcance, cy - alcance, cx + alcance, cy + alcance);
            g2.drawLine(cx + alcance, cy - alcance, cx - alcance, cy + alcance);
        } finally {
            g2.dispose();
        }
    }
}
