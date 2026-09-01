package gerard.pesquisador.auditoria;

/**
 * Classificação de UMA avaliação como gesto canônico do usuário ou
 * reavaliação reativa — determina se ela pode alterar estado pedagógico
 * real no Modelador ou só aparece no log técnico. Construída a partir
 * de {@link OrigemAvaliacao#isCanonica()}; counts_for_* são todos iguais a
 * `canonical` nesta versão (não há hoje um caso em que uma avaliação seja
 * canônica pra um efeito e não pra outro).
 */
public final class ClassificacaoEvento {
    private final OrigemAvaliacao origem;
    private final boolean canonical;
    private final String idempotencyKey;

    public ClassificacaoEvento(OrigemAvaliacao origem, String idempotencyKey) {
        this.origem = origem;
        this.canonical = origem != null && origem.isCanonica();
        this.idempotencyKey = idempotencyKey;
    }

    public OrigemAvaliacao getOrigem() {
        return origem;
    }

    public boolean isCanonical() {
        return canonical;
    }

    public boolean isReactiveEvaluation() {
        return !canonical;
    }

    public boolean isCountsForUserProfile() {
        return canonical;
    }

    public boolean isCountsForErrorSequence() {
        return canonical;
    }

    public boolean isCountsForCaseBase() {
        return canonical;
    }

    public boolean isCountsForRuleLearning() {
        return canonical;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
