package gerard.semantica.situacao;

import gerard.semantica.categoria.EsquemaCategoriaAditiva;
import gerard.semantica.contexto.ContextoSituacao;
import gerard.semantica.elemento.ElementoSemantico;
import gerard.semantica.entidade.EntidadeSemantica;
import gerard.semantica.pista.OcorrenciaPista;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Instância concreta que liga texto, contexto e elementos ao esquema abstrato. */
public final class InstanciaSituacaoAditiva {
    private final String id;
    private final String enunciado;
    private final EsquemaCategoriaAditiva esquema;
    private final ContextoSituacao contexto;
    private final List<EntidadeSemantica> entidades;
    private final List<ElementoSemantico> elementos;
    private final List<OcorrenciaPista> pistas;

    public InstanciaSituacaoAditiva(String id, String enunciado,
                                    EsquemaCategoriaAditiva esquema,
                                    ContextoSituacao contexto,
                                    List<EntidadeSemantica> entidades,
                                    List<ElementoSemantico> elementos,
                                    List<OcorrenciaPista> pistas) {
        this.id = id == null ? "" : id.trim();
        this.enunciado = enunciado == null ? "" : enunciado.trim();
        this.esquema = esquema;
        this.contexto = contexto;
        this.entidades = imutavel(entidades);
        this.elementos = imutavel(elementos);
        this.pistas = imutavel(pistas);
    }

    public String getId() { return id; }
    public String getEnunciado() { return enunciado; }
    public EsquemaCategoriaAditiva getEsquema() { return esquema; }
    public ContextoSituacao getContexto() { return contexto; }
    public List<EntidadeSemantica> getEntidades() { return entidades; }
    public List<ElementoSemantico> getElementos() { return elementos; }
    public List<OcorrenciaPista> getPistas() { return pistas; }

    private <T> List<T> imutavel(List<T> valores) {
        return Collections.unmodifiableList(new ArrayList<T>(
                valores == null ? Collections.<T>emptyList() : valores));
    }
}
