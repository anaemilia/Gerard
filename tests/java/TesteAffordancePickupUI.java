import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.imageio.ImageIO;

public class TesteAffordancePickupUI {
    public static void main(String[] args) throws Exception {
        prepararCuradoriaTemporaria();
        final Throwable[] erro = new Throwable[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    Main.TelaGerard tela = new Main.TelaGerard();
                    tela.setSize(1240, 760);
                    tela.idiomaSelecionado = IdiomaInterface.PORTUGUES;
                    tela.tipoSituacaoSelecionada = TipoSituacaoAditiva.COMPOSICAO_MEDIDAS;
                    tela.categoriaSelecionadaParaAtividade = true;
                    invocar(tela, "aplicarIdiomaSelecionado");
                    renderizar(tela);

                    exigir(!tela.marcadoresFixosTexto.isEmpty(), "Marcadores do texto não foram criados.");
                    MarcadorTexto marcador = tela.marcadoresFixosTexto.get(0);
                    int mx = marcador.x + marcador.largura / 2;
                    int my = marcador.y + marcador.altura / 2;

                    tela.mouseMoved(evento(tela, MouseEvent.MOUSE_MOVED, mx, my));
                    exigir(tela.getCursor() != null, "Cursor de disponibilidade não foi definido.");
                    exigir(tela.getCursor().getType() == Cursor.HAND_CURSOR,
                            "O mouseover deve usar a mão nativa do gráfico de barras.");

                    tela.mousePressed(evento(tela, MouseEvent.MOUSE_PRESSED, mx, my));
                    exigir(tela.getCursor().getType() == Cursor.MOVE_CURSOR,
                            "O pickup ativo deve mudar para o cursor de movimentação.");
                    exigir(tela.elementoTextoSelecionado != null || tela.itemSelecionado != null,
                            "Pickup do marcador não iniciou a seleção textual.");
                    tela.mouseDragged(evento(tela, MouseEvent.MOUSE_DRAGGED, mx + 30, my + 230));
                    exigir(tela.getCursor().getType() == Cursor.MOVE_CURSOR,
                            "Durante o arraste, o cursor deve permanecer em movimentação.");
                    exigir(tela.itemSelecionado != null,
                            "O elemento numérico não foi convertido em item ao sair do texto.");
                    int xInicial = tela.itemSelecionado.x;
                    int yInicial = tela.itemSelecionado.y;
                    tela.mouseDragged(evento(tela, MouseEvent.MOUSE_DRAGGED, mx + 50, my + 250));
                    exigir(tela.itemSelecionado.x != xInicial || tela.itemSelecionado.y != yInicial,
                            "O item não se deslocou durante o pickup.");
                    renderizar(tela, new File(System.getProperty("java.io.tmpdir"), "gerard_c113_pickup_item.png"));
                    tela.mouseReleased(evento(tela, MouseEvent.MOUSE_RELEASED, mx + 50, my + 250));
                    exigir(tela.itemSelecionado == null, "O pickup do item não terminou na soltura.");

                    // As representações complementares (inclusive o mouseover dos
                    // quadradinhos do Venn) ficam bloqueadas até existir o primeiro
                    // posicionamento válido no diagrama de Vergnaud
                    // (CondicaoDiagramaVergnaudNaoVazio). O arraste acima solta o
                    // item em coordenadas arbitrárias, que podem não cair sobre um
                    // elemento do Vergnaud; garantimos aqui a mesma pré-condição já
                    // usada em TesteBloqueioAdicaoAntesVergnaud.java.
                    if (!((Boolean) invocar(tela, "diagramaVergnaudPossuiConteudoSemantico"))) {
                        exigir(!tela.elementosVergnaud.isEmpty(),
                                "Nenhum elemento do Vergnaud disponível para simular o posicionamento.");
                        tela.elementosVergnaud.get(0).textoEditavel = "1";
                    }

                    renderizar(tela);
                    exigir(!tela.circulosVenn.isEmpty(), "Diagrama complementar sem agrupamentos para teste.");
                    if (tela.quadradinhosVenn.isEmpty()) {
                        CirculoVenn agrupamento = tela.circulosVenn.get(0);
                        tela.quadradinhosVenn.add(new QuadradinhoVenn(
                                agrupamento.x + 12, agrupamento.y + 12, 12, "teste_pickup"));
                    }
                    QuadradinhoVenn q = tela.quadradinhosVenn.get(0);
                    int qx = q.centroX();
                    int qy = q.centroY();
                    int qxInicial = q.x;
                    int qyInicial = q.y;
                    tela.mouseMoved(evento(tela, MouseEvent.MOUSE_MOVED, qx, qy));
                    exigir(tela.getCursor().getType() == Cursor.HAND_CURSOR,
                            "Quadradinho disponível deve usar a mão nativa.");
                    tela.mousePressed(evento(tela, MouseEvent.MOUSE_PRESSED, qx, qy));
                    exigir(tela.getCursor().getType() == Cursor.MOVE_CURSOR,
                            "Quadradinho em pickup deve usar cursor de movimentação.");
                    exigir(tela.quadradinhoVennSelecionado == q,
                            "Quadradinho não entrou no estado de pickup.");
                    tela.mouseDragged(evento(tela, MouseEvent.MOUSE_DRAGGED, qx + 6, qy + 6));
                    exigir(q.x != qxInicial || q.y != qyInicial,
                            "Quadradinho não se deslocou durante o pickup.");
                    renderizar(tela, new File(System.getProperty("java.io.tmpdir"), "gerard_c113_pickup_square.png"));
                    tela.mouseReleased(evento(tela, MouseEvent.MOUSE_RELEASED, qx + 6, qy + 6));
                    exigir(tela.quadradinhoVennSelecionado == null,
                            "Pickup do quadradinho não terminou na soltura.");

                    System.out.println("TesteAffordancePickupUI: OK");
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
        return new MouseEvent(tela, id, System.currentTimeMillis(), 0, x, y, 1, false);
    }

    private static void renderizar(Main.TelaGerard tela) {
        renderizar(tela, null);
    }

    private static void renderizar(Main.TelaGerard tela, File destino) {
        try {
            BufferedImage imagem = new BufferedImage(1240, 760, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = imagem.createGraphics();
            tela.paint(g2);
            g2.dispose();
            if (destino != null) {
                ImageIO.write(imagem, "png", destino);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Object invocar(Main.TelaGerard tela, String nome) throws Exception {
        Method metodo = Main.TelaGerard.class.getDeclaredMethod(nome);
        metodo.setAccessible(true);
        return metodo.invoke(tela);
    }

    private static void prepararCuradoriaTemporaria() throws Exception {
        Path raiz = Files.createTempDirectory("gerard-pickup-home-");
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
        if (situacao == null) {
            throw new AssertionError("Situação de composição não localizada.");
        }
        Files.write(destino, java.util.Arrays.asList(cabecalho, situacao), StandardCharsets.UTF_8);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
