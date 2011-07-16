
package gerard.util.Aplicativo1;
import gerard.banco.BancoSingleton;
import java.util.Iterator;
/**
 *
 * @author Alisson
 */
public class Conjugador {
private BancoSingleton dbCon= BancoSingleton.getInstancia();
public String dividir_string(String s,int inicio,int fim)
{
  if(inicio<0 || fim<inicio)
     return "";
  return s.substring(inicio, fim);
}

public String dividir_string(String s,int fim)
{
  if(fim>s.length() || fim == 0)
     return "";
  return s.substring(s.length()-fim, s.length());
}

public boolean comparar_string(String s1,String s2)
{
    if(s1 != null && s2 != null && (s1.compareTo(s2) == 0))
      return true;
    return false;
}

public boolean comparar_string(String s1,String s2,int fim)
{
    if(s1 != null && s2 != null && (s2.compareTo(dividir_string(s1,fim)) == 0))
      return true;
    return false;
}

public boolean comparar_string(String s1,String s2,int inicio,int fim)
{
    if(inicio<0 || fim<inicio)
      return false;
    
    if(s1 != null && s2 != null && (s2.compareTo(dividir_string(s1,inicio,fim)) == 0))
      return true;
    return false;
}

public void Conjugar_Presente_Indicativo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(comparar_string(terminacao,"ar"))
  {
     //Excerções terminados em 'iar'
    try
    {
//        DBConnection dbCon = new DBConnection();
	Iterator it = dbCon.showRecords("SELECT verbo FROM verbos_especiais WHERE tipo = 'irregular: iar' AND verbo ='"+verbo+"'","verbo").iterator();
	if(it.hasNext())
           radical = dividir_string(radical,0,radical.length()-1)+"e";
    }
    catch (Exception e)
    {
	e.printStackTrace();
    }


     conjugado[0] = radical + "o";
     conjugado[1] = radical + "as";
     conjugado[2] = radical + "a";
     conjugado[3] = radical + "amos";
     conjugado[4] = radical + "ais";
     conjugado[5] = radical + "am";

     //Excerção: Verbos terminados em 'ear'
     if(comparar_string(radical,"e",1))
     {
       conjugado[0] = radical + "io";
       conjugado[1] = radical + "ias";
       conjugado[2] = radical + "ia";
       conjugado[5] = radical + "iam";
     }
     //Excerção: Verbos terminados em 'oiar'
     else if(comparar_string(radical,"oi",2))
     {
       conjugado[0] = radical.substring(0,radical.length()-2) + "óio";
       conjugado[1] = radical.substring(0,radical.length()-2) + "óias";
       conjugado[2] = radical.substring(0,radical.length()-2) + "óia";
       conjugado[5] = radical.substring(0,radical.length()-2) + "óiam";
     }
     //Excerção: Verbos terminados em 'oar'
     else if(comparar_string(radical,"o",1))
     {
       conjugado[0] = radical.substring(0,radical.length()-1) + "ôo";
     }
  }
  else if(comparar_string(terminacao,"er"))
  {
     conjugado[0] = radical + "o";
     conjugado[1] = radical + "es";
     conjugado[2] = radical + "e";
     conjugado[3] = radical + "emos";
     conjugado[4] = radical + "eis";
     conjugado[5] = radical + "em";

     //Excerção: Verbos terminados em 'oer'
     if(comparar_string(radical,"o",1))
     {
       radical = radical.substring(0,radical.length()-1);
       conjugado[0] = radical + "ôo";
       conjugado[1] = radical + "óis";
       conjugado[2] = radical + "ói";
       conjugado[3] = radical + "oemos";
       conjugado[4] = radical + "oeis";
       conjugado[5] = radical + "oem";
     }
  }
  else if(comparar_string(terminacao,"ir"))
  {
     conjugado[0] = radical + "o";
     conjugado[1] = radical + "es";
     conjugado[2] = radical + "e";
     conjugado[3] = radical + "imos";
     conjugado[4] = radical + "is";
     conjugado[5] = radical + "em";

     //Excerção: Verbos terminados em 'air'
     if(comparar_string(radical,"a",1))
     {
       conjugado[0] = radical + "io";
       conjugado[1] = radical + "is";
       conjugado[2] = radical + "i";
       conjugado[3] = radical + "ímos";
       conjugado[4] = radical + "ís";
       conjugado[5] = radical + "em";
     }
     //Exer??o: Verbos terminador em  'uir'
     else if(comparar_string(radical,"u",1) && !comparar_string(radical,"g",radical.length()-2,radical.length()-1) && !comparar_string(radical,"s",radical.length()-2,radical.length()-1))
     {
       radical = dividir_string(radical, 0,radical.length()-1);
       conjugado[0] = radical + "uo";
       conjugado[1] = radical + "óis";
       conjugado[2] = radical + "ói";
       conjugado[3] = radical + "uímos";
       conjugado[4] = radical + "uís";
       conjugado[5] = radical + "oem";
     }
     //Excerção: Verbos terminados em 'zir'
     else if(comparar_string(radical,"z",1))
     {
       conjugado[2] = radical + "";
     }
     //Excerção: Verbos terminados em 'erir'
     else if(comparar_string(radical,"er",2))
     {
       conjugado[0] = radical.substring(0,radical.length()-2) + "iro";
     }
     //Excerção: Verbos terminados em 'erir'
     else if(comparar_string(radical,"ud",2))
     {
       conjugado[1] = radical.substring(0,radical.length()-2) + "odes";
       conjugado[2] = radical.substring(0,radical.length()-2) + "ode";
       conjugado[5] = radical.substring(0,radical.length()-2) + "odem";
     }
     else if(radical.indexOf("o") >= 0)
     {
        if(radical.lastIndexOf("o")>radical.lastIndexOf("a") && radical.lastIndexOf("o")>radical.lastIndexOf("e") && radical.lastIndexOf("o")>radical.lastIndexOf("i") && radical.lastIndexOf("o")>radical.lastIndexOf("u"))
          for(int i=1; i<6; i++)
             conjugado[i] =  radical.substring(0,radical.lastIndexOf("o"))+"u"+conjugado[i].substring(radical.lastIndexOf("o")+1,conjugado[i].length());
     }
     //Excerção: Verbos terminados em 'ir' com e na s�laba anterior
     else if(radical.indexOf("e") >= 0)
     {
        if(radical.lastIndexOf("e")>radical.lastIndexOf("a") && radical.lastIndexOf("e")>radical.lastIndexOf("i") && radical.lastIndexOf("e")>radical.lastIndexOf("o") && radical.lastIndexOf("e")>radical.lastIndexOf("u"))
          for(int i=1; i<6; i++)
             conjugado[i] =  radical.substring(0,radical.lastIndexOf("e"))+"i"+conjugado[i].substring(radical.lastIndexOf("o")+1,conjugado[i].length());
     }
   }
}

public void Conjugar_Preterito_Imperfeito_Indicativo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "ava";
     conjugado[1] = radical + "avas";
     conjugado[2] = radical + "ava";
     conjugado[3] = radical + "ávamos";
     conjugado[4] = radical + "áveis";
     conjugado[5] = radical + "avam";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "ia";
     conjugado[1] = radical + "ias";
     conjugado[2] = radical + "ia";
     conjugado[3] = radical + "íamos";
     conjugado[4] = radical + "íeis";
     conjugado[5] = radical + "iam";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "ia";
     conjugado[1] = radical + "ias";
     conjugado[2] = radical + "ia";
     conjugado[3] = radical + "íamos";
     conjugado[4] = radical + "iéis";
     conjugado[5] = radical + "iam";

     //Excerção: Verbos terminados em 'iar'
     if( comparar_string(radical,"a",radical.length()-1,radical.length()))
     {
       conjugado[0] = radical + "ía";
       conjugado[1] = radical + "ías";
       conjugado[2] = radical + "ía";
       conjugado[3] = radical + "íamos";
       conjugado[4] = radical + "íeis";
       conjugado[5] = radical + "íam";
     }
     else if( comparar_string(radical,"u",radical.length()-1,radical.length()))
     {
       conjugado[0] = radical + "ía";
       conjugado[1] = radical + "ías";
       conjugado[2] = radical + "ia";
       conjugado[3] = radical + "íamos";
       conjugado[4] = radical + "íeis";
       conjugado[5] = radical + "íam";
     }
  }
}



public void Conjugar_Preterito_Perfeito_Indicativo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "ei";
     conjugado[1] = radical + "aste";
     conjugado[2] = radical + "ou";
     conjugado[3] = radical + "amos";
     conjugado[4] = radical + "astes";
     conjugado[5] = radical + "aram";

     //Excerção: Verbos terminados em 'gar'
     if(comparar_string(radical,"g",1))
     {
       conjugado[0] = radical + "uei";
     }
     //Excerção: Verbos terminados em 'car'
     else if(comparar_string(radical,"c",1))
     {
       conjugado[0] = radical.substring(0,radical.length()-1) + "quei";
     }

  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "i";
     conjugado[1] = radical + "este";
     conjugado[2] = radical + "eu";
     conjugado[3] = radical + "emos";
     conjugado[4] = radical + "estes";
     conjugado[5] = radical + "eram";

     //Excerção: Verbos terminados em 'oer'
     if(comparar_string(radical,"o",1))
       conjugado[0] = radical + "í";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "i";
     conjugado[1] = radical + "iste";
     conjugado[2] = radical + "iu";
     conjugado[3] = radical + "imos";
     conjugado[4] = radical + "istes";
     conjugado[5] = radical + "iram";

     //Excerção: Verbos terminados em 'iar'
     if(comparar_string(radical,"a",1) || comparar_string(radical,"u",1))
     {
     conjugado[0] = radical + "í";
     conjugado[1] = radical + "íste";
     conjugado[2] = radical + "iu";
     conjugado[3] = radical + "ímos";
     conjugado[4] = radical + "ístes";
     conjugado[5] = radical + "íram";
     }
  }
}


