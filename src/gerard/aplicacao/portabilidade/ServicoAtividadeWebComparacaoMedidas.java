package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisComparacaoMedidas;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComparacao;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Tentativa web portátil de Comparação de Medidas. */
public final class ServicoAtividadeWebComparacaoMedidas
        implements ServicoAtividadeWeb {
    private final String tentativaId;
    private final SituacaoProblemaAditiva situacao;
    private PapelQuantitativo referido;
    private PapelQuantitativo valorRelativo;
    private PapelQuantitativo referendo;
    private PapelQuantitativo papelDesconhecido;
    private RelacaoEstruturalComparacao relacao;
    // Draft do servidor (nunca grava em papelDesconhecido) — "?" arrastado
    // até a caixa, ainda sem valor digitado/confirmado (ver
    // ServicoAtividadeWebComposicao.incognitaEngatada).
    private boolean incognitaEngatada;

    public ServicoAtividadeWebComparacaoMedidas(String tentativaId,
            SituacaoProblemaAditiva situacao) {
        if (situacao == null
                || situacao.getTipo() != TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            throw new IllegalArgumentException(
                    "situação de Comparação de Medidas é obrigatória");
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
        estado.put("categoria", TipoSituacaoAditiva.COMPARACAO_MEDIDAS.name());
        estado.put("relacao", relacao.descreverRelacao());
        estado.put("papel_desconhecido_original", papelDesconhecido.getChave());
        List<Object> papeis = new ArrayList<Object>();
        papeis.add(projetarPapel(referido));
        papeis.add(projetarPapel(valorRelativo));
        papeis.add(projetarPapel(referendo));
        estado.put("papeis", papeis);
        boolean concluida = papelDesconhecido.estaPreenchido()
                && relacao.verificarConsistencia(referido, valorRelativo, referendo)
                        == gerard.dominio.campoaditivo.EstadoConsistencia.CONSISTENTE;
        estado.put("concluida", Boolean.valueOf(concluida));
        // Protocolo de mouse é posicionar (ver ServicoAtividadeWebComposicao):
        // os papéis conhecidos não vêm pré-preenchidos, só a incógnita fica
        // disponível depois dos outros dois estarem posicionados.
        boolean papeisConhecidosProntos = todosOsConhecidosPreenchidos();
        List<Object> acoes = AcoesDisponiveisAtividadeWeb.modelagemPapel(
                concluida || !papeisConhecidosProntos, papelDesconhecido.getChave());
        if (!papeisConhecidosProntos) {
            for (PapelQuantitativo papel : new PapelQuantitativo[] {referido, valorRelativo, referendo}) {
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
        for (PapelQuantitativo papel : new PapelQuantitativo[] {referido, valorRelativo, referendo}) {
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
    public synchronized Map<String, Object> posicionarValorConhecido(String papelId) {
        PapelQuantitativo papel = papelPorChave(papelId);
        if (papel == papelDesconhecido) {
            throw new IllegalArgumentException(
                    "papel é a incógnita desta situação, use PROPOR_VALOR_PAPEL: " + papelId);
        }
        if (!papel.estaPreenchido()) {
            posicionarConhecido(papel);
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    /** Engata o "?" na caixa da incógnita (protocolo mouse-texto, Main.java). */
    public synchronized Map<String, Object> engatarIncognita(String papelId) {
        if (!papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException(
                    "papel não é a incógnita desta situação: " + papelId);
        }
        if (!papelDesconhecido.estaPreenchido()) {
            incognitaEngatada = true;
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", ServicoAtividadeWebComposicao.SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
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
                .diagnosticarValorProposto(referido, valorRelativo, referendo,
                        papelDesconhecido, proposta);
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
        // getChaveMensagem() não é texto exibível (ver o comentário em
        // ServicoAtividadeWebComposicao.proporValor) — chave_mensagem usa o
        // texto real via MensagemFeedbackIncognitaWeb.
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
        referido = FabricaPapeisComparacaoMedidas
                .referido(PublicadorEventoDominio.NENHUM);
        valorRelativo = FabricaPapeisComparacaoMedidas
                .valorRelativo(PublicadorEventoDominio.NENHUM);
        referendo = FabricaPapeisComparacaoMedidas
                .referendo(PublicadorEventoDominio.NENHUM);
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
        relacao = RelacaoEstruturalComparacao.comparacaoDeMedidas();
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
        if (referido.getChave().equals(chave)) return referido;
        if (valorRelativo.getChave().equals(chave)) return valorRelativo;
        if (referendo.getChave().equals(chave)) return referendo;
        throw new IllegalStateException(
                "papel incompatível com Comparação de Medidas: " + chave);
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
