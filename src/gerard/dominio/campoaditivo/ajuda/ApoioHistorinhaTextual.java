package gerard.dominio.campoaditivo.ajuda;

/**
 * Historinha feita do texto da própria situação-problema (fonte curada), usada quando não
 * há ilustração cadastrada para a situação. O texto não é composto livremente: é o
 * enunciado da situação.
 */
public final class ApoioHistorinhaTextual implements ApoioVisual {
    private final String idSituacaoCurada;
    private final String texto;

    public ApoioHistorinhaTextual(String idSituacaoCurada, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("texto da situação é obrigatório");
        }
        this.idSituacaoCurada = idSituacaoCurada == null ? "" : idSituacaoCurada;
        this.texto = texto.trim();
    }

    public String getTipo() { return TIPO_HISTORINHA_TEXTUAL; }
    public String getIdentificador() { return "texto:" + idSituacaoCurada; }
    public String getTexto() { return texto; }
}
