package gerard.aplicacao.portabilidade;

import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroInteiro;
import java.util.LinkedHashMap;
import java.util.Map;
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
        implements ServicoAtividadeWebEscolhaOperacao {
    public static final String SCHEMA_ESTADO = ServicoAtividadeWebComposicao.SCHEMA_ESTADO;
    public static final String SCHEMA_RESULTADO = ServicoAtividadeWebComposicao.SCHEMA_RESULTADO;

    private final String tentativaId;
    private final SituacaoProblemaAditiva situacao;
    private PapelQuantitativo relacao1;
    private PapelQuantitativo relacao2;
    private PapelQuantitativo relacaoFinal;
    private OpcaoOperacaoCuradoria escolha;

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
        boolean correta = respondeuCorretamente(escolha);
        estado.put("escolha_operacao", nomeOuNull(escolha));
        estado.put("correta", escolha == null || escolha == OpcaoOperacaoCuradoria.NAO_SELECIONADO
                ? null : Boolean.valueOf(correta));
        estado.put("concluida", Boolean.valueOf(correta));
        estado.put("acoes_disponiveis",
                AcoesDisponiveisAtividadeWeb.escolhaOperacaoRelacao(false, correta));
        return estado;
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

        posicionarSeCurado(relacao1, situacao.getQuantidade1(), "relacao_1");
        posicionarSeCurado(relacao2, situacao.getQuantidade2(), "relacao_2");
        posicionarSeCurado(relacaoFinal, situacao.getResultado(), "relacao_final");

        escolha = OpcaoOperacaoCuradoria.NAO_SELECIONADO;
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

    private static void posicionarSeCurado(PapelQuantitativo papel, String valorCurado,
            String campo) {
        String limpo = valorCurado == null ? "" : valorCurado.trim();
        if (limpo.isEmpty()) {
            return;
        }
        int numero;
        try {
            numero = Integer.parseInt(limpo);
        } catch (NumberFormatException invalido) {
            throw new IllegalStateException("Valor curado inválido em " + campo
                    + " da situação curada: " + valorCurado, invalido);
        }
        papel.posicionar(new NumeroInteiro(numero));
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
