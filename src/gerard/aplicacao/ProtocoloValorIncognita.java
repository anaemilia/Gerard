package gerard.aplicacao;

import gerard.dominio.atividade.TarefaInteracao;

/**
 * Os dois protocolos que informam o valor de uma incógnita e passam pela mesma confirmação: o editor numérico
 * (QUANTIFICAR) e a caixa de texto editável sobre o item arrastado (TEXTO). Só diferem no que o registro factual
 * descreve e em se a exibição da pergunta de confirmação carrega os identificadores da ação e da sequência de
 * rejeições (o protocolo TEXTO os carrega; o legado não, e isso é preservado).
 */
public enum ProtocoloValorIncognita {
    QUANTIFICAR("Quantificar a incógnita", "Editor numérico", "Item do diagrama", TarefaInteracao.QUANTIFICAR, false),
    TEXTO("Substituir incógnita por número", "Caixa de texto editável", "Item arrastável no diagrama",
            TarefaInteracao.TEXTO, true);

    private final String tarefa;
    private final String instrumentoOrganizacao;
    private final String instrumentoArtefato;
    private final TarefaInteracao tarefaDeInteracao;
    private final boolean feedbackCarregaIdentificadores;

    ProtocoloValorIncognita(String tarefa, String instrumentoOrganizacao, String instrumentoArtefato,
            TarefaInteracao tarefaDeInteracao, boolean feedbackCarregaIdentificadores) {
        this.tarefa = tarefa;
        this.instrumentoOrganizacao = instrumentoOrganizacao;
        this.instrumentoArtefato = instrumentoArtefato;
        this.tarefaDeInteracao = tarefaDeInteracao;
        this.feedbackCarregaIdentificadores = feedbackCarregaIdentificadores;
    }

    public String getTarefa() { return tarefa; }

    public String getInstrumentoOrganizacao() { return instrumentoOrganizacao; }

    public String getInstrumentoArtefato() { return instrumentoArtefato; }

    public TarefaInteracao getTarefaDeInteracao() { return tarefaDeInteracao; }

    public boolean feedbackCarregaIdentificadores() { return feedbackCarregaIdentificadores; }
}
