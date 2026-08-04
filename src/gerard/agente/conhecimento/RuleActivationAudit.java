package gerard.agente.conhecimento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registro de auditoria de UMA regra da base de conhecimento integrada
 * frente a um conjunto de fatos — quem a consultou, o que bateu, se ela
 * venceu na decisão final e por quê (não) — para o log estruturado dos
 * agentes (ver gerard.pesquisador.auditoria). Construído a partir de
 * {@link RegraConhecimento} já avaliada por {@link MotorRegrasConhecimento}
 * — não recalcula nada, só descreve o que já aconteceu.
 */
public final class RuleActivationAudit {
    private final String ruleId;
    private final String tipo;
    private final String fonte;
    private final String versao;
    private final List<String> antecedentesSatisfeitos;
    private final String conclusao;
    private final int prioridade;
    private final boolean usedInFinalDecision;
    private final String motivoDescarte;

    public RuleActivationAudit(String ruleId, String tipo, String fonte, String versao,
            List<String> antecedentesSatisfeitos, String conclusao, int prioridade,
            boolean usedInFinalDecision, String motivoDescarte) {
        this.ruleId = ruleId;
        this.tipo = tipo;
        this.fonte = fonte;
        this.versao = versao;
        this.antecedentesSatisfeitos = antecedentesSatisfeitos == null
                ? Collections.<String>emptyList() : new ArrayList<String>(antecedentesSatisfeitos);
        this.conclusao = conclusao;
        this.prioridade = prioridade;
        this.usedInFinalDecision = usedInFinalDecision;
        this.motivoDescarte = motivoDescarte;
    }

    /**
     * Constrói a partir de uma regra que bateu (antecedentes satisfeitos),
     * marcando se ela foi a vencedora (usedInFinalDecision) — quando não
     * foi, motivoDescarte explica que outra regra de prioridade maior
     * decidiu o mesmo campo primeiro.
     */
    public static RuleActivationAudit deRegraQueBateu(RegraConhecimento regra, boolean usedInFinalDecision,
            String motivoDescarte) {
        List<String> antecedentes = new ArrayList<String>();
        for (Condicao condicao : regra.getAntecedentes()) {
            antecedentes.add(condicao.getCampo() + " " + condicao.getOperador() + " " + String.valueOf(condicao.getValor()));
        }
        StringBuilder conclusao = new StringBuilder();
        for (Conclusao c : regra.getConclusoes()) {
            if (conclusao.length() > 0) {
                conclusao.append("; ");
            }
            conclusao.append(c.getCampo()).append("=").append(String.valueOf(c.getValor()));
        }
        String fonte = regra.getFontes().isEmpty() ? null : String.valueOf(regra.getFontes());
        return new RuleActivationAudit(regra.getRuleId(), regra.getTipo(), fonte, regra.getVersao(),
                antecedentes, conclusao.toString(), regra.getPrioridade(), usedInFinalDecision, motivoDescarte);
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getTipo() {
        return tipo;
    }

    public String getFonte() {
        return fonte;
    }

    public String getVersao() {
        return versao;
    }

    public List<String> getAntecedentesSatisfeitos() {
        return Collections.unmodifiableList(antecedentesSatisfeitos);
    }

    public String getConclusao() {
        return conclusao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public boolean isUsedInFinalDecision() {
        return usedInFinalDecision;
    }

    public String getMotivoDescarte() {
        return motivoDescarte;
    }
}
