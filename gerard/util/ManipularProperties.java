/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author Kecia
 */
public class  ManipularProperties {
    private ResourceBundle bundle;
    public ManipularProperties(String pathArquivo){
        //bundle = ResourceBundle.getBundle("gerard.propriedades.sqlBanco");
        bundle = ResourceBundle.getBundle(pathArquivo);
    }
      public static String replaceString(String novaString, String texto) {
        /*Modelo:
        String s = MessageFormat.format ("Troque o seu carro {0} por um {1} para não se encontrar com a Lady Murphy",
        new Object[]{ "Ford", "Fiat" });
         */ Object object[] = {novaString};
        String stringReplaced = MessageFormat.format(texto, object);
        return stringReplaced;
    }

    public static String replaceString(String novaString[], String texto) {
        /*Modelo:
        String s = MessageFormat.format ("Troque o seu carro {0} por um {1} para não se encontrar com a Lady Murphy",
        new Object[]{ "Ford", "Fiat" });
         */ Object object[] = novaString;
        String stringReplaced = MessageFormat.format(texto, object);
        return stringReplaced;
    }

    public String getMensagem(String chave) {
        String valor = null;
        try {
            valor = bundle.getString(chave);
        } catch (MissingResourceException e) {
            System.out.println("java.util.MissingResourceException: Couldn't find value for: " + chave);
        }
        if (valor == null) {
            //valor = "Could not find resource: " + chave + "  ";
            valor = chave;
        }
        return valor;
    }
    public static String getMensagemEm(String chave,String path) {
        String valor = null;
        try {
            valor = ResourceBundle.getBundle(path).getString(chave);
        } catch (MissingResourceException e) {
            System.out.println("java.util.MissingResourceException: Couldn't find value for: " + chave);
        }
        if (valor == null) {
            valor = "Could not find resource: " + chave + "  ";
        }
        return valor;
    }

}
