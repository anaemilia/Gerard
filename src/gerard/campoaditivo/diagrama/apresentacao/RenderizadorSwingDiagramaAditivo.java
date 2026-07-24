package gerard.campoaditivo.diagrama.apresentacao;

import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;

public class RenderizadorSwingDiagramaAditivo {
    private static final Color COR_ELEMENTO = gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO;
    private static final Color COR_ELEMENTO_SUCESSO = gerard.ui.UITemaGerard.COR_SUCESSO;

    public void renderizar(Graphics2D g2, Rectangle area, CenaDiagramaAditivo cena) {
        renderizar(g2, area, cena, true, false);
    }

    /**
     * Renderiza a cena permitindo ocultar a definição textual da categoria.
     * A sobrecarga preserva o comportamento das telas existentes e permite que
     * atividades específicas apresentem a definição sob demanda, por tooltip.
     */
    public void renderizar(Graphics2D g2, Rectangle area, CenaDiagramaAditivo cena,
                           boolean mostrarDescricao) {
        renderizar(g2, area, cena, mostrarDescricao, false);
    }

    /**
     * @param destaqueSucesso quando verdadeiro, desenha o diagrama em azul
     *        para sinalizar que a modelagem foi validada como correta.
     */
    public void renderizar(Graphics2D g2, Rectangle area, CenaDiagramaAditivo cena,
                           boolean mostrarDescricao, boolean destaqueSucesso) {
        if (cena == null) {
            return;
        }
        Color cor = destaqueSucesso ? COR_ELEMENTO_SUCESSO : COR_ELEMENTO;
        g2.setColor(cor);
        // Fontes escaladas em 1.5x junto com as figuras (ver comentário em
        // RenderizadorDiagramaAditivoBase.medida/relacao/transformacao) —
        // mesma proporção usada em ElementoVergnaud.desenhar para as mesmas
        // caixas, para não deixar o título pequeno perto do diagrama.
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString(cena.getTitulo(), area.x + 18, area.y + 28);
        if (mostrarDescricao) {
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            g2.drawString(cena.getDescricao(), area.x + 18, area.y + 48);
        }

        for (ConectorDiagrama conector : cena.getConectores()) {
            desenharConector(g2, conector, cor);
        }
        for (FiguraDiagrama figura : cena.getFiguras()) {
            desenharFigura(g2, figura, cor);
        }
    }

    private void desenharFigura(Graphics2D g2, FiguraDiagrama figura, Color cor) {
        Stroke old = g2.getStroke();
        g2.setColor(gerard.ui.UITemaGerard.COR_SUPERFICIE);
        if (figura.getTipo() == TipoFiguraDiagrama.ELIPSE) {
            g2.fillOval(figura.getX(), figura.getY(), figura.getLargura(), figura.getAltura());
        } else if (figura.getTipo() == TipoFiguraDiagrama.RETANGULO_ARREDONDADO) {
            g2.fillRoundRect(figura.getX(), figura.getY(), figura.getLargura(), figura.getAltura(), 20, 20);
        } else {
            g2.fillRect(figura.getX(), figura.getY(), figura.getLargura(), figura.getAltura());
        }

        g2.setColor(cor);
        g2.setStroke(new BasicStroke(1.4f));
        if (figura.getTipo() == TipoFiguraDiagrama.ELIPSE) {
            g2.drawOval(figura.getX(), figura.getY(), figura.getLargura(), figura.getAltura());
        } else if (figura.getTipo() == TipoFiguraDiagrama.RETANGULO_ARREDONDADO) {
            g2.drawRoundRect(figura.getX(), figura.getY(), figura.getLargura(), figura.getAltura(), 20, 20);
        } else {
            g2.drawRect(figura.getX(), figura.getY(), figura.getLargura(), figura.getAltura());
        }
        g2.setStroke(old);
    }

