package gerard.ui.vergnaud;

import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;
import gerard.interacao.selecao.AlvoSeletoresOperacaoRelacoes;

/** Traduz os dois widgets Swing para a porta portatil do protocolo. */
public final class AdaptadorSeletoresOperacaoRelacoes
        implements AlvoSeletoresOperacaoRelacoes {

    private final SeletorOperacaoRelacaoAluno primeiro;
    private final SeletorOperacaoRelacaoAluno segundo;

    public AdaptadorSeletoresOperacaoRelacoes(
            SeletorOperacaoRelacaoAluno primeiro,
            SeletorOperacaoRelacaoAluno segundo) {
        if (primeiro == null || segundo == null) {
            throw new IllegalArgumentException("seletores obrigatorios");
        }
        this.primeiro = primeiro;
        this.segundo = segundo;
    }

    @Override
    public ResultadoEscolhaOperacaoModelagem processarPrimeiraEscolha(
            int posicaoX, int posicaoY) {
        return primeiro.processarEscolha(posicaoX, posicaoY);
    }

    @Override
    public boolean primeiraEscolhaEstaCorreta() {
        return primeiro.respondeuCorretamente();
    }

    @Override
    public ResultadoEscolhaOperacaoModelagem processarSegundaEscolha(
            int posicaoX, int posicaoY) {
        return segundo.processarEscolha(posicaoX, posicaoY);
    }
}
