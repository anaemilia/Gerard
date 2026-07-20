package gerard.Scaffolding.venn;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.venn.interacao.RepresentacaoComUnidadesRemoviveis;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 * Controle visual discreto para remover uma unidade de agrupamentos que
 * possuem quadradinhos visíveis. O sinal de subtração é desenhado diretamente,
 * evitando dependência de glifos ou fontes do sistema.
 */
public final class ControleRemoverQuadradinhoVenn {
    private static final int TAMANHO = 20;
    private static final int AFASTAMENTO_HORIZONTAL = 2;
    private static final int AFASTAMENTO_VERTICAL = 6;
    private static final int ESPACO_ENTRE_CONTROLES = 5;

    private static final Color FUNDO = new Color(255, 255, 255);
    private static final Color FUNDO_FOCADO = new Color(243, 244, 246);
    private static final Color BORDA = new Color(148, 163, 184);
    private static final Color SINAL = new Color(71, 85, 105);
    private static final Color FUNDO_DESABILITADO = new Color(247, 248, 250);
    private static final Color BORDA_DESABILITADA = new Color(180, 188, 198);
    private static final Color SINAL_DESABILITADO = new Color(145, 154, 166);

    public Rectangle obterArea(RepresentacaoComUnidadesRemoviveis representacao, Rectangle areaDiagrama) {
        if (representacao == null || areaDiagrama == null) {
            return new Rectangle();
        }
        CirculoVenn agrupamento = representacao.obterAgrupamento();

        int x = agrupamento.x + agrupamento.largura + AFASTAMENTO_HORIZONTAL;
        if (x + TAMANHO > areaDiagrama.x + areaDiagrama.width - 8) {
            x = agrupamento.x - TAMANHO - AFASTAMENTO_HORIZONTAL;
        }

        int yAdicionar = agrupamento.y - AFASTAMENTO_VERTICAL;
        yAdicionar = Math.max(areaDiagrama.y + 38, yAdicionar);
        yAdicionar = Math.min(yAdicionar,
                areaDiagrama.y + areaDiagrama.height - TAMANHO - 8);

        int y = yAdicionar + TAMANHO + ESPACO_ENTRE_CONTROLES;
        if (y + TAMANHO > areaDiagrama.y + areaDiagrama.height - 8) {
            y = yAdicionar - TAMANHO - ESPACO_ENTRE_CONTROLES;
        }
        y = Math.max(areaDiagrama.y + 38, y);

        return new Rectangle(x, y, TAMANHO, TAMANHO);
    }

    public boolean contem(RepresentacaoComUnidadesRemoviveis representacao, Rectangle areaDiagrama,
            int x, int y) {
        return obterArea(representacao, areaDiagrama).contains(x, y);
    }

    public void desenhar(Graphics2D g2, RepresentacaoComUnidadesRemoviveis representacao,
            Rectangle areaDiagrama, boolean focado) {
        desenhar(g2, representacao, areaDiagrama, focado, true);
    }

    public void desenhar(Graphics2D g2, RepresentacaoComUnidadesRemoviveis representacao,
            Rectangle areaDiagrama, boolean focado, boolean habilitado) {
        if (g2 == null || representacao == null || areaDiagrama == null) {
            return;
        }

        Rectangle area = obterArea(representacao, areaDiagrama);
        Object antialiasAnterior = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Stroke strokeAnterior = g2.getStroke();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(!habilitado
                ? FUNDO_DESABILITADO
                : (focado ? FUNDO_FOCADO : FUNDO));
        g2.fillOval(area.x, area.y, area.width, area.height);
        g2.setColor(habilitado ? BORDA : BORDA_DESABILITADA);
        g2.setStroke(new BasicStroke(habilitado && focado ? 1.8f : 1.4f));
        g2.drawOval(area.x, area.y, area.width, area.height);

        int cx = area.x + area.width / 2;
        int cy = area.y + area.height / 2;
        int raio = 4;
        g2.setColor(habilitado ? SINAL : SINAL_DESABILITADO);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - raio, cy, cx + raio, cy);

        g2.setStroke(strokeAnterior);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasAnterior);
    }
}
