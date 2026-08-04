package gerard.agente.zdp;

/**
 * Resultado de {@link AgenteZDP#consultarConhecimento} — diagnóstico
 * adicional vindo das regras pedagógicas da base de conhecimento integrada
 * (ver dados/base_conhecimento_gerard_jsons/base_conhecimento_gerard/
 * regras_pedagogicas.jsonl). Não substitui {@link CamadaEstrategiaZDP}: é
 * informação complementar que quem chamar pode logar/expor, sem mudar o
 * comportamento das 4 camadas N0-N2 já em produção.
 */
public final class ResultadoConsultaConhecimento {
    private final String ruleId;
    private final String intervencaoSugerida;
    private final String riscoNovoErro;
    private final String explicacao;

    public ResultadoConsultaConhecimento(String ruleId, String intervencaoSugerida, String riscoNovoErro,
            String explicacao) {
        this.ruleId = ruleId;
        this.intervencaoSugerida = intervencaoSugerida;
        this.riscoNovoErro = riscoNovoErro;
        this.explicacao = explicacao;
    }

    public static ResultadoConsultaConhecimento semIntervencao() {
        return new ResultadoConsultaConhecimento(null, null, null, null);
    }

    public boolean temIntervencao() {
        return intervencaoSugerida != null;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getIntervencaoSugerida() {
        return intervencaoSugerida;
    }

    public String getRiscoNovoErro() {
        return riscoNovoErro;
    }

    public String getExplicacao() {
        return explicacao;
    }
}
