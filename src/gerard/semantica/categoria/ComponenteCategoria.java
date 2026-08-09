package gerard.semantica.categoria;

import gerard.semantica.papel.DescritorPapelQuantitativo;
import java.util.List;

/** Componente simples ou composto de uma categoria aditiva. */
public interface ComponenteCategoria {
    List<DescritorPapelQuantitativo> obterPapeis();
    List<RestricaoSemantica> obterRestricoes();
}
