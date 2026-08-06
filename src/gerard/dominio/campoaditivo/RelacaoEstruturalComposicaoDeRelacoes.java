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
public final class RelacaoEstruturalComposicaoDeRelacoes {

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
        return (rf == r1 + r2) ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
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

        int incognitas = 0;
        if (relacao1.ehIncognita()) incognitas++;
        if (relacao2.ehIncognita()) incognitas++;
        if (relacaoFinal.ehIncognita()) incognitas++;

        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (relacaoFinal.ehIncognita()) {
            int calculado = relacao1.valorAtual().valorOuNull() + relacao2.valorAtual().valorOuNull();
            return new ResultadoCalculo(relacaoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "RelacaoFinal = Relacao1 + Relacao2",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        if (relacao1.ehIncognita()) {
            int calculado = relacaoFinal.valorAtual().valorOuNull() - relacao2.valorAtual().valorOuNull();
            return new ResultadoCalculo(relacao1, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "Relacao1 = RelacaoFinal - Relacao2",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        int calculado = relacaoFinal.valorAtual().valorOuNull() - relacao1.valorAtual().valorOuNull();
        return new ResultadoCalculo(relacao2, new NumeroInteiro(calculado), descreverRelacao(),
                EstadoConsistencia.CONSISTENTE, "Relacao2 = RelacaoFinal - Relacao1",
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
                int calculado = relacao1.valorAtual().valorOuNull() + relacao2.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacaoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "RelacaoFinal recalculada = Relacao1 + Relacao2",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (relacao1.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                int calculado = relacaoFinal.valorAtual().valorOuNull() - relacao1.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacao2, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Relacao2 recalculada = RelacaoFinal - Relacao1",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
        } else if (papelAlterado == relacao2) {
            if (relacao1.estaPreenchido() && relacao2.estaPreenchido()) {
                int calculado = relacao1.valorAtual().valorOuNull() + relacao2.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacaoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "RelacaoFinal recalculada = Relacao1 + Relacao2",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (relacao2.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                int calculado = relacaoFinal.valorAtual().valorOuNull() - relacao2.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacao1, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Relacao1 recalculada = RelacaoFinal - Relacao2",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
        } else {
            if (relacao1.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                int calculado = relacaoFinal.valorAtual().valorOuNull() - relacao1.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacao2, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Relacao2 recalculada = RelacaoFinal - Relacao1",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (relacao2.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                int calculado = relacaoFinal.valorAtual().valorOuNull() - relacao2.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacao1, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Relacao1 recalculada = RelacaoFinal - Relacao2",
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
        ResultadoCalculo esperado = calcularValorAusente(relacao1, relacao2, relacaoFinal);
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
        int invertido = papelAlvo == relacaoFinal
                ? relacao1.valorAtual().valorOuNull() - relacao2.valorAtual().valorOuNull()
                : papelAlvo == relacao1
                        ? relacaoFinal.valorAtual().valorOuNull() + relacao2.valorAtual().valorOuNull()
                        : relacaoFinal.valorAtual().valorOuNull() + relacao1.valorAtual().valorOuNull();
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
