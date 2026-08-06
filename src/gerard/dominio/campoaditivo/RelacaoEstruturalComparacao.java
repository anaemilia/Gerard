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
        return (rn == rd + vr) ? EstadoConsistencia.CONSISTENTE : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
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

        int incognitas = 0;
        if (referido.ehIncognita()) incognitas++;
        if (valorRelativo.ehIncognita()) incognitas++;
        if (referendo.ehIncognita()) incognitas++;

        if (incognitas != 1) {
            return new ResultadoCalculo(null, null, descreverRelacao(),
                    EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                    "Encontrados " + incognitas + " papéis incógnitos entre os três; "
                            + "é preciso exatamente 1 para calcular um valor ausente único.",
                    OrigemAcao.ORIGEM_SISTEMA);
        }

        if (referendo.ehIncognita()) {
            int calculado = referido.valorAtual().valorOuNull() + valorRelativo.valorAtual().valorOuNull();
            return new ResultadoCalculo(referendo, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "Referendo = Referido + ValorRelativo",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        if (referido.ehIncognita()) {
            int calculado = referendo.valorAtual().valorOuNull() - valorRelativo.valorAtual().valorOuNull();
            return new ResultadoCalculo(referido, new NumeroInteiro(calculado), descreverRelacao(),
                    EstadoConsistencia.CONSISTENTE, "Referido = Referendo - ValorRelativo",
                    OrigemAcao.ORIGEM_SISTEMA);
        }
        int calculado = referendo.valorAtual().valorOuNull() - referido.valorAtual().valorOuNull();
        return new ResultadoCalculo(valorRelativo, new NumeroInteiro(calculado), descreverRelacao(),
                EstadoConsistencia.CONSISTENTE, "ValorRelativo = Referendo - Referido",
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
        ResultadoCalculo esperado = calcularValorAusente(referido, valorRelativo, referendo);
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
        int invertido = papelAlvo == referendo
                ? referido.valorAtual().valorOuNull() - valorRelativo.valorAtual().valorOuNull()
                : papelAlvo == referido
                        ? referendo.valorAtual().valorOuNull() + valorRelativo.valorAtual().valorOuNull()
                        : referendo.valorAtual().valorOuNull() + referido.valorAtual().valorOuNull();
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
