package gerard.campoaditivo.venn.apresentacao;

import java.awt.Color;

public final class RenderizadorUnidadeTransformacaoPositiva extends RenderizadorUnidadeVennAbstrato {
    protected Color corBase() { return gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_POSITIVA_BASE; }
    protected Color corBorda() { return gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_POSITIVA_BORDA; }
    protected Color corTexto() { return gerard.ui.CoresRepresentacaoGerard.TRANSFORMACAO_POSITIVA_TEXTO; }
}
