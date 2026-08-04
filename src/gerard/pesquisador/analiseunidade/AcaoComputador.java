package gerard.pesquisador.analiseunidade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Componente A — ação do computador que antecede a ação do usuário (B).
 * Montada a partir do que o próprio Gérard já sabe no instante em que abre a
 * avaliação canônica: a situação-problema/instrução vigente. Não há hoje um
 * registro estruturado separado de "mensagem apresentada" por gesto — content
 * é o enunciado da situação-problema ativa (mesmo texto usado em
 * IdentificacaoEvento.situacaoProblema), reaproveitado, não inventado.
 */
public final class AcaoComputador {
    private final String actionId;
    private final String actionType;
    private final String content;
    private final String messageTemplateId;
    private final boolean feedbackPresented;
    private final List<String> technicalEventIds;

    public AcaoComputador(String actionId, String actionType, String content, String messageTemplateId,
            boolean feedbackPresented, List<String> technicalEventIds) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.content = content;
        this.messageTemplateId = messageTemplateId;
        this.feedbackPresented = feedbackPresented;
        this.technicalEventIds = technicalEventIds == null
                ? new ArrayList<String>() : new ArrayList<String>(technicalEventIds);
    }

    public void adicionarEventoTecnico(String technicalEventId) {
        technicalEventIds.add(technicalEventId);
    }

    public String getActionId() { return actionId; }
    public String getActionType() { return actionType; }
    public String getContent() { return content; }
    public String getMessageTemplateId() { return messageTemplateId; }
    public boolean isFeedbackPresented() { return feedbackPresented; }
    public List<String> getTechnicalEventIds() { return Collections.unmodifiableList(technicalEventIds); }
}
