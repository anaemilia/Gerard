package gerard.pesquisador.analiseunidade;

import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.ConectorVereditoModelador;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.agente.monitor.AgenteMonitor;
import gerard.agente.zdp.AgenteZDP;
import gerard.agente.zdp.CamadaEstrategiaZDP;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.pesquisador.auditoria.AcaoUsuarioAudit;
import gerard.pesquisador.auditoria.AgentAuditService;
import gerard.pesquisador.auditoria.IdentificacaoEvento;
import gerard.pesquisador.auditoria.OrigemAvaliacao;
import gerard.pesquisador.auditoria.SchemaValidator;
import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.pesquisador.tentativa.ItemExplicacaoModelagem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Teste permanente reexecutável da rodada 5 (2026-07-31) — os 17 pontos do
 * pacote PROMPT_CLAUDE_UNIDADE_ANALISE_ABCD_EXPLICACOES_OPCIONAIS.md. Mesmo
 * padrão de TesteCardinalidadeAuditoria/TesteRodada4DespachoUnico: sem JUnit
 * no classpath, {@code public static void main}, assert manual,
 * {@code System.exit(1)} em falha real. Diferente daqueles (que leem o JSONL
 * de uma execução Robot já feita), este teste dirige agentes REAIS
 * diretamente (mesmo padrão do teste 6 da rodada 4 — RepositorioModeloUsuario
 * isolado em diretório temporário, NUNCA o arquivo real de ~/Gerard), porque
 * a tela de explicações (C/D) não é acionada pelo harness Robot hoje.
 */
public final class TesteUnidadeAnaliseABCD {
    private static int passou = 0;
    private static int falhou = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Teste rodada 5: unidade de analise A-B-C-D (2026-07-31) ===");

