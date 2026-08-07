package gerard.dominio.campoaditivo.evento;

import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.OrigemAcao;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Evento semântico do ciclo de vida de um PapelQuantitativo — representa o
 * SIGNIFICADO de uma tentativa de posicionamento, nunca um evento técnico
 * de interface. Carrega o suficiente para religar o evento à tentativa de
 * resolução específica em que ocorreu (id_acao, contexto) e para nunca
 * confundir uma inferência do sistema com uma ação do estudante
 * (origem_da_acao) — ver relatório técnico, seções "Origem das ações" e
 * "Contexto dos eventos semânticos".
 */
public final class EventoPapelQuantitativo implements EventoDominio {

    private final TipoEventoPapel tipo;
    private final String idAcao;
    private final String actionId; // correlaciona tentativas da mesma ação (REFERENCE.md §4.8); distinto de idAcao (que é, na prática, o event_id de cada evento individual — ver nota abaixo)
    private final OrigemAcao origemAcao;
    private final ContextoAcao contexto;
    private final String papelSemantico;
    private final String estadoAnterior;
    private final String estadoPosterior;
    private final String valorProposto;
    private final ResultadoAcao resultado;
    private final DiagnosticoErroPapel diagnostico; // null quando resultado == ACEITO
    private final long timestampEpocaMillis;

    /**
     * @param actionId correlaciona esta e outras tentativas da mesma ação
     *        (REFERENCE.md §4.8, cardinalidade ação:evento, Alternativa B —
     *        ver PapelQuantitativo.registrarTentativa). Nulo quando o
     *        evento não participa desse fluxo (ex.: os dois eventos
     *        publicados por posicionar(...), que são um conceito
     *        ortogonal — validade de domínio, não a sequência de
     *        tentativas rejeitadas de uma resposta). Não confundir com
     *        idAcao: hoje idAcao já identifica CADA evento individual (é,
     *        na prática, o event_id da Seção 4.8 — a renomeação está
     *        registrada como recomendação ainda não aplicada); actionId é
     *        o campo novo, que correlaciona vários eventos entre si.
     */
    public EventoPapelQuantitativo(TipoEventoPapel tipo, OrigemAcao origemAcao, ContextoAcao contexto,
                                    String papelSemantico, String estadoAnterior, String estadoPosterior,
                                    String valorProposto, ResultadoAcao resultado, DiagnosticoErroPapel diagnostico,
                                    String actionId) {
        this.tipo = tipo;
        this.idAcao = UUID.randomUUID().toString();
        this.actionId = actionId;
        this.origemAcao = origemAcao;
        this.contexto = contexto == null ? ContextoAcao.NAO_INFORMADO : contexto;
        this.papelSemantico = papelSemantico;
        this.estadoAnterior = estadoAnterior;
        this.estadoPosterior = estadoPosterior;
        this.valorProposto = valorProposto;
        this.resultado = resultado;
        this.diagnostico = diagnostico;
        this.timestampEpocaMillis = System.currentTimeMillis();
    }

    /** Compatibilidade: eventos que não participam do fluxo de tentativas (ver actionId). */
    public EventoPapelQuantitativo(TipoEventoPapel tipo, OrigemAcao origemAcao, ContextoAcao contexto,
                                    String papelSemantico, String estadoAnterior, String estadoPosterior,
                                    String valorProposto, ResultadoAcao resultado, DiagnosticoErroPapel diagnostico) {
        this(tipo, origemAcao, contexto, papelSemantico, estadoAnterior, estadoPosterior,
                valorProposto, resultado, diagnostico, null);
    }

    @Override
    public String getTipo() { return tipo.name(); }

    @Override
    public long getTimestampEpocaMillis() { return timestampEpocaMillis; }

    public String getIdAcao() { return idAcao; }
    public String getActionId() { return actionId; }
    public OrigemAcao getOrigemAcao() { return origemAcao; }
    public ContextoAcao getContexto() { return contexto; }
    public String getPapelSemantico() { return papelSemantico; }
    public ResultadoAcao getResultado() { return resultado; }
    public DiagnosticoErroPapel getDiagnostico() { return diagnostico; }
    public boolean isAceito() { return resultado == ResultadoAcao.ACEITO; }

    @Override
    public Map<String, Object> paraMapa() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id_acao", idAcao);
        mapa.put("action_id", actionId);
        mapa.put("tipo", getTipo());
        mapa.put("origem_da_acao", origemAcao == null ? null : origemAcao.name());
        mapa.put("id_sessao", contexto.getIdSessao());
        mapa.put("id_usuario_local", contexto.getIdUsuarioLocal());
        mapa.put("id_tentativa", contexto.getIdTentativa());
        mapa.put("id_situacao_problema", contexto.getIdSituacaoProblema());
        mapa.put("id_representacao", contexto.getIdRepresentacao());
        mapa.put("papel_semantico", papelSemantico);
        mapa.put("estado_anterior", estadoAnterior);
        mapa.put("estado_posterior", estadoPosterior);
        mapa.put("valor_proposto", valorProposto);
        mapa.put("resultado", resultado == null ? null : resultado.name());
        if (diagnostico != null) {
            mapa.put("tipo_erro", diagnostico.getTipo().name());
            mapa.put("chave_mensagem", diagnostico.getChaveMensagem());
            mapa.put("chave_feedback_pedagogico", diagnostico.getChaveFeedbackPedagogico());
            mapa.put("chave_sugestao_correcao", diagnostico.getChaveSugestaoCorrecao());
        } else {
            mapa.put("tipo_erro", null);
        }
        mapa.put("timestamp_epoca_millis", timestampEpocaMillis);
        return mapa;
    }

    @Override
    public String toString() {
        return paraMapa().toString();
    }
}
