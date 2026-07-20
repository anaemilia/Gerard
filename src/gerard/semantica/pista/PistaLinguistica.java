package gerard.semantica.pista;

/** Evidência linguística; não determina isoladamente a categoria. */
public final class PistaLinguistica {
    private final String idioma;
    private final String expressao;
    private final TipoPistaLinguistica tipo;

    public PistaLinguistica(String idioma, String expressao,
                            TipoPistaLinguistica tipo) {
        this.idioma = limpar(idioma).toLowerCase();
        this.expressao = limpar(expressao).toLowerCase();
        this.tipo = tipo == null ? TipoPistaLinguistica.OUTRA : tipo;
    }

    public String getIdioma() { return idioma; }
    public String getExpressao() { return expressao; }
    public TipoPistaLinguistica getTipo() { return tipo; }

    private String limpar(String valor) { return valor == null ? "" : valor.trim(); }
}
