package gerard.pesquisador.replay;

/**
 * Um preenchimento de incógnita por digitação (resultado calculado, ex.:
 * digitar "8" depois de calcular 4+4) num episódio real — irmão de
 * PassoReplayHumano (posicionamento por arraste) e PassoCategoriaReplayHumano
 * (escolha de categoria), mas para o terceiro tipo de ação avaliável: TEXTO.
 * Não existia até 2026-07-30: os quadros do mestrado sempre tiveram esse tipo
 * de ação com C/E já codificado pelo pesquisador, mas foi excluído do
 * catálogo porque não havia caminho de código real para avaliá-la (ver
 * AgenteMonitor.avaliarValorIncognita, criado nesse mesmo dia).
 */
public final class PassoTextoReplayHumano {
    public final String chavePapelAlvo;
    public final boolean correto;
    public final String descricao;

    public PassoTextoReplayHumano(String chavePapelAlvo, boolean correto, String descricao) {
        this.chavePapelAlvo = chavePapelAlvo;
        this.correto = correto;
        this.descricao = descricao;
    }
}
