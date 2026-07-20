import gerard.Scaffolding.venn.ScaffoldingLimiteQuantidadeVenn;

public class TesteLimiteQuantidadeVenn {
    public static void main(String[] args) {
        ScaffoldingLimiteQuantidadeVenn s = new ScaffoldingLimiteQuantidadeVenn();

        String[] composicao = {"papel.parte1", "papel.parte2", "papel.todo"};
        Integer limiteTodo = s.resolverLimite(composicao,
                new Integer[] {Integer.valueOf(6), Integer.valueOf(8), null}, 2);
        exigir(limiteTodo != null && limiteTodo.intValue() == 14,
                "Todo desconhecido deve ser derivado como 14.");

        String[] transformacao = {"papel.estadoInicial", "papel.transformacao", "papel.estadoFinal"};
        Integer limiteFinal = s.resolverLimite(transformacao,
                new Integer[] {Integer.valueOf(25), Integer.valueOf(-18), null}, 2);
        exigir(limiteFinal != null && limiteFinal.intValue() == 7,
                "Estado final deve ser derivado como 7.");

        String[] comparacao = {"papel.referido", "papel.diferenca", "papel.referendo"};
        Integer limiteReferendo = s.resolverLimite(comparacao,
                new Integer[] {Integer.valueOf(6), Integer.valueOf(8), null}, 2);
        exigir(limiteReferendo != null && limiteReferendo.intValue() == 14,
                "Referendo deve ser derivado como 14.");

        exigir(!s.atingiuLimite(5, Integer.valueOf(6)),
                "Quantidade abaixo do limite deve permitir adição.");
        exigir(s.atingiuLimite(6, Integer.valueOf(6)),
                "Quantidade igual ao limite deve bloquear adição.");
        exigir(s.atingiuLimite(7, Integer.valueOf(6)),
                "Quantidade superior ao limite legado também deve bloquear adição.");
        exigir(!s.atingiuLimite(100, null),
                "Sem valor curado numérico não deve haver limite inventado.");

        System.out.println("TesteLimiteQuantidadeVenn: OK");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
