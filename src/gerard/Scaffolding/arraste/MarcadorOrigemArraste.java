package gerard.Scaffolding.arraste;

import java.awt.Graphics2D;

/** Contrato do contorno fantasma mantido no ponto inicial do arraste. */
public interface MarcadorOrigemArraste {
    void iniciar(DesenhavelFantasmaOrigem origem);
    void desenhar(Graphics2D g2);
    void limpar();
    boolean estaAtivo();
}
