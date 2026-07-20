package gerard.ui.conclusao;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Container;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class RenderPreviaConclusaoProgressiva {
    public static void main(String[] args) throws Exception {
        final JPanel painel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(37, 99, 235));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(170, 115, 42, 42);
                g2.drawOval(300, 80, 54, 54);
                g2.drawRect(445, 115, 42, 42);
                g2.drawLine(212, 136, 445, 136);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("Estado inicial", 145, 180);
                g2.drawString("Transformação", 285, 158);
                g2.drawString("Estado final", 430, 180);
                g2.dispose();
            }
        };
        painel.setBackground(Color.WHITE);
        painel.setSize(660, 340);
        final Rectangle areaDiagrama = new Rectangle(140, 75, 350, 110);
        final Rectangle areaPermitida = new Rectangle(20, 40, 620, 285);
        final SeloConclusaoModelagem selo = new SeloConclusaoModelagem();
        final TipConclusaoModelagem tip = new TipConclusaoModelagem();
        selo.atualizarTexto("Modelagem concluída");
        tip.atualizarTextos("Podemos passar para a próxima tarefa?", "Sim", "Não");
        painel.add(selo);
        painel.add(tip);

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                selo.mostrarAbaixoDoDiagrama(areaDiagrama, areaPermitida,
                        painel.getWidth(), painel.getHeight());
                layoutRecursivo(selo);
            }
        });
        Thread.sleep(280);
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                salvar(painel, "/mnt/data/PREVIA_CONCLUSAO_C155_SELO.png");
                selo.ocultar();
                tip.mostrarAbaixoDoDiagrama(areaDiagrama, areaPermitida,
                        painel.getWidth(), painel.getHeight());
                layoutRecursivo(tip);
                salvar(painel, "/mnt/data/PREVIA_CONCLUSAO_C155_TIP.png");
            }
        });
    }

    private static void layoutRecursivo(Container container) {
        container.doLayout();
        for (java.awt.Component componente : container.getComponents()) {
            if (componente instanceof Container) {
                layoutRecursivo((Container) componente);
            }
        }
    }

    private static void salvar(JPanel painel, String caminho) {
        try {
            BufferedImage imagem = new BufferedImage(painel.getWidth(),
                    painel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = imagem.createGraphics();
            painel.paint(g2);
            g2.dispose();
            ImageIO.write(imagem, "png", new File(caminho));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
