package gerard.semantica.categoria;

import gerard.semantica.papel.PapelQuantitativo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CategoriaSimples implements ComponenteCategoria {
    private final List<PapelQuantitativo> papeis;
    private final List<RestricaoSemantica> restricoes;
    private final RelacaoSemantica relacao;

    public CategoriaSimples(List<PapelQuantitativo> papeis,
                            List<RestricaoSemantica> restricoes,
                            RelacaoSemantica relacao) {
        this.papeis = Collections.unmodifiableList(
                new ArrayList<PapelQuantitativo>(papeis == null
                        ? Collections.<PapelQuantitativo>emptyList() : papeis));
        this.restricoes = Collections.unmodifiableList(
                new ArrayList<RestricaoSemantica>(restricoes == null
                        ? Collections.<RestricaoSemantica>emptyList() : restricoes));
        this.relacao = relacao;
    }

    public List<PapelQuantitativo> obterPapeis() { return papeis; }
    public List<RestricaoSemantica> obterRestricoes() { return restricoes; }
    public RelacaoSemantica getRelacao() { return relacao; }
}
