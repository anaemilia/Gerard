package gerard.dominio.campoaditivo.ajuda;

/**
 * Descreve semanticamente uma narrativa visual de ajuda sem assumir a
 * tecnologia usada para apresentá-la. A referência de conteúdo é opaca:
 * Swing, web ou mobile podem resolvê-la com adaptadores diferentes.
 */
public final class HistorinhaAjudaVisual {

    private final String identificador;
    private final String idSituacaoCurada;
    private final String referenciaConteudo;

    public HistorinhaAjudaVisual(
            String identificador,
            String idSituacaoCurada,
            String referenciaConteudo) {
        this.identificador = exigirTexto(identificador, "identificador");
        this.idSituacaoCurada = exigirTexto(idSituacaoCurada, "idSituacaoCurada");
        this.referenciaConteudo = exigirTexto(referenciaConteudo, "referenciaConteudo");
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getIdSituacaoCurada() {
        return idSituacaoCurada;
    }

    public String getReferenciaConteudo() {
        return referenciaConteudo;
    }

    private static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.trim().length() == 0) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
        return valor;
    }
}
