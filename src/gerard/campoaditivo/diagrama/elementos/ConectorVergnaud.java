package gerard.campoaditivo.diagrama.elementos;

import java.awt.*;
import java.awt.geom.QuadCurve2D;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;

public class ConectorVergnaud {
    /** Sentinela: nenhum ponto de destino além do próprio conector (haste não desenhada). */
    public static final int SEM_ALVO = Integer.MIN_VALUE;

    public TipoConectorDiagrama tipo;
    public int x1;
    public int y1;
    public int x2;
    public int y2;
    public String legenda;
    public Rectangle zonaPermitida;
    public int xAlvo = SEM_ALVO;
    public int yAlvo = SEM_ALVO;
    private boolean conclusaoDestacada;

    public ConectorVergnaud(TipoConectorDiagrama tipo, int x1, int y1, int x2, int y2, String legenda, Rectangle zonaPermitida) {
        this.tipo = tipo;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.legenda = legenda;
        this.zonaPermitida = zonaPermitida;
    }

    /**
     * @param xAlvo,yAlvo ponto adicional (ex.: o centro do elemento "Todo") até
     *        onde uma haste é desenhada a partir do meio da chave, ligando
     *        visualmente as partes ao todo.
     */
    public ConectorVergnaud(TipoConectorDiagrama tipo, int x1, int y1, int x2, int y2, String legenda,
                             Rectangle zonaPermitida, int xAlvo, int yAlvo) {
        this(tipo, x1, y1, x2, y2, legenda, zonaPermitida);
        this.xAlvo = xAlvo;
        this.yAlvo = yAlvo;
    }

    public boolean temAlvo() {
        return xAlvo != SEM_ALVO && yAlvo != SEM_ALVO;
    }

    public void definirConclusaoDestacada(boolean destacada) {
        this.conclusaoDestacada = destacada;
    }

    public boolean isConclusaoDestacada() {
        return conclusaoDestacada;
    }

    private Color corDesenho() {
        return conclusaoDestacada ? gerard.ui.UITemaGerard.COR_SUCESSO : gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO;
    }

    public boolean contem(int mx, int my) {
        if (tipo == TipoConectorDiagrama.CHAVE_VERTICAL) {
            Rectangle r = new Rectangle(Math.min(x1, x2) - 8, Math.min(y1, y2), Math.abs(x2 - x1) + 28, Math.abs(y2 - y1) + 1);
            return r.contains(mx, my);
        }
        if (tipo == TipoConectorDiagrama.CHAVE_HORIZONTAL) {
            Rectangle r = new Rectangle(Math.min(x1, x2), Math.min(y1, y2) - 8, Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 28);
            return r.contains(mx, my);
        }
        if (tipo == TipoConectorDiagrama.SETA_CURVA) {
            int minX = Math.min(x1, x2) - 16;
            int maxX = Math.max(x1, x2) + 16;
            int controleY = Math.max(y1, y2) + 96;
            int minY = Math.min(Math.min(y1, y2), controleY) - 16;
            int maxY = Math.max(Math.max(y1, y2), controleY) + 16;
            return new Rectangle(minX, minY, maxX - minX, maxY - minY).contains(mx, my);
        }
        return distanciaPontoSegmento(mx, my, x1, y1, x2, y2) <= 8.0;
    }

    public void mover(int dx, int dy, Rectangle limite) {
        Rectangle zona = zonaPermitida != null ? zonaPermitida : limite;
        int minX = Math.min(x1, x2) + dx;
        int maxX = Math.max(x1, x2) + dx;
        int minY = Math.min(y1, y2) + dy;
        int maxY = Math.max(y1, y2) + dy;
        if (tipo == TipoConectorDiagrama.SETA_CURVA) {
            int controleY = Math.max(y1, y2) + 96;
            minY = Math.min(Math.min(y1, y2), controleY) + dy;
            maxY = Math.max(Math.max(y1, y2), controleY) + dy;
        } else {
            minY = Math.min(y1, y2) + dy;
        }
        if (minX < zona.x) {
            dx += zona.x - minX;
        }
        if (maxX > zona.x + zona.width) {
            dx -= maxX - (zona.x + zona.width);
        }
        if (minY < zona.y) {
            dy += zona.y - minY;
        }
        if (maxY > zona.y + zona.height) {
            dy -= maxY - (zona.y + zona.height);
        }
        x1 += dx; y1 += dy; x2 += dx; y2 += dy;
        if (temAlvo()) {
            xAlvo += dx; yAlvo += dy;
        }
    }

