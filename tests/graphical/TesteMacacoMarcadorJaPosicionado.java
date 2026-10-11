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
 * Seleção de marcador do texto no desktop, com o mouse REAL, para a paridade com a web (tests/web/e2e_marcador_ja_posicionado_web.mjs):
 * modela a situação (os números vão ao diagrama) e aperta de novo um número já posicionado no enunciado. Esperado
 * (HandlerInteracaoSelecaoMarcadorTexto, JA_POSICIONADO): nenhum arraste é iniciado, nenhum item novo entra no diagrama e o
 * aviso ui.tooltip.alreadyPositioned é apresentado.
 *
 * Uso: TesteMacacoMarcadorJaPosicionado <arquivo.tsv> <pastaPng> <idSituacao> <TIPO_SITUACAO>   (-Dpausa=ms)
 */
public class TesteMacacoMarcadorJaPosicionado {
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

        final Point o = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); } });
        final int[] antes = new int[1];
        final java.util.List<gerard.campoaditivo.diagrama.elementos.MarcadorTexto> marcadores =
                new java.util.ArrayList<gerard.campoaditivo.diagrama.elementos.MarcadorTexto>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            antes[0] = t.itensArrastaveis.size(); marcadores.addAll(t.marcadoresFixosTexto); t.mostrarAnotacaoMouseOver = false; } });
        conferir("ha_numero_posicionado_no_diagrama", antes[0] > 0);
        gerard.campoaditivo.diagrama.elementos.MarcadorTexto m = null;
        for (gerard.campoaditivo.diagrama.elementos.MarcadorTexto candidato : marcadores) {
            if (candidato.chavePapel != null && candidato.chavePapel.length() > 0) { m = candidato; break; }
        }
        conferir("ha_marcador_com_papel_no_enunciado", m != null);

        Thread.sleep(espera);
        r.mouseMove(o.x + m.x + m.largura / 2, o.y + m.y + m.altura / 2);
        Thread.sleep(150);
        r.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(150);
        final boolean[] ativo = new boolean[1], aviso = new boolean[1];
        final String[] texto = new String[1];
        final int[] depois = new int[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            ativo[0] = t.handlerItemTextoArrastavel.estaAtivo(); aviso[0] = t.mostrarAnotacaoMouseOver;
            texto[0] = t.textoAnotacaoMouseOver; depois[0] = t.itensArrastaveis.size(); } });
        r.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
        tirarCaptura("selecao_repetida");
        conferir("selecionar_de_novo_nao_inicia_arraste", !ativo[0]);
        conferir("mostra_o_aviso", aviso[0]);
        String esperado = gerard.i18n.ServicoLocalizacao.getInstancia().texto("ui.tooltip.alreadyPositioned");
        conferir("texto_do_aviso_e_ui.tooltip.alreadyPositioned (" + texto[0] + ")", esperado.equals(texto[0]));
        conferir("nao_cria_outro_item_no_diagrama", depois[0] == antes[0]);

        saida.close();
        System.out.println(falhas == 0 ? "APROVADO: número já posicionado não é selecionado de novo (mouse real)" : "FALHAS: " + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    static void conferir(String nome, boolean ok) {
        if (!ok) falhas++;
        saida.println(nome + "	" + (ok ? "OK" : "FALHA"));
        System.out.println((ok ? "[OK] " : "[FALHA] ") + nome);
    }

    static void tirarCaptura(String nome) throws Exception {
        final Rectangle area = new Rectangle();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            area.setBounds(t.getLocationOnScreen().x, t.getLocationOnScreen().y, t.getWidth(), t.getHeight()); } });
        if (pastaPng != null) ImageIO.write(r.createScreenCapture(area), "png", new File(pastaPng, nome + ".png"));
    }
}
