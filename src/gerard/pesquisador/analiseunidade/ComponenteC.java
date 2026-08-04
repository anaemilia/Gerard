package gerard.pesquisador.analiseunidade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Componente C — perguntas explicativas disponibilizadas pelo sistema. */
public final class ComponenteC {
    private final boolean buttonAvailable;
    private final boolean buttonActivated;
    private final boolean screenOpened;
    private final StatusExplicacao status;
    private final List<PerguntaExplicativa> questions;
    private final String openedAt;
    private final String closedAt;

    public ComponenteC(boolean buttonAvailable, boolean buttonActivated, boolean screenOpened,
            StatusExplicacao status, List<PerguntaExplicativa> questions, String openedAt, String closedAt) {
        this.buttonAvailable = buttonAvailable;
        this.buttonActivated = buttonActivated;
        this.screenOpened = screenOpened;
        this.status = status;
        this.questions = questions == null ? new ArrayList<PerguntaExplicativa>() : new ArrayList<PerguntaExplicativa>(questions);
        this.openedAt = openedAt;
        this.closedAt = closedAt;
    }

    /** Estado padrão de uma unidade nunca tocada por nenhuma abertura de tela. */
    public static ComponenteC naoAberto(boolean buttonAvailable, List<PerguntaExplicativa> perguntasNaoApresentadas) {
        return new ComponenteC(buttonAvailable, false, false, StatusExplicacao.NOT_OPENED,
                perguntasNaoApresentadas, null, null);
    }

    public boolean isButtonAvailable() { return buttonAvailable; }
    public boolean isButtonActivated() { return buttonActivated; }
    public boolean isScreenOpened() { return screenOpened; }
    public StatusExplicacao getStatus() { return status; }
    public List<PerguntaExplicativa> getQuestions() { return Collections.unmodifiableList(questions); }
    public String getOpenedAt() { return openedAt; }
    public String getClosedAt() { return closedAt; }
}
