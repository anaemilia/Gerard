package gerard.campoaditivo.modelo;

import gerard.i18n.ServicoLocalizacao;

public enum TipoSituacaoAditiva {
    COMPOSICAO_MEDIDAS("Composição de medidas", "CM"),
    TRANSFORMACAO_MEDIDAS("Transformação de medidas", "TM"),
    COMPOSICAO_TRANSFORMACAO_MEDIDAS("Composição seguida de transformação", "CMT"),
    COMPARACAO_MEDIDAS("Comparação de medidas", "COP"),
    COMPOSICAO_TRANSFORMACOES("Composição de transformações", "CT"),
    TRANSFORMACAO_COMPOSTA_DOIS_PASSOS("Transformação composta em dois passos", "TCP"),
    TRANSFORMACAO_RELACAO("Transformação de uma relação", "TR"),
    COMPOSICAO_RELACOES("Composição de relações", "CR");

    private final String descricao;
    private final String sigla;

    TipoSituacaoAditiva(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public String getDescricao() {
        return ServicoLocalizacao.getInstancia().descricaoTipo(this);
    }

    public String getSigla() {
        return sigla;
    }

    public String getRotuloBotao() {
        return sigla + " - " + ServicoLocalizacao.getInstancia().descricaoTipo(this);
    }
}
