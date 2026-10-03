package gerard.dominio.campoaditivo.ajuda;

/**
 * Apoio visual que a cena pode carregar para a tela (historinha ilustrada, historinha
 * de texto, ...). Descreve o apoio sem tecnologia de apresentação: Swing e React o
 * renderizam por {@link #getTipo()}. Quem decide se e qual apoio existe é a
 * {@link PoliticaApoioVisual}; nenhum cliente escolhe entre apoios.
 */
public interface ApoioVisual {
    String TIPO_HISTORINHA_ILUSTRADA = "HISTORINHA_ILUSTRADA";
    String TIPO_HISTORINHA_TEXTUAL = "HISTORINHA_TEXTUAL";

    String getTipo();

    String getIdentificador();
}
