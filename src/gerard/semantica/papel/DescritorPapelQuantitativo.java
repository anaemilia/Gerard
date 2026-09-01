package gerard.semantica.papel;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoPosicionamentoPapelQuantitativo;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.ValorNumerico;

/** Papel quantitativo com universo numérico explicitamente declarado. */
public final class DescritorPapelQuantitativo implements PapelSemantico {
    private final String chave;
    private final String nomeConceitual;
    private final DominioNumerico dominio;

    public DescritorPapelQuantitativo(String chave, String nomeConceitual,
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

    /**
     * Compatibilidade semântica para ocupar um papel do diagrama. As chaves
     * genéricas designam famílias; papéis específicos exigem igualdade.
     */
    public boolean podeOcupar(DescritorPapelQuantitativo destino) {
        if (destino == null || chave.length() == 0
                || destino.getChave().length() == 0
                || "papel.valor".equals(chave)
                || "papel.valor".equals(destino.getChave())) {
            return false;
        }
        if (chave.equals(destino.getChave())) {
            return true;
        }
        return pertenceAFamiliaGenerica("papel.transformacao", destino)
                || pertenceAFamiliaGenerica("papel.relacao", destino)
                || pertenceAFamiliaGenerica("papel.parte", destino);
    }

    public RegistroAcaoPosicionamentoPapelQuantitativo avaliarPosicionamento(
            DescritorPapelQuantitativo destino,
            TipoSituacaoAditiva categoria,
            ContextoAcaoInstrumental contexto) {
        return new RegistroAcaoPosicionamentoPapelQuantitativo(
                categoria, this, destino, podeOcupar(destino), contexto);
    }

    private boolean pertenceAFamiliaGenerica(
            String chaveFamilia,
            DescritorPapelQuantitativo destino) {
        return chaveFamilia.equals(chave)
                && destino.getChave().startsWith(chaveFamilia);
    }
}
