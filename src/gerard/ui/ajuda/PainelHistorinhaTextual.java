package gerard.ui.ajuda;

import gerard.ui.UITemaGerard;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Realiza a historinha de texto que a API projetou ({@code tipo = HISTORINHA_TEXTUAL}):
 * mostra o texto da situação-problema recebido, quebrado em linhas. Passivo; não decide
 * nada e não compõe texto próprio.
 */
public final class PainelHistorinhaTextual extends JPanel {
    private static final int MARGEM = 28;
    private final String texto;

    public PainelHistorinhaTextual(String texto, Dimension tamanho) {
        this.texto = texto == null ? "" : texto.trim();
        setOpaque(false);
        setPreferredSize(tamanho);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(UITemaGerard.COR_SUPERFICIE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
            g2.setColor(UITemaGerard.COR_BORDA);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            g2.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fm = g2.getFontMetrics();
            List<String> linhas = quebrar(texto, fm, Math.max(40, w - 2 * MARGEM));
            int alturaBloco = linhas.size() * fm.getHeight();
            int y = Math.max(MARGEM, (h - alturaBloco) / 2) + fm.getAscent();
            g2.setColor(UITemaGerard.COR_TEXTO);
            for (String linha : linhas) {
                g2.drawString(linha, MARGEM, y);
                y += fm.getHeight();
            }
        } finally {
            g2.dispose();
        }
    }

    private static List<String> quebrar(String texto, FontMetrics fm, int larguraMaxima) {
        List<String> linhas = new ArrayList<String>();
        StringBuilder atual = new StringBuilder();
        for (String palavra : texto.split("\s+")) {
            String candidata = atual.length() == 0 ? palavra : atual + " " + palavra;
            if (atual.length() > 0 && fm.stringWidth(candidata) > larguraMaxima) {
                linhas.add(atual.toString());
                atual = new StringBuilder(palavra);
            } else {
                atual = new StringBuilder(candidata);
            }
        }
        if (atual.length() > 0) {
            linhas.add(atual.toString());
        }
        return linhas;
    }
}