    private void desenharConector(Graphics2D g2, ConectorDiagrama c, Color cor) {
        if (c.getTipo() == TipoConectorDiagrama.CHAVE_VERTICAL) {
            desenharChaveVertical(g2, c, cor);
            return;
        }
        if (c.getTipo() == TipoConectorDiagrama.CHAVE_HORIZONTAL) {
            desenharChaveHorizontal(g2, c, cor);
            return;
        }
        if (c.getTipo() == TipoConectorDiagrama.SETA_CURVA) {
            desenharSetaCurva(g2, c, cor);
            return;
        }

        Stroke original = g2.getStroke();
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawLine(c.getX1(), c.getY1(), c.getX2(), c.getY2());
        if (c.getTipo() == TipoConectorDiagrama.SETA) {
            desenharPontaSeta(g2, c.getX1(), c.getY1(), c.getX2(), c.getY2());
        }
        desenharLegendaConector(g2, c);
        g2.setStroke(original);
    }

    private void desenharSetaCurva(Graphics2D g2, ConectorDiagrama c, Color cor) {
        Stroke original = g2.getStroke();
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(1.4f));

        int controleX = (c.getX1() + c.getX2()) / 2;
        int controleY = Math.max(c.getY1(), c.getY2()) + 96;
        QuadCurve2D curva = new QuadCurve2D.Double(c.getX1(), c.getY1(), controleX, controleY, c.getX2(), c.getY2());
        g2.draw(curva);

        double t = 0.94;
        double dx = 2.0 * (1.0 - t) * (controleX - c.getX1()) + 2.0 * t * (c.getX2() - controleX);
        double dy = 2.0 * (1.0 - t) * (controleY - c.getY1()) + 2.0 * t * (c.getY2() - controleY);
        desenharPontaSeta(g2, (int) (c.getX2() - dx), (int) (c.getY2() - dy), c.getX2(), c.getY2());
        desenharLegendaConector(g2, c);
        g2.setStroke(original);
    }

    private void desenharPontaSeta(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double angulo = Math.atan2(y2 - y1, x2 - x1);
        int tamanho = 10;
        int xA = (int) (x2 - tamanho * Math.cos(angulo - Math.PI / 6));
        int yA = (int) (y2 - tamanho * Math.sin(angulo - Math.PI / 6));
        int xB = (int) (x2 - tamanho * Math.cos(angulo + Math.PI / 6));
        int yB = (int) (y2 - tamanho * Math.sin(angulo + Math.PI / 6));
        g2.drawLine(x2, y2, xA, yA);
        g2.drawLine(x2, y2, xB, yB);
    }

    private void desenharChaveVertical(Graphics2D g2, ConectorDiagrama c, Color cor) {
        Stroke original = g2.getStroke();
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(1.4f));
        int x = c.getX1();
        int y1 = Math.min(c.getY1(), c.getY2());
        int y2 = Math.max(c.getY1(), c.getY2());
        int xChave = x + 18;
        g2.drawLine(x, y1, xChave, y1);
        g2.drawLine(xChave, y1, xChave, y2);
        g2.drawLine(xChave, y2, x, y2);
        if (c.temAlvo()) {
            g2.drawLine(xChave, (y1 + y2) / 2, c.getXAlvo(), c.getYAlvo());
        }
        desenharLegendaConector(g2, c);
        g2.setStroke(original);
    }

    private void desenharChaveHorizontal(Graphics2D g2, ConectorDiagrama c, Color cor) {
        Stroke original = g2.getStroke();
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(1.4f));
        int x1 = Math.min(c.getX1(), c.getX2());
        int x2 = Math.max(c.getX1(), c.getX2());
        int y = c.getY1();
        int yChave = y + 18;
        g2.drawLine(x1, y, x1, yChave);
        g2.drawLine(x1, yChave, x2, yChave);
        g2.drawLine(x2, yChave, x2, y);
        if (c.temAlvo()) {
            g2.drawLine((x1 + x2) / 2, yChave, c.getXAlvo(), c.getYAlvo());
        }
        desenharLegendaConector(g2, c);
        g2.setStroke(original);
    }

    private void desenharLegendaConector(Graphics2D g2, ConectorDiagrama c) {
        if (c.getLegenda() != null && c.getLegenda().trim().length() > 0) {
            g2.setFont(new Font("Arial", Font.PLAIN, 17));
            FontMetrics fm = g2.getFontMetrics();
            int mx = (c.getX1() + c.getX2()) / 2;
            int my = (c.getY1() + c.getY2()) / 2 - 4;
            g2.drawString(c.getLegenda(), mx - fm.stringWidth(c.getLegenda()) / 2, my);
        }
    }
}
