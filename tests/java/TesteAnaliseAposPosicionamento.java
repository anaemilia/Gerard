import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.SwingUtilities;

public class TesteAnaliseAposPosicionamento {
    public static void main(String[] args) throws Exception {
        prepararCuradoriaTemporaria();
        final Throwable[] erro = new Throwable[1];

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    Main.TelaGerard tela = new Main.TelaGerard();
                    tela.setSize(1240, 760);
                    tela.idiomaSelecionado = IdiomaInterface.PORTUGUES;
                    invocar(tela, "aplicarIdiomaSelecionadoMantendoEstadoTela");
                    tela.tipoSituacaoSelecionada = TipoSituacaoAditiva.COMPOSICAO_MEDIDAS;
                    tela.categoriaSelecionadaParaAtividade = true;
                    invocar(tela, "aplicarIdiomaSelecionado");
                    renderizar(tela);

                    exigir(tela.situacaoProblemaAtual != null, "A situação não foi carregada.");
                    exigir(!tela.elementosVergnaud.isEmpty(), "O diagrama de Vergnaud não foi inicializado.");
                    exigir(!tela.botaoArtefatoExplicativo.isVisible(),
                            "A análise apareceu antes de qualquer posicionamento.");
                    exigir(!tela.botaoArtefatoExplicativo.isEnabled(),
                            "A análise ficou habilitada antes de qualquer posicionamento.");

                    ElementoVergnaud alvo = tela.elementosVergnaud.get(0);
                    ItemTextoArrastavel item = new ItemTextoArrastavel(
                            alvo.x + alvo.largura / 2 - 10,
                            alvo.y + alvo.altura / 2 - 10,
                            20,
                            20,
                            "6",
                            false,
                            "6",
                            "papel.parte1");
                    tela.itensArrastaveis.add(item);
                    renderizar(tela);

                    exigir(tela.botaoArtefatoExplicativo.isVisible(),
                            "A análise não apareceu após um posicionamento no diagrama de Vergnaud.");
                    exigir(tela.botaoArtefatoExplicativo.isEnabled(),
                            "A análise não foi habilitada após um posicionamento no diagrama de Vergnaud.");

                    tela.itensArrastaveis.clear();
                    renderizar(tela);
                    exigir(!tela.botaoArtefatoExplicativo.isVisible(),
                            "A análise permaneceu visível sem posicionamentos no estado atual.");

                    System.out.println("OK: a análise só aparece após ao menos um posicionamento no diagrama de Vergnaud.");
                } catch (Throwable t) {
                    erro[0] = t;
                }
            }
        });

        if (erro[0] != null) {
            erro[0].printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    private static void prepararCuradoriaTemporaria() throws Exception {
        Path raiz = Files.createTempDirectory("gerard-c96-home-");
        System.setProperty("user.home", raiz.toAbsolutePath().toString());
        Path destino = raiz.resolve("Gerard/curadoria/situacoes_vergnaud_curadas.tsv");
        Files.createDirectories(destino.getParent());
        List<String> linhas = Files.readAllLines(new File("dados/situacoes_vergnaud.tsv").toPath(), StandardCharsets.UTF_8);
        String cabecalho = linhas.get(0);
        String situacao = null;
        for (String linha : linhas) {
            if (linha.contains("\tPORTUGUES\tCOMPOSICAO_MEDIDAS\t")) {
                situacao = linha.replace("\toriginal\t\tfalse\t", "\toriginal\t\ttrue\t");
                break;
            }
        }
        if (situacao == null) throw new AssertionError("Situação de composição não localizada.");
        Files.write(destino, java.util.Arrays.asList(cabecalho, situacao), StandardCharsets.UTF_8);
    }

    private static void invocar(Main.TelaGerard tela, String nome) throws Exception {
        Method metodo = Main.TelaGerard.class.getDeclaredMethod(nome);
        metodo.setAccessible(true);
        metodo.invoke(tela);
    }

    private static void renderizar(Main.TelaGerard tela) {
        BufferedImage imagem = new BufferedImage(1240, 760, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        tela.paint(g2);
        g2.dispose();
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
