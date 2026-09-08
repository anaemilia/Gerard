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
        if (projetor.selecionarValorRelativo(
                Integer.valueOf(-4), Integer.valueOf(7), 3) != -4) {
            throw new AssertionError("valor modelado deve prevalecer");
        }
        if (projetor.selecionarValorRelativo(
                null, Integer.valueOf(-7), 3) != -7) {
            throw new AssertionError("valor complementar deve preceder controle");
        }
        if (projetor.selecionarValorRelativo(null, null, 3) != 3) {
            throw new AssertionError("controle deve ser a ultima alternativa");
        }
        if (projetor.calcularMaximoEscala(
                Integer.valueOf(-5), Integer.valueOf(8),
                Integer.valueOf(-6)) != 8) {
            throw new AssertionError("escala deve usar modulos das fontes visiveis");
        }
    }

    private static void exigir(int[] atual, int a, int b, int c,
            String mensagem) {
        if (atual == null || atual.length != 3
                || atual[0] != a || atual[1] != b || atual[2] != c) {
            throw new AssertionError(mensagem);
        }
    }
}
