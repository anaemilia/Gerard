package gerard.agente.monitor;

/**
 * Observador rico do AgenteMonitor, para o log estruturado de auditoria
 * (ver gerard.pesquisador.auditoria.AgentAuditService) — canal adicional a
 * {@link OuvinteVeredictoAgenteMonitor}, que continua existindo do jeito
 * que está. Não carrega lógica de negócio, só recebe o registro já
 * montado pelo próprio AgenteMonitor.
 */
public interface OuvinteAuditoriaAgenteMonitor {
    void aoAvaliar(MonitorAuditData dados);
}
