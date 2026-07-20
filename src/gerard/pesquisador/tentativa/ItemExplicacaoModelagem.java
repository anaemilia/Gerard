package gerard.pesquisador.tentativa;

/** Elemento semântico apresentado no artefato explicativo. */
public final class ItemExplicacaoModelagem {
    private final String elemento;
    private final String chavePapel;
    private final boolean conhecido;

    public ItemExplicacaoModelagem(String elemento, String chavePapel, boolean conhecido) {
        this.elemento = elemento == null ? "" : elemento;
        this.chavePapel = chavePapel == null ? "" : chavePapel;
        this.conhecido = conhecido;
    }

    public String getElemento() { return elemento; }
    public String getChavePapel() { return chavePapel; }
    public boolean isConhecido() { return conhecido; }
}
