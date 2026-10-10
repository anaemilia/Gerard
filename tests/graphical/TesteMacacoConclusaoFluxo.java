import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Golden master do FLUXO de conclusão da modelagem, com o mouse REAL: conclui a modelagem pelos protocolos reais
 * (arrastar as palavras e digitar a soma na incógnita), espera o selo, passa o mouse no selo (tip Sim/Não), tira o mouse,
 * volta, escolhe "Não" (permanece), passa o mouse de novo e escolhe "Sim" (nova situação), depois clica no selo de outra
 * conclusão. Em cada passo captura a janela (hash + PNG) e, ao final, grava as linhas CONCLUSAO_MODELAGEM do log com os
 * campos estáveis (tarefa, origem, detalhes), já que a ordem e o conteúdo dessas linhas fazem parte do contrato.
 *
 * Uso: TesteMacacoConclusaoFluxo <arquivo.tsv> [pastaPng]   (-Dpausa=ms para a espera por passo)
 */
public class TesteMacacoConclusaoFluxo {
    static PrintWriter saida;
    static File pastaPng;
    static Robot r;
    static Main.TelaGerard t;
    static Point origem = new Point();
    static final String ID = "PO_COMPOSICAO_MEDIDAS_bolas_1098018440";
    static int espera = Integer.getInteger("pausa", 1500);

    public static void main(String[] a) throws Exception {
        File tsv = new File(a.length > 0 ? a[0] : "conclusao.tsv");
        pastaPng = a.length > 1 && a[1].length() > 0 ? new File(a[1]) : null;
        if (pastaPng != null) pastaPng.mkdirs();
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
        saida.println("fase\tpasso\thash");

        for (int rodada = 0; rodada < 2; rodada++) {
            for (int i = 0; i < 200 && !ID.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual()); i++) {
                TesteRobotExploracaoAposConclusao.selecionar(t, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
                Thread.sleep(300);
            }
            Thread.sleep(800);
            TesteRobotExploracaoAposConclusao.fechar(r);
            final Point o = new Point();
            SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); } });
            origem = o;
            concluir(o);
            String f = "r" + rodada;
            registrar(f, "selo_apos_conclusao");
            passar(selo());
            registrar(f, "tip_sobre_selo");
            passar(new Point(40, 400));
            Thread.sleep(espera);
            registrar(f, "mouse_fora_tip_some");
            passar(selo());
            registrar(f, "tip_de_novo");
            if (rodada == 0) {
                clicar(opcao(false));
                registrar(f, "escolheu_nao");
                passar(new Point(40, 400));
                registrar(f, "nao_mouse_fora");
                passar(selo());
                clicar(opcao(true));
                Thread.sleep(espera);
                TesteRobotExploracaoAposConclusao.fechar(r);
                saida.println(f + "\tsim_nova_situacao\ttipo=" + tipoAtual() + ";concluida=" + concluida());
            } else {
                passar(new Point(40, 400));
                Thread.sleep(espera);
                clicar(selo());
                Thread.sleep(espera);
                TesteRobotExploracaoAposConclusao.fechar(r);
                saida.println(f + "\tclique_proxima_tarefa\ttipo=" + tipoAtual() + ";concluida=" + concluida());
            }
        }
        for (String linha : linhasLog()) saida.println("LOG\t-\t" + linha);
        saida.close();
        System.out.println("CAPTURAS/linhas registradas em " + tsv);
        System.exit(0);
    }

    static void concluir(Point o) throws Exception {
        final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
        final List<ElementoVergnaud> els = new ArrayList<ElementoVergnaud>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            textos.addAll(t.elementosTexto); els.addAll(t.elementosVergnaud); }});
        int soma = 0;
        for (ElementoTextoMovel tx : textos) {
            if (!tx.possuiVinculoSemantico()) continue;
            ElementoVergnaud alvo = null;
            for (ElementoVergnaud e : els) if (e.chavePapelSemantico.equals(tx.chavePapelSemantico)) alvo = e;
            if (alvo == null) continue;
            if (!"?".equals(tx.valor)) soma += Integer.parseInt(tx.valor.replaceAll("[^0-9]", ""));
            TesteRobotExploracaoAposConclusao.arrastar(r,
                    new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                    new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
        }
        TesteRobotExploracaoAposConclusao.digitar(r, t, o, Integer.toString(soma));
        Thread.sleep(4000);
        TesteRobotExploracaoAposConclusao.fechar(r);
        if (!TesteRobotExploracaoAposConclusao.atingida(t)) System.out.println("[AVISO] modelagem não concluída");
        Thread.sleep(espera);
    }

    static Point selo() throws Exception {
        final Point p = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            Rectangle b = t.seloConclusaoModelagem.getBounds();
            p.setLocation(b.x + b.width / 2, b.y + b.height / 2); } });
        return p;
    }

    static Point opcao(final boolean sim) throws Exception {
        final Point p = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            List<JRadioButton> rb = new ArrayList<JRadioButton>();
            coletar(t.tipConclusaoModelagem, rb);
            JRadioButton alvo = rb.get(sim ? 0 : 1);
            Point l = alvo.getLocationOnScreen();
            p.setLocation(l.x + alvo.getWidth() / 2 - origem.x, l.y + alvo.getHeight() / 2 - origem.y); } });
        return p;
    }

    static void coletar(Container c, List<JRadioButton> saida) {
        for (Component x : c.getComponents()) {
            if (x instanceof JRadioButton) saida.add((JRadioButton) x);
            else if (x instanceof Container) coletar((Container) x, saida);
        }
    }

    static String tipoAtual() throws Exception {
        final String[] s = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { s[0] = String.valueOf(t.tipoSituacaoSelecionada); } });
        return s[0];
    }

    static boolean concluida() throws Exception {
        return TesteRobotExploracaoAposConclusao.atingida(t);
    }

    static void passar(Point p) throws Exception {
        r.mouseMove(origem.x + p.x + 2, origem.y + p.y + 1);
        Thread.sleep(60);
        r.mouseMove(origem.x + p.x, origem.y + p.y);
        Thread.sleep(espera);
    }

    static void clicar(Point p) throws Exception {
        passar(p);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(espera);
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
        saida.println(fase + "\t" + passo + "\t" + h);
        if (pastaPng != null) ImageIO.write(img, "png", new File(pastaPng, fase + "_" + passo + ".png"));
    }

    /** Linhas CONCLUSAO_MODELAGEM do log, sem o carimbo de data/hora (campo que varia entre execuções). */
    static List<String> linhasLog() throws Exception {
        List<String> out = new ArrayList<String>();
        File d = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        File[] fs = d.listFiles();
        if (fs == null) return out;
        java.util.Arrays.sort(fs, new java.util.Comparator<File>() {
            public int compare(File a, File b) { return Long.compare(b.lastModified(), a.lastModified()); } });
        for (File f : fs) {
            if (!f.getName().startsWith("gerard_interacao_")) continue;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            String l;
            while ((l = br.readLine()) != null) {
                if (!l.contains("CONCLUSAO_MODELAGEM")) continue;
                int i = l.indexOf("CONCLUSAO_MODELAGEM");
                out.add(l.substring(Math.max(0, i - 160)).replaceAll("\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}[.\\d]*", "<t>"));
            }
            br.close();
            break;
        }
        return out;
    }
}
