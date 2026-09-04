package gerard.aplicacao.portabilidade;

import java.util.Map;

/** Porta de uma tentativa aditiva exposta à infraestrutura HTTP. */
public interface ServicoAtividadeWeb {
    Map<String, Object> estadoAtual();
    Map<String, Object> proporValor(String papelId, int valor);
    /**
     * Posiciona um papel conhecido (nunca a incógnita) com o valor curado —
     * ação que soltar um elemento não-incógnita do enunciado sobre sua caixa
     * dispara (protocolo de mouse é posicionar; ver regra 6 de
     * gerard-consistencia-estado e ServicoAtividadeWebComposicao).
     */
    Map<String, Object> posicionarValorConhecido(String papelId);
    /**
     * Engata o "?" da incógnita na sua caixa — ação que soltar o token da
     * incógnita do enunciado sobre sua caixa dispara (protocolo mouse-texto,
     * Main.java). Não atribui valor: só marca, no servidor (nunca só no
     * cliente — ver projetarCena/figura.engatada), que a caixa deve mostrar
     * "?" até a digitação real via proporValor.
     */
    Map<String, Object> engatarIncognita(String papelId);
    Map<String, Object> reiniciar();
}
