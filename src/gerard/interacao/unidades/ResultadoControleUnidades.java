package gerard.interacao.unidades;

/** Desfecho do protocolo de clique num controle de unidades. */
public final class ResultadoControleUnidades<C> {

    public enum Desfecho {
        BLOQUEADO_PELA_MODELAGEM,
        LIMITE_ATINGIDO,
        APLICADO
    }

    private final OperacaoControleUnidade operacao;
    private final Desfecho desfecho;
    private final C controle;
    private final ResultadoAplicacaoControleUnidade aplicacao;

    ResultadoControleUnidades(OperacaoControleUnidade operacao, Desfecho desfecho,
            C controle, ResultadoAplicacaoControleUnidade aplicacao) {
        if (operacao == null || desfecho == null || controle == null) {
            throw new IllegalArgumentException("operacao, desfecho e controle obrigatorios");
        }
        if ((desfecho == Desfecho.APLICADO) != (aplicacao != null)) {
            throw new IllegalArgumentException("aplicacao presente somente quando APLICADO");
        }
        this.operacao = operacao;
        this.desfecho = desfecho;
        this.controle = controle;
        this.aplicacao = aplicacao;
    }

    public OperacaoControleUnidade getOperacao() { return operacao; }
    public Desfecho getDesfecho() { return desfecho; }
    public C getControle() { return controle; }
    /** Presente somente quando {@link Desfecho#APLICADO}. */
    public ResultadoAplicacaoControleUnidade getAplicacao() { return aplicacao; }
}
