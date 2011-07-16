/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util;

/**
 *
 * @author Kecia
 */
public interface DesfazerRefazerObserver {
         public void atualizarDesfazer(boolean desfazer);
         public void atualizarRefazer(boolean refazer);
         public void atualizarBotaoSalvar(boolean salvar);
}
