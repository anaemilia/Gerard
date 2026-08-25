package gerard.adaptacao;

/** Descritor abstrato de um apoio; não contém texto final nem componente de UI. */
public final class ItemRepertorioAjuda {

    private final String codigo;
    private final String tipoFuncional;
    private final String modalidadeEntrega;
    private final String chaveConteudo;

    public ItemRepertorioAjuda(
            String codigo,
            String tipoFuncional,
            String modalidadeEntrega,
            String chaveConteudo) {
        this.codigo = textoObrigatorio(codigo, "código");
        this.tipoFuncional = textoObrigatorio(tipoFuncional, "tipo funcional");
        this.modalidadeEntrega = textoObrigatorio(modalidadeEntrega, "modalidade de entrega");
        this.chaveConteudo = textoObrigatorio(chaveConteudo, "chave de conteúdo");
    }

    public String getCodigo() { return codigo; }
    public String getTipoFuncional() { return tipoFuncional; }
    public String getModalidadeEntrega() { return modalidadeEntrega; }
    public String getChaveConteudo() { return chaveConteudo; }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }
}
