package gerard.interacao.eixo;

/** Estado portátil do eixo de um papel, sem Swing, AWT, SVG ou geometria. */
public final class ControleVisibilidadeEixoPapel {
    public enum Estado { FECHADO, REVELADO }

    private Estado estado = Estado.FECHADO;

    public Estado getEstado() { return estado; }
    public boolean estaRevelado() { return estado == Estado.REVELADO; }
    public boolean podeRevelar() { return estado == Estado.FECHADO; }

    public boolean revelar() {
        if (!podeRevelar()) return false;
        estado = Estado.REVELADO;
        return true;
    }

    public boolean ocultar() {
        if (!estaRevelado()) return false;
        estado = Estado.FECHADO;
        return true;
    }
}
