/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gerard.util.desenhos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author Kecia
 */
public class ValorDesenho extends DesenhoDecorator {
    private Desenho desenho;
    private String valor;
    private int posX = 0;
    private int posY = 0;
    private int width;
    private int height;
    private Color color;
    
    
    public ValorDesenho(Desenho desenho,String valor, int x,int y, int w, int h, Color color){
        this.desenho = desenho;
        this.valor = valor;
        this.posX =x;
        this.posY = y;
        this.width=w;
        this.height=h;
        this.color=color;
    }
    public void setLocal(int x, int y){
        this.posX=x;
        this.posY=y;
    }

    @Override
    public void draw(Graphics2D g2d) {
        this.desenho.draw(g2d);
        Font font = new Font("SanSerif",Font.PLAIN,18);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2d.setFont(font);
        g2d.setColor(this.color);
        g2d.drawString(valor, posX,posY);
    }

    @Override
    public void configurar(int posX, int posY, int QUADRADO, int ESPACO_X, int ESPACO_Y) {
       
    }

    @Override
    public void centralizar(Dimension size) {
        
    }



}
