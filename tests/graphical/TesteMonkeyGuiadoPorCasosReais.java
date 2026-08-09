import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

import gerard.agente.modelador.OuvinteCasoAgenteModelador;
import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.monitor.OuvinteVeredictoAgenteMonitor;
import gerard.agente.zdp.CamadaEstrategiaZDP;
import gerard.agente.zdp.OuvinteEstrategiaAgenteZDP;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.pesquisador.auditoria.AgentAuditService;
import gerard.pesquisador.replay.RepositorioProtocolosReaisReplay;
import gerard.pesquisador.replay.EpisodioReplayHumano;
import gerard.pesquisador.replay.PassoReplayHumano;
import gerard.pesquisador.replay.PassoTextoReplayHumano;
import gerard.pesquisador.replay.robot.ComponenteLocalizado;
import gerard.pesquisador.replay.robot.EpisodeCardinalityReport;
import gerard.pesquisador.replay.robot.RobotGestureAttempt;
import gerard.pesquisador.replay.robot.RobotGestureLogger;
import gerard.pesquisador.replay.robot.RobotGestureStatus;
import gerard.pesquisador.replay.robot.RobotGestureTrace;

/**
 * Prova de conceito do que a usuária pediu em 2026-07-30: em vez de rodar o
 * replay dos casos reais direto contra os agentes (TesteReplayProtocolosReais,
 * que pula a tela inteiramente), dirige a TelaGerard DE VERDADE via
 * java.awt.Robot — mesmo mecanismo do TesteMonkeySemiGuiado — só que seguindo
 * o roteiro EXATO de um episódio real do catálogo (dados/protocolos_reais_replay.tsv),
 * não ações aleatórias. Isso exercita o pipeline inteiro de eventos reais de
 * mouse (MouseListener → drag → avaliarQuestionamentoPosicionamento → Monitor
 * → ZDP → Modelador), não só a chamada direta aos agentes.
 *
 * Escala ampliada em 2026-07-31 (prova de conceito original, 2026-07-30,
 * tinha só 1 episódio): 14 episódios reais, cruzados automaticamente contra
 * as 10 situações curadas+validadas (originais, composição/transformação
 * simples) em ~/Gerard/curadoria/situacoes_vergnaud_curadas.tsv — casamento
 * EXATO por papel (não só por conjunto de números: descoberto na prática que
 * "Rafael/refrigerantes" {estadoInicial=10, transformacao=6, estadoFinal=4}
 * e "Leandro/balas" {estadoInicial=10, transformacao=4, estadoFinal=6} usam
 * o mesmo conjunto {4,6,10} em papéis trocados — são problemas diferentes; um
 * casamento só por conjunto teria cruzado os dois errado). Cobre 2 problemas
 * recorrentes (Lucas/figurinhas — composição; Maria/figurinhas —
 * transformação) repetidos em 9 sessões diferentes. Continua faltando
 * comparação: o catálogo de replay usa REFERENTE/REFERIDO/DIFERENCA, o dado
 * curado usa referido/referendo/valor_relativo — cruzar errado essa
 * nomenclatura arriscava produzir um dado silenciosamente errado, então
 * segue de fora, mesma cautela da primeira versão.
 *
 * Fica no pacote padrao (sem "package"), mesmo motivo do TesteMonkeySemiGuiado:
 * precisa de acesso aos campos com visibilidade de pacote de Main.TelaGerard.
 * finalizarCarregamentoSituacao() é private nessa classe — usa reflection só
 * para essa única chamada (é o método que popula elementosTexto/
 * elementosVergnaud/itensArrastaveis a partir de situacaoProblemaAtual;
 * reimplementar isso aqui de fora seria duplicar lógica frágil).
 *
 * Não faz parte do app entregue ao usuário final — ferramenta de teste local,
 * fica de fora do instalador jpackage (não é main-class de nenhum módulo).
 */
public class TesteMonkeyGuiadoPorCasosReais {

