package gerard.ui.ajuda;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Envoltório visual que faz o conteúdo surgir por transparência crescente quando passa a ser
 * exibido. Pura apresentação: não sabe qual apoio contém nem por que foi acionado.
 */
public final class PainelAparicaoGradual extends JPanel {

    private static final int DURACAO_MS = 1100;
    private static final int PASSO_MS = 30;

    private float opacidade = 0f;
    private long inicio;
    private Timer relogio;

    public PainelAparicaoGradual(JComponent conteudo) {
        super(new BorderLayout());
        setOpaque(false);
        add(conteudo, BorderLayout.CENTER);
    }

    /**
     * O dono pode criar e posicionar este painel durante a pintura e chamar apenas
     * {@code doLayout()}; sem isto o conteúdo ficaria sem layout recursivo (moldura
     * visível, imagem vazia).
     */
    @Override
    public void doLayout() {
        super.doLayout();
        for (java.awt.Component filho : getComponents()) {
            organizarEmProfundidade(filho);
        }
    }

    private static void organizarEmProfundidade(java.awt.Component componente) {
        if (componente instanceof java.awt.Container) {
            java.awt.Container recipiente = (java.awt.Container) componente;
            recipiente.doLayout();
            for (java.awt.Component filho : recipiente.getComponents()) {
                organizarEmProfundidade(filho);
            }
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        opacidade = 0f;
        inicio = System.currentTimeMillis();
        if (relogio != null) {
            relogio.stop();
        }
        relogio = new Timer(PASSO_MS, e -> {
            float t = Math.min(1f, (System.currentTimeMillis() - inicio) / (float) DURACAO_MS);
            opacidade = 1f - (1f - t) * (1f - t) * (1f - t);
            repaint();
            if (t >= 1f) {
                relogio.stop();
            }
        });
        relogio.start();
    }

    @Override
    public void removeNotify() {
        if (relogio != null) {
            relogio.stop();
        }
        super.removeNotify();
    }

    @Override
    public void paint(Graphics g) {
        if (opacidade >= 1f) {
            super.paint(g);
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, opacidade)));
            super.paint(g2);
        } finally {
            g2.dispose();
        }
    }
}
