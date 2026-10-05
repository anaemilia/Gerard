package gerard.ui.comparacao;

import gerard.ui.UITemaGerard;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

/**
 * Superfície de desenho de uma mini-representação da tela "Comparar categorias": conhece o tamanho do
 * painel, o tamanho proporcional das caixas e como pintar uma caixa/círculo (vazio ou com valor). Cada
 * categoria decide só ONDE pintar (ver {@link RenderizadorMiniCategoria}). Não conhece o modelo numérico.
 */
public final class QuadroMiniCategoria {
    private final int largura;
    private final int altura;
    private final List<Rectangle> alvos;

    /** @param alvos lista preenchida com a área de cada caixa/círculo COM valor, na ordem em que são pintados. */
    public QuadroMiniCategoria(int largura, int altura, List<Rectangle> alvos) {
        this.largura = largura;
        this.altura = altura;
        this.alvos = alvos;
    }

    public int largura() { return largura; }

    public int altura() { return altura; }

    /** Tamanho das caixas/círculos proporcional ao espaço real do painel. */
    public int tamanhoCaixa() {
        return Math.max(30, Math.min(70, Math.min(largura, altura) / 4));
    }

    /** Fator de escala dos pequenos deslocamentos (folgas, pontas de seta) ao redor das caixas. */
    public double escalaTraco() {
        return tamanhoCaixa() / 44.0;
    }

    /** Fonte dos valores, proporcional ao tamanho das caixas. */
    public Font fonteDosValores() {
        return new Font("Arial", Font.BOLD, Math.max(13, Math.min(26, tamanhoCaixa() / 3)));
    }

    /**
     * Pinta uma caixa (ou círculo) centrada em (x,y). Com {@code valor} nulo a forma é só a referência
     * estrutural (vazia, sem alvo de edição); com valor, registra a área como alvo e escreve o número.
     * Ao final deixa o contexto gráfico na cor do traço, para as setas e chaves que a categoria desenha.
     */
    void caixa(Graphics2D g2, int x, int y, Integer valor, boolean circulo) {
        int tamanho = tamanhoCaixa();
        Rectangle r = new Rectangle(x - tamanho / 2, y - tamanho / 2, tamanho, tamanho);
        if (valor != null) {
            alvos.add(r);
        }
        g2.setColor(UITemaGerard.COR_SUPERFICIE);
        if (circulo) g2.fillOval(r.x, r.y, r.width, r.height); else g2.fillRect(r.x, r.y, r.width, r.height);
        g2.setColor(UITemaGerard.COR_BORDA);
        if (circulo) g2.drawOval(r.x, r.y, r.width, r.height); else g2.drawRect(r.x, r.y, r.width, r.height);
        if (valor != null) {
            String t = String.valueOf(valor);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(UITemaGerard.COR_TEXTO);
            g2.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getAscent() / 2 - 2);
        }
    }
}
