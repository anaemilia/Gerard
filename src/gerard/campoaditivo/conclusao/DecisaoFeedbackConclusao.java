package gerard.campoaditivo.conclusao;

/**
 * Decide o que o diagrama deve fazer com o destaque e o feedback de conclusão depois de cada verificação da
 * modelagem. É uma tabela de verdade pura (sem Swing): quem a recebe só materializa as ações, na ordem em que
 * aparecem aqui — remover o destaque, restaurá-lo em silêncio, cancelar a sequência de feedback ou iniciá-la.
 *
 * <ul>
 *   <li>não concluída: remove o destaque e cancela o feedback pendente;</li>
 *   <li>concluída de novo depois de a tentativa já ter sido encerrada, ou depois de o destaque ter sido
 *       suspenso por exploração: o destaque volta em silêncio, sem nova sinalização, tip ou registro;</li>
 *   <li>acabou de concluir pela primeira vez (a tentativa ainda não estava encerrada) e o tip ainda não foi
 *       apresentado: inicia o feedback de sucesso (primeiro a cena neutra, depois o azul pelo sequenciador).</li>
 * </ul>
 */
public final class DecisaoFeedbackConclusao {
    private final boolean removerDestaque;
    private final boolean restaurarDestaqueEmSilencio;
    private final boolean cancelarFeedbackPendente;
    private final boolean iniciarFeedbackDeSucesso;

    private DecisaoFeedbackConclusao(boolean removerDestaque, boolean restaurarDestaqueEmSilencio,
            boolean cancelarFeedbackPendente, boolean iniciarFeedbackDeSucesso) {
        this.removerDestaque = removerDestaque;
        this.restaurarDestaqueEmSilencio = restaurarDestaqueEmSilencio;
        this.cancelarFeedbackPendente = cancelarFeedbackPendente;
        this.iniciarFeedbackDeSucesso = iniciarFeedbackDeSucesso;
    }

    /**
     * @param concluida                 a modelagem está concluída agora
     * @param acabouDeConcluirPlenamente esta verificação foi a que concluiu
     * @param tentativaJaEncerrada      a tentativa (proprietária) já tinha sido encerrada por uma conclusão
     *                                  anterior: voltar a concluir é exploração, não nova conclusão
     * @param destaqueSuspenso          o destaque foi suspenso durante uma manipulação exploratória
     * @param deveApresentarTip         o tip de conclusão ainda não foi apresentado
     */
    public static DecisaoFeedbackConclusao decidir(boolean concluida, boolean acabouDeConcluirPlenamente,
            boolean tentativaJaEncerrada, boolean destaqueSuspenso, boolean deveApresentarTip) {
        boolean remover = !concluida;
        boolean restaurar = concluida && ((acabouDeConcluirPlenamente && tentativaJaEncerrada) || destaqueSuspenso);
        boolean cancelar = !concluida;
        boolean iniciar = concluida && acabouDeConcluirPlenamente && !tentativaJaEncerrada && deveApresentarTip;
        return new DecisaoFeedbackConclusao(remover, restaurar, cancelar, iniciar);
    }

    public boolean removerDestaque() { return removerDestaque; }

    public boolean restaurarDestaqueEmSilencio() { return restaurarDestaqueEmSilencio; }

    public boolean cancelarFeedbackPendente() { return cancelarFeedbackPendente; }

    public boolean iniciarFeedbackDeSucesso() { return iniciarFeedbackDeSucesso; }
}
