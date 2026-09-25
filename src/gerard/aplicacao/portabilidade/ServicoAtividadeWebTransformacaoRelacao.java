package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.CatalogoNecessidadeRepresentacaoDeSinal;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.numero.NumeroInteiro;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Tentativa web portátil de Transformação de Relação — caminho BÁSICO, sem
 * depender de narrativa rica (ver {@link ServicoAtividadeWebTransformacaoRelacaoRica},
 * que exige um XML sidecar em narrativas-ricas/ — pasta que hoje nem existe
 * neste ambiente, deixando 100% das situações desta categoria sem nenhuma
 * atividade, mesmo quando o dado tabular básico já é suficiente).
 *
 * Usa {@link RelacaoEstruturalTransformacaoDeRelacao} (RelacaoFinal =
 * RelacaoInicial + Transformacao, sem orientação narrativa — a variante
 * "Orientada" é que exige o dado rico) e
 * {@link FabricaPapeisTransformacaoDeRelacao}, exatamente o mesmo padrão já
 * usado por ServicoAtividadeWebTransformacaoMedidas/ComparacaoMedidas.
 * Confirmado contra captura de tela do desktop (2026-09-04): a mesma
 * situação ("Ana tem 5 figurinhas a mais que Bia...") funciona lá sem
 * nenhuma narrativa rica — RelacaoInicial já aparece posicionada com "+5".
 *
 * Auditoria de acoplamento Main/web, 2026-09-19: no desktop,
 * {@code Main.TelaGerard} sempre ativa {@code SeletorOperacaoRelacaoAluno}
 * para esta categoria (ver {@code montarNovoDiagrama}), independentemente da
 * digitação da incógnita acima — um GATE ADICIONAL, não alternativo (ver
 * {@code Main.operacoesDeSomaSubtracaoRespondidasCorretamente}, que exige os
 * dois). O seletor só fica de fato visível quando a situação curada tem
 * {@code operacao_relacao} preenchido (mesma guarda de
 * {@code SeletorOperacaoRelacaoAluno.ativar}) — "figurinhas" (citada acima)
 * nunca teve esse campo curado, por isso nunca precisou do seletor; "bonecas"
 * tem {@code operacao_relacao=subtracao} e por isso o desktop mostra os dois
 * botões ali. Esta classe implementa também
 * {@link ServicoAtividadeWebEscolhaOperacao} para cobrir esse segundo gate,
 * reaproveitando {@link AvaliacaoEscolhaOperacaoRelacao} (mesmo objeto rico
 * já usado por {@link ServicoAtividadeWebComposicaoRelacoes}) — nenhuma
 * semântica nova inventada aqui.
 */
