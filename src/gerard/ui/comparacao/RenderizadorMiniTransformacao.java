package gerard.ui.comparacao;

import gerard.aplicacao.EstadoNumericoComparacaoCategorias;
import gerard.aplicacao.EstadoNumericoComparacaoCategorias.Papel;
import java.awt.Graphics2D;

/** Transformação de medidas: estado inicial e final ligados por uma seta, com a transformação (círculo) acima. */
final class RenderizadorMiniTransformacao extends RenderizadorMiniCategoria {
    @Override
    public void desenhar(Graphics2D g2, QuadroMiniCategoria q, EstadoNumericoComparacaoCategorias modelo) {
        int w = q.largura(), h = q.altura(), y = 2 * h / 3;
        q.caixa(g2, w / 5, y, valor(modelo, Papel.PRIMEIRA_PARCELA), false);
        q.caixa(g2, w / 2, h / 3, valor(modelo, Papel.SEGUNDA_PARCELA), true);
        q.caixa(g2, 4 * w / 5, y, valor(modelo, Papel.TOTAL), false);
        double r = q.escalaTraco();
        int gap = arredondar(25 * r), ponta = arredondar(35 * r), asa = arredondar(6 * r);
        g2.drawLine(w / 5 + gap, y, 4 * w / 5 - gap, y);
        g2.drawLine(4 * w / 5 - gap, y, 4 * w / 5 - ponta, y - asa);
        g2.drawLine(4 * w / 5 - gap, y, 4 * w / 5 - ponta, y + asa);
    }
}
