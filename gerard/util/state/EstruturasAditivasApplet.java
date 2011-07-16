

package gerard.util.state;

import gerard.util.ComponenteDaSP;
import gerard.util.ComponenteDoDiagrama;
import gerard.util.ManipularProperties;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.DiagramaComparacaoDesenho;
import gerard.util.desenhos.DiagramaComposicaoDesenho;
import gerard.util.desenhos.DiagramaTransformacaoDesenho;
import java.util.ArrayList;

/**
 *
 * @author Kecia
 */
public class EstruturasAditivasApplet extends State{
    private int numeroProblema;
    private ManipularProperties problemas = new ManipularProperties("gerard.propriedades.problemasApplet");
    public EstruturasAditivasApplet(){
        String numero = this.problemas.getMensagem("NUMERO_ADITIVAS");
        this.numeroProblema= Integer.valueOf(numero);
        this.vetorCodProblema=new int[this.numeroProblema];
        for(int i =0;i<this.numeroProblema;i++)
            this.vetorCodProblema[i]=i+1;
    }
     public SituacaoProblema inicializar() {
        this.indexCodProblemaAtual = this.codProblemaRandom();
        this.codProblema = this.vetorCodProblema[this.indexCodProblemaAtual];
        String dado = this.problemas.getMensagem("ADITIVAS."+codProblema);
        String campos[] = dado.split("#");
        if(campos.length==5){
            this.sp= new SituacaoProblema(campos[0],Integer.valueOf(campos[1]),campos[2],campos[3],campos[4],3);
            ArrayList<ComponenteDaSP>componentes = new ArrayList<ComponenteDaSP>();
            int numeroComponentes = this.getNumeroComponentesDaSP();
            //public ComponenteDaSP(String texto, int posicao,int representacao, String sinal
            for(int i=1;i<=numeroComponentes;i++){
                 dado = this.problemas.getMensagem("COMPONENTE_A."+codProblema+"."+i);
                 campos = dado.split("#");
               if(campos.length==3){
                ComponenteDaSP comp = new ComponenteDaSP(campos[0],i,Integer.valueOf(campos[1]),campos[2]);
                componentes.add(comp);
                }
               else
                   return null;
            }
            this.sp.setComponentes(componentes);
            return this.sp;
         }

         return null;
    }

    @Override
    public String getTipoProblema() {
        return SituacaoProblema.aditiva;
    }

    @Override
    public int getNumeroComponentesDaSP() {
        return 3;
    }

    @Override
    public ComponenteDoDiagrama getDiagrama(int diagrama, Desenho desenho) {
        switch (diagrama) {
                case SituacaoProblema.COMPOSICAO:
                    return new DiagramaComposicaoDesenho(desenho);

                case SituacaoProblema.TRANSFORMACAO:
                    return new DiagramaTransformacaoDesenho(desenho);
            default:
                //SituacaoProblema.COMPARACAO:
                    return new DiagramaComparacaoDesenho(desenho);
            }
    }
    


}
