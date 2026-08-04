package gerard.agente.modelador;

import java.util.Collections;
import java.util.Map;

/**
 * Registro do caso ({@code DiagnosticoTarefa}) inserido por
 * {@code AgenteModelador.armazenarCaso} — esta parte É real:
 * {@code armazenarCaso} sempre cria um caso nesse método, sem checar
 * duplicidade nem calcular similaridade com casos existentes (isso não
 * existe hoje — ver {@code getMotivoSemSimilaridade}).
 */
public final class CaseInsertionAudit {
    private final boolean inserted;
    private final String caseId;
    private final String caseBase;
    private final Map<String, Object> atributos;
    private final String motivoSemSimilaridade;

    public CaseInsertionAudit(boolean inserted, String caseId, String caseBase, Map<String, Object> atributos,
            String motivoSemSimilaridade) {
        this.inserted = inserted;
        this.caseId = caseId;
        this.caseBase = caseBase;
        this.atributos = atributos == null ? Collections.<String, Object>emptyMap() : atributos;
        this.motivoSemSimilaridade = motivoSemSimilaridade;
    }

    public boolean isInserted() { return inserted; }
    public String getCaseId() { return caseId; }
    public String getCaseBase() { return caseBase; }
    public Map<String, Object> getAtributos() { return Collections.unmodifiableMap(atributos); }

    /** Explica por que duplicidade/similaridade não são checadas: nenhum código faz essa comparação hoje. */
    public String getMotivoSemSimilaridade() { return motivoSemSimilaridade; }
}
