import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Protege a curadoria humana contra perdas silenciosas durante leitura e gravação.
 */
public final class TesteRoundTripCuradoriaCanonica {

    private static final int TOTAL_COLUNAS = 37;
    private static final int INDICE_TIPO = 6;
    private static final int INDICE_ESTADO_FINAL = 14;
    private static final int INDICE_OPERACAO_RELACAO = 34;
    private static final int INDICE_ESTADO_INTERMEDIARIO = 35;
    private static final int INDICE_OPERACAO_ESTADO_TRANSFORMACAO = 36;

    private TesteRoundTripCuradoriaCanonica() {
    }

    public static void main(String[] args) throws Exception {
        String homeOriginal = System.getProperty("user.home");
        Path raizTemporaria = Files.createTempDirectory("gerard-roundtrip-curadoria-");
        Path canonico = Paths.get("src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv");
        Path copiaAtiva = raizTemporaria.resolve("Gerard/curadoria/situacoes_vergnaud_curadas.tsv");
        Files.createDirectories(copiaAtiva.getParent());
        Files.copy(canonico, copiaAtiva, StandardCopyOption.REPLACE_EXISTING);

        try {
            System.setProperty("user.home", raizTemporaria.toString());
            List<String> antes = Files.readAllLines(copiaAtiva, StandardCharsets.UTF_8);
            RepositorioSituacoesAditivas repositorio = new RepositorioSituacoesAditivas();
            List<SituacaoProblemaAditiva> situacoes = repositorio.listarTodas();
            exigir(situacoes.size() == 210, "o repositório deve carregar 210 situações");

            RepositorioSituacoesAditivas.salvarCuradoria(situacoes);
            List<String> depois = Files.readAllLines(copiaAtiva, StandardCharsets.UTF_8);
            exigir(antes.size() == depois.size(), "o round-trip não pode criar nem remover linhas");
            exigir(RepositorioSituacoesAditivas.CABECALHO_CURADORIA.equals(depois.get(0)),
                    "o cabeçalho canônico deve ser preservado");

            int transformacoesRelacao = 0;
            for (int indice = 1; indice < antes.size(); indice++) {
                String[] camposAntes = antes.get(indice).split("\\t", -1);
                String[] camposDepois = depois.get(indice).split("\\t", -1);
                exigir(camposAntes.length == TOTAL_COLUNAS,
                        "linha original " + (indice + 1) + " deve ter 37 colunas");
                exigir(camposDepois.length == TOTAL_COLUNAS,
                        "linha regravada " + (indice + 1) + " deve ter 37 colunas");

                for (int coluna = 0; coluna < TOTAL_COLUNAS; coluna++) {
                    exigir(camposAntes[coluna].equals(camposDepois[coluna]),
                            "campo alterado no round-trip: linha " + (indice + 1)
                            + ", coluna " + coluna);
                }

                if ("TRANSFORMACAO_RELACAO".equals(camposAntes[INDICE_TIPO])) {
                    transformacoesRelacao++;
                    exigir(camposAntes[INDICE_ESTADO_FINAL].equals(camposDepois[INDICE_ESTADO_FINAL]),
                            "relacao_final/estado_final deve permanecer exatamente como curada");
                    exigir(camposAntes[INDICE_OPERACAO_RELACAO].equals(camposDepois[INDICE_OPERACAO_RELACAO]),
                            "operacao_relacao deve permanecer exatamente como curada");
                }
                if ("COMPOSICAO_TRANSFORMACOES".equals(camposAntes[INDICE_TIPO])) {
                    exigir(camposAntes[INDICE_ESTADO_INTERMEDIARIO]
                                    .equals(camposDepois[INDICE_ESTADO_INTERMEDIARIO]),
                            "estado_intermediario deve permanecer exatamente como curado");
                    exigir(camposAntes[INDICE_OPERACAO_ESTADO_TRANSFORMACAO]
                                    .equals(camposDepois[INDICE_OPERACAO_ESTADO_TRANSFORMACAO]),
                            "operacao_estado_transformacao deve permanecer exatamente como curada");
                }
            }
            exigir(transformacoesRelacao == 8,
                    "as oito situações de TRANSFORMACAO_RELACAO devem participar do teste");
            exigir(contarCategorias(situacoes).size() == 6,
                    "o repositório deve expor exatamente as seis categorias canônicas");
            System.out.println("APROVADO: round-trip preservou os 37 campos das 210 situações curadas.");
        } finally {
            if (homeOriginal == null) {
                System.clearProperty("user.home");
            } else {
                System.setProperty("user.home", homeOriginal);
            }
        }
    }

    private static Map<String, Integer> contarCategorias(List<SituacaoProblemaAditiva> situacoes) {
        Map<String, Integer> totais = new HashMap<String, Integer>();
        for (SituacaoProblemaAditiva situacao : situacoes) {
            String tipo = situacao.getTipo().name();
            Integer total = totais.get(tipo);
            totais.put(tipo, total == null ? 1 : total + 1);
        }
        return totais;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
