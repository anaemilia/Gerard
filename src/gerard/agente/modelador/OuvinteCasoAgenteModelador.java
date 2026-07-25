package gerard.agente.modelador;

import gerard.agente.modelousuario.DiagnosticoTarefa;

/**
 * Observador de um caso armazenado pelo AgenteModelador (ação 1) a cada
 * chamada de armazenarCaso. Não carrega lógica de negócio — só recebe a
 * notificação (mesmo padrão de OuvinteVeredictoAgenteMonitor).
 */
public interface OuvinteCasoAgenteModelador {
    void aoArmazenar(String idUsuario, DiagnosticoTarefa diagnostico);
}