    /**
     * Liga o rótulo de um episódio real (dados/protocolos_reais_replay.tsv) ao id
     * da situação-problema curada e validada correspondente. Ver aviso de
     * escopo na Javadoc da classe — só entra aqui o que já foi confirmado
     * como totalmente curado (validada=true, papéis preenchidos).
     */
    private static final Map<String, String> ROTULO_PARA_SITUACAO_ID = new LinkedHashMap<String, String>();
    static {
        // Cruzamento feito em 2026-07-31: casamento EXATO por papel (não só
        // por conjunto de números — descoberto na prática que dois problemas
        // diferentes podem usar os mesmos 3 números em papéis trocados, ex.
        // "Rafael/refrigerantes" {estadoInicial=10, transformacao=6,
        // estadoFinal=4} vs. "Leandro/balas" {estadoInicial=10,
        // transformacao=4, estadoFinal=6} — mesmo conjunto {4,6,10}, papéis
        // diferentes; um casamento só por conjunto teria cruzado errado os
        // dois). Só entraram aqui os pares com casamento único e exato,
        // entre as 10 situações curadas+validadas (originais, composição ou
        // transformação simples) e os 63 episódios reais dessas duas
        // categorias no catálogo de 418 linhas
        // (build/classes/.../protocolos_reais_replay.tsv, formato de papéis
        // semânticos). Comparação ainda de fora (mesma cautela da primeira
        // versão desta classe): referente/referendo divergem entre o
        // catálogo de replay e SemanticaCuradaSituacao.
        ROTULO_PARA_SITUACAO_ID.put(
                "Felipe Wanderley 01-06-10 - Problema 01 - Transformação (Maria/figurinhas)",
                "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160");
        ROTULO_PARA_SITUACAO_ID.put(
                "Jamile 09-06-10 (S9) - Problema 2 - Transformação (Maria/figurinhas)",
                "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160");
        ROTULO_PARA_SITUACAO_ID.put(
                "Jamile 08-06 (S8) - Problema 1 - Transformação (Maria/figurinhas)",
                "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160");
        ROTULO_PARA_SITUACAO_ID.put(
                "FelipeWanderley 15-06-10 - Questão 3 - Composição (Lucas/figurinhas)",
                "PO_COMPOSICAO_MEDIDAS_figurinhas_755733109");
        ROTULO_PARA_SITUACAO_ID.put(
                "FelipeWanderley 17-07-10 - Questão 1 - Composição (Lucas/figurinhas)",
                "PO_COMPOSICAO_MEDIDAS_figurinhas_755733109");
        ROTULO_PARA_SITUACAO_ID.put(
                "FelipeWanderley 17-07-10 - Questão 4 - Transformação (Maria/figurinhas)",
                "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160");
        ROTULO_PARA_SITUACAO_ID.put(
                "FelipeWanderley 19-07-10 - Questão 2 - Transformação (Maria/figurinhas)",
                "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160");
        ROTULO_PARA_SITUACAO_ID.put(
                "FelipeWanderley 19-07-10 - Questão 6 - Composição (Lucas/figurinhas)",
                "PO_COMPOSICAO_MEDIDAS_figurinhas_755733109");
        ROTULO_PARA_SITUACAO_ID.put(
                "Jamilly 08-06-10 - Problema 2 - Transformação (Maria/figurinhas)",
                "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160");
        ROTULO_PARA_SITUACAO_ID.put(
                "Jamilly 08-06-10 - Problema 4 - Composição (Lucas/figurinhas)",
                "PO_COMPOSICAO_MEDIDAS_figurinhas_755733109");
        ROTULO_PARA_SITUACAO_ID.put(
                "Jamilly 13-07-10 - Problema 1 - Composição (Lucas/figurinhas)",
                "PO_COMPOSICAO_MEDIDAS_figurinhas_755733109");
        ROTULO_PARA_SITUACAO_ID.put(
                "Jamilly 13-07-10 - Problema 4 - Transformação (Maria/figurinhas)",
                "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160");
        ROTULO_PARA_SITUACAO_ID.put(
                "Jamilly 14-07-10 - Problema 3 - Composição (Lucas/figurinhas)",
                "PO_COMPOSICAO_MEDIDAS_figurinhas_755733109");
        ROTULO_PARA_SITUACAO_ID.put(
                "Jamilly 14-07-10 - Problema 7 - Transformação (Maria/figurinhas)",
                "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160");
    }

