package gerard.Scaffolding.proximidade;

import java.awt.Color;

/**
 * Define as cores e a espessura visual de cada estado de realce.
 */
public class EstiloRealceAlvo {
    private final Color corPreenchimento;
    private final Color corBorda;
    private final float espessuraBorda;

    private EstiloRealceAlvo(Color corPreenchimento, Color corBorda, float espessuraBorda) {
        this.corPreenchimento = corPreenchimento;
        this.corBorda = corBorda;
        this.espessuraBorda = espessuraBorda;
    }

    public Color getCorPreenchimento() {
        return corPreenchimento;
    }

    public Color getCorBorda() {
        return corBorda;
    }

    public float getEspessuraBorda() {
        return espessuraBorda;
    }

    public static EstiloRealceAlvo paraEstado(EstadoRealceAlvo estado) {
        if (estado == EstadoRealceAlvo.PROXIMIDADE) {
            return new EstiloRealceAlvo(new Color(231, 244, 234), new Color(69, 128, 82), 2.4f);
        }
        if (estado == EstadoRealceAlvo.DROP_TARGET) {
            return new EstiloRealceAlvo(new Color(255, 247, 214), new Color(156, 118, 31), 2.6f);
        }
        if (estado == EstadoRealceAlvo.DRAG_OVER) {
            return new EstiloRealceAlvo(new Color(228, 238, 247), new Color(56, 104, 151), 2.5f);
        }
        if (estado == EstadoRealceAlvo.AFFORDANCE) {
            return new EstiloRealceAlvo(new Color(246, 240, 251), new Color(118, 88, 143), 2.0f);
        }
        if (estado == EstadoRealceAlvo.SNAP) {
            return new EstiloRealceAlvo(new Color(250, 235, 224), new Color(169, 92, 47), 2.8f);
        }
        return new EstiloRealceAlvo(gerard.ui.UITemaGerard.COR_SUPERFICIE, gerard.ui.UITemaGerard.COR_BORDA, 1.5f);
    }
}
