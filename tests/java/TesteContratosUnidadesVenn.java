import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.venn.interacao.OperacoesUnidadesVenn;
import gerard.campoaditivo.venn.interacao.RepresentacaoComUnidadesAbstrata;
import gerard.campoaditivo.venn.interacao.RepresentacaoComUnidadesAdicionaveis;
import gerard.campoaditivo.venn.interacao.RepresentacaoComUnidadesRemoviveis;
import gerard.campoaditivo.venn.interacao.RepresentacaoVennEditavel;
import gerard.campoaditivo.venn.interacao.ResultadoOperacaoUnidade;

public class TesteContratosUnidadesVenn {
    public static void main(String[] args) {
        final int[] quantidade = {0};
        final int limite = 2;
        CirculoVenn agrupamento = new CirculoVenn(0, 0, 100, 100, "Parte 1", 2, true);

        OperacoesUnidadesVenn operacoes = new OperacoesUnidadesVenn() {
            public int contarUnidades(CirculoVenn a) { return quantidade[0]; }
            public Integer obterLimiteSemantico(CirculoVenn a) { return limite; }
            public boolean podeAlterarQuantidade(CirculoVenn a, int variacao) {
                return quantidade[0] + variacao >= 0
                        && quantidade[0] + variacao <= limite;
            }
            public void adicionarUnidade(CirculoVenn a) { quantidade[0]++; }
            public void removerUnidade(CirculoVenn a) { quantidade[0]--; }
        };

        RepresentacaoVennEditavel concreta = new RepresentacaoVennEditavel(
                agrupamento, "Parte 1", operacoes);
        if (!(concreta instanceof RepresentacaoComUnidadesAbstrata)) {
            throw new AssertionError("A implementação deve herdar da classe abstrata comum.");
        }

        RepresentacaoComUnidadesAdicionaveis adicionavel = concreta;
        RepresentacaoComUnidadesRemoviveis removivel = concreta;

        ResultadoOperacaoUnidade primeira = adicionavel.adicionarUnidade();
        if (!primeira.foiRealizada() || primeira.isLimiteAtingido() || quantidade[0] != 1) {
            throw new AssertionError("Primeira adição incorreta.");
        }

        ResultadoOperacaoUnidade segunda = adicionavel.adicionarUnidade();
        if (!segunda.foiRealizada() || !segunda.isLimiteAtingido() || quantidade[0] != 2) {
            throw new AssertionError("A segunda adição deve atingir o limite semântico.");
        }

        ResultadoOperacaoUnidade bloqueada = adicionavel.adicionarUnidade();
        if (bloqueada.foiRealizada() || !bloqueada.isLimiteAtingido() || quantidade[0] != 2) {
            throw new AssertionError("A adição acima do limite deveria ser bloqueada.");
        }

        ResultadoOperacaoUnidade remocao = removivel.removerUnidade();
        if (!remocao.foiRealizada() || quantidade[0] != 1) {
            throw new AssertionError("A remoção polimórfica falhou.");
        }

        System.out.println("Contratos, herança e polimorfismo das unidades Venn: OK");
    }
}
