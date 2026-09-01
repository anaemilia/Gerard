package gerard.dominio.campoaditivo;

import gerard.semantica.numero.NumeroInteiro;

/**
 * Relação estrutural formal do esquema Composição de Relações:
 * RelacaoFinal = Relacao1 + Relacao2.
 *
 * Categoria "Relações" de Vergnaud, não "Medidas": os três papéis são
 * números relativos (domínio INTEIROS nos três, sem restrição de sinal em
 * nenhum deles) — Relacao1/Relacao2/RelacaoFinal são comparações relativas
 * entre grandezas, não medidas. Ver RelacaoEstruturalComposicao para a
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
public final class RelacaoEstruturalComposicaoDeRelacoes implements RelacaoEstruturalDiagnosticavel {

    private RelacaoEstruturalComposicaoDeRelacoes() { }

    public static RelacaoEstruturalComposicaoDeRelacoes composicaoDeRelacoes() {
        return new RelacaoEstruturalComposicaoDeRelacoes();
    }

    public String descreverRelacao() { return "RelacaoFinal = Relacao1 + Relacao2"; }

    public EstadoConsistencia verificarConsistencia(PapelQuantitativo relacao1, PapelQuantitativo relacao2,
                                                      PapelQuantitativo relacaoFinal) {
        exigirNaoNulo(relacao1, "relacao1");
        exigirNaoNulo(relacao2, "relacao2");
        exigirNaoNulo(relacaoFinal, "relacaoFinal");
        if (!relacao1.estaPreenchido() || !relacao2.estaPreenchido() || !relacaoFinal.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        int r1 = relacao1.valorAtual().valorOuNull();
        int r2 = relacao2.valorAtual().valorOuNull();
        int rf = relacaoFinal.valorAtual().valorOuNull();
        try {
            return (rf == Math.addExact(r1, r2))
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
    public ResultadoCalculo calcularValorAusente(PapelQuantitativo relacao1, PapelQuantitativo relacao2,
                                                  PapelQuantitativo relacaoFinal) {
        exigirNaoNulo(relacao1, "relacao1");
        exigirNaoNulo(relacao2, "relacao2");
        exigirNaoNulo(relacaoFinal, "relacaoFinal");

        int incognitas = contarIncognitas(relacao1, relacao2, relacaoFinal);
        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (relacaoFinal.ehIncognita()) {
            return resultadoDaSoma(relacaoFinal, relacao1.valorAtual().valorOuNull(),
                    relacao2.valorAtual().valorOuNull(), "RelacaoFinal = Relacao1 + Relacao2");
        }
        if (relacao1.ehIncognita()) {
            return resultadoDaSubtracao(relacao1, relacaoFinal.valorAtual().valorOuNull(),
                    relacao2.valorAtual().valorOuNull(), "Relacao1 = RelacaoFinal - Relacao2");
        }
        return resultadoDaSubtracao(relacao2, relacaoFinal.valorAtual().valorOuNull(),
                relacao1.valorAtual().valorOuNull(), "Relacao2 = RelacaoFinal - Relacao1");
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
     * recalcular RelacaoFinal quando o par necessário (Relacao1 e
     * Relacao2) está totalmente conhecido; só recalcula um dos dois
     * papéis restantes quando RelacaoFinal não está disponível como alvo
     * (porque RelacaoFinal foi o papel alterado, ou porque falta valor no
     * par).
     *
     * Não modifica nenhum papel — mesmo contrato de calcularValorAusente:
     * devolve um ResultadoCalculo; aplicar esse valor é decisão explícita
     * de outra camada, chamando aplicar(...).
     *
     * @param papelAlterado precisa ser exatamente um entre relacao1,
     *        relacao2 ou relacaoFinal (por referência) — o papel que
     *        acabou de mudar
     * @return ResultadoCalculo com o papel recalculado (CONSISTENTE), ou
     *         com EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO se nenhum
     *         dos pares necessários estiver totalmente conhecido —
     *         situação legítima, não uma exceção
     * @throws IllegalArgumentException se papelAlterado não for
     *         exatamente um dos três papéis desta relação
     */
    public ResultadoCalculo recalcularParaConsistencia(PapelQuantitativo relacao1, PapelQuantitativo relacao2,
            PapelQuantitativo relacaoFinal, PapelQuantitativo papelAlterado) {
        exigirNaoNulo(relacao1, "relacao1");
        exigirNaoNulo(relacao2, "relacao2");
        exigirNaoNulo(relacaoFinal, "relacaoFinal");
        exigirNaoNulo(papelAlterado, "papelAlterado");
        if (papelAlterado != relacao1 && papelAlterado != relacao2 && papelAlterado != relacaoFinal) {
            throw new IllegalArgumentException(
                    "papelAlterado precisa ser exatamente um dos três papéis desta relação");
        }

        if (papelAlterado == relacao1) {
            if (relacao1.estaPreenchido() && relacao2.estaPreenchido()) {
                return resultadoDaSoma(relacaoFinal, relacao1.valorAtual().valorOuNull(),
                        relacao2.valorAtual().valorOuNull(), "RelacaoFinal recalculada = Relacao1 + Relacao2");
            }
            if (relacao1.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                return resultadoDaSubtracao(relacao2, relacaoFinal.valorAtual().valorOuNull(),
                        relacao1.valorAtual().valorOuNull(), "Relacao2 recalculada = RelacaoFinal - Relacao1");
            }
        } else if (papelAlterado == relacao2) {
            if (relacao1.estaPreenchido() && relacao2.estaPreenchido()) {
                return resultadoDaSoma(relacaoFinal, relacao1.valorAtual().valorOuNull(),
                        relacao2.valorAtual().valorOuNull(), "RelacaoFinal recalculada = Relacao1 + Relacao2");
            }
            if (relacao2.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                return resultadoDaSubtracao(relacao1, relacaoFinal.valorAtual().valorOuNull(),
                        relacao2.valorAtual().valorOuNull(), "Relacao1 recalculada = RelacaoFinal - Relacao2");
            }
        } else {
            if (relacao1.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                return resultadoDaSubtracao(relacao2, relacaoFinal.valorAtual().valorOuNull(),
                        relacao1.valorAtual().valorOuNull(), "Relacao2 recalculada = RelacaoFinal - Relacao1");
            }
            if (relacao2.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                return resultadoDaSubtracao(relacao1, relacaoFinal.valorAtual().valorOuNull(),
                        relacao2.valorAtual().valorOuNull(), "Relacao1 recalculada = RelacaoFinal - Relacao2");
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
            PapelQuantitativo relacao1, PapelQuantitativo relacao2, PapelQuantitativo relacaoFinal,
            PapelQuantitativo papelAlvo, gerard.semantica.numero.ValorNumerico valorProposto) {
        exigirNaoNulo(relacao1, "relacao1");
        exigirNaoNulo(relacao2, "relacao2");
        exigirNaoNulo(relacaoFinal, "relacaoFinal");
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
        if (!papelAlvo.ehIncognita() || contarIncognitas(relacao1, relacao2, relacaoFinal) != 1) {
            throw new IllegalStateException(
                    "papelAlvo precisa ser exatamente o único papel incógnito entre os três — mesma "
                            + "pré-condição de calcularValorAusente");
        }
        ResultadoCalculo esperado = calcularValorAusente(relacao1, relacao2, relacaoFinal);
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
        Integer invertido = valorDaOperacaoInvertida(relacao1, relacao2, relacaoFinal, papelAlvo);
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
    private Integer valorDaOperacaoInvertida(PapelQuantitativo relacao1, PapelQuantitativo relacao2,
            PapelQuantitativo relacaoFinal, PapelQuantitativo papelAlvo) {
        try {
            if (papelAlvo == relacaoFinal) {
                return Integer.valueOf(Math.subtractExact(
                        relacao1.valorAtual().valorOuNull(), relacao2.valorAtual().valorOuNull()));
            }
            if (papelAlvo == relacao1) {
                return Integer.valueOf(Math.addExact(
                        relacaoFinal.valorAtual().valorOuNull(), relacao2.valorAtual().valorOuNull()));
            }
            return Integer.valueOf(Math.addExact(
                    relacaoFinal.valorAtual().valorOuNull(), relacao1.valorAtual().valorOuNull()));
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
