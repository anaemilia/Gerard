package gerard.campoaditivo.categoria;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.semantica.categoria.CatalogoEsquemasCategoriasAditivas;
import gerard.semantica.categoria.EsquemaCategoriaAditiva;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.papel.PapelQuantitativo;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Fachada de compatibilidade. Os grupos e capacidades não são mais definidos
 * manualmente: são derivados dos papéis e universos numéricos do esquema.
 */
public final class CatalogoPerfisCategoriasAditivas {
    private final CatalogoEsquemasCategoriasAditivas esquemas =
            new CatalogoEsquemasCategoriasAditivas();

    public PerfilInteracaoCategoria obter(TipoSituacaoAditiva tipo) {
        EsquemaCategoriaAditiva esquema = esquemas.obter(tipo);
        Set<String> papeisInteiros = new LinkedHashSet<String>();
        Set<Integer> indicesInteiros = new LinkedHashSet<Integer>();

        for (PapelQuantitativo papel : esquema.obterPapeis()) {
            if (papel.getDominio() == DominioNumerico.INTEIROS) {
                papeisInteiros.add(papel.getChave());
                if (papel.getChave().startsWith("papel.transformacao")) {
                    papeisInteiros.add("papel.transformacao*");
                }
                if (papel.getChave().startsWith("papel.relacao")) {
                    papeisInteiros.add("papel.relacao*");
                }
                if ("papel.diferenca".equals(papel.getChave())) {
                    papeisInteiros.add("papel.valorRelativo");
                    papeisInteiros.add("papel.numeroRelativo");
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            if (esquema.obterDominioCompartilhado(i) == DominioNumerico.INTEIROS) {
                indicesInteiros.add(Integer.valueOf(i));
            }
        }

        boolean possuiInteiros = !papeisInteiros.isEmpty();
        EnumSet<CapacidadeInteracaoCategoria> capacidades = EnumSet.of(
                CapacidadeInteracaoCategoria.QUANTIDADES_CARDINAIS_NAO_NEGATIVAS);
        if (possuiInteiros) {
            capacidades.add(CapacidadeInteracaoCategoria.RELACAO_ASSINADA);
            capacidades.add(CapacidadeInteracaoCategoria.EIXO_DOS_INTEIROS);
            capacidades.add(CapacidadeInteracaoCategoria.CRUZAR_ZERO);
        }
        return new PerfilInteracaoCategoria(
                possuiInteiros
                        ? GrupoCategoriaAditiva.COM_RELACAO_ASSINADA
                        : GrupoCategoriaAditiva.SEM_RELACAO_ASSINADA,
                papeisInteiros, indicesInteiros, capacidades);
    }

    public GrupoCategoriaAditiva obterGrupo(TipoSituacaoAditiva tipo) {
        return obter(tipo).getGrupo();
    }
}
