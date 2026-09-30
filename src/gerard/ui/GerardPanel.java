package gerard.ui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * Painel/cartão do Design System do site institucional (ver
 * prompt-claude-code-cores.md, seção 3, 2026-09-30): fundo
 * {@link UITemaGerard#COR_SUPERFICIE}, borda {@link UITemaGerard#COR_BORDA}
 * de 1px, cantos arredondados (10–14px). Só apresentação — não decide
 * layout, conteúdo nem comportamento; é um JPanel comum para todo o resto.
 */
public class GerardPanel extends JPanel {

    private static final int RAIO_PADRAO = 12;
    private static final float ESPESSURA_BORDA = 1f;

    private int raio = RAIO_PADRAO;

    public GerardPanel() {
        setOpaque(false);
        setBackground(UITemaGerard.COR_SUPERFICIE);
    }

    /** Ajusta o raio dos cantos (10–14px, ver seção 3 do prompt). */
    public void setRaio(int raioEmPixels) {
        this.raio = raioEmPixels;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graficoBruto) {
        Graphics2D grafico = (Graphics2D) graficoBruto.create();
        grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float largura = getWidth() - ESPESSURA_BORDA;
        float altura = getHeight() - ESPESSURA_BORDA;
        RoundRectangle2D forma = new RoundRectangle2D.Float(
                ESPESSURA_BORDA / 2f, ESPESSURA_BORDA / 2f, largura, altura, raio, raio);
        grafico.setColor(getBackground());
        grafico.fill(forma);
        grafico.setColor(UITemaGerard.COR_BORDA);
        grafico.setStroke(new BasicStroke(ESPESSURA_BORDA));
        grafico.draw(forma);
        grafico.dispose();
        super.paintComponent(graficoBruto);
    }
}
