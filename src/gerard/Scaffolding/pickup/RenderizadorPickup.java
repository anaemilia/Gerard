package gerard.Scaffolding.pickup;

import java.awt.Graphics2D;

/** Contrato de apresentação do elemento enquanto está sendo segurado. */
public interface RenderizadorPickup {
    void desenharEmPrimeiroPlano(Graphics2D g2, DesenhavelPickup elemento);
}
