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
     * Recalcula um papel já conhecido para preservar a consistência da
     * relação aditiva quando outro papel muda depois que os três já
     * estavam preenchidos — situação distinta da que calcularValorAusente
     * resolve (lá, exatamente um papel está incógnito; aqui, os três já
     * têm valor, e um deles acabou de ser alterado, o que pode invalidar
     * a soma se nenhum dos outros dois se ajustar). Cenário comum durante
     * interação real: o sujeito arrasta um valor já posicionado num
     * diagrama já completo.
     *
     * Réplica, no piloto, do algoritmo genérico que já existia em
     * EstadoSemanticoCompartilhado.resolverRelacaoAditiva (2026-08-06, ver
     * RELATORIO_INVESTIGACAO_FASE_B2_COMPLETA_2026-08-06.md): nunca
     * recalcula o próprio papelAlterado; entre os outros dois, prioriza
     * recalcular EstadoFinal quando o par necessário (EstadoInicial e
     * Transformacao) está totalmente conhecido; só recalcula um dos dois
     * papéis restantes quando EstadoFinal não está disponível como alvo
     * (porque EstadoFinal foi o papel alterado, ou porque falta valor no
     * par).
     *
     * Não modifica nenhum papel — mesmo contrato de calcularValorAusente:
     * devolve um ResultadoCalculo; aplicar esse valor é decisão explícita
     * de outra camada, chamando aplicar(...).
     *
     * @param papelAlterado precisa ser exatamente um entre estadoInicial,
     *        transformacao ou estadoFinal (por referência) — o papel que
     *        acabou de mudar
     * @return ResultadoCalculo com o papel recalculado (CONSISTENTE), ou
     *         com EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO se nenhum
     *         dos pares necessários estiver totalmente conhecido —
     *         situação legítima, não uma exceção
     * @throws IllegalArgumentException se papelAlterado não for
     *         exatamente um dos três papéis desta relação
     */
    public ResultadoCalculo recalcularParaConsistencia(PapelQuantitativo estadoInicial, PapelQuantitativo transformacao,
            PapelQuantitativo estadoFinal, PapelQuantitativo papelAlterado) {
        exigirNaoNulo(estadoInicial, "estadoInicial");
        exigirNaoNulo(transformacao, "transformacao");
        exigirNaoNulo(estadoFinal, "estadoFinal");
        exigirNaoNulo(papelAlterado, "papelAlterado");
        if (papelAlterado != estadoInicial && papelAlterado != transformacao && papelAlterado != estadoFinal) {
            throw new IllegalArgumentException(
                    "papelAlterado precisa ser exatamente um dos três papéis desta relação");
        }

        if (papelAlterado == estadoInicial) {
            if (estadoInicial.estaPreenchido() && transformacao.estaPreenchido()) {
                int calculado = estadoInicial.valorAtual().valorOuNull() + transformacao.valorAtual().valorOuNull();
                return new ResultadoCalculo(estadoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "EstadoFinal recalculado = EstadoInicial + Transformacao",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (estadoInicial.estaPreenchido() && estadoFinal.estaPreenchido()) {
                int calculado = estadoFinal.valorAtual().valorOuNull() - estadoInicial.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacao, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Transformacao recalculada = EstadoFinal - EstadoInicial",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
        } else if (papelAlterado == transformacao) {
            if (estadoInicial.estaPreenchido() && transformacao.estaPreenchido()) {
                int calculado = estadoInicial.valorAtual().valorOuNull() + transformacao.valorAtual().valorOuNull();
                return new ResultadoCalculo(estadoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "EstadoFinal recalculado = EstadoInicial + Transformacao",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (transformacao.estaPreenchido() && estadoFinal.estaPreenchido()) {
                int calculado = estadoFinal.valorAtual().valorOuNull() - transformacao.valorAtual().valorOuNull();
                return new ResultadoCalculo(estadoInicial, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "EstadoInicial recalculado = EstadoFinal - Transformacao",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
        } else {
            if (estadoInicial.estaPreenchido() && estadoFinal.estaPreenchido()) {
                int calculado = estadoFinal.valorAtual().valorOuNull() - estadoInicial.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacao, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Transformacao recalculada = EstadoFinal - EstadoInicial",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (transformacao.estaPreenchido() && estadoFinal.estaPreenchido()) {
                int calculado = estadoFinal.valorAtual().valorOuNull() - transformacao.valorAtual().valorOuNull();
                return new ResultadoCalculo(estadoInicial, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "EstadoInicial recalculado = EstadoFinal - Transformacao",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
        }

        return new ResultadoCalculo(null, null, descreverRelacao(), EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                "Nenhum dos papéis não alterados tem o par necessário totalmente conhecido para recalcular.",
                OrigemAcao.ORIGEM_SISTEMA);
    }

    /**
     * Diagnostica um valor PROPOSTO (ex.: o que o estudante digitou) para o
     * papelAlvo, contra o valor correto calculado a partir dos outros dois
     * papéis — distinção que calcularValorAusente sozinho não faz (ele só
     * preenche o que falta; nunca avalia se um valor já presente está certo).
     *
     * Optional.empty() significa correto — não "não avaliado" (essa
     * ambiguidade não existe aqui: pré-condição não satisfeita lança
     * exceção, mesmo estilo de aplicar(...)). Quando incorreto, tenta
     * reconhecer o padrão "operação invertida" (ex.: subtraiu quando devia
     * somar) antes de cair no diagnóstico genérico — ver TipoErroPapel.
     *
     * @throws IllegalStateException se papelAlvo não for exatamente o único
     *         papel incógnito entre os três — mesma pré-condição de
     *         calcularValorAusente, violação de contrato do chamador
     */
    public java.util.Optional<DiagnosticoErroPapel> diagnosticarValorProposto(
            PapelQuantitativo estadoInicial, PapelQuantitativo transformacao, PapelQuantitativo estadoFinal,
            PapelQuantitativo papelAlvo, gerard.semantica.numero.ValorNumerico valorProposto) {
        exigirNaoNulo(estadoInicial, "estadoInicial");
        exigirNaoNulo(transformacao, "transformacao");
        exigirNaoNulo(estadoFinal, "estadoFinal");
        exigirNaoNulo(papelAlvo, "papelAlvo");
        if (valorProposto == null || !valorProposto.ehConhecido()) {
            throw new IllegalArgumentException("valorProposto precisa ser um valor conhecido para ser diagnosticado");
        }
        if (!papelAlvo.aceita(valorProposto)) {
            return java.util.Optional.of(new DiagnosticoErroPapel(TipoErroPapel.VALOR_FORA_DO_DOMINIO,
                    "erro.papel.valorForaDoDominio", "feedback.papel.valorForaDoDominio",
                    "correcao.papel.valorForaDoDominio"));
        }
        ResultadoCalculo esperado = calcularValorAusente(estadoInicial, transformacao, estadoFinal);
        if (!esperado.temValorCalculavel() || esperado.getPapelCalculado() != papelAlvo) {
            throw new IllegalStateException(
                    "papelAlvo precisa ser exatamente o único papel incógnito entre os três — mesma "
                            + "pré-condição de calcularValorAusente (estado: " + esperado.getEstadoConsistencia() + ")");
        }
        int correto = esperado.getValorCalculado().valorOuNull();
        int proposto = valorProposto.valorOuNull();
        if (proposto == correto) {
            return java.util.Optional.empty();
        }
        int invertido = papelAlvo == estadoFinal
                ? estadoInicial.valorAtual().valorOuNull() - transformacao.valorAtual().valorOuNull()
                : papelAlvo == estadoInicial
                        ? estadoFinal.valorAtual().valorOuNull() + transformacao.valorAtual().valorOuNull()
                        : estadoFinal.valorAtual().valorOuNull() + estadoInicial.valorAtual().valorOuNull();
        if (proposto == invertido) {
            return java.util.Optional.of(new DiagnosticoErroPapel(TipoErroPapel.OPERACAO_INVERTIDA,
                    "erro.papel.operacaoInvertida", "feedback.papel.operacaoInvertida",
                    "correcao.papel.operacaoInvertida"));
        }
        return java.util.Optional.of(new DiagnosticoErroPapel(TipoErroPapel.VALOR_INCORRETO,
                "erro.papel.valorIncorreto", "feedback.papel.valorIncorreto", "correcao.papel.valorIncorreto"));
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
