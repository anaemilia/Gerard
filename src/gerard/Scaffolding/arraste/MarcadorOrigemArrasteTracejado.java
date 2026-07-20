package gerard.Scaffolding.arraste;

import java.awt.Color;

/** Contorno neutro e discreto, semelhante a um encaixe deixado vazio. */
public final class MarcadorOrigemArrasteTracejado
        extends MarcadorOrigemArrasteAbstrato {

    @Override
    protected Color obterCor() {
        return new Color(76, 104, 128);
    }

    @Override
    protected float obterOpacidade() {
        return 0.46f;
    }

    @Override
    protected float obterEspessura() {
        return 1.35f;
    }

    @Override
    protected float[] obterTracejado() {
        return new float[] {4.0f, 4.0f};
    }
}
