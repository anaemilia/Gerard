package gerard.semantica.quantidade;

/**
 * Contexto mínimo necessário para interpretar e apresentar uma quantidade.
 * Evita que a semântica quantitativa dependa de um modelo concreto de situação.
 */
public interface ContextoQuantidade {
    String getCodigoIdioma();
    String getContexto();
    String getEnunciado();
    String getRepresentacaoVisual();
    String getObservacoes();
}
