import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;

/**
 * Macaco de ARRASTE (mouseDragged / processarMovimentoArraste) e de CLIQUE (mouseClicked, inclusive duplo clique) do
 * mouse REAL. Para o centro de cada peça (itens, textos do enunciado, elementos e conectores) faz, em sequência:
 *  - arraste curto (dentro da área) e arraste longo (para fora dela), registrando a assinatura NO MEIO do arraste
 *    (botão ainda apertado) e depois de SOLTAR;
 *  - clique simples e duplo clique, registrando a assinatura (o duplo clique abre diálogos de edição, fechados com Esc).
 * A assinatura é a de TesteMacacoPressionamentoMouse (quem está ativo, cursor, foco, dica, diálogo/menu, estado das
 * peças). Golden master: antes e depois de mover a ordem do movimento e do duplo clique para fora da Main, as tabelas
 * têm de ser IGUAIS.
 *
 * Uso: TesteMacacoArrasteCliqueMouse <arquivo.tsv> [id1,id2,...]
 */
public class TesteMacacoArrasteCliqueMouse {
    static PrintWriter saida;
    static int linhas;

    public static void main(String[] a) throws Exception {
        File tsv = new File(a.length > 0 ? a[0] : "arraste_clique.tsv");
        String[] ids = a.length > 1 ? a[1].split(",") : new String[]{
                "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114", "PO_COMPARACAO_MEDIDAS_bolas_487868670"};
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
        TesteMacacoHistorinhaNumeroRelativo.sorte = new Random(7);
        TesteRobotExploracaoAposConclusao.fechar(r);
        TesteMacacoHistorinhaNumeroRelativo.t = TesteRobotExploracaoAposConclusao.tela(j[0]);

        saida = new PrintWriter(tsv, "UTF-8");
        saida.println("cenario\tfase\tx\ty\toperacao\tdurante\tdepois");
        for (String id : ids) cenario(id);
        saida.close();
        System.out.println("OPERAÇÕES registradas: " + linhas + " em " + tsv);
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
            TesteMacacoPressionamentoMouse.limpar();
            achou = id.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual());
        }
        System.out.println("== " + id + (achou ? "" : " (NÃO SORTEADA)"));
        if (!achou) return;
        TesteMacacoHistorinhaNumeroRelativo.atualizarOrigem();
        operar(id, "vazio");                       // números e palavras ainda no enunciado
        TesteMacacoHistorinhaNumeroRelativo.modelar();
        TesteMacacoPressionamentoMouse.limpar();
        operar(id, "modelado");
        if (TesteMacacoHistorinhaNumeroRelativo.seletorOperacaoAtivo()) TesteMacacoHistorinhaNumeroRelativo.macacoOperacao();
        else TesteMacacoHistorinhaNumeroRelativo.macacoValor();
        Thread.sleep(2500);
        TesteMacacoPressionamentoMouse.limpar();
        operar(id, "limite");
    }

    /** Para o centro de cada peça: arraste curto, arraste longo, clique e duplo clique. */
    static void operar(String id, String fase) throws Exception {
        final Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        final List<Point> centros = new ArrayList<Point>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (ItemTextoArrastavel i : t.itensArrastaveis) centros.add(new Point(i.x + i.largura / 2, i.y + i.altura / 2));
            for (gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel x : t.elementosTexto) {
                if (centros.size() > 70) break;                                   // limita o volume de palavras
                centros.add(new Point(x.x + x.largura / 2, x.y - x.altura / 2));
            }
            for (gerard.campoaditivo.diagrama.elementos.ElementoVergnaud x : t.elementosVergnaud) centros.add(new Point(x.x + x.largura / 2, x.y + x.altura / 2));
            for (gerard.campoaditivo.diagrama.elementos.ConectorVergnaud c : t.conectoresVergnaud) centros.add(new Point((c.x1 + c.x2) / 2, (c.y1 + c.y2) / 2));
        } });
        final Point origem = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { origem.setLocation(t.getLocationOnScreen()); } });
        Robot r = TesteMacacoHistorinhaNumeroRelativo.r;
        int[][] deslocamentos = {{30, 8}, {220, 140}};
        for (Point p : centros) {
            for (int[] d : deslocamentos) {
                arrastar(r, t, id, fase, origem, p, d[0], d[1]);
            }
            clicar(r, t, id, fase, origem, p, 1);
            clicar(r, t, id, fase, origem, p, 2);
        }
        saida.flush();
    }

    static void arrastar(Robot r, Main.TelaGerard t, String id, String fase, Point origem, Point p, int dx, int dy) throws Exception {
        r.mouseMove(origem.x + p.x, origem.y + p.y);
        Thread.sleep(40);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(80);
        for (int k = 1; k <= 6; k++) {
            r.mouseMove(origem.x + p.x + dx * k / 6, origem.y + p.y + dy * k / 6);
            Thread.sleep(25);
        }
        Thread.sleep(120);
        String durante = TesteMacacoPressionamentoMouse.assinatura(t);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(160);
        String depois = TesteMacacoPressionamentoMouse.assinatura(t);
        saida.println(id + "\t" + fase + "\t" + p.x + "\t" + p.y + "\tarraste+" + dx + "," + dy + "\t" + durante + "\t" + depois);
        linhas++;
        TesteMacacoPressionamentoMouse.limpar();
    }

    static void clicar(Robot r, Main.TelaGerard t, String id, String fase, Point origem, Point p, int vezes) throws Exception {
        r.mouseMove(origem.x + p.x, origem.y + p.y);
        Thread.sleep(40);
        for (int k = 0; k < vezes; k++) {
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(60);
        }
        Thread.sleep(350);
        String durante = TesteMacacoPressionamentoMouse.assinatura(t);      // com o diálogo de edição ainda aberto, se abriu
        TesteMacacoPressionamentoMouse.limpar();
        Thread.sleep(150);
        String depois = TesteMacacoPressionamentoMouse.assinatura(t);
        saida.println(id + "\t" + fase + "\t" + p.x + "\t" + p.y + "\tclique x" + vezes + "\t" + durante + "\t" + depois);
        linhas++;
    }
}
