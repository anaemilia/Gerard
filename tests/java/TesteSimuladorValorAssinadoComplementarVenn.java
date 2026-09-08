import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.sincronizacao.SimuladorEstadoComplementarVenn;
import gerard.campoaditivo.transformacao.processo.PoliticaSinalTransformacaoComplementar;
import gerard.campoaditivo.venn.mapeamento.FabricaMapeamentosPapeisComplementares;
import java.util.Arrays;

public final class TesteSimuladorValorAssinadoComplementarVenn {
    public static void main(String[] args) {
        CirculoVenn inicial = no(true, 5, "");
        CirculoVenn transformacao = no(true, 1, "");
        CirculoVenn finalEstado = no(false, 0, "");
        SimuladorEstadoComplementarVenn simulador =
                new SimuladorEstadoComplementarVenn(
                        new PoliticaSinalTransformacaoComplementar());

        EstadoSemanticoCompartilhado.Snapshot resultado =
                simulador.simularValorAssinado(
                        Arrays.asList(inicial, transformacao, finalEstado),
                        new FabricaMapeamentosPapeisComplementares().obter(
                                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS),
                        TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                        true, null, 1, -1,
                        no -> no == inicial ? 5 : 1,
                        texto -> null);

        exigir(resultado != null, "A simulação assinada deve produzir estado.");
        exigir(resultado.valorOuZero(0) == 5,
                "O estado inicial deve preservar sua quantidade.");
        exigir(resultado.valorOuZero(1) == -1,
                "O valor proposto deve atravessar zero preservando o sinal.");
        exigir(resultado.valorOuZero(2) == 4,
                "A relação aditiva deve recalcular o estado final.");

        System.out.println("TesteSimuladorValorAssinadoComplementarVenn: OK");
    }

    private static CirculoVenn no(boolean unidades, int referencia, String texto) {
        CirculoVenn no = new CirculoVenn(0, 0, 10, 10, "", referencia, unidades);
        no.textoEditavel = texto;
        return no;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
