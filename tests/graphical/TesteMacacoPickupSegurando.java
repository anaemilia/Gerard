import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;

/**
 * Golden master VISUAL do pickup (peça carregada pelo mouse, desenhada em primeiro plano): com o mouse REAL,
 * pressiona uma palavra do enunciado e, depois de modelar, uma peça de texto e um quadradinho do Venn; MOVE em
 * passos SEGURANDO o botão e captura a janela em cada passo (antes de soltar). Reaproveita o registro de capturas de
 * TesteMacacoAnotacaoMouseOver.
 *
 * Uso: TesteMacacoPickupSegurando <arquivo.tsv> [pastaPng] [id1,id2,...]
 */
public class TesteMacacoPickupSegurando {
    public static void main(String[] a) throws Exception {
        File tsv = new File(a.length > 0 ? a[0] : "pickup.tsv");
        File png = a.length > 1 && a[1].length() > 0 ? new File(a[1]) : null;
        if (png != null) png.mkdirs();
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
        TesteMacacoAnotacaoMouseOver.pastaPng = png;
        TesteMacacoAnotacaoMouseOver.saida = new PrintWriter(tsv, "UTF-8");
        TesteMacacoAnotacaoMouseOver.saida.println("cenario\tfase\tpasso\thash");
        for (String id : ids) cenario(id);
        TesteMacacoAnotacaoMouseOver.saida.close();
        System.out.println("CAPTURAS registradas: " + TesteMacacoAnotacaoMouseOver.linhas + " em " + tsv);
        System.exit(0);
    }

    static Main.TelaGerard t() { return TesteMacacoHistorinhaNumeroRelativo.t; }

    static void cenario(String id) throws Exception {
        TipoSituacaoAditiva tipo = null;
        for (SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
            if (s.getId().equals(id)) { tipo = s.getTipo(); break; }
        }
        if (tipo == null) { System.out.println("[PULADO] " + id); return; }
        boolean achou = false;
        for (int i = 0; i < 200 && !achou; i++) {
            TesteRobotExploracaoAposConclusao.selecionar(t(), tipo);
            Thread.sleep(300);
            achou = id.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual());
        }
        System.out.println("== " + id + (achou ? "" : " (NÃO SORTEADA)"));
        if (!achou) return;
        TesteMacacoHistorinhaNumeroRelativo.atualizarOrigem();
        final Point origem = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { origem.setLocation(t().getLocationOnScreen()); } });
        Robot r = TesteMacacoHistorinhaNumeroRelativo.r;

        final List<Point> palavras = new ArrayList<Point>();
        final List<Point> elementos = new ArrayList<Point>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (ElementoTextoMovel x : t().elementosTexto) {
                if (palavras.size() >= 3) break;
                palavras.add(new Point(x.x + x.largura / 2, x.y - x.altura / 2));
            }
            for (ElementoVergnaud e : t().elementosVergnaud) elementos.add(new Point(e.x + e.largura / 2, e.y + e.altura / 2));
        } });
        int n = 0;
        for (Point p : palavras) segurar(id, "palavra" + (n++), r, origem, p, elementos.isEmpty() ? new Point(300, 400) : elementos.get(0));

        TesteMacacoHistorinhaNumeroRelativo.modelar();
        final List<Point> itens = new ArrayList<Point>();
        final List<Point> quadradinhos = new ArrayList<Point>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (ItemTextoArrastavel i : t().itensArrastaveis) itens.add(new Point(i.x + i.largura / 2, i.y + i.altura / 2));
            for (QuadradinhoVenn q : t().quadradinhosVenn) quadradinhos.add(new Point(q.x + q.tamanho / 2, q.y + q.tamanho / 2));
        } });
        n = 0;
        for (Point p : itens) { if (n >= 3) break; segurar(id, "item" + (n++), r, origem, p, new Point(p.x + 120, p.y + 60)); }
        n = 0;
        for (Point p : quadradinhos) { if (n >= 3) break; segurar(id, "quadradinho" + (n++), r, origem, p, new Point(p.x + 90, p.y + 50)); }
        TesteMacacoAnotacaoMouseOver.saida.flush();
    }

    /** Pressiona em "de", move em 4 passos SEGURANDO e captura em cada um; só então solta. */
    static void segurar(String id, String nome, Robot r, Point origem, Point de, Point para) throws Exception {
        r.mouseMove(origem.x + de.x, origem.y + de.y);
        Thread.sleep(80);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(120);
        for (int k = 1; k <= 4; k++) {
            r.mouseMove(origem.x + de.x + (para.x - de.x) * k / 4, origem.y + de.y + (para.y - de.y) * k / 4);
            Thread.sleep(Integer.getInteger("espera", 250));
            TesteMacacoAnotacaoMouseOver.registrar(id, "segurando", nome + "_passo" + k, origem);
        }
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(1600);
        TesteRobotExploracaoAposConclusao.fechar(r);
    }
}
