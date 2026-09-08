package gerard.aplicacao.feedback;

import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.EstadoFeedbackDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;

/** Decide a transição de feedback sem conhecer Swing, componentes ou tempo. */
public final class ControladorFeedbackModelagem {
    private final GeradorCenaDiagramaAditivo geradorCena;

    public ControladorFeedbackModelagem(GeradorCenaDiagramaAditivo geradorCena) {
        if (geradorCena == null) throw new IllegalArgumentException("geradorCena");
        this.geradorCena = geradorCena;
    }

    public CenaDiagramaAditivo transicionar(CenaDiagramaAditivo cena, EventoFeedbackModelagem evento) {
        if (evento == null) throw new IllegalArgumentException("evento");
        EstadoFeedbackDiagrama destino;
        switch (evento) {
            case REJEICAO: destino = EstadoFeedbackDiagrama.ERRO; break;
            case SUCESSO_APRESENTADO: destino = EstadoFeedbackDiagrama.SUCESSO; break;
            case CONCLUSAO_RECONHECIDA:
            case REINICIO:
            default: destino = EstadoFeedbackDiagrama.NEUTRO; break;
        }
        return geradorCena.comFeedback(cena, destino);
    }
}
