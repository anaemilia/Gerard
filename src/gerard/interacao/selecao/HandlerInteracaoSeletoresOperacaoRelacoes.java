package gerard.interacao.selecao;

import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;

/**
 * Protocolo portatil da selecao ordenada de operacoes das Relacoes.
 * A segunda escolha so existe para a interacao depois que a primeira esta
 * correta. O handler nao conhece Swing, feedback, log nem conclusao da tela.
 */
public final class HandlerInteracaoSeletoresOperacaoRelacoes {

    public ResultadoEscolhaOperacaoModelagem processar(
            AlvoSeletoresOperacaoRelacoes alvo, int posicaoX, int posicaoY) {
        if (alvo == null) {
            return null;
        }

        ResultadoEscolhaOperacaoModelagem primeira =
                alvo.processarPrimeiraEscolha(posicaoX, posicaoY);
        if (primeira != null) {
            return primeira;
        }
        if (!alvo.primeiraEscolhaEstaCorreta()) {
            return null;
        }
        return alvo.processarSegundaEscolha(posicaoX, posicaoY);
    }
}
