import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

/** Contrato: a opção Sim do tip deve acionar o botão Sortear já consolidado. */
public final class TesteConclusaoAcionaSortearMesmaCategoria {
    public static void main(String[] args) throws Exception {
        final Throwable[] falha = new Throwable[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    Main.TelaGerard tela = new Main.TelaGerard();
                    tela.categoriaSelecionadaParaAtividade = true;
                    tela.tipoSituacaoSelecionada = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
                    tela.botaoSortear.setEnabled(true);

                    final AtomicInteger acionamentosBotao = new AtomicInteger();
                    tela.botaoSortear.addActionListener(e -> acionamentosBotao.incrementAndGet());

                    JRadioButton opcaoSim = encontrarPrimeiroRadio(tela.tipConclusaoModelagem);
                    if (opcaoSim == null) {
                        throw new AssertionError("Opção Sim não encontrada no tip de conclusão.");
                    }

                    TipoSituacaoAditiva categoriaAntes = tela.tipoSituacaoSelecionada;
                    opcaoSim.doClick();

                    if (acionamentosBotao.get() != 1) {
                        throw new AssertionError("A opção Sim deve acionar exatamente uma vez o botão Sortear.");
                    }
                    if (tela.tipoSituacaoSelecionada != categoriaAntes) {
                        throw new AssertionError("Sortear pela conclusão não pode trocar a categoria selecionada.");
                    }
                    if (tela.situacaoProblemaAtual != null
                            && tela.situacaoProblemaAtual.getTipo() != categoriaAntes) {
                        throw new AssertionError("A nova situação deve pertencer à mesma categoria.");
                    }
                } catch (Throwable ex) {
                    falha[0] = ex;
                }
            }
        });
        if (falha[0] != null) {
            throw new RuntimeException(falha[0]);
        }
        System.out.println("Teste aprovado: Sim aciona Sortear e preserva a categoria.");
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
