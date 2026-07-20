package gerard.campoaditivo.venn.servico;

import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.NormalizadorRotulosSemanticosDiagrama;
import gerard.campoaditivo.transformacao.processo.LayoutProcessoTransformacao;
import gerard.campoaditivo.venn.modelo.CenaDiagramaVenn;
import gerard.campoaditivo.venn.modelo.ConectorDiagramaVenn;
import gerard.campoaditivo.venn.modelo.NoDiagramaVenn;
import gerard.i18n.ServicoLocalizacao;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class GeradorCenaDiagramaVenn {
    private final LayoutProcessoTransformacao layoutProcessoTransformacao =
            new LayoutProcessoTransformacao();
    private final NormalizadorRotulosSemanticosDiagrama normalizadorRotulos =
            new NormalizadorRotulosSemanticosDiagrama();

    public CenaDiagramaVenn gerar(TipoSituacaoAditiva tipo, Rectangle area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        if (tipo == null) {
            tipo = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
        }
        definicao = normalizadorRotulos.garantir(
                tipo, definicao, ServicoLocalizacao.getInstancia());
        List<NoDiagramaVenn> nos = new ArrayList<NoDiagramaVenn>();
        List<ConectorDiagramaVenn> conectores = new ArrayList<ConectorDiagramaVenn>();

        int ax = area.x;
        int ay = area.y;
        int w = area.width;
        int h = area.height;

        switch (tipo) {
            case TRANSFORMACAO_MEDIDAS:
                return layoutProcessoTransformacao.criarCena(
                        area, definicao, valores);
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                nos.add(new NoDiagramaVenn(ax + 28, ay + h/2 - 60, 110, 110, definicao.getRotulo1(), valor(valores, 0), true));
                nos.add(new NoDiagramaVenn(ax + w/2 - 55, ay + h/2 - 55, 110, 110, definicao.getRotulo2(), valor(valores, 1), false));
                nos.add(new NoDiagramaVenn(ax + w - 148, ay + h/2 - 60, 120, 120, definicao.getRotulo3(), valor(valores, 2), true));
                conectores.add(seta(ax + 140, ay + h/2, ax + w/2 - 65, ay + h/2));
                conectores.add(seta(ax + w/2 + 65, ay + h/2, ax + w - 160, ay + h/2));
                break;
            case COMPOSICAO_MEDIDAS:
                // Diagrama de composição de coleções:
                // duas coleções à esquerda compõem a coleção total à direita.
                // As três regiões são retangulares/arredondadas, equilibradas e
                // funcionam como áreas de pertencimento para quadradinhos arrastáveis.
                int ladoParcela = Math.max(86, Math.min(110, Math.min(w / 5, Math.max(86, (h - 130) / 2))));
                int ladoResultado = Math.max(136, Math.min(166, Math.min(w / 3, Math.max(136, h - 140))));
                int espacoVertical = Math.max(22, Math.min(34, h / 12));
                int xEsquerda = ax + Math.max(24, w / 18);
                int ySuperior = ay + Math.max(58, h / 7);
                int yInferior = ySuperior + ladoParcela + espacoVertical;
                int alturaBlocoEsquerdo = (yInferior - ySuperior) + ladoParcela;
                // Move a coleção total um pouco para a esquerda para deixar
                // margem suficiente ao rótulo/valor à direita e reduzir o
                // comprimento da seta, melhorando a distribuição espacial.
                int margemDireitaResultado = Math.max(74, w / 10);
                int xResultado = ax + w - ladoResultado - margemDireitaResultado;
                int yResultado = ySuperior + Math.max(0, (alturaBlocoEsquerdo - ladoResultado) / 2);

                nos.add(new NoDiagramaVenn(xEsquerda, ySuperior, ladoParcela, ladoParcela, definicao.getRotulo1(), valor(valores, 0), true));
                nos.add(new NoDiagramaVenn(xEsquerda, yInferior, ladoParcela, ladoParcela, definicao.getRotulo2(), valor(valores, 1), true));
                nos.add(new NoDiagramaVenn(xResultado, yResultado, ladoResultado, ladoResultado, definicao.getRotulo3(), valor(valores, 2), true));
                int inicioSeta = xEsquerda + ladoParcela + Math.max(20, w / 34);
                int fimSeta = xResultado - Math.max(16, w / 50);
                conectores.add(seta(inicioSeta, ySuperior + alturaBlocoEsquerdo / 2, fimSeta, yResultado + ladoResultado / 2));
                break;
            case COMPARACAO_MEDIDAS:
                // Representação em gráfico de barras manipulável.
                // As duas barras utilizam quadradinhos arrastáveis para tornar
                // visível a comparação entre referido e referendo. O valor
                // relativo permanece como cartão informativo ao lado.
                int larguraBarra = Math.max(56, Math.min(72, w / 8));
                int alturaBarra = Math.max(220, Math.min(270, h - 110));
                // Alinha o topo das barras à altura visual do diagrama de
                // Vergnaud, que também é centralizado verticalmente no painel.
                // Os limites preservam números e rótulos em janelas compactas.
                int yAlinhadoAoVergnaud = ay + h / 2 - 70;
                int yMinimo = ay + 14;
                int yMaximo = ay + h - alturaBarra - 44;
                int yBarra = Math.max(yMinimo,
                        Math.min(yAlinhadoAoVergnaud, yMaximo));
                int xBarra1 = ax + Math.max(34, w / 14);
                int xBarra2 = xBarra1 + larguraBarra + Math.max(52, w / 10);
                int larguraCartao = 78;
                int alturaCartao = 72;
                int xCartao = xBarra2 + larguraBarra + Math.max(62, w / 9);
                int yCartao = yBarra + Math.max(64, (alturaBarra - alturaCartao) / 2);

                ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
                nos.add(new NoDiagramaVenn(xBarra1, yBarra, larguraBarra, alturaBarra,
                        loc.texto("ui.comparisonBars.referred"), valor(valores, 0), true));
                nos.add(new NoDiagramaVenn(xBarra2, yBarra, larguraBarra, alturaBarra,
                        loc.texto("ui.comparisonBars.referendo"), valor(valores, 2), true));
                nos.add(new NoDiagramaVenn(xCartao, yCartao, larguraCartao, alturaCartao,
                        loc.texto("ui.comparisonBars.relative"), valor(valores, 1), false));
                break;
            case COMPOSICAO_TRANSFORMACOES:
                nos.add(new NoDiagramaVenn(ax + 25, ay + 90, 110, 110, definicao.getRotulo1(), valor(valores, 0), false));
                nos.add(new NoDiagramaVenn(ax + 25, ay + h - 160, 110, 110, definicao.getRotulo2(), valor(valores, 1), false));
                nos.add(new NoDiagramaVenn(ax + w - 190, ay + h/2 - 95, 165, 165, definicao.getRotulo3(), valor(valores, 2), false));
                conectores.add(seta(ax + 145, ay + 145, ax + w - 198, ay + h/2 - 30));
                conectores.add(seta(ax + 145, ay + h - 105, ax + w - 198, ay + h/2 + 30));
                break;
            case TRANSFORMACAO_RELACAO:
                nos.add(new NoDiagramaVenn(ax + 25, ay + h/2 - 60, 110, 110, definicao.getRotulo1(), valor(valores, 0), false));
                nos.add(new NoDiagramaVenn(ax + w/2 - 55, ay + h/2 - 55, 110, 110, definicao.getRotulo2(), valor(valores, 1), false));
                nos.add(new NoDiagramaVenn(ax + w - 148, ay + h/2 - 60, 120, 120, definicao.getRotulo3(), valor(valores, 2), false));
                conectores.add(seta(ax + 140, ay + h/2, ax + w/2 - 65, ay + h/2));
                conectores.add(seta(ax + w/2 + 65, ay + h/2, ax + w - 160, ay + h/2));
                break;
            case COMPOSICAO_RELACOES:
                nos.add(new NoDiagramaVenn(ax + 25, ay + 90, 110, 110, definicao.getRotulo1(), valor(valores, 0), false));
                nos.add(new NoDiagramaVenn(ax + 25, ay + h - 160, 110, 110, definicao.getRotulo2(), valor(valores, 1), false));
                nos.add(new NoDiagramaVenn(ax + w - 190, ay + h/2 - 95, 165, 165, definicao.getRotulo3(), valor(valores, 2), false));
                conectores.add(seta(ax + 145, ay + 145, ax + w - 198, ay + h/2 - 30));
                conectores.add(seta(ax + 145, ay + h - 105, ax + w - 198, ay + h/2 + 30));
                break;
            default:
                return gerar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, area, definicao, valores);
        }

        return new CenaDiagramaVenn(nos, conectores);
    }

    private ConectorDiagramaVenn seta(int x1, int y1, int x2, int y2) {
        return new ConectorDiagramaVenn(x1, y1, x2, y2);
    }

    private int valor(int[] valores, int indice) {
        return (valores != null && indice >= 0 && indice < valores.length) ? valores[indice] : 0;
    }
}
