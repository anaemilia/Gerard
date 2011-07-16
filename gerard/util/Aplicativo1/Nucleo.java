
package gerard.util.Aplicativo1;
import gerard.banco.BancoSingleton;
import gerard.util.ManipularProperties;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Alisson
 */
public class Nucleo {
    private ArrayList obesrverAplicativo1;
    private BancoSingleton dbCon= BancoSingleton.getInstancia();
    private ManipularProperties mensagens = new ManipularProperties("gerard.propriedades.mensagensAplicativo1");

    public String sentenca_atual,palavra_atual,classificacao_atual;
    public String[][] validacao = new String[7][3];
    public String numero_01,numero_02,numero_03;
    public int tipo;

    public Nucleo(){
        this.obesrverAplicativo1= new ArrayList();
    }
    public void registerObserver(Aplicativo1MensagemObserver o) {
        this.obesrverAplicativo1.add(o);
    }
    public void removeObserver(Aplicativo1MensagemObserver o) {
        int i = this.obesrverAplicativo1.indexOf(o);
        if (i >= 0) {
            this.obesrverAplicativo1.remove(i);
        }
    }
    public void notifyExibirMensagemObserver(String mensagem) {
        for (int i = 0; i < this.obesrverAplicativo1.size(); i++) {

            Aplicativo1MensagemObserver jopo = (Aplicativo1MensagemObserver) this.obesrverAplicativo1.get(i);
            jopo.updateMensagem(mensagem);
        }
    }


public void set_sentenca(String sentenca)
{
   this.sentenca_atual = sentenca.toLowerCase().trim();
}

public String get_validado(int i,int j)
{
   return validacao[j][i];
}
public void set_problema(String n1,String n2, String n3, int t)
{
    numero_01 = n1;
    numero_02 = n2;
    numero_03 = n3;
    tipo      = t;
}

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

public boolean Verificar_Equivalencia(String grupo, String p1, String p2)
{

  try
   {
//        DBConnection dbCon = new DBConnection();
        int qtd = 0;
        /*Verbos Impessoais*/
	Iterator it = dbCon.showRecords("SELECT sinonimo FROM sinonimos WHERE palavra = '"+grupo+"' AND sinonimo IN('"+p1+"','"+p2+"')","sinonimo").iterator();
	while(it.hasNext() && qtd<2)
        {
            if(comparar_string(p1,it.next().toString()))
               qtd++;
            else  if(comparar_string(p2,it.next().toString()))
               qtd++;
        }

        if(qtd == 2)
            return true;
        return false;
   }
   catch (Exception e)
   {
	e.printStackTrace();
   }

    return false;
}

public boolean Verificar_Equivalencia_Verbos(String grupo, String p1, String p2)
{

  try
   {
//        DBConnection dbCon = new DBConnection();
        int qtd = 0;
        /*Verbos Impessoais*/
	Iterator it = dbCon.showRecords("SELECT sinonimo FROM sinonimos WHERE palavra IN('"+p1+"','"+p2+"','"+grupo+"')","sinonimo").iterator();
	while(it.hasNext() && qtd<2)
        {
            if(comparar_string(p1,it.next().toString()))
               qtd++;
            else  if(comparar_string(p2,it.next().toString()))
               qtd++;
        }

        if(qtd == 2)
            return true;
        return false;
   }
   catch (Exception e)
   {
	e.printStackTrace();
   }

     return false;
}


void Tipo_Palavra(String Palavra)
{
       String[] tempo_pessoa_valor = new String[3];

       if(E_Pronome_Pessoal(Palavra))
          return;

       if(E_Numero(Palavra))
          return;

       if(E_Preposicao(Palavra))
          return;

       if(E_Substantivo(Palavra))
          return;

       if(E_Adjetivo(Palavra))
          return;

       if(E_Verbo(Palavra,tempo_pessoa_valor))
          return;


       return;
}


public boolean E_Preposicao(String palavra)
{
    try
    {
//        DBConnection dbCon = new DBConnection();
	Iterator it = dbCon.showRecords("SELECT preposicao FROM preposicoes WHERE preposicao = '"+palavra+"'","preposicao").iterator();
	if(it.hasNext())
        {
           this.classificacao_atual = "preposicao";
           return true;
        }
    }
    catch (Exception e)
    {
	e.printStackTrace();
    }

    this.classificacao_atual = "";
    return false;
}

public boolean E_Verbo(String palavra,String[] tempo_pessoa)
{
  String palavra_normalizada;

  Conjugador c        = new Conjugador();
  palavra_normalizada = c.Infinitivo_Verbo(palavra, tempo_pessoa);

  if(!comparar_string(palavra_normalizada,""))
  {
     String pronomes_pessoais[] = {"eu","tu","ele(a)","nós","vós","eles(as)"};
     String tempo_verbal[]      = {"Presente do indicativo","Pretérito imperfeito do indicativo","Pretérito perfeito do indicativo","Pretérito mais-que-perfeito do indicativo","Futuro do pretérito do indicativo","Futuro do presente do indicativo","Presente do subjuntivo","Pretérito imperfeito do subjuntivo","Futuro imperfeito do subjuntivo","Imperativo negativo","Imperativo afirmativo","Gerúndio","Particípio Passado","Infinitivo Pessoal","Infinitivo Impessoal"};

     if(E_Numero(tempo_pessoa[0]) && E_Numero(tempo_pessoa[1]))
     {
       tempo_pessoa[0] = palavra_normalizada+" | "+pronomes_pessoais[Integer.parseInt(tempo_pessoa[0])];
       tempo_pessoa[1] = tempo_verbal[Integer.parseInt(tempo_pessoa[1])];
     }

        try
        {
//            DBConnection dbCon = new DBConnection();
            Iterator it = dbCon.showRecords("SELECT agente || ' # ' || passivo FROM verbos WHERE verbo = '"+palavra_normalizada+"'","agente || ' # ' || passivo").iterator();
            if(it.hasNext())
            {
               tempo_pessoa[2] = it.next().toString();
               this.classificacao_atual = "verbo | "+tempo_pessoa[0]+" | "+tempo_pessoa[1]+" | "+tempo_pessoa[2];
               return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
  }

  this.classificacao_atual = "";
  return false;
}

public boolean E_Adjetivo(String palavra)
{
  String palavra_normalizada;

  //Adjetivo no plural?
  if( comparar_string(palavra,"s",1))
  {
    Plural p = new Plural();
    palavra_normalizada = p.Plural_Para_Singular(palavra,"adjetivo");
  }
  else
  {
    palavra_normalizada = palavra;
  }

        try
        {
//            DBConnection dbCon = new DBConnection();
            Iterator it = dbCon.showRecords("SELECT adjetivo FROM adjetivos WHERE adjetivo = '"+palavra_normalizada+"'","adjetivo").iterator();
            if(it.hasNext())
            {
               this.classificacao_atual = "adjetivo";
               return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
  this.classificacao_atual = "";
   return false;
}

public boolean E_Substantivo(String palavra)
{
  String palavra_normalizada;

  //Substantivo no plural?
  if( comparar_string(palavra,"s",1))
  {
    Plural p = new Plural();
    palavra_normalizada = p.Plural_Para_Singular(palavra,"substantivo");
  }
  else
  {
    palavra_normalizada = palavra;
  }

        try
        {
//            DBConnection dbCon = new DBConnection();
            Iterator it = dbCon.showRecords("SELECT tipo FROM substantivos WHERE substantivo = '"+palavra_normalizada+"'","tipo").iterator();
            if(it.hasNext())
            {
               this.palavra_atual       = palavra_normalizada;
               this.classificacao_atual = "substantivo";

               palavra_normalizada = it.next().toString();
               if(comparar_string(palavra_normalizada, "s."))
                   this.classificacao_atual = "substantivo";
               else if(comparar_string(palavra_normalizada, "s.pr."))
                   this.classificacao_atual = "substantivo_proprio";
               else if(comparar_string(palavra_normalizada, "s.m."))
                   this.classificacao_atual = "substantivo_masculino";
               else if(comparar_string(palavra_normalizada, "s.f."))
                   this.classificacao_atual = "substantivo_feminino";
               return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
  this.classificacao_atual = "";
  return false;
}

public boolean E_Pronome_Pessoal(String palavra)
{
   String pronomes_pessoais[] = {"eu","tu","ele","ela","nós","vós","eles","elas"};

   for(int i=0; i<8; i++)
     if(palavra.compareTo(pronomes_pessoais[i]) == 0)
     {
       this.classificacao_atual = "pronome_pessoal";
       return true;
     }

   this.classificacao_atual = "";
   return false;
}

public boolean E_Numero(String num)
{
    try {
        Integer.parseInt(num);
        this.classificacao_atual = "numero";
        return true;
    } catch (Exception e) {
        this.classificacao_atual = "";
        return false;
    }
}

public void Proxima_Palavra()
{
  if(sentenca_atual.indexOf(" ")>=0)
  {
     palavra_atual  = dividir_string(sentenca_atual,0,sentenca_atual.indexOf(" ")).trim();
     sentenca_atual = dividir_string(sentenca_atual,sentenca_atual.indexOf(" "),sentenca_atual.length()).trim();
     Tipo_Palavra(this.palavra_atual);
  }
  else
  {
     palavra_atual  = sentenca_atual.trim();
     Tipo_Palavra(palavra_atual);
     sentenca_atual = "";
  }

  //JOptionPane.showMessageDialog(null,palavra_atual+": "+classificacao_atual+"\n"+sentenca_atual,"Erro", JOptionPane.OK_OPTION);

}


public boolean Reconhecer_Pergunta_Quanto(String sentenca)
{
  String pergunta;

  if(sentenca.indexOf(".")<0)
  {
     String mensagem = this.mensagens.getMensagem("erro.pontoFinal");
     this.notifyExibirMensagemObserver(mensagem);
     return false;
  }

  if(sentenca.indexOf("?")<0)
  {
     String mensagem = this.mensagens.getMensagem("erro.pontoInterrogacao");
     this.notifyExibirMensagemObserver(mensagem);

     return false;
  }

  if(sentenca.indexOf("quantos")<0  && sentenca.indexOf("quantas")<0)
  {
     String mensagem = this.mensagens.getMensagem("erro.pronomes");
     this.notifyExibirMensagemObserver(mensagem);
     return false;
  }

  if(sentenca.indexOf(".")<0  && sentenca.indexOf("?")<0)
  {
           String mensagem = this.mensagens.getMensagem("erro.posicaoPerguntas");
     this.notifyExibirMensagemObserver(mensagem);
     return false;
  }

  //Para não atrapalhar o parse, juntando ? a palavra
  sentenca = sentenca.replace("?", " ?");
  sentenca = sentenca.replace("quantas", "quantos");

  pergunta  = dividir_string(sentenca,sentenca.indexOf("quantos"),sentenca.indexOf("?")).trim();
  set_sentenca(pergunta);

  Proxima_Palavra();

  if( comparar_string(palavra_atual,"com"))
     Proxima_Palavra();

  if(!comparar_string(palavra_atual,"quantos"))
  {
     String mensagem = this.mensagens.getMensagem("erro.perguntasSemPronome");
     this.notifyExibirMensagemObserver(mensagem);
     return false;
  }


  while(sentenca_atual.length() != 0)
  {
     Proxima_Palavra();

     if(classificacao_atual.indexOf("substantivo")>=0  &&  comparar_string(validacao[2][2],""))
     {
         validacao[2][2] = palavra_atual;
         continue;
     }
     else if(classificacao_atual.indexOf("pronome_pessoal") >= 0 || classificacao_atual.indexOf("substantivo_proprio") >= 0)
     {
        if(comparar_string(validacao[0][2],""))
          validacao[0][2] = palavra_atual.trim();
        else if(tipo == 1)
         validacao[0][2] = validacao[0][2]+" e "+palavra_atual.trim();
        else if(tipo == 3)
         validacao[0][2] = validacao[0][2]+" ("+palavra_atual.trim()+")";
        continue;
     }
     else if(classificacao_atual.indexOf("verbo ") >= 0)
     {
        String dados = classificacao_atual;
        dados        = dividir_string(dados, dados.indexOf("|")+1,dados.length());

        String verbo  = dividir_string(dados,0,dados.indexOf("|")-1).trim();
        dados = dividir_string(dados,dados.indexOf("|")+1,dados.length()).trim();
        String pessoa = dividir_string(dados,0,dados.indexOf("|")-1).trim();
        dados = dividir_string(dados,dados.indexOf("|")+1,dados.length()).trim();
        String tempo  = dividir_string(dados,0,dados.indexOf("|")-1).trim();
        dados = dividir_string(dados,dados.indexOf("|")+1,dados.length()).trim();
        String valor  = dividir_string(dados,0,dados.length()).trim();

        validacao[1][2] = verbo;
        validacao[5][2] = tempo;


        if(tipo == 2)
        {
           validacao[4][2] = valor;
           if(!comparar_string(valor.trim(),"0",0,valor.indexOf(" "))  && comparar_string(validacao[6][2],""))
             validacao[6][2] = "Transformação";
        }

        //Sujeito oculto
        if(comparar_string(validacao[0][2],""))
           validacao[0][2] = pessoa;

        continue;
     }

     //Problemas de comparação e composição
     if(tipo == 3 && (comparar_string(palavra_atual,"mais") || comparar_string(palavra_atual,"menos")))
        validacao[6][2] = "Relação";
     else if(tipo == 1 && (comparar_string(palavra_atual,"todo") || comparar_string(palavra_atual,"junto") || comparar_string(palavra_atual,"juntos")  || comparar_string(palavra_atual,"juntas")  || comparar_string(palavra_atual,"junta") || comparar_string(palavra_atual,"conjunto") || comparar_string(palavra_atual,"total")))
     {
        validacao[6][2] = "Todo";
        validacao[6][0] = "Parte";
        validacao[6][1] = "Parte";
     }
  }
  if(!comparar_string(validacao[0][2],"") && !comparar_string(validacao[1][2], "") && !comparar_string(validacao[2][2], "") && !comparar_string(validacao[5][2],""))
  {
     validacao[3][2] = "?";
     if(tipo == 1)
        validacao[4][2] = "?";
     set_sentenca(dividir_string(sentenca,0,sentenca.indexOf(pergunta)).trim());
     return true;
  }
  else
  {
     this.notifyExibirMensagemObserver("A pergunta está mal formulada.");
     return false;
  }
}



public boolean Reconhecer_Entrada(String sentenca)
{
  sentenca_atual = sentenca.replace(".", " ");
  String palavra_anterior;
  int tupla      = 0;
  int mais = 0;

  if(sentenca.indexOf("mais")>=0)
   mais = 1;

  while(sentenca_atual.length() != 0)
  {
     palavra_anterior = palavra_atual;
     Proxima_Palavra();

     if( comparar_string(classificacao_atual,"pronome_pessoal") || comparar_string(classificacao_atual,"substantivo_proprio"))
     {
        if(comparar_string(validacao[0][tupla],""))
          validacao[0][tupla] = palavra_atual.trim();
        else if(tipo == 1)
          validacao[0][tupla] = validacao[0][tupla]+" e "+palavra_atual.trim();
        else if(tipo == 3)
          validacao[0][tupla] = validacao[0][tupla]+" ("+palavra_atual.trim()+")";
        continue;
     }
     else if(classificacao_atual.indexOf("verbo ")>=0)
     {
        String dados = classificacao_atual;
        dados        = dividir_string(dados, dados.indexOf("|")+1,dados.length());

        String verbo  = dividir_string(dados,0,dados.indexOf("|")-1).trim();
        dados = dividir_string(dados,dados.indexOf("|")+1,dados.length()).trim();
        String pessoa = dividir_string(dados,0,dados.indexOf("|")-1).trim();
        dados = dividir_string(dados,dados.indexOf("|")+1,dados.length()).trim();
        String tempo  = dividir_string(dados,0,dados.indexOf("|")-1).trim();
        dados = dividir_string(dados,dados.indexOf("|")+1,dados.length()).trim();
        String valor  = dividir_string(dados,0,dados.length()).trim();

        if(tipo == 2)
        {
           validacao[4][tupla] = valor;
           if(comparar_string(validacao[0][2],pessoa))
              validacao[0][tupla] = pessoa;
           if(!comparar_string(valor.trim(),"0",0,valor.indexOf(" ")))
              validacao[6][tupla] = "Transformação";
        }

        validacao[1][tupla] = verbo;
        validacao[5][tupla] = tempo;

        //Sujeito oculto
        if(comparar_string(validacao[0][tupla],""))
           validacao[0][tupla] = pessoa;

        continue;
     }
     else if( comparar_string(classificacao_atual,"numero"))
     {
        validacao[3][tupla] = palavra_atual;

        //Problema de transformação não mantém quantidades iniciais e finais
        if(tipo != 2)
          validacao[4][tupla] = palavra_atual;

        //Não teve sujeito
        if(comparar_string(validacao[0][tupla],"") & tupla>0)
           validacao[0][tupla] = validacao[0][tupla-1];

        //Não teve substantivo
        if(comparar_string(validacao[2][tupla],"") & tupla>0)
           validacao[2][tupla] = validacao[2][tupla-1];

        //Não teve o verbo
        if(comparar_string(validacao[1][tupla],"") & tupla>0)
        {
           validacao[1][tupla] = validacao[1][tupla-1];
           validacao[5][tupla] = validacao[5][tupla-1];
        }
        continue;
     }
     else if(classificacao_atual.indexOf("substantivo") >= 0 &&  comparar_string(validacao[2][tupla],"") && !comparar_string(validacao[3][tupla],""))
     {
         validacao[2][tupla] = palavra_atual;

         //Fim da tupla 1
         if(tupla == 0)
           tupla = 1;

         continue;
     }

     if(!comparar_string(validacao[2][tupla],"")  && !comparar_string(validacao[3][tupla],"") && tupla>0)
     {
       validacao[2][tupla] = validacao[2][tupla-1];
     }

     if(tipo == 3 && (comparar_string(palavra_atual,"mais") || comparar_string(palavra_atual,"menos")) && comparar_string(palavra_anterior,"a"))
     {
          if(comparar_string(validacao[2][1],""))
            tupla = 0;

          validacao[6][tupla] = "Relação";

          do{
            Proxima_Palavra();
          }while(!comparar_string(classificacao_atual, "pronome_pessoal") && !comparar_string(classificacao_atual, "substantivo_proprio") && !comparar_string(palavra_atual,""));

          validacao[0][tupla]  = validacao[0][tupla]+" ("+palavra_atual+")";

          if(comparar_string(palavra_atual,validacao[0][2]))
          {
            validacao[6][2] = "Referente";
            if(tupla == 0)
               validacao[6][1] = "Referido";
            else
               validacao[6][0] = "Referido";
          }
          else
          {
            validacao[6][2] = "Referido";
            if(tupla == 0)
               validacao[6][1] = "Referente";
            else
               validacao[6][0] = "Referente";
          }

          tupla++;
     }
     else if(tipo == 1 && (comparar_string(palavra_atual,"todo")  || comparar_string(palavra_atual,"juntos") || comparar_string(palavra_atual,"junto")  ||  comparar_string(palavra_atual,"junta") || comparar_string(palavra_atual,"juntas") || comparar_string(palavra_atual,"conjunto") || comparar_string(palavra_atual,"total")))
     {
        validacao[6][2] = "Parte";
        if(comparar_string(validacao[3][tupla], ""))
        {
           validacao[6][tupla] = "Todo";
           if(tupla == 0)
             validacao[6][1] = "Parte";
           else
             validacao[6][0] = "Parte";
        }
        else
        {
             validacao[6][0] = "Todo";
             validacao[6][1] = "Parte";
        }
     }
  }

  //Comparação
  if(tipo == 3 && comparar_string(validacao[6][2],"Relação"))
  {
     if(validacao[0][2].indexOf(validacao[0][0]) < validacao[0][2].indexOf(validacao[0][1]))
     {
         validacao[6][0] = "Referido";
         validacao[6][1] = "Referente";
     }
     else
     {
         validacao[6][0] = "Referente";
         validacao[6][1] = "Referido";
     }
  }

  //transformação
  if(tipo == 2)
  {
      String valores[][] = new String[3][2];

      valores[0][0] = dividir_string(validacao[4][0],0,validacao[4][0].indexOf("#")).trim();
      valores[0][1] = dividir_string(validacao[4][0],1+validacao[4][0].indexOf("#"),validacao[4][0].length()).trim();
      valores[1][0] = dividir_string(validacao[4][1],0,validacao[4][1].indexOf("#")).trim();
      valores[1][1] = dividir_string(validacao[4][1],1+validacao[4][1].indexOf("#"),validacao[4][1].length()).trim();
      valores[2][0] = dividir_string(validacao[4][2],0,validacao[4][2].indexOf("#")).trim();
      valores[2][1] = dividir_string(validacao[4][2],1+validacao[4][2].indexOf("#"),validacao[4][2].length()).trim();

      if(comparar_string(validacao[6][2],"Transformação"))
      {
         if(comparar_string(valores[2][0],"1"))
         {
            if(Integer.parseInt(validacao[3][1]) > Integer.parseInt(validacao[3][0]))
            {
               validacao[6][0] = "Inicial";
               validacao[6][1] = "Final";
            }
            else
            {
               validacao[6][1] = "Inicial";
               validacao[6][0] = "Final";
            }
         }
         else
         {
            if(Integer.parseInt(validacao[3][1]) > Integer.parseInt(validacao[3][0]))
            {
               validacao[6][0] = "Final";
               validacao[6][1] = "Inicial";
            }
            else
            {
               validacao[6][1] = "Final";
               validacao[6][0] = "Inicial";
            }
         }
      }
      else if(comparar_string(validacao[6][0],"Transformação"))
      {
          if(validacao[5][1].indexOf("imperfeito") >=0 )
          {
               validacao[6][1] = "Inicial";
               validacao[6][2] = "Final";
          }
          else
          {
               validacao[6][1] = "Final";
               validacao[6][2] = "Inicial";
          }
      }
      else if(comparar_string(validacao[6][1],"Transformação"))
      {
          if(validacao[5][0].indexOf("imperfeito")>=0)
          {
               validacao[6][0] = "Inicial";
               validacao[6][2] = "Final";
          }
          else
          {
               validacao[6][0] = "Final";
               validacao[6][2] = "Inicial";
          }
      }
  }

  if(!comparar_string(validacao[0][1],"") && !comparar_string(validacao[1][1],"") && !comparar_string(validacao[2][1],"") && !comparar_string(validacao[5][1],"") && !comparar_string(validacao[3][1],""))
     return true;
  else{

        String mensagem = this.mensagens.getMensagem("erro.malFormulada");
     this.notifyExibirMensagemObserver(mensagem);
     return false;
    }

}


public boolean Validar_Problema_Composicao()
{
  sentenca_atual = sentenca_atual.replaceAll(",", "");
  sentenca_atual = sentenca_atual.replaceAll("  ", " ");

  for(int j=0; j<7; j++)
    for(int i=0; i<3; i++)
      validacao[j][i] = "";

  if(sentenca_atual.indexOf("junto") < 0 && sentenca_atual.indexOf("junta") < 0 && sentenca_atual.indexOf("todo") < 0 && sentenca_atual.indexOf("total")  < 0 && sentenca_atual.indexOf("conjunto") < 0)
  {
     String mensagem = this.mensagens.getMensagem("erro.categoriaComposicao");
     this.notifyExibirMensagemObserver(mensagem);
    return false;
  }

  if(sentenca_atual.indexOf("a mais") >= 0 || sentenca_atual.indexOf("a menos")  >= 0 || sentenca_atual.indexOf("em relação")  >= 0 || sentenca_atual.indexOf("em comparação") >= 0)
  {
    String mensagem = this.mensagens.getMensagem("erro.termosComparativos");
     this.notifyExibirMensagemObserver(mensagem);
    return false;
  }

  if(!Reconhecer_Pergunta_Quanto(sentenca_atual))
    return false;

  if(!Reconhecer_Entrada(sentenca_atual))
    return false;

   int todo = 0, parte1 = 0, parte2 = 0;
   if(comparar_string(validacao[6][0],"Todo"))
   {
       todo   = 0;
       parte1 = 1;
       parte2 = 2;
   }
   else if(comparar_string(validacao[6][1],"Todo"))
   {
       todo   = 1;
       parte1 = 0;
       parte2 = 2;
   }
   else if(comparar_string(validacao[6][2],"Todo"))
   {
       todo   = 2;
       parte1 = 0;
       parte2 = 1;
   }


  if(!comparar_string(numero_01,"?") && !comparar_string(numero_01,validacao[3][parte1]) && !comparar_string(numero_01,validacao[3][parte2]))
  {
       String mensagem = this.mensagens.getMensagem("erro.valoresErrados");
     this.notifyExibirMensagemObserver(mensagem);

       return false;
  }
  else if(!comparar_string(numero_02,"?")  && !comparar_string(numero_02,validacao[3][parte1]) && !comparar_string(numero_02,validacao[3][parte2]))
  {
         String mensagem = this.mensagens.getMensagem("erro.valoresErrados");
     this.notifyExibirMensagemObserver(mensagem);

       return false;
  }
  else if(!comparar_string(numero_03,"?")  &&  !comparar_string(numero_03,validacao[3][todo]))
  {
      String mensagem = this.mensagens.getMensagem("erro.valoresErrados");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
  }


  validacao[3][2] = "0";
  validacao[3][2] = Integer.toString(Math.abs(Integer.parseInt(validacao[3][todo])-Integer.parseInt(validacao[3][parte1])-Integer.parseInt(validacao[3][parte2])));
  validacao[4][2] = validacao[3][2];

  //Valida os tempos verbais
  if(!comparar_string(validacao[5][0],validacao[5][1]) || !comparar_string(validacao[5][0],validacao[5][2]))
  {
         String mensagem = this.mensagens.getMensagem("erro.temposVerbais");
     this.notifyExibirMensagemObserver(mensagem);

       return false;
  }

  //Valida os objetos
  if(!comparar_string(validacao[2][0],validacao[2][1]) || !comparar_string(validacao[2][0],validacao[2][2]))
  {
     if(!Verificar_Equivalencia(validacao[6][2],validacao[6][0],validacao[6][1]))
     {
        String mensagem = this.mensagens.getMensagem("erro.objetosDiferentes");
     this.notifyExibirMensagemObserver(mensagem);

       return false;
     }
  }

  //Valida as ações
  if(!comparar_string(validacao[1][0],validacao[1][1]) || !comparar_string(validacao[1][0],validacao[1][2]))
  {
     if(!Verificar_Equivalencia_Verbos(validacao[6][2],validacao[6][0],validacao[6][1]))
     {
        String mensagem = this.mensagens.getMensagem("erro.verbosSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
     }
  }

 if(comparar_string(validacao[0][todo],validacao[0][parte1])|| comparar_string(validacao[0][todo],validacao[0][parte2]))
      validacao[0][todo] = validacao[0][parte1]+" e "+validacao[0][parte2];

  if(validacao[0][todo].indexOf(" e ") >=0 && comparar_string(validacao[0][parte1],validacao[0][parte2]))
  {
       String mensagem = this.mensagens.getMensagem("erro.pessoasSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);

       return false;
  }
  else if((comparar_string(validacao[0][todo],validacao[0][parte1]) || comparar_string(validacao[0][todo],validacao[0][parte2]))  && !comparar_string(validacao[0][parte1],validacao[0][parte2]))
  {
      String mensagem = this.mensagens.getMensagem("erro.pessoasSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);

       return false;
  }

  //Valida as ações
  if(validacao[0][todo].indexOf(validacao[0][parte1])<0 || validacao[0][todo].indexOf(validacao[0][parte2])<0)
  {
     if( comparar_string(validacao[0][todo],"nós") && comparar_string(validacao[0][parte1],"eu") && comparar_string(validacao[0][parte2],"eu"))
     {
       String mensagem = this.mensagens.getMensagem("erro.pessoasSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
     }
     else if(comparar_string(validacao[0][todo],"eles(as)"))
     {
           if(comparar_string(validacao[0][parte1],"eu")  || comparar_string(validacao[0][parte1],"nós") || comparar_string(validacao[0][parte1],"tu") || comparar_string(validacao[0][parte1],"vós"))
           {
                String mensagem = this.mensagens.getMensagem("erro.pessoasSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);
               return false;
           }
           else if(comparar_string(validacao[0][parte2],"eu")  || comparar_string(validacao[0][parte2],"nós") || comparar_string(validacao[0][parte2],"tu") || comparar_string(validacao[0][parte2],"vós"))
           {
                String mensagem = this.mensagens.getMensagem("erro.pessoasSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);
               return false;
           }
     }
  }

String mensagem = this.mensagens.getMensagem("parabens");
     this.notifyExibirMensagemObserver(mensagem);

  return true;
}


public boolean Validar_Problema_Transformacao()
{
  sentenca_atual = sentenca_atual.replaceAll(",", "");
  sentenca_atual = sentenca_atual.replaceAll("  ", " ");
  String copia   = sentenca_atual;


  copia = numero_02;
  numero_02 = numero_03;
 numero_03 = copia;

  for(int j=0; j<7; j++)
    for(int i=0; i<3; i++)
      validacao[j][i] = "";


  if(sentenca_atual.indexOf("a mais") >= 0 && sentenca_atual.indexOf("a menos") >= 0)
  {
      String mensagem = this.mensagens.getMensagem("erro.termosComparativosComparacaoT");
     this.notifyExibirMensagemObserver(mensagem);

    return false;
  }

  if(sentenca_atual.indexOf("juntos")>= 0 ||  sentenca_atual.indexOf("juntas")>= 0 || sentenca_atual.indexOf("todo")>= 0 || sentenca_atual.indexOf("total")>= 0 || sentenca_atual.indexOf("conjunto")>= 0)
  {
      String mensagem = this.mensagens.getMensagem("erro.termosComposicaoT");
     this.notifyExibirMensagemObserver(mensagem);


    return false;
  }

  if(!Reconhecer_Pergunta_Quanto(sentenca_atual))
    return false;

  if(!Reconhecer_Entrada(sentenca_atual))
    return false;


  //Valida os objetos
  if(!comparar_string(validacao[2][0],validacao[2][1]) || !comparar_string(validacao[2][0],validacao[2][2]))
  {
     if(!Verificar_Equivalencia(validacao[6][2],validacao[6][0],validacao[6][1]))
     {
          String mensagem = this.mensagens.getMensagem("erro.objetosDiferentes");
     this.notifyExibirMensagemObserver(mensagem);

       return false;
     }
  }

  String[] valores = new String[3];

  valores[0] = dividir_string(validacao[4][0],0,validacao[4][0].indexOf("#")).trim();
  valores[1] = dividir_string(validacao[4][1],0,validacao[4][1].indexOf("#")).trim();
  valores[2] = dividir_string(validacao[4][2],0,validacao[4][2].indexOf("#")).trim();

  int inicial=0, fim=0, transformacao=0;

  if(comparar_string(validacao[6][0],"Inicial"))
    inicial = 0;
  else if(comparar_string(validacao[6][1],"Inicial"))
    inicial = 1;
  else if(comparar_string(validacao[6][2],"Inicial"))
    inicial = 2;

  if(comparar_string(validacao[6][0],"Final"))
    fim = 0;
  else if(comparar_string(validacao[6][1],"Final"))
    fim = 1;
  else if(comparar_string(validacao[6][2],"Final"))
    fim = 2;

  if(comparar_string(validacao[6][0],"Transformação"))
    transformacao = 0;
  else if(comparar_string(validacao[6][1],"Transformação"))
    transformacao = 1;
  else if(comparar_string(validacao[6][2],"Transformação"))
    transformacao = 2;


  if(comparar_string(validacao[6][2],"Transformação"))
  {
     if(comparar_string(valores[transformacao],"1"))
       validacao[3][2] = Integer.toString(Integer.parseInt(validacao[3][fim])-Integer.parseInt(validacao[3][inicial]));
     else
       validacao[3][2] = Integer.toString(Integer.parseInt(validacao[3][inicial])-Integer.parseInt(validacao[3][fim]));
  }
  else if(comparar_string(validacao[6][2],"Inicial"))
  {
     if(comparar_string(valores[transformacao],"1"))
       validacao[3][2] = Integer.toString(Integer.parseInt(validacao[3][fim])-Integer.parseInt(validacao[3][transformacao]));
     else
       validacao[3][2] = Integer.toString(Integer.parseInt(validacao[3][fim])+Integer.parseInt(validacao[3][transformacao]));
  }
  else if(comparar_string(validacao[6][2],"Final"))
  {
     if(comparar_string(valores[transformacao],"1"))
       validacao[3][2] = Integer.toString(Integer.parseInt(validacao[3][inicial])+Integer.parseInt(validacao[3][transformacao]));
     else
       validacao[3][2] = Integer.toString(Integer.parseInt(validacao[3][inicial])-Integer.parseInt(validacao[3][transformacao]));
  }


  //Valida as quantidades
  if(!comparar_string(numero_01,"?"))
  {
    if(!comparar_string(validacao[3][inicial],numero_01))
    {
       String mensagem = this.mensagens.getMensagem("erro.valoresErrados");
       this.notifyExibirMensagemObserver(mensagem);
       return false;
    }
  }
  if(!comparar_string(numero_03,"?"))
  {
    if(Integer.parseInt(validacao[3][transformacao]) != Math.abs(Integer.parseInt(numero_03)))
    {
         String mensagem = this.mensagens.getMensagem("erro.valoresErrados");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
    }

    if(comparar_string(validacao[4][transformacao],"1",0,1) && Integer.parseInt(numero_03)<0)
    {
        String mensagem = this.mensagens.getMensagem("erro.valoresErrados");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
    }
  }
  if(!comparar_string(numero_02,"?"))
  {
    if(!comparar_string(validacao[3][fim],numero_02))
    {
        String mensagem = this.mensagens.getMensagem("erro.valoresErrados");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
    }
  }


  //Valida as Pessoas do discursso
  if(!comparar_string(validacao[0][0],validacao[0][1]) || !comparar_string(validacao[0][0],validacao[0][2]))
  {
         String mensagem = this.mensagens.getMensagem("erro.pessoasSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
  }

   String mensagem = this.mensagens.getMensagem("parabens");
     this.notifyExibirMensagemObserver(mensagem);
  return true;
}

public boolean Validar_Problema_Comparacao()
{
  int valor = 1;
  sentenca_atual = sentenca_atual.replaceAll(",", "");
  sentenca_atual = sentenca_atual.replaceAll("  ", " ");
  String copia = sentenca_atual;

  for(int j=0; j<7; j++)
    for(int i=0; i<3; i++)
      validacao[j][i] = "";

  copia = numero_02;
  numero_02 = numero_03;
 numero_03 = copia;

  if(sentenca_atual.indexOf("a mais") < 0 && sentenca_atual.indexOf("a menos") < 0  && sentenca_atual.indexOf("em relação") < 0 && sentenca_atual.indexOf("em comparação") < 0)
  {
       String mensagem = this.mensagens.getMensagem("erro.categoriaComparacao");
     this.notifyExibirMensagemObserver(mensagem);

    return false;
  }

  if(sentenca_atual.indexOf("juntos")>= 0 ||  sentenca_atual.indexOf("juntas")>= 0 || sentenca_atual.indexOf("todo")>= 0 || sentenca_atual.indexOf("total")>= 0 || sentenca_atual.indexOf("conjunto")>= 0)
  {
        String mensagem = this.mensagens.getMensagem("erro.termosComposicaoT");
     this.notifyExibirMensagemObserver(mensagem);
    return false;
  }

  if(sentenca_atual.indexOf("a menos") >= 0)
     valor = -1;


  if(!comparar_string(numero_03,"?"))
  {
     if(Integer.parseInt(numero_03)<0 && sentenca_atual.indexOf("a mais") >= 0)
     {
           String mensagem = this.mensagens.getMensagem("erro.sinal");
     this.notifyExibirMensagemObserver(mensagem);

       return false;
     }
     else if(Integer.parseInt(numero_03)>0 && sentenca_atual.indexOf("a menos") >= 0)
     {
         String mensagem = this.mensagens.getMensagem("erro.sinal");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
     }
  }

  if(!Reconhecer_Pergunta_Quanto(sentenca_atual))
    return false;

  if(!Reconhecer_Entrada(sentenca_atual))
    return false;

  if(!comparar_string(numero_03,"?"))
    numero_03 = Integer.toString(Math.abs(Integer.parseInt(numero_03)));

  if(comparar_string(validacao[6][2],"Relação"))
  {
     if(comparar_string(validacao[6][0],"Referido"))
       validacao[3][2] = Integer.toString((Integer.parseInt(validacao[3][0])-Integer.parseInt(validacao[3][1]))*valor);
     else
       validacao[3][2] = Integer.toString((-Integer.parseInt(validacao[3][0])+Integer.parseInt(validacao[3][1]))*valor);
  }
  else if(comparar_string(validacao[6][2],"Referente"))
  {
     if(comparar_string(validacao[6][0],"Relação"))
       validacao[3][2] = Integer.toString((-valor*Integer.parseInt(validacao[3][0])+Integer.parseInt(validacao[3][1])));
     else
       validacao[3][2] = Integer.toString((Integer.parseInt(validacao[3][0])-valor*Integer.parseInt(validacao[3][1])));

  }
  else if(comparar_string(validacao[6][2],"Referido"))
  {
     if(comparar_string(validacao[6][0],"Relação"))
       validacao[3][2] = Integer.toString((valor*Integer.parseInt(validacao[3][0])+Integer.parseInt(validacao[3][1])));
     else
       validacao[3][2] = Integer.toString((Integer.parseInt(validacao[3][0])+valor*Integer.parseInt(validacao[3][1])));
  }


  validacao[4][0] = validacao[3][0];
  validacao[4][1] = validacao[3][1];
  validacao[4][2] = validacao[3][2];

  int referente = 0, referido = 0, relacao = 0;
  if(comparar_string(validacao[6][0],"Referente"))
    referente = 0;
  else if(comparar_string(validacao[6][1],"Referente"))
    referente = 1;
  else if(comparar_string(validacao[6][2],"Referente"))
    referente = 2;

  if(comparar_string(validacao[6][0],"Referido"))
    referido = 0;
  else if(comparar_string(validacao[6][1],"Referido"))
    referido = 1;
  else if(comparar_string(validacao[6][2],"Referido"))
    referido = 2;

  if(comparar_string(validacao[6][0],"Relação"))
    relacao = 0;
  else if(comparar_string(validacao[6][1],"Relação"))
    relacao = 1;
  else if(comparar_string(validacao[6][2],"Relação"))
    relacao = 2;


  if(!comparar_string(numero_01, "?") && !comparar_string(numero_01, validacao[3][referente]))
  {
      String mensagem = this.mensagens.getMensagem("erro.referente");
     this.notifyExibirMensagemObserver(mensagem);

      return false;
  }

  if(!comparar_string(numero_02, "?") && !comparar_string(numero_02, validacao[3][referido]))
  {
      String mensagem = this.mensagens.getMensagem("erro.referido");
     this.notifyExibirMensagemObserver(mensagem);
      return false;
  }

  if(!comparar_string(numero_03, "?") && !comparar_string(Integer.toString(Math.abs(Integer.parseInt(numero_03))), validacao[3][relacao]))
  {
      String mensagem = this.mensagens.getMensagem("erro.relacaoC");
     this.notifyExibirMensagemObserver(mensagem);
      return false;
  }
  if(Integer.parseInt(validacao[3][2]) <=0 )
  {
        String mensagem = this.mensagens.getMensagem("erro.relacaoReferidoReferente");
     this.notifyExibirMensagemObserver(mensagem);
      return false;
  }


  //Valida os tempos verbais
  if(!comparar_string(validacao[5][0],validacao[5][1]) || !comparar_string(validacao[5][0],validacao[5][2]))
  {
       String mensagem = this.mensagens.getMensagem("erro.temposVerbais");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
  }

  //Valida os objetos
  if(!comparar_string(validacao[2][0],validacao[2][1]) || !comparar_string(validacao[2][0],validacao[2][2]))
  {
     if(!Verificar_Equivalencia(validacao[6][2],validacao[6][0],validacao[6][1]))
     {
       String mensagem = this.mensagens.getMensagem("erro.objetosDiferentes");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
     }
  }

  //Valida as ações
  if(!comparar_string(validacao[1][0],validacao[1][1]) || !comparar_string(validacao[1][0],validacao[1][2]))
  {
     if(!Verificar_Equivalencia_Verbos(validacao[6][2],validacao[6][0],validacao[6][1]))
     {
       String mensagem = this.mensagens.getMensagem("erro.verbosSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
     }
  }

  //Valida as Pessoas do discursso
  if(comparar_string(validacao[6][0],"Relação"))
  {
     if( validacao[0][0].indexOf(validacao[0][1]) < 0 || validacao[0][0].indexOf(validacao[0][2]) < 0)
     {
       String mensagem = this.mensagens.getMensagem("erro.atoresSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
     }
  }
  else if(comparar_string(validacao[6][1],"Relação"))
  {
     if( validacao[0][1].indexOf(validacao[0][0]) < 0 || validacao[0][1].indexOf(validacao[0][2]) < 0)
     {
         String mensagem = this.mensagens.getMensagem("erro.atoresSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
     }
  }
  else if(comparar_string(validacao[6][2],"Relação"))
  {
     if( validacao[0][2].indexOf(validacao[0][0]) < 0 || validacao[0][2].indexOf(validacao[0][1]) < 0)
     {
       String mensagem = this.mensagens.getMensagem("erro.atoresSemEquivalencia");
     this.notifyExibirMensagemObserver(mensagem);
       return false;
     }
  }

  String tes = this.mensagens.getMensagem("parabens");
     this.notifyExibirMensagemObserver(tes);
  return true;
}


}
