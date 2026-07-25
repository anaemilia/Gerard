package gerard.agente.zdp;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Observador da estratégia pedagógica decidida pelo AgenteZDP a cada
 * chamada de decidirEstrategia. Não carrega lógica de negócio — só recebe
 * a notificação (mesmo padrão de OuvinteVeredictoAgenteMonitor).
 */
public interface OuvinteEstrategiaAgenteZDP {
    void aoDecidir(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo,
                    boolean correto, CamadaEstrategiaZDP estrategia);
}
