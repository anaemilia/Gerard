package gerard.campoaditivo.semantica;

/**
 * Natureza matemática do valor associado a um papel semântico do campo
 * aditivo. Quantidades são cardinais e não admitem valor negativo;
 * transformações e relações podem receber sinal.
 */
public enum NaturezaPapelAditivo {
    QUANTIDADE,
    TRANSFORMACAO_OU_RELACAO,
    INDEFINIDO
}
