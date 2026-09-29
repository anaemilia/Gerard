import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Validação Robot da Fase 7.10 (controles +/− do material concreto).
 *
 * Gera um traço determinístico do estado observável após cada clique real
 * (Robot -> AWT -> mousePressed) nos controles de adicionar/remover unidades,
 * antes e depois de iniciar a modelagem no Vergnaud. Rodado sobre o build
 * anterior e o posterior à extração, com as mesmas situações curadas, os
 * traços devem ser idênticos (equivalência comportamental A/B).
 *
 * Localiza os controles varrendo a tela com o hit-test real da própria tela
 * (reflexão somente leitura, sem efeito colateral). Pacote padrão, fora do JAR.
 *
 * Uso: java TesteRobotControlesUnidades <dir_saida> <rotulo> [situacoes.properties]
 */
public class TesteRobotControlesUnidades {

    private static PrintWriter traco;
    private static PrintWriter log;
    private static File dir;
    private static final List<Throwable> excecoes = new ArrayList<Throwable>();
    private static Properties alvoSituacoes = new Properties();
    private static final Properties situacoesUsadas = new Properties();
    private static int limiteMax = 12;

    public static void main(String[] args) throws Exception {
        String rotulo = args.length > 1 ? args[1] : "run";
        dir = new File(args.length > 0 ? args[0] : ".", "robot_controles_unidades_" + rotulo + "_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        dir.mkdirs();
        if (args.length > 2 && new File(args[2]).isFile()) {
            FileInputStream in = new FileInputStream(args[2]);
            alvoSituacoes.load(in);
            in.close();
        }
        traco = new PrintWriter(new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(new File(dir, "traco.tsv")), "UTF-8"), true);
        log = new PrintWriter(new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(new File(dir, "harness.log")), "UTF-8"), true);
        traco.println("categoria\tetapa\tpasso\toperacao\tagrupamento\testado");
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable ex) {
                synchronized (excecoes) { excecoes.add(ex); }
                log.println("EXCECAO NAO TRATADA em " + t.getName());
                ex.printStackTrace(log);
            }
        });

        final Main[] janela = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            janela[0] = new Main(); janela[0].setVisible(true); janela[0].toFront();
        }});
        Thread.sleep(2000);
        Robot robot = new Robot();
        robot.setAutoDelay(12);
        fecharDialogos(robot);
        Main.TelaGerard tela = encontrarTela(janela[0]);

        TipoSituacaoAditiva[] categorias = {
            TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
            TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
            TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
            TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES };
        if (args.length > 4) limiteMax = Integer.parseInt(args[4]);
        if (args.length > 3) categorias = new TipoSituacaoAditiva[] { TipoSituacaoAditiva.valueOf(args[3]) };
        for (TipoSituacaoAditiva tipo : categorias) {
            try { cenario(robot, tela, tipo); }
            catch (Exception ex) { log.println("falha cenario " + tipo); ex.printStackTrace(log); }
        }
        java.io.FileOutputStream out = new java.io.FileOutputStream(new File(dir, "situacoes.properties"));
        situacoesUsadas.store(out, "situacoes usadas por categoria");
        out.close();
        log.println("excecoes=" + excecoes.size() + " listeners=" + tela.getMouseListeners().length);
        traco.println("FIM\t-\t-\t-\t-\texcecoes=" + excecoes.size()
                + ";listeners=" + tela.getMouseListeners().length);
        traco.close(); log.close();
        System.out.println("OK -> " + dir);
        System.exit(0);
    }

    private static void cenario(Robot robot, final Main.TelaGerard tela, TipoSituacaoAditiva tipo)
            throws Exception {
        String alvo = alvoSituacoes.getProperty(tipo.name());
        String id = null;
        for (int i = 0; i < 40; i++) {
            if (!selecionarCategoria(tela, tipo)) { log.println("categoria indisponivel " + tipo); return; }
            Thread.sleep(600);
            fecharDialogos(robot);
            id = idSituacao(tela);
            if (alvo == null || alvo.equals(id)) break;
        }
        if (alvo != null && !alvo.equals(id)) { log.println("situacao alvo nao sorteada: " + alvo); return; }
        situacoesUsadas.setProperty(tipo.name(), id);
        log.println("== " + tipo + " situacao=" + id);
        String cat = tipo.name();

        // Etapa 1: antes de qualquer posicionamento no Vergnaud.
        Map<String, Point> controles = localizarControles(tela);
        traco.println(cat + "\tantes\t0\t-\t-\tcontroles=" + controles.keySet() + ";" + estado(tela));
        int passo = 1;
        for (Map.Entry<String, Point> c : controles.entrySet()) {
            clicar(robot, tela, c.getValue());
            traco.println(cat + "\tantes\t" + (passo++) + "\t" + c.getKey() + "\t\t" + estado(tela));
        }
        captura(cat + "_1_antes");

        // Etapa 2: modelagem — arrasta cada número conhecido ao papel curado.
        modelar(robot, tela);
        traco.println(cat + "\tmodelado\t0\t-\t-\t" + estado(tela));
        captura(cat + "_2_modelado");

        // Etapa 3: alterações liberadas: +2, −3, depois + até o limite.
        controles = localizarControles(tela);
        traco.println(cat + "\tdepois\t0\t-\t-\tcontroles=" + controles.keySet());
        passo = 1;
        for (String chave : new ArrayList<String>(controles.keySet())) {
            if (!chave.startsWith("ADICIONAR")) continue;
            String agr = chave.substring("ADICIONAR".length());
            String rem = "REMOVER" + agr;
            String[] seq = {chave, chave, rem, rem, rem};
            for (String op : seq) {
                Point p = pontoControle(tela, controles, op);
                if (p == null) { traco.println(cat + "\tdepois\t" + (passo++) + "\t" + op + "\t\tcontrole_ausente"); continue; }
                clicar(robot, tela, p);
                traco.println(cat + "\tdepois\t" + (passo++) + "\t" + op + "\t\t" + estado(tela));
            }
            for (int k = 0; k < limiteMax; k++) {
                Point p = pontoControle(tela, controles, chave);
                if (p == null) { traco.println(cat + "\tlimite\t" + (passo++) + "\t" + chave + "\t\tcontrole_ausente"); break; }
                clicar(robot, tela, p);
                String e = estado(tela);
                traco.println(cat + "\tlimite\t" + (passo++) + "\t" + chave + "\t\t" + e);
                if (e.contains("limiteQuestionado=true")) break;
            }
        }
        captura(cat + "_3_depois");
    }

    // ---- modelagem real por arraste ----
    private static void modelar(Robot robot, final Main.TelaGerard tela) throws Exception {
        final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
        final List<ElementoVergnaud> elementos = new ArrayList<ElementoVergnaud>();
        final Point[] origem = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            textos.addAll(tela.elementosTexto);
            elementos.addAll(tela.elementosVergnaud);
            origem[0] = tela.getLocationOnScreen();
        }});
        for (ElementoTextoMovel t : textos) {
            if (!t.possuiVinculoSemantico()) continue;
            ElementoVergnaud alvo = null;
            for (ElementoVergnaud e : elementos) {
                if (e.chavePapelSemantico.equals(t.chavePapelSemantico)) { alvo = e; break; }
            }
            if (alvo == null) continue;
            Point de = new Point(origem[0].x + t.x + t.largura / 2, origem[0].y + t.y - t.altura / 2);
            Point para = new Point(origem[0].x + alvo.x + alvo.largura / 2, origem[0].y + alvo.y + alvo.altura / 2);
            log.println("  arrastar " + t.valor + " papel=" + t.chavePapelSemantico);
            robot.mouseMove(de.x, de.y);
            robot.delay(80);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            for (int i = 1; i <= 16; i++) {
                robot.mouseMove(de.x + (para.x - de.x) * i / 16, de.y + (para.y - de.y) * i / 16);
                robot.delay(12);
            }
            robot.delay(80);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.waitForIdle();
            Thread.sleep(900);
            fecharDialogos(robot);
        }
        // O material concreto só aparece por escalada da ajuda (limite de
        // tentativas da incógnita): três valores errados digitados na
        // incógnita posicionada, pelo protocolo real de duplo clique.
        for (int tentativa = 0; tentativa < 5 && !complementarVisivel(tela); tentativa++) {
            final ItemTextoArrastavelRef ref = new ItemTextoArrastavelRef();
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                for (gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel it : tela.itensArrastaveis) {
                    if (it.estaNoDiagrama() && it.editavel) {
                        ref.x = it.x + it.largura / 2; ref.y = it.y + it.altura / 2; ref.ok = true; return;
                    }
                }
            }});
            if (!ref.ok) { log.println("  incognita editavel nao encontrada"); break; }
            robot.mouseMove(origem[0].x + ref.x, origem[0].y + ref.y);
            robot.delay(100);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK); robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(35);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK); robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(400);
            robot.keyPress(KeyEvent.VK_1); robot.keyRelease(KeyEvent.VK_1);
            robot.keyPress(KeyEvent.VK_ENTER); robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(900);
            fecharDialogos(robot);
            log.println("  valor errado digitado na incognita, tentativa " + (tentativa + 1)
                    + " complementar=" + complementarVisivel(tela));
        }
        robot.mouseMove(origem[0].x + 5, origem[0].y + 5);
        Thread.sleep(600);
    }

    private static final class ItemTextoArrastavelRef { int x, y; boolean ok; }

    private static boolean complementarVisivel(final Main.TelaGerard tela) throws Exception {
        final boolean[] b = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                Method m = Main.TelaGerard.class.getDeclaredMethod("deveExibirDiagramaComplementar");
                m.setAccessible(true);
                b[0] = (Boolean) m.invoke(tela);
            } catch (Exception ex) { throw new RuntimeException(ex); }
        }});
        return b[0];
    }

    // ---- localização dos controles pelo hit-test real ----
    private static Map<String, Point> localizarControles(final Main.TelaGerard tela) throws Exception {
        final Map<String, Point> r = new LinkedHashMap<String, Point>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                Method add = Main.TelaGerard.class.getDeclaredMethod(
                        "encontrarRepresentacaoPeloControleAdicionarQuadradinho", int.class, int.class);
                Method rem = Main.TelaGerard.class.getDeclaredMethod(
                        "encontrarRepresentacaoPeloControleRemoverQuadradinho", int.class, int.class);
                add.setAccessible(true); rem.setAccessible(true);
                Map<String, long[]> soma = new LinkedHashMap<String, long[]>();
                int w = tela.getWidth(), h = tela.getHeight();
                for (int y = 0; y < h; y += 4) {
                    for (int x = 0; x < w; x += 4) {
                        acumular(soma, "REMOVER", rem.invoke(tela, x, y), tela, x, y);
                        acumular(soma, "ADICIONAR", add.invoke(tela, x, y), tela, x, y);
                    }
                }
                for (Map.Entry<String, long[]> e : soma.entrySet()) {
                    long[] v = e.getValue();
                    r.put(e.getKey(), new Point((int) (v[0] / v[2]), (int) (v[1] / v[2])));
                }
            } catch (Exception ex) { throw new RuntimeException(ex); }
        }});
        return r;
    }

    /** Reusa o ponto já localizado se o hit-test real ainda o confirma; senão revarre. */
    private static Point pontoControle(final Main.TelaGerard tela, Map<String, Point> cache, final String chave)
            throws Exception {
        final Point p = cache.get(chave);
        if (p != null) {
            final String[] achado = new String[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                try {
                    String op = chave.startsWith("ADICIONAR") ? "Adicionar" : "Remover";
                    Method m = Main.TelaGerard.class.getDeclaredMethod(
                            "encontrarRepresentacaoPeloControle" + op + "Quadradinho", int.class, int.class);
                    m.setAccessible(true);
                    Object rep = m.invoke(tela, p.x, p.y);
                    if (rep != null) {
                        CirculoVenn c = (CirculoVenn) rep.getClass().getMethod("obterAgrupamento").invoke(rep);
                        achado[0] = (chave.startsWith("ADICIONAR") ? "ADICIONAR#" : "REMOVER#")
                                + tela.circulosVenn.indexOf(c);
                    }
                } catch (Exception ex) { throw new RuntimeException(ex); }
            }});
            if (chave.equals(achado[0])) return p;
        }
        Map<String, Point> novo = localizarControles(tela);
        cache.clear(); cache.putAll(novo);
        return novo.get(chave);
    }

    private static void acumular(Map<String, long[]> soma, String op, Object rep,
            Main.TelaGerard tela, int x, int y) throws Exception {
        if (rep == null) return;
        CirculoVenn c = (CirculoVenn) rep.getClass().getMethod("obterAgrupamento").invoke(rep);
        String chave = op + "#" + tela.circulosVenn.indexOf(c);
        long[] v = soma.get(chave);
        if (v == null) { v = new long[3]; soma.put(chave, v); }
        v[0] += x; v[1] += y; v[2]++;
    }

    // ---- estado observável ----
    private static String estado(final Main.TelaGerard tela) throws Exception {
        final StringBuilder sb = new StringBuilder();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                Method contar = Main.TelaGerard.class.getDeclaredMethod("contarQuadradinhosNoCirculo", CirculoVenn.class);
                contar.setAccessible(true);
                sb.append("unidades=");
                for (CirculoVenn c : tela.circulosVenn) sb.append(contar.invoke(tela, c)).append(',');
                sb.append(";valorRef=");
                for (CirculoVenn c : tela.circulosVenn) sb.append(c.valorReferencia).append(',');
                sb.append(";anotacao=").append(tela.mostrarAnotacaoMouseOver);
                if (tela.mostrarAnotacaoMouseOver) sb.append(':').append(tela.textoAnotacaoMouseOver);
                sb.append(";limiteQuestionado=").append(tela.mostrarLimiteQuantidadeQuestionado);
                sb.append(";focoAdd=").append(tela.circulosVenn.indexOf(tela.agrupamentoAdicionarQuadradinhoFocado));
                sb.append(";focoRem=").append(tela.circulosVenn.indexOf(tela.agrupamentoRemoverQuadradinhoFocado));
                sb.append(";cursor=").append(tela.getCursor().getType());
            } catch (Exception ex) { sb.append("ERRO:").append(ex); }
        }});
        sb.append(";logBloqueio=").append(contarLog("Tentar adicionar unidade antes da modelagem"));
        sb.append(";logLimite=").append(contarLog("Informar limite semântico da coleção"));
        return sb.toString();
    }

    private static int contarLog(String trecho) throws Exception {
        File d = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        File[] fs = d.listFiles();
        int n = 0;
        if (fs == null) return 0;
        for (File f : fs) {
            if (!f.isFile() || !f.getName().endsWith(".tsv") || f.lastModified() < INICIO) continue;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            String l;
            while ((l = br.readLine()) != null) if (l.contains(trecho)) n++;
            br.close();
        }
        return n;
    }
    private static final long INICIO = System.currentTimeMillis();

    // ---- utilitários ----
    private static void clicar(Robot robot, final Main.TelaGerard tela, Point local) throws Exception {
        final Point o = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(tela.getLocationOnScreen()); }});
        robot.mouseMove(o.x + local.x, o.y + local.y);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(30);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.waitForIdle();
        Thread.sleep(700);
        fecharDialogos(robot);
    }

    private static String idSituacao(final Main.TelaGerard tela) throws Exception {
        final String[] id = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            id[0] = tela.situacaoProblemaAtual == null ? "-" : tela.situacaoProblemaAtual.getId();
        }});
        return id[0];
    }

    private static boolean selecionarCategoria(final Main.TelaGerard tela, final TipoSituacaoAditiva tipo)
            throws Exception {
        final List<JMenuItem> itens = new ArrayList<JMenuItem>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (int g = 0; g < tela.menuCategoria.getItemCount(); g++) {
                JMenuItem grupo = tela.menuCategoria.getItem(g);
                if (!(grupo instanceof JMenu)) continue;
                for (int i = 0; i < ((JMenu) grupo).getItemCount(); i++) {
                    JMenuItem it = ((JMenu) grupo).getItem(i);
                    if (it != null && it.isEnabled()) itens.add(it);
                }
            }
        }});
        for (final JMenuItem it : itens) {
            SwingUtilities.invokeAndWait(new Runnable() { public void run() { it.doClick(); }});
            Thread.sleep(300);
            final boolean[] ok = new boolean[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                ok[0] = tela.categoriaSelecionadaParaAtividade && tela.tipoSituacaoSelecionada == tipo;
            }});
            if (ok[0]) return true;
        }
        return false;
    }

    private static void fecharDialogos(Robot robot) throws Exception {
        for (int i = 0; i < 4; i++) {
            Thread.sleep(150);
            final boolean[] ha = new boolean[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                for (Window w : Window.getWindows()) if (w.isVisible() && w instanceof java.awt.Dialog) ha[0] = true;
            }});
            if (!ha[0]) return;
            log.println("  dialogo -> ENTER");
            robot.keyPress(KeyEvent.VK_ENTER); robot.keyRelease(KeyEvent.VK_ENTER);
        }
    }

    private static void captura(String nome) {
        try {
            java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage img = new Robot().createScreenCapture(new Rectangle(d));
            ImageIO.write(img, "png", new File(dir, nome + ".png"));
        } catch (Exception ex) { log.println("captura falhou " + ex); }
    }

    private static Main.TelaGerard encontrarTela(java.awt.Container c) {
        for (java.awt.Component comp : c.getComponents()) {
            if (comp instanceof Main.TelaGerard) return (Main.TelaGerard) comp;
            if (comp instanceof java.awt.Container) {
                Main.TelaGerard t = encontrarTela((java.awt.Container) comp);
                if (t != null) return t;
            }
        }
        return null;
    }
}
