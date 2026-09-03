package gerard.campoaditivo.sincronizacao.representacoes;

public final class TesteProjetorValoresComparacaoComplementar {
    public static void main(String[] args) {
        ProjetorValoresComparacaoComplementar projetor =
                new ProjetorValoresComparacaoComplementar();

        exigir(projetor.projetar(null, null, null, Integer.valueOf(8)),
                0, 0, 0, "curadoria nao deve antecipar cena sem modelagem");
        exigir(projetor.projetar(Integer.valueOf(6), Integer.valueOf(-4),
                        Integer.valueOf(2), Integer.valueOf(8)),
                6, -4, 2, "valor modelado deve prevalecer e preservar sinal");
        exigir(projetor.projetar(Integer.valueOf(6), null, null,
                        Integer.valueOf(8)),
                6, 8, 0, "valor relativo curado visivel deve completar a projecao");
        exigir(projetor.projetar(Integer.valueOf(6), null,
                        Integer.valueOf(14), null),
                6, 8, 14, "relacao estrutural deve resolver valor relativo ausente");
        exigir(projetor.projetar(Integer.valueOf(-3), Integer.valueOf(-2),
                        Integer.valueOf(-1), null),
                0, -2, 0, "medidas devem ser naturais sem apagar sinal relativo");
    }

    private static void exigir(int[] atual, int a, int b, int c,
            String mensagem) {
        if (atual == null || atual.length != 3
                || atual[0] != a || atual[1] != b || atual[2] != c) {
            throw new AssertionError(mensagem);
        }
    }
}
