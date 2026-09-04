package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.PosicaoRotuloFigura;
import java.util.List;

public class RenderizadorTransformacaoMedidas extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(AreaDiagrama area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.transformacao_medidas");

        // Rotulo1/3 ("Estado inicial"/"Estado final") são texto longo demais
        // para caber dentro da caixa de 63px — ABAIXO em vez do CENTRO
        // default, mesma correção já aplicada em RenderizadorComparacaoMedidas
        // e RenderizadorComposicaoTransformacoes para o mesmo par de papéis.
        FiguraDiagrama inicial = medida("papel.estadoInicial", area.x + 57, area.y + 234, definicao.getRotulo1(), valor(valores, 0), PosicaoRotuloFigura.ABAIXO);
        FiguraDiagrama transf = transformacao("papel.transformacao", area.x + 303, area.y + 132, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama fin = medida("papel.estadoFinal", area.x + 561, area.y + 234, definicao.getRotulo3(), valor(valores, 2), PosicaoRotuloFigura.ABAIXO);

        figs.add(inicial);
        figs.add(transf);
        figs.add(fin);
        cons.add(seta(right(inicial) + 48, cy(inicial), left(fin) - 48, cy(fin), ""));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
