package gerard.campoaditivo.curadoria.sinal;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Impede a digitação de + ou - no campo de magnitude. O sinal deve ser
 * escolhido exclusivamente no seletor correspondente.
 */
final class FiltroMagnitudeSemSinal extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string,
            AttributeSet attr) throws BadLocationException {
        replace(fb, offset, 0, string, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text,
            AttributeSet attrs) throws BadLocationException {
        String insercao = text == null ? "" : text;
        if (insercao.indexOf('+') >= 0 || insercao.indexOf('-') >= 0
                || insercao.indexOf('\u2212') >= 0) {
            return;
        }
        super.replace(fb, offset, length, insercao, attrs);
    }
}
