package gerard.dominio.campoaditivo;

import gerard.semantica.numero.NumeroInteiro;

/**
 * Relação estrutural formal do esquema Comparação de Medidas:
 * Referendo = Referido + ValorRelativo.
 *
 * DISTINÇÃO CONCEITUAL OBRIGATÓRIA (ver relatório técnico do baseline v2,
 * seção "Situações, Invariantes Operatórios e Representações" — tripé
 * C=(S,I,R) de Vergnaud): esta classe NÃO representa um invariante
 * operatório. Um invariante operatório é uma proposição sobre o mundo
 * mobilizada pelo sujeito durante sua atividade — pode emergir
 * implicitamente por meio de ações, ser inferido a partir de uma sequência
 * de ações, ou ser verbalizado pelo estudante em qualquer momento da
 * tentativa. Não pertence a Referido, não pertence a Referendo, não
 * pertence a nenhum objeto gráfico específico, e não deve ser modelado
 * como uma regra formal possuída por um elemento do diagrama.
 *
 * O que esta classe modela é a organização matemática formal que relaciona
 * os papéis Referido/ValorRelativo/Referendo dentro de UMA representação —
 * pertence ao R do tripé (representações), nunca ao I (invariantes
 * operatórios). O invariante operatório em si não é e não pode ser
 * modelado por nenhuma classe deste pacote: ele é mobilizado pelo
 * estudante, não pelo sistema.
 *
 * Objeto coordenador de escopo fechado (só os três papéis deste esquema):
 * a regra cruza três conceitos, então não pertence a nenhum
 * PapelQuantitativo isolado — a mesma regra de revisão da skill
 * KnowledgeLocalityPrinciple que já justificava RelacaoEstruturalComposicao
 * e RelacaoEstruturalTransformacao continua valendo.
 *
 * calcularValorAusente NUNCA modifica o papel calculado — só devolve um
 * ResultadoCalculo com origem ORIGEM_SISTEMA. Aplicar esse valor ao papel
 * (e só então gerar o evento correspondente) é uma decisão explícita de
 * outra camada, tomada chamando aplicar(...) — nunca automática.
 */
public final class RelacaoEstruturalComparacao {

    private RelacaoEstruturalComparacao() { }

    public static RelacaoEstruturalComparacao comparacaoDeMedidas() {
        return new RelacaoEstruturalComparacao();
    }

    public String descreverRelacao() { return "Referendo = Referido + ValorRelativo"; }

