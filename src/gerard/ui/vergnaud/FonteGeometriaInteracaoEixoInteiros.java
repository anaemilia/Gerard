package gerard.ui.vergnaud;

import java.awt.Rectangle;

/**
 * Fornece ao adaptador desktop a geometria real vigente no instante de cada
 * evento. Assim, a tela compoe a fonte uma vez e nao precisa copiar medidas
 * para dentro dos protocolos centrais de mouse.
 */
public interface FonteGeometriaInteracaoEixoInteiros {
    int obterLarguraTela();

    int obterAlturaTela();

    Rectangle obterAreaDiagrama();
}
