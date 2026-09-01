package gerard.dominio.campoaditivo.situacao;

/** Diagnóstico factual estruturado, sem mensagem final de interface. */
public final class DiagnosticoSituacao {
    private final String codigo;
    private final String detalhe;

    public DiagnosticoSituacao(String codigo, String detalhe) {
        this.codigo = FamiliaObjeto.obrigatorio(codigo, "código do diagnóstico não pode ser vazio");
        this.detalhe = detalhe == null ? "" : detalhe.trim();
    }

    public String getCodigo() { return codigo; }
    public String getDetalhe() { return detalhe; }

    @Override
    public String toString() {
        return detalhe.isEmpty() ? codigo : codigo + ":" + detalhe;
    }
}
