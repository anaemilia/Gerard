package gerard.campoaditivo.sincronizacao.texto;

import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

/**
 * Implementa o percurso e a validação comuns. Subclasses definem somente a
 * forma como o valor deve aparecer no enunciado.
 */
public abstract class SincronizadorElementosSemanticosTextoAbstrato
        implements SincronizadorElementosSemanticosTexto {

    @Override
    public final void sincronizar(
            Iterable<? extends ElementoSemanticoTexto> elementos,
            EstadoSemanticoCompartilhado.Snapshot snapshot,
            MapeadorPapelSemanticoTexto mapeador) {
        if (elementos == null || snapshot == null || mapeador == null) {
            return;
        }
        for (ElementoSemanticoTexto elemento : elementos) {
            if (elemento == null || !elemento.possuiVinculoSemantico()
                    || elemento.representaIncognitaOriginal()) {
                continue;
            }
            int indice = mapeador.paraIndiceSemantico(
                    elemento.getChavePapelSemantico());
            if (indice < 0 || !snapshot.isConhecido(indice)) {
                continue;
            }
            elemento.atualizarValorSemantico(formatarValor(
                    snapshot.valorOuZero(indice), elemento));
        }
    }

    protected abstract String formatarValor(
            int valor, ElementoSemanticoTexto elemento);
}
