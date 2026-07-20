package gerard.campoaditivo.conclusao;

import java.util.Collection;
import java.util.List;

/** Mantém o ciclo de conclusão e impede a repetição contínua do tip. */
public final class ControladorConclusaoModelagem {
    private final AvaliadorConclusaoModelagem avaliador =
            new AvaliadorConclusaoModelagem();
    private FaseConclusaoModelagem fase = FaseConclusaoModelagem.INCOMPLETA;
    private boolean tipApresentado;

    public AtualizacaoConclusaoModelagem atualizar(
            Collection<String> papeisEsperados,
            List<EstadoPosicionamentoModelagem> posicionamentos) {
        FaseConclusaoModelagem novaFase = avaliador.avaliar(
                papeisEsperados, posicionamentos);
        boolean estavaConcluida = fase == FaseConclusaoModelagem.CONCLUIDA;
        boolean novaConclusao = novaFase == FaseConclusaoModelagem.CONCLUIDA;
        fase = novaFase;

        if (novaConclusao) {
            if (!estavaConcluida) {
                tipApresentado = false;
                return AtualizacaoConclusaoModelagem.CONCLUIDA_AGORA;
            }
            return AtualizacaoConclusaoModelagem.CONTINUA_CONCLUIDA;
        }
        if (estavaConcluida) {
            tipApresentado = false;
            return AtualizacaoConclusaoModelagem.DEIXOU_DE_ESTAR_CONCLUIDA;
        }
        return AtualizacaoConclusaoModelagem.CONTINUA_INCOMPLETA;
    }

    public boolean isConcluida() {
        return fase == FaseConclusaoModelagem.CONCLUIDA;
    }

    public FaseConclusaoModelagem getFase() { return fase; }

    public boolean deveApresentarTip() {
        return isConcluida() && !tipApresentado;
    }

    public void registrarTipApresentado() {
        if (isConcluida()) tipApresentado = true;
    }

    public void reiniciar() {
        fase = FaseConclusaoModelagem.INCOMPLETA;
        tipApresentado = false;
    }
}
