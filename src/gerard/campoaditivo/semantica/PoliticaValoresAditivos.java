package gerard.campoaditivo.semantica;

import gerard.campoaditivo.categoria.CatalogoPerfisCategoriasAditivas;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.semantica.categoria.CatalogoEsquemasCategoriasAditivas;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.papel.CatalogoPapeisSemanticos;

/**
 * Política de compatibilidade mantida para as camadas antigas.
 * A validade deixou de ser definida pela categoria ou pela representação:
 * o papel semântico fornece o universo e o próprio número aplica a restrição.
 */
public final class PoliticaValoresAditivos {
    private final CatalogoPapeisSemanticosAditivos catalogoLegado;
    private final CatalogoPapeisSemanticos catalogoConceitual;
    private final CatalogoEsquemasCategoriasAditivas esquemas;

    public PoliticaValoresAditivos() {
        this(new CatalogoPapeisSemanticosAditivos(), null);
    }

    public PoliticaValoresAditivos(CatalogoPapeisSemanticosAditivos catalogo) {
        this(catalogo, null);
    }

    /**
     * O parâmetro de perfis é preservado por compatibilidade binária. A fonte
     * conceitual atual são os papéis e os esquemas semânticos.
     */
    public PoliticaValoresAditivos(
            CatalogoPapeisSemanticosAditivos catalogo,
            CatalogoPerfisCategoriasAditivas perfisLegados) {
        this.catalogoLegado = catalogo == null
                ? new CatalogoPapeisSemanticosAditivos() : catalogo;
        this.catalogoConceitual = new CatalogoPapeisSemanticos();
        this.esquemas = new CatalogoEsquemasCategoriasAditivas();
    }

    public boolean quantidadeEhNaoNegativa(Integer quantidade) {
        return quantidade == null
                || DominioNumerico.NATURAIS.aceita(quantidade.intValue());
    }

    public boolean relacaoPreservaQuantidadeNaoNegativa(
            Integer quantidadeBase, Integer relacao) {
        if (quantidadeBase == null || relacao == null) {
            return true;
        }
        return quantidadeEhNaoNegativa(Integer.valueOf(
                quantidadeBase.intValue() + relacao.intValue()));
    }

    public DominioNumerico dominioDoPapel(String chavePapel) {
        return catalogoConceitual.dominioDoPapel(chavePapel);
    }

    public boolean valorEhValidoParaPapel(String chavePapel, Integer valor) {
        return valor == null || dominioDoPapel(chavePapel).aceita(valor.intValue());
    }

    public boolean valorEhValidoParaElemento(
            TipoSituacaoAditiva tipo,
            int indiceElemento,
            boolean transformacaoCompostaEncadeada,
            int quantidadePassosTransformacaoComposta,
            Integer valor) {
        String chavePapel = catalogoLegado.obterChavePapelDoElemento(
                tipo, indiceElemento, transformacaoCompostaEncadeada,
                quantidadePassosTransformacaoComposta);
        return valorEhValidoParaPapel(chavePapel, valor);
    }

    public boolean indiceDoEstadoCompartilhadoRepresentaQuantidade(
            TipoSituacaoAditiva tipo, int indice) {
        return esquemas.obter(tipo).obterDominioCompartilhado(indice)
                == DominioNumerico.NATURAIS;
    }

    public boolean valorEhValidoNoEstadoCompartilhado(
            TipoSituacaoAditiva tipo, int indice, Integer valor) {
        DominioNumerico dominio = esquemas.obter(tipo)
                .obterDominioCompartilhado(indice);
        return valor == null || dominio.aceita(valor.intValue());
    }
}
