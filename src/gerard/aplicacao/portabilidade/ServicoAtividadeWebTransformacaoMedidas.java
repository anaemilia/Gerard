package gerard.aplicacao.portabilidade;

import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.semantica.numero.ValorNumerico;
import java.util.Arrays;
import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;

import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.CatalogoNecessidadeRepresentacaoDeSinal;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoMedidas;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacao;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.numero.NumeroInteiro;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Tentativa web portátil de Transformação de Medidas. */
public final class ServicoAtividadeWebTransformacaoMedidas
        implements ServicoAtividadeWeb, ServicoAtividadeWebComSinal {
    private final String tentativaId;
    private final SituacaoProblemaAditiva situacao;
    private final EscopoTentativaWeb escopo;
    private final AvaliadorOrigemDestinoWeb avaliadorOrigemDestino;
    private IncognitaQuantitativa incognita;
    private SinalNumeroRelativoWeb sinais;
    private PapelQuantitativo estadoInicial;
    private PapelQuantitativo transformacao;
    private PapelQuantitativo estadoFinal;
    private PapelQuantitativo papelDesconhecido;
    private RelacaoEstruturalTransformacao relacao;
    // Draft do servidor (nunca grava em papelDesconhecido) — "?" arrastado
    // até a caixa, ainda sem valor digitado/confirmado (ver
    // ServicoAtividadeWebComposicao.incognitaEngatada).
    private boolean incognitaEngatada;
    // Ver ServicoAtividadeWebComparacaoMedidas — mesmo protocolo de
    // sinal-aguardando-escolha para o papel "transformacao" (que precisa de
    // representação de sinal nesta categoria).
    private PapelQuantitativo papelAguardandoSinal;
    private Integer valorCuradoAguardandoSinal;

    public ServicoAtividadeWebTransformacaoMedidas(String tentativaId,
            SituacaoProblemaAditiva situacao) {
        this(tentativaId, situacao, EscopoTentativaWeb.isolado(tentativaId));
    }

    public ServicoAtividadeWebTransformacaoMedidas(String tentativaId,
            SituacaoProblemaAditiva situacao, EscopoTentativaWeb escopo) {
        this(tentativaId, situacao, escopo, PortaQuestionamentoPosicionamento.NAO_APLICAVEL);
    }

    public ServicoAtividadeWebTransformacaoMedidas(String tentativaId,
            SituacaoProblemaAditiva situacao, EscopoTentativaWeb escopo,
            PortaQuestionamentoPosicionamento questionamento) {
        if (situacao == null
                || situacao.getTipo() != TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            throw new IllegalArgumentException(
                    "situação de Transformação de Medidas é obrigatória");
        }
        this.tentativaId = tentativaId;
        this.situacao = situacao;
        this.escopo = escopo == null ? EscopoTentativaWeb.isolado(tentativaId) : escopo;
        this.avaliadorOrigemDestino = new AvaliadorOrigemDestinoWeb(questionamento);
        reiniciar();
    }

    public synchronized Map<String, Object> estadoAtual() {
        Map<String, Object> estado = mapa();
        estado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_ESTADO);
        estado.put("situacao_id", situacao.getId());
        estado.put("tentativa_id", tentativaId);
        estado.put("categoria", TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS.name());
        estado.put("relacao", relacao.descreverRelacao());
        estado.put("papel_desconhecido_original", papelDesconhecido.getChave());
        List<Object> papeis = new ArrayList<Object>();
        papeis.add(projetarPapel(estadoInicial));
        papeis.add(projetarPapel(transformacao));
        papeis.add(projetarPapel(estadoFinal));
        estado.put("papeis", papeis);
        boolean concluida = papelDesconhecido.estaPreenchido()
                && relacao.verificarConsistencia(
                        estadoInicial, transformacao, estadoFinal)
                        == gerard.dominio.campoaditivo.EstadoConsistencia.CONSISTENTE;
        estado.put("concluida", Boolean.valueOf(concluida));
        estado.put("papel_aguardando_sinal",
                papelAguardandoSinal == null ? null : papelAguardandoSinal.getChave());
        // Escalada de Scaffolding da incógnita no limite (3ª tentativa rejeitada
        // consecutiva, ver PapelQuantitativo.estaBloqueadoPorLimiteTentativas):
        // fato do domínio, persistente até restaurar(). O cliente só o exibe —
        // a historinha passiva (representação complementar) depende dele e
        // nunca de memória própria (decisão de 2026-10-01).
        estado.put("escalada_no_limite",
                Boolean.valueOf(papelDesconhecido.estaBloqueadoPorLimiteTentativas()));
        estado.put("magnitude_aguardando_sinal", valorCuradoAguardandoSinal == null
                ? null : Integer.valueOf(Math.abs(valorCuradoAguardandoSinal.intValue())));
        // Protocolo de mouse é posicionar (ver ServicoAtividadeWebComposicao):
        // os papéis conhecidos não vêm pré-preenchidos, só a incógnita fica
        // disponível depois dos outros dois estarem posicionados.
        boolean papeisConhecidosProntos = todosOsConhecidosPreenchidos();
        List<Object> acoes = AcoesDisponiveisAtividadeWeb.modelagemPapel(
                concluida || !papeisConhecidosProntos, papelDesconhecido.getChave());
        if (papelAguardandoSinal != null) {
            acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEscolherSinal(papelAguardandoSinal.getChave()));
        } else if (!papeisConhecidosProntos) {
            for (PapelQuantitativo papel : new PapelQuantitativo[] {estadoInicial, transformacao, estadoFinal}) {
                if (papel != papelDesconhecido && !papel.estaPreenchido()) {
                    acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoPosicionarConhecido(papel.getChave()));
                }
            }
        } else if (!concluida) {
            acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEngatarIncognita(papelDesconhecido.getChave()));
        }
        estado.put("acoes_disponiveis", acoes);
        return estado;
    }

    private boolean todosOsConhecidosPreenchidos() {
        for (PapelQuantitativo papel : new PapelQuantitativo[] {estadoInicial, transformacao, estadoFinal}) {
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
        ResultadoQuestionamentoPosicionamento questionamento =
                avaliadorOrigemDestino.avaliar(origemPapelId, papel.getChave(), situacao.getTipo(),
                        participantes(), escopo);
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

    /** Ver ServicoAtividadeWebComparacaoMedidas.escolherSinalNumeroRelativo. */
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
        boolean sinalDivergeDoCurado = sinais.avaliarDivergencia(
                papel.getChave(), sinal, base, participantes(), escopo);
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
        ResultadoQuestionamentoPosicionamento questionamento =
                avaliadorOrigemDestino.avaliar(origemPapelId, papelDesconhecido.getChave(), situacao.getTipo(),
                        participantes(), escopo);
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
            papelDesconhecido.posicionar(proposta, OrigemAcao.ORIGEM_USUARIO, contexto);
        }
        Map<String, Object> resultado = avaliacao.getProjecao();
        resultado.put("estado", estadoAtual());
        return resultado;
    }


    public synchronized Map<String, Object> reiniciar() {
        estadoInicial = FabricaPapeisTransformacaoMedidas
                .estadoInicial(PublicadorEventoDominio.NENHUM);
        transformacao = FabricaPapeisTransformacaoMedidas
                .transformacao(PublicadorEventoDominio.NENHUM);
        estadoFinal = FabricaPapeisTransformacaoMedidas
                .estadoFinal(PublicadorEventoDominio.NENHUM);
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
        relacao = RelacaoEstruturalTransformacao.transformacaoDeMedidas();
        incognitaEngatada = false;
        papelAguardandoSinal = null;
        valorCuradoAguardandoSinal = null;
        escopo.incorporar(estadoInicial, transformacao, estadoFinal);
        this.incognita = papelDesconhecido == null ? null
                : new IncognitaQuantitativa(papelDesconhecido.getChave(),
                        situacao.getTipo(), papelDesconhecido);
        sinais = new SinalNumeroRelativoWeb(situacao);
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
        if (estadoInicial.getChave().equals(chave)) return estadoInicial;
        if (transformacao.getChave().equals(chave)) return transformacao;
        if (estadoFinal.getChave().equals(chave)) return estadoFinal;
        throw new IllegalStateException(
                "papel incompatível com Transformação de Medidas: " + chave);
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

    /** O valor esperado da incógnita é calculado pela relação estrutural. */
    private ValorNumerico valorEsperado() {
        gerard.dominio.campoaditivo.ResultadoCalculo calculo =
                relacao.calcularValorAusente(estadoInicial, transformacao, estadoFinal);
        return calculo.temValorCalculavel() ? calculo.getValorCalculado() : null;
    }

    private List<String> participantes() {
        return Arrays.asList(estadoInicial.getChave(), transformacao.getChave(), estadoFinal.getChave());
    }

}
