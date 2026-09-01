package gerard.dominio.campoaditivo.situacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Roteiro semântico independente de mídia. Renderizadores futuros decidem
 * se a sequência será texto, quadrinhos, animação ou vídeo.
 */
public final class SequenciaNarrativa {
    private final EstadoNarrativo estadoInicial;
    private final List<EventoNarrativoCurado> eventos;
    private final List<EstadoNarrativo> estadosAposEventos;

    public SequenciaNarrativa(
            EstadoNarrativo estadoInicial,
            List<EventoNarrativoCurado> eventos,
            List<EstadoNarrativo> estadosAposEventos) {
        if (estadoInicial == null || eventos == null || estadosAposEventos == null
                || eventos.size() != estadosAposEventos.size()) {
            throw new IllegalArgumentException(
                    "sequência exige um estado posterior para cada evento");
        }
        this.estadoInicial = estadoInicial;
        this.eventos = Collections.unmodifiableList(new ArrayList<>(eventos));
        this.estadosAposEventos = Collections.unmodifiableList(
                new ArrayList<>(estadosAposEventos));
    }

    public EstadoNarrativo getEstadoInicial() { return estadoInicial; }
    public List<EventoNarrativoCurado> getEventos() { return eventos; }
    public List<EstadoNarrativo> getEstadosAposEventos() { return estadosAposEventos; }

    public EstadoNarrativo getEstadoFinalCalculado() {
        return estadosAposEventos.isEmpty()
                ? estadoInicial
                : estadosAposEventos.get(estadosAposEventos.size() - 1);
    }
}
