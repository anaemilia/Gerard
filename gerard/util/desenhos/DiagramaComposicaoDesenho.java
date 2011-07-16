/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util.desenhos;

import gerard.util.ComponenteDoDiagrama;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author Kecia
 */
public class DiagramaComposicaoDesenho  extends DesenhoDecorator implements ComponenteDoDiagrama {
    private Desenho desenho;
    private Rectangle2D.Double quad1;
    private Rectangle2D.Double quad2;
    private Rectangle2D.Double quad3;
    ArrayList<Rectangle2D.Double> c;
    private int QUADRADO = 60;
    private int ESPACO_Y = 20;
    private int ESPACO_X = 60;
    private int posX = 360;
    private int posY = 300;

    public DiagramaComposicaoDesenho(Desenho desenho, int x, int y){
        this.desenho = desenho;
        this.posX =x;
        this.posY = y;
    }

    public DiagramaComposicaoDesenho(Desenho desenho) {
        this.desenho=desenho;

    }

    private void desenhaChave(Graphics2D g2d){
        g2d.setFont(new Font("Serif", Font.LAYOUT_LEFT_TO_RIGHT, 2*QUADRADO+3*ESPACO_Y));
        g2d.drawString("}", posX+QUADRADO/2+ESPACO_X/4, posY+QUADRADO+ESPACO_Y+QUADRADO/2+4);
    }


    @Override
    public void draw(Graphics2D g2d) {
        this.desenho.draw(g2d);

       g2d.setStroke(new BasicStroke(2.0f));

        ArrayList<Shape> elementos = this.getShapes();
        for(int i=0;i<elementos.size();i++)
            g2d.draw(elementos.get(i));

        desenhaChave(g2d);
        g2d.setStroke(new BasicStroke());
    }

    @Override
     public ArrayList<Shape> getShapes() {
        quad1 = new Rectangle2D.Double(posX, posY, QUADRADO, QUADRADO);
        quad2 = new Rectangle2D.Double(posX, posY+QUADRADO+ESPACO_Y, QUADRADO, QUADRADO);
        quad3 = new Rectangle2D.Double(posX+QUADRADO+ESPACO_X,posY+QUADRADO+(ESPACO_Y-QUADRADO)/2, QUADRADO, QUADRADO);
        ArrayList<Shape> array = new ArrayList<Shape>();
        array.add(quad1);
        array.add(quad2);
        array.add(quad3);
        return array;
    }
    public Shape getShapeAt(int cont) {
       return (Shape)getShapes().get(cont);
    }

    @Override
    public void configurar(int posX, int posY, int QUADRADO, int ESPACO_X, int ESPACO_Y) {
        this.QUADRADO = (QUADRADO>0?QUADRADO:this.QUADRADO);
        this.ESPACO_Y = (ESPACO_Y>0?ESPACO_Y:this.ESPACO_Y);
        this.ESPACO_X = (ESPACO_X>0?ESPACO_X:this.ESPACO_X);
        this.posX = (posX>0?posX:this.posX);
        this.posY = (posY>0?posY:this.posY);
      
    }


    @Override
    public void centralizar(Dimension size) {
    
               int w = 2*this.QUADRADO+this.ESPACO_X;
            this.posX=(size.width-w)/2;
            int h =2*this.QUADRADO+ESPACO_Y;
        w = size.height - DISTANCIA_Y;
        this.posY =  DISTANCIA_Y+(w - h) / 2;
    }
}
