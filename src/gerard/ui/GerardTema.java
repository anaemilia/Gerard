package gerard.ui;

import java.awt.Color;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;

/**
 * Ponto ÚNICO de instalação do Design System em todo componente Swing do
 * Gérard (prompt-claude-code-cores.md, 2026-09-30). Chamado uma vez na
 * partida do app ({@code Main.aplicarTemaSwingPadrao}) e vale para todas as
 * telas, diálogos, menus, dicas, botões, campos, abas e painéis — inclusive
 * os que ainda não foram migrados individualmente.
 *
 * Os valores vêm de {@link UITemaGerard} (cores) e {@link GerardFontes}
 * (tipografia); aqui só se decide ONDE cada token se aplica. Tudo é
 * instalado como UIResource: uma tela que defina explicitamente sua própria
 * cor, borda ou fonte continua prevalecendo.
 */
public final class GerardTema {

    private static final int RAIO_CAMPO = 8;

    private GerardTema() {
    }

    public static void instalar() {
        GerardFontes.aplicarFontesGlobais();
        instalarCores();
        instalarBordas();
        instalarComponentes();
    }

    private static ColorUIResource cor(Color c) {
        return new ColorUIResource(c);
    }

    private static void instalarCores() {
        Color fundo = UITemaGerard.COR_SUPERFICIE;
        Color janela = UITemaGerard.COR_FUNDO;
        Color suave = UITemaGerard.COR_SUPERFICIE_SUAVE;
        Color texto = UITemaGerard.COR_TEXTO;
        Color secundario = UITemaGerard.COR_TEXTO_SECUNDARIO;
        Color destaqueFundo = UITemaGerard.COR_ACAO_FUNDO;
        Color destaqueTexto = UITemaGerard.COR_ACAO_PRESSIONADA;

        // Base (janela, painéis, áreas de rolagem)
        for (String chave : new String[] {"Panel.background", "OptionPane.background",
                "Viewport.background", "ScrollPane.background", "SplitPane.background",
                "CheckBox.background", "RadioButton.background", "Label.background",
                "ToolBar.background", "control", "window", "menu"}) {
            UIManager.put(chave, cor(fundo));
        }
        gerard.ui.swing.LocalizacaoControlesSwing.instalar();
        UIManager.put("RootPane.background", cor(janela));
        UIManager.put("Button.background", cor(fundo));

        // Texto
        for (String chave : new String[] {"Label.foreground", "Button.foreground",
                "CheckBox.foreground", "RadioButton.foreground", "OptionPane.messageForeground",
                "OptionPane.foreground", "Panel.foreground", "TitledBorder.titleColor",
                "TabbedPane.foreground", "ToolTip.foreground", "textText", "controlText",
                "infoText", "MenuItem.foreground", "Menu.foreground", "CheckBoxMenuItem.foreground",
                "RadioButtonMenuItem.foreground", "MenuBar.foreground", "PopupMenu.foreground"}) {
            UIManager.put(chave, cor(texto));
        }
        for (String chave : new String[] {"Label.disabledForeground", "Button.disabledText",
                "CheckBox.disabledText", "RadioButton.disabledText", "MenuItem.disabledForeground",
                "TextField.inactiveForeground", "ComboBox.disabledForeground"}) {
            UIManager.put(chave, cor(secundario));
        }

        // Campos de entrada
        for (String prefixo : new String[] {"TextField", "PasswordField", "FormattedTextField",
                "TextArea", "TextPane", "EditorPane", "ComboBox", "Spinner", "List", "Table", "Tree"}) {
            UIManager.put(prefixo + ".background", cor(fundo));
            UIManager.put(prefixo + ".foreground", cor(texto));
        }
        for (String prefixo : new String[] {"TextField", "PasswordField", "FormattedTextField",
                "TextArea", "TextPane", "EditorPane"}) {
            UIManager.put(prefixo + ".caretForeground", cor(texto));
            UIManager.put(prefixo + ".selectionBackground", cor(destaqueFundo));
            UIManager.put(prefixo + ".selectionForeground", cor(texto));
            UIManager.put(prefixo + ".inactiveBackground", cor(suave));
        }
        for (String prefixo : new String[] {"ComboBox", "List", "Table", "Tree"}) {
            UIManager.put(prefixo + ".selectionBackground", cor(destaqueFundo));
            UIManager.put(prefixo + ".selectionForeground", cor(destaqueTexto));
        }
        UIManager.put("ComboBox.buttonBackground", cor(fundo));
        UIManager.put("ComboBox.disabledBackground", cor(suave));
        UIManager.put("Table.gridColor", cor(UITemaGerard.COR_BORDA));
        UIManager.put("TableHeader.background", cor(suave));
        UIManager.put("TableHeader.foreground", cor(texto));

        // Menus e dicas
        for (String chave : new String[] {"PopupMenu.background", "Menu.background",
                "MenuItem.background", "MenuBar.background", "CheckBoxMenuItem.background",
                "RadioButtonMenuItem.background", "ToolTip.background"}) {
            UIManager.put(chave, cor(fundo));
        }
        for (String chave : new String[] {"Menu.selectionBackground", "MenuItem.selectionBackground",
                "CheckBoxMenuItem.selectionBackground", "RadioButtonMenuItem.selectionBackground"}) {
            UIManager.put(chave, cor(destaqueFundo));
        }
        for (String chave : new String[] {"Menu.selectionForeground", "MenuItem.selectionForeground",
                "CheckBoxMenuItem.selectionForeground", "RadioButtonMenuItem.selectionForeground"}) {
            UIManager.put(chave, cor(destaqueTexto));
        }

        // Abas
        UIManager.put("TabbedPane.background", cor(UITemaGerard.COR_FUNDO_CONTEUDO));
        UIManager.put("TabbedPane.selected", cor(fundo));
        UIManager.put("TabbedPane.selectHighlight", cor(fundo));
        UIManager.put("TabbedPane.focus", cor(UITemaGerard.COR_ACAO));
        UIManager.put("TabbedPane.contentAreaColor", cor(fundo));

        // Botões (estados) e rolagem
        UIManager.put("Button.select", cor(destaqueFundo));
        UIManager.put("Button.focus", cor(UITemaGerard.COR_ACAO));
        UIManager.put("Button.gradient", null);
        UIManager.put("ScrollBar.thumb", cor(UITemaGerard.COR_BORDA_CONTROLE));
        UIManager.put("ScrollBar.thumbShadow", cor(UITemaGerard.COR_BORDA_CONTROLE));
        UIManager.put("ScrollBar.thumbHighlight", cor(UITemaGerard.COR_BORDA_CONTROLE));
        UIManager.put("ScrollBar.track", cor(suave));
        UIManager.put("ScrollBar.trackHighlight", cor(suave));
        UIManager.put("ProgressBar.foreground", cor(UITemaGerard.COR_ACAO));
        UIManager.put("Separator.foreground", cor(UITemaGerard.COR_BORDA));
        UIManager.put("Separator.background", cor(fundo));
    }

    private static void instalarBordas() {
        Color controle = UITemaGerard.COR_BORDA_CONTROLE;
        Insets campo = new Insets(6, 8, 6, 8);
        BorderUIResource bordaCampo = new BorderUIResource(
                new GerardBordaArredondada(controle, RAIO_CAMPO, campo));
        for (String chave : new String[] {"TextField.border", "PasswordField.border",
                "FormattedTextField.border"}) {
            UIManager.put(chave, bordaCampo);
        }
        UIManager.put("Button.border", new BorderUIResource(
                new GerardBordaArredondada(controle, 10, new Insets(8, 18, 8, 18))));
        UIManager.put("ToolTip.border", new BorderUIResource(
                new GerardBordaArredondada(controle, 6, new Insets(4, 8, 4, 8))));
        UIManager.put("PopupMenu.border", new BorderUIResource(
                new GerardBordaArredondada(UITemaGerard.COR_BORDA, 8, new Insets(4, 2, 4, 2))));
    }

    private static void instalarComponentes() {
        UIManager.put("ButtonUI", GerardButtonUI.class.getName());
        UIManager.put("TabbedPaneUI", GerardTabbedPaneUI.class.getName());
    }
}
