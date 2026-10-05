package gerard.ui.dialogos;

import gerard.i18n.ServicoLocalizacao;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.ui.UITemaGerard;
import gerard.ui.ajuda.ExplicacaoNarrativaDaCategoria;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Diálogo da explicação por categoria (3º erro de categoria): intro, aviso de mídia pendente, a história/animação
 * da categoria e a linha com ícone e definição. Quem abre entrega a explicação já escolhida, a linha da categoria
 * e o observador que registra a exibição.
 */
public final class DialogoExplicacaoCategoria {
    private DialogoExplicacaoCategoria() {
    }

    public static void mostrar(Component pai, ServicoLocalizacao localizacao, final TipoSituacaoAditiva categoriaReal,
                ExplicacaoNarrativaDaCategoria explicacaoNarrativa, JComponent linhaDaCategoria,
                final java.util.function.Consumer<String> aoAbrir) {
        final gerard.ui.ajuda.PainelAjudaNarrativaVisualCategoria painelNarrativa = explicacaoNarrativa.getPainel();

        final JDialog dialogo = new JDialog(
                SwingUtilities.getWindowAncestor(pai),
                localizacao.texto("ui.dialog.categoryExplanation.title"),
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialogo.setResizable(false);

        JPanel conteudo = new JPanel(new BorderLayout(0, 16));
        conteudo.setBorder(BorderFactory.createEmptyBorder(18, 20, 14, 20));
        conteudo.setBackground(UITemaGerard.COR_SUPERFICIE);

        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);

        JLabel intro = new JLabel("<html><body style='width: 320px'>"
                + localizacao.texto("ui.dialog.categoryExplanation.intro") + "</body></html>");
        intro.setFont(new Font("Arial", Font.PLAIN, 15));
        intro.setForeground(UITemaGerard.COR_TEXTO);
        intro.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(intro);
        corpo.add(Box.createVerticalStrut(12));

        boolean formatoPendente = explicacaoNarrativa.isFormatoPendente();
        if (formatoPendente) {
            JLabel avisoConstrucao = new JLabel("<html><body style='width: 320px'><i>"
                    + localizacao.texto("ui.dialog.categoryExplanation.midiaPendente") + "</i></body></html>");
            avisoConstrucao.setFont(new Font("Arial", Font.PLAIN, 13));
            avisoConstrucao.setForeground(UITemaGerard.COR_TEXTO);
            avisoConstrucao.setAlignmentX(Component.LEFT_ALIGNMENT);
            corpo.add(avisoConstrucao);
            corpo.add(Box.createVerticalStrut(10));
        }

        if (painelNarrativa != null) {
            corpo.add(painelNarrativa);
            corpo.add(Box.createVerticalStrut(12));
        }

        corpo.add(linhaDaCategoria);

        conteudo.add(corpo, BorderLayout.CENTER);

        final JButton fechar = new JButton(localizacao.texto("ui.dialog.categoryExplanation.close"));
        fechar.setOpaque(true);
        fechar.setBackground(UITemaGerard.COR_SUPERFICIE_SUAVE);
        fechar.setForeground(UITemaGerard.COR_TEXTO);
        fechar.setFocusPainted(false);
        fechar.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA, 1));
        ActionListener fecharDialogo = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose();
            }
        };
        fechar.addActionListener(fecharDialogo);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        botoes.add(fechar);
        conteudo.add(botoes, BorderLayout.SOUTH);

        dialogo.getRootPane().setDefaultButton(fechar);
        dialogo.getRootPane().registerKeyboardAction(
                fecharDialogo,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        dialogo.setContentPane(conteudo);
        dialogo.pack();
        dialogo.setLocationRelativeTo(pai);
        final String formatoExibido = painelNarrativa == null
                ? "linguagem_natural"
                : painelNarrativa.getFormato() == gerard.dominio.campoaditivo.ajuda.FormatoAjudaNarrativaVisual.ANIMACAO
                        ? "historinha_animada"
                        : "historia_em_quadrinhos";
        dialogo.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                    aoAbrir.accept("categoria=" + categoriaReal
                            + "; formato=" + formatoExibido
                            + "; gatilho=terceiro_erro_categoria"
                            + (painelNarrativa == null ? ""
                                    : "; repertorio=" + painelNarrativa.getChaveRepertorio()
                                            + "; quantidade_historinhas="
                                            + painelNarrativa.getQuantidadeHistorias()));
            }
        });
        dialogo.setVisible(true);
    }
}
