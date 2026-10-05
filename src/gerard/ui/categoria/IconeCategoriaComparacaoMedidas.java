package gerard.ui.categoria;

import java.awt.Graphics2D;

/** Glifo da categoria "Comparação de medidas" no botão de atalho. */
final class IconeCategoriaComparacaoMedidas extends IconeCategoria {
    @Override
    protected void tracar(Graphics2D g2, int x, int y) {
        forma(g2, x + 40, y + 14, false);
        forma(g2, x + 40, y + 66, false);
        forma(g2, x + 72, y + 40, true);

        int cx = x + 40;
        java.awt.geom.Path2D.Float seta = new java.awt.geom.Path2D.Float();
        seta.moveTo(cx, y + 23);
        seta.lineTo(cx, y + 58);
        seta.moveTo(cx, y + 20);
        seta.lineTo(cx - 7, y + 29);
        seta.moveTo(cx, y + 20);
        seta.lineTo(cx + 7, y + 29);
        g2.draw(seta);
    }
}
