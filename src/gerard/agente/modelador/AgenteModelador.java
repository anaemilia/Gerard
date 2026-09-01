package gerard.agente.modelador;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelConceitualExplicacao;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.adaptacao.RegraAdaptativaPublicada;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Único agente da arquitetura-alvo: mantém o Modelo do Usuário atualizado e
 * concentra a aprendizagem computacional.
 *
 * Implementa o armazenamento de casos, a inferência via PART e Apriori e a
 * publicação editorial de regras explicáveis. Não avalia a ação instrumental
 * e não escolhe a ajuda: recebe o registro factual produzido pelo proprietário
 * semântico. Os próprios proprietários aplicam as regras publicadas em seus
 * repertórios locais.
 */
public class AgenteModelador {
    private final RepositorioModeloUsuario repositorio;
    private final RepositorioRegrasAdaptativasPublicadas repositorioRegrasPublicadas;
    private final InferenciaRegrasModelador inferenciaRegras = new InferenciaRegrasModelador();
    private final AnalisadorNivelConceitual analisadorNivelConceitual = new AnalisadorNivelConceitual();
    // Contador global de novos casos, para o gatilho automático de mineração
    // (ver Main — dispara ao abrir/fechar quando o limiar é atingido).
    // Arquivo compartilhado em ~/Gerard/analises/contador_mineracao.txt —
    // qualquer instância de AgenteModelador lê/escreve o mesmo contador.
    private final ContadorMineracao contadorMineracao = new ContadorMineracao();
    private final List<OuvinteCasoAgenteModelador> ouvintes = new ArrayList<OuvinteCasoAgenteModelador>();
    private final List<OuvinteAuditoriaAgenteModelador> ouvintesAuditoria =
            new ArrayList<OuvinteAuditoriaAgenteModelador>();
    // Idempotência (pedido 2026-07-31): uma chave session_id|episode_id|gesture_id
    // só pode inserir UM caso, mesmo se armazenarCaso for chamado de novo pra
    // ela (ex.: reentrada acidental). Chave é opcional — chamadores que não
    // passam idempotencyKey (ex.: TesteReplayProtocolosReais, chamadas antigas)
    // continuam sem checagem, comportamento inalterado.
    private final Set<String> chavesDeIdempotenciaProcessadas = new HashSet<String>();

    public AgenteModelador(RepositorioModeloUsuario repositorio) {
        this(repositorio, new RepositorioRegrasAdaptativasPublicadas());
    }

    public AgenteModelador(
            RepositorioModeloUsuario repositorio,
            RepositorioRegrasAdaptativasPublicadas repositorioRegrasPublicadas) {
        if (repositorio == null || repositorioRegrasPublicadas == null) {
            throw new IllegalArgumentException(
                    "repositórios do modelo e das regras são obrigatórios");
        }
        this.repositorio = repositorio;
        this.repositorioRegrasPublicadas = repositorioRegrasPublicadas;
    }

    /** Publicação editorial explícita, elegível somente em login posterior. */
    public void publicarRegrasAdaptativas(
            String idUsuario,
            List<RegraAdaptativaPublicada> regras) throws IOException {
        repositorioRegrasPublicadas.publicarPara(idUsuario, regras);
    }

    public void adicionarOuvinte(OuvinteCasoAgenteModelador ouvinte) {
        if (ouvinte != null) {
            ouvintes.add(ouvinte);
        }
    }

    public void removerOuvinte(OuvinteCasoAgenteModelador ouvinte) {
        ouvintes.remove(ouvinte);
    }

    /** Canal adicional pro log estruturado de auditoria — ver OuvinteAuditoriaAgenteModelador. */
    public void adicionarOuvinteAuditoria(OuvinteAuditoriaAgenteModelador ouvinte) {
        if (ouvinte != null) {
            ouvintesAuditoria.add(ouvinte);
        }
    }

    public void removerOuvinteAuditoria(OuvinteAuditoriaAgenteModelador ouvinte) {
        ouvintesAuditoria.remove(ouvinte);
    }

    public ModeloUsuario armazenarCaso(String idUsuario, DiagnosticoTarefa diagnostico) {
        return armazenarCaso(idUsuario, diagnostico, null);
    }

