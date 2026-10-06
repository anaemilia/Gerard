import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * O botão "T" (abrir o editor de enunciado) foi retirado do desktop. Conclui uma modelagem por mouse real, que é
 * quando ele aparecia, e confere: o campo do botão é nulo e nenhum botão visível da coluna de ações do enunciado
 * tem o tooltip do editor; guarda uma captura da coluna.
 *
 * Uso: TesteRobotSemBotaoEditarEnunciado [pastaSaida]
 */
public class TesteRobotSemBotaoEditarEnunciado {
    public static void main(String[] args) throws Exception {
        File saida = new File(args.length > 0 ? args[0] : "sem_botao_t");
        saida.mkdirs();
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        gerard.ui.GerardTema.instalar();
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        Thread.sleep(2500);
        Robot r = new Robot(); r.setAutoDelay(12);
        TesteRobotExploracaoAposConclusao.fechar(r);
        final Main.TelaGerard t = TesteRobotExploracaoAposConclusao.tela(j[0]);
        TesteRobotExploracaoAposConclusao.selecionar(t, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
        Thread.sleep(800);
        TesteRobotExploracaoAposConclusao.fechar(r);
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
            TesteRobotExploracaoAposConclusao.arrastar(r, new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                    new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
        }
        TesteRobotExploracaoAposConclusao.digitar(r, t, o, Integer.toString(soma));
        Thread.sleep(4000);
        TesteRobotExploracaoAposConclusao.fechar(r);

        final boolean concluida = TesteRobotExploracaoAposConclusao.atingida(t);
        final boolean[] campoNulo = new boolean[1];
        final List<String> botoesDaColuna = new ArrayList<String>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            campoNulo[0] = t.botaoEditarNarrativa == null;
            for (Component c : t.getComponents()) {
                if (c instanceof JButton && c.isVisible() && c.getX() < 60 && c.getY() > 150) {
                    botoesDaColuna.add(((JButton) c).getToolTipText() + " @" + c.getX() + "," + c.getY());
                }
            } }});
        boolean semTooltipDoEditor = true;
        for (String b : botoesDaColuna) {
            if (b.toLowerCase().contains("editor") || b.toLowerCase().contains("enunciado")) semTooltipDoEditor = false;
        }
        final Rectangle[] coluna = new Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            Point p = t.getLocationOnScreen();
            coluna[0] = new Rectangle(p.x, p.y + 150, 120, 320); }});
        BufferedImage img = r.createScreenCapture(coluna[0]);
        ImageIO.write(img, "png", new File(saida, "coluna_de_botoes_apos_conclusao.png"));
        System.out.println("modelagem concluída por mouse real: " + concluida);
        System.out.println("botaoEditarNarrativa é nulo: " + campoNulo[0]);
        System.out.println("botões visíveis da coluna: " + botoesDaColuna);
        boolean ok = concluida && campoNulo[0] && semTooltipDoEditor;
        System.out.println(ok ? "APROVADO: o botão T não existe depois da conclusão." : "REPROVADO");
        System.exit(ok ? 0 : 1);
    }
}
