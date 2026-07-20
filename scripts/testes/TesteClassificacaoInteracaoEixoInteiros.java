import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import java.awt.Rectangle;

public class TesteClassificacaoInteracaoEixoInteiros {
    public static void main(String[] args) {
        ScaffoldingGraficoInteiros eixo = new ScaffoldingGraficoInteiros();
        eixo.mostrar(new Rectangle(600, 350, 50, 50), "6");
        eixo.registrarEscolha("6", "+");
        Rectangle painel = eixo.obterAreaVisualPainel();
        Rectangle ponto = eixo.obterAreaVisualPontoControle();

        ScaffoldingGraficoInteiros.NaturezaInteracao cabecalho =
                eixo.identificarNaturezaInteracao(
                        painel.x + 20, painel.y + 18, 1240, 760,
                        new Rectangle(0, 210, 1240, 550));
        exigir(cabecalho == ScaffoldingGraficoInteiros.NaturezaInteracao.COMPONENTE_TELA,
                "Arrastar o painel deve ser classificado como componente de tela.");

        ScaffoldingGraficoInteiros.NaturezaInteracao controle =
                eixo.identificarNaturezaInteracao(
                        ponto.x + ponto.width / 2, ponto.y + ponto.height / 2,
                        1240, 760, new Rectangle(0, 210, 1240, 550));
        exigir(controle == ScaffoldingGraficoInteiros.NaturezaInteracao.VALOR_SEMANTICO,
                "O ponto azul deve ser classificado como alteração semântica.");

        exigir(eixo.processarPressionamento(
                        painel.x + 20, painel.y + 18, 1240, 760,
                        new Rectangle(0, 210, 1240, 550)),
                "O painel deve continuar aceitando o início do arraste.");
        exigir(eixo.estaArrastandoPainel(),
                "A flutuação do componente deve permanecer ativa.");
        eixo.finalizarArraste();
        System.out.println("TesteClassificacaoInteracaoEixoInteiros: OK");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