    /**
     * Versão com chave de idempotência (session_id|episode_id|gesture_id):
     * se essa chave já foi processada antes, NÃO insere um segundo caso
     * (evita duplicar caso pedagógico quando a mesma ação canônica, por
     * algum motivo, chega aqui mais de uma vez) — só grava/notifica na
     * primeira vez. idempotencyKey null desliga a checagem (comportamento
     * antigo, usado por chamadores que não têm esse conceito ainda, ex.
     * TesteReplayProtocolosReais).
     */
    public ModeloUsuario armazenarCaso(String idUsuario, DiagnosticoTarefa diagnostico, String idempotencyKey) {
        long inicio = System.currentTimeMillis();
        String erro = null;
        ModeloUsuario modelo = null;
        boolean duplicado = idempotencyKey != null && chavesDeIdempotenciaProcessadas.contains(idempotencyKey);
        try {
            modelo = repositorio.obterOuCriar(idUsuario);
            if (!duplicado) {
                modelo.adicionarDiagnostico(diagnostico);
                repositorio.salvarDiagnosticos();
                contadorMineracao.incrementar();
                if (idempotencyKey != null) {
                    chavesDeIdempotenciaProcessadas.add(idempotencyKey);
                }
                for (OuvinteCasoAgenteModelador ouvinte : ouvintes) {
                    ouvinte.aoArmazenar(idUsuario, diagnostico);
                }
            }
        } catch (RuntimeException e) {
            erro = e.toString();
            throw e;
        } finally {
            if (!ouvintesAuditoria.isEmpty()) {
                ModeladorAuditData dados = construirDadosAuditoria(
                        idUsuario, diagnostico, idempotencyKey, duplicado, System.currentTimeMillis() - inicio, erro);
                for (OuvinteAuditoriaAgenteModelador ouvinte : ouvintesAuditoria) {
                    ouvinte.aoArmazenar(dados);
                }
            }
        }
        return modelo;
    }

    /**
     * Modo "avaliar sem gravar": monta o ModeladorAuditData que
     * armazenarCaso montaria, SEM chamar repositorio/adicionarDiagnostico/
     * salvarDiagnosticos nem marcar a chave de idempotência. Usado só pra
     * eventos reativos (ver Main.java, OrigemAvaliacao) — nunca cria caso
     * nem atualiza o Modelo do Usuário real.
     */
    public ModeladorAuditData avaliarSemArmazenar(String idUsuario, DiagnosticoTarefa diagnostico) {
        long inicio = System.currentTimeMillis();
        java.util.Map<String, Object> atributos = new java.util.LinkedHashMap<String, Object>();
        atributos.put("tarefa", diagnostico == null ? null : diagnostico.getTarefa());
        atributos.put("regraDeAcao", diagnostico == null ? null : diagnostico.getRegraDeAcao());
        atributos.put("suporte", diagnostico == null || diagnostico.getSuporte() == null
                ? null : diagnostico.getSuporte().name());
        atributos.put("action_id", diagnostico == null ? null : diagnostico.getActionId());
        atributos.put("avaliacao", diagnostico == null ? null : diagnostico.getAvaliacao());
        atributos.put("tipo_erro", diagnostico == null ? null : diagnostico.getTipoErro());
        atributos.put("participantes_semanticos",
                diagnostico == null ? null : diagnostico.getParticipantesSemanticos());
        CaseInsertionAudit casoInserido = new CaseInsertionAudit(false, null, "diagnosticos_tarefa.tsv", atributos,
                "reactive_evaluation_does_not_create_case — evento reativo, não persistido.");
        ModeladorAuditData dados = new ModeladorAuditData(
                "REAVALIACAO_REATIVA_NAO_PERSISTIDA",
                null, null,
                diagnostico == null ? null : diagnostico.getRegraDeAcao(),
                idUsuario, null,
                false, false, false,
                "Evento reativo — não atualiza perfil, não insere caso, não alimenta mineração.",
                java.util.Collections.<gerard.agente.conhecimento.RuleActivationAudit>emptyList(),
                DiagnosisSnapshot.indisponivel("Não existe motor de diagnóstico automático."),
                DiagnosisSnapshot.indisponivel("Não existe motor de diagnóstico automático."),
                StrategyDetectionAudit.indisponivel("Evento reativo — estratégia não avaliada."),
                casoInserido,
                PatternIncorporationAudit.indisponivel("Evento reativo — incorporação de padrão não avaliada."),
                "REAVALIACAO_NAO_PERSISTIDA",
                System.currentTimeMillis() - inicio, null);
        return dados;
    }

