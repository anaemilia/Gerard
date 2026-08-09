import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.venn.mapeamento.FabricaMapeamentosPapeisComplementares;
import gerard.campoaditivo.venn.mapeamento.MapeamentoPapeisRepresentacaoComplementar;

public final class TesteMapeamentoComparacaoComplementar {
    public static void main(String[] args) {
        MapeamentoPapeisRepresentacaoComplementar mapeamento =
                new FabricaMapeamentosPapeisComplementares().obter(
                        TipoSituacaoAditiva.COMPARACAO_MEDIDAS);

        exigir(mapeamento.paraIndiceSemantico(0) == 0,
                "Referido deve permanecer no índice semântico 0.");
        exigir(mapeamento.paraIndiceSemantico(1) == 2,
                "A segunda barra visual é o referendo, não o valor relativo.");
        exigir(mapeamento.paraIndiceSemantico(2) == 1,
                "O cartão visual é o valor relativo.");
        exigir(mapeamento.paraIndiceVisual(2) == 1,
                "O referendo semântico deve apontar para a segunda barra.");

        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        estado.limpar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
        estado.atualizar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] { 6, 8, 14 },
                new boolean[] { true, true, true },
                -1, EstadoSemanticoCompartilhado.Origem.INICIALIZACAO);

        // Um clique no + do referendo altera visualmente 14 para 15. Após o
        // mapeamento, o índice alterado é o papel semântico 2; portanto o
        // estado recalcula somente o valor relativo (9), sem multiplicar as
        // unidades da barra.
        EstadoSemanticoCompartilhado.Snapshot snapshot = estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] { 6, 8, 15 },
                new boolean[] { true, true, true },
                mapeamento.paraIndiceSemantico(1),
                EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);

        exigir(snapshot.valorOuZero(0) == 6, "Referido foi alterado indevidamente.");
        exigir(snapshot.valorOuZero(1) == 9, "Valor relativo deveria passar de 8 para 9.");
        exigir(snapshot.valorOuZero(2) == 15, "Um clique deve acrescentar exatamente uma unidade ao referendo.");

        System.out.println("Teste aprovado: ordem visual da comparação mapeada para a ordem semântica e incremento unitário preservado.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
