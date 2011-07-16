/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.model;

import gerard.util.DesenharObserver;
import gerard.util.DesfazerRefazerObserver;
import gerard.util.MensagensObserver;
import gerard.util.SituacaoProblema;
import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.FaixaDesenho;
import gerard.util.desenhos.SituacaoProblemaDesenho;
import gerard.util.desenhos.ValorDesenho;
import gerard.util.state.State;
import gerard.util.state.State;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Kecia
 */
public interface GerardModelInterface {
    
    public void registerObserver(MensagensObserver o);
    public void removeObserver(MensagensObserver o);
    public String getJOptionPaneTexto();
  
    public void registerObserver(DesenharObserver o);
    public void removeObserver(DesenharObserver o);
    public Desenho getDesenho();
    
    public void validarDiagrama(int DIAGRAMA);
    public void exibirProximoPasso();
    public void exibirMaisDica();
    public int getProximoPasso();
    public void validarResultado(String text);
    public void validarPosicionamento(int x, int y);
    public void validarSinal(String sinal);

    public SituacaoProblema getSituacaoProblema();
    public void setSP(SituacaoProblema aux);

    public void setDesenho(Desenho desenho);

    public Rectangle2D.Double getQuadradoSelecao();
    public ValorDesenho getValorSelecionado();

    public Point getPontoResultado();

    public void inicializar();

 

    public void verificarSIMeNAO(String string);

    public void setEstado(State estad);

    public void recomecar();

    public void registerObserver(DesfazerRefazerObserver desfazerRefazerObserver);

    public void desfazer();

    public void refazer();

    public void abrir();

    public void salvar();



    public void setGraphics2D(Graphics2D g2d, Dimension size);

    public SituacaoProblemaDesenho getSituacaoProblemaDesenho();

    public FaixaDesenho getFaixaDesenho();

}
