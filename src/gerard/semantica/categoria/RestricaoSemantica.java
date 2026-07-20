package gerard.semantica.categoria;

/** Restrição declarativa do esquema, sem dependência da interface gráfica. */
public final class RestricaoSemantica {
    private final String id;
    private final String descricao;

    public RestricaoSemantica(String id, String descricao) {
        this.id = id == null ? "" : id.trim();
        this.descricao = descricao == null ? "" : descricao.trim();
    }

    public String getId() { return id; }
    public String getDescricao() { return descricao; }
}
