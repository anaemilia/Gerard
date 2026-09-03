package gerard.aplicacao;

import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.curadoria.ResultadoConversaoSituacaoProblemaRica;
import gerard.interpretacao.modelo.ResultadoInterpretacao;

/** Resultado imutável produzido pela fachada de carregamento. */
public final class ContextoCarregamentoAtividade {
    private final SituacaoProblemaAditiva situacao;
    private final DefinicaoDiagramaAditivo definicao;
    private final ResultadoInterpretacao interpretacao;
    private final String enunciadoExibido;
    private final ResultadoConversaoSituacaoProblemaRica resultadoSituacaoRica;

    public ContextoCarregamentoAtividade(SituacaoProblemaAditiva situacao,
            DefinicaoDiagramaAditivo definicao,
            ResultadoInterpretacao interpretacao) {
        this(situacao, definicao, interpretacao,
                situacao == null ? "" : situacao.getEnunciado(), null);
    }

    public ContextoCarregamentoAtividade(SituacaoProblemaAditiva situacao,
            DefinicaoDiagramaAditivo definicao,
            ResultadoInterpretacao interpretacao, String enunciadoExibido) {
        this(situacao, definicao, interpretacao, enunciadoExibido, null);
    }

    public ContextoCarregamentoAtividade(SituacaoProblemaAditiva situacao,
            DefinicaoDiagramaAditivo definicao,
            ResultadoInterpretacao interpretacao, String enunciadoExibido,
            ResultadoConversaoSituacaoProblemaRica resultadoSituacaoRica) {
        this.situacao = situacao;
        this.definicao = definicao;
        this.interpretacao = interpretacao;
        this.enunciadoExibido = enunciadoExibido == null ? "" : enunciadoExibido;
        this.resultadoSituacaoRica = resultadoSituacaoRica;
    }

    public SituacaoProblemaAditiva getSituacao() { return situacao; }
    public DefinicaoDiagramaAditivo getDefinicao() { return definicao; }
    public ResultadoInterpretacao getInterpretacao() { return interpretacao; }
    public String getEnunciadoExibido() { return enunciadoExibido; }
    public ResultadoConversaoSituacaoProblemaRica getResultadoSituacaoRica() {
        return resultadoSituacaoRica;
    }
    public boolean possuiSituacaoRicaValida() {
        return resultadoSituacaoRica != null && resultadoSituacaoRica.ehValida();
    }
    public boolean possuiSituacaoExibivel() {
        return situacao != null && situacao.isValidada()
                && situacao.getEnunciado() != null
                && situacao.getEnunciado().trim().length() > 0;
    }
}
