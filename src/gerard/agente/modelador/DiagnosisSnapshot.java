package gerard.agente.modelador;

import java.util.Collections;
import java.util.List;

/**
 * Diagnóstico automático do usuário num instante — pedido no schema de
 * referência, mas {@code AgenteModelador} não gera diagnóstico algum hoje
 * (só armazena {@code DiagnosticoTarefa} bruto: tarefa, regra de ação,
 * suporte). Motor de diagnóstico automático é um projeto à parte, ainda não
 * construído. Esta classe existe pra o log ter o lugar certo pra esse dado
 * quando (se) esse motor existir — por ora, toda instância vem com
 * {@code disponivel=false} e o motivo.
 */
public final class DiagnosisSnapshot {
    private final boolean disponivel;
    private final String diagnosisId;
    private final String descricao;
    private final Double confianca;
    private final List<String> evidencias;
    private final String motivoIndisponivel;

    private DiagnosisSnapshot(boolean disponivel, String diagnosisId, String descricao, Double confianca,
            List<String> evidencias, String motivoIndisponivel) {
        this.disponivel = disponivel;
        this.diagnosisId = diagnosisId;
        this.descricao = descricao;
        this.confianca = confianca;
        this.evidencias = evidencias == null ? Collections.<String>emptyList() : evidencias;
        this.motivoIndisponivel = motivoIndisponivel;
    }

    public static DiagnosisSnapshot indisponivel(String motivo) {
        return new DiagnosisSnapshot(false, null, null, null, null, motivo);
    }

    public boolean isDisponivel() { return disponivel; }
    public String getDiagnosisId() { return diagnosisId; }
    public String getDescricao() { return descricao; }
    public Double getConfianca() { return confianca; }
    public List<String> getEvidencias() { return Collections.unmodifiableList(evidencias); }
    public String getMotivoIndisponivel() { return motivoIndisponivel; }
}
