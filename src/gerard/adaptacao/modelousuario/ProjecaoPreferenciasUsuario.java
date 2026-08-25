package gerard.adaptacao.modelousuario;

/**
 * Recorte separado para ensaios de personalização/materialização.
 * Sua existência não o transforma em contexto de decisão adaptativa.
 */
public final class ProjecaoPreferenciasUsuario {

    private final String versaoModelo;
    private final String usuarioId;
    private final ValorProjetado<PerfilAlunoProjetado> perfilAluno;
    private final ValorProjetado<PerfilAprendizagemProjetado> perfilAprendizagem;

    ProjecaoPreferenciasUsuario(
            String versaoModelo,
            String usuarioId,
            ValorProjetado<PerfilAlunoProjetado> perfilAluno,
            ValorProjetado<PerfilAprendizagemProjetado> perfilAprendizagem) {
        this.versaoModelo = versaoModelo;
        this.usuarioId = usuarioId;
        this.perfilAluno = perfilAluno;
        this.perfilAprendizagem = perfilAprendizagem;
    }

    public String getVersaoModelo() { return versaoModelo; }
    public String getUsuarioId() { return usuarioId; }
    public ValorProjetado<PerfilAlunoProjetado> getPerfilAluno() { return perfilAluno; }
    public ValorProjetado<PerfilAprendizagemProjetado> getPerfilAprendizagem() {
        return perfilAprendizagem;
    }
}
