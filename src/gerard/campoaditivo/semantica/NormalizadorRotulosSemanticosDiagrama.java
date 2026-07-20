package gerard.campoaditivo.semantica;

import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.papel.CatalogoPapeisSemanticos;

/**
 * Garante que cada figura do diagrama formal apresente seu papel semântico.
 *
 * A curadoria pode especializar os rótulos, mas um valor ausente nunca deve
 * eliminar o papel estrutural da categoria. O fallback parte do catálogo de
 * papéis e da localização ativa, não de textos espalhados pelos renderizadores.
 */
public final class NormalizadorRotulosSemanticosDiagrama {
    private final CatalogoPapeisSemanticosAditivos catalogoIndices =
            new CatalogoPapeisSemanticosAditivos();
    private final CatalogoPapeisSemanticos catalogoConceitual =
            new CatalogoPapeisSemanticos();

    public DefinicaoDiagramaAditivo garantir(
            TipoSituacaoAditiva tipo,
            DefinicaoDiagramaAditivo definicao,
            ServicoLocalizacao localizacao) {
        if (definicao == null) {
            return null;
        }
        ServicoLocalizacao loc = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        return new DefinicaoDiagramaAditivo(
                definicao.getTitulo(),
                resolver(tipo, 0, definicao.getRotulo1(), loc),
                resolver(tipo, 1, definicao.getRotulo2(), loc),
                resolver(tipo, 2, definicao.getRotulo3(), loc));
    }

    private String resolver(TipoSituacaoAditiva tipo, int indice,
            String rotuloAtual, ServicoLocalizacao localizacao) {
        String atual = limpar(rotuloAtual);
        if (atual.length() > 0 && !pareceChaveNaoResolvida(atual)) {
            return atual;
        }
        String chave = catalogoIndices.obterChavePapelDoElemento(
                tipo, indice, false, 1);
        String localizado = limpar(localizacao.texto(chave));
        if (localizado.length() > 0 && !pareceChaveNaoResolvida(localizado)) {
            return localizado;
        }
        return catalogoConceitual.obter(chave).getNomeConceitual();
    }

    private boolean pareceChaveNaoResolvida(String texto) {
        return texto.startsWith("papel.") || texto.startsWith("def.")
                || texto.startsWith("ui.");
    }

    private String limpar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
