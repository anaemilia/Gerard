package gerard.dominio.campoaditivo.evento;

import gerard.dominio.campoaditivo.DiagnosticoErroPapel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Evento semântico do ciclo de vida de um PapelQuantitativo. Carrega o mesmo
 * tipo de informação que gerard.pesquisador.auditoria.AgentAuditEvent já
 * valida em produção (papel envolvido, resultado, mensagem, feedback) — não
 * é um formato novo, é o mesmo vocabulário já correto estendido a um objeto
 * de domínio (ver auditoria Semantic Event Logging, seção 2).
 */
public final class EventoPapelQuantitativo implements EventoDominio {

    private final TipoEventoPapel tipo;
    private final String chavePapel;
    private final String valorProposto;
    private final boolean aceito;
    private final DiagnosticoErroPapel diagnostico; // null quando aceito
    private final long timestampEpocaMillis;

    public EventoPapelQuantitativo(TipoEventoPapel tipo, String chavePapel, String valorProposto,
                                    boolean aceito, DiagnosticoErroPapel diagnostico) {
        this.tipo = tipo;
        this.chavePapel = chavePapel;
        this.valorProposto = valorProposto;
        this.aceito = aceito;
        this.diagnostico = diagnostico;
        this.timestampEpocaMillis = System.currentTimeMillis();
    }

    @Override
    public String getTipo() { return tipo.name(); }

    @Override
    public long getTimestampEpocaMillis() { return timestampEpocaMillis; }

    public String getChavePapel() { return chavePapel; }
    public boolean isAceito() { return aceito; }
    public DiagnosticoErroPapel getDiagnostico() { return diagnostico; }

    @Override
    public Map<String, Object> paraMapa() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("tipo", getTipo());
        mapa.put("papel", chavePapel);
        mapa.put("valor_proposto", valorProposto);
        mapa.put("aceito", aceito);
        if (diagnostico != null) {
            mapa.put("tipo_erro", diagnostico.getTipo().name());
            mapa.put("chave_mensagem", diagnostico.getChaveMensagem());
            mapa.put("chave_feedback_pedagogico", diagnostico.getChaveFeedbackPedagogico());
            mapa.put("chave_sugestao_correcao", diagnostico.getChaveSugestaoCorrecao());
        }
        mapa.put("timestamp_epoca_millis", timestampEpocaMillis);
        return mapa;
    }

    @Override
    public String toString() {
        return paraMapa().toString();
    }
}
