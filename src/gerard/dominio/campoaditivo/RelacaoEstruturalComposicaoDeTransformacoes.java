package gerard.dominio.campoaditivo;

import gerard.semantica.numero.NumeroInteiro;

/**
 * Relação estrutural formal do esquema Composição de Transformações:
 * TransformacaoFinal = Transformacao1 + Transformacao2.
 *
 * Categoria "Relações" de Vergnaud, não "Medidas": os três papéis são
 * números relativos (domínio INTEIROS nos três, sem restrição de sinal em
 * nenhum deles) — diferente de Composição de Medidas, cujo Todo e Partes
 * são grandezas não-negativas. Ver RelacaoEstruturalComposicao para a
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
public final class RelacaoEstruturalComposicaoDeTransformacoes {

    private RelacaoEstruturalComposicaoDeTransformacoes() { }

    public static RelacaoEstruturalComposicaoDeTransformacoes composicaoDeTransformacoes() {
        return new RelacaoEstruturalComposicaoDeTransformacoes();
    }

    public String descreverRelacao() { return "TransformacaoFinal = Transformacao1 + Transformacao2"; }

    public EstadoConsistencia verificarConsistencia(PapelQuantitativo transformacao1, PapelQuantitativo transformacao2,
                                                      PapelQuantitativo transformacaoFinal) {
        exigirNaoNulo(transformacao1, "transformacao1");
        exigirNaoNulo(transformacao2, "transformacao2");
        exigirNaoNulo(transformacaoFinal, "transformacaoFinal");
        if (!transformacao1.estaPreenchido() || !transformacao2.estaPreenchido() || !transformacaoFinal.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        int t1 = transformacao1.valorAtual().valorOuNull();
        int t2 = transformacao2.valorAtual().valorOuNull();
        int tf = transformacaoFinal.valorAtual().valorOuNull();
        return (tf == t1 + t2) ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
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
    public ResultadoCalculo calcularValorAusente(PapelQuantitativo transformacao1, PapelQuantitativo transformacao2,
                                                  PapelQuantitativo transformacaoFinal) {
        exigirNaoNulo(transformacao1, "transformacao1");
        exigirNaoNulo(transformacao2, "transformacao2");
        exigirNaoNulo(transformacaoFinal, "transformacaoFinal");

        int incognitas = 0;
        if (transformacao1.ehIncognita()) incognitas++;
        if (transformacao2.ehIncognita()) incognitas++;
        if (transformacaoFinal.ehIncognita()) incognitas++;

        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (transformacaoFinal.ehIncognita()) {
            int calculado = transformacao1.valorAtual().valorOuNull() + transformacao2.valorAtual().valorOuNull();
            return new ResultadoCalculo(transformacaoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "TransformacaoFinal = Transformacao1 + Transformacao2",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        if (transformacao1.ehIncognita()) {
            int calculado = transformacaoFinal.valorAtual().valorOuNull() - transformacao2.valorAtual().valorOuNull();
            return new ResultadoCalculo(transformacao1, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "Transformacao1 = TransformacaoFinal - Transformacao2",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        int calculado = transformacaoFinal.valorAtual().valorOuNull() - transformacao1.valorAtual().valorOuNull();
        return new ResultadoCalculo(transformacao2, new NumeroInteiro(calculado), descreverRelacao(),
                EstadoConsistencia.CONSISTENTE, "Transformacao2 = TransformacaoFinal - Transformacao1",
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
