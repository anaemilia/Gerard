package gerard.ui.ajuda;

import gerard.ui.UITemaGerard;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Animação da historinha com as legendas DENTRO dela: pinta, sobre a parte inferior da imagem, a
 * legenda cujo intervalo contém o instante atual do laço. O cronograma (texto, início, fim, duração)
 * vem pronto da projeção da API; aqui só se mede o tempo desde que a animação entrou em cena e se
 * desenha. Não escolhe nem compõe texto.
 */
final class RotuloAnimacaoComLegendas extends JLabel {
    private static final int PASSO_MS = 100;
    private static final int MARGEM = 12;

    private static final class Legenda {
        final String texto;
        final double inicio;
        final double fim;

        Legenda(String texto, double inicio, double fim) {
            this.texto = texto;
            this.inicio = inicio;
            this.fim = fim;
        }
    }

    private final List<Legenda> legendas = new ArrayList<Legenda>();
    private double duracao;
    private long inicioNanos;
    private final Timer relogio = new Timer(PASSO_MS, e -> repaint());

    RotuloAnimacaoComLegendas(ImageIcon icone) {
        super(icone);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    /** Aplica o cronograma da projeção ({@code duracao_s} e {@code legendas}); ausente = sem legenda. */
    void definirCronograma(Map<?, ?> item) {
        legendas.clear();
        duracao = 0.0;
        Object d = item.get("duracao_s");
        Object lista = item.get("legendas");
        if (d instanceof Number && lista instanceof List) {
            duracao = ((Number) d).doubleValue();
            for (Object o : (List<?>) lista) {
                Map<?, ?> m = (Map<?, ?>) o;
                legendas.add(new Legenda(String.valueOf(m.get("texto")),
                        ((Number) m.get("inicio_s")).doubleValue(),
                        ((Number) m.get("fim_s")).doubleValue()));
            }
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        inicioNanos = 0;     // o laço começa na primeira pintura do GIF (ver paintComponent)
        if (!legendas.isEmpty()) {
            relogio.start();
        }
    }

    @Override
    public void removeNotify() {
        relogio.stop();
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics g) {
        desenharAnimacaoEscalada(g);      // escala a cada quadro: a animação segue viva em qualquer tamanho
        if (legendas.isEmpty() || duracao <= 0.0) {
            return;
        }
        if (inicioNanos == 0) {
            // O GIF só começa a animar quando é desenhado pela primeira vez: é daí que se conta o laço.
            inicioNanos = System.nanoTime();
        }
        double t = ((System.nanoTime() - inicioNanos) / 1e9) % duracao;
        for (Legenda legenda : legendas) {
            if (t >= legenda.inicio && t < legenda.fim) {
                desenhar((Graphics2D) g.create(), legenda.texto);
                return;
            }
        }
    }

    /**
     * Área que a animação ocupa dentro do rótulo: a maior que cabe sem distorcer (ocupa toda a área de
     * renderização, para cima ou para baixo); a legenda fica DENTRO dela.
     */
    java.awt.Rectangle areaDaAnimacao() {
        javax.swing.Icon icone = getIcon();
        if (icone == null || icone.getIconWidth() <= 0 || icone.getIconHeight() <= 0) {
            return new java.awt.Rectangle(0, 0, getWidth(), getHeight());
        }
        double escala = Math.min((double) getWidth() / icone.getIconWidth(),
                (double) getHeight() / icone.getIconHeight());
        int w = Math.max(1, (int) Math.round(icone.getIconWidth() * escala));
        int h = Math.max(1, (int) Math.round(icone.getIconHeight() * escala));
        return new java.awt.Rectangle((getWidth() - w) / 2, (getHeight() - h) / 2, w, h);
    }

    private void desenharAnimacaoEscalada(Graphics g) {
        javax.swing.Icon icone = getIcon();
        if (!(icone instanceof ImageIcon)) {
            return;
        }
        java.awt.Rectangle a = areaDaAnimacao();
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(((ImageIcon) icone).getImage(), a.x, a.y, a.width, a.height, this);
        } finally {
            g2.dispose();
        }
    }

    private void desenhar(Graphics2D g2, String texto) {
        try {
            java.awt.Rectangle area = areaDaAnimacao();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            FontMetrics fm = g2.getFontMetrics();
            int largura = Math.min(area.width - 2 * MARGEM, fm.stringWidth(texto) + 2 * MARGEM);
            int altura = fm.getHeight() + 8;
            int x = area.x + (area.width - largura) / 2;
            int y = area.y + area.height - altura - MARGEM;
            g2.setColor(new Color(255, 255, 255, 225));
            g2.fillRoundRect(x, y, largura, altura, 10, 10);
            g2.setColor(UITemaGerard.COR_TEXTO);
            g2.drawString(texto, x + MARGEM, y + 4 + fm.getAscent());
        } finally {
            g2.dispose();
        }
    }
}
