package gerard.ui.ajuda;

import gerard.ui.UITemaGerard;
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
 * Desenha, abaixo da animação, os trechos de texto que a API projetou para a historinha
 * (já no idioma da situação). Passivo: não escolhe nem compõe texto.
 */
public final class PainelTrechosHistorinha extends JPanel {
    private static final int ALTURA_LINHA = 22;
    private static final int MARGEM = 8;
    private final List<String> trechos = new ArrayList<String>();

    public PainelTrechosHistorinha(List<?> trechos) {
        for (Object trecho : trechos) {
            if (trecho != null && !String.valueOf(trecho).trim().isEmpty()) {
                this.trechos.add(String.valueOf(trecho).trim());
            }
        }
        setOpaque(false);
        setPreferredSize(new Dimension(1, alturaPara(this.trechos.size())));
    }

    /** Altura reservada para a quantidade de trechos (uma linha por trecho). */
    public static int alturaPara(int quantidade) {
        return quantidade <= 0 ? 0 : quantidade * ALTURA_LINHA + 2 * MARGEM;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font("Arial", Font.PLAIN, 15));
            g2.setColor(UITemaGerard.COR_TEXTO);
            FontMetrics fm = g2.getFontMetrics();
            int y = MARGEM + fm.getAscent();
            for (String trecho : trechos) {
                int x = Math.max(MARGEM, (getWidth() - fm.stringWidth(trecho)) / 2);
                g2.drawString(trecho, x, y);
                y += ALTURA_LINHA;
            }
        } finally {
            g2.dispose();
        }
    }
}
