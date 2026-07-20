package gerard.Scaffolding.menus;

import gerard.ui.UITemaGerard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * Menu de radio buttons usado pelo scaffolding do numero relativo.
 *
 * O componente exibe as opcoes de sinal ao lado do circulo que representa
 * relacao/transformacao e devolve ao chamador apenas o sinal escolhido.
 */
public class MenuSinalNumeroRelativo {

    public interface AcaoSinalNumeroRelativo {
        void sinalEscolhido(String sinal);
    }

    public void mostrar(
            Component componente,
            Rectangle circulo,
            final AcaoSinalNumeroRelativo acao
    ) {
        if (componente == null || circulo == null || acao == null) {
            return;
        }

        final JPopupMenu menu = new JPopupMenu();
        menu.setBackground(new Color(255, 255, 255));
        menu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 218, 224)),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        final JRadioButtonMenuItem positivo = criarOpcao("positivo  (+)");
        final JRadioButtonMenuItem negativo = criarOpcao("negativo  (-)");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(positivo);
        grupo.add(negativo);

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == negativo) {
                    acao.sinalEscolhido("-");
                } else {
                    acao.sinalEscolhido("+");
                }
                menu.setVisible(false);
            }
        };

        positivo.addActionListener(listener);
        negativo.addActionListener(listener);

        menu.add(positivo);
        menu.add(negativo);

        int x = circulo.x + circulo.width + 8;
        int y = Math.max(0, circulo.y + Math.max(0, (circulo.height - 48) / 2));

        if (x + 140 > componente.getWidth()) {
            x = Math.max(0, circulo.x - 148);
        }
        if (y + 58 > componente.getHeight()) {
            y = Math.max(0, componente.getHeight() - 58);
        }

        menu.show(componente, x, y);
    }

    private JRadioButtonMenuItem criarOpcao(String texto) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(texto);
        item.setFont(UITemaGerard.FONTE_ITEM_MENU);
        item.setForeground(new Color(31, 41, 51));
        item.setBackground(new Color(255, 255, 255));
        item.setMargin(new Insets(6, 10, 6, 10));
        item.setOpaque(true);
        return item;
    }
}
