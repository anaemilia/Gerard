package gerard.semantica.elemento;

import gerard.semantica.contexto.ReferenteContextual;

public final class ElementoContextual implements ElementoSemantico {
    private final String id;
    private final ReferenteContextual referente;

    public ElementoContextual(String id, ReferenteContextual referente) {
        this.id = id == null ? "" : id.trim();
        this.referente = referente;
    }

    public String getId() { return id; }
    public TipoElementoSemantico getTipo() { return TipoElementoSemantico.CONTEXTUAL; }
    public ReferenteContextual getReferente() { return referente; }
}
