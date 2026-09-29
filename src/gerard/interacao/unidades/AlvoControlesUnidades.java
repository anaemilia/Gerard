package gerard.interacao.unidades;

/**
 * Porta neutra dos controles de adicionar/remover unidades do material
 * concreto. {@code C} é um identificador opaco do agrupamento localizado;
 * o handler nunca o inspeciona. Geometria, representação e regras de limite
 * pertencem às implementações da porta.
 */
public interface AlvoControlesUnidades<C> {

    /** Hit-test do controle da operação; {@code null} quando não atingido. */
    C localizarControle(OperacaoControleUnidade operacao, int posicaoX, int posicaoY);

    /** Liberação decidida pela política de modelagem já existente. */
    boolean alteracaoLiberadaPelaModelagem();

    /** Limite decidido pela representação ou pelo estado semântico simulado. */
    boolean podeAplicar(C controle, OperacaoControleUnidade operacao);

    /** Aplica a alteração no proprietário da quantidade. */
    ResultadoAplicacaoControleUnidade aplicar(C controle, OperacaoControleUnidade operacao);
}
