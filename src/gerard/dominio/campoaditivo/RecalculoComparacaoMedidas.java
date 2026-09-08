package gerard.dominio.campoaditivo;

/**
 * Decide qual dos dois papéis de medida (Referido ou Referendo) do esquema
 * Comparação de Medidas deve ser recalculado quando o Valor Relativo é
 * editado diretamente no controle do gráfico de barras, e qual seria o novo
 * valor.
 *
 * Extraído de {@code Main.TelaGerard.aplicarEdicaoValorRelativoComparacao} em
 * 2026-09-01 (ver {@code LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md},
 * item P0 "8267–8706"): esta decisão não é puramente aritmética — ela
 * respeita qual papel foi curricularmente designado como a incógnita da
 * situação-problema (termo desconhecido curado), distinguindo-se por isso do
 * resolvedor genérico de {@link RelacaoEstruturalComparacao#recalcularParaConsistencia},
 * que não conhece esse metadado pedagógico e usa uma prioridade fixa (sempre
 * preferindo recalcular Referendo quando o par necessário está completo).
 *
 * Não decide QUANDO recalcular — isso continua sendo decidido por quem
 * chama, a partir do gesto do usuário — nem escreve em nenhum elemento
 * visual: apenas devolve qual papel e qual valor, preservando exatamente a
 * mesma ordem de prioridade que já existia embutida em {@code Main}.
 */
public final class RecalculoComparacaoMedidas {

    private RecalculoComparacaoMedidas() {
    }

    public enum PapelAlvo { REFERIDO, REFERENDO, NENHUM }

    public static final class Resultado {
        private static final Resultado NENHUM_RESULTADO =
                new Resultado(PapelAlvo.NENHUM, 0);

        private final PapelAlvo papel;
        private final int valor;

        private Resultado(PapelAlvo papel, int valor) {
            this.papel = papel;
            this.valor = valor;
        }

        public PapelAlvo getPapel() {
            return papel;
        }

        public int getValor() {
            return valor;
        }
    }

    /**
     * @param papelDesconhecido chave do papel designado como incógnita pela
     *        situação-problema
     * @param valorReferido valor atualmente modelado no papel Referido, ou
     *        {@code null} se ainda não preenchido
     * @param valorReferendo valor atualmente modelado no papel Referendo, ou
     *        {@code null} se ainda não preenchido
     * @param valorRelativo novo valor do Valor Relativo, já aplicado
     */
    public static Resultado decidir(String papelDesconhecido,
            Integer valorReferido, Integer valorReferendo, int valorRelativo) {
        boolean desconhecidoEhReferido =
                "papel.referido".equals(papelDesconhecido);
        if (desconhecidoEhReferido && valorReferendo != null) {
            return new Resultado(PapelAlvo.REFERIDO,
                    valorReferendo.intValue() - valorRelativo);
        }
        if (valorReferido != null) {
            return new Resultado(PapelAlvo.REFERENDO,
                    valorReferido.intValue() + valorRelativo);
        }
        if (valorReferendo != null) {
            return new Resultado(PapelAlvo.REFERIDO,
                    valorReferendo.intValue() - valorRelativo);
        }
        return Resultado.NENHUM_RESULTADO;
    }
}
