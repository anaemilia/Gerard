package gerard.pesquisador.auditoria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Identidade de um evento de auditoria multiagente (schema 2.0.0,
 * 2026-07-31) — episode_id/session_id são conceitos do harness de teste
 * (replay/monkey), não da sessão ao vivo; ficam null quando ninguém os
 * fornece.
 *
 * gesture_id/action_id identificam o GESTO físico do usuário — o mesmo
 * valor se repete em toda reavaliação reativa originada por ele (só uma
 * avaliação canônica avança pra um gesture_id novo). evaluation_id é único
 * por CHAMADA (canônica ou reativa) — é o que diferencia reavaliações entre
 * si. step_user conta só gestos canônicos; step_internal conta toda
 * chamada (canônica + reativa).
 */
public final class IdentificacaoEvento {
    private final String schemaVersion;
    private final String eventId;
    private final String episodeId;
    private final String sessionId;
    private final String gestureId;
    private final String actionId;
    private final String evaluationId;
    private final Integer stepUser;
    private final Integer stepInternal;
    private final String timestamp;
    private final String userId;
    private final String problemId;
    private final String situacaoProblema;
    private final String categoriaEsperada;
    private final String papelDesconhecido;
    private final String systemVersion;
    private final String rulesBaseVersion;
    private final List<String> executionSequence;

    public IdentificacaoEvento(String schemaVersion, String eventId, String episodeId, String sessionId,
            String gestureId, String actionId, String evaluationId, Integer stepUser, Integer stepInternal,
            String timestamp, String userId, String problemId, String situacaoProblema, String categoriaEsperada,
            String papelDesconhecido, String systemVersion, String rulesBaseVersion,
            List<String> executionSequence) {
        this.schemaVersion = schemaVersion;
        this.eventId = eventId;
        this.episodeId = episodeId;
        this.sessionId = sessionId;
        this.gestureId = gestureId;
        this.actionId = actionId;
        this.evaluationId = evaluationId;
        this.stepUser = stepUser;
        this.stepInternal = stepInternal;
        this.timestamp = timestamp;
        this.userId = userId;
        this.problemId = problemId;
        this.situacaoProblema = situacaoProblema;
        this.categoriaEsperada = categoriaEsperada;
        this.papelDesconhecido = papelDesconhecido;
        this.systemVersion = systemVersion;
        this.rulesBaseVersion = rulesBaseVersion;
        this.executionSequence = executionSequence == null
                ? Collections.<String>emptyList() : new ArrayList<String>(executionSequence);
    }

    /**
     * Construtor de conveniência pros pontos de chamada em Main.java: só os
     * campos que quem chama realmente sabe. Identidade de gesto/ação/
     * avaliação e as versões ficam a cargo de AgentAuditService.iniciarAcao.
     */
    public IdentificacaoEvento(String episodeId, String sessionId, String userId, String problemId,
            String situacaoProblema, String categoriaEsperada, String papelDesconhecido) {
        this(null, null, episodeId, sessionId, null, null, null, null, null, null, userId, problemId,
                situacaoProblema, categoriaEsperada, papelDesconhecido, null, null, null);
    }

    public String getSchemaVersion() { return schemaVersion; }
    public String getEventId() { return eventId; }
    public String getEpisodeId() { return episodeId; }
    public String getSessionId() { return sessionId; }
    public String getGestureId() { return gestureId; }
    public String getActionId() { return actionId; }
    public String getEvaluationId() { return evaluationId; }
    public Integer getStepUser() { return stepUser; }
    public Integer getStepInternal() { return stepInternal; }
    public String getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public String getProblemId() { return problemId; }
    public String getSituacaoProblema() { return situacaoProblema; }
    public String getCategoriaEsperada() { return categoriaEsperada; }
    public String getPapelDesconhecido() { return papelDesconhecido; }
    public String getSystemVersion() { return systemVersion; }
    public String getRulesBaseVersion() { return rulesBaseVersion; }
    public List<String> getExecutionSequence() { return Collections.unmodifiableList(executionSequence); }
}
