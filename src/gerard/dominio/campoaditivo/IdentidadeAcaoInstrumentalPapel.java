package gerard.dominio.campoaditivo;

import java.util.Objects;

/**
 * Identidade emitida pelo papel semântico para uma única ação instrumental.
 *
 * A identidade nasce quando o protocolo constitui a ação e permanece a mesma
 * em todos os eventos derivados dela. Ela não identifica o gesto físico nem
 * uma sequência de rejeições.
 */
public final class IdentidadeAcaoInstrumentalPapel {

    private final String actionId;
    private final String papelSemantico;
    private final OrigemAcao origem;

    IdentidadeAcaoInstrumentalPapel(String actionId, String papelSemantico, OrigemAcao origem) {
        this.actionId = Objects.requireNonNull(actionId, "actionId não pode ser nulo");
        this.papelSemantico = Objects.requireNonNull(papelSemantico,
                "papel semântico não pode ser nulo");
        this.origem = Objects.requireNonNull(origem, "origem não pode ser nula");
    }

    public String getActionId() { return actionId; }
    public String getPapelSemantico() { return papelSemantico; }
    public OrigemAcao getOrigem() { return origem; }
}
