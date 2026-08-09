import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.venn.apresentacao.EstadoVisualUnidadeVenn;
import gerard.campoaditivo.venn.apresentacao.FabricaRenderizadoresUnidadeVenn;
import gerard.campoaditivo.venn.apresentacao.RenderizadorUnidadeVenn;
import gerard.campoaditivo.venn.apresentacao.RenderizadorUnidadeVennAbstrato;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TesteRenderizacaoUnidadesVenn {
    public static void main(String[] args) {
        QuadradinhoVenn unidade = new QuadradinhoVenn(12, 12, 18, "transformacao_positiva");
        RenderizadorUnidadeVenn[] renderizadores = new RenderizadorUnidadeVenn[] {
            FabricaRenderizadoresUnidadeVenn.paraComposicao(),
            FabricaRenderizadoresUnidadeVenn.paraComparacao(true),
            FabricaRenderizadoresUnidadeVenn.paraComparacao(false),
            FabricaRenderizadoresUnidadeVenn.paraOrigem(unidade),
            FabricaRenderizadoresUnidadeVenn.paraOrigem(new QuadradinhoVenn(12, 12, 18, "transformacao_negativa"))
        };

        for (RenderizadorUnidadeVenn renderizador : renderizadores) {
            if (!(renderizador instanceof RenderizadorUnidadeVennAbstrato)) {
                throw new AssertionError("A implementação deve herdar do renderizador abstrato comum.");
            }
            long normal = desenharECalcular(renderizador, unidade, EstadoVisualUnidadeVenn.NORMAL);
            long focada = desenharECalcular(renderizador, unidade, EstadoVisualUnidadeVenn.FOCADA);
            long arrastada = desenharECalcular(renderizador, unidade, EstadoVisualUnidadeVenn.ARRASTADA);
            if (normal == focada || focada == arrastada || normal == arrastada) {
                throw new AssertionError("Os estados normal, focado e arrastado devem produzir affordances distintas.");
            }
        }
        System.out.println("Teste de renderização 2,5D das unidades Venn: OK");
    }

    private static long desenharECalcular(RenderizadorUnidadeVenn renderizador,
            QuadradinhoVenn unidade, EstadoVisualUnidadeVenn estado) {
        BufferedImage imagem = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        try {
            renderizador.desenhar(g2, unidade, estado);
        } finally {
            g2.dispose();
        }
        long soma = 0L;
        for (int y = 0; y < imagem.getHeight(); y++) {
            for (int x = 0; x < imagem.getWidth(); x++) {
                soma = soma * 31L + (imagem.getRGB(x, y) & 0xffffffffL);
            }
        }
        return soma;
    }
}
