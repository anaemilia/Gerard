package gerard.ui.conclusao;

import java.awt.Rectangle;
import javax.swing.SwingUtilities;

public final class TestePosicionamentoSeloConclusao {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        verificar("transformação", new Rectangle(330, 330, 470, 150));
        verificar("composição", new Rectangle(280, 300, 520, 250));
        verificar("comparação", new Rectangle(330, 280, 360, 300));
        verificar("categoria composta", new Rectangle(180, 260, 760, 310));
        System.out.println("Teste de posicionamento do selo aprovado: "
                + verificacoes + " verificações.");
    }

    private static void verificar(final String categoria,
            final Rectangle areaDiagrama) throws Exception {
        final SeloConclusaoModelagem selo = new SeloConclusaoModelagem();
        selo.atualizarTexto("Modelagem concluída");
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                selo.mostrarAbaixoDoDiagrama(areaDiagrama,
                        new Rectangle(15, 215, 1110, 530), 1140, 800);
            }
        });
        confirmar(selo.isVisible(), "o selo deve aparecer em " + categoria);
        confirmar(selo.getY() >= areaDiagrama.y + areaDiagrama.height + 10,
                "o selo deve ficar abaixo do diagrama de " + categoria);
        confirmar(selo.getX() >= 25 && selo.getX() + selo.getWidth() <= 1115,
                "o selo deve permanecer dentro do painel de " + categoria);
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() { selo.ocultar(); }
        });
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