    private double distanciaPontoSegmento(double px, double py, double ax, double ay, double bx, double by) {
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
        if (tipo == TipoConectorDiagrama.CHAVE_VERTICAL) {
            desenharChaveVertical(g2);
            return;
        }
        if (tipo == TipoConectorDiagrama.CHAVE_HORIZONTAL) {
            desenharChaveHorizontal(g2);
            return;
        }
        if (tipo == TipoConectorDiagrama.SETA_CURVA) {
            desenharSetaCurva(g2);
            return;
        }
        Stroke original = g2.getStroke();
        g2.setColor(corDesenho());
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(x1, y1, x2, y2);
        if (tipo == TipoConectorDiagrama.SETA) {
            desenharPontaSeta(g2, x1, y1, x2, y2);
        }
        desenharLegenda(g2);
        g2.setStroke(original);
    }

    private void desenharSetaCurva(Graphics2D g2) {
        Stroke original = g2.getStroke();
        g2.setColor(corDesenho());
        g2.setStroke(new BasicStroke(1.5f));
        int controleX = (x1 + x2) / 2;
        int controleY = Math.max(y1, y2) + 96;
        QuadCurve2D curva = new QuadCurve2D.Double(x1, y1, controleX, controleY, x2, y2);
        g2.draw(curva);

        double t = 0.94;
        double dx = 2.0 * (1.0 - t) * (controleX - x1) + 2.0 * t * (x2 - controleX);
        double dy = 2.0 * (1.0 - t) * (controleY - y1) + 2.0 * t * (y2 - controleY);
        desenharPontaSeta(g2, (int) (x2 - dx), (int) (y2 - dy), x2, y2);
        desenharLegenda(g2);
        g2.setStroke(original);
    }

    private void desenharPontaSeta(Graphics2D g2, int ax, int ay, int bx, int by) {
        double angulo = Math.atan2(by - ay, bx - ax);
        int tamanho = 10;
        int xA = (int) (bx - tamanho * Math.cos(angulo - Math.PI / 6));
        int yA = (int) (by - tamanho * Math.sin(angulo - Math.PI / 6));
        int xB = (int) (bx - tamanho * Math.cos(angulo + Math.PI / 6));
        int yB = (int) (by - tamanho * Math.sin(angulo + Math.PI / 6));
        g2.drawLine(bx, by, xA, yA);
        g2.drawLine(bx, by, xB, yB);
    }

    private void desenharLegenda(Graphics2D g2) {
        if (legenda != null && legenda.trim().length() > 0) {
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();
            int mx = (x1 + x2) / 2;
            int my = (y1 + y2) / 2 - 4;
            g2.drawString(legenda, mx - fm.stringWidth(legenda) / 2, my);
        }
    }

    private void desenharChaveVertical(Graphics2D g2) {
        Stroke original = g2.getStroke();
        g2.setColor(corDesenho());
        g2.setStroke(new BasicStroke(1.5f));
        int x = x1;
        int a = Math.min(y1, y2);
        int b = Math.max(y1, y2);
        int mid = (a + b) / 2;
        int xChave = x + 18;
        g2.drawLine(x, a, xChave, a);
        g2.drawLine(xChave, a, xChave, b);
        g2.drawLine(xChave, b, x, b);
        if (temAlvo()) {
            g2.drawLine(xChave, mid, xAlvo, yAlvo);
        }
        if (legenda != null && legenda.trim().length() > 0) {
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.drawString(legenda, x + 22, mid + 4);
        }
        g2.setStroke(original);
    }

    private void desenharChaveHorizontal(Graphics2D g2) {
        Stroke original = g2.getStroke();
        g2.setColor(corDesenho());
        g2.setStroke(new BasicStroke(1.5f));
        int a = Math.min(x1, x2);
        int b = Math.max(x1, x2);
        int y = y1;
        int mid = (a + b) / 2;
        int yChave = y + 18;
        g2.drawLine(a, y, a, yChave);
        g2.drawLine(a, yChave, b, yChave);
        g2.drawLine(b, yChave, b, y);
        if (temAlvo()) {
            g2.drawLine(mid, yChave, xAlvo, yAlvo);
        }
        desenharLegenda(g2);
        g2.setStroke(original);
    }
}
