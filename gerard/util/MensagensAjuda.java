/*
 * A classe MensagensAjuda implementa a interface MensagensAjudasInterface
 *fornece as mensagens para cada acao do usuario.
 * As mensagens estao salvas no arquivo mensagens.properties
 */
package gerard.util;

import java.util.ResourceBundle;

/**
 *
 * @author Kecia
 */
public class MensagensAjuda implements MensagensAjudasInterface {

    private ResourceBundle bundle = null;
    private String property;
    public MensagensAjuda() {
        this.property="gerard.propriedades.ajudas";
    }


    public String proximoPasso(int passo) {
        String chave = "passo." + String.valueOf(passo);
        return ManipularProperties.getMensagemEm(chave,property);
    }

    public String erroCategorizacao(int categoriaSelecionadaPeloUsuario) {
        String chave = "erro.categorizacao." + String.valueOf(categoriaSelecionadaPeloUsuario);
        return ManipularProperties.getMensagemEm(chave,property);
    }

    public String erroPosicionamento() {
        String chave = "erro.posicionamento";
        return ManipularProperties.getMensagemEm(chave,property);
    }

    public String erroSinal() {
        String chave = "erro.sinal";
        return ManipularProperties.getMensagemEm(chave,property);
    }

    public String erroCalculo() {
        String chave = "erro.calculo";
        return ManipularProperties.getMensagemEm(chave,property);
    }

    public String maisDica(int passoAtual, int categoria) {
        String chave;
        switch (passoAtual) {
            case 1://categorização
                chave = "erro.categorizacao." + String.valueOf(categoria);
                return ManipularProperties.getMensagemEm(chave,property);
            case 2://compor diagrama
                chave = "maisdica." + String.valueOf(2);
                 return ManipularProperties.getMensagemEm(chave,property);
            case 3://compor diagrama
                chave = "maisdica." + String.valueOf(3);
                return ManipularProperties.getMensagemEm(chave,property);
            default:
                return "ops! Sem mais dicas! =(";
        }
    }


}
