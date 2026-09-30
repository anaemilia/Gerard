package gerard.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalButtonUI;

/**
 * Visual padrão de TODO {@link javax.swing.JButton} do Gérard (instalado por
 * {@link GerardTema}): fundo plano arredondado, sem o degradê do Metal, e
 * anel de foco de 3px em COR_ACAO no lugar do retângulo pontilhado.
 *
 * Um botão que já definiu o próprio fundo (setBackground) continua com ele —
 * só o que é padrão do tema (UIResource) é repintado. {@link GerardButton}
 * (primário/secundário) tem pintura própria e não passa por aqui.
 */
public final class GerardButtonUI extends MetalButtonUI {

    private static final GerardButtonUI INSTANCIA = new GerardButtonUI();
    private static final int RAIO = 10;

    public static ComponentUI createUI(JComponent c) {
        return INSTANCIA;
    }

    @Override
    public void update(Graphics g, JComponent c) {
        AbstractButton botao = (AbstractButton) c;
        boolean fundoPadrao = botao.getBackground() instanceof UIResource;
        if (fundoPadrao && botao.isOpaque() && botao.isContentAreaFilled()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getParent() == null ? UITemaGerard.COR_SUPERFICIE : c.getParent().getBackground());
            g2.fillRect(0, 0, c.getWidth(), c.getHeight());
            boolean pressionado = botao.isEnabled() && botao.getModel().isPressed();
            boolean sobre = botao.isEnabled() && botao.getModel().isRollover();
            g2.setColor(pressionado ? UITemaGerard.COR_ACAO_FUNDO : UITemaGerard.COR_SUPERFICIE);
            RoundRectangle2D forma = new RoundRectangle2D.Float(
                    0.5f, 0.5f, c.getWidth() - 1f, c.getHeight() - 1f, RAIO, RAIO);
            g2.fill(forma);
            g2.setColor(sobre || pressionado ? UITemaGerard.COR_ACAO : UITemaGerard.COR_BORDA_CONTROLE);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(forma);
            g2.dispose();
            paint(g, c);
            return;
        }
        super.update(g, c);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if (b.getBackground() instanceof UIResource) {
            return;
        }
        super.paintButtonPressed(g, b);
    }

    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
            Rectangle textRect, Rectangle iconRect) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UITemaGerard.COR_ACAO);
        g2.setStroke(new BasicStroke(3f));
        g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, b.getWidth() - 3f, b.getHeight() - 3f,
                RAIO + 2, RAIO + 2));
        g2.dispose();
    }

    @Override
    protected Color getFocusColor() {
        return UITemaGerard.COR_ACAO;
    }
}
