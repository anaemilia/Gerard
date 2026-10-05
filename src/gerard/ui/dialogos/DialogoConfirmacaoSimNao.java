package gerard.ui.dialogos;

import gerard.i18n.ServicoLocalizacao;
import gerard.ui.UITemaGerard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Diálogo Sim/Não no padrão visual do Gérard (JDialog + JPanel/JButton), em vez de
 * JOptionPane.showConfirmDialog, que usa ícone e rótulos nativos fora do idioma e da paleta do app.
 * As respostas são opcionais (podem ser nulas).
 */
public final class DialogoConfirmacaoSimNao {
    private DialogoConfirmacaoSimNao() {
    }

    public static void mostrar(Component pai, ServicoLocalizacao localizacao, String pergunta,
            final Runnable aoResponderSim, final Runnable aoResponderNao) {
        final JDialog dialogo = new JDialog(
                SwingUtilities.getWindowAncestor(pai),
                localizacao.texto("ui.dialog.confirm"),
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialogo.setResizable(false);

        JPanel conteudo = new JPanel(new BorderLayout(0, 16));
        conteudo.setBorder(BorderFactory.createEmptyBorder(18, 20, 14, 20));
        conteudo.setBackground(UITemaGerard.COR_SUPERFICIE);

        JLabel mensagem = new JLabel("<html><body style='width: 280px'>" + pergunta + "</body></html>");
        // Fonte maior pra melhor leitura (decisão da usuária, 2026-07-28:
        // "mensagens muito importantes") — a largura do HTML continua
        // fixa em 280px, então o texto ganha altura (mais linhas), não
        // largura, mantendo o diálogo estreito.
        mensagem.setFont(new Font("Arial", Font.PLAIN, 17));
        mensagem.setForeground(UITemaGerard.COR_TEXTO);
        conteudo.add(mensagem, BorderLayout.CENTER);

        JButton nao = new JButton(localizacao.texto("ui.completion.no"));
        JButton sim = new JButton(localizacao.texto("ui.completion.yes"));
        // Estiliza direto no componente em vez de confiar no L&F nativo
        // do Windows para o destaque azul do botão padrão — o mesmo
        // motivo já documentado para os menus (estilizarItemMenuPopup):
        // o L&F nativo ignora boa parte do UIManager.put, mas pintar
        // direto no componente funciona (relatado pela usuária,
        // 2026-07-28, com captura mostrando o azul nativo do Windows).
        for (JButton botao : new JButton[] {nao, sim}) {
            botao.setOpaque(true);
            botao.setBackground(UITemaGerard.COR_SUPERFICIE_SUAVE);
            botao.setForeground(UITemaGerard.COR_TEXTO);
            botao.setFocusPainted(false);
            botao.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA, 1));
        }
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        botoes.add(nao);
        botoes.add(sim);
        conteudo.add(botoes, BorderLayout.SOUTH);

        ActionListener fechar = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose();
            }
        };
        sim.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose();
                if (aoResponderSim != null) {
                    aoResponderSim.run();
                }
            }
        });
        nao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogo.dispose();
                if (aoResponderNao != null) {
                    aoResponderNao.run();
                }
            }
        });

        dialogo.getRootPane().setDefaultButton(sim);
        dialogo.getRootPane().registerKeyboardAction(
                fechar,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        dialogo.setContentPane(conteudo);
        dialogo.pack();
        dialogo.setLocationRelativeTo(pai);
        dialogo.setVisible(true);
    }
}
