package gerard.aplicacao.portabilidade;

import java.util.Map;

/**
 * Porta de uma tentativa web de "escolher a operação" (soma/subtração) entre
 * papéis já revelados — paradigma distinto de {@link ServicoAtividadeWeb}
 * (que modela "propor um valor para um papel incógnito"). Implementado por
 * {@link ServicoAtividadeWebComposicaoTransformacoes} (duas etapas
 * sequenciais) e {@link ServicoAtividadeWebComposicaoRelacoes} (uma etapa).
 */
public interface ServicoAtividadeWebEscolhaOperacao {
    Map<String, Object> estadoAtual();
    Map<String, Object> escolherOperacao(String seletor, String operacao);
    /**
     * Posiciona um papel conhecido com o valor curado — ação que soltar um
     * elemento do enunciado sobre sua caixa dispara (protocolo de mouse é
     * posicionar; ver ServicoAtividadeWebComposicao). {@code origemPapelId} é
     * o vínculo semântico do elemento de texto arrastado (pode ser
     * {@code null}); a implementação valida a compatibilidade com o
     * papel-alvo via AvaliadorOrigemDestinoWeb antes de aplicar a posição.
     */
    Map<String, Object> posicionarValorConhecido(String papelId, String origemPapelId);
    Map<String, Object> reiniciar();
}
