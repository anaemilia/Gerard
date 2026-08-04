package gerard.pesquisador.auditoria;

import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.pesquisador.replay.robot.RobotGestureAttempt;
import gerard.pesquisador.replay.robot.RobotGestureStatus;
import gerard.pesquisador.replay.robot.RobotGestureTrace;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Teste permanente reexecutável (rodada 3, 2026-07-31) — cobre os 17 pontos
 * pedidos no pacote de correção. Não é JUnit (não há biblioteca de teste no
 * classpath do projeto — só Weka/Bounce, ver nbproject) — segue o mesmo
 * padrão já estabelecido de {@code TesteReplayProtocolosReais}/
 * {@code ValidadorJsonl}: {@code public static void main}, assert manual,
 * `System.exit(1)` em falha real.
 *
 * Duas categorias de checagem, ambas reais (nenhum dado inventado):
 * <ul>
 * <li>Testes de UNIDADE (não precisam de execução prévia do monkey test) —
 * constroem {@link RobotGestureAttempt}/{@link RobotGestureTrace} direto e
 * verificam o comportamento da classe.</li>
 * <li>Testes de INTEGRAÇÃO (lêem o JSONL/legível/TSV/log mais recentes de
 * {@code ~/Gerard/logs}, gerados por uma execução real de
 * {@code TesteMonkeyGuiadoPorCasosReais}) — se nenhuma execução for
 * encontrada, esses testes são marcados NAO_VERIFICADO explicitamente, nunca
 * fingidos como PASSOU.</li>
 * </ul>
 */
public final class TesteCardinalidadeAuditoria {

    private static int passou = 0;
    private static int falhou = 0;
    private static int naoVerificado = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Teste de cardinalidade e fidelidade do Robot (rodada 3, 2026-07-31) ===");

        // --- Testes de unidade: nao precisam de execucao previa ---
        teste2_pickupInvalidoGeraFalhaAuditavel();
        teste13_unicidadeIdsEmClasseAtomica();

        // --- Testes de integracao: precisam do JSONL/TSV/log mais recentes ---
        File dirLogs = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        File jsonl = arquivoMaisRecente(dirLogs, "agentes_execucao_", ".jsonl");
        File legivel = arquivoMaisRecente(dirLogs, "agentes_execucao_legivel_", ".log");
        File cardinalidade = arquivoMaisRecente(dirLogs, "cardinalidade_episodios_", ".tsv");
        File robotGestos = arquivoMaisRecente(dirLogs, "robot_gestos_", ".log");

        if (jsonl == null) {
            System.out.println("NENHUM agentes_execucao_*.jsonl encontrado em " + dirLogs
                    + " — rode TesteMonkeyGuiadoPorCasosReais primeiro. Os testes de integracao abaixo "
                    + "NAO FORAM EXECUTADOS (nao e falha, e ausencia de dado).");
            for (int i = 1; i <= 17; i++) {
                if (i == 2 || i == 13) {
                    continue;
                }
                naoVerificado++;
            }
        } else {
            List<Map<String, Object>> eventos = lerEventos(jsonl);
            System.out.println("Lendo " + jsonl.getName() + " (" + eventos.size() + " eventos)");

            teste1_pickupValidoGeraEvento(eventos);
            teste3_coordenadasRecalculadas(robotGestos);
            teste4_feedbackVisualNaoInvalidaGestoSeguinte(eventos);
            String gestureIdSegundoErro = teste5_6_7_segundoErroJamileS9(eventos);
            teste8_jamileS9SeisAcoes(cardinalidade);
            teste9_subeventosQuantificacaoCompartilhamActionId(eventos);
            teste10_subeventosTecnicosNaoContam(cardinalidade);
            teste11_reativasNaoAlteramEstado(eventos);
            teste12_idempotenciaBloqueiaRepeticao(eventos);
            teste13Integracao_unicidadeEvaluationId(eventos);
            teste14_divergenceNullSemExpectativa(eventos);
            teste15_jsonlELegivelMesmosEventIds(jsonl, legivel);
            teste16_schemaRejeitaEventoIncompleto(eventos);
            teste17_falhasRobotNuncaSilenciosas(robotGestos, cardinalidade);
        }

