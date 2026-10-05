import gerard.aplicacao.CategoriasComparaveis;
import gerard.aplicacao.EstadoNumericoComparacaoCategorias.Papel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/** A decisão por categoria da tela "Comparar categorias" tem dono (não é mais da Main). */
public class TesteCategoriasComparaveis {
    public static void main(String[] a) {
        exigir(CategoriasComparaveis.todas().size() == 3, "três categorias de medidas");
        TipoSituacaoAditiva comp = TipoSituacaoAditiva.COMPARACAO_MEDIDAS;
        exigir(CategoriasComparaveis.papelNoIndice(comp, 0) == Papel.TOTAL, "comparação: 0 = total");
        exigir(CategoriasComparaveis.papelNoIndice(comp, 1) == Papel.PRIMEIRA_PARCELA, "comparação: 1 = primeira parcela");
        exigir(CategoriasComparaveis.papelNoIndice(comp, 2) == Papel.SEGUNDA_PARCELA, "comparação: 2 = segunda parcela");
        for (TipoSituacaoAditiva t : new TipoSituacaoAditiva[]{TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS}) {
            exigir(CategoriasComparaveis.papelNoIndice(t, 0) == Papel.PRIMEIRA_PARCELA, t + ": 0 = primeira parcela");
            exigir(CategoriasComparaveis.papelNoIndice(t, 1) == Papel.SEGUNDA_PARCELA, t + ": 1 = segunda parcela");
            exigir(CategoriasComparaveis.papelNoIndice(t, 2) == Papel.TOTAL, t + ": 2 = total");
        }
        exigir("ui.compare.problem.composition".equals(CategoriasComparaveis.chaveTextoDoProblema(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS)), "chave composição");
        exigir("ui.compare.problem.transformation".equals(CategoriasComparaveis.chaveTextoDoProblema(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS)), "chave transformação");
        exigir("ui.compare.problem.comparison".equals(CategoriasComparaveis.chaveTextoDoProblema(comp)), "chave comparação");
        System.out.println("APROVADO: papel por posição, chave de texto e lista de categorias têm dono fora da Main.");
    }

    private static void exigir(boolean ok, String msg) {
        if (!ok) { System.out.println("[FALHA] " + msg); System.exit(1); }
    }
}
