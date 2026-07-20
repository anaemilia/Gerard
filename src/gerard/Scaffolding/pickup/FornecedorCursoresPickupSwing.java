package gerard.Scaffolding.pickup;

import java.awt.Cursor;

/**
 * Fornecedor de cursores de pickup baseado apenas em cursores nativos do
 * Swing/sistema operacional.
 *
 * No mouseover, reutiliza a mão nativa já empregada nos controles laterais do
 * gráfico de barras. Durante o arraste, muda para o cursor nativo de
 * movimentação. Assim, disponibilidade e deslocamento são diferenciados sem
 * introduzir desenhos personalizados de mão.
 */
public final class FornecedorCursoresPickupSwing
        extends FornecedorCursoresPickupAbstrato {

    @Override
    protected Cursor criarMaoAberta() {
        return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }

    @Override
    protected Cursor criarMaoFechada() {
        return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    }
}
