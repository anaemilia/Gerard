package gerard.ui.conclusao;

import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;
import java.awt.Rectangle;
import java.util.Collection;

/**
 * Calcula a área visual efetivamente ocupada pelo diagrama de Vergnaud.
 * Considera figuras, rótulos, subtítulos, conectores, curvas e legendas para
 * que componentes contextuais possam ser posicionados sem cobrir o diagrama.
 */
public final class CalculadorAreaVisualDiagramaVergnaud {
    private static final int MARGEM_HORIZONTAL_ROTULO = 78;
    private static final int ESPACO_ROTULO_INFERIOR = 42;
    private static final int ESPACO_ROTULO_SUPERIOR = 38;
    private static final int MARGEM_CONECTOR = 18;
    private static final int EXTENSAO_LEGENDA_CHAVE = 72;

    public Rectangle calcular(Collection<ElementoVergnaud> elementos,
                               Collection<ConectorVergnaud> conectores,
                               Rectangle areaFallback) {
        Rectangle area = null;

        if (elementos != null) {
            for (ElementoVergnaud elemento : elementos) {
                if (elemento == null) {
                    continue;
                }
                Rectangle limite = limiteVisualElemento(elemento);
                area = unir(area, limite);
            }
        }

        if (conectores != null) {
            for (ConectorVergnaud conector : conectores) {
                if (conector == null) {
                    continue;
                }
                area = unir(area, limiteVisualConector(conector));
            }
        }

        if (area == null || area.width <= 0 || area.height <= 0) {
            return areaFallback == null ? new Rectangle() : new Rectangle(areaFallback);
        }
        return area;
    }

    private Rectangle limiteVisualElemento(ElementoVergnaud elemento) {
        int esquerda = elemento.x - MARGEM_HORIZONTAL_ROTULO;
        int direita = elemento.x + elemento.largura + MARGEM_HORIZONTAL_ROTULO;
        int topo = elemento.y;
        int base = elemento.y + elemento.altura;

        if (elemento.rotulosAcima) {
            topo -= ESPACO_ROTULO_SUPERIOR;
        } else {
            base += ESPACO_ROTULO_INFERIOR;
        }
        return new Rectangle(esquerda, topo,
                Math.max(1, direita - esquerda), Math.max(1, base - topo));
    }

    private Rectangle limiteVisualConector(ConectorVergnaud conector) {
        int esquerda = Math.min(conector.x1, conector.x2) - MARGEM_CONECTOR;
        int direita = Math.max(conector.x1, conector.x2) + MARGEM_CONECTOR;
        int topo = Math.min(conector.y1, conector.y2) - MARGEM_CONECTOR;
        int base = Math.max(conector.y1, conector.y2) + MARGEM_CONECTOR;

        if (conector.tipo == TipoConectorDiagrama.SETA_CURVA) {
            base = Math.max(base, Math.max(conector.y1, conector.y2) + 114);
        } else if (conector.tipo == TipoConectorDiagrama.CHAVE_VERTICAL) {
            direita += EXTENSAO_LEGENDA_CHAVE;
        } else if (conector.tipo == TipoConectorDiagrama.CHAVE_HORIZONTAL) {
            base += 34;
        }
        return new Rectangle(esquerda, topo,
                Math.max(1, direita - esquerda), Math.max(1, base - topo));
    }

    private Rectangle unir(Rectangle atual, Rectangle novo) {
        if (atual == null) {
            return new Rectangle(novo);
        }
        Rectangle unido = new Rectangle(atual);
        unido.add(novo);
        return unido;
    }
}
