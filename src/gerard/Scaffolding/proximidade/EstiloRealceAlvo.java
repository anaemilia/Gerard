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
            return new EstiloRealceAlvo(gerard.ui.UITemaGerard.COR_SUCESSO_FUNDO, gerard.ui.UITemaGerard.COR_SUCESSO, 2.4f);
        }
        if (estado == EstadoRealceAlvo.DROP_TARGET) {
            return new EstiloRealceAlvo(gerard.ui.UITemaGerard.COR_ALERTA_FUNDO, gerard.ui.UITemaGerard.COR_ALERTA, 2.6f);
        }
        if (estado == EstadoRealceAlvo.DRAG_OVER) {
            return new EstiloRealceAlvo(gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE, gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO, 2.5f);
        }
        if (estado == EstadoRealceAlvo.AFFORDANCE) {
            return new EstiloRealceAlvo(gerard.ui.UITemaGerard.COR_SUPERFICIE, gerard.ui.UITemaGerard.COR_BORDA_CONTROLE, 2.0f);
        }
        if (estado == EstadoRealceAlvo.SNAP) {
            return new EstiloRealceAlvo(gerard.ui.UITemaGerard.COR_SUPERFICIE_SUAVE, gerard.ui.UITemaGerard.COR_TEXTO, 2.8f);
        }
        return new EstiloRealceAlvo(gerard.ui.UITemaGerard.COR_SUPERFICIE, gerard.ui.UITemaGerard.COR_BORDA, 1.5f);
    }
}
