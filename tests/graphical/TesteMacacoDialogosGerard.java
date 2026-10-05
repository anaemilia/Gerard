import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;

/**
 * Macaco dos diálogos que saíram da Main (gerard.ui.dialogos): para cada diálogo, interação real por mouse e
 * teclado (Robot) e conferência dos DESDOBRAMENTOS — qual resposta foi entregue a quem chamou, se o diálogo
 * fechou, se a validação apareceu e se nada foi registrado por engano.
 *
 *  1. Sim/Não: clicar "Sim" -> só a ação Sim roda; "Não" -> só a Não; Esc -> nenhuma.
 *  2. Valor inteiro: letras -> aviso e o diálogo continua aberto; dígitos + Confirmar -> devolve o número;
 *     Cancelar -> devolve nulo.
 *  3. Relato de bug: Cancelar -> nada acontece; Enviar sem texto -> aviso de obrigatório e NENHUM relato gravado
 *     (nem e-mail aberto).
 *  4. Explicação da categoria: abre com o texto da categoria e fecha em "Fechar" e em Esc.
 *
 * Uso: TesteMacacoDialogosGerard [pastaSaida]
 */
public class TesteMacacoDialogosGerard {
    static Main janela;
    static Main.TelaGerard t;
    static Robot r;
    static File saida;
    static ServicoLocalizacao loc;
    static int falhas;
    static int verificacoes;

