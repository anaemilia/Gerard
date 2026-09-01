import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.venn.apresentacao.ProjetorValorPapelDiagramaComplementar;

public final class TesteProjetorValorPapelDiagramaComplementar {
    public static void main(String[] args) {
        ProjetorValorPapelDiagramaComplementar projetor =
                new ProjetorValorPapelDiagramaComplementar();
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot snapshot = estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] { Integer.valueOf(6), null, Integer.valueOf(14) },
                new boolean[] { true, false, true }, -1,
                EstadoSemanticoCompartilhado.Origem.INICIALIZACAO,
                1, false);

        checar("6", projetor.projetar(snapshot,
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS, 0, 99));
        checar("?", projetor.projetar(snapshot,
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS, 1, 8));
        checar("14", projetor.projetar(snapshot,
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS, 2, 99));
        checar("99", projetor.projetar(snapshot,
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, 0, 99));
        System.out.println("OK - projeção de valores do diagrama complementar");
    }

    private static void checar(String esperado, String atual) {
        if (!esperado.equals(atual)) {
            throw new AssertionError("esperado=" + esperado + ", atual=" + atual);
        }
    }
}
