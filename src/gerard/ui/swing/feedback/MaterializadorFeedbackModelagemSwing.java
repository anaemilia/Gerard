package gerard.ui.swing.feedback;

import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.EstadoFeedbackDiagrama;
import gerard.ui.conclusao.SeloConclusaoModelagem;
import gerard.ui.conclusao.SeloErroModelagem;
import javax.swing.JComponent;

/** Materializa a decisão portátil nos controles próprios da interface Swing. */
public final class MaterializadorFeedbackModelagemSwing {
    private final JComponent recipiente;
    private final SeloErroModelagem seloErro;
    private final SeloConclusaoModelagem seloSucesso;
    private final FonteGeometriaFeedbackModelagemSwing geometria;

    public MaterializadorFeedbackModelagemSwing(JComponent recipiente, SeloErroModelagem seloErro,
            SeloConclusaoModelagem seloSucesso, FonteGeometriaFeedbackModelagemSwing geometria) {
        this.recipiente = recipiente;
        this.seloErro = seloErro;
        this.seloSucesso = seloSucesso;
        this.geometria = geometria;
    }

    public void materializar(CenaDiagramaAditivo cena) {
        EstadoFeedbackDiagrama estado = cena == null ? EstadoFeedbackDiagrama.NEUTRO : cena.getEstadoFeedback();
        seloErro.ocultar();
        seloSucesso.ocultar();
        if (estado == EstadoFeedbackDiagrama.ERRO) {
            recipiente.setComponentZOrder(seloErro, 0);
            seloErro.mostrarAoLadoDireitoDoDiagrama(geometria.obterAreaDiagrama(), geometria.obterAreaPermitida(),
                    geometria.obterLarguraPai(), geometria.obterAlturaPai());
        } else if (estado == EstadoFeedbackDiagrama.SUCESSO) {
            recipiente.setComponentZOrder(seloSucesso, 0);
            seloSucesso.mostrarAoLadoDireitoDoDiagrama(geometria.obterAreaDiagrama(), geometria.obterAreaPermitida(),
                    geometria.obterLarguraPai(), geometria.obterAlturaPai());
        }
        recipiente.repaint();
        if (estado == EstadoFeedbackDiagrama.ERRO) {
            recipiente.paintImmediately(0, 0, recipiente.getWidth(), recipiente.getHeight());
        }
    }
}
