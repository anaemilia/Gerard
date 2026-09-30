package gerard.campoaditivo.venn.apresentacao;

import java.awt.Color;

public final class RenderizadorUnidadeComparacaoCorrespondente extends RenderizadorUnidadeVennAbstrato {
    protected Color corBase() { return gerard.ui.CoresRepresentacaoGerard.COMPARACAO_CORRESPONDENTE_BASE; }
    protected Color corBorda() { return gerard.ui.CoresRepresentacaoGerard.COMPARACAO_CORRESPONDENTE_BORDA; }
    protected Color corTexto() { return gerard.ui.CoresRepresentacaoGerard.COMPARACAO_CORRESPONDENTE_TEXTO; }
}
