package gerard.Scaffolding.questionamento;

/**
 * Resultado da avaliação do posicionamento de um número ou interrogação no
 * diagrama de Vergnaud.
 */
public class ResultadoQuestionamento {
    private final boolean aplicavel;
    private final boolean correto;
    private final String chavePapelNumeral;
    private final String chavePapelAlvo;
    private final String mensagem;

    private ResultadoQuestionamento(boolean aplicavel, boolean correto, String chavePapelNumeral, String chavePapelAlvo, String mensagem) {
        this.aplicavel = aplicavel;
        this.correto = correto;
        this.chavePapelNumeral = chavePapelNumeral;
        this.chavePapelAlvo = chavePapelAlvo;
        this.mensagem = mensagem;
    }

    public static ResultadoQuestionamento naoAplicavel() {
        return new ResultadoQuestionamento(false, true, null, null, "");
    }

    public static ResultadoQuestionamento correto(String chavePapelNumeral, String chavePapelAlvo) {
        return new ResultadoQuestionamento(true, true, chavePapelNumeral, chavePapelAlvo, "");
    }

    public static ResultadoQuestionamento incorreto(String chavePapelNumeral, String chavePapelAlvo, String mensagem) {
        return new ResultadoQuestionamento(true, false, chavePapelNumeral, chavePapelAlvo, mensagem);
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
