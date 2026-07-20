package gerard.semantica.contexto;

/** Unidade linguística associada a uma quantidade. */
public final class UnidadeMedida {
    private final String singular;
    private final String plural;
    private final String simbolo;

    public UnidadeMedida(String singular, String plural, String simbolo) {
        this.singular = limpar(singular);
        this.plural = limpar(plural);
        this.simbolo = limpar(simbolo);
    }

    public String getSingular() { return singular; }
    public String getPlural() { return plural; }
    public String getSimbolo() { return simbolo; }

    public String formatar(int quantidade) {
        String nome = Math.abs(quantidade) == 1 ? singular : plural;
        return nome.length() > 0 ? nome : simbolo;
    }

    private String limpar(String valor) { return valor == null ? "" : valor.trim(); }
}
