package gerard.idioma;

/**
 * Representa uma situação-problema em um idioma específico.
 * Esta classe mantém a tradução separada da interface gráfica.
 */
public class SituacaoProblemaTraduzida {
    private final IdiomaInterface idioma;
    private final String enunciado;

    public SituacaoProblemaTraduzida(IdiomaInterface idioma, String enunciado) {
        this.idioma = idioma;
        this.enunciado = enunciado;
    }

    public IdiomaInterface getIdioma() {
        return idioma;
    }

    public String getEnunciado() {
        return enunciado;
    }
}