    private ModeladorAuditData construirDadosAuditoria(String idUsuario, DiagnosticoTarefa diagnostico,
            String idempotencyKey, boolean duplicado, long processingTimeMs, String erro) {
        java.util.Map<String, Object> atributos = new java.util.LinkedHashMap<String, Object>();
        atributos.put("tarefa", diagnostico == null ? null : diagnostico.getTarefa());
        atributos.put("regraDeAcao", diagnostico == null ? null : diagnostico.getRegraDeAcao());
        atributos.put("suporte", diagnostico == null || diagnostico.getSuporte() == null
                ? null : diagnostico.getSuporte().name());
        atributos.put("action_id", diagnostico == null ? null : diagnostico.getActionId());
        atributos.put("avaliacao", diagnostico == null ? null : diagnostico.getAvaliacao());
        atributos.put("tipo_erro", diagnostico == null ? null : diagnostico.getTipoErro());
        atributos.put("participantes_semanticos",
                diagnostico == null ? null : diagnostico.getParticipantesSemanticos());
        boolean produzidoPorProprietarioSemantico = diagnostico != null
                && diagnostico.getActionId() != null;
        String caseId = duplicado ? null
                : idUsuario + "#" + (diagnostico == null ? "?" : diagnostico.getTarefa()) + "#" + idempotencyKey;
        String motivo = duplicado
                ? "idempotency_key_already_processed — chave " + idempotencyKey
                        + " já tinha inserido um caso antes; esta chamada não duplicou."
                : "AgenteModelador.armazenarCaso não calcula similaridade com casos existentes além da checagem "
                        + "de idempotência (chave exata) — nenhum código compara conteúdo de casos hoje.";
        CaseInsertionAudit casoInserido = new CaseInsertionAudit(erro == null && !duplicado, caseId,
                "diagnosticos_tarefa.tsv", atributos, motivo);
        return new ModeladorAuditData(
                produzidoPorProprietarioSemantico
                        ? "REGISTRO_PROPRIETARIO_SEMANTICO_PROCESSADO"
                        : "MONITOR_ZDP_PROCESSADOS",
                null,
                null,
                diagnostico == null ? null : diagnostico.getRegraDeAcao(),
                idUsuario,
                null,
                !duplicado,
                erro == null && !duplicado,
                false,
                duplicado
                        ? "Chave de idempotência já processada — não atualiza perfil nem insere caso de novo."
                        : "armazenarCaso sempre atualiza o perfil e insere um novo caso; incorporação de padrão "
                                + "(PART/Apriori) é ação 2, roda sob demanda via inferirRegras, não a cada caso.",
                java.util.Collections.<gerard.agente.conhecimento.RuleActivationAudit>emptyList(),
                DiagnosisSnapshot.indisponivel(
                        "Não existe motor de diagnóstico automático — AgenteModelador só arquiva DiagnosticoTarefa bruto."),
                DiagnosisSnapshot.indisponivel(
                        "Não existe motor de diagnóstico automático — AgenteModelador só arquiva DiagnosticoTarefa bruto."),
                StrategyDetectionAudit.indisponivel(
                        "R-ENT-* dependem de explicacaoElemento/explicacaoGeral do pesquisador (TelaArtefatoExplicativo), "
                        + "preenchidos depois da ação, não durante — e por decisão de 2026-07-23 classificação "
                        + "automática de texto livre não deve alimentar decisão sem curadoria humana."),
                casoInserido,
                PatternIncorporationAudit.indisponivel(
                        "Incorporação de padrão é a ação 2 do Modelador (InferenciaRegrasModelador via "
                        + "inferirRegras), chamada sob demanda, não a cada ação armazenada."),
                "MODELO_USUARIO_ATUALIZADO",
                processingTimeMs,
                erro);
    }

