import gerard.Scaffolding.pickup.DesenhavelPickup;
import gerard.Scaffolding.pickup.FornecedorCursoresPickup;
import gerard.Scaffolding.pickup.FornecedorCursoresPickupSwing;
import gerard.Scaffolding.pickup.RenderizadorPickup;
import gerard.Scaffolding.pickup.RenderizadorPickupElevado;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class TesteAffordancePickup {
    public static void main(String[] args) {
        BufferedImage imagem = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        RenderizadorPickup renderizador = new RenderizadorPickupElevado();
        renderizador.desenharEmPrimeiroPlano(g2, new DesenhavelPickup() {
            public Rectangle obterLimitesVisuais() {
                return new Rectangle(25, 25, 20, 20);
            }
            public void desenharConteudo(Graphics2D grafico) {
                grafico.setColor(Color.RED);
                grafico.fillRect(25, 25, 20, 20);
            }
        });
        g2.dispose();

        int pixels = 0;
        for (int y = 0; y < imagem.getHeight(); y++) {
            for (int x = 0; x < imagem.getWidth(); x++) {
                if ((imagem.getRGB(x, y) >>> 24) != 0) {
                    pixels++;
                }
            }
        }
        if (pixels <= 400) {
            throw new AssertionError("A escala e a sombra deveriam ampliar a área visual: " + pixels);
        }

        FornecedorCursoresPickup cursores = new FornecedorCursoresPickupSwing();
        Cursor aberta = cursores.obterMaoAberta();
        Cursor fechada = cursores.obterMaoFechada();
        if (aberta == null || fechada == null) {
            throw new AssertionError("Os cursores devem possuir fallback não nulo.");
        }
        if (aberta.getType() != Cursor.HAND_CURSOR) {
            throw new AssertionError(
                    "O mouseover deve usar a mão nativa do gráfico de barras.");
        }
        if (fechada.getType() != Cursor.MOVE_CURSOR) {
            throw new AssertionError(
                    "O arraste ativo deve usar o cursor nativo de movimentação.");
        }

        System.out.println("TesteAffordancePickup: OK");
    }
}
