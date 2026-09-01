package gerard.pesquisador.auditoria;

/**
 * Vocabulário controlado de origem de uma avaliação (ver PROMPT de correção
 * 2026-07-31) — de onde veio a chamada que gerou a avaliação factual.
 * Só {@link #SOLTURA_USUARIO}, {@link #SELECAO_CATEGORIA},
 * {@link #SELECAO_SINAL}, {@link #QUANTIFICACAO} e
 * {@link #SOLICITACAO_AJUDA} representam uma decisão efetiva e observável
 * do usuário (gesto canônico); as demais são reavaliação interna
 * (reativa) — ver {@link #isCanonica()}.
 */
public enum OrigemAvaliacao {
    SOLTURA_USUARIO(true),
    SELECAO_CATEGORIA(true),
    SELECAO_SINAL(true),
    QUANTIFICACAO(true),
    SOLICITACAO_AJUDA(true),
    SINCRONIZACAO_REPRESENTACOES(false),
    REAVALIACAO_CONSISTENCIA(false),
    ATUALIZACAO_DIAGRAMA(false),
    ATUALIZACAO_REPRESENTACAO(false),
    OUTRO(false);

    private final boolean canonica;

    OrigemAvaliacao(boolean canonica) {
        this.canonica = canonica;
    }

    public boolean isCanonica() {
        return canonica;
    }

    /** Valor de texto usado no log (schema), minúsculo com underscore. */
    public String paraTexto() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
