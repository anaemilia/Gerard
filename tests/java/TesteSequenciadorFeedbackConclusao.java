package gerard.ui.conclusao;

import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;

public final class TesteSequenciadorFeedbackConclusao {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        testarSequenciaCompleta();
        testarCancelamentoAntesDoTip();
        System.out.println("Teste do sequenciador de conclusão aprovado: "
                + verificacoes + " verificações.");
    }

    private static void testarSequenciaCompleta() throws Exception {
        final AtomicInteger selos = new AtomicInteger();
        final AtomicInteger decisoes = new AtomicInteger();
        final AtomicInteger cancelamentos = new AtomicInteger();
        final SequenciadorFeedbackConclusao sequenciador =
                new SequenciadorFeedbackConclusao(45);
        sequenciador.definirOuvinte(new SequenciadorFeedbackConclusao.Ouvinte() {
            public void aoMostrarConfirmacaoVisual() { selos.incrementAndGet(); }
            public void aoSolicitarDecisao() { decisoes.incrementAndGet(); }
            public void aoCancelar() { cancelamentos.incrementAndGet(); }
        });

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() { sequenciador.iniciar(); }
        });
        confirmar(sequenciador.getEstado()
                        == EstadoFeedbackConclusao.CONFIRMACAO_VISUAL,
                "a sequência deve começar pelo selo discreto");
        confirmar(selos.get() == 1 && decisoes.get() == 0,
                "o tip não pode aparecer simultaneamente ao selo");
        Thread.sleep(120);
        confirmar(sequenciador.getEstado()
                        == EstadoFeedbackConclusao.AGUARDANDO_DECISAO,
                "após o intervalo, a sequência deve solicitar decisão");
        confirmar(decisoes.get() == 1,
                "o tip deve ser solicitado uma única vez");

        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() { sequenciador.encerrar(); }
        });
        confirmar(sequenciador.getEstado() == EstadoFeedbackConclusao.ENCERRADO,
                "uma escolha deve encerrar a sequência");
        confirmar(cancelamentos.get() == 1,
                "encerrar deve ocultar os componentes visuais");
    }

    private static void testarCancelamentoAntesDoTip() throws Exception {
        final AtomicInteger decisoes = new AtomicInteger();
        final AtomicInteger cancelamentos = new AtomicInteger();
        final SequenciadorFeedbackConclusao sequenciador =
                new SequenciadorFeedbackConclusao(100);
        sequenciador.definirOuvinte(new SequenciadorFeedbackConclusao.Ouvinte() {
            public void aoMostrarConfirmacaoVisual() { }
            public void aoSolicitarDecisao() { decisoes.incrementAndGet(); }
            public void aoCancelar() { cancelamentos.incrementAndGet(); }
        });
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                sequenciador.iniciar();
                sequenciador.cancelar();
            }
        });
        Thread.sleep(150);
        confirmar(sequenciador.getEstado() == EstadoFeedbackConclusao.INATIVO,
                "alterar a modelagem deve cancelar a sequência");
        confirmar(decisoes.get() == 0,
                "o tip não pode aparecer após o cancelamento");
        confirmar(cancelamentos.get() == 1,
                "o cancelamento deve ocultar o selo");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
