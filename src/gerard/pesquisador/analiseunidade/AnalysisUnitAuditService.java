package gerard.pesquisador.analiseunidade;

import gerard.agente.modelador.ModeladorAuditData;
import gerard.agente.monitor.MonitorAuditData;
import gerard.agente.zdp.ZdpAuditData;
import gerard.pesquisador.auditoria.AcaoUsuarioAudit;
import gerard.pesquisador.auditoria.AgentAuditEvent;
import gerard.pesquisador.auditoria.ClassificacaoEvento;
import gerard.pesquisador.auditoria.EscritorJsonSimples;
import gerard.pesquisador.auditoria.FalhaAuditoriaLogger;
import gerard.pesquisador.auditoria.IdentificacaoEvento;
import gerard.pesquisador.auditoria.OrigemAvaliacao;
import gerard.pesquisador.auditoria.OuvinteUnidadeAnalise;
import gerard.pesquisador.tentativa.ItemExplicacaoModelagem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Serviço da unidade de análise A-B-C-D (rodada 5, 2026-07-31,
 * PROMPT_CLAUDE_UNIDADE_ANALISE_ABCD_EXPLICACOES_OPCIONAIS.md). Observa
 * (não decide) o que {@link gerard.pesquisador.auditoria.AgentAuditService}
 * já produziu (A+B, via {@link OuvinteUnidadeAnalise}) e o que
 * {@code TelaArtefatoExplicativo} reporta do seu próprio ciclo de vida
 * (C+D) — nunca reprocessa MONITOR/ZDP/MODELADOR, nunca decide protocolo ou
 * avaliação sozinho.
 *
 * Cada unidade fica "aberta" (em memória, ainda não gravada em
 * unidades_analise.jsonl) enquanto for a MAIS RECENTE para sua tarefa
 * (usuário+categoria+papel-alvo) dentro do episódio atual — mesma convenção
 * de "mais recente" que {@code AgenteModelador.registrarExplicacaoNoUltimoDiagnostico}
 * já usa. Fecha (grava, uma linha por unidade) quando: (a) uma nova ação
 * canônica chega para a MESMA tarefa, (b) a tela de explicações fecha para
 * essa tarefa (salva ou cancelada), ou (c) o episódio termina.
 */
public final class AnalysisUnitAuditService implements OuvinteUnidadeAnalise {
    private final Writer unidadesDestino;
    private final Writer acoesDestino;
    private final Writer eventosDestino;
    private final Writer explicacoesDestino;
    private final FalhaAuditoriaLogger falhaLogger;

    private String episodeIdAtual;
    private String sessionIdAtual;
    private int contadorUnidades;
    private int contadorEventosTecnicos;
    private boolean disponibilidadeBotaoAtual;

    private final Map<String, UnidadeAnalise> unidadesAbertasPorTarefa = new LinkedHashMap<String, UnidadeAnalise>();
    private final Set<String> protocolInstancesProcessadas = new HashSet<String>();
    private final Set<String> tarefasJaExplicadasNaSessao = new HashSet<String>();
    private final Set<String> tarefasComFalhaTecnicaPendente = new HashSet<String>();
    private final List<UnidadeAnalise> unidadesFechadasNoEpisodioAtual = new ArrayList<UnidadeAnalise>();
    private int eventosTecnicosNoEpisodioAtual;
    private int divergenciasNoEpisodioAtual;

    public AnalysisUnitAuditService(File arquivoUnidades, File arquivoAcoes, File arquivoEventos,
            File arquivoExplicacoes, File diretorioLogs) throws IOException {
        this.unidadesDestino = abrir(arquivoUnidades);
        this.acoesDestino = abrir(arquivoAcoes);
        this.eventosDestino = abrir(arquivoEventos);
        this.explicacoesDestino = abrir(arquivoExplicacoes);
        this.falhaLogger = new FalhaAuditoriaLogger(diretorioLogs);
    }

