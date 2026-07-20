package gerard.semantica.categoria;

/** Equação conceitual entre papéis, por exemplo A + B = C. */
public final class RelacaoSemantica {
    private final String expressao;

    public RelacaoSemantica(String expressao) {
        this.expressao = expressao == null ? "" : expressao.trim();
    }

    public String getExpressao() { return expressao; }
}
