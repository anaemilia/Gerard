/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.model;

import gerard.util.desenhos.Desenho;
import gerard.util.desenhos.QuadroSelecaoDesenho;
import gerard.util.desenhos.ValorDesenho;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Kecia
 */
public class Configuracao {

    private Desenho desenho;//conjunto de desenhos (Decorator patterns) atual
    private Desenho backup;//salva o desenho anterior
    private Rectangle2D.Double quadradoSelecao;
    private ValorDesenho valorSelecao;
    private int contadorPosicoesSelecao = 0;
    private String jOptionPaneTexto;//mensagens a serem exibidas
    private int passo = 0;
    private int contadorDragDrop = 0;

    /*public Configuracao(Desenho desenho, Desenho backup, Rectangle2D.Double quadradoSelecao,
            ValorDesenho valorSelecao, int contadorPosicoesSelecao, String jOptionPaneTexto,
            int passo, int contadorDragDro) {
        this.desenho = desenho;
        this.backup = backup;
        this.quadradoSelecao = quadradoSelecao;
        this.valorSelecao = valorSelecao;
        this.contadorPosicoesSelecao = contadorPosicoesSelecao;
        this.jOptionPaneTexto = jOptionPaneTexto;
        this.passo = passo;
        this.contadorDragDrop = contadorDragDro;
    }*/

    public Configuracao(Desenho backup, int contadorPosicoesSelecao,
            int passo, int contadorDragDro) {
        this.backup = backup;
        this.contadorPosicoesSelecao = contadorPosicoesSelecao;
        this.passo = passo;
        this.contadorDragDrop = contadorDragDro;
    }
  /*  public Configuracao(Desenho backup,QuadroSelecaoDesenho quadradoSelecao,
            ValorDesenho valorSelecao, int contadorPosicoesSelecao,
            int passo, int contadorDragDro) {
       // this.desenho = desenho;
           this.quadradoSelecao = quadradoSelecao;
        this.valorSelecao = valorSelecao;
        this.backup = backup;
        this.contadorPosicoesSelecao = contadorPosicoesSelecao;
        this.passo = passo;
        this.contadorDragDrop = contadorDragDro;
    }*/

    public Desenho getDesenho() {
        return this.desenho;
    }

    public Desenho getBackup() {
        return this.backup;
    }

    public Rectangle2D.Double getQuadradoSelecao() {
        return this.quadradoSelecao;
    }

    public ValorDesenho getValorSelecao() {
        return this.valorSelecao;
    }

    public int getContadorPosicoesSelecao() {
        return this.contadorPosicoesSelecao;
    }

    public String getJOptionPaneTexto() {
        return this.jOptionPaneTexto;
    }

    public int getPasso() {
        return this.passo;
    }

    public int getContadorDragDrop() {
        return this.contadorDragDrop;
    }
}
