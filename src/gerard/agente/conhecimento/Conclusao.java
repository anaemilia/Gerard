package gerard.agente.conhecimento;

/**
 * Uma conclusão de {@link RegraConhecimento}: campo que a regra determina e
 * o valor atribuído a ele quando os antecedentes batem.
 */
public final class Conclusao {
    private final String campo;
    private final Object valor;

    public Conclusao(String campo, Object valor) {
        this.campo = campo;
        this.valor = valor;
    }

    public String getCampo() {
        return campo;
    }

    public Object getValor() {
        return valor;
    }
}
