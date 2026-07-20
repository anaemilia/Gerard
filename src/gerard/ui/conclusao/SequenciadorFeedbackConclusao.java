package gerard.ui.conclusao;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Controla a transição discreta entre a confirmação visual e o tip de decisão.
 * A classe é independente da categoria: qualquer diagrama de Vergnaud concluído
 * percorre a mesma sequência.
 */
public final class SequenciadorFeedbackConclusao {
    public interface Ouvinte {
        void aoMostrarConfirmacaoVisual();
        void aoSolicitarDecisao();
        void aoCancelar();
    }

    public static final int ATRASO_PADRAO_DECISAO_MS = 1150;

    private final Timer timerDecisao;
    private EstadoFeedbackConclusao estado = EstadoFeedbackConclusao.INATIVO;
    private Ouvinte ouvinte;

    public SequenciadorFeedbackConclusao() {
        this(ATRASO_PADRAO_DECISAO_MS);
    }

    public SequenciadorFeedbackConclusao(int atrasoDecisaoMs) {
        timerDecisao = new Timer(Math.max(1, atrasoDecisaoMs), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (estado != EstadoFeedbackConclusao.CONFIRMACAO_VISUAL) {
                    return;
                }
                estado = EstadoFeedbackConclusao.AGUARDANDO_DECISAO;
                if (ouvinte != null) {
                    ouvinte.aoSolicitarDecisao();
                }
            }
        });
        timerDecisao.setRepeats(false);
    }

    public void definirOuvinte(Ouvinte ouvinte) {
        this.ouvinte = ouvinte;
    }

    public void iniciar() {
        if (estado == EstadoFeedbackConclusao.CONFIRMACAO_VISUAL
                || estado == EstadoFeedbackConclusao.AGUARDANDO_DECISAO) {
            return;
        }
        timerDecisao.stop();
        estado = EstadoFeedbackConclusao.CONFIRMACAO_VISUAL;
        if (ouvinte != null) {
            ouvinte.aoMostrarConfirmacaoVisual();
        }
        timerDecisao.restart();
    }

    public void cancelar() {
        timerDecisao.stop();
        boolean haviaFeedbackAtivo = estado != EstadoFeedbackConclusao.INATIVO;
        estado = EstadoFeedbackConclusao.INATIVO;
        if (haviaFeedbackAtivo && ouvinte != null) {
            ouvinte.aoCancelar();
        }
    }

    public void encerrar() {
        timerDecisao.stop();
        estado = EstadoFeedbackConclusao.ENCERRADO;
        if (ouvinte != null) {
            ouvinte.aoCancelar();
        }
    }

    public EstadoFeedbackConclusao getEstado() {
        return estado;
    }
}
