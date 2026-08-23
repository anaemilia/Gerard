import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

/** Contrato: a opção Sim deve acionar o item consolidado de nova situação. */
public final class TesteConclusaoAcionaNovaSituacao {
    public static void main(String[] args) throws Exception {
        final Throwable[] falha = new Throwable[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    Main.TelaGerard tela = new Main.TelaGerard();
                    tela.itemNovaSituacao.setEnabled(true);

                    final AtomicInteger acionamentosItem = new AtomicInteger();
                    tela.itemNovaSituacao.addActionListener(e -> acionamentosItem.incrementAndGet());

                    JRadioButton opcaoSim = encontrarPrimeiroRadio(tela.tipConclusaoModelagem);
                    if (opcaoSim == null) {
                        throw new AssertionError("Opção Sim não encontrada no tip de conclusão.");
                    }

                    opcaoSim.doClick();

                    if (acionamentosItem.get() != 1) {
                        throw new AssertionError("A opção Sim deve acionar exatamente uma vez o item Nova situação-problema.");
                    }
                } catch (Throwable ex) {
                    falha[0] = ex;
                }
            }
        });
        if (falha[0] != null) {
            throw new RuntimeException(falha[0]);
        }
        System.out.println("Teste aprovado: Sim aciona Nova situação-problema uma vez.");
        System.exit(0);
    }

    private static JRadioButton encontrarPrimeiroRadio(Container raiz) {
        for (Component componente : raiz.getComponents()) {
            if (componente instanceof JRadioButton) {
                return (JRadioButton) componente;
            }
            if (componente instanceof Container) {
                JRadioButton encontrado = encontrarPrimeiroRadio((Container) componente);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}
