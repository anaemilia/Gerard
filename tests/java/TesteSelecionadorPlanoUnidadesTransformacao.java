package gerard.campoaditivo.representacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.transformacao.processo.PlanoUnidadesProcessoTransformacao;

public final class TesteSelecionadorPlanoUnidadesTransformacao {
    public static void main(String[] args) {
        SelecionadorPlanoUnidadesTransformacao seletor = new SelecionadorPlanoUnidadesTransformacao();
        PlanoUnidadesProcessoTransformacao processo = seletor.criarPlano(
                TipoRepresentacaoComplementar.PROCESSO_TRANSFORMACAO,
                snapshot(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, new Integer[] {9, -4, 5}), null);
        exigir(processo != null, "processo simples deve produzir plano");
        exigir("transformacao_negativa".equals(processo.getOrigem(1)),
                "processo simples deve preservar a origem negativa central");

        PlanoUnidadesProcessoTransformacao composicao = seletor.criarPlano(
                TipoRepresentacaoComplementar.PROCESSO_COMPOSICAO_TRANSFORMACOES,
                snapshot(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, new Integer[] {-2, 5, 3}), null);
        exigir(composicao != null, "composicao deve produzir plano");
        exigir("transformacao_negativa".equals(composicao.getOrigem(0))
                        && "situacao_problema".equals(composicao.getOrigem(1))
                        && "situacao_problema".equals(composicao.getOrigem(2)),
                "composicao deve preservar a origem independente de cada papel: "
                        + composicao.getOrigem(0) + ","
                        + composicao.getOrigem(1) + ","
                        + composicao.getOrigem(2));

        exigir(seletor.criarPlano(TipoRepresentacaoComplementar.COLECOES_COMPOSICAO,
                snapshot(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, new Integer[] {2, 3, 5}), null) == null,
                "representacao nao processual nao deve ganhar plano de transformacao");
    }

    private static EstadoSemanticoCompartilhado.Snapshot snapshot(
            TipoSituacaoAditiva tipo, Integer[] valores) {
        return new EstadoSemanticoCompartilhado().atualizar(tipo, valores,
                new boolean[] {true, true, true}, 0,
                EstadoSemanticoCompartilhado.Origem.INICIALIZACAO);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
