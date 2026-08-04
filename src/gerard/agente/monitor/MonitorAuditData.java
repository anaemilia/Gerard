package gerard.agente.monitor;

import gerard.agente.conhecimento.RuleActivationAudit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registro de auditoria de UMA avaliação do AgenteMonitor — entrada,
 * decisão, regras consultadas, saída e tempo de processamento. Montado
 * DENTRO de cada método avaliar* do AgenteMonitor, com os mesmos parâmetros
 * que o método já recebe: não é uma segunda decisão, é a mesma decisão
 * descrita pra auditoria.
 *
 * AgenteMonitor é stateless por design (ver javadoc da classe — cada
 * avaliação é independente, sem memória entre chamadas). Por isso não há
 * "estado antes"/"estado depois" real aqui: quem acumula erro consecutivo é
 * o AgenteZDP (ver ZdpAuditData). Os campos correspondentes ficam vazios,
 * documentados no motivo.
 */
public final class MonitorAuditData {
    private final String acaoRecebida;
    private final String elemento;
    private final String destino;
    private final String categoriaAtiva;
    private final String estadoSemanticoAtual;

    private final String avaliacao;
    private final String tipoErro;
    private final String papelEsperado;
    private final String papelRecebido;
    private final String gravidade;
    private final double confianca;
    private final String justificativa;

    private final List<RuleActivationAudit> regrasAtivadas;

    private final String eventoEmitido;
    private final boolean acaoAceita;
    private final boolean novaTentativaPermitida;
    private final boolean encaminhadoParaModelador;
    private final boolean encaminhadoParaZdp;

    private final long processingTimeMs;
    private final String erro;

    public MonitorAuditData(String acaoRecebida, String elemento, String destino, String categoriaAtiva,
            String estadoSemanticoAtual, String avaliacao, String tipoErro, String papelEsperado,
            String papelRecebido, String gravidade, double confianca, String justificativa,
            List<RuleActivationAudit> regrasAtivadas, String eventoEmitido, boolean acaoAceita,
            boolean novaTentativaPermitida, boolean encaminhadoParaModelador, boolean encaminhadoParaZdp,
            long processingTimeMs, String erro) {
        this.acaoRecebida = acaoRecebida;
        this.elemento = elemento;
        this.destino = destino;
        this.categoriaAtiva = categoriaAtiva;
        this.estadoSemanticoAtual = estadoSemanticoAtual;
        this.avaliacao = avaliacao;
        this.tipoErro = tipoErro;
        this.papelEsperado = papelEsperado;
        this.papelRecebido = papelRecebido;
        this.gravidade = gravidade;
        this.confianca = confianca;
        this.justificativa = justificativa;
        this.regrasAtivadas = regrasAtivadas == null
                ? Collections.<RuleActivationAudit>emptyList() : new ArrayList<RuleActivationAudit>(regrasAtivadas);
        this.eventoEmitido = eventoEmitido;
        this.acaoAceita = acaoAceita;
        this.novaTentativaPermitida = novaTentativaPermitida;
        this.encaminhadoParaModelador = encaminhadoParaModelador;
        this.encaminhadoParaZdp = encaminhadoParaZdp;
        this.processingTimeMs = processingTimeMs;
        this.erro = erro;
    }

    public String getAcaoRecebida() { return acaoRecebida; }
    public String getElemento() { return elemento; }
    public String getDestino() { return destino; }
    public String getCategoriaAtiva() { return categoriaAtiva; }
    public String getEstadoSemanticoAtual() { return estadoSemanticoAtual; }
    public String getAvaliacao() { return avaliacao; }
    public String getTipoErro() { return tipoErro; }
    public String getPapelEsperado() { return papelEsperado; }
    public String getPapelRecebido() { return papelRecebido; }
    public String getGravidade() { return gravidade; }
    public double getConfianca() { return confianca; }
    public String getJustificativa() { return justificativa; }
    public List<RuleActivationAudit> getRegrasAtivadas() { return Collections.unmodifiableList(regrasAtivadas); }
    public String getEventoEmitido() { return eventoEmitido; }
    public boolean isAcaoAceita() { return acaoAceita; }
    public boolean isNovaTentativaPermitida() { return novaTentativaPermitida; }
    public boolean isEncaminhadoParaModelador() { return encaminhadoParaModelador; }
    public boolean isEncaminhadoParaZdp() { return encaminhadoParaZdp; }
    public long getProcessingTimeMs() { return processingTimeMs; }
    public String getErro() { return erro; }
}
