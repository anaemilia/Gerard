package gerard.campoaditivo.diagrama.modelo;

/**
 * Onde a cena posiciona o seletor de operação: centro (x, y) e a largura em que a dica
 * de erro cabe sem cobrir figuras vizinhas. Geometria da cena, sem Swing/AWT.
 */
public final class AncoraSeletorOperacao {
    private final int x;
    private final int y;
    private final int larguraDica;
    private final int xCentroDica;

    public AncoraSeletorOperacao(int x, int y, int larguraDica) {
        this(x, y, larguraDica, x);
    }

    public AncoraSeletorOperacao(int x, int y, int larguraDica, int xCentroDica) {
        this.x = x;
        this.y = y;
        this.larguraDica = larguraDica;
        this.xCentroDica = xCentroDica;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getLarguraDica() { return larguraDica; }
    /** Centro horizontal da dica de erro; pode diferir do seletor para a dica não cobrir figuras. */
    public int getXCentroDica() { return xCentroDica; }

    public AncoraSeletorOperacao deslocada(int dx, int dy) {
        return new AncoraSeletorOperacao(x + dx, y + dy, larguraDica, xCentroDica + dx);
    }
}
