package gerard.agente.conhecimento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Uma regra da base de conhecimento integrada do Gérard (ver
 * dados/base_conhecimento_gerard_jsons/base_conhecimento_gerard/), no
 * formato descrito por schema_regras_gerard.json: identificação, tipo,
 * prioridade, status, antecedentes/conclusões e proveniência.
 */
public final class RegraConhecimento {
    private final String ruleId;
    private final String nome;
    private final String tipo;
    private final String versao;
    private final String status;
    private final int prioridade;
    private final List<Condicao> antecedentes;
    private final List<Conclusao> conclusoes;
    private final List<String> fontes;
    private final String explicacao;

    public RegraConhecimento(String ruleId, String nome, String tipo, String versao, String status, int prioridade,
            List<Condicao> antecedentes, List<Conclusao> conclusoes, List<String> fontes, String explicacao) {
        this.ruleId = ruleId;
        this.nome = nome;
        this.tipo = tipo;
        this.versao = versao;
        this.status = status;
        this.prioridade = prioridade;
        this.antecedentes = antecedentes == null
                ? Collections.<Condicao>emptyList() : new ArrayList<Condicao>(antecedentes);
        this.conclusoes = conclusoes == null
                ? Collections.<Conclusao>emptyList() : new ArrayList<Conclusao>(conclusoes);
        this.fontes = fontes == null
                ? Collections.<String>emptyList() : new ArrayList<String>(fontes);
        this.explicacao = explicacao;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getVersao() {
        return versao;
    }

    public String getStatus() {
        return status;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public List<Condicao> getAntecedentes() {
        return Collections.unmodifiableList(antecedentes);
    }

    public List<Conclusao> getConclusoes() {
        return Collections.unmodifiableList(conclusoes);
    }

    public List<String> getFontes() {
        return Collections.unmodifiableList(fontes);
    }

    public String getExplicacao() {
        return explicacao;
    }

    public boolean isAtiva() {
        return "ativa".equals(status);
    }
}
