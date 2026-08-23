import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestePersistenciaSeguraCuradoria {
    public static void main(String[] args) throws Exception {
        String homeOriginal = System.getProperty("user.home");
        Path homeIsolado = Files.createTempDirectory("gerard-curadoria-segura-");
        try {
            System.setProperty("user.home", homeIsolado.toString());
            RepositorioSituacoesAditivas repositorio = new RepositorioSituacoesAditivas();
            List<SituacaoProblemaAditiva> situacoes = repositorio.listarTodas();
            exigir(situacoes.size() == 210,
                    "Os nomes historicos devem preservar as 210 situacoes empacotadas");
            exigir(TipoSituacaoAditiva.deCodigoPersistido("COMPOSICAO_TRANSFORMACAO_MEDIDAS")
                            == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                    "O primeiro nome historico deve convergir para composicao de transformacoes");
            exigir(TipoSituacaoAditiva.deCodigoPersistido("TRANSFORMACAO_COMPOSTA_DOIS_PASSOS")
                            == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                    "O segundo nome historico deve convergir para composicao de transformacoes");

            RepositorioSituacoesAditivas.salvarCuradoria(situacoes);
            Path destino = homeIsolado.resolve("Gerard/curadoria/situacoes_vergnaud_curadas.tsv");
            byte[] primeiraVersao = Files.readAllBytes(destino);

            RepositorioSituacoesAditivas.salvarCuradoria(situacoes);

            File[] backups = destino.getParent().toFile().listFiles((diretorio, nome) ->
                    nome.startsWith("situacoes_vergnaud_curadas.tsv.backup-"));
            exigir(backups != null && backups.length == 1,
                    "A segunda gravacao deve preservar exatamente um backup da primeira versao");
            exigir(java.util.Arrays.equals(primeiraVersao, Files.readAllBytes(backups[0].toPath())),
                    "O backup deve preservar integralmente a versao anterior");
            exigir(Files.readAllLines(destino, StandardCharsets.UTF_8).size() == situacoes.size() + 1,
                    "O arquivo ativo deve permanecer completo depois da substituicao");

            System.out.println("OK: curadoria gravada por substituicao e versao anterior preservada");
        } finally {
            if (homeOriginal == null) {
                System.clearProperty("user.home");
            } else {
                System.setProperty("user.home", homeOriginal);
            }
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
