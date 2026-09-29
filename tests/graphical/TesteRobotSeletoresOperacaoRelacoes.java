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
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno;

/**
 * Validacao Robot dirigida da Fase 7.8 (seletores de operacao das Relacoes).
 *
 * Exercita o protocolo apontar-e-clicar real (java.awt.Robot -> AWT ->
 * Main.TelaGerard.mousePressed -> CasoDeUsoSelecaoOperacoesRelacoes) nas
 * tres categorias de Relacoes e verifica o comportamento preservado
 * declarado no relatorio da fase:
 *  1. o segundo seletor (Composicao de Transformacoes) nao recebe clique
 *     antes de a primeira resposta estar correta;
 *  2. erro altera a escolha e nao conta como correta;
 *  3. cada escolha consumida produz exatamente um registro C/E no log real;
 *  4. a escolha correta libera o segundo seletor;
 *  5. nenhuma excecao nao tratada; listeners nao duplicados.
 *
 * A resposta esperada vem do mesmo oraculo de dominio usado pela tela
 * (AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta); as areas dos
 * botoes sao lidas por reflexao somente neste teste, sem getter novo em
 * producao. Pacote padrao pelo mesmo motivo de TesteMonkeySemiGuiado.
 */
public class TesteRobotSeletoresOperacaoRelacoes {

    private static PrintWriter log;
    private static int falhas = 0;
    private static int verificacoes = 0;
    private static final List<Throwable> excecoes = new ArrayList<Throwable>();
    private static File dirEvidencias;

