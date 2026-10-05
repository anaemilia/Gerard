package gerard.ui.comparacao;

import gerard.aplicacao.EstadoNumericoComparacaoCategorias;
import gerard.aplicacao.EstadoNumericoComparacaoCategorias.Papel;
import java.awt.Graphics2D;

/** Comparação de medidas: quadrado de cima (total) e de baixo (parcela), seta de baixo para cima e a diferença (círculo). */
final class RenderizadorMiniComparacao extends RenderizadorMiniCategoria {
    @Override
    public void desenhar(Graphics2D g2, QuadroMiniCategoria q, EstadoNumericoComparacaoCategorias modelo) {
        int w = q.largura(), h = q.altura(), x = w / 2;
        q.caixa(g2, x, h / 5, valor(modelo, Papel.TOTAL), false);
        q.caixa(g2, x, h * 4 / 5, valor(modelo, Papel.PRIMEIRA_PARCELA), false);
        q.caixa(g2, x + w / 4, h / 2, valor(modelo, Papel.SEGUNDA_PARCELA), true);
        double r = q.escalaTraco();
        int gap = arredondar(24 * r), ponta = arredondar(34 * r), asa = arredondar(6 * r);
        g2.drawLine(x, h / 5 + gap, x, h * 4 / 5 - gap);
        // Seta sai do quadrado de baixo para o de cima (2026-08-07, mesma correção do ícone de atalho).
        g2.drawLine(x, h / 5 + gap, x - asa, h / 5 + ponta);
        g2.drawLine(x, h / 5 + gap, x + asa, h / 5 + ponta);
    }
}
