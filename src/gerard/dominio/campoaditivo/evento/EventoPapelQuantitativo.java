package gerard.dominio.campoaditivo.evento;

import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding;
import gerard.dominio.campoaditivo.OrigemAcao;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Evento semântico do ciclo de vida de um PapelQuantitativo — representa o
 * SIGNIFICADO de uma tentativa de posicionamento ou apresentação de
 * apoio pedagógico, nunca um evento técnico de interface. Compõe um
 * {@link EventoEnvelope} (núcleo fixo — event_id, action_id, tipo
 * versionado, origem da ação, timestamp) com os campos de payload
 * (variáveis por tipo de evento) — arquitetura decidida em
 * {@code REFERENCE.md §4.8}, implementada aqui em 2026-08-07. Antes desta
 * data os campos do envelope viviam soltos nesta classe; a separação
 * interna não muda o formato de {@link #paraMapa()}, para não quebrar
 * nenhum consumidor existente do mapa.
 */
public final class EventoPapelQuantitativo implements EventoDominio {

    private final EventoEnvelope envelope;
    private final TipoEventoPapel tipo;
    private final ContextoAcao contexto;
    private final String papelSemantico;
    private final String estadoAnterior;
    private final String estadoPosterior;
    private final String valorProposto;
    private final ResultadoAcao resultado;
    private final DiagnosticoErroPapel diagnostico; // null quando resultado == ACEITO ou tipo == FEEDBACK_EXIBIDO
    private final String rejectionSequenceId;
    private final String estiloScaffolding; // ex.: "AG_EMLQ" — só para FEEDBACK_EXIBIDO
    private final ModalidadeEntregaScaffolding modalidadeEntrega; // idem

    /**
     * @param actionId correlaciona os eventos derivados de uma única ação.
     *        Tentativas diferentes recebem action_id diferentes; rejeições
     *        consecutivas usam rejectionSequenceId para formar uma sequência.
     *        Nulo quando o
     *        evento não participa desse fluxo (ex.: os dois eventos
     *        publicados por posicionar(...), que são um conceito
     *        ortogonal — validade de domínio, não a sequência de
     *        tentativas rejeitadas de uma resposta).
     */
    public EventoPapelQuantitativo(TipoEventoPapel tipo, OrigemAcao origemAcao, ContextoAcao contexto,
                                    String papelSemantico, String estadoAnterior, String estadoPosterior,
                                    String valorProposto, ResultadoAcao resultado, DiagnosticoErroPapel diagnostico,
                                    String actionId) {
        this(tipo, origemAcao, contexto, papelSemantico, estadoAnterior, estadoPosterior,
                valorProposto, resultado, diagnostico, actionId, null, null, null);
    }

    public EventoPapelQuantitativo(TipoEventoPapel tipo, OrigemAcao origemAcao, ContextoAcao contexto,
                                    String papelSemantico, String estadoAnterior, String estadoPosterior,
                                    String valorProposto, ResultadoAcao resultado, DiagnosticoErroPapel diagnostico,
                                    String actionId, String rejectionSequenceId) {
        this(tipo, origemAcao, contexto, papelSemantico, estadoAnterior, estadoPosterior,
                valorProposto, resultado, diagnostico, actionId, rejectionSequenceId, null, null);
    }

    /** Compatibilidade: eventos que não participam do fluxo de tentativas (ver actionId). */
    public EventoPapelQuantitativo(TipoEventoPapel tipo, OrigemAcao origemAcao, ContextoAcao contexto,
                                    String papelSemantico, String estadoAnterior, String estadoPosterior,
                                    String valorProposto, ResultadoAcao resultado, DiagnosticoErroPapel diagnostico) {
        this(tipo, origemAcao, contexto, papelSemantico, estadoAnterior, estadoPosterior,
                valorProposto, resultado, diagnostico, null, null, null, null);
    }

    private EventoPapelQuantitativo(TipoEventoPapel tipo, OrigemAcao origemAcao, ContextoAcao contexto,
                                     String papelSemantico, String estadoAnterior, String estadoPosterior,
                                     String valorProposto, ResultadoAcao resultado, DiagnosticoErroPapel diagnostico,
                                     String actionId, String rejectionSequenceId, String estiloScaffolding,
                                     ModalidadeEntregaScaffolding modalidadeEntrega) {
        this.envelope = new EventoEnvelope(actionId, tipo.chaveVersionada(), origemAcao);
        this.tipo = tipo;
        this.contexto = contexto == null ? ContextoAcao.NAO_INFORMADO : contexto;
        this.papelSemantico = papelSemantico;
        this.estadoAnterior = estadoAnterior;
        this.estadoPosterior = estadoPosterior;
        this.valorProposto = valorProposto;
        this.resultado = resultado;
        this.diagnostico = diagnostico;
        this.rejectionSequenceId = rejectionSequenceId;
        this.estiloScaffolding = estiloScaffolding;
        this.modalidadeEntrega = modalidadeEntrega;
    }

    /**
     * Evento FEEDBACK_EXIBIDO (REFERENCE.md §4.8) — um elemento do
     * repertório de Scaffolding foi apresentado. Quem chama decide, pela
     * modalidade, se já satisfez o critério de confirmação
     * ("renderizado" para VISUAL/SONORA, "affordance ativada" para
     * HAPTICA/MANIPULATIVA/GUIADA_POR_MOVIMENTO —
     * {@link ModalidadeEntregaScaffolding}) antes de publicar — este
     * método não verifica isso, só nomeia e carrega o fato.
     *
     * @param estiloScaffolding código do repertório concreto (ex.:
     *        "AG_EMLQ" — ver TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md)
     */
    public static EventoPapelQuantitativo feedbackExibido(OrigemAcao origemAcao, ContextoAcao contexto,
                                    String papelSemantico, String estiloScaffolding,
                                    ModalidadeEntregaScaffolding modalidadeEntrega, String actionId) {
        return new EventoPapelQuantitativo(TipoEventoPapel.FEEDBACK_EXIBIDO, origemAcao, contexto,
                papelSemantico, null, null, null, null, null, actionId, null,
                estiloScaffolding, modalidadeEntrega);
    }

    public static EventoPapelQuantitativo feedbackExibido(OrigemAcao origemAcao, ContextoAcao contexto,
                                    String papelSemantico, String estiloScaffolding,
                                    ModalidadeEntregaScaffolding modalidadeEntrega, String actionId,
                                    String rejectionSequenceId) {
        return new EventoPapelQuantitativo(TipoEventoPapel.FEEDBACK_EXIBIDO, origemAcao, contexto,
                papelSemantico, null, null, null, null, null, actionId, rejectionSequenceId,
                estiloScaffolding, modalidadeEntrega);
    }

    @Override
    public String getTipo() { return tipo.name(); }

    @Override
    public long getTimestampEpocaMillis() { return envelope.getTimestampEpocaMillis(); }

    /** Tipo semântico com sufixo de versão — ver {@link EventoEnvelope#getTipoVersionado()}. */
    public String getTipoVersionado() { return envelope.getTipoVersionado(); }

    public EventoEnvelope getEnvelope() { return envelope; }
    public String getIdAcao() { return envelope.getEventId(); }
    public String getActionId() { return envelope.getActionId(); }
    public String getRejectionSequenceId() { return rejectionSequenceId; }
    public OrigemAcao getOrigemAcao() { return envelope.getOrigemAcao(); }
    public ContextoAcao getContexto() { return contexto; }
    public String getPapelSemantico() { return papelSemantico; }
    public ResultadoAcao getResultado() { return resultado; }
    public DiagnosticoErroPapel getDiagnostico() { return diagnostico; }
    public String getEstiloScaffolding() { return estiloScaffolding; }
    public ModalidadeEntregaScaffolding getModalidadeEntrega() { return modalidadeEntrega; }
    public boolean isAceito() { return resultado == ResultadoAcao.ACEITO; }

    @Override
    public Map<String, Object> paraMapa() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("id_acao", envelope.getEventId());
        mapa.put("action_id", envelope.getActionId());
        mapa.put("rejection_sequence_id", rejectionSequenceId);
        mapa.put("tipo", getTipo());
        mapa.put("tipo_versionado", envelope.getTipoVersionado());
        mapa.put("origem_da_acao", envelope.getOrigemAcao() == null ? null : envelope.getOrigemAcao().name());
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
        mapa.put("estilo_scaffolding", estiloScaffolding);
        mapa.put("modalidade_entrega", modalidadeEntrega == null ? null : modalidadeEntrega.name());
        mapa.put("timestamp_epoca_millis", envelope.getTimestampEpocaMillis());
        return mapa;
    }

    @Override
    public String toString() {
        return paraMapa().toString();
    }
}
