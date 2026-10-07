import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;

/**
 * Fluxo real da pessoa: clica em SORTEAR (sem escolher a categoria ainda — fase de adivinhação) e tenta arrastar as
 * palavras do texto da situação-problema com o mouse real. Registra, antes e depois do arraste, quantos elementos de
 * texto existem, o estado da atividade e se a palavra andou.
 */
public class TesteRobotMoverTextoAposSortear {
    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        try { Class.forName("gerard.ui.GerardTema").getMethod("instalar").invoke(null); } catch (Exception semTema) { }
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        Thread.sleep(2500);
        Robot r = new Robot(); r.setAutoDelay(12);
        TesteRobotExploracaoAposConclusao.fechar(r);
        final Main.TelaGerard t = TesteRobotExploracaoAposConclusao.tela(j[0]);
        for (int rodada = 0; rodada < 2; rodada++) {
            clicar(r, rodada == 0 ? t.botaoFerramentaSortearMedidas : t.botaoFerramentaSortearRelacoes);
            Thread.sleep(900);
            TesteRobotExploracaoAposConclusao.fechar(r);
            final Point o = new Point();
            final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
            final boolean[] estado = new boolean[2];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                o.setLocation(t.getLocationOnScreen()); textos.addAll(t.elementosTexto);
                estado[0] = t.aguardandoAdivinhacaoCategoria; estado[1] = t.categoriaSelecionadaParaAtividade; }});
            System.out.println("== depois de SORTEAR (" + (rodada == 0 ? "Medidas" : "Relações") + "): aguardando adivinhação=" + estado[0]
                    + ", categoria selecionada=" + estado[1] + ", elementos de texto=" + textos.size());
            // Depois de SORTEAR a pessoa adivinha a categoria: clica no ícone CERTO e só então a atividade começa.
            final gerard.campoaditivo.modelo.TipoSituacaoAditiva[] tipoSorteado = new gerard.campoaditivo.modelo.TipoSituacaoAditiva[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() { tipoSorteado[0] = t.situacaoProblemaAtual.getTipo(); } });
            clicar(r, icone(t, tipoSorteado[0]));
            Thread.sleep(1500);
            TesteRobotExploracaoAposConclusao.fechar(r);
            textos.clear();
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                o.setLocation(t.getLocationOnScreen()); textos.addAll(t.elementosTexto);
                estado[0] = t.aguardandoAdivinhacaoCategoria; estado[1] = t.categoriaSelecionadaParaAtividade; }});
            System.out.println("   depois de acertar a categoria (" + tipoSorteado[0] + "): aguardando adivinhação=" + estado[0]
                    + ", categoria selecionada=" + estado[1] + ", elementos de texto=" + textos.size());
            int moveu = 0, tentou = 0;
            for (ElementoTextoMovel x : textos) {
                if (x.possuiVinculoSemantico() || tentou >= 4) continue;
                final int antesX = x.x, antesY = x.y;
                final int px = o.x + x.x + x.largura / 2, py = o.y + x.y - x.altura / 2;
                TesteRobotExploracaoAposConclusao.arrastar(r, new Point(px, py), new Point(px + 40, py + 6));
                Thread.sleep(300);
                tentou++;
                boolean andou = x.x != antesX || x.y != antesY;
                if (andou) moveu++;
                System.out.println((andou ? "  [MOVEU]  " : "  [PARADO] ") + "\"" + x.valor + "\" (" + antesX + "," + antesY + ") -> (" + x.x + "," + x.y + ")");
            }
            System.out.println("   " + moveu + " de " + tentou + " palavras se moveram");
        }
        System.exit(0);
    }

    static javax.swing.JButton icone(Main.TelaGerard t, gerard.campoaditivo.modelo.TipoSituacaoAditiva tipo) {
        switch (tipo) {
            case COMPOSICAO_MEDIDAS: return t.botaoAtalhoComposicao;
            case TRANSFORMACAO_MEDIDAS: return t.botaoAtalhoTransformacao;
            case COMPARACAO_MEDIDAS: return t.botaoAtalhoComparacao;
            case COMPOSICAO_TRANSFORMACOES: return t.botaoAtalhoComposicaoTransformacoes;
            case TRANSFORMACAO_RELACAO: return t.botaoAtalhoTransformacaoRelacao;
            default: return t.botaoAtalhoComposicaoRelacoes;
        }
    }

    static void clicar(Robot r, final Component c) throws Exception {
        final Point p = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            Point l = c.getLocationOnScreen(); p.setLocation(l.x + c.getWidth() / 2, l.y + c.getHeight() / 2); } });
        r.mouseMove(p.x, p.y); r.delay(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); r.delay(250);
    }
}
