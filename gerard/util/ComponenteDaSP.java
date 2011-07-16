/*
 * Esta classe representa os componentes da Situacao Problema
 * Dependendo do tipo da situação-problema, ela poderá ter vários componentes
 * Por exemplo: Uma situação problema do tipo TRANSFORMAÇÃO simples terá três componentes,
 * um correspondente ao valor inicial (numero natural - representado por um quadrado)
 * um correspondente ao valor de transformação (número relativo - representado pelo círculo)
 * um correspondente ao valor final (numero natural - representado por um quadrado)
 *
 */

package gerard.util;

import java.awt.Point;

/**
 *
 * @author Kecia
 */
public class ComponenteDaSP {
    public static final int QUADRADO =0;//Representa o numero natural
    public static final int CIRCULO = 1;//representa o numero relativo

    public static final String quadrado ="QUADRADO";//Representa o numero natural
    public static final String circulo = "CIRCULO";//representa o numero relativo


    private int representacao;
    private String texto;// texto equeivale ao valor do coponente (Ex: uma, 22..)
    private int posicaoNoTexto;// posicao diz respeito à posicao deste componente na situação problema.. (Ex: Maria tem uma bola - o componente "bola" tem posição 3 no texto. ")
    private String sinal;//sinal do numero relativo (Ex: + ou -)
    private int width;// quando o texto for desenhado na tela do computador, é obtido o valor que esse texto ocupa na largura da tela
    private int height;// corresponde a altura do texto quando desenhado na tela do computador
    private Point point;//corresponde ao valor x, y de acordo com a tela em que é desenhado este componente
    
   /* public ComponenteDaSP(String texto, int posicao, int width, int height){
        this.texto = texto;
        this.posicaoNoTexto = posicao;
        this.width=width;
        this.height=height;
        //old
    }
    public ComponenteDaSP(String texto, int posicao) {
        this.texto = texto;
        this.posicaoNoTexto = posicao;
        //old
    }*/


   public ComponenteDaSP(String texto, int posicao,int representacao, String sinal) {
        this.texto = texto;
        this.posicaoNoTexto = posicao;
        this.sinal=(sinal==null?"":sinal);
        this.representacao=representacao;
   }

    public ComponenteDaSP(String texto, int posicao,String representacao, String sinal) {
        this.texto = texto;
        this.posicaoNoTexto = posicao;
        this.sinal=(sinal==null?"":sinal);
        this.representacao=this.getRepresentacao(representacao);
   }

 /*  public ComponenteDaSP(String texto, int posicao,int representacao) {
        this.texto = texto;
        this.posicaoNoTexto = posicao;
        this.sinal="";
        this.representacao=representacao;
   }*/
    public void setWidht(int width){
        this.width=width;
    }
    public void setHeight(int height){
        this.height=height;
    }
    public void setPoint(Point point){
        this.point=point;
    }

    public void setPosicaoNoTexto(int posicao){
        this.posicaoNoTexto=posicao;
    }
    public String getTexto(){
        return this.texto;
    }
    public int getPosicao(){
        return this.posicaoNoTexto;
    }
    public int getWidth(){
        return this.width;
    }
    public Point getPoint(){
        return this.point;
    }
    public int getRepresentacao(){
        return this.representacao;
    }
    public String getSinal(){
        return this.sinal;
    }

       private int getRepresentacao(String representacao){
        if(representacao.equalsIgnoreCase(ComponenteDaSP.quadrado))
            return ComponenteDaSP.QUADRADO;
        if(representacao.equalsIgnoreCase(ComponenteDaSP.circulo))
            return ComponenteDaSP.CIRCULO;
        return 0;

    }

}
