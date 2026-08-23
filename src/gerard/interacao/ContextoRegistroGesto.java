package gerard.interacao;

/**
 * Contexto factual que permite correlacionar um gesto à sessão em que foi
 * observado. Não contém avaliação, diagnóstico ou interpretação semântica.
 */
public final class ContextoRegistroGesto {
    private final String sessaoId;
    private final String usuarioId;
    private final String problemaId;
    private final String tentativaId;

    public ContextoRegistroGesto(String sessaoId, String usuarioId,
            String problemaId, String tentativaId) {
        this.sessaoId = normalizar(sessaoId);
        this.usuarioId = normalizar(usuarioId);
        this.problemaId = normalizar(problemaId);
        this.tentativaId = normalizar(tentativaId);
    }

    public String getSessaoId() { return sessaoId; }
    public String getUsuarioId() { return usuarioId; }
    public String getProblemaId() { return problemaId; }
    public String getTentativaId() { return tentativaId; }

    private static String normalizar(String valor) {
        return valor == null ? "" : valor;
    }
}
