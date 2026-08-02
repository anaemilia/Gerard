package gerard.dominio.campoaditivo;

/**
 * Contexto de rastreabilidade de uma ação sobre um papel quantitativo —
 * os identificadores que ligam um evento semântico à tentativa específica
 * de resolução em que ocorreu. Uma mesma situação-problema pode gerar
 * várias tentativas, cada uma com ações, verbalizações, estratégias e
 * hipóteses próprias; sem este contexto um evento fica sem rastro de a
 * qual tentativa pertence.
 *
 * Todos os campos são opcionais (podem ser {@code null}) — o objeto de
 * domínio não exige que o chamador já tenha toda a informação de sessão
 * disponível; NAO_INFORMADO cobre o caso de uso isolado (testes, uso sem
 * infraestrutura de sessão).
 */
public final class ContextoAcao {

    public static final ContextoAcao NAO_INFORMADO = new ContextoAcao(null, null, null, null, null);

    private final String idSessao;
    private final String idUsuarioLocal;
    private final String idTentativa;
    private final String idSituacaoProblema;
    private final String idRepresentacao;

    public ContextoAcao(String idSessao, String idUsuarioLocal, String idTentativa,
                         String idSituacaoProblema, String idRepresentacao) {
        this.idSessao = idSessao;
        this.idUsuarioLocal = idUsuarioLocal;
        this.idTentativa = idTentativa;
        this.idSituacaoProblema = idSituacaoProblema;
        this.idRepresentacao = idRepresentacao;
    }

    public String getIdSessao() { return idSessao; }
    public String getIdUsuarioLocal() { return idUsuarioLocal; }
    public String getIdTentativa() { return idTentativa; }
    public String getIdSituacaoProblema() { return idSituacaoProblema; }
    public String getIdRepresentacao() { return idRepresentacao; }
}
