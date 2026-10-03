package gerard.dominio.campoaditivo.ajuda;

/** Historinha com ilustração (referência opaca de conteúdo, resolvida por cada plataforma). */
public final class ApoioHistorinhaIlustrada implements ApoioVisual {
    private final HistorinhaAjudaVisual historinha;

    public ApoioHistorinhaIlustrada(HistorinhaAjudaVisual historinha) {
        if (historinha == null) {
            throw new IllegalArgumentException("historinha é obrigatória");
        }
        this.historinha = historinha;
    }

    public String getTipo() { return TIPO_HISTORINHA_ILUSTRADA; }
    public String getIdentificador() { return historinha.getIdentificador(); }
    public String getReferenciaConteudo() { return historinha.getReferenciaConteudo(); }
}
