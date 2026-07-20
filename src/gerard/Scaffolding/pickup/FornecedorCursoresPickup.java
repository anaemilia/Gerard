package gerard.Scaffolding.pickup;

import java.awt.Cursor;

/**
 * Contrato dos cursores associados ao pickup.
 *
 * A mão nativa sinaliza que o elemento está disponível para ser pego. Durante
 * o arraste ativo, o cursor de movimentação comunica deslocamento sem recorrer
 * a uma mão fechada personalizada.
 */
public interface FornecedorCursoresPickup {
    Cursor obterMaoAberta();
    Cursor obterMaoFechada();
}
