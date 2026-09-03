package gerard.campoaditivo.representacao;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.transformacao.composicao.SincronizadorUnidadesComposicaoTransformacoes;
import gerard.campoaditivo.transformacao.processo.PlanoUnidadesProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.SincronizadorUnidadesProcessoTransformacao;

/**
 * Despacha a projeção semântica de unidades para a representação
 * complementar selecionada, sem conhecer Swing ou geometria de tela.
 */
public final class SelecionadorPlanoUnidadesTransformacao {
    private final SincronizadorUnidadesProcessoTransformacao sincronizadorProcesso;
    private final SincronizadorUnidadesComposicaoTransformacoes sincronizadorComposicao;

    public SelecionadorPlanoUnidadesTransformacao() {
        this(new SincronizadorUnidadesProcessoTransformacao(),
                new SincronizadorUnidadesComposicaoTransformacoes());
    }

    SelecionadorPlanoUnidadesTransformacao(
            SincronizadorUnidadesProcessoTransformacao sincronizadorProcesso,
            SincronizadorUnidadesComposicaoTransformacoes sincronizadorComposicao) {
        this.sincronizadorProcesso = sincronizadorProcesso;
        this.sincronizadorComposicao = sincronizadorComposicao;
    }

    public PlanoUnidadesProcessoTransformacao criarPlano(
            TipoRepresentacaoComplementar representacao,
            EstadoSemanticoCompartilhado.Snapshot snapshot,
            SituacaoProblemaAditiva situacao) {
        if (representacao == TipoRepresentacaoComplementar.PROCESSO_TRANSFORMACAO) {
            return sincronizadorProcesso.criarPlano(snapshot, situacao);
        }
        if (representacao
                == TipoRepresentacaoComplementar.PROCESSO_COMPOSICAO_TRANSFORMACOES) {
            return sincronizadorComposicao.criarPlano(snapshot, situacao);
        }
        return null;
    }
}
