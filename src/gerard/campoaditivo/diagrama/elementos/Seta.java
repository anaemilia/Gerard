package gerard.campoaditivo.diagrama.elementos;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class Seta {
    public int x1;
    public int y1;
    public int x2;
    public int y2;

    public Seta(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public void mover(int dx, int dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }

    public boolean contem(int mx, int my) {
        double distancia = distanciaPontoSegmento(mx, my, x1, y1, x2, y2);
        return distancia <= 8.0;
    }

    private double distanciaPontoSegmento(
            double px, double py,
            double ax, double ay,
            double bx, double by
    ) {
        double dx = bx - ax;
        double dy = by - ay;

        if (dx == 0 && dy == 0) {
            return Point.distance(px, py, ax, ay);
        }

        double t = ((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double projX = ax + t * dx;
        double projY = ay + t * dy;

        return Point.distance(px, py, projX, projY);
    }

    public void desenhar(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.3f));
        g2.drawLine(x1, y1, x2, y2);

        double angulo = Math.atan2(y2 - y1, x2 - x1);
        int tamanho = 14;

        int xA = (int) (x2 - tamanho * Math.cos(angulo - Math.PI / 6));
        int yA = (int) (y2 - tamanho * Math.sin(angulo - Math.PI / 6));
        int xB = (int) (x2 - tamanho * Math.cos(angulo + Math.PI / 6));
        int yB = (int) (y2 - tamanho * Math.sin(angulo + Math.PI / 6));

        g2.drawLine(x2, y2, xA, yA);
        g2.drawLine(x2, y2, xB, yB);
    }
}
