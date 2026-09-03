package gerard.campoaditivo.sincronizacao.representacoes;

import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.ResolvedorRelacoesEstruturaisAditivas;
import gerard.i18n.ServicoLocalizacao;

/** Projeta valores semanticos para as barras, sem conhecer a interface. */
public final class ProjetorValoresComparacaoComplementar {
    private final ResolvedorRelacoesEstruturaisAditivas resolvedor =
            new ResolvedorRelacoesEstruturaisAditivas();

    public int[] projetar(SituacaoProblemaAditiva situacao,
            ServicoLocalizacao localizacao, Integer referidoModelado,
            Integer valorRelativoModelado, Integer referendoModelado) {
        Integer relativoCurado = situacao == null ? null
                : SemanticaCuradaSituacao.buscarValorInteiroVisivel(
                        situacao, localizacao, "papel.diferenca");
        return projetar(referidoModelado, valorRelativoModelado,
                referendoModelado, relativoCurado);
    }

    public int[] projetar(Integer referidoModelado,
            Integer valorRelativoModelado, Integer referendoModelado,
            Integer valorRelativoCuradoVisivel) {
        if (referidoModelado == null && valorRelativoModelado == null
                && referendoModelado == null) {
            return new int[] {0, 0, 0};
        }
        Integer relativo = valorRelativoModelado != null
                ? valorRelativoModelado : valorRelativoCuradoVisivel;
        if (relativo == null && referidoModelado != null
                && referendoModelado != null) {
            ResolvedorRelacoesEstruturaisAditivas.ResolucaoAutomatica resolucao =
                    resolvedor.resolverValores(
                            TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                            new Integer[] {referidoModelado, null, referendoModelado},
                            new boolean[] {true, false, true}, -1);
            if (resolucao.foiResolvida() && resolucao.getIndice() == 1) {
                relativo = Integer.valueOf(resolucao.getValor());
            }
        }
        return new int[] {
            Math.max(0, referidoModelado == null ? 0 : referidoModelado.intValue()),
            relativo == null ? 0 : relativo.intValue(),
            Math.max(0, referendoModelado == null ? 0 : referendoModelado.intValue())
        };
    }
}
