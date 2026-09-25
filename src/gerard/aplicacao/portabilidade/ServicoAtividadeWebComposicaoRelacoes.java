package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.CatalogoNecessidadeRepresentacaoDeSinal;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.numero.NumeroInteiro;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fachada de aplicação portátil para Composição de Relações na web, mesmo
 * paradigma de {@link ServicoAtividadeWebComposicaoTransformacoes}: os 3
 * papéis aparecem revelados desde o início e o aluno escolhe a operação
 * certa (soma/subtração), avaliada por {@link AvaliacaoEscolhaOperacaoRelacao}.
 * Mais simples que Composição de Transformações — uma única relação, sem
 * segunda etapa — por isso sempre usa
 * {@link TipoOperacaoSeletor#ENTRE_TRANSFORMACOES}.
 */
public final class ServicoAtividadeWebComposicaoRelacoes
        implements ServicoAtividadeWeb, ServicoAtividadeWebEscolhaOperacao, ServicoAtividadeWebComSinal {
    public static final String SCHEMA_ESTADO = ServicoAtividadeWebComposicao.SCHEMA_ESTADO;
    public static final String SCHEMA_RESULTADO = ServicoAtividadeWebComposicao.SCHEMA_RESULTADO;

    private final String tentativaId;
    private final SituacaoProblemaAditiva situacao;
    private final RelacaoEstruturalComposicaoDeRelacoes relacaoDiagnostico =
            RelacaoEstruturalComposicaoDeRelacoes.composicaoDeRelacoes();
    private PapelQuantitativo relacao1;
    private PapelQuantitativo relacao2;
    private PapelQuantitativo relacaoFinal;
    private OpcaoOperacaoCuradoria escolha;
    // A incógnita é arrastável em toda situação curada com termo_desconhecido
    // resolvível (mesmo invariante de ServicoAtividadeWebComparacaoMedidas) —
    // vale também para categorias de escolha de operação: os dois mecanismos
    // coexistem sem se excluir. null quando a situação não tem incógnita
    // curada nesta categoria (nem toda situação de Composição de Relações
    // precisa ter uma).
    private PapelQuantitativo papelDesconhecido;
    private boolean incognitaEngatada;
    // Papel conhecido que precisa de representação de sinal (ver
    // ServicoAtividadeWebComSinal) já revelado (arrastado) mas ainda sem
    // sinal escolhido — mesmo protocolo do desktop e mesmo padrão já usado
    // por ServicoAtividadeWebComparacaoMedidas/TransformacaoMedidas.
    private PapelQuantitativo papelAguardandoSinal;
    private Integer valorCuradoAguardandoSinal;

    public ServicoAtividadeWebComposicaoRelacoes(String tentativaId,
            SituacaoProblemaAditiva situacao) {
        if (situacao == null
                || situacao.getTipo() != TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            throw new IllegalArgumentException(
                    "situação de Composição de Relações é obrigatória");
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
        estado.put("categoria", "COMPOSICAO_RELACOES");
        estado.put("enunciado", new gerard.campoaditivo.curadoria.MaterializadorEnunciadoCurado()
                .materializar(situacao));
        estado.put("relacao_1", projetarPapel(relacao1));
        estado.put("relacao_2", projetarPapel(relacao2));
        estado.put("relacao_final", projetarPapel(relacaoFinal));
        // Mesma guarda de SeletorOperacaoRelacaoAluno.ativar() no desktop:
        // "só fica ativo quando a situação curada tem uma operação válida
        // (getOperacaoRelacao() resolve para SOMA ou SUBTRACAO) -- situações
        // antigas, sem esse campo preenchido, não mostram o seletor (nada a
        // avaliar)". Sem isso, o web oferecia e avaliava a escolha mesmo
        // quando não havia resposta curada nenhuma pra comparar (auditoria
        // de acoplamento Main/web, 2026-09-19).
        boolean seletorAtivo = AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                TipoSituacaoAditiva.COMPOSICAO_RELACOES, situacao,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES) != OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        boolean correta = seletorAtivo && respondeuCorretamente(escolha);
        estado.put("escolha_operacao", nomeOuNull(escolha));
        estado.put("correta", !seletorAtivo || escolha == null
                || escolha == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                ? null : Boolean.valueOf(correta));
        estado.put("concluida", Boolean.valueOf(correta));
        // Protocolo de mouse é posicionar (ver ServicoAtividadeWebComposicao):
        // relação 1 e relação 2 não vêm pré-preenchidas; escolher a operação
        // só libera depois delas estarem posicionadas. A incógnita é
        // arrastável em toda categoria, nas duas versões (desktop e web) —
        // isso não depende de a categoria também ter seletor de operação: os
        // dois mecanismos coexistem sem se excluir (ver papelDesconhecido).
        boolean papeisConhecidosProntos = todosOsConhecidosPreenchidos();
        estado.put("papel_aguardando_sinal",
                papelAguardandoSinal == null ? null : papelAguardandoSinal.getChave());
        List<Object> acoes = AcoesDisponiveisAtividadeWeb.sorteios();
        acoes.add(AcoesDisponiveisAtividadeWeb.acaoReiniciar());
        if (papelAguardandoSinal != null) {
            // Enquanto o sinal não é escolhido, nenhuma outra ação de
            // posicionamento fica disponível para este papel — mesmo
            // protocolo do desktop e do padrão já usado em
            // ServicoAtividadeWebComparacaoMedidas/TransformacaoMedidas.
            acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEscolherSinal(papelAguardandoSinal.getChave()));
        } else if (!papeisConhecidosProntos) {
            for (PapelQuantitativo papel : papeisPosicionaveis()) {
                if (!papel.estaPreenchido()) {
                    acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoPosicionarConhecido(papel.getChave()));
                }
            }
        } else {
            if (seletorAtivo && !correta) {
                acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEscolherOperacaoRelacao());
            }
            if (papelDesconhecido != null && !papelDesconhecido.estaPreenchido()) {
                acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEngatarIncognita(papelDesconhecido.getChave()));
            }
        }
        estado.put("acoes_disponiveis", acoes);
        return estado;
    }

    /** Engata o "?" na caixa da incógnita (protocolo mouse-texto, Main.java). */
    public synchronized Map<String, Object> engatarIncognita(String papelId, String origemPapelId) {
        if (papelDesconhecido == null || !papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException(
                    "papel não é a incógnita curada desta situação: " + papelId);
        }
        gerard.Scaffolding.questionamento.ResultadoQuestionamento questionamento =
                AvaliadorOrigemDestinoWeb.avaliar(origemPapelId, papelDesconhecido.getChave(), situacao.getTipo());
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

    public synchronized Map<String, Object> proporValor(String papelId, int valor) {
        if (papelDesconhecido == null || !papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException(
                    "papel não é a incógnita curada desta situação: " + papelId);
        }
        NumeroInteiro proposta = new NumeroInteiro(valor);
        ContextoAcao contexto = new ContextoAcao(
                "sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        IdentidadeAcaoInstrumentalPapel identidade = papelDesconhecido
                .iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO);
        Optional<DiagnosticoErroPapel> diagnostico = relacaoDiagnostico
                .diagnosticarValorProposto(relacao1, relacao2, relacaoFinal, papelDesconhecido, proposta);
        ResultadoRegistroTentativaPapel registro = papelDesconhecido
                .registrarTentativaComIdentidade(identidade, diagnostico, contexto, proposta);
        if (!diagnostico.isPresent()) {
            papelDesconhecido.posicionar(proposta, OrigemAcao.ORIGEM_USUARIO, contexto);
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("action_id", registro.getActionId());
        resultado.put("aceita", Boolean.valueOf(!diagnostico.isPresent()));
        resultado.put("diagnostico", diagnostico.isPresent() ? diagnostico.get().getTipo().name() : null);
        resultado.put("chave_mensagem", diagnostico.isPresent()
                ? MensagemFeedbackIncognitaWeb.resolver(papelDesconhecido, registro) : null);
        resultado.put("limite_atingido", Boolean.valueOf(
                diagnostico.isPresent() && MensagemFeedbackIncognitaWeb.limiteAtingido(registro)));
        resultado.put("rejeicoes_consecutivas", Integer.valueOf(registro.getRejeicoesConsecutivas()));
        resultado.put("estado", estadoAtual());
        return resultado;
    }


    public synchronized Map<String, Object> escolherOperacao(String seletor, String operacao) {
        OpcaoOperacaoCuradoria escolhaAluno = OpcaoOperacaoCuradoria.aPartirDoEstado(operacao);
        OpcaoOperacaoCuradoria escolhaCorreta = AvaliacaoEscolhaOperacaoRelacao
                .determinarOperacaoCorreta(TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                        situacao, TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        boolean aceita = AvaliacaoEscolhaOperacaoRelacao
                .respondeuCorretamente(escolhaAluno, escolhaCorreta);
        escolha = escolhaAluno;

        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("action_id", UUID.randomUUID().toString());
        resultado.put("aceita", Boolean.valueOf(aceita));
        resultado.put("diagnostico", aceita ? null : escolhaAluno.name());
        resultado.put("chave_mensagem", aceita ? null : resolverExplicacao(
                TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES, escolhaCorreta));
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    public synchronized Map<String, Object> reiniciar() {
        PublicadorEventoDominio nenhum = PublicadorEventoDominio.NENHUM;
        relacao1 = FabricaPapeisComposicaoDeRelacoes.relacao1(nenhum);
        relacao2 = FabricaPapeisComposicaoDeRelacoes.relacao2(nenhum);
        relacaoFinal = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(nenhum);

        // Nada é pré-posicionado — protocolo de mouse é posicionar: o aluno
        // arrasta cada papel do enunciado até o diagrama (ver
        // posicionarValorConhecido).

        ResolvedorIncognitaCurada.Resultado incognita =
                new ResolvedorIncognitaCurada().resolver(situacao);
        papelDesconhecido = incognita.possuiIncognita() && !incognita.possuiConflito()
                ? papelPorChave(incognita.getChaveEfetiva()) : null;
        incognitaEngatada = false;

        escolha = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        papelAguardandoSinal = null;
        valorCuradoAguardandoSinal = null;
        return estadoAtual();
    }

    private boolean respondeuCorretamente(OpcaoOperacaoCuradoria escolhaAluno) {
        if (escolhaAluno == null) {
            return false;
        }
        OpcaoOperacaoCuradoria escolhaCorreta = AvaliacaoEscolhaOperacaoRelacao
                .determinarOperacaoCorreta(TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                        situacao, TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        return AvaliacaoEscolhaOperacaoRelacao.respondeuCorretamente(escolhaAluno, escolhaCorreta);
    }

    private PapelQuantitativo[] todosOsPapeis() {
        return new PapelQuantitativo[] {relacao1, relacao2, relacaoFinal};
    }

    /**
     * Todos os papéis exceto a incógnita curada (se houver) — mesmo padrão
     * de ServicoAtividadeWebComparacaoMedidas.todosOsConhecidosPreenchidos:
     * qualquer um dos três pode ser a incógnita, dependendo da situação
     * curada, então a exclusão é por identidade de papelDesconhecido, nunca
     * por posição fixa.
     */
    private PapelQuantitativo[] papeisPosicionaveis() {
        List<PapelQuantitativo> posicionaveis = new java.util.ArrayList<PapelQuantitativo>();
        for (PapelQuantitativo papel : todosOsPapeis()) {
            if (papel != papelDesconhecido) {
                posicionaveis.add(papel);
            }
        }
        return posicionaveis.toArray(new PapelQuantitativo[0]);
    }

    private boolean todosOsConhecidosPreenchidos() {
        for (PapelQuantitativo papel : papeisPosicionaveis()) {
            if (!papel.estaPreenchido()) {
                return false;
            }
        }
        return true;
    }

    private String valorCuradoDoPapel(PapelQuantitativo papel) {
        if (papel == relacao1) return situacao.getQuantidade1();
        if (papel == relacao2) return situacao.getQuantidade2();
        if (papel == relacaoFinal) return situacao.getResultado();
        throw new IllegalStateException("papel incompatível com Composição de Relações");
    }

    private PapelQuantitativo papelPorChave(String chave) {
        for (PapelQuantitativo papel : todosOsPapeis()) {
            if (papel.getChave().equals(chave)) {
                return papel;
            }
        }
        throw new IllegalStateException(
                "papel incompatível com Composição de Relações: " + chave);
    }

    /**
     * Posiciona um papel conhecido com o valor curado — ação que soltar um
     * elemento do enunciado sobre sua caixa dispara (protocolo de mouse é
     * posicionar).
     */
    public synchronized Map<String, Object> posicionarValorConhecido(String papelId, String origemPapelId) {
        PapelQuantitativo papel = papelPorChave(papelId);
        if (papel == papelDesconhecido) {
            throw new IllegalArgumentException(
                    "papel é a incógnita desta situação, use ENGATAR_INCOGNITA/PROPOR_VALOR_PAPEL: " + papelId);
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
        if (!papel.estaPreenchido() && papel != papelAguardandoSinal) {
            posicionarConhecido(papel);
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("chave_mensagem", null);
        resultado.put("estado", estadoAtual());
        return resultado;
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
        ContextoAcao contexto = new ContextoAcao(
                "sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        Optional<DiagnosticoErroPapel> diagnostico = papel.posicionar(
                new NumeroInteiro(valorEscolhido), OrigemAcao.ORIGEM_USUARIO, contexto);
        boolean sinalDivergeDoCurado = Integer.signum(valorEscolhido)
                != Integer.signum(valorCuradoAguardandoSinal.intValue());
        papelAguardandoSinal = null;
        valorCuradoAguardandoSinal = null;
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
        String limpo = valorCuradoDoPapel(papel) == null ? "" : valorCuradoDoPapel(papel).trim();
        int valor;
        try {
            valor = Integer.parseInt(limpo);
        } catch (NumberFormatException invalido) {
            throw new IllegalStateException(
                    "Valor curado inválido para " + papel.getChave() + ": " + limpo, invalido);
        }
        if (CatalogoNecessidadeRepresentacaoDeSinal.necessitaRepresentacaoDeSinal(papel.getChave())) {
            // Mesmo protocolo do desktop: a magnitude é revelada, mas o
            // sinal só é aplicado quando o estudante escolhe explicitamente
            // (ver escolherSinalNumeroRelativo) — nunca de imediato aqui.
            papelAguardandoSinal = papel;
            valorCuradoAguardandoSinal = Integer.valueOf(valor);
            return;
        }
        ContextoAcao contexto = new ContextoAcao(
                "sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        papel.posicionar(new NumeroInteiro(valor), OrigemAcao.ORIGEM_USUARIO, contexto);
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
}
