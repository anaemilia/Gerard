package gerard.semantica.papel;

import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.ValorNumerico;

/** Papel quantitativo com universo numérico explicitamente declarado. */
public final class PapelQuantitativo implements PapelSemantico {
    private final String chave;
    private final String nomeConceitual;
    private final DominioNumerico dominio;

    public PapelQuantitativo(String chave, String nomeConceitual,
                             DominioNumerico dominio) {
        this.chave = chave == null ? "" : chave.trim();
        this.nomeConceitual = nomeConceitual == null ? "" : nomeConceitual.trim();
        this.dominio = dominio == null ? DominioNumerico.NATURAIS : dominio;
    }

    public String getChave() { return chave; }
    public String getNomeConceitual() { return nomeConceitual; }
    public DominioNumerico getDominio() { return dominio; }

    public boolean aceita(ValorNumerico valor) {
        return valor != null && valor.getDominio() == dominio;
    }

    public boolean aceita(int valor) {
        return dominio.aceita(valor);
    }
}
