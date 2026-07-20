package gerard.estilointeracao.estrategia;

import gerard.Scaffolding.proximidade.EstadoRealceAlvo;
import gerard.estilointeracao.EstiloInteracao;
import java.util.EnumMap;
import java.util.Map;

/** Registro único das estratégias de interação. */
public final class EstrategiasEstiloInteracao {
    private final Map<EstiloInteracao, EstrategiaEstiloInteracao> estrategias =
            new EnumMap<EstiloInteracao, EstrategiaEstiloInteracao>(EstiloInteracao.class);

    public EstrategiasEstiloInteracao() {
        estrategias.put(EstiloInteracao.PROXIMIDADE,
                simples(EstadoRealceAlvo.PROXIMIDADE, false));
        estrategias.put(EstiloInteracao.DROP_TARGET_HIGHLIGHTING,
                new EstrategiaEstiloInteracao() {
                    public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                        return dentro ? EstadoRealceAlvo.DROP_TARGET : EstadoRealceAlvo.NENHUM;
                    }
                    public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return false; }
                    public boolean deveCentralizarAoSoltar(boolean proximo) { return false; }
                });
        estrategias.put(EstiloInteracao.DRAG_OVER_FEEDBACK,
                new EstrategiaEstiloInteracao() {
                    public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                        return (proximo || dentro) ? EstadoRealceAlvo.DRAG_OVER : EstadoRealceAlvo.NENHUM;
                    }
                    public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return false; }
                    public boolean deveCentralizarAoSoltar(boolean proximo) { return false; }
                });
        estrategias.put(EstiloInteracao.AFFORDANCE,
                new EstrategiaEstiloInteracao() {
                    public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                        return proximo ? EstadoRealceAlvo.DRAG_OVER : EstadoRealceAlvo.AFFORDANCE;
                    }
                    public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return false; }
                    public boolean deveCentralizarAoSoltar(boolean proximo) { return false; }
                });
        estrategias.put(EstiloInteracao.SNAP_TO_TARGET,
                new EstrategiaEstiloInteracao() {
                    public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                        return proximo ? EstadoRealceAlvo.SNAP : EstadoRealceAlvo.NENHUM;
                    }
                    public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return proximo; }
                    public boolean deveCentralizarAoSoltar(boolean proximo) { return proximo; }
                });
    }

    public EstrategiaEstiloInteracao obter(EstiloInteracao estilo) {
        EstrategiaEstiloInteracao estrategia = estrategias.get(estilo);
        return estrategia != null ? estrategia : simples(EstadoRealceAlvo.NENHUM, false);
    }

    private EstrategiaEstiloInteracao simples(final EstadoRealceAlvo estado, final boolean magnetico) {
        return new EstrategiaEstiloInteracao() {
            public EstadoRealceAlvo calcularEstado(boolean proximo, boolean dentro) {
                return proximo ? estado : EstadoRealceAlvo.NENHUM;
            }
            public boolean deveAplicarAtracaoMagnetica(boolean proximo) { return magnetico && proximo; }
            public boolean deveCentralizarAoSoltar(boolean proximo) { return magnetico && proximo; }
        };
    }
}
