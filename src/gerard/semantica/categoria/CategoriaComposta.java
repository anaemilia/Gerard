package gerard.semantica.categoria;

import gerard.semantica.papel.PapelQuantitativo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Composite de esquemas simples ou de outros compostos. */
public final class CategoriaComposta implements ComponenteCategoria {
    private final List<ComponenteCategoria> componentes;

    public CategoriaComposta(List<ComponenteCategoria> componentes) {
        this.componentes = Collections.unmodifiableList(
                new ArrayList<ComponenteCategoria>(componentes == null
                        ? Collections.<ComponenteCategoria>emptyList() : componentes));
    }

    public List<ComponenteCategoria> getComponentes() { return componentes; }

    public List<PapelQuantitativo> obterPapeis() {
        List<PapelQuantitativo> resultado = new ArrayList<PapelQuantitativo>();
        for (ComponenteCategoria componente : componentes) {
            resultado.addAll(componente.obterPapeis());
        }
        return Collections.unmodifiableList(resultado);
    }

    public List<RestricaoSemantica> obterRestricoes() {
        List<RestricaoSemantica> resultado = new ArrayList<RestricaoSemantica>();
        for (ComponenteCategoria componente : componentes) {
            resultado.addAll(componente.obterRestricoes());
        }
        return Collections.unmodifiableList(resultado);
    }
}
