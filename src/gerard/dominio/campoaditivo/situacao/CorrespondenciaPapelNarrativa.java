package gerard.dominio.campoaditivo.situacao;

/** Correspondência curada entre um papel formal e um fato narrativo. */
public final class CorrespondenciaPapelNarrativa {
    private final String chavePapel;
    private final ReferenciaValorNarrativo referencia;

    public CorrespondenciaPapelNarrativa(
            String chavePapel,
            ReferenciaValorNarrativo referencia) {
        this.chavePapel = FamiliaObjeto.obrigatorio(
                chavePapel, "chave do papel não pode ser vazia");
        if (referencia == null) {
            throw new IllegalArgumentException("referência narrativa é obrigatória");
        }
        this.referencia = referencia;
    }

    public String getChavePapel() { return chavePapel; }
    public ReferenciaValorNarrativo getReferencia() { return referencia; }
}
