import java.awt.Point;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Robot: depois do diagrama ficar azul, toda modificação é exploratória
 * (decisão da usuária, 2026-09-29). Conclui Composição de medidas pelos
 * protocolos reais, depois digita um valor errado na incógnita e verifica:
 * nenhum registro instrumental novo, nenhuma pergunta, conclusão mantida.
 */
public class TesteRobotExploracaoAposConclusao {
    static int falhas = 0;

    public static void main(String[] args) throws Exception {
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setVisible(true); j[0].toFront(); }});
        Thread.sleep(2000);
        Robot r = new Robot(); r.setAutoDelay(12);
        fechar(r);
        final Main.TelaGerard t = tela(j[0]);
        selecionar(t, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS); Thread.sleep(800); fechar(r);
        final Point o = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); }});
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
            arrastar(r, new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                    new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
        }
        System.out.println("soma=" + soma + " textos=" + textos.size());
        digitar(r, t, o, Integer.toString(soma));
        Thread.sleep(4000); fechar(r);
        boolean atingida = atingida(t);
        verificar("modelagem concluída pelos protocolos reais", atingida);
        int antes = contar("gerard_interacao_", "papel.incognita");
        digitar(r, t, o, "1");
        Thread.sleep(1200);
        boolean dialogo = haDialogo();
        fechar(r);
        int depois = contar("gerard_interacao_", "papel.incognita");
        verificar("sem pergunta de confirmação após a conclusão", !dialogo);
        verificar("sem registro instrumental novo após a conclusão (" + antes + "->" + depois + ")", depois == antes);
        verificar("conclusão atingida mantida", atingida(t));
        // Regra de 2026-10-02: restaurar reabre a primeira modelagem (volta a
        // persistir); sortear também. A exploração sem persistência só vale
        // após a conclusão correta com os dados originais.
        final Point b = new Point(); final boolean[] visivel = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            if (t.botaoRestaurarDiagrama != null && t.botaoRestaurarDiagrama.isShowing()) {
                visivel[0] = true;
                Point p = t.botaoRestaurarDiagrama.getLocationOnScreen();
                b.setLocation(p.x + t.botaoRestaurarDiagrama.getWidth() / 2,
                        p.y + t.botaoRestaurarDiagrama.getHeight() / 2);
            }
        }});
        verificar("botão Restaurar diagrama visível após a conclusão", visivel[0]);
        if (visivel[0]) {
            int restAntes = contar("gerard_interacao_", "tipo_restauracao=");
            r.mouseMove(b.x, b.y); r.delay(150);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(1500); fechar(r);
            int restDepois = contar("gerard_interacao_", "tipo_restauracao=");
            verificar("restauração após a conclusão gera registro (" + restAntes + "->" + restDepois + ")",
                    restDepois == restAntes + 1);
            verificar("restauração reabre a primeira modelagem", !atingida(t));
            // Nova primeira modelagem pelos protocolos reais: posicionar tudo e errar a incógnita.
            final List<ElementoTextoMovel> textos2 = new ArrayList<ElementoTextoMovel>();
            final List<ElementoVergnaud> els2 = new ArrayList<ElementoVergnaud>();
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                textos2.addAll(t.elementosTexto); els2.addAll(t.elementosVergnaud); }});
            for (ElementoTextoMovel tx : textos2) {
                if (!tx.possuiVinculoSemantico()) continue;
                ElementoVergnaud alvo = null;
                for (ElementoVergnaud e : els2) if (e.chavePapelSemantico.equals(tx.chavePapelSemantico)) alvo = e;
                if (alvo == null) continue;
                arrastar(r, new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                        new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
            }
            int incAntes = contar("gerard_interacao_", "papel.incognita");
            digitar(r, t, o, "1");
            Thread.sleep(1200);
            boolean perguntou = haDialogo();
            fechar(r);
            int incDepois = contar("gerard_interacao_", "papel.incognita");
            verificar("após restaurar, valor errado volta a perguntar confirmação", perguntou);
            verificar("após restaurar, a incógnita volta a gerar registro (" + incAntes + "->" + incDepois + ")",
                    incDepois > incAntes);
        }
        System.out.println("RESUMO falhas=" + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    static void digitar(Robot r, final Main.TelaGerard t, Point o, String v) throws Exception {
        final int[] c = new int[2]; final boolean[] ok = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (ItemTextoArrastavel it : t.itensArrastaveis)
                if (it.estaNoDiagrama() && it.editavel) { c[0] = it.x + it.largura / 2; c[1] = it.y + it.altura / 2; ok[0] = true; }
        }});
        if (!ok[0]) { System.out.println("incógnita editável não encontrada"); return; }
        System.out.println("digitando " + v);
        r.mouseMove(o.x + c[0], o.y + c[1]); r.delay(100);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); r.delay(35);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(400);
        for (char ch : v.toCharArray()) { int k = KeyEvent.VK_0 + (ch - '0'); r.keyPress(k); r.keyRelease(k); }
        r.keyPress(KeyEvent.VK_ENTER); r.keyRelease(KeyEvent.VK_ENTER);
    }

    static boolean atingida(final Main.TelaGerard t) throws Exception {
        final boolean[] b = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                java.lang.reflect.Field f = Main.TelaGerard.class.getDeclaredField("tentativaModelagemAtual");
                f.setAccessible(true);
                b[0] = ((gerard.dominio.campoaditivo.TentativaModelagemAditiva) f.get(t)).estaEncerradaPorConclusao();
            } catch (Exception e) { throw new RuntimeException(e); }
        }});
        return b[0];
    }

    static int contar(String prefixo, String trecho) throws Exception {
        File d = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        int n = 0; File[] fs = d.listFiles(); if (fs == null) return 0;
        for (File f : fs) {
            if (!f.getName().startsWith(prefixo)) continue;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            String l; while ((l = br.readLine()) != null) if (l.contains(trecho)) n++;
            br.close();
        }
        return n;
    }

    static void arrastar(Robot r, Point a, Point b) throws Exception {
        r.mouseMove(a.x, a.y); r.delay(80); r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        for (int i = 1; i <= 16; i++) { r.mouseMove(a.x + (b.x - a.x) * i / 16, a.y + (b.y - a.y) * i / 16); r.delay(12); }
        r.delay(80); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); r.waitForIdle(); Thread.sleep(900); fechar(r);
    }

    static boolean haDialogo() throws Exception {
        final boolean[] b = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (Window w : Window.getWindows()) if (w.isVisible() && w instanceof java.awt.Dialog) b[0] = true; }});
        return b[0];
    }

    static void fechar(Robot r) throws Exception {
        for (int i = 0; i < 4 && haDialogo(); i++) { r.keyPress(KeyEvent.VK_ENTER); r.keyRelease(KeyEvent.VK_ENTER); Thread.sleep(200); }
    }

    static void selecionar(final Main.TelaGerard t, final TipoSituacaoAditiva tipo) throws Exception {
        final List<JMenuItem> itens = new ArrayList<JMenuItem>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (int g = 0; g < t.menuCategoria.getItemCount(); g++) {
                JMenuItem gr = t.menuCategoria.getItem(g);
                if (gr instanceof JMenu) for (int i = 0; i < ((JMenu) gr).getItemCount(); i++) itens.add(((JMenu) gr).getItem(i));
            }
        }});
        for (final JMenuItem it : itens) {
            if (it == null || !it.isEnabled()) continue;
            SwingUtilities.invokeAndWait(new Runnable() { public void run() { it.doClick(); }});
            Thread.sleep(300);
            final boolean[] ok = new boolean[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() { ok[0] = t.tipoSituacaoSelecionada == tipo && t.categoriaSelecionadaParaAtividade; }});
            if (ok[0]) return;
        }
    }

    static void verificar(String n, boolean ok) { if (!ok) falhas++; System.out.println((ok ? "[OK] " : "[FALHA] ") + n); }

    static Main.TelaGerard tela(java.awt.Container c) {
        for (java.awt.Component x : c.getComponents()) {
            if (x instanceof Main.TelaGerard) return (Main.TelaGerard) x;
            if (x instanceof java.awt.Container) { Main.TelaGerard y = tela((java.awt.Container) x); if (y != null) return y; }
        }
        return null;
    }
}
