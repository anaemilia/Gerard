package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicaoDeTransformacoes;
import gerard.semantica.numero.ValorNumerico;
import java.util.Arrays;
import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;

import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.representacao.texto.RealizadorTextoExplicacaoOperacaoRelacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.CatalogoNecessidadeRepresentacaoDeSinal;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fachada de aplicação portátil para Composição de Transformações na web,
 * fiel ao paradigma do desktop: os 6 papéis (3 estados + 3 transformações)
 * aparecem revelados desde o início — quando a base curada tiver valor para
 * eles; hoje ela só preenche as 3 transformações para esta categoria, os 3
 * estados ficam "não conhecido" (ver {@link #posicionarSeCurado}) — e o
 * aluno escolhe a operação certa (soma/subtração) em duas etapas
 * sequenciais — a segunda só libera depois que a primeira é respondida
 * corretamente, mesma regra de {@code Main.java} para
 * {@code seletorOperacaoRelacaoAluno}/
 * {@code seletorOperacaoEstadoTransformacaoAluno}.
 *
 * A escolha de operação é avaliada por {@link AvaliacaoEscolhaOperacaoRelacao}
 * (extraída de {@code gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno} em
 * 2026-09-03). Como em Composição de Relações, a incógnita curada
 * ({@code termo_desconhecido}) é arrastável e recebe uma proposta de valor
 * em toda situação que a tenha, com ou sem operação curada: os dois
 * mecanismos coexistem. O papel incógnito nunca é pré-posicionado com o
 * valor curado.
 */
public final class ServicoAtividadeWebComposicaoTransformacoes
        implements ServicoAtividadeWeb, ServicoAtividadeWebEscolhaOperacao, ServicoAtividadeWebComSinal {
    public static final String SCHEMA_ESTADO = ServicoAtividadeWebComposicao.SCHEMA_ESTADO;
    public static final String SCHEMA_RESULTADO = ServicoAtividadeWebComposicao.SCHEMA_RESULTADO;

    private final String tentativaId;
    private final SituacaoProblemaAditiva situacao;
    private final EscopoTentativaWeb escopo;
    private final AvaliadorOrigemDestinoWeb avaliadorOrigemDestino;
    private SinalNumeroRelativoWeb sinais;
    private PapelQuantitativo estadoInicial;
    private PapelQuantitativo transformacao1;
    private PapelQuantitativo estadoIntermediario;
    private PapelQuantitativo transformacao2;
    private PapelQuantitativo transformacaoFinal;
    private PapelQuantitativo estadoFinal;
    // Decomposição opcional do estado inicial (ver
    // SituacaoProblemaAditiva.getEstadoInicialParte1/2) — "não conhecido"
    // como qualquer outro papel sem valor curado nesta categoria (ver
    // reiniciar/temValorCurado); só aparecem ao aluno quando a situação tem
    // essa decomposição.
    private PapelQuantitativo estadoInicialParte1;
    private PapelQuantitativo estadoInicialParte2;
    private final RelacaoEstruturalComposicaoDeTransformacoes relacaoDiagnostico =
            RelacaoEstruturalComposicaoDeTransformacoes.composicaoDeTransformacoes();
    private IncognitaQuantitativa incognita;
    // null quando a situação não tem incógnita curada resolvível.
    private PapelQuantitativo papelDesconhecido;
    private boolean incognitaEngatada;
    // Papéis que têm elemento (token) próprio no enunciado; null = desconhecido (todos arrastáveis).
    private java.util.Set<String> papeisComElementoNoEnunciado;
    private OpcaoOperacaoCuradoria escolhaEntreTransformacoes;
    private OpcaoOperacaoCuradoria escolhaEntreEstadoTransformacao;
    // Papel conhecido que precisa de representação de sinal (ver
    // ServicoAtividadeWebComSinal) já revelado (arrastado) mas ainda sem
    // sinal escolhido — mesmo protocolo do desktop e mesmo padrão já usado
    // por ServicoAtividadeWebComparacaoMedidas/TransformacaoMedidas.
    private PapelQuantitativo papelAguardandoSinal;
    private Integer valorCuradoAguardandoSinal;

    public ServicoAtividadeWebComposicaoTransformacoes(String tentativaId,
            SituacaoProblemaAditiva situacao) {
        this(tentativaId, situacao, EscopoTentativaWeb.isolado(tentativaId));
    }

    public ServicoAtividadeWebComposicaoTransformacoes(String tentativaId,
            SituacaoProblemaAditiva situacao, EscopoTentativaWeb escopo) {
        this(tentativaId, situacao, escopo, PortaQuestionamentoPosicionamento.NAO_APLICAVEL);
    }

    public ServicoAtividadeWebComposicaoTransformacoes(String tentativaId,
            SituacaoProblemaAditiva situacao, EscopoTentativaWeb escopo,
            PortaQuestionamentoPosicionamento questionamento) {
        if (situacao == null
                || situacao.getTipo() != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            throw new IllegalArgumentException(
                    "situação de Composição de Transformações é obrigatória");
        }
        this.tentativaId = tentativaId;
        this.situacao = situacao;
        this.escopo = escopo == null ? EscopoTentativaWeb.isolado(tentativaId) : escopo;
        this.avaliadorOrigemDestino = new AvaliadorOrigemDestinoWeb(questionamento);
        reiniciar();
    }

    public synchronized Map<String, Object> estadoAtual() {
        Map<String, Object> estado = mapa();
        estado.put("schema", SCHEMA_ESTADO);
        estado.put("situacao_id", situacao.getId());
        estado.put("situacao_grupo_id", situacao.getSituacaoGrupoId());
        estado.put("situacao_validada", Boolean.valueOf(situacao.isValidada()));
        estado.put("idioma", situacao.getCodigoIdioma());
        estado.put("fonte", situacao.getFonte());
        estado.put("contexto", situacao.getContexto());
        estado.put("tentativa_id", tentativaId);
        estado.put("categoria", "COMPOSICAO_TRANSFORMACOES");
        estado.put("enunciado", new gerard.campoaditivo.curadoria.MaterializadorEnunciadoCurado()
                .materializar(situacao));
        estado.put("estado_inicial", projetarPapel(estadoInicial));
        estado.put("transformacao_1", projetarPapel(transformacao1));
        estado.put("estado_intermediario", projetarPapel(estadoIntermediario));
        estado.put("transformacao_2", projetarPapel(transformacao2));
        estado.put("transformacao_final", projetarPapel(transformacaoFinal));
        estado.put("estado_final", projetarPapel(estadoFinal));
        estado.put("estado_inicial_parte1", projetarPapel(estadoInicialParte1));
        estado.put("estado_inicial_parte2", projetarPapel(estadoInicialParte2));
        boolean primeiraAtiva = seletorAtivo(TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        boolean segundaAtiva = seletorAtivo(TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO);
        boolean primeiraCorreta = primeiraAtiva && respondeuCorretamente(escolhaEntreTransformacoes,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        // A segunda etapa só depende da primeira estar correta quando a
        // primeira de fato existe (tem operacao_relacao curada) -- se não
        // existe, não há o que aguardar (mesmo espírito de estaAtivo() no
        // desktop: um seletor inativo nunca bloqueia nada).
        boolean segundaHabilitada = !primeiraAtiva || primeiraCorreta;
        boolean segundaCorreta = segundaAtiva && segundaHabilitada
                && respondeuCorretamente(escolhaEntreEstadoTransformacao,
                        TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO);
        estado.put("escolha_entre_transformacoes", nomeOuNull(escolhaEntreTransformacoes));
        estado.put("escolha_entre_estado_transformacao",
                nomeOuNull(escolhaEntreEstadoTransformacao));
        estado.put("correta_entre_transformacoes",
                !primeiraAtiva || escolhaEntreTransformacoes == null
                        || escolhaEntreTransformacoes == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                                ? null : Boolean.valueOf(primeiraCorreta));
        estado.put("correta_entre_estado_transformacao",
                !segundaAtiva || escolhaEntreEstadoTransformacao == null
                        || escolhaEntreEstadoTransformacao == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                                ? null : Boolean.valueOf(segundaCorreta));
        estado.put("segunda_etapa_habilitada", Boolean.valueOf(segundaHabilitada));
        // Escolher a operação só escolhe; o "?" é preenchido pelo protocolo mouse-texto. Sem a
        // incógnita preenchida (quando a situação tem uma) a atividade não está concluída.
        boolean incognitaOk = papelDesconhecido == null || papelDesconhecido.estaPreenchido();
        boolean concluida = incognitaOk
                && (!primeiraAtiva || primeiraCorreta) && (!segundaAtiva || segundaCorreta);
        estado.put("concluida", Boolean.valueOf(concluida));
        estado.put("escalada_no_limite", Boolean.valueOf(
                escopo.getTentativa().estaNoLimiteAjudaVisual()));
        // Protocolo de mouse é posicionar (ver ServicoAtividadeWebComposicao):
        // os papéis com valor curado não vêm pré-preenchidos; escolher a
        // operação só libera depois deles estarem posicionados. Papéis sem
        // valor curado (tipicamente os 3 de estado nesta categoria) nunca
        // bloqueiam — não têm o que posicionar.
        boolean papeisConhecidosProntos = todosOsConhecidosPreenchidos();
        estado.put("papel_aguardando_sinal",
                papelAguardandoSinal == null ? null : papelAguardandoSinal.getChave());
        estado.put("magnitude_aguardando_sinal", valorCuradoAguardandoSinal == null
                ? null : Integer.valueOf(Math.abs(valorCuradoAguardandoSinal.intValue())));
        List<Object> acoes = AcoesDisponiveisAtividadeWeb.sorteios();
        acoes.add(AcoesDisponiveisAtividadeWeb.acaoReiniciar());
        if (papelAguardandoSinal != null) {
            // Enquanto o sinal não é escolhido, nenhuma outra ação de
            // posicionamento fica disponível para este papel — mesmo
            // protocolo do desktop e do padrão já usado em
            // ServicoAtividadeWebComparacaoMedidas/TransformacaoMedidas.
            acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEscolherSinal(papelAguardandoSinal.getChave()));
        } else if (!papeisConhecidosProntos) {
            for (PapelQuantitativo papel : todosOsPapeis()) {
                if (ehConhecido(papel) && !papel.estaPreenchido()) {
                    acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoPosicionarConhecido(papel.getChave()));
                }
            }
        } else {
            if (papelDesconhecido != null && !papelDesconhecido.estaPreenchido()) {
                // ENGATAR_INCOGNITA é o que o arraste do "?" dispara; PROPOR_VALOR_PAPEL é a
                // digitação, que o cliente só habilita depois de engatada (mesmo padrão de
                // Composição de Relações).
                acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEngatarIncognita(papelDesconhecido.getChave()));
                acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoProporValorPapel(papelDesconhecido.getChave()));
            }
            // Os dois gates são independentes (ver seletorAtivo/Javadoc de
            // ServicoAtividadeWebTransformacaoRelacao, mesmo espírito) --
            // oferece cada etapa enquanto ativa e ainda não respondida certa.
            if (primeiraAtiva && !primeiraCorreta) {
                acoes.add(AcoesDisponiveisAtividadeWeb
                        .acaoEscolherOperacaoRelacaoComSeletor(false));
            }
            if (segundaAtiva && segundaHabilitada && !segundaCorreta) {
                acoes.add(AcoesDisponiveisAtividadeWeb
                        .acaoEscolherOperacaoRelacaoComSeletor(true));
            }
        }
        estado.put("acoes_disponiveis", acoes);
        return estado;
    }

    public synchronized Map<String, Object> escolherOperacao(String seletor, String operacao) {
        TipoOperacaoSeletor seletorConvertido = converterSeletor(seletor);
        if (seletorConvertido == TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO
                && !segundaEtapaHabilitada()) {
            throw new IllegalStateException(
                    "a segunda etapa só libera depois que a primeira é respondida corretamente");
        }
        OpcaoOperacaoCuradoria escolhaAluno = OpcaoOperacaoCuradoria.aPartirDoEstado(operacao);
        OpcaoOperacaoCuradoria escolhaCorreta = AvaliacaoEscolhaOperacaoRelacao
                .determinarOperacaoCorreta(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                        situacao, seletorConvertido);
        ResultadoEscolhaOperacaoModelagem registroOperacao = EscolhaOperacaoWeb.registrar(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, seletorConvertido, escolhaAluno, escolhaCorreta, escopo);
        boolean aceita = registroOperacao != null && registroOperacao.foiCorreta();
        if (seletorConvertido == TipoOperacaoSeletor.ENTRE_TRANSFORMACOES) {
            escolhaEntreTransformacoes = escolhaAluno;
        } else {
            escolhaEntreEstadoTransformacao = escolhaAluno;
        }

        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("action_id", registroOperacao != null
                ? registroOperacao.getActionId() : UUID.randomUUID().toString());
        resultado.put("aceita", Boolean.valueOf(aceita));
        resultado.put("diagnostico", aceita ? null : escolhaAluno.name());
        resultado.put("chave_mensagem", aceita || escopo.getTentativa().estaNoLimiteAjudaVisual() ? null : resolverExplicacao(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                seletorConvertido, escolhaCorreta));
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    public synchronized Map<String, Object> reiniciar() {
        gerard.dominio.campoaditivo.evento.PublicadorEventoDominio nenhum =
                gerard.dominio.campoaditivo.evento.PublicadorEventoDominio.NENHUM;
        estadoInicial = FabricaPapeisComposicaoDeTransformacoes.estadoInicial(nenhum);
        transformacao1 = FabricaPapeisComposicaoDeTransformacoes.transformacao1(nenhum);
        estadoIntermediario = FabricaPapeisComposicaoDeTransformacoes.estadoIntermediario(nenhum);
        transformacao2 = FabricaPapeisComposicaoDeTransformacoes.transformacao2(nenhum);
        transformacaoFinal = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(nenhum);
        estadoFinal = FabricaPapeisComposicaoDeTransformacoes.estadoFinal(nenhum);
        estadoInicialParte1 = FabricaPapeisComposicaoDeTransformacoes.estadoInicialParte1(nenhum);
        estadoInicialParte2 = FabricaPapeisComposicaoDeTransformacoes.estadoInicialParte2(nenhum);

        // Os 3 papéis de estado (inicial/intermediário/final) não são curados
        // hoje para esta categoria — a base tabular só preenche estado_inicial/
        // estado_intermediario/estado_final para o tipo legado
        // TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, nunca para
        // COMPOSICAO_TRANSFORMACOES (confirmado em situacoes_vergnaud.tsv:
        // todas as linhas com tipo COMPOSICAO_TRANSFORMACOES têm esses 3
        // campos vazios). AvaliacaoEscolhaOperacaoRelacao também não precisa
        // deles — julga a escolha do aluno só pelos campos curados
        // operacao_relacao/operacao_estado_transformacao. Por isso os 3
        // papéis de estado ficam "não conhecido" quando o campo está vazio,
        // em vez de lançar exceção.
        // Nada é pré-posicionado — protocolo de mouse é posicionar: o aluno
        // arrasta cada papel com valor curado do enunciado até o diagrama
        // (ver posicionarValorConhecido).

        ResolvedorIncognitaCurada.Resultado resolucao = new ResolvedorIncognitaCurada().resolver(situacao);
        papelDesconhecido = resolucao.possuiIncognita() && !resolucao.possuiConflito()
                ? papelPorChave(resolucao.getChaveEfetiva()) : null;
        incognitaEngatada = false;
        escolhaEntreTransformacoes = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        escolhaEntreEstadoTransformacao = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        papelAguardandoSinal = null;
        valorCuradoAguardandoSinal = null;
        escopo.incorporar(estadoInicial, transformacao1, estadoIntermediario, transformacao2, transformacaoFinal, estadoFinal, estadoInicialParte1, estadoInicialParte2);
        this.incognita = papelDesconhecido == null ? null
                : new IncognitaQuantitativa(papelDesconhecido.getChave(), situacao.getTipo(), papelDesconhecido);
        sinais = new SinalNumeroRelativoWeb(situacao);
        return estadoAtual();
    }

    private boolean respondeuCorretamente(OpcaoOperacaoCuradoria escolhaAluno,
            TipoOperacaoSeletor seletor) {
        if (escolhaAluno == null) {
            return false;
        }
        OpcaoOperacaoCuradoria escolhaCorreta = AvaliacaoEscolhaOperacaoRelacao
                .determinarOperacaoCorreta(
                        TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, situacao, seletor);
        return AvaliacaoEscolhaOperacaoRelacao.respondeuCorretamente(escolhaAluno, escolhaCorreta);
    }

    /**
     * Mesma guarda de SeletorOperacaoRelacaoAluno.ativar() no desktop: só
     * ativa quando a situação curada tem uma operação válida pra ESTE
     * seletor (getOperacaoRelacao()/getOperacaoEstadoTransformacao() resolve
     * para SOMA ou SUBTRACAO). Cada uma das duas etapas tem sua própria
     * curadoria independente -- auditoria de acoplamento Main/web, 2026-09-19.
     */
    private boolean seletorAtivo(TipoOperacaoSeletor seletor) {
        return AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, situacao, seletor)
                != OpcaoOperacaoCuradoria.NAO_SELECIONADO;
    }

    /** Ver comentário de segunda_etapa_habilitada em estadoAtual(). */
    private boolean segundaEtapaHabilitada() {
        boolean primeiraAtiva = seletorAtivo(TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        return !primeiraAtiva || respondeuCorretamente(
                escolhaEntreTransformacoes, TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
    }

    private static TipoOperacaoSeletor converterSeletor(String seletor) {
        if ("ENTRE_TRANSFORMACOES".equals(seletor)) {
            return TipoOperacaoSeletor.ENTRE_TRANSFORMACOES;
        }
        if ("ENTRE_ESTADO_E_TRANSFORMACAO".equals(seletor)) {
            return TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO;
        }
        throw new IllegalArgumentException("seletor de operação desconhecido: " + seletor);
    }

    /**
     * Resolve a chave i18n da explicação para texto final, com os
     * personagens curados já substituídos — mesma composição de chamadas de
     * SeletorOperacaoRelacaoAluno.ativar() no desktop. O contrato web nunca
     * expõe chaves i18n cruas ao cliente (ver gerard-api-semantica).
     */
    private String resolverExplicacao(TipoSituacaoAditiva tipo,
            TipoOperacaoSeletor papel, OpcaoOperacaoCuradoria operacao) {
        return RealizadorTextoExplicacaoOperacaoRelacao.realizar(
                tipo, papel, operacao, situacao,
                gerard.i18n.ServicoLocalizacao.getInstancia());
    }

    private static String nomeOuNull(OpcaoOperacaoCuradoria opcao) {
        return opcao == null || opcao == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                ? null : opcao.name();
    }

    private PapelQuantitativo[] todosOsPapeis() {
        return new PapelQuantitativo[] {estadoInicial, transformacao1, estadoIntermediario,
                transformacao2, transformacaoFinal, estadoFinal,
                estadoInicialParte1, estadoInicialParte2};
    }

    private String valorCuradoDoPapel(PapelQuantitativo papel) {
        if (papel == estadoInicial) return situacao.getEstadoInicial();
        if (papel == transformacao1) return situacao.getQuantidade1();
        if (papel == estadoIntermediario) return situacao.getEstadoIntermediario();
        if (papel == transformacao2) return situacao.getQuantidade2();
        if (papel == transformacaoFinal) return situacao.getResultado();
        if (papel == estadoFinal) return situacao.getEstadoFinal();
        if (papel == estadoInicialParte1) return situacao.getEstadoInicialParte1();
        if (papel == estadoInicialParte2) return situacao.getEstadoInicialParte2();
        throw new IllegalStateException("papel incompatível com Composição de Transformações");
    }

    private boolean ehNatural(PapelQuantitativo papel) {
        return papel == estadoInicial || papel == estadoIntermediario || papel == estadoFinal
                || papel == estadoInicialParte1 || papel == estadoInicialParte2;
    }

    private boolean temValorCurado(PapelQuantitativo papel) {
        String valor = valorCuradoDoPapel(papel);
        return valor != null && !valor.trim().isEmpty();
    }

    private boolean todosOsConhecidosPreenchidos() {
        for (PapelQuantitativo papel : todosOsPapeis()) {
            if (ehConhecido(papel) && !papel.estaPreenchido()) {
                return false;
            }
        }
        return true;
    }

    private PapelQuantitativo papelPorChave(String chave) {
        for (PapelQuantitativo papel : todosOsPapeis()) {
            if (papel.getChave().equals(chave)) {
                return papel;
            }
        }
        throw new IllegalStateException(
                "papel incompatível com Composição de Transformações: " + chave);
    }

    /**
     * Posiciona um papel conhecido (só os que têm valor curado — os 3 de
     * estado tipicamente não têm nesta categoria, ver reiniciar) com esse
     * valor — ação que soltar um elemento do enunciado sobre sua caixa
     * dispara (protocolo de mouse é posicionar).
     */
    public synchronized Map<String, Object> posicionarValorConhecido(String papelId, String origemPapelId) {
        PapelQuantitativo papel = papelPorChave(papelId);
        if (papel == papelDesconhecido) {
            throw new IllegalArgumentException(
                    "papel é a incógnita desta situação, use ENGATAR_INCOGNITA/PROPOR_VALOR_PAPEL: " + papelId);
        }
        if (!temValorCurado(papel)) {
            throw new IllegalArgumentException(
                    "papel sem valor curado para posicionar: " + papelId);
        }
        ResultadoQuestionamentoPosicionamento questionamento =
                avaliadorOrigemDestino.avaliar(origemPapelId, papel.getChave(), situacao.getTipo(),
                        participantes(), escopo);
        if (questionamento.isAplicavel() && !questionamento.isCorreto()) {
            Map<String, Object> rejeitado = mapa();
            rejeitado.put("schema", SCHEMA_RESULTADO);
            rejeitado.put("aceita", Boolean.FALSE);
            rejeitado.put("chave_mensagem", questionamento.getMensagem());
            rejeitado.put("estado", estadoAtual());
            return rejeitado;
        }
        if (!papel.estaPreenchido() && papel != papelAguardandoSinal) {
            posicionarConhecido(papel);
        }
        completarDerivadosPorConsistencia();
        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("chave_mensagem", null);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    /** Engata o "?" na caixa da incógnita (protocolo mouse-texto, Main.java). */
    public synchronized Map<String, Object> engatarIncognita(String papelId, String origemPapelId) {
        if (papelDesconhecido == null || !papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException(
                    "papel não é a incógnita curada desta situação: " + papelId);
        }
        ResultadoQuestionamentoPosicionamento questionamento =
                avaliadorOrigemDestino.avaliar(origemPapelId, papelDesconhecido.getChave(), situacao.getTipo(),
                        participantes(), escopo);
        if (questionamento.isAplicavel() && !questionamento.isCorreto()) {
            Map<String, Object> rejeitado = mapa();
            rejeitado.put("schema", SCHEMA_RESULTADO);
            rejeitado.put("aceita", Boolean.FALSE);
            rejeitado.put("chave_mensagem", questionamento.getMensagem());
            rejeitado.put("estado", estadoAtual());
            return rejeitado;
        }
        if (!papelDesconhecido.estaPreenchido()) {
            incognitaEngatada = true;
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("chave_mensagem", null);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    /** Resposta Sim/Não à confirmação do valor rejeitado da incógnita. */
    public synchronized Map<String, Object> responderConfirmacaoValor(
            String papelId, boolean confirmou, int valor) {
        if (papelDesconhecido == null || !papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException(
                    "papel não é a incógnita desta situação: " + papelId);
        }
        Map<String, Object> resultado = RespostaConfirmacaoValorWeb.responder(
                incognita, confirmou, new NumeroInteiro(valor), valorEsperado(),
                ContextosAcaoInstrumentalWeb.respostaConfirmacao(
                        papelId, confirmou, valor, participantes()),
                escopo);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    public synchronized Map<String, Object> proporValor(String papelId, int valor) {
        if (papelDesconhecido == null || !papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException(
                    "papel não é a incógnita desta situação: " + papelId);
        }
        NumeroInteiro proposta = new NumeroInteiro(valor);
        ValorIncognitaWeb.Resultado avaliacao = ValorIncognitaWeb.propor(
                incognita, proposta, valorEsperado(),
                ContextosAcaoInstrumentalWeb.valorIncognita(papelId, valor, participantes()),
                escopo);
        if (avaliacao.isAceita()) {
            ContextoAcao contexto = new ContextoAcao(
                    "sessao.web.local", "usuario.web.local", tentativaId,
                    situacao.getId(), "diagrama.vergnaud.web");
            papelDesconhecido.posicionar(
                    ehNatural(papelDesconhecido) ? new NumeroNatural(valor) : proposta,
                    OrigemAcao.ORIGEM_USUARIO, contexto);
        }
        Map<String, Object> resultado = avaliacao.getProjecao();
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    /**
     * Valor que a incógnita deve receber: a relação estrutural das transformações quando a
     * incógnita é uma delas e as outras duas estão posicionadas; senão, o valor curado do próprio
     * papel (ex.: o estado final de uma situação com estado inicial decomposto).
     */
    private ValorNumerico valorEsperado() {
        if (papelDesconhecido == transformacao1 || papelDesconhecido == transformacao2
                || papelDesconhecido == transformacaoFinal) {
            gerard.dominio.campoaditivo.ResultadoCalculo calculo =
                    relacaoDiagnostico.calcularValorAusente(transformacao1, transformacao2, transformacaoFinal);
            if (calculo.temValorCalculavel()) {
                return calculo.getValorCalculado();
            }
        }
        String curado = valorCuradoDoPapel(papelDesconhecido);
        try {
            int valor = Integer.parseInt(curado == null ? "" : curado.trim());
            return ehNatural(papelDesconhecido) ? new NumeroNatural(valor) : new NumeroInteiro(valor);
        } catch (NumberFormatException invalido) {
            return null;
        }
    }

    /**
     * Informa quais papéis têm elemento arrastável no enunciado. Papéis exigidos pela modelagem
     * que NÃO têm elemento (ex.: o estado inicial de uma situação com estado inicial decomposto,
     * ou a transformação resultante quando o enunciado só traz as duas transformações) não podem
     * ser posicionados pelo participante: como no desktop (consistência entre representações),
     * recebem o valor derivado dos componentes quando estes são posicionados.
     */
    public synchronized void definirPapeisComElementoNoEnunciado(java.util.Set<String> chaves) {
        this.papeisComElementoNoEnunciado = chaves;
    }

    private boolean ehDerivado(PapelQuantitativo papel) {
        return papeisComElementoNoEnunciado != null
                && !papeisComElementoNoEnunciado.contains(papel.getChave());
    }

    /** Preenche, a partir dos componentes já posicionados, os papéis derivados (sem elemento no enunciado). */
    private void completarDerivadosPorConsistencia() {
        ContextoAcao contexto = new ContextoAcao("sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        if (transformacaoFinal != papelDesconhecido && !transformacaoFinal.estaPreenchido()
                && temValorCurado(transformacaoFinal) && ehDerivado(transformacaoFinal)
                && transformacao1.estaPreenchido() && transformacao2.estaPreenchido()) {
            int soma = transformacao1.valorAtual().valorOuNull() + transformacao2.valorAtual().valorOuNull();
            transformacaoFinal.posicionar(new NumeroInteiro(soma), OrigemAcao.ORIGEM_SISTEMA, contexto);
        }
        if (estadoInicial != papelDesconhecido && !estadoInicial.estaPreenchido()
                && temValorCurado(estadoInicial) && ehDerivado(estadoInicial)
                && estadoInicialParte1.estaPreenchido() && estadoInicialParte2.estaPreenchido()) {
            int soma = estadoInicialParte1.valorAtual().valorOuNull() + estadoInicialParte2.valorAtual().valorOuNull();
            estadoInicial.posicionar(new NumeroNatural(soma), OrigemAcao.ORIGEM_SISTEMA, contexto);
        }
    }

    /**
     * Dado a posicionar = tem valor curado, não é a incógnita (esta nunca é pré-posicionada) e a
     * modelagem o exige (mesma função de domínio que o desktop usa para saber quando conclui:
     * papéis derivados, sem elemento próprio no enunciado, como o estado inicial de uma situação
     * com estado inicial decomposto, não são arrastáveis e por isso não bloqueiam a modelagem).
     */
    private boolean ehConhecido(PapelQuantitativo papel) {
        return temValorCurado(papel) && papel != papelDesconhecido && !ehDerivado(papel)
                && gerard.campoaditivo.curadoria.SemanticaCuradaSituacao.papelExigidoNaModelagem(
                        situacao, ServicoLocalizacao.getInstancia(), papel.getChave());
    }

    /**
     * Escolhe explicitamente o sinal do papel revelado por
     * posicionarValorConhecido que precisa de representação de sinal —
     * mesmo protocolo/padrão de
     * ServicoAtividadeWebComparacaoMedidas.escolherSinalNumeroRelativo.
     */
    public synchronized Map<String, Object> escolherSinalNumeroRelativo(String papelId, String sinal) {
        if (papelAguardandoSinal == null || !papelAguardandoSinal.getChave().equals(papelId)) {
            throw new IllegalStateException(
                    "nenhum papel aguardando escolha de sinal com esta chave: " + papelId);
        }
        if (!"+".equals(sinal) && !"-".equals(sinal)) {
            throw new IllegalArgumentException("sinal precisa ser \"+\" ou \"-\": " + sinal);
        }
        PapelQuantitativo papel = papelAguardandoSinal;
        int base = Math.abs(valorCuradoAguardandoSinal.intValue());
        int valorEscolhido = "-".equals(sinal) ? -base : base;
        gerard.dominio.campoaditivo.ContextoAcao contexto = new gerard.dominio.campoaditivo.ContextoAcao(
                "sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        Optional<DiagnosticoErroPapel> diagnostico = papel.posicionar(
                new NumeroInteiro(valorEscolhido),
                gerard.dominio.campoaditivo.OrigemAcao.ORIGEM_USUARIO, contexto);
        boolean sinalDivergeDoCurado = sinais.avaliarDivergencia(
                papel.getChave(), sinal, base, participantes(), escopo);
        papelAguardandoSinal = null;
        valorCuradoAguardandoSinal = null;
        completarDerivadosPorConsistencia();
        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.valueOf(!diagnostico.isPresent()));
        resultado.put("mensagem_sinal_divergente", sinalDivergeDoCurado
                ? ServicoLocalizacao.getInstancia().formatar("ui.tooltip.relativeSign.confirm", sinal)
                : null);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    private void posicionarConhecido(PapelQuantitativo papel) {
        String limpo = valorCuradoDoPapel(papel).trim();
        int valor;
        try {
            valor = Integer.parseInt(limpo);
        } catch (NumberFormatException invalido) {
            throw new IllegalStateException(
                    "Valor curado inválido para " + papel.getChave() + ": " + limpo, invalido);
        }
        if (!ehNatural(papel)
                && CatalogoNecessidadeRepresentacaoDeSinal.necessitaRepresentacaoDeSinal(papel.getChave())) {
            // Mesmo protocolo do desktop: a magnitude é revelada, mas o
            // sinal só é aplicado quando o estudante escolhe explicitamente
            // (ver escolherSinalNumeroRelativo) — nunca de imediato aqui.
            papelAguardandoSinal = papel;
            valorCuradoAguardandoSinal = Integer.valueOf(valor);
            return;
        }
        gerard.dominio.campoaditivo.ContextoAcao contexto = new gerard.dominio.campoaditivo.ContextoAcao(
                "sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        papel.posicionar(ehNatural(papel) ? new NumeroNatural(valor) : new NumeroInteiro(valor),
                gerard.dominio.campoaditivo.OrigemAcao.ORIGEM_USUARIO, contexto);
    }

    private Map<String, Object> projetarPapel(PapelQuantitativo papel) {
        Map<String, Object> item = mapa();
        item.put("id", papel.getChave());
        item.put("nome", papel.getNomeConceitual());
        item.put("conhecido", Boolean.valueOf(papel.estaPreenchido()));
        item.put("valor", papel.estaPreenchido()
                ? papel.valorAtual().valorOuNull() : null);
        item.put("engatada", Boolean.valueOf(
                papel == papelDesconhecido && incognitaEngatada && !papel.estaPreenchido()));
        return item;
    }

    private static Map<String, Object> mapa() {
        return new LinkedHashMap<String, Object>();
    }

    private List<String> participantes() {
        return Arrays.asList(estadoInicial.getChave(), transformacao1.getChave(), estadoIntermediario.getChave(), transformacao2.getChave(), transformacaoFinal.getChave(), estadoFinal.getChave(), estadoInicialParte1.getChave(), estadoInicialParte2.getChave());
    }

}
