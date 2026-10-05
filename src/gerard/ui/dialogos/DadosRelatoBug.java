package gerard.ui.dialogos;

/** Contexto da atividade que acompanha um relato de bug; montado por quem abre o diálogo. */
public final class DadosRelatoBug {
    private final String situacaoId;
    private final String categoria;
    private final String representacoes;
    private final String idiomaInterface;
    private final String idiomaSituacao;
    private final String enunciadoAtual;

    public DadosRelatoBug(String situacaoId, String categoria, String representacoes, String idiomaInterface,
            String idiomaSituacao, String enunciadoAtual) {
        this.situacaoId = situacaoId;
        this.categoria = categoria;
        this.representacoes = representacoes;
        this.idiomaInterface = idiomaInterface;
        this.idiomaSituacao = idiomaSituacao;
        this.enunciadoAtual = enunciadoAtual;
    }

    public String getSituacaoId() { return situacaoId; }
    public String getCategoria() { return categoria; }
    public String getRepresentacoes() { return representacoes; }
    public String getIdiomaInterface() { return idiomaInterface; }
    public String getIdiomaSituacao() { return idiomaSituacao; }
    public String getEnunciadoAtual() { return enunciadoAtual; }
}
