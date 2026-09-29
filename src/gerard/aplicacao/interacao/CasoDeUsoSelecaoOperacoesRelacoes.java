package gerard.aplicacao.interacao;

import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;
import gerard.interacao.selecao.AlvoSeletoresOperacaoRelacoes;
import gerard.interacao.selecao.HandlerInteracaoSeletoresOperacaoRelacoes;

/**
 * Caso de uso portátil da seleção ordenada das operações de Relações.
 * Apenas sequencia colaboradores; não interpreta o resultado factual.
 */
public final class CasoDeUsoSelecaoOperacoesRelacoes {

    private final HandlerInteracaoSeletoresOperacaoRelacoes handler;
    private final AlvoSeletoresOperacaoRelacoes alvo;
    private final PortaFeedbackEscolhaOperacaoRelacao feedback;
    private final PortaPersistenciaAcaoInstrumental persistencia;
    private final PortaEncaminhamentoAcaoInstrumental encaminhamento;
    private final PortaLimpezaFocoAposEscolhaOperacao limpezaFoco;
    private final PortaReavaliacaoConclusaoAposEscolhaOperacao reavaliacaoConclusao;

    public CasoDeUsoSelecaoOperacoesRelacoes(
            HandlerInteracaoSeletoresOperacaoRelacoes handler,
            AlvoSeletoresOperacaoRelacoes alvo,
            PortaFeedbackEscolhaOperacaoRelacao feedback,
            PortaPersistenciaAcaoInstrumental persistencia,
            PortaEncaminhamentoAcaoInstrumental encaminhamento,
            PortaLimpezaFocoAposEscolhaOperacao limpezaFoco,
            PortaReavaliacaoConclusaoAposEscolhaOperacao reavaliacaoConclusao) {
        if (handler == null || alvo == null || feedback == null
                || persistencia == null || encaminhamento == null
                || limpezaFoco == null
                || reavaliacaoConclusao == null) {
            throw new IllegalArgumentException("portas obrigatorias");
        }
        this.handler = handler;
        this.alvo = alvo;
        this.feedback = feedback;
        this.persistencia = persistencia;
        this.encaminhamento = encaminhamento;
        this.limpezaFoco = limpezaFoco;
        this.reavaliacaoConclusao = reavaliacaoConclusao;
    }

    public boolean processar(int posicaoX, int posicaoY) {
        ResultadoEscolhaOperacaoModelagem resultado =
                handler.processar(alvo, posicaoX, posicaoY);
        if (resultado == null) {
            return false;
        }
        feedback.materializar(resultado.getFeedback());
        persistencia.persistir(resultado);
        encaminhamento.encaminhar(resultado);
        limpezaFoco.limparFoco();
        reavaliacaoConclusao.reavaliarConclusao();
        return true;
    }
}
