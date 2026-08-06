package gerard.dominio.campoaditivo;

import gerard.semantica.numero.NumeroInteiro;

/**
 * Relação estrutural formal do esquema Composição de Medidas:
 * Todo = Parte1 + Parte2.
 *
 * DISTINÇÃO CONCEITUAL OBRIGATÓRIA (ver relatório técnico do baseline v2,
 * seção "Situações, Invariantes Operatórios e Representações" — tripé
 * C=(S,I,R) de Vergnaud): esta classe NÃO representa um invariante
 * operatório. Um invariante operatório é uma proposição sobre o mundo
 * mobilizada pelo sujeito durante sua atividade — pode emergir
 * implicitamente por meio de ações, ser inferido a partir de uma sequência
 * de ações, ou ser verbalizado pelo estudante em qualquer momento da
 * tentativa. Não pertence a Parte, não pertence a Todo, não pertence a
 * nenhum objeto gráfico específico, e não deve ser modelado como uma regra
 * formal possuída por um elemento do diagrama.
 *
 * O que esta classe modela é a organização matemática formal que relaciona
 * os papéis Parte1/Parte2/Todo dentro de UMA representação — pertence ao R
 * do tripé (representações), nunca ao I (invariantes operatórios). O
 * invariante operatório em si — a compreensão de que "juntar duas
 * quantidades produz uma terceira maior" — não é e não pode ser modelado
 * por nenhuma classe deste pacote: ele é mobilizado pelo estudante, não
 * pelo sistema.
 *
 * Objeto coordenador de escopo fechado (só os três papéis deste esquema):
 * a regra cruza três conceitos, então não pertence a nenhum
 * PapelQuantitativo isolado — a mesma regra de revisão da skill
 * KnowledgeLocalityPrinciple que já justificava esta classe antes de ser
 * renomeada continua valendo.
 */
public final class RelacaoEstruturalComposicao {

    private RelacaoEstruturalComposicao() { }

    public static RelacaoEstruturalComposicao composicaoDeMedidas() {
        return new RelacaoEstruturalComposicao();
    }

    /** Descreve a relação estrutural em notação simbólica. Não é uma verbalização de invariante operatório. */
    public String descreverRelacao() { return "Todo = Parte1 + Parte2"; }

