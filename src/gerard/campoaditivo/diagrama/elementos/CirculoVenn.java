package gerard.campoaditivo.diagrama.elementos;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class CirculoVenn {
    public int x;
    public int y;
    public int largura;
    public int altura;
    public String rotulo;
    public String textoEditavel = "";
    public int valorReferencia;
    public boolean exibirQuadradinhos;
    public boolean formaRetangular = false;

    public CirculoVenn(int x, int y, int largura, int altura, String rotulo, int valorReferencia, boolean exibirQuadradinhos) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.rotulo = rotulo;
        this.valorReferencia = valorReferencia;
        this.exibirQuadradinhos = exibirQuadradinhos;
    }

    public boolean contem(int px, int py) {
        if (formaRetangular) {
            return px >= x && px <= x + largura && py >= y && py <= y + altura;
        }
        double centroX = x + largura / 2.0;
        double centroY = y + altura / 2.0;
        double raioX = largura / 2.0;
        double raioY = altura / 2.0;

        double valor =
                Math.pow((px - centroX) / raioX, 2) +
                Math.pow((py - centroY) / raioY, 2);

        return valor <= 1.0;
    }

    public void desenhar(Graphics2D g2) {
        Stroke original = g2.getStroke();

        g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
        g2.fillOval(x, y, largura, altura);

        g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x, y, largura, altura);

        g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString(rotulo, x + 8, y - 6);

        if (textoEditavel != null && textoEditavel.trim().length() > 0) {
            g2.setFont(new Font("Arial", Font.BOLD, 17));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (largura - fm.stringWidth(textoEditavel)) / 2;
            int ty = y + (altura - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(textoEditavel, tx, ty);
        }

        g2.setStroke(original);
    }
}
