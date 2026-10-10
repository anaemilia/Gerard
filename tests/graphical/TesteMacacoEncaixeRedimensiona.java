import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Golden master do encaixe no diagrama e do redimensionamento, com o mouse REAL: para uma situação fixa, arrasta cada
 * palavra do enunciado até a figura correspondente (captura a cada soltura), solta uma palavra fora de qualquer figura,
 * e depois redimensiona a janela arrastando o canto inferior direito (menor, depois maior), capturando em cada passo.
 * Cada captura vai para o tsv (hash) e, se houver pasta, para PNG, para comparação pixel a pixel entre versões.
 *
 * Uso: TesteMacacoEncaixeRedimensiona <arquivo.tsv> <pastaPng> <idSituacao> <TIPO_SITUACAO>   (-Dpausa=ms)
 */
public class TesteMacacoEncaixeRedimensiona {
    static PrintWriter saida;
    static File pastaPng;
    static Robot r;
    static Main.TelaGerard t;
    static Main janela;
    static int espera = Integer.getInteger("pausa", 1000);

    public static void main(String[] a) throws Exception {
        File tsv = new File(a[0]);
        pastaPng = a[1].length() > 0 ? new File(a[1]) : null;
        if (pastaPng != null) pastaPng.mkdirs();
        final String id = a[2];
        TipoSituacaoAditiva tipo = TipoSituacaoAditiva.valueOf(a[3]);
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        gerard.ui.GerardTema.instalar();
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        janela = j[0];
        Thread.sleep(2500);
        r = new Robot(); r.setAutoDelay(10);
        TesteRobotExploracaoAposConclusao.fechar(r);
        t = TesteRobotExploracaoAposConclusao.tela(janela);
        TesteMacacoHistorinhaNumeroRelativo.janela = janela;
        TesteMacacoHistorinhaNumeroRelativo.t = t;
        TesteMacacoHistorinhaNumeroRelativo.r = r;
        saida = new PrintWriter(tsv, "UTF-8");
        saida.println("fase\tpasso\thash");

        int i = 0;
        do {
            TesteRobotExploracaoAposConclusao.selecionar(t, tipo);
            Thread.sleep(300);
        } while (++i < 300 && !id.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual()));
        if (!id.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual())) {
            System.out.println("[NÃO] situação " + id + " não sorteada");
            System.exit(1);
        }
        Thread.sleep(800);
        TesteRobotExploracaoAposConclusao.fechar(r);
        Thread.sleep(espera);
        registrar("e0", "inicio");

        final Point o = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); } });
        final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
        final List<ElementoVergnaud> els = new ArrayList<ElementoVergnaud>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            textos.addAll(t.elementosTexto); els.addAll(t.elementosVergnaud); }});
        int n = 0;
        ElementoTextoMovel primeiro = null;
        for (ElementoTextoMovel tx : textos) {
            if (!tx.possuiVinculoSemantico()) continue;
            ElementoVergnaud alvo = null;
            for (ElementoVergnaud e : els) if (e.chavePapelSemantico.equals(tx.chavePapelSemantico)) alvo = e;
            if (alvo == null) continue;
            if (primeiro == null) primeiro = tx;
            TesteRobotExploracaoAposConclusao.arrastar(r,
                    new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                    new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
            Thread.sleep(espera);
            TesteRobotExploracaoAposConclusao.fechar(r);
            registrar("e1", "encaixe_" + (n++));
        }
        // solta fora de qualquer figura: canto inferior esquerdo da tela do jogo
        if (primeiro != null) {
            final ElementoTextoMovel px = primeiro;
            final Point pos = new Point();
            SwingUtilities.invokeAndWait(new Runnable() { public void run() { pos.setLocation(px.x, px.y); } });
            TesteRobotExploracaoAposConclusao.arrastar(r,
                    new Point(o.x + pos.x + px.largura / 2, o.y + pos.y - px.altura / 2),
                    new Point(o.x + 30, o.y + t.getHeight() - 40));
            Thread.sleep(espera);
            TesteRobotExploracaoAposConclusao.fechar(r);
            registrar("e2", "solto_fora");
        }

        // a janela abre maximizada: sai desse estado (preparo) e então redimensiona arrastando o canto com o mouse
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            janela.setExtendedState(java.awt.Frame.NORMAL); janela.setBounds(60, 40, 1500, 850); } });
        Thread.sleep(espera + 1000);
        registrar("e3", "janela_normal");
        redimensionar(-220, -160);
        registrar("e3", "janela_menor");
        redimensionar(+220, +160);
        registrar("e3", "janela_volta");
        redimensionar(+160, +100);
        registrar("e3", "janela_maior");
        saida.close();
        System.out.println("CAPTURAS registradas em " + tsv);
        System.exit(0);
    }

    static void redimensionar(int dx, int dy) throws Exception {
        final Rectangle b = new Rectangle();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { b.setBounds(janela.getBounds()); } });
        int x = b.x + b.width - 3, y = b.y + b.height - 3;
        r.mouseMove(x, y);
        Thread.sleep(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        for (int k = 1; k <= 8; k++) {
            r.mouseMove(x + dx * k / 8, y + dy * k / 8);
            Thread.sleep(40);
        }
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(espera + 500);
    }

    static void registrar(String fase, String passo) throws Exception {
        final Rectangle area = new Rectangle();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            area.setBounds(t.getLocationOnScreen().x, t.getLocationOnScreen().y, t.getWidth(), t.getHeight()); } });
        BufferedImage img = r.createScreenCapture(area);
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        ImageIO.write(img, "png", bos);
        byte[] md = MessageDigest.getInstance("MD5").digest(bos.toByteArray());
        StringBuilder h = new StringBuilder();
        for (byte b : md) h.append(String.format("%02x", b));
        saida.println(fase + "\t" + passo + "\t" + h + "\t" + area.width + "x" + area.height);
        if (pastaPng != null) ImageIO.write(img, "png", new File(pastaPng, fase + "_" + passo + ".png"));
    }
}