public final class ServicoAtividadeWebTransformacaoRelacao
        implements ServicoAtividadeWeb, ServicoAtividadeWebComSinal,
        ServicoAtividadeWebEscolhaOperacao {
    private final String tentativaId;
    private final SituacaoProblemaAditiva situacao;
    private PapelQuantitativo relacaoInicial;
    private PapelQuantitativo transformacao;
    private PapelQuantitativo relacaoFinal;
    private PapelQuantitativo papelDesconhecido;
    private RelacaoEstruturalTransformacaoDeRelacao relacao;
    // Draft do servidor (nunca grava em papelDesconhecido) — "?" arrastado
    // até a caixa, ainda sem valor digitado/confirmado (ver
    // ServicoAtividadeWebComposicao.incognitaEngatada).
    private boolean incognitaEngatada;
    // Papel conhecido que precisa de representação de sinal (ver
    // ServicoAtividadeWebComSinal) já revelado (arrastado) mas ainda sem
    // sinal escolhido — mesmo protocolo do desktop e mesmo padrão já usado
    // por ServicoAtividadeWebComparacaoMedidas/TransformacaoMedidas.
    private PapelQuantitativo papelAguardandoSinal;
    private Integer valorCuradoAguardandoSinal;
    // Segundo gate de conclusão, independente da incógnita digitada acima
    // (ver Javadoc da classe) -- só relevante quando a situação curada tem
    // operacao_relacao preenchido.
    private OpcaoOperacaoCuradoria escolhaOperacao;

    public ServicoAtividadeWebTransformacaoRelacao(String tentativaId,
            SituacaoProblemaAditiva situacao) {
        if (situacao == null
                || situacao.getTipo() != TipoSituacaoAditiva.TRANSFORMACAO_RELACAO) {
            throw new IllegalArgumentException(
                    "situação de Transformação de Relação é obrigatória");
        }
        this.tentativaId = tentativaId;
        this.situacao = situacao;
        reiniciar();
    }

    public synchronized Map<String, Object> estadoAtual() {
        Map<String, Object> estado = mapa();
        estado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_ESTADO);
        estado.put("situacao_id", situacao.getId());
        estado.put("tentativa_id", tentativaId);
        estado.put("categoria", TipoSituacaoAditiva.TRANSFORMACAO_RELACAO.name());
        estado.put("relacao", relacao.descreverRelacao());
        estado.put("papel_desconhecido_original", papelDesconhecido.getChave());
        List<Object> papeis = new ArrayList<Object>();
        papeis.add(projetarPapel(relacaoInicial));
        papeis.add(projetarPapel(transformacao));
        papeis.add(projetarPapel(relacaoFinal));
        estado.put("papeis", papeis);
        boolean concluidaPosicionamento = papelDesconhecido.estaPreenchido()
                && relacao.verificarConsistencia(
                        relacaoInicial, transformacao, relacaoFinal)
                        == gerard.dominio.campoaditivo.EstadoConsistencia.CONSISTENTE;
        boolean seletorAtivo = seletorOperacaoAtivo();
        boolean operacaoCorreta = seletorAtivo && respondeuOperacaoCorretamente();
        boolean concluida = concluidaPosicionamento && (!seletorAtivo || operacaoCorreta);
        estado.put("concluida", Boolean.valueOf(concluida));
        estado.put("escolha_operacao", nomeOuNull(escolhaOperacao));
        // Sinal explícito e estável pro cliente saber se este seletor se
        // aplica a esta situação -- distinto de "a ação está em
        // acoes_disponiveis agora" (que também fica ausente só porque já foi
        // respondida corretamente). Sem isso, SeletorOperacaoDiagramaGerard
        // não tinha como distinguir "nunca teve operacao_relacao curado, não
        // mostrar" de "curado e já respondido certo, mostrar sem interação"
        // -- os dois casos deixam a ação fora de acoes_disponiveis do mesmo
        // jeito (achado ao corrigir o mesmo problema em
        // ServicoAtividadeWebComposicaoRelacoes).
        estado.put("operacao_disponivel", Boolean.valueOf(seletorAtivo));
        // Mesmo nome de campo de ServicoAtividadeWebComposicaoRelacoes
        // ("correta", não "correta_operacao") -- os dois compartilham o
        // mesmo componente React de seletor único (SeletorOperacaoDiagramaGerard).
        estado.put("correta", !seletorAtivo || escolhaOperacao == null
                || escolhaOperacao == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                ? null : Boolean.valueOf(operacaoCorreta));
        // Protocolo de mouse é posicionar (ver ServicoAtividadeWebComposicao):
        // os papéis conhecidos não vêm pré-preenchidos, só a incógnita fica
        // disponível depois dos outros dois estarem posicionados.
        boolean papeisConhecidosProntos = todosOsConhecidosPreenchidos();
        estado.put("papel_aguardando_sinal",
                papelAguardandoSinal == null ? null : papelAguardandoSinal.getChave());
        List<Object> acoes = AcoesDisponiveisAtividadeWeb.modelagemPapel(
                concluidaPosicionamento || !papeisConhecidosProntos, papelDesconhecido.getChave());
        if (papelAguardandoSinal != null) {
            // Enquanto o sinal não é escolhido, nenhuma outra ação de
            // posicionamento fica disponível para este papel — mesmo
            // protocolo do desktop e do padrão já usado em
            // ServicoAtividadeWebComparacaoMedidas/TransformacaoMedidas.
            acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEscolherSinal(papelAguardandoSinal.getChave()));
        } else if (!papeisConhecidosProntos) {
            for (PapelQuantitativo papel : new PapelQuantitativo[] {relacaoInicial, transformacao, relacaoFinal}) {
                if (papel != papelDesconhecido && !papel.estaPreenchido()) {
                    acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoPosicionarConhecido(papel.getChave()));
                }
            }
        } else {
            // Os dois gates são independentes (ver Javadoc da classe):
            // oferece a escolha de operação enquanto ela não foi respondida
            // certa, e a digitação da incógnita enquanto ela não foi
            // respondida certa -- mesmo com o outro gate já satisfeito.
            if (seletorAtivo && !operacaoCorreta) {
                acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEscolherOperacaoRelacao());
            }
            if (!concluidaPosicionamento) {
                acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEngatarIncognita(papelDesconhecido.getChave()));
            }
        }
        estado.put("acoes_disponiveis", acoes);
        return estado;
    }

    /** Ver Javadoc da classe: segundo gate de conclusão, independente da incógnita. */
    private boolean seletorOperacaoAtivo() {
        return AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, situacao,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES) != OpcaoOperacaoCuradoria.NAO_SELECIONADO;
    }

    private boolean respondeuOperacaoCorretamente() {
        if (escolhaOperacao == null) {
            return false;
        }
        OpcaoOperacaoCuradoria escolhaCorreta = AvaliacaoEscolhaOperacaoRelacao
                .determinarOperacaoCorreta(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                        situacao, TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        return AvaliacaoEscolhaOperacaoRelacao.respondeuCorretamente(escolhaOperacao, escolhaCorreta);
    }

    /**
     * Escolhe soma/subtração para o segundo gate de conclusão (ver Javadoc da
     * classe) — mesmo protocolo/padrão de
     * ServicoAtividadeWebComposicaoRelacoes.escolherOperacao, só que sem
     * parâmetro de seletor (esta categoria só tem uma operação, como
     * Composição de Relações).
     */
    public synchronized Map<String, Object> escolherOperacao(String seletor, String operacao) {
        OpcaoOperacaoCuradoria escolhaAluno = OpcaoOperacaoCuradoria.aPartirDoEstado(operacao);
        OpcaoOperacaoCuradoria escolhaCorreta = AvaliacaoEscolhaOperacaoRelacao
                .determinarOperacaoCorreta(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                        situacao, TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
        boolean aceita = AvaliacaoEscolhaOperacaoRelacao
                .respondeuCorretamente(escolhaAluno, escolhaCorreta);
        escolhaOperacao = escolhaAluno;

        Map<String, Object> resultado = mapa();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("action_id", UUID.randomUUID().toString());
        resultado.put("aceita", Boolean.valueOf(aceita));
        resultado.put("diagnostico", aceita ? null : escolhaAluno.name());
        resultado.put("chave_mensagem", aceita ? null : resolverExplicacaoOperacao(escolhaCorreta));
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    private String resolverExplicacaoOperacao(OpcaoOperacaoCuradoria operacao) {
        String chave = AvaliacaoEscolhaOperacaoRelacao.chaveExplicacao(
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                TipoOperacaoSeletor.ENTRE_TRANSFORMACOES, operacao);
        if (chave == null) {
            return null;
        }
        String modelo = ServicoLocalizacao.getInstancia().texto(chave);
        return AvaliacaoEscolhaOperacaoRelacao.preencherPersonagensCurados(modelo, situacao);
    }

    private static String nomeOuNull(OpcaoOperacaoCuradoria opcao) {
        return opcao == null || opcao == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                ? null : opcao.name();
    }

    private boolean todosOsConhecidosPreenchidos() {
        for (PapelQuantitativo papel : new PapelQuantitativo[] {relacaoInicial, transformacao, relacaoFinal}) {
            if (papel != papelDesconhecido && !papel.estaPreenchido()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Posiciona um papel conhecido (nunca a incógnita) com o valor curado —
     * ação que soltar um elemento não-incógnita do enunciado sobre sua caixa
     * dispara (protocolo de mouse é posicionar, ver ServicoAtividadeWebComposicao).
     */
    public synchronized Map<String, Object> posicionarValorConhecido(String papelId, String origemPapelId) {
        PapelQuantitativo papel = papelPorChave(papelId);
        if (papel == papelDesconhecido) {
            throw new IllegalArgumentException(
                    "papel é a incógnita desta situação, use PROPOR_VALOR_PAPEL: " + papelId);
        }
        gerard.Scaffolding.questionamento.ResultadoQuestionamento questionamento =
                AvaliadorOrigemDestinoWeb.avaliar(origemPapelId, papel.getChave(), situacao.getTipo());
        if (questionamento.isAplicavel() && !questionamento.isCorreto()) {
            Map<String, Object> rejeitado = mapa();
            rejeitado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
            rejeitado.put("aceita", Boolean.FALSE);
            rejeitado.put("chave_mensagem", questionamento.getMensagem());
            rejeitado.put("estado", estadoAtual());
            return rejeitado;
        }
        if (!papel.estaPreenchido() && papel != papelAguardandoSinal) {
            posicionarConhecido(papel);
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
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
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.valueOf(!diagnostico.isPresent()));
        resultado.put("mensagem_sinal_divergente", sinalDivergeDoCurado
                ? ServicoLocalizacao.getInstancia().formatar("ui.tooltip.relativeSign.confirm", sinal)
                : null);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    /** Engata o "?" na caixa da incógnita (protocolo mouse-texto, Main.java). */
    public synchronized Map<String, Object> engatarIncognita(String papelId, String origemPapelId) {
        if (!papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException(
                    "papel não é a incógnita desta situação: " + papelId);
        }
        gerard.Scaffolding.questionamento.ResultadoQuestionamento questionamento =
                AvaliadorOrigemDestinoWeb.avaliar(origemPapelId, papelDesconhecido.getChave(), situacao.getTipo());
        if (questionamento.isAplicavel() && !questionamento.isCorreto()) {
            Map<String, Object> rejeitado = mapa();
            rejeitado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
            rejeitado.put("aceita", Boolean.FALSE);
            rejeitado.put("chave_mensagem", questionamento.getMensagem());
            rejeitado.put("estado", estadoAtual());
            return rejeitado;
        }
        if (!papelDesconhecido.estaPreenchido()) {
            incognitaEngatada = true;
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("chave_mensagem", null);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    public synchronized Map<String, Object> proporValor(String papelId, int valor) {
        if (!papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException(
                    "papel não é a incógnita desta situação: " + papelId);
        }
        NumeroInteiro proposta = new NumeroInteiro(valor);
        ContextoAcao contexto = new ContextoAcao(
                "sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        IdentidadeAcaoInstrumentalPapel identidade = papelDesconhecido
                .iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO);
        Optional<DiagnosticoErroPapel> diagnostico = relacao
                .diagnosticarValorProposto(relacaoInicial, transformacao,
                        relacaoFinal, papelDesconhecido, proposta);
        ResultadoRegistroTentativaPapel registro = papelDesconhecido
                .registrarTentativaComIdentidade(
                        identidade, diagnostico, contexto, proposta);
        if (!diagnostico.isPresent()) {
            papelDesconhecido.posicionar(
                    proposta, OrigemAcao.ORIGEM_USUARIO, contexto);
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("action_id", registro.getActionId());
        resultado.put("aceita", Boolean.valueOf(!diagnostico.isPresent()));
        resultado.put("diagnostico", diagnostico.isPresent()
                ? diagnostico.get().getTipo().name() : null);
        resultado.put("chave_mensagem", diagnostico.isPresent()
                ? MensagemFeedbackIncognitaWeb.resolver(papelDesconhecido, registro) : null);
        resultado.put("limite_atingido", Boolean.valueOf(
                diagnostico.isPresent() && MensagemFeedbackIncognitaWeb.limiteAtingido(registro)));
        resultado.put("rejeicoes_consecutivas",
                Integer.valueOf(registro.getRejeicoesConsecutivas()));
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    public synchronized Map<String, Object> reiniciar() {
        relacaoInicial = FabricaPapeisTransformacaoDeRelacao
                .relacaoInicial(PublicadorEventoDominio.NENHUM);
        transformacao = FabricaPapeisTransformacaoDeRelacao
                .transformacao(PublicadorEventoDominio.NENHUM);
        relacaoFinal = FabricaPapeisTransformacaoDeRelacao
                .relacaoFinal(PublicadorEventoDominio.NENHUM);
        ResolvedorIncognitaCurada.Resultado incognita =
                new ResolvedorIncognitaCurada().resolver(situacao);
        if (!incognita.possuiIncognita() || incognita.possuiConflito()) {
            throw new IllegalStateException("incógnita curada inválida: "
                    + incognita.mensagemInconsistencia());
        }
        papelDesconhecido = papelPorChave(incognita.getChaveEfetiva());
        // Nada é pré-posicionado — protocolo de mouse é posicionar: o aluno
        // arrasta cada papel (conhecido ou incógnita) do enunciado até o
        // diagrama (ver posicionarValorConhecido / proporValor).
        relacao = RelacaoEstruturalTransformacaoDeRelacao.transformacaoDeRelacao();
        incognitaEngatada = false;
        papelAguardandoSinal = null;
        valorCuradoAguardandoSinal = null;
        escolhaOperacao = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
        return estadoAtual();
    }

    private void posicionarConhecido(PapelQuantitativo papel) {
        Integer valor = SemanticaCuradaSituacao.buscarValorInteiroVisivel(
                situacao, null, papel.getChave());
        if (valor == null) {
            throw new IllegalStateException(
                    "valor curado ausente para " + papel.getChave());
        }
        if (CatalogoNecessidadeRepresentacaoDeSinal.necessitaRepresentacaoDeSinal(papel.getChave())) {
            // Mesmo protocolo do desktop: a magnitude é revelada, mas o
            // sinal só é aplicado quando o estudante escolhe explicitamente
            // (ver escolherSinalNumeroRelativo) — nunca de imediato aqui.
            papelAguardandoSinal = papel;
            valorCuradoAguardandoSinal = valor;
            return;
        }
        ContextoAcao contexto = new ContextoAcao(
                "sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        papel.posicionar(new NumeroInteiro(valor.intValue()),
                OrigemAcao.ORIGEM_USUARIO, contexto);
    }

    private PapelQuantitativo papelPorChave(String chave) {
        if (relacaoInicial.getChave().equals(chave)) return relacaoInicial;
        if (transformacao.getChave().equals(chave)) return transformacao;
        if (relacaoFinal.getChave().equals(chave)) return relacaoFinal;
        throw new IllegalStateException(
                "papel incompatível com Transformação de Relação: " + chave);
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
