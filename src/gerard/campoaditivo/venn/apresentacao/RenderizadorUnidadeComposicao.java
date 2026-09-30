package gerard.campoaditivo.venn.apresentacao;

import java.awt.Color;

public final class RenderizadorUnidadeComposicao extends RenderizadorUnidadeVennAbstrato {
    protected Color corBase() { return gerard.ui.CoresRepresentacaoGerard.COMPOSICAO_BASE; }
    protected Color corBorda() { return gerard.ui.CoresRepresentacaoGerard.COMPOSICAO_BORDA; }
    protected Color corTexto() { return gerard.ui.CoresRepresentacaoGerard.COMPOSICAO_TEXTO; }
}
