import java.awt.Point;
import java.awt.Robot;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * O texto da situação-problema é manipulável dentro da área do enunciado: arrasta com o mouse REAL cada tipo de
 * elemento do enunciado (palavra comum e número) por um deslocamento pequeno, dentro da área, e confere se ele
 * andou (posição antes/depois) e se continuou dentro da área do texto.
 *
 * Uso: TesteRobotMoverTextoEnunciado [pastaSaida]
 */
public class TesteRobotMoverTextoEnunciado {
    public static void main(String[] args) throws Exception {
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        try { Class.forName("gerard.ui.GerardTema").getMethod("instalar").invoke(null); } catch (Exception semTema) { }   // commits antigos não têm o tema
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        Thread.sleep(2500);
        Robot r = new Robot(); r.setAutoDelay(12);
        TesteRobotExploracaoAposConclusao.fechar(r);
        final Main.TelaGerard t = TesteRobotExploracaoAposConclusao.tela(j[0]);
        int moveuTotal = 0, tentouTotal = 0;
        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            TesteRobotExploracaoAposConclusao.selecionar(t, tipo);
            Thread.sleep(800);
            TesteRobotExploracaoAposConclusao.fechar(r);
            final Point o = new Point();
            final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                o.setLocation(t.getLocationOnScreen()); textos.addAll(t.elementosTexto); }});
            int moveu = 0, tentou = 0;
            StringBuilder parados = new StringBuilder();
            for (ElementoTextoMovel x : textos) {
                if (x.possuiVinculoSemantico() || tentou >= 5) continue;
                final int antesX = x.x, antesY = x.y;
                final int px = o.x + x.x + x.largura / 2, py = o.y + x.y - x.altura / 2;
                TesteRobotExploracaoAposConclusao.arrastar(r, new Point(px, py), new Point(px + 40, py + 6));
                Thread.sleep(300);
                tentou++;
                if (x.x != antesX || x.y != antesY) moveu++; else parados.append('"').append(x.valor).append("\" ");
            }
            moveuTotal += moveu; tentouTotal += tentou;
            System.out.println(tipo + ": " + moveu + " de " + tentou + " palavras se moveram" + (parados.length() > 0 ? "; paradas: " + parados : ""));
        }
        int moveu = moveuTotal, tentou = tentouTotal;
        System.out.println("RESUMO: " + moveu + " de " + tentou + " elementos de texto se moveram com o mouse real");
        System.exit(moveu > 0 ? 0 : 1);
    }
}
