package gerard.ui.categoria;

import java.awt.Graphics2D;

/** Glifo da categoria "Composição de transformações" no botão de atalho. */
final class IconeCategoriaComposicaoTransformacoes extends IconeCategoria {
    @Override
    protected void tracar(Graphics2D g2, int x, int y) {
        forma(g2, x + 16, y + 30, false);
        forma(g2, x + 46, y + 30, false);
        forma(g2, x + 76, y + 30, false);
        forma(g2, x + 31, y + 12, true);
        forma(g2, x + 61, y + 12, true);
        forma(g2, x + 46, y + 70, true);

        java.awt.geom.Path2D.Float setas = new java.awt.geom.Path2D.Float();
        setas.moveTo(x + 24, y + 30);
        setas.lineTo(x + 38, y + 30);
        setas.moveTo(x + 38, y + 30);
        setas.lineTo(x + 33, y + 26);
        setas.moveTo(x + 38, y + 30);
        setas.lineTo(x + 33, y + 34);

        setas.moveTo(x + 54, y + 30);
        setas.lineTo(x + 68, y + 30);
        setas.moveTo(x + 68, y + 30);
        setas.lineTo(x + 63, y + 26);
        setas.moveTo(x + 68, y + 30);
        setas.lineTo(x + 63, y + 34);
        g2.draw(setas);

        java.awt.geom.Path2D.Float arco = new java.awt.geom.Path2D.Float();
        arco.moveTo(x + 16, y + 38);
        arco.curveTo(x + 22, y + 66, x + 70, y + 66, x + 74, y + 38);
        arco.moveTo(x + 74, y + 38);
        arco.lineTo(x + 67, y + 35);
        arco.moveTo(x + 74, y + 38);
        arco.lineTo(x + 72, y + 45);
        g2.draw(arco);
    }
}
