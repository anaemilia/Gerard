package gerard.Scaffolding.venn;

/**
 * Base comum para condições de habilitação que apresentam uma mensagem de
 * orientação quando o controle ainda não pode ser utilizado.
 */
public abstract class CondicaoHabilitacaoAdicaoUnidadesAbstrata
        implements CondicaoHabilitacaoAdicaoUnidades {

    private final String chaveMensagemBloqueio;

    protected CondicaoHabilitacaoAdicaoUnidadesAbstrata(
            String chaveMensagemBloqueio) {
        this.chaveMensagemBloqueio = chaveMensagemBloqueio == null
                ? "" : chaveMensagemBloqueio;
    }

    @Override
    public final String obterChaveMensagemBloqueio() {
        return chaveMensagemBloqueio;
    }
}
