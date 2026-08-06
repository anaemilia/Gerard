package gerard.Scaffolding.proximidade;

import gerard.estilointeracao.EstiloInteracao;
import gerard.estilointeracao.estrategia.EstrategiaEstiloInteracao;
import gerard.estilointeracao.estrategia.EstrategiasEstiloInteracao;

import java.awt.Graphics2D;

/**
 * Aplica os mecanismos de scaffolding por proximidade. A variação de
 * comportamento foi transferida para estratégias independentes da representação.
 */
public class ScaffoldingProximidade {
    private final EstrategiasEstiloInteracao estrategias = new EstrategiasEstiloInteracao();

    private EstrategiaEstiloInteracao estrategia(EstiloInteracao modo) {
        return estrategias.obter(modo);
    }

    public EstadoRealceAlvo calcularEstadoAlvo(EstiloInteracao modo, boolean proximo, boolean dentro) {
        return estrategia(modo).calcularEstado(proximo, dentro);
    }

    public boolean deveAplicarAtracaoMagnetica(EstiloInteracao modo, boolean proximo) {
        return estrategia(modo).deveAplicarAtracaoMagnetica(proximo);
    }

    public boolean deveCentralizarAoSoltar(EstiloInteracao modo, boolean proximo) {
        return estrategia(modo).deveCentralizarAoSoltar(proximo);
    }

    /**
     * Desenha a pista visual do estilo ativo durante o arraste. Extraído de
     * Main.java/TelaGerard (antes: desenharPistaVisualDoModo, se ramificava
     * de novo sobre o mesmo EstiloInteracao só para decidir o desenho).
     */
    public void desenhar(EstiloInteracao modo, Graphics2D g2,
            int itemX, int itemY, int itemLargura, int itemAltura,
            int alvoX, int alvoY, int alvoLargura, int alvoAltura,
            boolean proximo, boolean dentro) {
        estrategia(modo).desenhar(g2,
                itemX, itemY, itemLargura, itemAltura,
                alvoX, alvoY, alvoLargura, alvoAltura,
                proximo, dentro);
    }
}