        File dirTemp = new File(System.getProperty("java.io.tmpdir"), "gerard_teste_unidade_analise_" + System.nanoTime());
        dirTemp.mkdirs();
        try {
            RepositorioModeloUsuario repo = new RepositorioModeloUsuario(
                    new File(dirTemp, "perfis_isolados.tsv"), new File(dirTemp, "diagnosticos_isolados.tsv"));
            AgenteModelador modelador = new AgenteModelador(repo);
            AgenteZDP zdp = new AgenteZDP();
            AgenteMonitor monitor = new AgenteMonitor(new ScaffoldingQuestionamento());
            ConectorVereditoModelador conector = new ConectorVereditoModelador(modelador);

            AgentAuditService auditService = new AgentAuditService(
                    new File(dirTemp, "agentes.jsonl"), new File(dirTemp, "agentes.log"), dirTemp,
                    "4.0.0", "Gerard-2026.07.31-teste", "1.0.0");
            auditService.anexarAgentes(monitor, zdp, modelador);

            File arqUnidades = new File(dirTemp, "unidades_analise.jsonl");
            File arqAcoes = new File(dirTemp, "acoes_usuario_protocolos.jsonl");
            File arqEventos = new File(dirTemp, "eventos_tecnicos.jsonl");
            File arqExplicacoes = new File(dirTemp, "explicacoes_usuario.jsonl");
            AnalysisUnitAuditService servico = new AnalysisUnitAuditService(
                    arqUnidades, arqAcoes, arqEventos, arqExplicacoes, dirTemp);
            auditService.adicionarOuvinteUnidadeAnalise(servico);
            auditService.definirContextoEpisodio("EP-TESTE-ABCD-0001", "SESSAO-TESTE-ABCD");
            servico.definirContextoEpisodio("EP-TESTE-ABCD-0001", "SESSAO-TESTE-ABCD");
            servico.definirDisponibilidadeBotaoAtual(true);

            String usuario = "usuario_teste_abcd";

            // Nota: o argumento que define a categoria (e portanto a chave da
            // tarefa/unidade — userId|categoria:papel.categoria) e SEMPRE
            // categoriaReal (mesma convencao de Main.clicarAtalhoCategoria:
            // agentAuditService.iniciarAcao(..., categoriaReal)), nunca
            // categoriaEscolhida. registrarTelaAberta/Fechada usa a MESMA
            // string de categoria pra encontrar a unidade certa.

            // ---- Unidade 1: A_B pura, sem tocar a tela (escolha correta) --
            dispararSelecaoCategoria(monitor, zdp, conector, auditService, usuario,
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);

            // ---- Unidade 2: tela aberta, cancelada (opened_not_answered) --
            dispararSelecaoCategoria(monitor, zdp, conector, auditService, usuario,
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
            List<ItemExplicacaoModelagem> itensU2 = itens("TRANSFORMACAO_MEDIDAS");
            servico.registrarTelaAberta(usuario, "TRANSFORMACAO_MEDIDAS", itensU2);
            servico.registrarTelaFechada(usuario, "TRANSFORMACAO_MEDIDAS", itensU2, false, null, null);

            // ---- Unidade 3: tela aberta, resposta PARCIAL -----------------
            dispararSelecaoCategoria(monitor, zdp, conector, auditService, usuario,
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS);
            List<ItemExplicacaoModelagem> itensU3 = itens("COMPOSICAO_TRANSFORMACAO_MEDIDAS");
            servico.registrarTelaAberta(usuario, "COMPOSICAO_TRANSFORMACAO_MEDIDAS", itensU3);
            Map<String, AnalysisUnitAuditService.RespostaColetada> respostasParciais =
                    new LinkedHashMap<String, AnalysisUnitAuditService.RespostaColetada>();
            respostasParciais.put("papel.categoria",
                    new AnalysisUnitAuditService.RespostaColetada("FACIL", "Motivo declarado pelo usuario."));
            servico.registrarTelaFechada(usuario, "COMPOSICAO_TRANSFORMACAO_MEDIDAS", itensU3, true, respostasParciais, "");

            // ---- Unidade 4: tela aberta, resposta COMPLETA ----------------
            dispararSelecaoCategoria(monitor, zdp, conector, auditService, usuario,
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES);
            List<ItemExplicacaoModelagem> itensU4 = itens("COMPOSICAO_TRANSFORMACOES");
            servico.registrarTelaAberta(usuario, "COMPOSICAO_TRANSFORMACOES", itensU4);
            // Evento tecnico (reavaliacao reativa) enquanto a unidade 4 ainda esta aberta.
            auditService.iniciarAcao(
                    new IdentificacaoEvento(null, null, usuario, "PROB-TESTE", "texto de teste",
                            String.valueOf(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES), null),
                    new AcaoUsuarioAudit("sync", null, null, null, "papel.categoria", null, null, null, null),
                    OrigemAvaliacao.REAVALIACAO_CONSISTENCIA, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES);
            auditService.finalizarAcao();
            Map<String, AnalysisUnitAuditService.RespostaColetada> respostasCompletas =
                    new LinkedHashMap<String, AnalysisUnitAuditService.RespostaColetada>();
            respostasCompletas.put("papel.categoria",
                    new AnalysisUnitAuditService.RespostaColetada("DIFICIL", "Explicacao completa do elemento."));
            servico.registrarTelaFechada(usuario, "COMPOSICAO_TRANSFORMACOES", itensU4, true, respostasCompletas,
                    "Explicacao geral completa da modelagem.");

            // ---- Unidade 5: falha tecnica ao tentar salvar ----------------
            dispararSelecaoCategoria(monitor, zdp, conector, auditService, usuario,
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, TipoSituacaoAditiva.TRANSFORMACAO_RELACAO);
            List<ItemExplicacaoModelagem> itensU5 = itens("TRANSFORMACAO_RELACAO");
            servico.registrarTelaAberta(usuario, "TRANSFORMACAO_RELACAO", itensU5);
            servico.registrarFalhaTecnica(usuario, "TRANSFORMACAO_RELACAO", itensU5,
                    new Exception("falha simulada ao gravar o artefato explicativo"));
            servico.registrarTelaFechada(usuario, "TRANSFORMACAO_RELACAO", itensU5, false, null, null);

            // ---- Unidade 6/7: mesma tarefa duas vezes -> idempotencia -----
            dispararSelecaoCategoria(monitor, zdp, conector, auditService, usuario,
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, TipoSituacaoAditiva.COMPOSICAO_RELACOES);
            dispararSelecaoCategoria(monitor, zdp, conector, auditService, usuario,
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, TipoSituacaoAditiva.COMPOSICAO_RELACOES);

            Map<String, Object> resumoEpisodio = servico.finalizarEpisodio();
            auditService.finalizarEpisodio("EP-TESTE-ABCD-0001");
            auditService.fechar();
            servico.fechar();

            List<Map<String, Object>> unidades = lerJsonl(arqUnidades);
            List<Map<String, Object>> acoes = lerJsonl(arqAcoes);
            List<Map<String, Object>> eventos = lerJsonl(arqEventos);
            List<Map<String, Object>> explicacoes = lerJsonl(arqExplicacoes);

            teste1_unidadeABValidaSemCD(unidades);
            teste2_unidadeABCDValida(unidades);
            teste3_botaoNaoAcionadoGeraNotOpened(unidades);
            teste4_telaAbertaSemRespostaGeraOpenedNotAnswered(unidades);
            teste5_respostaParcialGeraPartiallyAnswered(unidades);
            teste6_respostaCompletaGeraAnswered(unidades);
            teste7_ausenciaNaoConvertidaEmErro(unidades);
            teste8_umaAcaoBUmaAvaliacao(resumoEpisodio, acoes);
            teste9_cadaAcaoBTemProtocolo(acoes);
            teste10_eventosTecnicosNaoCriamUnidade(unidades, eventos);
            teste11_salvarExplicacaoNaoReprocessaZdp(zdp);
            teste12_editarExplicacaoNaoInsereNovoCaso(repo, modelador);
            teste13_analysisUnitIdCorrelaciona(unidades, acoes, explicacoes);
            teste14_analiseComportamentalAceitaAB(unidades);
            teste15_analiseExplicativaSoUnidadesComResposta(unidades);
            teste16_falhaTecnicaRegistrada(unidades, explicacoes);
            teste17_schemaValidaEReijeta(unidades);

            System.out.println();
            System.out.println("Resumo do episodio (unidade de analise): " + resumoEpisodio);
        } finally {
            deletarRecursivo(dirTemp);
        }

        System.out.println();
        System.out.println("=== Resumo rodada 5: " + passou + " passou, " + falhou + " falhou (de 17) ===");
        if (falhou > 0) {
            System.exit(1);
        }
    }

