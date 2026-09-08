package gerard.dominio.campoaditivo;

public final class TesteRecalculoComparacaoMedidas {
    public static void main(String[] args) {
        RecalculoComparacaoMedidas.Resultado referido =
                RecalculoComparacaoMedidas.decidir(
                        "papel.referido", null, Integer.valueOf(10), 4);
        exigir(referido, RecalculoComparacaoMedidas.PapelAlvo.REFERIDO, 6,
                "incognita referida deve ser recalculada");

        RecalculoComparacaoMedidas.Resultado referendo =
                RecalculoComparacaoMedidas.decidir(
                        "papel.diferenca", Integer.valueOf(6),
                        Integer.valueOf(10), 5);
        exigir(referendo, RecalculoComparacaoMedidas.PapelAlvo.REFERENDO, 11,
                "referendo deve preservar a prioridade normal");

        RecalculoComparacaoMedidas.Resultado nenhum =
                RecalculoComparacaoMedidas.decidir(
                        "papel.diferenca", null, null, 5);
        exigir(nenhum, RecalculoComparacaoMedidas.PapelAlvo.NENHUM, 0,
                "estado insuficiente nao deve inventar valor");
    }

    private static void exigir(RecalculoComparacaoMedidas.Resultado resultado,
            RecalculoComparacaoMedidas.PapelAlvo papel, int valor,
            String mensagem) {
        if (resultado.getPapel() != papel || resultado.getValor() != valor) {
            throw new AssertionError(mensagem);
        }
    }
}
