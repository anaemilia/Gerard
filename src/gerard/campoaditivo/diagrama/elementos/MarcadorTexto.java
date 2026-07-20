package gerard.campoaditivo.diagrama.elementos;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class MarcadorTexto {
    public int x;
    public int y;
    public int largura;
    public int altura;
    public String valor;
    public boolean editavel;
    public String chavePapel;

    public MarcadorTexto(int x, int y, int largura, int altura, String valor, boolean editavel, String chavePapel) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.valor = valor;
        this.editavel = editavel;
        this.chavePapel = chavePapel;
    }

    public boolean contem(int mx, int my) {
        // A área de interação é ligeiramente maior que o contorno visível.
        // Isso aplica o mesmo pickup a números e à incógnita, mesmo quando o
        // símbolo ocupa poucos pixels no enunciado.
        int margem = 4;
        return mx >= x - margem && mx <= x + largura + margem &&
               my >= y - margem && my <= y + altura + margem;
    }
}
