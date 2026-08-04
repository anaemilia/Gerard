package gerard.pesquisador.analiseunidade;

/**
 * A unidade de análise A-B(-C-D) completa. A e B são fixados na criação
 * (nunca mudam depois). C e D começam em {@code not_opened} e só são
 * substituídos se o usuário efetivamente interagir com a tela de
 * explicações para a MESMA tarefa (user+categoria+papel-alvo) antes da
 * unidade ser fechada — ver AnalysisUnitAuditService.
 */
public final class UnidadeAnalise {
    private final String analysisUnitId;
    private final String sessionId;
    private final String episodeId;
    private final String userId;
    private final int sequenceOrder;
    private final String startedAt;
    private final AcaoComputador a;
    private final AcaoUsuarioProtocolo b;

    private String finishedAt;
    private ComponenteC c;
    private ComponenteD d;
    private boolean fechada;
    private String motivoAusenciaForcado;

    public UnidadeAnalise(String analysisUnitId, String sessionId, String episodeId, String userId,
            int sequenceOrder, String startedAt, AcaoComputador a, AcaoUsuarioProtocolo b) {
        this.analysisUnitId = analysisUnitId;
        this.sessionId = sessionId;
        this.episodeId = episodeId;
        this.userId = userId;
        this.sequenceOrder = sequenceOrder;
        this.startedAt = startedAt;
        this.a = a;
        this.b = b;
        this.c = ComponenteC.naoAberto(true, java.util.Collections.<PerguntaExplicativa>emptyList());
        this.d = ComponenteD.naoAberto();
    }

    public void definirExplicacao(ComponenteC c, ComponenteD d) {
        this.c = c;
        this.d = d;
    }

    public void fechar(String finishedAt) {
        this.finishedAt = finishedAt;
        this.fechada = true;
    }

    public boolean isFechada() { return fechada; }

    /** A_B / A_B_C / A_B_C_D — ver "CAMPO DE COMPLETUDE" do pacote da rodada 5. */
    public String getCompleteness() {
        if (d.getStatus() == StatusExplicacao.ANSWERED || !d.getResponses().isEmpty()) {
            return "A_B_C_D";
        }
        if (c.isScreenOpened()) {
            return "A_B_C";
        }
        return "A_B";
    }

    public boolean isEligibleForBehavioralAnalysis() {
        return true;
    }

    public boolean isEligibleForExplanatoryAnalysis() {
        return d.getStatus() == StatusExplicacao.ANSWERED && !d.getResponses().isEmpty();
    }

    /**
     * Sobrescreve o motivo (ex.: "technical_failure") quando uma tentativa
     * real de salvar falhou tecnicamente para esta tarefa antes do
     * fechamento — ver AnalysisUnitAuditService.registrarFalhaTecnica.
     */
    public void forcarMotivoAusencia(String motivo) {
        this.motivoAusenciaForcado = motivo;
    }

    /** Ver "INTERPRETAÇÃO DA AUSÊNCIA" do pacote — nunca inferido como erro/recusa/incapacidade. */
    public String getMissingExplanationReason() {
        if (isEligibleForExplanatoryAnalysis()) {
            return "not_applicable";
        }
        if (motivoAusenciaForcado != null) {
            return motivoAusenciaForcado;
        }
        switch (d.getStatus()) {
            case NOT_OPENED:
                return "user_did_not_open_explanation_screen";
            case OPENED_NOT_ANSWERED:
                return "user_opened_but_did_not_answer";
            case PARTIALLY_ANSWERED:
                return "user_answered_partially";
            default:
                return "unknown";
        }
    }

    public String getAnalysisUnitId() { return analysisUnitId; }
    public String getSessionId() { return sessionId; }
    public String getEpisodeId() { return episodeId; }
    public String getUserId() { return userId; }
    public int getSequenceOrder() { return sequenceOrder; }
    public String getStartedAt() { return startedAt; }
    public String getFinishedAt() { return finishedAt; }
    public AcaoComputador getA() { return a; }
    public AcaoUsuarioProtocolo getB() { return b; }
    public ComponenteC getC() { return c; }
    public ComponenteD getD() { return d; }
}
