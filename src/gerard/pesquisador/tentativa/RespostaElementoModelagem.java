package gerard.pesquisador.tentativa;

public final class RespostaElementoModelagem {
    private final String elemento;
    private final String papelSemantico;
    private final String explicacao;
    private final String dificuldade;

    public RespostaElementoModelagem(String elemento, String papelSemantico, String explicacao, String dificuldade) {
        this.elemento = limpar(elemento);
        this.papelSemantico = limpar(papelSemantico);
        this.explicacao = limpar(explicacao);
        this.dificuldade = limpar(dificuldade);
    }

    private static String limpar(String valor) { return valor == null ? "" : valor.trim(); }
    public String getElemento() { return elemento; }
    public String getPapelSemantico() { return papelSemantico; }
    public String getExplicacao() { return explicacao; }
    public String getDificuldade() { return dificuldade; }
}
