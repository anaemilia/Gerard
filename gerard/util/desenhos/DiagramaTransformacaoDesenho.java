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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author Kecia
 */
public class DiagramaTransformacaoDesenho extends DesenhoDecorator implements ComponenteDoDiagrama {

    private Desenho desenho;
    private Rectangle2D.Double quad1;
    private Ellipse2D.Double quad2;
    private Rectangle2D.Double quad3;
    private int QUADRADO = 60;
    private int ESPACO_Y = 130;
    private int ESPACO_X = 130;
    private int posX = 350;
    private int posY = 350;

    public DiagramaTransformacaoDesenho(Desenho desenho, int x, int y) {
        this.desenho = desenho;
        this.posX = x;
        this.posY = y;
           
    }

    public DiagramaTransformacaoDesenho(Desenho desenho) {
        this.desenho=desenho;
      
    }

    @Override
    public void draw(Graphics2D g2d) {
        this.desenho.draw(g2d);
          g2d.setStroke(new BasicStroke(2.0f));
    ArrayList<Shape> elementos = this.getShapes();
        for(int i=0;i<elementos.size();i++)
            g2d.draw(elementos.get(i));

        //SETA
        g2d.drawLine(posX + QUADRADO , posY + QUADRADO/2+QUADRADO, posX + QUADRADO +ESPACO_X, posY + QUADRADO/2+QUADRADO);
        g2d.drawLine(posX + QUADRADO+ESPACO_X, posY + QUADRADO/2+QUADRADO, posX + QUADRADO+ESPACO_X-8, posY +QUADRADO+ QUADRADO/4 + 8);
        g2d.drawLine(posX + QUADRADO+ESPACO_X, posY + QUADRADO/2+QUADRADO, posX + QUADRADO+ESPACO_X-8, posY +QUADRADO+ 3*QUADRADO/4- 8);
        g2d.setStroke(new BasicStroke());

    }

    public ArrayList<Shape> getShapes() {
          quad1 = new Rectangle2D.Double(posX, posY+QUADRADO, QUADRADO, QUADRADO);
        quad2 = new Ellipse2D.Double(posX+QUADRADO+(ESPACO_X-QUADRADO)/2,posY, QUADRADO, QUADRADO);
        quad3 = new Rectangle2D.Double(posX + QUADRADO + ESPACO_X, posY+QUADRADO, QUADRADO, QUADRADO);
        ArrayList<Shape> array = new ArrayList<Shape>();
        array.add(quad1);
        array.add(quad2);
        array.add(quad3);
        return array;
    }

    public Shape getShapeAt(int cont) {
        return (Shape) getShapes().get(cont);
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

            int h =2*this.QUADRADO;
      w = size.height - DISTANCIA_Y;
        this.posY =  DISTANCIA_Y+(w - h) / 2;
    }
}
