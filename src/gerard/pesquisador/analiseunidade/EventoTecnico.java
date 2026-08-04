package gerard.pesquisador.analiseunidade;

/**
 * Evento técnico (mouse/callback/falha) — nunca cria unidade de análise nem
 * instância de protocolo (counts_as_analysis_unit e counts_as_protocol_instance
 * são sempre false, effective sempre false). Vinculado à unidade e/ou à
 * instância de protocolo abertas no momento em que ocorreu, quando existirem.
 */
public final class EventoTecnico {
    private final String technicalEventId;
    private final String analysisUnitId;
    private final String protocolInstanceId;
    private final String episodeId;
    private final String sessionId;
    private final String eventType;
    private final String timestamp;
    private final String details;

    public EventoTecnico(String technicalEventId, String analysisUnitId, String protocolInstanceId,
            String episodeId, String sessionId, String eventType, String timestamp, String details) {
        this.technicalEventId = technicalEventId;
        this.analysisUnitId = analysisUnitId;
        this.protocolInstanceId = protocolInstanceId;
        this.episodeId = episodeId;
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getTechnicalEventId() { return technicalEventId; }
    public String getAnalysisUnitId() { return analysisUnitId; }
    public String getProtocolInstanceId() { return protocolInstanceId; }
    public String getEpisodeId() { return episodeId; }
    public String getSessionId() { return sessionId; }
    public String getEventType() { return eventType; }
    public String getTimestamp() { return timestamp; }
    public String getDetails() { return details; }
}
