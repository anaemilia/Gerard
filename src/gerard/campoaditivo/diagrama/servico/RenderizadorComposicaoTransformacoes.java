package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.PosicaoRotuloFigura;
import java.util.List;

public class RenderizadorComposicaoTransformacoes extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(AreaDiagrama area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.composicao_transformacoes");

        // Regra da usuária (2026-08-23): quadrado é estado (inicial,
        // intermediário, final); círculo é transformação (primeira,
        // segunda, e a resultante — rotulo1/2/3, vindos de
        // SemanticaCuradaSituacao.aplicarRotulos). Os 3 rótulos de estado
        // são papéis estruturais fixos desta categoria, não curados por
        // situação — por isso vêm direto da localização, mesmo padrão já
        // usado aqui embaixo para a descrição da cena.
        ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
        FiguraDiagrama t1 = transformacao("papel.transformacao1", area.x + 218, area.y + 75, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama t2 = transformacao("papel.transformacao2", area.x + 495, area.y + 75, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama tr = transformacao("papel.transformacaoFinal", area.x + 357, area.y + 348, definicao.getRotulo3(), valor(valores, 2));

        FiguraDiagrama inicial = medida("papel.estadoInicial", area.x + 51, area.y + 177, loc.texto("papel.estadoInicial"), 0, PosicaoRotuloFigura.ABAIXO);
        FiguraDiagrama intermediario = medida("papel.estadoIntermediario", area.x + 378, area.y + 177, loc.texto("papel.estadoIntermediario"), 0, PosicaoRotuloFigura.ABAIXO);
        FiguraDiagrama fin = medida("papel.estadoFinal", area.x + 705, area.y + 177, loc.texto("papel.estadoFinal"), 0, PosicaoRotuloFigura.ABAIXO);

        figs.add(t1);
        figs.add(t2);
        figs.add(tr);
        figs.add(inicial);
        figs.add(intermediario);
        figs.add(fin);

        cons.add(seta(right(inicial) + 27, cy(inicial), left(intermediario) - 27, cy(intermediario), ""));
        cons.add(seta(right(intermediario) + 27, cy(intermediario), left(fin) - 27, cy(fin), ""));
        // +58 (não +15) para o arco nascer abaixo das duas linhas de rótulo
        // ("Estado inicial"/"Estado final" + subtítulo) que passaram a ser
        // desenhadas ABAIXO da figura — ver PosicaoRotuloFigura.ABAIXO acima.
        cons.add(setaCurva(cx(inicial), bottom(inicial) + 58, cx(fin), bottom(fin) + 58, ""));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
