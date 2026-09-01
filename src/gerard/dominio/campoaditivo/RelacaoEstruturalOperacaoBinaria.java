package gerard.dominio.campoaditivo;

import gerard.semantica.numero.NumeroInteiro;

/**
 * Relação estrutural cujo operador foi declarado pela curadoria humana:
 * resultado = primeiro [operação] segundo.
 */
public final class RelacaoEstruturalOperacaoBinaria
        implements RelacaoEstruturalAditiva {
    private final OperacaoAditiva operacao;

    private RelacaoEstruturalOperacaoBinaria(OperacaoAditiva operacao) {
        if (operacao == null) {
            throw new IllegalArgumentException("operação aditiva é obrigatória");
        }
        this.operacao = operacao;
    }

    public static RelacaoEstruturalOperacaoBinaria com(OperacaoAditiva operacao) {
        return new RelacaoEstruturalOperacaoBinaria(operacao);
    }

    public OperacaoAditiva getOperacao() { return operacao; }

    public String descreverRelacao() {
        return "Resultado = Primeiro " + operacao.getSimbolo() + " Segundo";
    }

    public EstadoConsistencia verificarConsistencia(
            PapelQuantitativo primeiro,
            PapelQuantitativo segundo,
            PapelQuantitativo resultado) {
        exigirPapeis(primeiro, segundo, resultado);
        if (!primeiro.estaPreenchido() || !segundo.estaPreenchido()
                || !resultado.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        try {
            int esperado = operacao.aplicar(
                    primeiro.valorAtual().valorOuNull(),
                    segundo.valorAtual().valorOuNull());
            return esperado == resultado.valorAtual().valorOuNull()
                    ? EstadoConsistencia.CONSISTENTE
                    : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
        } catch (ArithmeticException estouro) {
            return EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO;
        }
    }

    public ResultadoCalculo calcularValorAusente(
            PapelQuantitativo primeiro,
            PapelQuantitativo segundo,
            PapelQuantitativo resultado) {
        exigirPapeis(primeiro, segundo, resultado);
        int incognitas = contarIncognitas(primeiro, segundo, resultado);
        if (incognitas != 1) {
            return naoResolvido("é preciso exatamente um papel incógnito");
        }
        try {
            if (resultado.ehIncognita()) {
                return calculado(resultado, operacao.aplicar(
                        primeiro.valorAtual().valorOuNull(),
                        segundo.valorAtual().valorOuNull()));
            }
            if (primeiro.ehIncognita()) {
                return calculado(primeiro, operacao.isolarPrimeiro(
                        resultado.valorAtual().valorOuNull(),
                        segundo.valorAtual().valorOuNull()));
            }
            return calculado(segundo, operacao.isolarSegundo(
                    resultado.valorAtual().valorOuNull(),
                    primeiro.valorAtual().valorOuNull()));
        } catch (ArithmeticException estouro) {
            return naoResolvido("o valor excede o intervalo dos inteiros");
        }
    }

    public ResultadoCalculo recalcularParaConsistencia(
            PapelQuantitativo primeiro,
            PapelQuantitativo segundo,
            PapelQuantitativo resultado,
            PapelQuantitativo papelAlterado) {
        exigirPapeis(primeiro, segundo, resultado);
        if (papelAlterado != primeiro && papelAlterado != segundo
                && papelAlterado != resultado) {
            throw new IllegalArgumentException(
                    "papel alterado não pertence a esta relação");
        }
        try {
            if (papelAlterado != resultado && primeiro.estaPreenchido()
                    && segundo.estaPreenchido()) {
                return calculado(resultado, operacao.aplicar(
                        primeiro.valorAtual().valorOuNull(),
                        segundo.valorAtual().valorOuNull()));
            }
            if (papelAlterado != primeiro && resultado.estaPreenchido()
                    && segundo.estaPreenchido()) {
                return calculado(primeiro, operacao.isolarPrimeiro(
                        resultado.valorAtual().valorOuNull(),
                        segundo.valorAtual().valorOuNull()));
            }
            if (papelAlterado != segundo && resultado.estaPreenchido()
                    && primeiro.estaPreenchido()) {
                return calculado(segundo, operacao.isolarSegundo(
                        resultado.valorAtual().valorOuNull(),
                        primeiro.valorAtual().valorOuNull()));
            }
            return naoResolvido("não há dois papéis conhecidos suficientes");
        } catch (ArithmeticException estouro) {
            return naoResolvido("o valor excede o intervalo dos inteiros");
        }
    }

    private ResultadoCalculo calculado(PapelQuantitativo papel, int valor) {
        return new ResultadoCalculo(
                papel, new NumeroInteiro(valor), descreverRelacao(),
                EstadoConsistencia.CONSISTENTE,
                "valor calculado pela operação explicitamente curada",
                OrigemAcao.ORIGEM_SISTEMA);
    }

    private ResultadoCalculo naoResolvido(String justificativa) {
        return new ResultadoCalculo(
                null, null, descreverRelacao(),
                EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO,
                justificativa, OrigemAcao.ORIGEM_SISTEMA);
    }

    private static int contarIncognitas(
            PapelQuantitativo primeiro,
            PapelQuantitativo segundo,
            PapelQuantitativo resultado) {
        int total = 0;
        if (primeiro.ehIncognita()) total++;
        if (segundo.ehIncognita()) total++;
        if (resultado.ehIncognita()) total++;
        return total;
    }

    private static void exigirPapeis(
            PapelQuantitativo primeiro,
            PapelQuantitativo segundo,
            PapelQuantitativo resultado) {
        if (primeiro == null || segundo == null || resultado == null) {
            throw new IllegalArgumentException(
                    "os três papéis da relação são obrigatórios");
        }
    }
}
