package gerard.aplicacao.interacao;

import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;

/** Materializa feedback sem transferir a avaliação para a apresentação. */
public interface PortaFeedbackEscolhaOperacaoRelacao {
    void materializar(ResultadoEscolhaOperacaoModelagem.Feedback feedback);
}
