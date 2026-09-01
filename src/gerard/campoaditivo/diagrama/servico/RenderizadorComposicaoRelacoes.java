package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import java.util.List;

public class RenderizadorComposicaoRelacoes extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(AreaDiagrama area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.composicao_relacoes");

        FiguraDiagrama r1 = relacaoGrande("papel.relacao1", area.x + 105, area.y + 102, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama r2 = relacaoGrande("papel.relacao2", area.x + 105, area.y + 297, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama rf = relacaoGrande("papel.relacaoFinal", area.x + 390, area.y + 200, definicao.getRotulo3(), valor(valores, 2));

        figs.add(r1);
        figs.add(r2);
        figs.add(rf);
        cons.add(chaveVertical(area.x + 233, top(r1) - 12, bottom(r2) + 12, "",
                left(rf), cy(rf)));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
