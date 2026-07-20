package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.List;

/** Geometria derivada das três zonas semânticas da transformação. */
public final class GeometriaProcessoTransformacao {
    private final Rectangle canal;
    private final Polygon funilInsercao;
    private final Rectangle hasteInsercao;
    private final Polygon funilRetirada;
    private final Rectangle hasteRetirada;
    private final Rectangle areaUnidadesInsercao;
    private final Rectangle areaUnidadesRetirada;

    private GeometriaProcessoTransformacao(Rectangle canal,
            Polygon funilInsercao, Rectangle hasteInsercao,
            Polygon funilRetirada, Rectangle hasteRetirada,
            Rectangle areaUnidadesInsercao,
            Rectangle areaUnidadesRetirada) {
        this.canal = canal;
        this.funilInsercao = funilInsercao;
        this.hasteInsercao = hasteInsercao;
        this.funilRetirada = funilRetirada;
        this.hasteRetirada = hasteRetirada;
        this.areaUnidadesInsercao = areaUnidadesInsercao;
        this.areaUnidadesRetirada = areaUnidadesRetirada;
    }

    public static GeometriaProcessoTransformacao calcular(
            List<CirculoVenn> zonas) {
        if (zonas == null || zonas.size() < 3) {
            Rectangle vazio = new Rectangle();
            return new GeometriaProcessoTransformacao(vazio,
                    new Polygon(), vazio, new Polygon(), vazio, vazio, vazio);
        }
        CirculoVenn inicial = zonas.get(0);
        CirculoVenn processo = zonas.get(1);
        CirculoVenn finalEstado = zonas.get(2);
        int canalY = inicial.y + inicial.altura / 2;
        int canalX = inicial.x + inicial.largura;
        int canalFim = finalEstado.x;
        Rectangle canal = new Rectangle(canalX, canalY - 8,
                Math.max(1, canalFim - canalX), 16);

        // O cone do funil tem forma fixa (largura e altura escalam juntas,
        // a partir da largura da caixa "processo" — que é o que varia com a
        // janela na referência minimizada). O espaço vertical sobrando entre
        // o cone e o canal é absorvido pela haste (o cano), não pelo cone —
        // antes o cone era esticado para preencher todo o vão até o canal,
        // ficando alto e fino em janelas maximizadas.
        final float REFERENCIA_LARGURA_PROCESSO = 116f;
        float escala = Math.max(1f, Math.min(1.4f,
                processo.largura / REFERENCIA_LARGURA_PROCESSO));

        int centroX = processo.x + processo.largura / 2;
        int meiaGargalo = 10;

        int larguraTopo = Math.round(92 * escala);
        int alturaFunilInsercao = Math.round(58 * escala);
        int topoInsercao = inicial.y;
        int baseInsercao = Math.max(topoInsercao + 40,
                Math.min(topoInsercao + alturaFunilInsercao, canalY - 10));
        int meia = larguraTopo / 2;
        Polygon funilInsercao = new Polygon(
                new int[] {centroX - meia, centroX + meia,
                    centroX + meiaGargalo, centroX - meiaGargalo},
                new int[] {topoInsercao, topoInsercao,
                    baseInsercao, baseInsercao}, 4);
        Rectangle hasteInsercao = new Rectangle(
                centroX - meiaGargalo, baseInsercao,
                meiaGargalo * 2, Math.max(1, canalY - baseInsercao));
        Rectangle areaUnidadesInsercao = new Rectangle(
                centroX - meia + 8, topoInsercao + 8,
                Math.max(20, larguraTopo - 16),
                Math.max(24, baseInsercao - topoInsercao - 14));

        int larguraAbertura = Math.round(88 * escala);
        int alturaFunilRetirada = Math.round(54 * escala);
        int topoRetirada = canalY + 10;
        int baseRetirada = processo.y + processo.altura - 38;
        int fimConeRetirada = Math.max(topoRetirada + 30,
                Math.min(topoRetirada + alturaFunilRetirada, baseRetirada - 20));
        int meiaAbertura = larguraAbertura / 2;
        Polygon funilRetirada = new Polygon(
                new int[] {centroX - meiaAbertura, centroX + meiaAbertura,
                    centroX + meiaGargalo, centroX - meiaGargalo},
                new int[] {topoRetirada, topoRetirada,
                    fimConeRetirada, fimConeRetirada}, 4);
        Rectangle hasteRetirada = new Rectangle(
                centroX - meiaGargalo, fimConeRetirada,
                meiaGargalo * 2, Math.max(1, baseRetirada - fimConeRetirada));
        Rectangle areaUnidadesRetirada = new Rectangle(
                centroX - meiaAbertura + 8, topoRetirada + 8,
                Math.max(24, larguraAbertura - 16),
                Math.max(28, fimConeRetirada - topoRetirada - 12));

        return new GeometriaProcessoTransformacao(canal,
                funilInsercao, hasteInsercao,
                funilRetirada, hasteRetirada,
                areaUnidadesInsercao, areaUnidadesRetirada);
    }

    public Rectangle getCanal() { return new Rectangle(canal); }
    public Polygon getFunilInsercao() { return copiar(funilInsercao); }
    public Rectangle getHasteInsercao() { return new Rectangle(hasteInsercao); }
    public Polygon getFunilRetirada() { return copiar(funilRetirada); }
    public Rectangle getHasteRetirada() { return new Rectangle(hasteRetirada); }
    public Rectangle getAreaUnidadesInsercao() { return new Rectangle(areaUnidadesInsercao); }
    public Rectangle getAreaUnidadesRetirada() { return new Rectangle(areaUnidadesRetirada); }

    private Polygon copiar(Polygon origem) {
        return new Polygon(origem.xpoints, origem.ypoints, origem.npoints);
    }
}
