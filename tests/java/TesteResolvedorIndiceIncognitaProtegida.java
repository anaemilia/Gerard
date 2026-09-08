import gerard.campoaditivo.conclusao.PoliticaPreenchimentoIncognita;
import gerard.campoaditivo.sincronizacao.ResolvedorIndiceIncognitaProtegida;

public final class TesteResolvedorIndiceIncognitaProtegida {
    public static void main(String[] args) {
        ResolvedorIndiceIncognitaProtegida resolvedor =
                new ResolvedorIndiceIncognitaProtegida(
                        new PoliticaPreenchimentoIncognita());
        String[] papeis = {
            "papel.parte1", "papel.parte2", "papel.todo",
            "papel.estadoInicial", "papel.transformacao", "papel.estadoFinal"
        };

        exigir(resolvedor.resolver(new int[] {0, 1, 2}, papeis.length,
                indice -> papeis[indice], "papel.todo") == 2,
                "Deve localizar a incógnita na janela simples.");
        exigir(resolvedor.resolver(new int[] {3, 4, 5}, papeis.length,
                indice -> papeis[indice], "papel.transformacao") == 1,
                "Deve devolver a posição semântica, não o índice real.");
        exigir(resolvedor.resolver(new int[] {-1, 8, 5}, papeis.length,
                indice -> papeis[indice], "papel.estadoFinal") == 2,
                "Deve ignorar índices reais inválidos.");
        exigir(resolvedor.resolver(new int[] {0, 1, 2}, papeis.length,
                indice -> papeis[indice], "papel.estadoFinal") == -1,
                "Papel fora da janela não deve ser inventado.");
        exigir(resolvedor.resolver(null, papeis.length,
                indice -> papeis[indice], "papel.todo") == -1,
                "Janela ausente não possui incógnita protegida.");

        System.out.println("TesteResolvedorIndiceIncognitaProtegida: OK");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
