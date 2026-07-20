import gerard.campoaditivo.curadoria.sinal.ModoPersistenciaSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.OpcaoSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.PainelValorComSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.PapelSinalCuradoria;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GerarPreviaSinalCuradoriaC165 {
    public static void main(String[] args) throws Exception {
        final String destino = args.length == 0
                ? "PREVIA_CURADORIA_SINAL_OBRIGATORIO_C165.png" : args[0];
        SwingUtilities.invokeAndWait(() -> {
            try {
                JPanel formulario = new JPanel(new GridBagLayout());
                formulario.setBackground(new Color(246, 247, 248));
                formulario.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(213, 218, 224)),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 4, 5, 4);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
                PainelValorComSinalCuradoria transformacao =
                        new PainelValorComSinalCuradoria(
                                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                                PapelSinalCuradoria.TRANSFORMACAO,
                                ModoPersistenciaSinalCuradoria.METADADO_SEPARADO,
                                new JTextField("5"), "", loc);
                PainelValorComSinalCuradoria relativo =
                        new PainelValorComSinalCuradoria(
                                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                                PapelSinalCuradoria.VALOR_RELATIVO,
                                ModoPersistenciaSinalCuradoria.METADADO_SEPARADO,
                                new JTextField("?"), "negativo", loc);
                relativo.getSeletorSinal().setSelectedItem(OpcaoSinalCuradoria.NEGATIVO);

                adicionar(formulario, gbc, 0, "transformacao *", transformacao);
                adicionar(formulario, gbc, 1, "valor_relativo *", relativo);

                JLabel nota = new JLabel("* escolha obrigatória antes de salvar");
                nota.setFont(new Font("Arial", Font.PLAIN, 11));
                nota.setForeground(new Color(82, 97, 107));
                gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
                formulario.add(nota, gbc);

                JFrame frame = new JFrame();
                frame.setUndecorated(true);
                frame.setContentPane(formulario);
                frame.pack();
                frame.setSize(new Dimension(730, 145));
                frame.validate();
                BufferedImage imagem = new BufferedImage(730, 145,
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = imagem.createGraphics();
                frame.getContentPane().printAll(g);
                g.dispose();
                frame.dispose();
                ImageIO.write(imagem, "png", new File(destino));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private static void adicionar(JPanel painel, GridBagConstraints gbc,
            int linha, String rotulo, JPanel componente) {
        JLabel label = new JLabel(rotulo);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = linha; gbc.weightx = 0.0;
        painel.add(label, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        painel.add(componente, gbc);
    }
}
