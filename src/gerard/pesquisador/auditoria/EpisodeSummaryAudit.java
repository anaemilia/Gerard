package gerard.pesquisador.auditoria;

import java.util.Collections;
import java.util.Map;

/**
 * Agregado de um episódio inteiro, calculado pelo AgentAuditService a
 * partir dos AgentAuditEvent reais coletados durante o episódio — nenhum
 * valor aqui é recalculado por um agente, é soma/contagem simples sobre o
 * que já foi registrado.
 */
public final class EpisodeSummaryAudit {
    private final String episodeId;
    private final int numeroAcoes;
    private final int acertos;
    private final int erros;
    private final int maiorSequenciaErros;
    private final int ajudasOferecidas;
    private final Map<String, Integer> regrasAtivadasPorAgente;
    private final int divergencias;
    private final String estadoFinal;
    private final long duracaoMs;

    public EpisodeSummaryAudit(String episodeId, int numeroAcoes, int acertos, int erros,
            int maiorSequenciaErros, int ajudasOferecidas, Map<String, Integer> regrasAtivadasPorAgente,
            int divergencias, String estadoFinal, long duracaoMs) {
        this.episodeId = episodeId;
        this.numeroAcoes = numeroAcoes;
        this.acertos = acertos;
        this.erros = erros;
        this.maiorSequenciaErros = maiorSequenciaErros;
        this.ajudasOferecidas = ajudasOferecidas;
        this.regrasAtivadasPorAgente = regrasAtivadasPorAgente == null
                ? Collections.<String, Integer>emptyMap() : regrasAtivadasPorAgente;
        this.divergencias = divergencias;
        this.estadoFinal = estadoFinal;
        this.duracaoMs = duracaoMs;
    }

    public String getEpisodeId() { return episodeId; }
    public int getNumeroAcoes() { return numeroAcoes; }
    public int getAcertos() { return acertos; }
    public int getErros() { return erros; }
    public int getMaiorSequenciaErros() { return maiorSequenciaErros; }
    public int getAjudasOferecidas() { return ajudasOferecidas; }
    public Map<String, Integer> getRegrasAtivadasPorAgente() { return Collections.unmodifiableMap(regrasAtivadasPorAgente); }
    public int getDivergencias() { return divergencias; }
    public String getEstadoFinal() { return estadoFinal; }
    public long getDuracaoMs() { return duracaoMs; }

    /**
     * Campos pedidos no schema de referência que não existem por não haver
     * motor de estratégia/diagnóstico ainda: estratégias detectadas,
     * diagnóstico inicial/final, casos inseridos (contável, mas não somado
     * aqui nesta primeira versão), padrões incorporados. Documentado no
     * relatório, não fabricado aqui.
     */
    public static String motivoCamposAusentes() {
        return "estrategias_detectadas/diagnostico_inicial/diagnostico_final/padroes_incorporados exigem "
                + "motor de estrategia/diagnostico que o AgenteModelador ainda nao tem (ver ModeladorAuditData).";
    }
}
