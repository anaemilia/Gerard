import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

public final class TesteProjecaoValoresEstadoCompartilhado {
    public static void main(String[] args) {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] {6, -2, 4},
                new boolean[] {true, true, true},
                1,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD);

        exigirVetor(estado.valoresOuZeroPara(TipoSituacaoAditiva.COMPARACAO_MEDIDAS),
                6, -2, 4, "a projeção preserva ordem e sinal");
        exigirVetor(estado.valoresOuZeroPara(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS),
                0, 0, 0, "estado de outra categoria não vaza valores");

        EstadoSemanticoCompartilhado parcial = new EstadoSemanticoCompartilhado();
        parcial.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] {6, null, null},
                new boolean[] {true, false, false},
                0,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD);
        exigirVetor(parcial.valoresOuZeroPara(TipoSituacaoAditiva.COMPARACAO_MEDIDAS),
                6, 0, 0, "papéis desconhecidos são projetados como zero");

        System.out.println("APROVADO: projeção portátil do estado compartilhado.");
    }

    private static void exigirVetor(int[] atual, int a, int b, int c, String mensagem) {
        if (atual == null || atual.length != 3
                || atual[0] != a || atual[1] != b || atual[2] != c) {
            throw new AssertionError(mensagem);
        }
    }
}
