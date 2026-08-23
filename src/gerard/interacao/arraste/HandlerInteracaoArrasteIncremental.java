package gerard.interacao.arraste;

import gerard.interacao.geometria.LimitesMovimento;

/**
 * Mantém o estado portátil de um arraste incremental. Recebe coordenadas já
 * traduzidas pelo adaptador de plataforma e não conhece AWT, Swing, tela,
 * hit-testing nem a classe visual concreta movimentada.
 */
public final class HandlerInteracaoArrasteIncremental<T extends AlvoMovelIncremental> {
    private final ControladorLimiarArrasteEstrutural limiar =
            new ControladorLimiarArrasteEstrutural();

    private T alvoAtivo;
    private int posicaoAnteriorX;
    private int posicaoAnteriorY;

    public boolean iniciar(T alvo, int posicaoX, int posicaoY) {
        if (alvo == null) {
            cancelar();
            return false;
        }
        cancelar();
        alvoAtivo = alvo;
        posicaoAnteriorX = posicaoX;
        posicaoAnteriorY = posicaoY;
        limiar.iniciar(posicaoX, posicaoY);
        return true;
    }

    public boolean mover(int posicaoX, int posicaoY,
            LimitesMovimento limites) {
        if (limites == null) {
            throw new IllegalArgumentException("limites obrigatorios");
        }
        if (alvoAtivo == null || !limiar.deveMovimentar(posicaoX, posicaoY)) {
            return false;
        }
        int deltaX = posicaoX - posicaoAnteriorX;
        int deltaY = posicaoY - posicaoAnteriorY;
        alvoAtivo.moverPor(deltaX, deltaY, limites);
        posicaoAnteriorX = posicaoX;
        posicaoAnteriorY = posicaoY;
        return true;
    }

    public void finalizarLimiar() {
        limiar.finalizar();
    }

    public void cancelar() {
        alvoAtivo = null;
        posicaoAnteriorX = 0;
        posicaoAnteriorY = 0;
        limiar.finalizar();
    }

    public boolean estaAtivo() {
        return alvoAtivo != null;
    }

    public T obterAlvoAtivo() {
        return alvoAtivo;
    }
}
