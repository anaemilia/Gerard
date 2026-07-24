package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import java.awt.Rectangle;
import java.util.List;

public class RenderizadorComposicaoTransformacoes extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(Rectangle area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.composicao_transformacoes");

        FiguraDiagrama t1 = transformacao(area.x + 218, area.y + 75, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama t2 = transformacao(area.x + 495, area.y + 75, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama tr = transformacao(area.x + 357, area.y + 348, definicao.getRotulo3(), valor(valores, 2));

        FiguraDiagrama inicial = medida(area.x + 51, area.y + 177, "", 0);
        FiguraDiagrama intermediario = medida(area.x + 378, area.y + 177, "", 0);
        FiguraDiagrama fin = medida(area.x + 705, area.y + 177, "", 0);

        figs.add(t1);
        figs.add(t2);
        figs.add(tr);
        figs.add(inicial);
        figs.add(intermediario);
        figs.add(fin);

        cons.add(seta(right(inicial) + 27, cy(inicial), left(intermediario) - 27, cy(intermediario), ""));
        cons.add(seta(right(intermediario) + 27, cy(intermediario), left(fin) - 27, cy(fin), ""));
        cons.add(setaCurva(cx(inicial), bottom(inicial) + 15, cx(fin), bottom(fin) + 15, ""));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
