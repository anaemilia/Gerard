import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.campoaditivo.semantica.NaturezaPapelAditivo;
import gerard.campoaditivo.semantica.PoliticaValoresAditivos;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.ui.enunciado.MapeadorPapelSemanticoTextoPadrao;

public final class TestePoliticaValoresAditivos {
    public static void main(String[] args) {
        CatalogoPapeisSemanticosAditivos catalogo =
                new CatalogoPapeisSemanticosAditivos();
        PoliticaValoresAditivos politica = new PoliticaValoresAditivos(catalogo);

        testarMapeamentoCentralizado(catalogo);
        testarNaturezaDosPapeis(catalogo);
        testarPoliticaPorCategoria(politica);
        testarEstadoCompartilhado(politica);

        System.out.println("TestePoliticaValoresAditivos: OK");
    }

    private static void testarMapeamentoCentralizado(
            CatalogoPapeisSemanticosAditivos catalogo) {
        exigir("papel.parte1".equals(catalogo.obterChavePapelDoElemento(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, 0, false, 1)),
                "Composição deveria mapear índice 0 para parte 1.");
        exigir("papel.transformacao".equals(catalogo.obterChavePapelDoElemento(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, 1, false, 1)),
                "Transformação deveria mapear índice 1 para transformação.");
        exigir("papel.diferenca".equals(catalogo.obterChavePapelDoElemento(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS, 1, false, 1)),
                "Comparação deveria mapear índice 1 para valor relativo.");
        exigir("papel.diferenca".equals(catalogo.obterChavePapelDoElemento(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS, 1)),
                "Contrato canônico deveria mapear índice sem flags legadas.");
        exigir(catalogo.obterIndiceElementoPorPapel("papel.diferenca",
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS) == 1,
                "Contrato canônico deveria localizar o papel sem flags legadas.");
        MapeadorPapelSemanticoTextoPadrao mapeador =
                new MapeadorPapelSemanticoTextoPadrao(
                        TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                        new int[] {0, 1, 2});
        exigir(mapeador.paraIndiceSemantico("papel.diferenca") == 1,
                "Mapeador textual deveria consumir o catálogo canônico.");
        exigir("papel.estadoIntermediario".equals(catalogo.obterChavePapelDoElemento(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                3, true, 2)),
                "O segundo passo encadeado deveria iniciar no estado intermediário.");
        exigir(catalogo.obterIndiceElementoPorPapel(
                "papel.estadoFinal",
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                true, 2) == 5,
                "Estado final de dois passos deveria ocupar o índice 5.");
    }

    private static void testarNaturezaDosPapeis(
            CatalogoPapeisSemanticosAditivos catalogo) {
        exigir(catalogo.obterNatureza("papel.estadoInicial")
                        == NaturezaPapelAditivo.QUANTIDADE,
                "Estado inicial deve ser quantidade.");
        exigir(catalogo.obterNatureza("papel.referendo")
                        == NaturezaPapelAditivo.QUANTIDADE,
                "Referendo deve ser quantidade.");
        exigir(catalogo.obterNatureza("papel.transformacao2")
                        == NaturezaPapelAditivo.TRANSFORMACAO_OU_RELACAO,
                "Transformação deve admitir sinal.");
        exigir(catalogo.obterNatureza("papel.relacaoFinal")
                        == NaturezaPapelAditivo.TRANSFORMACAO_OU_RELACAO,
                "Relação deve admitir sinal.");
        exigir(catalogo.obterNatureza("papel.diferenca")
                        == NaturezaPapelAditivo.TRANSFORMACAO_OU_RELACAO,
                "Valor relativo deve admitir sinal.");
    }

    private static void testarPoliticaPorCategoria(PoliticaValoresAditivos politica) {
        exigir(!politica.valorEhValidoParaElemento(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, 0, false, 1, -1),
                "Parte negativa deve ser rejeitada.");
        exigir(!politica.valorEhValidoParaElemento(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS, 2, false, 1, -1),
                "Referendo negativo deve ser rejeitado.");
        exigir(politica.valorEhValidoParaElemento(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS, 1, false, 1, -8),
                "Valor relativo negativo deve ser aceito.");
        exigir(politica.valorEhValidoParaElemento(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, 2, false, 1, -5),
                "Transformação resultante negativa deve ser aceita.");
        exigir(politica.valorEhValidoParaElemento(
                TipoSituacaoAditiva.COMPOSICAO_RELACOES, 0, false, 1, -2),
                "Relação negativa deve ser aceita.");
        exigir(!politica.valorEhValidoParaElemento(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                3, true, 2, -1),
                "Estado intermediário negativo deve ser rejeitado.");
        exigir(politica.valorEhValidoParaElemento(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                4, true, 2, -7),
                "Transformação do segundo passo deve admitir sinal.");
    }

    private static void testarEstadoCompartilhado(PoliticaValoresAditivos politica) {
        exigir(politica.indiceDoEstadoCompartilhadoRepresentaQuantidade(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS, 0),
                "Índice 0 da comparação deve ser quantidade.");
        exigir(!politica.indiceDoEstadoCompartilhadoRepresentaQuantidade(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS, 1),
                "Índice 1 da comparação deve admitir sinal.");

        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot comparacao = estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] {6, -8, -2},
                new boolean[] {true, true, true},
                1,
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        exigir(comparacao.isConhecido(1) && comparacao.valorOuZero(1) == -8,
                "Valor relativo negativo deve permanecer no estado.");
        exigir(!comparacao.isConhecido(2),
                "Quantidade negativa deve ser removida do estado.");

        EstadoSemanticoCompartilhado.Snapshot relacoes = estado.atualizar(
                TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                new Integer[] {-2, -3, -5},
                new boolean[] {true, true, true},
                -1,
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        exigir(relacoes.isConhecido(0) && relacoes.valorOuZero(0) == -2,
                "Relações assinadas não podem ser tratadas como quantidades.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
