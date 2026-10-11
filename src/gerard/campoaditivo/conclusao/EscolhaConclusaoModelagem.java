package gerard.campoaditivo.conclusao;

/**
 * O que o participante pode escolher depois que a modelagem ficou azul: responder Sim ou Não à pergunta do tip, ou
 * clicar no selo ("Próxima tarefa"). A decisão pertence ao domínio — o desktop (Swing) e a web (React) só realizam o
 * que ela manda: Sim e Próxima tarefa iniciam uma nova situação; Não permanece na atual. É decisão de interface, não
 * de modelagem, e por isso não gera registro no log (decisão da usuária, 2026-10-10).
 */
public enum EscolhaConclusaoModelagem {
    SIM(true),
    NAO(false),
    PROXIMA_TAREFA(true);

    /** Pausa entre a confirmação visual (azul) e a pergunta/selo; mesma nos dois clientes. */
    public static final int ATRASO_DECISAO_MS = 1150;

    private final boolean iniciaNovaSituacao;

    EscolhaConclusaoModelagem(boolean iniciaNovaSituacao) {
        this.iniciaNovaSituacao = iniciaNovaSituacao;
    }

    public boolean iniciaNovaSituacao() {
        return iniciaNovaSituacao;
    }

    /** Interpreta o código recebido pela API; código desconhecido é erro do chamador. */
    public static EscolhaConclusaoModelagem de(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("escolha da conclusão é obrigatória");
        }
        try {
            return valueOf(codigo.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException erro) {
            throw new IllegalArgumentException("escolha da conclusão desconhecida: " + codigo);
        }
    }
}
