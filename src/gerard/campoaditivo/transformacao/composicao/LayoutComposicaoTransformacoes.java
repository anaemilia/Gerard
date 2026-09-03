package gerard.campoaditivo.transformacao.composicao;

import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import gerard.campoaditivo.venn.modelo.ConectorDiagramaVenn;
import gerard.campoaditivo.venn.modelo.NoDiagramaVenn;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Layout responsivo de Composição de Transformações: três funis (mesma
 * forma visual, todos "zona de processo") num único canal — Transformação
 * 1, Transformação 2, Transformação Final. Análogo a
 * {@link gerard.campoaditivo.transformacao.processo.LayoutProcessoTransformacao},
 * mas sem caixas de estado antes/depois — as três zonas são do mesmo tipo.
 */
public final class LayoutComposicaoTransformacoes {

    public CenaDiagramaVenn criarCena(Rectangle area,
            DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<NoDiagramaVenn> nos = new ArrayList<NoDiagramaVenn>();
        List<ConectorDiagramaVenn> conectores = new ArrayList<ConectorDiagramaVenn>();

        int margem = Math.max(18, area.width / 24);
        int larguraZona = limitar((int) Math.round(area.width * 0.22), 108, 150);
        int espaco = Math.max(18, (area.width - 2 * margem - 3 * larguraZona) / 2);
        int larguraTotal = 3 * larguraZona + 2 * espaco;
        int xInicial = area.x + Math.max(margem, (area.width - larguraTotal) / 2);

        int alturaZona = limitar(area.height - 96, 168, 240);
        int yZona = area.y + Math.max(66, (area.height - alturaZona) / 2 + 12);

        int x1 = xInicial;
        int x2 = x1 + larguraZona + espaco;
        int x3 = x2 + larguraZona + espaco;

        nos.add(new NoDiagramaVenn(x1, yZona, larguraZona, alturaZona,
                definicao.getRotulo1(), valor(valores, 0), true, NoDiagramaVenn.Forma.RETANGULO));
        nos.add(new NoDiagramaVenn(x2, yZona, larguraZona, alturaZona,
                definicao.getRotulo2(), valor(valores, 1), true, NoDiagramaVenn.Forma.RETANGULO));
        nos.add(new NoDiagramaVenn(x3, yZona, larguraZona, alturaZona,
                definicao.getRotulo3(), valor(valores, 2), true, NoDiagramaVenn.Forma.RETANGULO));

        // O canal e os três funis são desenhados pelo renderizador especializado.
        return new CenaDiagramaVenn(nos, conectores);
    }

    private int limitar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    private int valor(int[] valores, int indice) {
        return valores != null && indice >= 0 && indice < valores.length
                ? valores[indice] : 0;
    }
}