public void Conjugar_Preterito_Mais_Que_Perfeito_Indicativo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "ara";
     conjugado[1] = radical + "aras";
     conjugado[2] = radical + "ara";
     conjugado[3] = radical + "áramos";
     conjugado[4] = radical + "áreis";
     conjugado[5] = radical + "aram";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "era";
     conjugado[1] = radical + "eras";
     conjugado[2] = radical + "era";
     conjugado[3] = radical + "êramos";
     conjugado[4] = radical + "êreis";
     conjugado[5] = radical + "eram";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "ira";
     conjugado[1] = radical + "iras";
     conjugado[2] = radical + "ira";
     conjugado[3] = radical + "íramos";
     conjugado[4] = radical + "íreis";
     conjugado[5] = radical + "iram";

     //Excerção: Verbos terminados em 'iar'
     if(comparar_string(radical,"a",1) || comparar_string(radical,"u",1))
     {
     conjugado[0] = radical + "íra";
     conjugado[1] = radical + "íras";
     conjugado[2] = radical + "íra";
     conjugado[3] = radical + "íramos";
     conjugado[4] = radical + "íreis";
     conjugado[5] = radical + "íram";
     }
  }
}



public void Conjugar_Futuro_Preterito_Indicativo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "aria";
     conjugado[1] = radical + "arias";
     conjugado[2] = radical + "aria";
     conjugado[3] = radical + "aríamos";
     conjugado[4] = radical + "aríeis";
     conjugado[5] = radical + "ariam";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "eria";
     conjugado[1] = radical + "erias";
     conjugado[2] = radical + "eria";
     conjugado[3] = radical + "eríamos";
     conjugado[4] = radical + "eríeis";
     conjugado[5] = radical + "eriam";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "iria";
     conjugado[1] = radical + "irias";
     conjugado[2] = radical + "iria";
     conjugado[3] = radical + "iríamos";
     conjugado[4] = radical + "iríeis";
     conjugado[5] = radical + "iriam";
  }
}


public void Conjugar_Futuro_Presente_Indicativo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "arei";
     conjugado[1] = radical + "arás";
     conjugado[2] = radical + "ará";
     conjugado[3] = radical + "aremos";
     conjugado[4] = radical + "areis";
     conjugado[5] = radical + "arão";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "erei";
     conjugado[1] = radical + "erás";
     conjugado[2] = radical + "erá";
     conjugado[3] = radical + "eremos";
     conjugado[4] = radical + "ereis";
     conjugado[5] = radical + "erão";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "irei";
     conjugado[1] = radical + "irás";
     conjugado[2] = radical + "irá";
     conjugado[3] = radical + "iremos";
     conjugado[4] = radical + "ireis";
     conjugado[5] = radical + "irão";
  }
}


public void Conjugar_Presente_Subjuntivo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {

    //Excerções terminados em 'iar'
    try
    {
//        DBConnection dbCon = new DBConnection();
	Iterator it = dbCon.showRecords("SELECT verbo FROM verbos_especiais WHERE tipo = 'irregular: iar' AND verbo ='"+verbo+"'","verbo").iterator();
	if(it.hasNext())
           radical = dividir_string(radical,0,radical.length()-1)+"e";
    }
    catch (Exception e)
    {
	e.printStackTrace();
    }

     conjugado[0] = radical + "e";
     conjugado[1] = radical + "es";
     conjugado[2] = radical + "e";
     conjugado[3] = radical + "emos";
     conjugado[4] = radical + "eis";
     conjugado[5] = radical + "em";

     //Excerção: Verbos terminados em 'ear'
     if(comparar_string(radical,"e",1))
     {
       conjugado[0] = radical + "ie";
       conjugado[1] = radical + "ies";
       conjugado[2] = radical + "ie";
       conjugado[5] = radical + "iem";
     }
     //Excerção: Verbos terminados em 'oiar'
     else if(comparar_string(radical,"oi",2))
     {
       conjugado[0] = radical.substring(0,radical.length()-2) + "óie";
       conjugado[1] = radical.substring(0,radical.length()-2) + "óies";
       conjugado[2] = radical.substring(0,radical.length()-2) + "óie";
       conjugado[5] = radical.substring(0,radical.length()-2) + "óiem";
     }
     //Excerção: Verbos terminados em 'gar'
     else if(comparar_string(radical,"g",1))
     {
       conjugado[0] = radical + "ue";
       conjugado[1] = radical + "ues";
       conjugado[2] = radical + "ue";
       conjugado[3] = radical + "uemos";
       conjugado[4] = radical + "ueis";
       conjugado[5] = radical + "uem";
     }
     //Excerção: Verbos terminados em 'car'
     else if(comparar_string(radical,"c",1))
     {
       conjugado[0] = radical.substring(0,radical.length()-1) + "que";
       conjugado[1] = radical.substring(0,radical.length()-1) + "ques";
       conjugado[2] = radical.substring(0,radical.length()-1) + "que";
       conjugado[3] = radical.substring(0,radical.length()-1) + "quemos";
       conjugado[4] = radical.substring(0,radical.length()-1) + "queis";
       conjugado[5] = radical.substring(0,radical.length()-1) + "quem";
     }
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "a";
     conjugado[1] = radical + "as";
     conjugado[2] = radical + "a";
     conjugado[3] = radical + "amos";
     conjugado[4] = radical + "ais";
     conjugado[5] = radical + "am";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "a";
     conjugado[1] = radical + "as";
     conjugado[2] = radical + "a";
     conjugado[3] = radical + "amos";
     conjugado[4] = radical + "ais";
     conjugado[5] = radical + "am";

     //Excerção: Verbos terminados em 'iar'
     if(comparar_string(radical,"a",1))
     {
       conjugado[0] = radical + "ia";
       conjugado[1] = radical + "ias";
       conjugado[2] = radical + "ia";
       conjugado[3] = radical + "iamos";
       conjugado[4] = radical + "iais";
       conjugado[5] = radical + "iam";
     }
     //Excerção: Verbos terminados em 'erir'
     else if(comparar_string(radical,"er",2))
     {
       conjugado[0] = radical.substring(0,radical.length()-2) + "ira";
       conjugado[1] = radical.substring(0,radical.length()-2) + "iras";
       conjugado[2] = radical.substring(0,radical.length()-2) + "ira";
       conjugado[3] = radical.substring(0,radical.length()-2) + "iramos";
       conjugado[4] = radical.substring(0,radical.length()-2) + "irais";
       conjugado[5] = radical.substring(0,radical.length()-2) + "iram";
     }
     else if(radical.indexOf("o") >= 0)
     {
        if(radical.lastIndexOf("o")>radical.lastIndexOf("a") && radical.lastIndexOf("o")>radical.lastIndexOf("e") && radical.lastIndexOf("o")>radical.lastIndexOf("i") && radical.lastIndexOf("o")>radical.lastIndexOf("u"))
          for(int i=1; i<6; i++)
             conjugado[i] =  radical.substring(0,radical.lastIndexOf("o"))+"u"+conjugado[i].substring(radical.lastIndexOf("o")+1,conjugado[i].length());
     }
     //Excerção: Verbos terminados em 'ir' com e na s�laba anterior
     else if(radical.indexOf("e") >= 0)
     {
        if(radical.lastIndexOf("e")>radical.lastIndexOf("a") && radical.lastIndexOf("e")>radical.lastIndexOf("i") && radical.lastIndexOf("e")>radical.lastIndexOf("o") && radical.lastIndexOf("e")>radical.lastIndexOf("u"))
          for(int i=1; i<6; i++)
             conjugado[i] =  radical.substring(0,radical.lastIndexOf("e"))+"i"+conjugado[i].substring(radical.lastIndexOf("o")+1,conjugado[i].length());
     }

     /*
     else if(radical.indexOf("o") >= 0)
     {
        int vogais = 0;
        for(int i=1; (i<radical.length() ) && (vogais == 0); i++)
        {
           if(radical.substring(radical.length()-i,radical.length()-i+1).compareTo("o") == 0)
             vogais = radical.length()-i;
           else if(radical.substring(radical.length()-i,radical.length()-i+1).compareTo("a") == 0 || radical.substring(radical.length()-i,radical.length()-i+1).compareTo("e") == 0 || radical.substring(radical.length()-i,radical.length()-i+1).compareTo("i") == 0 || radical.substring(radical.length()-i,radical.length()-i+1).compareTo("u") == 0)
             vogais = -1;
        }

        if(vogais>0)
          for(int i=0; i<6; i++)
            conjugado[i] =  conjugado[i].substring(0,vogais-1)+"u"+conjugado[i].substring(vogais,conjugado[i].length());
     }
     //Excerção: Verbos terminados em 'ir' com e na s?laba anterior
     else if(radical.indexOf("e") >= 0)
     {
        int vogais = 0;
        for(int i=1; (i<radical.length() ) && (vogais == 0); i++)
        {
           if(radical.substring(radical.length()-i,radical.length()-i+1).compareTo("e") == 0)
             vogais = radical.length()-i;
           else if(radical.substring(radical.length()-i,radical.length()-i+1).compareTo("a") == 0 || radical.substring(radical.length()-i,radical.length()-i+1).compareTo("i") == 0 || radical.substring(radical.length()-i,radical.length()-i+1).compareTo("o") == 0 || radical.substring(radical.length()-i,radical.length()-i+1).compareTo("u") == 0)
             vogais = -1;
        }

        if(vogais>0)
          for(int i=0; i<6; i++)
            conjugado[i] =  conjugado[i].substring(0,vogais-1)+"i"+conjugado[i].substring(vogais,conjugado[i].length());            
     }
    */
  }
}



