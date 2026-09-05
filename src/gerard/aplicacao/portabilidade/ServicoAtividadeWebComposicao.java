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
import java.util.List;
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
    // Rascunho da contagem de quadradinhos — NUNCA grava em papelDesconhecido
    // (PapelQuantitativo.posicionar marcaria o papel como preenchido, tirando-o
    // de ehIncognita() e quebrando a precondição de "exatamente um incógnito"
    // de RelacaoEstruturalX.diagnosticarValorProposto/calcularValorAusente
    // para qualquer proporValor futuro). Contar não é confirmar (regra 7 de
    // gerard-consistencia-estado); só a confirmação explícita via
    // PROPOR_VALOR_PAPEL grava de verdade.
    private int contagemMaterialConcreto;
    // Draft do servidor (nunca grava em papelDesconhecido, mesmo raciocínio
    // de contagemMaterialConcreto): o "?" foi arrastado até a caixa mas
    // ainda não tem valor digitado/confirmado. Precisa ficar aqui, não só no
    // cliente — gerador de cena/projetarCena é quem decide o que a figura
    // mostra, o cliente só materializa (achado explícito da usuária:
    // "engatada" client-only quebrava esse princípio).
    private boolean incognitaEngatada;

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
        estado.put("parte1", projetarPapelParaEstado(parte1));
        estado.put("parte2", projetarPapelParaEstado(parte2));
        estado.put("todo", projetarPapelParaEstado(todo));
        boolean concluida = papelDesconhecido.estaPreenchido()
                && relacao.verificarConsistencia(parte1, parte2, todo)
                        == gerard.dominio.campoaditivo.EstadoConsistencia.CONSISTENTE;
        estado.put("concluida", Boolean.valueOf(concluida));
        // Material concreto (AG_EMCME, ver gerard-scaffolding-interacao seção 3
        // e deveExibirDiagramaComplementar em Main.java): só fica disponível na
        // última opção da escalada — 3ª rejeição consecutiva da incógnita —
        // nunca durante a modelagem normal. Persistente enquanto o papel
        // continuar bloqueado, igual ao desktop (não é um flash de uma única
        // resposta de ação).
        boolean materialConcretoDisponivel = papelDesconhecido.estaBloqueadoPorLimiteTentativas();
        estado.put("material_concreto_disponivel", Boolean.valueOf(materialConcretoDisponivel));
        // Texto do material concreto resolvido no mesmo lugar que os tips
        // (AjudaContextualWeb) — nunca hardcoded no componente web, porque
        // será internacionalizado junto com o resto da interface.
        if (materialConcretoDisponivel) {
            estado.put("material_concreto_texto", AjudaContextualWeb.textoInstrucaoMaterialConcreto());
            estado.put("material_concreto_texto_adicionar", AjudaContextualWeb.textoAdicionarQuadradinho());
            estado.put("material_concreto_texto_remover", AjudaContextualWeb.textoRemoverQuadradinho());
        }
        // Protocolo de mouse é posicionar: os papéis conhecidos (parte1/parte2)
        // não vêm pré-preenchidos — o aluno arrasta cada um do enunciado até o
        // diagrama pra posicioná-lo (ver posicionarValorConhecido). Só depois
        // dos dois estarem posicionados a incógnita tem sentido (calcular
        // "quanto é ao todo" exige conhecer as duas partes primeiro) — e é
        // também precondição de RelacaoEstruturalX.diagnosticarValorProposto
        // ("exatamente um papel incógnito entre os três").
        boolean papeisConhecidosProntos = parte1.estaPreenchido() && parte2.estaPreenchido();
        List<Object> acoes = AcoesDisponiveisAtividadeWeb.modelagemPapel(
                concluida || !papeisConhecidosProntos, papelDesconhecido.getChave());
        if (!papeisConhecidosProntos) {
            if (!parte1.estaPreenchido()) {
                acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoPosicionarConhecido(parte1.getChave()));
            }
            if (!parte2.estaPreenchido()) {
                acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoPosicionarConhecido(parte2.getChave()));
            }
        } else if (!concluida) {
            acoes.addAll(AcoesDisponiveisAtividadeWeb.acaoEngatarIncognita(papelDesconhecido.getChave()));
            if (materialConcretoDisponivel) {
                acoes.addAll(AcoesDisponiveisAtividadeWeb
                        .acaoAjustarQuadradinho(papelDesconhecido.getChave()));
            }
        }
        estado.put("acoes_disponiveis", acoes);
        return estado;
    }

    /**
     * Posiciona um papel CONHECIDO (nunca a incógnita) com o valor curado —
     * a ação que o protocolo de arrastar do enunciado até o diagrama dispara
     * pra qualquer elemento que não seja o "?" (ver EnunciadoInterativo no
     * cliente): a "?" é o único token que carrega os dois papéis, o
     * semântico e o de item desconhecido, e por isso é a única cuja soltura
     * abre o editor de valor em vez de só posicionar. Idempotente: soltar de
     * novo um papel já posicionado não faz nada.
     */
    public synchronized Map<String, Object> posicionarValorConhecido(String papelId) {
        PapelQuantitativo papel = papelPorChave(papelId);
        if (papel == papelDesconhecido) {
            throw new IllegalArgumentException(
                    "papel é a incógnita desta situação, use PROPOR_VALOR_PAPEL: " + papelId);
        }
        if (!papel.estaPreenchido()) {
            ContextoAcao contexto = new ContextoAcao(
                    "sessao.web.local", "usuario.web.local", tentativaId,
                    situacao.getId(), "diagrama.vergnaud.web");
            papel.posicionar(numeroCurado(campoCuradoDoPapel(papel), papel.getChave()),
                    OrigemAcao.ORIGEM_USUARIO, contexto);
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    /**
     * Engata o "?" na caixa da incógnita (protocolo mouse-texto, Main.java) —
     * ação que soltar o token da incógnita do enunciado sobre sua caixa
     * dispara. Só marca o rascunho; a digitação em si (duplo-clique na caixa
     * já engatada) continua indo por PROPOR_VALOR_PAPEL.
     */
    public synchronized Map<String, Object> engatarIncognita(String papelId) {
        if (!papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException(
                    "papel não é a incógnita desta situação: " + papelId);
        }
        if (!papelDesconhecido.estaPreenchido()) {
            incognitaEngatada = true;
        }
        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("estado", estadoAtual());
        return resultado;
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
        // getChaveMensagem() não é texto exibível — são chaves do piloto
        // DiagnosticoErroPapel, deliberadamente não adicionadas a
        // mensagens_*.properties (ver Javadoc da classe). O texto real que o
        // desktop mostra ao aluno (confirmarValorIncognitaAceito em
        // Main.java, sem ajuda adaptativa materializada) é o aviso de limite
        // de tentativas ou a pergunta genérica de confirmação — replicado em
        // MensagemFeedbackIncognitaWeb.
        resultado.put("chave_mensagem", diagnostico.isPresent()
                ? MensagemFeedbackIncognitaWeb.resolver(papelDesconhecido, registro) : null);
        resultado.put("limite_atingido", Boolean.valueOf(
                diagnostico.isPresent() && MensagemFeedbackIncognitaWeb.limiteAtingido(registro)));
        resultado.put("rejeicoes_consecutivas",
                Integer.valueOf(registro.getRejeicoesConsecutivas()));
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    /**
     * Ajusta em ±1 o RASCUNHO da contagem de quadradinhos da incógnita —
     * mesma via de preenchimento que adicionarQuadradinhoAoAgrupamentoInterno/
     * removerQuadradinhoDoAgrupamentoInterno em Main.java, só que aqui o
     * limite curado (obterLimiteSemanticoCuradoDoAgrupamento no desktop) é
     * conferido no servidor sem nunca ser devolvido ao cliente — do
     * contrário a contagem máxima permitida entregaria a resposta.
     *
     * Nunca chama papelDesconhecido.posicionar() aqui: contar não é a mesma
     * ação que confirmar (regra 7 de gerard-consistencia-estado) — e, mais
     * concretamente, posicionar() marcaria o papel como preenchido, tirando-o
     * de ehIncognita() e quebrando a precondição de "exatamente um incógnito"
     * de RelacaoEstruturalComposicao.diagnosticarValorProposto para qualquer
     * confirmação posterior via PROPOR_VALOR_PAPEL (bug encontrado e
     * corrigido em teste manual: confirmar depois de contar devolvia
     * "papelAlvo precisa ser exatamente o único papel incógnito entre os
     * três"). O rascunho só vira valor real quando o aluno confirma pelo
     * editor numérico, que já vem pré-preenchido com esta contagem.
     */
    public synchronized Map<String, Object> ajustarQuadradinho(String papelId, int delta) {
        if (!papelDesconhecido.getChave().equals(papelId)) {
            throw new IllegalArgumentException("papel não é a incógnita desta situação: " + papelId);
        }
        if (delta != 1 && delta != -1) {
            throw new IllegalArgumentException("delta deve ser 1 ou -1");
        }
        if (!papelDesconhecido.estaBloqueadoPorLimiteTentativas()) {
            throw new IllegalStateException(
                    "material concreto só fica disponível após o limite de tentativas ser atingido");
        }
        int novo = contagemMaterialConcreto + delta;

        Map<String, Object> resultado = mapa();
        resultado.put("schema", SCHEMA_RESULTADO);
        if (novo < 0) {
            resultado.put("aceita", Boolean.FALSE);
            resultado.put("limite_atingido", Boolean.FALSE);
            resultado.put("chave_mensagem", null);
            resultado.put("estado", estadoAtual());
            return resultado;
        }
        Integer limiteCurado = numeroCuradoOuNull(
                campoCuradoDoPapel(papelDesconhecido), "limite do material concreto");
        if (limiteCurado != null && novo > limiteCurado.intValue()) {
            resultado.put("aceita", Boolean.FALSE);
            resultado.put("limite_atingido", Boolean.TRUE);
            resultado.put("chave_mensagem", gerard.i18n.ServicoLocalizacao.getInstancia()
                    .texto("ui.tooltip.venn.semanticLimitReached"));
            resultado.put("estado", estadoAtual());
            return resultado;
        }
        contagemMaterialConcreto = novo;
        resultado.put("aceita", Boolean.TRUE);
        resultado.put("limite_atingido", Boolean.FALSE);
        resultado.put("chave_mensagem", null);
        resultado.put("estado", estadoAtual());
        return resultado;
    }

    /**
     * Projeção do papel para o contrato público — igual a projetarPapel,
     * exceto para a incógnita quando o material concreto está disponível:
     * nesse caso mostra o RASCUNHO da contagem de quadradinhos (nunca gravado
     * em papelDesconhecido, ver ajustarQuadradinho) em vez do valor real
     * (que continua vazio até a confirmação).
     */
    private Map<String, Object> projetarPapelParaEstado(PapelQuantitativo papel) {
        if (papel == papelDesconhecido && papelDesconhecido.estaBloqueadoPorLimiteTentativas()) {
            Map<String, Object> item = mapa();
            item.put("id", papel.getChave());
            item.put("nome", papel.getNomeConceitual());
            item.put("conhecido", Boolean.TRUE);
            item.put("valor", Integer.valueOf(contagemMaterialConcreto));
            item.put("engatada", Boolean.FALSE);
            return item;
        }
        return projetarPapel(papel);
    }

    private String campoCuradoDoPapel(PapelQuantitativo papel) {
        if (papel == parte1) return situacao.getQuantidade1();
        if (papel == parte2) return situacao.getQuantidade2();
        if (papel == todo) return situacao.getResultado();
        throw new IllegalStateException("papel incompatível com Composição de Medidas");
    }

    private static Integer numeroCuradoOuNull(String valor, String descricao) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(Integer.parseInt(valor.trim()));
        } catch (NumberFormatException invalido) {
            throw new IllegalStateException("Valor curado inválido em " + descricao + ": " + valor, invalido);
        }
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
        // Nada é pré-posicionado — protocolo de mouse é posicionar: o aluno
        // arrasta cada papel (conhecido ou incógnita) do enunciado até o
        // diagrama (ver posicionarValorConhecido / proporValor).
        relacao = RelacaoEstruturalComposicao.composicaoDeMedidas();
        contagemMaterialConcreto = 0;
        incognitaEngatada = false;
        return estadoAtual();
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