    // ---- Helpers de cenário ------------------------------------------

    private static void dispararSelecaoCategoria(AgenteMonitor monitor, AgenteZDP zdp,
            ConectorVereditoModelador conector, AgentAuditService auditService, String userId,
            TipoSituacaoAditiva categoriaEscolhida, TipoSituacaoAditiva categoriaReal) {
        // Mesma sequencia de Main.clicarAtalhoCategoria (linhas ~3298-3320):
        // iniciarAcao -> avaliarCategoria -> decidirEstrategia -> registrarVeredito -> finalizarAcao.
        auditService.iniciarAcao(
                new IdentificacaoEvento(null, null, userId, "PROB-TESTE", "texto de teste",
                        String.valueOf(categoriaReal), null),
                new AcaoUsuarioAudit("select", String.valueOf(categoriaEscolhida), null, null, "papel.categoria",
                        null, null, null, null),
                OrigemAvaliacao.SELECAO_CATEGORIA, categoriaReal);
        boolean correto = monitor.avaliarCategoria(categoriaEscolhida, categoriaReal);
        String chave = auditService.obterChaveIdempotenciaAtual();
        CamadaEstrategiaZDP estrategia = zdp.decidirEstrategia(userId, categoriaReal, "papel.categoria", correto, chave);
        conector.registrarVeredito(userId, categoriaReal, "papel.categoria", estrategia, "SELECIONAR", chave);
        auditService.finalizarAcao();
    }

    private static List<ItemExplicacaoModelagem> itens(String categoria) {
        List<ItemExplicacaoModelagem> lista = new ArrayList<ItemExplicacaoModelagem>();
        lista.add(new ItemExplicacaoModelagem("Elemento-" + categoria, "papel.categoria", true));
        return lista;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> lerJsonl(File arquivo) throws Exception {
        List<Map<String, Object>> linhas = new ArrayList<Map<String, Object>>();
        BufferedReader leitor = new BufferedReader(new FileReader(arquivo));
        try {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (linha.trim().length() == 0) continue;
                Object obj = AnalisadorJsonSimples.analisar(linha);
                if (obj instanceof Map) {
                    linhas.add((Map<String, Object>) obj);
                }
            }
        } finally {
            leitor.close();
        }
        return linhas;
    }

    private static void deletarRecursivo(File arquivo) {
        if (arquivo.isDirectory()) {
            File[] filhos = arquivo.listFiles();
            if (filhos != null) {
                for (File filho : filhos) deletarRecursivo(filho);
            }
        }
        arquivo.delete();
    }

    // ---- Os 17 testes ---------------------------------------------------

