package gerard.ui.conclusao;

import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import java.awt.Rectangle;
import java.util.Arrays;
import javax.swing.SwingUtilities;

public final class TestePosicionamentoTipConclusao {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        testarAbaixoDaTransformacao();
        testarAbaixoDaComposicao();
        testarAbaixoDaComparacao();
        System.out.println("Teste de posicionamento do tip aprovado: " + verificacoes + " verificações.");
    }

    private static void testarAbaixoDaTransformacao() throws Exception {
        ElementoVergnaud inicial = elemento(340, 410, "Estado inicial");
        ElementoVergnaud transformacao = elementoElipse(565, 345, "Transformação");
        ElementoVergnaud finalEstado = elemento(750, 410, "Estado final");
        ConectorVergnaud seta = conector(390, 430, 735, 430);
        verificarAbaixo("transformação", new ElementoVergnaud[] {inicial, transformacao, finalEstado},
                new ConectorVergnaud[] {seta});
    }

    private static void testarAbaixoDaComposicao() throws Exception {
        ElementoVergnaud parte1 = elemento(330, 350, "Parte 1");
        ElementoVergnaud parte2 = elemento(330, 470, "Parte 2");
        ElementoVergnaud todo = elemento(700, 410, "Todo");
        ConectorVergnaud chave = new ConectorVergnaud(TipoConectorDiagrama.CHAVE_VERTICAL,
                500, 360, 500, 500, "Composição", new Rectangle(20, 210, 1120, 530));
        verificarAbaixo("composição", new ElementoVergnaud[] {parte1, parte2, todo},
                new ConectorVergnaud[] {chave});
    }

    private static void testarAbaixoDaComparacao() throws Exception {
        ElementoVergnaud referido = elemento(370, 330, "Referido");
        ElementoVergnaud referendo = elemento(370, 500, "Referendo");
        ElementoVergnaud relacao = elementoElipse(630, 405, "Valor relativo");
        ConectorVergnaud seta = conector(430, 440, 610, 440);
        verificarAbaixo("comparação", new ElementoVergnaud[] {referido, referendo, relacao},
                new ConectorVergnaud[] {seta});
    }

    private static void verificarAbaixo(String categoria,
                                        ElementoVergnaud[] elementos,
                                        ConectorVergnaud[] conectores) throws Exception {
        CalculadorAreaVisualDiagramaVergnaud calculador = new CalculadorAreaVisualDiagramaVergnaud();
        Rectangle area = calculador.calcular(Arrays.asList(elementos), Arrays.asList(conectores),
                new Rectangle(25, 215, 1100, 520));
        final TipConclusaoModelagem tip = new TipConclusaoModelagem();
        tip.atualizarTextos("Parabéns! Podemos passar para a próxima tarefa?", "Sim", "Não");
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                tip.mostrarAbaixoDoDiagrama(area, new Rectangle(15, 215, 1110, 530), 1140, 800);
            }
        });
        confirmar(tip.getY() >= area.y + area.height + 10,
                "o tip deve ficar abaixo do diagrama de " + categoria);
        confirmar(tip.getX() >= 25 && tip.getX() + tip.getWidth() <= 1115,
                "o tip deve permanecer dentro do painel de " + categoria);
    }

    private static ElementoVergnaud elemento(int x, int y, String rotulo) {
        return new ElementoVergnaud(x, y, 44, 44, TipoFiguraDiagrama.RETANGULO,
                rotulo, new Rectangle(25, 215, 1100, 520), false);
    }

    private static ElementoVergnaud elementoElipse(int x, int y, String rotulo) {
        return new ElementoVergnaud(x, y, 54, 54, TipoFiguraDiagrama.ELIPSE,
                rotulo, new Rectangle(25, 215, 1100, 520), false);
    }

    private static ConectorVergnaud conector(int x1, int y1, int x2, int y2) {
        return new ConectorVergnaud(TipoConectorDiagrama.SETA, x1, y1, x2, y2,
                "", new Rectangle(25, 215, 1100, 520));
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
