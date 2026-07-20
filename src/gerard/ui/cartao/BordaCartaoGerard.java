package gerard.ui.cartao;

import javax.swing.border.AbstractBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Borda com cantos arredondados que também preenche o fundo do
 * componente antes de desenhar o contorno — permite aplicar a
 * "suavidade" da identidade visual (cantos arredondados, borda fina,
 * respiro) em qualquer {@link javax.swing.JComponent} já existente,
 * bastando chamar {@code componente.setOpaque(false)} e
 * {@code componente.setBorder(new BordaCartaoGerard(fundo, borda))}.
 *
 * Como o Swing pinta a borda antes dos componentes filhos, o
 * preenchimento arredondado aparece atrás do conteúdo normalmente.
 */
public class BordaCartaoGerard extends AbstractBorder {

    private final Color corFundo;
    private final Color corBorda;
    private final int raio;
    private final int espessura;
    private final Insets respiro;

    public BordaCartaoGerard(Color corFundo, Color corBorda) {
        this(corFundo, corBorda, 14, 1, new Insets(12, 14, 12, 14));
    }

    public BordaCartaoGerard(Color corFundo, Color corBorda, int raio, int espessura, Insets respiro) {
        this.corFundo = corFundo;
        this.corBorda = corBorda;
        this.raio = raio;
        this.espessura = espessura;
        this.respiro = respiro;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int largura, int altura) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float metade = espessura / 2f;
            RoundRectangle2D forma = new RoundRectangle2D.Float(
                    x + metade, y + metade,
                    largura - espessura, altura - espessura,
                    raio, raio);

            if (corFundo != null) {
                g2.setColor(corFundo);
                g2.fill(forma);
            }
            if (corBorda != null && espessura > 0) {
                g2.setStroke(new java.awt.BasicStroke(espessura));
                g2.setColor(corBorda);
                g2.draw(forma);
            }
        } finally {
            g2.dispose();
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return (Insets) respiro.clone();
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(respiro.top, respiro.left, respiro.bottom, respiro.right);
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
