package gerard.semantica.elemento;

import gerard.semantica.numero.ValorNumerico;
import gerard.semantica.papel.PapelQuantitativo;

public final class ElementoNumerico implements ElementoSemantico {
    private final String id;
    private final PapelQuantitativo papel;
    private final ValorNumerico valor;
    private final String trechoOriginal;

    public ElementoNumerico(String id, PapelQuantitativo papel,
                            ValorNumerico valor, String trechoOriginal) {
        if (papel == null || valor == null || !papel.aceita(valor)) {
            throw new IllegalArgumentException("Valor incompatível com o papel semântico.");
        }
        this.id = id == null ? "" : id.trim();
        this.papel = papel;
        this.valor = valor;
        this.trechoOriginal = trechoOriginal == null ? "" : trechoOriginal;
    }

    public String getId() { return id; }
    public TipoElementoSemantico getTipo() { return TipoElementoSemantico.NUMERICO; }
    public PapelQuantitativo getPapel() { return papel; }
    public ValorNumerico getValor() { return valor; }
    public String getTrechoOriginal() { return trechoOriginal; }
}
