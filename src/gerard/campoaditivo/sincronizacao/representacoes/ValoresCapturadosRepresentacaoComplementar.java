package gerard.campoaditivo.sincronizacao.representacoes;

/** Dados representacionais capturados antes da atualização do domínio. */
public final class ValoresCapturadosRepresentacaoComplementar {

    private final Integer[] valores;
    private final boolean[] conhecidos;
    private final int indiceAlteradoSemantico;

    public ValoresCapturadosRepresentacaoComplementar(
            Integer[] valores, boolean[] conhecidos, int indiceAlteradoSemantico) {
        this.valores = valores.clone();
        this.conhecidos = conhecidos.clone();
        this.indiceAlteradoSemantico = indiceAlteradoSemantico;
    }

    public Integer[] getValores() {
        return valores.clone();
    }

    public boolean[] getConhecidos() {
        return conhecidos.clone();
    }

    public int getIndiceAlteradoSemantico() {
        return indiceAlteradoSemantico;
    }
}
