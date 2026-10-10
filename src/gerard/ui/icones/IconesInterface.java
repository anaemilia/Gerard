package gerard.ui.icones;

import gerard.ui.UITemaGerard;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

/**
 * Ícones vetoriais da barra de menus, do cabeçalho e dos botões de ação contextual. Só desenham: tamanho, traço e
 * cor vêm do tema; quem os usa decide onde e quando aparecem.
 */
public final class IconesInterface {
    private IconesInterface() {
    }

    public static Icon restaurar() {
        return new Icon() {
            public int getIconWidth() { return 16; }
            public int getIconHeight() { return 16; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c.isEnabled() ? UITemaGerard.COR_TEXTO_SECUNDARIO : gerard.ui.UITemaGerard.COR_ICONE_DESABILITADO);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawArc(x + 3, y + 3, 10, 10, 40, 285);
                    Path2D seta = new Path2D.Double();
                    seta.moveTo(x + 2.5, y + 7.0);
                    seta.lineTo(x + 3.7, y + 2.5);
                    seta.lineTo(x + 7.5, y + 4.7);
                    g2.draw(seta);
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    public static Icon editar() {
        return new Icon() {
            public int getIconWidth() { return 16; }
            public int getIconHeight() { return 16; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c.isEnabled() ? UITemaGerard.COR_TEXTO_SECUNDARIO : gerard.ui.UITemaGerard.COR_ICONE_DESABILITADO);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 4, y + 12, x + 11, y + 5);
                    g2.drawLine(x + 6, y + 14, x + 13, y + 7);
                    g2.drawLine(x + 4, y + 12, x + 3, y + 15);
                    g2.drawLine(x + 3, y + 15, x + 6, y + 14);
                    g2.drawLine(x + 11, y + 5, x + 13, y + 7);
                    g2.drawLine(x + 12, y + 4, x + 14, y + 6);
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    public static Icon idiomaSituacao() {
        return new Icon() {
            public int getIconWidth() { return 18; }
            public int getIconHeight() { return 16; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c.isEnabled() ? UITemaGerard.COR_TEXTO_SECUNDARIO : gerard.ui.UITemaGerard.COR_ICONE_DESABILITADO);
                    g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
                    g2.drawString("A", x + 1, y + 11);
                    g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                    g2.drawString("文", x + 9, y + 11);
                    g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 5, y + 14, x + 13, y + 14);
                    g2.drawLine(x + 11, y + 12, x + 13, y + 14);
                    g2.drawLine(x + 11, y + 16, x + 13, y + 14);
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    public static Icon chat() {
        return new Icon() {
            public int getIconWidth() { return 30; }
            public int getIconHeight() { return 22; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c.isEnabled() ? UITemaGerard.COR_TEXTO_SECUNDARIO : gerard.ui.UITemaGerard.COR_ICONE_DESABILITADO);
                    g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawRoundRect(x + 1, y + 1, 27, 16, 14, 14);
                    Path2D cauda = new Path2D.Float();
                    cauda.moveTo(x + 9, y + 17);
                    cauda.curveTo(x + 8, y + 19, x + 6, y + 20, x + 4, y + 21);
                    cauda.curveTo(x + 6, y + 19, x + 6, y + 18, x + 6, y + 16);
                    g2.draw(cauda);
                    g2.fillOval(x + 8, y + 8, 3, 3);
                    g2.fillOval(x + 14, y + 8, 3, 3);
                    g2.fillOval(x + 20, y + 8, 3, 3);
                } finally { g2.dispose(); }
            }
        };
    }

    public static Icon interrogacaoContextual() {
        return new Icon() {
            public int getIconWidth() { return 20; }
            public int getIconHeight() { return 20; }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(UITemaGerard.COR_SUPERFICIE);
                    g2.fillOval(x + 1, y + 1, 18, 18);
                    g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO);
                    g2.setStroke(new BasicStroke(1.4f));
                    g2.drawOval(x + 1, y + 1, 18, 18);
                    // Icone "info" (circulo + haste + ponto), mesmo simbolo
                    // do botao web (IconeAjudaContextual, MenuAjudaContextual.tsx).
                    g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(x + 10, y + 9, x + 10, y + 13);
                    g2.fillOval(x + 9, y + 5, 2, 2);
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    public static Icon comunidade() {
        final int tamanho = 22;
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = gerard.ui.categoria.IconeCategoria.prepararTraco(g);
                try {
                    g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO);
                    g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    desenharPessoaIconeComunidade(g2, x + 6, y + 4);
                    desenharPessoaIconeComunidade(g2, x + 13, y + 6);
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    public static Icon site() {
        final int tamanho = 22;
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = gerard.ui.categoria.IconeCategoria.prepararTraco(g);
                try {
                    int diametro = tamanho - 4;
                    float ox = x + 2;
                    float oy = y + 2;
                    g2.draw(new java.awt.geom.Ellipse2D.Float(ox, oy, diametro, diametro));
                    g2.draw(new java.awt.geom.Line2D.Float(ox, oy + diametro / 2f, ox + diametro, oy + diametro / 2f));
                    g2.draw(new java.awt.geom.Ellipse2D.Float(ox + diametro * 0.28f, oy, diametro * 0.44f, diametro));
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    public static Icon entrar() {
        final int tamanho = 28;
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawOval(x + 2, y + 2, tamanho - 5, tamanho - 5);
                    int cy = y + tamanho / 2;
                    g2.drawLine(x + 8, cy, x + 19, cy);
                    g2.drawLine(x + 15, cy - 4, x + 19, cy);
                    g2.drawLine(x + 15, cy + 4, x + 19, cy);
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    public static Icon reportarBug() {
        final int tamanho = 22;
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = gerard.ui.categoria.IconeCategoria.prepararTraco(g);
                try {
                    int corpoLargura = 10;
                    int corpoAltura = 12;
                    int cx = x + tamanho / 2;
                    int topoCorpo = y + 7;
                    g2.draw(new java.awt.geom.Ellipse2D.Float(cx - corpoLargura / 2f, topoCorpo, corpoLargura, corpoAltura));

                    int cabecaDiametro = 5;
                    g2.draw(new java.awt.geom.Ellipse2D.Float(cx - cabecaDiametro / 2f, topoCorpo - cabecaDiametro + 1, cabecaDiametro, cabecaDiametro));

                    g2.drawLine(cx - 1, topoCorpo - cabecaDiametro + 1, cx - 3, y + 1);
                    g2.drawLine(cx + 1, topoCorpo - cabecaDiametro + 1, cx + 3, y + 1);

                    int[] deslocamentosY = {topoCorpo + 2, topoCorpo + 6, topoCorpo + 10};
                    for (int dy : deslocamentosY) {
                        g2.drawLine(cx - corpoLargura / 2, dy, cx - corpoLargura / 2 - 4, dy - 2);
                        g2.drawLine(cx - corpoLargura / 2, dy, cx - corpoLargura / 2 - 4, dy + 2);
                        g2.drawLine(cx + corpoLargura / 2, dy, cx + corpoLargura / 2 + 4, dy - 2);
                        g2.drawLine(cx + corpoLargura / 2, dy, cx + corpoLargura / 2 + 4, dy + 2);
                    }
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    public static Icon compararCategorias() {
        final int tamanho = 30;
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = gerard.ui.categoria.IconeCategoria.prepararTraco(g);
                try {
                    int ladoCaixa = 9;
                    int yCaixa = y + 10;
                    g2.drawRoundRect(x + 3, yCaixa, ladoCaixa, ladoCaixa, 2, 2);
                    g2.drawRoundRect(x + tamanho - 3 - ladoCaixa, yCaixa, ladoCaixa, ladoCaixa, 2, 2);

                    int yFlecha = y + tamanho / 2;
                    int xEsq = x + 3 + ladoCaixa + 2;
                    int xDir = x + tamanho - 3 - ladoCaixa - 2;
                    g2.drawLine(xEsq, yFlecha, xDir, yFlecha);
                    g2.drawLine(xEsq, yFlecha, xEsq + 4, yFlecha - 3);
                    g2.drawLine(xEsq, yFlecha, xEsq + 4, yFlecha + 3);
                    g2.drawLine(xDir, yFlecha, xDir - 4, yFlecha - 3);
                    g2.drawLine(xDir, yFlecha, xDir - 4, yFlecha + 3);
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    public static Icon ferramentaSortear() {
        final int tamanho = 26;
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = gerard.ui.categoria.IconeCategoria.prepararTraco(g);
                try {
                    g2.drawRoundRect(x + 2, y + 2, tamanho - 4, tamanho - 4, 6, 6);
                    int raioPonto = 2;
                    desenharPontoIconeFerramentaSortear(g2, x + 8, y + 8, raioPonto);
                    desenharPontoIconeFerramentaSortear(g2, x + tamanho / 2, y + tamanho / 2, raioPonto);
                    desenharPontoIconeFerramentaSortear(g2, x + tamanho - 8, y + tamanho - 8, raioPonto);
                } finally {
                    g2.dispose();
                }
            }
        };
    }

    private static void desenharPessoaIconeComunidade(Graphics2D g2, int cx, int topoCabeca) {
        int diametroCabeca = 6;
        g2.draw(new java.awt.geom.Ellipse2D.Float(cx - diametroCabeca / 2f, topoCabeca, diametroCabeca, diametroCabeca));
        java.awt.geom.Arc2D.Float corpo = new java.awt.geom.Arc2D.Float(
                cx - 6, topoCabeca + diametroCabeca - 1, 12, 11, 15, 150, java.awt.geom.Arc2D.OPEN);
        g2.draw(corpo);
    }

    private static void desenharPontoIconeFerramentaSortear(Graphics2D g2, int cx, int cy, int raio) {
        g2.fill(new java.awt.geom.Ellipse2D.Float(cx - raio, cy - raio, raio * 2, raio * 2));
    }

    public static BufferedImage recortarImagemCircular(Image origem, int tamanho) {
        BufferedImage quadro = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = quadro.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, tamanho, tamanho));
            g2.drawImage(origem, 0, 0, tamanho, tamanho, null);
        } finally {
            g2.dispose();
        }
        return quadro;
    }

    /** Silhueta genérica (círculo + cabeça + ombros) usada quando o usuário não tem foto. */
    public static Icon pessoaSilhueta(final int tamanho) {
        return new Icon() {
            public int getIconWidth() { return tamanho; }
            public int getIconHeight() { return tamanho; }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = gerard.ui.categoria.IconeCategoria.prepararTraco(g);
                try {
                    g2.draw(new java.awt.geom.Ellipse2D.Float(x + 1, y + 1, tamanho - 2, tamanho - 2));
                    int cabecaDiam = tamanho / 3;
                    g2.draw(new java.awt.geom.Ellipse2D.Float(
                            x + tamanho / 2f - cabecaDiam / 2f, y + tamanho * 0.28f, cabecaDiam, cabecaDiam));
                    java.awt.geom.Arc2D.Float ombros = new java.awt.geom.Arc2D.Float(
                            x + tamanho * 0.2f, y + tamanho * 0.55f, tamanho * 0.6f, tamanho * 0.55f,
                            20, 140, java.awt.geom.Arc2D.OPEN);
                    g2.draw(ombros);
                } finally {
                    g2.dispose();
                }
            }
        };
    }
}
