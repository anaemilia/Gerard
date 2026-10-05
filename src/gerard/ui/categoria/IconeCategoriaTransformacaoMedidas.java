package gerard.ui.categoria;

import java.awt.Graphics2D;

/** Glifo da categoria "Transformação de medidas" no botão de atalho. */
final class IconeCategoriaTransformacaoMedidas extends IconeCategoria {
    @Override
    protected void tracar(Graphics2D g2, int x, int y) {
        forma(g2, x + 46, y + 16, true);
        forma(g2, x + 18, y + 52, false);
        forma(g2, x + 74, y + 52, false);

        int cy = y + 52;
        java.awt.geom.Path2D.Float seta = new java.awt.geom.Path2D.Float();
        seta.moveTo(x + 26, cy);
        seta.lineTo(x + 63, cy);
        seta.moveTo(x + 66, cy);
        seta.lineTo(x + 57, cy - 7);
        seta.moveTo(x + 66, cy);
        seta.lineTo(x + 57, cy + 7);
        g2.draw(seta);
    }
}
