package gerard.ui.enunciado.editor;

import gerard.ui.UITemaGerard;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.List;

/**
 * Desenha o editor de enunciado diretamente na cena (Graphics2D), no mesmo
 * estilo visual das peças de texto arrastáveis já existentes em Main.java
 * (ver {@code desenharElementoTextoMovel}) — sem nenhum componente Swing
 * aninhado (JPanel/JLabel), conforme decisão da usuária de manter o editor
 * "dentro da cena". As posições de cada peça já vêm calculadas por
 * {@link GeometriaEditorNarrativa#recalcular}; esta classe só pinta.
 */
public final class RenderizadorEditorNarrativa {

    private RenderizadorEditorNarrativa() {
    }

    public static void desenhar(Graphics2D g2, FontMetrics fm, GeometriaEditorNarrativa geometria,
            List<PecaPalavraRascunho> frase, List<PecaPalavraRascunho> comuns,
            List<PecaPalavraRascunho> organizadores, HandlerInteracaoPecaPalavraRascunho handlerArraste,
            String rotuloTitulo, String rotuloComuns, String rotuloOrganizadores, String nota) {
        Font fontePalavras = fm.getFont();

        desenharRotuloSecao(g2, fontePalavras, rotuloTitulo, geometria.obterAreaFrase());
        desenharRotuloSecao(g2, fontePalavras, rotuloComuns, geometria.obterAreaComuns());
        desenharRotuloSecao(g2, fontePalavras, rotuloOrganizadores, geometria.obterAreaOrganizadores());
        g2.setFont(fontePalavras);

        desenharPecas(g2, fm, frase, handlerArraste);
        desenharPecas(g2, fm, comuns, handlerArraste);
        desenharPecas(g2, fm, organizadores, handlerArraste);

        if (handlerArraste.estaAtivo()) {
            desenharFantasma(g2, fm, handlerArraste);
        }

        if (nota != null && nota.length() > 0) {
            Rectangle areaOrganizadores = geometria.obterAreaOrganizadores();
            g2.setFont(fontePalavras.deriveFont(Font.ITALIC, 11f));
            g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO);
            g2.drawString(nota, areaOrganizadores.x, areaOrganizadores.y + areaOrganizadores.height + 20);
            g2.setFont(fontePalavras);
        }
    }

    private static void desenharRotuloSecao(Graphics2D g2, Font fontePalavras, String rotulo, Rectangle area) {
        if (rotulo == null || rotulo.length() == 0) {
            return;
        }
        g2.setFont(fontePalavras.deriveFont(Font.BOLD, 13f));
        g2.setColor(UITemaGerard.COR_TEXTO);
        g2.drawString(rotulo, area.x, area.y - 6);
    }

    private static void desenharPecas(Graphics2D g2, FontMetrics fm, List<PecaPalavraRascunho> pecas,
            HandlerInteracaoPecaPalavraRascunho handlerArraste) {
        for (PecaPalavraRascunho peca : pecas) {
            if (peca == handlerArraste.obterPecaAtiva()) {
                continue;
            }
            desenharPeca(g2, fm, peca, false);
        }
    }

    private static void desenharFantasma(Graphics2D g2, FontMetrics fm, HandlerInteracaoPecaPalavraRascunho handlerArraste) {
        PecaPalavraRascunho peca = handlerArraste.obterPecaAtiva();
        peca.atualizarTamanho(fm);
        peca.x = handlerArraste.obterXAtual() - peca.largura / 2;
        peca.y = handlerArraste.obterYAtual();
        desenharPeca(g2, fm, peca, true);
    }

    private static void desenharPeca(Graphics2D g2, FontMetrics fm, PecaPalavraRascunho peca, boolean destaque) {
        String texto = peca.getValor() == null ? "" : peca.getValor();

        if (peca.isManipulavel()) {
            Stroke original = g2.getStroke();
            if (destaque) {
                g2.setColor(UITemaGerard.COR_DESTAQUE);
                g2.fillRoundRect(peca.x - 4, peca.y - fm.getAscent() + 1, peca.largura + 8, peca.altura + 4, 8, 8);
                g2.setColor(UITemaGerard.COR_PRIMARIA);
                g2.setStroke(new BasicStroke(1.2f));
            } else {
                g2.setColor(new Color(247, 246, 241));
                g2.fillRoundRect(peca.x - 4, peca.y - fm.getAscent() + 1, peca.largura + 8, peca.altura + 4, 8, 8);
                g2.setColor(UITemaGerard.COR_BORDA);
                g2.setStroke(new BasicStroke(0.8f));
            }
            g2.drawRoundRect(peca.x - 4, peca.y - fm.getAscent() + 1, peca.largura + 8, peca.altura + 4, 8, 8);
            g2.setStroke(original);
            g2.setColor(peca.isIncognita() ? UITemaGerard.COR_ICONE_NARRATIVA : UITemaGerard.COR_TEXTO);
        } else {
            g2.setColor(UITemaGerard.COR_TEXTO_SECUNDARIO);
        }

        g2.drawString(texto, peca.x, peca.y);
    }
}
