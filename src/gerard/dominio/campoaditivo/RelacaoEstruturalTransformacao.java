package gerard.dominio.campoaditivo;

import gerard.semantica.numero.NumeroInteiro;

/**
 * Relação estrutural formal do esquema Transformação de Medidas:
 * EstadoFinal = EstadoInicial + Transformacao.
 *
 * Ver RelacaoEstruturalComposicao para a distinção conceitual completa
 * entre relação estrutural formal (esta classe, pertence a R no tripé
 * C=(S,I,R)) e invariante operatório (proposição mobilizada pelo sujeito,
 * pertence a I, nunca modelada por código).
 *
 * calcularValorAusente NUNCA modifica o papel calculado — só devolve um
 * ResultadoCalculo com origem ORIGEM_SISTEMA. Aplicar esse valor ao papel
 * (e só então gerar o evento correspondente) é uma decisão explícita de
 * outra camada, tomada chamando aplicar(...) — nunca automática. Isso
 * existe para que um valor calculado pelo sistema nunca seja confundido,
 * no log de pesquisa, com um valor que o estudante digitou.
 */
public final class RelacaoEstruturalTransformacao {

    private RelacaoEstruturalTransformacao() { }

    public static RelacaoEstruturalTransformacao transformacaoDeMedidas() {
        return new RelacaoEstruturalTransformacao();
    }

    public String descreverRelacao() { return "EstadoFinal = EstadoInicial + Transformacao"; }

    public EstadoConsistencia verificarConsistencia(PapelQuantitativo estadoInicial, PapelQuantitativo transformacao,
                                                      PapelQuantitativo estadoFinal) {
        exigirNaoNulo(estadoInicial, "estadoInicial");
        exigirNaoNulo(transformacao, "transformacao");
        exigirNaoNulo(estadoFinal, "estadoFinal");
        if (!estadoInicial.estaPreenchido() || !transformacao.estaPreenchido() || !estadoFinal.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        int ei = estadoInicial.valorAtual().valorOuNull();
        int tr = transformacao.valorAtual().valorOuNull();
        int ef = estadoFinal.valorAtual().valorOuNull();
        return (ef == ei + tr) ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
    }

    /**
     * Calcula o valor do único papel incógnito entre os três, sem
     * modificá-lo. Se não houver exatamente uma incógnita, o resultado é
     * NAO_RESOLVIVEL_NESTE_ESTADO — não uma exceção: em uma atividade
     * pedagógica interativa, zero, duas ou três incógnitas são estados
     * legítimos (representação ainda incompleta, ou já totalmente
     * preenchida), não violações de contrato de programação. Exceção só é
     * lançada para argumento nulo, que é, de fato, um erro do chamador.
     */
    public ResultadoCalculo calcularValorAusente(PapelQuantitativo estadoInicial, PapelQuantitativo transformacao,
                                                  PapelQuantitativo estadoFinal) {
        exigirNaoNulo(estadoInicial, "estadoInicial");
        exigirNaoNulo(transformacao, "transformacao");
        exigirNaoNulo(estadoFinal, "estadoFinal");

        int incognitas = 0;
        if (estadoInicial.ehIncognita()) incognitas++;
        if (transformacao.ehIncognita()) incognitas++;
        if (estadoFinal.ehIncognita()) incognitas++;

        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (estadoFinal.ehIncognita()) {
            int calculado = estadoInicial.valorAtual().valorOuNull() + transformacao.valorAtual().valorOuNull();
            return new ResultadoCalculo(estadoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "EstadoFinal = EstadoInicial + Transformacao",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        if (estadoInicial.ehIncognita()) {
            int calculado = estadoFinal.valorAtual().valorOuNull() - transformacao.valorAtual().valorOuNull();
            return new ResultadoCalculo(estadoInicial, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "EstadoInicial = EstadoFinal - Transformacao",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        int calculado = estadoFinal.valorAtual().valorOuNull() - estadoInicial.valorAtual().valorOuNull();
        return new ResultadoCalculo(transformacao, new NumeroInteiro(calculado), descreverRelacao(),
                EstadoConsistencia.CONSISTENTE, "Transformacao = EstadoFinal - EstadoInicial",
                OrigemAcao.ORIGEM_SISTEMA);
    }

    /**
     * Aplica um ResultadoCalculo previamente obtido ao papel que ele
     * calculou — o passo explícito que uma camada externa decide dar. Só
     * então o papel gera seu evento semântico, com a origem que o
     * resultado já carregava (tipicamente ORIGEM_SISTEMA).
     *
     * @throws IllegalStateException se o resultado não tiver valor calculável — violação de contrato do chamador
     */
    public java.util.Optional<DiagnosticoErroPapel> aplicar(ResultadoCalculo resultado, ContextoAcao contexto) {
        if (resultado == null || !resultado.temValorCalculavel()) {
            throw new IllegalStateException(
                    "Não há valor calculado para aplicar (estado: "
                            + (resultado == null ? "resultado nulo" : resultado.getEstadoConsistencia()) + ")");
        }
        return resultado.getPapelCalculado().posicionar(resultado.getValorCalculado(), resultado.getOrigem(), contexto);
    }

    private static void exigirNaoNulo(PapelQuantitativo papel, String nomeParametro) {
        if (papel == null) {
            throw new IllegalArgumentException(nomeParametro + " não pode ser nulo — violação de contrato de programação");
        }
    }
}
