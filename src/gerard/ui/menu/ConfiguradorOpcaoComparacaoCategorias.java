package gerard.ui.menu;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;

/**
 * Habilita e conecta a opção do menu que abre a comparação entre categorias.
 *
 * A classe mantém fora da composição principal da tela a regra de estado e o
 * acionamento da opção. O menu principal apenas fornece o botão e a operação
 * de abertura da tela já existente.
 */
public final class ConfiguradorOpcaoComparacaoCategorias {

    private ConfiguradorOpcaoComparacaoCategorias() {
    }

    public static void configurar(final AbstractButton opcao,
            final Runnable abrirComparacao) {
        if (opcao == null) {
            throw new IllegalArgumentException("A opção de comparação é obrigatória.");
        }
        if (abrirComparacao == null) {
            throw new IllegalArgumentException("A ação de comparação é obrigatória.");
        }

        opcao.setEnabled(true);
        opcao.setToolTipText(null);
        opcao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        opcao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                abrirComparacao.run();
            }
        });
    }
}
