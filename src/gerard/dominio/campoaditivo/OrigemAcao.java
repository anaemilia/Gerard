package gerard.dominio.campoaditivo;

/**
 * Origem de uma ação sobre um papel quantitativo. Existe para que um valor
 * calculado automaticamente pelo sistema nunca seja registrado como se
 * fosse uma ação do estudante — distinção obrigatória para a integridade
 * dos dados de pesquisa (log de ação instrumental).
 */
public enum OrigemAcao {
    /** O estudante posicionou o valor diretamente. */
    ORIGEM_USUARIO,
    /** O valor foi calculado/aplicado automaticamente pelo sistema (ex.: a partir de uma relação estrutural). */
    ORIGEM_SISTEMA,
    /** O valor foi proposto por um mecanismo de inferência (ex.: agente pedagógico), não uma regra determinística. */
    ORIGEM_INFERENCIA,
    /** A ação foi realizada por um pesquisador (ex.: curadoria, correção manual de dado). */
    ORIGEM_PESQUISADOR
}
