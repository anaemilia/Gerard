package gerard.interpretacao.modelo;

public class NumeroEncontrado {
    private final String textoOriginal;
    private final String valorCanonico;
    private final int posicaoInicial;
    private final int posicaoFinal;

    public NumeroEncontrado(String textoOriginal, int posicaoInicial, int posicaoFinal) {
        this(textoOriginal, posicaoInicial, posicaoFinal, textoOriginal);
    }

    public NumeroEncontrado(String textoOriginal, int posicaoInicial, int posicaoFinal, String valorCanonico) {
        this.textoOriginal = textoOriginal;
        this.valorCanonico = valorCanonico == null ? textoOriginal : valorCanonico;
        this.posicaoInicial = posicaoInicial;
        this.posicaoFinal = posicaoFinal;
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

    public String toString() {
        return valorCanonico;
    }
}
