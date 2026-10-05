import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import gerard.aplicacao.EstadoNumericoComparacaoCategorias;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Macaco da tela "Comparar categorias" (Arquivo > Comparar categorias): para CADA categoria e CADA caixa de
 * valor da representação preenchida, duplo clique real (Robot) abre o diálogo de valor, digita um número e
 * confirma; o teste confere o DESDOBRAMENTO: o papel numérico certo mudou (e só ele, nas relações diretas)
 * e as outras representações da mesma tela acompanharam o modelo. O oráculo é uma tabela independente do
 * código de produção (não usa CategoriasComparaveis).
 *
 * Uso: TesteMacacoComparacaoCategorias [pastaSaida]
 */
public class TesteMacacoComparacaoCategorias {
    static Main janela;
    static Main.TelaGerard t;
    static Robot r;
    static File saida;
    static int falhas;
    static int cliques;

    public static void main(String[] a) throws Exception {
        saida = new File(a.length > 0 ? a[0] : "macaco_comparacao_categorias");
        saida.mkdirs();
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        gerard.ui.GerardTema.instalar();
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        janela = j[0];
        Thread.sleep(2500);
        r = new Robot(); r.setAutoDelay(15);
        TesteRobotExploracaoAposConclusao.fechar(r);
        t = TesteRobotExploracaoAposConclusao.tela(janela);

        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                java.lang.reflect.Method m = Main.TelaGerard.class.getDeclaredMethod("abrirTelaComparacaoCategorias");
                m.setAccessible(true);
                m.invoke(t);
            } catch (Exception e) { throw new IllegalStateException(e); } }});
        Thread.sleep(2000);

        final JDialog[] dialogo = new JDialog[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (Window w : Window.getWindows()) {
                if (w instanceof JDialog && w.isShowing() && acharMini(((JDialog) w).getContentPane(), new ArrayList<Component>()) > 0) {
                    dialogo[0] = (JDialog) w;
                }
            } }});
        if (dialogo[0] == null) { System.out.println("[FALHA] tela Comparar categorias não abriu"); System.exit(1); }
        dialogo[0].setAlwaysOnTop(true);
        Thread.sleep(800);
        final List<Component> minis = new ArrayList<Component>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { acharMini(dialogo[0].getContentPane(), minis); } });
        System.out.println("representações na tela: " + minis.size());

        int preenchidas = 0;
        for (Component mini : minis) {
            Field fFormal = mini.getClass().getDeclaredField("formal"); fFormal.setAccessible(true);
            if (fFormal.getBoolean(mini)) continue;                 // a representação formal é estática (sem edição)
            Field fCat = mini.getClass().getDeclaredField("categoria"); fCat.setAccessible(true);
            TipoSituacaoAditiva categoria = (TipoSituacaoAditiva) fCat.get(mini);
            preenchidas++;
            for (int i = 0; i < 3; i++) conferirCaixa(dialogo[0], mini, categoria, i);
        }
        capturar(dialogo[0], "tela_final");
        System.out.println("CLIQUES (duplo) verificados com desdobramentos: " + cliques + " em " + preenchidas + " categorias");
        System.out.println("RESUMO: falhas=" + falhas);
        System.exit(falhas == 0 && preenchidas == 3 && cliques == 9 ? 0 : 1);
    }

    /** Oráculo independente: papel esperado por categoria e posição. */
    static EstadoNumericoComparacaoCategorias.Papel esperado(TipoSituacaoAditiva c, int i) {
        EstadoNumericoComparacaoCategorias.Papel p1 = EstadoNumericoComparacaoCategorias.Papel.PRIMEIRA_PARCELA;
        EstadoNumericoComparacaoCategorias.Papel p2 = EstadoNumericoComparacaoCategorias.Papel.SEGUNDA_PARCELA;
        EstadoNumericoComparacaoCategorias.Papel tt = EstadoNumericoComparacaoCategorias.Papel.TOTAL;
        if (c == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) return i == 0 ? tt : i == 1 ? p1 : p2;
        return i == 0 ? p1 : i == 1 ? p2 : tt;
    }

    static void conferirCaixa(final JDialog dialogo, final Component mini, TipoSituacaoAditiva categoria, int indice) throws Exception {
        final Object painel = painelDe(mini);
        EstadoNumericoComparacaoCategorias modelo = (EstadoNumericoComparacaoCategorias) campo(painel, "modelo");
        final Point[] alvo = new Point[1];
        final int idx = indice;
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                List<?> alvos = (List<?>) campo(mini, "alvos");
                Rectangle rc = (Rectangle) alvos.get(idx);
                Point l = mini.getLocationOnScreen();
                alvo[0] = new Point(l.x + rc.x + rc.width / 2, l.y + rc.y + rc.height / 2);
            } catch (Exception e) { throw new IllegalStateException(e); } }});
        int valor = 31 + 7 * indice + (categoria.ordinal() % 5);
        r.mouseMove(alvo[0].x, alvo[0].y); r.delay(150);
        for (int k = 0; k < 2; k++) { r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); r.delay(60); }
        Thread.sleep(1200);
        if (dialogoDeValor() == null) {
            falhas++; System.out.println("[FALHA] " + categoria + " caixa " + indice + ": o duplo clique não abriu o diálogo de valor");
            return;
        }
        clicarCampoDoDialogo(dialogoDeValor());      // o foco inicial é do botão: clica no campo, como a pessoa faria
        Thread.sleep(300);
        r.keyPress(KeyEvent.VK_CONTROL); r.keyPress(KeyEvent.VK_A); r.keyRelease(KeyEvent.VK_A); r.keyRelease(KeyEvent.VK_CONTROL);
        for (char c : String.valueOf(valor).toCharArray()) { r.keyPress(KeyEvent.VK_0 + (c - '0')); r.keyRelease(KeyEvent.VK_0 + (c - '0')); }
        r.keyPress(KeyEvent.VK_ENTER); r.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(900);
        if (dialogoDeValor() != null) {              // Enter no campo não confirmou: clica em "Confirmar" com o mouse
            clicarBotaoDoDialogo(dialogoDeValor(), "Confirmar");
            Thread.sleep(900);
        }
        if (dialogoDeValor() != null) { falhas++; System.out.println("[FALHA] diálogo de valor não fechou"); return; }
        cliques++;
        EstadoNumericoComparacaoCategorias.Papel papel = esperado(categoria, indice);
        int atual = papel == EstadoNumericoComparacaoCategorias.Papel.PRIMEIRA_PARCELA ? modelo.getPrimeiraParcela()
                : papel == EstadoNumericoComparacaoCategorias.Papel.SEGUNDA_PARCELA ? modelo.getSegundaParcela()
                : modelo.getTotal();
        // desdobramento 1: o papel certo recebeu o valor digitado
        boolean papelOk = atual == valor;
        // desdobramento 2: a regra aditiva continua valendo (total = parcela 1 + parcela 2)
        boolean aditivaOk = modelo.getTotal() == modelo.getPrimeiraParcela() + modelo.getSegundaParcela();
        // desdobramento 3: os controles da tela (spinners) acompanham o modelo
        final int[] spinners = new int[2];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                spinners[0] = ((Number) ((javax.swing.JSpinner) campo(painel, "spinnerA")).getValue()).intValue();
                spinners[1] = ((Number) ((javax.swing.JSpinner) campo(painel, "spinnerB")).getValue()).intValue();
            } catch (Exception e) { throw new IllegalStateException(e); } }});
        boolean controlesOk = spinners[0] == modelo.getPrimeiraParcela() && spinners[1] == modelo.getSegundaParcela();
        boolean ok = papelOk && aditivaOk && controlesOk;
        if (!ok) falhas++;
        System.out.println((ok ? "[OK]    " : "[FALHA] ") + categoria + " caixa " + indice + " -> " + papel + "=" + valor
                + " (modelo A=" + modelo.getPrimeiraParcela() + " B=" + modelo.getSegundaParcela() + " T=" + modelo.getTotal()
                + "; papelOk=" + papelOk + " aditiva=" + aditivaOk + " controles=" + controlesOk + ")");
        capturar(dialogo, categoria.name() + "_caixa" + indice);
    }

    /** O diálogo de valor (JOptionPane) aberto pelo duplo clique, distinto da própria tela de comparação. */
    static JDialog dialogoDeValor() throws Exception {
        final JDialog[] d = new JDialog[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (Window w : Window.getWindows()) {
                if (w.isShowing() && w instanceof JDialog && temOptionPane(((JDialog) w).getContentPane())) {
                    d[0] = (JDialog) w;
                }
            } }});
        return d[0];
    }

    static void clicarCampoDoDialogo(final JDialog d) throws Exception {
        final Point[] p = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { p[0] = campoTexto(d.getContentPane()); } });
        if (p[0] == null) return;
        r.mouseMove(p[0].x, p[0].y); r.delay(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    static Point campoTexto(Container c) {
        for (Component f : c.getComponents()) {
            if (f instanceof javax.swing.JTextField) {
                Point l = f.getLocationOnScreen();
                return new Point(l.x + f.getWidth() / 2, l.y + f.getHeight() / 2);
            }
            if (f instanceof Container) { Point p = campoTexto((Container) f); if (p != null) return p; }
        }
        return null;
    }

    static boolean temOptionPane(Container c) {
        for (Component f : c.getComponents()) {
            if (f instanceof javax.swing.JOptionPane) return true;
            if (f instanceof Container && temOptionPane((Container) f)) return true;
        }
        return false;
    }

    static void clicarBotaoDoDialogo(final JDialog d, final String texto) throws Exception {
        final Point[] p = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { p[0] = botao(d.getContentPane(), texto); } });
        if (p[0] == null) return;
        r.mouseMove(p[0].x, p[0].y); r.delay(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    static Point botao(Container c, String texto) {
        for (Component f : c.getComponents()) {
            if (f instanceof javax.swing.JButton && texto.equals(((javax.swing.JButton) f).getText())) {
                Point l = f.getLocationOnScreen();
                return new Point(l.x + f.getWidth() / 2, l.y + f.getHeight() / 2);
            }
            if (f instanceof Container) { Point p = botao((Container) f, texto); if (p != null) return p; }
        }
        return null;
    }

    static Object painelDe(Component c) {
        Component p = c;
        while (p != null && !p.getClass().getSimpleName().equals("PainelComparacaoCategorias")) p = p.getParent();
        return p;
    }

    static Object campo(Object o, String nome) throws Exception {
        Class<?> k = o.getClass();
        while (k != null) {
            try { Field f = k.getDeclaredField(nome); f.setAccessible(true); return f.get(o); }
            catch (NoSuchFieldException e) { k = k.getSuperclass(); }
        }
        throw new NoSuchFieldException(nome);
    }

    static int acharMini(Container c, List<Component> saida) {
        for (Component f : c.getComponents()) {
            if (f.getClass().getSimpleName().equals("MiniRepresentacao")) saida.add(f);
            if (f instanceof Container) acharMini((Container) f, saida);
        }
        return saida.size();
    }

    static void capturar(JDialog d, String nome) throws Exception {
        final Rectangle[] rc = new Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { rc[0] = d.getBounds(); } });
        BufferedImage img = r.createScreenCapture(rc[0]);
        ImageIO.write(img, "png", new File(saida, nome + ".png"));
    }
}
