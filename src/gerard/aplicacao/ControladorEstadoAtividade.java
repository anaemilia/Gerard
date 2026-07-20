package gerard.aplicacao;

/**
 * Controlador simples do estado da atividade. Centraliza a decisão de preservar
 * ou reiniciar a modelagem sem introduzir uma hierarquia extensa de estados.
 */
public final class ControladorEstadoAtividade {
    private AcaoAtividade ultimaAcao = AcaoAtividade.INICIALIZAR;

    public void registrar(AcaoAtividade acao) {
        if (acao != null) {
            ultimaAcao = acao;
        }
    }

    public boolean deveReiniciarModelagem(AcaoAtividade acao) {
        return acao != null && acao.reiniciaModelagem();
    }

    public AcaoAtividade getUltimaAcao() {
        return ultimaAcao;
    }
}
