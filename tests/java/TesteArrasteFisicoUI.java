import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.SwingUtilities;

public class TesteArrasteFisicoUI {
    public static void main(String[] args) throws Exception {
        prepararCuradoriaTemporaria();
        final Throwable[] erro = new Throwable[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    Main.TelaGerard tela = new Main.TelaGerard();
                    tela.setSize(1240, 760);
                    tela.idiomaSelecionado = IdiomaInterface.PORTUGUES;
                    tela.tipoSituacaoSelecionada = TipoSituacaoAditiva.COMPOSICAO_MEDIDAS;
                    tela.categoriaSelecionadaParaAtividade = true;
                    invocar(tela, "aplicarIdiomaSelecionado");
                    renderizar(tela);

                    exigir(!tela.marcadoresFixosTexto.isEmpty(),
                            "Marcador textual não disponível para o teste.");
                    MarcadorTexto marcador = tela.marcadoresFixosTexto.get(0);
                    int mx = marcador.x + marcador.largura / 2;
                    int my = marcador.y + marcador.altura / 2;

                    tela.mousePressed(evento(tela, MouseEvent.MOUSE_PRESSED, mx, my));
                    exigir(tela.marcadorOrigemArraste.estaAtivo(),
                            "O buraco de origem deve aparecer no pickup.");
                    exigir(tela.controladorArrasteElastico.estaAtivo(),
                            "A mola deve iniciar junto com o pickup.");
                    exigir(tela.controladorArrasteElastico.getClass().getSimpleName()
                                    .equals("ControladorArrasteMolaMomento"),
                            "A tela deve usar o controlador explícito de mola e momento.");

                    int alvoX = mx + 80;
                    int alvoY = my + 250;
                    tela.mouseDragged(evento(tela, MouseEvent.MOUSE_DRAGGED, alvoX, alvoY));
                    Point visual = tela.controladorArrasteElastico.obterPosicaoVisual();
                    exigir(visual.x != alvoX || visual.y != alvoY,
                            "O elemento não deve teleportar exatamente ao cursor.");
                    exigir(Point.distance(visual.x, visual.y, alvoX, alvoY) <= 26.5,
                            "O atraso visual ultrapassou o limite discreto.");
                    exigir(tela.itemSelecionado != null,
                            "O numeral não foi convertido em item durante o arraste.");
                    renderizar(tela);

                    tela.mouseReleased(evento(tela, MouseEvent.MOUSE_RELEASED,
                            alvoX, alvoY));
                    exigir(!tela.controladorArrasteElastico.estaAtivo(),
                            "A mola não terminou na soltura.");
                    exigir(!tela.marcadorOrigemArraste.estaAtivo(),
                            "O buraco de origem não foi removido na soltura.");
                    exigir(tela.itemSelecionado == null,
                            "O item permaneceu selecionado após a soltura.");

                    System.out.println("TesteArrasteFisicoUI: OK");
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

    private static MouseEvent evento(Main.TelaGerard tela, int id, int x, int y) {
        return new MouseEvent(tela, id, System.currentTimeMillis(), 0,
                x, y, 1, false);
    }

    private static void renderizar(Main.TelaGerard tela) {
        BufferedImage imagem = new BufferedImage(1240, 760,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        tela.paint(g2);
        g2.dispose();
    }

    private static void invocar(Main.TelaGerard tela, String nome) throws Exception {
        Method metodo = Main.TelaGerard.class.getDeclaredMethod(nome);
        metodo.setAccessible(true);
        metodo.invoke(tela);
    }

    private static void prepararCuradoriaTemporaria() throws Exception {
        Path raiz = Files.createTempDirectory("gerard-arraste-fisico-home-");
        System.setProperty("user.home", raiz.toAbsolutePath().toString());
        Path destino = raiz.resolve("Gerard/curadoria/situacoes_vergnaud_curadas.tsv");
        Files.createDirectories(destino.getParent());
        List<String> linhas = Files.readAllLines(
                new File("dados/situacoes_vergnaud.tsv").toPath(),
                StandardCharsets.UTF_8);
        String cabecalho = linhas.get(0);
        String situacao = null;
        for (String linha : linhas) {
            if (linha.contains("\tPORTUGUES\tCOMPOSICAO_MEDIDAS\t")) {
                situacao = linha.replace("\toriginal\t\tfalse\t",
                        "\toriginal\t\ttrue\t");
                break;
            }
        }
        if (situacao == null) {
            throw new AssertionError("Situação de composição não localizada.");
        }
        Files.write(destino, java.util.Arrays.asList(cabecalho, situacao),
                StandardCharsets.UTF_8);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
