package gerard.Scaffolding.arraste;

/**
 * Mola discreta: atraso máximo de 26 px, resposta rápida e pequeno efeito de
 * peso sem comprometer precisão ou o local final da soltura.
 */
public final class ControladorArrasteElasticoMola
        extends ControladorArrasteElasticoAbstrato {

    @Override
    protected double obterRigidez() {
        return 0.34d;
    }

    @Override
    protected double obterAmortecimento() {
        return 0.65d;
    }

    @Override
    protected double obterMassa() {
        return 1.0d;
    }

    @Override
    protected double obterVelocidadeMaxima() {
        return 42.0d;
    }

    @Override
    protected double obterAtrasoMaximo() {
        return 26.0d;
    }
}
