package gerard.campoaditivo.sincronizacao.representacoes;

/** Resultado imutável da leitura de uma janela semântica do Vergnaud. */
public final class ValoresCapturadosVergnaud {
    private final Integer[] valores;
    private final boolean[] conhecidos;
    private final int indiceAlteradoSemantico;

    public ValoresCapturadosVergnaud(Integer[] valores, boolean[] conhecidos,
            int indiceAlteradoSemantico) {
        this.valores = valores == null ? new Integer[0] : valores.clone();
        this.conhecidos = conhecidos == null ? new boolean[0] : conhecidos.clone();
        this.indiceAlteradoSemantico = indiceAlteradoSemantico;
    }

    public Integer[] getValores() { return valores.clone(); }
    public boolean[] getConhecidos() { return conhecidos.clone(); }
    public int getIndiceAlteradoSemantico() { return indiceAlteradoSemantico; }
}
