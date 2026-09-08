import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TesteFronteiraEditorNarrativaWeb {
    public static void main(String[] args) throws Exception {
        Path raiz = localizarRaiz();
        String editor = ler(raiz.resolve("web-poc/src/EditorNarrativa.tsx"));
        String servico = ler(raiz.resolve(
                "src/gerard/aplicacao/portabilidade/ServicoSorteioAtividadeWeb.java"));

        exigir(servico.contains("modelo_palavra_comum"),
                "A API deve publicar o descritor para palavras comuns novas.");
        exigir(servico.contains("saco_destino"),
                "A API deve publicar o destino de retorno de cada palavra.");
        exigir(editor.contains("peca.saco_destino"),
                "O editor deve materializar o destino recebido da API.");
        exigir(!editor.contains("c.origem === \"organizador\""),
                "O React não pode classificar palavra pela origem do arraste.");
        exigir(!editor.contains("peca.tipo === \"COMUM\""),
                "O React não pode deduzir o saco a partir do tipo semântico.");
        exigir(!editor.contains("const tipo ="),
                "O editor não pode produzir uma classificação semântica local.");

        System.out.println("TesteFronteiraEditorNarrativaWeb: OK");
    }

    private static Path localizarRaiz() {
        Path atual = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 6 && atual != null; i++, atual = atual.getParent()) {
            if (Files.exists(atual.resolve("web-poc/src/EditorNarrativa.tsx"))) {
                return atual;
            }
        }
        throw new AssertionError("Raiz do projeto Gérard não localizada.");
    }

    private static String ler(Path caminho) throws Exception {
        return new String(Files.readAllBytes(caminho), StandardCharsets.UTF_8);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
