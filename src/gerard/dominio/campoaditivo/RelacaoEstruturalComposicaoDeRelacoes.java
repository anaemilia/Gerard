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
