package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import java.awt.Rectangle;
import java.util.List;

public class RenderizadorTransformacaoRelacao extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(Rectangle area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.transformacao_relacao");

        FiguraDiagrama rInicial = relacaoGrande(area.x + 63, area.y + 228, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama transf = transformacao(area.x + 333, area.y + 108, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama rFinal = relacaoGrande(area.x + 615, area.y + 228, definicao.getRotulo3(), valor(valores, 2));

        figs.add(rInicial);
        figs.add(transf);
        figs.add(rFinal);
        cons.add(seta(right(rInicial) + 30, cy(rInicial), left(rFinal) - 30, cy(rFinal), ""));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
