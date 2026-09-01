package gerard.pesquisador.analiseunidade;

/**
 * Instância de protocolo B — uma ação do usuário já classificada num dos
 * seis tipos (quando aplicável), com exatamente uma avaliação final C/E e os
 * efeitos reais do proprietário semântico, da ajuda local e do Modelador.
 * Esta classe não decide nada; apenas reempacota o registro factual.
 */
public final class AcaoUsuarioProtocolo {
    private final String protocolInstanceId;
    private final String analysisUnitId;
    private final String episodeId;
    private final String sessionId;
    private final String userId;
    private final TipoProtocolo protocolType;
    private final String protocolTypeUnavailableReason;
    private final String actionDescription;
    private final String category;
    private final String element;
    private final String sourceRole;
    private final String targetRole;
    private final String evaluationResult;
    private final String evaluationType;
    private final boolean effective;
    private final String errorType;
    private final String rationale;
    private final int semanticEvaluations;
    private final int localHelpDecisions;
    private final int modelerEffectiveUpdates;
    private final int casesInserted;
    private final String idempotencyKey;
    private final String gestureId;
    private final String actionId;
    private final String startedAt;
    private final String finishedAt;

    public AcaoUsuarioProtocolo(String protocolInstanceId, String analysisUnitId, String episodeId, String sessionId,
            String userId, TipoProtocolo protocolType, String protocolTypeUnavailableReason, String actionDescription,
            String category, String element, String sourceRole, String targetRole, String evaluationResult,
            String evaluationType, boolean effective, String errorType, String rationale,
            int semanticEvaluations, int localHelpDecisions, int modelerEffectiveUpdates, int casesInserted,
            String idempotencyKey, String gestureId, String actionId, String startedAt, String finishedAt) {
        this.protocolInstanceId = protocolInstanceId;
        this.analysisUnitId = analysisUnitId;
        this.episodeId = episodeId;
        this.sessionId = sessionId;
        this.userId = userId;
        this.protocolType = protocolType;
        this.protocolTypeUnavailableReason = protocolTypeUnavailableReason;
        this.actionDescription = actionDescription;
        this.category = category;
        this.element = element;
        this.sourceRole = sourceRole;
        this.targetRole = targetRole;
        this.evaluationResult = evaluationResult;
        this.evaluationType = evaluationType;
        this.effective = effective;
        this.errorType = errorType;
        this.rationale = rationale;
        this.semanticEvaluations = semanticEvaluations;
        this.localHelpDecisions = localHelpDecisions;
        this.modelerEffectiveUpdates = modelerEffectiveUpdates;
        this.casesInserted = casesInserted;
        this.idempotencyKey = idempotencyKey;
        this.gestureId = gestureId;
        this.actionId = actionId;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public String getProtocolInstanceId() { return protocolInstanceId; }
    public String getAnalysisUnitId() { return analysisUnitId; }
    public String getEpisodeId() { return episodeId; }
    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }
    public TipoProtocolo getProtocolType() { return protocolType; }
    public String getProtocolTypeUnavailableReason() { return protocolTypeUnavailableReason; }
    public String getActionDescription() { return actionDescription; }
    public String getCategory() { return category; }
    public String getElement() { return element; }
    public String getSourceRole() { return sourceRole; }
    public String getTargetRole() { return targetRole; }
    public String getEvaluationResult() { return evaluationResult; }
    public String getEvaluationType() { return evaluationType; }
    public boolean isEffective() { return effective; }
    public String getErrorType() { return errorType; }
    public String getRationale() { return rationale; }
    public int getSemanticEvaluations() { return semanticEvaluations; }
    public int getLocalHelpDecisions() { return localHelpDecisions; }
    public int getModelerEffectiveUpdates() { return modelerEffectiveUpdates; }
    public int getCasesInserted() { return casesInserted; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getGestureId() { return gestureId; }
    public String getActionId() { return actionId; }
    public String getStartedAt() { return startedAt; }
    public String getFinishedAt() { return finishedAt; }
}
