package gerard.agente.modelador;

import gerard.agente.conhecimento.RuleActivationAudit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registro de auditoria de UMA chamada de {@code AgenteModelador.armazenarCaso}
 * — entrada, decisão, caso inserido, tempo de processamento. Montado DENTRO
 * de armazenarCaso, com os mesmos dados que o método já recebe/produz.
 *
 * Não carrega perfil antes/depois: {@code ModeloUsuario} não guarda os
 * contadores agregados que o schema de referência pede (ver
 * {@link ProfileSnapshot}) — quem monta esse retrato é
 * um consumidor de auditoria, observando o histórico de eventos, não o
 * próprio AgenteModelador.
 */
public final class ModeladorAuditData {
    private final String entradaEvento;
    private final String avaliacaoRecebida;
    private final String tipoErroRecebido;
    private final String acaoRecebida;
    private final String idUsuario;
    private final String problema;

    private final boolean decisaoAtualizarPerfil;
    private final boolean decisaoInserirCaso;
    private final boolean decisaoIncorporarPadrao;
    private final String justificativaDecisao;

    private final List<RuleActivationAudit> regrasAtivadas;

    private final DiagnosisSnapshot diagnosisAntes;
    private final DiagnosisSnapshot diagnosisDepois;
    private final StrategyDetectionAudit estrategiaDetectada;
    private final CaseInsertionAudit casoInserido;
    private final PatternIncorporationAudit padraoIncorporado;

    private final String eventoEmitido;
    private final long processingTimeMs;
    private final String erro;

    public ModeladorAuditData(String entradaEvento, String avaliacaoRecebida, String tipoErroRecebido,
            String acaoRecebida, String idUsuario, String problema, boolean decisaoAtualizarPerfil,
            boolean decisaoInserirCaso, boolean decisaoIncorporarPadrao, String justificativaDecisao,
            List<RuleActivationAudit> regrasAtivadas, DiagnosisSnapshot diagnosisAntes,
            DiagnosisSnapshot diagnosisDepois, StrategyDetectionAudit estrategiaDetectada,
            CaseInsertionAudit casoInserido, PatternIncorporationAudit padraoIncorporado, String eventoEmitido,
            long processingTimeMs, String erro) {
        this.entradaEvento = entradaEvento;
        this.avaliacaoRecebida = avaliacaoRecebida;
        this.tipoErroRecebido = tipoErroRecebido;
        this.acaoRecebida = acaoRecebida;
        this.idUsuario = idUsuario;
        this.problema = problema;
        this.decisaoAtualizarPerfil = decisaoAtualizarPerfil;
        this.decisaoInserirCaso = decisaoInserirCaso;
        this.decisaoIncorporarPadrao = decisaoIncorporarPadrao;
        this.justificativaDecisao = justificativaDecisao;
        this.regrasAtivadas = regrasAtivadas == null
                ? Collections.<RuleActivationAudit>emptyList() : new ArrayList<RuleActivationAudit>(regrasAtivadas);
        this.diagnosisAntes = diagnosisAntes;
        this.diagnosisDepois = diagnosisDepois;
        this.estrategiaDetectada = estrategiaDetectada;
        this.casoInserido = casoInserido;
        this.padraoIncorporado = padraoIncorporado;
        this.eventoEmitido = eventoEmitido;
        this.processingTimeMs = processingTimeMs;
        this.erro = erro;
    }

    public String getEntradaEvento() { return entradaEvento; }
    public String getAvaliacaoRecebida() { return avaliacaoRecebida; }
    public String getTipoErroRecebido() { return tipoErroRecebido; }
    public String getAcaoRecebida() { return acaoRecebida; }
    public String getIdUsuario() { return idUsuario; }
    public String getProblema() { return problema; }
    public boolean isDecisaoAtualizarPerfil() { return decisaoAtualizarPerfil; }
    public boolean isDecisaoInserirCaso() { return decisaoInserirCaso; }
    public boolean isDecisaoIncorporarPadrao() { return decisaoIncorporarPadrao; }
    public String getJustificativaDecisao() { return justificativaDecisao; }
    public List<RuleActivationAudit> getRegrasAtivadas() { return Collections.unmodifiableList(regrasAtivadas); }
    public DiagnosisSnapshot getDiagnosisAntes() { return diagnosisAntes; }
    public DiagnosisSnapshot getDiagnosisDepois() { return diagnosisDepois; }
    public StrategyDetectionAudit getEstrategiaDetectada() { return estrategiaDetectada; }
    public CaseInsertionAudit getCasoInserido() { return casoInserido; }
    public PatternIncorporationAudit getPadraoIncorporado() { return padraoIncorporado; }
    public String getEventoEmitido() { return eventoEmitido; }
    public long getProcessingTimeMs() { return processingTimeMs; }
    public String getErro() { return erro; }
}
