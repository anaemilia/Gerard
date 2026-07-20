package gerard.agente.monitor;

/**
 * Observador do veredito produzido pelo AgenteMonitor a cada avaliação
 * aplicável. Não carrega lógica de negócio — só recebe a notificação.
 */
public interface OuvinteVeredictoAgenteMonitor {
    void aoAvaliar(boolean correto);
}
