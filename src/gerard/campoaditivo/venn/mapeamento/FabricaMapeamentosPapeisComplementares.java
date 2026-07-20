package gerard.campoaditivo.venn.mapeamento;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.EnumMap;
import java.util.Map;

/**
 * Seleciona polimorficamente o contrato de mapeamento adequado à categoria.
 */
public final class FabricaMapeamentosPapeisComplementares {
    private final MapeamentoPapeisRepresentacaoComplementar identidade =
            new MapeamentoIdentidadePapeisComplementares();
    private final Map<TipoSituacaoAditiva, MapeamentoPapeisRepresentacaoComplementar>
            especificos = new EnumMap<TipoSituacaoAditiva,
                    MapeamentoPapeisRepresentacaoComplementar>(TipoSituacaoAditiva.class);

    public FabricaMapeamentosPapeisComplementares() {
        especificos.put(TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new MapeamentoComparacaoPapeisComplementares());
    }

    public MapeamentoPapeisRepresentacaoComplementar obter(
            TipoSituacaoAditiva tipo) {
        MapeamentoPapeisRepresentacaoComplementar mapeamento =
                tipo == null ? null : especificos.get(tipo);
        return mapeamento == null ? identidade : mapeamento;
    }
}
