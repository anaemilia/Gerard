package gerard.interpretacao.incognita;

public class ResultadoIncognitaLinguistica {
    private final String chavePapel;
    private final boolean inferida;
    private final String justificativa;

    public ResultadoIncognitaLinguistica(String chavePapel, boolean inferida, String justificativa) {
        this.chavePapel = chavePapel;
        this.inferida = inferida;
        this.justificativa = justificativa == null ? "" : justificativa;
    }

    public String getChavePapel() {
        return chavePapel;
    }

    public boolean isInferida() {
        return inferida;
    }

    public String getJustificativa() {
        return justificativa;
    }
}
