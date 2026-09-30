package gerard.campoaditivo.venn.apresentacao;

import java.awt.Color;

public final class RenderizadorUnidadeTransformacaoNegativa extends RenderizadorUnidadeVennAbstrato {
    protected Color corBase() { return gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_NEGATIVA_BASE; }
    protected Color corBorda() { return gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_NEGATIVA_BORDA; }
    protected Color corTexto() { return gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_NEGATIVA_TEXTO; }
}
