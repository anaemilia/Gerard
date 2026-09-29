package gerard.interacao.unidades;

/**
 * Protocolo portátil do clique nos controles +/− do material concreto.
 *
 * Conhece somente a sequência do gesto: o controle de remover tem prioridade
 * sobre o de adicionar; a liberação pela modelagem é consultada antes do
 * limite; o limite antes da aplicação. Não conhece Swing, geometria,
 * mensagens, cursor, log nem a regra de limite, que pertencem às
 * implementações da porta e à apresentação da plataforma.
 */
public final class HandlerInteracaoControlesUnidades {

    public <C> ResultadoControleUnidades<C> processar(
            AlvoControlesUnidades<C> alvo, int posicaoX, int posicaoY) {
        if (alvo == null) {
            return null;
        }
        OperacaoControleUnidade[] prioridade = {
            OperacaoControleUnidade.REMOVER, OperacaoControleUnidade.ADICIONAR };
        for (OperacaoControleUnidade operacao : prioridade) {
            C controle = alvo.localizarControle(operacao, posicaoX, posicaoY);
            if (controle == null) {
                continue;
            }
            if (!alvo.alteracaoLiberadaPelaModelagem()) {
                return new ResultadoControleUnidades<C>(operacao,
                        ResultadoControleUnidades.Desfecho.BLOQUEADO_PELA_MODELAGEM,
                        controle, null);
            }
            if (!alvo.podeAplicar(controle, operacao)) {
                return new ResultadoControleUnidades<C>(operacao,
                        ResultadoControleUnidades.Desfecho.LIMITE_ATINGIDO,
                        controle, null);
            }
            ResultadoAplicacaoControleUnidade aplicacao = alvo.aplicar(controle, operacao);
            if (aplicacao == null) {
                throw new IllegalStateException("aplicar deve devolver o fato da aplicacao");
            }
            return new ResultadoControleUnidades<C>(operacao,
                    ResultadoControleUnidades.Desfecho.APLICADO, controle, aplicacao);
        }
        return null;
    }
}
