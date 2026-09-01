package gerard.ui.vergnaud;

import java.awt.Rectangle;

/**
 * Fonte desktop da geometria vigente dos paineis de eixo das Relacoes.
 * Mantem dimensoes e Rectangle na fronteira da plataforma.
 */
public interface FonteGeometriaInteracaoPaineisEixosRelacoes {
    int obterLarguraTela();

    int obterAlturaTela();

    Rectangle obterAreaDiagrama();
}
