package gerard.pesquisador.replay.robot;

import java.util.ArrayList;
import java.util.List;

/**
 * Traço completo de UM passo do protocolo curado conduzido por Robot —
 * agrega as tentativas ({@link RobotGestureAttempt}, com retentativa
 * limitada em caso de falha de pickup) e o resultado final. Uma linha
 * disto sempre é gravada em {@code robot_gestos.log}, mesmo quando nenhuma
 * tentativa chega a completar — é assim que "nenhum gesto pode desaparecer
 * silenciosamente" é garantido no nível do harness.
 */
public final class RobotGestureTrace {
    private final String episodeId;
    private final int ordemProtocolo;
    private final String descricaoProtocolo;
    private final String elementoProtocolo;
    private final String papelOrigemEsperado;
    private final String papelDestinoEsperado;
    private final String avaliacaoEsperada;

    private final List<RobotGestureAttempt> tentativas = new ArrayList<RobotGestureAttempt>();
    private String gestureId;
    private String actionId;

    public RobotGestureTrace(String episodeId, int ordemProtocolo, String descricaoProtocolo,
            String elementoProtocolo, String papelOrigemEsperado, String papelDestinoEsperado,
            String avaliacaoEsperada) {
        this.episodeId = episodeId;
        this.ordemProtocolo = ordemProtocolo;
        this.descricaoProtocolo = descricaoProtocolo;
        this.elementoProtocolo = elementoProtocolo;
        this.papelOrigemEsperado = papelOrigemEsperado;
        this.papelDestinoEsperado = papelDestinoEsperado;
        this.avaliacaoEsperada = avaliacaoEsperada;
    }

    public RobotGestureAttempt novaTentativa() {
        RobotGestureAttempt tentativa = new RobotGestureAttempt(tentativas.size() + 1);
        tentativas.add(tentativa);
        return tentativa;
    }

    public int getPickupRetryCount() {
        int falhasPickup = 0;
        for (RobotGestureAttempt t : tentativas) {
            if (t.getStatus() == RobotGestureStatus.PICKUP_FAILED) {
                falhasPickup++;
            }
        }
        return falhasPickup;
    }

    public RobotGestureAttempt ultimaTentativa() {
        return tentativas.isEmpty() ? null : tentativas.get(tentativas.size() - 1);
    }

    public RobotGestureStatus statusFinal() {
        RobotGestureAttempt ultima = ultimaTentativa();
        return ultima == null ? RobotGestureStatus.CANCELLED : ultima.getStatus();
    }

    public boolean chegouAAuditoria() {
        RobotGestureAttempt ultima = ultimaTentativa();
        return ultima != null && (ultima.getStatus() == RobotGestureStatus.COMPLETED
                || ultima.getStatus() == RobotGestureStatus.EVALUATION_NOT_DISPATCHED);
    }

    public boolean chegouAoMonitor() {
        RobotGestureAttempt ultima = ultimaTentativa();
        return ultima != null && ultima.getStatus() == RobotGestureStatus.COMPLETED
                && ultima.isAvaliacaoDisparada();
    }

    public void definirIdentidade(String gestureId, String actionId) {
        this.gestureId = gestureId;
        this.actionId = actionId;
    }

    public String getGestureId() { return gestureId; }
    public String getActionId() { return actionId; }
    public String getEpisodeId() { return episodeId; }
    public int getOrdemProtocolo() { return ordemProtocolo; }
    public String getDescricaoProtocolo() { return descricaoProtocolo; }
    public String getPapelOrigemEsperado() { return papelOrigemEsperado; }
    public String getPapelDestinoEsperado() { return papelDestinoEsperado; }
    public String getAvaliacaoEsperada() { return avaliacaoEsperada; }
    public List<RobotGestureAttempt> getTentativas() { return tentativas; }

    /**
     * Diagnóstico honesto de fidelidade: compara o que o protocolo esperava
     * executar com o que de fato chegou à auditoria/ao Monitor — nunca
     * assume que "executado pelo Robot" implica "chegou à auditoria".
     */
    public String diagnosticoFidelidade() {
        boolean chegouAuditoria = chegouAAuditoria();
        boolean chegouMonitor = chegouAoMonitor();
        if (chegouMonitor) {
            return "ok";
        }
        RobotGestureAttempt ultima = ultimaTentativa();
        if (ultima == null) {
            return "gesto_nunca_tentado";
        }
        switch (ultima.getStatus()) {
            case PICKUP_FAILED: return "gesto_perdido_no_pickup";
            case DROP_FAILED: return "gesto_perdido_na_soltura";
            case EVALUATION_NOT_DISPATCHED: return "avaliacao_nao_despachada_apos_soltura_valida";
            case EXCEPTION: return "excecao_durante_gesto";
            case CANCELLED: return "gesto_cancelado";
            default: return chegouAuditoria ? "chegou_auditoria_mas_nao_monitor" : "motivo_desconhecido";
        }
    }

    public String resumoLinhaUnica() {
        StringBuilder sb = new StringBuilder();
        sb.append("episodio=").append(episodeId)
                .append(" ordem=").append(ordemProtocolo)
                .append(" descricao=\"").append(descricaoProtocolo).append("\"")
                .append(" papelOrigem=").append(papelOrigemEsperado)
                .append(" papelDestino=").append(papelDestinoEsperado)
                .append(" avaliacaoEsperada=").append(avaliacaoEsperada)
                .append(" gestureId=").append(gestureId)
                .append(" tentativas=").append(tentativas.size())
                .append(" pickupRetryCount=").append(getPickupRetryCount())
                .append(" statusFinal=").append(statusFinal())
                .append(" diagnosticoFidelidade=").append(diagnosticoFidelidade());
        return sb.toString();
    }
}
