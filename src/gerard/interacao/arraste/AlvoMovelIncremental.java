package gerard.interacao.arraste;

import gerard.interacao.geometria.LimitesMovimento;

/**
 * Porta portátil para um alvo reposicionado por deltas sucessivos de um
 * gesto. A implementação concreta pertence à representação da plataforma.
 */
public interface AlvoMovelIncremental {
    void moverPor(int deltaX, int deltaY, LimitesMovimento limites);
}
