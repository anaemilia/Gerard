package gerard.ui.vergnaud;

/** Instrução imutável para projetar um valor em um elemento visual. */
public final class AtualizacaoElementoVergnaud {

    public enum NaturezaVisual {
        MEDIDA,
        NUMERO_RELATIVO
    }

    private final int indiceElemento;
    private final int valor;
    private final NaturezaVisual naturezaVisual;

    public AtualizacaoElementoVergnaud(
            int indiceElemento, int valor, NaturezaVisual naturezaVisual) {
        this.indiceElemento = indiceElemento;
        this.valor = valor;
        this.naturezaVisual = naturezaVisual;
    }

    public int getIndiceElemento() {
        return indiceElemento;
    }

    public int getValor() {
        return valor;
    }

    public NaturezaVisual getNaturezaVisual() {
        return naturezaVisual;
    }
}
