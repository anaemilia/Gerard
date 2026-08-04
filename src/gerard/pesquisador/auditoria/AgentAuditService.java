package gerard.pesquisador.auditoria;

import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.ModeladorAuditData;
import gerard.agente.modelador.OuvinteAuditoriaAgenteModelador;
import gerard.agente.modelador.ProfileSnapshot;
import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.monitor.AgenteMonitor;
import gerard.agente.monitor.MonitorAuditData;
import gerard.agente.monitor.OuvinteAuditoriaAgenteMonitor;
import gerard.agente.zdp.AgenteZDP;
import gerard.agente.zdp.OuvinteAuditoriaAgenteZDP;
import gerard.agente.zdp.ZdpAuditData;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de auditoria: observa (não decide) o que os três agentes reais já
 * produziram por avaliação, correlaciona num AgentAuditEvent e grava nos
 * dois arquivos (JSONL + legível), sempre a partir do MESMO objeto.
 *
 * Correção 2026-07-31: distingue gesto canônico do usuário de reavaliação
 * reativa (ver OrigemAvaliacao/ClassificacaoEvento). Só eventos canônicos
 * avançam gesture_id/action_id e alimentam os contadores
 * "canonical_user_action_counters"; eventos reativos herdam o
 * gesture_id/action_id do último canônico da mesma tarefa e só alimentam
 * "technical_evaluation_counters". O GATE real (impedir ZDP/Modelador de
 * mutar estado em evento reativo) é feito em Main.java, não aqui — este
 * serviço só PREENCHE o bloco ZDP/MODELADOR do evento reativo com uma
 * avaliação hipotética (via AgenteZDP.avaliarSemAlterarEstado/
 * AgenteModelador.avaliarSemArmazenar) quando percebe que Main.java não
 * chamou os métodos reais pra essa avaliação.
 */
public final class AgentAuditService {
    private final JsonlAgentAuditWriter jsonlWriter;
    private final HumanReadableAgentAuditWriter legivelWriter;
    private final FalhaAuditoriaLogger falhaLogger;
    private final String schemaVersion;
    private final String systemVersion;
    private final String rulesBaseVersion;

    private AgenteZDP agenteZDPReal;
    private AgenteModelador agenteModeladorReal;

    private final List<OuvinteUnidadeAnalise> ouvintesUnidadeAnalise = new ArrayList<OuvinteUnidadeAnalise>();
    private final Map<String, PerfilAgregado> perfisTecnicos = new HashMap<String, PerfilAgregado>();
    private final Map<String, PerfilAgregado> perfisCanonicos = new HashMap<String, PerfilAgregado>();
    private final Map<String, String> gestureIdPorTarefa = new HashMap<String, String>();
    private final Map<String, String> actionIdPorTarefa = new HashMap<String, String>();
    private final Map<String, Long> timestampMsUltimaCanonicaPorTarefa = new HashMap<String, Long>();
    private final Map<String, String> valorUltimaCanonicaPorTarefa = new HashMap<String, String>();
    private static final long DEBOUNCE_MS = 1500;
    private String gestureIdReservado;
    private String actionIdReservado;
    private final Map<String, Integer> avaliacoesPorGesto = new HashMap<String, Integer>();
    private final List<AgentAuditEvent> eventosEpisodioAtual = new ArrayList<AgentAuditEvent>();
    private int contadorEventos = 0;
    private int contadorGestos = 0;
    private int contadorEventosGravados = 0;
    private String ultimoGestureIdGravado;
    private boolean ultimoEventoGravadoCanonico;
    private boolean ultimaAvaliacaoMonitorAplicavel;

    private String episodeIdAtual;
    private String sessionIdAtual;
    private int stepUserAtual;
    private int stepInternalAtual;
    private String avaliacaoEsperadaProximaAcao;

