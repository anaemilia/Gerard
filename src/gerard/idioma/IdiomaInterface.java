package gerard.idioma;

import gerard.i18n.ServicoLocalizacao;

/**
 * Idiomas disponíveis para apresentação da situação-problema na interface.
 * A escolha do idioma é uma decisão de apresentação, separada do módulo
 * de interpretação linguística.
 */
public enum IdiomaInterface {
    PORTUGUES("Português", "PT"),
    INGLES("Inglês", "EN"),
    FRANCES("Francês", "FR"),
    ESPANHOL("Espanhol", "ES");

    private final String nome;
    private final String sigla;

    IdiomaInterface(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
    }

    public String getNome() {
        return ServicoLocalizacao.getInstancia().nomeIdioma(this);
    }

    public String getSigla() {
        return sigla;
    }

    public String getRotuloBotao() {
        return ServicoLocalizacao.getInstancia().formatar("ui.button.language", sigla);
    }
}
