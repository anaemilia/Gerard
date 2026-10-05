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
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;

/**
 * Macaco da faixa de atalhos de categoria (gerard.ui.categoria): cliques REAIS (Robot) em cada botão e
 * conferência dos desdobramentos.
 *
 *  1. Geometria: os 6 ícones, os 2 sorteios, o separador e o "?" estão em ordem, sem sobreposição, e o grupo
 *     fica centralizado na tela; ao redimensionar a janela, a faixa volta a se centralizar.
 *  2. Para cada uma das 6 categorias: sorteia a situação clicando no botão de sorteio do grupo (até sair uma da
 *     categoria); clicar num ícone ERRADO do mesmo grupo abre o questionamento (Sim/Não), não seleciona a
 *     categoria e a atividade continua aguardando; clicar no ícone CERTO seleciona a categoria da situação e
 *     encerra a adivinhação.
 *
 * Uso: TesteMacacoBarraAtalhosCategoria [pastaSaida]
 */
public class TesteMacacoBarraAtalhosCategoria {
    static Main janela;
    static Main.TelaGerard t;
    static Robot r;
    static File saida;
    static ServicoLocalizacao loc;
    static int falhas;
    static int verificacoes;

    public static void main(String[] a) throws Exception {
        saida = new File(a.length > 0 ? a[0] : "macaco_barra_atalhos");
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

        geometria("inicial");
        capturar("barra_inicial");
        redimensionarEConferir();

        TipoSituacaoAditiva[] medidas = {TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS};
        TipoSituacaoAditiva[] relacoes = {TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                TipoSituacaoAditiva.COMPOSICAO_RELACOES};
        for (int i = 0; i < 3; i++) categoria(medidas[i], medidas[(i + 1) % 3], t.botaoFerramentaSortearMedidas);
        for (int i = 0; i < 3; i++) categoria(relacoes[i], relacoes[(i + 1) % 3], t.botaoFerramentaSortearRelacoes);

        System.out.println("VERIFICAÇÕES de desdobramentos: " + verificacoes + "; RESUMO: falhas=" + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    // ------------------------------------------------------------------------------------------------ 1. geometria
    static void geometria(final String quando) throws Exception {
        final Rectangle[] b = new Rectangle[10];
        final int[] larguraTela = new int[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            JButton[] bs = {t.botaoAtalhoComposicao, t.botaoAtalhoTransformacao, t.botaoAtalhoComparacao, t.botaoFerramentaSortearMedidas};
            for (int i = 0; i < bs.length; i++) b[i] = bs[i].getBounds();
            b[4] = t.separadorAtalhoCategoria.getBounds();
            b[5] = t.botaoFerramentaSortearRelacoes.getBounds();
            b[6] = t.botaoAtalhoComposicaoTransformacoes.getBounds();
            b[7] = t.botaoAtalhoTransformacaoRelacao.getBounds();
            b[8] = t.botaoAtalhoComposicaoRelacoes.getBounds();
            b[9] = t.botaoAtalhoProximoPasso.getBounds();
            larguraTela[0] = t.getWidth();
        }});
        boolean ordem = true, semSobreposicao = true;
        for (int i = 0; i < 9; i++) {
            if (b[i].x + b[i].width > b[i + 1].x + 1 && i != 8) ordem = false;
            if (i < 8 && b[i].intersects(b[i + 1]) && !(b[i].x + b[i].width == b[i + 1].x)) semSobreposicao = false;
        }
        conferir(ordem, "geometria (" + quando + "): ícones, sorteios e separador em ordem da esquerda para a direita");
        conferir(semSobreposicao, "geometria (" + quando + "): nenhum botão se sobrepõe ao vizinho");
        int esquerda = b[0].x, direita = b[8].x + b[8].width;      // do primeiro ícone ao último (sem o "?")
        int centro = (esquerda + direita) / 2;
        int tela = larguraTela[0] > 0 ? larguraTela[0] : 1240;
        conferir(Math.abs(centro - tela / 2) <= 60, "geometria (" + quando + "): o grupo de ícones está centralizado (centro=" + centro + ", tela/2=" + tela / 2 + ")");
    }

