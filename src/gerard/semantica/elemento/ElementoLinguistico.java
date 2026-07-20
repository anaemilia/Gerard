package gerard.semantica.elemento;

import gerard.semantica.pista.OcorrenciaPista;

public final class ElementoLinguistico implements ElementoSemantico {
    private final String id;
    private final OcorrenciaPista ocorrencia;

    public ElementoLinguistico(String id, OcorrenciaPista ocorrencia) {
        this.id = id == null ? "" : id.trim();
        this.ocorrencia = ocorrencia;
    }

    public String getId() { return id; }
    public TipoElementoSemantico getTipo() { return TipoElementoSemantico.LINGUISTICO; }
    public OcorrenciaPista getOcorrencia() { return ocorrencia; }
}
