package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.semantica.categoria.CatalogoEsquemasCategoriasAditivas;
import gerard.semantica.numero.DominioNumerico;

/**
 * Preserva o sinal do valor inteiro enquanto a representação concreta exibe
 * apenas sua magnitude em unidades. A decisão vem do universo do papel.
 */
public final class PoliticaSinalTransformacaoComplementar {
    private final CatalogoEsquemasCategoriasAditivas esquemas =
            new CatalogoEsquemasCategoriasAditivas();

    public int aplicarSinal(int magnitude, int valorReferencia,
                            Integer valorAnterior) {
        int absoluto = Math.abs(magnitude);
        int referencia = valorReferencia;
        if (referencia == 0 && valorAnterior != null) {
            referencia = valorAnterior.intValue();
        }
        return referencia < 0 ? -absoluto : absoluto;
    }

    public boolean permiteValorAssinado(int indiceSemantico) {
        return permiteValorAssinado(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                indiceSemantico);
    }

    public boolean permiteValorAssinado(
            TipoSituacaoAditiva tipo, int indiceSemantico) {
        return esquemas.obter(tipo).obterDominioCompartilhado(indiceSemantico)
                == DominioNumerico.INTEIROS;
    }

    public Integer normalizarLimiteParaUnidades(Integer limite,
                                                 int indiceSemantico) {
        if (limite == null) {
            return null;
        }
        return permiteValorAssinado(
                        TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                        indiceSemantico)
                ? Integer.valueOf(Math.abs(limite.intValue()))
                : limite;
    }
}
