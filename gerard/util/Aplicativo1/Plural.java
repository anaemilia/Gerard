

package gerard.util.Aplicativo1;
import gerard.banco.BancoSingleton;
import java.util.Iterator;
import java.text.Normalizer;
/**
 *
 * @author Alisson
 */
public class Plural {
private BancoSingleton dbCon= BancoSingleton.getInstancia();
    public boolean inArray(String s, String[] array)
    {
        for (String string : array)
        {
            if (string.equals(s))
            {
		return true;
            }
	}
        return false;
    }

    public  boolean E_Nasal(String c)
    {
	String[] chars = new String[] {
		"ã","õ"
	};

	return inArray(c, chars);
    }
    public boolean E_Vogal(String v)
    {
        		String[] vogais = {
				"a","e","i","o","u",
				"á","é","í","ó","ú",
				"à","è","ì","ò","ù",
				"â","ê","î","ô","û",
				"ä","ë","ï","ö","ü",
				"ã","õ",
				"A","E","I","O","U",
				"Á","É","Í","Ó","Ú",
				"À","È","Ì","Ò","Ù",
				"Â","Ê","Î","Ô","Û",
				"Ä","Ë","Ï","Ö","Ü",
				"Ã","Õ"
		};

		return inArray(v, vogais);
    }

    public boolean E_Digito(String digito)
    {
        try
        {
            Integer.parseInt( digito );
            return true;
        }
        catch( Exception e)
        {
            return false;
        }
    }

    public boolean E_Consoante(String c)
    {
	 return (c.equals("")) || (!E_Vogal(c) && !Character.isDigit(c.charAt(0)));
    }

    public String Normalizar_Palavra(String entrada)
    {
         String saida;
         saida = Normalizer.normalize(entrada, Normalizer.Form.NFD);
         saida = saida.replaceAll("[^\\p{ASCII}]", "");
         return saida;
    }

    public  boolean Tem_Acentuacao(String silaba)
    {
		String[] accentuatedVogals = new String[] {
			"á","é","í","ó","ú","à","è","ì","ò","ù","â","ê","î","ô","û","ã","õ",
			"Á","É","Í","Ó","Ú","À","È","Ì","Ò","Ù","Â","Ê","Î","Ô","Û","Ã","Õ"
		};
		int syllableLenght = silaba.length();
		for (int i = 0; i < syllableLenght; i++) {
			if (inArray(String.valueOf(silaba.charAt(i)), accentuatedVogals)) {
				return true;
			}
		}

		return false;
    }

    public boolean E_Oxitona(String silaba)
    {
		int syllableLenght = silaba.length();

                if(Tem_Acentuacao(silaba.substring(syllableLenght-3, syllableLenght)))
                    return true;
                else if(Tem_Acentuacao(silaba))
                    return false;

		String lastChar = "";
		String lastLastChar = "";
		if (syllableLenght -1 > 0) {
			lastChar = String.valueOf(silaba.charAt(syllableLenght-1));
		}
		if (syllableLenght -2 > 0) {
			lastLastChar = String.valueOf(silaba.charAt(syllableLenght-2));
		}

		if (inArray(lastChar.toUpperCase(), new String[]{"L","R"}) && E_Vogal(lastLastChar)) {
			return true;
		}

		return (inArray(lastChar.toUpperCase(), new String[]{"U", "I"}) && E_Consoante(lastLastChar));
    }

