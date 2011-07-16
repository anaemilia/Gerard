
package gerard.util.state;

import gerard.util.ComponenteDaSP;
import gerard.util.ComponenteDoDiagrama;
import gerard.util.ManipularProperties;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.DiagramaDivisaoPartesDesenho;
import gerard.util.desenhos.DiagramaMultiplicacaoDivisaoCotasDesenho;
import java.util.ArrayList;

/**
 *
 * @author Kecia
 */
public class EstruturasMultiplicativasApplet extends State {
 private int numeroProblema;
    private ManipularProperties problemas = new ManipularProperties("gerard.propriedades.problemasApplet");
       public EstruturasMultiplicativasApplet(){
        String numero = this.problemas.getMensagem("NUMERO_MULTIPLICATIVAS");
        this.numeroProblema= Integer.valueOf(numero);
        this.vetorCodProblema=new int[this.numeroProblema];
        for(int i =0;i<this.numeroProblema;i++)
            this.vetorCodProblema[i]=i+1;
    }
     public SituacaoProblema inicializar() {
        this.indexCodProblemaAtual = this.codProblemaRandom();
        this.codProblema = this.vetorCodProblema[this.indexCodProblemaAtual];
        String dado = this.problemas.getMensagem("MULTIPLICATIVAS."+codProblema);
        String campos[] = dado.split("#");
        int numeroComponentes = this.getNumeroComponentesDaSP();
        if(campos.length==5){
            this.sp= new SituacaoProblema(campos[0],Integer.valueOf(campos[1]),campos[2],campos[3],campos[4],numeroComponentes);
            ArrayList<ComponenteDaSP>componentes = new ArrayList<ComponenteDaSP>();
            
            //public ComponenteDaSP(String texto, int posicao,int representacao, String sinal
            for(int i=1;i<=numeroComponentes;i++){
                 dado = this.problemas.getMensagem("COMPONENTE_M."+codProblema+"."+i);
                 campos = dado.split("#");
                 if(campos.length==3){
                ComponenteDaSP comp = new ComponenteDaSP(campos[0],i,Integer.valueOf(campos[1]),campos[2]);
                componentes.add(comp);
                     System.out.println(campos[0]);
                     System.out.println(campos[1]);
                     System.out.println(campos[2]);
                 }else
                     return null;
            }
            this.sp.setComponentes(componentes);
            return this.sp;
         }

         return null;
    }
    @Override
    public String getTipoProblema() {
       return SituacaoProblema.multiplicativa;
    }

    @Override
    public int getNumeroComponentesDaSP() {
       return 4;
    }

    @Override
    public ComponenteDoDiagrama getDiagrama(int diagrama, Desenho desenho) {
         switch (diagrama) {
            case SituacaoProblema.MULTIPLICACAO:
                    return new DiagramaMultiplicacaoDivisaoCotasDesenho(desenho,sp.getRotuloEsquerda(),sp.getRotuloDireita());

                case SituacaoProblema.DIVISAO_PARTES:
                    return new DiagramaDivisaoPartesDesenho(desenho,sp.getRotuloEsquerda(),sp.getRotuloDireita());
            default:
                //SituacaoProblema.DIVISAO_COTAS
                    return new DiagramaMultiplicacaoDivisaoCotasDesenho(desenho,sp.getRotuloEsquerda(),sp.getRotuloDireita());
            }
    }

 
}
