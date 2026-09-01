package gerard.dominio.campoaditivo.situacao;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Fotografia factual dos inventários em um momento da narrativa. */
public final class EstadoNarrativo {
    private final MarcadorTemporal marcador;
    private final Map<ParticipanteNarrativo, InventarioNarrativo> inventarios;

    public EstadoNarrativo(
            MarcadorTemporal marcador,
            Map<ParticipanteNarrativo, InventarioNarrativo> inventarios) {
        if (marcador == null || inventarios == null || inventarios.isEmpty()) {
            throw new IllegalArgumentException("marcador e inventários do estado são obrigatórios");
        }
        LinkedHashMap<ParticipanteNarrativo, InventarioNarrativo> copia = new LinkedHashMap<>();
        for (Map.Entry<ParticipanteNarrativo, InventarioNarrativo> entrada : inventarios.entrySet()) {
            if (entrada.getKey() == null || entrada.getValue() == null) {
                throw new IllegalArgumentException("participante e inventário são obrigatórios");
            }
            copia.put(entrada.getKey(), entrada.getValue());
        }
        this.marcador = marcador;
        this.inventarios = Collections.unmodifiableMap(copia);
    }

    public MarcadorTemporal getMarcador() { return marcador; }
    public Map<ParticipanteNarrativo, InventarioNarrativo> getInventarios() {
        return inventarios;
    }

    public boolean possuiParticipante(ParticipanteNarrativo participante) {
        return inventarios.containsKey(participante);
    }

    public InventarioNarrativo inventarioDe(ParticipanteNarrativo participante) {
        InventarioNarrativo inventario = inventarios.get(participante);
        if (inventario == null) {
            throw new IllegalStateException("narrativa.estado.participante_ausente:" + participante.getId());
        }
        return inventario;
    }

    public EstadoNarrativo comInventario(
            ParticipanteNarrativo participante,
            InventarioNarrativo inventario,
            MarcadorTemporal novoMarcador) {
        if (!possuiParticipante(participante)) {
            throw new IllegalStateException("narrativa.estado.participante_ausente:" + participante.getId());
        }
        LinkedHashMap<ParticipanteNarrativo, InventarioNarrativo> copia =
                new LinkedHashMap<>(inventarios);
        copia.put(participante, inventario);
        return new EstadoNarrativo(novoMarcador, copia);
    }

    public boolean mesmosInventarios(EstadoNarrativo outro) {
        if (outro == null || inventarios.size() != outro.inventarios.size()) {
            return false;
        }
        for (Map.Entry<ParticipanteNarrativo, InventarioNarrativo> entrada : inventarios.entrySet()) {
            InventarioNarrativo correspondente = outro.inventarios.get(entrada.getKey());
            if (correspondente == null || !entrada.getValue().mesmasQuantidades(correspondente)) {
                return false;
            }
        }
        return true;
    }
}
