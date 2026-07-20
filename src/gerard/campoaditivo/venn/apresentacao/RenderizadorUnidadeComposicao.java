package gerard.campoaditivo.venn.apresentacao;

import java.awt.Color;

public final class RenderizadorUnidadeComposicao extends RenderizadorUnidadeVennAbstrato {
    protected Color corBase() { return new Color(239, 68, 68); }
    protected Color corBorda() { return new Color(30, 64, 175); }
    protected Color corTexto() { return Color.WHITE; }
}
