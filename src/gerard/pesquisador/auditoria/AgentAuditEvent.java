package gerard.pesquisador.auditoria;

import gerard.agente.conhecimento.RuleActivationAudit;
import gerard.agente.modelador.CaseInsertionAudit;
import gerard.agente.modelador.DiagnosisSnapshot;
import gerard.agente.modelador.ModeladorAuditData;
import gerard.agente.modelador.PatternIncorporationAudit;
import gerard.agente.modelador.ProfileSnapshot;
import gerard.agente.modelador.StrategyDetectionAudit;
import gerard.agente.monitor.MonitorAuditData;
import gerard.agente.zdp.ZdpAuditData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Um evento completo de auditoria (schema 2.0.0, 2026-07-31) — uma
 * avaliação atômica (canônica ou reativa), com o raciocínio dos três
 * agentes reais. Única fonte usada tanto por {@link JsonlAgentAuditWriter}
 * quanto por {@link HumanReadableAgentAuditWriter} (via {@link #paraMapa()}).
 *
 * Separa explicitamente (pedido da usuária): perfil REAL persistido
 * ({@link UserProfileSnapshot}, sempre indisponível hoje) de contadores
 * técnicos calculados pelo serviço de auditoria — em DOIS grupos:
 * "technical_evaluation_counters" (toda avaliação, canônica+reativa) e
 * "canonical_user_action_counters" (só gestos canônicos — é o único que
 * pode, de fato, alimentar ZDP/Modelador/mineração).
 */
public final class AgentAuditEvent {
    private final IdentificacaoEvento identificacao;
    private final ClassificacaoEvento classificacao;
    private final AcaoUsuarioAudit acaoUsuario;
    private final MonitorAuditData monitor;
    private final ZdpAuditData zdp;
    private final ModeladorAuditData modelador;
    private final UserProfileSnapshot userProfileAntes;
    private final UserProfileSnapshot userProfileDepois;
    private final ProfileSnapshot technicalCountersAntes;
    private final ProfileSnapshot technicalCountersDepois;
    private final ProfileSnapshot canonicalCountersAntes;
    private final ProfileSnapshot canonicalCountersDepois;
    private final InteractionComparisonAudit comparacao;

    public AgentAuditEvent(IdentificacaoEvento identificacao, ClassificacaoEvento classificacao,
            AcaoUsuarioAudit acaoUsuario, MonitorAuditData monitor, ZdpAuditData zdp,
            ModeladorAuditData modelador, UserProfileSnapshot userProfileAntes, UserProfileSnapshot userProfileDepois,
            ProfileSnapshot technicalCountersAntes, ProfileSnapshot technicalCountersDepois,
            ProfileSnapshot canonicalCountersAntes, ProfileSnapshot canonicalCountersDepois,
            InteractionComparisonAudit comparacao) {
        this.identificacao = identificacao;
        this.classificacao = classificacao;
        this.acaoUsuario = acaoUsuario;
        this.monitor = monitor;
        this.zdp = zdp;
        this.modelador = modelador;
        this.userProfileAntes = userProfileAntes;
        this.userProfileDepois = userProfileDepois;
        this.technicalCountersAntes = technicalCountersAntes;
        this.technicalCountersDepois = technicalCountersDepois;
        this.canonicalCountersAntes = canonicalCountersAntes;
        this.canonicalCountersDepois = canonicalCountersDepois;
        this.comparacao = comparacao;
    }

    public IdentificacaoEvento getIdentificacao() { return identificacao; }
    public ClassificacaoEvento getClassificacao() { return classificacao; }
    public AcaoUsuarioAudit getAcaoUsuario() { return acaoUsuario; }
    public MonitorAuditData getMonitor() { return monitor; }
    public ZdpAuditData getZdp() { return zdp; }
    public ModeladorAuditData getModelador() { return modelador; }
    public InteractionComparisonAudit getComparacao() { return comparacao; }
    public ProfileSnapshot getCanonicalCountersDepois() { return canonicalCountersDepois; }

    public Map<String, Object> paraMapa() {
        Map<String, Object> raiz = new LinkedHashMap<String, Object>();
        // Bug real encontrado pelo SchemaValidator na rodada 4 (2026-07-31):
        // este campo ficava hardcoded em "2.0.0" desde a rodada 2, nunca
        // acompanhando a versao real passada por AgentAuditService — agora
        // le da mesma identificacao que ja guarda a versao correta.
        raiz.put("schema_version", identificacao == null ? null : identificacao.getSchemaVersion());
        raiz.put("tipo_registro", "evento");
        raiz.put("identificacao", mapaIdentificacao());
        raiz.put("classificacao_evento", mapaClassificacao());
        raiz.put("acao_usuario", mapaAcaoUsuario());
        raiz.put("user_profile_before", mapaUserProfile(userProfileAntes));
        raiz.put("audit_session_counters_before", mapaCounters(technicalCountersAntes, canonicalCountersAntes));
        raiz.put("agents", mapaAgentes());
        raiz.put("user_profile_after", mapaUserProfile(userProfileDepois));
        raiz.put("audit_session_counters_after", mapaCounters(technicalCountersDepois, canonicalCountersDepois));
        raiz.put("interaction_result", mapaComparacao());
        return raiz;
    }

    private Map<String, Object> mapaIdentificacao() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (identificacao == null) {
            return m;
        }
        m.put("schema_version", identificacao.getSchemaVersion());
        m.put("event_id", identificacao.getEventId());
        m.put("episode_id", identificacao.getEpisodeId());
        m.put("session_id", identificacao.getSessionId());
        m.put("gesture_id", identificacao.getGestureId());
        m.put("action_id", identificacao.getActionId());
        m.put("rejection_sequence_id", identificacao.getRejectionSequenceId());
        m.put("evaluation_id", identificacao.getEvaluationId());
        m.put("step_user", identificacao.getStepUser());
        m.put("step_internal", identificacao.getStepInternal());
        m.put("timestamp", identificacao.getTimestamp());
        m.put("user_id", identificacao.getUserId());
        m.put("problem_id", identificacao.getProblemId());
        m.put("situacao_problema", identificacao.getSituacaoProblema());
        m.put("category", identificacao.getCategoriaEsperada());
        m.put("unknown_role", identificacao.getPapelDesconhecido());
        m.put("system_version", identificacao.getSystemVersion());
        m.put("rules_base_version", identificacao.getRulesBaseVersion());
        m.put("execution_sequence", identificacao.getExecutionSequence());
        return m;
    }

    private Map<String, Object> mapaClassificacao() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (classificacao == null) {
            return m;
        }
        m.put("origin", classificacao.getOrigem() == null ? null : classificacao.getOrigem().paraTexto());
        m.put("canonical", classificacao.isCanonical());
        m.put("reactive_evaluation", classificacao.isReactiveEvaluation());
        m.put("counts_for_user_profile", classificacao.isCountsForUserProfile());
        m.put("counts_for_error_sequence", classificacao.isCountsForErrorSequence());
        m.put("counts_for_case_base", classificacao.isCountsForCaseBase());
        m.put("counts_for_rule_learning", classificacao.isCountsForRuleLearning());
        m.put("idempotency_key", classificacao.getIdempotencyKey());
        return m;
    }

    private Map<String, Object> mapaAcaoUsuario() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (acaoUsuario == null) {
            return m;
        }
        m.put("type", acaoUsuario.getTipo());
        m.put("element", acaoUsuario.getElemento());
        m.put("value", acaoUsuario.getValor());
        m.put("source_role", acaoUsuario.getPapelOrigem());
        m.put("target_role", acaoUsuario.getPapelDestino());
        m.put("source_artifact", acaoUsuario.getArtefatoOrigem());
        m.put("target_artifact", acaoUsuario.getArtefatoDestino());
        Map<String, Object> coordOrigem = new LinkedHashMap<String, Object>();
        coordOrigem.put("x", acaoUsuario.getCoordenadaOrigemX());
        coordOrigem.put("y", acaoUsuario.getCoordenadaOrigemY());
        m.put("source_coordinates", coordOrigem);
        Map<String, Object> coordDestino = new LinkedHashMap<String, Object>();
        coordDestino.put("x", acaoUsuario.getCoordenadaDestinoX());
        coordDestino.put("y", acaoUsuario.getCoordenadaDestinoY());
        m.put("target_coordinates", coordDestino);
        m.put("interface_state_before", acaoUsuario.getEstadoInterfaceAntes());
        m.put("interface_state_after", acaoUsuario.getEstadoInterfaceDepois());
        m.put("expected_result", acaoUsuario.getResultadoEsperado());
        m.put("observed_result", acaoUsuario.getResultadoObservado());
        return m;
    }

    private Map<String, Object> mapaUserProfile(UserProfileSnapshot perfil) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("available", perfil != null && perfil.isAvailable());
        m.put("value", null);
        m.put("unavailable_reason", perfil == null ? null : perfil.getUnavailableReason());
        return m;
    }

    private Map<String, Object> mapaCounters(ProfileSnapshot tecnico, ProfileSnapshot canonico) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("technical_evaluation_counters", mapaProfile(tecnico));
        m.put("canonical_user_action_counters", mapaProfile(canonico));
        return m;
    }

    private Map<String, Object> mapaProfile(ProfileSnapshot perfil) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (perfil == null) {
            return m;
        }
        m.put("total_errors", perfil.getTotalErros());
        m.put("consecutive_errors", perfil.getErrosConsecutivos());
        m.put("category_errors", perfil.getErrosCategoria());
        m.put("positioning_errors", perfil.getErrosPosicionamento());
        m.put("sign_errors", perfil.getErrosSinal());
        m.put("calculation_errors", perfil.getErrosCalculo());
        m.put("total_correct", perfil.getTotalAcertos());
        m.put("consecutive_correct", perfil.getAcertosConsecutivos());
        m.put("helps_used", perfil.getAjudasUsadas());
        m.put("last_evaluation", perfil.getUltimaAvaliacao());
        m.put("last_action_family", perfil.getUltimaFamiliaAcao());
        return m;
    }

    private Map<String, Object> mapaAgentes() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("MONITOR", mapaMonitor());
        m.put("ZDP", mapaZdp());
        m.put("MODELADOR", mapaModelador());
        return m;
    }

    private Map<String, Object> mapaMonitor() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (monitor == null) {
            return m;
        }
        Map<String, Object> entrada = new LinkedHashMap<String, Object>();
        entrada.put("gesture_id", identificacao == null ? null : identificacao.getGestureId());
        entrada.put("action_id", identificacao == null ? null : identificacao.getActionId());
        entrada.put("rejection_sequence_id", identificacao == null ? null : identificacao.getRejectionSequenceId());
        entrada.put("evaluation_id", identificacao == null ? null : identificacao.getEvaluationId());
        entrada.put("origin", classificacao == null || classificacao.getOrigem() == null
                ? null : classificacao.getOrigem().paraTexto());
        entrada.put("canonical", classificacao != null && classificacao.isCanonical());
        entrada.put("action_type", monitor.getAcaoRecebida());
        entrada.put("element", monitor.getElemento());
        entrada.put("target", monitor.getDestino());
        entrada.put("category", monitor.getCategoriaAtiva());
        entrada.put("semantic_state", monitor.getEstadoSemanticoAtual());
        m.put("input", entrada);

        Map<String, Object> estadoAntes = new LinkedHashMap<String, Object>();
        estadoAntes.put("stateless", true);
        m.put("state_before", estadoAntes);

        Map<String, Object> decisao = new LinkedHashMap<String, Object>();
        decisao.put("evaluation", monitor.getAvaliacao());
        decisao.put("error_type", monitor.getTipoErro());
        decisao.put("expected_role", monitor.getPapelEsperado());
        decisao.put("received_role", monitor.getPapelRecebido());
        decisao.put("severity", monitor.getGravidade());
        decisao.put("confidence", monitor.getConfianca());
        decisao.put("rationale", monitor.getJustificativa());
        decisao.put("canonical", classificacao != null && classificacao.isCanonical());
        m.put("decision", decisao);

        m.put("rules_fired", mapaRegras(monitor.getRegrasAtivadas()));

        Map<String, Object> saida = new LinkedHashMap<String, Object>();
        saida.put("event", monitor.getEventoEmitido());
        saida.put("action_accepted", monitor.isAcaoAceita());
        saida.put("allow_retry", monitor.isNovaTentativaPermitida());
        saida.put("forward_to_modeler", monitor.isEncaminhadoParaModelador());
        saida.put("forward_to_zdp", monitor.isEncaminhadoParaZdp());
        m.put("output", saida);

        Map<String, Object> estadoDepois = new LinkedHashMap<String, Object>();
        estadoDepois.put("stateless", true);
        m.put("state_after", estadoDepois);
        m.put("state_change", new LinkedHashMap<String, Object>());

        m.put("processing_time_ms", monitor.getProcessingTimeMs());
        m.put("error", monitor.getErro());
        return m;
    }

    private Map<String, Object> mapaZdp() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (zdp == null) {
            return m;
        }
        Map<String, Object> entrada = new LinkedHashMap<String, Object>();
        entrada.put("gesture_id", identificacao == null ? null : identificacao.getGestureId());
        entrada.put("action_id", identificacao == null ? null : identificacao.getActionId());
        entrada.put("rejection_sequence_id", identificacao == null ? null : identificacao.getRejectionSequenceId());
        entrada.put("canonical", classificacao != null && classificacao.isCanonical());
        entrada.put("event", zdp.getEventoRecebido());
        entrada.put("evaluation", zdp.getAvaliacao());
        entrada.put("consecutive_user_errors_before", zdp.getErrosConsecutivosAntes());
        entrada.put("total_user_errors_before", zdp.getErrosTotaisAntes());
        entrada.put("previous_help", zdp.isAjudaPreviaAntes());
        m.put("input", entrada);

        Map<String, Object> estadoAntes = new LinkedHashMap<String, Object>();
        estadoAntes.put("consecutive_errors", zdp.getErrosConsecutivosAntes());
        estadoAntes.put("total_errors", zdp.getErrosTotaisAntes());
        estadoAntes.put("previous_help", zdp.isAjudaPreviaAntes());
        m.put("state_before", estadoAntes);

        Map<String, Object> decisao = new LinkedHashMap<String, Object>();
        decisao.put("layer", zdp.getCamadaEstrategia());
        decisao.put("intervention", zdp.getIntervencao());
        decisao.put("help_level", zdp.getNivelAjuda());
        decisao.put("provide_answer", zdp.isForneceResposta());
        decisao.put("block_incorrect_action", zdp.isBloqueiaAcao());
        decisao.put("allow_retry", zdp.isPermiteNovaTentativa());
        decisao.put("rationale", zdp.getJustificativa());
        m.put("decision", decisao);

        m.put("rules_fired", mapaRegras(zdp.getRegrasAtivadas()));

        Map<String, Object> estadoDepois = new LinkedHashMap<String, Object>();
        estadoDepois.put("consecutive_errors", zdp.getErrosConsecutivosDepois());
        estadoDepois.put("total_errors", zdp.getErrosTotaisDepois());
        estadoDepois.put("previous_help", zdp.isAjudaPreviaDepois());
        m.put("state_after", estadoDepois);
        Map<String, Object> mudanca = new LinkedHashMap<String, Object>();
        mudanca.put("consecutive_errors", diferenca(zdp.getErrosConsecutivosAntes(), zdp.getErrosConsecutivosDepois()));
        mudanca.put("total_errors", diferenca(zdp.getErrosTotaisAntes(), zdp.getErrosTotaisDepois()));
        m.put("state_change", mudanca);

        m.put("processing_time_ms", zdp.getProcessingTimeMs());
        m.put("error", zdp.getErro());
        return m;
    }

    private Map<String, Object> diferenca(int antes, int depois) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("before", antes);
        m.put("after", depois);
        return m;
    }

    private Map<String, Object> mapaModelador() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (modelador == null) {
            return m;
        }
        Map<String, Object> entrada = new LinkedHashMap<String, Object>();
        entrada.put("gesture_id", identificacao == null ? null : identificacao.getGestureId());
        entrada.put("action_id", identificacao == null ? null : identificacao.getActionId());
        entrada.put("rejection_sequence_id", identificacao == null ? null : identificacao.getRejectionSequenceId());
        entrada.put("evaluation_id", identificacao == null ? null : identificacao.getEvaluationId());
        entrada.put("canonical", classificacao != null && classificacao.isCanonical());
        entrada.put("idempotency_key", classificacao == null ? null : classificacao.getIdempotencyKey());
        entrada.put("event", modelador.getEntradaEvento());
        entrada.put("evaluation", monitor == null ? null : monitor.getAvaliacao());
        entrada.put("error_type", monitor == null ? null : monitor.getTipoErro());
        entrada.put("expected_role", monitor == null ? null : monitor.getPapelEsperado());
        entrada.put("received_role", monitor == null ? null : monitor.getPapelRecebido());
        entrada.put("action", modelador.getAcaoRecebida());
        entrada.put("intervention", zdp == null ? null : zdp.getIntervencao());
        entrada.put("help_level", zdp == null ? null : zdp.getNivelAjuda());
        entrada.put("user_id", modelador.getIdUsuario());
        entrada.put("problem_id", identificacao == null ? null : identificacao.getProblemId());
        entrada.put("category", identificacao == null ? null : identificacao.getCategoriaEsperada());
        m.put("input", entrada);

        Map<String, Object> decisao = new LinkedHashMap<String, Object>();
        decisao.put("update_profile", modelador.isDecisaoAtualizarPerfil());
        decisao.put("create_case", modelador.isDecisaoInserirCaso());
        decisao.put("incorporate_pattern", modelador.isDecisaoIncorporarPadrao());
        decisao.put("rationale", modelador.getJustificativaDecisao());
        m.put("decision", decisao);

        m.put("rules_fired", mapaRegras(modelador.getRegrasAtivadas()));
        m.put("diagnosis_before", mapaDiagnostico(modelador.getDiagnosisAntes()));
        m.put("detected_strategy", mapaEstrategia(modelador.getEstrategiaDetectada()));
        m.put("new_case_inserted", mapaCaso(modelador.getCasoInserido()));
        m.put("rule_or_pattern_incorporated", mapaPadrao(modelador.getPadraoIncorporado()));
        m.put("diagnosis_after", mapaDiagnostico(modelador.getDiagnosisDepois()));

        Map<String, Object> saida = new LinkedHashMap<String, Object>();
        saida.put("event", modelador.getEventoEmitido());
        m.put("output", saida);

        m.put("processing_time_ms", modelador.getProcessingTimeMs());
        m.put("error", modelador.getErro());
        return m;
    }

    private Map<String, Object> mapaDiagnostico(DiagnosisSnapshot diagnostico) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (diagnostico == null) {
            return m;
        }
        m.put("available", diagnostico.isDisponivel());
        m.put("diagnosis_id", diagnostico.getDiagnosisId());
        m.put("description", diagnostico.getDescricao());
        m.put("confidence", diagnostico.getConfianca());
        m.put("evidence", diagnostico.getEvidencias());
        m.put("unavailable_reason", diagnostico.getMotivoIndisponivel());
        return m;
    }

    private Map<String, Object> mapaEstrategia(StrategyDetectionAudit estrategia) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (estrategia == null) {
            return m;
        }
        m.put("available", estrategia.isDisponivel());
        m.put("strategy_id", estrategia.getStrategyId());
        m.put("name", estrategia.getNome());
        m.put("confidence", estrategia.getConfianca());
        m.put("evidence", estrategia.getEvidencias());
        m.put("unavailable_reason", estrategia.getMotivoIndisponivel());
        return m;
    }

    private Map<String, Object> mapaCaso(CaseInsertionAudit caso) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (caso == null) {
            return m;
        }
        m.put("inserted", caso.isInserted());
        m.put("case_id", caso.getCaseId());
        m.put("idempotency_key", classificacao == null ? null : classificacao.getIdempotencyKey());
        m.put("duplicate", !caso.isInserted() && classificacao != null && classificacao.isCanonical());
        m.put("case_base", caso.getCaseBase());
        m.put("gesture_id", identificacao == null ? null : identificacao.getGestureId());
        m.put("action_id", identificacao == null ? null : identificacao.getActionId());
        m.put("rejection_sequence_id", identificacao == null ? null : identificacao.getRejectionSequenceId());
        m.put("attributes", caso.getAtributos());
        m.put("reason", caso.getMotivoSemSimilaridade());
        return m;
    }

    private Map<String, Object> mapaPadrao(PatternIncorporationAudit padrao) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (padrao == null) {
            return m;
        }
        m.put("incorporated", padrao.isIncorporated());
        m.put("unavailable_reason", padrao.getMotivoIndisponivel());
        return m;
    }

    private List<Map<String, Object>> mapaRegras(List<RuleActivationAudit> regras) {
        List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();
        if (regras == null) {
            return lista;
        }
        for (RuleActivationAudit regra : regras) {
            Map<String, Object> m = new LinkedHashMap<String, Object>();
            m.put("rule_id", regra.getRuleId());
            m.put("type", regra.getTipo());
            m.put("source", regra.getFonte());
            m.put("version", regra.getVersao());
            m.put("antecedents_satisfied", regra.getAntecedentesSatisfeitos());
            m.put("conclusion", regra.getConclusao());
            m.put("priority", regra.getPrioridade());
            m.put("used_in_final_decision", regra.isUsedInFinalDecision());
            m.put("discard_reason", regra.getMotivoDescarte());
            lista.add(m);
        }
        return lista;
    }

    private Map<String, Object> mapaComparacao() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (comparacao == null) {
            m.put("comparison_status", "not_evaluable");
            m.put("expected", null);
            m.put("observed", new LinkedHashMap<String, Object>());
            m.put("divergence", null);
            m.put("divergence_reason", "expected_result_not_available");
            return m;
        }
        boolean avaliavel = comparacao.getAvaliacaoEsperada() != null;
        m.put("comparison_status", avaliavel ? "evaluated" : "not_evaluable");
        if (avaliavel) {
            Map<String, Object> esperado = new LinkedHashMap<String, Object>();
            esperado.put("monitor_evaluation", comparacao.getAvaliacaoEsperada());
            esperado.put("zdp_intervention", comparacao.getIntervencaoEsperada());
            m.put("expected", esperado);
            m.put("divergence", comparacao.isDivergence());
            m.put("divergence_type", comparacao.getDivergenceType());
            m.put("divergence_reason", comparacao.getDivergenceReason());
        } else {
            m.put("expected", null);
            m.put("divergence", null);
            m.put("divergence_reason", "expected_result_not_available");
        }
        Map<String, Object> observado = new LinkedHashMap<String, Object>();
        observado.put("monitor_evaluation", comparacao.getAvaliacaoObservada());
        observado.put("zdp_intervention", comparacao.getIntervencaoObservada());
        observado.put("message_displayed", comparacao.getMensagemExibida());
        m.put("observed", observado);
        m.put("responsible_agent", comparacao.getAgenteResponsavel());
        return m;
    }
}
