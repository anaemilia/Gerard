package gerard.ui.swing.adaptacao;

import gerard.Scaffolding.feedbackerro.ScaffoldingFeedbackMultissensorialErro;
import gerard.aplicacao.interacao.PortaFeedbackEscolhaOperacaoRelacao;
import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;

/** Adaptador Swing do feedback já decidido pelo proprietário semântico. */
public final class ApresentadorFeedbackEscolhaOperacaoSwing
        implements PortaFeedbackEscolhaOperacaoRelacao {

    private final ScaffoldingFeedbackMultissensorialErro feedbackErro;

    public ApresentadorFeedbackEscolhaOperacaoSwing(
            ScaffoldingFeedbackMultissensorialErro feedbackErro) {
        if (feedbackErro == null) {
            throw new IllegalArgumentException("feedback obrigatorio");
        }
        this.feedbackErro = feedbackErro;
    }

    @Override
    public void materializar(ResultadoEscolhaOperacaoModelagem.Feedback feedback) {
        if (feedback == ResultadoEscolhaOperacaoModelagem.Feedback.SOM_ERRO) {
            feedbackErro.emitirApenasSom();
        }
    }
}
