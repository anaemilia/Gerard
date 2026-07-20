package gerard.campoaditivo.venn.interacao;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;

/**
 * Porta entre o contrato polimórfico das representações e a infraestrutura
 * concreta de layout, sincronização e persistência da tela.
 */
public interface OperacoesUnidadesVenn {
    int contarUnidades(CirculoVenn agrupamento);
    Integer obterLimiteSemantico(CirculoVenn agrupamento);
    boolean podeAlterarQuantidade(CirculoVenn agrupamento, int variacao);
    void adicionarUnidade(CirculoVenn agrupamento);
    void removerUnidade(CirculoVenn agrupamento);
}
