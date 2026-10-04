package gerard.campoaditivo.sincronizacao.texto;

import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

/**
 * Implementa o percurso e a validação comuns. Subclasses definem somente a
 * forma como o valor deve aparecer no enunciado.
 */
public abstract class SincronizadorElementosSemanticosTextoAbstrato
        implements SincronizadorElementosSemanticosTexto {

    private java.util.function.BooleanSupplier atualizacaoAdmitida = () -> true;

    /**
     * Os números do enunciado são os dados curados da situação e só podem mudar depois da
     * conclusão correta (exploração, diagrama azul). Quem possui a tentativa informa a decisão;
     * sem informação, vale o padrão antigo.
     */
    public final void definirAdmissaoAtualizacao(java.util.function.BooleanSupplier admissao) {
        this.atualizacaoAdmitida = admissao == null ? () -> true : admissao;
    }

    @Override
    public final void sincronizar(
            Iterable<? extends ElementoSemanticoTexto> elementos,
            EstadoSemanticoCompartilhado.Snapshot snapshot,
            MapeadorPapelSemanticoTexto mapeador) {
        if (elementos == null || snapshot == null || mapeador == null
                || !atualizacaoAdmitida.getAsBoolean()) {
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
