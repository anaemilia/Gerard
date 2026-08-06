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
     * recalcular RelacaoFinal quando o par necessário (RelacaoInicial e
     * Transformacao) está totalmente conhecido; só recalcula um dos dois
     * papéis restantes quando RelacaoFinal não está disponível como alvo
     * (porque RelacaoFinal foi o papel alterado, ou porque falta valor no
     * par).
     *
     * Não modifica nenhum papel — mesmo contrato de calcularValorAusente:
     * devolve um ResultadoCalculo; aplicar esse valor é decisão explícita
     * de outra camada, chamando aplicar(...).
     *
     * @param papelAlterado precisa ser exatamente um entre relacaoInicial,
     *        transformacao ou relacaoFinal (por referência) — o papel que
     *        acabou de mudar
     * @return ResultadoCalculo com o papel recalculado (CONSISTENTE), ou
     *         com EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO se nenhum
     *         dos pares necessários estiver totalmente conhecido —
     *         situação legítima, não uma exceção
     * @throws IllegalArgumentException se papelAlterado não for
     *         exatamente um dos três papéis desta relação
     */
    public ResultadoCalculo recalcularParaConsistencia(PapelQuantitativo relacaoInicial, PapelQuantitativo transformacao,
            PapelQuantitativo relacaoFinal, PapelQuantitativo papelAlterado) {
        exigirNaoNulo(relacaoInicial, "relacaoInicial");
        exigirNaoNulo(transformacao, "transformacao");
        exigirNaoNulo(relacaoFinal, "relacaoFinal");
        exigirNaoNulo(papelAlterado, "papelAlterado");
        if (papelAlterado != relacaoInicial && papelAlterado != transformacao && papelAlterado != relacaoFinal) {
            throw new IllegalArgumentException(
                    "papelAlterado precisa ser exatamente um dos três papéis desta relação");
        }

        if (papelAlterado == relacaoInicial) {
            if (relacaoInicial.estaPreenchido() && transformacao.estaPreenchido()) {
                int calculado = relacaoInicial.valorAtual().valorOuNull() + transformacao.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacaoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "RelacaoFinal recalculada = RelacaoInicial + Transformacao",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (relacaoInicial.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                int calculado = relacaoFinal.valorAtual().valorOuNull() - relacaoInicial.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacao, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Transformacao recalculada = RelacaoFinal - RelacaoInicial",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
        } else if (papelAlterado == transformacao) {
            if (relacaoInicial.estaPreenchido() && transformacao.estaPreenchido()) {
                int calculado = relacaoInicial.valorAtual().valorOuNull() + transformacao.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacaoFinal, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "RelacaoFinal recalculada = RelacaoInicial + Transformacao",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (transformacao.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                int calculado = relacaoFinal.valorAtual().valorOuNull() - transformacao.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacaoInicial, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "RelacaoInicial recalculada = RelacaoFinal - Transformacao",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
        } else {
            if (relacaoInicial.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                int calculado = relacaoFinal.valorAtual().valorOuNull() - relacaoInicial.valorAtual().valorOuNull();
                return new ResultadoCalculo(transformacao, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Transformacao recalculada = RelacaoFinal - RelacaoInicial",
                        OrigemAcao.ORIGEM_SISTEMA);
            }
            if (transformacao.estaPreenchido() && relacaoFinal.estaPreenchido()) {
                int calculado = relacaoFinal.valorAtual().valorOuNull() - transformacao.valorAtual().valorOuNull();
                return new ResultadoCalculo(relacaoInicial, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "RelacaoInicial recalculada = RelacaoFinal - Transformacao",
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
            PapelQuantitativo relacaoInicial, PapelQuantitativo transformacao, PapelQuantitativo relacaoFinal,
            PapelQuantitativo papelAlvo, gerard.semantica.numero.ValorNumerico valorProposto) {
        exigirNaoNulo(relacaoInicial, "relacaoInicial");
        exigirNaoNulo(transformacao, "transformacao");
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
        ResultadoCalculo esperado = calcularValorAusente(relacaoInicial, transformacao, relacaoFinal);
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
                ? relacaoInicial.valorAtual().valorOuNull() - transformacao.valorAtual().valorOuNull()
                : papelAlvo == relacaoInicial
                        ? relacaoFinal.valorAtual().valorOuNull() + transformacao.valorAtual().valorOuNull()
                        : relacaoFinal.valorAtual().valorOuNull() + relacaoInicial.valorAtual().valorOuNull();
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
