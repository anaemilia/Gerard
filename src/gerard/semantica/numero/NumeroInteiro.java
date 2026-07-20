package gerard.semantica.numero;

/** Número inteiro em Z. */
public final class NumeroInteiro implements ValorNumerico {
    private final int valor;

    public NumeroInteiro(int valor) {
        this.valor = valor;
    }

    public DominioNumerico getDominio() { return DominioNumerico.INTEIROS; }
    public boolean ehConhecido() { return true; }
    public Integer valorOuNull() { return Integer.valueOf(valor); }
    public int intValue() { return valor; }

    public String formatar(boolean explicitarSinalPositivo) {
        if (valor > 0 && explicitarSinalPositivo) {
            return "+" + valor;
        }
        return Integer.toString(valor);
    }

    @Override
    public String toString() { return formatar(false); }
}
