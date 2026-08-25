package gerard.dominio.atividade;

/** Resultado factual produzido pelo proprietário semântico da ação. */
public enum ResultadoAvaliacaoAcaoInstrumental {
    CORRETA("C"),
    ERRADA("E"),
    NAO_APLICAVEL("-");

    private final String codigoCe;

    ResultadoAvaliacaoAcaoInstrumental(String codigoCe) {
        this.codigoCe = codigoCe;
    }

    public String getCodigoCe() {
        return codigoCe;
    }
}
