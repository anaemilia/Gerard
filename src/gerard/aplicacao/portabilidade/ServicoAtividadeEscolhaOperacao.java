package gerard.aplicacao.portabilidade;

import java.util.Map;

/**
 * Porta de uma tentativa web de "escolher a operação" (soma/subtração) entre
 * papéis já revelados — paradigma distinto de {@link ServicoAtividade}
 * (que modela "propor um valor para um papel incógnito"). Implementado por
 * {@link ServicoAtividadeComposicaoTransformacoes} (duas etapas
 * sequenciais) e {@link ServicoAtividadeComposicaoRelacoes} (uma etapa).
 */
public interface ServicoAtividadeEscolhaOperacao {
    Map<String, Object> estadoAtual();
    Map<String, Object> escolherOperacao(String seletor, String operacao);
    /**
     * Posiciona um papel conhecido com o valor curado — ação que soltar um
     * elemento do enunciado sobre sua caixa dispara (protocolo de mouse é
     * posicionar; ver ServicoAtividadeComposicao). {@code origemPapelId} é
     * o vínculo semântico do elemento de texto arrastado (pode ser
     * {@code null}); a implementação valida a compatibilidade com o
     * papel-alvo via AvaliadorOrigemDestino antes de aplicar a posição.
     */
    Map<String, Object> posicionarValorConhecido(String papelId, String origemPapelId);
    Map<String, Object> reiniciar();
}
