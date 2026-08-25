package gerard.dominio.campoaditivo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Registro factual produzido pela tentativa que possui a ação de restaurar.
 *
 * A ação não integra a sequência de rejeições que encerra. As identidades
 * encerradas aparecem apenas como contexto factual, nunca como o
 * {@code rejection_sequence_id} da própria restauração.
 */
public final class RegistroAcaoRestauracaoModelagem {

    private final String actionId;
    private final String tentativaId;
    private final TipoRestauracaoModelagem tipo;
    private final OrigemAcao origem;
    private final List<String> papeisParticipantes;
    private final List<String> sequenciasRejeicaoEncerradas;

    RegistroAcaoRestauracaoModelagem(String actionId, String tentativaId,
            TipoRestauracaoModelagem tipo, OrigemAcao origem,
            List<String> papeisParticipantes,
            List<String> sequenciasRejeicaoEncerradas) {
        this.actionId = exigirTexto(actionId, "actionId");
        this.tentativaId = tentativaId == null ? "" : tentativaId.trim();
        this.tipo = Objects.requireNonNull(tipo, "tipo não pode ser nulo");
        this.origem = Objects.requireNonNull(origem, "origem não pode ser nula");
        this.papeisParticipantes = copiarImutavel(papeisParticipantes);
        this.sequenciasRejeicaoEncerradas = copiarImutavel(sequenciasRejeicaoEncerradas);
    }

    public String getActionId() { return actionId; }
    public String getTentativaId() { return tentativaId; }
    public TipoRestauracaoModelagem getTipo() { return tipo; }
    public OrigemAcao getOrigem() { return origem; }
    public List<String> getPapeisParticipantes() { return papeisParticipantes; }
    public List<String> getSequenciasRejeicaoEncerradas() {
        return sequenciasRejeicaoEncerradas;
    }

    /**
     * A restauração encerra sequências anteriores, mas não é uma rejeição.
     */
    public String getRejectionSequenceId() { return ""; }

    private static List<String> copiarImutavel(List<String> valores) {
        if (valores == null || valores.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<String>(valores));
    }

    private static String exigirTexto(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.length() == 0) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }
}
