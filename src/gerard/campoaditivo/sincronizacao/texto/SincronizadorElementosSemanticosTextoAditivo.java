package gerard.campoaditivo.sincronizacao.texto;

/**
 * Formatação textual para situações aditivas. O enunciado normalmente já
 * expressa o sentido por palavras como "a mais", "a menos", "ganhou" ou
 * "perdeu"; por isso o valor exibido permanece sempre como magnitude, sem
 * sinal.
 *
 * Até 2026-08-18 havia uma exceção aqui — preservar o sinal quando "o texto
 * original trazia sinal explícito" — mas na prática o valor comparado
 * (elemento.getValorSemanticoOriginal()) é o canônico do papel curado
 * (ex.: "-3" para uma Relação inicial negativa), não o texto literalmente
 * digitado no enunciado (que continua sendo só "3"). Ou seja, a condição
 * quase sempre disparava para as categorias de Relações, vazando o sinal
 * curado de volta para dentro da frase — "Julia tem -3 bonecas a mais que
 * Maria" — mesmo quando o enunciado original nunca teve um sinal ali.
 * Decisão da usuária: "Retire os sinais dos números no texto. Não faz
 * sentido" — o sinal pertence à representação do número relativo (diagrama),
 * não ao enunciado.
 */
public final class SincronizadorElementosSemanticosTextoAditivo
        extends SincronizadorElementosSemanticosTextoAbstrato {

    @Override
    protected String formatarValor(int valor, ElementoSemanticoTexto elemento) {
        return Integer.toString(Math.abs(valor));
    }
}
