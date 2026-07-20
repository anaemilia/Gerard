package gerard.interacao.arraste;

/**
 * Diferencia clique/duplo clique de arraste das figuras estruturais do
 * diagrama. Pequenas oscilações do mouse não podem reposicionar círculos,
 * retângulos ou conectores durante uma ação de edição por duplo clique.
 */
public final class ControladorLimiarArrasteEstrutural {
    private final int limiarPixels;
    private int origemX;
    private int origemY;
    private boolean aguardando;
    private boolean ativado;

    public ControladorLimiarArrasteEstrutural() {
        this(6);
    }

    public ControladorLimiarArrasteEstrutural(int limiarPixels) {
        this.limiarPixels = Math.max(1, limiarPixels);
    }

    public void iniciar(int x, int y) {
        origemX = x;
        origemY = y;
        aguardando = true;
        ativado = false;
    }

    public boolean deveMovimentar(int x, int y) {
        if (!aguardando) {
            return true;
        }
        if (!ativado) {
            long dx = x - origemX;
            long dy = y - origemY;
            ativado = dx * dx + dy * dy >= (long) limiarPixels * limiarPixels;
        }
        return ativado;
    }

    public boolean foiArraste() {
        return ativado;
    }

    public void finalizar() {
        aguardando = false;
        ativado = false;
    }

    public void cancelar() {
        finalizar();
    }
}
