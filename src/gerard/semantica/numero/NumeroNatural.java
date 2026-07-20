package gerard.semantica.numero;

/** Número natural em N0. */
public final class NumeroNatural implements ValorNumerico {
    private final int valor;

    public NumeroNatural(int valor) {
        if (!DominioNumerico.NATURAIS.aceita(valor)) {
            throw new IllegalArgumentException("Número natural não pode ser negativo: " + valor);
        }
        this.valor = valor;
    }

    public DominioNumerico getDominio() { return DominioNumerico.NATURAIS; }
    public boolean ehConhecido() { return true; }
    public Integer valorOuNull() { return Integer.valueOf(valor); }
    public int intValue() { return valor; }
    public String formatar(boolean explicitarSinalPositivo) { return Integer.toString(valor); }

    @Override
    public String toString() { return formatar(false); }
}
