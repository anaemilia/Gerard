package gerard.campoaditivo.conclusao;

import java.util.Collection;
import java.util.List;

/**
 * Dono do ciclo VISUAL do azul no desktop: avalia o diagrama corrente (incompleto, concluído, deixou de
 * estar concluído), controla o destaque e impede a repetição contínua do tip. Não decide a FASE da
 * atividade: o que está concluído aqui pode voltar a incompleto durante a exploração, enquanto a fase
 * (modelagem/exploratória) pertence à tentativa, que o observa por {@link gerard.dominio.campoaditivo.FonteDeConclusao}
 * e se encerra uma única vez ({@code TentativaModelagemAditiva.encerrarSeConcluida}).
 */
public final class ControladorConclusaoModelagem
        implements gerard.dominio.campoaditivo.FonteDeConclusao {
    private final AvaliadorConclusaoModelagem avaliador =
            new AvaliadorConclusaoModelagem();
    private FaseConclusaoModelagem fase = FaseConclusaoModelagem.INCOMPLETA;
    private boolean tipApresentado;

    public AtualizacaoConclusaoModelagem atualizar(
            Collection<String> papeisEsperados,
            List<EstadoPosicionamentoModelagem> posicionamentos) {
        return atualizar(papeisEsperados, posicionamentos, true);
    }

    public AtualizacaoConclusaoModelagem atualizar(
            Collection<String> papeisEsperados,
            List<EstadoPosicionamentoModelagem> posicionamentos,
            boolean requisitosAdicionaisSatisfeitos) {
        if (isConcluida()) return AtualizacaoConclusaoModelagem.CONTINUA_CONCLUIDA;
        FaseConclusaoModelagem novaFase = avaliador.avaliar(
                papeisEsperados, posicionamentos);
        if (novaFase == FaseConclusaoModelagem.CONCLUIDA
                && !requisitosAdicionaisSatisfeitos) {
            novaFase = FaseConclusaoModelagem.INCOMPLETA;
        }
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

    @Override
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
