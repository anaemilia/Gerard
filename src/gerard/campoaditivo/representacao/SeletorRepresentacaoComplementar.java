package gerard.campoaditivo.representacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/** Centraliza a escolha da representação complementar por categoria. */
public final class SeletorRepresentacaoComplementar {

    public boolean deveExibir(boolean categoriaSelecionada,
                              TipoSituacaoAditiva tipo) {
        return categoriaSelecionada && tipo != null;
    }

    public TipoRepresentacaoComplementar selecionar(TipoSituacaoAditiva tipo,
                                                     boolean cenaComposta) {
        if (!cenaComposta && tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            return TipoRepresentacaoComplementar.PROCESSO_TRANSFORMACAO;
        }
        if (!cenaComposta && tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            return TipoRepresentacaoComplementar.COLECOES_COMPOSICAO;
        }
        if (!cenaComposta && tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            return TipoRepresentacaoComplementar.BARRAS_COMPARACAO;
        }
        return TipoRepresentacaoComplementar.GENERICA;
    }
}
