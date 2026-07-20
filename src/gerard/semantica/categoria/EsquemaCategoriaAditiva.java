package gerard.semantica.categoria;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.papel.PapelQuantitativo;
import java.util.List;

/** Categoria abstrata como composição explícita de papéis e relações. */
public final class EsquemaCategoriaAditiva {
    private final TipoSituacaoAditiva tipo;
    private final String nome;
    private final ComponenteCategoria raiz;
    private final List<PapelQuantitativo> papeisCompartilhados;

    public EsquemaCategoriaAditiva(TipoSituacaoAditiva tipo, String nome,
                                   ComponenteCategoria raiz,
                                   List<PapelQuantitativo> papeisCompartilhados) {
        this.tipo = tipo;
        this.nome = nome == null ? "" : nome.trim();
        this.raiz = raiz;
        this.papeisCompartilhados = papeisCompartilhados;
    }

    public TipoSituacaoAditiva getTipo() { return tipo; }
    public String getNome() { return nome; }
    public ComponenteCategoria getRaiz() { return raiz; }
    public List<PapelQuantitativo> obterPapeis() { return raiz.obterPapeis(); }

    public PapelQuantitativo obterPapelCompartilhado(int indice) {
        return papeisCompartilhados != null && indice >= 0
                && indice < papeisCompartilhados.size()
                ? papeisCompartilhados.get(indice) : null;
    }

    public DominioNumerico obterDominioCompartilhado(int indice) {
        PapelQuantitativo papel = obterPapelCompartilhado(indice);
        return papel == null ? DominioNumerico.NATURAIS : papel.getDominio();
    }
}
