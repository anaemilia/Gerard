package gerard.ui.categoria;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.ui.UITemaGerard;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;

/**
 * Glifo de uma categoria nos botões de atalho: vocabulário visual das representações (caixas, círculos,
 * setas, chaves) em escala de ícone. Cada categoria sabe desenhar o seu (polimórfico); quem monta a barra só
 * pergunta {@link #para(TipoSituacaoAditiva)}. Cores estruturais neutras, sem significado de feedback.
 */
public abstract class IconeCategoria implements Icon {
    public static final int LARGURA = 92;
    public static final int ALTURA = 84;
    private static final int CAIXA = 16;

    /** O ícone da categoria (composição de medidas se a categoria for nula ou desconhecida). */
    public static IconeCategoria para(TipoSituacaoAditiva tipo) {
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) return new IconeCategoriaComposicaoMedidas();
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) return new IconeCategoriaTransformacaoMedidas();
        if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) return new IconeCategoriaComparacaoMedidas();
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) return new IconeCategoriaComposicaoTransformacoes();
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) return new IconeCategoriaTransformacaoRelacao();
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) return new IconeCategoriaComposicaoRelacoes();
        return new IconeCategoriaComposicaoMedidas();
    }

    @Override
    public int getIconWidth() { return LARGURA; }

    @Override
    public int getIconHeight() { return ALTURA; }

    @Override
    public final void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = prepararTraco(g);
        try {
            tracar(g2, x, y);
        } finally {
            g2.dispose();
        }
    }

    /** Desenha o glifo a partir do canto superior esquerdo (x,y). */
    protected abstract void tracar(Graphics2D g2, int x, int y);

    /** Estilo de traço compartilhado: fino, com pontas e junções arredondadas. */
    public static Graphics2D prepararTraco(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setColor(UITemaGerard.COR_BORDA);
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        return g2;
    }

    /** Uma forma (quadrado ou círculo) do glifo, centrada em (cx,cy): preenchimento e contorno estruturais. */
    protected final void forma(Graphics2D g2, int cx, int cy, boolean circulo) {
        int metade = CAIXA / 2;
        g2.setColor(UITemaGerard.COR_SUPERFICIE);
        if (circulo) g2.fillOval(cx - metade, cy - metade, CAIXA, CAIXA); else g2.fillRect(cx - metade, cy - metade, CAIXA, CAIXA);
        g2.setColor(UITemaGerard.COR_BORDA);
        if (circulo) g2.drawOval(cx - metade, cy - metade, CAIXA, CAIXA); else g2.drawRect(cx - metade, cy - metade, CAIXA, CAIXA);
    }
}