    public static void main(String[] a) throws Exception {
        saida = new File(a.length > 0 ? a[0] : "macaco_dialogos");
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
        loc = (ServicoLocalizacao) campo(t, "localizacao");

        simNao();
        valorInteiro();
        relatoBug();
        explicacaoCategoria();

        System.out.println("VERIFICAÇÕES de desdobramentos: " + verificacoes + "; RESUMO: falhas=" + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    // ------------------------------------------------------------------------------------------------ 1. Sim/Não
    static void simNao() throws Exception {
        for (String escolha : new String[]{"sim", "nao", "esc"}) {
            final AtomicInteger sim = new AtomicInteger(), nao = new AtomicInteger();
            abrirNaEdt("mostrarDialogoConfirmacaoSimNao", new Class<?>[]{String.class, Runnable.class, Runnable.class},
                    "Pergunta de teste?",
                    new Runnable() { public void run() { sim.incrementAndGet(); } },
                    new Runnable() { public void run() { nao.incrementAndGet(); } });
            JDialog d = esperarDialogo(null);
            conferir(d != null, "Sim/Não: o diálogo abriu (" + escolha + ")");
            if (d == null) return;
            if (escolha.equals("sim")) clicarBotao(d, loc.texto("ui.completion.yes"));
            else if (escolha.equals("nao")) clicarBotao(d, loc.texto("ui.completion.no"));
            else tecla(KeyEvent.VK_ESCAPE);
            Thread.sleep(800);
            conferir(esperarDialogo(null) == null, "Sim/Não: o diálogo fechou (" + escolha + ")");
            conferir(sim.get() == (escolha.equals("sim") ? 1 : 0) && nao.get() == (escolha.equals("nao") ? 1 : 0),
                    "Sim/Não (" + escolha + "): só a resposta escolhida rodou (sim=" + sim + ", nao=" + nao + ")");
        }
    }

    // ------------------------------------------------------------------------------------------------ 2. Valor inteiro
    static void valorInteiro() throws Exception {
        // letras: aviso e o diálogo continua; depois dígitos + Confirmar: devolve o número
        final AtomicReference<Object> retorno = new AtomicReference<Object>("(pendente)");
        abrirNaEdt("solicitarNumeroInteiroParaInterrogacao", new Class<?>[]{String.class}, retorno, "5");
        JDialog d = esperarDialogo(null);
        conferir(d != null, "Valor: o diálogo abriu");
        if (d == null) return;
        clicarCampo(d);
        selecionarTudoEDigitar("abc");
        clicarBotao(d, loc.texto("ui.dialog.confirm"));
        Thread.sleep(700);
        JDialog ainda = esperarDialogo(null);
        conferir(ainda != null && contemTexto(ainda, loc.texto("ui.dialog.invalidValue")),
                "Valor: letras mostram o aviso e o diálogo continua aberto");
        conferir("(pendente)".equals(retorno.get()), "Valor: nada foi devolvido enquanto o valor é inválido");
        clicarCampo(ainda);
        selecionarTudoEDigitar("123");
        clicarBotao(ainda, loc.texto("ui.dialog.confirm"));
        Thread.sleep(900);
        conferir(esperarDialogo(null) == null && "123".equals(retorno.get()), "Valor: dígitos + Confirmar devolvem \"123\" (devolveu " + retorno.get() + ")");

        // Cancelar devolve nulo
        final AtomicReference<Object> retorno2 = new AtomicReference<Object>("(pendente)");
        abrirNaEdt("solicitarNumeroInteiroParaInterrogacao", new Class<?>[]{String.class}, retorno2, "9");
        JDialog d2 = esperarDialogo(null);
        conferir(d2 != null, "Valor (cancelar): o diálogo abriu");
        if (d2 == null) return;
        clicarBotao(d2, loc.texto("analise.cancel"));
        Thread.sleep(800);
        conferir(esperarDialogo(null) == null && retorno2.get() == null, "Valor: Cancelar devolve nulo (devolveu " + retorno2.get() + ")");
    }

    // ------------------------------------------------------------------------------------------------ 3. Relato de bug
    static void relatoBug() throws Exception {
        File relatos = pastaDeRelatos();
        int antes = contarArquivos(relatos);
        // Cancelar
        abrirNaEdt("mostrarDialogoRelatoBug", new Class<?>[]{});
        JDialog d = esperarDialogo(null);
        conferir(d != null && contemTexto(d, loc.texto("ui.bug.title")) || d != null, "Bug: o diálogo abriu");
        if (d == null) return;
        clicarBotao(d, loc.texto("analise.cancel"));
        Thread.sleep(800);
        conferir(esperarDialogo(null) == null, "Bug: Cancelar fecha sem aviso nem relato");
        // Enviar sem texto: aviso de obrigatório
        abrirNaEdt("mostrarDialogoRelatoBug", new Class<?>[]{});
        JDialog d2 = esperarDialogo(null);
        conferir(d2 != null, "Bug (sem texto): o diálogo abriu");
        if (d2 == null) return;
        clicarBotao(d2, loc.texto("ui.bug.submit"));
        Thread.sleep(900);
        JDialog aviso = esperarDialogo(null);
        conferir(aviso != null && contemTexto(aviso, loc.texto("ui.bug.required")),
                "Bug: Enviar sem texto mostra o aviso de descrição obrigatória");
        if (aviso != null) { tecla(KeyEvent.VK_ENTER); Thread.sleep(700); }
        conferir(esperarDialogo(null) == null, "Bug: o aviso fecha e nada mais abre (nenhum e-mail)");
        conferir(contarArquivos(relatos) == antes, "Bug: nenhum relato foi gravado sem descrição");
    }

    // ------------------------------------------------------------------------------------------------ 4. Explicação da categoria
    static void explicacaoCategoria() throws Exception {
        for (String fechar : new String[]{"botao", "esc"}) {
            abrirNaEdt("mostrarExplicacaoCategorias", new Class<?>[]{TipoSituacaoAditiva.class}, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
            JDialog d = esperarDialogo(null);
            conferir(d != null, "Explicação: o diálogo abriu (" + fechar + ")");
            if (d == null) return;
            conferir(contemTexto(d, loc.texto("ui.dialog.categoryExplanation.intro")), "Explicação: mostra o texto de introdução");
            conferir(contemTexto(d, loc.texto("ui.question.category.transformacao_medidas")), "Explicação: mostra a definição da categoria");
            capturar(d, "explicacao_" + fechar);
            if (fechar.equals("botao")) clicarBotao(d, loc.texto("ui.dialog.categoryExplanation.close"));
            else tecla(KeyEvent.VK_ESCAPE);
            Thread.sleep(800);
            conferir(esperarDialogo(null) == null, "Explicação: fecha (" + fechar + ")");
        }
    }

    // ------------------------------------------------------------------------------------------------ infraestrutura
    static void abrirNaEdt(final String metodo, final Class<?>[] tipos, final Object... args) throws Exception {
        final Method m = Main.TelaGerard.class.getDeclaredMethod(metodo, tipos);
        m.setAccessible(true);
        // argumento especial: AtomicReference recebe o retorno do método (que bloqueia a EDT enquanto o diálogo está aberto)
        final Object[] reais;
        final AtomicReference<Object> destino;
        if (args.length > 0 && args[0] instanceof AtomicReference) {
            @SuppressWarnings("unchecked") AtomicReference<Object> ar = (AtomicReference<Object>) args[0];
            destino = ar;
            reais = java.util.Arrays.copyOfRange(args, 1, args.length);
        } else { destino = null; reais = args; }
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            try {
                Object ret = m.invoke(t, reais);
                if (destino != null) destino.set(ret);
            } catch (Exception e) { throw new IllegalStateException(e); } }});
        Thread.sleep(1200);
    }

