package gerard.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Botão do Design System do site institucional (ver
 * prompt-claude-code-cores.md, seção 4, 2026-09-30) — substitui os botões em
 * relevo cinza padrão do Swing. Só apresentação: não decide quando um botão
 * aparece, habilita ou faz nada além do que {@link JButton} já faz (o
 * {@code ActionListener} de cada chamador continua sendo o dono do
 * comportamento).
 */
public class GerardButton extends JButton {

    /** Estilo visual do botão — ver seção 4 do prompt. */
    public enum Estilo {
        /** Fundo azul de ação, texto branco — ação principal da tela. */
        PRIMARIO,
        /** Fundo branco, borda neutra, texto escuro — ação secundária. */
        SECUNDARIO
    }

    private static final int RAIO = 10;
    private static final int ALTURA_MINIMA = 44;
    private static final int PADDING_HORIZONTAL = 18;
    private static final int PADDING_VERTICAL = 8;
    private static final float ESPESSURA_BORDA = 1f;
    private static final float ESPESSURA_ANEL_FOCO = 3f;
    private static final int MARGEM_ANEL_FOCO = 2;
    private static final float OPACIDADE_DESABILITADO = 0.45f;

    private final Estilo estilo;

    public GerardButton(String texto, Estilo estilo) {
        super(texto);
        this.estilo = estilo == null ? Estilo.SECUNDARIO : estilo;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setFont(GerardFontes.botao());
        setForeground(corTexto(false, false));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(
                PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL));
    }

    /** Atalho para o estilo secundário (o mais comum fora da ação principal da tela). */
    public GerardButton(String texto) {
        this(texto, Estilo.SECUNDARIO);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension base = super.getPreferredSize();
        int margem = 2 * (int) (ESPESSURA_ANEL_FOCO + MARGEM_ANEL_FOCO);
        return new Dimension(base.width + margem, Math.max(base.height + margem, ALTURA_MINIMA));
    }

    @Override
    protected void paintComponent(Graphics graficoBruto) {
        int largura = getWidth();
        int altura = getHeight();
        float borda = ESPESSURA_ANEL_FOCO + MARGEM_ANEL_FOCO;
        RoundRectangle2D forma = new RoundRectangle2D.Float(
                borda, borda, largura - 2 * borda, altura - 2 * borda, RAIO, RAIO);

        boolean sobreMouse = isEnabled() && getModel().isRollover();
        boolean pressionado = isEnabled() && getModel().isPressed();
        setForeground(corTexto(pressionado, sobreMouse));

        Graphics2D grafico = (Graphics2D) graficoBruto.create();
        grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (!isEnabled()) {
            Composite original = grafico.getComposite();
            grafico.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, OPACIDADE_DESABILITADO));
            pintarFundo(grafico, forma, false, false);
            grafico.setComposite(original);
        } else {
            pintarFundo(grafico, forma, pressionado, sobreMouse);
        }
        grafico.dispose();

        super.paintComponent(graficoBruto);

        if (isFocusOwner()) {
            pintarAnelFoco(graficoBruto, largura, altura);
        }
    }

    private void pintarFundo(Graphics2D grafico, RoundRectangle2D forma,
            boolean pressionado, boolean sobreMouse) {
        if (estilo == Estilo.PRIMARIO) {
            grafico.setColor(corFundoPrimario(pressionado, sobreMouse));
            grafico.fill(forma);
        } else {
            grafico.setColor(UITemaGerard.COR_SUPERFICIE);
            grafico.fill(forma);
            grafico.setColor(sobreMouse ? UITemaGerard.COR_ACAO : UITemaGerard.COR_BORDA_CONTROLE);
            grafico.setStroke(new BasicStroke(ESPESSURA_BORDA));
            grafico.draw(forma);
        }
    }

    /**
     * Anel de foco de 3px, não o retângulo pontilhado padrão do Swing
     * ({@code setFocusPainted(false)} já removeu o padrão).
     */
    private void pintarAnelFoco(Graphics graficoBruto, int largura, int altura) {
        Graphics2D grafico = (Graphics2D) graficoBruto.create();
        grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        grafico.setColor(UITemaGerard.COR_ACAO);
        grafico.setStroke(new BasicStroke(ESPESSURA_ANEL_FOCO));
        float metade = ESPESSURA_ANEL_FOCO / 2f;
        RoundRectangle2D anel = new RoundRectangle2D.Float(
                metade, metade, largura - ESPESSURA_ANEL_FOCO, altura - ESPESSURA_ANEL_FOCO,
                RAIO + MARGEM_ANEL_FOCO, RAIO + MARGEM_ANEL_FOCO);
        grafico.draw(anel);
        grafico.dispose();
    }

    private Color corFundoPrimario(boolean pressionado, boolean sobreMouse) {
        if (pressionado) {
            return UITemaGerard.COR_ACAO_PRESSIONADA;
        }
        if (sobreMouse) {
            return UITemaGerard.COR_ACAO_HOVER;
        }
        return UITemaGerard.COR_ACAO;
    }

    private Color corTexto(boolean pressionado, boolean sobreMouse) {
        if (estilo == Estilo.PRIMARIO) {
            return Color.WHITE;
        }
        return sobreMouse || pressionado ? UITemaGerard.COR_ACAO_PRESSIONADA : UITemaGerard.COR_TEXTO;
    }
}
