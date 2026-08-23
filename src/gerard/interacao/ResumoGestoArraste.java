package gerard.interacao;

/**
 * Observações físicas produzidas durante um único arraste. Este valor não
 * conhece objeto semântico, regra matemática, acerto, erro ou scaffolding.
 */
public final class ResumoGestoArraste {
    private final String gestoId;
    private final int inicioX;
    private final int inicioY;
    private final int fimX;
    private final int fimY;
    private final int amostras;
    private final int mudancasOrientacao;
    private final long distanciaPercorrida;
    private final long instanteConclusaoMillis;

    public ResumoGestoArraste(String gestoId, int inicioX, int inicioY,
            int fimX, int fimY, int amostras, int mudancasOrientacao,
            long distanciaPercorrida, long instanteConclusaoMillis) {
        this.gestoId = gestoId == null ? "" : gestoId;
        this.inicioX = inicioX;
        this.inicioY = inicioY;
        this.fimX = fimX;
        this.fimY = fimY;
        this.amostras = amostras;
        this.mudancasOrientacao = mudancasOrientacao;
        this.distanciaPercorrida = distanciaPercorrida;
        this.instanteConclusaoMillis = instanteConclusaoMillis;
    }

    public String getGestoId() { return gestoId; }
    public int getInicioX() { return inicioX; }
    public int getInicioY() { return inicioY; }
    public int getFimX() { return fimX; }
    public int getFimY() { return fimY; }
    public int getAmostras() { return amostras; }
    public int getMudancasOrientacao() { return mudancasOrientacao; }
    public long getDistanciaPercorrida() { return distanciaPercorrida; }
    public long getInstanteConclusaoMillis() { return instanteConclusaoMillis; }
}
