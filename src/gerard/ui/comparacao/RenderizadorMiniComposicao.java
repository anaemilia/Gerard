package gerard.ui.comparacao;

import gerard.aplicacao.EstadoNumericoComparacaoCategorias;
import gerard.aplicacao.EstadoNumericoComparacaoCategorias.Papel;
import java.awt.Graphics2D;

/** Composição de medidas: duas partes à esquerda, ligadas por uma chave ao todo à direita. */
final class RenderizadorMiniComposicao extends RenderizadorMiniCategoria {
    @Override
    public void desenhar(Graphics2D g2, QuadroMiniCategoria q, EstadoNumericoComparacaoCategorias modelo) {
        int w = q.largura(), h = q.altura(), x1 = w / 4, xt = 3 * w / 4;
        q.caixa(g2, x1, h / 3, valor(modelo, Papel.PRIMEIRA_PARCELA), false);
        q.caixa(g2, x1, 2 * h / 3, valor(modelo, Papel.SEGUNDA_PARCELA), false);
        q.caixa(g2, xt, h / 2, valor(modelo, Papel.TOTAL), false);
        double r = q.escalaTraco();
        int bx = w / 2 - arredondar(12 * r);
        g2.drawArc(bx, h / 3 - arredondar(18 * r), arredondar(30 * r), h / 3 + arredondar(36 * r), 270, 180);
    }
}
