package gerard.aplicacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.Random;

/**
 * Conhecimento de aplicação sobre os grupos usados no sorteio de situações.
 * Não conhece tela, componentes, geometria, persistência ou renderização.
 */
public final class PoliticaSorteioSituacoesAditivas {
    public enum Grupo { MEDIDAS, RELACOES, TODAS }

    private static final TipoSituacaoAditiva[] MEDIDAS = {
        TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
        TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
        TipoSituacaoAditiva.COMPARACAO_MEDIDAS
    };
    private static final TipoSituacaoAditiva[] RELACOES = {
        TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
        TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
        TipoSituacaoAditiva.COMPOSICAO_RELACOES
    };

    public TipoSituacaoAditiva[] categoriasDoGrupo(Grupo grupo) {
        if (grupo == null) throw new IllegalArgumentException("grupo é obrigatório");
        if (grupo == Grupo.MEDIDAS) return MEDIDAS.clone();
        if (grupo == Grupo.RELACOES) return RELACOES.clone();
        TipoSituacaoAditiva[] todas = new TipoSituacaoAditiva[MEDIDAS.length + RELACOES.length];
        System.arraycopy(MEDIDAS, 0, todas, 0, MEDIDAS.length);
        System.arraycopy(RELACOES, 0, todas, MEDIDAS.length, RELACOES.length);
        return todas;
    }

    public TipoSituacaoAditiva sortearCategoria(Grupo grupo, Random fonte) {
        if (fonte == null) throw new IllegalArgumentException("fonte aleatória é obrigatória");
        TipoSituacaoAditiva[] candidatas = categoriasDoGrupo(grupo);
        return candidatas[fonte.nextInt(candidatas.length)];
    }
}
