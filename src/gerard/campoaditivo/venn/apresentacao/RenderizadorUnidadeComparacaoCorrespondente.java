package gerard.campoaditivo.venn.apresentacao;

import java.awt.Color;

public final class RenderizadorUnidadeComparacaoCorrespondente extends RenderizadorUnidadeVennAbstrato {
    protected Color corBase() { return new Color(235, 154, 154); }
    protected Color corBorda() { return new Color(176, 92, 92); }
    protected Color corTexto() { return new Color(58, 36, 36); }
}
