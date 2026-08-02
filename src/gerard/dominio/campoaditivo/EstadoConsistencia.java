package gerard.dominio.campoaditivo;

/**
 * Resultado de avaliar uma relação estrutural formal contra os papéis
 * quantitativos de uma representação — nunca contra um invariante
 * operatório (ver distinção C=(S,I,R) no relatório técnico do baseline v2:
 * S=situações, I=invariantes operatórios, R=representações; esta classe
 * pertence exclusivamente ao lado R).
 *
 * Deliberadamente não é um boolean: um estado incompleto de uma atividade
 * pedagógica interativa em andamento não é a mesma coisa que uma
 * representação inconsistente, e nenhum dos dois é um erro de programação.
 */
public enum EstadoConsistencia {
    /** Os papéis preenchidos satisfazem a relação estrutural formal. */
    CONSISTENTE,
    /** Nem todos os papéis necessários estão preenchidos ainda — estado normal de atividade em andamento. */
    REPRESENTACAO_INCOMPLETA,
    /** Todos os papéis necessários estão preenchidos, mas não satisfazem a relação estrutural formal. */
    REPRESENTACAO_INCONSISTENTE,
    /** Não há exatamente um papel incógnito entre os avaliados — não é possível calcular um valor ausente único neste estado. */
    NAO_RESOLVIVEL_NESTE_ESTADO
}
