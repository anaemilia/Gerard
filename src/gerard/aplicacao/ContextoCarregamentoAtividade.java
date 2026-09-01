package gerard.aplicacao;

import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.interpretacao.modelo.ResultadoInterpretacao;

/** Resultado imutável produzido pela fachada de carregamento. */
public final class ContextoCarregamentoAtividade {
    private final SituacaoProblemaAditiva situacao;
    private final DefinicaoDiagramaAditivo definicao;
    private final ResultadoInterpretacao interpretacao;
    private final String enunciadoExibido;

    public ContextoCarregamentoAtividade(SituacaoProblemaAditiva situacao,
            DefinicaoDiagramaAditivo definicao,
            ResultadoInterpretacao interpretacao) {
        this.situacao = situacao;
        this.definicao = definicao;
        this.interpretacao = interpretacao;
        this.enunciadoExibido = situacao == null ? "" : situacao.getEnunciado();
    }

    public ContextoCarregamentoAtividade(SituacaoProblemaAditiva situacao,
            DefinicaoDiagramaAditivo definicao,
            ResultadoInterpretacao interpretacao, String enunciadoExibido) {
        this.situacao = situacao;
        this.definicao = definicao;
        this.interpretacao = interpretacao;
        this.enunciadoExibido = enunciadoExibido == null ? "" : enunciadoExibido;
    }

    public SituacaoProblemaAditiva getSituacao() { return situacao; }
    public DefinicaoDiagramaAditivo getDefinicao() { return definicao; }
    public ResultadoInterpretacao getInterpretacao() { return interpretacao; }
    public String getEnunciadoExibido() { return enunciadoExibido; }
    public boolean possuiSituacaoExibivel() {
        return situacao != null && situacao.isValidada()
                && situacao.getEnunciado() != null
                && situacao.getEnunciado().trim().length() > 0;
    }
}
