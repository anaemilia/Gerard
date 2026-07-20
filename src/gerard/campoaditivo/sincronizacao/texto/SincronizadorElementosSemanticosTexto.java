package gerard.campoaditivo.sincronizacao.texto;

import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

/**
 * Contrato para propagar um snapshot semântico aos elementos do enunciado.
 */
public interface SincronizadorElementosSemanticosTexto {
    void sincronizar(
            Iterable<? extends ElementoSemanticoTexto> elementos,
            EstadoSemanticoCompartilhado.Snapshot snapshot,
            MapeadorPapelSemanticoTexto mapeador);
}
