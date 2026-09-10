package gerard.ui.enunciado.editor;

import gerard.ui.UITemaGerard;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.Icon;

/**
 * Ícones dos dois botões do editor de enunciado (editar/concluir) — mesmo
 * traçado e cor (símbolo "T" e círculo com marca de confirmação) do
 * protótipo web (.botao-editar-narrativa / .botao-concluir-narrativa em
 * styles.css, viewBox 16x16), redesenhados aqui via Graphics2D seguindo o
 * mesmo padrão de ícone vetorial já usado em Main.java (ver
 * criarIconeEditar/criarIconeRestaurar). Símbolo "T" fornecido pela usuária.
 */
public final class IconesEditorNarrativa {

    private IconesEditorNarrativa() {
    }

    /** "T" — abre o editor de enunciado (mesmo símbolo do botão web .botao-editar-narrativa). */
    public static Icon criarIconeEditar() {
        return new Icon() {
            public int getIconWidth() { return 16; }
            public int getIconHeight() { return 16; }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c.isEnabled() ? UITemaGerard.COR_ICONE_NARRATIVA
                            : UITemaGerard.COR_ICONE_DESABILITADO);
                    g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    Path2D topo = new Path2D.Double();
                    topo.moveTo(x + 2, y + 4);
                    topo.lineTo(x + 2, y + 2);
                    topo.lineTo(x + 14, y + 2);
                    topo.lineTo(x + 14, y + 4);
                    g2.draw(topo);

                    g2.draw(new java.awt.geom.Line2D.Double(x + 8, y + 2, x + 8, y + 14));
                    g2.draw(new java.awt.geom.Line2D.Double(x + 5, y + 14, x + 11, y + 14));
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    /** Círculo com confirmação — conclui a edição (mesmo símbolo do botão web .botao-concluir-narrativa, ícone circle-check-big fornecido pela usuária). */
    public static Icon criarIconeConcluir() {
        return new Icon() {
            public int getIconWidth() { return 16; }
            public int getIconHeight() { return 16; }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c.isEnabled() ? UITemaGerard.COR_ICONE_NARRATIVA
                            : UITemaGerard.COR_ICONE_DESABILITADO);
                    g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    g2.draw(new java.awt.geom.Ellipse2D.Double(x + 2, y + 2, 12, 12));

                    Path2D marca = new Path2D.Double();
                    marca.moveTo(x + 5, y + 8.3);
                    marca.lineTo(x + 7.2, y + 10.5);
                    marca.lineTo(x + 11.3, y + 5);
                    g2.draw(marca);
                } finally {
                    g2.dispose();
                }
            }
        };
    }
}
