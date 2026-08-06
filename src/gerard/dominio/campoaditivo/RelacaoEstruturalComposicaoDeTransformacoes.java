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
     * recalcular TransformacaoFinal quando o par necessário (Transformacao1
     * e Transformacao2) está totalmente conhecido; só recalcula uma das
     * duas transformações quando TransformacaoFinal não está disponível
     * como alvo (porque TransformacaoFinal foi o papel alterado, ou porque
     * falta valor no par).
     *
     * Não modifica nenhum papel — mesmo contrato de calcularValorAusente:
     * devolve um ResultadoCalculo; aplicar esse valor é decisão explícita
     * de outra camada, chamando aplicar(...).
     *
     * @param papelAlterado precisa ser exatamente um entre transformacao1,
     *        transformacao2 ou transformacaoFinal (por referência) — o
     *        papel que acabou de mudar
     * @return ResultadoCalculo com o papel recalculado (CONSISTENTE), ou
     *         com EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO se nenhum
     *         dos pares necessários estiver totalmente conhecido —
     *         situação legítima, não uma exceção
     * @throws IllegalArgumentException se papelAlterado não for
     *         exatamente um dos três papéis desta relação
     */
    public ResultadoCalculo recalcularParaConsistencia(PapelQuantitativo transformacao1, PapelQuantitativo transformacao2,
            PapelQuantitativo transformacaoFinal, PapelQuantitativo papelAlterado) {
        exigirNaoNulo(transformacao1, "transformacao1");
        exigirNaoNulo(transformacao2, "transformacao2");
        exigirNaoNulo(transformacaoFinal, "transformacaoFinal");
        exigirNaoNulo(papelAlterado, "papelAlterado");
        if (papelAlterado != transformacao1 && papelAlterado != transformacao2 && papelAlterado != transformacaoFinal) {
            throw new IllegalArgumentException(
                    "papelAlterado precisa ser exatamente um dos três papéis desta relação");
        }

        if (papelAlterado == transformacao1) {
            if (transformacao1.estaPreenchido() && transformacao2.estaPreenchido()) {
                int calculado = transformacao1.valorAtual().valorOuNull() + transformacao2.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacaoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "TransformacaoFinal recalculada = Transformacao1 + Transformacao2",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (transformacao1.estaPreenchido() && transformacaoFinal.estaPreenchido()) {
                int calculado = transformacaoFinal.valorAtual().valorOuNull() - transformacao1.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacao2, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Transformacao2 recalculada = TransformacaoFinal - Transformacao1",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
        } else if (papelAlterado == transformacao2) {
            if (transformacao1.estaPreenchido() && transformacao2.estaPreenchido()) {
                int calculado = transformacao1.valorAtual().valorOuNull() + transformacao2.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacaoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "TransformacaoFinal recalculada = Transformacao1 + Transformacao2",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (transformacao2.estaPreenchido() && transformacaoFinal.estaPreenchido()) {
                int calculado = transformacaoFinal.valorAtual().valorOuNull() - transformacao2.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacao1, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Transformacao1 recalculada = TransformacaoFinal - Transformacao2",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
        } else {
            if (transformacao1.estaPreenchido() && transformacaoFinal.estaPreenchido()) {
                int calculado = transformacaoFinal.valorAtual().valorOuNull() - transformacao1.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacao2, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Transformacao2 recalculada = TransformacaoFinal - Transformacao1",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (transformacao2.estaPreenchido() && transformacaoFinal.estaPreenchido()) {
                int calculado = transformacaoFinal.valorAtual().valorOuNull() - transformacao2.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacao1, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Transformacao1 recalculada = TransformacaoFinal - Transformacao2",
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
            PapelQuantitativo transformacao1, PapelQuantitativo transformacao2, PapelQuantitativo transformacaoFinal,
            PapelQuantitativo papelAlvo, gerard.semantica.numero.ValorNumerico valorProposto) {
        exigirNaoNulo(transformacao1, "transformacao1");
        exigirNaoNulo(transformacao2, "transformacao2");
        exigirNaoNulo(transformacaoFinal, "transformacaoFinal");
        exigirNaoNulo(papelAlvo, "papelAlvo");
        if (valorProposto == null || !valorProposto.ehConhecido()) {
            throw new IllegalArgumentException("valorProposto precisa ser um valor conhecido para ser diagnosticado");
        }
        if (!papelAlvo.aceita(valorProposto)) {
            return java.util.Optional.of(new DiagnosticoErroPapel(TipoErroPapel.VALOR_FORA_DO_DOMINIO,
                    "erro.papel.valorForaDoDominio", "feedback.papel.valorForaDoDominio",
                    "correcao.papel.valorForaDoDominio"));
        }
        ResultadoCalculo esperado = calcularValorAusente(transformacao1, transformacao2, transformacaoFinal);
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
        int invertido = papelAlvo == transformacaoFinal
                ? transformacao1.valorAtual().valorOuNull() - transformacao2.valorAtual().valorOuNull()
                : papelAlvo == transformacao1
                        ? transformacaoFinal.valorAtual().valorOuNull() + transformacao2.valorAtual().valorOuNull()
                        : transformacaoFinal.valorAtual().valorOuNull() + transformacao1.valorAtual().valorOuNull();
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