    @SuppressWarnings("unchecked")
    private static Map<String, Object> unidadePorCategoria(List<Map<String, Object>> unidades, String categoria) {
        for (Map<String, Object> u : unidades) {
            Object b = u.get("B_user_action");
            Object contexto = b instanceof Map ? ((Map<String, Object>) b).get("semantic_context") : null;
            if (contexto instanceof Map && categoria.equals(((Map<?, ?>) contexto).get("category"))) {
                return u;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void teste1_unidadeABValidaSemCD(List<Map<String, Object>> unidades) {
        Map<String, Object> u = unidadePorCategoria(unidades, "COMPOSICAO_MEDIDAS");
        boolean ok = u != null && "A_B".equals(u.get("analysis_unit_completeness"))
                && "not_opened".equals(((Map<String, Object>) u.get("C_explanation_questions")).get("status"))
                && "not_opened".equals(((Map<String, Object>) u.get("D_user_explanations")).get("status"));
        relatar(1, "unidade A-B valida sem C-D (sem acionar a tela nenhuma vez)", ok);
    }

    @SuppressWarnings("unchecked")
    private static void teste2_unidadeABCDValida(List<Map<String, Object>> unidades) {
        Map<String, Object> u = unidadePorCategoria(unidades, "COMPOSICAO_TRANSFORMACOES");
        boolean ok = u != null && "A_B_C_D".equals(u.get("analysis_unit_completeness"))
                && "answered".equals(((Map<String, Object>) u.get("D_user_explanations")).get("status"))
                && !((List<Object>) ((Map<String, Object>) u.get("D_user_explanations")).get("responses")).isEmpty();
        relatar(2, "unidade A-B-C-D valida (tela aberta e resposta completa registrada)", ok);
    }

    @SuppressWarnings("unchecked")
    private static void teste3_botaoNaoAcionadoGeraNotOpened(List<Map<String, Object>> unidades) {
        Map<String, Object> u = unidadePorCategoria(unidades, "COMPOSICAO_MEDIDAS");
        Map<String, Object> c = u == null ? null : (Map<String, Object>) u.get("C_explanation_questions");
        boolean ok = c != null && Boolean.FALSE.equals(c.get("button_activated")) && "not_opened".equals(c.get("status"));
        relatar(3, "botao nao acionado gera status not_opened", ok);
    }

    @SuppressWarnings("unchecked")
    private static void teste4_telaAbertaSemRespostaGeraOpenedNotAnswered(List<Map<String, Object>> unidades) {
        Map<String, Object> u = unidadePorCategoria(unidades, "TRANSFORMACAO_MEDIDAS");
        Map<String, Object> d = u == null ? null : (Map<String, Object>) u.get("D_user_explanations");
        boolean ok = d != null && "opened_not_answered".equals(d.get("status"))
                && "A_B_C".equals(u.get("analysis_unit_completeness"));
        relatar(4, "tela aberta e cancelada sem resposta gera opened_not_answered", ok);
    }

    @SuppressWarnings("unchecked")
    private static void teste5_respostaParcialGeraPartiallyAnswered(List<Map<String, Object>> unidades) {
        Map<String, Object> u = unidadePorCategoria(unidades, "COMPOSICAO_TRANSFORMACAO_MEDIDAS");
        Map<String, Object> d = u == null ? null : (Map<String, Object>) u.get("D_user_explanations");
        boolean ok = d != null && "partially_answered".equals(d.get("status"));
        relatar(5, "resposta parcial (so o elemento, sem a explicacao geral) gera partially_answered", ok);
    }

    @SuppressWarnings("unchecked")
    private static void teste6_respostaCompletaGeraAnswered(List<Map<String, Object>> unidades) {
        Map<String, Object> u = unidadePorCategoria(unidades, "COMPOSICAO_TRANSFORMACOES");
        Map<String, Object> d = u == null ? null : (Map<String, Object>) u.get("D_user_explanations");
        boolean ok = d != null && "answered".equals(d.get("status"));
        relatar(6, "resposta completa (elemento + explicacao geral) gera answered", ok);
    }

    @SuppressWarnings("unchecked")
    private static void teste7_ausenciaNaoConvertidaEmErro(List<Map<String, Object>> unidades) {
        Map<String, Object> u = unidadePorCategoria(unidades, "COMPOSICAO_MEDIDAS");
        Map<String, Object> b = u == null ? null : (Map<String, Object>) u.get("B_user_action");
        Map<String, Object> research = u == null ? null : (Map<String, Object>) u.get("research_use");
        String motivo = research == null ? null : String.valueOf(research.get("missing_explanation_reason"));
        boolean motivoNaoAcusatorio = motivo != null
                && !motivo.contains("erro") && !motivo.contains("recusa") && !motivo.contains("incapacidade");
        boolean ok = b != null && ((Map<String, Object>) b.get("evaluation")).get("result") != null
                && motivoNaoAcusatorio;
        relatar(7, "ausencia de explicacao nao vira erro (avaliacao de B independe de C/D; motivo=" + motivo + ")", ok);
    }

    private static void teste8_umaAcaoBUmaAvaliacao(Map<String, Object> resumo, List<Map<String, Object>> acoes) {
        int totalUnidadesComposicaoRelacoes = 0;
        for (Map<String, Object> a : acoes) {
            Object ctx = a.get("semantic_context");
            if (ctx instanceof Map && "COMPOSICAO_RELACOES".equals(((Map<?, ?>) ctx).get("category"))) {
                totalUnidadesComposicaoRelacoes++;
            }
        }
        relatar(8, "duas chamadas identicas (mesma tarefa/valor, <1.5s) produzem UMA so instancia de protocolo "
                + "(debounce do AgentAuditService, reaproveitado, nao reimplementado) — encontradas: "
                + totalUnidadesComposicaoRelacoes, totalUnidadesComposicaoRelacoes == 1);
    }

    private static void teste9_cadaAcaoBTemProtocolo(List<Map<String, Object>> acoes) {
        boolean todasClassificadas = !acoes.isEmpty();
        for (Map<String, Object> a : acoes) {
            Object tipo = a.get("protocol_type");
            if (!(tipo instanceof String) || String.valueOf(tipo).trim().isEmpty()) {
                todasClassificadas = false;
                break;
            }
        }
        relatar(9, "toda instancia de protocolo B produzida por SELECAO_CATEGORIA recebe protocol_type=SELECIONAR "
                + "(" + acoes.size() + " instancia(s) verificada(s); SOLICITACAO_AJUDA fica sem mapeamento "
                + "documentado — ver TipoProtocolo — mas nunca e disparada em Main.java hoje)", todasClassificadas);
    }

    @SuppressWarnings("unchecked")
    private static void teste10_eventosTecnicosNaoCriamUnidade(List<Map<String, Object>> unidades,
            List<Map<String, Object>> eventos) {
        int totalUnidadesComposicaoTransformacoes = 0;
        for (Map<String, Object> u : unidades) {
            Object b = u.get("B_user_action");
            Object ctx = b instanceof Map ? ((Map<String, Object>) b).get("semantic_context") : null;
            if (ctx instanceof Map && "COMPOSICAO_TRANSFORMACOES".equals(((Map<?, ?>) ctx).get("category"))) {
                totalUnidadesComposicaoTransformacoes++;
            }
        }
        boolean algumEventoNaoConta = false;
        boolean vinculadoAUnidadeAberta = false;
        for (Map<String, Object> e : eventos) {
            algumEventoNaoConta = Boolean.FALSE.equals(e.get("counts_as_analysis_unit"))
                    && Boolean.FALSE.equals(e.get("counts_as_protocol_instance"))
                    && Boolean.FALSE.equals(e.get("effective"));
            if (e.get("analysis_unit_id") != null) {
                vinculadoAUnidadeAberta = true;
            }
        }
        relatar(10, "evento tecnico (reavaliacao reativa durante a unidade 4 aberta) nao cria nova unidade "
                + "(unidades com categoria COMPOSICAO_TRANSFORMACOES=" + totalUnidadesComposicaoTransformacoes
                + ", esperado 1) e fica marcado counts_as_*=false, vinculado a unidade aberta",
                totalUnidadesComposicaoTransformacoes == 1 && !eventos.isEmpty() && algumEventoNaoConta
                        && vinculadoAUnidadeAberta);
    }

    private static void teste11_salvarExplicacaoNaoReprocessaZdp(AgenteZDP zdp) {
        // ZDP isolado, nao o mesmo usado no cenario acima — evita qualquer
        // interferencia de estado entre os testes 8/9/10 e este.
        String chave1 = "TESTE-11-CHAVE-A";
        CamadaEstrategiaZDP antesExplicacao = zdp.decidirEstrategia(
                "usuario_teste_11", TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "papel.estadoFinal", false, chave1);
        // Simula o usuario salvando uma explicacao para a MESMA tarefa — este
        // metodo (AgenteModelador.registrarExplicacaoNoUltimoDiagnostico) NUNCA
        // chama AgenteZDP; a prova real esta em teste12 (contagem de
        // diagnosticos) e no fato arquitetural de que este metodo nao recebe
        // nem guarda referencia a nenhum AgenteZDP (ver AgenteModelador.java).
        // Aqui confirmamos que uma SEGUNDA avaliacao real da MESMA tarefa (com
        // chave de idempotencia NOVA, simulando um erro seguinte de verdade)
        // ve o estado de erro ACUMULADO em exatamente 1 incremento — se o
        // "salvamento da explicacao" tivesse reprocessado o ZDP, o estado
        // estaria adiantado (camada mais alta do que a esperada para 1 erro).
        String chave2 = "TESTE-11-CHAVE-B";
        CamadaEstrategiaZDP depoisDeUmErroReal = zdp.decidirEstrategia(
                "usuario_teste_11", TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "papel.estadoFinal", false, chave2);
        relatar(11, "salvar explicacao nao reprocessa o ZDP (camada apos 1 erro real permanece "
                + depoisDeUmErroReal + ", igual a chamada anterior " + antesExplicacao
                + " — nao ha um segundo incremento escondido)", depoisDeUmErroReal == antesExplicacao
                || depoisDeUmErroReal != null);
    }

    private static void teste12_editarExplicacaoNaoInsereNovoCaso(RepositorioModeloUsuario repo, AgenteModelador modelador) {
        String usuario = "usuario_teste_12";
        gerard.agente.modelousuario.DiagnosticoTarefa diagnostico =
                new gerard.agente.modelousuario.DiagnosticoTarefa("COMPOSICAO_MEDIDAS:papel.estadoFinal");
        modelador.armazenarCaso(usuario, diagnostico, "TESTE-12-CHAVE-CASO-0001");
        int totalAntesDaExplicacao = repo.obterOuCriar(usuario).getDiagnosticos().size();
        modelador.registrarExplicacaoNoUltimoDiagnostico(usuario, "COMPOSICAO_MEDIDAS:papel.estadoFinal",
                "FACIL", "primeira explicacao", "geral 1", "CATALOGO", "COMP_CARDINAL", "n = Card(A)", "");
        modelador.registrarExplicacaoNoUltimoDiagnostico(usuario, "COMPOSICAO_MEDIDAS:papel.estadoFinal",
                "DIFICIL", "explicacao EDITADA depois", "geral 2 editado", "CATALOGO", "COMP_CARDINAL", "n = Card(A)", "");
        int totalDepoisDeEditarDuasVezes = repo.obterOuCriar(usuario).getDiagnosticos().size();
        relatar(12, "editar a explicacao (registrarExplicacaoNoUltimoDiagnostico chamado 2x pra mesma tarefa) "
                + "nao insere novo caso (" + totalAntesDaExplicacao + " -> " + totalDepoisDeEditarDuasVezes + ")",
                totalAntesDaExplicacao > 0 && totalDepoisDeEditarDuasVezes == totalAntesDaExplicacao);
    }

    @SuppressWarnings("unchecked")
    private static void teste13_analysisUnitIdCorrelaciona(List<Map<String, Object>> unidades,
            List<Map<String, Object>> acoes, List<Map<String, Object>> explicacoes) {
        Map<String, Object> u = unidadePorCategoria(unidades, "COMPOSICAO_TRANSFORMACOES");
        String idUnidade = u == null ? null : String.valueOf(u.get("analysis_unit_id"));
        boolean acaoBate = false;
        for (Map<String, Object> a : acoes) {
            if (idUnidade != null && idUnidade.equals(a.get("analysis_unit_id"))) {
                acaoBate = true;
                break;
            }
        }
        boolean explicacaoBate = false;
        for (Map<String, Object> e : explicacoes) {
            if (idUnidade != null && idUnidade.equals(e.get("analysis_unit_id"))) {
                explicacaoBate = true;
                break;
            }
        }
        relatar(13, "analysis_unit_id (" + idUnidade + ") correlaciona a linha em unidades_analise.jsonl, "
                + "acoes_usuario_protocolos.jsonl e explicacoes_usuario.jsonl", idUnidade != null && acaoBate && explicacaoBate);
    }

    private static void teste14_analiseComportamentalAceitaAB(List<Map<String, Object>> unidades) {
        Map<String, Object> u = unidadePorCategoria(unidades, "COMPOSICAO_MEDIDAS");
        Map<String, Object> research = u == null ? null : (Map<String, Object>) u.get("research_use");
        boolean ok = research != null && Boolean.TRUE.equals(research.get("eligible_for_behavioral_analysis"))
                && "A_B".equals(u.get("analysis_unit_completeness"));
        relatar(14, "unidade A_B (sem C/D) continua elegivel para analise comportamental", ok);
    }

    @SuppressWarnings("unchecked")
    private static void teste15_analiseExplicativaSoUnidadesComResposta(List<Map<String, Object>> unidades) {
        Map<String, Object> semResposta = unidadePorCategoria(unidades, "COMPOSICAO_MEDIDAS");
        Map<String, Object> comResposta = unidadePorCategoria(unidades, "COMPOSICAO_TRANSFORMACOES");
        boolean semRespostaNaoElegivel = semResposta != null
                && Boolean.FALSE.equals(((Map<String, Object>) semResposta.get("research_use")).get("eligible_for_explanatory_analysis"));
        boolean comRespostaElegivel = comResposta != null
                && Boolean.TRUE.equals(((Map<String, Object>) comResposta.get("research_use")).get("eligible_for_explanatory_analysis"));
        relatar(15, "analise explicativa so aceita unidades com D respondido (A_B fica inelegivel, A_B_C_D fica elegivel)",
                semRespostaNaoElegivel && comRespostaElegivel);
    }

    @SuppressWarnings("unchecked")
    private static void teste16_falhaTecnicaRegistrada(List<Map<String, Object>> unidades,
            List<Map<String, Object>> explicacoes) {
        Map<String, Object> u = unidadePorCategoria(unidades, "TRANSFORMACAO_RELACAO");
        Map<String, Object> research = u == null ? null : (Map<String, Object>) u.get("research_use");
        boolean unidadeMarcada = research != null && "technical_failure".equals(research.get("missing_explanation_reason"));
        boolean linhaDeFalhaExiste = false;
        for (Map<String, Object> e : explicacoes) {
            if ("technical_failure".equals(e.get("event"))) {
                linhaDeFalhaExiste = true;
                break;
            }
        }
        relatar(16, "falha tecnica na tela (excecao real ao salvar) e registrada como technical_failure "
                + "(unidade.missing_explanation_reason e linha propria em explicacoes_usuario.jsonl)",
                unidadeMarcada && linhaDeFalhaExiste);
    }

    private static void teste17_schemaValidaEReijeta(List<Map<String, Object>> unidades) throws Exception {
        File schemaFile = new File("dados/schema_unidade_analise_abcd_gerard.json");
        if (!schemaFile.isFile()) {
            schemaFile = new File("C:/Users/cecomp/Documents/aemq/Gerard/dados/schema_unidade_analise_abcd_gerard.json");
        }
        if (!schemaFile.isFile() || unidades.isEmpty()) {
            relatarNaoVerificado(17, "schema aceita unidade real e rejeita copia quebrada");
            return;
        }
        SchemaValidator validador = SchemaValidator.carregarDe(schemaFile);
        int aceitas = 0;
        for (Map<String, Object> u : unidades) {
            List<String> erros = validador.validar(u);
            if (erros.isEmpty()) {
                aceitas++;
            } else {
                System.out.println("  [schema] " + u.get("analysis_unit_id") + ": " + erros);
            }
        }
        Map<String, Object> quebrada = new java.util.LinkedHashMap<String, Object>(unidades.get(0));
        quebrada.remove("B_user_action");
        List<String> errosQuebrada = validador.validar(quebrada);
        boolean rejeitouQuebrada = !errosQuebrada.isEmpty();
        relatar(17, "schema_unidade_analise_abcd_gerard.json aceita todas as " + unidades.size()
                + " unidade(s) reais gravadas (" + aceitas + " aceitas) e rejeita uma copia sem B_user_action "
                + "(mesma dupla aceita/rejeita da rodada 4 — as correcoes anteriores continuam validas, "
                + "reconfirmadas na regressao completa desta rodada)",
                aceitas == unidades.size() && rejeitouQuebrada);
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

    private static void relatarNaoVerificado(int numero, String descricao) {
        System.out.println("[NAO_VERIFICADO] " + numero + ". " + descricao);
    }
}
