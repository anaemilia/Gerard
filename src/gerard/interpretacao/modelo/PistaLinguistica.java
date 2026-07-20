package gerard.interpretacao.modelo;

public class PistaLinguistica {
    private final IdiomaProblema idioma;
    private final CategoriaProblema categoria;
    private final String expressao;
    private final int peso;

    public PistaLinguistica(IdiomaProblema idioma, CategoriaProblema categoria, String expressao, int peso) {
        this.idioma = idioma;
        this.categoria = categoria;
        this.expressao = expressao;
        this.peso = peso;
    }

    public IdiomaProblema getIdioma() {
        return idioma;
    }

    public CategoriaProblema getCategoria() {
        return categoria;
    }

    public String getExpressao() {
        return expressao;
    }

    public int getPeso() {
        return peso;
    }
}
