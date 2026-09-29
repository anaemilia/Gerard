import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Prova Robot dirigida da Fase 7.11. */
public final class TesteRobotSelecaoMarcadorTexto {

    public static void main(String[] args) throws Exception {
        prepararCuradoriaTemporaria();
        final JFrame[] janela = new JFrame[1];
        final Main.TelaGerard[] tela = new Main.TelaGerard[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                tela[0] = new Main.TelaGerard();
                tela[0].setSize(1240, 760);
                tela[0].idiomaSelecionado = IdiomaInterface.PORTUGUES;
                tela[0].tipoSituacaoSelecionada = TipoSituacaoAditiva.COMPOSICAO_MEDIDAS;
                tela[0].categoriaSelecionadaParaAtividade = true;
                try {
                    invocar(tela[0], "aplicarIdiomaSelecionado");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                tela[0].paint(new BufferedImage(
                        1240, 760, BufferedImage.TYPE_INT_ARGB).getGraphics());
                janela[0] = new JFrame("Robot Fase 7.11");
                janela[0].setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                janela[0].setContentPane(tela[0]);
                janela[0].setSize(1240, 760);
                janela[0].setLocationRelativeTo(null);
                janela[0].setVisible(true);
                janela[0].toFront();
            }
        });

        try {
            Thread.sleep(800);
            Robot robot = new Robot();
            robot.setAutoDelay(40);
            for (int tentativa = 0;
                    tentativa < 20 && tela[0].marcadoresFixosTexto.isEmpty();
                    tentativa++) {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        BufferedImage imagem = new BufferedImage(
                                Math.max(1, tela[0].getWidth()),
                                Math.max(1, tela[0].getHeight()),
                                BufferedImage.TYPE_INT_ARGB);
                        java.awt.Graphics2D g2 = imagem.createGraphics();
                        tela[0].paint(g2);
                        g2.dispose();
                    }
                });
                Thread.sleep(100);
            }
            exigir(!tela[0].marcadoresFixosTexto.isEmpty(),
                    "A tela visível não materializou marcadores do enunciado.");
            final MarcadorTexto marcador = tela[0].marcadoresFixosTexto.get(0);
            final int quantidadeMarcadores = tela[0].marcadoresFixosTexto.size();
            Point origem = tela[0].getLocationOnScreen();
            int x = origem.x + marcador.x + marcador.largura / 2;
            int y = origem.y + marcador.y + marcador.altura / 2;

            robot.mouseMove(x, y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle();
            exigir(tela[0].handlerItemTextoArrastavel.estaAtivo(),
                    "Pressionamento Robot não iniciou o item textual.");
            exigir(tela[0].marcadoresFixosTexto.size() == quantidadeMarcadores
                            && possuiMarcadorEquivalente(tela[0], marcador),
                    "A origem do enunciado foi removida durante a seleção.");
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle();

            final SessaoArrasteTextoParaDiagrama sessaoPreparacao =
                    new SessaoArrasteTextoParaDiagrama();
            final ItemTextoArrastavel existente =
                    sessaoPreparacao.iniciarPorMarcador(marcador);
            existente.y = 300;
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    tela[0].itensArrastaveis.add(existente);
                    tela[0].mostrarAnotacaoMouseOver = false;
                }
            });

            robot.mouseMove(x, y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle();
            exigir(!tela[0].handlerItemTextoArrastavel.estaAtivo(),
                    "Ocorrência já posicionada abriu outro arraste.");
            exigir(tela[0].mostrarAnotacaoMouseOver,
                    "Ocorrência repetida não apresentou o aviso existente.");
            exigir(tela[0].itensArrastaveis.size() == 1,
                    "Ocorrência repetida criou outro item no diagrama.");
            System.out.println(
                    "TesteRobotSelecaoMarcadorTexto: OK — seleção real, origem preservada e duplicação bloqueada.");
        } finally {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    if (janela[0] != null) {
                        janela[0].dispose();
                    }
                }
            });
        }
        System.exit(0);
    }

    private static Object invocar(Main.TelaGerard tela, String nome)
            throws Exception {
        Method metodo = Main.TelaGerard.class.getDeclaredMethod(nome);
        metodo.setAccessible(true);
        return metodo.invoke(tela);
    }

    private static void prepararCuradoriaTemporaria() throws Exception {
        Path raiz = Files.createTempDirectory("gerard-robot-marcador-home-");
        System.setProperty("user.home", raiz.toAbsolutePath().toString());
        Path destino = raiz.resolve(
                "Gerard/curadoria/situacoes_vergnaud_curadas.tsv");
        Files.createDirectories(destino.getParent());
        List<String> linhas = Files.readAllLines(new File(
                "src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv").toPath(),
                StandardCharsets.UTF_8);
        String situacao = null;
        for (String linha : linhas) {
            if (linha.contains("\tpt-BR\tCOMPOSICAO_MEDIDAS\t")) {
                situacao = linha.replace(
                        "\toriginal\t\tfalse\t", "\toriginal\t\ttrue\t");
                break;
            }
        }
        if (situacao == null) {
            throw new AssertionError("Situação de composição não localizada.");
        }
        Files.write(destino, java.util.Arrays.asList(linhas.get(0), situacao),
                StandardCharsets.UTF_8);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static boolean possuiMarcadorEquivalente(
            Main.TelaGerard tela, MarcadorTexto origem) {
        for (MarcadorTexto candidato : tela.marcadoresFixosTexto) {
            if (origem.valor.equals(candidato.valor)
                    && iguais(origem.chavePapel, candidato.chavePapel)) {
                return true;
            }
        }
        return false;
    }

    private static boolean iguais(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
}
