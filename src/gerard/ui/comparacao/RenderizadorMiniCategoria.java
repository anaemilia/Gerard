package gerard.ui.comparacao;

import gerard.aplicacao.EstadoNumericoComparacaoCategorias;
import gerard.aplicacao.EstadoNumericoComparacaoCategorias.Papel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.awt.Graphics2D;

/**
 * Desenho da mini-representação de UMA categoria na tela "Comparar categorias" (polimórfico: cada
 * categoria sabe onde pintar suas caixas, setas e chaves). Com {@code modelo} nulo pinta a representação
 * formal da categoria (vazia, estática); com modelo, a instância preenchida e interativa. A tela só
 * pergunta qual renderizador usar; não decide por categoria.
 */
public abstract class RenderizadorMiniCategoria {

    public static RenderizadorMiniCategoria para(TipoSituacaoAditiva categoria) {
        if (categoria == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) return new RenderizadorMiniComposicao();
        if (categoria == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) return new RenderizadorMiniTransformacao();
        return new RenderizadorMiniComparacao();
    }

    public abstract void desenhar(Graphics2D g2, QuadroMiniCategoria quadro,
            EstadoNumericoComparacaoCategorias modelo);

    /** Valor do papel, ou nulo na representação formal. */
    static Integer valor(EstadoNumericoComparacaoCategorias modelo, Papel papel) {
        if (modelo == null) return null;
        if (papel == Papel.PRIMEIRA_PARCELA) return modelo.getPrimeiraParcela();
        if (papel == Papel.SEGUNDA_PARCELA) return modelo.getSegundaParcela();
        return modelo.getTotal();
    }

    static int arredondar(double v) {
        return (int) Math.round(v);
    }
}
