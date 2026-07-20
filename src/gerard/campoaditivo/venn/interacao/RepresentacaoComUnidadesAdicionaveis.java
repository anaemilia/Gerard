package gerard.campoaditivo.venn.interacao;

/**
 * Contrato para representações cuja quantidade pode ser incrementada.
 */
public interface RepresentacaoComUnidadesAdicionaveis extends RepresentacaoComUnidades {
    Integer obterLimiteSemantico();
    boolean podeAdicionarUnidade();
    ResultadoOperacaoUnidade adicionarUnidade();
}
