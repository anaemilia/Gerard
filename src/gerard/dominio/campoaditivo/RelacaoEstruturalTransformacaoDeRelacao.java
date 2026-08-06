package gerard.dominio.campoaditivo;

import gerard.semantica.numero.NumeroInteiro;

/**
 * Relação estrutural formal do esquema Transformação de Relação:
 * RelacaoFinal = RelacaoInicial + Transformacao.
 *
 * Categoria "Relações" de Vergnaud, não "Medidas": os três papéis são
 * números relativos (domínio INTEIROS nos três) — a Relação, inicial ou
 * final, não é uma medida (não representa uma quantidade de algo), é uma
 * comparação relativa entre grandezas, por isso não herda a restrição de
 * não-negatividade que Composição/Transformação/Comparação de Medidas têm
 * em seus papéis de medida. Ver RelacaoEstruturalComposicao para a
 * distinção conceitual completa entre relação estrutural formal (esta
 * classe, pertence a R no tripé C=(S,I,R) de Vergnaud) e invariante
 * operatório (proposição mobilizada pelo sujeito, pertence a I, nunca
 * modelada por código).
 *
 * calcularValorAusente NUNCA modifica o papel calculado — só devolve um
 * ResultadoCalculo com origem ORIGEM_SISTEMA. Aplicar esse valor ao papel
 * (e só então gerar o evento correspondente) é uma decisão explícita de
 * outra camada, tomada chamando aplicar(...) — nunca automática.
 */
public final class RelacaoEstruturalTransformacaoDeRelacao {

    private RelacaoEstruturalTransformacaoDeRelacao() { }

    public static RelacaoEstruturalTransformacaoDeRelacao transformacaoDeRelacao() {
        return new RelacaoEstruturalTransformacaoDeRelacao();
    }

    public String descreverRelacao() { return "RelacaoFinal = RelacaoInicial + Transformacao"; }

    public EstadoConsistencia verificarConsistencia(PapelQuantitativo relacaoInicial, PapelQuantitativo transformacao,
                                                      PapelQuantitativo relacaoFinal) {
        exigirNaoNulo(relacaoInicial, "relacaoInicial");
        exigirNaoNulo(transformacao, "transformacao");
        exigirNaoNulo(relacaoFinal, "relacaoFinal");
        if (!relacaoInicial.estaPreenchido() || !transformacao.estaPreenchido() || !relacaoFinal.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        int ri = relacaoInicial.valorAtual().valorOuNull();
        int tr = transformacao.valorAtual().valorOuNull();
        int rf = relacaoFinal.valorAtual().valorOuNull();
        return (rf == ri + tr) ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
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
    public ResultadoCalculo calcularValorAusente(PapelQuantitativo relacaoInicial, PapelQuantitativo transformacao,
                                                  PapelQuantitativo relacaoFinal) {
        exigirNaoNulo(relacaoInicial, "relacaoInicial");
        exigirNaoNulo(transformacao, "transformacao");
        exigirNaoNulo(relacaoFinal, "relacaoFinal");

        int incognitas = 0;
        if (relacaoInicial.ehIncognita()) incognitas++;
        if (transformacao.ehIncognita()) incognitas++;
        if (relacaoFinal.ehIncognita()) incognitas++;

        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (relacaoFinal.ehIncognita()) {
            int calculado = relacaoInicial.valorAtual().valorOuNull() + transformacao.valorAtual().valorOuNull();
            return new ResultadoCalculo(relacaoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "RelacaoFinal = RelacaoInicial + Transformacao",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        if (relacaoInicial.ehIncognita()) {
            int calculado = relacaoFinal.valorAtual().valorOuNull() - transformacao.valorAtual().valorOuNull();
            return new ResultadoCalculo(relacaoInicial, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "RelacaoInicial = RelacaoFinal - Transformacao",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        int calculado = relacaoFinal.valorAtual().valorOuNull() - relacaoInicial.valorAtual().valorOuNull();
        return new ResultadoCalculo(transformacao, new NumeroInteiro(calculado), descreverRelacao(),
                EstadoConsistencia.CONSISTENTE, "Transformacao = RelacaoFinal - RelacaoInicial",
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
