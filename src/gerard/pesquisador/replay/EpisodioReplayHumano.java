package gerard.pesquisador.replay;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Um episódio (uma situação-problema resolvida por uma pessoa real) do
 * conjunto de 5 protocolos do mestrado (quadros de análise da tarefa),
 * pronto para ser tocado pelos agentes reais via TesteReplayProtocolosReais.
 *
 * idUsuarioReal é um código, não o nome verdadeiro da participante (ex.:
 * "hist_aldenira") — os quadros originais (fora deste repositório) usam o
 * nome real; aqui, dentro do código-fonte, mantemos só um identificador
 * técnico. Ver PassoReplayHumano para o porquê de passos de erro de sinal
 * ficarem de fora.
 */
public final class EpisodioReplayHumano {
    public final String idUsuarioReal;
    public final TipoSituacaoAditiva categoria;
    public final String rotulo;
    public final List<PassoReplayHumano> passos;
    /**
     * Cliques de escolha de categoria antes do posicionamento no diagrama —
     * vazio para os episódios extraídos antes de 2026-07-30, quando essa
     * ação ainda não tinha caminho de código para ser avaliada (ver
     * PassoCategoriaReplayHumano).
     */
    public final List<PassoCategoriaReplayHumano> categorizacao;
    /**
     * Preenchimentos de incógnita por digitação (resultado calculado) —
     * vazio para os episódios extraídos antes de 2026-07-30. Ver
     * PassoTextoReplayHumano.
     */
    public final List<PassoTextoReplayHumano> textos;

    public EpisodioReplayHumano(String idUsuarioReal, TipoSituacaoAditiva categoria, String rotulo,
            List<PassoReplayHumano> passos) {
        this(idUsuarioReal, categoria, rotulo, passos, new ArrayList<PassoCategoriaReplayHumano>(),
                new ArrayList<PassoTextoReplayHumano>());
    }

    public EpisodioReplayHumano(String idUsuarioReal, TipoSituacaoAditiva categoria, String rotulo,
            List<PassoReplayHumano> passos, List<PassoCategoriaReplayHumano> categorizacao) {
        this(idUsuarioReal, categoria, rotulo, passos, categorizacao, new ArrayList<PassoTextoReplayHumano>());
    }

    public EpisodioReplayHumano(String idUsuarioReal, TipoSituacaoAditiva categoria, String rotulo,
            List<PassoReplayHumano> passos, List<PassoCategoriaReplayHumano> categorizacao,
            List<PassoTextoReplayHumano> textos) {
        this.idUsuarioReal = idUsuarioReal;
        this.categoria = categoria;
        this.rotulo = rotulo;
        this.passos = Collections.unmodifiableList(passos);
        this.categorizacao = Collections.unmodifiableList(categorizacao);
        this.textos = Collections.unmodifiableList(textos);
    }
}
