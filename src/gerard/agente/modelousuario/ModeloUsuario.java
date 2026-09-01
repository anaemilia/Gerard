package gerard.agente.modelousuario;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Repositório de dados compartilhado descrito em gerard-modelo-usuario/
 * SKILL.md (Quadro 5.60): as cinco dimensões armazenadas sobre um usuário.
 *
 * Esta classe armazena a fotografia publicada pelo Agente Modelador. Ela não
 * infere regras nem escolhe uma intervenção: cada proprietário semântico lê
 * somente a projeção pertinente e decide dentro de seu repertório local.
 */
public class ModeloUsuario {
    private final PerfilAluno perfilAluno;
    private final PerfilAprendizagem perfilAprendizagem = new PerfilAprendizagem();

    // Dimensão 1: nível de complexidade de tarefa em que o usuário está, por categoria.
    private final Map<TipoSituacaoAditiva, NivelComplexidadeTarefa> nivelTarefaPorCategoria =
            new EnumMap<TipoSituacaoAditiva, NivelComplexidadeTarefa>(TipoSituacaoAditiva.class);

    // Dimensão 2: em qual categoria o usuário tem maior/menor domínio.
    private TipoSituacaoAditiva categoriaMaiorDominio;
    private TipoSituacaoAditiva categoriaMenorDominio;

    // Dimensão 5: histórico de diagnósticos de tarefa (entrada do Agente Modelador).
    private final List<DiagnosticoTarefa> diagnosticos = new ArrayList<DiagnosticoTarefa>();

    public ModeloUsuario(String idUsuario) {
        this.perfilAluno = new PerfilAluno(idUsuario);
    }

    public PerfilAluno getPerfilAluno() {
        return perfilAluno;
    }

    public PerfilAprendizagem getPerfilAprendizagem() {
        return perfilAprendizagem;
    }

    public NivelComplexidadeTarefa getNivelTarefa(TipoSituacaoAditiva categoria) {
        return nivelTarefaPorCategoria.get(categoria);
    }

    public void setNivelTarefa(TipoSituacaoAditiva categoria, NivelComplexidadeTarefa nivel) {
        nivelTarefaPorCategoria.put(categoria, nivel);
    }

    public TipoSituacaoAditiva getCategoriaMaiorDominio() {
        return categoriaMaiorDominio;
    }

    public void setCategoriaMaiorDominio(TipoSituacaoAditiva categoriaMaiorDominio) {
        this.categoriaMaiorDominio = categoriaMaiorDominio;
    }

    public TipoSituacaoAditiva getCategoriaMenorDominio() {
        return categoriaMenorDominio;
    }

    public void setCategoriaMenorDominio(TipoSituacaoAditiva categoriaMenorDominio) {
        this.categoriaMenorDominio = categoriaMenorDominio;
    }

    public List<DiagnosticoTarefa> getDiagnosticos() {
        return diagnosticos;
    }

    public void adicionarDiagnostico(DiagnosticoTarefa diagnostico) {
        if (diagnostico != null) {
            diagnosticos.add(diagnostico);
        }
    }
}
