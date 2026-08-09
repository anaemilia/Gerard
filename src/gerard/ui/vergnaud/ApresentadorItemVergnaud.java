package gerard.ui.vergnaud;

import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import java.awt.FontMetrics;

/** Operações exclusivamente visuais sobre itens posicionados no Vergnaud. */
public final class ApresentadorItemVergnaud {

    private static final int MARGEM_HORIZONTAL_TEXTO = 8;
    private static final int AJUSTE_VERTICAL_TEXTO = 5;

    public void atualizarEDimensionar(
            ItemTextoArrastavel item, String valor, FontMetrics metricas) {
        if (item == null || metricas == null) {
            return;
        }
        item.valor = valor == null ? "" : valor;
        item.largura = metricas.stringWidth(item.valor) + MARGEM_HORIZONTAL_TEXTO;
        item.altura = metricas.getHeight() - AJUSTE_VERTICAL_TEXTO;
    }

    public void centralizar(ItemTextoArrastavel item, ElementoVergnaud alvo) {
        if (item == null || alvo == null) {
            return;
        }
        expandirAlvoMantendoCentro(item, alvo);
        item.x = alvo.x + (alvo.largura - item.largura) / 2;
        item.y = alvo.y + (alvo.altura - item.altura) / 2;
    }

    public void centralizarSeCentroEstiverContido(
            ItemTextoArrastavel item, ElementoVergnaud alvo) {
        if (item == null || alvo == null) {
            return;
        }
        int centroX = item.x + item.largura / 2;
        int centroY = item.y + item.altura / 2;
        if (alvo.contem(centroX, centroY)) {
            centralizar(item, alvo);
        }
    }

    private void expandirAlvoMantendoCentro(
            ItemTextoArrastavel item, ElementoVergnaud alvo) {
        if (item.largura <= alvo.largura) {
            return;
        }
        int centroX = alvo.x + alvo.largura / 2;
        alvo.largura = item.largura;
        alvo.x = centroX - alvo.largura / 2;
    }
}
