package gerard.pesquisador.auditoria;

/**
 * Comparação entre o que se esperava do evento e o que os agentes reais
 * produziram — "esperado" aqui é o próprio veredito/decisão que os agentes
 * de produção retornaram (não existe um segundo oráculo separado rodando
 * em paralelo); a divergência que este objeto pode capturar de verdade é
 * entre o veredito do AgenteMonitor e o rótulo original do protocolo
 * humano, quando esse dado existir (replay de casos reais).
 */
public final class InteractionComparisonAudit {
    private final String avaliacaoEsperada;
    private final String avaliacaoObservada;
    private final String diagnosticoEsperado;
    private final String diagnosticoObservado;
    private final String intervencaoEsperada;
    private final String intervencaoObservada;
    private final String mensagemExibida;
    private final boolean divergence;
    private final String divergenceType;
    private final String divergenceReason;
    private final String agenteResponsavel;

    public InteractionComparisonAudit(String avaliacaoEsperada, String avaliacaoObservada,
            String diagnosticoEsperado, String diagnosticoObservado, String intervencaoEsperada,
            String intervencaoObservada, String mensagemExibida, boolean divergence, String divergenceType,
            String divergenceReason, String agenteResponsavel) {
        this.avaliacaoEsperada = avaliacaoEsperada;
        this.avaliacaoObservada = avaliacaoObservada;
        this.diagnosticoEsperado = diagnosticoEsperado;
        this.diagnosticoObservado = diagnosticoObservado;
        this.intervencaoEsperada = intervencaoEsperada;
        this.intervencaoObservada = intervencaoObservada;
        this.mensagemExibida = mensagemExibida;
        this.divergence = divergence;
        this.divergenceType = divergenceType;
        this.divergenceReason = divergenceReason;
        this.agenteResponsavel = agenteResponsavel;
    }

    public String getAvaliacaoEsperada() { return avaliacaoEsperada; }
    public String getAvaliacaoObservada() { return avaliacaoObservada; }
    public String getDiagnosticoEsperado() { return diagnosticoEsperado; }
    public String getDiagnosticoObservado() { return diagnosticoObservado; }
    public String getIntervencaoEsperada() { return intervencaoEsperada; }
    public String getIntervencaoObservada() { return intervencaoObservada; }
    public String getMensagemExibida() { return mensagemExibida; }
    public boolean isDivergence() { return divergence; }
    public String getDivergenceType() { return divergenceType; }
    public String getDivergenceReason() { return divergenceReason; }
    public String getAgenteResponsavel() { return agenteResponsavel; }
}
