package gerard.campoaditivo.diagrama.servico;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import java.util.List;

public class RenderizadorComposicaoMedidas extends RenderizadorDiagramaAditivoBase {
    public CenaDiagramaAditivo criarCena(AreaDiagrama area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.composicao_medidas");

        FiguraDiagrama parte1 = medida("papel.parte1", area.x + 83, area.y + 117, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama parte2 = medida("papel.parte2", area.x + 83, area.y + 258, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama todo = medida("papel.todo", area.x + 243, area.y + 188, definicao.getRotulo3(), valor(valores, 2));

        figs.add(parte1);
        figs.add(parte2);
        figs.add(todo);
        cons.add(chaveVertical(area.x + 168, top(parte1) - 9, bottom(parte2) + 9, "",
                left(todo), cy(todo)));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }

    /**
     * Duas coleções (parte1/parte2) compondo uma coleção total (todo) — o
     * mesmo Venn de duas coleções do desktop (adicionarQuadradinhosNoCirculo
     * em Main.java), só que aqui como cena genérica (2 grupos de origem +
     * seta + 1 grupo de destino) em vez de círculos concêntricos, já que o
     * corte web usa grupos retangulares para todas as figuras (ver medida()
     * acima). Única categoria com material concreto confirmado no web até
     * agora (ver RenderizadorDiagramaAditivo.criarCenaMaterialConcreto).
     */
    @Override
    public CenaDiagramaAditivo criarCenaMaterialConcreto(AreaDiagrama area,
            DefinicaoDiagramaAditivo definicao, int[] valores, String chavePapelAlvo) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String titulo = ServicoLocalizacao.getInstancia().texto("ui.collections.title");
        String descricao = ServicoLocalizacao.getInstancia().texto("ui.collections.description");

        FiguraDiagrama parte1 = grupoQuadradinhos("papel.parte1", area.x + 10, area.y + 10,
                150, 130, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama parte2 = grupoQuadradinhos("papel.parte2", area.x + 10, area.y + 170,
                150, 130, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama todo = grupoQuadradinhos("papel.todo", area.x + 250, area.y + 60,
                160, 240, definicao.getRotulo3(), valor(valores, 2));

        figs.add(parte1);
        figs.add(parte2);
        figs.add(todo);
        cons.add(seta(right(parte1), (bottom(parte1) + top(parte2)) / 2, left(todo), cy(todo), ""));

        return new CenaDiagramaAditivo(titulo, descricao, figs, cons);
    }
}
