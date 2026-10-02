package gerard.campoaditivo.representacao;

/** Tipos visuais usados como representação complementar ao diagrama formal. */
public enum TipoRepresentacaoComplementar {
    PROCESSO_TRANSFORMACAO,
    /** Compatibilidade com versões anteriores. */
    TABULEIRO_TRANSFORMACAO,
    COLECOES_COMPOSICAO,
    BARRAS_COMPARACAO,
    /** Historinhas passivas que substituem as representações com funil. */
    HISTORINHAS_CATEGORIA,
    /**
     * Três funis (um por transformação) num único canal — Composição de
     * Transformações (TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES).
     * Adicionado em 2026-08-07, mesma família visual de
     * {@link #PROCESSO_TRANSFORMACAO}, mas sem caixas de estado — ver
     * gerard.campoaditivo.transformacao.composicao.
     */
    PROCESSO_COMPOSICAO_TRANSFORMACOES,
    GENERICA
}
