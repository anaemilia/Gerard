package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import gerard.campoaditivo.venn.modelo.ConectorDiagramaVenn;
import gerard.campoaditivo.venn.modelo.NoDiagramaVenn;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/** Layout responsivo do processo antes–canal–depois. */
public final class LayoutProcessoTransformacao {

    public CenaDiagramaVenn criarCena(Rectangle area,
            DefinicaoDiagramaAditivo definicao, int[] valores) {
        List<NoDiagramaVenn> nos = new ArrayList<NoDiagramaVenn>();
        List<ConectorDiagramaVenn> conectores =
                new ArrayList<ConectorDiagramaVenn>();

        int margem = Math.max(18, area.width / 24);
        int larguraEstado = limitar((int) Math.round(area.width * 0.23), 104, 138);
        int larguraProcesso = limitar((int) Math.round(area.width * 0.24), 116, 154);
        int larguraDisponivel = area.width - 2 * margem
                - 2 * larguraEstado - larguraProcesso;
        int espaco = Math.max(14, larguraDisponivel / 2);
        int larguraTotal = 2 * larguraEstado + larguraProcesso + 2 * espaco;
        int xInicial = area.x + Math.max(margem,
                (area.width - larguraTotal) / 2);
        int xProcesso = xInicial + larguraEstado + espaco;
        int xFinal = xProcesso + larguraProcesso + espaco;

        int alturaEstado = limitar(area.height - 180, 168, 252);
        int yEstado = area.y + Math.max(104,
                (area.height - alturaEstado) / 2 + 20);
        int yProcesso = area.y + 66;
        int alturaProcesso = Math.max(210,
                Math.min(area.height - 96,
                        yEstado + alturaEstado - yProcesso + 18));

        nos.add(new NoDiagramaVenn(xInicial, yEstado,
                larguraEstado, alturaEstado, definicao.getRotulo1(),
                valor(valores, 0), true));
        nos.add(new NoDiagramaVenn(xProcesso, yProcesso,
                larguraProcesso, alturaProcesso, definicao.getRotulo2(),
                valor(valores, 1), true));
        nos.add(new NoDiagramaVenn(xFinal, yEstado,
                larguraEstado, alturaEstado, definicao.getRotulo3(),
                valor(valores, 2), true));

        // O canal e os funis são desenhados pelo renderizador especializado.
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
