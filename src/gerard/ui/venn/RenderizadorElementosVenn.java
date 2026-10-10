package gerard.ui.venn;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.ui.UITemaGerard;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 * Desenho puro dos elementos do diagrama de Venn (coleção da composição, barra da comparação, seta e contagens da
 * composição). Só pinta o que recebe: quantidades, valores formatados e a escolha de qual representação desenhar
 * vêm de fora; nada aqui lê o estado da tela.
 */
public final class RenderizadorElementosVenn {
    private RenderizadorElementosVenn() {
    }

    /** Coleção da composição de medidas: cartão com borda e o rótulo editável centralizado. */
    public static void desenharColecaoComposicao(Graphics2D g2, CirculoVenn circulo) {
        Stroke original = g2.getStroke();
        g2.setColor(UITemaGerard.COR_SUPERFICIE);
        g2.fillRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 16, 16);
        g2.setColor(UITemaGerard.COR_BORDA);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 16, 16);

        if (circulo.textoEditavel != null && circulo.textoEditavel.trim().length() > 0) {
            g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO);
            g2.setFont(new Font("Arial", Font.BOLD, 17));
            FontMetrics fm = g2.getFontMetrics();
            int tx = circulo.x + (circulo.largura - fm.stringWidth(circulo.textoEditavel)) / 2;
            int ty = circulo.y + (circulo.altura - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(circulo.textoEditavel, tx, ty);
        }

        g2.setStroke(original);
    }

    /**
     * Barra da comparação de medidas. Com quadradinhos visíveis só desenha o cartão; sem eles, desenha rótulo e o
     * valor já formatado em "valorFormatado".
     */
    public static void desenharBarraComparacao(Graphics2D g2, CirculoVenn circulo, String valorFormatado) {
        Stroke original = g2.getStroke();
        if (circulo.exibirQuadradinhos) {
            g2.setColor(UITemaGerard.COR_SUPERFICIE);
            g2.fillRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 12, 12);
            g2.setColor(UITemaGerard.COR_BORDA);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 12, 12);
        } else {
            g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fmRotulo = g2.getFontMetrics();
            int xRotulo = circulo.x + (circulo.largura - fmRotulo.stringWidth(circulo.rotulo)) / 2;
            g2.drawString(circulo.rotulo, xRotulo, circulo.y - 8);

            g2.setColor(UITemaGerard.COR_SUPERFICIE);
            g2.fillRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 12, 12);
            g2.setColor(UITemaGerard.COR_BORDA);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(circulo.x, circulo.y, circulo.largura, circulo.altura, 12, 12);

            g2.setColor(UITemaGerard.COR_TEXTO);
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            FontMetrics fmValor = g2.getFontMetrics();
            int xValor = circulo.x + (circulo.largura - fmValor.stringWidth(valorFormatado)) / 2;
            int yValor = circulo.y + (circulo.altura - fmValor.getHeight()) / 2 + fmValor.getAscent();
            g2.drawString(valorFormatado, xValor, yValor);
        }
        g2.setStroke(original);
    }

    /** Contagens ao lado das três coleções da composição e a equação "a + b = total" no rodapé da área. */
    public static void desenharContagensComposicao(
            Graphics2D g2,
            Rectangle area,
            CirculoVenn parcela1,
            CirculoVenn parcela2,
            CirculoVenn resultado,
            int quantidade1,
            int quantidade2,
            int quantidadeResultado,
            int totalComposicao
    ) {
        g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO);
        g2.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fm = g2.getFontMetrics();

        desenharNumeroCentralizado(g2, String.valueOf(quantidade1), parcela1.x + parcela1.largura + 22, parcela1.y + parcela1.altura / 2 + fm.getAscent() / 2);
        desenharNumeroCentralizado(g2, String.valueOf(quantidade2), parcela2.x + parcela2.largura + 22, parcela2.y + parcela2.altura / 2 + fm.getAscent() / 2);
        // O número junto à coleção resultado representa a quantidade real
        // de quadradinhos dentro dela. O ponto de ancoragem é ajustado
        // para permanecer confortavelmente dentro do card do diagrama.
        int ancoraResultado = Math.min(area.x + area.width - 26, resultado.x + resultado.largura + 18);
        desenharNumeroCentralizado(g2, String.valueOf(quantidadeResultado), ancoraResultado, resultado.y + resultado.altura / 2 + fm.getAscent() / 2);

        String equacao = quantidade1 + " + " + quantidade2 + " = " + totalComposicao;
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fmEquacao = g2.getFontMetrics();
        int xEquacao = area.x + Math.max(18, (area.width - fmEquacao.stringWidth(equacao)) / 2);
        int yEquacao = area.y + area.height - 18;
        g2.drawString(equacao, xEquacao, yEquacao);
    }

    private static void desenharNumeroCentralizado(Graphics2D g2, String texto, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, x - fm.stringWidth(texto) / 2, y);
    }

    public static void desenharSeta(Graphics2D g2, int x1, int y1, int x2, int y2) {
        Stroke original = g2.getStroke();

        g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawLine(x1, y1, x2, y2);

        double angulo = Math.atan2(y2 - y1, x2 - x1);
        int tamanho = 10;

        int xA = (int) (x2 - tamanho * Math.cos(angulo - Math.PI / 6));
        int yA = (int) (y2 - tamanho * Math.sin(angulo - Math.PI / 6));

        int xB = (int) (x2 - tamanho * Math.cos(angulo + Math.PI / 6));
        int yB = (int) (y2 - tamanho * Math.sin(angulo + Math.PI / 6));

        g2.drawLine(x2, y2, xA, yA);
        g2.drawLine(x2, y2, xB, yB);

        g2.setStroke(original);
    }
}
