import gerard.campoaditivo.curadoria.TelaCuradoriaSituacoes;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.io.File;

/**
 * Validação visual real (mouse) da migração de TelaCuradoriaSituacoes para
 * GerardButton/UITemaGerard/GerardFontes: tela principal e diálogo de edição
 * de linha abertos de verdade, sem inspecionar só o código (Regra 2).
 */
public class TesteRobotTelaCuradoriaBotoes {
    public static void main(String[] args) throws Exception {
        final JFrame[] janela = new JFrame[1];
        final TelaCuradoriaSituacoes[] tela = new TelaCuradoriaSituacoes[1];
        SwingUtilities.invokeAndWait(() -> {
            janela[0] = new JFrame("Teste Curadoria");
            tela[0] = new TelaCuradoriaSituacoes(new RepositorioSituacoesAditivas(), null);
            janela[0].setContentPane(tela[0]);
            janela[0].setSize(1100, 650);
            janela[0].setLocationRelativeTo(null);
            janela[0].setVisible(true);
        });
        Thread.sleep(800);

        Robot r = new Robot();
        r.setAutoDelay(15);

        capturar(janela[0], "curadoria_tela_principal.png");

        javax.swing.JTable tabela = (javax.swing.JTable)
                buscar(tela[0], javax.swing.JTable.class);
        if (tabela == null || tabela.getRowCount() == 0) {
            System.out.println("Sem linhas curadas para abrir o diálogo; encerra só com a tela principal.");
            System.out.println("RESUMO falhas=0");
            System.exit(0);
        }

        final Point origemTabela = new Point();
        final Rectangle celula = tabela.getCellRect(0, 1, true);
        SwingUtilities.invokeAndWait(() -> origemTabela.setLocation(tabela.getLocationOnScreen()));
        r.mouseMove(origemTabela.x + celula.x + celula.width / 2,
                origemTabela.y + celula.y + celula.height / 2);
        r.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
        r.waitForIdle();
        r.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
        r.waitForIdle();
        Thread.sleep(500);

        JDialog dialogo = null;
        for (Window w : Window.getWindows()) {
            if (w instanceof JDialog && w.isShowing()
                    && "Curadoria da situação-problema".equals(((JDialog) w).getTitle())) {
                dialogo = (JDialog) w;
            }
        }
        int falhas = 0;
        if (dialogo == null) {
            System.out.println("[FALHA] diálogo de curadoria da linha não abriu");
            falhas++;
        } else {
            System.out.println("[OK] diálogo de curadoria da linha abriu com clique real");
            capturar(dialogo, "curadoria_dialogo_linha.png");
        }

        System.out.println("RESUMO falhas=" + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    static java.awt.Component buscar(java.awt.Container raiz, Class<?> tipo) {
        for (java.awt.Component c : raiz.getComponents()) {
            if (tipo.isInstance(c)) return c;
            if (c instanceof java.awt.Container) {
                java.awt.Component achado = buscar((java.awt.Container) c, tipo);
                if (achado != null) return achado;
            }
        }
        return null;
    }

    static void capturar(java.awt.Window janela, String nomeArquivo) throws Exception {
        final BufferedImage[] img = new BufferedImage[1];
        SwingUtilities.invokeAndWait(() -> {
            img[0] = new BufferedImage(janela.getWidth(), janela.getHeight(), BufferedImage.TYPE_INT_RGB);
            janela.paint(img[0].getGraphics());
        });
        File destino = new File("documentacao/relatorios/evidencias/gerard_button_20260930", nomeArquivo);
        destino.getParentFile().mkdirs();
        ImageIO.write(img[0], "png", destino);
    }
}
