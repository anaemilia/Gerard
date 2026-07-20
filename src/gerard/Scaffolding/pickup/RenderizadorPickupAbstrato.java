package gerard.Scaffolding.pickup;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

/**
 * Template comum de elevação, escala e sombra. As subclasses definem apenas
 * a intensidade visual, mantendo o comportamento uniforme por herança.
 */
public abstract class RenderizadorPickupAbstrato implements RenderizadorPickup {

    protected abstract double obterEscala();
    protected abstract int obterElevacao();
    protected abstract int obterDeslocamentoSombra();
    protected abstract float obterAlphaSombra();

    @Override
    public final void desenharEmPrimeiroPlano(Graphics2D g2, DesenhavelPickup elemento) {
        if (g2 == null || elemento == null) {
            return;
        }
        Rectangle limites = elemento.obterLimitesVisuais();
        if (limites == null || limites.width <= 0 || limites.height <= 0) {
            return;
        }

        double centroX = limites.getCenterX();
        double centroY = limites.getCenterY();
        AffineTransform transformacao = new AffineTransform();
        transformacao.translate(centroX, centroY - obterElevacao());
        transformacao.scale(obterEscala(), obterEscala());
        transformacao.translate(-centroX, -centroY);

        Graphics2D sombra = (Graphics2D) g2.create();
        sombra.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        sombra.transform(transformacao);
        Composite compositeAnterior = sombra.getComposite();
        sombra.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, obterAlphaSombra()));
        sombra.setColor(new Color(22, 35, 48));
        int arco = Math.max(6, Math.min(14,
                Math.min(limites.width, limites.height) / 3));
        sombra.fillRoundRect(
                limites.x + obterDeslocamentoSombra(),
                limites.y + obterDeslocamentoSombra(),
                limites.width,
                limites.height,
                arco,
                arco);
        sombra.setComposite(compositeAnterior);
        sombra.dispose();

        Graphics2D conteudo = (Graphics2D) g2.create();
        conteudo.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        conteudo.transform(transformacao);
        elemento.desenharConteudo(conteudo);
        conteudo.dispose();
    }
}