    private IdentificacaoEvento identificacaoEmConstrucao;
    private ClassificacaoEvento classificacaoEmConstrucao;
    private AcaoUsuarioAudit acaoEmConstrucao;
    private TipoSituacaoAditiva categoriaEmConstrucao;
    private MonitorAuditData monitorEmConstrucao;
    private ZdpAuditData zdpEmConstrucao;
    private ModeladorAuditData modeladorEmConstrucao;

    public AgentAuditService(File arquivoJsonl, File arquivoLegivel, File diretorioLogs, String schemaVersion,
            String systemVersion, String rulesBaseVersion) throws IOException {
        this.jsonlWriter = new JsonlAgentAuditWriter(arquivoJsonl);
        this.legivelWriter = new HumanReadableAgentAuditWriter(arquivoLegivel);
        this.falhaLogger = new FalhaAuditoriaLogger(diretorioLogs);
        this.schemaVersion = schemaVersion;
        this.systemVersion = systemVersion;
        this.rulesBaseVersion = rulesBaseVersion;
    }

    /**
     * Registra um observador da unidade de análise A-B-C-D (rodada 5,
     * 2026-07-31) — chamado uma vez por AgentAuditEvent finalizado, DEPOIS
     * de já gravado nos writers desta classe. Não decide nada: só recebe o
     * evento já pronto.
     */
    public void adicionarOuvinteUnidadeAnalise(OuvinteUnidadeAnalise ouvinte) {
        if (ouvinte != null) {
            ouvintesUnidadeAnalise.add(ouvinte);
        }
    }

    /** Liga os três canais de auditoria dos agentes reais a este serviço, e guarda referência pro modo dry-run. */
    public void anexarAgentes(AgenteMonitor monitor, AgenteZDP zdp, AgenteModelador modelador) {
        this.agenteZDPReal = zdp;
        this.agenteModeladorReal = modelador;
        monitor.adicionarOuvinteAuditoria(new OuvinteAuditoriaAgenteMonitor() {
            public void aoAvaliar(MonitorAuditData dados) {
                monitorEmConstrucao = dados;
            }
        });
        zdp.adicionarOuvinteAuditoria(new OuvinteAuditoriaAgenteZDP() {
            public void aoDecidir(ZdpAuditData dados) {
                zdpEmConstrucao = dados;
            }
        });
        modelador.adicionarOuvinteAuditoria(new OuvinteAuditoriaAgenteModelador() {
            public void aoArmazenar(ModeladorAuditData dados) {
                modeladorEmConstrucao = dados;
            }
        });
    }

    public void definirContextoEpisodio(String episodeId, String sessionId) {
        this.episodeIdAtual = episodeId;
        this.sessionIdAtual = sessionId;
        this.stepUserAtual = 0;
        this.stepInternalAtual = 0;
    }

    /** Rótulo C/E do protocolo humano original pra próxima ação — só usado se ela for canônica. */
    public void definirAvaliacaoEsperadaProximaAcao(String avaliacaoEsperada) {
        this.avaliacaoEsperadaProximaAcao = avaliacaoEsperada;
    }

    /**
     * Chave de idempotência (session_id|episode_id|gesture_id) pra ação em
     * construção — só existe depois de iniciarAcao com origem canônica.
     * Quem chama (Main.java) repassa pro método real de gravação
     * (ConectorVereditoModelador.registrarVeredito) — o serviço só gera, não
     * decide se grava.
     */
    public String obterChaveIdempotenciaAtual() {
        return classificacaoEmConstrucao == null ? null : classificacaoEmConstrucao.getIdempotencyKey();
    }

    /**
     * Sinal observável de fora (usado pelo Robot/harness, rodada 3,
     * 2026-07-31): quantos eventos já foram efetivamente gravados no JSONL
     * até agora nesta sessão. Comparar antes/depois de um gesto conduzido
     * por Robot diz se {@code avaliarQuestionamentoPosicionamento} (ou
     * equivalente) de fato despachou uma avaliação — sem precisar reabrir
     * o JSONL a cada gesto.
     */
    public int getContadorEventosGravados() {
        return contadorEventosGravados;
    }

