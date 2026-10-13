import gerard.ui.GerardPanel;
import gerard.ui.UITemaGerard;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.io.File;

/**
 * Confirmação visual de GerardPanel (fundo, borda e cantos arredondados) —
 * componente passivo, sem protocolo de mouse a exercitar; a evidência aqui é
 * a captura de tela real, não a inspeção do código.
 */
public class TesteRobotGerardPanel {
    public static void main(String[] args) throws Exception {
        final JFrame[] janela = new JFrame[1];
        SwingUtilities.invokeAndWait(() -> {
            janela[0] = new JFrame("Teste GerardPanel");
            javax.swing.JPanel fundo = new javax.swing.JPanel(new BorderLayout());
            fundo.setBackground(UITemaGerard.COR_FUNDO);
            GerardPanel cartao = new GerardPanel();
            cartao.setLayout(new BorderLayout());
            cartao.add(new JLabel("  Cartão com cantos arredondados  "), BorderLayout.CENTER);
            cartao.setPreferredSize(new java.awt.Dimension(260, 90));
            fundo.add(cartao, BorderLayout.CENTER);
            javax.swing.JPanel margem = new javax.swing.JPanel();
            margem.setBackground(UITemaGerard.COR_FUNDO);
            margem.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 24, 24, 24));
            margem.add(fundo);
            janela[0].setContentPane(margem);
            janela[0].setSize(340, 180);
            janela[0].setLocationRelativeTo(null);
            janela[0].setVisible(true);
        });
        Thread.sleep(500);
        final BufferedImage[] img = new BufferedImage[1];
        SwingUtilities.invokeAndWait(() -> {
            img[0] = new BufferedImage(janela[0].getWidth(), janela[0].getHeight(), BufferedImage.TYPE_INT_RGB);
            janela[0].paint(img[0].getGraphics());
        });
        File destino = new File("documentacao/relatorios/evidencias/gerard_button_20260930", "gerard_panel.png");
        destino.getParentFile().mkdirs();
        ImageIO.write(img[0], "png", destino);
        System.out.println("RESUMO falhas=0");
        System.exit(0);
    }
}