    public EstadoConsistencia verificarConsistencia(PapelQuantitativo referido, PapelQuantitativo valorRelativo,
                                                      PapelQuantitativo referendo) {
        exigirNaoNulo(referido, "referido");
        exigirNaoNulo(valorRelativo, "valorRelativo");
        exigirNaoNulo(referendo, "referendo");
        if (!referido.estaPreenchido() || !valorRelativo.estaPreenchido() || !referendo.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        int rd = referido.valorAtual().valorOuNull();
        int vr = valorRelativo.valorAtual().valorOuNull();
        int rn = referendo.valorAtual().valorOuNull();
        try {
            return (rn == Math.addExact(rd, vr))
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
    public ResultadoCalculo calcularValorAusente(PapelQuantitativo referido, PapelQuantitativo valorRelativo,
                                                  PapelQuantitativo referendo) {
        exigirNaoNulo(referido, "referido");
        exigirNaoNulo(valorRelativo, "valorRelativo");
        exigirNaoNulo(referendo, "referendo");

        int incognitas = contarIncognitas(referido, valorRelativo, referendo);
        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (referendo.ehIncognita()) {
            return resultadoDaSoma(referendo, referido.valorAtual().valorOuNull(),
                    valorRelativo.valorAtual().valorOuNull(), "Referendo = Referido + ValorRelativo");
        }
        if (referido.ehIncognita()) {
            return resultadoDaSubtracao(referido, referendo.valorAtual().valorOuNull(),
                    valorRelativo.valorAtual().valorOuNull(), "Referido = Referendo - ValorRelativo");
        }
        return resultadoDaSubtracao(valorRelativo, referendo.valorAtual().valorOuNull(),
                referido.valorAtual().valorOuNull(), "ValorRelativo = Referendo - Referido");
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
     * recalcular Referendo quando o par necessário (Referido e
     * ValorRelativo) está totalmente conhecido; só recalcula um dos dois
     * papéis restantes quando Referendo não está disponível como alvo
     * (porque Referendo foi o papel alterado, ou porque falta valor no
     * par).
     *
     * Não modifica nenhum papel — mesmo contrato de calcularValorAusente:
     * devolve um ResultadoCalculo; aplicar esse valor é decisão explícita
     * de outra camada, chamando aplicar(...).
     *
     * @param papelAlterado precisa ser exatamente um entre referido,
     *        valorRelativo ou referendo (por referência) — o papel que
     *        acabou de mudar
     * @return ResultadoCalculo com o papel recalculado (CONSISTENTE), ou
     *         com EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO se nenhum
     *         dos pares necessários estiver totalmente conhecido —
     *         situação legítima, não uma exceção
     * @throws IllegalArgumentException se papelAlterado não for
     *         exatamente um dos três papéis desta relação
     */
    public ResultadoCalculo recalcularParaConsistencia(PapelQuantitativo referido, PapelQuantitativo valorRelativo,
            PapelQuantitativo referendo, PapelQuantitativo papelAlterado) {
        exigirNaoNulo(referido, "referido");
        exigirNaoNulo(valorRelativo, "valorRelativo");
        exigirNaoNulo(referendo, "referendo");
        exigirNaoNulo(papelAlterado, "papelAlterado");
        if (papelAlterado != referido && papelAlterado != valorRelativo && papelAlterado != referendo) {
            throw new IllegalArgumentException(
                    "papelAlterado precisa ser exatamente um dos três papéis desta relação");
        }

        if (papelAlterado == referido) {
            if (referido.estaPreenchido() && valorRelativo.estaPreenchido()) {
                return resultadoDaSoma(referendo, referido.valorAtual().valorOuNull(),
                        valorRelativo.valorAtual().valorOuNull(),
                        "Referendo recalculado = Referido + ValorRelativo");
            }
            if (referido.estaPreenchido() && referendo.estaPreenchido()) {
                return resultadoDaSubtracao(valorRelativo, referendo.valorAtual().valorOuNull(),
                        referido.valorAtual().valorOuNull(),
                        "ValorRelativo recalculado = Referendo - Referido");
            }
        } else if (papelAlterado == valorRelativo) {
            if (referido.estaPreenchido() && valorRelativo.estaPreenchido()) {
                return resultadoDaSoma(referendo, referido.valorAtual().valorOuNull(),
                        valorRelativo.valorAtual().valorOuNull(),
                        "Referendo recalculado = Referido + ValorRelativo");
            }
            if (valorRelativo.estaPreenchido() && referendo.estaPreenchido()) {
                return resultadoDaSubtracao(referido, referendo.valorAtual().valorOuNull(),
                        valorRelativo.valorAtual().valorOuNull(),
                        "Referido recalculado = Referendo - ValorRelativo");
            }
        } else {
            if (referido.estaPreenchido() && referendo.estaPreenchido()) {
                return resultadoDaSubtracao(valorRelativo, referendo.valorAtual().valorOuNull(),
                        referido.valorAtual().valorOuNull(),
                        "ValorRelativo recalculado = Referendo - Referido");
            }
            if (valorRelativo.estaPreenchido() && referendo.estaPreenchido()) {
                return resultadoDaSubtracao(referido, referendo.valorAtual().valorOuNull(),
                        valorRelativo.valorAtual().valorOuNull(),
                        "Referido recalculado = Referendo - ValorRelativo");
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
            PapelQuantitativo referido, PapelQuantitativo valorRelativo, PapelQuantitativo referendo,
            PapelQuantitativo papelAlvo, gerard.semantica.numero.ValorNumerico valorProposto) {
        exigirNaoNulo(referido, "referido");
        exigirNaoNulo(valorRelativo, "valorRelativo");
        exigirNaoNulo(referendo, "referendo");
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
        if (!papelAlvo.ehIncognita() || contarIncognitas(referido, valorRelativo, referendo) != 1) {
            throw new IllegalStateException(
                    "papelAlvo precisa ser exatamente o único papel incógnito entre os três — mesma "
                            + "pré-condição de calcularValorAusente");
        }
        ResultadoCalculo esperado = calcularValorAusente(referido, valorRelativo, referendo);
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
        Integer invertido = valorDaOperacaoInvertida(referido, valorRelativo, referendo, papelAlvo);
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
    private Integer valorDaOperacaoInvertida(PapelQuantitativo referido, PapelQuantitativo valorRelativo,
            PapelQuantitativo referendo, PapelQuantitativo papelAlvo) {
        try {
            if (papelAlvo == referendo) {
                return Integer.valueOf(Math.subtractExact(
                        referido.valorAtual().valorOuNull(), valorRelativo.valorAtual().valorOuNull()));
            }
            if (papelAlvo == referido) {
                return Integer.valueOf(Math.addExact(
                        referendo.valorAtual().valorOuNull(), valorRelativo.valorAtual().valorOuNull()));
            }
            return Integer.valueOf(Math.addExact(
                    referendo.valorAtual().valorOuNull(), referido.valorAtual().valorOuNull()));
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
