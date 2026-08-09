package gerard.semantica.categoria;

import gerard.semantica.papel.DescritorPapelQuantitativo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CategoriaSimples implements ComponenteCategoria {
    private final List<DescritorPapelQuantitativo> papeis;
    private final List<RestricaoSemantica> restricoes;
    private final RelacaoSemantica relacao;

    public CategoriaSimples(List<DescritorPapelQuantitativo> papeis,
                            List<RestricaoSemantica> restricoes,
                            RelacaoSemantica relacao) {
        this.papeis = Collections.unmodifiableList(
                new ArrayList<DescritorPapelQuantitativo>(papeis == null
                        ? Collections.<DescritorPapelQuantitativo>emptyList() : papeis));
        this.restricoes = Collections.unmodifiableList(
                new ArrayList<RestricaoSemantica>(restricoes == null
                        ? Collections.<RestricaoSemantica>emptyList() : restricoes));
        this.relacao = relacao;
    }

    public List<DescritorPapelQuantitativo> obterPapeis() { return papeis; }
    public List<RestricaoSemantica> obterRestricoes() { return restricoes; }
    public RelacaoSemantica getRelacao() { return relacao; }
}
