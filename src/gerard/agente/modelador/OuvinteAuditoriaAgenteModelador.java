package gerard.agente.modelador;

/**
 * Observador rico do AgenteModelador, para o log estruturado de auditoria
 * (ver gerard.pesquisador.auditoria.AgentAuditService) — canal adicional a
 * {@link OuvinteCasoAgenteModelador}, que continua existindo do jeito que
 * está.
 */
public interface OuvinteAuditoriaAgenteModelador {
    void aoArmazenar(ModeladorAuditData dados);
}
