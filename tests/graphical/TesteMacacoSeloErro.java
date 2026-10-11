import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Evidência do selo de erro do desktop (SeloErroModelagem + feedback ERRO nas figuras), com o mouse REAL, para a paridade com a web
 * (tests/web/e2e_selo_erro_web.mjs): modela a situação, digita um valor errado na incógnita (captura com a pergunta Sim/Não aberta
 * e depois dela respondida), digita o valor correto (captura) e conta, em cada captura, os pixels na cor de erro do app
 * (UITemaGerard.COR_ERRO). Esperado: ruído (até 50 pixels) antes do erro, milhares com o selo e as figuras em erro, ruído depois da conclusão.
 *
 * Uso: TesteMacacoSeloErro <arquivo.tsv> <pastaPng> <idSituacao> <TIPO_SITUACAO> <valorErrado> <valorCorreto>   (-Dpausa=ms)
 */
public class TesteMacacoSeloErro {
    static PrintWriter saida;
    static File pastaPng;
    static Robot r;
    static Main.TelaGerard t;
    static int espera = Integer.getInteger("pausa", 1000);
    static int falhas = 0;
    /** Antialiasing de textos e bordas pode casar com a cor de erro em poucos pixels; o erro real tem milhares. */
    static final int RUIDO_MAXIMO = 50;

    public static void main(String[] a) throws Exception {
        File tsv = new File(a[0]);
        pastaPng = a[1].length() > 0 ? new File(a[1]) : null;
        if (pastaPng != null) pastaPng.mkdirs();
        final String id = a[2];
        TipoSituacaoAditiva tipo = TipoSituacaoAditiva.valueOf(a[3]);
        String errado = a[4];
        String correto = a[5];
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        gerard.ui.GerardTema.instalar();
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        Thread.sleep(2500);
        r = new Robot(); r.setAutoDelay(10);
        TesteRobotExploracaoAposConclusao.fechar(r);
        t = TesteRobotExploracaoAposConclusao.tela(j[0]);
        TesteMacacoHistorinhaNumeroRelativo.janela = j[0];
        TesteMacacoHistorinhaNumeroRelativo.t = t;
        TesteMacacoHistorinhaNumeroRelativo.r = r;
        saida = new PrintWriter(tsv, "UTF-8");
        saida.println("passo\tpixels_cor_erro\tesperado");

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

        TesteMacacoHistorinhaNumeroRelativo.atualizarOrigem();
        TesteMacacoHistorinhaNumeroRelativo.modelar();
        Thread.sleep(espera);
        TesteRobotExploracaoAposConclusao.fechar(r);
        verificar("modelado_sem_erro", true);

        final Point o = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); } });
        TesteRobotExploracaoAposConclusao.digitar(r, t, o, errado);
        Thread.sleep(espera + 1000);
        verificar("errado_com_pergunta_aberta", false);
        TesteRobotExploracaoAposConclusao.fechar(r);
        Thread.sleep(espera);
        verificar("errado_pergunta_respondida", false);

        TesteRobotExploracaoAposConclusao.digitar(r, t, o, correto);
        Thread.sleep(espera + 2500);
        TesteRobotExploracaoAposConclusao.fechar(r);
        verificar("correto_concluido", true);

        saida.close();
        System.out.println(falhas == 0 ? "APROVADO: selo de erro do desktop (mouse real)" : "FALHAS: " + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    /** Captura a tela do diagrama, grava o PNG e confere a contagem de pixels na cor de erro (zero ou positiva). */
    static void verificar(String passo, boolean semErro) throws Exception {
        final Rectangle area = new Rectangle();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            area.setBounds(t.getLocationOnScreen().x, t.getLocationOnScreen().y, t.getWidth(), t.getHeight()); } });
        BufferedImage img = r.createScreenCapture(area);
        Color erro = gerard.ui.UITemaGerard.COR_ERRO;
        int n = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                if (Math.abs(c.getRed() - erro.getRed()) <= 12 && Math.abs(c.getGreen() - erro.getGreen()) <= 12
                        && Math.abs(c.getBlue() - erro.getBlue()) <= 12) n++;
            }
        }
        boolean ok = semErro ? n <= RUIDO_MAXIMO : n > RUIDO_MAXIMO;
        if (!ok) falhas++;
        saida.println(passo + "\t" + n + "\t" + (semErro ? "<=" + RUIDO_MAXIMO : ">" + RUIDO_MAXIMO));
        System.out.println((ok ? "[OK] " : "[FALHA] ") + passo + ": " + n + " pixels na cor de erro (esperado " + (semErro ? "<=" + RUIDO_MAXIMO : ">" + RUIDO_MAXIMO) + ")");
        if (pastaPng != null) ImageIO.write(img, "png", new File(pastaPng, passo + ".png"));
    }
}
