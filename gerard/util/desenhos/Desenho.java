/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util.desenhos;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 *
 * @author Kecia
 */
public abstract class Desenho {
    protected int DISTANCIA_Y;
    public abstract void draw(Graphics2D g2d);
    public abstract void configurar(int posX, int posY, int QUADRADO, int ESPACO_X,int ESPACO_Y);

    public abstract void centralizar(Dimension size);
    public void setDistacia_Y(int y){
        this.DISTANCIA_Y=y;
    }


    
}
