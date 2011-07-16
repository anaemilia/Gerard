/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util.state;

import gerard.util.ComponenteDoDiagrama;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.DiagramaComparacaoDesenho;
import gerard.util.desenhos.DiagramaComposicaoDesenho;
import gerard.util.desenhos.DiagramaTransformacaoDesenho;

/**
 *
 * @author Kecia
 */
public class EstruturasAditivas extends State {

    public EstruturasAditivas(){
        super();
    }

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
