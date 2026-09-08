import gerard.Scaffolding.venn.ScaffoldingLimiteQuantidadeVenn;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.venn.ResolvedorLimiteQuantidadeCuradaVenn;
import gerard.idioma.IdiomaInterface;

public final class TesteResolvedorLimiteQuantidadeCuradaVenn {
    public static void main(String[] args) {
        ResolvedorLimiteQuantidadeCuradaVenn resolvedor =
                new ResolvedorLimiteQuantidadeCuradaVenn(
                        new ScaffoldingLimiteQuantidadeVenn());
        SituacaoProblemaAditiva situacao = new SituacaoProblemaAditiva(
                "teste", true, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                IdiomaInterface.PORTUGUES, "", "", "", "", "", "",
                "", "6", "8", "?", "papel.todo", "", "");

        String[] chaves = {"papel.parte1", "papel.parte2", "papel.todo"};
        exigir(Integer.valueOf(6).equals(
                resolvedor.resolver(situacao, null, chaves, 0)),
                "Deve consultar o valor conhecido pelo papel curado.");
        exigir(Integer.valueOf(14).equals(
                resolvedor.resolver(situacao, null, chaves, 2)),
                "Deve derivar o limite da incógnita pela relação existente.");
        exigir(resolvedor.resolver(situacao, null,
                new String[] {"papel.inexistente", "", null}, 0) == null,
                "Não deve inventar limite sem vínculo curado.");
        exigir(resolvedor.resolver(null, null, chaves, 0) == null,
                "Sem situação curada não há limite semântico.");

        System.out.println("TesteResolvedorLimiteQuantidadeCuradaVenn: OK");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
