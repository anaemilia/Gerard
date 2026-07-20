package gerard.aplicacao;

/**
 * Ações relevantes para o ciclo de vida da atividade. A política associada
 * informa, em um único ponto, quais ações iniciam uma nova modelagem e quais
 * preservam o trabalho já construído pelo usuário.
 */
public enum AcaoAtividade {
    INICIALIZAR(true),
    SORTEAR(true),
    SELECIONAR_CATEGORIA(true),
    RESTAURAR(true),
    TROCAR_IDIOMA(false),
    TROCAR_ESTILO_INTERACAO(false),
    ABRIR_OU_FECHAR_CURADORIA(false),
    ATUALIZAR_TEXTO(false),
    TROCAR_REPRESENTACAO(false);

    private final boolean reiniciaModelagem;

    AcaoAtividade(boolean reiniciaModelagem) {
        this.reiniciaModelagem = reiniciaModelagem;
    }

    public boolean reiniciaModelagem() {
        return reiniciaModelagem;
    }
}
