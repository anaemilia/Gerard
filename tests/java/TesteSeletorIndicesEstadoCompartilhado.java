package gerard.ui.vergnaud;

import java.util.Arrays;

public final class TesteSeletorIndicesEstadoCompartilhado {

    public static void main(String[] args) {
        SeletorIndicesEstadoCompartilhado seletor =
                new SeletorIndicesEstadoCompartilhado();

        exigirIndices(new int[] {0, 1, 2}, seletor.selecionar(
                false, false, 3, -1, -1, 0), "diagrama simples");
        exigirIndices(new int[] {3, 4, 5}, seletor.selecionar(
                true, false, 6, 0, -1, 0), "composição-transformação");
        exigirIndices(new int[] {3, 4, 5}, seletor.selecionar(
                false, true, 9, 4, -1, 0), "elemento alterado no segundo bloco");
        exigirIndices(new int[] {6, 7, 8}, seletor.selecionar(
                false, true, 9, -1, 7, 0), "número relativo no terceiro bloco");
        exigirIndices(new int[] {3, 4, 5}, seletor.selecionar(
                false, true, 9, -1, -1, 3), "janela atual preservada");
        exigirIndices(new int[] {0, 1, 2}, seletor.selecionar(
                false, true, 5, 4, -1, 0), "bloco incompleto volta ao primeiro");

        System.out.println("Teste aprovado: seleção local das janelas do Vergnaud.");
    }

    private static void exigirIndices(int[] esperado, int[] atual, String caso) {
        if (!Arrays.equals(esperado, atual)) {
            throw new AssertionError(caso + ": esperado "
                    + Arrays.toString(esperado) + ", obtido " + Arrays.toString(atual));
        }
    }
}
