package gerard.pesquisador.auditoria;

/**
 * Observador de todo AgentAuditEvent finalizado (canônico ou reativo) —
 * usado pela unidade de análise A-B-C-D (rodada 5, 2026-07-31) para montar
 * B_user_action/eventos técnicos sem duplicar nenhuma decisão dos agentes:
 * só reage ao que o AgentAuditService já observou e gravou.
 */
public interface OuvinteUnidadeAnalise {
    void aoFinalizarAvaliacao(AgentAuditEvent evento);
}
