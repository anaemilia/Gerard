package gerard.semantica.quantidade;

/** Resultado da resolução contextual da grandeza e de sua unidade. */
public final class PerfilQuantidadeSituacao {
    private final GrandezaQuantitativa grandeza;
    private final UnidadeQuantitativa unidade;
    private final boolean definicaoExplicita;
    private final String origem;

    public PerfilQuantidadeSituacao(GrandezaQuantitativa grandeza,
            UnidadeQuantitativa unidade, boolean definicaoExplicita,
            String origem) {
        this.grandeza = grandeza == null ? new GrandezaContagem() : grandeza;
        this.unidade = unidade == null
                ? new UnidadeQuantitativa("UNIDADE", "unidade", "unidades", "")
                : unidade;
        this.definicaoExplicita = definicaoExplicita;
        this.origem = origem == null ? "padrao" : origem.trim();
    }

    public GrandezaQuantitativa getGrandeza() { return grandeza; }
    public UnidadeQuantitativa getUnidade() { return unidade; }
    public TipoGrandezaQuantitativa getTipo() { return grandeza.getTipo(); }
    public boolean isDefinicaoExplicita() { return definicaoExplicita; }
    public String getOrigem() { return origem; }
}
