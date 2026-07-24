package gerard.agente.modelousuario;

/**
 * Dimensão 3 do Modelo do Usuário (Quadro 5.60): identificação única do
 * aluno. Ver gerard-modelo-usuario/SKILL.md.
 *
 * "fotoCaminho" não está no Quadro 5.60 original (que lista só nome, id,
 * idade, sexo) — adicionado em 2026-07-22 por pedido direto do usuário
 * (espaço de upload de foto ao lado do nome no cadastro). Caminho absoluto
 * para o arquivo de imagem copiado para ~/Gerard/fotos/ — ver
 * RepositorioModeloUsuario.
 */
public class PerfilAluno {
    private String id;
    private String nome;
    private Integer idade;
    private Genero sexo;
    private String fotoCaminho;

    public PerfilAluno(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public Genero getSexo() {
        return sexo;
    }

    public void setSexo(Genero sexo) {
        this.sexo = sexo;
    }

    public String getFotoCaminho() {
        return fotoCaminho;
    }

    public void setFotoCaminho(String fotoCaminho) {
        this.fotoCaminho = fotoCaminho;
    }
}
