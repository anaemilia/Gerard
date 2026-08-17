package gerard.ui.geometria;

import java.awt.Rectangle;

/**
 * Nó de uma árvore geométrica de representação. Seus limites são locais ao
 * pai; a geometria absoluta é resolvida percorrendo somente a ascendência.
 */
public final class NoGeometriaRepresentacao {
    private final NoGeometriaRepresentacao pai;
    private Rectangle limitesLocais;

    public NoGeometriaRepresentacao(NoGeometriaRepresentacao pai,
            Rectangle limitesLocais) {
        if (limitesLocais == null) {
            throw new IllegalArgumentException("limites locais obrigatorios");
        }
        this.pai = pai;
        this.limitesLocais = new Rectangle(limitesLocais);
    }

    public NoGeometriaRepresentacao getPai() {
        return pai;
    }

    public void atualizarLimitesLocais(Rectangle novosLimites) {
        if (novosLimites == null) {
            throw new IllegalArgumentException("limites locais obrigatorios");
        }
        limitesLocais = new Rectangle(novosLimites);
    }

    public Rectangle obterLimitesLocais() {
        return new Rectangle(limitesLocais);
    }

    public Rectangle obterLimitesAbsolutos() {
        Rectangle absolutos = new Rectangle(limitesLocais);
        NoGeometriaRepresentacao ancestral = pai;
        while (ancestral != null) {
            Rectangle limitesAncestral = ancestral.limitesLocais;
            absolutos.translate(limitesAncestral.x, limitesAncestral.y);
            ancestral = ancestral.pai;
        }
        return absolutos;
    }
}
