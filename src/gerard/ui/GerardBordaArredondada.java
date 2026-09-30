package gerard.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

/**
 * Borda de 1px com cantos arredondados do Design System (campos, botões e
 * painéis). Implementa {@link UIResource} para que o Swing a trate como
 * padrão do tema — uma tela que definir a própria borda a substitui
 * normalmente.
 */
public final class GerardBordaArredondada extends AbstractBorder implements UIResource {

    private final Color cor;
    private final int raio;
    private final Insets margem;

    public GerardBordaArredondada(Color cor, int raio, Insets margem) {
        this.cor = cor;
        this.raio = raio;
        this.margem = margem;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int largura, int altura) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, largura - 1f, altura - 1f, raio, raio));
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(margem.top, margem.left, margem.bottom, margem.right);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets destino) {
        destino.set(margem.top, margem.left, margem.bottom, margem.right);
        return destino;
    }
}
