package gerard.pesquisador.analiseunidade;

/** Uma resposta do usuário (D) a uma pergunta explicativa (C) real. */
public final class RespostaExplicativa {
    private final String questionId;
    private final String content;
    private final boolean saved;
    private final boolean editedLater;
    private final String timestamp;

    public RespostaExplicativa(String questionId, String content, boolean saved, boolean editedLater, String timestamp) {
        this.questionId = questionId;
        this.content = content;
        this.saved = saved;
        this.editedLater = editedLater;
        this.timestamp = timestamp;
    }

    public String getQuestionId() { return questionId; }
    public String getContent() { return content; }
    public boolean isSaved() { return saved; }
    public boolean isEditedLater() { return editedLater; }
    public String getTimestamp() { return timestamp; }
}
