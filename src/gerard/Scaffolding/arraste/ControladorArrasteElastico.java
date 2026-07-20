package gerard.Scaffolding.arraste;

import java.awt.Point;

/**
 * Contrato do seguimento elástico entre o cursor e o elemento manipulado.
 * A posição final continua exata no momento da soltura.
 */
public interface ControladorArrasteElastico {
    void iniciar(int x, int y, OuvinteArrasteElastico ouvinte);
    void atualizarAlvo(int x, int y);
    void concluir(int x, int y);
    void cancelar();
    boolean estaAtivo();
    Point obterPosicaoVisual();
}
