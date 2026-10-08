import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;

/**
 * Golden master VISUAL da anotação flutuante (balão de mouse-over, questionamento persistente e dica de
 * posicionamento): com o mouse REAL, passa por cada palavra do enunciado, clica em "Ver dica", arrasta uma palavra
 * para um elemento errado do diagrama (questionamento persistente) e passa o mouse pelas peças depois de modelar;
 * em cada passo captura a tela da janela e registra o hash dos pixels. Antes e depois de separar a decisão da
 * anotação do desenho, as tabelas têm de ser IGUAIS (o balão é pixel a pixel o mesmo).
 *
 * Uso: TesteMacacoAnotacaoMouseOver <arquivo.tsv> [pastaPng] [id1,id2,...]
 */
public class TesteMacacoAnotacaoMouseOver {
    static PrintWriter saida;
    static File pastaPng;
    static int linhas;

    public static void main(String[] a) throws Exception {
        File tsv = new File(a.length > 0 ? a[0] : "anotacao.tsv");
        pastaPng = a.length > 1 && a[1].length() > 0 ? new File(a[1]) : null;
        if (pastaPng != null) pastaPng.mkdirs();
        String[] ids = a.length > 2 ? a[2].split(",") : new String[]{
                "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114", "PO_COMPARACAO_MEDIDAS_bolas_487868670",
                "PO_TRANSFORMACAO_RELACAO_bonecas_620955739"};
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        gerard.ui.GerardTema.instalar();
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        TesteMacacoHistorinhaNumeroRelativo.janela = j[0];
        Thread.sleep(2500);
        Robot r = new Robot(); r.setAutoDelay(10);
        TesteMacacoHistorinhaNumeroRelativo.r = r;
        TesteMacacoHistorinhaNumeroRelativo.saida = tsv.getAbsoluteFile().getParentFile();
        TesteMacacoHistorinhaNumeroRelativo.sorte = new java.util.Random(7);
        TesteRobotExploracaoAposConclusao.fechar(r);
        TesteMacacoHistorinhaNumeroRelativo.t = TesteRobotExploracaoAposConclusao.tela(j[0]);

        saida = new PrintWriter(tsv, "UTF-8");
        saida.println("cenario\tfase\tpasso\thash");
        for (String id : ids) cenario(id);
        saida.close();
        System.out.println("CAPTURAS registradas: " + linhas + " em " + tsv);
        System.exit(0);
    }

