package gerard.campoaditivo.venn.modelo;

public class NoDiagramaVenn {
    private final int x;
    private final int y;
    private final int largura;
    private final int altura;
    private final String rotulo;
    private final int valorReferencia;
    private final boolean exibirQuadradinhos;

    public NoDiagramaVenn(int x, int y, int largura, int altura, String rotulo, int valorReferencia, boolean exibirQuadradinhos) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.rotulo = rotulo;
        this.valorReferencia = valorReferencia;
        this.exibirQuadradinhos = exibirQuadradinhos;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getLargura() { return largura; }
    public int getAltura() { return altura; }
    public String getRotulo() { return rotulo; }
    public int getValorReferencia() { return valorReferencia; }
    public boolean isExibirQuadradinhos() { return exibirQuadradinhos; }
}
