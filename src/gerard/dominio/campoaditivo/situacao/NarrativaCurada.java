package gerard.dominio.campoaditivo.situacao;

import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agregado narrativo curado. Preserva a ordem fornecida pelo humano e valida
 * a evolução sem reordenar, corrigir ou sobrescrever o estado declarado.
 */
public final class NarrativaCurada {
    private final String contexto;
    private final EstadoNarrativo estadoInicial;
    private final List<EventoNarrativoCurado> eventos;
    private final EstadoNarrativo estadoFinalDeclarado;

    public NarrativaCurada(
            String contexto,
            EstadoNarrativo estadoInicial,
            List<EventoNarrativoCurado> eventos,
            EstadoNarrativo estadoFinalDeclarado) {
        this.contexto = contexto == null ? "" : contexto.trim();
        if (estadoInicial == null || eventos == null || estadoFinalDeclarado == null) {
            throw new IllegalArgumentException(
                    "estado inicial, eventos e estado final declarado são obrigatórios");
        }
        this.estadoInicial = estadoInicial;
        this.eventos = Collections.unmodifiableList(new ArrayList<>(eventos));
        this.estadoFinalDeclarado = estadoFinalDeclarado;
    }

    public String getContexto() { return contexto; }
    public EstadoNarrativo getEstadoInicial() { return estadoInicial; }
    public List<EventoNarrativoCurado> getEventos() { return eventos; }
    public EstadoNarrativo getEstadoFinalDeclarado() { return estadoFinalDeclarado; }

    public ResultadoValidacaoSituacao validar() {
        Simulacao simulacao = simular();
        if (simulacao.diagnosticos.isEmpty()
                && !simulacao.sequencia.getEstadoFinalCalculado()
                        .mesmosInventarios(estadoFinalDeclarado)) {
            simulacao.diagnosticos.add(new DiagnosticoSituacao(
                    "narrativa.estado_final.divergente",
                    estadoFinalDeclarado.getMarcador().getChave()));
        }
        return new ResultadoValidacaoSituacao(simulacao.diagnosticos);
    }

    public SequenciaNarrativa criarSequenciaNarrativa() {
        Simulacao simulacao = simular();
        if (!simulacao.diagnosticos.isEmpty()
                || !simulacao.sequencia.getEstadoFinalCalculado()
                        .mesmosInventarios(estadoFinalDeclarado)) {
            throw new IllegalStateException(
                    "situação narrativa inconsistente; valide antes de renderizar");
        }
        return simulacao.sequencia;
    }

    public NumeroNatural quantidadeInicial(
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        return estadoInicial.inventarioDe(participante).totalDaFamilia(familia);
    }

    public NumeroNatural quantidadeInicial(
            ParticipanteNarrativo participante,
            ObjetoContado objeto) {
        if (objeto == null) {
            throw new IllegalArgumentException("objeto contado é obrigatório");
        }
        return estadoInicial.inventarioDe(participante).quantidadeDe(objeto);
    }

    /**
     * Calcula o valor relativo entre dois participantes nomeados pela
     * curadoria: quantidade do Referendo menos quantidade do Referido.
     * Nenhuma posição narrativa ou visual participa dessa decisão.
     */
    public NumeroInteiro diferencaEntreQuantidadesIniciais(
            ParticipanteNarrativo participanteDoReferendo,
            ParticipanteNarrativo participanteDoReferido,
            FamiliaObjeto familia) {
        int referendo = quantidadeInicial(
                participanteDoReferendo, familia).intValue();
        int referido = quantidadeInicial(
                participanteDoReferido, familia).intValue();
        return new NumeroInteiro(Math.subtractExact(referendo, referido));
    }

