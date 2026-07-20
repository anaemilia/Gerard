package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import java.awt.Rectangle;
import java.util.List;

public class RenderizadorTransformacaoMedidas extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(Rectangle area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.transformacao_medidas");

        FiguraDiagrama inicial = medida(area.x + 38, area.y + 156, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama transf = transformacao(area.x + 202, area.y + 88, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama fin = medida(area.x + 374, area.y + 156, definicao.getRotulo3(), valor(valores, 2));

        figs.add(inicial);
        figs.add(transf);
        figs.add(fin);
        cons.add(seta(right(inicial) + 32, cy(inicial), left(fin) - 32, cy(fin), ""));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
