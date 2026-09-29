package gerard.dominio.campoaditivo.situacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.dominio.campoaditivo.OperacaoAditiva;
import gerard.dominio.campoaditivo.OrigemAcao;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** Resultado factual produzido pelo criterio semantico de operacao. */
public final class ResultadoEscolhaOperacaoModelagem
        implements RegistroFactualAcaoInstrumental {

    public enum Feedback {
        NENHUM,
        SOM_ERRO
    }

    private final String actionId = UUID.randomUUID().toString();
    private final CriterioOperacaoModelagem criterio;
    private final TipoSituacaoAditiva categoria;
    private final OperacaoAditiva tentativa;
    private final boolean correta;
    private final ContextoAcaoInstrumental contexto;
    private final List<String> participantes;

    ResultadoEscolhaOperacaoModelagem(
            CriterioOperacaoModelagem criterio,
            TipoSituacaoAditiva categoria,
            OperacaoAditiva tentativa,
            ContextoAcaoInstrumental contexto) {
        if (criterio == null || categoria == null || tentativa == null
                || contexto == null) {
            throw new IllegalArgumentException(
                    "criterio, categoria, tentativa e contexto sao obrigatorios");
        }
        this.criterio = criterio;
        this.categoria = categoria;
        this.tentativa = tentativa;
        this.correta = criterio.correspondeA(tentativa);
        this.contexto = new ContextoAcaoInstrumental(
                contexto.getTarefa(),
                contexto.getInstrumentoOrganizacao(),
                contexto.getInstrumentoArtefato(),
                contexto.getFuncaoArtefato(),
                contexto.getObjeto(),
                contexto.getOrigemEvento(),
                correta ? "CORRETO" : "INCORRETO",
                contexto.getMudancaObservavel(),
                contexto.getParticipantesSemanticos());
        this.participantes = Collections.unmodifiableList(
                this.contexto.getParticipantesSemanticos());
    }

    public boolean foiCorreta() { return correta; }
    public Feedback getFeedback() {
        return correta ? Feedback.NENHUM : Feedback.SOM_ERRO;
    }
    public OperacaoAditiva getEscolha() { return tentativa; }
    public String getActionId() { return actionId; }
    public OrigemAcao getOrigemAcao() { return OrigemAcao.ORIGEM_USUARIO; }
    public TarefaInteracao getTarefaInteracao() { return TarefaInteracao.SELECIONAR; }
    public TipoSituacaoAditiva getCategoria() { return categoria; }
    public String getProprietarioSemantico() { return criterio.getChave(); }
    public String getAlvoSemantico() { return criterio.getChave() + ".escolha"; }
    public ResultadoAvaliacaoAcaoInstrumental getResultado() {
        return correta ? ResultadoAvaliacaoAcaoInstrumental.CORRETA
                : ResultadoAvaliacaoAcaoInstrumental.ERRADA;
    }
    public String getTipoDiagnosticoFactual() {
        return correta ? "" : "OPERACAO_DIVERGENTE_DA_CURADORIA";
    }
    public String getValorPropostoFactual() { return tentativa.name(); }
    public String getValorEsperadoFactual() {
        return criterio.getOperacaoEsperada().name();
    }
    public String getRegraSemantica() {
        if ("relacao.operacao.estadoTransformacao".equals(criterio.getChave())) {
            return correta
                    ? "O aluno escolheu a operação (soma/subtração) que combina estado inicial e transformação resultante."
                    : "O aluno escolheu uma operação diferente da curada — explicação exibida perto do seletor.";
        }
        return correta
                ? "O aluno escolheu a operação (soma/subtração) que combina os papéis curados."
                : "O aluno escolheu uma operação diferente da curada — explicação exibida perto do seletor.";
    }
    public ContextoAcaoInstrumental getContexto() { return contexto; }
    public List<String> getParticipantesSemanticos() { return participantes; }
    public String getRejectionSequenceId() { return null; }
}