public void Conjugar_Preterito_Imperfeito_Subjuntivo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "asse";
     conjugado[1] = radical + "asses";
     conjugado[2] = radical + "asse";
     conjugado[3] = radical + "ássemos";
     conjugado[4] = radical + "ásseis";
     conjugado[5] = radical + "assem";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "esse";
     conjugado[1] = radical + "esses";
     conjugado[2] = radical + "esse";
     conjugado[3] = radical + "êssemos";
     conjugado[4] = radical + "êsseis";
     conjugado[5] = radical + "essem";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "isse";
     conjugado[1] = radical + "isses";
     conjugado[2] = radical + "isse";
     conjugado[3] = radical + "íssemos";
     conjugado[4] = radical + "ísseis";
     conjugado[5] = radical + "issem";

     //Excerção: Verbos terminados em 'iar'
     if(comparar_string(radical,"a",1) || comparar_string(radical,"u",1))
     {
       conjugado[0] = radical + "ísse";
       conjugado[1] = radical + "ísses";
       conjugado[2] = radical + "ísse";
       conjugado[3] = radical + "íssemos";
       conjugado[4] = radical + "ísseis";
       conjugado[5] = radical + "íssem";
     }
  }
}

public void Conjugar_Futuro_Imperfeito_Subjuntivo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "ar";
     conjugado[1] = radical + "ares";
     conjugado[2] = radical + "ar";
     conjugado[3] = radical + "armos";
     conjugado[4] = radical + "ardes";
     conjugado[5] = radical + "arem";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "er";
     conjugado[1] = radical + "eres";
     conjugado[2] = radical + "er";
     conjugado[3] = radical + "ermos";
     conjugado[4] = radical + "erdes";
     conjugado[5] = radical + "erem";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "ir";
     conjugado[1] = radical + "ires";
     conjugado[2] = radical + "ir";
     conjugado[3] = radical + "irmos";
     conjugado[4] = radical + "irdes";
     conjugado[5] = radical + "irem";

     //Excerção: Verbos terminados em 'iar'
     if(comparar_string(radical,"a",1) || comparar_string(radical,"u",1))
     {
       conjugado[0] = radical + "ir";
       conjugado[1] = radical + "íres";
       conjugado[2] = radical + "ir";
       conjugado[3] = radical + "irmos";
       conjugado[4] = radical + "irdes";
       conjugado[5] = radical + "írem";
     }
  }
}


public void Conjugar_Imperativo_Negativo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = "--";
     conjugado[1] = radical + "as";
     conjugado[2] = radical + "a";
     conjugado[3] = radical + "amos";
     conjugado[4] = radical + "ais";
     conjugado[5] = radical + "am";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = "--";
     conjugado[1] = radical + "as";
     conjugado[2] = radical + "a";
     conjugado[3] = radical + "amos";
     conjugado[4] = radical + "ais";
     conjugado[5] = radical + "am";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = "--";
     conjugado[1] = radical + "as";
     conjugado[2] = radical + "a";
     conjugado[3] = radical + "amos";
     conjugado[4] = radical + "ais";
     conjugado[5] = radical + "am";

     //Excerção: Verbos terminados em 'iar'
     if(comparar_string(radical,"a",1))
     {
       conjugado[0] = "--";
       conjugado[1] = radical + "ias";
       conjugado[2] = radical + "ia";
       conjugado[3] = radical + "iamos";
       conjugado[4] = radical + "iais";
       conjugado[5] = radical + "iam";
     }
     else if(comparar_string(radical,"u",1))
     {
       conjugado[0] = "--";
       conjugado[1] = radical + "as";
       conjugado[2] = radical + "a";
       conjugado[3] = radical + "amos";
       conjugado[4] = radical + "ís";
       conjugado[5] = radical + "am";
     }
     //Excerção: Verbos terminados em 'erir'
     else if(comparar_string(radical,"er",2))
     {
       conjugado[0] = "--";
       conjugado[1] = radical.substring(0,radical.length()-2) + "iras";
       conjugado[2] = radical.substring(0,radical.length()-2) + "ira";
       conjugado[3] = radical.substring(0,radical.length()-2) + "iramos";
       conjugado[4] = radical.substring(0,radical.length()-2) + "irais";
       conjugado[5] = radical.substring(0,radical.length()-2) + "iram";
     }
     //Excerção: Verbos terminados em 'ir' com 'o' na sílaba anterior
     else if(radical.indexOf("o") >= 0)
     {
        if(radical.lastIndexOf("o")>radical.lastIndexOf("a") && radical.lastIndexOf("o")>radical.lastIndexOf("e") && radical.lastIndexOf("o")>radical.lastIndexOf("i") && radical.lastIndexOf("o")>radical.lastIndexOf("u"))
          for(int i=1; i<6; i++)
             conjugado[i] =  radical.substring(0,radical.lastIndexOf("o"))+"u"+conjugado[i].substring(radical.lastIndexOf("o")+1,conjugado[i].length());
     }
     //Excerção: Verbos terminados em 'ir' com e na s�laba anterior
     else if(radical.indexOf("e") >= 0)
     {
        if(radical.lastIndexOf("e")>radical.lastIndexOf("a") && radical.lastIndexOf("e")>radical.lastIndexOf("i") && radical.lastIndexOf("e")>radical.lastIndexOf("o") && radical.lastIndexOf("e")>radical.lastIndexOf("u"))
          for(int i=1; i<6; i++)
             conjugado[i] =  radical.substring(0,radical.lastIndexOf("e"))+"i"+conjugado[i].substring(radical.lastIndexOf("o")+1,conjugado[i].length());
     }
  }
}



public void Conjugar_Imperativo_Positivo(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = "--";
     conjugado[1] = radical + "a";
     conjugado[2] = radical + "e";
     conjugado[3] = radical + "emos";
     conjugado[4] = radical + "ai";
     conjugado[5] = radical + "em";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = "--";
     conjugado[1] = radical + "e";
     conjugado[2] = radical + "a";
     conjugado[3] = radical + "amos";
     conjugado[4] = radical + "ei";
     conjugado[5] = radical + "am";

     //Excerção: Verbos terminados em 'oer'
     if(comparar_string(radical,"o",1))
     {
       radical = radical.substring(0,radical.length()-1);
       conjugado[1] = radical + "ói";
     }

  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = "--";
     conjugado[1] = radical + "e";
     conjugado[2] = radical + "a";
     conjugado[3] = radical + "amos";
     conjugado[4] = radical + "i";
     conjugado[5] = radical + "am";

     //Excerção: Verbos terminados em 'iar'
     if(comparar_string(radical,"a",1))
     {
       conjugado[0] = "--";
       conjugado[1] = radical + "i";
       conjugado[2] = radical + "ia";
       conjugado[3] = radical + "iamos";
       conjugado[4] = radical + "í";
       conjugado[5] = radical + "iam";
     }
     //Excerção: Verbos terminados em 'uir'
     else if(comparar_string(radical,"u",1))
     {
       radical = radical.substring(1,radical.length()-1);
       conjugado[0] = "--";
       conjugado[1] = radical + "?i";
       conjugado[2] = radical + "ua";
       conjugado[3] = radical + "uamos";
       conjugado[4] = radical + "uí";
       conjugado[5] = radical + "uam";
     }
     //Excerção: Verbos terminados em 'zir'
     else if(comparar_string(radical,"z",1))
     {
       conjugado[1] = radical + "";
     }
     //Excerção: Verbos terminados em 'erir'
     else if(comparar_string(radical,"er",2))
     {
       conjugado[2] = radical.substring(0,radical.length()-2) + "ira";
       conjugado[3] = radical.substring(0,radical.length()-2) + "iramos";
       conjugado[5] = radical.substring(0,radical.length()-2) + "iram";
     }
     //Excerção: Verbos terminados em 'udir'
     else if(comparar_string(radical,"ud",2))
     {
       conjugado[1] = radical.substring(0,radical.length()-2) + "ode";
     }
     else if(radical.indexOf("o") >= 0)
     {
        if(radical.lastIndexOf("o")>radical.lastIndexOf("a") && radical.lastIndexOf("o")>radical.lastIndexOf("e") && radical.lastIndexOf("o")>radical.lastIndexOf("i") && radical.lastIndexOf("o")>radical.lastIndexOf("u"))
          for(int i=1; i<6; i++)
            if(i == 2 || i == 3 || i ==5)
             conjugado[i] =  radical.substring(0,radical.lastIndexOf("o"))+"u"+conjugado[i].substring(radical.lastIndexOf("o")+1,conjugado[i].length());
     }
     //Excerção: Verbos terminados em 'ir' com e na s�laba anterior
     else if(radical.indexOf("e") >= 0)
     {
        if(radical.lastIndexOf("e")>radical.lastIndexOf("a") && radical.lastIndexOf("e")>radical.lastIndexOf("i") && radical.lastIndexOf("e")>radical.lastIndexOf("o") && radical.lastIndexOf("e")>radical.lastIndexOf("u"))
          for(int i=1; i<6; i++)
            if(i == 2 || i == 3 || i ==5)
             conjugado[i] =  radical.substring(0,radical.lastIndexOf("e"))+"i"+conjugado[i].substring(radical.lastIndexOf("o")+1,conjugado[i].length());
     }
  }
}



