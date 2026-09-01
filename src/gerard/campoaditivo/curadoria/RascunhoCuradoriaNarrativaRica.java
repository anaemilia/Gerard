package gerard.campoaditivo.curadoria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dados nominais coletados pelo editor antes da construção dos objetos ricos.
 * As linhas não possuem significado por posição: todas as ligações usam ids.
 */
public final class RascunhoCuradoriaNarrativaRica {
    private final String idSituacao;
    private final String contexto;
    private final String ordemEstadoInicial;
    private final String chaveEstadoInicial;
    private final String ordemEstadoFinal;
    private final String chaveEstadoFinal;
    private final List<Participante> participantes;
    private final List<Familia> familias;
    private final List<Objeto> objetos;
    private final List<ItemEstado> estadoInicial;
    private final List<Evento> eventos;
    private final List<ItemEstado> estadoFinal;
    private final List<Correspondencia> correspondencias;

    public RascunhoCuradoriaNarrativaRica(
            String idSituacao,
            String contexto,
            String ordemEstadoInicial,
            String chaveEstadoInicial,
            String ordemEstadoFinal,
            String chaveEstadoFinal,
            List<Participante> participantes,
            List<Familia> familias,
            List<Objeto> objetos,
            List<ItemEstado> estadoInicial,
            List<Evento> eventos,
            List<ItemEstado> estadoFinal,
            List<Correspondencia> correspondencias) {
        this.idSituacao = limpar(idSituacao);
        this.contexto = contexto == null ? "" : contexto.trim();
        this.ordemEstadoInicial = limpar(ordemEstadoInicial);
        this.chaveEstadoInicial = limpar(chaveEstadoInicial);
        this.ordemEstadoFinal = limpar(ordemEstadoFinal);
        this.chaveEstadoFinal = limpar(chaveEstadoFinal);
        this.participantes = copiar(participantes);
        this.familias = copiar(familias);
        this.objetos = copiar(objetos);
        this.estadoInicial = copiar(estadoInicial);
        this.eventos = copiar(eventos);
        this.estadoFinal = copiar(estadoFinal);
        this.correspondencias = copiar(correspondencias);
    }

    public String getIdSituacao() { return idSituacao; }
    public String getContexto() { return contexto; }
    public String getOrdemEstadoInicial() { return ordemEstadoInicial; }
    public String getChaveEstadoInicial() { return chaveEstadoInicial; }
    public String getOrdemEstadoFinal() { return ordemEstadoFinal; }
    public String getChaveEstadoFinal() { return chaveEstadoFinal; }
    public List<Participante> getParticipantes() { return participantes; }
    public List<Familia> getFamilias() { return familias; }
    public List<Objeto> getObjetos() { return objetos; }
    public List<ItemEstado> getEstadoInicial() { return estadoInicial; }
    public List<Evento> getEventos() { return eventos; }
    public List<ItemEstado> getEstadoFinal() { return estadoFinal; }
    public List<Correspondencia> getCorrespondencias() {
        return correspondencias;
    }

    private static <T> List<T> copiar(List<T> origem) {
        return Collections.unmodifiableList(new ArrayList<T>(
                origem == null ? Collections.<T>emptyList() : origem));
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    public static final class Participante {
        private final String id;
        private final String nome;
        public Participante(String id, String nome) {
            this.id = limpar(id); this.nome = limpar(nome);
        }
        public String getId() { return id; }
        public String getNome() { return nome; }
    }

    public static final class Familia {
        private final String id;
        private final String nome;
        public Familia(String id, String nome) {
            this.id = limpar(id); this.nome = limpar(nome);
        }
        public String getId() { return id; }
        public String getNome() { return nome; }
    }

    public static final class Objeto {
        private final String id;
        private final String familiaId;
        private final String chaveVisual;
        private final String caracteristicas;
        public Objeto(
                String id, String familiaId,
                String chaveVisual, String caracteristicas) {
            this.id = limpar(id);
            this.familiaId = limpar(familiaId);
            this.chaveVisual = limpar(chaveVisual);
            this.caracteristicas = limpar(caracteristicas);
        }
        public String getId() { return id; }
        public String getFamiliaId() { return familiaId; }
        public String getChaveVisual() { return chaveVisual; }
        public String getCaracteristicas() { return caracteristicas; }
    }

    /** Objeto vazio representa inventário explicitamente vazio. */
    public static final class ItemEstado {
        private final String participanteId;
        private final String objetoId;
        private final String quantidade;
        public ItemEstado(
                String participanteId, String objetoId, String quantidade) {
            this.participanteId = limpar(participanteId);
            this.objetoId = limpar(objetoId);
            this.quantidade = limpar(quantidade);
        }
        public String getParticipanteId() { return participanteId; }
        public String getObjetoId() { return objetoId; }
        public String getQuantidade() { return quantidade; }
    }

    public static final class Evento {
        private final String tipo;
        private final String ordem;
        private final String chave;
        private final String origemId;
        private final String destinoId;
        private final String objetoId;
        private final String quantidade;
        public Evento(
                String tipo, String ordem, String chave,
                String origemId, String destinoId,
                String objetoId, String quantidade) {
            this.tipo = limpar(tipo); this.ordem = limpar(ordem);
            this.chave = limpar(chave); this.origemId = limpar(origemId);
            this.destinoId = limpar(destinoId);
            this.objetoId = limpar(objetoId);
            this.quantidade = limpar(quantidade);
        }
        public String getTipo() { return tipo; }
        public String getOrdem() { return ordem; }
        public String getChave() { return chave; }
        public String getOrigemId() { return origemId; }
        public String getDestinoId() { return destinoId; }
        public String getObjetoId() { return objetoId; }
        public String getQuantidade() { return quantidade; }
    }

    public static final class Correspondencia {
        private final String papel;
        private final String tipo;
        private final String participanteId;
        private final String participanteComparadoId;
        private final String familiaId;
        private final String eventoChave;
        private final String objetoId;
        public Correspondencia(
                String papel, String tipo, String participanteId,
                String participanteComparadoId, String familiaId,
                String eventoChave, String objetoId) {
            this.papel = limpar(papel); this.tipo = limpar(tipo);
            this.participanteId = limpar(participanteId);
            this.participanteComparadoId = limpar(participanteComparadoId);
            this.familiaId = limpar(familiaId);
            this.eventoChave = limpar(eventoChave);
            this.objetoId = limpar(objetoId);
        }
        public String getPapel() { return papel; }
        public String getTipo() { return tipo; }
        public String getParticipanteId() { return participanteId; }
        public String getParticipanteComparadoId() {
            return participanteComparadoId;
        }
        public String getFamiliaId() { return familiaId; }
        public String getEventoChave() { return eventoChave; }
        public String getObjetoId() { return objetoId; }
    }
}
