package gerard.interacao.geometria;

/**
 * Intervalos inclusivos produzidos pela representação para restringir um
 * movimento. Não conhece AWT, Swing, componente, tela ou unidade de pixel.
 */
public final class LimitesMovimento {
    private final int minimoX;
    private final int maximoX;
    private final int minimoY;
    private final int maximoY;

    public LimitesMovimento(int minimoX, int maximoX,
            int minimoY, int maximoY) {
        if (maximoX < minimoX || maximoY < minimoY) {
            throw new IllegalArgumentException("intervalo de movimento invalido");
        }
        this.minimoX = minimoX;
        this.maximoX = maximoX;
        this.minimoY = minimoY;
        this.maximoY = maximoY;
    }

    public int limitarX(int x) {
        return limitar(x, minimoX, maximoX);
    }

    public int limitarY(int y) {
        return limitar(y, minimoY, maximoY);
    }

    public int getMinimoX() {
        return minimoX;
    }

    public int getMaximoX() {
        return maximoX;
    }

    public int getMinimoY() {
        return minimoY;
    }

    public int getMaximoY() {
        return maximoY;
    }

    private int limitar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(valor, maximo));
    }
}
