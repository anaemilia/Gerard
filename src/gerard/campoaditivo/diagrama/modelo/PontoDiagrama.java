package gerard.campoaditivo.diagrama.modelo;

/** Ponto na geometria da cena, sem depender de Swing/AWT. */
public final class PontoDiagrama {
    private final int x;
    private final int y;

    public PontoDiagrama(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public PontoDiagrama deslocado(int dx, int dy) {
        return new PontoDiagrama(x + dx, y + dy);
    }
}
