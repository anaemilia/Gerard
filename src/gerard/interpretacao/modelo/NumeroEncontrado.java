package gerard.interpretacao.modelo;

public class NumeroEncontrado {
    private final String textoOriginal;
    private final String valorCanonico;
    private final int posicaoInicial;
    private final int posicaoFinal;
    private final String chavePapelSemantico;

    public NumeroEncontrado(String textoOriginal, int posicaoInicial, int posicaoFinal) {
        this(textoOriginal, posicaoInicial, posicaoFinal, textoOriginal);
    }

    public NumeroEncontrado(String textoOriginal, int posicaoInicial, int posicaoFinal, String valorCanonico) {
        this(textoOriginal, posicaoInicial, posicaoFinal, valorCanonico, null);
    }

    public NumeroEncontrado(String textoOriginal, int posicaoInicial,
            int posicaoFinal, String valorCanonico,
            String chavePapelSemantico) {
        this.textoOriginal = textoOriginal;
        this.valorCanonico = valorCanonico == null ? textoOriginal : valorCanonico;
        this.posicaoInicial = posicaoInicial;
        this.posicaoFinal = posicaoFinal;
        this.chavePapelSemantico = chavePapelSemantico;
    }

    public String getTextoOriginal() {
        return textoOriginal;
    }

    public String getValorCanonico() {
        return valorCanonico;
    }

    public int getPosicaoInicial() {
        return posicaoInicial;
    }

    public int getPosicaoFinal() {
        return posicaoFinal;
    }

    public String getChavePapelSemantico() {
        return chavePapelSemantico;
    }

    public String toString() {
        return valorCanonico;
    }
}
