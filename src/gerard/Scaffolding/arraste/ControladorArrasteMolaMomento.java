package gerard.Scaffolding.arraste;

/**
 * Drag-and-drop com física de mola e momento. O elemento visual persegue o
 * cursor com pequeno atraso, massa e inércia, mas a coordenada de soltura
 * continua exata para não alterar validação, semântica ou sincronização.
 */
public final class ControladorArrasteMolaMomento
        extends ControladorArrasteElasticoAbstrato {

    @Override
    protected double obterRigidez() {
        return 0.48d;
    }

    @Override
    protected double obterAmortecimento() {
        return 0.72d;
    }

    @Override
    protected double obterMassa() {
        return 1.18d;
    }

    @Override
    protected double obterVelocidadeMaxima() {
        return 38.0d;
    }

    @Override
    protected double obterAtrasoMaximo() {
        return 26.0d;
    }
}
