package gerard.ui.conclusao;

import java.awt.Rectangle;

/**
 * Geometria de posicionamento ao lado direito do diagrama de Vergnaud,
 * compartilhada por qualquer selo circular exibido ali (conclusão da
 * modelagem, erro) — extraída de SeloConclusaoModelagem para não duplicar a
 * mesma conta em cada selo novo.
 */
final class PosicionadorSeloDiagrama {
    private PosicionadorSeloDiagrama() {
    }

    static Rectangle calcular(Rectangle areaDiagrama, Rectangle areaPermitida,
            int larguraPai, int alturaPai, int largura, int altura) {
        Rectangle permitida = areaPermitida == null
                ? new Rectangle(12, 55, Math.max(1, larguraPai - 24),
                        Math.max(1, alturaPai - 67))
                : new Rectangle(areaPermitida);

        int xDesejado = (areaDiagrama == null
                ? permitida.x + permitida.width
                : areaDiagrama.x + areaDiagrama.width) + 14;
        int maximoX = Math.min(larguraPai - largura - 12, permitida.x + permitida.width + largura + 40);
        int x = Math.max(permitida.x + 10, Math.min(xDesejado, maximoX));

        int centroYDiagrama = areaDiagrama == null
                ? permitida.y + permitida.height / 2
                : areaDiagrama.y + areaDiagrama.height / 2;
        int maximoY = Math.min(alturaPai - altura - 12, permitida.y + permitida.height - altura - 10);
        int y = Math.max(permitida.y + 10, Math.min(maximoY, centroYDiagrama - altura / 2));

        return new Rectangle(x, y, largura, altura);
    }
}
