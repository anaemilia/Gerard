package gerard.Scaffolding.feedbackerro;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;

/**
 * Coordena o feedback de erro aplicado a um proxy temporário oriundo do texto.
 * O proxy permanece visível durante o som e o tremor e é descartado somente
 * quando a animação termina. O texto de origem e o estado semântico não são
 * modificados.
 */
public final class ScaffoldingFeedbackProxyPosicionamento {

    private final ScaffoldingFeedbackMultissensorialErro feedbackMultissensorial;
    private final SessaoArrasteTextoParaDiagrama sessaoArraste;

    public ScaffoldingFeedbackProxyPosicionamento(
            ScaffoldingFeedbackMultissensorialErro feedbackMultissensorial,
            SessaoArrasteTextoParaDiagrama sessaoArraste) {
        if (feedbackMultissensorial == null || sessaoArraste == null) {
            throw new IllegalArgumentException("Feedback e sessão de arraste são obrigatórios.");
        }
        this.feedbackMultissensorial = feedbackMultissensorial;
        this.sessaoArraste = sessaoArraste;
    }

    public boolean sinalizar(final ItemTextoArrastavel proxy, final Runnable repaint) {
        if (!sessaoArraste.iniciarFeedbackIncorreto(proxy)) {
            return false;
        }

        feedbackMultissensorial.sinalizarErro(proxy, repaint, new Runnable() {
            public void run() {
                sessaoArraste.concluirFeedbackIncorreto(proxy);
                if (repaint != null) {
                    repaint.run();
                }
            }
        });
        return true;
    }

    public ItemTextoArrastavel obterProxyEmFeedback() {
        return sessaoArraste.obterProxyEmFeedback();
    }

    public void cancelar() {
        feedbackMultissensorial.pararTremor();
    }
}
