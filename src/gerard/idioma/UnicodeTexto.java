package gerard.idioma;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.text.Normalizer;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/** Utilitários Unicode sem dependências externas. */
public final class UnicodeTexto {
    private UnicodeTexto() { }

    public static String normalizarNfc(String texto) {
        return texto == null ? "" : Normalizer.normalize(texto, Normalizer.Form.NFC);
    }

    public static Font fonteCompativel(Component componente, int estilo, int tamanho) {
        Font base = componente == null ? null : componente.getFont();
        if (base != null && Font.DIALOG.equals(base.getFamily())) return base.deriveFont(estilo, (float)tamanho);
        return new Font(Font.SANS_SERIF, estilo, tamanho);
    }

    public static boolean fonteExibeTudo(Component componente, String texto) {
        if (texto == null || texto.isEmpty()) return true;
        Font f = componente == null ? new Font(Font.SANS_SERIF, Font.PLAIN, 12) : componente.getFont();
        return f != null && f.canDisplayUpTo(texto) == -1;
    }

    /**
     * Mantido para compatibilidade com o fluxo existente. Os quatro idiomas
     * suportados usam orientação da esquerda para a direita.
     */
    public static void aplicarDirecao(Component componente, String direcao) {
        if (componente == null) return;
        ComponentOrientation orientacao = ComponentOrientation.LEFT_TO_RIGHT;
        componente.applyComponentOrientation(orientacao);
        if (componente instanceof JTextComponent) {
            ((JTextComponent) componente).setComponentOrientation(orientacao);
        }
        if (componente instanceof JComponent) ((JComponent) componente).revalidate();
    }
}
