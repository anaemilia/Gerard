package gerard.interacao.arraste;

/** Desfecho portátil do pressionamento sobre a barra de Comparação (Fase 7.12). */
public enum ResultadoPressionamentoControleComparacao {
    /** Fora do controle e da escala: o clique segue para os demais protocolos. */
    NAO_CONSUMIDO,
    /** Sobre o controle, com a manipulação ainda não liberada pela modelagem. */
    BLOQUEADO,
    /** Sobre o controle, gesto iniciado. */
    INICIADO
}
