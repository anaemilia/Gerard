package gerard.pesquisador.analiseunidade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Componente D — respostas/explicações fornecidas pelo usuário. */
public final class ComponenteD {
    private final StatusExplicacao status;
    private final List<RespostaExplicativa> responses;
    private final String completedAt;

    public ComponenteD(StatusExplicacao status, List<RespostaExplicativa> responses, String completedAt) {
        this.status = status;
        this.responses = responses == null ? new ArrayList<RespostaExplicativa>() : new ArrayList<RespostaExplicativa>(responses);
        this.completedAt = completedAt;
    }

    public static ComponenteD naoAberto() {
        return new ComponenteD(StatusExplicacao.NOT_OPENED, Collections.<RespostaExplicativa>emptyList(), null);
    }

    public StatusExplicacao getStatus() { return status; }
    public List<RespostaExplicativa> getResponses() { return Collections.unmodifiableList(responses); }
    public String getCompletedAt() { return completedAt; }
}
