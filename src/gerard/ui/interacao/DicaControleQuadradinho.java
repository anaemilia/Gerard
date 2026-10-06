package gerard.ui.interacao;

/**
 * Dica do mouse sobre o controle de adicionar ou remover quadradinho (ou, nas transformações, de aumentar ou
 * diminuir o valor inteiro): qual mensagem aparece e se o cursor vira mão. A regra tem quatro estados e vale igual
 * para os dois controles, espelhada:
 *
 * <ul>
 *   <li>modelagem ainda não iniciada: mensagem de bloqueio da adição de unidades (quem pergunta a fornece);</li>
 *   <li>liberado: aumentar/diminuir o valor (valor inteiro) ou adicionar/remover o quadrado;</li>
 *   <li>no limite do valor inteiro: "limite do valor inteiro";</li>
 *   <li>no limite de quadradinhos: "máximo semântico atingido" (adicionar) ou "mínimo atingido" (remover).</li>
 * </ul>
 *
 * Só decide; a tela aplica a mensagem e o cursor. Sem Swing.
 */
public final class DicaControleQuadradinho {
    public enum Controle { ADICIONAR, REMOVER }

    private final boolean bloqueadaPelaModelagem;
    private final boolean liberada;
    private final String chaveMensagem;

    private DicaControleQuadradinho(boolean bloqueadaPelaModelagem, boolean liberada, String chaveMensagem) {
        this.bloqueadaPelaModelagem = bloqueadaPelaModelagem;
        this.liberada = liberada;
        this.chaveMensagem = chaveMensagem;
    }

    /**
     * @param modelagemIniciada a modelagem já liberou a adição/remoção de unidades
     * @param liberada          a ação está disponível agora (há o que adicionar/remover dentro dos limites)
     * @param valorInteiro      o controle atua sobre o valor assinado inteiro da transformação (e não sobre quadradinhos)
     */
    public static DicaControleQuadradinho decidir(Controle controle, boolean modelagemIniciada, boolean liberada,
            boolean valorInteiro) {
        if (!modelagemIniciada) {
            return new DicaControleQuadradinho(true, false, null);
        }
        boolean adicionar = controle == Controle.ADICIONAR;
        String chave;
        if (liberada) {
            chave = valorInteiro
                    ? (adicionar ? "ui.tooltip.venn.increaseIntegerValue" : "ui.tooltip.venn.decreaseIntegerValue")
                    : (adicionar ? "ui.tooltip.venn.addSquare" : "ui.tooltip.venn.removeSquare");
        } else {
            chave = valorInteiro
                    ? "ui.tooltip.venn.integerLimitReached"
                    : (adicionar ? "ui.tooltip.venn.semanticLimitReached" : "ui.tooltip.venn.minimumReached");
        }
        return new DicaControleQuadradinho(false, liberada, chave);
    }

    /** A modelagem ainda não iniciou: a mensagem é a de bloqueio (não há chave própria). */
    public boolean bloqueadaPelaModelagem() { return bloqueadaPelaModelagem; }

    /** O cursor vira mão (a ação está disponível). */
    public boolean cursorMao() { return liberada; }

    /** Chave da mensagem localizada; nula quando {@link #bloqueadaPelaModelagem()}. */
    public String getChaveMensagem() { return chaveMensagem; }
}