    /**
     * Calcula uma relação final com a orientação nominal fornecida pela
     * curadoria. A orientação pode ser diferente daquela usada para a relação
     * inicial; essa diferença é conhecimento explícito da situação, não uma
     * inferência pela ordem dos participantes.
     */
    public NumeroInteiro diferencaEntreQuantidadesFinais(
            ParticipanteNarrativo participanteDoReferendo,
            ParticipanteNarrativo participanteDoReferido,
            FamiliaObjeto familia) {
        int referendo = quantidadeFinalCalculada(
                participanteDoReferendo, familia).intValue();
        int referido = quantidadeFinalCalculada(
                participanteDoReferido, familia).intValue();
        return new NumeroInteiro(Math.subtractExact(referendo, referido));
    }

    public NumeroNatural quantidadeFinalCalculada(
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        return criarSequenciaNarrativa().getEstadoFinalCalculado()
                .inventarioDe(participante).totalDaFamilia(familia);
    }

    public NumeroInteiro variacaoDoEvento(
            String chaveEvento,
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        for (EventoNarrativoCurado evento : eventos) {
            if (evento.getMarcador().getChave().equals(chaveEvento)) {
                return new NumeroInteiro(evento.variacaoPara(participante, familia));
            }
        }
        throw new IllegalStateException("narrativa.evento.ausente:" + chaveEvento);
    }

    /**
     * Recupera o estado produzido pelo evento explicitamente nomeado. A chave
     * vem da curadoria; a ordem da lista é usada somente para alinhar cada
     * evento ao estado que ele próprio produziu, nunca para inferir papéis.
     */
    public NumeroNatural quantidadeAposEvento(
            String chaveEvento,
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        SequenciaNarrativa sequencia = criarSequenciaNarrativa();
        for (int i = 0; i < sequencia.getEventos().size(); i++) {
            EventoNarrativoCurado evento = sequencia.getEventos().get(i);
            if (evento.getMarcador().getChave().equals(chaveEvento)) {
                return sequencia.getEstadosAposEventos().get(i)
                        .inventarioDe(participante).totalDaFamilia(familia);
            }
        }
        throw new IllegalStateException("narrativa.evento.ausente:" + chaveEvento);
    }

    public NumeroInteiro variacaoTotal(
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        int total = 0;
        for (EventoNarrativoCurado evento : eventos) {
            total = Math.addExact(total, evento.variacaoPara(participante, familia));
        }
        return new NumeroInteiro(total);
    }

    private Simulacao simular() {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();
        List<EstadoNarrativo> estados = new ArrayList<>();
        EstadoNarrativo corrente = estadoInicial;
        int ordemAnterior = estadoInicial.getMarcador().getOrdem();
        for (EventoNarrativoCurado evento : eventos) {
            if (evento.getMarcador().getOrdem() <= ordemAnterior) {
                diagnosticos.add(new DiagnosticoSituacao(
                        "narrativa.ordem_temporal.invalida",
                        evento.getMarcador().getChave()));
            }
            ordemAnterior = evento.getMarcador().getOrdem();
            try {
                corrente = evento.aplicarEm(corrente);
                estados.add(corrente);
            } catch (IllegalStateException inconsistencia) {
                diagnosticos.add(new DiagnosticoSituacao(
                        "narrativa.evento.inaplicavel",
                        evento.getMarcador().getChave() + ":" + inconsistencia.getMessage()));
                break;
            }
        }
        if (estadoFinalDeclarado.getMarcador().getOrdem() <= ordemAnterior) {
            diagnosticos.add(new DiagnosticoSituacao(
                    "narrativa.estado_final.ordem_invalida",
                    estadoFinalDeclarado.getMarcador().getChave()));
        }
        return new Simulacao(
                new SequenciaNarrativa(estadoInicial,
                        eventos.subList(0, estados.size()), estados),
                diagnosticos);
    }

    private static final class Simulacao {
        private final SequenciaNarrativa sequencia;
        private final List<DiagnosticoSituacao> diagnosticos;

        private Simulacao(
                SequenciaNarrativa sequencia,
                List<DiagnosticoSituacao> diagnosticos) {
            this.sequencia = sequencia;
            this.diagnosticos = diagnosticos;
        }
    }
}
