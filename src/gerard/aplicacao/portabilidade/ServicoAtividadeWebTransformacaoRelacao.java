package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
 */
public final class ServicoAtividadeWebTransformacaoRelacao
        implements ServicoAtividadeWeb {
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
        boolean concluida = papelDesconhecido.estaPreenchido()
                && relacao.verificarConsistencia(
                        relacaoInicial, transformacao, relacaoFinal)
                        == gerard.dominio.campoaditivo.EstadoConsistencia.CONSISTENTE;
        estado.put("concluida", Boolean.valueOf(concluida));
        // Protocolo de mouse é posicionar (ver ServicoAtividadeWebComposicao):
        // os papéis conhecidos não vêm pré-preenchidos, só a incógnita fica
        // disponível depois dos outros dois estarem posicionados.
        boolean papeisConhecidosProntos = todosOsConhecidosPreenchidos();
        List<Object> acoes = AcoesDisponiveisAtividadeWeb.modelagemPapel(
                concluida || !papeisConhecidosProntos, papelDesconhecido.getChave());
        if (!papeisConhecidosProntos) {
            for (PapelQuantitativo papel : new PapelQuantitativo[] {relacaoInicial, transformacao, relacaoFinal}) {
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
        if (!papel.estaPreenchido()) {
            posicionarConhecido(papel);
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("chave_mensagem", null);
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
        return estadoAtual();
    }

    private void posicionarConhecido(PapelQuantitativo papel) {
        Integer valor = SemanticaCuradaSituacao.buscarValorInteiroVisivel(
                situacao, null, papel.getChave());
        if (valor == null) {
            throw new IllegalStateException(
                    "valor curado ausente para " + papel.getChave());
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
