package gerard.adaptacao.modelousuario;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Recorte tipado, imutável e explicitamente limitado da fotografia de sessão.
 * Objetos semânticos recebem este recorte, nunca o {@code ModeloUsuario}
 * mutável completo.
 */
public final class ProjecaoModeloUsuario {

    private final String versaoModelo;
    private final String usuarioId;
    private final Set<DimensaoModeloUsuario> dimensoesSolicitadas;
    private final ValorProjetado<NiveisTarefasProjetados> niveisTarefas;
    private final ValorProjetado<DominioCategoriasProjetado> dominioCategorias;
    private final ValorProjetado<PerfilAlunoProjetado> perfilAluno;
    private final ValorProjetado<PerfilAprendizagemProjetado> perfilAprendizagem;
    private final ValorProjetado<HistoricoDiagnosticosProjetado> diagnosticosTarefa;

    ProjecaoModeloUsuario(
            String versaoModelo,
            String usuarioId,
            Set<DimensaoModeloUsuario> dimensoesSolicitadas,
            ValorProjetado<NiveisTarefasProjetados> niveisTarefas,
            ValorProjetado<DominioCategoriasProjetado> dominioCategorias,
            ValorProjetado<PerfilAlunoProjetado> perfilAluno,
            ValorProjetado<PerfilAprendizagemProjetado> perfilAprendizagem,
            ValorProjetado<HistoricoDiagnosticosProjetado> diagnosticosTarefa) {
        this.versaoModelo = versaoModelo;
        this.usuarioId = usuarioId;
        EnumSet<DimensaoModeloUsuario> copia = EnumSet.noneOf(DimensaoModeloUsuario.class);
        copia.addAll(dimensoesSolicitadas);
        this.dimensoesSolicitadas = Collections.unmodifiableSet(copia);
        this.niveisTarefas = niveisTarefas;
        this.dominioCategorias = dominioCategorias;
        this.perfilAluno = perfilAluno;
        this.perfilAprendizagem = perfilAprendizagem;
        this.diagnosticosTarefa = diagnosticosTarefa;
    }

    public String getVersaoModelo() { return versaoModelo; }
    public String getUsuarioId() { return usuarioId; }
    public Set<DimensaoModeloUsuario> getDimensoesSolicitadas() {
        return dimensoesSolicitadas;
    }
    public ValorProjetado<NiveisTarefasProjetados> getNiveisTarefas() {
        return niveisTarefas;
    }
    public ValorProjetado<DominioCategoriasProjetado> getDominioCategorias() {
        return dominioCategorias;
    }
    public ValorProjetado<PerfilAlunoProjetado> getPerfilAluno() { return perfilAluno; }
    public ValorProjetado<PerfilAprendizagemProjetado> getPerfilAprendizagem() {
        return perfilAprendizagem;
    }
    public ValorProjetado<HistoricoDiagnosticosProjetado> getDiagnosticosTarefa() {
        return diagnosticosTarefa;
    }

    public boolean solicitaConhecimentoAlemDePerfil() {
        for (DimensaoModeloUsuario dimensao : dimensoesSolicitadas) {
            if (!dimensao.ehPerfil()) return true;
        }
        return false;
    }
}
