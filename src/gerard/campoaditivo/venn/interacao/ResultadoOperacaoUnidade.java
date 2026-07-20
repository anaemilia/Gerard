package gerard.campoaditivo.venn.interacao;

/**
 * Resultado imutável de uma operação sobre unidades visuais.
 */
public final class ResultadoOperacaoUnidade {
    private final boolean realizada;
    private final boolean limiteAtingido;
    private final int quantidadeAnterior;
    private final int quantidadeAtual;

    private ResultadoOperacaoUnidade(boolean realizada, boolean limiteAtingido,
            int quantidadeAnterior, int quantidadeAtual) {
        this.realizada = realizada;
        this.limiteAtingido = limiteAtingido;
        this.quantidadeAnterior = quantidadeAnterior;
        this.quantidadeAtual = quantidadeAtual;
    }

    public static ResultadoOperacaoUnidade realizada(int anterior, int atual,
            boolean limiteAtingido) {
        return new ResultadoOperacaoUnidade(true, limiteAtingido, anterior, atual);
    }

    public static ResultadoOperacaoUnidade limiteAtingido(int quantidade) {
        return new ResultadoOperacaoUnidade(false, true, quantidade, quantidade);
    }

    public static ResultadoOperacaoUnidade naoPermitida(int quantidade) {
        return new ResultadoOperacaoUnidade(false, false, quantidade, quantidade);
    }

    public boolean foiRealizada() { return realizada; }
    public boolean isLimiteAtingido() { return limiteAtingido; }
    public int getQuantidadeAnterior() { return quantidadeAnterior; }
    public int getQuantidadeAtual() { return quantidadeAtual; }
}
