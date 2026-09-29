package gerard.interacao.selecao;

import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;

/** Porta neutra para as duas escolhas ordenadas de operacao das Relacoes. */
public interface AlvoSeletoresOperacaoRelacoes {

    ResultadoEscolhaOperacaoModelagem processarPrimeiraEscolha(
            int posicaoX, int posicaoY);

    boolean primeiraEscolhaEstaCorreta();

    ResultadoEscolhaOperacaoModelagem processarSegundaEscolha(
            int posicaoX, int posicaoY);
}
