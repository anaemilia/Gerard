import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Robot da Fase 7.12: pressionamento da barra de Comparação de Medidas por
 * mouse real. Antes do primeiro posicionamento no Vergnaud, pressionar o
 * ponto de controle mostra o aviso de bloqueio, grava um único registro
 * granular do artefato "Controle do gráfico de barras" e não inicia o gesto.
 * Depois do posicionamento, pressionar e arrastar inicia o gesto, muda a
 * proporção do controle e não grava novo bloqueio.
 */
public class TesteRobotControleComparacao {
    static int falhas = 0;
    static final String ARTEFATO = "Controle do gráfico de barras";

    public static void main(String[] args) throws Exception {
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setVisible(true); j[0].toFront(); }});
        Thread.sleep(2000);
        Robot r = new Robot(); r.setAutoDelay(12);
        TesteRobotExploracaoAposConclusao.fechar(r);
        final Main.TelaGerard t = TesteRobotExploracaoAposConclusao.tela(j[0]);
        String exigida = args.length > 0 ? args[0] : null;
        for (int tentativa = 0; tentativa < 40; tentativa++) {
            TesteRobotExploracaoAposConclusao.selecionar(t, TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
            Thread.sleep(500); TesteRobotExploracaoAposConclusao.fechar(r);
            if (exigida == null || exigida.equals(situacaoId(t))) break;
        }
        System.out.println("situacao=" + situacaoId(t));
        if (exigida != null && !exigida.equals(situacaoId(t))) {
            System.out.println("situação exigida não sorteada; execução descartada");
            System.exit(2);
        }
        final Point o = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); }});

        Rectangle controle = retanguloControle(t);
        verificar("barra de Comparação com ponto de controle visível (" + controle + ")",
                controle.width > 0 && controle.height > 0);
        if (controle.width <= 0) { fim(); return; }

        // 1) Antes do primeiro posicionamento: bloqueio.
        int bloqueiosAntes = contar(ARTEFATO);
        double proporcaoAntes = proporcao(t);
        clicar(r, o.x + controle.x + controle.width / 2, o.y + controle.y + controle.height / 2);
        r.waitForIdle(); Thread.sleep(500);
        verificar("bloqueio grava exatamente um registro granular (" + bloqueiosAntes + "->" + contar(ARTEFATO) + ")",
                contar(ARTEFATO) == bloqueiosAntes + 1);
        verificar("bloqueio mostra o aviso na tela", anotacao(t));
        verificar("bloqueio não inicia o gesto", !ativo(t));
        verificar("bloqueio não altera a proporção do controle", proporcao(t) == proporcaoAntes);

        // 2) Primeiro posicionamento semântico por arraste real.
        posicionarUmPapel(r, t, o);
        Thread.sleep(800); TesteRobotExploracaoAposConclusao.fechar(r);
        controle = retanguloControle(t);
        int bloqueiosDepois = contar(ARTEFATO);
        double proporcaoAntesArraste = proporcao(t);
        int cx = o.x + controle.x + controle.width / 2;
        int cy = o.y + controle.y + controle.height / 2;
        r.mouseMove(cx, cy); r.delay(120);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.waitForIdle(); r.delay(150); r.waitForIdle();
        boolean ativoDurante = ativo(t);
        // Arrasta em direção ao meio da escala: para baixo se o controle está
        // na metade superior, para cima se está na inferior (evita o limite).
        int sentido = proporcaoAntesArraste >= 0.5 ? 1 : -1;
        for (int i = 1; i <= 10; i++) { r.mouseMove(cx, cy + sentido * i * 6); r.delay(25); }
        r.delay(150);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(700); TesteRobotExploracaoAposConclusao.fechar(r);
        verificar("após o posicionamento, pressionar inicia o gesto", ativoDurante);
        verificar("arrastar muda a proporção do controle (" + proporcaoAntesArraste + "->" + proporcao(t) + ")",
                proporcao(t) != proporcaoAntesArraste);
        verificar("soltar encerra o gesto", !ativo(t));
        verificar("sem novo registro de bloqueio após a liberação (" + bloqueiosDepois + "->" + contar(ARTEFATO) + ")",
                contar(ARTEFATO) == bloqueiosDepois);
        fim();
    }

    static void fim() {
        System.out.println("RESUMO falhas=" + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    static void posicionarUmPapel(Robot r, final Main.TelaGerard t, Point o) throws Exception {
        final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
        final List<ElementoVergnaud> els = new ArrayList<ElementoVergnaud>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            textos.addAll(t.elementosTexto); els.addAll(t.elementosVergnaud); }});
        for (ElementoTextoMovel tx : textos) {
            if (!tx.possuiVinculoSemantico() || "?".equals(tx.valor)) continue;
            ElementoVergnaud alvo = null;
            for (ElementoVergnaud e : els) if (e.chavePapelSemantico.equals(tx.chavePapelSemantico)) alvo = e;
            if (alvo == null) continue;
            System.out.println("posicionando " + tx.chavePapelSemantico);
            TesteRobotExploracaoAposConclusao.arrastar(r,
                    new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                    new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
            return;
        }
        System.out.println("nenhum papel conhecido encontrado para posicionar");
    }

    static String situacaoId(final Main.TelaGerard t) throws Exception {
        final String[] v = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            v[0] = t.situacaoProblemaAtual == null ? null : t.situacaoProblemaAtual.getId(); }});
        return v[0];
    }

    static void clicar(Robot r, int x, int y) {
        r.mouseMove(x, y); r.delay(120);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.delay(60);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    static Rectangle retanguloControle(final Main.TelaGerard t) throws Exception {
        final Rectangle[] ret = new Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                Method m = Main.TelaGerard.class.getDeclaredMethod("obterRetanguloPontoControleComparacao");
                m.setAccessible(true);
                ret[0] = (Rectangle) m.invoke(t);
            } catch (Exception e) { throw new RuntimeException(e); }
        }});
        return ret[0];
    }

    static double proporcao(final Main.TelaGerard t) throws Exception {
        final double[] v = new double[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { v[0] = t.proporcaoControleComparacao; }});
        return v[0];
    }

    static boolean anotacao(final Main.TelaGerard t) throws Exception {
        final boolean[] v = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { v[0] = t.mostrarAnotacaoMouseOver; }});
        return v[0];
    }

    static boolean ativo(final Main.TelaGerard t) throws Exception {
        final boolean[] v = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { v[0] = t.handlerControleComparacao.estaAtivo(); }});
        return v[0];
    }

    static int contar(String trecho) throws Exception {
        File d = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        int n = 0; File[] fs = d.listFiles(); if (fs == null) return 0;
        for (File f : fs) {
            if (!f.getName().startsWith("gerard_interacao_")) continue;
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            String l; while ((l = br.readLine()) != null) if (l.contains(trecho)) n++;
            br.close();
        }
        return n;
    }

    static void verificar(String n, boolean ok) { if (!ok) falhas++; System.out.println((ok ? "[OK] " : "[FALHA] ") + n); }
}
