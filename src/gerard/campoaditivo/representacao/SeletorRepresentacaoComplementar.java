package gerard.campoaditivo.representacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/** Centraliza a escolha da representação complementar por categoria. */
public final class SeletorRepresentacaoComplementar {

    /**
     * @param categoriaSelecionada categoria de atividade em andamento
     * @param tipo tipo da situação-problema atual
     * @param escaladaDeAjudaNoLimite verdadeiro quando a escalada de
     *        Scaffolding da incógnita atual chegou à última opção
     *        (3ª tentativa rejeitada consecutiva — AG_EMCME, ver
     *        TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md). O material
     *        concreto (este diagrama complementar — quadradinhos, barras,
     *        processo) só aparece nesse momento, como último recurso de
     *        apoio, não durante a modelagem normal (decisão de 2026-08-07).
     *        Quem chama decide o que conta como "chegou no limite" —
     *        este método só aplica a regra de visibilidade dado esse fato.
     */
    public boolean deveExibir(boolean categoriaSelecionada,
                              TipoSituacaoAditiva tipo,
                              boolean escaladaDeAjudaNoLimite) {
        return categoriaSelecionada && tipo != null && escaladaDeAjudaNoLimite;
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
        if (!cenaComposta && tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            return TipoRepresentacaoComplementar.PROCESSO_COMPOSICAO_TRANSFORMACOES;
        }
        return TipoRepresentacaoComplementar.GENERICA;
    }
}
