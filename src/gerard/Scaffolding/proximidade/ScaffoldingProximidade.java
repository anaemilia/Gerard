package gerard.Scaffolding.proximidade;

import gerard.estilointeracao.EstiloInteracao;
import gerard.estilointeracao.estrategia.EstrategiaEstiloInteracao;
import gerard.estilointeracao.estrategia.EstrategiasEstiloInteracao;

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
}
