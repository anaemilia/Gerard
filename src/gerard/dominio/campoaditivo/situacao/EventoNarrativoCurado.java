package gerard.dominio.campoaditivo.situacao;

import gerard.semantica.numero.NumeroNatural;

/**
 * Alteração quantitativa explicitamente curada. Não contém frase pronta,
 * quadro, animação ou componente visual.
 */
public final class EventoNarrativoCurado {
    public enum Tipo {
        ACRESCIMO,
        REDUCAO,
        TRANSFERENCIA
    }

    private final Tipo tipo;
    private final MarcadorTemporal marcador;
    private final ParticipanteNarrativo origem;
    private final ParticipanteNarrativo destino;
    private final ObjetoContado objeto;
    private final NumeroNatural quantidade;

    private EventoNarrativoCurado(
            Tipo tipo,
            MarcadorTemporal marcador,
            ParticipanteNarrativo origem,
            ParticipanteNarrativo destino,
            ObjetoContado objeto,
            NumeroNatural quantidade) {
        if (tipo == null || marcador == null || objeto == null || quantidade == null) {
            throw new IllegalArgumentException("tipo, marcador, objeto e quantidade são obrigatórios");
        }
        if (quantidade.intValue() == 0) {
            throw new IllegalArgumentException("evento narrativo precisa alterar uma quantidade");
        }
        if (tipo == Tipo.ACRESCIMO && destino == null) {
            throw new IllegalArgumentException("acréscimo exige participante de destino");
        }
        if (tipo == Tipo.REDUCAO && origem == null) {
            throw new IllegalArgumentException("redução exige participante de origem");
        }
        if (tipo == Tipo.TRANSFERENCIA
                && (origem == null || destino == null || origem.equals(destino))) {
            throw new IllegalArgumentException(
                    "transferência exige origem e destino distintos");
        }
        this.tipo = tipo;
        this.marcador = marcador;
        this.origem = origem;
        this.destino = destino;
        this.objeto = objeto;
        this.quantidade = quantidade;
    }

    public static EventoNarrativoCurado acrescimo(
            MarcadorTemporal marcador,
            ParticipanteNarrativo destino,
            ObjetoContado objeto,
            NumeroNatural quantidade) {
        return new EventoNarrativoCurado(
                Tipo.ACRESCIMO, marcador, null, destino, objeto, quantidade);
    }

    public static EventoNarrativoCurado reducao(
            MarcadorTemporal marcador,
            ParticipanteNarrativo origem,
            ObjetoContado objeto,
            NumeroNatural quantidade) {
        return new EventoNarrativoCurado(
                Tipo.REDUCAO, marcador, origem, null, objeto, quantidade);
    }

    public static EventoNarrativoCurado transferencia(
            MarcadorTemporal marcador,
            ParticipanteNarrativo origem,
            ParticipanteNarrativo destino,
            ObjetoContado objeto,
            NumeroNatural quantidade) {
        return new EventoNarrativoCurado(
                Tipo.TRANSFERENCIA, marcador, origem, destino, objeto, quantidade);
    }

    public Tipo getTipo() { return tipo; }
    public MarcadorTemporal getMarcador() { return marcador; }
    public ParticipanteNarrativo getOrigem() { return origem; }
    public ParticipanteNarrativo getDestino() { return destino; }
    public ObjetoContado getObjeto() { return objeto; }
    public NumeroNatural getQuantidade() { return quantidade; }

    public EstadoNarrativo aplicarEm(EstadoNarrativo estado) {
        EstadoNarrativo corrente = estado;
        if (tipo == Tipo.REDUCAO || tipo == Tipo.TRANSFERENCIA) {
            InventarioNarrativo inventarioOrigem = corrente.inventarioDe(origem)
                    .retirar(objeto, quantidade);
            corrente = corrente.comInventario(origem, inventarioOrigem, marcador);
        }
        if (tipo == Tipo.ACRESCIMO || tipo == Tipo.TRANSFERENCIA) {
            InventarioNarrativo inventarioDestino = corrente.inventarioDe(destino)
                    .acrescentar(objeto, quantidade);
            corrente = corrente.comInventario(destino, inventarioDestino, marcador);
        }
        return corrente;
    }

    /** Variação assinada deste evento para participante e família explícitos. */
    public int variacaoPara(
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        if (!objeto.pertenceA(familia)) {
            return 0;
        }
        int delta = 0;
        if (participante.equals(origem)) {
            delta = Math.subtractExact(delta, quantidade.intValue());
        }
        if (participante.equals(destino)) {
            delta = Math.addExact(delta, quantidade.intValue());
        }
        return delta;
    }
}
