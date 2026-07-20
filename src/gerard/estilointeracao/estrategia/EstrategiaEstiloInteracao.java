package gerard.estilointeracao.estrategia;

import gerard.Scaffolding.proximidade.EstadoRealceAlvo;

/** Estratégia de comportamento independente da representação. */
public interface EstrategiaEstiloInteracao {
    EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro);
    boolean deveAplicarAtracaoMagnetica(boolean proximo);
    boolean deveCentralizarAoSoltar(boolean proximo);
}