    static void redimensionarEConferir() throws Exception {
        final java.awt.Dimension original = new java.awt.Dimension();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { original.setSize(janela.getSize()); } });
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            janela.setSize(Math.max(1100, original.width - 260), original.height); janela.validate(); } });
        Thread.sleep(1200);
        geometria("janela mais estreita");
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { janela.setSize(original); janela.validate(); } });
        Thread.sleep(1200);
        geometria("janela restaurada");
    }

    // ------------------------------------------------------------------------------------------------ 2. cliques por categoria
    static JButton icone(TipoSituacaoAditiva tipo) {
        switch (tipo) {
            case COMPOSICAO_MEDIDAS: return t.botaoAtalhoComposicao;
            case TRANSFORMACAO_MEDIDAS: return t.botaoAtalhoTransformacao;
            case COMPARACAO_MEDIDAS: return t.botaoAtalhoComparacao;
            case COMPOSICAO_TRANSFORMACOES: return t.botaoAtalhoComposicaoTransformacoes;
            case TRANSFORMACAO_RELACAO: return t.botaoAtalhoTransformacaoRelacao;
            default: return t.botaoAtalhoComposicaoRelacoes;
        }
    }

    static void categoria(TipoSituacaoAditiva certa, TipoSituacaoAditiva errada, JButton sortear) throws Exception {
        boolean sorteada = false;
        for (int i = 0; i < 200 && !sorteada; i++) {
            clicar(sortear); Thread.sleep(300); TesteRobotExploracaoAposConclusao.fechar(r);
            sorteada = estado("tipoSorteado", certa);
        }
        conferir(sorteada, certa + ": o clique em Sortear trouxe uma situação da categoria");
        if (!sorteada) return;
        conferir(booleano("aguardandoAdivinhacaoCategoria") && !booleano("categoriaSelecionadaParaAtividade"),
                certa + ": depois de sortear, a atividade aguarda a adivinhação da categoria");

        // clique no ícone ERRADO do mesmo grupo
        clicar(icone(errada));
        Thread.sleep(1000);
        JDialog d = dialogoModal();
        conferir(d != null, certa + ": clicar no ícone errado (" + errada + ") abre o questionamento");
        if (d != null) {
            clicarBotao(d, loc.texto("ui.completion.no"));
            Thread.sleep(800);
        }
        conferir(dialogoModal() == null, certa + ": o questionamento fecha em \"Não\"");
        conferir(booleano("aguardandoAdivinhacaoCategoria") && !booleano("categoriaSelecionadaParaAtividade"),
                certa + ": o ícone errado NÃO seleciona a categoria (continua aguardando)");

        // clique no ícone CERTO
        clicar(icone(certa));
        Thread.sleep(1200);
        TesteRobotExploracaoAposConclusao.fechar(r);
        conferir(booleano("categoriaSelecionadaParaAtividade") && estado("tipoSelecionado", certa),
                certa + ": o ícone certo seleciona a categoria da situação");
        conferir(!booleano("aguardandoAdivinhacaoCategoria"), certa + ": a adivinhação se encerra");
        capturar("categoria_" + certa.name().toLowerCase());
    }

    // ------------------------------------------------------------------------------------------------ infraestrutura
    static boolean estado(final String qual, final TipoSituacaoAditiva tipo) throws Exception {
        final boolean[] ok = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            if (qual.equals("tipoSorteado")) ok[0] = t.situacaoProblemaAtual != null && t.situacaoProblemaAtual.getTipo() == tipo;
            else ok[0] = t.tipoSituacaoSelecionada == tipo;
        }});
        return ok[0];
    }

    static boolean booleano(final String campo) throws Exception {
        final boolean[] v = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try { v[0] = (Boolean) campo(t, campo); } catch (Exception e) { throw new IllegalStateException(e); } }});
        return v[0];
    }

    static void clicar(final Component c) throws Exception {
        final Point[] p = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            Point l = c.getLocationOnScreen(); p[0] = new Point(l.x + c.getWidth() / 2, l.y + c.getHeight() / 2); } });
        r.mouseMove(p[0].x, p[0].y); r.delay(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); r.delay(250);
    }

    static JDialog dialogoModal() throws Exception {
        final JDialog[] d = new JDialog[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (Window w : Window.getWindows()) if (w.isShowing() && w instanceof JDialog && ((JDialog) w).isModal()) d[0] = (JDialog) w; }});
        return d[0];
    }

    static void clicarBotao(final Container d, final String texto) throws Exception {
        final JButton[] b = new JButton[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { b[0] = botao(d, texto); } });
        if (b[0] == null) { falhas++; System.out.println("[FALHA] botão não encontrado: " + texto); return; }
        clicar(b[0]);
    }

    static JButton botao(Container c, String texto) {
        for (Component f : c.getComponents()) {
            if (f instanceof JButton && texto.equals(((JButton) f).getText()) && f.isShowing()) return (JButton) f;
            if (f instanceof Container) { JButton b = botao((Container) f, texto); if (b != null) return b; }
        }
        return null;
    }

    static Object campo(Object o, String nome) throws Exception {
        Class<?> k = o.getClass();
        while (k != null) {
            try { java.lang.reflect.Field f = k.getDeclaredField(nome); f.setAccessible(true); return f.get(o); }
            catch (NoSuchFieldException e) { k = k.getSuperclass(); }
        }
        throw new NoSuchFieldException(nome);
    }

    static void conferir(boolean ok, String msg) {
        verificacoes++;
        if (!ok) falhas++;
        System.out.println((ok ? "[OK]    " : "[FALHA] ") + msg);
    }

    static void capturar(String nome) throws Exception {
        final Rectangle[] rc = new Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            Point p = janela.getRootPane().getLocationOnScreen();
            rc[0] = new Rectangle(p.x, p.y, janela.getRootPane().getWidth(), Math.min(260, janela.getRootPane().getHeight())); }});
        BufferedImage img = r.createScreenCapture(rc[0]);
        ImageIO.write(img, "png", new File(saida, nome + ".png"));
    }
}
