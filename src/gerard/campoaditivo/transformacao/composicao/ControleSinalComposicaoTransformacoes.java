package gerard.campoaditivo.transformacao.composicao;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.transformacao.processo.TipoProcessoTransformacao;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.List;

/**
 * Posiciona e desenha os controles de incremento e decremento junto ao funil
 * ativo de uma das três transformações de Composição de Transformações —
 * análogo a
 * {@link gerard.campoaditivo.transformacao.processo.ControleSinalProcessoTransformacao},
 * mas parametrizado pelo índice do funil (0/1/2: Transformação 1,
 * Transformação 2, Transformação Final) em vez de assumir um único funil
 * implícito. A classe não altera o valor semântico; apenas oferece
 * geometria, hit-test e projeção visual para o controlador da tela.
 */
public final class ControleSinalComposicaoTransformacoes {
    private static final int TAMANHO = 20;
    private static final int ESPACO_FUNIL = 7;
    private static final int ESPACO_CONTROLES = 5;

    private static final Color FUNDO = new Color(255, 255, 255);
    private static final Color FUNDO_MAIS_FOCADO = new Color(234, 244, 255);
    private static final Color FUNDO_MENOS_FOCADO = new Color(255, 242, 242);
    private static final Color AZUL = new Color(59, 130, 246);
    private static final Color AZUL_ESCURO = new Color(29, 78, 216);
    private static final Color VERMELHO = new Color(220, 86, 86);
    private static final Color VERMELHO_ESCURO = new Color(176, 48, 48);
    private static final Color FUNDO_DESABILITADO = new Color(247, 248, 250);
    private static final Color BORDA_DESABILITADA = new Color(180, 188, 198);
    private static final Color SINAL_DESABILITADO = new Color(145, 154, 166);

    public Rectangle obterAreaAdicionar(List<CirculoVenn> zonas,
            EstadoComposicaoTransformacoes estado, int indice) {
        return obterAreas(zonas, estado, indice)[0];
    }

    public Rectangle obterAreaRemover(List<CirculoVenn> zonas,
            EstadoComposicaoTransformacoes estado, int indice) {
        return obterAreas(zonas, estado, indice)[1];
    }

    public boolean contemAdicionar(List<CirculoVenn> zonas,
            EstadoComposicaoTransformacoes estado, int indice, int x, int y) {
        return obterAreaAdicionar(zonas, estado, indice).contains(x, y);
    }

    public boolean contemRemover(List<CirculoVenn> zonas,
            EstadoComposicaoTransformacoes estado, int indice, int x, int y) {
        return obterAreaRemover(zonas, estado, indice).contains(x, y);
    }

    public void desenharAdicionar(Graphics2D g2, List<CirculoVenn> zonas,
            EstadoComposicaoTransformacoes estado, int indice, boolean focado,
            boolean habilitado) {
        desenhar(g2, obterAreaAdicionar(zonas, estado, indice), true, focado,
                habilitado);
    }

    public void desenharRemover(Graphics2D g2, List<CirculoVenn> zonas,
            EstadoComposicaoTransformacoes estado, int indice, boolean focado,
            boolean habilitado) {
        desenhar(g2, obterAreaRemover(zonas, estado, indice), false, focado,
                habilitado);
    }

    private Rectangle[] obterAreas(List<CirculoVenn> zonas,
            EstadoComposicaoTransformacoes estado, int indice) {
        if (zonas == null || zonas.size() < 3 || estado == null
                || indice < 0 || indice > 2) {
            return new Rectangle[] {new Rectangle(), new Rectangle()};
        }
        GeometriaComposicaoTransformacoes geo =
                GeometriaComposicaoTransformacoes.calcular(zonas);
        Rectangle ancora = obterAncoraFunil(geo, estado.getTipoProcesso(indice), indice);
        int x = ancora.x + ancora.width + ESPACO_FUNIL;
        int y = ancora.y + 2;
        Rectangle mais = new Rectangle(x, y, TAMANHO, TAMANHO);
        Rectangle menos = new Rectangle(x,
                y + TAMANHO + ESPACO_CONTROLES, TAMANHO, TAMANHO);
        return new Rectangle[] {mais, menos};
    }

    private Rectangle obterAncoraFunil(GeometriaComposicaoTransformacoes geo,
            TipoProcessoTransformacao tipo, int indice) {
        if (tipo == TipoProcessoTransformacao.RETIRADA) {
            return unir(geo.getFunilRetirada(indice).getBounds(),
                    geo.getHasteRetirada(indice));
        }
        return unir(geo.getFunilInsercao(indice).getBounds(),
                geo.getHasteInsercao(indice));
    }

    private Rectangle unir(Rectangle a, Rectangle b) {
        Rectangle resultado = new Rectangle(a);
        resultado.add(b);
        return resultado;
    }

    private void desenhar(Graphics2D g2, Rectangle area, boolean adicao,
            boolean focado, boolean habilitado) {
        if (g2 == null || area == null || area.width <= 0 || area.height <= 0) {
            return;
        }
        Object antialiasAnterior = g2.getRenderingHint(
                RenderingHints.KEY_ANTIALIASING);
        Stroke strokeAnterior = g2.getStroke();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Color fundoFocado = adicao ? FUNDO_MAIS_FOCADO : FUNDO_MENOS_FOCADO;
        Color borda = adicao ? AZUL : VERMELHO;
        Color sinal = adicao ? AZUL_ESCURO : VERMELHO_ESCURO;
        g2.setColor(!habilitado ? FUNDO_DESABILITADO
                : (focado ? fundoFocado : FUNDO));
        g2.fillOval(area.x, area.y, area.width, area.height);
        g2.setColor(habilitado ? borda : BORDA_DESABILITADA);
        g2.setStroke(new BasicStroke(habilitado && focado ? 1.8f : 1.4f));
        g2.drawOval(area.x, area.y, area.width, area.height);

        int cx = area.x + area.width / 2;
        int cy = area.y + area.height / 2;
        int raio = 4;
        g2.setColor(habilitado ? sinal : SINAL_DESABILITADO);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - raio, cy, cx + raio, cy);
        if (adicao) {
            g2.drawLine(cx, cy - raio, cx, cy + raio);
        }

        g2.setStroke(strokeAnterior);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                antialiasAnterior);
    }
}