    /**
     * Complementa, com o autorrelato e o invariante operatório atribuído
     * pelo pesquisador no Artefato Explicativo (ver
     * DiagnosticoTarefa.getDificuldadeAutorrelatada/getInvarianteCodigo), o
     * caso mais recente já armazenado para a tarefa indicada — não cria um
     * caso novo, a ação instrumental em si já foi armazenada por
     * armazenarCaso no momento em que aconteceu. Chamado para cada ação
     * instrumental explicada no artefato (decisão do usuário em
     * 2026-07-23). O invariante entra direto como insumo de
     * InferenciaRegrasModelador: é escolha humana no momento da ação
     * (catálogo fechado ou forma simbólica nova), não um palpite que
     * precise de curadoria posterior como nivelConceitualEstimado.
     *
     * @return true se encontrou um caso da tarefa para complementar; false
     *         se não havia nenhum (ex.: ação nunca avaliada certo/errado).
     */
    public boolean registrarExplicacaoNoUltimoDiagnostico(String idUsuario, String tarefa,
            String dificuldadeAutorrelatada, String explicacaoElemento, String explicacaoGeral,
            String invarianteOrigem, String invarianteCodigo, String invarianteSimbolico, String invarianteObservacao) {
        if (idUsuario == null || tarefa == null) {
            return false;
        }
        ModeloUsuario modelo = repositorio.obterOuCriar(idUsuario);
        List<DiagnosticoTarefa> diagnosticos = modelo.getDiagnosticos();
        for (int i = diagnosticos.size() - 1; i >= 0; i--) {
            DiagnosticoTarefa diagnostico = diagnosticos.get(i);
            if (tarefa.equals(diagnostico.getTarefa())) {
                diagnostico.setDificuldadeAutorrelatada(dificuldadeAutorrelatada);
                diagnostico.setExplicacaoElemento(explicacaoElemento);
                diagnostico.setExplicacaoGeral(explicacaoGeral);
                NivelConceitualExplicacao nivelEstimado = analisadorNivelConceitual.classificar(explicacaoElemento);
                diagnostico.setNivelConceitualEstimado(nivelEstimado);
                diagnostico.setInvarianteOrigem(invarianteOrigem);
                diagnostico.setInvarianteCodigo(invarianteCodigo);
                diagnostico.setInvarianteSimbolico(invarianteSimbolico);
                diagnostico.setInvarianteObservacao(invarianteObservacao);
                repositorio.salvarDiagnosticos();
                return true;
            }
        }
        return false;
    }

    /**
     * Roda a ação 2 sobre os casos acumulados de um usuário: PART (indução
     * de regras) + Apriori (associação). Ver InferenciaRegrasModelador para
     * o porquê de não combinar as duas saídas via AND automaticamente, e
     * para o porquê dos dois limiares mínimos (projeto ainda sem corpus
     * real acumulado).
     */
    public InferenciaRegrasModelador.Resultado inferirRegras(String idUsuario, int numeroMinimoInstanciasPart,
                                                               int numeroMinimoInstanciasApriori) throws Exception {
        ModeloUsuario modelo = repositorio.obterOuCriar(idUsuario);
        return inferenciaRegras.inferir(modelo.getDiagnosticos(), numeroMinimoInstanciasPart,
                numeroMinimoInstanciasApriori);
    }

    /**
     * Mesma ação 2, mas sobre o conjunto acumulado de TODOS os
     * participantes — usada pelo gatilho automático de mineração (Main),
     * nunca pela ferramenta manual (que continua por usuário, ver
     * inferirRegras acima). Reaproveita exatamente a mesma chamada a
     * InferenciaRegrasModelador.inferir — só a montagem da lista de entrada
     * muda (todos os modelos, em vez de um só).
     */
    public InferenciaRegrasModelador.Resultado inferirRegrasGlobal(int numeroMinimoInstanciasPart,
                                                                     int numeroMinimoInstanciasApriori) throws Exception {
        List<DiagnosticoTarefa> todos = new ArrayList<DiagnosticoTarefa>();
        for (ModeloUsuario modelo : repositorio.listarTodosOsModelos()) {
            todos.addAll(modelo.getDiagnosticos());
        }
        return inferenciaRegras.inferir(todos, numeroMinimoInstanciasPart, numeroMinimoInstanciasApriori);
    }

    /** Quantidade de novos casos armazenados desde a última mineração automática bem-sucedida. */
    public int contadorMineracaoAtual() {
        return contadorMineracao.obter();
    }

    /** Chamado depois que o gatilho automático persiste um resultado com sucesso. */
    public void zerarContadorMineracao() {
        contadorMineracao.zerar();
    }
}