    public String getUltimoGestureIdGravado() {
        return ultimoGestureIdGravado;
    }

    public boolean isUltimoEventoGravadoCanonico() {
        return ultimoEventoGravadoCanonico;
    }

    public boolean isUltimaAvaliacaoMonitorAplicavel() {
        return ultimaAvaliacaoMonitorAplicavel;
    }

    /**
     * Reserva um gesture_id/action_id ANTES de uma ação pedagógica composta
     * de vários subeventos (rodada 3, 2026-07-31 — ex.: quantificação:
     * verificar posição da interrogação, depois validar o valor digitado).
     * Chamadas subsequentes a {@link #iniciarAcao} (canônicas ou reativas)
     * usam este id em vez de gerar um novo, até {@link
     * #liberarReservaDeGesto()} ser chamado — Main.java é responsável por
     * liberar sempre, em todo caminho de saída, pra não vazar a reserva
     * para a PRÓXIMA ação do usuário.
     */
    public void reservarProximoGesto() {
        if (gestureIdReservado != null) {
            return;
        }
        contadorGestos++;
        gestureIdReservado = "GESTO-" + String.format("%04d", contadorGestos);
        actionIdReservado = "ACAO-" + String.format("%04d", contadorGestos);
    }

    public void liberarReservaDeGesto() {
        gestureIdReservado = null;
        actionIdReservado = null;
    }

