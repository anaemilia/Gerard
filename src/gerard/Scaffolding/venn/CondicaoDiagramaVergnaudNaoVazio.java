package gerard.Scaffolding.venn;

/**
 * Libera a edição das unidades assim que o diagrama formal deixa de estar
 * vazio. A origem do conteúdo não importa: arraste, edição textual ou
 * preenchimento automático produzem o mesmo estado de habilitação.
 */
public final class CondicaoDiagramaVergnaudNaoVazio
        extends CondicaoHabilitacaoAdicaoUnidadesAbstrata {

    private final EstadoModelagemVergnaud estadoModelagem;

    public CondicaoDiagramaVergnaudNaoVazio(
            EstadoModelagemVergnaud estadoModelagem,
            String chaveMensagemBloqueio) {
        super(chaveMensagemBloqueio);
        if (estadoModelagem == null) {
            throw new IllegalArgumentException(
                    "O estado da modelagem de Vergnaud não pode ser nulo.");
        }
        this.estadoModelagem = estadoModelagem;
    }

    @Override
    public boolean estaSatisfeita() {
        return estadoModelagem.possuiConteudoSemantico();
    }
}
