package gerard.semantica.entidade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GrupoPersonagens implements EntidadeSemantica {
    private final String id;
    private final String nome;
    private final List<Personagem> membros;

    public GrupoPersonagens(String id, String nome, List<Personagem> membros) {
        this.id = id == null ? "" : id.trim();
        this.nome = nome == null ? "" : nome.trim();
        this.membros = Collections.unmodifiableList(
                new ArrayList<Personagem>(membros == null
                        ? Collections.<Personagem>emptyList() : membros));
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public List<Personagem> getMembros() { return membros; }
}
