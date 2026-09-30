package gerard.campoaditivo.venn.apresentacao;

import java.awt.Color;

public final class RenderizadorUnidadeComparacaoExcedente extends RenderizadorUnidadeVennAbstrato {
    protected Color corBase() { return gerard.ui.CoresRepresentacaoGerard.COMPARACAO_EXCEDENTE_BASE; }
    protected Color corBorda() { return gerard.ui.CoresRepresentacaoGerard.COMPARACAO_EXCEDENTE_BORDA; }
    protected Color corTexto() { return gerard.ui.CoresRepresentacaoGerard.COMPARACAO_EXCEDENTE_TEXTO; }
}
