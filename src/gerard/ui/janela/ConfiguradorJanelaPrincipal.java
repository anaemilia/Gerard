package gerard.ui.janela;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * Configura a janela principal sem acoplar regras de apresentação à Main.
 * A aplicação inicia maximizada, mas preserva a moldura e os controles normais
 * do sistema operacional.
 */
public final class ConfiguradorJanelaPrincipal {
    private static final Dimension TAMANHO_RESTAURADO_PADRAO =
            new Dimension(1240, 760);
    private static final Dimension TAMANHO_MINIMO =
            new Dimension(960, 620);

    private ConfiguradorJanelaPrincipal() {
    }

    public static void aplicar(JFrame janela) {
        if (janela == null) {
            return;
        }
        janela.setMinimumSize(new Dimension(TAMANHO_MINIMO));
        janela.setSize(new Dimension(TAMANHO_RESTAURADO_PADRAO));
        janela.setLocationRelativeTo(null);
        janela.setExtendedState(
                janela.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }
}
