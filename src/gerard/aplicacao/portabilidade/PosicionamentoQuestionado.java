package gerard.aplicacao.portabilidade;

/**
 * Número solto numa caixa incompatível: fica na caixa, com a pergunta aberta, até ser corrigido
 * (mesmo estado do desktop, itemQuestionadoPersistente/textoQuestionamentoPersistente em Main).
 */
public final class PosicionamentoQuestionado {
    private final String origemPapelId;
    private final String chavePapelAlvo;
    private final String mensagem;

    public PosicionamentoQuestionado(String origemPapelId, String chavePapelAlvo, String mensagem) {
        this.origemPapelId = origemPapelId;
        this.chavePapelAlvo = chavePapelAlvo;
        this.mensagem = mensagem;
    }

    public String getOrigemPapelId() {
        return origemPapelId;
    }

    public String getChavePapelAlvo() {
        return chavePapelAlvo;
    }

    public String getMensagem() {
        return mensagem;
    }
}
