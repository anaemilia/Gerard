package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
 * Não implementa {@link ServicoAtividadeWeb}: essa interface modela "propor
 * um valor para um papel incógnito", paradigma diferente do usado aqui, onde
 * não há incógnita — a interação é escolher uma operação, avaliada por
 * {@link AvaliacaoEscolhaOperacaoRelacao} (extraída de
 * {@code gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno} em 2026-09-03).
 */
public final class ServicoAtividadeWebComposicaoTransformacoes
        implements ServicoAtividadeWebEscolhaOperacao {
    public static final String SCHEMA_ESTADO = ServicoAtividadeWebComposicao.SCHEMA_ESTADO;
    public static final String SCHEMA_RESULTADO = ServicoAtividadeWebComposicao.SCHEMA_RESULTADO;

    private final String tentativaId;
    private final SituacaoProblemaAditiva situacao;
    private PapelQuantitativo estadoInicial;
    private PapelQuantitativo transformacao1;
    private PapelQuantitativo estadoIntermediario;
    private PapelQuantitativo transformacao2;
    private PapelQuantitativo transformacaoFinal;
    private PapelQuantitativo estadoFinal;
    private OpcaoOperacaoCuradoria escolhaEntreTransformacoes;
    private OpcaoOperacaoCuradoria escolhaEntreEstadoTransformacao;

    public ServicoAtividadeWebComposicaoTransformacoes(String tentativaId,
            SituacaoProblemaAditiva situacao) {
        if (situacao == null
                || situacao.getTipo() != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            throw new IllegalArgumentException(
                    "situação de Composição de Transformações é obrigatória");
        }
        this.tentativaId = tentativaId;
        this.situacao = situacao;
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
        boolean primeiraCorreta = respondeuCorretamente(escolhaEntreTransformacoes,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        boolean segundaCorreta = respondeuCorretamente(escolhaEntreEstadoTransformacao,
                TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO);
        estado.put("escolha_entre_transformacoes", nomeOuNull(escolhaEntreTransformacoes));
        estado.put("escolha_entre_estado_transformacao",
                nomeOuNull(escolhaEntreEstadoTransformacao));
        estado.put("correta_entre_transformacoes",
                escolhaEntreTransformacoes == null
                        || escolhaEntreTransformacoes == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                                ? null : Boolean.valueOf(primeiraCorreta));
        estado.put("correta_entre_estado_transformacao",
                escolhaEntreEstadoTransformacao == null
                        || escolhaEntreEstadoTransformacao == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                                ? null : Boolean.valueOf(segundaCorreta));
        estado.put("segunda_etapa_habilitada", Boolean.valueOf(primeiraCorreta));
        boolean concluida = primeiraCorreta && segundaCorreta;
        estado.put("concluida", Boolean.valueOf(concluida));
        // Protocolo de mouse é posicionar (ver ServicoAtividadeWebComposicao):
        // os papéis com valor curado não vêm pré-preenchidos; escolher a
        // operação só libera depois deles estarem posicionados. Papéis sem
        // valor curado (tipicamente os 3 de estado nesta categoria) nunca
        // bloqueiam — não têm o que posicionar.
        boolean papeisConhecidosProntos = todosOsConhecidosPreenchidos();
        List<Object> acoes = AcoesDisponiveisAtividadeWeb.escolhaOperacaoRelacao(
                primeiraCorreta, concluida || !papeisConhecidosProntos);
        if (!papeisConhecidosProntos) {
            for (PapelQuantitativo papel : todosOsPapeis()) {
                if (temValorCurado(papel) && !papel.estaPreenchido()) {
                    acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoPosicionarConhecido(papel.getChave()));
                }
            }
        }
        estado.put("acoes_disponiveis", acoes);
        return estado;
    }

    public synchronized Map<String, Object> escolherOperacao(String seletor, String operacao) {
        TipoOperacaoSeletor seletorConvertido = converterSeletor(seletor);
        boolean primeiraCorreta = respondeuCorretamente(escolhaEntreTransformacoes,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        if (seletorConvertido == TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO
                && !primeiraCorreta) {
            throw new IllegalStateException(
                    "a segunda etapa só libera depois que a primeira é respondida corretamente");
        }
        OpcaoOperacaoCuradoria escolhaAluno = OpcaoOperacaoCuradoria.aPartirDoEstado(operacao);
        OpcaoOperacaoCuradoria escolhaCorreta = AvaliacaoEscolhaOperacaoRelacao
                .determinarOperacaoCorreta(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                        situacao, seletorConvertido);
        boolean aceita = AvaliacaoEscolhaOperacaoRelacao
                .respondeuCorretamente(escolhaAluno, escolhaCorreta);
        if (seletorConvertido == TipoOperacaoSeletor.ENTRE_TRANSFORMACOES) {
            escolhaEntreTransformacoes = escolhaAluno;
        } else {
            escolhaEntreEstadoTransformacao = escolhaAluno;
        }

        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("action_id", UUID.randomUUID().toString());
        resultado.put("aceita", Boolean.valueOf(aceita));
        resultado.put("diagnostico", aceita ? null : escolhaAluno.name());
        resultado.put("chave_mensagem", aceita ? null : resolverExplicacao(
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

        escolhaEntreTransformacoes = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        escolhaEntreEstadoTransformacao = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
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
        String chave = AvaliacaoEscolhaOperacaoRelacao.chaveExplicacao(tipo, papel, operacao);
        if (chave == null) {
            return null;
        }
        String modelo = gerard.i18n.ServicoLocalizacao.getInstancia().texto(chave);
        return AvaliacaoEscolhaOperacaoRelacao.preencherPersonagensCurados(modelo, situacao);
    }

    private static String nomeOuNull(OpcaoOperacaoCuradoria opcao) {
        return opcao == null || opcao == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                ? null : opcao.name();
    }

    private PapelQuantitativo[] todosOsPapeis() {
        return new PapelQuantitativo[] {estadoInicial, transformacao1, estadoIntermediario,
                transformacao2, transformacaoFinal, estadoFinal};
    }

    private String valorCuradoDoPapel(PapelQuantitativo papel) {
        if (papel == estadoInicial) return situacao.getEstadoInicial();
        if (papel == transformacao1) return situacao.getQuantidade1();
        if (papel == estadoIntermediario) return situacao.getEstadoIntermediario();
        if (papel == transformacao2) return situacao.getQuantidade2();
        if (papel == transformacaoFinal) return situacao.getResultado();
        if (papel == estadoFinal) return situacao.getEstadoFinal();
        throw new IllegalStateException("papel incompatível com Composição de Transformações");
    }

    private boolean ehNatural(PapelQuantitativo papel) {
        return papel == estadoInicial || papel == estadoIntermediario || papel == estadoFinal;
    }

    private boolean temValorCurado(PapelQuantitativo papel) {
        String valor = valorCuradoDoPapel(papel);
        return valor != null && !valor.trim().isEmpty();
    }

    private boolean todosOsConhecidosPreenchidos() {
        for (PapelQuantitativo papel : todosOsPapeis()) {
            if (temValorCurado(papel) && !papel.estaPreenchido()) {
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
        if (!temValorCurado(papel)) {
            throw new IllegalArgumentException(
                    "papel sem valor curado para posicionar: " + papelId);
        }
        gerard.Scaffolding.questionamento.ResultadoQuestionamento questionamento =
                AvaliadorOrigemDestinoWeb.avaliar(origemPapelId, papel.getChave(), situacao.getTipo());
        if (questionamento.isAplicavel() && !questionamento.isCorreto()) {
            Map<String, Object> rejeitado = mapa();
            rejeitado.put("schema", SCHEMA_RESULTADO);
            rejeitado.put("aceita", Boolean.FALSE);
            rejeitado.put("chave_mensagem", questionamento.getMensagem());
            rejeitado.put("estado", estadoAtual());
            return rejeitado;
        }
        if (!papel.estaPreenchido()) {
            String limpo = valorCuradoDoPapel(papel).trim();
            int numero;
            try {
                numero = Integer.parseInt(limpo);
            } catch (NumberFormatException invalido) {
                throw new IllegalStateException(
                        "Valor curado inválido para " + papel.getChave() + ": " + limpo, invalido);
            }
            gerard.dominio.campoaditivo.ContextoAcao contexto = new gerard.dominio.campoaditivo.ContextoAcao(
                    "sessao.web.local", "usuario.web.local", tentativaId,
                    situacao.getId(), "diagrama.vergnaud.web");
            papel.posicionar(ehNatural(papel) ? new NumeroNatural(numero) : new NumeroInteiro(numero),
                    gerard.dominio.campoaditivo.OrigemAcao.ORIGEM_USUARIO, contexto);
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("chave_mensagem", null);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    private static Map<String, Object> projetarPapel(PapelQuantitativo papel) {
        Map<String, Object> item = mapa();
        item.put("id", papel.getChave());
        item.put("nome", papel.getNomeConceitual());
        item.put("conhecido", Boolean.valueOf(papel.estaPreenchido()));
        item.put("valor", papel.estaPreenchido()
                ? papel.valorAtual().valorOuNull() : null);
        // Sem incógnita nesta categoria (ver Javadoc da classe) — nunca
        // engatada, mas o campo precisa existir pra extrairCampo em
        // ServicoSorteioAtividadeWeb.projetarCena não depender de qual
        // categoria está projetando.
        item.put("engatada", Boolean.FALSE);
        return item;
    }

    private static Map<String, Object> mapa() {
        return new LinkedHashMap<String, Object>();
    }
}
