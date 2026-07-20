package gerard.Scaffolding.venn;

/**
 * Contrato para condições que governam a liberação dos controles de adição de
 * unidades nas representações complementares.
 */
public interface CondicaoHabilitacaoAdicaoUnidades {
    boolean estaSatisfeita();
    String obterChaveMensagemBloqueio();
}
