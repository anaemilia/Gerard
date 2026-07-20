package gerard.campoaditivo.transformacao.processo;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.List;

/**
 * Posiciona e desenha os controles de incremento e decremento junto ao funil
 * ativo da transformação. A classe não altera o valor semântico; apenas
 * oferece geometria, hit-test e projeção visual para o controlador da tela.
 */
public final class ControleSinalProcessoTransformacao {
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
            EstadoProcessoTransformacao estado) {
        return obterAreas(zonas, estado)[0];
    }

    public Rectangle obterAreaRemover(List<CirculoVenn> zonas,
            EstadoProcessoTransformacao estado) {
        return obterAreas(zonas, estado)[1];
    }

    public boolean contemAdicionar(List<CirculoVenn> zonas,
            EstadoProcessoTransformacao estado, int x, int y) {
        return obterAreaAdicionar(zonas, estado).contains(x, y);
    }

    public boolean contemRemover(List<CirculoVenn> zonas,
            EstadoProcessoTransformacao estado, int x, int y) {
        return obterAreaRemover(zonas, estado).contains(x, y);
    }

    public void desenharAdicionar(Graphics2D g2, List<CirculoVenn> zonas,
            EstadoProcessoTransformacao estado, boolean focado,
            boolean habilitado) {
        desenhar(g2, obterAreaAdicionar(zonas, estado), true, focado,
                habilitado);
    }

    public void desenharRemover(Graphics2D g2, List<CirculoVenn> zonas,
            EstadoProcessoTransformacao estado, boolean focado,
            boolean habilitado) {
        desenhar(g2, obterAreaRemover(zonas, estado), false, focado,
                habilitado);
    }

    private Rectangle[] obterAreas(List<CirculoVenn> zonas,
            EstadoProcessoTransformacao estado) {
        if (zonas == null || zonas.size() < 3 || estado == null) {
            return new Rectangle[] {new Rectangle(), new Rectangle()};
        }
        GeometriaProcessoTransformacao geo =
                GeometriaProcessoTransformacao.calcular(zonas);
        Rectangle ancora = obterAncoraFunil(geo, estado.getTipoProcesso());
        int x = ancora.x + ancora.width + ESPACO_FUNIL;
        int y = ancora.y + 2;
        Rectangle mais = new Rectangle(x, y, TAMANHO, TAMANHO);
        Rectangle menos = new Rectangle(x,
                y + TAMANHO + ESPACO_CONTROLES, TAMANHO, TAMANHO);
        return new Rectangle[] {mais, menos};
    }

    private Rectangle obterAncoraFunil(GeometriaProcessoTransformacao geo,
            TipoProcessoTransformacao tipo) {
        if (tipo == TipoProcessoTransformacao.RETIRADA) {
            return unir(geo.getFunilRetirada().getBounds(),
                    geo.getHasteRetirada());
        }
        // Na transformação desconhecida ou neutra, o funil superior já é
        // apresentado como estrutura disponível para a primeira ação.
        return unir(geo.getFunilInsercao().getBounds(),
                geo.getHasteInsercao());
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
