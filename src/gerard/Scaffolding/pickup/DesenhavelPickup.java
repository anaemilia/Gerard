package gerard.Scaffolding.pickup;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Contrato mínimo para qualquer elemento que possa receber a affordance
 * visual de pickup sem expor sua implementação concreta.
 */
public interface DesenhavelPickup {
    Rectangle obterLimitesVisuais();
    void desenharConteudo(Graphics2D g2);
}
