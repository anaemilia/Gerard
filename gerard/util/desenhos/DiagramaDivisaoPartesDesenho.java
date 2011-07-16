/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.util.desenhos;

import gerard.util.ComponenteDoDiagrama;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 *
 * @author Kecia
 */
public class DiagramaDivisaoPartesDesenho extends DesenhoDecorator implements ComponenteDoDiagrama {

    private Desenho desenho;
    private Rectangle2D.Double quad1;
    //   private Ellipse2D.Double quad2;
    private Rectangle2D.Double quad2;
    private Rectangle2D.Double quad3;
    //   private Ellipse2D.Double quad5;
    private Rectangle2D.Double quad4;
    private String rotuloEsquerdo = "";
    private String rotuloDireito = "";
    private int QUADRADO = 60;
    private int ESPACO_Y = 100;
    private int ESPACO_X = 50;
    private int posX = 350;
    private int posY = 170;

    public DiagramaDivisaoPartesDesenho(Desenho desenho, String rotuloEsquerdo, String rotuloDireito) {
    
        this.desenho = desenho;
        this.rotuloDireito = rotuloDireito;
        this.rotuloEsquerdo = rotuloEsquerdo;

    }

    public DiagramaDivisaoPartesDesenho(Desenho desenho) {
        this.desenho = desenho;
     

    }

    @Override
    public void draw(Graphics2D g2d) {
        this.desenho.draw(g2d);

        if(!rotuloEsquerdo.equalsIgnoreCase(""))
            g2d.drawString(rotuloEsquerdo, posX, posY-10);
        if(!rotuloDireito.equalsIgnoreCase(""))
            g2d.drawString(rotuloDireito, posX + QUADRADO + ESPACO_X, posY-10);


        g2d.setStroke(new BasicStroke(2.0f));
        ArrayList<Shape> elementos = this.getShapes();
        for (int i = 0; i < elementos.size(); i++) {
            g2d.draw(elementos.get(i));
        }

        //SETA 1
        g2d.drawLine(posX + QUADRADO / 2, posY + QUADRADO, posX + QUADRADO / 2, posY + QUADRADO + ESPACO_Y);
        g2d.drawLine(posX + QUADRADO / 2, posY + QUADRADO + ESPACO_Y, posX + QUADRADO / 4 + 8, posY + QUADRADO + ESPACO_Y - 8);
        g2d.drawLine(posX + QUADRADO / 2, posY + QUADRADO + ESPACO_Y, posX + 3 * QUADRADO / 4 - 8, posY + ESPACO_Y + QUADRADO - 8);
        //SETA 2
        g2d.drawLine(posX + QUADRADO + ESPACO_X + QUADRADO / 2, posY + QUADRADO, posX + QUADRADO + ESPACO_X + QUADRADO / 2, posY + QUADRADO + ESPACO_Y);
        g2d.drawLine(posX + QUADRADO + ESPACO_X + QUADRADO / 2, posY + QUADRADO + ESPACO_Y, posX + QUADRADO + ESPACO_X + QUADRADO / 4 + 8, posY + QUADRADO + ESPACO_Y - 8);
        g2d.drawLine(posX + QUADRADO + ESPACO_X + QUADRADO / 2, posY + QUADRADO + ESPACO_Y, posX + QUADRADO + ESPACO_X + 3 * QUADRADO / 4 - 8, posY + QUADRADO + ESPACO_Y - 8);
        g2d.setStroke(new BasicStroke());
        //DIVISORIA
        g2d.drawLine(posX + QUADRADO + ESPACO_X / 2, posY - 4, posX + QUADRADO + ESPACO_X / 2, +posY + QUADRADO + ESPACO_Y + QUADRADO + 4);
    }

    public ArrayList<Shape> getShapes() {
        quad1 = new Rectangle2D.Double(posX, posY + QUADRADO + ESPACO_Y, QUADRADO, QUADRADO);
        quad2 = new Rectangle2D.Double(posX, posY, QUADRADO, QUADRADO);
        quad3 = new Rectangle2D.Double(posX + QUADRADO + ESPACO_X, posY + QUADRADO + ESPACO_Y, QUADRADO, QUADRADO);
        quad4 = new Rectangle2D.Double(posX + QUADRADO + ESPACO_X, posY, QUADRADO, QUADRADO);
        ArrayList<Shape> array = new ArrayList<Shape>();
        array.add(quad1);
        array.add(quad2);
        array.add(quad3);
        array.add(quad4);
        return array;
    }

    public Shape getShapeAt(int cont) {
        return (Shape) getShapes().get(cont);
    }

    @Override
    public void configurar(int posX, int posY, int QUADRADO, int ESPACO_X, int ESPACO_Y) {
        this.QUADRADO = (QUADRADO > 0 ? QUADRADO : this.QUADRADO);
        this.ESPACO_Y = (ESPACO_Y > 0 ? ESPACO_Y : this.ESPACO_Y);
        this.ESPACO_X = (ESPACO_X > 0 ? ESPACO_X : this.ESPACO_X);
        this.posX = (posX > 0 ? posX : this.posX);
        this.posY = (posY > 0 ? posY : this.posY);
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
