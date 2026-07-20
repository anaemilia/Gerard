package gerard.Scaffolding.arraste;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Forma estática que representa o lugar de onde o elemento foi retirado. */
public interface DesenhavelFantasmaOrigem {
    Rectangle obterLimitesOrigem();
    void desenharContorno(Graphics2D g2);
}
