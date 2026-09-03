package gerard.ui.enunciado;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
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

    private final CatalogoPapeisSemanticosAditivos catalogoPapeis =
            new CatalogoPapeisSemanticosAditivos();
    private final TipoSituacaoAditiva tipoSituacaoSelecionada;
    private final int[] indicesElementosEstadoCompartilhado;

    public MapeadorPapelSemanticoTextoPadrao(
            TipoSituacaoAditiva tipoSituacaoSelecionada,
            int[] indicesElementosEstadoCompartilhado) {
        this.tipoSituacaoSelecionada = tipoSituacaoSelecionada;
        this.indicesElementosEstadoCompartilhado = indicesElementosEstadoCompartilhado;
    }

    @Override
    public int paraIndiceSemantico(String chavePapel) {
        int indiceReal = catalogoPapeis.obterIndiceElementoPorPapel(
                chavePapel, tipoSituacaoSelecionada);
        return ConversorIndiceEstadoCompartilhado.converterIndiceRealParaPapel(
                indicesElementosEstadoCompartilhado, indiceReal);
    }
}
