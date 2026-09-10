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

    /**
     * Mesma cena de criarCena, mas com o estado inicial desenhado como uma
     * composição de 2 partes conhecidas (Parte1 + Parte2 = Todo, via chave
     * vertical — mesmo padrão de RenderizadorComposicaoMedidas) alimentando
     * a cadeia de transformações — chamado só quando a situação curada tem
     * essa decomposição (ver GeradorCenaDiagramaAditivo, SituacaoProblemaAditiva.
     * getEstadoInicialParte1/2). Toda a cadeia existente é deslocada para a
     * direita (DESLOCAMENTO_PARTES) para abrir espaço à esquerda — mesmas
     * fórmulas relativas de criarCena, só alimentadas com as figuras
     * deslocadas, para não duplicar a lógica de conectores.
     */
    private static final int DESLOCAMENTO_PARTES = 190;

    public CenaDiagramaAditivo criarCenaComEstadoInicialDecomposto(
            AreaDiagrama area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<FiguraDiagrama> figs = figuras();
        List<ConectorDiagrama> cons = conectores();
        String descricao = ServicoLocalizacao.getInstancia().texto("diag.desc.composicao_transformacoes");
        ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();

        int d = DESLOCAMENTO_PARTES;
        FiguraDiagrama t1 = transformacao("papel.transformacao1", area.x + 218 + d, area.y + 75, definicao.getRotulo1(), valor(valores, 0));
        FiguraDiagrama t2 = transformacao("papel.transformacao2", area.x + 495 + d, area.y + 75, definicao.getRotulo2(), valor(valores, 1));
        FiguraDiagrama tr = transformacao("papel.transformacaoFinal", area.x + 357 + d, area.y + 348, definicao.getRotulo3(), valor(valores, 2));

        FiguraDiagrama inicial = medida("papel.estadoInicial", area.x + 51 + d, area.y + 177, loc.texto("papel.estadoInicial"), 0, PosicaoRotuloFigura.ABAIXO);
        FiguraDiagrama intermediario = medida("papel.estadoIntermediario", area.x + 378 + d, area.y + 177, loc.texto("papel.estadoIntermediario"), 0, PosicaoRotuloFigura.ABAIXO);
        FiguraDiagrama fin = medida("papel.estadoFinal", area.x + 705 + d, area.y + 177, loc.texto("papel.estadoFinal"), 0, PosicaoRotuloFigura.ABAIXO);

        // Parte1/Parte2 do estado inicial — mesmo padrão geométrico de
        // RenderizadorComposicaoMedidas (2 caixas + chave vertical
        // convergindo no centro da caixa "todo"), aqui "todo" é a própria
        // figura "inicial" acima (mantém a chave papel.estadoInicial já
        // usada por toda a cadeia de posicionamento/validação existente).
        FiguraDiagrama parte1 = medida("papel.estadoInicialParte1", area.x + 90, area.y + 97,
                loc.texto("papel.estadoInicialParte1"), 0, PosicaoRotuloFigura.ABAIXO);
        FiguraDiagrama parte2 = medida("papel.estadoInicialParte2", area.x + 90, area.y + 257,
                loc.texto("papel.estadoInicialParte2"), 0, PosicaoRotuloFigura.ABAIXO);

        figs.add(t1);
        figs.add(t2);
        figs.add(tr);
        figs.add(inicial);
        figs.add(intermediario);
        figs.add(fin);
        figs.add(parte1);
        figs.add(parte2);

        cons.add(seta(right(inicial) + 27, cy(inicial), left(intermediario) - 27, cy(intermediario), ""));
        cons.add(seta(right(intermediario) + 27, cy(intermediario), left(fin) - 27, cy(fin), ""));
        cons.add(setaCurva(cx(inicial), bottom(inicial) + 58, cx(fin), bottom(fin) + 58, ""));
        cons.add(chaveVertical(area.x + 185, top(parte1) - 9, bottom(parte2) + 9, "",
                left(inicial), cy(inicial)));

        return new CenaDiagramaAditivo(definicao.getTitulo(), descricao, figs, cons);
    }
}
