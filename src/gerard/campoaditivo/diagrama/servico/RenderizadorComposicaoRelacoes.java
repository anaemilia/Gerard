package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import java.awt.Rectangle;
import java.util.List;

public class RenderizadorComposicaoRelacoes extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(Rectangle area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.composicao_relacoes");

        FiguraDiagrama r1 = relacaoGrande(area.x + 70, area.y + 68, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama r2 = relacaoGrande(area.x + 70, area.y + 198, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama rf = relacaoGrande(area.x + 260, area.y + 133, definicao.getRotulo3(), valor(valores, 2));

        figs.add(r1);
        figs.add(r2);
        figs.add(rf);
        cons.add(chaveVertical(area.x + 155, top(r1) - 8, bottom(r2) + 8, "",
                left(rf), cy(rf)));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
