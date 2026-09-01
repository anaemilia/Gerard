package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.PosicaoRotuloFigura;
import java.util.List;

public class RenderizadorComparacaoMedidas extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(AreaDiagrama area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.comparacao_medidas");

        FiguraDiagrama referente = medida("papel.referido", area.x + 123, area.y + 309,
                definicao.getRotulo1(), valor(valores, 1), PosicaoRotuloFigura.ABAIXO);
        FiguraDiagrama rel = relacao("papel.diferenca", area.x + 297, area.y + 218, definicao.getRotulo2(), valor(valores, 0));
        FiguraDiagrama referido = medida("papel.referendo", area.x + 123, area.y + 108,
                definicao.getRotulo3(), valor(valores, 2), PosicaoRotuloFigura.ACIMA);

        figs.add(referente);
        figs.add(rel);
        figs.add(referido);
        cons.add(seta(cx(referente), top(referente) - 18, cx(referido), bottom(referido) + 18, ""));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
