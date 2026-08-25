package gerard.dominio.campoaditivo;

/**
 * Resultado factual produzido pelo papel ao concluir uma ação de tentativa.
 * Mantém separadas a identidade da ação e a correlação opcional da sequência
 * de rejeições consecutivas.
 */
public final class ResultadoRegistroTentativaPapel {

    private final boolean acaoRegistrada;
    private final boolean correta;
    private final boolean rejeitadaSemAvaliacaoPorBloqueio;
    private final boolean limiteAtingidoAgora;
    private final int rejeicoesConsecutivas;
    private final String actionId;
    private final String rejectionSequenceId;

    ResultadoRegistroTentativaPapel(boolean acaoRegistrada, boolean correta,
            boolean rejeitadaSemAvaliacaoPorBloqueio, boolean limiteAtingidoAgora,
            int rejeicoesConsecutivas, String actionId, String rejectionSequenceId) {
        this.acaoRegistrada = acaoRegistrada;
        this.correta = correta;
        this.rejeitadaSemAvaliacaoPorBloqueio = rejeitadaSemAvaliacaoPorBloqueio;
        this.limiteAtingidoAgora = limiteAtingidoAgora;
        this.rejeicoesConsecutivas = rejeicoesConsecutivas;
        this.actionId = actionId;
        this.rejectionSequenceId = rejectionSequenceId;
    }

    static ResultadoRegistroTentativaPapel ignorada(int rejeicoesConsecutivas) {
        return new ResultadoRegistroTentativaPapel(false, false, false, false,
                rejeicoesConsecutivas, null, null);
    }

    public boolean isAcaoRegistrada() { return acaoRegistrada; }
    public boolean isCorreta() { return correta; }
    public boolean isRejeitadaSemAvaliacaoPorBloqueio() {
        return rejeitadaSemAvaliacaoPorBloqueio;
    }
    public boolean isLimiteAtingidoAgora() { return limiteAtingidoAgora; }
    public int getRejeicoesConsecutivas() { return rejeicoesConsecutivas; }
    public String getActionId() { return actionId; }
    public String getRejectionSequenceId() { return rejectionSequenceId; }
}
