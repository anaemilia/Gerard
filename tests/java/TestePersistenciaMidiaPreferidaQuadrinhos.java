import gerard.agente.modelousuario.Genero;
import gerard.agente.modelousuario.MidiaPreferida;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelEscolaridade;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/** Confirma a persistência da preferência exclusiva por quadrinhos. */
public final class TestePersistenciaMidiaPreferidaQuadrinhos {

    public static void main(String[] args) throws Exception {
        Path pasta = Files.createTempDirectory("gerard-midia-quadrinhos-");
        File perfis = pasta.resolve("perfis.tsv").toFile();
        File diagnosticos = pasta.resolve("diagnosticos.tsv").toFile();

        RepositorioModeloUsuario repositorio =
                new RepositorioModeloUsuario(perfis, diagnosticos);
        String id = repositorio.cadastrarPerfil(
                "Teste Quadrinhos",
                Integer.valueOf(10),
                Genero.OUTRO,
                MidiaPreferida.HISTORIA_EM_QUADRINHOS,
                NivelEscolaridade.PRIMEIRO_GRAU,
                null);

        ModeloUsuario recarregado =
                new RepositorioModeloUsuario(perfis, diagnosticos).obter(id);
        exigir(recarregado != null, "Perfil não foi recarregado");
        exigir(recarregado.getPerfilAprendizagem().getMidiaPreferida()
                        == MidiaPreferida.HISTORIA_EM_QUADRINHOS,
                "Preferência por quadrinhos não persistiu");

        excluir(perfis);
        excluir(diagnosticos);
        excluir(pasta.toFile());
        System.out.println("OK TestePersistenciaMidiaPreferidaQuadrinhos");
    }

    private static void excluir(File arquivo) {
        if (arquivo.exists() && !arquivo.delete()) {
            arquivo.deleteOnExit();
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
