package gerard.campoaditivo.sincronizacao.texto;

/**
 * Formatação textual para situações aditivas. O enunciado normalmente já
 * expressa o sentido por palavras como "a mais", "a menos", "ganhou" ou
 * "perdeu"; por isso o valor exibido permanece como magnitude, exceto quando
 * o texto original trazia sinal explícito.
 */
public final class SincronizadorElementosSemanticosTextoAditivo
        extends SincronizadorElementosSemanticosTextoAbstrato {

    @Override
    protected String formatarValor(int valor, ElementoSemanticoTexto elemento) {
        String original = elemento.getValorSemanticoOriginal();
        String limpo = original == null ? "" : original.trim();
        if (limpo.startsWith("+")) {
            return valor >= 0 ? "+" + Math.abs(valor) : Integer.toString(valor);
        }
        if (limpo.startsWith("-")) {
            return Integer.toString(valor);
        }
        return Integer.toString(Math.abs(valor));
    }
}
