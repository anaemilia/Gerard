package gerard.ui.interacao;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ordem de prioridade de quem atende um gesto do mouse na área de trabalho (pressionar, mover durante o arraste, duplo
 * clique). Cada elo tenta atender o gesto; o PRIMEIRO que o atende (devolve {@code true}) encerra a cadeia e os
 * seguintes nem são consultados — inclusive os que atendem só para bloquear (por exemplo, o clique numa unidade antes
 * de a modelagem liberar a interação). A prioridade é conhecimento único, escrito num lugar só, na ordem em que os
 * elos são adicionados: o que fica por cima do quê é decisão daqui, não de quem os implementa. Há uma cadeia por
 * gesto, todas com esta mesma classe.
 *
 * Sem Swing além do evento (que pode ser nulo quando o gesto vem só de coordenadas): não conhece nenhum alvo, só a
 * ordem.
 */
public final class CadeiaDeAtendimento {

    /** Um candidato a atender o pressionamento. */
    public interface Elo {
        /** @return true se atendeu (ou bloqueou) o pressionamento: nenhum elo seguinte é consultado. */
        boolean tentar(int x, int y, MouseEvent evento);
    }

    private final List<String> nomes = new ArrayList<String>();
    private final List<Elo> elos = new ArrayList<Elo>();

    public CadeiaDeAtendimento adicionar(String nome, Elo elo) {
        if (nome == null || nome.trim().isEmpty() || elo == null) {
            throw new IllegalArgumentException("nome e elo são obrigatórios");
        }
        nomes.add(nome);
        elos.add(elo);
        return this;
    }

    /**
     * Percorre os elos na ordem de prioridade.
     *
     * @return o nome do elo que atendeu, ou {@code null} se nenhum atendeu
     */
    public String atender(int x, int y, MouseEvent evento) {
        for (int i = 0; i < elos.size(); i++) {
            if (elos.get(i).tentar(x, y, evento)) {
                return nomes.get(i);
            }
        }
        return null;
    }

    /** Os nomes dos elos, do de maior para o de menor prioridade. */
    public List<String> ordem() {
        return Collections.unmodifiableList(nomes);
    }
}
