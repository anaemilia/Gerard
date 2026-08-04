package gerard.agente.modelador;

import java.util.Collections;
import java.util.List;

/**
 * Estratégia detectada (vocabulário SEM/VIS/ORD/OPE/ELI/FBK/RER/MOT — ver
 * ontologia_gerard.json e regras_entrevistas.jsonl, R-ENT-*) num instante.
 *
 * As regras R-ENT-* já existem na base de conhecimento e SABEM detectar
 * essas estratégias a partir de texto livre — mas dependem de
 * {@code explicacaoElemento}/{@code explicacaoGeral}
 * ({@code DiagnosticoTarefa}), preenchido pelo pesquisador via
 * {@code TelaArtefatoExplicativo} DEPOIS da ação, não durante ela. Por
 * decisão da usuária em 2026-07-23 (ver {@code NivelConceitualExplicacao}),
 * classificação automática de texto livre é sinal fraco e não deve
 * alimentar decisão sem curadoria humana — por isso não ligo R-ENT-* aqui
 * agora; fica registrado como próximo passo natural, não implementado
 * nesta rodada.
 */
public final class StrategyDetectionAudit {
    private final boolean disponivel;
    private final String strategyId;
    private final String nome;
    private final Double confianca;
    private final List<String> evidencias;
    private final String motivoIndisponivel;

    private StrategyDetectionAudit(boolean disponivel, String strategyId, String nome, Double confianca,
            List<String> evidencias, String motivoIndisponivel) {
        this.disponivel = disponivel;
        this.strategyId = strategyId;
        this.nome = nome;
        this.confianca = confianca;
        this.evidencias = evidencias == null ? Collections.<String>emptyList() : evidencias;
        this.motivoIndisponivel = motivoIndisponivel;
    }

    public static StrategyDetectionAudit indisponivel(String motivo) {
        return new StrategyDetectionAudit(false, null, null, null, null, motivo);
    }

    public boolean isDisponivel() { return disponivel; }
    public String getStrategyId() { return strategyId; }
    public String getNome() { return nome; }
    public Double getConfianca() { return confianca; }
    public List<String> getEvidencias() { return Collections.unmodifiableList(evidencias); }
    public String getMotivoIndisponivel() { return motivoIndisponivel; }
}
