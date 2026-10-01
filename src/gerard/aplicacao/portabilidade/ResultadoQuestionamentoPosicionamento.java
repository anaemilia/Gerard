package gerard.aplicacao.portabilidade;

/**
 * Cópia neutra (sem depender de {@code gerard.Scaffolding.*}) do resultado da
 * avaliação de compatibilidade origem/destino — mesmos campos de
 * {@code gerard.Scaffolding.questionamento.ResultadoQuestionamento}, para que
 * a camada de aplicação não precise importar o pacote concreto do scaffolding
 * (ver {@link PortaQuestionamentoPosicionamento}).
 */
public final class ResultadoQuestionamentoPosicionamento {
    private final boolean aplicavel;
    private final boolean correto;
    private final String chavePapelNumeral;
    private final String chavePapelAlvo;
    private final String mensagem;

    private ResultadoQuestionamentoPosicionamento(boolean aplicavel, boolean correto,
            String chavePapelNumeral, String chavePapelAlvo, String mensagem) {
        this.aplicavel = aplicavel;
        this.correto = correto;
        this.chavePapelNumeral = chavePapelNumeral;
        this.chavePapelAlvo = chavePapelAlvo;
        this.mensagem = mensagem;
    }

    public static ResultadoQuestionamentoPosicionamento naoAplicavel() {
        return new ResultadoQuestionamentoPosicionamento(false, true, null, null, "");
    }

    public static ResultadoQuestionamentoPosicionamento correto(
            String chavePapelNumeral, String chavePapelAlvo) {
        return new ResultadoQuestionamentoPosicionamento(true, true, chavePapelNumeral, chavePapelAlvo, "");
    }

    public static ResultadoQuestionamentoPosicionamento incorreto(
            String chavePapelNumeral, String chavePapelAlvo, String mensagem) {
        return new ResultadoQuestionamentoPosicionamento(true, false, chavePapelNumeral, chavePapelAlvo, mensagem);
    }

    public boolean isAplicavel() {
        return aplicavel;
    }

    public boolean isCorreto() {
        return correto;
    }

    public String getChavePapelNumeral() {
        return chavePapelNumeral;
    }

    public String getChavePapelAlvo() {
        return chavePapelAlvo;
    }

    public String getMensagem() {
        return mensagem;
    }
}