    public static void main(String[] args) throws Exception {
        String carimbo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File dirLogs = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        dirLogs.mkdirs();
        dirEvidencias = new File(args.length > 0 ? args[0] : dirLogs.getPath(),
                "robot_fase_7_8_" + carimbo);
        dirEvidencias.mkdirs();
        log = new PrintWriter(new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(new File(dirEvidencias, "harness.log")), "UTF-8"), true);
        log.println("Validacao Robot dirigida Fase 7.8 — " + new Date());

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable ex) {
                synchronized (excecoes) { excecoes.add(ex); }
                log.println("EXCECAO NAO TRATADA em " + t.getName() + ":");
                ex.printStackTrace(log);
            }
        });

        final Main[] janela = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            janela[0] = new Main();
            janela[0].setVisible(true);
            janela[0].toFront();
        }});
        Thread.sleep(2000);
        Robot robot = new Robot();
        robot.setAutoDelay(15);
        fecharDialogos(robot);

        final Main.TelaGerard tela = encontrarTela(janela[0]);
        if (tela == null) { log.println("TelaGerard nao encontrada"); System.exit(2); }
        verificar("MouseListener unico na TelaGerard", tela.getMouseListeners().length == 1,
                "listeners=" + tela.getMouseListeners().length);

        TipoSituacaoAditiva[] alvo = {
            TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
            TipoSituacaoAditiva.COMPOSICAO_RELACOES,
            TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES };
        for (TipoSituacaoAditiva tipo : alvo) {
            try {
                cenario(robot, tela, tipo);
            } catch (Exception ex) {
                falhas++;
                log.println("FALHA de execucao no cenario " + tipo + ":");
                ex.printStackTrace(log);
            }
        }

        verificar("Nenhuma excecao nao tratada", excecoes.isEmpty(), "excecoes=" + excecoes.size());
        verificar("MouseListener continua unico", tela.getMouseListeners().length == 1,
                "listeners=" + tela.getMouseListeners().length);
        log.println("RESUMO: " + verificacoes + " verificacoes, " + falhas + " falhas");
        log.println("Evidencias: " + dirEvidencias.getAbsolutePath());
        log.close();
        System.out.println("RESUMO: " + verificacoes + " verificacoes, " + falhas + " falhas -> " + dirEvidencias);
        System.exit(falhas == 0 ? 0 : 1);
    }

    private static void cenario(Robot robot, final Main.TelaGerard tela, final TipoSituacaoAditiva tipo)
            throws Exception {
        log.println();
        log.println("=== Cenario " + tipo);
        if (!selecionarCategoria(tela, tipo)) {
            verificar(tipo + ": categoria disponivel no menu", false, "item nao encontrado/habilitado");
            return;
        }
        Thread.sleep(900);
        fecharDialogos(robot);

        final SeletorOperacaoRelacaoAluno primeiro = tela.seletorOperacaoRelacaoAluno;
        final SeletorOperacaoRelacaoAluno segundo = tela.seletorOperacaoEstadoTransformacaoAluno;
        final boolean esperaSegundo = tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES;
        // Em Composicao de Transformacoes procura-se uma situacao com as duas
        // operacoes curadas, para exercitar a liberacao ordenada.
        for (int i = 0; i < 40 && (!ativo(primeiro) || (esperaSegundo && !ativo(segundo))); i++) {
            log.println("  sorteio " + i + ": situacao=" + idSituacao(tela) + " sem operacao curada ativa; nova situacao");
            // "Nova situacao-problema" sorteia entre TODAS as categorias
            // (fluxo de adivinhacao da categoria); para permanecer na
            // categoria do cenario, reescolhe-se a categoria no menu, que
            // inicia nova atividade dentro dela (aplicarIdiomaSelecionado).
            selecionarCategoria(tela, tipo);
            Thread.sleep(500);
            fecharDialogos(robot);
        }
        final String[] enunciado = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            enunciado[0] = tela.situacaoProblemaAtual == null ? "-" : tela.situacaoProblemaAtual.getId();
        }});
        log.println("Situacao: " + enunciado[0]);
        if (!verificar(tipo + ": primeiro seletor ativo", ativo(primeiro), "")) {
            return;
        }
        verificar(tipo + ": segundo seletor " + (esperaSegundo ? "ativo" : "inativo"),
                ativo(segundo) == esperaSegundo, "segundo.ativo=" + ativo(segundo));
        captura(tipo + "_0_inicial");

        OpcaoOperacaoCuradoria certa1 = oraculo(tela, TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        OpcaoOperacaoCuradoria errada1 = oposta(certa1);
        log.println("Oraculo primeiro: " + certa1);

        if (esperaSegundo && ativo(segundo)) {
            OpcaoOperacaoCuradoria certa2 = oraculo(tela, TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO);
            int antes = contarRegistrosOperacao();
            clicar(robot, tela, segundo, certa2);
            verificar(tipo + ": segundo seletor bloqueado antes do primeiro correto",
                    escolha(segundo) == OpcaoOperacaoCuradoria.NAO_SELECIONADO,
                    "escolha2=" + escolha(segundo));
            verificar(tipo + ": clique bloqueado nao gera registro",
                    contarRegistrosOperacao() == antes, "antes=" + antes + " depois=" + contarRegistrosOperacao());
        }

        int r0 = contarRegistrosOperacao();
        clicar(robot, tela, primeiro, errada1);
        verificar(tipo + ": escolha errada registrada no widget", escolha(primeiro) == errada1,
                "escolha=" + escolha(primeiro));
        verificar(tipo + ": escolha errada nao conta como correta", !correto(primeiro), "");
        int r1 = contarRegistrosOperacao();
        verificar(tipo + ": escolha errada gera exatamente 1 registro", r1 == r0 + 1, r0 + "->" + r1);
        verificar(tipo + ": ultimo registro e INCORRETO/E", ultimoRegistroContem("OPERACAO_DIVERGENTE_DA_CURADORIA"), "");
        captura(tipo + "_1_primeiro_errado");

        if (esperaSegundo && ativo(segundo)) {
            OpcaoOperacaoCuradoria certa2 = oraculo(tela, TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO);
            int a = contarRegistrosOperacao();
            clicar(robot, tela, segundo, certa2);
            verificar(tipo + ": segundo continua bloqueado apos primeiro errado",
                    escolha(segundo) == OpcaoOperacaoCuradoria.NAO_SELECIONADO && contarRegistrosOperacao() == a,
                    "escolha2=" + escolha(segundo));
        }

        clicar(robot, tela, primeiro, certa1);
        verificar(tipo + ": escolha correta aceita", correto(primeiro), "escolha=" + escolha(primeiro));
        int r2 = contarRegistrosOperacao();
        verificar(tipo + ": escolha correta gera exatamente 1 registro", r2 == r1 + 1, r1 + "->" + r2);
        captura(tipo + "_2_primeiro_correto");

        if (esperaSegundo && ativo(segundo)) {
            OpcaoOperacaoCuradoria certa2 = oraculo(tela, TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO);
            log.println("Oraculo segundo: " + certa2);
            clicar(robot, tela, segundo, oposta(certa2));
            int r3 = contarRegistrosOperacao();
            verificar(tipo + ": segundo liberado recebe escolha errada", escolha(segundo) == oposta(certa2),
                    "escolha2=" + escolha(segundo));
            verificar(tipo + ": segundo errado gera 1 registro", r3 == r2 + 1, r2 + "->" + r3);
            verificar(tipo + ": primeiro permanece correto", correto(primeiro), "");
            clicar(robot, tela, segundo, certa2);
            int r4 = contarRegistrosOperacao();
            verificar(tipo + ": segundo correto aceito", correto(segundo), "escolha2=" + escolha(segundo));
            verificar(tipo + ": segundo correto gera 1 registro", r4 == r3 + 1, r3 + "->" + r4);
            verificar(tipo + ": registro do segundo usa proprietario estadoTransformacao",
                    ultimoRegistroContem("relacao.operacao.estadoTransformacao"), "");
            captura(tipo + "_3_segundo_correto");
        }
    }

    // ---------- ações Robot ----------

    private static void clicar(Robot robot, final Main.TelaGerard tela, SeletorOperacaoRelacaoAluno s,
            OpcaoOperacaoCuradoria opcao) throws Exception {
        Rectangle area = areaDe(s, opcao);
        if (area == null) { throw new IllegalStateException("area nula para " + opcao); }
        final Point origem = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            Point p = tela.getLocationOnScreen(); origem.setLocation(p);
        }});
        int x = origem.x + area.x + area.width / 2;
        int y = origem.y + area.y + area.height / 2;
        log.println("  clique Robot " + opcao + " em tela(" + x + "," + y + ") area=" + area);
        robot.mouseMove(x, y);
        robot.delay(120);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(40);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.waitForIdle();
        Thread.sleep(450);
        fecharDialogos(robot);
    }

    private static Rectangle areaDe(SeletorOperacaoRelacaoAluno s, OpcaoOperacaoCuradoria opcao) throws Exception {
        Field f = SeletorOperacaoRelacaoAluno.class.getDeclaredField(
                opcao == OpcaoOperacaoCuradoria.SOMA ? "areaSoma" : "areaSubtracao");
        f.setAccessible(true);
        Rectangle r = (Rectangle) f.get(s);
        return r == null ? null : new Rectangle(r);
    }

    private static void captura(String nome) {
        try {
            Robot r = new Robot();
            java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage img = r.createScreenCapture(new Rectangle(d));
            ImageIO.write(img, "png", new File(dirEvidencias, nome + ".png"));
        } catch (Exception ex) { log.println("captura falhou: " + ex); }
    }

    private static void fecharDialogos(Robot robot) throws Exception {
        for (int i = 0; i < 4; i++) {
            Thread.sleep(200);
            final String[] titulo = new String[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                for (Window w : Window.getWindows()) {
                    if (w.isVisible() && w instanceof java.awt.Dialog) {
                        titulo[0] = ((java.awt.Dialog) w).getTitle(); return;
                    }
                }
            }});
            if (titulo[0] == null) return;
            log.println("  dialogo '" + titulo[0] + "' -> ENTER");
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }
    }

    // ---------- leitura de estado (EDT) ----------

    private static boolean ativo(final SeletorOperacaoRelacaoAluno s) throws Exception {
        final boolean[] b = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { b[0] = s.estaAtivo(); }});
        return b[0];
    }

    private static boolean correto(final SeletorOperacaoRelacaoAluno s) throws Exception {
        final boolean[] b = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { b[0] = s.respondeuCorretamente(); }});
        return b[0];
    }

    private static OpcaoOperacaoCuradoria escolha(final SeletorOperacaoRelacaoAluno s) throws Exception {
        final OpcaoOperacaoCuradoria[] o = new OpcaoOperacaoCuradoria[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o[0] = s.obterEscolhaAluno(); }});
        return o[0];
    }

    private static OpcaoOperacaoCuradoria oraculo(final Main.TelaGerard tela, final TipoOperacaoSeletor papel)
            throws Exception {
        final OpcaoOperacaoCuradoria[] o = new OpcaoOperacaoCuradoria[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            o[0] = AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                    tela.tipoSituacaoSelecionada, tela.situacaoProblemaAtual, papel);
        }});
        return o[0];
    }

    private static String idSituacao(final Main.TelaGerard tela) throws Exception {
        final String[] id = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            id[0] = tela.situacaoProblemaAtual == null ? "-" : tela.situacaoProblemaAtual.getId()
                    + "/" + tela.situacaoProblemaAtual.getOperacaoRelacao()
                    + "/" + tela.situacaoProblemaAtual.getOperacaoEstadoTransformacao();
        }});
        return id[0];
    }

    private static OpcaoOperacaoCuradoria oposta(OpcaoOperacaoCuradoria o) {
        return o == OpcaoOperacaoCuradoria.SOMA ? OpcaoOperacaoCuradoria.SUBTRACAO : OpcaoOperacaoCuradoria.SOMA;
    }

    private static boolean selecionarCategoria(final Main.TelaGerard tela, final TipoSituacaoAditiva tipo)
            throws Exception {
        final List<JMenuItem> itens = new ArrayList<JMenuItem>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            if (tela.menuCategoria == null) return;
            for (int g = 0; g < tela.menuCategoria.getItemCount(); g++) {
                JMenuItem grupo = tela.menuCategoria.getItem(g);
                if (!(grupo instanceof JMenu)) continue;
                JMenu sub = (JMenu) grupo;
                for (int i = 0; i < sub.getItemCount(); i++) {
                    JMenuItem it = sub.getItem(i);
                    if (it != null && it.isEnabled()) itens.add(it);
                }
            }
        }});
        for (final JMenuItem it : itens) {
            SwingUtilities.invokeAndWait(new Runnable() { public void run() { it.doClick(); }});
            Thread.sleep(500);
            final boolean[] ok = new boolean[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                ok[0] = tela.categoriaSelecionadaParaAtividade && tela.tipoSituacaoSelecionada == tipo;
            }});
            if (ok[0]) { log.println("Categoria via menu: '" + it.getText() + "'"); return true; }
        }
        return false;
    }

    // ---------- log real ----------

    private static File logSessaoMaisRecente() {
        File dir = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        File[] fs = dir.listFiles();
        File melhor = null;
        if (fs == null) return null;
        for (File f : fs) {
            if (f.getName().startsWith("gerard_interacao_") && f.getName().endsWith(".tsv")
                    && (melhor == null || f.lastModified() > melhor.lastModified())) melhor = f;
        }
        return melhor;
    }

    private static List<String> linhasOperacao() throws Exception {
        List<String> out = new ArrayList<String>();
        File f = logSessaoMaisRecente();
        if (f == null) return out;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
        try {
            String l;
            while ((l = br.readLine()) != null) {
                if (l.contains("relacao.operacao.")) out.add(l);
            }
        } finally { br.close(); }
        return out;
    }

    private static int contarRegistrosOperacao() throws Exception { return linhasOperacao().size(); }

    private static boolean ultimoRegistroContem(String trecho) throws Exception {
        List<String> ls = linhasOperacao();
        return !ls.isEmpty() && ls.get(ls.size() - 1).contains(trecho);
    }

    private static boolean verificar(String nome, boolean ok, String detalhe) {
        verificacoes++;
        if (!ok) falhas++;
        log.println((ok ? "[OK]    " : "[FALHA] ") + nome + (detalhe.isEmpty() ? "" : "  (" + detalhe + ")"));
        return ok;
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
