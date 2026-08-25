package gerard.aplicacao.adaptacao;

/**
 * Identidades factuais que correlacionam a decisao e as materializacoes de
 * uma ajuda com a acao instrumental e com a sequencia de rejeicoes.
 */
public final class ContextoRegistroAjuda {

    private final String actionId;
    private final String rejectionSequenceId;

    public ContextoRegistroAjuda(String actionId, String rejectionSequenceId) {
        this.actionId = normalizar(actionId);
        this.rejectionSequenceId = normalizar(rejectionSequenceId);
    }

    public String getActionId() { return actionId; }
    public String getRejectionSequenceId() { return rejectionSequenceId; }

    private static String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
