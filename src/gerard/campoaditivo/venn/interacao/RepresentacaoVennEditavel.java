package gerard.campoaditivo.venn.interacao;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;

/**
 * Implementação polimórfica de uma representação de Venn que admite adição e
 * remoção de unidades. Os controles da interface dependem apenas dos
 * contratos, não desta classe concreta.
 */
public final class RepresentacaoVennEditavel extends RepresentacaoComUnidadesAbstrata
        implements RepresentacaoComUnidadesAdicionaveis,
                   RepresentacaoComUnidadesRemoviveis {

    public RepresentacaoVennEditavel(CirculoVenn agrupamento,
            String papelSemantico, OperacoesUnidadesVenn operacoes) {
        super(agrupamento, papelSemantico, operacoes);
    }

    public Integer obterLimiteSemantico() {
        return operacoes.obterLimiteSemantico(obterAgrupamento());
    }

    public boolean podeAdicionarUnidade() {
        Integer limite = obterLimiteSemantico();
        boolean abaixoDoLimite = limite == null
                || obterQuantidadeAtual() < Math.max(0, limite.intValue());
        return abaixoDoLimite
                && operacoes.podeAlterarQuantidade(obterAgrupamento(), 1);
    }

    public ResultadoOperacaoUnidade adicionarUnidade() {
        int anterior = obterQuantidadeAtual();
        Integer limite = obterLimiteSemantico();
        if (!podeAdicionarUnidade()) {
            return ResultadoOperacaoUnidade.limiteAtingido(anterior);
        }
        operacoes.adicionarUnidade(obterAgrupamento());
        int atual = obterQuantidadeAtual();
        boolean atingiu = limite != null && atual >= Math.max(0, limite.intValue());
        return ResultadoOperacaoUnidade.realizada(anterior, atual, atingiu);
    }

    public boolean podeRemoverUnidade() {
        return obterQuantidadeAtual() > 0
                && operacoes.podeAlterarQuantidade(obterAgrupamento(), -1);
    }

    public ResultadoOperacaoUnidade removerUnidade() {
        int anterior = obterQuantidadeAtual();
        if (!podeRemoverUnidade()) {
            return ResultadoOperacaoUnidade.naoPermitida(anterior);
        }
        operacoes.removerUnidade(obterAgrupamento());
        return ResultadoOperacaoUnidade.realizada(
                anterior, obterQuantidadeAtual(), false);
    }
}
