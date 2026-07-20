package gerard.semantica.elemento;

import gerard.semantica.entidade.EntidadeSemantica;

public final class ElementoEntidade implements ElementoSemantico {
    private final String id;
    private final EntidadeSemantica entidade;

    public ElementoEntidade(String id, EntidadeSemantica entidade) {
        this.id = id == null ? "" : id.trim();
        this.entidade = entidade;
    }

    public String getId() { return id; }
    public TipoElementoSemantico getTipo() { return TipoElementoSemantico.ENTIDADE; }
    public EntidadeSemantica getEntidade() { return entidade; }
}
