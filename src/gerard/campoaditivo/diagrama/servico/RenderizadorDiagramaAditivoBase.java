package gerard.campoaditivo.diagrama.servico;

import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import java.util.ArrayList;
import java.util.List;

abstract class RenderizadorDiagramaAditivoBase implements RenderizadorDiagramaAditivo {
    // Tamanhos das figuras escalados em 1.5x (ver commit "Aumenta diagramas
    // de Vergnaud") — os originais (42/52/58) deixavam o diagrama pequeno
    // dentro da area disponivel em telas maiores, sem usar o espaco real
    // calculado por Main.obterAreasDiagramasProporcionais().
    protected FiguraDiagrama medida(int x, int y, String rotulo, int valor) {
        return new FiguraDiagrama(TipoFiguraDiagrama.RETANGULO_ARREDONDADO, x, y, 63, 63, rotulo, valor, true);
    }

    protected FiguraDiagrama relacao(int x, int y, String rotulo, int valor) {
        return new FiguraDiagrama(TipoFiguraDiagrama.ELIPSE, x, y, 78, 78, rotulo, valor, true);
    }

    protected FiguraDiagrama transformacao(int x, int y, String rotulo, int valor) {
        return new FiguraDiagrama(TipoFiguraDiagrama.ELIPSE, x, y, 78, 78, rotulo, valor, true);
    }

    protected FiguraDiagrama relacaoGrande(int x, int y, String rotulo, int valor) {
        return new FiguraDiagrama(TipoFiguraDiagrama.ELIPSE, x, y, 87, 87, rotulo, valor, true);
    }

    protected ConectorDiagrama seta(int x1, int y1, int x2, int y2, String legenda) {
        return new ConectorDiagrama(TipoConectorDiagrama.SETA, x1, y1, x2, y2, legenda);
    }

    protected ConectorDiagrama setaCurva(int x1, int y1, int x2, int y2, String legenda) {
        return new ConectorDiagrama(TipoConectorDiagrama.SETA_CURVA, x1, y1, x2, y2, legenda);
    }

    protected ConectorDiagrama linha(int x1, int y1, int x2, int y2, String legenda) {
        return new ConectorDiagrama(TipoConectorDiagrama.LINHA, x1, y1, x2, y2, legenda);
    }

    protected ConectorDiagrama chaveVertical(int x, int y1, int y2, String legenda) {
        return new ConectorDiagrama(TipoConectorDiagrama.CHAVE_VERTICAL, x, y1, x, y2, legenda);
    }

    /** Chave vertical com uma haste ligando seu meio até (xAlvo, yAlvo) — ex.: o centro da figura "Todo". */
    protected ConectorDiagrama chaveVertical(int x, int y1, int y2, String legenda, int xAlvo, int yAlvo) {
        return new ConectorDiagrama(TipoConectorDiagrama.CHAVE_VERTICAL, x, y1, x, y2, legenda, xAlvo, yAlvo);
    }

    protected ConectorDiagrama chaveHorizontal(int x1, int y, int x2, String legenda) {
        return new ConectorDiagrama(TipoConectorDiagrama.CHAVE_HORIZONTAL, x1, y, x2, y, legenda);
    }

    /** Chave horizontal com uma haste ligando seu meio até (xAlvo, yAlvo) — ex.: o centro da figura "Todo". */
    protected ConectorDiagrama chaveHorizontal(int x1, int y, int x2, String legenda, int xAlvo, int yAlvo) {
        return new ConectorDiagrama(TipoConectorDiagrama.CHAVE_HORIZONTAL, x1, y, x2, y, legenda, xAlvo, yAlvo);
    }

    protected int valor(int[] valores, int indice) {
        return (valores != null && indice >= 0 && indice < valores.length) ? valores[indice] : 0;
    }

    protected List<FiguraDiagrama> figuras() { return new ArrayList<FiguraDiagrama>(); }
    protected List<ConectorDiagrama> conectores() { return new ArrayList<ConectorDiagrama>(); }
    protected int cx(FiguraDiagrama f) { return f.getX() + f.getLargura()/2; }
    protected int cy(FiguraDiagrama f) { return f.getY() + f.getAltura()/2; }
    protected int right(FiguraDiagrama f) { return f.getX() + f.getLargura(); }
    protected int left(FiguraDiagrama f) { return f.getX(); }
    protected int top(FiguraDiagrama f) { return f.getY(); }
    protected int bottom(FiguraDiagrama f) { return f.getY() + f.getAltura(); }
}
