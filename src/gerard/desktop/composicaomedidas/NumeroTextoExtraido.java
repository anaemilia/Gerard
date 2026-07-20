package gerard.desktop.composicaomedidas;

/**
 * Representa um número (ou a interrogação "?") localizado dentro do
 * enunciado de uma situação-problema, junto da posição exata (início/fim)
 * em que aparece no texto original — usado para destacar o trecho
 * diretamente dentro da frase, sem lista separada de peças.
 */
public final class NumeroTextoExtraido {

    /** Texto exatamente como aparece no enunciado (ex.: "seis", "12", "?"). */
    public final String textoOriginal;

    /** Valor numérico correspondente, ou null quando é a incógnita "?". */
    public final Integer valor;

    /** Posição inicial (inclusive) do trecho dentro do enunciado. */
    public final int inicio;

    /** Posição final (exclusiva) do trecho dentro do enunciado. */
    public final int fim;

    public NumeroTextoExtraido(String textoOriginal, Integer valor, int inicio, int fim) {
        this.textoOriginal = textoOriginal;
        this.valor = valor;
        this.inicio = inicio;
        this.fim = fim;
    }

    public boolean ehIncognita() {
        return valor == null;
    }

    @Override
    public String toString() {
        return textoOriginal + (valor != null ? "=" + valor : "=?");
    }
}
