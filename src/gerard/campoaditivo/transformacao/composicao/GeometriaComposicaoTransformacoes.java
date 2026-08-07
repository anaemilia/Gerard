package gerard.campoaditivo.transformacao.composicao;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.List;

/**
 * Geometria derivada das três zonas de Composição de Transformações —
 * análoga a
 * {@link gerard.campoaditivo.transformacao.processo.GeometriaProcessoTransformacao},
 * mas sem caixas de "estado": as três zonas (Transformação 1, Transformação
 * 2, Transformação Final) são todas funil, cada uma podendo ser inserção ou
 * retirada de forma independente (cada papel tem seu próprio sinal — não há
 * um único "tipo de processo" compartilhado como na Transformação de Medidas
 * simples). As três compartilham um único canal horizontal, como no
 * rascunho da usuária (2026-08-07): um funil por transformação, alimentando
 * a mesma esteira.
 */
public final class GeometriaComposicaoTransformacoes {
    private final Rectangle canal;
    private final Polygon[] funilInsercao = new Polygon[3];
    private final Rectangle[] hasteInsercao = new Rectangle[3];
    private final Polygon[] funilRetirada = new Polygon[3];
    private final Rectangle[] hasteRetirada = new Rectangle[3];
    private final Rectangle[] areaUnidadesInsercao = new Rectangle[3];
    private final Rectangle[] areaUnidadesRetirada = new Rectangle[3];

    private GeometriaComposicaoTransformacoes(Rectangle canal) {
        this.canal = canal;
    }

    public static GeometriaComposicaoTransformacoes calcular(List<CirculoVenn> zonas) {
        if (zonas == null || zonas.size() < 3) {
            GeometriaComposicaoTransformacoes vazio =
                    new GeometriaComposicaoTransformacoes(new Rectangle());
            for (int i = 0; i < 3; i++) {
                vazio.funilInsercao[i] = new Polygon();
                vazio.hasteInsercao[i] = new Rectangle();
                vazio.funilRetirada[i] = new Polygon();
                vazio.hasteRetirada[i] = new Rectangle();
                vazio.areaUnidadesInsercao[i] = new Rectangle();
                vazio.areaUnidadesRetirada[i] = new Rectangle();
            }
            return vazio;
        }

        CirculoVenn primeira = zonas.get(0);
        CirculoVenn ultima = zonas.get(2);
        int canalY = primeira.y + primeira.altura / 2;
        int canalX = primeira.x;
        int canalFim = ultima.x + ultima.largura;
        Rectangle canal = new Rectangle(canalX, canalY - 8,
                Math.max(1, canalFim - canalX), 16);

        GeometriaComposicaoTransformacoes geo =
                new GeometriaComposicaoTransformacoes(canal);
        for (int i = 0; i < 3; i++) {
            geo.calcularFunisDaZona(zonas.get(i), canalY, i);
        }
        return geo;
    }

    private void calcularFunisDaZona(CirculoVenn zona, int canalY, int i) {
        final float REFERENCIA_LARGURA = 116f;
        float escala = Math.max(1f, Math.min(1.4f, zona.largura / REFERENCIA_LARGURA));

        int centroX = zona.x + zona.largura / 2;
        int meiaGargalo = 10;

        int larguraTopo = Math.round(92 * escala);
        int alturaFunilInsercao = Math.round(58 * escala);
        int topoInsercao = zona.y;
        int baseInsercao = Math.max(topoInsercao + 40,
                Math.min(topoInsercao + alturaFunilInsercao, canalY - 10));
        int meia = larguraTopo / 2;
        funilInsercao[i] = new Polygon(
                new int[] {centroX - meia, centroX + meia,
                    centroX + meiaGargalo, centroX - meiaGargalo},
                new int[] {topoInsercao, topoInsercao,
                    baseInsercao, baseInsercao}, 4);
        hasteInsercao[i] = new Rectangle(
                centroX - meiaGargalo, baseInsercao,
                meiaGargalo * 2, Math.max(1, canalY - baseInsercao));
        areaUnidadesInsercao[i] = new Rectangle(
                centroX - meia + 8, topoInsercao + 8,
                Math.max(20, larguraTopo - 16),
                Math.max(24, baseInsercao - topoInsercao - 14));

        int larguraAbertura = Math.round(88 * escala);
        int alturaFunilRetirada = Math.round(54 * escala);
        int topoRetirada = canalY + 10;
        int baseRetirada = zona.y + zona.altura - 38;
        int fimConeRetirada = Math.max(topoRetirada + 30,
                Math.min(topoRetirada + alturaFunilRetirada, baseRetirada - 20));
        int meiaAbertura = larguraAbertura / 2;
        funilRetirada[i] = new Polygon(
                new int[] {centroX - meiaAbertura, centroX + meiaAbertura,
                    centroX + meiaGargalo, centroX - meiaGargalo},
                new int[] {topoRetirada, topoRetirada,
                    fimConeRetirada, fimConeRetirada}, 4);
        hasteRetirada[i] = new Rectangle(
                centroX - meiaGargalo, fimConeRetirada,
                meiaGargalo * 2, Math.max(1, baseRetirada - fimConeRetirada));
        areaUnidadesRetirada[i] = new Rectangle(
                centroX - meiaAbertura + 8, topoRetirada + 8,
                Math.max(24, larguraAbertura - 16),
                Math.max(28, fimConeRetirada - topoRetirada - 12));
    }

    public Rectangle getCanal() { return new Rectangle(canal); }
    public Polygon getFunilInsercao(int i) { return copiar(funilInsercao[i]); }
    public Rectangle getHasteInsercao(int i) { return new Rectangle(hasteInsercao[i]); }
    public Polygon getFunilRetirada(int i) { return copiar(funilRetirada[i]); }
    public Rectangle getHasteRetirada(int i) { return new Rectangle(hasteRetirada[i]); }
    public Rectangle getAreaUnidadesInsercao(int i) { return new Rectangle(areaUnidadesInsercao[i]); }
    public Rectangle getAreaUnidadesRetirada(int i) { return new Rectangle(areaUnidadesRetirada[i]); }

    private Polygon copiar(Polygon origem) {
        return new Polygon(origem.xpoints, origem.ypoints, origem.npoints);
    }
}
