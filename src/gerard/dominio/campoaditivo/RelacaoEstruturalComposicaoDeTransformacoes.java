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
public final class RelacaoEstruturalComposicaoDeTransformacoes implements RelacaoEstruturalDiagnosticavel {

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
        try {
            return (tf == Math.addExact(t1, t2))
                    ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
        } catch (ArithmeticException estouro) {
            // A soma não é representável como int — não dá para afirmar nem
            // consistência nem inconsistência sem inventar um número errado.
            return EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO;
        }
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

        int incognitas = contarIncognitas(transformacao1, transformacao2, transformacaoFinal);
        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (transformacaoFinal.ehIncognita()) {
            return resultadoDaSoma(transformacaoFinal, transformacao1.valorAtual().valorOuNull(),
                    transformacao2.valorAtual().valorOuNull(),
                    "TransformacaoFinal = Transformacao1 + Transformacao2");
        }
        if (transformacao1.ehIncognita()) {
            return resultadoDaSubtracao(transformacao1, transformacaoFinal.valorAtual().valorOuNull(),
                    transformacao2.valorAtual().valorOuNull(),
                    "Transformacao1 = TransformacaoFinal - Transformacao2");
        }
        return resultadoDaSubtracao(transformacao2, transformacaoFinal.valorAtual().valorOuNull(),
                transformacao1.valorAtual().valorOuNull(),
                "Transformacao2 = TransformacaoFinal - Transformacao1");
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
                return resultadoDaSoma(transformacaoFinal, transformacao1.valorAtual().valorOuNull(),
                        transformacao2.valorAtual().valorOuNull(),
                        "TransformacaoFinal recalculada = Transformacao1 + Transformacao2");
            }
            if (transformacao1.estaPreenchido() && transformacaoFinal.estaPreenchido()) {
                return resultadoDaSubtracao(transformacao2, transformacaoFinal.valorAtual().valorOuNull(),
                        transformacao1.valorAtual().valorOuNull(),
                        "Transformacao2 recalculada = TransformacaoFinal - Transformacao1");
            }
        } else if (papelAlterado == transformacao2) {
            if (transformacao1.estaPreenchido() && transformacao2.estaPreenchido()) {
                return resultadoDaSoma(transformacaoFinal, transformacao1.valorAtual().valorOuNull(),
                        transformacao2.valorAtual().valorOuNull(),
                        "TransformacaoFinal recalculada = Transformacao1 + Transformacao2");
            }
            if (transformacao2.estaPreenchido() && transformacaoFinal.estaPreenchido()) {
                return resultadoDaSubtracao(transformacao1, transformacaoFinal.valorAtual().valorOuNull(),
                        transformacao2.valorAtual().valorOuNull(),
                        "Transformacao1 recalculada = TransformacaoFinal - Transformacao2");
            }
        } else {
            if (transformacao1.estaPreenchido() && transformacaoFinal.estaPreenchido()) {
                return resultadoDaSubtracao(transformacao2, transformacaoFinal.valorAtual().valorOuNull(),
                        transformacao1.valorAtual().valorOuNull(),
                        "Transformacao2 recalculada = TransformacaoFinal - Transformacao1");
            }
            if (transformacao2.estaPreenchido() && transformacaoFinal.estaPreenchido()) {
                return resultadoDaSubtracao(transformacao1, transformacaoFinal.valorAtual().valorOuNull(),
                        transformacao2.valorAtual().valorOuNull(),
                        "Transformacao1 recalculada = TransformacaoFinal - Transformacao2");
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
        // Pré-condição verificada diretamente (não pelo resultado de
        // calcularValorAusente): desde a guarda de estouro, esse método também
        // devolve NAO_RESOLVIVEL quando a conta não é representável, o que não
        // é violação de contrato do chamador e não deve virar a mesma exceção.
        if (!papelAlvo.ehIncognita()
                || contarIncognitas(transformacao1, transformacao2, transformacaoFinal) != 1) {
            throw new IllegalStateException(
                    "papelAlvo precisa ser exatamente o único papel incógnito entre os três — mesma "
                            + "pré-condição de calcularValorAusente");
        }
        ResultadoCalculo esperado = calcularValorAusente(transformacao1, transformacao2, transformacaoFinal);
        if (!esperado.temValorCalculavel()) {
            throw new ArithmeticException(
                    "O valor correto para o papelAlvo não é representável como inteiro; "
                            + "não há contra o que diagnosticar a proposta.");
        }
        int correto = esperado.getValorCalculado().valorOuNull();
        int proposto = valorProposto.valorOuNull();
        if (proposto == correto) {
            return java.util.Optional.empty();
        }
        Integer invertido = valorDaOperacaoInvertida(
                transformacao1, transformacao2, transformacaoFinal, papelAlvo);
        if (invertido != null && proposto == invertido.intValue()) {
            return java.util.Optional.of(new DiagnosticoErroPapel(TipoErroPapel.OPERACAO_INVERTIDA,
                    "erro.papel.operacaoInvertida", "feedback.papel.operacaoInvertida",
                    "correcao.papel.operacaoInvertida"));
        }
        return java.util.Optional.of(new DiagnosticoErroPapel(TipoErroPapel.VALOR_INCORRETO,
                "erro.papel.valorIncorreto", "feedback.papel.valorIncorreto", "correcao.papel.valorIncorreto"));
    }

    /**
     * Valor que a operação inversa da correta produziria, ou null quando essa
     * conta não é representável como int — nesse caso o padrão "operação
     * invertida" simplesmente não pode ser reconhecido, e o diagnóstico cai no
     * genérico VALOR_INCORRETO em vez de comparar contra um número estourado.
     */
    private Integer valorDaOperacaoInvertida(PapelQuantitativo transformacao1, PapelQuantitativo transformacao2,
            PapelQuantitativo transformacaoFinal, PapelQuantitativo papelAlvo) {
        try {
            if (papelAlvo == transformacaoFinal) {
                return Integer.valueOf(Math.subtractExact(
                        transformacao1.valorAtual().valorOuNull(), transformacao2.valorAtual().valorOuNull()));
            }
            if (papelAlvo == transformacao1) {
                return Integer.valueOf(Math.addExact(
                        transformacaoFinal.valorAtual().valorOuNull(), transformacao2.valorAtual().valorOuNull()));
            }
            return Integer.valueOf(Math.addExact(
                    transformacaoFinal.valorAtual().valorOuNull(), transformacao1.valorAtual().valorOuNull()));
        } catch (ArithmeticException estouro) {
            return null;
        }
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

    /**
     * Soma protegida contra estouro de int. Antes desta guarda (2026-08-06), o
     * cálculo devolvia silenciosamente um número errado — 2000000000 +
     * 2000000000 dava -294967296, reportado como CONSISTENTE. Um valor não
     * representável não é erro do estudante nem violação de contrato do
     * chamador: é um estado legítimo a comunicar, então vira
     * NAO_RESOLVIVEL_NESTE_ESTADO, o mesmo estado que calcularValorAusente já
     * usa quando não há uma incógnita única a resolver.
     */
    private ResultadoCalculo resultadoDaSoma(PapelQuantitativo alvo, int a, int b, String explicacao) {
        try {
            return new ResultadoCalculo(alvo, new NumeroInteiro(Math.addExact(a, b)), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, explicacao, OrigemAcao.ORIGEM_SISTEMA);
        } catch (ArithmeticException estouro) {
            return resultadoNaoRepresentavel();
        }
    }

    /** Subtração protegida contra estouro de int — ver resultadoDaSoma. */
    private ResultadoCalculo resultadoDaSubtracao(PapelQuantitativo alvo, int a, int b, String explicacao) {
        try {
            return new ResultadoCalculo(alvo, new NumeroInteiro(Math.subtractExact(a, b)), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, explicacao, OrigemAcao.ORIGEM_SISTEMA);
        } catch (ArithmeticException estouro) {
            return resultadoNaoRepresentavel();
        }
    }

    private ResultadoCalculo resultadoNaoRepresentavel() {
        return new ResultadoCalculo(null, null, descreverRelacao(),
                EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                "O valor resultante ultrapassa o intervalo representável de um número inteiro; "
                        + "nenhum valor é calculado, em vez de devolver um resultado estourado.",
                OrigemAcao.ORIGEM_SISTEMA);
    }

    private static int contarIncognitas(PapelQuantitativo a, PapelQuantitativo b, PapelQuantitativo c) {
        int incognitas = 0;
        if (a.ehIncognita()) incognitas++;
        if (b.ehIncognita()) incognitas++;
        if (c.ehIncognita()) incognitas++;
        return incognitas;
    }

    private static void exigirNaoNulo(PapelQuantitativo papel, String nomeParametro) {
        if (papel == null) {
            throw new IllegalArgumentException(nomeParametro + " não pode ser nulo — violação de contrato de programação");
        }
    }
}
