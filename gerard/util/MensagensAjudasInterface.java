/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util;

/**
 *
 * @author Kecia
 */
public interface MensagensAjudasInterface {

    public String maisDica(int passoAtual,int categoria);
    public String proximoPasso(int passo);
    /*Passos:
     * 1- Categorização (escolha do diagrama correspondente)
     * 2- Posicionamento dos elmentos no diagrama (arrastar os elementos para diagrama)
     * 3- Calculo do resultado (usuário digita o resultado da questao)
     * 4- Finalização (pergunta se quer responder mais uma questão)
     */

    public String erroCategorizacao(int categoriaSelecionadaPeloUsuario);
    public String erroPosicionamento();
    public String erroSinal();
    public String erroCalculo();

}
