package gerard.semantica.pista;

/** Posição concreta de uma pista no enunciado. */
public final class OcorrenciaPista {
    private final PistaLinguistica pista;
    private final int inicio;
    private final int fim;
    private final String trechoOriginal;

    public OcorrenciaPista(PistaLinguistica pista, int inicio, int fim,
                           String trechoOriginal) {
        this.pista = pista;
        this.inicio = Math.max(0, inicio);
        this.fim = Math.max(this.inicio, fim);
        this.trechoOriginal = trechoOriginal == null ? "" : trechoOriginal;
    }

    public PistaLinguistica getPista() { return pista; }
    public int getInicio() { return inicio; }
    public int getFim() { return fim; }
    public String getTrechoOriginal() { return trechoOriginal; }
}
