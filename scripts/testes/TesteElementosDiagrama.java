import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.Elemento;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.FragmentoAnotacao;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import gerard.campoaditivo.diagrama.elementos.Seta;
import gerard.campoaditivo.diagrama.modelo.TipoConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Teste de fumaça sem dependências externas para as classes extraídas de Main.
 * Valida construção, detecção geométrica, movimento e renderização headless.
 */
public final class TesteElementosDiagrama {
    private static int verificacoes;

    private static void verificar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        BufferedImage imagem = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        g2.setFont(new Font("Arial", Font.PLAIN, 12));

        Elemento retangulo = new Elemento(10, 10, 60, 40, false);
        verificar(retangulo.contem(20, 20), "Retângulo deve conter ponto interno");
        verificar(!retangulo.contem(100, 100), "Retângulo não deve conter ponto externo");
        retangulo.desenhar(g2);

        Elemento circulo = new Elemento(100, 10, 40, 40, true);
        verificar(circulo.contem(120, 30), "Elipse deve conter o centro");
        circulo.desenhar(g2);

        Seta seta = new Seta(10, 100, 110, 100);
        verificar(seta.contem(60, 104), "Seta deve reconhecer proximidade do segmento");
        seta.mover(5, 5);
        verificar(seta.x1 == 15 && seta.y1 == 105, "Seta deve mover as duas extremidades");
        seta.desenhar(g2);

        CirculoVenn venn = new CirculoVenn(150, 20, 100, 80, "A", 4, true);
        verificar(venn.contem(200, 60), "Círculo de Venn deve conter o centro");
        venn.textoEditavel = "4";
        venn.desenhar(g2);

        QuadradinhoVenn quadrado = new QuadradinhoVenn(280, 30, 24, "parte");
        verificar(quadrado.centroX() == 292 && quadrado.centroY() == 42, "Centro do quadradinho incorreto");
        quadrado.textoEditavel = "7";
        quadrado.desenhar(g2);

        ElementoTextoMovel texto = new ElementoTextoMovel("11", 3);
        texto.x = 20; texto.y = 180;
        texto.atualizarTamanho(g2.getFontMetrics());
        verificar(texto.contem(21, 179), "Texto móvel deve reconhecer sua área");

        FragmentoAnotacao fragmento = new FragmentoAnotacao(null, true);
        verificar(fragmento.texto.length() == 0 && fragmento.negrito, "Fragmento deve normalizar texto nulo");

        MarcadorTexto marcador = new MarcadorTexto(20, 210, 40, 30, "?", true, "todo");
        verificar(marcador.contem(30, 220), "Marcador deve conter ponto interno");

        ItemTextoArrastavel item = new ItemTextoArrastavel(80, 220, 50, 36, "4", true, "texto", "parte1");
        verificar(item.estaNoDiagrama(), "Item abaixo de y=210 deve estar no diagrama");
        item.desenhar(g2);

        Rectangle limite = new Rectangle(0, 0, 400, 300);
        ElementoVergnaud ev = new ElementoVergnaud(160, 150, 60, 50,
                TipoFiguraDiagrama.RETANGULO, "estado", limite, false);
        verificar(ev.contem(170, 160), "Elemento de Vergnaud deve reconhecer ponto interno");
        ev.moverPara(500, 500, limite);
        verificar(ev.x <= 340 && ev.y <= 250, "Movimento deve respeitar limites");
        ev.textoEditavel = "11";
        ev.desenhar(g2);

        ConectorVergnaud conector = new ConectorVergnaud(TipoConectorDiagrama.SETA,
                20, 300, 120, 300, "", new Rectangle(0, 250, 300, 100));
        verificar(conector.contem(70, 304), "Conector deve reconhecer proximidade do segmento");
        conector.mover(10, 0, limite);
        verificar(conector.x1 == 30 && conector.x2 == 130, "Conector deve mover as extremidades");
        conector.desenhar(g2);

        g2.dispose();
        System.out.println("OK: " + verificacoes + " verificações dos elementos gráficos extraídos.");
    }
}
