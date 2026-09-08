package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.transformacao.processo.PoliticaSinalTransformacaoComplementar;
import gerard.campoaditivo.venn.mapeamento.MapeamentoPapeisRepresentacaoComplementar;
import gerard.campoaditivo.sincronizacao.representacoes.CapturadorValoresRepresentacaoComplementar;
import gerard.campoaditivo.sincronizacao.representacoes.ValoresCapturadosRepresentacaoComplementar;

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
    private final CapturadorValoresRepresentacaoComplementar capturador;

    public SimuladorEstadoComplementarVenn(
            PoliticaSinalTransformacaoComplementar politicaSinal) {
        this.politicaSinal = politicaSinal;
        this.capturador = new CapturadorValoresRepresentacaoComplementar(politicaSinal);
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

        ValoresCapturadosRepresentacaoComplementar captura = capturador.capturar(
                circulosVenn, mapeamento, tipo, processoTransformacao, anterior,
                indiceAlteradoVisual, false,
                (indice, no) -> indice.intValue() == indiceAlteradoVisual
                        ? Integer.valueOf(quantidadeProposta)
                        : Integer.valueOf(contadorQuadradinhos.applyAsInt(no)),
                conversorTexto);

        EstadoSemanticoCompartilhado simulacao = new EstadoSemanticoCompartilhado();
        return simulacao.atualizar(
                tipo, captura.getValores(), captura.getConhecidos(),
                captura.getIndiceAlteradoSemantico(),
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
    }

    /**
     * Simula a edição direta de um papel cujo domínio admite inteiros. O nó
     * alterado recebe o valor assinado proposto; nos demais nós concretos, a
     * magnitude continua vindo das unidades e o sinal é preservado pela
     * política do papel.
     */
    public EstadoSemanticoCompartilhado.Snapshot simularValorAssinado(
            List<CirculoVenn> circulosVenn,
            MapeamentoPapeisRepresentacaoComplementar mapeamento,
            TipoSituacaoAditiva tipo,
            boolean processoTransformacao,
            EstadoSemanticoCompartilhado.Snapshot anterior,
            int indiceAlteradoVisual,
            int valorAssinadoProposto,
            ToIntFunction<CirculoVenn> contadorQuadradinhos,
            Function<String, Integer> conversorTexto) {
        if (circulosVenn == null || mapeamento == null
                || indiceAlteradoVisual < 0
                || indiceAlteradoVisual >= circulosVenn.size()) {
            return null;
        }

        ValoresCapturadosRepresentacaoComplementar captura = capturador.capturar(
                circulosVenn, mapeamento, tipo, processoTransformacao, anterior,
                indiceAlteradoVisual, false,
                (indice, no) -> {
                    if (indice.intValue() == indiceAlteradoVisual) {
                        return Integer.valueOf(valorAssinadoProposto);
                    }
                    return Integer.valueOf(contadorQuadradinhos.applyAsInt(no));
                },
                conversorTexto);

        Integer[] valores = captura.getValores();
        int indiceAlteradoSemantico = captura.getIndiceAlteradoSemantico();
        if (indiceAlteradoSemantico >= 0
                && indiceAlteradoSemantico < valores.length) {
            valores[indiceAlteradoSemantico] =
                    Integer.valueOf(valorAssinadoProposto);
        }
        EstadoSemanticoCompartilhado simulacao =
                new EstadoSemanticoCompartilhado();
        return simulacao.atualizar(
                tipo, valores, captura.getConhecidos(),
                indiceAlteradoSemantico,
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
