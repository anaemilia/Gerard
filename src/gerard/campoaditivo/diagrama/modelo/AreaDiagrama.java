package gerard.campoaditivo.diagrama.modelo;

/** Limites geométricos neutros usados para gerar uma cena em qualquer plataforma. */
public final class AreaDiagrama {
    public final int x;
    public final int y;
    public final int largura;
    public final int altura;

    public AreaDiagrama(int x, int y, int largura, int altura) {
        if (largura < 0 || altura < 0) {
            throw new IllegalArgumentException("dimensões da área não podem ser negativas");
        }
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getLargura() { return largura; }
    public int getAltura() { return altura; }
}
