package gerard.campoaditivo.diagrama.elementos;

import java.awt.*;
import java.awt.geom.QuadCurve2D;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.Scaffolding.proximidade.EstadoRealceAlvo;
import gerard.Scaffolding.proximidade.EstiloRealceAlvo;

public class ElementoVergnaud {
    public int x;
    public int y;
    public int largura;
    public int altura;
    public TipoFiguraDiagrama tipo;
    public String rotulo;
    public String textoEditavel = "";
    public String subtitulo = "";
    public boolean rotulosAcima = false;
    public Rectangle zonaPermitida;
    public boolean incognitaPrincipal;
    /**
     * Indica que o conteúdo foi informado diretamente pelo usuário por meio
     * da edição do elemento. Atualizações reativas entre representações não
     * devem marcar este estado.
     */
    public boolean preenchidoExplicitamentePeloUsuario = false;
    public EstadoRealceAlvo estadoRealce = EstadoRealceAlvo.NENHUM;
    private boolean conclusaoDestacada;

    public ElementoVergnaud(int x, int y, int largura, int altura, TipoFiguraDiagrama tipo, String rotulo, Rectangle zonaPermitida, boolean incognitaPrincipal) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.tipo = tipo;
        this.rotulo = rotulo;
        this.zonaPermitida = zonaPermitida;
        this.incognitaPrincipal = incognitaPrincipal;
    }

    public boolean contem(int mx, int my) {
        if (tipo == TipoFiguraDiagrama.ELIPSE) {
            double centroX = x + largura / 2.0;
            double centroY = y + altura / 2.0;
            double raioX = largura / 2.0;
            double raioY = altura / 2.0;
            double valor = Math.pow((mx - centroX) / raioX, 2) + Math.pow((my - centroY) / raioY, 2);
            return valor <= 1.0;
        }
        return mx >= x && mx <= x + largura && my >= y && my <= y + altura;
    }

    public void definirConclusaoDestacada(boolean destacada) {
        this.conclusaoDestacada = destacada;
    }

    public boolean isConclusaoDestacada() {
        return conclusaoDestacada;
    }

    public void moverPara(int novoX, int novoY, Rectangle limite) {
        Rectangle zona = zonaPermitida != null ? zonaPermitida : limite;
        int maxX = zona.x + zona.width - largura;
        int maxY = zona.y + zona.height - altura;
        x = Math.max(zona.x, Math.min(maxX, novoX));
        y = Math.max(zona.y, Math.min(maxY, novoY));
    }

    public void desenhar(Graphics2D g2) {
        Stroke old = g2.getStroke();
        EstiloRealceAlvo estiloRealce = EstiloRealceAlvo.paraEstado(estadoRealce);
        Color corPreenchimento = conclusaoDestacada
                ? gerard.ui.UITemaGerard.COR_SUCESSO_FUNDO : estiloRealce.getCorPreenchimento();
        Color corBorda = conclusaoDestacada
                ? gerard.ui.UITemaGerard.COR_SUCESSO : estiloRealce.getCorBorda();
        float espessura = conclusaoDestacada
                ? 2.4f : estiloRealce.getEspessuraBorda();

        g2.setColor(corPreenchimento);
        if (tipo == TipoFiguraDiagrama.ELIPSE) {
            g2.fillOval(x, y, largura, altura);
        } else if (tipo == TipoFiguraDiagrama.RETANGULO_ARREDONDADO) {
            g2.fillRoundRect(x, y, largura, altura, 20, 20);
        } else {
            g2.fillRect(x, y, largura, altura);
        }

        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke(espessura));
        if (tipo == TipoFiguraDiagrama.ELIPSE) {
            g2.drawOval(x, y, largura, altura);
        } else if (tipo == TipoFiguraDiagrama.RETANGULO_ARREDONDADO) {
            g2.drawRoundRect(x, y, largura, altura, 20, 20);
        } else {
            g2.drawRect(x, y, largura, altura);
        }
        if (textoEditavel != null && textoEditavel.trim().length() > 0) {
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (largura - fm.stringWidth(textoEditavel)) / 2;
            int ty = y + (altura - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(conclusaoDestacada ? gerard.ui.UITemaGerard.COR_SUCESSO_TEXTO : gerard.ui.UITemaGerard.COR_TEXTO);
            g2.drawString(textoEditavel, tx, ty);
        }

        if (rotulosAcima) {
            int yBaseAcima = y - 8;
            if (subtitulo != null && subtitulo.trim().length() > 0) {
                String textoSubtitulo = subtitulo.trim();
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fmSubtitulo = g2.getFontMetrics();
                int sx = x + (largura - fmSubtitulo.stringWidth(textoSubtitulo)) / 2;
                int sy = yBaseAcima - 2;
                g2.setColor(conclusaoDestacada ? gerard.ui.UITemaGerard.COR_SUCESSO : gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
                g2.drawString(textoSubtitulo, sx, sy);
                yBaseAcima = sy - fmSubtitulo.getDescent() - 8;
            }
            if (rotulo != null && rotulo.trim().length() > 0) {
                String textoRotulo = rotulo.trim();
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fmRotulo = g2.getFontMetrics();
                int rx = x + (largura - fmRotulo.stringWidth(textoRotulo)) / 2;
                int ry = yBaseAcima - 2;
                g2.setColor(conclusaoDestacada ? gerard.ui.UITemaGerard.COR_SUCESSO_TEXTO : gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
                g2.drawString(textoRotulo, rx, ry);
            }
        } else {
            int yRotuloBase = y + altura + 6;
            if (rotulo != null && rotulo.trim().length() > 0) {
                String textoRotulo = rotulo.trim();
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fmRotulo = g2.getFontMetrics();
                int rx = x + (largura - fmRotulo.stringWidth(textoRotulo)) / 2;
                int ry = yRotuloBase + fmRotulo.getAscent();
                g2.setColor(conclusaoDestacada ? gerard.ui.UITemaGerard.COR_SUCESSO_TEXTO : gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
                g2.drawString(textoRotulo, rx, ry);
                yRotuloBase = ry + 4;
            }

            if (subtitulo != null && subtitulo.trim().length() > 0) {
                String textoSubtitulo = subtitulo.trim();
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fmSubtitulo = g2.getFontMetrics();
                int sx = x + (largura - fmSubtitulo.stringWidth(textoSubtitulo)) / 2;
                int sy = yRotuloBase + fmSubtitulo.getAscent();
                g2.setColor(conclusaoDestacada ? gerard.ui.UITemaGerard.COR_SUCESSO : gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
                g2.drawString(textoSubtitulo, sx, sy);
            }
        }

        g2.setStroke(old);
    }
}
