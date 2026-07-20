package gerard.campoaditivo.diagrama.elementos;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class Elemento {
    public int x;
    public int y;
    public int largura;
    public int altura;
    public boolean circulo;

    public Elemento(int x, int y, int largura, int altura, boolean circulo) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.circulo = circulo;
    }

    public boolean contem(int mx, int my) {
        if (!circulo) {
            return mx >= x && mx <= x + largura &&
                   my >= y && my <= y + altura;
        }

        int centroX = x + largura / 2;
        int centroY = y + altura / 2;
        int raioX = largura / 2;
        int raioY = altura / 2;

        double valor =
                Math.pow((double) (mx - centroX) / raioX, 2) +
                Math.pow((double) (my - centroY) / raioY, 2);

        return valor <= 1.0;
    }

    public void desenhar(Graphics2D g2) {
        g2.setColor(Color.WHITE);

        if (circulo) {
            g2.fillOval(x, y, largura, altura);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.3f));
            g2.drawOval(x, y, largura, altura);
        } else {
            g2.fillRect(x, y, largura, altura);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.3f));
            g2.drawRect(x, y, largura, altura);
        }
    }
}
