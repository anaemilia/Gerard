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
    Map<String, Object> reiniciar();
}
