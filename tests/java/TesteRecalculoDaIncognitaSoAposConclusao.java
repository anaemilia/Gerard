package gerard.campoaditivo.conclusao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

/**
 * Antes da conclusão correta, o sistema não pode recalcular uma incógnita que o participante já
 * preencheu (substituiria a resposta rejeitada pelo valor certo e concluiria sem ele informá-lo).
 * Depois da conclusão (exploração), o recálculo mantém as representações consistentes.
 * Cenário: Transformação de Medidas 25 + (-18) = 7; incógnita = estado final (índice 2).
 */
public final class TesteRecalculoDaIncognitaSoAposConclusao {
    public static void main(String[] args) {
        // Participante respondeu 99 (errado); um dado muda e a relação é reavaliada.
        final boolean[] concluida = {false};
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        estado.definirAdmissaoRecalculoDaIncognita(() -> concluida[0]);
        EstadoSemanticoCompartilhado.Snapshot s = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {25, -18, 99}, new boolean[] {true, true, true},
                0, EstadoSemanticoCompartilhado.Origem.ARRASTE, 2, true);
        exigir(s.valorOuZero(2) == 99, "antes da conclusão a resposta do participante (99) é mantida, veio "
                + s.valorOuZero(2));
        exigir(s.getIndiceResolvidoAutomaticamente() != 2, "nada foi resolvido automaticamente na incógnita");

        // Após a conclusão correta, a exploração recalcula a incógnita.
        concluida[0] = true;
        s = estado.atualizar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {30, -18, 99}, new boolean[] {true, true, true},
                0, EstadoSemanticoCompartilhado.Origem.ARRASTE, 2, true);
        exigir(s.valorOuZero(2) == 12, "após a conclusão a exploração recalcula a incógnita (12), veio "
                + s.valorOuZero(2));
        System.out.println("APROVADO: recálculo da incógnita só após a conclusão.");
    }

    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
