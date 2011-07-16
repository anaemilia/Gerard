/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util.state;

import gerard.util.ComponenteDoDiagrama;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.DiagramaDivisaoPartesDesenho;
import gerard.util.desenhos.DiagramaMultiplicacaoDivisaoCotasDesenho;
import gerard.util.desenhos.DiagramaTransformacaoDesenho;


public class EstruturasMultiplicativas extends State {

    public EstruturasMultiplicativas(){
        super();
        
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

    @Override
    public String getTipoProblema() {
        return SituacaoProblema.multiplicativa;
    }

}
