package gerard.semantica.categoria;

import gerard.semantica.papel.PapelQuantitativo;
import java.util.List;

/** Componente simples ou composto de uma categoria aditiva. */
public interface ComponenteCategoria {
    List<PapelQuantitativo> obterPapeis();
    List<RestricaoSemantica> obterRestricoes();
}
