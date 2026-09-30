package gerard.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * Abas do Design System (prompt-claude-code-cores.md, 2026-09-30): planas,
 * sem degradê nem cantos retos do Swing padrão. A aba ativa recebe um
 * sublinhado de 3px em COR_ACAO e texto INK; as demais, texto secundário.
 * Só apresentação — a seleção e o conteúdo continuam do {@link JTabbedPane}.
 */
public final class GerardTabbedPaneUI extends BasicTabbedPaneUI {

    private static final int ESPESSURA_SUBLINHADO = 3;

    /** Fábrica usada pelo UIManager ("TabbedPaneUI") — ver {@link GerardTema}. */
    public static ComponentUI createUI(JComponent c) {
        return new GerardTabbedPaneUI();
    }

    /** Aplica o visual a uma aba já criada. */
    public static void aplicar(JTabbedPane abas) {
        abas.setUI(new GerardTabbedPaneUI());
        abas.setFont(GerardFontes.sans(true, 13f));
        abas.setBackground(UITemaGerard.COR_FUNDO_CONTEUDO);
        abas.setForeground(UITemaGerard.COR_TEXTO);
        abas.setOpaque(true);
        abas.setFocusable(true);
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(10, 16, 10, 16);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
        tabAreaInsets = new Insets(4, 0, 0, 0);
        contentBorderInsets = new Insets(1, 0, 0, 0);
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
            int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(isSelected ? UITemaGerard.COR_SUPERFICIE : UITemaGerard.COR_FUNDO_CONTEUDO);
        g2.fillRect(x, y, w, h);
        g2.dispose();
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
            int x, int y, int w, int h, boolean isSelected) {
        if (!isSelected) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(UITemaGerard.COR_ACAO);
        g2.fillRect(x, y + h - ESPESSURA_SUBLINHADO, w, ESPESSURA_SUBLINHADO);
        g2.dispose();
    }

    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
            int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(font);
        g2.setColor(isSelected ? UITemaGerard.COR_TEXTO : UITemaGerard.COR_TEXTO_SECUNDARIO);
        g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
        g2.dispose();
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
            int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        if (!isSelected || !tabPane.hasFocus()) {
            return;
        }
        Rectangle r = rects[tabIndex];
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UITemaGerard.COR_ACAO);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(r.x + 1, r.y + 1, r.width - 3, r.height - 3);
        g2.dispose();
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex,
            int x, int y, int w, int h) {
        g.setColor(UITemaGerard.COR_BORDA);
        g.fillRect(x, y, w, 1);
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex,
            int x, int y, int w, int h) {
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex,
            int x, int y, int w, int h) {
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex,
            int x, int y, int w, int h) {
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        g.setColor(c.getBackground());
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        super.paint(g, c);
    }
}
