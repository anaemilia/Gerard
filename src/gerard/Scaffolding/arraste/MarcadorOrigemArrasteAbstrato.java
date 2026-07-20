package gerard.Scaffolding.arraste;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

/** Template comum de apresentação do lugar de origem. */
public abstract class MarcadorOrigemArrasteAbstrato
        implements MarcadorOrigemArraste {

    private DesenhavelFantasmaOrigem origem;

    protected abstract Color obterCor();
    protected abstract float obterOpacidade();
    protected abstract float obterEspessura();
    protected abstract float[] obterTracejado();

    @Override
    public final void iniciar(DesenhavelFantasmaOrigem novaOrigem) {
        Rectangle limites = novaOrigem == null
                ? null : novaOrigem.obterLimitesOrigem();
        origem = limites == null || limites.width <= 0 || limites.height <= 0
                ? null : novaOrigem;
    }

    @Override
    public final void desenhar(Graphics2D g2) {
        if (g2 == null || origem == null) {
            return;
        }
        Graphics2D fantasma = (Graphics2D) g2.create();
        fantasma.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Composite compositeAnterior = fantasma.getComposite();
        Stroke strokeAnterior = fantasma.getStroke();
        fantasma.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, obterOpacidade()));
        fantasma.setColor(obterCor());
        fantasma.setStroke(new BasicStroke(
                obterEspessura(), BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 10.0f, obterTracejado(), 0.0f));
        origem.desenharContorno(fantasma);
        fantasma.setStroke(strokeAnterior);
        fantasma.setComposite(compositeAnterior);
        fantasma.dispose();
    }

    @Override
    public final void limpar() {
        origem = null;
    }

    @Override
    public final boolean estaAtivo() {
        return origem != null;
    }
}