    static JDialog esperarDialogo(String ignorar) throws Exception {
        for (int i = 0; i < 12; i++) {
            final JDialog[] d = new JDialog[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                for (Window w : Window.getWindows()) {
                    if (w.isShowing() && w instanceof JDialog && ((JDialog) w).isModal()) d[0] = (JDialog) w;
                } }});
            if (d[0] != null) return d[0];
            Thread.sleep(150);
        }
        return null;
    }

    static boolean contemTexto(final Container c, final String trecho) throws Exception {
        final boolean[] ok = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { ok[0] = procura(c, trecho); } });
        return ok[0];
    }

    static boolean procura(Container c, String trecho) {
        for (Component f : c.getComponents()) {
            String s = null;
            if (f instanceof JLabel) s = ((JLabel) f).getText();
            else if (f instanceof JButton) s = ((JButton) f).getText();
            if (s != null && s.contains(trecho.length() > 25 ? trecho.substring(0, 25) : trecho)) return true;
            if (f instanceof Container && procura((Container) f, trecho)) return true;
        }
        return false;
    }

    static void clicarBotao(final Container d, final String texto) throws Exception {
        final Point[] p = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { p[0] = botao(d, texto); } });
        if (p[0] == null) { falhas++; System.out.println("[FALHA] botão não encontrado: " + texto); return; }
        r.mouseMove(p[0].x, p[0].y); r.delay(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    static Point botao(Container c, String texto) {
        for (Component f : c.getComponents()) {
            if (f instanceof JButton && texto.equals(((JButton) f).getText()) && f.isShowing()) {
                Point l = f.getLocationOnScreen();
                return new Point(l.x + f.getWidth() / 2, l.y + f.getHeight() / 2);
            }
            if (f instanceof Container) { Point p = botao((Container) f, texto); if (p != null) return p; }
        }
        return null;
    }

    static void clicarCampo(final Container d) throws Exception {
        final Point[] p = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { p[0] = campoTexto(d); } });
        if (p[0] == null) return;
        r.mouseMove(p[0].x, p[0].y); r.delay(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(200);
    }

    static Point campoTexto(Container c) {
        for (Component f : c.getComponents()) {
            if ((f instanceof JTextField || f instanceof JTextArea) && f.isShowing()) {
                Point l = f.getLocationOnScreen();
                return new Point(l.x + f.getWidth() / 2, l.y + f.getHeight() / 2);
            }
            if (f instanceof Container) { Point p = campoTexto((Container) f); if (p != null) return p; }
        }
        return null;
    }

    static void selecionarTudoEDigitar(String s) throws Exception {
        r.keyPress(KeyEvent.VK_CONTROL); r.keyPress(KeyEvent.VK_A); r.keyRelease(KeyEvent.VK_A); r.keyRelease(KeyEvent.VK_CONTROL);
        for (char ch : s.toCharArray()) {
            int k = Character.isDigit(ch) ? KeyEvent.VK_0 + (ch - '0') : KeyEvent.VK_A + (Character.toLowerCase(ch) - 'a');
            r.keyPress(k); r.keyRelease(k);
        }
        Thread.sleep(200);
    }

    static void tecla(int k) { r.keyPress(k); r.keyRelease(k); }

    static Object campo(Object o, String nome) throws Exception {
        Class<?> k = o.getClass();
        while (k != null) {
            try { java.lang.reflect.Field f = k.getDeclaredField(nome); f.setAccessible(true); return f.get(o); }
            catch (NoSuchFieldException e) { k = k.getSuperclass(); }
        }
        throw new NoSuchFieldException(nome);
    }

    static File pastaDeRelatos() {
        return new File(System.getProperty("user.home"), "Gerard");
    }

    static int contarArquivos(File f) {
        if (!f.exists()) return 0;
        int n = 0;
        File[] fs = f.listFiles();
        if (fs == null) return 0;
        for (File x : fs) n += x.isDirectory() ? contarArquivos(x) : (x.getName().toLowerCase().contains("bug") || x.getName().toLowerCase().contains("relato") ? 1 : 0);
        return n;
    }

    static void conferir(boolean ok, String msg) {
        verificacoes++;
        if (!ok) falhas++;
        System.out.println((ok ? "[OK]    " : "[FALHA] ") + msg);
    }

    static void capturar(JDialog d, String nome) throws Exception {
        final java.awt.Rectangle[] rc = new java.awt.Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { rc[0] = d.getBounds(); } });
        BufferedImage img = r.createScreenCapture(rc[0]);
        ImageIO.write(img, "png", new File(saida, nome + ".png"));
    }
}
