package gerard.interacao;

/**
 * Fato observável de interação produzido pelo objeto representacional que
 * participou do gesto. Deliberadamente não possui C/E, diagnóstico,
 * action_id, papel de destino ou hipótese sobre o participante.
 */
public final class RegistroGestoInteracao {
    private final long instanteMillis;
    private final ContextoRegistroGesto contexto;
    private final String gestoId;
    private final String tipoGesto;
    private final String objetoInterfaceId;
    private final String artefatoInterface;
    private final int inicioX;
    private final int inicioY;
    private final int fimX;
    private final int fimY;
    private final int amostras;
    private final int mudancasOrientacao;
    private final long distanciaPercorrida;
    private final DestinoGeometricoGesto destinoGeometrico;

    public RegistroGestoInteracao(long instanteMillis,
            ContextoRegistroGesto contexto, String gestoId, String tipoGesto,
            String objetoInterfaceId, String artefatoInterface,
            int inicioX, int inicioY, int fimX, int fimY, int amostras,
            int mudancasOrientacao, long distanciaPercorrida,
            DestinoGeometricoGesto destinoGeometrico) {
        if (contexto == null) {
            throw new IllegalArgumentException("O contexto factual do gesto é obrigatório.");
        }
        if (destinoGeometrico == null) {
            throw new IllegalArgumentException("O destino geométrico do gesto é obrigatório.");
        }
        this.instanteMillis = instanteMillis;
        this.contexto = contexto;
        this.gestoId = normalizar(gestoId);
        this.tipoGesto = normalizar(tipoGesto);
        this.objetoInterfaceId = normalizar(objetoInterfaceId);
        this.artefatoInterface = normalizar(artefatoInterface);
        this.inicioX = inicioX;
        this.inicioY = inicioY;
        this.fimX = fimX;
        this.fimY = fimY;
        this.amostras = amostras;
        this.mudancasOrientacao = mudancasOrientacao;
        this.distanciaPercorrida = distanciaPercorrida;
        this.destinoGeometrico = destinoGeometrico;
    }

    public long getInstanteMillis() { return instanteMillis; }
    public ContextoRegistroGesto getContexto() { return contexto; }
    public String getGestoId() { return gestoId; }
    public String getTipoGesto() { return tipoGesto; }
    public String getObjetoInterfaceId() { return objetoInterfaceId; }
    public String getArtefatoInterface() { return artefatoInterface; }
    public int getInicioX() { return inicioX; }
    public int getInicioY() { return inicioY; }
    public int getFimX() { return fimX; }
    public int getFimY() { return fimY; }
    public int getAmostras() { return amostras; }
    public int getMudancasOrientacao() { return mudancasOrientacao; }
    public long getDistanciaPercorrida() { return distanciaPercorrida; }
    public DestinoGeometricoGesto getDestinoGeometrico() { return destinoGeometrico; }

    private static String normalizar(String valor) {
        return valor == null ? "" : valor;
    }
}
