package gerard.ui.conclusao;

import gerard.campoaditivo.conclusao.ControladorConclusaoModelagem;
import gerard.campoaditivo.conclusao.EscolhaConclusaoModelagem;

/**
 * Fluxo do feedback de conclusão da modelagem: o que acontece quando o usuário escolhe Sim/Não no tip, clica em
 * "Próxima tarefa", passa o mouse sobre o selo, e quando a sequência de confirmação pede a decisão ou é cancelada.
 * A decisão (e o fato registrado no log) é daqui; a realização na tela (destaque, selo, repintura, nova situação)
 * fica com a {@link Porta}, implementada pela tela.
 */
public final class FluxoFeedbackConclusaoModelagem {
    /** O que a tela sabe realizar; nenhuma regra de conclusão mora aqui. */
    public interface Porta {
        void registrarLogUsuario(String tarefa, String ce, String instrumentoOrganizacao,
                String instrumentoArtefato, String funcaoDoArtefato, String objeto, String regras,
                String origemEvento, String detalhes);

        /** Equivale a escolher "Nova situação" no menu, se disponível. */
        void iniciarNovaSituacao();

        void repintar();

        /** Retira qualquer vermelho do diagrama antes do destaque de sucesso (ERRO -> NEUTRO -> SUCESSO). */
        void limparFeedbackVisualErro();

        void aplicarDestaqueConclusao(boolean ativo);

        /** Mostra o selo ao lado do diagrama, oculta o tip e registra o feedback exibido. */
        void mostrarSeloConclusao();

        void ocultarSeloETip();

        void mostrarTipConclusao();
    }

    private final TipConclusaoModelagem tip;
    private final SeloConclusaoModelagem selo;
    private final SequenciadorFeedbackConclusao sequenciador;
    private final ControladorConclusaoModelagem controlador;
    private final Porta porta;

    public FluxoFeedbackConclusaoModelagem(
            TipConclusaoModelagem tip,
            SeloConclusaoModelagem selo,
            SequenciadorFeedbackConclusao sequenciador,
            ControladorConclusaoModelagem controlador,
            Porta porta
    ) {
        this.tip = tip;
        this.selo = selo;
        this.sequenciador = sequenciador;
        this.controlador = controlador;
        this.porta = porta;
    }

    public void ligar() {
        tip.definirOuvinte(new TipConclusaoModelagem.OuvinteEscolha() {
            public void aoEscolherSim() {
                sequenciador.encerrar();
                porta.registrarLogUsuario(
                        "Concluir modelagem e avançar", "C",
                        "Diagrama de Vergnaud", "Tip de conclusão",
                        "Escolher a continuidade da atividade", "OBJ_CONCLUSAO",
                        "A modelagem concluída permite iniciar uma nova situação-problema.",
                        "CONCLUSAO_MODELAGEM", "escolha=sim");
                if (EscolhaConclusaoModelagem.SIM.iniciaNovaSituacao()) {
                    porta.iniciarNovaSituacao();
                }
            }

            public void aoEscolherNao() {
                sequenciador.encerrar();
                porta.registrarLogUsuario(
                        "Concluir modelagem e permanecer", "C",
                        "Diagrama de Vergnaud", "Tip de conclusão",
                        "Escolher a continuidade da atividade", "OBJ_CONCLUSAO",
                        "A escolha Não mantém a situação atual e o destaque de conclusão.",
                        "CONCLUSAO_MODELAGEM", "escolha=nao");
                porta.repintar();
            }
        });
        sequenciador.definirOuvinte(new SequenciadorFeedbackConclusao.Ouvinte() {
            public void aoMostrarConfirmacaoVisual() {
                // Aguarda a pausa (ver SequenciadorFeedbackConclusao) sem
                // alterar nada visualmente ainda — o diagrama só passa a
                // azul depois do atraso, em aoSolicitarDecisao().
            }

            public void aoSolicitarDecisao() {
                if (!controlador.isConcluida()) {
                    sequenciador.cancelar();
                    return;
                }
                controlador.registrarTipApresentado();
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // Defesa da transição ERRO -> NEUTRO -> SUCESSO:
                        // nenhum vermelho pode sobreviver sob o destaque azul.
                        porta.limparFeedbackVisualErro();
                        porta.aplicarDestaqueConclusao(true);
                        porta.mostrarSeloConclusao();
                    }
                });
            }

            public void aoCancelar() {
                porta.aplicarDestaqueConclusao(false);
                porta.ocultarSeloETip();
            }
        });
        selo.definirAcaoClique(new Runnable() {
            public void run() {
                sequenciador.encerrar();
                porta.registrarLogUsuario(
                        "Concluir modelagem e avançar", "C",
                        "Diagrama de Vergnaud", "Próxima tarefa",
                        "Escolher a continuidade da atividade", "OBJ_CONCLUSAO",
                        "A modelagem concluída permite iniciar uma nova situação-problema.",
                        "CONCLUSAO_MODELAGEM", "escolha=proxima_tarefa");
                if (EscolhaConclusaoModelagem.PROXIMA_TAREFA.iniciaNovaSituacao()) {
                    porta.iniciarNovaSituacao();
                }
            }
        });
        selo.definirAcaoHover(new Runnable() {
            public void run() {
                porta.mostrarTipConclusao();
            }
        });
    }
}
