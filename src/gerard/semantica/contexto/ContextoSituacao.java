package gerard.semantica.contexto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Contexto linguístico e objetos quantificados da situação-problema. */
public final class ContextoSituacao {
    private final String descricao;
    private final List<ReferenteContextual> referentes;

    public ContextoSituacao(String descricao, List<ReferenteContextual> referentes) {
        this.descricao = descricao == null ? "" : descricao.trim();
        this.referentes = Collections.unmodifiableList(
                new ArrayList<ReferenteContextual>(referentes == null
                        ? Collections.<ReferenteContextual>emptyList() : referentes));
    }

    public String getDescricao() { return descricao; }
    public List<ReferenteContextual> getReferentes() { return referentes; }
}
