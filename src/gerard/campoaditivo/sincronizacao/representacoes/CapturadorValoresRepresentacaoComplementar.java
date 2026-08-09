package gerard.campoaditivo.sincronizacao.representacoes;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.transformacao.processo.PoliticaSinalTransformacaoComplementar;
import gerard.campoaditivo.venn.mapeamento.MapeamentoPapeisRepresentacaoComplementar;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Traduz observações da representação complementar para a ordem semântica.
 * Não atualiza nem resolve o estado do domínio.
 */
public final class CapturadorValoresRepresentacaoComplementar {

    private final PoliticaSinalTransformacaoComplementar politicaSinal;

    public CapturadorValoresRepresentacaoComplementar(
            PoliticaSinalTransformacaoComplementar politicaSinal) {
        if (politicaSinal == null) {
            throw new IllegalArgumentException("politicaSinal não pode ser nula");
        }
        this.politicaSinal = politicaSinal;
    }

    public ValoresCapturadosRepresentacaoComplementar capturar(
            List<CirculoVenn> agrupamentos,
            MapeamentoPapeisRepresentacaoComplementar mapeamento,
            TipoSituacaoAditiva tipo,
            boolean processoTransformacao,
            EstadoSemanticoCompartilhado.Snapshot anterior,
            int indiceAlteradoVisual,
            boolean alteracaoTextualSemTextoContaComoConhecida,
            BiFunction<Integer, CirculoVenn, Integer> leitorQuantidade,
            Function<String, Integer> conversorTexto) {
        Integer[] valores = new Integer[] {null, null, null};
        boolean[] conhecidos = new boolean[] {false, false, false};
        if (agrupamentos == null || mapeamento == null) {
            return new ValoresCapturadosRepresentacaoComplementar(
                    valores, conhecidos, -1);
        }

        for (int indiceVisual = 0;
                indiceVisual < 3 && indiceVisual < agrupamentos.size();
                indiceVisual++) {
            int indiceSemantico = mapeamento.paraIndiceSemantico(indiceVisual);
            if (indiceSemantico < 0 || indiceSemantico >= valores.length) {
                continue;
            }
            CirculoVenn agrupamento = agrupamentos.get(indiceVisual);
            if (agrupamento.exibirQuadradinhos) {
                int quantidade = leitorQuantidade.apply(
                        Integer.valueOf(indiceVisual), agrupamento).intValue();
                if (processoTransformacao
                        && politicaSinal.permiteValorAssinado(tipo, indiceSemantico)) {
                    Integer valorAnterior = anterior != null
                            && anterior.isConhecido(indiceSemantico)
                            ? Integer.valueOf(anterior.valorOuZero(indiceSemantico))
                            : null;
                    quantidade = politicaSinal.aplicarSinal(
                            quantidade, agrupamento.valorReferencia, valorAnterior);
                }
                valores[indiceSemantico] = Integer.valueOf(quantidade);
                conhecidos[indiceSemantico] = Math.abs(quantidade) > 0
                        || indiceVisual == indiceAlteradoVisual
                        || conhecidoAnteriormente(anterior, indiceSemantico);
            } else {
                Integer editado = conversorTexto.apply(agrupamento.textoEditavel);
                if (editado != null) {
                    valores[indiceSemantico] = editado;
                    conhecidos[indiceSemantico] = true;
                } else if (agrupamento.valorReferencia != 0
                        || (alteracaoTextualSemTextoContaComoConhecida
                            && indiceVisual == indiceAlteradoVisual)
                        || conhecidoAnteriormente(anterior, indiceSemantico)) {
                    valores[indiceSemantico] = Integer.valueOf(
                            agrupamento.valorReferencia);
                    conhecidos[indiceSemantico] = true;
                }
            }
        }
        return new ValoresCapturadosRepresentacaoComplementar(
                valores, conhecidos,
                mapeamento.paraIndiceSemantico(indiceAlteradoVisual));
    }

    private boolean conhecidoAnteriormente(
            EstadoSemanticoCompartilhado.Snapshot anterior, int indiceSemantico) {
        return anterior != null && anterior.isConhecido(indiceSemantico);
    }
}
