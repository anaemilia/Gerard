/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util.desenhos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Kecia
 */
public class QuadroSelecaoDesenho extends DesenhoDecorator {

    private Desenho desenho;
    private int posX = 0;
    private int posY = 0;
    private int width;
    private int height;
    private Rectangle2D.Double quad;
    public QuadroSelecaoDesenho(Desenho desenho,int x,int y, int w, int h){
        this.desenho = desenho;
        this.posX =x;
        this.posY = y;
        this.width=w;
        this.height=h;
        quad = new Rectangle2D.Double(posX, posY, width, height);
    }
    public Rectangle2D.Double getQuadroSelecao(){
        
        return quad;
    }

    @Override
    public void draw(Graphics2D g2d) {
        this.desenho.draw(g2d);
        g2d.setStroke( new BasicStroke( 2.0f ) );
        g2d.setColor(Color.blue);
        g2d.draw((Shape) quad);
    }

    @Override
    public void configurar(int posX, int posY, int QUADRADO, int ESPACO_X, int ESPACO_Y) {
       
    }


    @Override
    public void centralizar(Dimension size) {

    }

}
