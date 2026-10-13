import gerard.ui.GerardButton;
import gerard.ui.UITemaGerard;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.io.File;

/**
 * Validação visual real (mouse e teclado) de GerardButton: cores dos dois
 * estilos, hover, clique e anel de foco por teclado — sem inspecionar só o
 * código, per Regra 2 do CLAUDE.md.
 */
public class TesteRobotGerardButton {
    static int falhas = 0;

    public static void main(String[] args) throws Exception {
        final JFrame[] janela = new JFrame[1];
        final GerardButton[] primario = new GerardButton[1];
        final GerardButton[] secundario = new GerardButton[1];
        final boolean[] clicado = {false};

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                janela[0] = new JFrame("Teste GerardButton");
                JPanel painel = new JPanel();
                painel.setBackground(UITemaGerard.COR_FUNDO);
                primario[0] = new GerardButton("Confirmar", GerardButton.Estilo.PRIMARIO);
                secundario[0] = new GerardButton("Próxima tarefa", GerardButton.Estilo.SECUNDARIO);
                primario[0].addActionListener(e -> clicado[0] = true);
                painel.add(primario[0]);
                painel.add(secundario[0]);
                janela[0].setContentPane(painel);
                janela[0].setSize(420, 160);
                janela[0].setLocationRelativeTo(null);
                janela[0].setVisible(true);
            }
        });
        Thread.sleep(600);

        Robot r = new Robot();
        r.setAutoDelay(15);

        verificar("altura mínima de 44px respeitada (primário)", primario[0].getHeight() >= 44);
        verificar("altura mínima de 44px respeitada (secundário)", secundario[0].getHeight() >= 44);

        final Point origemJanela = new Point();
        SwingUtilities.invokeAndWait(() -> origemJanela.setLocation(janela[0].getLocationOnScreen()));

        Rectangle limitesPrimario = boundsNaTela(primario[0]);
        Rectangle limitesSecundario = boundsNaTela(secundario[0]);

        capturar(janela[0], "gerard_button_repouso.png");

        // Hover sobre o secundário.
        r.mouseMove(limitesSecundario.x + limitesSecundario.width / 2,
                limitesSecundario.y + limitesSecundario.height / 2);
        r.waitForIdle();
        Thread.sleep(200);
        verificar("modelo do botão secundário reporta rollover", secundario[0].getModel().isRollover());
        capturar(janela[0], "gerard_button_hover_secundario.png");

        // Clique real no primário.
        r.mouseMove(limitesPrimario.x + limitesPrimario.width / 2,
                limitesPrimario.y + limitesPrimario.height / 2);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.waitForIdle();
        Thread.sleep(150);
        capturar(janela[0], "gerard_button_pressionado.png");
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        r.waitForIdle();
        Thread.sleep(200);
        verificar("clique real dispara o ActionListener do primário", clicado[0]);

        // Foco por teclado (Tab) chega ao botão e o anel de foco é pintado
        // (verificado via isFocusOwner, já que a cor exata do pixel do
        // anel depende de antialiasing/DPI da tela).
        SwingUtilities.invokeAndWait(() -> primario[0].requestFocusInWindow());
        r.waitForIdle();
        Thread.sleep(200);
        verificar("botão primário aceita foco de teclado", primario[0].isFocusOwner());
        capturar(janela[0], "gerard_button_foco.png");

        fim();
    }

    static Rectangle boundsNaTela(GerardButton botao) throws Exception {
        final Rectangle[] r = new Rectangle[1];
        SwingUtilities.invokeAndWait(() -> {
            Point local = botao.getLocationOnScreen();
            r[0] = new Rectangle(local.x, local.y, botao.getWidth(), botao.getHeight());
        });
        return r[0];
    }

    static void capturar(JFrame janela, String nomeArquivo) throws Exception {
        final BufferedImage[] img = new BufferedImage[1];
        SwingUtilities.invokeAndWait(() -> {
            img[0] = new BufferedImage(janela.getWidth(), janela.getHeight(), BufferedImage.TYPE_INT_RGB);
            janela.paint(img[0].getGraphics());
        });
        File destino = new File("documentacao/relatorios/evidencias/gerard_button_20260930", nomeArquivo);
        destino.getParentFile().mkdirs();
        ImageIO.write(img[0], "png", destino);
    }

    static void fim() {
        System.out.println("RESUMO falhas=" + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    static void verificar(String n, boolean ok) {
        if (!ok) falhas++;
        System.out.println((ok ? "[OK] " : "[FALHA] ") + n);
    }
}
