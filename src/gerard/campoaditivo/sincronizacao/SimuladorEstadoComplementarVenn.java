package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.transformacao.processo.PoliticaSinalTransformacaoComplementar;
import gerard.campoaditivo.venn.mapeamento.MapeamentoPapeisRepresentacaoComplementar;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

/**
 * Decide, a partir do estado atual dos agrupamentos do diagrama Venn
 * complementar, qual seria o próximo instantâneo do estado semântico
 * compartilhado e se ele respeita os limites de não negatividade das
 * quantidades (exceto onde o sinal é permitido).
 *
 * Extraído de Main.java/TelaGerard: a leitura de estado de interface
 * (contagem de quadradinhos, conversão de texto editável, limite curado)
 * continua a cargo do chamador, que a repassa por função; esta classe
 * concentra apenas a decisão semântica em si.
 */
public final class SimuladorEstadoComplementarVenn {

    private final PoliticaSinalTransformacaoComplementar politicaSinal;

    public SimuladorEstadoComplementarVenn(
            PoliticaSinalTransformacaoComplementar politicaSinal) {
        this.politicaSinal = politicaSinal;
    }

    public EstadoSemanticoCompartilhado.Snapshot simular(
            List<CirculoVenn> circulosVenn,
            MapeamentoPapeisRepresentacaoComplementar mapeamento,
            TipoSituacaoAditiva tipo,
            boolean processoTransformacao,
            EstadoSemanticoCompartilhado.Snapshot anterior,
            int indiceAlteradoVisual,
            int quantidadeProposta,
            ToIntFunction<CirculoVenn> contadorQuadradinhos,
            Function<String, Integer> conversorTexto) {
        if (indiceAlteradoVisual < 0 || indiceAlteradoVisual >= circulosVenn.size()) {
            return null;
        }

        Integer[] valores = new Integer[] { null, null, null };
        boolean[] conhecidos = new boolean[] { false, false, false };

        for (int indiceVisual = 0;
                indiceVisual < 3 && indiceVisual < circulosVenn.size();
                indiceVisual++) {
            int indiceSemantico = mapeamento.paraIndiceSemantico(indiceVisual);
            if (indiceSemantico < 0) {
                continue;
            }
            CirculoVenn no = circulosVenn.get(indiceVisual);
            if (no.exibirQuadradinhos) {
                int quantidade = indiceVisual == indiceAlteradoVisual
                        ? quantidadeProposta
                        : contadorQuadradinhos.applyAsInt(no);
                if (processoTransformacao
                        && politicaSinal.permiteValorAssinado(tipo, indiceSemantico)) {
                    Integer valorAnterior = anterior != null
                            && anterior.isConhecido(indiceSemantico)
                            ? Integer.valueOf(anterior.valorOuZero(indiceSemantico))
                            : null;
                    quantidade = politicaSinal.aplicarSinal(
                            quantidade, no.valorReferencia, valorAnterior);
                }
                valores[indiceSemantico] = Integer.valueOf(quantidade);
                conhecidos[indiceSemantico] = Math.abs(quantidade) > 0
                        || indiceVisual == indiceAlteradoVisual
                        || (anterior != null
                            && anterior.isConhecido(indiceSemantico));
            } else {
                Integer editado = conversorTexto.apply(no.textoEditavel);
                if (editado != null) {
                    valores[indiceSemantico] = editado;
                    conhecidos[indiceSemantico] = true;
                } else if (no.valorReferencia != 0
                        || (anterior != null
                            && anterior.isConhecido(indiceSemantico))) {
                    valores[indiceSemantico] = Integer.valueOf(no.valorReferencia);
                    conhecidos[indiceSemantico] = true;
                }
            }
        }

        EstadoSemanticoCompartilhado simulacao = new EstadoSemanticoCompartilhado();
        return simulacao.atualizar(
                tipo, valores, conhecidos,
                mapeamento.paraIndiceSemantico(indiceAlteradoVisual),
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
    }

    public boolean respeitaLimites(
            EstadoSemanticoCompartilhado.Snapshot snapshot,
            List<CirculoVenn> circulosVenn,
            MapeamentoPapeisRepresentacaoComplementar mapeamento,
            TipoSituacaoAditiva tipo,
            boolean processoTransformacao,
            IntFunction<Integer> limiteSemanticoCuradoDoAgrupamento) {
        if (snapshot == null) {
            return false;
        }
        for (int indiceVisual = 0;
                indiceVisual < 3 && indiceVisual < circulosVenn.size();
                indiceVisual++) {
            CirculoVenn no = circulosVenn.get(indiceVisual);
            if (no == null || !no.exibirQuadradinhos) {
                continue;
            }
            int indiceSemantico = mapeamento.paraIndiceSemantico(indiceVisual);
            int quantidade = snapshot.isConhecido(indiceSemantico)
                    ? snapshot.valorOuZero(indiceSemantico) : 0;
            boolean valorComSinal = processoTransformacao
                    && politicaSinal.permiteValorAssinado(tipo, indiceSemantico);
            if (quantidade < 0 && !valorComSinal) {
                return false;
            }
            // O valor com sinal (transformação/valor relativo) pertence
            // ao conjunto dos inteiros e se move livremente, como no
            // eixo dos inteiros arrastável — não faz sentido travá-lo no
            // valor curado (que é a resposta esperada, não um teto de
            // quantidade). Esse limite só se aplica às representações
            // que são de fato quantidades (estado inicial, estado final
            // etc.).
            if (!valorComSinal) {
                Integer limiteCurado =
                        limiteSemanticoCuradoDoAgrupamento.apply(indiceVisual);
                if (limiteCurado != null
                        && quantidade > Math.max(0,
                                Math.abs(limiteCurado.intValue()))) {
                    return false;
                }
            }
        }
        return true;
    }
}
