package gerard.ui.categoria;

import java.awt.Graphics2D;

/** Glifo da categoria "Composição de medidas" no botão de atalho. */
final class IconeCategoriaComposicaoMedidas extends IconeCategoria {
    @Override
    protected void tracar(Graphics2D g2, int x, int y) {
        forma(g2, x + 26, y + 22, false);
        forma(g2, x + 26, y + 58, false);
        forma(g2, x + 70, y + 40, false);

        int cy = y + 40;
        java.awt.geom.Path2D.Float chave = new java.awt.geom.Path2D.Float();
        chave.moveTo(x + 44, y + 14);
        chave.quadTo(x + 56, y + 22, x + 56, cy - 6);
        chave.quadTo(x + 56, cy, x + 62, cy);
        chave.quadTo(x + 56, cy, x + 56, cy + 6);
        chave.quadTo(x + 56, y + 58, x + 44, y + 66);
        g2.draw(chave);
    }
}
