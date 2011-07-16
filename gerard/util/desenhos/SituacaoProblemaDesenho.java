/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.util.desenhos;

import gerard.util.SituacaoProblema;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author Kecia
 */
public class SituacaoProblemaDesenho extends Desenho {

    private SituacaoProblema sp;
    private Dimension janelaDeTexto;
    private int posY = 10;
    private int posX = 10;
    private int distanciaYsituacaoProblema;

    public SituacaoProblemaDesenho(Dimension d, int x, int y, SituacaoProblema sp) {
        this.janelaDeTexto = d;
        this.posX = x;
        this.posY = y;
        this.sp = sp;
    }

    public SituacaoProblema getSP() {
        return this.sp;
    }

    public int alturaTexto() {//usado para desenhar a faixa que separa o texto do diagrama
        return this.distanciaYsituacaoProblema;

    }

    public void setLocal(Dimension d, int x, int y) {
        this.janelaDeTexto = d;
        this.posX = x;
        this.posY = y;
    }

    @Override
    public void draw(Graphics2D g2d) {
        Font font = new Font("SanSerif", Font.PLAIN, 18);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics(font);
        int widthString = 0;
        //Dimension dim = this.getSize();
        int widthTela = janelaDeTexto.width;
        String aux = "";
        int posicaoX = this.posX;
        int posicaoY = this.posY;

        String string[] = eliminarEspacosEmBranco(sp.getQuestao().split(" "));

        //Salvar a largura dentro da tela dos componentes da SP
        int numeroComponentes = sp.getNumeroComponentes();

        String ccstring;
        for (int cc = 0; cc < numeroComponentes; cc++) {
            ccstring = sp.getComponenteTextoAt(cc);
            sp.setComponenteWidthAt(cc, fm.stringWidth(ccstring));
        }
        sp.setHeight(fm.getHeight());//salva a atura dos componentes da SP

        //desenha na tela de acordo com a largura widthTela
        for (int i = 0; string[i]!=null; i++) {

            widthString = fm.stringWidth(aux + " " + string[i]);
            if (widthString + 10 > widthTela) {
                posicaoY += fm.getAscent() + fm.getDescent();
                g2d.drawString(aux, posicaoX, posicaoY);
                aux = string[i];
            } else {
                aux += " " + string[i];
            }

            //salva a localização(x,Y) de cada componente da SP desenhada na tela
            for (int cc = 0; cc < numeroComponentes; cc++) {
                if (i == sp.getComponentePosicaoAt(cc)) {
                    String xx = sp.getComponenteTextoAt(cc).substring(0, sp.getComponenteTextoAt(cc).length() - 1);
                    int tirarPonto=tirarPonto(string[i],fm);
                    if (sp.getComponenteTextoAt(cc).length() > 1) {
                        sp.setComponentePointAt(cc, widthString - fm.stringWidth(xx)-tirarPonto, posicaoY + fm.getAscent() + fm.getDescent());
                    } else {
                        sp.setComponentePointAt(cc, widthString-tirarPonto, posicaoY + fm.getAscent() + fm.getDescent());
                    }
                }
            }
        }
        g2d.drawString(aux, posicaoX, posicaoY += fm.getAscent() + fm.getDescent());
        this.distanciaYsituacaoProblema = posicaoY += fm.getAscent() + fm.getDescent();
    }

    @Override
    public void configurar(int posX, int posY, int QUADRADO, int ESPACO_X, int ESPACO_Y) {
    }

    @Override
    public void centralizar(Dimension size) {

    }

    private String[] eliminarEspacosEmBranco(String[] aux2) {
        String aux3[]= new String[aux2.length+1];
        int cont=0;
        for(int i =0; i < aux2.length;i++){
            if(!aux2[i].equalsIgnoreCase(""))
                    aux3[cont++]=aux2[i];

        }
        aux3[cont]=null;
        return aux3;
    }



    private int tirarPonto(String string, FontMetrics fm) {
         if(string.endsWith("."))
            return fm.stringWidth(".");
         if(string.endsWith(","))
             return fm.stringWidth(".");
         if(string.endsWith(";"))
             return fm.stringWidth(";");
         if(string.endsWith(":"))
             return fm.stringWidth(":");
        return 0;
    }
}
