package gerard.campoaditivo.modelo;

public enum TipoSituacaoAditiva {
    COMPOSICAO_MEDIDAS("tipo.composicao_medidas", "CM"),
    TRANSFORMACAO_MEDIDAS("tipo.transformacao_medidas", "TM"),
    COMPOSICAO_TRANSFORMACAO_MEDIDAS("tipo.composicao_transformacao_medidas", "CMT"),
    COMPARACAO_MEDIDAS("tipo.comparacao_medidas", "COP"),
    COMPOSICAO_TRANSFORMACOES("tipo.composicao_transformacoes", "CT"),
    TRANSFORMACAO_COMPOSTA_DOIS_PASSOS("tipo.transformacao_composta_dois_passos", "TCP"),
    TRANSFORMACAO_RELACAO("tipo.transformacao_relacao", "TR"),
    COMPOSICAO_RELACOES("tipo.composicao_relacoes", "CR");

    private final String chaveDescricao;
    private final String sigla;

    TipoSituacaoAditiva(String chaveDescricao, String sigla) {
        this.chaveDescricao = chaveDescricao;
        this.sigla = sigla;
    }

    public String getChaveDescricao() {
        return chaveDescricao;
    }

    public String getSigla() {
        return sigla;
    }
}
