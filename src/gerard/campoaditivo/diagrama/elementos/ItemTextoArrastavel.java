package gerard.campoaditivo.diagrama.elementos;

import gerard.campoaditivo.sincronizacao.texto.ElementoSemanticoTexto;
import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class ItemTextoArrastavel implements ElementoSemanticoTexto {
    public int x;
    public int y;
    public int largura;
    public int altura;
    public String valor;
    public boolean editavel;
    public String origemValor;
    public String chavePapel;
    private boolean conclusaoDestacada;
    public String tokenSemanticoId;
    private boolean preenchidoPeloProtocoloMouseTexto;

    public ItemTextoArrastavel(int x, int y, int largura, int altura, String valor, boolean editavel, String origemValor, String chavePapel) {
        this(x, y, largura, altura, valor, editavel, origemValor, chavePapel, "");
    }

    public ItemTextoArrastavel(int x, int y, int largura, int altura, String valor, boolean editavel, String origemValor, String chavePapel, String tokenSemanticoId) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.valor = valor;
        this.editavel = editavel;
        this.origemValor = origemValor;
        this.chavePapel = chavePapel;
        this.tokenSemanticoId = tokenSemanticoId == null ? "" : tokenSemanticoId;
        this.preenchidoPeloProtocoloMouseTexto = false;
    }

    public void registrarPreenchimentoPeloProtocoloMouseTexto() {
        if (representaIncognitaOriginal()) {
            preenchidoPeloProtocoloMouseTexto = true;
        }
    }

    public boolean isPreenchidoPeloProtocoloMouseTexto() {
        return preenchidoPeloProtocoloMouseTexto;
    }


    @Override
    public String getChavePapelSemantico() {
        return chavePapel;
    }

    @Override
    public String getValorSemanticoOriginal() {
        return origemValor;
    }

    @Override
    public boolean possuiVinculoSemantico() {
        return chavePapel != null && chavePapel.trim().length() > 0
                && !"papel.valor".equals(chavePapel.trim());
    }

    @Override
    public boolean representaIncognitaOriginal() {
        return "?".equals(origemValor == null ? "" : origemValor.trim());
    }

    @Override
    public void atualizarValorSemantico(String novoValor) {
        if (novoValor != null) {
            valor = novoValor;
        }
    }

    public boolean contem(int mx, int my) {
        return mx >= x && mx <= x + largura &&
               my >= y && my <= y + altura;
    }

    public boolean estaNoDiagrama() {
        return y >= 210;
    }

    public void definirConclusaoDestacada(boolean destacada) {
        this.conclusaoDestacada = destacada;
    }

    public boolean isConclusaoDestacada() {
        return conclusaoDestacada;
    }

    public void desenhar(Graphics2D g2) {
        g2.setColor(conclusaoDestacada ? gerard.ui.UITemaGerard.COR_SUCESSO_FUNDO : gerard.ui.UITemaGerard.COR_SUPERFICIE);
        g2.fillRoundRect(x, y, largura, altura, 8, 8);

        Stroke original = g2.getStroke();
        float[] tracejado = {2.0f, 2.0f};

        g2.setColor(conclusaoDestacada ? gerard.ui.UITemaGerard.COR_SUCESSO : gerard.ui.UITemaGerard.COR_BORDA);
        g2.setStroke(new BasicStroke(
                0.9f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f,
                tracejado,
                0.0f
        ));
        g2.drawRoundRect(x, y, largura, altura, 8, 8);
        g2.setStroke(original);

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();

        int textoX = x + (largura - fm.stringWidth(valor)) / 2;
        int textoY = y + ((altura - fm.getHeight()) / 2) + fm.getAscent();

        g2.setColor(conclusaoDestacada ? gerard.ui.UITemaGerard.COR_SUCESSO_TEXTO : gerard.ui.UITemaGerard.COR_TEXTO);
        g2.drawString(valor, textoX, textoY);
    }
}
