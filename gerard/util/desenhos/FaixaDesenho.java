/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util.desenhos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;



public class FaixaDesenho extends DesenhoDecorator {

    private Desenho desenho;
    private int x1=0;
    private int y=0;
    private int x2=0;
    
    public FaixaDesenho(Desenho desenho, int x1,int x2, int y){
        this.desenho = desenho;
        this.x1=x1;
        this.y=y;
        this.x2=x2;
        
    }

    public void setLocal( int x1,int x2, int y){
        this.x1=x1;
        this.y=y;
        this.x2=x2;
    }
    @Override
    public void draw(Graphics2D g2d){
        this.desenho.draw(g2d);
        int y = (this.y>120?this.y+10:120);
        g2d.setStroke( new BasicStroke( 10.0f ) );
        g2d.setColor(Color.white);
        g2d.drawLine(x1, y, x2,y);
        g2d.setStroke( new BasicStroke(  ) );
        g2d.setColor(Color.BLACK);
    }

    @Override
    public void configurar(int posX, int posY, int QUADRADO, int ESPACO_X, int ESPACO_Y) {
        
    }


    @Override
    public void centralizar(Dimension size) {
     
    }
}
