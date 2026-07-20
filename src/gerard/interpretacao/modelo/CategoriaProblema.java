package gerard.interpretacao.modelo;

import gerard.i18n.ServicoLocalizacao;

public enum CategoriaProblema {
    COMPOSICAO_MEDIDAS("Composição de medidas", "CM"),
    TRANSFORMACAO_MEDIDAS("Transformação de medidas", "TM"),
    COMPOSICAO_TRANSFORMACAO_MEDIDAS("Composição seguida de transformação", "CMT"),
    COMPARACAO_MEDIDAS("Comparação de medidas", "COP"),
    COMPOSICAO_TRANSFORMACOES("Composição de transformações", "CT"),
    TRANSFORMACAO_COMPOSTA_DOIS_PASSOS("Transformação composta em dois passos", "TCP"),
    TRANSFORMACAO_RELACAO("Transformação de uma relação", "TR"),
    COMPOSICAO_RELACOES("Composição de relações", "CR"),
    MULTIPLICACAO("Multiplicação", "MUL"),
    DIVISAO_PARTES("Divisão por partes", "DP"),
    DIVISAO_COTAS("Divisão por cotas", "DC"),
    INDEFINIDA("Indefinida", "IND");

    private final String descricao;
    private final String sigla;

    CategoriaProblema(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public String getDescricao() {
        return ServicoLocalizacao.getInstancia().descricaoCategoria(this);
    }

    public String getSigla() {
        return sigla;
    }
}
