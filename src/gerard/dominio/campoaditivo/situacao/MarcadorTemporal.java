package gerard.dominio.campoaditivo.situacao;

/** Ordem e identidade temporal independentes da expressão linguística. */
public final class MarcadorTemporal {
    private final int ordem;
    private final String chave;

    public MarcadorTemporal(int ordem, String chave) {
        if (ordem < 0) {
            throw new IllegalArgumentException("ordem temporal não pode ser negativa");
        }
        this.ordem = ordem;
        this.chave = FamiliaObjeto.obrigatorio(chave, "chave temporal não pode ser vazia");
    }

    public int getOrdem() { return ordem; }
    public String getChave() { return chave; }

    @Override
    public boolean equals(Object outro) {
        return this == outro || (outro instanceof MarcadorTemporal
                && chave.equals(((MarcadorTemporal) outro).chave));
    }

    @Override
    public int hashCode() { return chave.hashCode(); }
}
