import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;

/**
 * Desktop com o mouse REAL, referência da paridade com tests/web/e2e_posicionamento_incorreto_web.mjs: soltar um número em
 * qualquer figura é julgado só como posicionamento certo ou errado. O posicionamento incorreto deixa a pergunta aberta e o
 * número visível até ser corrigido (arrastá-lo à figura certa).
 *
 * Uso: TesteMacacoPosicionamentoIncorreto <arquivo.tsv> <pastaPng> <idSituacao> <TIPO_SITUACAO> <papelOrigem> <papelDestino> <papelCerto>   (-Dpausa=ms)
 */
public class TesteMacacoPosicionamentoIncorreto {
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
        final Point o = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); } });
        final java.util.List<ElementoTextoMovel> textos = new java.util.ArrayList<ElementoTextoMovel>();
        final java.util.List<gerard.campoaditivo.diagrama.elementos.ElementoVergnaud> els = new java.util.ArrayList<gerard.campoaditivo.diagrama.elementos.ElementoVergnaud>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { textos.addAll(t.elementosTexto); els.addAll(t.elementosVergnaud); } });
        ElementoTextoMovel origem = null;
        for (ElementoTextoMovel tx : textos) if (tx.possuiVinculoSemantico() && a[4].equals(tx.chavePapelSemantico)) origem = tx;
        gerard.campoaditivo.diagrama.elementos.ElementoVergnaud destino = null;
        for (gerard.campoaditivo.diagrama.elementos.ElementoVergnaud e : els) if (a[5].equals(e.chavePapelSemantico)) destino = e;
        System.out.println("origem=" + (origem != null) + " destino=" + (destino != null));
        TesteRobotExploracaoAposConclusao.arrastar(r,
                new Point(o.x + origem.x + origem.largura / 2, o.y + origem.y - origem.altura / 2),
                new Point(o.x + destino.x + destino.largura / 2, o.y + destino.y + destino.altura / 2));
        Thread.sleep(espera + 1000);
        final StringBuilder estado = new StringBuilder();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (gerard.campoaditivo.diagrama.elementos.ElementoVergnaud e : t.elementosVergnaud)
                estado.append(e.chavePapelSemantico).append("=[").append(e.textoEditavel).append("] ");
            for (gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel it : t.itensArrastaveis)
                estado.append("item(").append(it.valor).append(",noDiagrama=").append(it.estaNoDiagrama()).append(") ");
            estado.append("anotacao=").append(t.mostrarAnotacaoMouseOver ? t.textoAnotacaoMouseOver : "-");
        } });
        System.out.println("ESTADO " + estado);
        final boolean[] pergunta = new boolean[1];
        final String[] textoPergunta = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            pergunta[0] = t.questionamentoPersistente.estaAberta(); textoPergunta[0] = t.questionamentoPersistente.getTexto(); } });
        conferir("pergunta_aberta_no_posicionamento_incorreto (" + textoPergunta[0] + ")", pergunta[0]);
        tirarCaptura("posicionamento_incorreto");
        Thread.sleep(6000);
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { pergunta[0] = t.questionamentoPersistente.estaAberta(); } });
        conferir("pergunta_continua_aberta_depois_de_6s", pergunta[0]);
        tirarCaptura("posicionamento_incorreto_apos_6s");
        // No desktop o número já está na caixa: corrigir é arrastá-lo de lá até a caixa certa.
        final gerard.campoaditivo.diagrama.elementos.ElementoVergnaud certo = figuraDe(els, a[6]);
        final java.util.List<gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel> itens =
                new java.util.ArrayList<gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { itens.addAll(t.itensArrastaveis); } });
        gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel item = itens.get(0);
        TesteRobotExploracaoAposConclusao.arrastar(r,
                new Point(o.x + item.x + item.largura / 2, o.y + item.y + item.altura / 2),
                new Point(o.x + certo.x + certo.largura / 2, o.y + certo.y + certo.altura / 2));
        Thread.sleep(espera + 1000);
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { pergunta[0] = t.questionamentoPersistente.estaAberta(); } });
        conferir("corrigido_a_pergunta_some", !pergunta[0]);
        tirarCaptura("posicionamento_corrigido");
        saida.close();
        System.out.println(falhas == 0 ? "APROVADO: posicionamento incorreto julgado e persistente (mouse real)" : "FALHAS: " + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    static gerard.campoaditivo.diagrama.elementos.ElementoVergnaud figuraDe(java.util.List<gerard.campoaditivo.diagrama.elementos.ElementoVergnaud> els, String papel) {
        for (gerard.campoaditivo.diagrama.elementos.ElementoVergnaud e : els) if (papel.equals(e.chavePapelSemantico)) return e;
        return null;
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
