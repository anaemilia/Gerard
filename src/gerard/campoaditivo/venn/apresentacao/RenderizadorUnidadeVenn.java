package gerard.campoaditivo.venn.apresentacao;

import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import java.awt.Graphics2D;

/**
 * Contrato de apresentação para unidades visuais manipuláveis.
 */
public interface RenderizadorUnidadeVenn {
    void desenhar(Graphics2D g2, QuadradinhoVenn unidade, EstadoVisualUnidadeVenn estado);
}