    private static Writer abrir(File arquivo) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8));
    }

    public void definirContextoEpisodio(String episodeId, String sessionId) {
        this.episodeIdAtual = episodeId;
        this.sessionIdAtual = sessionId;
        this.contadorUnidades = 0;
        this.unidadesFechadasNoEpisodioAtual.clear();
        this.eventosTecnicosNoEpisodioAtual = 0;
        this.divergenciasNoEpisodioAtual = 0;
    }

    /**
     * Espelha, no instante em que uma nova unidade abre, se o botão da tela
     * de explicações está de fato visível/habilitado em Main.java —
     * reaproveita o sinal real (existeAoMenosUmPosicionamentoNoDiagramaVergnaud),
     * não recalcula a regra aqui.
     */
    public void definirDisponibilidadeBotaoAtual(boolean disponivel) {
        this.disponibilidadeBotaoAtual = disponivel;
    }

    public void aoFinalizarAvaliacao(AgentAuditEvent evento) {
        try {
            ClassificacaoEvento classificacao = evento.getClassificacao();
            IdentificacaoEvento identificacao = evento.getIdentificacao();
            if (classificacao == null || identificacao == null) {
                return;
            }
            String tarefaKey = chaveTarefa(identificacao, evento.getAcaoUsuario());
            if (classificacao.isCanonical()) {
                processarAcaoCanonica(evento, tarefaKey);
            } else {
                registrarEventoTecnico(evento, tarefaKey);
            }
        } catch (RuntimeException e) {
            falhaLogger.registrar("AnalysisUnitAuditService.aoFinalizarAvaliacao", e);
        }
    }

    private String chaveTarefa(IdentificacaoEvento identificacao, AcaoUsuarioAudit acao) {
        String userId = identificacao.getUserId();
        String categoria = identificacao.getCategoriaEsperada();
        String papelAlvo = acao == null ? null : acao.getPapelDestino();
        return (userId == null ? "" : userId) + "|" + (categoria == null ? "" : categoria) + ":"
                + (papelAlvo == null ? "" : papelAlvo);
    }

    private void processarAcaoCanonica(AgentAuditEvent evento, String tarefaKey) {
        MonitorAuditData monitorVerificacao = evento.getMonitor();
        if (monitorVerificacao == null || monitorVerificacao.getAvaliacao() == null) {
            // Gesto canônico sem avaliação C/E aplicável (ex.: origem canônica
            // mas ResultadoQuestionamento.naoAplicavel() — item que não exige
            // validação, como um numeral não-curado). A rodada 5 exige que TODA
            // instância de protocolo B receba exatamente uma avaliação C/E — sem
            // isso, não é uma instância de protocolo nesta definição, então não
            // abre nem fecha unidade nenhuma por causa dele.
            return;
        }
        IdentificacaoEvento identificacao = evento.getIdentificacao();
        String protocolInstanceId = "PI-" + identificacao.getGestureId();
        if (!protocolInstancesProcessadas.add(protocolInstanceId)) {
            // Defesa redundante (rodada 5): AgentAuditService já garante um único
            // evento canônico por gesto (debounce/idempotência das rodadas 3/4) —
            // isto só evita, mesmo numa regressão futura, que a MESMA instância de
            // protocolo produza duas unidades/avaliações aqui.
            return;
        }

        UnidadeAnalise unidadeAnterior = unidadesAbertasPorTarefa.get(tarefaKey);
        if (unidadeAnterior != null) {
            fecharEEscrever(unidadeAnterior);
            unidadesAbertasPorTarefa.remove(tarefaKey);
        }

        AcaoUsuarioAudit acao = evento.getAcaoUsuario();
        MonitorAuditData monitor = evento.getMonitor();
        ZdpAuditData zdp = evento.getZdp();
        ModeladorAuditData modelador = evento.getModelador();
        OrigemAvaliacao origem = evento.getClassificacao().getOrigem();
        TipoProtocolo tipoProtocolo = TipoProtocolo.deOrigem(origem);
        String motivoSemProtocolo = tipoProtocolo == null ? TipoProtocolo.motivoSemProtocolo(origem) : null;

        String resultado = monitor == null ? null : monitor.getAvaliacao();
        boolean caseInserted = modelador != null && modelador.getCasoInserido() != null
                && modelador.getCasoInserido().isInserted();
        if (evento.getComparacao() != null && Boolean.TRUE.equals(evento.getComparacao().isDivergence())) {
            divergenciasNoEpisodioAtual++;
        }

        contadorUnidades++;
        String analysisUnitId = "UA-" + (episodeIdAtual == null ? "SESSAO" : episodeIdAtual) + "-"
                + String.format("%04d", contadorUnidades);

        AcaoUsuarioProtocolo b = new AcaoUsuarioProtocolo(
                protocolInstanceId, analysisUnitId, identificacao.getEpisodeId(), identificacao.getSessionId(),
                identificacao.getUserId(), tipoProtocolo, motivoSemProtocolo,
                acao == null ? null : acao.getTipo(), identificacao.getCategoriaEsperada(),
                acao == null ? null : acao.getElemento(), acao == null ? null : acao.getPapelOrigem(),
                acao == null ? null : acao.getPapelDestino(), resultado, "protocol_final", true,
                monitor == null ? null : monitor.getTipoErro(), monitor == null ? null : monitor.getJustificativa(),
                monitor != null && monitor.getAvaliacao() != null ? 1 : 0, zdp != null ? 1 : 0,
                modelador != null ? 1 : 0, caseInserted ? 1 : 0, evento.getClassificacao().getIdempotencyKey(),
                identificacao.getGestureId(), identificacao.getActionId(), identificacao.getTimestamp(),
                identificacao.getTimestamp());
        escreverAcaoUsuarioProtocolo(b);

        AcaoComputador a = new AcaoComputador("A-" + analysisUnitId, "instruction",
                identificacao.getSituacaoProblema(), null, false, new ArrayList<String>());

        UnidadeAnalise unidade = new UnidadeAnalise(analysisUnitId, identificacao.getSessionId(),
                identificacao.getEpisodeId(), identificacao.getUserId(), contadorUnidades,
                identificacao.getTimestamp(), a, b);
        unidade.definirExplicacao(
                ComponenteC.naoAberto(disponibilidadeBotaoAtual, Collections.<PerguntaExplicativa>emptyList()),
                ComponenteD.naoAberto());
        unidadesAbertasPorTarefa.put(tarefaKey, unidade);
    }

    private void registrarEventoTecnico(AgentAuditEvent evento, String tarefaKey) {
        UnidadeAnalise unidadeAberta = unidadesAbertasPorTarefa.get(tarefaKey);
        contadorEventosTecnicos++;
        eventosTecnicosNoEpisodioAtual++;
        String technicalEventId = "TE-" + (episodeIdAtual == null ? "SESSAO" : episodeIdAtual) + "-"
                + String.format("%05d", contadorEventosTecnicos);
        String origem = evento.getClassificacao().getOrigem() == null
                ? null : evento.getClassificacao().getOrigem().paraTexto();
        String detalhes = "reavaliacao_reativa_do_agentauditservice;origin=" + origem
                + ";canonical=false";
        EventoTecnico eventoTecnico = new EventoTecnico(technicalEventId,
                unidadeAberta == null ? null : unidadeAberta.getAnalysisUnitId(),
                unidadeAberta == null ? null : unidadeAberta.getB().getProtocolInstanceId(),
                evento.getIdentificacao().getEpisodeId(), evento.getIdentificacao().getSessionId(),
                "reavaliacao_" + origem, evento.getIdentificacao().getTimestamp(), detalhes);
        if (unidadeAberta != null) {
            unidadeAberta.getA().adicionarEventoTecnico(technicalEventId);
        }
        escreverEventoTecnico(eventoTecnico);
    }

    // ---- Ciclo de vida da tela de explicações (C/D) ----------------------

    public static final class RespostaColetada {
        public final String dificuldade;
        public final String explicacao;

        public RespostaColetada(String dificuldade, String explicacao) {
            this.dificuldade = dificuldade;
            this.explicacao = explicacao;
        }
    }

    /** Botão acionado + tela aberta — chamado no construtor de TelaArtefatoExplicativo. */
    public synchronized void registrarTelaAberta(String usuarioId, String categoria, List<ItemExplicacaoModelagem> itens) {
        String abertura = agora();
        if (itens == null) {
            return;
        }
        for (ItemExplicacaoModelagem item : itens) {
            String tarefaKey = chaveTarefaExplicacao(usuarioId, categoria, item.getChavePapel());
            UnidadeAnalise unidade = unidadesAbertasPorTarefa.get(tarefaKey);
            if (unidade == null) {
                // Botão disponível não implica necessariamente unidade aberta para
                // TODOS os papéis (o botão liga quando HÁ AO MENOS UM posicionamento no
                // diagrama, não quando TODOS os papéis já foram avaliados) — sem
                // unidade aberta pra este papel, não há onde anexar C/D ainda; a tela
                // pode legitimamente ser aberta antes de qualquer avaliação para ele.
                continue;
            }
            List<PerguntaExplicativa> perguntas = new ArrayList<PerguntaExplicativa>();
            perguntas.add(new PerguntaExplicativa("Q-" + unidade.getAnalysisUnitId() + "-01",
                    "dificuldade_e_motivo_do_elemento",
                    "Qual foi a dificuldade percebida e o motivo, para o elemento \"" + item.getElemento() + "\"?",
                    true));
            perguntas.add(new PerguntaExplicativa("Q-" + unidade.getAnalysisUnitId() + "-02",
                    "explicacao_geral_da_modelagem",
                    "Explicação geral sobre como a modelagem foi construída.", true));
            unidade.definirExplicacao(
                    new ComponenteC(true, true, true, StatusExplicacao.OPENED_NOT_ANSWERED, perguntas, abertura, null),
                    unidade.getD());
        }
    }

    /**
     * Fecha a tela para as tarefas afetadas — salvou (com o que foi
     * realmente digitado/selecionado, coletado por TelaArtefatoExplicativo)
     * ou cancelou (respostasPorChavePapel/explicacaoGeral nulos).
     */
    public synchronized void registrarTelaFechada(String usuarioId, String categoria,
            List<ItemExplicacaoModelagem> itens, boolean salvou,
            Map<String, RespostaColetada> respostasPorChavePapel, String explicacaoGeral) {
        String fechamento = agora();
        if (itens == null) {
            return;
        }
        for (ItemExplicacaoModelagem item : itens) {
            String tarefaKey = chaveTarefaExplicacao(usuarioId, categoria, item.getChavePapel());
            UnidadeAnalise unidade = unidadesAbertasPorTarefa.get(tarefaKey);
            if (unidade == null) {
                continue;
            }
            ComponenteC cAtual = unidade.getC();
            boolean editada = tarefasJaExplicadasNaSessao.contains(tarefaKey);
            List<RespostaExplicativa> respostas = new ArrayList<RespostaExplicativa>();
            boolean algumConteudo = false;
            RespostaColetada respostaItem = respostasPorChavePapel == null
                    ? null : respostasPorChavePapel.get(item.getChavePapel());
            if (salvou && respostaItem != null && temConteudo(respostaItem.dificuldade, respostaItem.explicacao)) {
                respostas.add(new RespostaExplicativa("Q-" + unidade.getAnalysisUnitId() + "-01",
                        "dificuldade=" + vazio(respostaItem.dificuldade) + ";explicacao=" + vazio(respostaItem.explicacao),
                        true, editada, fechamento));
                algumConteudo = true;
            }
            if (salvou && temConteudo(explicacaoGeral)) {
                respostas.add(new RespostaExplicativa("Q-" + unidade.getAnalysisUnitId() + "-02",
                        explicacaoGeral, true, editada, fechamento));
                algumConteudo = true;
            }
            StatusExplicacao statusFinal;
            if (!salvou) {
                statusFinal = StatusExplicacao.OPENED_NOT_ANSWERED;
            } else if (algumConteudo && respostas.size() >= cAtual.getQuestions().size()) {
                statusFinal = StatusExplicacao.ANSWERED;
            } else if (algumConteudo) {
                statusFinal = StatusExplicacao.PARTIALLY_ANSWERED;
            } else {
                statusFinal = StatusExplicacao.OPENED_NOT_ANSWERED;
            }
            unidade.definirExplicacao(
                    new ComponenteC(true, true, true, statusFinal, cAtual.getQuestions(), cAtual.getOpenedAt(), fechamento),
                    new ComponenteD(statusFinal, respostas, algumConteudo ? fechamento : null));
            if (algumConteudo) {
                tarefasJaExplicadasNaSessao.add(tarefaKey);
            }
            if (tarefasComFalhaTecnicaPendente.remove(tarefaKey) && !algumConteudo) {
                unidade.forcarMotivoAusencia("technical_failure");
            }
            escreverExplicacao(unidade, salvou, editada, "tela_fechada");
            fecharEEscrever(unidade);
            unidadesAbertasPorTarefa.remove(tarefaKey);
        }
    }

    /** Falha técnica ao tentar salvar — a tela permanece aberta (usuário pode tentar de novo). */
    public synchronized void registrarFalhaTecnica(String usuarioId, String categoria,
            List<ItemExplicacaoModelagem> itens, Exception erro) {
        if (itens == null) {
            return;
        }
        for (ItemExplicacaoModelagem item : itens) {
            String tarefaKey = chaveTarefaExplicacao(usuarioId, categoria, item.getChavePapel());
            UnidadeAnalise unidade = unidadesAbertasPorTarefa.get(tarefaKey);
            if (unidade == null) {
                continue;
            }
            tarefasComFalhaTecnicaPendente.add(tarefaKey);
            escreverFalhaTecnica(unidade, erro);
        }
    }

    private String chaveTarefaExplicacao(String usuarioId, String categoria, String chavePapel) {
        return (usuarioId == null ? "" : usuarioId) + "|" + (categoria == null ? "" : categoria) + ":"
                + (chavePapel == null ? "" : chavePapel);
    }

    private boolean temConteudo(String... valores) {
        for (String v : valores) {
            if (v != null && v.trim().length() > 0) {
                return true;
            }
        }
        return false;
    }

    private String vazio(String v) { return v == null ? "" : v; }

    /**
     * Fecha todas as unidades ainda abertas e devolve o resumo do episódio
     * pra {@code cardinalidade_unidades_analise.tsv} (escrito por quem
     * chama, mesmo padrão de AgentAuditService.finalizarEpisodio/
     * EpisodeCardinalityReport das rodadas 3/4 — este serviço só sabe o
     * lado da unidade de análise).
     */
    public synchronized Map<String, Object> finalizarEpisodio() {
        List<UnidadeAnalise> restantes = new ArrayList<UnidadeAnalise>(unidadesAbertasPorTarefa.values());
        for (UnidadeAnalise unidade : restantes) {
            fecharEEscrever(unidade);
        }
        unidadesAbertasPorTarefa.clear();

        int unitsAB = 0, unitsABC = 0, unitsABCD = 0, corretas = 0, erradas = 0;
        int telasAbertas = 0, naoRespondidas = 0, parciais = 0, respondidas = 0;
        for (UnidadeAnalise u : unidadesFechadasNoEpisodioAtual) {
            String completude = u.getCompleteness();
            if ("A_B".equals(completude)) unitsAB++;
            else if ("A_B_C".equals(completude)) unitsABC++;
            else if ("A_B_C_D".equals(completude)) unitsABCD++;
            if ("C".equals(u.getB().getEvaluationResult())) corretas++;
            else if ("E".equals(u.getB().getEvaluationResult())) erradas++;
            if (u.getC().isScreenOpened()) {
                telasAbertas++;
                switch (u.getD().getStatus()) {
                    case OPENED_NOT_ANSWERED: naoRespondidas++; break;
                    case PARTIALLY_ANSWERED: parciais++; break;
                    case ANSWERED: respondidas++; break;
                    default: break;
                }
            }
        }
        Map<String, Object> resumo = new LinkedHashMap<String, Object>();
        resumo.put("analysis_units_total", unidadesFechadasNoEpisodioAtual.size());
        resumo.put("units_A_B", unitsAB);
        resumo.put("units_A_B_C", unitsABC);
        resumo.put("units_A_B_C_D", unitsABCD);
        resumo.put("protocol_instances_total", unidadesFechadasNoEpisodioAtual.size());
        resumo.put("correct_actions", corretas);
        resumo.put("error_actions", erradas);
        resumo.put("explanation_screens_opened", telasAbertas);
        resumo.put("opened_not_answered", naoRespondidas);
        resumo.put("partially_answered", parciais);
        resumo.put("answered", respondidas);
        resumo.put("technical_events", eventosTecnicosNoEpisodioAtual);
        resumo.put("divergences", divergenciasNoEpisodioAtual);
        return resumo;
    }

    private void fecharEEscrever(UnidadeAnalise unidade) {
        if (unidade.isFechada()) {
            return;
        }
        unidade.fechar(agora());
        unidadesFechadasNoEpisodioAtual.add(unidade);
        escreverUnidade(unidade);
    }

    private String agora() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
    }

    // ---- Serialização ------------------------------------------------

    private void escreverUnidade(UnidadeAnalise u) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("tipo_registro", "unidade_analise");
        m.put("analysis_unit_id", u.getAnalysisUnitId());
        m.put("session_id", u.getSessionId());
        m.put("episode_id", u.getEpisodeId());
        m.put("user_id", u.getUserId());
        m.put("sequence_order", u.getSequenceOrder());
        m.put("started_at", u.getStartedAt());
        m.put("finished_at", u.getFinishedAt());
        m.put("status", "completed");
        m.put("A_computer_action", mapaA(u.getA()));
        m.put("B_user_action", mapaB(u.getB()));
        m.put("C_explanation_questions", mapaC(u.getC()));
        m.put("D_user_explanations", mapaD(u.getD()));
        m.put("analysis_unit_completeness", u.getCompleteness());
        Map<String, Object> researchUse = new LinkedHashMap<String, Object>();
        researchUse.put("eligible_for_behavioral_analysis", u.isEligibleForBehavioralAnalysis());
        researchUse.put("eligible_for_explanatory_analysis", u.isEligibleForExplanatoryAnalysis());
        researchUse.put("missing_explanation_reason", u.getMissingExplanationReason());
        m.put("research_use", researchUse);
        escrever(unidadesDestino, m);
    }

    private Map<String, Object> mapaA(AcaoComputador a) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("action_id", a.getActionId());
        m.put("action_type", a.getActionType());
        m.put("content", a.getContent());
        m.put("message_template_id", a.getMessageTemplateId());
        m.put("feedback_presented", a.isFeedbackPresented());
        m.put("technical_events", new ArrayList<Object>(a.getTechnicalEventIds()));
        return m;
    }

    private Map<String, Object> mapaB(AcaoUsuarioProtocolo b) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("protocol_instance_id", b.getProtocolInstanceId());
        m.put("protocol_type", b.getProtocolType() == null ? null : b.getProtocolType().name());
        m.put("protocol_type_unavailable_reason", b.getProtocolTypeUnavailableReason());
        m.put("action_description", b.getActionDescription());
        Map<String, Object> contexto = new LinkedHashMap<String, Object>();
        contexto.put("category", b.getCategory());
        contexto.put("element", b.getElement());
        contexto.put("source_role", b.getSourceRole());
        contexto.put("target_role", b.getTargetRole());
        m.put("semantic_context", contexto);
        Map<String, Object> avaliacao = new LinkedHashMap<String, Object>();
        avaliacao.put("result", b.getEvaluationResult());
        avaliacao.put("evaluation_type", b.getEvaluationType());
        avaliacao.put("effective", b.isEffective());
        avaliacao.put("error_type", b.getErrorType());
        avaliacao.put("rationale", b.getRationale());
        m.put("evaluation", avaliacao);
        Map<String, Object> efeitos = new LinkedHashMap<String, Object>();
        efeitos.put("monitor_final_evaluations", b.getMonitorFinalEvaluations());
        efeitos.put("zdp_effective_decisions", b.getZdpEffectiveDecisions());
        efeitos.put("modeler_effective_updates", b.getModelerEffectiveUpdates());
        efeitos.put("cases_inserted", b.getCasesInserted());
        m.put("agent_effects", efeitos);
        m.put("idempotency_key", b.getIdempotencyKey());
        return m;
    }

    private Map<String, Object> mapaC(ComponenteC c) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("button_available", c.isButtonAvailable());
        m.put("button_activated", c.isButtonActivated());
        m.put("screen_opened", c.isScreenOpened());
        m.put("status", c.getStatus().paraTexto());
        List<Object> perguntas = new ArrayList<Object>();
        for (PerguntaExplicativa p : c.getQuestions()) {
            Map<String, Object> pm = new LinkedHashMap<String, Object>();
            pm.put("question_id", p.getQuestionId());
            pm.put("question_type", p.getQuestionType());
            pm.put("content", p.getContent());
            pm.put("presented_to_user", p.isPresentedToUser());
            perguntas.add(pm);
        }
        m.put("questions", perguntas);
        m.put("opened_at", c.getOpenedAt());
        m.put("closed_at", c.getClosedAt());
        return m;
    }

    private Map<String, Object> mapaD(ComponenteD d) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("status", d.getStatus().paraTexto());
        List<Object> respostas = new ArrayList<Object>();
        for (RespostaExplicativa r : d.getResponses()) {
            Map<String, Object> rm = new LinkedHashMap<String, Object>();
            rm.put("question_id", r.getQuestionId());
            rm.put("content", r.getContent());
            rm.put("saved", r.isSaved());
            rm.put("edited_later", r.isEditedLater());
            rm.put("timestamp", r.getTimestamp());
            respostas.add(rm);
        }
        m.put("responses", respostas);
        m.put("completed_at", d.getCompletedAt());
        return m;
    }

    private void escreverAcaoUsuarioProtocolo(AcaoUsuarioProtocolo b) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("tipo_registro", "acao_usuario_protocolo");
        m.putAll(mapaB(b));
        m.put("analysis_unit_id", b.getAnalysisUnitId());
        m.put("episode_id", b.getEpisodeId());
        m.put("session_id", b.getSessionId());
        m.put("user_id", b.getUserId());
        m.put("gesture_id", b.getGestureId());
        m.put("action_id", b.getActionId());
        m.put("started_at", b.getStartedAt());
        m.put("finished_at", b.getFinishedAt());
        escrever(acoesDestino, m);
    }

    private void escreverEventoTecnico(EventoTecnico e) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("tipo_registro", "evento_tecnico");
        m.put("technical_event_id", e.getTechnicalEventId());
        m.put("analysis_unit_id", e.getAnalysisUnitId());
        m.put("protocol_instance_id", e.getProtocolInstanceId());
        m.put("episode_id", e.getEpisodeId());
        m.put("session_id", e.getSessionId());
        m.put("event_type", e.getEventType());
        m.put("timestamp", e.getTimestamp());
        m.put("counts_as_analysis_unit", false);
        m.put("counts_as_protocol_instance", false);
        m.put("effective", false);
        m.put("details", e.getDetails());
        escrever(eventosDestino, m);
    }

    private void escreverExplicacao(UnidadeAnalise u, boolean salvou, boolean editada, String evento) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("tipo_registro", "interacao_tela_explicacoes");
        m.put("analysis_unit_id", u.getAnalysisUnitId());
        m.put("episode_id", u.getEpisodeId());
        m.put("session_id", u.getSessionId());
        m.put("user_id", u.getUserId());
        m.put("protocol_instance_id", u.getB().getProtocolInstanceId());
        m.put("event", evento);
        m.put("saved", salvou);
        m.put("edited_later", editada);
        m.put("status", u.getD().getStatus().paraTexto());
        m.put("timestamp", agora());
        escrever(explicacoesDestino, m);
    }

    private void escreverFalhaTecnica(UnidadeAnalise u, Exception erro) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("tipo_registro", "interacao_tela_explicacoes");
        m.put("analysis_unit_id", u.getAnalysisUnitId());
        m.put("episode_id", u.getEpisodeId());
        m.put("session_id", u.getSessionId());
        m.put("user_id", u.getUserId());
        m.put("protocol_instance_id", u.getB().getProtocolInstanceId());
        m.put("event", "technical_failure");
        m.put("saved", false);
        m.put("error_message", erro == null ? null : String.valueOf(erro.getMessage()));
        m.put("timestamp", agora());
        escrever(explicacoesDestino, m);
    }

    private void escrever(Writer destino, Map<String, Object> mapa) {
        try {
            synchronized (destino) {
                destino.write(EscritorJsonSimples.escrever(mapa));
                destino.write("\n");
                destino.flush();
            }
        } catch (IOException e) {
            falhaLogger.registrar("AnalysisUnitAuditService.escrever:" + mapa.get("tipo_registro"), e);
        }
    }

    public void fechar() {
        fecharSilenciosamente(unidadesDestino);
        fecharSilenciosamente(acoesDestino);
        fecharSilenciosamente(eventosDestino);
        fecharSilenciosamente(explicacoesDestino);
    }

    private void fecharSilenciosamente(Writer w) {
        try {
            w.close();
        } catch (IOException e) {
            falhaLogger.registrar("AnalysisUnitAuditService.fechar", e);
        }
    }
}
