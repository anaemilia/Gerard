package gerard.agente.modelousuario;

/**
 * Dimensão 3 do Modelo do Usuário (Quadro 5.60): identificação única do
 * aluno. Ver gerard-modelo-usuario/SKILL.md.
 */
public class PerfilAluno {
    private String id;
    private String nome;
    private Integer idade;
    private String sexo;

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

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
}
