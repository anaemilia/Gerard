package gerard.dominio.campoaditivo.situacao;

import gerard.semantica.numero.NumeroInteiro;

/**
 * Referência explícita, fornecida pela curadoria humana, para recuperar de
 * uma narrativa o valor correspondente a um papel semântico.
 */
public final class ReferenciaValorNarrativo {
    public enum Tipo {
        QUANTIDADE_INICIAL,
        QUANTIDADE_INICIAL_DO_OBJETO,
        DIFERENCA_ENTRE_QUANTIDADES_INICIAIS,
        DIFERENCA_ENTRE_QUANTIDADES_FINAIS,
        VARIACAO_DE_EVENTO,
        QUANTIDADE_APOS_EVENTO,
        VARIACAO_TOTAL,
        QUANTIDADE_FINAL_CALCULADA
    }

    private final Tipo tipo;
    private final ParticipanteNarrativo participante;
    private final ParticipanteNarrativo participanteComparado;
    private final FamiliaObjeto familia;
    private final String chaveEvento;
    private final ObjetoContado objeto;

    private ReferenciaValorNarrativo(
            Tipo tipo,
            ParticipanteNarrativo participante,
            FamiliaObjeto familia,
            String chaveEvento) {
        this(tipo, participante, null, familia, chaveEvento, null);
    }

    private ReferenciaValorNarrativo(
            Tipo tipo,
            ParticipanteNarrativo participante,
            ParticipanteNarrativo participanteComparado,
            FamiliaObjeto familia,
            String chaveEvento,
            ObjetoContado objeto) {
        if (tipo == null || participante == null) {
            throw new IllegalArgumentException(
                    "tipo e participante da referência são obrigatórios");
        }
        if (tipo == Tipo.QUANTIDADE_INICIAL_DO_OBJETO) {
            if (objeto == null) {
                throw new IllegalArgumentException(
                        "quantidade inicial de objeto exige o objeto contado");
            }
            this.familia = objeto.getFamilia();
        } else {
            if (familia == null) {
                throw new IllegalArgumentException(
                        "família da referência é obrigatória");
            }
            this.familia = familia;
        }
        if ((tipo == Tipo.DIFERENCA_ENTRE_QUANTIDADES_INICIAIS
                || tipo == Tipo.DIFERENCA_ENTRE_QUANTIDADES_FINAIS)
                && participanteComparado == null) {
            throw new IllegalArgumentException(
                    "diferença entre quantidades exige os dois participantes curados");
        }
        if (tipo == Tipo.VARIACAO_DE_EVENTO
                || tipo == Tipo.QUANTIDADE_APOS_EVENTO) {
            this.chaveEvento = FamiliaObjeto.obrigatorio(
                    chaveEvento, "variação de evento exige a chave do evento");
        } else {
            this.chaveEvento = "";
        }
        this.tipo = tipo;
        this.participante = participante;
        this.participanteComparado = participanteComparado;
        this.objeto = objeto;
    }

    public static ReferenciaValorNarrativo quantidadeInicial(
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        return new ReferenciaValorNarrativo(
                Tipo.QUANTIDADE_INICIAL, participante, familia, null);
    }

    public static ReferenciaValorNarrativo quantidadeInicialDoObjeto(
            ParticipanteNarrativo participante,
            ObjetoContado objeto) {
        return new ReferenciaValorNarrativo(
                Tipo.QUANTIDADE_INICIAL_DO_OBJETO,
                participante, null, null, null, objeto);
    }

    /**
     * Referência relacional explicitamente curada para Comparação de Medidas.
     * A ordem dos parâmetros expressa a relação formal; não é inferida da
     * posição dos participantes no texto, na tela ou em qualquer coleção.
     */
    public static ReferenciaValorNarrativo diferencaEntreQuantidadesIniciais(
            ParticipanteNarrativo participanteDoReferendo,
            ParticipanteNarrativo participanteDoReferido,
            FamiliaObjeto familia) {
        return new ReferenciaValorNarrativo(
                Tipo.DIFERENCA_ENTRE_QUANTIDADES_INICIAIS,
                participanteDoReferendo, participanteDoReferido,
                familia, null, null);
    }

    /**
     * Relação final orientada por dois participantes nominalmente declarados.
     * A ordem expressa quantidade(participanteDoReferendo) menos
     * quantidade(participanteDoReferido); nenhuma posição textual ou visual
     * participa dessa decisão.
     */
    public static ReferenciaValorNarrativo diferencaEntreQuantidadesFinais(
            ParticipanteNarrativo participanteDoReferendo,
            ParticipanteNarrativo participanteDoReferido,
            FamiliaObjeto familia) {
        return new ReferenciaValorNarrativo(
                Tipo.DIFERENCA_ENTRE_QUANTIDADES_FINAIS,
                participanteDoReferendo, participanteDoReferido,
                familia, null, null);
    }

    public static ReferenciaValorNarrativo variacaoDeEvento(
            String chaveEvento,
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        return new ReferenciaValorNarrativo(
                Tipo.VARIACAO_DE_EVENTO, participante, familia, chaveEvento);
    }

    public static ReferenciaValorNarrativo quantidadeAposEvento(
            String chaveEvento,
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        return new ReferenciaValorNarrativo(
                Tipo.QUANTIDADE_APOS_EVENTO, participante, familia, chaveEvento);
    }

    public static ReferenciaValorNarrativo variacaoTotal(
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        return new ReferenciaValorNarrativo(
                Tipo.VARIACAO_TOTAL, participante, familia, null);
    }

    public static ReferenciaValorNarrativo quantidadeFinalCalculada(
            ParticipanteNarrativo participante,
            FamiliaObjeto familia) {
        return new ReferenciaValorNarrativo(
                Tipo.QUANTIDADE_FINAL_CALCULADA, participante, familia, null);
    }

    public Tipo getTipo() { return tipo; }
    public ParticipanteNarrativo getParticipante() { return participante; }
    public ParticipanteNarrativo getParticipanteComparado() {
        return participanteComparado;
    }
    public FamiliaObjeto getFamilia() { return familia; }
    public String getChaveEvento() { return chaveEvento; }
    public ObjetoContado getObjeto() { return objeto; }

    public NumeroInteiro resolver(NarrativaCurada narrativa) {
        switch (tipo) {
            case QUANTIDADE_INICIAL:
                return new NumeroInteiro(
                        narrativa.quantidadeInicial(participante, familia).intValue());
            case QUANTIDADE_INICIAL_DO_OBJETO:
                return new NumeroInteiro(
                        narrativa.quantidadeInicial(participante, objeto).intValue());
            case DIFERENCA_ENTRE_QUANTIDADES_INICIAIS:
                return narrativa.diferencaEntreQuantidadesIniciais(
                        participante, participanteComparado, familia);
            case DIFERENCA_ENTRE_QUANTIDADES_FINAIS:
                return narrativa.diferencaEntreQuantidadesFinais(
                        participante, participanteComparado, familia);
            case VARIACAO_DE_EVENTO:
                return narrativa.variacaoDoEvento(chaveEvento, participante, familia);
            case QUANTIDADE_APOS_EVENTO:
                return new NumeroInteiro(
                        narrativa.quantidadeAposEvento(
                                chaveEvento, participante, familia).intValue());
            case VARIACAO_TOTAL:
                return narrativa.variacaoTotal(participante, familia);
            case QUANTIDADE_FINAL_CALCULADA:
                return new NumeroInteiro(
                        narrativa.quantidadeFinalCalculada(participante, familia).intValue());
            default:
                throw new IllegalStateException("tipo de referência narrativa não suportado");
        }
    }
}
