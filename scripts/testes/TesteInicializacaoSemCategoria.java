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
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class TesteInicializacaoSemCategoria {
    public static void main(String[] args) throws Exception {
        prepararCuradoriaTemporaria();
        final Throwable[] erro = new Throwable[1];

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    Main.TelaGerard tela = new Main.TelaGerard();
                    tela.setSize(1240, 760);
                    BufferedImage imagemInicial = renderizar(tela, new File(System.getProperty("java.io.tmpdir"), "gerard_c94_inicial.png"));

                    exigir(!tela.categoriaSelecionadaParaAtividade, "A tela iniciou com categoria ativa.");
                    exigir(tela.situacaoProblemaAtual == null, "A tela iniciou com situação-problema.");
                    exigir(tela.textoProblema.length() == 0, "A tela iniciou com texto.");
                    exigir(tela.elementosTexto.isEmpty(), "A tela iniciou com elementos textuais.");
                    exigir(tela.elementosVergnaud.isEmpty(), "A tela iniciou com diagrama de Vergnaud.");
                    exigir(tela.circulosVenn.isEmpty(), "A tela iniciou com diagrama complementar.");
                    exigir(!tela.botaoSortear.isEnabled(), "Sortear deveria estar desabilitado antes da categoria.");
                    exigir(!tela.botaoAjudaTexto.isVisible(), "Ajuda textual deveria estar oculta.");
                    exigir(!tela.botaoAjudaVergnaud.isVisible(), "Ajuda de Vergnaud deveria estar oculta.");
                    exigir(painelVisivel(imagemInicial, 20, 60), "O painel do texto não permaneceu visível.");
                    exigir(painelVisivel(imagemInicial, 20, 220), "O painel de Vergnaud não permaneceu visível.");
                    exigir(painelVisivel(imagemInicial, 720, 220), "O painel complementar não permaneceu visível.");

                    tela.idiomaSelecionado = IdiomaInterface.INGLES;
                    invocar(tela, "aplicarIdiomaSelecionadoMantendoEstadoTela");
                    exigir(!tela.categoriaSelecionadaParaAtividade, "Trocar o idioma ativou uma categoria.");
                    exigir(tela.situacaoProblemaAtual == null, "Trocar o idioma carregou uma situação.");
                    exigir(tela.elementosVergnaud.isEmpty(), "Trocar o idioma carregou um diagrama.");
                    exigir(!tela.botaoSortear.isEnabled(), "Trocar o idioma habilitou o sorteio.");

                    tela.idiomaSelecionado = IdiomaInterface.PORTUGUES;
                    invocar(tela, "aplicarIdiomaSelecionadoMantendoEstadoTela");
                    tela.tipoSituacaoSelecionada = TipoSituacaoAditiva.COMPOSICAO_MEDIDAS;
                    tela.categoriaSelecionadaParaAtividade = true;
                    invocar(tela, "aplicarIdiomaSelecionado");
                    renderizar(tela, new File(System.getProperty("java.io.tmpdir"), "gerard_c94_com_categoria.png"));

                    exigir(tela.situacaoProblemaAtual != null, "A categoria não carregou situação-problema.");
                    exigir(tela.textoProblema.length() > 0, "A categoria não carregou o enunciado.");
                    exigir(!tela.elementosTexto.isEmpty(), "A categoria não inicializou os elementos do texto.");
                    exigir(!tela.elementosVergnaud.isEmpty(), "A categoria não inicializou o diagrama de Vergnaud.");
                    exigir(!tela.circulosVenn.isEmpty(), "Composição de medidas não inicializou o diagrama complementar.");
                    exigir(tela.botaoSortear.isEnabled(), "Sortear não foi habilitado após a categoria.");
                    exigir(tela.botaoAjudaTexto.isVisible(), "Ajuda textual não apareceu após a categoria.");
                    exigir(tela.botaoAjudaVergnaud.isVisible(), "Ajuda de Vergnaud não apareceu após a categoria.");
                    exigir(tela.botaoAjudaComplementar.isVisible(), "Ajuda complementar não apareceu após a categoria.");

                    System.out.println("OK: a tela mantém os painéis vazios e carrega os conteúdos educativos somente após a categoria.");
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
        Path raiz = Files.createTempDirectory("gerard-c93-home-");
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
        if (situacao == null) throw new AssertionError("Situação de composição não localizada para o teste.");
        Files.write(destino, java.util.Arrays.asList(cabecalho, situacao), StandardCharsets.UTF_8);
    }

    private static void invocar(Main.TelaGerard tela, String nome) throws Exception {
        Method metodo = Main.TelaGerard.class.getDeclaredMethod(nome);
        metodo.setAccessible(true);
        metodo.invoke(tela);
    }

    private static BufferedImage renderizar(Main.TelaGerard tela, File destino) throws Exception {
        BufferedImage imagem = new BufferedImage(1240, 760, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        tela.paint(g2);
        g2.dispose();
        ImageIO.write(imagem, "png", destino);
        return imagem;
    }

    private static boolean painelVisivel(BufferedImage imagem, int x, int y) {
        int rgb = imagem.getRGB(x, y);
        int alpha = (rgb >>> 24) & 0xff;
        int vermelho = (rgb >>> 16) & 0xff;
        int verde = (rgb >>> 8) & 0xff;
        int azul = rgb & 0xff;
        return alpha > 0 && vermelho > 220 && verde > 220 && azul > 220;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
