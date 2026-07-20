package gerard.semantica.quantidade;

/** Unidade contextual associada à grandeza: bolas, reais, euros etc. */
public final class UnidadeQuantitativa {
    private final String codigo;
    private final String singular;
    private final String plural;
    private final String simbolo;

    public UnidadeQuantitativa(String codigo, String singular,
            String plural, String simbolo) {
        this.codigo = limpar(codigo);
        this.singular = limpar(singular);
        this.plural = limpar(plural);
        this.simbolo = limpar(simbolo);
    }

    public String getCodigo() { return codigo; }
    public String getSingular() { return singular; }
    public String getPlural() { return plural; }
    public String getSimbolo() { return simbolo; }

    public String nomePara(int quantidade) {
        String nome = Math.abs(quantidade) == 1 ? singular : plural;
        return nome.length() > 0 ? nome : simbolo;
    }

    private String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
