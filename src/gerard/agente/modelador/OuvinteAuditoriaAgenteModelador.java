package gerard.agente.modelador;

/**
 * Observador rico do AgenteModelador para consumidores de auditoria — canal adicional a
 * {@link OuvinteCasoAgenteModelador}, que continua existindo do jeito que
 * está.
 */
public interface OuvinteAuditoriaAgenteModelador {
    void aoArmazenar(ModeladorAuditData dados);
}
