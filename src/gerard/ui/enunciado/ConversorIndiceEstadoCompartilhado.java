package gerard.ui.enunciado;

/**
 * Converte um índice "real" (posição visual/estrutural de um elemento) para
 * o índice canônico de papel (0, 1 ou 2) usado por
 * {@link gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado},
 * a partir do mapeamento atual entre os dois espaços de índice.
 *
 * Extraído de Main.TelaGerard (método original converterIndiceRealParaPapel)
 * como parte da Fase 1 do plano de refatoração — ver
 * PLANO_REFATORACAO_ARQUITETURA_GERARD.md.
 *
 * Função pura: não lê nem escreve nenhum campo de TelaGerard, apenas o array
 * que recebe como parâmetro.
 */
public final class ConversorIndiceEstadoCompartilhado {

    private ConversorIndiceEstadoCompartilhado() {
    }

    /**
     * @param indicesElementosEstadoCompartilhado mapeamento atual (posição no
     *        array = índice de papel; valor = índice real correspondente)
     * @param indiceReal índice real a converter
     * @return índice de papel (0, 1 ou 2) correspondente, ou -1 se
     *         indiceReal não estiver presente no mapeamento
     */
    public static int converterIndiceRealParaPapel(
            int[] indicesElementosEstadoCompartilhado, int indiceReal) {
        for (int i = 0; i < indicesElementosEstadoCompartilhado.length; i++) {
            if (indicesElementosEstadoCompartilhado[i] == indiceReal) {
                return i;
            }
        }
        return -1;
    }
}
