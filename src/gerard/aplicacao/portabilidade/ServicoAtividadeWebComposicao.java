package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.curadoria.MaterializadorEnunciadoCurado;
import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicao;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroNatural;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fachada de aplicação portátil para uma tentativa mínima de Composição de
 * Medidas. Coordena a ação, mas deixa o diagnóstico com a relação estrutural
 * e a validação local com o papel quantitativo.
 */
public final class ServicoAtividadeWebComposicao implements ServicoAtividadeWeb {
    private static final String SITUACAO_CURADA_ID =
            "PO_COMPOSICAO_MEDIDAS_bolas_1098018440";
    public static final String SCHEMA_ESTADO = "gerard.atividade-web.estado.v1";
    public static final String SCHEMA_RESULTADO = "gerard.atividade-web.resultado-acao.v1";

    private PapelQuantitativo parte1;
    private PapelQuantitativo parte2;
    private PapelQuantitativo todo;
    private RelacaoEstruturalComposicao relacao;
    private final String tentativaId;
    private final SituacaoProblemaAditiva situacao;
    private PapelQuantitativo papelDesconhecido;

    public ServicoAtividadeWebComposicao() {
        this("tentativa.web.composicao-medidas");
    }

    public ServicoAtividadeWebComposicao(String tentativaId) {
        this(tentativaId, carregarSituacaoCurada());
    }

    public ServicoAtividadeWebComposicao(String tentativaId,
            SituacaoProblemaAditiva situacao) {
        if (situacao == null || situacao.getTipo() != TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            throw new IllegalArgumentException("situação de Composição de Medidas é obrigatória");
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
        estado.put("categoria", "COMPOSICAO_MEDIDAS");
        estado.put("enunciado", new MaterializadorEnunciadoCurado().materializar(situacao));
        estado.put("relacao", relacao.descreverRelacao());
        estado.put("papel_desconhecido_original", papelDesconhecido.getChave());
        estado.put("rotulo_papel_desconhecido", papelDesconhecido.getNomeConceitual());
        estado.put("parte1", projetarPapel(parte1));
        estado.put("parte2", projetarPapel(parte2));
        estado.put("todo", projetarPapel(todo));
        boolean concluida = papelDesconhecido.estaPreenchido()
                && relacao.verificarConsistencia(parte1, parte2, todo)
                        == gerard.dominio.campoaditivo.EstadoConsistencia.CONSISTENTE;
        estado.put("concluida", Boolean.valueOf(concluida));
        estado.put("acoes_disponiveis",
                AcoesDisponiveisAtividadeWeb.modelagemPapel(
                        concluida, papelDesconhecido.getChave()));
        return estado;
    }

    public synchronized Map<String, Object> proporTodo(int valor) {
        return proporValor(papelDesconhecido.getChave(), valor);
    }

    public synchronized Map<String, Object> proporValor(String papelId, int valor) {
        if (!papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException("papel não é a incógnita desta situação: " + papelId);
        }
        NumeroNatural proposta = new NumeroNatural(valor);
        ContextoAcao contexto = new ContextoAcao(
                "sessao.web.local", "usuario.web.local", tentativaId,
                situacao.getId(), "diagrama.vergnaud.web");
        IdentidadeAcaoInstrumentalPapel identidade =
                papelDesconhecido.iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO);
        Optional<DiagnosticoErroPapel> diagnostico =
                relacao.diagnosticarValorProposto(
                        parte1, parte2, todo, papelDesconhecido, proposta);
        ResultadoRegistroTentativaPapel registro =
                papelDesconhecido.registrarTentativaComIdentidade(
                        identidade, diagnostico, contexto, proposta);

        if (!diagnostico.isPresent()) {
            papelDesconhecido.posicionar(proposta, OrigemAcao.ORIGEM_USUARIO, contexto);
        }

        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("action_id", registro.getActionId());
        resultado.put("aceita", Boolean.valueOf(!diagnostico.isPresent()));
        resultado.put("diagnostico", diagnostico.isPresent()
                ? diagnostico.get().getTipo().name() : null);
        resultado.put("chave_mensagem", diagnostico.isPresent()
                ? diagnostico.get().getChaveMensagem() : null);
        resultado.put("rejeicoes_consecutivas",
                Integer.valueOf(registro.getRejeicoesConsecutivas()));
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    public synchronized Map<String, Object> reiniciar() {
        parte1 = PapelQuantitativo.parte1(PublicadorEventoDominio.NENHUM);
        parte2 = PapelQuantitativo.parte2(PublicadorEventoDominio.NENHUM);
        todo = PapelQuantitativo.todo(PublicadorEventoDominio.NENHUM);
        ResolvedorIncognitaCurada.Resultado incognita =
                new ResolvedorIncognitaCurada().resolver(situacao);
        if (!incognita.possuiIncognita() || incognita.possuiConflito()) {
            throw new IllegalStateException("incógnita curada inválida: "
                    + incognita.mensagemInconsistencia());
        }
        papelDesconhecido = papelPorChave(incognita.getChaveEfetiva());
        posicionarConhecido(parte1, situacao.getQuantidade1(), "quantidade_1");
        posicionarConhecido(parte2, situacao.getQuantidade2(), "quantidade_2");
        posicionarConhecido(todo, situacao.getResultado(), "resultado");
        relacao = RelacaoEstruturalComposicao.composicaoDeMedidas();
        return estadoAtual();
    }

    private void posicionarConhecido(PapelQuantitativo papel, String valor, String campo) {
        if (papel != papelDesconhecido) {
            papel.posicionar(numeroCurado(valor, campo), OrigemAcao.ORIGEM_SISTEMA,
                    ContextoAcao.NAO_INFORMADO);
        }
    }

    private PapelQuantitativo papelPorChave(String chave) {
        if (parte1.getChave().equals(chave)) return parte1;
        if (parte2.getChave().equals(chave)) return parte2;
        if (todo.getChave().equals(chave)) return todo;
        throw new IllegalStateException("papel desconhecido incompatível com Composição: " + chave);
    }

    private static SituacaoProblemaAditiva carregarSituacaoCurada() {
        RepositorioSituacoesAditivas repositorio = new RepositorioSituacoesAditivas();
        for (SituacaoProblemaAditiva candidata : repositorio.listarValidadas()) {
            if (SITUACAO_CURADA_ID.equals(candidata.getId())) {
                if (candidata.getTipo() != TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                        || !"todo".equalsIgnoreCase(candidata.getTermoDesconhecido())) {
                    throw new IllegalStateException("Situação curada incompatível com o corte web: "
                            + SITUACAO_CURADA_ID);
                }
                return candidata;
            }
        }
        throw new IllegalStateException("Situação validada não encontrada no catálogo: "
                + SITUACAO_CURADA_ID);
    }

    private static NumeroNatural numeroCurado(String valor, String campo) {
        try {
            return new NumeroNatural(Integer.parseInt(valor));
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Valor curado inválido em " + campo
                    + " da situação curada: " + valor, ex);
        }
    }

    private static Map<String, Object> projetarPapel(PapelQuantitativo papel) {
        Map<String, Object> item = mapa();
        item.put("id", papel.getChave());
        item.put("nome", papel.getNomeConceitual());
        item.put("conhecido", Boolean.valueOf(papel.estaPreenchido()));
        item.put("valor", papel.estaPreenchido()
                ? papel.valorAtual().valorOuNull() : null);
        return item;
    }

    private static Map<String, Object> mapa() {
        return new LinkedHashMap<String, Object>();
    }
}
