import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Golden master do encaixe no diagrama de Venn e do redimensionamento, com o mouse REAL: para uma situação fixa, modela a situação e arrasta
 * cada quadradinho até um círculo do Venn (captura a cada soltura), solta um quadradinho fora de qualquer círculo,
 * e depois redimensiona a janela arrastando o canto inferior direito (menor, depois maior), capturando em cada passo.
 * Cada captura vai para o tsv (hash) e, se houver pasta, para PNG, para comparação pixel a pixel entre versões.
 *
 * Uso: TesteMacacoVennEncaixe <arquivo.tsv> <pastaPng> <idSituacao> <TIPO_SITUACAO>   (-Dpausa=ms)
 */
public class TesteMacacoVennEncaixe {
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

        TesteMacacoHistorinhaNumeroRelativo.atualizarOrigem();
        TesteMacacoHistorinhaNumeroRelativo.modelar();
        Thread.sleep(espera);
        TesteRobotExploracaoAposConclusao.fechar(r);
        registrar("e0", "modelado");
        final Point o = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); } });
        // o Venn só aparece por escalada da ajuda: três valores errados consecutivos digitados na incógnita
        String[] errados = {"99", "98", "97"};
        for (int e = 0; e < errados.length; e++) {
            TesteRobotExploracaoAposConclusao.digitar(r, t, o, errados[e]);
            Thread.sleep(espera + 1000);
            TesteRobotExploracaoAposConclusao.fechar(r);
            registrar("e0", "errado_" + e);
        }
        for (int q = 0; q < 4; q++) {
            final int k = q;
            final Point[] de = new Point[1], para = new Point[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                if (k >= t.quadradinhosVenn.size() || t.circulosVenn.isEmpty()) return;
                QuadradinhoVenn qd = t.quadradinhosVenn.get(k);
                CirculoVenn c = t.circulosVenn.get(k % t.circulosVenn.size());
                de[0] = new Point(qd.x + qd.tamanho / 2, qd.y + qd.tamanho / 2);
                para[0] = new Point(c.x + c.largura / 2, c.y + c.altura / 2);
            } });
            if (de[0] == null) { saida.println("e1	sem_quadradinho_" + k + "	-	-"); continue; }
            TesteRobotExploracaoAposConclusao.arrastar(r,
                    new Point(o.x + de[0].x, o.y + de[0].y), new Point(o.x + para[0].x, o.y + para[0].y));
            Thread.sleep(espera);
            TesteRobotExploracaoAposConclusao.fechar(r);
            registrar("e1", "venn_encaixe_" + q);
        }
        // solta o último quadradinho fora de qualquer círculo
        final Point[] de2 = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            if (!t.quadradinhosVenn.isEmpty()) {
                QuadradinhoVenn qd = t.quadradinhosVenn.get(0);
                de2[0] = new Point(qd.x + qd.tamanho / 2, qd.y + qd.tamanho / 2); } } });
        if (de2[0] != null) {
            TesteRobotExploracaoAposConclusao.arrastar(r,
                    new Point(o.x + de2[0].x, o.y + de2[0].y), new Point(o.x + 30, o.y + t.getHeight() - 40));
            Thread.sleep(espera);
            TesteRobotExploracaoAposConclusao.fechar(r);
            registrar("e2", "venn_solto_fora");
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
