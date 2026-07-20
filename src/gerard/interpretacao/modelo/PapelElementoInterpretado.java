package gerard.interpretacao.modelo;

public class PapelElementoInterpretado {
    private final String elemento;
    private final String chavePapel;
    private final boolean conhecido;

    public PapelElementoInterpretado(String elemento, String chavePapel, boolean conhecido) {
        this.elemento = elemento;
        this.chavePapel = chavePapel;
        this.conhecido = conhecido;
    }

    public String getElemento() {
        return elemento;
    }

    public String getChavePapel() {
        return chavePapel;
    }

    public boolean isConhecido() {
        return conhecido;
    }
}
