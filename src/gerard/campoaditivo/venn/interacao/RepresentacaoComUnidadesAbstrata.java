package gerard.campoaditivo.venn.interacao;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;

/**
 * Implementação-base que concentra estado comum e permite especialização por
 * herança sem duplicar a identidade do agrupamento.
 */
public abstract class RepresentacaoComUnidadesAbstrata implements RepresentacaoComUnidades {
    private final CirculoVenn agrupamento;
    private final String papelSemantico;
    protected final OperacoesUnidadesVenn operacoes;

    protected RepresentacaoComUnidadesAbstrata(CirculoVenn agrupamento,
            String papelSemantico, OperacoesUnidadesVenn operacoes) {
        if (agrupamento == null) {
            throw new IllegalArgumentException("O agrupamento não pode ser nulo.");
        }
        if (operacoes == null) {
            throw new IllegalArgumentException("As operações não podem ser nulas.");
        }
        this.agrupamento = agrupamento;
        this.papelSemantico = papelSemantico == null ? "" : papelSemantico;
        this.operacoes = operacoes;
    }

    public final CirculoVenn obterAgrupamento() {
        return agrupamento;
    }

    public final int obterQuantidadeAtual() {
        return Math.max(0, operacoes.contarUnidades(agrupamento));
    }

    public final String obterPapelSemantico() {
        return papelSemantico;
    }
}
