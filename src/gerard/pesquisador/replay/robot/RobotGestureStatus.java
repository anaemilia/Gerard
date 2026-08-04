package gerard.pesquisador.replay.robot;

/**
 * Estados possíveis de UMA tentativa de gesto conduzido por Robot (ver
 * pacote de correção rodada 3, 2026-07-31: "nenhum gesto pode desaparecer
 * silenciosamente"). Cada tentativa termina em exatamente um destes.
 */
public enum RobotGestureStatus {
    STARTED,
    PICKUP_FAILED,
    DRAGGING,
    DROP_FAILED,
    EVALUATION_NOT_DISPATCHED,
    COMPLETED,
    CANCELLED,
    EXCEPTION
}
