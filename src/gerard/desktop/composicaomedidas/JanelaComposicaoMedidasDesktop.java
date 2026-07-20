package gerard.desktop.composicaomedidas;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Janela autônoma da tela de composição de medidas (visão do usuário
 * comum). Pode ser aberta diretamente para desenvolvimento/teste desta
 * tela isoladamente, sem depender do restante da aplicação Main.java.
 */
public class JanelaComposicaoMedidasDesktop extends JFrame {

    public JanelaComposicaoMedidasDesktop() {
        super("Gérard — Composição de medidas");
        getContentPane().setBackground(TemaComposicaoMedidas.FUNDO_GERAL);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        PainelComposicaoMedidasDesktop painel = new PainelComposicaoMedidasDesktop();
        add(painel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JanelaComposicaoMedidasDesktop().setVisible(true);
            }
        });
    }
}
