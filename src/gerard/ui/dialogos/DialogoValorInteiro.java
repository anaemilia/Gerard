package gerard.ui.dialogos;

import gerard.i18n.ServicoLocalizacao;
import gerard.ui.UITemaGerard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Pede um número natural (só dígitos); devolve o texto digitado ou nulo se a pessoa cancelar. */
public final class DialogoValorInteiro {
    private DialogoValorInteiro() {
    }

    public static String solicitar(Component pai, ServicoLocalizacao localizacao, String valorAtual) {
        final JDialog dialogo = new JDialog(
                SwingUtilities.getWindowAncestor(pai),
                localizacao.texto("ui.dialog.insertValueTitle"),
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialogo.setResizable(false);

        JPanel conteudo = new JPanel(new BorderLayout(0, 12));
        conteudo.setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));

        JLabel instrucao = new JLabel(localizacao.texto("ui.dialog.replaceQuestion"));
        instrucao.setFont(instrucao.getFont().deriveFont(Font.BOLD));
        conteudo.add(instrucao, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 5));
        JTextField campo = new JTextField(valorAtual == null ? "" : valorAtual, 18);
        campo.getAccessibleContext().setAccessibleName(localizacao.texto("ui.dialog.insertValueTitle"));
        campo.getAccessibleContext().setAccessibleDescription(localizacao.texto("ui.dialog.replaceQuestion"));
        JLabel aviso = new JLabel(" ");
        aviso.setForeground(UITemaGerard.COR_ERRO);
        centro.add(campo, BorderLayout.NORTH);
        centro.add(aviso, BorderLayout.SOUTH);
        conteudo.add(centro, BorderLayout.CENTER);

        JButton cancelar = new JButton(localizacao.texto("analise.cancel"));
        JButton confirmar = new JButton(localizacao.texto("ui.dialog.confirm"));
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.add(cancelar);
        botoes.add(confirmar);
        conteudo.add(botoes, BorderLayout.SOUTH);

        final String[] resultado = new String[1];
        Runnable confirmarAcao = new Runnable() {
            public void run() {
                String valor = campo.getText() == null ? "" : campo.getText().trim();
                if (!valor.matches("[0-9]+")) {
                    aviso.setText(localizacao.texto("ui.dialog.invalidValue"));
                    campo.requestFocusInWindow();
                    campo.selectAll();
                    dialogo.pack();
                    return;
                }
                resultado[0] = valor;
                dialogo.dispose();
            }
        };

        confirmar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmarAcao.run();
            }
        });
        cancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose();
            }
        });
        campo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmarAcao.run();
            }
        });

        dialogo.getRootPane().setDefaultButton(confirmar);
        dialogo.getRootPane().registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialogo.dispose();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        dialogo.setContentPane(conteudo);
        dialogo.pack();
        dialogo.setMinimumSize(new Dimension(390, dialogo.getHeight()));
        dialogo.setLocationRelativeTo(pai);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                campo.requestFocusInWindow();
                campo.selectAll();
            }
        });
        dialogo.setVisible(true);
        return resultado[0];
    }
}
