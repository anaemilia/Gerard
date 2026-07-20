package gerard.interpretacao.modelo;

import gerard.i18n.ServicoLocalizacao;

public enum IdiomaProblema {
    PORTUGUES("Portugues", "pt"),
    INGLES("Ingles", "en"),
    FRANCES("Frances", "fr"),
    ESPANHOL("Espanhol", "es"),
    INDEFINIDO("Indefinido", "ind");

    private final String descricao;
    private final String codigo;

    IdiomaProblema(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return ServicoLocalizacao.getInstancia().nomeIdiomaDetectado(this);
    }

    public String getCodigo() {
        return codigo;
    }
}