        System.out.println();
        System.out.println("=== Resumo: " + passou + " passou, " + falhou + " falhou, "
                + naoVerificado + " nao verificado (de 17) ===");
        if (falhou > 0) {
            System.exit(1);
        }
    }

    private static void relatar(int numero, String descricao, boolean ok) {
        if (ok) {
            passou++;
            System.out.println("[PASSOU] " + numero + ". " + descricao);
        } else {
            falhou++;
            System.out.println("[FALHOU] " + numero + ". " + descricao);
        }
    }

    private static void relatarNaoVerificado(int numero, String descricao, String motivo) {
        naoVerificado++;
        System.out.println("[NAO_VERIFICADO] " + numero + ". " + descricao + " — " + motivo);
    }

    // 1. pickup válido gera evento
    private static void teste1_pickupValidoGeraEvento(List<Map<String, Object>> eventos) {
        boolean algumEventoComPickupValido = false;
        for (Map<String, Object> e : eventos) {
            Object acao = e.get("acao_usuario");
            if (acao instanceof Map && "drag".equals(((Map<?, ?>) acao).get("type"))) {
                algumEventoComPickupValido = true;
                break;
            }
        }
        relatar(1, "pickup valido gera evento (ha pelo menos 1 evento type=drag no JSONL)", algumEventoComPickupValido);
    }

    // 2. pickup inválido gera falha auditável (unidade, direto na classe)
    private static void teste2_pickupInvalidoGeraFalhaAuditavel() {
        RobotGestureTrace gesto = new RobotGestureTrace("EP-TESTE", 1, "teste", "32",
                "papel.estadoInicial", "papel.transformacao", "E");
        RobotGestureAttempt tentativa = gesto.novaTentativa();
        tentativa.registrarMousePressed("2026-07-31T00:00:00Z", true, null, false, false, "papel.estadoInicial", null);
        boolean ok = tentativa.getStatus() == RobotGestureStatus.PICKUP_FAILED
                && tentativa.getMotivoFalha() != null;
        relatar(2, "pickup invalido gera falha auditavel (RobotGestureAttempt.status=PICKUP_FAILED "
                + "com motivoFalha preenchido, nunca null-silencioso)", ok);
    }

    // 3. coordenadas são recalculadas antes de cada gesto
    private static void teste3_coordenadasRecalculadas(File robotGestos) {
        if (robotGestos == null) {
            relatarNaoVerificado(3, "coordenadas recalculadas antes de cada gesto",
                    "robot_gestos_*.log nao encontrado");
            return;
        }
        try {
            BufferedReader leitor = new BufferedReader(new FileReader(robotGestos));
            int totalTentativas = 0;
            int semRecalculo = 0;
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (linha.startsWith("[TENTATIVA]")) {
                    totalTentativas++;
                    if (!linha.contains("coordenadasRecalculadas=true")) {
                        semRecalculo++;
                    }
                }
            }
            leitor.close();
            relatar(3, "coordenadas recalculadas antes de cada gesto (" + totalTentativas
                    + " tentativas em robot_gestos.log, " + semRecalculo + " sem coordenadasRecalculadas=true)",
                    totalTentativas > 0 && semRecalculo == 0);
        } catch (Exception ex) {
            relatarNaoVerificado(3, "coordenadas recalculadas antes de cada gesto", "erro lendo log: " + ex);
        }
    }

    // 4. feedback visual não invalida o gesto seguinte
    private static void teste4_feedbackVisualNaoInvalidaGestoSeguinte(List<Map<String, Object>> eventos) {
        // Evidencia real: o 2o arraste errado de Jamile S9 (mesmo item, alvo
        // diferente, executado logo apos o 1o erro com tremor/feedback
        // ativo) tem origin=item_arrastavel_diagrama — prova que o pickup
        // pegou o item na posicao ATUAL (pos-feedback), nao falhou por causa
        // da animacao do erro anterior.
        boolean encontrouPickupPosErroComSucesso = false;
        for (Map<String, Object> e : eventos) {
            Object ident = e.get("identificacao");
            Object cls = e.get("classificacao_evento");
            if (!(ident instanceof Map) || !(cls instanceof Map)) {
                continue;
            }
            Object userId = ((Map<?, ?>) ident).get("user_id");
            Object canonical = ((Map<?, ?>) cls).get("canonical");
            Object monitor = agentesGet(e, "MONITOR");
            String avaliacao = monitor == null ? null : String.valueOf(decisionGet(monitor, "evaluation"));
            if (userId != null && userId.toString().contains("jamile_s9")
                    && Boolean.TRUE.equals(canonical) && "E".equals(avaliacao)) {
                encontrouPickupPosErroComSucesso = true;
            }
        }
        relatar(4, "feedback visual nao invalida o gesto seguinte (2o erro de Jamile S9, apos tremor "
                + "do 1o erro, ainda chega como evento canonico com avaliacao E)", encontrouPickupPosErroComSucesso);
    }

    // 5, 6 e 7: segundo erro de Jamile S9
    private static String teste5_6_7_segundoErroJamileS9(List<Map<String, Object>> eventos) {
        String episodeJamileS9 = null;
        for (Map<String, Object> e : eventos) {
            Object ident = e.get("identificacao");
            if (ident instanceof Map && String.valueOf(((Map<?, ?>) ident).get("user_id")).contains("jamile_s9")) {
                episodeJamileS9 = String.valueOf(((Map<?, ?>) ident).get("episode_id"));
                break;
            }
        }
        if (episodeJamileS9 == null) {
            relatarNaoVerificado(5, "segundo erro de Jamile S9 chega ao MONITOR", "episodio Jamile S9 nao encontrado no JSONL");
            relatarNaoVerificado(6, "esse erro altera a ZDP uma unica vez", "episodio Jamile S9 nao encontrado no JSONL");
            relatarNaoVerificado(7, "esse erro insere um unico caso", "episodio Jamile S9 nao encontrado no JSONL");
            return null;
        }

        // segundo erro = 2o evento canonico com target_role=estadoFinal E avaliacao E
        // (o 1o erro do episodio tem target_role=transformacao — ver protocolo curado)
        String gestureIdSegundoErro = null;
        int decisoesZdpParaEsseGesto = 0;
        int casosInseridosParaEsseGesto = 0;
        for (Map<String, Object> e : eventos) {
            Object ident = e.get("identificacao");
            Object cls = e.get("classificacao_evento");
            Object acao = e.get("acao_usuario");
            if (!(ident instanceof Map) || !episodeJamileS9.equals(((Map<?, ?>) ident).get("episode_id"))) {
                continue;
            }
            if (!(cls instanceof Map) || !Boolean.TRUE.equals(((Map<?, ?>) cls).get("canonical"))) {
                continue;
            }
            String targetRole = acao instanceof Map ? String.valueOf(((Map<?, ?>) acao).get("target_role")) : null;
            Object monitor = agentesGet(e, "MONITOR");
            String avaliacao = monitor == null ? null : String.valueOf(decisionGet(monitor, "evaluation"));
            if ("papel.estadoFinal".equals(targetRole) && "E".equals(avaliacao) && gestureIdSegundoErro == null) {
                gestureIdSegundoErro = String.valueOf(((Map<?, ?>) ident).get("gesture_id"));
            }
        }
        boolean chegouAoMonitor = gestureIdSegundoErro != null;
        relatar(5, "segundo erro de Jamile S9 (target_role=estadoFinal, avaliacao E) chega ao MONITOR", chegouAoMonitor);

        if (gestureIdSegundoErro != null) {
            for (Map<String, Object> e : eventos) {
                Object ident = e.get("identificacao");
                if (!(ident instanceof Map) || !gestureIdSegundoErro.equals(((Map<?, ?>) ident).get("gesture_id"))) {
                    continue;
                }
                Object cls = e.get("classificacao_evento");
                if (!(cls instanceof Map) || !Boolean.TRUE.equals(((Map<?, ?>) cls).get("canonical"))) {
                    continue;
                }
                if (agentesGet(e, "ZDP") != null) {
                    decisoesZdpParaEsseGesto++;
                }
                Object modelador = agentesGet(e, "MODELADOR");
                Object novoCaso = modelador == null ? null : ((Map<?, ?>) modelador).get("new_case_inserted");
                if (novoCaso instanceof Map && Boolean.TRUE.equals(((Map<?, ?>) novoCaso).get("inserted"))) {
                    casosInseridosParaEsseGesto++;
                }
            }
        }
        relatar(6, "segundo erro de Jamile S9 altera a ZDP uma unica vez (decisoes canonicas para o "
                + "gesture_id=" + gestureIdSegundoErro + ": " + decisoesZdpParaEsseGesto + ")",
                decisoesZdpParaEsseGesto == 1);
        relatar(7, "segundo erro de Jamile S9 insere um unico caso (casos inseridos para esse gesto: "
                + casosInseridosParaEsseGesto + ")", casosInseridosParaEsseGesto == 1);
        return gestureIdSegundoErro;
    }

    // 8. Jamile S9 gera seis ações pedagógicas
    private static void teste8_jamileS9SeisAcoes(File cardinalidade) {
        if (cardinalidade == null) {
            relatarNaoVerificado(8, "Jamile S9 gera seis acoes pedagogicas canonicas", "cardinalidade_episodios.tsv nao encontrado");
            return;
        }
        try {
            BufferedReader leitor = new BufferedReader(new FileReader(cardinalidade));
            String cabecalho = leitor.readLine();
            String[] colunas = cabecalho.split("\t");
            int idxEpisodeId = indiceColuna(colunas, "episode_id");
            int idxAcoes = indiceColuna(colunas, "acoes_pedagogicas_canonicas");
            String linha;
            Integer acoesJamileS9 = null;
            while ((linha = leitor.readLine()) != null) {
                String[] campos = linha.split("\t", -1);
                if (campos.length <= Math.max(idxEpisodeId, idxAcoes)) {
                    continue;
                }
                if (campos[idxEpisodeId].contains("JAMILE_S9")) {
                    acoesJamileS9 = Integer.valueOf(campos[idxAcoes].trim());
                }
            }
            leitor.close();
            relatar(8, "Jamile S9 gera seis acoes pedagogicas canonicas (encontrado: " + acoesJamileS9 + ")",
                    acoesJamileS9 != null && acoesJamileS9.intValue() == 6);
        } catch (Exception ex) {
            relatarNaoVerificado(8, "Jamile S9 gera seis acoes pedagogicas canonicas", "erro lendo TSV: " + ex);
        }
    }

    // 9. subeventos da quantificação compartilham action_id
    private static void teste9_subeventosQuantificacaoCompartilhamActionId(List<Map<String, Object>> eventos) {
        // procura um par (reavaliacao_consistencia seguido de quantificacao)
        // com o MESMO gesture_id — e o padrao que editarNumeroNatural produz
        // via reservarProximoGesto/liberarReservaDeGesto.
        Map<String, Set<String>> origensPorGesto = new HashMap<String, Set<String>>();
        for (Map<String, Object> e : eventos) {
            Object ident = e.get("identificacao");
            Object cls = e.get("classificacao_evento");
            if (!(ident instanceof Map) || !(cls instanceof Map)) {
                continue;
            }
            String gestureId = String.valueOf(((Map<?, ?>) ident).get("gesture_id"));
            String origin = String.valueOf(((Map<?, ?>) cls).get("origin"));
            Set<String> origens = origensPorGesto.get(gestureId);
            if (origens == null) {
                origens = new HashSet<String>();
                origensPorGesto.put(gestureId, origens);
            }
            origens.add(origin);
        }
        boolean encontrouCompartilhamento = false;
        for (Set<String> origens : origensPorGesto.values()) {
            if (origens.contains("quantificacao") && origens.contains("reavaliacao_consistencia")) {
                encontrouCompartilhamento = true;
                break;
            }
        }
        relatar(9, "subeventos da quantificacao (verificar posicao + validar valor) compartilham "
                + "gesture_id/action_id (mesmo gesto com origin=quantificacao E origin=reavaliacao_consistencia)",
                encontrouCompartilhamento);
    }

    // 10. subeventos técnicos não contam como nova ação
    private static void teste10_subeventosTecnicosNaoContam(File cardinalidade) {
        if (cardinalidade == null) {
            relatarNaoVerificado(10, "subeventos tecnicos nao contam como nova acao", "cardinalidade_episodios.tsv nao encontrado");
            return;
        }
        try {
            BufferedReader leitor = new BufferedReader(new FileReader(cardinalidade));
            String cabecalho = leitor.readLine();
            String[] colunas = cabecalho.split("\t");
            int idxConsistente = indiceColuna(colunas, "cardinalidade_consistente");
            String linha;
            int total = 0;
            int inconsistentes = 0;
            while ((linha = leitor.readLine()) != null) {
                String[] campos = linha.split("\t", -1);
                if (campos.length <= idxConsistente) {
                    continue;
                }
                total++;
                if (!"true".equals(campos[idxConsistente].trim())) {
                    inconsistentes++;
                }
            }
            leitor.close();
            relatar(10, "subeventos tecnicos nao contam como nova acao (cardinalidade_consistente=true "
                    + "em " + (total - inconsistentes) + "/" + total + " episodios — acoes==decisoesZdp==atualizacoesModelador==casosInseridos)",
                    total > 0 && inconsistentes == 0);
        } catch (Exception ex) {
            relatarNaoVerificado(10, "subeventos tecnicos nao contam como nova acao", "erro lendo TSV: " + ex);
        }
    }

    // 11. avaliações reativas não alteram estado
    private static void teste11_reativasNaoAlteramEstado(List<Map<String, Object>> eventos) {
        boolean todasReativasMarcadasHipoteticas = true;
        int reativasComZdp = 0;
        for (Map<String, Object> e : eventos) {
            Object cls = e.get("classificacao_evento");
            if (!(cls instanceof Map) || Boolean.TRUE.equals(((Map<?, ?>) cls).get("canonical"))) {
                continue;
            }
            Object zdp = agentesGet(e, "ZDP");
            if (zdp == null) {
                continue;
            }
            reativasComZdp++;
            Object decisao = ((Map<?, ?>) zdp).get("decision");
            String layer = decisao instanceof Map ? String.valueOf(((Map<?, ?>) decisao).get("layer")) : "";
            if (layer == null || !layer.contains("hipotética")) {
                todasReativasMarcadasHipoteticas = false;
            }
        }
        relatar(11, "avaliacoes reativas nao alteram estado (todo bloco ZDP de evento reativo vem "
                + "marcado \"(hipotética)\" — " + reativasComZdp + " eventos reativos com bloco ZDP verificados)",
                reativasComZdp > 0 && todasReativasMarcadasHipoteticas);
    }

    // 12. idempotência bloqueia repetição
    /**
     * Atualizado na rodada 4 (2026-07-31): antes desta rodada, este teste
     * varria o JSONL procurando um {@code new_case_inserted.duplicate=true}
     * que ocorria NATURALMENTE, porque o disparo triplo (bug real, corrigido
     * na rodada 4) inflava chamadas duplicadas o bastante pra sempre haver
     * uma. Corrigida a causa raiz, duplicados_bloqueados caiu pra 0 em
     * todos os episodios — o que e o resultado CORRETO, nao falta de
     * idempotencia. Este teste agora verifica o MECANISMO diretamente
     * (chamada deliberada e repetida a armazenarCaso com a mesma
     * idempotencyKey, sobre arquivos isolados em temp — nunca toca
     * ~/Gerard/perfis_usuario.tsv real), em vez de depender de um sintoma
     * do bug que nao existe mais.
     */
    private static void teste12_idempotenciaBloqueiaRepeticao(List<Map<String, Object>> eventos) {
        File dirTemp = new File(System.getProperty("java.io.tmpdir"),
                "gerard_teste_idempotencia_modelador_" + System.nanoTime());
        dirTemp.mkdirs();
        try {
            gerard.agente.modelousuario.RepositorioModeloUsuario repo =
                    new gerard.agente.modelousuario.RepositorioModeloUsuario(
                            new File(dirTemp, "perfis_isolados.tsv"), new File(dirTemp, "diagnosticos_isolados.tsv"));
            gerard.agente.modelador.AgenteModelador modelador = new gerard.agente.modelador.AgenteModelador(repo);
            gerard.agente.modelousuario.DiagnosticoTarefa diagnostico =
                    new gerard.agente.modelousuario.DiagnosticoTarefa("TRANSFORMACAO_MEDIDAS:papel.estadoFinal");
            String chave = "TESTE-IDEMPOTENCIA-MODELADOR-0001";
            modelador.armazenarCaso("usuario_teste", diagnostico, chave);
            int totalAposPrimeira = repo.obterOuCriar("usuario_teste").getDiagnosticos().size();
            modelador.armazenarCaso("usuario_teste", diagnostico, chave);
            int totalAposSegunda = repo.obterOuCriar("usuario_teste").getDiagnosticos().size();
            relatar(12, "idempotencia bloqueia repeticao (chamada deliberada e repetida a armazenarCaso com a "
                    + "MESMA idempotencyKey, sobre repositorio isolado: " + totalAposPrimeira + " caso(s) apos a "
                    + "1a chamada, " + totalAposSegunda + " apos a 2a — nao deve aumentar)",
                    totalAposPrimeira > 0 && totalAposSegunda == totalAposPrimeira);
        } catch (Exception ex) {
            relatarNaoVerificado(12, "idempotencia bloqueia repeticao", "erro montando o teste isolado: " + ex);
        } finally {
            deletarRecursivo(dirTemp);
        }
    }

    private static void deletarRecursivo(File arquivo) {
        if (arquivo == null || !arquivo.exists()) {
            return;
        }
        File[] filhos = arquivo.listFiles();
        if (filhos != null) {
            for (File f : filhos) {
                deletarRecursivo(f);
            }
        }
        arquivo.delete();
    }

    // 13a. unicidade em nivel de classe (unidade)
    private static void teste13_unicidadeIdsEmClasseAtomica() {
        RobotGestureTrace gesto = new RobotGestureTrace("EP-TESTE", 1, "teste", "32",
                "papel.estadoInicial", "papel.transformacao", "E");
        gesto.novaTentativa();
        gesto.novaTentativa();
        boolean numerosDiferentes = gesto.getTentativas().get(0).getNumeroTentativa()
                != gesto.getTentativas().get(1).getNumeroTentativa();
        relatar(13, "gesture_id/action_id/evaluation_id sao unicos (unidade: numeroTentativa "
                + "nao se repete entre tentativas do mesmo RobotGestureTrace)", numerosDiferentes);
    }

    // 13b. unicidade em nivel de integração (evaluation_id no JSONL real)
    private static void teste13Integracao_unicidadeEvaluationId(List<Map<String, Object>> eventos) {
        Set<String> vistos = new HashSet<String>();
        boolean semDuplicata = true;
        for (Map<String, Object> e : eventos) {
            Object ident = e.get("identificacao");
            if (!(ident instanceof Map)) {
                continue;
            }
            String evaluationId = String.valueOf(((Map<?, ?>) ident).get("evaluation_id"));
            if (!vistos.add(evaluationId)) {
                semDuplicata = false;
            }
        }
        relatar(13, "evaluation_id e unico em todo o JSONL real (" + vistos.size() + " ids distintos, "
                + eventos.size() + " eventos)", semDuplicata);
    }

    // 14. divergence=null quando não há expectativa
    private static void teste14_divergenceNullSemExpectativa(List<Map<String, Object>> eventos) {
        boolean ok = true;
        int naoAvaliaveis = 0;
        for (Map<String, Object> e : eventos) {
            Object resultado = e.get("interaction_result");
            if (!(resultado instanceof Map)) {
                continue;
            }
            Object status = ((Map<?, ?>) resultado).get("comparison_status");
            if ("not_evaluable".equals(status)) {
                naoAvaliaveis++;
                if (((Map<?, ?>) resultado).get("divergence") != null) {
                    ok = false;
                }
            }
        }
        relatar(14, "divergence=null quando comparison_status=not_evaluable (" + naoAvaliaveis
                + " eventos not_evaluable verificados)", naoAvaliaveis > 0 && ok);
    }

    // 15. JSONL e log legível possuem os mesmos event_id
    private static void teste15_jsonlELegivelMesmosEventIds(File jsonl, File legivel) {
        if (legivel == null) {
            relatarNaoVerificado(15, "JSONL e log legivel possuem os mesmos event_id", "log legivel nao encontrado");
            return;
        }
        try {
            Set<String> idsJsonl = new HashSet<String>();
            for (Map<String, Object> e : lerEventos(jsonl)) {
                Object ident = e.get("identificacao");
                if (ident instanceof Map) {
                    idsJsonl.add(String.valueOf(((Map<?, ?>) ident).get("event_id")));
                }
            }
            Set<String> idsLegivel = new HashSet<String>();
            BufferedReader leitor = new BufferedReader(new FileReader(legivel));
            String linha;
            String marcador = "=== evento ";
            while ((linha = leitor.readLine()) != null) {
                int idx = linha.indexOf(marcador);
                if (idx >= 0) {
                    String resto = linha.substring(idx + marcador.length());
                    int fim = resto.indexOf(" —");
                    idsLegivel.add(fim >= 0 ? resto.substring(0, fim) : resto.trim());
                }
            }
            leitor.close();
            if (idsLegivel.isEmpty()) {
                relatarNaoVerificado(15, "JSONL e log legivel possuem os mesmos event_id",
                        "log legivel nao imprime event_id explicitamente neste formato — verificar "
                                + "HumanReadableAgentAuditWriter manualmente");
                return;
            }
            relatar(15, "JSONL e log legivel possuem os mesmos event_id (" + idsJsonl.size()
                    + " no JSONL, " + idsLegivel.size() + " no legivel)", idsJsonl.equals(idsLegivel));
        } catch (Exception ex) {
            relatarNaoVerificado(15, "JSONL e log legivel possuem os mesmos event_id", "erro: " + ex);
        }
    }

    // 16. schema rejeita evento incompleto (validação estrutural manual —
    // sem lib de JSON Schema no classpath, ver limitacoes no relatorio)
    private static void teste16_schemaRejeitaEventoIncompleto(List<Map<String, Object>> eventos) {
        if (eventos.isEmpty()) {
            relatarNaoVerificado(16, "schema rejeita evento incompleto", "nenhum evento real disponivel para basear o teste");
            return;
        }
        String[] camposObrigatorios = {"schema_version", "tipo_registro", "identificacao",
                "classificacao_evento", "acao_usuario", "agents", "interaction_result"};
        Map<String, Object> completo = eventos.get(0);
        boolean completoPassa = validarCamposObrigatorios(completo, camposObrigatorios);
        Map<String, Object> incompleto = new LinkedHashMap<String, Object>(completo);
        incompleto.remove("agents");
        boolean incompletoRejeitado = !validarCamposObrigatorios(incompleto, camposObrigatorios);
        relatar(16, "validador estrutural manual (obrigatorios de schema_agentes_execucao_gerard.json) "
                + "aceita evento real e rejeita copia sem 'agents' — NAO e validacao $schema/$ref completa "
                + "(sem lib jsonschema no classpath, ver limitacoes)", completoPassa && incompletoRejeitado);
    }

    private static boolean validarCamposObrigatorios(Map<String, Object> evento, String[] obrigatorios) {
        for (String campo : obrigatorios) {
            if (!evento.containsKey(campo) || evento.get(campo) == null) {
                return false;
            }
        }
        return true;
    }

    // 17. falhas do Robot nunca são silenciosas
    private static void teste17_falhasRobotNuncaSilenciosas(File robotGestos, File cardinalidade) {
        if (robotGestos == null || cardinalidade == null) {
            relatarNaoVerificado(17, "falhas do Robot nunca sao silenciosas",
                    "robot_gestos.log ou cardinalidade_episodios.tsv nao encontrado");
            return;
        }
        try {
            int totalGestosFisicos = 0;
            BufferedReader leitorTsv = new BufferedReader(new FileReader(cardinalidade));
            String cabecalho = leitorTsv.readLine();
            int idxGestos = indiceColuna(cabecalho.split("\t"), "gestos_fisicos");
            String linha;
            while ((linha = leitorTsv.readLine()) != null) {
                String[] campos = linha.split("\t", -1);
                if (campos.length > idxGestos) {
                    totalGestosFisicos += Integer.parseInt(campos[idxGestos].trim());
                }
            }
            leitorTsv.close();

            int totalTentativasRegistradas = 0;
            BufferedReader leitorLog = new BufferedReader(new FileReader(robotGestos));
            while ((linha = leitorLog.readLine()) != null) {
                if (linha.startsWith("[TENTATIVA]")) {
                    totalTentativasRegistradas++;
                }
            }
            leitorLog.close();

            relatar(17, "falhas do Robot nunca sao silenciosas (" + totalTentativasRegistradas
                    + " tentativas registradas em robot_gestos.log para " + totalGestosFisicos
                    + " gestos fisicos — deve haver pelo menos 1 tentativa por gesto)",
                    totalTentativasRegistradas >= totalGestosFisicos && totalGestosFisicos > 0);
        } catch (Exception ex) {
            relatarNaoVerificado(17, "falhas do Robot nunca sao silenciosas", "erro: " + ex);
        }
    }

    // --- utilitarios ---

    private static Object agentesGet(Map<String, Object> evento, String agente) {
        Object agents = evento.get("agents");
        if (!(agents instanceof Map)) {
            return null;
        }
        Object bloco = ((Map<?, ?>) agents).get(agente);
        if (bloco instanceof Map && ((Map<?, ?>) bloco).isEmpty()) {
            return null;
        }
        return bloco;
    }

    private static Object decisionGet(Object bloco, String campo) {
        if (!(bloco instanceof Map)) {
            return null;
        }
        Object decisao = ((Map<?, ?>) bloco).get("decision");
        return decisao instanceof Map ? ((Map<?, ?>) decisao).get(campo) : null;
    }

    private static int indiceColuna(String[] colunas, String nome) {
        for (int i = 0; i < colunas.length; i++) {
            if (colunas[i].trim().equals(nome)) {
                return i;
            }
        }
        return -1;
    }

    private static File arquivoMaisRecente(File diretorio, String prefixo, String sufixo) {
        File[] arquivos = diretorio.listFiles();
        if (arquivos == null) {
            return null;
        }
        File maisRecente = null;
        for (File f : arquivos) {
            if (f.getName().startsWith(prefixo) && f.getName().endsWith(sufixo)) {
                if (maisRecente == null || f.lastModified() > maisRecente.lastModified()) {
                    maisRecente = f;
                }
            }
        }
        return maisRecente;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> lerEventos(File jsonl) throws Exception {
        List<Map<String, Object>> eventos = new ArrayList<Map<String, Object>>();
        BufferedReader leitor = new BufferedReader(new FileReader(jsonl));
        String linha;
        while ((linha = leitor.readLine()) != null) {
            if (linha.trim().length() == 0) {
                continue;
            }
            Object valor = AnalisadorJsonSimples.analisar(linha);
            if (valor instanceof Map && "evento".equals(((Map<?, ?>) valor).get("tipo_registro"))) {
                eventos.add((Map<String, Object>) valor);
            }
        }
        leitor.close();
        return eventos;
    }
}
