package gerard.campoaditivo.venn.interacao;

/**
 * Contrato para representações cuja quantidade pode ser reduzida.
 */
public interface RepresentacaoComUnidadesRemoviveis extends RepresentacaoComUnidades {
    boolean podeRemoverUnidade();
    ResultadoOperacaoUnidade removerUnidade();
}
