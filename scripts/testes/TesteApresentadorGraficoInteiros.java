package gerard.ui.vergnaud;

import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import java.awt.Rectangle;

public final class TesteApresentadorGraficoInteiros {

    public static void main(String[] args) {
        GraficoEspiao grafico = new GraficoEspiao();
        ApresentadorGraficoInteiros apresentador =
                new ApresentadorGraficoInteiros(grafico);
        Rectangle primeiraGeometria = new Rectangle(10, 20, 30, 40);
        Rectangle segundaGeometria = new Rectangle(50, 60, 70, 80);

        apresentador.registrarEscolha(primeiraGeometria, "12", "-");
        exigir(grafico.quantidadeMostrar == 1,
                "gráfico invisível deveria ser mostrado");
        exigir(grafico.quantidadeAtualizar == 0,
                "primeira apresentação não deveria atualizar separadamente");
        exigir(grafico.quantidadeRegistrar == 1
                        && "12".equals(grafico.valorRegistrado)
                        && "-".equals(grafico.sinalRegistrado),
                "valor e sinal não foram apresentados");

        apresentador.registrarEscolha(segundaGeometria, "7", "+");
        exigir(grafico.quantidadeMostrar == 1,
                "gráfico visível não deveria ser mostrado novamente");
        exigir(grafico.quantidadeAtualizar == 1
                        && segundaGeometria.equals(grafico.geometriaAtualizada),
                "geometria real não foi atualizada");
        exigir(grafico.quantidadeRegistrar == 2,
                "nova escolha não foi registrada");

        apresentador.registrarEscolha(null, "9", "-");
        exigir(grafico.quantidadeRegistrar == 2,
                "geometria ausente deveria impedir atualização visual");

        System.out.println("Teste aprovado: apresentação do gráfico preserva a geometria local.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static final class GraficoEspiao extends ScaffoldingGraficoInteiros {
        private boolean visivel;
        private int quantidadeMostrar;
        private int quantidadeAtualizar;
        private int quantidadeRegistrar;
        private Rectangle geometriaAtualizada;
        private String valorRegistrado;
        private String sinalRegistrado;

        @Override
        public boolean isVisivel() {
            return visivel;
        }

        @Override
        public void mostrar(Rectangle geometria, String valor) {
            quantidadeMostrar++;
            geometriaAtualizada = new Rectangle(geometria);
            visivel = true;
        }

        @Override
        public void atualizarCirculo(Rectangle geometria) {
            quantidadeAtualizar++;
            geometriaAtualizada = new Rectangle(geometria);
        }

        @Override
        public void registrarEscolha(String valor, String sinal) {
            quantidadeRegistrar++;
            valorRegistrado = valor;
            sinalRegistrado = sinal;
        }
    }
}
