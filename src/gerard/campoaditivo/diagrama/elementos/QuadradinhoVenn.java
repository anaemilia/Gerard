package gerard.campoaditivo.diagrama.elementos;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class QuadradinhoVenn {
    public int x;
    public int y;
    public int tamanho;
    public String origem;
    public String textoEditavel = "";
    private boolean conclusaoDestacada;

    public QuadradinhoVenn(int x, int y, int tamanho, String origem) {
        this.x = x;
        this.y = y;
        this.tamanho = tamanho;
        this.origem = origem;
    }

    /** Marca/desmarca o destaque azul de conclusão da modelagem, espelhando
     *  o comportamento equivalente em ElementoVergnaud/ConectorVergnaud. */
    public void definirConclusaoDestacada(boolean destacada) {
        this.conclusaoDestacada = destacada;
    }

    public boolean isConclusaoDestacada() {
        return conclusaoDestacada;
    }

    public int centroX() {
        return x + tamanho / 2;
    }

    public int centroY() {
        return y + tamanho / 2;
    }

    public boolean contem(int mx, int my) {
        return mx >= x && mx <= x + tamanho &&
               my >= y && my <= y + tamanho;
    }

    public void desenhar(Graphics2D g2) {
        if ("transformacao_negativa".equals(origem)) {
            g2.setColor(gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_NEGATIVA_BASE);
        } else {
            g2.setColor(gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_POSITIVA_BASE);
        }

        g2.fillRoundRect(x, y, tamanho, tamanho, 6, 6);

        g2.setColor(gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_POSITIVA_BORDA);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(x, y, tamanho, tamanho, 6, 6);

        if (textoEditavel != null && textoEditavel.trim().length() > 0) {
            g2.setFont(gerard.ui.GerardFontes.monoTexto(10f));
            FontMetrics fm = g2.getFontMetrics();
            String texto = textoEditavel;
            while (texto.length() > 1 && fm.stringWidth(texto) > tamanho - 3) {
                texto = texto.substring(0, texto.length() - 1);
            }
            int tx = x + (tamanho - fm.stringWidth(texto)) / 2;
            int ty = y + (tamanho - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_POSITIVA_TEXTO);
            g2.drawString(texto, tx, ty);
        }
    }
}
