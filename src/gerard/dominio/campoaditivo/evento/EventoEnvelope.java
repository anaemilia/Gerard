package gerard.dominio.campoaditivo.evento;

import gerard.dominio.campoaditivo.OrigemAcao;

import java.util.UUID;

/**
 * Núcleo fixo, presente em todo evento semântico do campo aditivo —
 * arquitetura envelope + payload decidida em {@code REFERENCE.md §4.8}
 * (Seção 19.1 da Revisão 5), com referência a xAPI/Experience API,
 * Caliper Analytics e CloudEvents. Nenhuma estrutura nova foi inventada
 * para essa decisão; esta classe só implementa o que já estava
 * especificado.
 *
 * O payload (variável, específico por tipo de evento) continua morando em
 * cada classe de evento concreta — {@link EventoPapelQuantitativo}, por
 * exemplo — que compõe um envelope em vez de duplicar estes campos.
 */
public final class EventoEnvelope {

    private final String eventId;
    private final String actionId;
    private final String tipoVersionado;
    private final OrigemAcao origemAcao;
    private final long timestampEpocaMillis;

    /**
     * @param actionId correlaciona este evento a outros da mesma ação
     *        (REFERENCE.md §4.8, cardinalidade ação:evento, Alternativa B).
     *        Nulo quando o evento não participa desse fluxo.
     * @param tipoVersionado tipo semântico do evento com sufixo de versão
     *        embutido (ex.: {@code "papel_quantitativo.valor_posicionado.v1"}),
     *        seguindo a convenção "type-based versioning" do CloudEvents já
     *        referenciada em REFERENCE.md — não há campo de versão separado.
     */
    public EventoEnvelope(String actionId, String tipoVersionado, OrigemAcao origemAcao) {
        this.eventId = UUID.randomUUID().toString();
        this.actionId = actionId;
        this.tipoVersionado = tipoVersionado;
        this.origemAcao = origemAcao;
        this.timestampEpocaMillis = System.currentTimeMillis();
    }

    public String getEventId() { return eventId; }
    public String getActionId() { return actionId; }
    public String getTipoVersionado() { return tipoVersionado; }
    public OrigemAcao getOrigemAcao() { return origemAcao; }
    public long getTimestampEpocaMillis() { return timestampEpocaMillis; }
}
