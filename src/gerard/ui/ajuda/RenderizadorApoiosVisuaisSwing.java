package gerard.ui.ajuda;

import gerard.dominio.campoaditivo.ajuda.ApoioVisual;
import gerard.dominio.campoaditivo.ajuda.FormatoAjudaNarrativaVisual;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;

/**
 * Adaptador Swing dos apoios visuais projetados pela API. Renderiza a primeira entrada pelo
 * seu {@code tipo}; não escolhe entre apoios nem interpreta o conteúdo.
 */
public final class RenderizadorApoiosVisuaisSwing {

    private RenderizadorApoiosVisuaisSwing() {
    }

    /** Componente da primeira entrada da projeção, ou {@code null} se não há apoio. */
    public static JComponent criar(List<Object> projecao, Dimension tamanho) {
        JComponent conteudo = criarConteudo(projecao, tamanho);
        return conteudo == null ? null : new PainelAparicaoGradual(conteudo);
    }

    private static JComponent criarConteudo(List<Object> projecao, Dimension tamanho) {
        if (projecao == null || projecao.isEmpty()) {
            return null;
        }
        Map<?, ?> primeiro = (Map<?, ?>) projecao.get(0);
        String tipo = String.valueOf(primeiro.get("tipo"));
        if (ApoioVisual.TIPO_HISTORINHA_TEXTUAL.equals(tipo)) {
            return new PainelHistorinhaTextual(String.valueOf(primeiro.get("texto")), tamanho);
        }
        if (ApoioVisual.TIPO_HISTORINHA_ILUSTRADA.equals(tipo)) {
            // A animação não grava texto: as legendas (no idioma da situação) vêm da projeção e
            // são pintadas DENTRO da animação, no cronograma projetado.
            return PainelAjudaNarrativaVisualCategoria.criarPassivoDaProjecao(
                    projecao.subList(0, 1), FormatoAjudaNarrativaVisual.ANIMACAO, tamanho);
        }
        return null;
    }
}
