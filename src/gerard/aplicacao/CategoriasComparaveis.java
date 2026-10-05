package gerard.aplicacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Conhecimento da tela "Comparar categorias" que não é desenho: quais categorias de medidas se
 * comparam, qual papel numérico cada posição da representação ocupa em cada categoria e qual chave
 * de texto narra o problema. A tela só desenha e pergunta; não decide por categoria.
 */
public final class CategoriasComparaveis {
    private static final List<TipoSituacaoAditiva> CATEGORIAS = Collections.unmodifiableList(Arrays.asList(
            TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
            TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
            TipoSituacaoAditiva.COMPARACAO_MEDIDAS));

    private CategoriasComparaveis() {
    }

    /** As categorias de medidas que a tela compara, na ordem de exibição. */
    public static List<TipoSituacaoAditiva> todas() {
        return CATEGORIAS;
    }

    /**
     * Papel numérico representado na posição {@code indice} (0, 1 ou 2) da representação da categoria.
     * Na comparação de medidas a primeira posição é o total; nas demais, a primeira parcela.
     */
    public static EstadoNumericoComparacaoCategorias.Papel papelNoIndice(TipoSituacaoAditiva categoria, int indice) {
        if (categoria == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            if (indice == 0) return EstadoNumericoComparacaoCategorias.Papel.TOTAL;
            if (indice == 1) return EstadoNumericoComparacaoCategorias.Papel.PRIMEIRA_PARCELA;
            return EstadoNumericoComparacaoCategorias.Papel.SEGUNDA_PARCELA;
        }
        if (indice == 0) return EstadoNumericoComparacaoCategorias.Papel.PRIMEIRA_PARCELA;
        if (indice == 1) return EstadoNumericoComparacaoCategorias.Papel.SEGUNDA_PARCELA;
        return EstadoNumericoComparacaoCategorias.Papel.TOTAL;
    }

    /** Chave de i18n do texto que narra o problema da categoria. */
    public static String chaveTextoDoProblema(TipoSituacaoAditiva categoria) {
        if (categoria == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) return "ui.compare.problem.composition";
        if (categoria == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) return "ui.compare.problem.transformation";
        return "ui.compare.problem.comparison";
    }
}
