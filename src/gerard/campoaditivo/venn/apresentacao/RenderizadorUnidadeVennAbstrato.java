package gerard.campoaditivo.venn.apresentacao;

import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 * Implementação-base do estilo 2,5D discreto. A herança concentra somente o
 * comportamento visual realmente comum; as subclasses fornecem as paletas.
 */
public abstract class RenderizadorUnidadeVennAbstrato implements RenderizadorUnidadeVenn {

    protected abstract Color corBase();
    protected abstract Color corBorda();
    protected abstract Color corTexto();

    @Override
    public final void desenhar(Graphics2D g2, QuadradinhoVenn unidade, EstadoVisualUnidadeVenn estado) {
        if (g2 == null || unidade == null) {
            return;
        }

        EstadoVisualUnidadeVenn estadoEfetivo = estado == null
                ? EstadoVisualUnidadeVenn.NORMAL : estado;

        Object antialiasAnterior = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke strokeAnterior = g2.getStroke();
        Composite compositeAnterior = g2.getComposite();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int elevacao = estadoEfetivo == EstadoVisualUnidadeVenn.ARRASTADA ? 2
                : estadoEfetivo == EstadoVisualUnidadeVenn.FOCADA ? 1 : 0;
        int deslocamentoSombra = estadoEfetivo == EstadoVisualUnidadeVenn.ARRASTADA ? 3
                : estadoEfetivo == EstadoVisualUnidadeVenn.FOCADA ? 2 : 1;
        float alphaSombra = estadoEfetivo == EstadoVisualUnidadeVenn.ARRASTADA ? 0.28f
                : estadoEfetivo == EstadoVisualUnidadeVenn.FOCADA ? 0.20f : 0.12f;

        int x = unidade.x;
        int y = unidade.y - elevacao;
        int tamanho = unidade.tamanho;
        int arco = Math.max(3, Math.min(6, tamanho / 3));

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaSombra));
        g2.setColor(new Color(22, 35, 48));
        g2.fillRoundRect(x + deslocamentoSombra, y + deslocamentoSombra,
                tamanho, tamanho, arco, arco);

        boolean concluido = unidade.isConclusaoDestacada();
        Color corBaseEfetiva = concluido ? gerard.ui.UITemaGerard.COR_SUCESSO_FUNDO : corBase();
        Color corBordaEfetiva = concluido ? gerard.ui.UITemaGerard.COR_SUCESSO : corBorda();

        g2.setComposite(compositeAnterior);
        g2.setColor(corBaseEfetiva);
        g2.fillRoundRect(x, y, tamanho, tamanho, arco, arco);

        int intensidade = estadoEfetivo == EstadoVisualUnidadeVenn.NORMAL ? 46 : 76;
        g2.setColor(comAlpha(Color.WHITE, intensidade));
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 2, y + 1, x + tamanho - 3, y + 1);
        g2.drawLine(x + 1, y + 2, x + 1, y + tamanho - 3);

        g2.setColor(comAlpha(escurecer(corBordaEfetiva, 0.82),
                estadoEfetivo == EstadoVisualUnidadeVenn.NORMAL ? 72 : 105));
        g2.drawLine(x + 2, y + tamanho - 1, x + tamanho - 3, y + tamanho - 1);
        g2.drawLine(x + tamanho - 1, y + 2, x + tamanho - 1, y + tamanho - 3);

        g2.setColor(corBordaEfetiva);
        g2.setStroke(new BasicStroke(concluido ? 2.0f
                : estadoEfetivo == EstadoVisualUnidadeVenn.ARRASTADA ? 1.35f : 0.95f));
        g2.drawRoundRect(x, y, tamanho, tamanho, arco, arco);

        if (estadoEfetivo != EstadoVisualUnidadeVenn.NORMAL) {
            g2.setColor(comAlpha(clarear(corBaseEfetiva, 1.18), 78));
            g2.setStroke(new BasicStroke(0.8f));
            g2.drawRoundRect(x + 2, y + 2, Math.max(1, tamanho - 4),
                    Math.max(1, tamanho - 4), Math.max(2, arco - 1), Math.max(2, arco - 1));
        }

        desenharTexto(g2, unidade, x, y, tamanho, concluido);

        g2.setComposite(compositeAnterior);
        g2.setStroke(strokeAnterior);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasAnterior);
    }

    private void desenharTexto(Graphics2D g2, QuadradinhoVenn unidade, int x, int y, int tamanho,
                                boolean concluido) {
        if (unidade.textoEditavel == null || unidade.textoEditavel.trim().length() == 0) {
            return;
        }
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        String texto = unidade.textoEditavel;
        while (texto.length() > 1 && fm.stringWidth(texto) > tamanho - 3) {
            texto = texto.substring(0, texto.length() - 1);
        }
        int tx = x + (tamanho - fm.stringWidth(texto)) / 2;
        int ty = y + (tamanho - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(concluido ? gerard.ui.UITemaGerard.COR_SUCESSO_TEXTO : corTexto());
        g2.drawString(texto, tx, ty);
    }

    private static Color comAlpha(Color cor, int alpha) {
        return new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    private static Color clarear(Color cor, double fator) {
        return new Color(
                limitar((int) Math.round(cor.getRed() * fator)),
                limitar((int) Math.round(cor.getGreen() * fator)),
                limitar((int) Math.round(cor.getBlue() * fator)));
    }

    private static Color escurecer(Color cor, double fator) {
        return new Color(
                limitar((int) Math.round(cor.getRed() * fator)),
                limitar((int) Math.round(cor.getGreen() * fator)),
                limitar((int) Math.round(cor.getBlue() * fator)));
    }

    private static int limitar(int valor) {
        return Math.max(0, Math.min(255, valor));
    }
}
