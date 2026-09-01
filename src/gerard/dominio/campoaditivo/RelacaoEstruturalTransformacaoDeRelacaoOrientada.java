package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.situacao.ParticipanteNarrativo;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.semantica.numero.NumeroInteiro;

/**
 * Transformação de Relação com orientação narrativa explicitamente curada.
 *
 * A relação inicial define a orientação de referência. A variação de um dos
 * participantes pode aumentar ou reduzir essa relação, e a relação final pode
 * conservar ou inverter a orientação. Esse conhecimento pertence à relação,
 * não ao conversor tabular nem à interface.
 */
public final class RelacaoEstruturalTransformacaoDeRelacaoOrientada
        implements RelacaoEstruturalAditiva {
    private final int efeitoDaTransformacao;
    private final int orientacaoDaRelacaoFinal;

    private RelacaoEstruturalTransformacaoDeRelacaoOrientada(
            int efeitoDaTransformacao,
            int orientacaoDaRelacaoFinal) {
        this.efeitoDaTransformacao = efeitoDaTransformacao;
        this.orientacaoDaRelacaoFinal = orientacaoDaRelacaoFinal;
    }

    public static RelacaoEstruturalTransformacaoDeRelacaoOrientada
            aPartirDasReferencias(
                    ReferenciaValorNarrativo relacaoInicial,
                    ReferenciaValorNarrativo transformacao,
                    ReferenciaValorNarrativo relacaoFinal) {
        exigirTipo(relacaoInicial,
                ReferenciaValorNarrativo.Tipo.DIFERENCA_ENTRE_QUANTIDADES_INICIAIS,
                "relação inicial");
        exigirTipo(transformacao,
                ReferenciaValorNarrativo.Tipo.VARIACAO_DE_EVENTO,
                "transformação");
        exigirTipo(relacaoFinal,
                ReferenciaValorNarrativo.Tipo.DIFERENCA_ENTRE_QUANTIDADES_FINAIS,
                "relação final");
        if (!relacaoInicial.getFamilia().equals(transformacao.getFamilia())
                || !relacaoInicial.getFamilia().equals(relacaoFinal.getFamilia())) {
            throw new IllegalArgumentException(
                    "os três papéis precisam referenciar a mesma família de objetos");
        }

        ParticipanteNarrativo primeiroInicial = relacaoInicial.getParticipante();
        ParticipanteNarrativo segundoInicial =
                relacaoInicial.getParticipanteComparado();
        ParticipanteNarrativo participanteTransformado =
                transformacao.getParticipante();
        int efeito;
        if (participanteTransformado.equals(primeiroInicial)) {
            efeito = 1;
        } else if (participanteTransformado.equals(segundoInicial)) {
            efeito = -1;
        } else {
            throw new IllegalArgumentException(
                    "o participante transformado precisa integrar a relação inicial");
        }

        ParticipanteNarrativo primeiroFinal = relacaoFinal.getParticipante();
        ParticipanteNarrativo segundoFinal =
                relacaoFinal.getParticipanteComparado();
        int orientacaoFinal;
        if (primeiroFinal.equals(primeiroInicial)
                && segundoFinal.equals(segundoInicial)) {
            orientacaoFinal = 1;
        } else if (primeiroFinal.equals(segundoInicial)
                && segundoFinal.equals(primeiroInicial)) {
            orientacaoFinal = -1;
        } else {
            throw new IllegalArgumentException(
                    "a relação final precisa conservar ou inverter os participantes da relação inicial");
        }
        return new RelacaoEstruturalTransformacaoDeRelacaoOrientada(
                efeito, orientacaoFinal);
    }

    public String descreverRelacao() {
        return "RelacaoInicial + efeitoOrientado(Transformacao) = relacaoFinalNaOrientacaoInicial";
    }

    public EstadoConsistencia verificarConsistencia(
            PapelQuantitativo relacaoInicial,
            PapelQuantitativo transformacao,
            PapelQuantitativo relacaoFinal) {
        exigirPapeis(relacaoInicial, transformacao, relacaoFinal);
        if (!relacaoInicial.estaPreenchido() || !transformacao.estaPreenchido()
                || !relacaoFinal.estaPreenchido()) {
            return EstadoConsistencia.REPRESENTACAO_INCOMPLETA;
        }
        try {
            int esperado = somarComEfeito(
                    relacaoInicial.valorAtual().valorOuNull(),
                    transformacao.valorAtual().valorOuNull());
            int finalOrientada = orientarFinal(
                    relacaoFinal.valorAtual().valorOuNull());
            return esperado == finalOrientada
                    ? EstadoConsistencia.CONSISTENTE
                    : EstadoConsistencia.REPRESENTACAO_INCONSISTENTE;
        } catch (ArithmeticException estouro) {
            return EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO;
        }
    }

    public ResultadoCalculo calcularValorAusente(
            PapelQuantitativo relacaoInicial,
            PapelQuantitativo transformacao,
            PapelQuantitativo relacaoFinal) {
        exigirPapeis(relacaoInicial, transformacao, relacaoFinal);
        if (contarIncognitas(relacaoInicial, transformacao, relacaoFinal) != 1) {
            return naoResolvido("é preciso exatamente um papel incógnito");
        }
        try {
            if (relacaoFinal.ehIncognita()) {
                return calculado(relacaoFinal, desorientarFinal(somarComEfeito(
                        relacaoInicial.valorAtual().valorOuNull(),
                        transformacao.valorAtual().valorOuNull())));
            }
            if (relacaoInicial.ehIncognita()) {
                return calculado(relacaoInicial, Math.subtractExact(
                        orientarFinal(relacaoFinal.valorAtual().valorOuNull()),
                        aplicarSinal(transformacao.valorAtual().valorOuNull(),
                                efeitoDaTransformacao)));
            }
            int diferenca = Math.subtractExact(
                    orientarFinal(relacaoFinal.valorAtual().valorOuNull()),
                    relacaoInicial.valorAtual().valorOuNull());
            return calculado(transformacao,
                    aplicarSinal(diferenca, efeitoDaTransformacao));
        } catch (ArithmeticException estouro) {
            return naoResolvido("o valor excede o intervalo dos inteiros");
        }
    }

    public ResultadoCalculo recalcularParaConsistencia(
            PapelQuantitativo relacaoInicial,
            PapelQuantitativo transformacao,
            PapelQuantitativo relacaoFinal,
            PapelQuantitativo papelAlterado) {
        exigirPapeis(relacaoInicial, transformacao, relacaoFinal);
        if (papelAlterado != relacaoInicial && papelAlterado != transformacao
                && papelAlterado != relacaoFinal) {
            throw new IllegalArgumentException(
                    "papel alterado não pertence à transformação de relação");
        }
        try {
            if (papelAlterado != relacaoFinal && relacaoInicial.estaPreenchido()
                    && transformacao.estaPreenchido()) {
                return calculado(relacaoFinal, desorientarFinal(somarComEfeito(
                        relacaoInicial.valorAtual().valorOuNull(),
                        transformacao.valorAtual().valorOuNull())));
            }
            if (papelAlterado != relacaoInicial && relacaoFinal.estaPreenchido()
                    && transformacao.estaPreenchido()) {
                return calculado(relacaoInicial, Math.subtractExact(
                        orientarFinal(relacaoFinal.valorAtual().valorOuNull()),
                        aplicarSinal(transformacao.valorAtual().valorOuNull(),
                                efeitoDaTransformacao)));
            }
            if (papelAlterado != transformacao && relacaoFinal.estaPreenchido()
                    && relacaoInicial.estaPreenchido()) {
                int diferenca = Math.subtractExact(
                        orientarFinal(relacaoFinal.valorAtual().valorOuNull()),
                        relacaoInicial.valorAtual().valorOuNull());
                return calculado(transformacao,
                        aplicarSinal(diferenca, efeitoDaTransformacao));
            }
            return naoResolvido("não há dois papéis conhecidos suficientes");
        } catch (ArithmeticException estouro) {
            return naoResolvido("o valor excede o intervalo dos inteiros");
        }
    }

    private int somarComEfeito(int relacaoInicial, int transformacao) {
        return Math.addExact(relacaoInicial,
                aplicarSinal(transformacao, efeitoDaTransformacao));
    }

    private int orientarFinal(int relacaoFinal) {
        return aplicarSinal(relacaoFinal, orientacaoDaRelacaoFinal);
    }

    private int desorientarFinal(int relacaoNaOrientacaoInicial) {
        return aplicarSinal(relacaoNaOrientacaoInicial,
                orientacaoDaRelacaoFinal);
    }

    private static int aplicarSinal(int valor, int sinal) {
        return sinal == 1 ? valor : Math.negateExact(valor);
    }

    private ResultadoCalculo calculado(PapelQuantitativo papel, int valor) {
        return new ResultadoCalculo(
                papel, new NumeroInteiro(valor), descreverRelacao(),
                EstadoConsistencia.CONSISTENTE,
                "valor calculado pela orientação explicitamente curada",
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
            PapelQuantitativo terceiro) {
        int total = 0;
        if (primeiro.ehIncognita()) total++;
        if (segundo.ehIncognita()) total++;
        if (terceiro.ehIncognita()) total++;
        return total;
    }

    private static void exigirTipo(
            ReferenciaValorNarrativo referencia,
            ReferenciaValorNarrativo.Tipo tipo,
            String papel) {
        if (referencia == null || referencia.getTipo() != tipo) {
            throw new IllegalArgumentException(
                    papel + " exige referência narrativa do tipo " + tipo);
        }
    }

    private static void exigirPapeis(
            PapelQuantitativo primeiro,
            PapelQuantitativo segundo,
            PapelQuantitativo terceiro) {
        if (primeiro == null || segundo == null || terceiro == null) {
            throw new IllegalArgumentException(
                    "os três papéis da transformação de relação são obrigatórios");
        }
    }
}
