package gerard.ui.enunciado;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.sincronizacao.texto.ElementoSemanticoTexto;
import java.awt.FontMetrics;
import java.util.List;

/**
 * Recalcula largura/altura/posição dos itens de texto arrastáveis do
 * enunciado a partir da métrica de fonte usada para desenhá-los, preservando
 * o centro horizontal de cada item.
 *
 * Extraído de Main.TelaGerard (método original
 * ajustarDimensoesItensSemanticosDoEnunciado) como parte da Fase 1 do plano
 * de refatoração — ver PLANO_REFATORACAO_ARQUITETURA_GERARD.md.
 *
 * É um método estático e sem estado (pure function sobre a lista recebida):
 * não guarda nada entre chamadas, então não precisa virar instância como
 * AreaTituloCategoriaEnunciado.
 */
public final class AjustadorDimensoesItensTexto {

    private AjustadorDimensoesItensTexto() {
    }

    /**
     * Ajusta, em memória, os itens da lista que forem {@link ItemTextoArrastavel}.
     * Itens de outros tipos de {@link ElementoSemanticoTexto} são ignorados,
     * exatamente como no comportamento original.
     *
     * @param elementos lista de elementos semânticos do enunciado (pode conter
     *                  tipos diferentes de ItemTextoArrastavel; pode ser nula ou vazia)
     * @param fm métrica da fonte usada para desenhar os itens
     *           (no chamador original: getFontMetrics(new Font("Arial", Font.BOLD, 20)))
     */
    public static void ajustar(List<ElementoSemanticoTexto> elementos, FontMetrics fm) {
        if (elementos == null || elementos.isEmpty()) {
            return;
        }
        for (ElementoSemanticoTexto elemento : elementos) {
            if (!(elemento instanceof ItemTextoArrastavel)) {
                continue;
            }
            ItemTextoArrastavel item = (ItemTextoArrastavel) elemento;
            int centroX = item.x + item.largura / 2;
            int novaLargura = Math.max(14, fm.stringWidth(item.valor) + 8);
            item.largura = novaLargura;
            item.altura = Math.max(item.altura, fm.getHeight() - 5);
            item.x = centroX - novaLargura / 2;
        }
    }
}
