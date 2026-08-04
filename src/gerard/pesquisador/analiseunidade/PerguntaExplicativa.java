package gerard.pesquisador.analiseunidade;

/**
 * Uma pergunta explicativa (C) — previamente cadastrada/incorporada ao
 * Gérard (o par dificuldade+motivo por elemento semântico e a explicação
 * geral da tela de TelaArtefatoExplicativo), apresentada ou não conforme o
 * usuário abre a tela.
 */
public final class PerguntaExplicativa {
    private final String questionId;
    private final String questionType;
    private final String content;
    private final boolean presentedToUser;

    public PerguntaExplicativa(String questionId, String questionType, String content, boolean presentedToUser) {
        this.questionId = questionId;
        this.questionType = questionType;
        this.content = content;
        this.presentedToUser = presentedToUser;
    }

    public String getQuestionId() { return questionId; }
    public String getQuestionType() { return questionType; }
    public String getContent() { return content; }
    public boolean isPresentedToUser() { return presentedToUser; }
}
