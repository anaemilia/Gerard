package gerard.ui.interacao;

/**
 * Decide QUAL mensagem a anotação flutuante da tela mostra. Há cinco fontes possíveis e a prioridade entre elas é
 * conhecimento único, escrito aqui: o questionamento persistente de posicionamento vence o aviso de limite de
 * quantidade, que vence o aviso de sinal divergente, que vence a dica de posicionamento (AG_AE), que vence o texto de
 * mouse-over. Cada fonte entra só com a pergunta "ainda é válida?", já respondida por quem a possui.
 *
 * Sem Swing, sem estado, sem efeito: o mesmo conjunto de entradas dá sempre a mesma fonte.
 */
public final class DecisaoAnotacaoMouseOver {
    public enum Fonte {
        QUESTIONAMENTO_PERSISTENTE,
        LIMITE_QUANTIDADE,
        SINAL_DIVERGENTE,
        DICA_POSICIONAMENTO,
        MOUSE_OVER,
        NENHUMA
    }

    private DecisaoAnotacaoMouseOver() {
    }

    public static Fonte decidir(boolean questionamentoValido, boolean limiteQuantidadeValido,
            boolean sinalDivergenteValido, boolean dicaPosicionamentoValida, boolean mouseOverAtivo) {
        if (questionamentoValido) {
            return Fonte.QUESTIONAMENTO_PERSISTENTE;
        }
        if (limiteQuantidadeValido) {
            return Fonte.LIMITE_QUANTIDADE;
        }
        if (sinalDivergenteValido) {
            return Fonte.SINAL_DIVERGENTE;
        }
        if (dicaPosicionamentoValida) {
            return Fonte.DICA_POSICIONAMENTO;
        }
        return mouseOverAtivo ? Fonte.MOUSE_OVER : Fonte.NENHUMA;
    }
}
