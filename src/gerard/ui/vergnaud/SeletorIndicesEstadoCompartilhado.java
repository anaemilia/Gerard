package gerard.ui.vergnaud;

/**
 * Seleciona a janela de três elementos irmãos do diagrama de Vergnaud que
 * representa o estado semântico ativo. Não interpreta papéis nem valores.
 */
public final class SeletorIndicesEstadoCompartilhado {

    public int[] selecionar(
            boolean composicaoTransformacaoMedidas,
            boolean diagramasEncadeados,
            int quantidadeElementos,
            int indiceAlterado,
            int indiceNumeroRelativo,
            int indiceInicialAtual) {
        if (composicaoTransformacaoMedidas && quantidadeElementos >= 6) {
            return new int[] {3, 4, 5};
        }
        if (diagramasEncadeados && quantidadeElementos >= 3) {
            int indiceBase = indiceAlterado;
            if (indiceBase < 0 && indiceNumeroRelativo >= 0) {
                indiceBase = indiceNumeroRelativo;
            }
            if (indiceBase < 0) {
                indiceBase = indiceInicialAtual;
            }
            int inicio = Math.max(0, (indiceBase / 3) * 3);
            if (inicio + 2 >= quantidadeElementos) {
                inicio = 0;
            }
            return new int[] {inicio, inicio + 1, inicio + 2};
        }
        return new int[] {0, 1, 2};
    }
}
