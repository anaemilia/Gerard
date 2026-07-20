package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import java.awt.Rectangle;
import java.util.List;

public class RenderizadorComposicaoMedidas extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(Rectangle area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.composicao_medidas");

        FiguraDiagrama parte1 = medida(area.x + 55, area.y + 78, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama parte2 = medida(area.x + 55, area.y + 172, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama todo = medida(area.x + 162, area.y + 125, definicao.getRotulo3(), valor(valores, 2));

        figs.add(parte1);
        figs.add(parte2);
        figs.add(todo);
        cons.add(chaveVertical(area.x + 112, top(parte1) - 6, bottom(parte2) + 6, "",
                left(todo), cy(todo)));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