    /**
     * Avalia a relação estrutural formal contra os três papéis.
     * REPRESENTACAO_INCOMPLETA quando nem todos estão preenchidos ainda —
     * isso é o estado normal de uma atividade em andamento, não um erro.
     */
    public EstadoConsistencia verificarConsistencia(PapelQuantitativo parte1, PapelQuantitativo parte2,
                                                      PapelQuantitativo todo) {
        exigirNaoNulo(parte1, "parte1");
        exigirNaoNulo(parte2, "parte2");
        exigirNaoNulo(todo, "todo");
        if (!parte1.estaPreenchido() || !parte2.estaPreenchido() || !todo.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        int p1 = parte1.valorAtual().valorOuNull();
        int p2 = parte2.valorAtual().valorOuNull();
        int t = todo.valorAtual().valorOuNull();
        return (t == p1 + p2) ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
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
    public ResultadoCalculo calcularValorAusente(PapelQuantitativo parte1, PapelQuantitativo parte2,
                                                  PapelQuantitativo todo) {
        exigirNaoNulo(parte1, "parte1");
        exigirNaoNulo(parte2, "parte2");
        exigirNaoNulo(todo, "todo");

        int incognitas = 0;
        if (parte1.ehIncognita()) incognitas++;
        if (parte2.ehIncognita()) incognitas++;
        if (todo.ehIncognita()) incognitas++;

        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (todo.ehIncognita()) {
            int calculado = parte1.valorAtual().valorOuNull() + parte2.valorAtual().valorOuNull();
            return new ResultadoCalculo(todo, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "Todo = Parte1 + Parte2",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        if (parte1.ehIncognita()) {
            int calculado = todo.valorAtual().valorOuNull() - parte2.valorAtual().valorOuNull();
            return new ResultadoCalculo(parte1, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "Parte1 = Todo - Parte2",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        int calculado = todo.valorAtual().valorOuNull() - parte1.valorAtual().valorOuNull();
        return new ResultadoCalculo(parte2, new NumeroInteiro(calculado), descreverRelacao(),
                EstadoConsistencia.CONSISTENTE, "Parte2 = Todo - Parte1",
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
     * recalcular Todo quando o par necessário (Parte1 e Parte2) está
     * totalmente conhecido; só recalcula uma das duas partes quando Todo
     * não está disponível como alvo (porque Todo foi o papel alterado, ou
     * porque falta valor no par).
     *
     * Não modifica nenhum papel — mesmo contrato de calcularValorAusente:
     * devolve um ResultadoCalculo; aplicar esse valor é decisão explícita
     * de outra camada, chamando aplicar(...).
     *
     * @param papelAlterado precisa ser exatamente um entre parte1, parte2
     *        ou todo (por referência) — o papel que acabou de mudar
     * @return ResultadoCalculo com o papel recalculado (CONSISTENTE), ou
     *         com EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO se nenhum
     *         dos pares necessários estiver totalmente conhecido —
     *         situação legítima, não uma exceção
     * @throws IllegalArgumentException se papelAlterado não for
     *         exatamente um dos três papéis desta relação
     */
    public ResultadoCalculo recalcularParaConsistencia(PapelQuantitativo parte1, PapelQuantitativo parte2,
            PapelQuantitativo todo, PapelQuantitativo papelAlterado) {
        exigirNaoNulo(parte1, "parte1");
        exigirNaoNulo(parte2, "parte2");
        exigirNaoNulo(todo, "todo");
        exigirNaoNulo(papelAlterado, "papelAlterado");
        if (papelAlterado != parte1 && papelAlterado != parte2 && papelAlterado != todo) {
            throw new IllegalArgumentException(
                    "papelAlterado precisa ser exatamente um dos três papéis desta relação");
        }

        if (papelAlterado == parte1) {
            if (parte1.estaPreenchido() && parte2.estaPreenchido()) {
                int calculado = parte1.valorAtual().valorOuNull() + parte2.valorAtual().valorOuNull();
                return new ResultadoCalculo(todo, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Todo recalculado = Parte1 + Parte2", OrigemAcao.ORIGEM_SISTEMA);
            }
            if (parte1.estaPreenchido() && todo.estaPreenchido()) {
                int calculado = todo.valorAtual().valorOuNull() - parte1.valorAtual().valorOuNull();
                return new ResultadoCalculo(parte2, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Parte2 recalculada = Todo - Parte1", OrigemAcao.ORIGEM_SISTEMA);
            }
        } else if (papelAlterado == parte2) {
            if (parte1.estaPreenchido() && parte2.estaPreenchido()) {
                int calculado = parte1.valorAtual().valorOuNull() + parte2.valorAtual().valorOuNull();
                return new ResultadoCalculo(todo, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Todo recalculado = Parte1 + Parte2", OrigemAcao.ORIGEM_SISTEMA);
            }
            if (parte2.estaPreenchido() && todo.estaPreenchido()) {
                int calculado = todo.valorAtual().valorOuNull() - parte2.valorAtual().valorOuNull();
                return new ResultadoCalculo(parte1, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Parte1 recalculada = Todo - Parte2", OrigemAcao.ORIGEM_SISTEMA);
            }
        } else {
            if (parte1.estaPreenchido() && todo.estaPreenchido()) {
                int calculado = todo.valorAtual().valorOuNull() - parte1.valorAtual().valorOuNull();
                return new ResultadoCalculo(parte2, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Parte2 recalculada = Todo - Parte1", OrigemAcao.ORIGEM_SISTEMA);
            }
            if (parte2.estaPreenchido() && todo.estaPreenchido()) {
                int calculado = todo.valorAtual().valorOuNull() - parte2.valorAtual().valorOuNull();
                return new ResultadoCalculo(parte1, new NumeroInteiro(calculado), descreverRelacao(),
                        EstadoConsistencia.CONSISTENTE, "Parte1 recalculada = Todo - Parte2", OrigemAcao.ORIGEM_SISTEMA);
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
            PapelQuantitativo parte1, PapelQuantitativo parte2, PapelQuantitativo todo,
            PapelQuantitativo papelAlvo, gerard.semantica.numero.ValorNumerico valorProposto) {
        exigirNaoNulo(parte1, "parte1");
        exigirNaoNulo(parte2, "parte2");
        exigirNaoNulo(todo, "todo");
        exigirNaoNulo(papelAlvo, "papelAlvo");
        if (valorProposto == null || !valorProposto.ehConhecido()) {
            throw new IllegalArgumentException("valorProposto precisa ser um valor conhecido para ser diagnosticado");
        }
        if (!papelAlvo.aceita(valorProposto)) {
            return java.util.Optional.of(new DiagnosticoErroPapel(TipoErroPapel.VALOR_FORA_DO_DOMINIO,
                    "erro.papel.valorForaDoDominio", "feedback.papel.valorForaDoDominio",
                    "correcao.papel.valorForaDoDominio"));
        }
        ResultadoCalculo esperado = calcularValorAusente(parte1, parte2, todo);
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
        int invertido = papelAlvo == todo
                ? parte1.valorAtual().valorOuNull() - parte2.valorAtual().valorOuNull()
                : papelAlvo == parte1
                        ? todo.valorAtual().valorOuNull() + parte2.valorAtual().valorOuNull()
                        : todo.valorAtual().valorOuNull() + parte1.valorAtual().valorOuNull();
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
