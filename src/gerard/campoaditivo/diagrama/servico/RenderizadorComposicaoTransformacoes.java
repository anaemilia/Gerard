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

        FiguraDiagrama t1 = transformacao(area.x + 145, area.y + 50, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama t2 = transformacao(area.x + 330, area.y + 50, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama tr = transformacao(area.x + 238, area.y + 232, definicao.getRotulo3(), valor(valores, 2));

        FiguraDiagrama inicial = medida(area.x + 34, area.y + 118, "", 0);
        FiguraDiagrama intermediario = medida(area.x + 252, area.y + 118, "", 0);
        FiguraDiagrama fin = medida(area.x + 470, area.y + 118, "", 0);

        figs.add(t1);
        figs.add(t2);
        figs.add(tr);
        figs.add(inicial);
        figs.add(intermediario);
        figs.add(fin);

        cons.add(seta(right(inicial) + 18, cy(inicial), left(intermediario) - 18, cy(intermediario), ""));
        cons.add(seta(right(intermediario) + 18, cy(intermediario), left(fin) - 18, cy(fin), ""));
        cons.add(setaCurva(cx(inicial), bottom(inicial) + 10, cx(fin), bottom(fin) + 10, ""));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
