package gerard.ui.enunciado;

import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.texto.MapeadorPapelSemanticoTexto;

/**
 * Implementação padrão de {@link MapeadorPapelSemanticoTexto}, extraída da
 * classe anônima original dentro de
 * Main.TelaGerard.sincronizarElementosSemanticosDoTexto (Fase 1 do plano de
 * refatoração — ver PLANO_REFATORACAO_ARQUITETURA_GERARD.md).
 *
 * Recebe no construtor os valores/objetos que a classe anônima original lia
 * diretamente dos campos de TelaGerard. Como esses valores não mudam durante
 * o ciclo síncrono de sincronização do texto, capturá-los no momento da
 * construção preserva o comportamento original.
 */
public final class MapeadorPapelSemanticoTextoPadrao implements MapeadorPapelSemanticoTexto {

    private final ScaffoldingQuestionamento scaffoldingQuestionamento;
    private final TipoSituacaoAditiva tipoSituacaoSelecionada;
    private final boolean usaDiagramasEncadeadosTransformacaoComposta;
    private final int quantidadePassosTransformacaoComposta;
    private final int[] indicesElementosEstadoCompartilhado;

    public MapeadorPapelSemanticoTextoPadrao(
            ScaffoldingQuestionamento scaffoldingQuestionamento,
            TipoSituacaoAditiva tipoSituacaoSelecionada,
            boolean usaDiagramasEncadeadosTransformacaoComposta,
            int quantidadePassosTransformacaoComposta,
            int[] indicesElementosEstadoCompartilhado) {
        this.scaffoldingQuestionamento = scaffoldingQuestionamento;
        this.tipoSituacaoSelecionada = tipoSituacaoSelecionada;
        this.usaDiagramasEncadeadosTransformacaoComposta = usaDiagramasEncadeadosTransformacaoComposta;
        this.quantidadePassosTransformacaoComposta = quantidadePassosTransformacaoComposta;
        this.indicesElementosEstadoCompartilhado = indicesElementosEstadoCompartilhado;
    }

    @Override
    public int paraIndiceSemantico(String chavePapel) {
        int indiceReal = scaffoldingQuestionamento.obterIndiceElementoPorPapel(
                chavePapel,
                tipoSituacaoSelecionada,
                usaDiagramasEncadeadosTransformacaoComposta,
                quantidadePassosTransformacaoComposta);
        return ConversorIndiceEstadoCompartilhado.converterIndiceRealParaPapel(
                indicesElementosEstadoCompartilhado, indiceReal);
    }
}
