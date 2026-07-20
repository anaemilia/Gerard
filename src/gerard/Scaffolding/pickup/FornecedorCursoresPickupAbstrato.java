package gerard.Scaffolding.pickup;

import java.awt.Cursor;

/** Mantém cache e fallback comum aos fornecedores concretos de cursor. */
public abstract class FornecedorCursoresPickupAbstrato implements FornecedorCursoresPickup {
    private Cursor maoAberta;
    private Cursor maoFechada;

    protected abstract Cursor criarMaoAberta();
    protected abstract Cursor criarMaoFechada();

    @Override
    public final Cursor obterMaoAberta() {
        if (maoAberta == null) {
            maoAberta = criarMaoAberta();
            if (maoAberta == null) {
                maoAberta = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            }
        }
        return maoAberta;
    }

    @Override
    public final Cursor obterMaoFechada() {
        if (maoFechada == null) {
            maoFechada = criarMaoFechada();
            if (maoFechada == null) {
                maoFechada = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            }
        }
        return maoFechada;
    }
}
