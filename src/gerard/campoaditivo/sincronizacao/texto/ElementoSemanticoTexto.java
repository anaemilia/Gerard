package gerard.campoaditivo.sincronizacao.texto;

/**
 * Contrato comum para elementos visuais do enunciado vinculados a um papel
 * semântico do problema aditivo.
 */
public interface ElementoSemanticoTexto {
    String getChavePapelSemantico();
    String getValorSemanticoOriginal();
    boolean possuiVinculoSemantico();

    /**
     * Indica que o elemento representa a incógnita original da situação.
     * A interrogação é um marcador teórico e não deve ser substituída por
     * resultados calculados durante a sincronização visual.
     */
    boolean representaIncognitaOriginal();

    void atualizarValorSemantico(String novoValor);
}
