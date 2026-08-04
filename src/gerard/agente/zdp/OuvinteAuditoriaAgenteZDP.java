package gerard.agente.zdp;

/**
 * Observador rico do AgenteZDP, para o log estruturado de auditoria (ver
 * gerard.pesquisador.auditoria.AgentAuditService) — canal adicional a
 * {@link OuvinteEstrategiaAgenteZDP}, que continua existindo do jeito que
 * está.
 */
public interface OuvinteAuditoriaAgenteZDP {
    void aoDecidir(ZdpAuditData dados);
}
