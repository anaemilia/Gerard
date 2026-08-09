package gerard.ui.vergnaud;

import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

public final class TestePlanejadorAplicacaoEstadoVergnaud {

    public static void main(String[] args) {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot snapshot = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {4, -2, null},
                new boolean[] {true, true, false},
                1,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD);
        List<ElementoVergnaud> elementos = Arrays.asList(
                elemento(TipoFiguraDiagrama.RETANGULO),
                elemento(TipoFiguraDiagrama.RETANGULO),
                elemento(TipoFiguraDiagrama.RETANGULO),
                elemento(TipoFiguraDiagrama.RETANGULO),
                elemento(TipoFiguraDiagrama.ELIPSE),
                elemento(TipoFiguraDiagrama.RETANGULO));

        List<AtualizacaoElementoVergnaud> plano =
                new PlanejadorAplicacaoEstadoVergnaud().planejar(
                        snapshot, new int[] {3, 4, 5}, elementos);

        exigir(plano.size() == 3,
                "valor resolvido pelo domínio deve integrar a projeção");
        exigir(plano.get(0).getIndiceElemento() == 3
                && plano.get(0).getValor() == 4
                && plano.get(0).getNaturezaVisual()
                    == AtualizacaoElementoVergnaud.NaturezaVisual.MEDIDA,
                "medida não planejada corretamente");
        exigir(plano.get(1).getIndiceElemento() == 4
                && plano.get(1).getValor() == -2
                && plano.get(1).getNaturezaVisual()
                    == AtualizacaoElementoVergnaud.NaturezaVisual.NUMERO_RELATIVO,
                "número relativo não planejado corretamente");
        exigir(plano.get(2).getIndiceElemento() == 5
                && plano.get(2).getValor() == 2,
                "valor final resolvido pelo domínio não foi projetado");

        EstadoSemanticoCompartilhado estadoIncompleto =
                new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot incompleto = estadoIncompleto.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {4, null, null},
                new boolean[] {true, false, false},
                0,
                EstadoSemanticoCompartilhado.Origem.VERGNAUD);
        exigir(new PlanejadorAplicacaoEstadoVergnaud().planejar(
                incompleto, new int[] {3, 4, 5}, elementos).size() == 1,
                "valores realmente desconhecidos não devem ser projetados");
        exigir(new PlanejadorAplicacaoEstadoVergnaud().planejar(
                snapshot, new int[] {-1, 9, 8}, elementos).isEmpty(),
                "índices inválidos devem ser ignorados");

        System.out.println("Teste aprovado: planejamento visual do Vergnaud preservado.");
    }

    private static ElementoVergnaud elemento(TipoFiguraDiagrama tipo) {
        return new ElementoVergnaud(0, 0, 10, 10, tipo, "",
                new Rectangle(0, 0, 100, 100), false);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
