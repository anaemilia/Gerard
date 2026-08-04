package gerard.pesquisador.replay;

/**
 * Um passo de posicionamento (arrastar/informar valor) transcrito de um
 * protocolo real de pesquisa (quadro de análise da tarefa, época do
 * mestrado) — a chave semântica real do numeral e a chave semântica do
 * elemento onde ele foi colocado, exatamente como
 * Main.avaliarQuestionamentoPosicionamento monta antes de chamar
 * AgenteMonitor.avaliarPosicionamento.
 *
 * corretoNoProtocoloOriginal guarda o veredito C/E que a pesquisadora deu no
 * quadro original — não é usado para decidir nada (quem decide é o
 * ScaffoldingQuestionamento real, a partir das duas chaves de papel); serve
 * só para o harness conferir se o julgamento atual do Agente Monitor bate
 * com o julgamento humano original, e reportar divergências.
 *
 * Passos cujo erro real era só ausência/erro de sinal no número relativo
 * (muito comuns nos 5 protocolos) foram deliberadamente deixados de fora do
 * catálogo de replay — ver dados/protocolos_reais_replay_notas.md: hoje o sinal é
 * validado só na interface (ScaffoldingNumeroRelativo, um menu de escolha),
 * sem nenhum caminho até AgenteMonitor/AgenteZDP/AgenteModelador, então não
 * há como reproduzir esse tipo de erro no nível de API dos agentes sem
 * inventar um sinal que o código real não julga.
 */
public final class PassoReplayHumano {
    public final String chavePapelNumeral;
    public final String chavePapelAlvo;
    public final boolean corretoNoProtocoloOriginal;
    public final String descricao;

    public PassoReplayHumano(String chavePapelNumeral, String chavePapelAlvo,
            boolean corretoNoProtocoloOriginal, String descricao) {
        this.chavePapelNumeral = chavePapelNumeral;
        this.chavePapelAlvo = chavePapelAlvo;
        this.corretoNoProtocoloOriginal = corretoNoProtocoloOriginal;
        this.descricao = descricao;
    }
}
