package gerard.semantica.contexto;

/** Bolas, carrinhos, figurinhas, reais, metros e outros referentes do enunciado. */
public final class ReferenteContextual {
    private final String id;
    private final String nome;
    private final TipoReferenteContextual tipo;
    private final UnidadeMedida unidade;

    public ReferenteContextual(String id, String nome,
                               TipoReferenteContextual tipo,
                               UnidadeMedida unidade) {
        this.id = limpar(id);
        this.nome = limpar(nome);
        this.tipo = tipo == null ? TipoReferenteContextual.OUTRO : tipo;
        this.unidade = unidade;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public TipoReferenteContextual getTipo() { return tipo; }
    public UnidadeMedida getUnidade() { return unidade; }

    private String limpar(String valor) { return valor == null ? "" : valor.trim(); }
}