    public static void main(String[] args) throws Exception {
        File diretorioLogs = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        diretorioLogs.mkdirs();
        File arquivoLog = new File(diretorioLogs,
                "monkey_casos_reais_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".log");
        final PrintWriter log = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoLog), "UTF-8")), true);
        log.println("Teste monkey guiado por casos reais");

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable ex) {
                // Diagnostico 2026-07-31: se esta excecao vier da propria
                // thread main depois que o finally do try principal ja
                // fechou "log" (PrintWriter), o println acima em log vira
                // no-op silencioso — sem isto em System.err, a excecao
                // desaparece sem deixar rastro em nenhum arquivo.
                System.err.println("[" + new Date() + "] EXCECAO NAO TRATADA na thread " + t.getName() + ":");
                ex.printStackTrace(System.err);
                log.println("[" + new Date() + "] EXCECAO NAO TRATADA na thread " + t.getName() + ":");
                ex.printStackTrace(log);
            }
        });

        // Este harness lanca o Main() de verdade, que grava em
        // ~/Gerard/perfis_usuario.tsv e ~/Gerard/diagnosticos_tarefa.tsv (nao
        // tem como injetar arquivos isolados no construtor sem mexer em
        // Main.java) — descoberto na pratica em 2026-07-30, quando um
        // episodio real via Robot poluiu diagnosticos_tarefa.tsv de
        // producao. Backup/restauracao automatica em vez disso: copia os
        // dois arquivos reais antes de comecar, restaura no finally,
        // independente do teste terminar bem ou com excecao.
        File arquivoRealPerfis = new File(new File(System.getProperty("user.home"), "Gerard"), "perfis_usuario.tsv");
        File arquivoRealDiagnosticos = new File(new File(System.getProperty("user.home"), "Gerard"), "diagnosticos_tarefa.tsv");
        File backupPerfis = new File(diretorioLogs, "backup_perfis_usuario_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".tsv");
        File backupDiagnosticos = new File(diretorioLogs, "backup_diagnosticos_tarefa_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".tsv");
        boolean perfisExistiaAntes = arquivoRealPerfis.isFile();
        boolean diagnosticosExistiaAntes = arquivoRealDiagnosticos.isFile();
        if (perfisExistiaAntes) {
            copiarArquivo(arquivoRealPerfis, backupPerfis);
            log.println("Backup de perfis_usuario.tsv em: " + backupPerfis.getAbsolutePath());
        }
        if (diagnosticosExistiaAntes) {
            copiarArquivo(arquivoRealDiagnosticos, backupDiagnosticos);
            log.println("Backup de diagnosticos_tarefa.tsv em: " + backupDiagnosticos.getAbsolutePath());
        }

        try {
            final Main[] janelaRef = new Main[1];
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    janelaRef[0] = new Main();
                    janelaRef[0].setVisible(true);
                    janelaRef[0].toFront();
                }
            });
            Thread.sleep(1500);

            Main.TelaGerard tela = encontrarTelaGerard(janelaRef[0]);
            if (tela == null) {
                log.println("Nao foi possivel localizar a TelaGerard dentro da janela. Abortando.");
                log.close();
                return;
            }

            // Hipotese "listener registrado mais de uma vez" (rodada 4,
            // 2026-07-31) — checado via metodo padrao de
            // java.awt.Component/JComponent (getMouseListeners/
            // getMouseMotionListeners), de FORA da TelaGerard, sem
            // instrumentar o construtor (uma tentativa anterior de
            // instrumentar dentro do construtor se mostrou associada a
            // falhas de pickup no harness em testes repetidos — nao
            // confirmado como causa real, mas descartado por seguranca;
            // ver relatorio, secao "achado descartado por seguranca").
            final int[] contagemListeners = new int[2];
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    contagemListeners[0] = tela.getMouseListeners().length;
                    contagemListeners[1] = tela.getMouseMotionListeners().length;
                }
            });
            log.println("Listeners registrados em TelaGerard: MouseListener=" + contagemListeners[0]
                    + " MouseMotionListener=" + contagemListeners[1]
                    + " (esperado: 1 e 1 — TelaGerard.<init> so chama addMouseListener(this)/"
                    + "addMouseMotionListener(this) uma vez cada)");
            System.out.println("Listeners registrados em TelaGerard: MouseListener=" + contagemListeners[0]
                    + " MouseMotionListener=" + contagemListeners[1]);

            File arquivoAgentes = new File(diretorioLogs,
                    "monkey_casos_reais_agentes_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".tsv");
            final PrintWriter logAgentes = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoAgentes), "UTF-8")), true);
            logAgentes.println("seq\ttMs\tidUsuario\tagente\tdetalhe");
            GravadorAtividadeAgentes gravador = new GravadorAtividadeAgentes(logAgentes, System.currentTimeMillis());
            tela.agenteMonitor.adicionarOuvinte(gravador);
            tela.agenteZDP.adicionarOuvinte(gravador);
            tela.agenteModelador.adicionarOuvinte(gravador);
            System.out.println("Log de atividade dos agentes: " + arquivoAgentes.getAbsolutePath());

            // Log estruturado de auditoria (entrada/decisao/regras/saida por
            // agente) — pedido da usuaria em 2026-07-31, ver
            // gerard.pesquisador.auditoria.AgentAuditService. Mesmo objeto
            // alimenta os dois arquivos (JSONL + legivel), nunca diverge.
            String carimboExecucao = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File arquivoJsonl = new File(diretorioLogs, "agentes_execucao_" + carimboExecucao + ".jsonl");
            File arquivoLegivel = new File(diretorioLogs, "agentes_execucao_legivel_" + carimboExecucao + ".log");
            AgentAuditService servicoAuditoria = new AgentAuditService(arquivoJsonl, arquivoLegivel, diretorioLogs,
                    "4.0.0", "Gerard-2026.07.31", "1.0.0");
            servicoAuditoria.anexarAgentes(tela.agenteMonitor, tela.agenteZDP, tela.agenteModelador);
            tela.agentAuditService = servicoAuditoria;

            // Unidade de análise A-B-C-D (rodada 5, 2026-07-31) — observa o
            // MESMO AgentAuditService acima (nunca reprocessa MONITOR/ZDP/
            // MODELADOR), sem gerar avaliação nova nenhuma.
            File arquivoUnidadesAnalise = new File(diretorioLogs, "unidades_analise_" + carimboExecucao + ".jsonl");
            File arquivoAcoesProtocolos = new File(diretorioLogs, "acoes_usuario_protocolos_" + carimboExecucao + ".jsonl");
            File arquivoEventosTecnicos = new File(diretorioLogs, "eventos_tecnicos_" + carimboExecucao + ".jsonl");
            File arquivoExplicacoesUsuario = new File(diretorioLogs, "explicacoes_usuario_" + carimboExecucao + ".jsonl");
            gerard.pesquisador.analiseunidade.AnalysisUnitAuditService servicoUnidadeAnalise =
                    new gerard.pesquisador.analiseunidade.AnalysisUnitAuditService(arquivoUnidadesAnalise,
                            arquivoAcoesProtocolos, arquivoEventosTecnicos, arquivoExplicacoesUsuario, diretorioLogs);
            servicoAuditoria.adicionarOuvinteUnidadeAnalise(servicoUnidadeAnalise);
            tela.analysisUnitAuditService = servicoUnidadeAnalise;
            System.out.println("Unidades de analise A-B-C-D (JSONL): " + arquivoUnidadesAnalise.getAbsolutePath());
            System.out.println("Acoes de usuario por protocolo (JSONL): " + arquivoAcoesProtocolos.getAbsolutePath());
            System.out.println("Eventos tecnicos (JSONL): " + arquivoEventosTecnicos.getAbsolutePath());
            System.out.println("Explicacoes do usuario (JSONL): " + arquivoExplicacoesUsuario.getAbsolutePath());

            File arquivoCardinalidade = new File(diretorioLogs, "cardinalidade_episodios_" + carimboExecucao + ".tsv");
            EpisodeCardinalityReport.escreverCabecalho(arquivoCardinalidade);
            File arquivoCardinalidadeUnidades = new File(diretorioLogs,
                    "cardinalidade_unidades_analise_" + carimboExecucao + ".tsv");
            gerard.pesquisador.analiseunidade.AnalysisUnitCardinalityReport.escreverCabecalho(arquivoCardinalidadeUnidades);
            System.out.println("Cardinalidade por unidade de analise (TSV): " + arquivoCardinalidadeUnidades.getAbsolutePath());
            File arquivoRobotGestos = new File(diretorioLogs, "robot_gestos_" + carimboExecucao + ".log");
            RobotGestureLogger robotLog = new RobotGestureLogger(arquivoRobotGestos);
            File arquivoDespachoMouse = new File(diretorioLogs, "despacho_mouse_released_" + carimboExecucao + ".log");
            gerard.pesquisador.auditoria.DespachoMouseReleasedDiagnostico.definirArquivo(arquivoDespachoMouse);
            String sessionId = "SESSAO-" + carimboExecucao;
            System.out.println("Log estruturado de auditoria (JSONL): " + arquivoJsonl.getAbsolutePath());
            System.out.println("Log estruturado de auditoria (legivel): " + arquivoLegivel.getAbsolutePath());
            System.out.println("Cardinalidade por episodio (TSV): " + arquivoCardinalidade.getAbsolutePath());
            System.out.println("Traco de gestos do Robot: " + arquivoRobotGestos.getAbsolutePath());
            System.out.println("Despacho de mouseReleased: " + arquivoDespachoMouse.getAbsolutePath());

            Robot robot = new Robot();
            robot.setAutoDelay(15);

            List<EpisodioReplayHumano> todos = new RepositorioProtocolosReaisReplay().obterTodos();
            int episodiosRodados = 0;
            int passosOk = 0;
            int passosDivergentes = 0;

            for (EpisodioReplayHumano episodio : todos) {
                String situacaoId = ROTULO_PARA_SITUACAO_ID.get(episodio.rotulo);
                if (situacaoId == null) {
                    continue;
                }
                log.println("=== Episodio real: " + episodio.rotulo + " -> situacao " + situacaoId
                        + " (idUsuario=" + episodio.idUsuarioReal + ") ===");
                System.out.println("Rodando episodio real: " + episodio.rotulo);

                SituacaoProblemaAditiva situacao = encontrarSituacaoPorId(tela, situacaoId);
                if (situacao == null) {
                    log.println("  situacao " + situacaoId + " nao encontrada no repositorio — pulando episodio.");
                    continue;
                }

                // Marca a sessao com o mesmo id "hist_*" que
                // protocolos_reais_replay.tsv ja usa para este episodio —
                // sem isto, qualquer registro caido no arquivo real usaria o
                // id generico "usuario_local", indistinguivel do ruido
                // acumulado de testes tecnicos anteriores (usuaria pediu
                // separacao clara em 2026-07-30, apos descobrir esse ruido).
                tela.loggerInteracaoGerard.definirUsuario(episodio.idUsuarioReal);
                gravador.definirUsuarioEpisodioAtual(episodio.idUsuarioReal);
                carregarSituacaoEspecifica(tela, situacao, log);
                episodiosRodados++;

                String episodeId = "EP-" + sanitizarIdEpisodio(episodio.idUsuarioReal) + "-" + episodiosRodados;
                servicoAuditoria.definirContextoEpisodio(episodeId, sessionId);
                servicoUnidadeAnalise.definirContextoEpisodio(episodeId, sessionId);

                List<RobotGestureTrace> gestosDoEpisodio = new ArrayList<RobotGestureTrace>();
                int ordemProtocolo = 0;

                for (PassoReplayHumano passo : episodio.passos) {
                    ordemProtocolo++;
                    servicoAuditoria.definirAvaliacaoEsperadaProximaAcao(
                            passo.corretoNoProtocoloOriginal ? "C" : "E");
                    RobotGestureTrace gesto = new RobotGestureTrace(episodeId, ordemProtocolo, passo.descricao,
                            passo.chavePapelNumeral, passo.chavePapelNumeral, passo.chavePapelAlvo,
                            passo.corretoNoProtocoloOriginal ? "C" : "E");
                    gestosDoEpisodio.add(gesto);
                    int[] resultado = executarPasso(robot, tela, passo, log, servicoAuditoria, gesto, robotLog);
                    passosOk += resultado[0];
                    passosDivergentes += resultado[1];
                    Thread.sleep(400);
                }

                for (PassoTextoReplayHumano passoTexto : episodio.textos) {
                    ordemProtocolo++;
                    servicoAuditoria.definirAvaliacaoEsperadaProximaAcao(passoTexto.correto ? "C" : "E");
                    RobotGestureTrace gesto = new RobotGestureTrace(episodeId, ordemProtocolo, passoTexto.descricao,
                            passoTexto.chavePapelAlvo, "?", passoTexto.chavePapelAlvo,
                            passoTexto.correto ? "C" : "E");
                    gestosDoEpisodio.add(gesto);
                    int[] resultado = executarPassoTexto(robot, tela, passoTexto, situacao, log, servicoAuditoria,
                            gesto, robotLog);
                    passosOk += resultado[0];
                    passosDivergentes += resultado[1];
                    Thread.sleep(400);
                }

                Map<String, Object> resumoEpisodio = servicoAuditoria.finalizarEpisodio(episodeId);
                Map<String, Object> resumoUnidadesAnalise = servicoUnidadeAnalise.finalizarEpisodio();
                escreverCardinalidadeEpisodio(arquivoCardinalidade, episodeId, gestosDoEpisodio, resumoEpisodio,
                        robotLog);
                gerard.pesquisador.analiseunidade.AnalysisUnitCardinalityReport.escreverLinha(
                        arquivoCardinalidadeUnidades, episodeId, resumoUnidadesAnalise);
            }

            log.println("Fim: " + episodiosRodados + " episodio(s) reais rodados via Robot, "
                    + passosOk + " passo(s) sem divergencia, " + passosDivergentes + " divergencia(s).");
            tela.agenteMonitor.removerOuvinte(gravador);
            tela.agenteZDP.removerOuvinte(gravador);
            tela.agenteModelador.removerOuvinte(gravador);
            logAgentes.close();
            servicoAuditoria.fechar();
            servicoUnidadeAnalise.fechar();
            gerard.pesquisador.auditoria.DespachoMouseReleasedDiagnostico.fechar();
            System.out.println("Teste concluido: " + episodiosRodados + " episodio(s), " + passosOk
                    + " passo(s) ok, " + passosDivergentes + " divergencia(s). Log em " + arquivoLog.getAbsolutePath());
            System.out.println("Eventos de agentes gravados: " + gravador.totalEventos() + " em "
                    + arquivoAgentes.getAbsolutePath());
        } finally {
            if (perfisExistiaAntes) {
                copiarArquivo(backupPerfis, arquivoRealPerfis);
            } else {
                arquivoRealPerfis.delete();
            }
            if (diagnosticosExistiaAntes) {
                copiarArquivo(backupDiagnosticos, arquivoRealDiagnosticos);
            } else {
                arquivoRealDiagnosticos.delete();
            }
            log.println("Arquivos reais (perfis_usuario.tsv / diagnosticos_tarefa.tsv) restaurados ao estado anterior ao teste.");
            log.close();
        }
    }

    private static String sanitizarIdEpisodio(String idUsuarioReal) {
        return idUsuarioReal == null ? "desconhecido" : idUsuarioReal.replaceAll("[^A-Za-z0-9]+", "_").toUpperCase();
    }

    /**
     * Junta o lado Robot (gestos físicos, falhas de pickup/drop, avaliações
     * não disparadas — de {@link RobotGestureTrace}) com o lado da
     * auditoria (ações canônicas, decisões reais do ZDP/Modelador, casos,
     * duplicados, divergências — devolvido por
     * {@code AgentAuditService.finalizarEpisodio}) numa única linha de
     * {@code cardinalidade_episodios.tsv} (rodada 3, 2026-07-31).
     */
    private static void escreverCardinalidadeEpisodio(File arquivo, String episodeId,
            List<RobotGestureTrace> gestos, Map<String, Object> resumoAuditoria, RobotGestureLogger robotLog)
            throws java.io.IOException {
        int gestosFisicos = gestos.size();
        int subeventosTecnicos = 0;
        int falhasPickup = 0;
        int falhasDrop = 0;
        int avaliacoesNaoDisparadas = 0;
        for (RobotGestureTrace gesto : gestos) {
            subeventosTecnicos += gesto.getTentativas().size();
            switch (gesto.statusFinal()) {
                case PICKUP_FAILED: falhasPickup++; break;
                case DROP_FAILED: falhasDrop++; break;
                case EVALUATION_NOT_DISPATCHED: avaliacoesNaoDisparadas++; break;
                default: break;
            }
            robotLog.registrarGestoFinalizado(gesto);
        }

        EpisodeCardinalityReport relatorio = new EpisodeCardinalityReport(
                episodeId,
                gestosFisicos,
                inteiroOuZero(resumoAuditoria.get("canonical_actions")),
                subeventosTecnicos,
                inteiroOuZero(resumoAuditoria.get("canonical_actions")),
                inteiroOuZero(resumoAuditoria.get("reactive_evaluations")),
                falhasPickup,
                falhasDrop,
                avaliacoesNaoDisparadas,
                inteiroOuZero(resumoAuditoria.get("zdp_canonical_decisions")),
                inteiroOuZero(resumoAuditoria.get("modeler_canonical_updates")),
                inteiroOuZero(resumoAuditoria.get("cases_inserted")),
                inteiroOuZero(resumoAuditoria.get("duplicate_cases_blocked")),
                inteiroOuZero(resumoAuditoria.get("divergences")));
        relatorio.escreverLinha(arquivo);
    }

    private static int inteiroOuZero(Object valor) {
        return valor instanceof Integer ? ((Integer) valor).intValue() : 0;
    }

    private static void copiarArquivo(File origem, File destino) throws Exception {
        java.nio.file.Files.copy(origem.toPath(), destino.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Busca a situação-problema pelo id direto em listarTodas() (não pelo
     * sorteio normal, que só devolve validada=true por categoria aleatória —
     * aqui queremos EXATAMENTE esta situação, não uma qualquer da categoria).
     */
    private static SituacaoProblemaAditiva encontrarSituacaoPorId(final Main.TelaGerard tela, final String id)
            throws Exception {
        final SituacaoProblemaAditiva[] resultado = new SituacaoProblemaAditiva[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                for (SituacaoProblemaAditiva s : tela.repositorioSituacoesAditivas.listarTodas()) {
                    if (id.equals(s.getId())) {
                        resultado[0] = s;
                        return;
                    }
                }
            }
        });
        return resultado[0];
    }

    /**
     * Réplica mínima do que confirmarCategoriaAdivinhada (Main.java) faz para
     * carregar uma situação escolhida: preenche o estado de TelaGerard e
     * chama finalizarCarregamentoSituacao() (private — só por isso via
     * reflection) para popular elementosTexto/elementosVergnaud/
     * itensArrastaveis a partir dela.
     */
    private static void carregarSituacaoEspecifica(final Main.TelaGerard tela,
            final SituacaoProblemaAditiva situacao, final PrintWriter log) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    TipoSituacaoAditiva tipo = situacao.getTipo();
                    tela.situacaoProblemaAtual = situacao;
                    tela.textoProblema = situacao.getEnunciado();
                    tela.textoProblemaEhMensagemSistema = false;
                    tela.tipoSituacaoSelecionada = tipo;
                    tela.categoriaSelecionadaParaAtividade = true;
                    tela.aguardandoAdivinhacaoCategoria = false;
                    tela.categoriaSorteioOculta = null;

                    tela.controladorContextoSituacao.registrarNovaSituacao(situacao, tipo.name(), tela.textoProblema);
                    tela.definicaoDiagramaAtual = SemanticaCuradaSituacao.aplicarRotulos(
                            tela.catalogoDefinicoesAditivas.obter(tipo), situacao, tela.localizacao);
                    tela.resultadoInterpretacao = tela.construtorResultadoCurado.construir(situacao, tela.textoProblema);

                    Method metodo = Main.TelaGerard.class.getDeclaredMethod("finalizarCarregamentoSituacao");
                    metodo.setAccessible(true);
                    metodo.invoke(tela);
                } catch (Exception ex) {
                    log.println("  erro ao carregar situacao " + situacao.getId() + ":");
                    ex.printStackTrace(log);
                }
            }
        });
        Thread.sleep(600);
    }

    private static final int MAX_TENTATIVAS_GESTO = 3;

    /**
     * Executa um passo do episódio real via Robot — reescrito na rodada 3
     * (2026-07-31) para corrigir o bug raiz encontrado: um numeral já
     * arrastado uma vez (mesmo com veredito ERRADO) fica bloqueado para
     * novo pickup a partir da posição original no enunciado
     * ({@code PoliticaUnicidadeElementoMatematicoTexto.jaEstaNoDiagrama}),
     * mas a versão anterior deste método SEMPRE procurava a origem em
     * {@code elementosTexto} (posição original), nunca em
     * {@code itensArrastaveis} (posição atual, se já posicionado) — por
     * isso o SEGUNDO arraste errado do episódio Jamile S9 ("repete o
     * engano") nunca disparava pickup nenhum, silenciosamente.
     *
     * Agora: (1) relocaliza origem+destino do zero a cada tentativa via
     * {@link GestureCoordinateResolver} (que prioriza a posição ATUAL no
     * diagrama sobre a posição original no texto — ver
     * {@link SemanticComponentLocator}); (2) observa
     * handler de item da tela depois do mousePressed para saber se o
     * pickup realmente aconteceu; (3) observa o contador de eventos
     * gravados de {@link AgentAuditService} antes/depois da soltura pra
     * saber se uma avaliação foi de fato despachada; (4) grava CADA
     * tentativa (sucesso ou falha) em {@link RobotGestureTrace}/
     * {@code robot_gestos.log} — nenhum gesto desaparece sem deixar
     * rastro. Devolve {passosSemDivergencia, passosComDivergencia}.
     */
    private static int[] executarPasso(Robot robot, final Main.TelaGerard tela, final PassoReplayHumano passo,
            final PrintWriter log, AgentAuditService servicoAuditoria, RobotGestureTrace gesto,
            RobotGestureLogger robotLog) throws Exception {
        for (int numeroTentativa = 1; numeroTentativa <= MAX_TENTATIVAS_GESTO; numeroTentativa++) {
            RobotGestureAttempt tentativa = gesto.novaTentativa();
            try {
                final GestureCoordinateResolver.Resolucao[] resolucaoRef = new GestureCoordinateResolver.Resolucao[1];
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        resolucaoRef[0] = GestureCoordinateResolver.resolverPosicionamento(
                                tela, passo.chavePapelNumeral, passo.chavePapelAlvo);
                    }
                });
                GestureCoordinateResolver.Resolucao resolucao = resolucaoRef[0];
                String timestampPress = java.time.Instant.now().toString();

                if (!resolucao.valido) {
                    tentativa.registrarMousePressed(timestampPress, true, resolucao.origem,
                            resolucao.origem != null, false, passo.chavePapelNumeral, null);
                    tentativa.registrarCancelamento(resolucao.motivoInvalido);
                    robotLog.registrarTentativa(gesto, tentativa);
                    log.println("  [passo] tentativa " + numeroTentativa + " nao localizou componente ("
                            + resolucao.motivoInvalido + "): " + passo.descricao);
                    continue;
                }

                Point origemPonto = resolucao.pontoOrigemAbsoluto();
                Point destinoPonto = resolucao.pontoDestinoAbsoluto();

                robot.mouseMove(origemPonto.x, origemPonto.y);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.waitForIdle();

                final boolean[] itemSelecionadoNoPickup = new boolean[1];
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        itemSelecionadoNoPickup[0] =
                                tela.handlerItemTextoArrastavel.estaAtivo();
                    }
                });

                String valorItem = SemanticComponentLocator.valorDoComponente(tela, resolucao.origem);
                tentativa.registrarMousePressed(timestampPress, true, resolucao.origem, true,
                        itemSelecionadoNoPickup[0], resolucao.origem.getOrigem(), valorItem);
                robotLog.registrarTentativa(gesto, tentativa);

                if (!itemSelecionadoNoPickup[0]) {
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    robot.waitForIdle();
                    log.println("  [passo] tentativa " + numeroTentativa + " — pickup falhou (item nao "
                            + "selecionado apos mousePressed) em " + origemPonto + ": " + passo.descricao);
                    continue;
                }

                int passosArraste = 14;
                for (int i = 1; i <= passosArraste; i++) {
                    int x = origemPonto.x + (destinoPonto.x - origemPonto.x) * i / passosArraste;
                    int y = origemPonto.y + (destinoPonto.y - origemPonto.y) * i / passosArraste;
                    robot.mouseMove(x, y);
                    robot.delay(10);
                }
                robot.waitForIdle();
                tentativa.registrarMouseDragged(passosArraste, destinoPonto.x, destinoPonto.y,
                        resolucao.destino.getComponentId());

                final boolean[] itemAindaSelecionadoAntesSoltura = new boolean[1];
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        itemAindaSelecionadoAntesSoltura[0] =
                                tela.handlerItemTextoArrastavel.estaAtivo();
                    }
                });

                int eventosAntes = servicoAuditoria.getContadorEventosGravados();
                String timestampRelease = java.time.Instant.now().toString();
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                robot.waitForIdle();
                SwingUtilities.invokeAndWait(new Runnable() { public void run() { } });
                int eventosDepois = servicoAuditoria.getContadorEventosGravados();
                boolean avaliacaoDisparada = eventosDepois > eventosAntes;

                tentativa.registrarMouseReleased(timestampRelease, resolucao.destino,
                        itemAindaSelecionadoAntesSoltura[0], true, resolucao.destino.getComponentId(),
                        avaliacaoDisparada);
                robotLog.registrarTentativa(gesto, tentativa);

                if (tentativa.getStatus() == RobotGestureStatus.COMPLETED) {
                    log.println("  [passo] " + passo.descricao + " — arrastou " + passo.chavePapelNumeral
                            + " (" + origemPonto + ", origem=" + resolucao.origem.getOrigem() + ") para "
                            + passo.chavePapelAlvo + " (" + destinoPonto + ") — tentativa " + numeroTentativa);
                    gesto.definirIdentidade(servicoAuditoria.getUltimoGestureIdGravado(),
                            servicoAuditoria.getUltimoGestureIdGravado());
                    varrerDialogosAbertos(robot, log);
                    return new int[] {1, 0};
                }
                log.println("  [passo] tentativa " + numeroTentativa + " terminou em " + tentativa.getStatus()
                        + " (" + tentativa.getMotivoFalha() + "): " + passo.descricao);
            } catch (Exception ex) {
                tentativa.registrarExcecao(ex);
                robotLog.registrarTentativa(gesto, tentativa);
                log.println("  [passo] tentativa " + numeroTentativa + " lancou excecao: " + ex);
            }
        }
        log.println("  [passo] ESGOTADAS " + MAX_TENTATIVAS_GESTO + " tentativas, gesto NAO executado: "
                + passo.descricao + " (diagnostico: " + gesto.diagnosticoFidelidade() + ")");
        return new int[] {0, 1};
    }

    /**
     * Executa um passo de digitação (PassoTextoReplayHumano) via Robot:
     * duplo-clique no item que ocupa o papel-alvo (a interrogação já
     * posicionada lá, mesmo caminho real de Main.editarNumeroNatural /
     * solicitarNumeroInteiroParaInterrogacao — diálogo modal próprio, com
     * JTextField com foco e texto pré-selecionado automaticamente), digita
     * o valor curado da situação para esse papel e confirma com ENTER (o
     * campo de texto já dispara o botão padrão "Confirmar" no Enter).
     *
     * Só passos com correto=true são simulados: o catálogo real não grava o
     * valor ERRADO que o humano de fato digitou, só se acertou ou errou —
     * não há como reproduzir fielmente uma digitação errada sem inventar um
     * valor, e inventar dado é exatamente o que este projeto evita (mesmo
     * critério "na dúvida, excluir" já usado no resto da curadoria). Quando
     * correto=true, Main.confirmarValorIncognitaAceito nem abre diálogo de
     * confirmação extra (só aparece em caso de divergência do curado) — só
     * o diálogo de digitação mesmo.
     *
     * Achado na prática (2026-07-31, primeira versão deste método): o "?"
     * (interrogação) não fica pré-posicionado no papel-alvo — é um item
     * arrastável como qualquer outro (mesmo pool de elementosTexto dos
     * numerais), e sem arrastá-lo antes o duplo-clique cai no ramo de
     * "elemento ainda é a incógnita" (elementoEhPapelDaIncognita em
     * Main.java), que só mostra uma anotação no canvas
     * (informarPosicionamentoIncognitaAntesDaEdicao) — não abre diálogo
     * nenhum. De 14 episódios, só 1 tinha esse arrasto já registrado como
     * PassoReplayHumano explícito no catálogo (o único que funcionou de
     * primeira); os outros 13 falharam com "dialogo nao abriu". Por isso
     * este método agora arrasta o "?" pro papel-alvo primeiro, sempre —
     * inofensivo repetir quando o passo explícito já fez isso.
     */
    private static int[] executarPassoTexto(Robot robot, final Main.TelaGerard tela,
            final PassoTextoReplayHumano passo, final SituacaoProblemaAditiva situacao, final PrintWriter log,
            AgentAuditService servicoAuditoria, RobotGestureTrace gesto, RobotGestureLogger robotLog)
            throws Exception {
        if (!passo.correto) {
            log.println("  [texto] pulando (correto=false no protocolo, valor errado real nao fica "
                    + "registrado no catalogo): " + passo.descricao);
            gesto.novaTentativa().registrarCancelamento("passo_correto_false_nao_simulado");
            robotLog.registrarTentativa(gesto, gesto.ultimaTentativa());
            return new int[] {0, 1};
        }

        SemanticaCuradaSituacao.PapelCurado curado = SemanticaCuradaSituacao.buscar(
                situacao, gerard.i18n.ServicoLocalizacao.getInstancia(), passo.chavePapelAlvo);
        String digitos = curado == null ? "" : curado.getValor().replaceAll("[^0-9]", "");
        if (digitos.length() == 0) {
            log.println("  [texto] valor curado sem digitos para papel " + passo.chavePapelAlvo
                    + " — pulando: " + passo.descricao);
            gesto.novaTentativa().registrarCancelamento("valor_curado_sem_digitos");
            robotLog.registrarTentativa(gesto, gesto.ultimaTentativa());
            return new int[] {0, 1};
        }

        for (int numeroTentativa = 1; numeroTentativa <= MAX_TENTATIVAS_GESTO; numeroTentativa++) {
            RobotGestureAttempt tentativa = gesto.novaTentativa();
            try {
                final GestureCoordinateResolver.Resolucao[] resolucaoRef = new GestureCoordinateResolver.Resolucao[1];
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        resolucaoRef[0] = GestureCoordinateResolver.resolverPosicionamento(
                                tela, passo.chavePapelAlvo, passo.chavePapelAlvo);
                    }
                });
                GestureCoordinateResolver.Resolucao resolucao = resolucaoRef[0];
                String timestampPress = java.time.Instant.now().toString();

                if (!resolucao.valido) {
                    tentativa.registrarMousePressed(timestampPress, true, resolucao.origem,
                            resolucao.origem != null, false, passo.chavePapelAlvo, null);
                    tentativa.registrarCancelamento(resolucao.motivoInvalido);
                    robotLog.registrarTentativa(gesto, tentativa);
                    log.println("  [texto] tentativa " + numeroTentativa + " nao localizou componente ("
                            + resolucao.motivoInvalido + "): " + passo.descricao);
                    continue;
                }

                Point pontoAlvo = resolucao.pontoDestinoAbsoluto();

                // A interrogação só precisa ser arrastada se ainda não
                // estiver no diagrama (origem.getOrigem() diz onde ela foi
                // encontrada) — evita um arrasto redundante quando uma
                // tentativa anterior já a posicionou.
                if (ComponenteLocalizado.ORIGEM_TEXTO_ENUNCIADO.equals(resolucao.origem.getOrigem())) {
                    Point pontoInterrogacao = resolucao.pontoOrigemAbsoluto();
                    log.println("  [texto] tentativa " + numeroTentativa + " — arrastando a interrogacao de "
                            + passo.chavePapelAlvo + " (" + pontoInterrogacao + ") para o papel-alvo ("
                            + pontoAlvo + ") antes de digitar");
                    arrastar(robot, pontoInterrogacao, pontoAlvo);
                    robot.waitForIdle();
                    Thread.sleep(400);
                } else {
                    log.println("  [texto] tentativa " + numeroTentativa + " — interrogacao ja no diagrama para "
                            + passo.chavePapelAlvo + ", pulando arrasto preparatorio");
                }

                log.println("  [texto] " + passo.descricao + " — duplo-clique em " + passo.chavePapelAlvo
                        + " (" + pontoAlvo + ") e digita " + digitos);

                robot.mouseMove(pontoAlvo.x, pontoAlvo.y);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(80);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                robot.waitForIdle();

                Thread.sleep(400);
                boolean dialogoAbriu = existeDialogoVisivel();
                tentativa.registrarMousePressed(timestampPress, true, resolucao.origem, true, dialogoAbriu,
                        passo.chavePapelAlvo, digitos);
                if (!dialogoAbriu) {
                    tentativa.registrarCancelamento("dialogo_de_digitacao_nao_abriu");
                    robotLog.registrarTentativa(gesto, tentativa);
                    log.println("  [texto] tentativa " + numeroTentativa
                            + " — dialogo de digitacao nao abriu: " + passo.descricao);
                    continue;
                }

                int eventosAntes = servicoAuditoria.getContadorEventosGravados();
                for (int i = 0; i < digitos.length(); i++) {
                    int codigoTecla = java.awt.event.KeyEvent.VK_0 + (digitos.charAt(i) - '0');
                    robot.keyPress(codigoTecla);
                    robot.keyRelease(codigoTecla);
                    robot.delay(40);
                }
                String timestampRelease = java.time.Instant.now().toString();
                robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
                robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
                robot.waitForIdle();
                SwingUtilities.invokeAndWait(new Runnable() { public void run() { } });
                int eventosDepois = servicoAuditoria.getContadorEventosGravados();
                boolean avaliacaoDisparada = eventosDepois > eventosAntes;

                tentativa.registrarMouseReleased(timestampRelease, resolucao.destino, true, true,
                        resolucao.destino.getComponentId(), avaliacaoDisparada);
                robotLog.registrarTentativa(gesto, tentativa);

                if (tentativa.getStatus() == RobotGestureStatus.COMPLETED) {
                    gesto.definirIdentidade(servicoAuditoria.getUltimoGestureIdGravado(),
                            servicoAuditoria.getUltimoGestureIdGravado());
                    varrerDialogosAbertos(robot, log);
                    return new int[] {1, 0};
                }
                log.println("  [texto] tentativa " + numeroTentativa + " terminou em " + tentativa.getStatus()
                        + " (" + tentativa.getMotivoFalha() + "): " + passo.descricao);
            } catch (Exception ex) {
                tentativa.registrarExcecao(ex);
                robotLog.registrarTentativa(gesto, tentativa);
                log.println("  [texto] tentativa " + numeroTentativa + " lancou excecao: " + ex);
            }
        }
        log.println("  [texto] ESGOTADAS " + MAX_TENTATIVAS_GESTO + " tentativas, passo NAO executado: "
                + passo.descricao + " (diagnostico: " + gesto.diagnosticoFidelidade() + ")");
        return new int[] {0, 1};
    }

    private static void arrastar(Robot robot, Point origem, Point destino) {
        robot.mouseMove(origem.x, origem.y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        int passos = 14;
        for (int i = 1; i <= passos; i++) {
            int x = origem.x + (destino.x - origem.x) * i / passos;
            int y = origem.y + (destino.y - origem.y) * i / passos;
            robot.mouseMove(x, y);
            robot.delay(10);
        }
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static void varrerDialogosAbertos(Robot robot, PrintWriter log) throws Exception {
        for (int tentativa = 0; tentativa < 3; tentativa++) {
            Thread.sleep(200);
            if (!existeDialogoVisivel()) {
                return;
            }
            log.println("  dialogo modal detectado — enviando ENTER (tentativa " + (tentativa + 1) + ")");
            robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
            robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
        }
    }

    private static boolean existeDialogoVisivel() throws Exception {
        final boolean[] resultado = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                for (java.awt.Window w : java.awt.Window.getWindows()) {
                    if (w.isVisible() && w instanceof java.awt.Dialog) {
                        resultado[0] = true;
                        return;
                    }
                }
            }
        });
        return resultado[0];
    }

    private static Main.TelaGerard encontrarTelaGerard(final Main janela) throws Exception {
        final Main.TelaGerard[] resultado = new Main.TelaGerard[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                for (java.awt.Component c : janela.getContentPane().getComponents()) {
                    if (c instanceof javax.swing.JTabbedPane) {
                        javax.swing.JTabbedPane abas = (javax.swing.JTabbedPane) c;
                        java.awt.Component primeira = abas.getComponentAt(0);
                        if (primeira instanceof Main.TelaGerard) {
                            resultado[0] = (Main.TelaGerard) primeira;
                        }
                    }
                }
            }
        });
        return resultado[0];
    }

    /**
     * Variante de TesteMonkeySemiGuiado.GravadorAtividadeAgentes com uma
     * coluna a mais, idUsuario — usuária pediu (2026-07-30) para separar bem,
     * nos logs, o que vem de episódios reais (id "hist_*") do que é ruído
     * técnico acumulado (id genérico "usuario_local"). aoAvaliar (Monitor)
     * não recebe idUsuario do próprio evento — usa idUsuarioEpisodioAtual,
     * atualizado pelo chamador via definirUsuarioEpisodioAtual antes de cada
     * episódio, já que o Monitor é reativo simples e não guarda estado por
     * usuário mesmo na arquitetura de produção.
     */
    private static final class GravadorAtividadeAgentes
            implements OuvinteVeredictoAgenteMonitor, OuvinteEstrategiaAgenteZDP, OuvinteCasoAgenteModelador {
        private final PrintWriter destino;
        private final long inicioMs;
        private int sequencia;
        private String idUsuarioEpisodioAtual = "-";

        GravadorAtividadeAgentes(PrintWriter destino, long inicioMs) {
            this.destino = destino;
            this.inicioMs = inicioMs;
        }

        int totalEventos() {
            return sequencia;
        }

        void definirUsuarioEpisodioAtual(String idUsuario) {
            this.idUsuarioEpisodioAtual = idUsuario;
        }

        @Override
        public void aoAvaliar(boolean correto) {
            registrar(idUsuarioEpisodioAtual, "Monitor", correto ? "CORRETO" : "ERRADO");
        }

        @Override
        public void aoDecidir(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo,
                               boolean correto, CamadaEstrategiaZDP estrategia) {
            registrar(idUsuario, "ZDP", String.valueOf(estrategia));
        }

        @Override
        public void aoArmazenar(String idUsuario, DiagnosticoTarefa diagnostico) {
            registrar(idUsuario, "Modelador", String.valueOf(diagnostico.getSuporte()));
        }

        private void registrar(String idUsuario, String agente, String detalhe) {
            sequencia++;
            destino.println(sequencia + "\t" + (System.currentTimeMillis() - inicioMs) + "\t" + idUsuario
                    + "\t" + agente + "\t" + detalhe);
        }
    }
}
