package gerard.dominio.campoaditivo.evento;

import java.util.Map;

/**
 * Evento semântico produzido por um objeto de domínio (skill Semantic Event
 * Logging): representa o SIGNIFICADO de uma mudança, nunca um evento técnico
 * de interface (clique, posição de mouse). A infraestrutura só persiste o
 * que este objeto já decidiu que aconteceu — nunca decide por conta própria.
 */
public interface EventoDominio {
    String getTipo();
    long getTimestampEpocaMillis();
    Map<String, Object> paraMapa();
}
