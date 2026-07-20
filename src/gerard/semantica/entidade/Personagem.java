package gerard.semantica.entidade;

/** Nome de pessoa ou participante contextual, sem fixar seu papel matemático. */
public final class Personagem implements EntidadeSemantica {
    private final String id;
    private final String nome;

    public Personagem(String id, String nome) {
        this.id = id == null ? "" : id.trim();
        this.nome = nome == null ? "" : nome.trim();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
}