    /**
     * Marca o início de uma avaliação (canônica ou reativa). tarefaKey
     * identifica a tarefa (idUsuario+categoria+papel-alvo) — mesma
     * convenção de AgenteZDP.chaveTarefa — usada pra decidir se esta
     * avaliação herda o gesture_id/action_id de uma canônica anterior ou
     * inaugura um novo.
     */
    public void iniciarAcao(IdentificacaoEvento identificacao, AcaoUsuarioAudit acaoUsuario, OrigemAvaliacao origem,
            TipoSituacaoAditiva categoria) {
        try {
            contadorEventos++;
            stepInternalAtual++;
            this.categoriaEmConstrucao = categoria;
            this.monitorEmConstrucao = null;
            this.zdpEmConstrucao = null;
            this.modeladorEmConstrucao = null;

            String userId = identificacao == null ? null : identificacao.getUserId();
            String papelAlvo = acaoUsuario == null ? null : acaoUsuario.getPapelDestino();
            String tarefaKey = chaveTarefa(userId, categoria, papelAlvo);

            String gestureId;
            String actionId;
            if (gestureIdReservado != null) {
                // Subevento de uma acao composta (rodada 3, 2026-07-31 —
                // ver reservarProximoGesto/liberarReservaDeGesto): todos os
                // subeventos de UMA acao pedagogica (ex.: quantificacao —
                // verificar posicao da interrogacao + validar valor
                // digitado) compartilham o MESMO gesture_id/action_id,
                // mesmo vindo de chamadas iniciarAcao separadas com origens
                // diferentes (uma reativa, outra canonica).
                gestureId = gestureIdReservado;
                actionId = actionIdReservado;
                if (origem != null && origem.isCanonica()) {
                    stepUserAtual++;
                    gestureIdPorTarefa.put(tarefaKey, gestureId);
                    actionIdPorTarefa.put(tarefaKey, actionId);
                }
                if (!avaliacoesPorGesto.containsKey(gestureId)) {
                    avaliacoesPorGesto.put(gestureId, Integer.valueOf(0));
                }
            } else if (origem != null && origem.isCanonica()) {
                // Achado da rodada 3 (2026-07-31, nao pedido explicitamente,
                // descoberto ao validar Jamile S9): o mesmo gesto fisico
                // (confirmado por robot_gestos.log — o harness so chama
                // arrastar() UMA vez) as vezes dispara mouseReleased mais de
                // uma vez em Main.java (mecanismo exato nao identificado —
                // investigacao descartou o loop de retentativa do harness e
                // finalizarProxyTextoSolto; segue como limitacao registrada,
                // nao "causa confirmada"). Sem isto, cada disparo extra
                // minerava um gesture_id novo e inflava
                // "acoes pedagogicas canonicas" com repeticoes identicas do
                // MESMO valor pra MESMA tarefa em menos de 1.5s — tratado
                // aqui como o MESMO gesto (nunca escondido do log: ainda vira
                // uma linha JSONL, so nao conta como acao nova).
                String valorAtual = acaoUsuario == null ? null : acaoUsuario.getValor();
                Long tsAnterior = timestampMsUltimaCanonicaPorTarefa.get(tarefaKey);
                String valorAnterior = valorUltimaCanonicaPorTarefa.get(tarefaKey);
                long agora = System.currentTimeMillis();
                boolean debounce = tsAnterior != null && (agora - tsAnterior.longValue()) < DEBOUNCE_MS
                        && java.util.Objects.equals(valorAtual, valorAnterior)
                        && gestureIdPorTarefa.get(tarefaKey) != null;
                if (debounce) {
                    gestureId = gestureIdPorTarefa.get(tarefaKey);
                    actionId = actionIdPorTarefa.get(tarefaKey);
                } else {
                    stepUserAtual++;
                    contadorGestos++;
                    gestureId = "GESTO-" + String.format("%04d", contadorGestos);
                    actionId = "ACAO-" + String.format("%04d", contadorGestos);
                    gestureIdPorTarefa.put(tarefaKey, gestureId);
                    actionIdPorTarefa.put(tarefaKey, actionId);
                    avaliacoesPorGesto.put(gestureId, Integer.valueOf(0));
                }
                timestampMsUltimaCanonicaPorTarefa.put(tarefaKey, Long.valueOf(agora));
                valorUltimaCanonicaPorTarefa.put(tarefaKey, valorAtual);
            } else {
                gestureId = gestureIdPorTarefa.get(tarefaKey);
                actionId = actionIdPorTarefa.get(tarefaKey);
                if (gestureId == null) {
                    // Reavaliação reativa sem gesto canônico anterior pra essa
                    // tarefa (ex.: sincronização no carregamento da tela) — ganha
                    // gesto próprio, só pra não ficar com id nulo no log.
                    contadorGestos++;
                    gestureId = "GESTO-" + String.format("%04d", contadorGestos) + "-ORFAO";
                    actionId = "ACAO-" + String.format("%04d", contadorGestos) + "-ORFAO";
                    avaliacoesPorGesto.put(gestureId, Integer.valueOf(0));
                }
            }
            int numeroAvaliacao = valorOuZero(avaliacoesPorGesto.get(gestureId)) + 1;
            avaliacoesPorGesto.put(gestureId, Integer.valueOf(numeroAvaliacao));
            String evaluationId = "AVAL-" + gestureId.replace("GESTO-", "") + "-" + String.format("%03d", numeroAvaliacao);

            String idempotencyKey = (sessionIdAtual == null ? "SESSAO" : sessionIdAtual) + "|"
                    + (episodeIdAtual == null ? "EPISODIO" : episodeIdAtual) + "|" + gestureId;

            this.classificacaoEmConstrucao = new ClassificacaoEvento(origem, idempotencyKey);

            String eventId = "EVT-" + gestureId + "-" + evaluationId;
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new java.util.Date());
            String episodeIdInformado = identificacao == null ? null : identificacao.getEpisodeId();
            String sessionIdInformado = identificacao == null ? null : identificacao.getSessionId();
            this.identificacaoEmConstrucao = new IdentificacaoEvento(
                    schemaVersion, eventId,
                    episodeIdInformado == null ? episodeIdAtual : episodeIdInformado,
                    sessionIdInformado == null ? sessionIdAtual : sessionIdInformado,
                    gestureId, actionId, evaluationId,
                    Integer.valueOf(stepUserAtual), Integer.valueOf(stepInternalAtual),
                    timestamp,
                    userId,
                    identificacao == null ? null : identificacao.getProblemId(),
                    identificacao == null ? null : identificacao.getSituacaoProblema(),
                    categoria == null ? (identificacao == null ? null : identificacao.getCategoriaEsperada()) : categoria.name(),
                    identificacao == null ? null : identificacao.getPapelDesconhecido(),
                    systemVersion, rulesBaseVersion,
                    java.util.Arrays.asList("MONITOR", "ZDP", "MODELADOR"));
            this.acaoEmConstrucao = acaoUsuario;
        } catch (RuntimeException e) {
            falhaLogger.registrar("iniciarAcao", e);
        }
    }

    public void finalizarAcao() {
        String esperada = avaliacaoEsperadaProximaAcao;
        avaliacaoEsperadaProximaAcao = null;
        finalizarAcao(esperada);
    }

    public void finalizarAcao(String avaliacaoEsperadaProtocoloOriginal) {
        try {
            if (identificacaoEmConstrucao == null || classificacaoEmConstrucao == null) {
                return;
            }
            String userId = identificacaoEmConstrucao.getUserId();
            boolean canonical = classificacaoEmConstrucao.isCanonical();

            // Preenchimento hipotético (dry-run): se esta avaliação é reativa e
            // Main.java corretamente NÃO chamou os métodos reais de ZDP/
            // Modelador, preenche os blocos com "o que teria acontecido" sem
            // mutar nada — só pra o log técnico não ficar sem os 3 blocos.
            if (!canonical && monitorEmConstrucao != null && monitorEmConstrucao.getAvaliacao() != null) {
                boolean correto = "C".equals(monitorEmConstrucao.getAvaliacao());
                String papelAlvo = acaoEmConstrucao == null ? null : acaoEmConstrucao.getPapelDestino();
                if (zdpEmConstrucao == null && agenteZDPReal != null && papelAlvo != null) {
                    zdpEmConstrucao = agenteZDPReal.avaliarSemAlterarEstado(userId, categoriaEmConstrucao, papelAlvo, correto);
                }
                if (modeladorEmConstrucao == null && agenteModeladorReal != null) {
                    String tarefa = (categoriaEmConstrucao == null ? "?" : categoriaEmConstrucao.name()) + ":" + papelAlvo;
                    DiagnosticoTarefa hipotetico = new DiagnosticoTarefa(tarefa);
                    modeladorEmConstrucao = agenteModeladorReal.avaliarSemArmazenar(userId, hipotetico);
                }
            }

            atualizarPerfil(perfisTecnicos, userId, monitorEmConstrucao);
            if (canonical) {
                atualizarPerfil(perfisCanonicos, userId, monitorEmConstrucao);
            }
            ProfileSnapshot tecnicoDepois = construirProfileSnapshot(perfisTecnicos, userId);
            ProfileSnapshot canonicoDepois = construirProfileSnapshot(perfisCanonicos, userId);

            InteractionComparisonAudit comparacao = construirComparacao(
                    canonical ? avaliacaoEsperadaProtocoloOriginal : null);

            AgentAuditEvent evento = new AgentAuditEvent(identificacaoEmConstrucao, classificacaoEmConstrucao,
                    acaoEmConstrucao, monitorEmConstrucao, zdpEmConstrucao, modeladorEmConstrucao,
                    UserProfileSnapshot.indisponivel(MOTIVO_PERFIL_INDISPONIVEL),
                    UserProfileSnapshot.indisponivel(MOTIVO_PERFIL_INDISPONIVEL),
                    tecnicoDepois, tecnicoDepois, canonicoDepois, canonicoDepois, comparacao);
            eventosEpisodioAtual.add(evento);
            jsonlWriter.escrever(evento);
            legivelWriter.escrever(evento);
            contadorEventosGravados++;
            ultimoGestureIdGravado = identificacaoEmConstrucao.getGestureId();
            ultimoEventoGravadoCanonico = canonical;
            ultimaAvaliacaoMonitorAplicavel = monitorEmConstrucao != null && monitorEmConstrucao.getAvaliacao() != null;
            for (OuvinteUnidadeAnalise ouvinte : ouvintesUnidadeAnalise) {
                ouvinte.aoFinalizarAvaliacao(evento);
            }
        } catch (Exception e) {
            falhaLogger.registrar("finalizarAcao", e);
        } finally {
            identificacaoEmConstrucao = null;
            classificacaoEmConstrucao = null;
            acaoEmConstrucao = null;
            categoriaEmConstrucao = null;
            monitorEmConstrucao = null;
            zdpEmConstrucao = null;
            modeladorEmConstrucao = null;
        }
    }

    private static final String MOTIVO_PERFIL_INDISPONIVEL =
            "ModeloUsuario/PerfilAluno/PerfilAprendizagem nao guardam nivel global, diagnostico atual, "
                    + "estrategia predominante nem confianca de modelo hoje — ver audit_session_counters_* "
                    + "pros agregados que o proprio servico de auditoria calcula (nao e o perfil persistido real).";

    /**
     * Fecha o episódio, grava o resumo (JSONL + legível) e devolve o mesmo
     * mapa gravado — quem chama (o harness Robot, que também sabe as
     * contagens do LADO do Robot: falhas de pickup/drop, gestos físicos)
     * usa isso para montar a linha completa de
     * {@code cardinalidade_episodios.tsv} (rodada 3, 2026-07-31). Este
     * serviço não escreve mais esse TSV sozinho — só sabe o lado da
     * auditoria, não o lado do Robot.
     */
    public Map<String, Object> finalizarEpisodio(String episodeId) {
        try {
            int gestosObservados = 0;
            int acoesCanonicas = 0;
            int avaliacoesTecnicas = eventosEpisodioAtual.size();
            int avaliacoesReativas = 0;
            int decisoesCanonicasZdp = 0;
            int atualizacoesCanonicasModelador = 0;
            int casosInseridos = 0;
            int casosDuplicadosBloqueados = 0;
            int divergencias = 0;
            java.util.Set<String> gestosVistos = new java.util.HashSet<String>();
            // Contadores canônicos por GESTO DISTINTO, não por linha (rodada
            // 3, 2026-07-31): um debounce (ver iniciarAcao) faz disparos
            // duplicados do mesmo gesto físico compartilharem gesture_id —
            // sem contar por gesto distinto aqui, cada disparo extra ainda
            // inflava "ações pedagógicas canônicas" mesmo já não inflando
            // mais o estado real do ZDP/Modelador (que já tem idempotência
            // própria). Garante a igualdade pedida: ações canônicas ==
            // decisões reais do ZDP == atualizações reais do Modelador ==
            // casos inseridos.
            java.util.Set<String> gestosCanonicosVistos = new java.util.HashSet<String>();
            java.util.Set<String> gestosComDecisaoZdp = new java.util.HashSet<String>();
            java.util.Set<String> gestosComAtualizacaoModelador = new java.util.HashSet<String>();
            Map<String, Integer> regrasPorAgente = new LinkedHashMap<String, Integer>();
            regrasPorAgente.put("MONITOR", 0);
            regrasPorAgente.put("ZDP", 0);
            regrasPorAgente.put("MODELADOR", 0);
            long duracaoMs = 0;
            String estadoFinal = "sem_acoes";

            for (AgentAuditEvent evento : eventosEpisodioAtual) {
                String gestureId = evento.getIdentificacao() == null ? null : evento.getIdentificacao().getGestureId();
                if (gestureId != null) {
                    gestosVistos.add(gestureId);
                }
                boolean canonical = evento.getClassificacao() != null && evento.getClassificacao().isCanonical();
                if (canonical) {
                    if (gestureId == null || gestosCanonicosVistos.add(gestureId)) {
                        acoesCanonicas++;
                    }
                } else {
                    avaliacoesReativas++;
                }
                if (evento.getMonitor() != null) {
                    regrasPorAgente.put("MONITOR", regrasPorAgente.get("MONITOR") + evento.getMonitor().getRegrasAtivadas().size());
                    duracaoMs += evento.getMonitor().getProcessingTimeMs();
                }
                if (evento.getZdp() != null) {
                    regrasPorAgente.put("ZDP", regrasPorAgente.get("ZDP") + evento.getZdp().getRegrasAtivadas().size());
                    duracaoMs += evento.getZdp().getProcessingTimeMs();
                    if (canonical) {
                        if (gestureId == null || gestosComDecisaoZdp.add(gestureId)) {
                            decisoesCanonicasZdp++;
                        }
                        estadoFinal = evento.getZdp().getCamadaEstrategia();
                    }
                }
                if (evento.getModelador() != null) {
                    regrasPorAgente.put("MODELADOR", regrasPorAgente.get("MODELADOR") + evento.getModelador().getRegrasAtivadas().size());
                    duracaoMs += evento.getModelador().getProcessingTimeMs();
                    if (canonical) {
                        if (gestureId == null || gestosComAtualizacaoModelador.add(gestureId)) {
                            atualizacoesCanonicasModelador++;
                        }
                        if (evento.getModelador().getCasoInserido() != null) {
                            if (evento.getModelador().getCasoInserido().isInserted()) {
                                casosInseridos++;
                            } else {
                                casosDuplicadosBloqueados++;
                            }
                        }
                    }
                }
                if (evento.getComparacao() != null && Boolean.TRUE.equals(evento.getComparacao().isDivergence())) {
                    divergencias++;
                }
            }
            gestosObservados = gestosVistos.size();

            Map<String, Object> mapaResumo = new LinkedHashMap<String, Object>();
            mapaResumo.put("gestures_observed", gestosObservados);
            mapaResumo.put("canonical_actions", acoesCanonicas);
            mapaResumo.put("technical_evaluations", avaliacoesTecnicas);
            mapaResumo.put("reactive_evaluations", avaliacoesReativas);
            mapaResumo.put("zdp_canonical_decisions", decisoesCanonicasZdp);
            mapaResumo.put("modeler_canonical_updates", atualizacoesCanonicasModelador);
            mapaResumo.put("cases_inserted", casosInseridos);
            mapaResumo.put("duplicate_cases_blocked", casosDuplicadosBloqueados);
            mapaResumo.put("divergences", divergencias);
            mapaResumo.put("rules_fired_by_agent", regrasPorAgente);
            mapaResumo.put("final_state", estadoFinal);
            mapaResumo.put("duration_ms", duracaoMs);

            jsonlWriter.escreverResumoEpisodio(episodeId, mapaResumo);
            legivelWriter.escreverResumoEpisodio(episodeId, mapaResumo);
            return mapaResumo;
        } catch (Exception e) {
            falhaLogger.registrar("finalizarEpisodio:" + episodeId, e);
            return java.util.Collections.<String, Object>emptyMap();
        } finally {
            eventosEpisodioAtual.clear();
        }
    }

    public void fechar() {
        try {
            jsonlWriter.fechar();
            legivelWriter.fechar();
        } catch (IOException e) {
            falhaLogger.registrar("fechar", e);
        }
    }

    private String chaveTarefa(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo) {
        return (idUsuario == null ? "" : idUsuario) + "|"
                + (categoria == null ? "" : categoria.name()) + ":"
                + (chavePapelAlvo == null ? "" : chavePapelAlvo);
    }

    private InteractionComparisonAudit construirComparacao(String avaliacaoEsperada) {
        String avaliacaoObservada = monitorEmConstrucao == null ? null : monitorEmConstrucao.getAvaliacao();
        boolean divergence = avaliacaoEsperada != null && avaliacaoObservada != null
                && !avaliacaoEsperada.equals(avaliacaoObservada);
        String intervencao = zdpEmConstrucao == null ? null : zdpEmConstrucao.getIntervencao();
        return new InteractionComparisonAudit(
                avaliacaoEsperada, avaliacaoObservada,
                null, null,
                intervencao, intervencao,
                null,
                divergence,
                divergence ? "avaliacao_monitor_diverge_do_protocolo_original" : null,
                divergence ? "O veredito do AgenteMonitor atual diverge do rótulo humano original do protocolo "
                        + "real replayado." : null,
                divergence ? "MONITOR" : null);
    }

    private static String familiaDe(String acaoRecebida) {
        if ("SELECIONAR_CATEGORIA".equals(acaoRecebida)) {
            return "category";
        }
        if ("POSICIONAR".equals(acaoRecebida)) {
            return "positioning";
        }
        if ("SELECIONAR_SINAL".equals(acaoRecebida)) {
            return "sign";
        }
        if ("TEXTO".equals(acaoRecebida)) {
            return "calculation";
        }
        return null;
    }

    private void atualizarPerfil(Map<String, PerfilAgregado> perfis, String userId, MonitorAuditData monitor) {
        if (userId == null || monitor == null || monitor.getAvaliacao() == null) {
            return;
        }
        PerfilAgregado p = perfis.get(userId);
        if (p == null) {
            p = new PerfilAgregado();
            perfis.put(userId, p);
        }
        boolean correto = "C".equals(monitor.getAvaliacao());
        String familia = familiaDe(monitor.getAcaoRecebida());
        if (correto) {
            p.totalAcertos++;
            p.acertosConsecutivos++;
            p.errosConsecutivos = 0;
        } else {
            p.totalErros++;
            p.errosConsecutivos++;
            p.acertosConsecutivos = 0;
            if ("category".equals(familia)) {
                p.errosCategoria++;
            } else if ("positioning".equals(familia)) {
                p.errosPosicionamento++;
            } else if ("sign".equals(familia)) {
                p.errosSinal++;
            } else if ("calculation".equals(familia)) {
                p.errosCalculo++;
            }
        }
        p.ultimaAvaliacao = monitor.getAvaliacao();
        p.ultimaFamiliaAcao = familia;
        if (zdpEmConstrucao != null && zdpEmConstrucao.getIntervencao() != null) {
            p.ajudasUsadas++;
        }
    }

    private ProfileSnapshot construirProfileSnapshot(Map<String, PerfilAgregado> perfis, String userId) {
        PerfilAgregado p = userId == null ? null : perfis.get(userId);
        String motivo = "Agregado calculado por este serviço de auditoria a partir dos eventos observados "
                + "nesta sessão — não um estado que o AgenteModelador guarde internamente.";
        if (p == null) {
            return new ProfileSnapshot(
                    Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                    Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
                    Integer.valueOf(0), null, null, motivo);
        }
        return new ProfileSnapshot(
                Integer.valueOf(p.totalErros), Integer.valueOf(p.errosConsecutivos),
                Integer.valueOf(p.errosCategoria), Integer.valueOf(p.errosPosicionamento),
                Integer.valueOf(p.errosSinal), Integer.valueOf(p.errosCalculo),
                Integer.valueOf(p.totalAcertos), Integer.valueOf(p.acertosConsecutivos),
                Integer.valueOf(p.ajudasUsadas), p.ultimaAvaliacao, p.ultimaFamiliaAcao, motivo);
    }

    private int valorOuZero(Integer valor) {
        return valor == null ? 0 : valor.intValue();
    }

    private static final class PerfilAgregado {
        int totalErros;
        int errosConsecutivos;
        int errosCategoria;
        int errosPosicionamento;
        int errosSinal;
        int errosCalculo;
        int totalAcertos;
        int acertosConsecutivos;
        int ajudasUsadas;
        String ultimaAvaliacao;
        String ultimaFamiliaAcao;
    }
}
