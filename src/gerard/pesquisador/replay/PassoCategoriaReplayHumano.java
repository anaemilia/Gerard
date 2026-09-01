package gerard.pesquisador.replay;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Um clique de escolha de categoria (Composição/Transformação/Comparação)
 * num episódio real, capturado da transcrição bruta — irmão de
 * PassoReplayHumano, mas para a ação de categorização em vez de
 * posicionamento no diagrama. Não existia até 2026-07-30: os quadros/
 * transcrições sempre tiveram esses cliques, mas foram excluídos de
 * protocolos_reais_replay.tsv porque não havia caminho de código real para
 * avaliá-los no proprietário semântico da classificação.
 */
public final class PassoCategoriaReplayHumano {
    public final TipoSituacaoAditiva categoriaEscolhida;
    public final boolean correta;
    public final String descricao;

    public PassoCategoriaReplayHumano(TipoSituacaoAditiva categoriaEscolhida, boolean correta, String descricao) {
        this.categoriaEscolhida = categoriaEscolhida;
        this.correta = correta;
        this.descricao = descricao;
    }
}
