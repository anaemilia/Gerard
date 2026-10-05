package gerard.ui.categoria;

import gerard.ui.UITemaGerard;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/** Divisor vertical fino entre os dois grupos de categoria — cor estrutural neutra, sem significado próprio. */
public final class SeparadorAtalhoCategoria extends JPanel {
    public SeparadorAtalhoCategoria() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(UITemaGerard.COR_BORDA);
        int x = getWidth() / 2;
        g2.drawLine(x, 6, x, getHeight() - 6);
        g2.dispose();
    }
}
