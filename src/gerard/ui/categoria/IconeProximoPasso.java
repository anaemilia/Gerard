package gerard.ui.categoria;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * Ícone "?" do atalho de "próximo passo" — mesmo estilo fino e neutro dos glifos de categoria, um
 * placeholder desabilitado (não o ícone escuro de ajuda contextual).
 */
public final class IconeProximoPasso extends IconeCategoria {
    public static final int LARGURA = 54;
    public static final int ALTURA = 54;

    @Override
    public int getIconWidth() { return LARGURA; }

    @Override
    public int getIconHeight() { return ALTURA; }

    @Override
    protected void tracar(Graphics2D g2, int x, int y) {
        int diametro = 34;
        int cx = x + LARGURA / 2 - diametro / 2;
        int cy = y + ALTURA / 2 - diametro / 2;
        g2.draw(new java.awt.geom.Ellipse2D.Float(cx, cy, diametro, diametro));
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        FontMetrics fm = g2.getFontMetrics();
        String simbolo = "?";
        int tx = x + LARGURA / 2 - fm.stringWidth(simbolo) / 2;
        int ty = y + ALTURA / 2 + (fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(simbolo, tx, ty);
    }
}
