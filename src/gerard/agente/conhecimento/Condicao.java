package gerard.agente.conhecimento;

/**
 * Um antecedente de {@link RegraConhecimento}: campo, operador e valor de
 * comparação, no vocabulário do schema em
 * dados/base_conhecimento_gerard_jsons/base_conhecimento_gerard/schema_regras_gerard.json.
 */
public final class Condicao {
    private final String campo;
    private final String operador;
    private final Object valor;

    public Condicao(String campo, String operador, Object valor) {
        this.campo = campo;
        this.operador = operador;
        this.valor = valor;
    }

    public String getCampo() {
        return campo;
    }

    public String getOperador() {
        return operador;
    }

    public Object getValor() {
        return valor;
    }
}
