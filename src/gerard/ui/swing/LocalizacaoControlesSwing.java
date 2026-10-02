package gerard.ui.swing;

import gerard.i18n.ServicoLocalizacao;
import javax.swing.UIManager;

/**
 * Adaptador de apresentação: os botões Sim/Não dos diálogos de confirmação do
 * Swing vêm do toolkit e dependem do idioma da máquina. Aqui eles passam a usar
 * os textos já existentes em mensagens_*.properties (ui.completion.yes/no) no
 * idioma da interface, inclusive quando ele muda. Não decide nada.
 */
public final class LocalizacaoControlesSwing {
    private static boolean instalado;

    private LocalizacaoControlesSwing() {
    }

    public static synchronized void instalar() {
        aplicar();
        if (!instalado) {
            instalado = true;
            ServicoLocalizacao.getInstancia().adicionarObservadorIdioma(new Runnable() {
                public void run() {
                    aplicar();
                }
            });
        }
    }

    private static void aplicar() {
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        UIManager.put("OptionPane.yesButtonText", localizacao.texto("ui.completion.yes"));
        UIManager.put("OptionPane.noButtonText", localizacao.texto("ui.completion.no"));
    }
}