public void Conjugar_Gerundio(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "ando";
     conjugado[1] = radical + "ando";
     conjugado[2] = radical + "ando";
     conjugado[3] = radical + "ando";
     conjugado[4] = radical + "ando";
     conjugado[5] = radical + "ando";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "endo";
     conjugado[1] = radical + "endo";
     conjugado[2] = radical + "endo";
     conjugado[3] = radical + "endo";
     conjugado[4] = radical + "endo";
     conjugado[5] = radical + "endo";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "indo";
     conjugado[1] = radical + "indo";
     conjugado[2] = radical + "indo";
     conjugado[3] = radical + "indo";
     conjugado[4] = radical + "indo";
     conjugado[5] = radical + "indo";
  }
}

public void Conjugar_Participio(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "ado";
     conjugado[1] = radical + "ado";
     conjugado[2] = radical + "ado";
     conjugado[3] = radical + "ado";
     conjugado[4] = radical + "ado";
     conjugado[5] = radical + "ado";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "ido";
     conjugado[1] = radical + "ido";
     conjugado[2] = radical + "ido";
     conjugado[3] = radical + "ido";
     conjugado[4] = radical + "ido";
     conjugado[5] = radical + "ido";

     //Excerção: Verbos terminados em oer
     if(comparar_string(radical,"o",1))
     {
       conjugado[0] = radical + "ído";
       conjugado[1] = radical + "ído";
       conjugado[2] = radical + "ído";
       conjugado[3] = radical + "ído";
       conjugado[4] = radical + "ído";
       conjugado[5] = radical + "ído";
     }
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "ido";
     conjugado[1] = radical + "ido";
     conjugado[2] = radical + "ido";
     conjugado[3] = radical + "ido";
     conjugado[4] = radical + "ido";
     conjugado[5] = radical + "ido";

     //Excerção: Verbos terminados em 'air' e uir
     if(comparar_string(radical,"a",1) || comparar_string(radical,"u",1))
     {
       conjugado[0] = radical + "ído";
       conjugado[1] = radical + "ído";
       conjugado[2] = radical + "ído";
       conjugado[3] = radical + "ído";
       conjugado[4] = radical + "ído";
       conjugado[5] = radical + "ído";
     }
  }
}

public void Conjugar_Infinitivo_Pessoal(String verbo,String[] conjugado)
{
     conjugado[0] = verbo;
     conjugado[1] = verbo;
     conjugado[2] = verbo;
     conjugado[3] = verbo;
     conjugado[4] = verbo;
     conjugado[5] = verbo;
}

public void Conjugar_Infinitivo_Impessoal(String verbo,String[] conjugado)
{
  String terminacao = dividir_string(verbo,2);
  String radical    = dividir_string(verbo,0,verbo.length()-2);

  if(terminacao.compareTo("ar") == 0)
  {
     conjugado[0] = radical + "ar";
     conjugado[1] = radical + "ares";
     conjugado[2] = radical + "ar";
     conjugado[3] = radical + "armos";
     conjugado[4] = radical + "ardes";
     conjugado[5] = radical + "arem";
  }
  else if(terminacao.compareTo("er") == 0)
  {
     conjugado[0] = radical + "er";
     conjugado[1] = radical + "eres";
     conjugado[2] = radical + "er";
     conjugado[3] = radical + "ermos";
     conjugado[4] = radical + "erdes";
     conjugado[5] = radical + "erem";
  }
  else if(terminacao.compareTo("ir") == 0)
  {
     conjugado[0] = radical + "ir";
     conjugado[1] = radical + "ires";
     conjugado[2] = radical + "ir";
     conjugado[3] = radical + "irmos";
     conjugado[4] = radical + "irdes";
     conjugado[5] = radical + "irem";

     //Excerção: Verbos terminados em 'iar'
     if(comparar_string(radical,"a",1))
     {
       conjugado[0] = radical + "ir";
       conjugado[1] = radical + "íres";
       conjugado[2] = radical + "ir";
       conjugado[3] = radical + "irmos";
       conjugado[4] = radical + "irdes";
       conjugado[5] = radical + "írem";
     }
  }
}


