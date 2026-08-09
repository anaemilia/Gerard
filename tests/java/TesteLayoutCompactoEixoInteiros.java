import gerard.Scaffolding.grafico.LayoutPainelEixoInteiros;
import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public final class TesteLayoutCompactoEixoInteiros {
    public static void main(String[] args) {
        LayoutPainelEixoInteiros layout = new LayoutPainelEixoInteiros();

        Dimension semValor = layout.calcularDimensao(1240, 8, false);
        exigir(semValor.width <= 420,
                "Escala 8 deve continuar usando largura compacta.");
        exigir(semValor.height <= 110,
                "Sem ponto azul, o painel não deve reservar faixas verticais vazias.");
        exigir(semValor.height - (layout.getOffsetOrigemEixo(false) + 20) <= 10,
                "A margem inferior após os rótulos das marcas deve ser mínima.");
        exigir(layout.getOffsetOrigemEixo(false) - layout.getOffsetRotulosLados() <= 30,
                "O espaço entre os rótulos laterais e o eixo deve ser reduzido.");

        Dimension comValor = layout.calcularDimensao(1240, 8, true);
        exigir(comValor.height > semValor.height,
                "O painel só deve crescer quando precisar mostrar valor e instrução.");
        exigir(comValor.height <= 132,
                "Mesmo com ponto azul e instrução, a altura deve permanecer compacta.");
        exigir(comValor.height - layout.getOffsetInstrucao() <= 7,
                "A instrução deve ficar próxima da borda inferior, sem faixa ociosa.");

        Dimension escalaVinteEDois = layout.calcularDimensao(1240, 22, true);
        exigir(escalaVinteEDois.width <= 480,
                "Escalas maiores devem respeitar o limite de largura.");
        exigir(escalaVinteEDois.height == comValor.height,
                "A altura não deve crescer com a escala numérica.");

        ScaffoldingGraficoInteiros eixo = new ScaffoldingGraficoInteiros();
        BufferedImage imagem = new BufferedImage(1240, 760, BufferedImage.TYPE_INT_ARGB);
        Rectangle areaDiagrama = new Rectangle(0, 210, 1240, 550);
        eixo.mostrar(new Rectangle(600, 350, 50, 50), "8");
        eixo.desenhar(imagem.createGraphics(), 1240, 760, areaDiagrama);
        Rectangle painelSemValor = eixo.obterAreaVisualPainel();
        exigir(painelSemValor.height == semValor.height,
                "Antes da escolha do sinal, o componente deve usar a altura mínima.");

        eixo.registrarEscolha("8", "+");
        eixo.desenhar(imagem.createGraphics(), 1240, 760, areaDiagrama);
        Rectangle painelComValor = eixo.obterAreaVisualPainel();
        exigir(painelComValor.width == comValor.width
                        && painelComValor.height == comValor.height,
                "Após a escolha, o painel deve expandir apenas o necessário.");
        exigir(eixo.contemBotaoEsconder(
                        painelComValor.x + painelComValor.width - 20,
                        painelComValor.y + 16),
                "O olhinho deve permanecer dentro do cabeçalho reduzido.");

        System.out.println("TesteLayoutCompactoEixoInteiros: OK");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
