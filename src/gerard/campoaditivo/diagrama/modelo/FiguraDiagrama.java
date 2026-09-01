package gerard.campoaditivo.diagrama.modelo;

public class FiguraDiagrama {
    private final TipoFiguraDiagrama tipo;
    private final int x;
    private final int y;
    private final int largura;
    private final int altura;
    private final String rotulo;
    private final int valorReferencia;
    private final boolean exibirQuantidadeInterna;
    private final PosicaoRotuloFigura posicaoRotulo;
    private final boolean exibirLupa;
    private final String chavePapelSemantico;

    public FiguraDiagrama(TipoFiguraDiagrama tipo, int x, int y, int largura, int altura,
                          String rotulo, int valorReferencia, boolean exibirQuantidadeInterna) {
        this(tipo, x, y, largura, altura, rotulo, valorReferencia,
                exibirQuantidadeInterna, PosicaoRotuloFigura.CENTRO, false, "");
    }

    public FiguraDiagrama(TipoFiguraDiagrama tipo, int x, int y, int largura, int altura,
                          String rotulo, int valorReferencia, boolean exibirQuantidadeInterna,
                          PosicaoRotuloFigura posicaoRotulo, boolean exibirLupa) {
        this(tipo, x, y, largura, altura, rotulo, valorReferencia,
                exibirQuantidadeInterna, posicaoRotulo, exibirLupa, "");
    }

    public FiguraDiagrama(TipoFiguraDiagrama tipo, int x, int y, int largura, int altura,
                          String rotulo, int valorReferencia, boolean exibirQuantidadeInterna,
                          PosicaoRotuloFigura posicaoRotulo, boolean exibirLupa,
                          String chavePapelSemantico) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.rotulo = rotulo;
        this.valorReferencia = valorReferencia;
        this.exibirQuantidadeInterna = exibirQuantidadeInterna;
        this.posicaoRotulo = posicaoRotulo == null ? PosicaoRotuloFigura.CENTRO : posicaoRotulo;
        this.exibirLupa = exibirLupa;
        this.chavePapelSemantico = chavePapelSemantico == null
                ? "" : chavePapelSemantico.trim();
    }

    public TipoFiguraDiagrama getTipo() { return tipo; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getLargura() { return largura; }
    public int getAltura() { return altura; }
    public String getRotulo() { return rotulo; }
    public int getValorReferencia() { return valorReferencia; }
    public boolean isExibirQuantidadeInterna() { return exibirQuantidadeInterna; }
    public PosicaoRotuloFigura getPosicaoRotulo() { return posicaoRotulo; }
    public boolean isExibirLupa() { return exibirLupa; }
    public String getChavePapelSemantico() { return chavePapelSemantico; }
}
