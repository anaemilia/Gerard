package gerard.aplicacao.portabilidade;

import java.util.Map;

/** Porta de uma tentativa aditiva exposta à infraestrutura HTTP. */
public interface ServicoAtividadeWeb {
    Map<String, Object> estadoAtual();
    Map<String, Object> proporValor(String papelId, int valor);
    Map<String, Object> reiniciar();
}
