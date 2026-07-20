package gerard.campoaditivo.venn.apresentacao;

import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;

/**
 * Fábrica que seleciona implementações polimórficas sem expor detalhes de
 * paleta à tela principal.
 */
public final class FabricaRenderizadoresUnidadeVenn {
    private static final RenderizadorUnidadeVenn COMPOSICAO = new RenderizadorUnidadeComposicao();
    private static final RenderizadorUnidadeVenn COMPARACAO_CORRESPONDENTE = new RenderizadorUnidadeComparacaoCorrespondente();
    private static final RenderizadorUnidadeVenn COMPARACAO_EXCEDENTE = new RenderizadorUnidadeComparacaoExcedente();
    private static final RenderizadorUnidadeVenn TRANSFORMACAO_POSITIVA = new RenderizadorUnidadeTransformacaoPositiva();
    private static final RenderizadorUnidadeVenn TRANSFORMACAO_NEGATIVA = new RenderizadorUnidadeTransformacaoNegativa();

    private FabricaRenderizadoresUnidadeVenn() {
    }

    public static RenderizadorUnidadeVenn paraComposicao() {
        return COMPOSICAO;
    }

    public static RenderizadorUnidadeVenn paraComparacao(boolean correspondente) {
        return correspondente ? COMPARACAO_CORRESPONDENTE : COMPARACAO_EXCEDENTE;
    }

    public static RenderizadorUnidadeVenn paraOrigem(QuadradinhoVenn unidade) {
        if (unidade != null && "transformacao_negativa".equals(unidade.origem)) {
            return TRANSFORMACAO_NEGATIVA;
        }
        return TRANSFORMACAO_POSITIVA;
    }
}
