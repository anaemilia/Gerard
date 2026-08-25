package gerard.adaptacao.modelousuario;

import gerard.agente.modelousuario.Genero;
import gerard.agente.modelousuario.PerfilAluno;
import java.util.Optional;

/** Cópia imutável dos dados de identificação que integram o modelo. */
public final class PerfilAlunoProjetado {

    private final String id;
    private final String nome;
    private final Integer idade;
    private final Genero sexo;
    private final String fotoCaminho;

    public PerfilAlunoProjetado(PerfilAluno origem) {
        if (origem == null) {
            throw new IllegalArgumentException("perfil do aluno não pode ser nulo");
        }
        this.id = textoObrigatorio(origem.getId(), "id do usuário");
        this.nome = textoOpcional(origem.getNome());
        this.idade = origem.getIdade();
        this.sexo = origem.getSexo();
        this.fotoCaminho = textoOpcional(origem.getFotoCaminho());
    }

    public String getId() { return id; }
    public Optional<String> getNome() { return Optional.ofNullable(nome); }
    public Optional<Integer> getIdade() { return Optional.ofNullable(idade); }
    public Optional<Genero> getSexo() { return Optional.ofNullable(sexo); }
    public Optional<String> getFotoCaminho() { return Optional.ofNullable(fotoCaminho); }

    private static String textoOpcional(String valor) {
        if (valor == null) return null;
        String normalizado = valor.trim();
        return normalizado.isEmpty() ? null : normalizado;
    }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = textoOpcional(valor);
        if (normalizado == null) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }
}
