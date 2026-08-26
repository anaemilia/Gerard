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

    /**
     * Opção que representa este valor no seletor binário de sinal.
     *
     * <p>O zero usa MAIS porque a sintaxe atual da interface oferece somente
     * as opções + e -. Isso não altera a propriedade matemática de que zero
     * não é positivo nem negativo.</p>
     */
    public OpcaoSinalNumeroInteiro sinalParaRepresentacaoBinaria() {
        return valor < 0
                ? OpcaoSinalNumeroInteiro.MENOS
                : OpcaoSinalNumeroInteiro.MAIS;
    }

    /** A correspondência de sinal pertence ao próprio número. */
    public boolean correspondeAoSinalRepresentado(
            OpcaoSinalNumeroInteiro sinalRepresentado) {
        if (sinalRepresentado == null) {
            throw new IllegalArgumentException("sinal representado é obrigatório");
        }
        return sinalParaRepresentacaoBinaria() == sinalRepresentado;
    }

    public String formatar(boolean explicitarSinalPositivo) {
        if (valor > 0 && explicitarSinalPositivo) {
            return "+" + valor;
        }
        return Integer.toString(valor);
    }

    @Override
    public String toString() { return formatar(false); }
}
