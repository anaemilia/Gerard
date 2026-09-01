import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/** Protege a direção de dependência: aplicação web não conhece Swing/AWT/Main. */
public class TestePortabilidadeAplicacaoWeb {
    public static void main(String[] args) throws Exception {
        List<File> fontes = new ArrayList<File>();
        coletar(new File("src/gerard/aplicacao/portabilidade"), fontes);
        for (File fonte : fontes) {
            String codigo = new String(Files.readAllBytes(fonte.toPath()), StandardCharsets.UTF_8);
            exigir(!codigo.contains("javax.swing"), fonte + " importa Swing");
            exigir(!codigo.contains("java.awt"), fonte + " importa AWT");
            exigir(!codigo.matches("(?s).*\\bimport\\s+Main\\s*;.*"), fonte + " importa Main");
        }
        System.out.println("APROVADO: aplicação web portátil não depende de Swing/AWT/Main.");
    }

    private static void coletar(File diretorio, List<File> resultado) {
        File[] itens = diretorio.listFiles();
        if (itens == null) return;
        for (File item : itens) {
            if (item.isDirectory()) coletar(item, resultado);
            else if (item.getName().endsWith(".java")) resultado.add(item);
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
