package gerard.ui.vergnaud;

import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public final class TesteApresentadorItemVergnaud {

    public static void main(String[] args) {
        ApresentadorItemVergnaud apresentador = new ApresentadorItemVergnaud();
        ItemTextoArrastavel item = new ItemTextoArrastavel(
                0, 0, 1, 1, "1", false, "1", "papel.parte");
        FontMetrics metricas = criarMetricas();

        apresentador.atualizarEDimensionar(item, "15,00", metricas);
        exigir("15,00".equals(item.valor), "valor visual não atualizado");
        exigir(item.largura == metricas.stringWidth("15,00") + 8,
                "largura não derivada das métricas");
        exigir(item.altura == metricas.getHeight() - 5,
                "altura não derivada das métricas");

        ElementoVergnaud alvo = elemento(100, 80, 20, 30);
        int centroOriginal = alvo.x + alvo.largura / 2;
        apresentador.centralizar(item, alvo);
        exigir(alvo.largura == item.largura,
                "alvo menor não foi expandido");
        exigir(alvo.x + alvo.largura / 2 == centroOriginal,
                "expansão não preservou o centro do alvo");
        exigir(item.x == alvo.x + (alvo.largura - item.largura) / 2
                && item.y == alvo.y + (alvo.altura - item.altura) / 2,
                "item não foi centralizado pela geometria do alvo");

        ItemTextoArrastavel externo = new ItemTextoArrastavel(
                0, 0, 10, 10, "2", false, "2", "papel.parte");
        apresentador.centralizarSeCentroEstiverContido(externo, alvo);
        exigir(externo.x == 0 && externo.y == 0,
                "item externo não deveria ser centralizado");

        System.out.println("Teste aprovado: apresentação dos itens usa geometria real.");
    }

    private static FontMetrics criarMetricas() {
        BufferedImage imagem = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D grafico = imagem.createGraphics();
        try {
            return grafico.getFontMetrics(new Font("Arial", Font.BOLD, 20));
        } finally {
            grafico.dispose();
        }
    }

    private static ElementoVergnaud elemento(int x, int y, int largura, int altura) {
        return new ElementoVergnaud(x, y, largura, altura,
                TipoFiguraDiagrama.RETANGULO, "",
                new Rectangle(0, 0, 500, 500), false);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
