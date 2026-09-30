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
import java.awt.geom.Path2D;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Selo azul (círculo + marca de visto) exibido ao lado direito do diagrama de
 * Vergnaud após a conclusão da modelagem — substitui o antigo link de texto
 * "Próxima tarefa" (decisão da usuária, 2026-07-28). Azul porque o
 * posicionamento foi concluído com sucesso — mesma convenção de cor de
 * sucesso do resto do app (ver UITemaGerard.COR_SUCESSO).
 */
public final class SeloConclusaoModelagem extends JPanel {
    private static final Color COR_SELO = gerard.ui.UITemaGerard.COR_SUCESSO;
    private static final int DIAMETRO = 44;
    private static final int LADO_CIRCULO = DIAMETRO + 8;
    private static final int ESPACO_ROTULO = 10;
    private static final int DURACAO_ROTULO_MS = 4000;

    private final Timer timerEntrada;
    private final Timer timerRotulo;
    private String rotuloConclusao = "";
    private boolean rotuloVisivel;
    private float opacidade;
    private Runnable acaoClique;
    private Runnable acaoHover;

    public SeloConclusaoModelagem() {
        setOpaque(false);
        setPreferredSize(new Dimension(DIAMETRO + 8, DIAMETRO + 8));
        setVisible(false);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (isVisible() && acaoClique != null && dentroDoCirculo(e)) {
                    acaoClique.run();
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (isVisible() && acaoHover != null && dentroDoCirculo(e)) {
                    acaoHover.run();
                }
            }
        });

        timerRotulo = new Timer(DURACAO_ROTULO_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotuloVisivel = false;
                timerRotulo.stop();
                repaint();
            }
        });
        timerRotulo.setRepeats(false);

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

    /** Define a ação executada ao clicar no selo. */
    public void definirAcaoClique(Runnable acao) {
        this.acaoClique = acao;
    }

    /** Define a ação executada ao passar o mouse sobre o selo. */
    public void definirAcaoHover(Runnable acao) {
        this.acaoHover = acao;
    }

    /**
     * Mantém o texto "Próxima tarefa" acessível — via tooltip e leitor de
     * tela — mesmo sem um rótulo visível (a funcionalidade original do link
     * de texto continua preservada, só a apresentação visual mudou para o
     * selo).
     */
    public void atualizarTexto(String texto) {
        String valor = texto == null ? "" : texto;
        setToolTipText(valor.isEmpty() ? null : valor);
        getAccessibleContext().setAccessibleName(valor);
        repaint();
    }

    /**
     * Texto "Modelagem concluída" (ui.completion.completed) exibido ao lado
     * do selo quando ele aparece e que some sozinho depois de alguns
     * segundos — o selo com a marca de visto permanece. Junto com a marca de
     * visto, garante que o sucesso não dependa só da cor.
     */
    public void atualizarTextoConclusao(String texto) {
        rotuloConclusao = texto == null ? "" : texto;
        getAccessibleContext().setAccessibleDescription(rotuloConclusao);
    }

    private boolean dentroDoCirculo(java.awt.event.MouseEvent e) {
        return e.getX() <= LADO_CIRCULO;
    }

    private int larguraRotulo() {
        if (rotuloConclusao.isEmpty()) {
            return 0;
        }
        java.awt.FontMetrics fm = getFontMetrics(rotuloFonte());
        return ESPACO_ROTULO + fm.stringWidth(rotuloConclusao) + 4;
    }

    private java.awt.Font rotuloFonte() {
        return gerard.ui.GerardFontes.sans(true, 15f);
    }

    /** Posiciona o selo ao lado direito do diagrama de Vergnaud, verticalmente centralizado nele. */
    public void mostrarAoLadoDireitoDoDiagrama(Rectangle areaDiagrama,
            Rectangle areaPermitida, int larguraPai, int alturaPai) {
        int largura = LADO_CIRCULO + larguraRotulo();
        int altura = LADO_CIRCULO;
        Rectangle bounds = PosicionadorSeloDiagrama.calcular(
                areaDiagrama, areaPermitida, larguraPai, alturaPai, largura, altura);
        setBounds(bounds);
        opacidade = 0.0f;
        setVisible(true);
        timerEntrada.restart();
        rotuloVisivel = !rotuloConclusao.isEmpty();
        if (rotuloVisivel) {
            timerRotulo.restart();
        }
        repaint();
    }

    public void ocultar() {
        timerEntrada.stop();
        timerRotulo.stop();
        rotuloVisivel = false;
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
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(COR_SELO);

            int cx = LADO_CIRCULO / 2;
            int cy = getHeight() / 2;
            int raio = DIAMETRO / 2;

            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Ellipse2D.Float(cx - raio, cy - raio, DIAMETRO, DIAMETRO));

            Path2D.Float visto = new Path2D.Float();
            visto.moveTo(cx - raio * 0.45f, cy + raio * 0.02f);
            visto.lineTo(cx - raio * 0.12f, cy + raio * 0.35f);
            visto.lineTo(cx + raio * 0.48f, cy - raio * 0.32f);
            g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(visto);

            if (rotuloVisivel) {
                g2.setFont(rotuloFonte());
                g2.setColor(gerard.ui.UITemaGerard.COR_SUCESSO_TEXTO);
                java.awt.FontMetrics fm = g2.getFontMetrics();
                g2.drawString(rotuloConclusao, LADO_CIRCULO + ESPACO_ROTULO,
                        cy + (fm.getAscent() - fm.getDescent()) / 2);
            }
        } finally {
            g2.dispose();
        }
    }
}
