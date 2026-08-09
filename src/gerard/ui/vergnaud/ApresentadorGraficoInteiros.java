package gerard.ui.vergnaud;

import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import java.awt.Rectangle;

/** Operações exclusivamente visuais do gráfico de números inteiros. */
public final class ApresentadorGraficoInteiros {

    private final ScaffoldingGraficoInteiros grafico;

    public ApresentadorGraficoInteiros(ScaffoldingGraficoInteiros grafico) {
        if (grafico == null) {
            throw new IllegalArgumentException("grafico obrigatorio");
        }
        this.grafico = grafico;
    }

    public void mostrar(Rectangle geometriaNumeroRelativo, String valorBase) {
        if (geometriaNumeroRelativo == null) {
            return;
        }
        grafico.mostrar(geometriaNumeroRelativo, valorBase);
    }

    public void registrarEscolha(
            Rectangle geometriaNumeroRelativo, String valorBase, String sinal) {
        if (geometriaNumeroRelativo == null) {
            return;
        }
        if (!grafico.isVisivel()) {
            grafico.mostrar(geometriaNumeroRelativo, valorBase);
        } else {
            grafico.atualizarCirculo(geometriaNumeroRelativo);
        }
        grafico.registrarEscolha(valorBase, sinal);
    }

    public void atualizarGeometria(Rectangle geometriaNumeroRelativo) {
        if (geometriaNumeroRelativo != null) {
            grafico.atualizarCirculo(geometriaNumeroRelativo);
        }
    }
}
