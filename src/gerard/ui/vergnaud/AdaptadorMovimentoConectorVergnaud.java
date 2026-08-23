package gerard.ui.vergnaud;

import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.interacao.arraste.AlvoMovelIncremental;
import gerard.interacao.geometria.LimitesMovimento;
import java.awt.Rectangle;

/**
 * Adaptador da representação desktop/Swing para o protocolo portátil de
 * arraste incremental. É o único participante desse protocolo que traduz
 * entre Rectangle e LimitesMovimento.
 */
public final class AdaptadorMovimentoConectorVergnaud
        implements AlvoMovelIncremental {
    private final ConectorVergnaud conector;

    public AdaptadorMovimentoConectorVergnaud(ConectorVergnaud conector) {
        if (conector == null) {
            throw new IllegalArgumentException("conector obrigatorio");
        }
        this.conector = conector;
    }

    @Override
    public void moverPor(int deltaX, int deltaY, LimitesMovimento limites) {
        conector.mover(deltaX, deltaY, paraRectangle(limites));
    }

    public ConectorVergnaud obterConector() {
        return conector;
    }

    public static LimitesMovimento traduzir(Rectangle area) {
        if (area == null) {
            throw new IllegalArgumentException("area obrigatoria");
        }
        return new LimitesMovimento(
                area.x, area.x + area.width,
                area.y, area.y + area.height);
    }

    private Rectangle paraRectangle(LimitesMovimento limites) {
        if (limites == null) {
            throw new IllegalArgumentException("limites obrigatorios");
        }
        return new Rectangle(
                limites.getMinimoX(),
                limites.getMinimoY(),
                limites.getMaximoX() - limites.getMinimoX(),
                limites.getMaximoY() - limites.getMinimoY());
    }
}
