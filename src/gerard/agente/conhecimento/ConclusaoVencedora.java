package gerard.agente.conhecimento;

/**
 * Resultado de {@link MotorRegrasConhecimento#melhorConclusao}: o valor
 * vencedor para um campo, e qual regra o produziu (rastreabilidade — o
 * mesmo espírito do campo "explicacao" no schema da base de conhecimento).
 */
public final class ConclusaoVencedora {
    private final String ruleId;
    private final Object valor;
    private final String explicacao;

    public ConclusaoVencedora(String ruleId, Object valor, String explicacao) {
        this.ruleId = ruleId;
        this.valor = valor;
        this.explicacao = explicacao;
    }

    public String getRuleId() {
        return ruleId;
    }

    public Object getValor() {
        return valor;
    }

    public String getExplicacao() {
        return explicacao;
    }
}
