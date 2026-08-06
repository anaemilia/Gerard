package gerard.estilointeracao.estrategia;

import gerard.Scaffolding.proximidade.EstadoRealceAlvo;
import java.awt.Graphics2D;

/** Estratégia de comportamento independente da representação. */
public interface EstrategiaEstiloInteracao {
    EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro);
    boolean deveAplicarAtracaoMagnetica(boolean proximo);
    boolean deveCentralizarAoSoltar(boolean proximo);

    /**
     * Desenha a pista visual do estilo durante o arraste (linha, realce,
     * seta ou cantos de enquadramento, conforme o estilo). Recebe a
     * geometria de item e alvo já resolvida pelo chamador — a estratégia
     * não conhece as classes de representação, só desenha. Estratégias
     * sem pista própria além do estado de realce padrão não desenham nada.
     */
    void desenhar(Graphics2D g2,
            int itemX, int itemY, int itemLargura, int itemAltura,
            int alvoX, int alvoY, int alvoLargura, int alvoAltura,
            boolean proximo, boolean dentro);
}