    public String Singular_Para_Plural(String palavra)
    {
        String ultima = palavra.toLowerCase().substring(palavra.length()-1,palavra.length()), palavra_plural ="";
        String penultima="#", antepenultima = "#";

        if(palavra.length()>1)
            penultima     = palavra.toLowerCase().substring(palavra.length()-2,palavra.length()-1);
        if(palavra.length()>2)
            antepenultima = palavra.toLowerCase().substring(palavra.length()-3,palavra.length()-2);

        /*EXCERÇÕES*/        
        try
        {
//                DBConnection dbCon = new DBConnection();

		Iterator it = dbCon.showRecords("SELECT plural FROM plural_excecoes where palavra = '"+palavra+"';","plural").iterator();

		if(it.hasNext())
   		  palavra_plural = (String) it.next();               
        } 
        catch (Exception e)
        {
		e.printStackTrace();
        }

        if(palavra_plural.compareTo("") != 0)
            return palavra_plural;

        //Caso 1: Substantivos terminados em vogais
        if(E_Vogal(ultima) && E_Consoante(penultima))
            return palavra+"s";

        //Caso 2: Substantivos terminados em ditongo oral
        if(E_Vogal(ultima) && E_Vogal(penultima) && !E_Nasal(ultima) && !E_Nasal(penultima))
            return palavra+"s";

        //Caso 3: Substantivos terminados em n
        if(ultima.compareTo("n") == 0)
            return Normalizar_Palavra(palavra)+"s";

        //Caso 4: Substantivos terminados em ã ou ãe
        if((ultima.compareTo("ã") == 0 && E_Consoante(penultima)) || (penultima.compareTo("ã") ==0 && ultima.compareTo("e") == 0))
            return palavra+"s";

        //Caso 5: Substantivos terminados em r e z
        if(ultima.compareTo("r") == 0 || ultima.compareTo("z") == 0)
            return palavra+"es";

        //Caso 6: Substantivos terminados em al, el, ol, ul
        if((ultima.compareTo("l") == 0) && E_Vogal(penultima) && (penultima.compareTo("i") != 0))
        {
            if(penultima.compareTo("e") == 0)
                return palavra.substring(0,palavra.length()-2)+"éis";
            else if(penultima.compareTo("o") == 0)
                return palavra.substring(0,palavra.length()-2)+"óis";

            return palavra.substring(0,palavra.length()-1)+"is";
        }

        //Caso 7: Substantivos terminados em il
        if(ultima.compareTo("l")  == 0  && penultima.compareTo("i") == 0)
        {
            if(E_Oxitona(palavra))
              return palavra.substring(0,palavra.length()-1)+"s";
            else
              return palavra.substring(0,palavra.length()-2)+"eis";
        }
        //Caso 8: Substantivos terminados em m
        if(ultima.compareTo("m") == 0)
            return palavra.substring(0,palavra.length()-1)+"ns";

        //Caso 9: Substantivos terminados em s
        if(ultima.compareTo("s") == 0)
        {
            if(E_Oxitona(palavra))
               return Normalizar_Palavra(palavra)+"es";
            else
               return palavra;
        }
        //Caso 10: Substantivos terminados em x
        if(ultima.compareTo("x") == 0)
            return palavra;

        //Caso 11: Substantivos terminados em ão
        if(penultima.compareTo("ã") == 0 && ultima.compareTo("o") == 0)
           return palavra.substring(0,palavra.length()-2)+"ões";

        return palavra+"'s";
    }


public String Plural_Para_Singular(String plural,String classe)
{
   String palavra = "", original = plural;
   String palavra_banco, palavra_normalizada;
   
   try
   {
//        DBConnection dbCon = new DBConnection();

	Iterator it = dbCon.showRecords("SELECT palavra FROM plural_excecoes WHERE plural ='"+palavra+"'","palavra").iterator();

	if(it.hasNext())
        {
   	  palavra = (String) it.next();
          return palavra;
        }
   }
   catch (Exception e)
   {
	e.printStackTrace();
   }

   palavra = original;

   if(E_Consoante(palavra.substring(palavra.length()-2,palavra.length()-1)))
      palavra = palavra.substring(0,palavra.length()-1);
   else
      palavra = palavra.substring(0,palavra.length()-2);
 
   //Form1->Consulta_ListBox(Form1->Temporario,"SELECT "+classe+" FROM "+classe+"s WHERE "+classe+" LIKE '"+StringReplace(palavra, "'", "''",TReplaceFlags() << rfReplaceAll)+"%'");
   try
   {
//        DBConnection dbCon = new DBConnection();

	Iterator it = dbCon.showRecords("SELECT "+classe+" FROM "+classe+"s WHERE "+classe+" LIKE '"+palavra+"%'",classe).iterator();

	while(it.hasNext())
        {
              palavra_banco = (String) it.next();
              palavra_normalizada = Normalizar_Palavra(palavra_banco);
              if(palavra_banco.indexOf(palavra)>=0 || palavra_normalizada.indexOf(palavra)>=0)
              {
                if(original.compareTo(Singular_Para_Plural(palavra_banco)) == 0)
                {
                   return palavra_banco;
                }
              }

        }
   }
   catch (Exception e)
   {
	e.printStackTrace();
   }

  return "Erro: Palavra inexistente.";
}

}
