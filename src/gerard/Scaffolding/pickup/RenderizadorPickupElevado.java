package gerard.Scaffolding.pickup;

/** Implementação discreta: escala de 106%, elevação de 2 px e sombra curta. */
public final class RenderizadorPickupElevado extends RenderizadorPickupAbstrato {
    @Override
    protected double obterEscala() {
        return 1.06d;
    }

    @Override
    protected int obterElevacao() {
        return 2;
    }

    @Override
    protected int obterDeslocamentoSombra() {
        return 4;
    }

    @Override
    protected float obterAlphaSombra() {
        return 0.24f;
    }
}