void Corrigir_Irregulares(String[][] conjugado,String verbo)
{
  if(verbo.compareTo("estar") == 0)
  {
    //Presente do indicativo
    conjugado[0][0] = "estou"; conjugado[0][1] = "estás"; conjugado[0][2] = "está"; conjugado[0][3] = "estamos"; conjugado[0][4] = "estais"; conjugado[0][5] = "estão";

    //Pretérito perfeito do indicativo
    conjugado[2][0] = "estive"; conjugado[2][1] = "estivestes"; conjugado[2][2] = "estive"; conjugado[2][3] = "estivemos"; conjugado[2][4] = "estivestes";  conjugado[2][5] = "estiveram";

    //Pretérito mais-que-perfeito do indicativo
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo("estiver",conjugado[3]);
    conjugado[3][3] = "estivéramos"; conjugado[3][4] = "estivéreis";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("estejer",conjugado[6]);

    //Pretérito imperfeito do subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo("estiver",conjugado[7]);
    conjugado[7][3] = "estivéssemos"; conjugado[7][4] = "estivésseis";

    //Futuro  imperfeito do subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo("estiver",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("estejer",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = "está"; conjugado[10][2] = "esteja"; conjugado[10][3] = "estejamos"; conjugado[10][4] = "estai"; conjugado[10][5] = "estejam";
  }
  else if(verbo.compareTo("prazer") == 0)
  {
    conjugado[2][2] = "prouve";   conjugado[2][5] = "prouveram";
    conjugado[3][2] = "prouvera"; conjugado[3][5] = "prouveram";

    conjugado[6][2] = "praza"; conjugado[6][5] = "prazam";
    conjugado[7][2] = "prouvesse"; conjugado[7][5] = "prouvessem";
    conjugado[8][2] = "prouver"; conjugado[8][5] = "prouverem";
    conjugado[9][2] = "praza"; conjugado[9][5] = "prazam";
    conjugado[10][2] = "praza"; conjugado[10][5] = "prazam";

  }
  else if(verbo.compareTo("querer") == 0)
  {
    //Pretérito perfeito
    Conjugar_Preterito_Perfeito_Indicativo("quiser",conjugado[2]);
    conjugado[2][0] = "quis";  conjugado[2][2] = "quis";

    //pretérito mais-que-perfeito
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo("quiser",conjugado[3]);
    conjugado[3][3] = "quiséramos";  conjugado[3][4] = "quiséreis";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("queirer",conjugado[6]);

    //Pretérito imperfeito subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo("quiser",conjugado[7]);
    conjugado[7][3] = "quiséssemos"; conjugado[7][4] = "quisésseis";

    //Futuro imperfeito subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo("quiser",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("queirer",conjugado[9]);

    //Imperativo positivo
    conjugado[10][2] = "queira"; conjugado[10][3] = "queiramos";  conjugado[10][5] = "queiram";
  }
  else if(verbo.compareTo("trazer") == 0)
  {
   //Presente indicativo
    conjugado[0][0] = "trago"; conjugado[0][2] = "traz";

    //Pretérito perfeito do indicativo
    conjugado[2][0] = "trouxe";  conjugado[2][1] = "trouxeste"; conjugado[2][2] = "trouxe"; conjugado[2][3] = "trouxemos"; conjugado[2][4] = "trouxestes";   conjugado[2][5] = "trouxeram";

    //Pretérito mais-que-perfeito do indicativo
    conjugado[3][0] = "trouxera";  conjugado[3][1] = "trouxeras"; conjugado[3][2] = "trouxera"; conjugado[3][3] = "trouxéramos"; conjugado[3][4] = "trouxéreis";   conjugado[3][5] = "trouxeram";

    //Futuro do Pretérito do Indicativo
    Conjugar_Futuro_Preterito_Indicativo("trar",conjugado[4]);

    //Futuro do presente do Indicativo
    Conjugar_Futuro_Presente_Indicativo("trar",conjugado[5]);

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("trager",conjugado[6]);

    //Pretérito imperfeito do subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo("trouxer",conjugado[7]);
    conjugado[7][3] = "trouxéssemos";   conjugado[7][4] = "trouxésseis";

    //Futuro  imperfeito do subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo("trouxer",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("trager",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = "traze"; conjugado[10][2] = "traga"; conjugado[10][3] = "tragamos"; conjugado[10][4] = "trazei"; conjugado[10][5] = "tragam";
  }
  else if( comparar_string(verbo,"azer",4))
  {
   String extra = verbo.substring(0,verbo.length()-4);

    //Presente do indicativo
    conjugado[0][0] = extra+"aço"; conjugado[0][2] = extra+"az";

    //Pretérito perfeito
    Conjugar_Preterito_Perfeito_Indicativo(extra+"izer",conjugado[2]);
    conjugado[2][0] = extra+"iz";  conjugado[2][2] = extra+"ez";

    //pretérito mais-que-perfeito
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo(extra+"izer",conjugado[3]);
    conjugado[3][3] = extra+"izéramos";  conjugado[3][4] = extra+"izéreis";

    //Futuro do Pretérito do Indicativo
    Conjugar_Futuro_Preterito_Indicativo(extra+"ar",conjugado[4]);

    //Futuro do presente do Indicativo
    Conjugar_Futuro_Presente_Indicativo(extra+"ar",conjugado[5]);

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo(extra+"açir",conjugado[6]);

    //Pretérito imperfeito subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo(extra+"izer",conjugado[7]);
    conjugado[7][3] = extra+"izéssemos"; conjugado[7][4] = "izésseis";

    //Futuro imperfeito subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo(extra+"izer",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo(extra+"açir",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][1] = extra+"az"; conjugado[10][2] = extra+"aça"; conjugado[10][3] = extra+"açamos"; conjugado[10][4] = extra+"azei";   conjugado[10][5] = extra+"açam";


  }
  else if(verbo.compareTo("fugir") == 0)
  {
     conjugado[0][1]  = "foges"; conjugado[0][2] = "foge";  conjugado[0][5] = "fogem";
     conjugado[2][2]  = "fugiu";
     conjugado[10][1] = "foge";
  }
  else if(verbo.compareTo("rir") == 0 || verbo.compareTo("sorrir") == 0)
  {
    String extra = verbo.substring(0,verbo.length()-3);
    //Presente do indicativo
    conjugado[0][0]  = extra+"rio";  conjugado[0][1] = extra+"ris";  conjugado[0][2] = extra+"ri"; conjugado[0][3] = extra+"rimos"; conjugado[0][4] = extra+"rides"; conjugado[0][5] = extra+"riem";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo(extra+"riir",conjugado[6]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo(extra+"riir",conjugado[9]);

    //Imperativo positivo
    conjugado[10][1] = extra+"ri"; conjugado[10][2] = extra+"ria"; conjugado[10][3] = extra+"riamos"; conjugado[10][4] = extra+"ride"; conjugado[10][5] = extra+"riam";
  }
  else if(verbo.compareTo("saber") == 0)
  {
     //Presente do indicativo
     conjugado[0][0]  = "sei";

    //Presente mais-que-perfeito do indicativo
    Conjugar_Preterito_Perfeito_Indicativo("souber",conjugado[2]);
    conjugado[2][0] = "soube"; conjugado[2][2] = "soube";

    //Presente mais-que-perfeito do indicativo
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo("souber",conjugado[3]);
    conjugado[3][3] = "soubéramos"; conjugado[3][4] = "soubéreis";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("saiber",conjugado[6]);

    //Pretérito imperfeito subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo("souber",conjugado[7]);
    conjugado[7][3] = "soubéssemos"; conjugado[7][4] = "soubésseis";

    //Futuro imperfeito subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo("souber",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("saiber",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][2] = "saiba"; conjugado[10][3] = "saibamos"; conjugado[10][5] = "saibam";
  }
  else if(verbo.compareTo("haver") == 0)
  {
    //Presente do indicativo
     conjugado[0][0]  = "hei";  conjugado[0][1] = "hás";  conjugado[0][2] = "há"; conjugado[0][5] = "hão";

     //Pretérito perfeito do indicativo
     conjugado[2][0]  = "houve";  conjugado[2][1] = "houveste";  conjugado[2][2] = "houve"; conjugado[2][3] = "houvemos";    conjugado[2][4] = "houvestes";   conjugado[2][5] = "houveram";

    //Presente mais-que-perfeito do indicativo
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo("houver",conjugado[3]);
    conjugado[3][3] = "houvéramos"; conjugado[3][3] = "houvéreis";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("hajir",conjugado[6]);

    //Pretérito imperfeito subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo("houver",conjugado[7]);
    conjugado[7][3] = "houvéssemos"; conjugado[7][4] = "houvésseis";

    //Futuro imperfeito subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo("houver",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("hajir",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][1] = "haja"; conjugado[10][2] = "haja"; conjugado[10][3] = "hajamos"; conjugado[10][4] = "havei"; conjugado[10][5] = "hajam";
  }
  else if(verbo.compareTo("ser") == 0)
  {
    //Presente do indicativo
    conjugado[0][0]  = "sou";  conjugado[0][1] = "és";  conjugado[0][2] = "é"; conjugado[0][3] = "somos"; conjugado[0][4] = "sois"; conjugado[0][5] = "são";

    //Pretérito imperfeito do indicativo
    conjugado[1][0]  = "era";  conjugado[1][1] = "eras";  conjugado[1][2] = "era"; conjugado[1][3] = "éramos";  conjugado[1][4] = "éreis"; conjugado[1][5] = "eram";

    //Pretérito perfeito do indicativo
    conjugado[2][0]  = "fui";  conjugado[2][1] = "foste";  conjugado[2][2] = "foi"; conjugado[2][3] = "fomos";  conjugado[2][4] = "fostes"; conjugado[2][5] = "foram";

    //Pretérito mais-que-perfeito do indicativo
    conjugado[3][0]  = "fora";  conjugado[3][1] = "foras";  conjugado[3][2] = "fora"; conjugado[3][3] = "fôramos";  conjugado[3][4] = "fôreis"; conjugado[3][5] = "foram";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("sejer",conjugado[6]);

    //Pretérito imperfeito subjuntivo
    conjugado[7][0]  = "fosse";  conjugado[7][1] = "fosses";  conjugado[7][2] = "fosse"; conjugado[7][3] = "fôssemos";  conjugado[7][4] = "fôsseis"; conjugado[7][5] = "fossem";

    //Futuro imperfeito subjuntivo
    conjugado[8][0]  = "for";  conjugado[8][1] = "fores";  conjugado[8][2] = "for"; conjugado[8][3] = "formos";  conjugado[8][4] = "fordes"; conjugado[8][5] = "forem";

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("sejer",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][1] = "sê"; conjugado[10][2] = "seja"; conjugado[10][3] = "sejamos"; conjugado[10][4] = "sede"; conjugado[10][5] = "sejam";
  }
  else if(verbo.compareTo("ir") == 0)
  {
   //Presente do indicativo
    conjugado[0][0]  = "vou";  conjugado[0][1] = "vais";  conjugado[0][2] = "vai"; conjugado[0][5] = "vamos";

     //Pretérito perfeito do indicativo
    conjugado[2][0]  = "fui";  conjugado[2][1] = "foste";  conjugado[2][2] = "foi"; conjugado[2][3] = "fomos";    conjugado[2][4] = "fostes";   conjugado[2][5] = "foram";

    //Presente mais-que-perfeito do indicativo
    conjugado[3][0] = "fora"; conjugado[3][1] = "foras";  conjugado[3][2] = "fora";  conjugado[3][3] = "fôramos"; conjugado[3][4] = "fôreis";   conjugado[3][5] = "foram";

    //Presente subjuntivo
    conjugado[6][0] = "vá"; conjugado[6][1] = "vás";  conjugado[6][2] = "vá";  conjugado[6][3] = "vamos"; conjugado[6][4] = "vades";   conjugado[6][5] = "vão";

    //Pretérito imperfeito subjuntivo
    conjugado[7][0] = "fosse"; conjugado[7][1] = "fosses";  conjugado[7][2] = "fosse";  conjugado[7][3] = "fôssemos"; conjugado[7][4] = "fôsseis";   conjugado[7][5] = "fossem";

    //Futuro imperfeito subjuntivo
    conjugado[8][0] = "for"; conjugado[8][1] = "fores";  conjugado[8][2] = "for";  conjugado[8][3] = "formos"; conjugado[8][4] = "fordes";   conjugado[8][5] = "forem";

    //Imperativo negativo
    conjugado[9][0] = "--"; conjugado[9][1] = "vás";  conjugado[9][2] = "vá";  conjugado[9][3] = "vamos"; conjugado[9][4] = "vades";   conjugado[9][5] = "vão";

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = "vai";  conjugado[10][2] = "vá";  conjugado[10][3] = "vamos"; conjugado[10][4] = "ide";   conjugado[10][5] = "vão";
  }
  else if(verbo.compareTo("ler") == 0)
  {
    //Presente do indicativo
    conjugado[0][0]  = "leio";  conjugado[0][1] = "lês";  conjugado[0][2] = "lê";  conjugado[0][2] = "lemos";  conjugado[0][2] = "ledes";  conjugado[0][5] = "lêem";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("leier",conjugado[6]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("leier",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = "lê";  conjugado[10][2] = "leia";  conjugado[10][3] = "leiamos"; conjugado[10][4] = "lede";   conjugado[10][5] = "leiam";

  }
  else if(verbo.compareTo("ouvir") == 0)
  {
    //Presente do indicativo
    conjugado[0][0]  = "ouço";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("ouçer",conjugado[6]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("ouçer",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = "ouve";  conjugado[10][2] = "ouça";  conjugado[10][3] = "ouçamos"; conjugado[10][4] = "ouvi";   conjugado[10][5] = "ouçam";
  }
  else if(verbo.compareTo("perder") == 0)
  {
    //Presente do indicativo
    conjugado[0][0]  = "perco";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("percer",conjugado[6]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("percer",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = "perde";  conjugado[10][2] = "perca";  conjugado[10][3] = "percamos"; conjugado[10][4] = "perdei";   conjugado[10][5] = "percam";
  }
  else if(verbo.compareTo("poder") == 0)
  {
     //Presente do indicativo
    conjugado[0][0]  = "posso";

    //Pretérito perfeito
    Conjugar_Preterito_Perfeito_Indicativo("puder",conjugado[2]);
    conjugado[2][0] = "pude";  conjugado[2][2] = "pôde";

    //pretérito mais-que-perfeito
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo("puder",conjugado[3]);
    conjugado[3][3] = "pudéramos";  conjugado[3][4] = "pudéreis";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("posser",conjugado[6]);

    //Pretérito imperfeito subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo("puder",conjugado[7]);
    conjugado[7][3] = "pudéssemos"; conjugado[7][4] = "pudésseis";

    //Futuro imperfeito subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo("puder",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("posser",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = "--";  conjugado[10][2] = "--";  conjugado[10][3] = "--"; conjugado[10][4] = "--";   conjugado[10][5] = "--";
  }
  else if(comparar_string(verbo,"pôr",3) || comparar_string(verbo,"por",3))
  {
    String extra = verbo.substring(0,verbo.length()-3);

     //Presente do indicativo
    conjugado[0][0]  = extra+"ponho"; conjugado[0][1] = extra+"pões"; conjugado[0][2] = extra+"põe"; conjugado[0][3] = extra+"pomos"; conjugado[0][4] = extra+"ponde"; conjugado[0][5] = extra+"põem";

    //Pretérito imperfeito
    conjugado[1][0]  = extra+"punha"; conjugado[1][1] = extra+"punhas"; conjugado[1][2] = extra+"punha"; conjugado[1][3] = extra+"púnhamos"; conjugado[1][4] = extra+"púnheis"; conjugado[1][5] = extra+"punham";

    //Pretérito perfeito
    conjugado[2][0]  = extra+"pus"; conjugado[2][1] = extra+"puseste"; conjugado[2][2] = extra+"pôs"; conjugado[2][3] = extra+"pusemos"; conjugado[2][4] = extra+"pusestes"; conjugado[2][5] = extra+"puseram";

    //pretérito mais-que-perfeito
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo(extra+"puser",conjugado[3]);
    conjugado[3][3] = extra+"puséramos";  conjugado[3][4] = extra+"puséreis";

    //Futuro do Pretérito do Indicativo
    conjugado[4][0]  = extra+"poria"; conjugado[4][1] = extra+"porias"; conjugado[4][2] = extra+"poria"; conjugado[4][3] = extra+"poríamos"; conjugado[4][4] = extra+"poríeis"; conjugado[4][5] = extra+"poriam";

    //Futuro do presente do Indicativo
    conjugado[5][0]  = extra+"porei"; conjugado[5][1] = extra+"porás"; conjugado[5][2] = extra+"porá"; conjugado[5][3] = extra+"poremos"; conjugado[5][4] = extra+"poreis"; conjugado[5][5] = extra+"porão";

    //Presente subjuntivo
    conjugado[6][0]  = extra+"ponha"; conjugado[6][1] = extra+"ponhas"; conjugado[6][2] = extra+"ponha"; conjugado[6][3] = extra+"ponhamos"; conjugado[6][4] = extra+"ponhais"; conjugado[6][5] = extra+"ponham";

    //Pretérito imperfeito subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo(extra+"puser",conjugado[7]);
    conjugado[7][3] = extra+"puséssemos"; conjugado[7][4] = extra+"pusésseis";

    //Futuro imperfeito subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo(extra+"puser",conjugado[8]);

    //Imperativo negativo
    conjugado[9][0]  = "--"; conjugado[9][1] = extra+"ponhas"; conjugado[9][2] = extra+"ponha"; conjugado[9][3] = extra+"ponhamos"; conjugado[9][4] = extra+"ponhais"; conjugado[9][5] = extra+"ponham";

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = extra+"põe";  conjugado[10][2] = extra+"ponha";  conjugado[10][3] = extra+"ponhamos"; conjugado[10][4] = extra+"ponde";   conjugado[10][5] = extra+"ponham";

    //Gerúndio
    conjugado[11][0] = extra+"pondo"; conjugado[11][1] = extra+"pondo";  conjugado[11][2] = extra+"pondo";  conjugado[11][3] = extra+"pondo"; conjugado[11][4] = extra+"pondo";   conjugado[11][5] = extra+"pondo";

    //Particípio
    conjugado[12][0] = extra+"posto"; conjugado[12][1] = extra+"posto";  conjugado[12][2] = extra+"posto";  conjugado[12][3] = extra+"posto"; conjugado[12][4] = extra+"posto";   conjugado[12][5] = extra+"posto";

    //infinitivo pessoal
    conjugado[13][0] = extra+"por"; conjugado[13][1] = extra+"pores";  conjugado[13][2] = extra+"por";  conjugado[13][3] = extra+"pormos"; conjugado[13][4] = extra+"pordes";   conjugado[13][5] = extra+"porem";

    //infinitivo impessoal
    conjugado[14][0] = verbo; conjugado[14][1] = verbo;  conjugado[14][2] = verbo;  conjugado[14][3] = verbo; conjugado[14][4] = verbo;   conjugado[14][5] = verbo;
  }
  else if(verbo.compareTo("caber") == 0)
  {
    //Presente do indicativo
    conjugado[0][0] = "caibo";

    //Pretérito perfeito
    conjugado[2][0] = "coube"; conjugado[2][1] = "coubeste"; conjugado[2][2] = "coube"; conjugado[2][3] = "coubemos"; conjugado[2][4] = "coubestes"; conjugado[2][5] = "couberam";

    //pretérito mais-que-perfeito
    conjugado[3][0] = "coubera"; conjugado[3][1] = "couberas"; conjugado[3][2] = "coubera"; conjugado[3][3] = "coubéramos"; conjugado[3][4] = "coubéreis"; conjugado[3][5] = "couberam";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("caiber",conjugado[6]);

    //Pretérito imperfeito subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo("couber",conjugado[7]);
    conjugado[7][3] = "coubéssemos"; conjugado[7][4] = "coubésseis";

    //Futuro imperfeito subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo("couber",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("caiber",conjugado[9]);

    //Imperativo afirmativo
    Conjugar_Imperativo_Positivo("caibar",conjugado[10]);
    conjugado[10][1] = "cabe";      conjugado[10][4] = "cabei";
  }
  else if(verbo.compareTo("dar") == 0)
  {
   //Presente do indicativo
    conjugado[0][0] = "dou"; conjugado[0][1] = "dás"; conjugado[0][2] = "dá"; conjugado[0][3] = "damos"; conjugado[0][4] = "dais"; conjugado[0][5] = "dão";

    //Pretérito perfeito do indicativo
    Conjugar_Preterito_Perfeito_Indicativo("der",conjugado[2]);
    conjugado[2][0] = "dei";

    //Presente mais-que-perfeito do indicativo
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo("der",conjugado[3]);
    conjugado[3][3] = "déramos"; conjugado[3][3] = "déreis";

    //Presente subjuntivo
    conjugado[6][0] = "dê"; conjugado[6][1] = "dês"; conjugado[6][2] = "dê"; conjugado[6][5] = "dêem";

    //Pretérito imperfeito do subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo("der",conjugado[7]);

    //Futuro imperfeito do subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo("der",conjugado[8]);

    //Imperativo negativo
    conjugado[9][0] = "--"; conjugado[9][1] = "dês"; conjugado[9][2] = "dê"; conjugado[9][3] = "demos"; conjugado[9][4] = "deis"; conjugado[9][5] = "dêem";

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = "dá"; conjugado[10][2] = "dê"; conjugado[10][3] = "demos"; conjugado[10][4] = "dai"; conjugado[10][5] = "dêem";
  }
  else if(comparar_string(verbo,"crer",4))
  {
    String extra = "";
    if (verbo.compareTo("crer") != 0)
      extra = verbo.substring(0,verbo.length()-4);

    //Presente do indicativo
    conjugado[0][0] = extra+"creio"; conjugado[0][1] = extra+"crês"; conjugado[0][2] = extra+"crê"; conjugado[0][3] = extra+"cremos"; conjugado[0][4] = extra+"credes"; conjugado[0][5] = extra+"crêem";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo(extra+"creiir",conjugado[6]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo(extra+"creiir",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = extra+"crê"; conjugado[10][2] = extra+"creia"; conjugado[10][3] = extra+"creiamos"; conjugado[10][4] = extra+"crede"; conjugado[10][5] = extra+"creiam";
  }
  else if(comparar_string(verbo,"izer",4))
  {
     String extra = verbo.substring(0,verbo.length()-4);

    //Presente do indicativo
    conjugado[0][0] = extra+"igo";   conjugado[0][2] = extra+"iz";

    //Pretérito perfeito do indicativo
    Conjugar_Preterito_Perfeito_Indicativo(extra+"isser",conjugado[2]);
    conjugado[2][0] = extra+"isse";   conjugado[2][2] = extra+"isse";

    //Pretérito mais-que-perfeito do indicativo
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo(extra+"isser",conjugado[3]);
    conjugado[3][3] = extra+"isséramos";   conjugado[3][4] = extra+"isséram";

    //Futuro do Pretérito do Indicativo
    Conjugar_Futuro_Preterito_Indicativo(extra+"ir",conjugado[4]);

    //Futuro do presente do Indicativo
    Conjugar_Futuro_Presente_Indicativo(extra+"ir",conjugado[5]);

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo(extra+"iger",conjugado[6]);

    //Pretérito imperfeito do subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo(extra+"isser",conjugado[7]);
    conjugado[7][3] = extra+"isséssemos";   conjugado[7][4] = extra+"issésseis";

    //Futuro  imperfeito do subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo(extra+"isser",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo(extra+"iger",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = extra+"iz"; conjugado[10][2] = extra+"iga"; conjugado[10][3] = extra+"igamos"; conjugado[10][4] = extra+"izei"; conjugado[10][5] = extra+"igam";
  }
  else if(comparar_string(verbo,"edir",4))
  {
    String extra = verbo.substring(0,verbo.length()-4);

    //Presente do indicativo
    conjugado[0][0] = extra+"eço";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo(extra+"eçer",conjugado[6]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo(extra+"eçer",conjugado[9]);

    //Imperativo positivo
    conjugado[10][0] = "--"; conjugado[10][1] = extra+"ede"; conjugado[10][2] = extra+"eça"; conjugado[10][3] = extra+"eçamos"; conjugado[10][4] = extra+"edi"; conjugado[10][5] = extra+"eçam";
  }
  else if(verbo.compareTo("ver") == 0 || comparar_string(verbo,"ever",4))
  {
    String extra = verbo.substring(0,verbo.length()-3);

   //Presente do indicativo
    conjugado[0][0] = extra+"vejo"; conjugado[0][1] = extra+"vês"; conjugado[0][2] = extra+"vê"; conjugado[0][5] = extra+"vêem";

    //Pretérito perfeito do indicativo
    Conjugar_Preterito_Perfeito_Indicativo(extra+"vir",conjugado[2]);

    //Presente mais-que-perfeito do indicativo
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo(extra+"vir",conjugado[3]);
    conjugado[3][3] = extra+"víramos"; conjugado[3][4] = extra+"víreis";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo(extra+"vejer",conjugado[6]);

    //Pretérito imperfeito do subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo(extra+"vir",conjugado[7]);

    //Futuro imperfeito do subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo(extra+"vir",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo(extra+"vejer",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = extra+"vê"; conjugado[10][2] = extra+"veja"; conjugado[10][3] = extra+"vejamos"; conjugado[10][4] = extra+"vede"; conjugado[10][5] = extra+"vejam";
  }
  else if(comparar_string(verbo,"vir",3))
  {
    String extra = verbo.substring(0,verbo.length()-3);

    //Presente do indicativo
    conjugado[0][0] = extra+"venho"; conjugado[0][1] = extra+"vens"; conjugado[0][2] = extra+"vem"; conjugado[0][3] = extra+"vimos"; conjugado[0][4] = extra+"vindes"; conjugado[0][5] = extra+"vêm";

    //Pretérito imperfeito do indicativo
    conjugado[1][0] = extra+"vinha"; conjugado[1][1] = extra+"vinhas"; conjugado[1][2] = extra+"vinha"; conjugado[1][3] = extra+"vínhamos"; conjugado[1][4] = extra+"vínheis"; conjugado[1][5] = extra+"vinham";

    //Pretérito perfeito do indicativo
    conjugado[2][0] = extra+"vim"; conjugado[2][1] = extra+"vieste"; conjugado[2][2] = extra+"veio"; conjugado[2][3] = extra+"viemos"; conjugado[2][4] = extra+"viestes"; conjugado[2][5] = extra+"vieram";

    //Presente mais-que-perfeito do indicativo
    Conjugar_Preterito_Mais_Que_Perfeito_Indicativo(extra+"vier",conjugado[3]);
    conjugado[3][3] = extra+"viéramos"; conjugado[3][4] = extra+"viéreis";

    //Futuro do Pretérito do Indicativo
    Conjugar_Futuro_Preterito_Indicativo(extra+"vir",conjugado[4]);

    //Futuro do presente do Indicativo
    Conjugar_Futuro_Presente_Indicativo(extra+"vir",conjugado[5]);

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo(extra+"venher",conjugado[6]);

    //Pretérito imperfeito do subjuntivo
    Conjugar_Preterito_Imperfeito_Subjuntivo(extra+"vier",conjugado[7]);
    conjugado[7][3] = extra+"viéssemos"; conjugado[7][4] = extra+"viésseis";

    //Futuro imperfeito do subjuntivo
    Conjugar_Futuro_Imperfeito_Subjuntivo(extra+"vier",conjugado[8]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo(extra+"venher",conjugado[9]);

    //Imperativo afirmativo
    conjugado[10][0] = "--"; conjugado[10][1] = extra+"vem"; conjugado[10][2] = extra+"venha"; conjugado[10][3] = extra+"venhamos"; conjugado[10][4] = extra+"vinde"; conjugado[10][5] = extra+"venham";
  }
  else if(verbo.compareTo("sortir") == 0)
  {
    //Presente do indicativo
    conjugado[0][1] = "surtes"; conjugado[0][2] = "surte"; conjugado[0][5] = "surtem";

    //Imperativo positivo
    conjugado[10][0] = "--"; conjugado[10][1] = "surte"; conjugado[10][2] = "surta"; conjugado[10][3] = "surtamos"; conjugado[10][4] = "sorti"; conjugado[10][5] = "surtam";
  }
  else if(verbo.compareTo("subir") == 0)
  {
    //Presente do indicativo
    conjugado[0][1] = "sobes"; conjugado[0][2] = "sobe"; conjugado[0][5] = "sobem";

    //Imperativo positivo
    conjugado[10][0] = "--"; conjugado[10][1] = "sobe"; conjugado[10][2] = "suba"; conjugado[10][3] = "subamos"; conjugado[10][4] = "subi"; conjugado[10][5] = "subam";
  }
  else if(verbo.compareTo("valer") == 0)
  {
    //Presente do indicativo
    conjugado[0][1] = "valho";

    //Presente subjuntivo
    Conjugar_Presente_Subjuntivo("valher",conjugado[6]);

    //Imperativo negativo
    Conjugar_Imperativo_Negativo("valher",conjugado[9]);

    //Imperativo positivo
    conjugado[10][2] = "valha"; conjugado[10][3] = "valhamos"; conjugado[10][5] = "valham";
  }
  else if(comparar_string(verbo,"ter",3))
  {
    /*Particípios Irregulares*/
    String extra = "";

    try
    {
//        DBConnection dbCon = new DBConnection();
	Iterator it = dbCon.showRecords("SELECT verbo FROM verbos_especiais WHERE tipo = 'irregular: ter' AND verbo ='"+verbo+"'","verbo").iterator();
	if(it.hasNext())
        {
           extra = (String)it.next();
           extra = dividir_string(extra,0,extra.length()-3);
        }
    }
    catch (Exception e)
    {
	e.printStackTrace();
    }
    
         //Presente do indicativo
         conjugado[0][0] = extra+"tenho"; conjugado[0][1] = extra+"tens"; conjugado[0][2] = extra+"tem"; conjugado[0][3] = extra+"temos"; conjugado[0][4] = extra+"tendes"; conjugado[0][5] = extra+"têm";

         //Pret?rito imperfeito do indicativo
         conjugado[1][0] = extra+"tinha"; conjugado[1][1] = extra+"tinhas"; conjugado[1][2] = extra+"tinha"; conjugado[1][3] = extra+"tínhamos"; conjugado[1][4] = extra+"tínheis"; conjugado[1][5] = extra+"tinham";

         //Pret?rito perfeito do indicativo
         conjugado[2][0] = extra+"tive"; conjugado[2][1] = extra+"tiveste"; conjugado[2][2] = extra+"teve"; conjugado[2][3] = extra+"tivemos"; conjugado[2][4] = extra+"tivestes"; conjugado[2][5] = extra+"tiveram";

         //Pretérito mais-que-perfeito do indicativo
         Conjugar_Preterito_Mais_Que_Perfeito_Indicativo(extra+"tiver",conjugado[3]);
         conjugado[3][3] = extra+"tivéramos";   conjugado[3][4] = extra+"tivéreis";

         //Presente do subjuntivo
         conjugado[6][0] = extra+"tenha"; conjugado[6][1] = extra+"tenhas"; conjugado[6][2] = extra+"tenha"; conjugado[6][3] = extra+"tenhamos"; conjugado[6][4] = extra+"tenhais"; conjugado[6][5] = extra+"tenham";

        //Pretérito imperfeito do subjuntivo
        Conjugar_Preterito_Imperfeito_Subjuntivo(extra+"tiver",conjugado[7]);
        conjugado[7][3] = extra+"tivéssemos";   conjugado[7][4] = extra+"tivésseis";

        //Futuro  imperfeito do subjuntivo
        Conjugar_Futuro_Imperfeito_Subjuntivo(extra+"tiver",conjugado[8]);

         //Imperativo negativo
         conjugado[9][0] = "--"; conjugado[9][1] = extra+"tenhas"; conjugado[9][2] = extra+"tenha"; conjugado[9][3] = extra+"tenhamos"; conjugado[9][4] = extra+"tenhais"; conjugado[9][5] = extra+"tenham";

         //Imperativo positivo
         conjugado[10][0] = "--"; conjugado[10][1] = "tem"; conjugado[10][2] = "tenha"; conjugado[10][3] = "tenhamos"; conjugado[10][4] = "tende"; conjugado[10][5] = "tenham";
    
  }
}


void Conjugar_Verbos(String[][] conjugado,String verbo)
{
  Conjugar_Presente_Indicativo(verbo,conjugado[0]);
  Conjugar_Preterito_Imperfeito_Indicativo(verbo,conjugado[1]);
  Conjugar_Preterito_Perfeito_Indicativo(verbo,conjugado[2]);
  Conjugar_Preterito_Mais_Que_Perfeito_Indicativo(verbo,conjugado[3]);
  Conjugar_Futuro_Preterito_Indicativo(verbo,conjugado[4]);
  Conjugar_Futuro_Presente_Indicativo(verbo,conjugado[5]);

  Conjugar_Presente_Subjuntivo(verbo,conjugado[6]);
  Conjugar_Preterito_Imperfeito_Subjuntivo(verbo,conjugado[7]);
  Conjugar_Futuro_Imperfeito_Subjuntivo(verbo,conjugado[8]);

  Conjugar_Imperativo_Negativo(verbo,conjugado[9]);
  Conjugar_Imperativo_Positivo(verbo,conjugado[10]);

  Conjugar_Gerundio(verbo,conjugado[11]);
  Conjugar_Participio(verbo,conjugado[12]);

  Conjugar_Infinitivo_Pessoal(verbo,conjugado[13]);
  Conjugar_Infinitivo_Impessoal(verbo,conjugado[14]);


  //Casos especiais - Correção de acentos e normas ortográficas
  if(comparar_string(verbo,"çar",3) || comparar_string(verbo,"cer",3) || comparar_string(verbo,"cir",3))
  {
   
   for(int i=0; i<15; i++)
     for(int j=0; j<6; j++)
       if(conjugado[i][j].indexOf("çe")>=0 || conjugado[i][j].indexOf("çi")>=0)
       {
          conjugado[i][j] = conjugado[i][j].replace("çe","ce");
          conjugado[i][j] = conjugado[i][j].replace("çi","ci");
       }
  }
  else if(comparar_string(verbo,"guer",4) || comparar_string(verbo,"guir",4))
  {
   for(int i=0; i<15; i++)
   {
     for(int j=0; j<6; j++)
     {
         if(conjugado[i][j].indexOf("egua")>=0  || conjugado[i][j].indexOf("eguo")>=0)
             conjugado[i][j] = conjugado[i][j].replace("egu","ig");
         else if(conjugado[i][j].indexOf("gua")>=0  || conjugado[i][j].indexOf("guo")>=0)
             conjugado[i][j] = conjugado[i][j].replace("gu","g");
     }
   }
  }
  else if(comparar_string(verbo,"ger",3) || comparar_string(verbo,"gir",3))
  {
     for(int i=0; i<15; i++)
       for(int j=0; j<6; j++)
         if(conjugado[i][j].indexOf("ga")>=0  || conjugado[i][j].indexOf("go")>=0)
         {
             conjugado[i][j] = conjugado[i][j].replace("ga","ja");
             conjugado[i][j] = conjugado[i][j].replace("go","jo");
         }
  }
 
 Corrigir_Irregulares(conjugado,verbo);

 
  try
   {
//        DBConnection dbCon = new DBConnection();

        /*Verbos Impessoais*/
	Iterator it = dbCon.showRecords("SELECT verbo FROM verbos_especiais WHERE tipo = 'impessoal' AND verbo ='"+verbo+"'","verbo").iterator();
	if(it.hasNext())
        {
                   for(int i=0; i<15; i++)
                      for(int j=0; j<6; j++)
                        if(j != 2)
                           conjugado[i][j] = "--";

        }

        /*Verbos unipessoais*/
        it = dbCon.showRecords("SELECT verbo FROM verbos_especiais WHERE tipo = 'unipessoal' AND verbo ='"+verbo+"'","verbo").iterator();
        if(it.hasNext())
        {
               for(int i=0; i<15; i++)
                  for(int j=0; j<6; j++)
                    if((j!=2) && (j!=5))
                       conjugado[i][j] = "--";
        }

        /*Verbos Pessoais - Estou retirando somente o presente do subjuntivo e a primeira pessoa do presente indicativo*/
        it = dbCon.showRecords("SELECT verbo FROM verbos_especiais WHERE tipo = 'pessoal' AND verbo ='"+verbo+"'","verbo").iterator();
        if(it.hasNext())
        {
          conjugado[0][0] = "--";
          for(int j=0; j<6; j++)
               conjugado[6][j] = "--";
        }

        /*Particípios irregulares*/
 
        it = dbCon.showRecords("SELECT participio FROM participios_especiais WHERE tipo = 'irregular' AND verbo ='"+verbo+"'","participio").iterator();
        if(it.hasNext())
        {
            String participio = (String)it.next();
            for(int j=0; j<6; j++)
              conjugado[12][j] = participio;
        }

   }
   catch (Exception e)
   {
	e.printStackTrace();
   }
}


String Infinitivo_Verbo(String verbo_conjugado,String[] Tempo_Pessoa)
{
    String[][] conjugado = new String[16][6];
    String resposta = "", palavra_banco;


try
   {
//        DBConnection dbCon = new DBConnection();

        /*Participio irregular*/
	Iterator it = dbCon.showRecords("SELECT verbo FROM participios_especiais WHERE tipo = 'abundante' AND participio ='"+verbo_conjugado+"'","verbo").iterator();
	if(it.hasNext())
            return (String)it.next();

        /*Infinitivo do verbo*/
        if(verbo_conjugado.length() == 0)
            return "";

	it = dbCon.showRecords("SELECT verbo FROM verbos WHERE verbo LIKE '"+dividir_string(verbo_conjugado,0,1)+"%'","verbo").iterator();
	while(it.hasNext())
        {
           palavra_banco = (String)it.next();
           //System.out.println(palavra_banco);
           Conjugar_Verbos(conjugado,palavra_banco);
           for(int i=0; i<15; i++)
           {
               for(int j=0; j<6; j++)
               {
                   if(conjugado[i][j].compareTo(verbo_conjugado) == 0)
                   {
                     resposta       += palavra_banco+",";
                     Tempo_Pessoa[0] = Integer.toString(j) ;
                     Tempo_Pessoa[1] = Integer.toString(i);
                     i = 16;
                     break;
                   }
               }
           }
        }

   }
   catch (Exception e)
   {
	e.printStackTrace();
   }


   if(resposta.length() == 0)
   {
         Conjugar_Verbos(conjugado,"ir");
         for(int i=0; i<15; i++)
         {
           for(int j=0; j<6; j++)
           {
               if(conjugado[i][j].compareTo(verbo_conjugado) == 0)
               {
                 resposta = "ir,";
                 Tempo_Pessoa[0] = Integer.toString(j);
                 Tempo_Pessoa[1] = Integer.toString(i);
                 i = 16;
                 return dividir_string(resposta,0,resposta.length()-1);
               }
           }
         }

         Conjugar_Verbos(conjugado,"ser");
         for(int i=0; i<15; i++)
         {
           for(int j=0; j<6; j++)
           {
               if(conjugado[i][j].compareTo(verbo_conjugado) == 0)
               {
                 resposta = "ir,";
                 Tempo_Pessoa[0] = Integer.toString(j);
                 Tempo_Pessoa[1] = Integer.toString(i);
                 i = 16;
                 return dividir_string(resposta,0,resposta.length()-1);
               }
           }
         }
   }

   return dividir_string(resposta,0,resposta.length()-1);
}

}
