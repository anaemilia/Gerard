package gerard.campoaditivo.sincronizacao.representacoes;

import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

/**
 * Coordena a propagação de um snapshot sem conhecer Swing ou as regras de
 * cada representação. Preserva a ordem histórica e impede propagação
 * recursiva durante a reconstrução da representação complementar.
 */
public final class CoordenadorSincronizacaoRepresentacoes {

    private boolean sincronizando;

    public boolean estaSincronizando() {
        return sincronizando;
    }

    public void sincronizar(
            EstadoSemanticoCompartilhado.Snapshot snapshot,
            boolean reconstruirComplementar,
            DestinoSincronizacaoRepresentacoes destino) {
        if (snapshot == null || destino == null || sincronizando) {
            return;
        }
        sincronizando = true;
        try {
            destino.aplicarNoVergnaud(snapshot);
            destino.aplicarNoTexto(snapshot);
            if (reconstruirComplementar) {
                destino.reconstruirRepresentacaoComplementar();
            }
            destino.aplicarNosEixos(snapshot);
        } finally {
            sincronizando = false;
        }
    }
}