    static void cenario(String id) throws Exception {
        Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        TipoSituacaoAditiva tipo = null;
        for (SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
            if (s.getId().equals(id)) { tipo = s.getTipo(); break; }
        }
        if (tipo == null) { System.out.println("[PULADO] " + id); return; }
        boolean achou = false;
        for (int i = 0; i < 200 && !achou; i++) {
            TesteRobotExploracaoAposConclusao.selecionar(t, tipo);
            Thread.sleep(300);
            achou = id.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual());
        }
        System.out.println("== " + id + (achou ? "" : " (NÃO SORTEADA)"));
        if (!achou) return;
        TesteMacacoHistorinhaNumeroRelativo.atualizarOrigem();
        final Point origem = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { origem.setLocation(t().getLocationOnScreen()); } });
        Robot r = TesteMacacoHistorinhaNumeroRelativo.r;

        // 1) mouse-over em cada palavra do enunciado (até 16)
        final List<Point> palavras = new ArrayList<Point>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (ElementoTextoMovel x : t().elementosTexto) {
                if (palavras.size() >= 16) break;
                palavras.add(new Point(x.x + x.largura / 2, x.y - x.altura / 2));
            }
        } });
        int n = 0;
        for (Point p : palavras) { passar(r, origem, p); registrar(id, "enunciado", "palavra" + (n++), origem); }

        // 2) "Ver dica" três vezes (cada clique mostra o próximo papel; a dica fica persistente)
        final JButton[] dica = new JButton[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { dica[0] = t().botaoVerDicaPosicionamento; } });
        for (int k = 0; k < 3 && dica[0] != null; k++) {
            clicarBotao(r, dica[0]);
            registrar(id, "dica", "ver_dica" + k, origem);
            passar(r, origem, new Point(40, 400));
            registrar(id, "dica", "ver_dica" + k + "_mouse_fora", origem);
        }

        // 3) arrasta uma palavra com vínculo para um elemento do diagrama (certo ou errado) e observa o balão
        final List<Point> elementos = new ArrayList<Point>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (ElementoVergnaud e : t().elementosVergnaud) elementos.add(new Point(e.x + e.largura / 2, e.y + e.altura / 2));
        } });
        for (int k = 0; k < Math.min(palavras.size(), 4); k++) {
            Point de = palavras.get(k);
            for (int e = 0; e < Math.min(elementos.size(), 3); e++) {
                arrastar(r, origem, de, elementos.get(e));
                registrar(id, "soltura", "palavra" + k + "_elemento" + e, origem);
                passar(r, origem, new Point(40, 400));
                registrar(id, "soltura", "palavra" + k + "_elemento" + e + "_mouse_fora", origem);
                final Point alvo = elementos.get(e);
                passar(r, origem, alvo);
                registrar(id, "soltura", "palavra" + k + "_elemento" + e + "_sobre_elemento", origem);
            }
        }

        // 4) modela e passa o mouse pelas peças
        TesteMacacoHistorinhaNumeroRelativo.modelar();
        final List<Point> pecas = new ArrayList<Point>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel i : t().itensArrastaveis)
                pecas.add(new Point(i.x + i.largura / 2, i.y + i.altura / 2));
            for (ElementoVergnaud e : t().elementosVergnaud) pecas.add(new Point(e.x + e.largura / 2, e.y + e.altura / 2));
            for (gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn q : t().quadradinhosVenn)
                pecas.add(new Point(q.x + q.tamanho / 2, q.y + q.tamanho / 2));
        } });
        n = 0;
        for (Point p : pecas) { if (n > 40) break; passar(r, origem, p); registrar(id, "modelado", "peca" + (n++), origem); }
        saida.flush();
    }

    static Main.TelaGerard t() { return TesteMacacoHistorinhaNumeroRelativo.t; }

    static void passar(Robot r, Point origem, Point p) throws Exception {
        r.mouseMove(origem.x + p.x + 2, origem.y + p.y + 1);
        Thread.sleep(60);
        r.mouseMove(origem.x + p.x, origem.y + p.y);
        Thread.sleep(260);
    }

    static void arrastar(Robot r, Point origem, Point de, Point para) throws Exception {
        r.mouseMove(origem.x + de.x, origem.y + de.y);
        Thread.sleep(60);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(80);
        for (int k = 1; k <= 8; k++) {
            r.mouseMove(origem.x + de.x + (para.x - de.x) * k / 8, origem.y + de.y + (para.y - de.y) * k / 8);
            Thread.sleep(25);
        }
        Thread.sleep(120);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(1600);                                  // deixa o tremor/som de erro terminar antes de capturar
        TesteRobotExploracaoAposConclusao.fechar(r);
    }

    static void clicarBotao(Robot r, final JButton b) throws Exception {
        final Point p = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            Point l = b.getLocationOnScreen(); p.setLocation(l.x + b.getWidth() / 2, l.y + b.getHeight() / 2); } });
        r.mouseMove(p.x, p.y);
        Thread.sleep(120);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(600);
    }

    /** Captura a janela inteira e registra o hash (e, se pedido, o PNG). */
    static void registrar(String id, String fase, String passo, Point origem) throws Exception {
        final Rectangle area = new Rectangle();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            area.setBounds(t().getLocationOnScreen().x, t().getLocationOnScreen().y, t().getWidth(), t().getHeight()); } });
        BufferedImage img = TesteMacacoHistorinhaNumeroRelativo.r.createScreenCapture(area);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bos);
        byte[] md = MessageDigest.getInstance("MD5").digest(bos.toByteArray());
        StringBuilder h = new StringBuilder();
        for (byte b : md) h.append(String.format("%02x", b));
        saida.println(id + "\t" + fase + "\t" + passo + "\t" + h);
        linhas++;
        if (pastaPng != null) {
            String nome = (id.length() > 24 ? id.substring(3, 24) : id) + "_" + fase + "_" + passo + ".png";
            ImageIO.write(img, "png", new File(pastaPng, nome.replaceAll("[^A-Za-z0-9_.-]", "_")));
        }
    }
}
