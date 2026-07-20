package gerard.semantica.numero;

/** Incógnita que preserva o universo numérico esperado pelo papel semântico. */
public final class ValorDesconhecido implements ValorNumerico {
    private final DominioNumerico dominio;

    public ValorDesconhecido(DominioNumerico dominio) {
        this.dominio = dominio == null ? DominioNumerico.NATURAIS : dominio;
    }

    public DominioNumerico getDominio() { return dominio; }
    public boolean ehConhecido() { return false; }
    public Integer valorOuNull() { return null; }
    public String formatar(boolean explicitarSinalPositivo) { return "?"; }

    @Override
    public String toString() { return "?"; }
}
