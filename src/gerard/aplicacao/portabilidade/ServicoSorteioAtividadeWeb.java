package gerard.aplicacao.portabilidade;

import gerard.aplicacao.ContextoCarregamentoAtividade;
import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas.Grupo;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.DecisaoExibicaoPaineisEixo;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.servico.PosicaoSeletorOperacaoDiagrama;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.idioma.IdiomaInterface;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoClassificacaoCategoria;
import gerard.dominio.campoaditivo.TentativaClassificacaoCategoriaAditiva;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Coordena o sorteio web sem conhecer HTTP ou qualquer tecnologia de tela. */
public final class ServicoSorteioAtividadeWeb {
    public static final String SCHEMA = "gerard.atividade-web.estado.v1";

    private final PoliticaSorteioSituacoesAditivas politica;
    private final FachadaCarregamentoAtividade carregamento;
    private final Random aleatorio;
    private final IdiomaInterface idioma;
    private ContextoCarregamentoAtividade contextoAtual;
    private Grupo grupoAtual;
    private TentativaClassificacaoCategoriaAditiva tentativaClassificacao;
    private TipoSituacaoAditiva categoriaSelecionada;
    private String questionamento;
    private ServicoAtividadeWeb atividadeModelagem;
    private ServicoAtividadeWebEscolhaOperacao atividadeEscolhaOperacao;

    public ServicoSorteioAtividadeWeb() {
        this(new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(
                        new RepositorioSituacoesAditivas(),
                        new CatalogoDefinicoesAditivas(),
                        new ConstrutorResultadoCurado()),
                new Random(), IdiomaInterface.PORTUGUES);
    }

    public ServicoSorteioAtividadeWeb(PoliticaSorteioSituacoesAditivas politica,
            FachadaCarregamentoAtividade carregamento, Random aleatorio,
            IdiomaInterface idioma) {
        if (politica == null || carregamento == null || aleatorio == null || idioma == null) {
            throw new IllegalArgumentException("dependências do sorteio são obrigatórias");
        }
        this.politica = politica;
        this.carregamento = carregamento;
        this.aleatorio = aleatorio;
        this.idioma = idioma;
    }

    /**
     * Estado a exibir na carga inicial da página — nunca um diagrama pronto:
     * se ainda não há nenhuma situação sorteada nesta sessão, sorteia uma
     * (grupo aleatório) e devolve o estado de classificação recém-criado,
     * que por si só já esconde a cena até a categoria ser acertada (ver
     * projetarEstado/revelar). Chamadas seguintes apenas reprojetam o estado
     * corrente, sem sortear de novo.
     */
    public synchronized Map<String, Object> estadoInicial() {
        if (contextoAtual == null) {
            return sortear(aleatorio.nextBoolean() ? Grupo.MEDIDAS : Grupo.RELACOES);
        }
        return projetarEstado();
    }

    public synchronized Map<String, Object> sortearMedidas() {
        return sortear(Grupo.MEDIDAS);
    }

    public synchronized Map<String, Object> sortearRelacoes() {
        return sortear(Grupo.RELACOES);
    }

    private Map<String, Object> sortear(Grupo grupo) {
        TipoSituacaoAditiva categoria = politica.sortearCategoria(grupo, aleatorio);
        ContextoCarregamentoAtividade contexto = carregamento.carregarNova(idioma, categoria);
        if (!contexto.possuiSituacaoExibivel()) {
            throw new IllegalStateException("não há situação curada exibível para " + categoria.name());
        }
        contextoAtual = contexto;
        grupoAtual = grupo;
        tentativaClassificacao = new TentativaClassificacaoCategoriaAditiva(
                contexto.getSituacao());
        categoriaSelecionada = null;
        questionamento = null;
        atividadeModelagem = null;
        atividadeEscolhaOperacao = null;
        return projetarEstado();
    }

    public synchronized Map<String, Object> escolherCategoria(String categoria) {
        exigirClassificacaoAtiva();
        TipoSituacaoAditiva escolhida = TipoSituacaoAditiva.valueOf(categoria);
        RegistroAcaoClassificacaoCategoria registro = tentativaClassificacao
                .avaliarEscolha(escolhida, contextoEscolha(escolhida));
        if (registro.foiCorreta()) {
            categoriaSelecionada = escolhida;
            if (escolhida == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
                atividadeModelagem = new ServicoAtividadeWebComposicao(
                        "tentativa.web." + contextoAtual.getSituacao().getId(),
                        contextoAtual.getSituacao());
            } else if (escolhida == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
                atividadeModelagem = new ServicoAtividadeWebTransformacaoMedidas(
                        "tentativa.web." + contextoAtual.getSituacao().getId(),
                        contextoAtual.getSituacao());
            } else if (escolhida == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
                atividadeModelagem = new ServicoAtividadeWebComparacaoMedidas(
                        "tentativa.web." + contextoAtual.getSituacao().getId(),
                        contextoAtual.getSituacao());
            } else if (escolhida == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO
                    && contextoAtual.possuiSituacaoRicaValida()) {
                atividadeModelagem =
                        new ServicoAtividadeWebTransformacaoRelacaoRica(
                                "tentativa.web."
                                        + contextoAtual.getSituacao().getId(),
                                contextoAtual.getResultadoSituacaoRica()
                                        .getSituacaoOuFalhar());
            } else if (escolhida == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
                atividadeEscolhaOperacao =
                        new ServicoAtividadeWebComposicaoTransformacoes(
                                "tentativa.web." + contextoAtual.getSituacao().getId(),
                                contextoAtual.getSituacao());
            } else if (escolhida == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
                atividadeEscolhaOperacao =
                        new ServicoAtividadeWebComposicaoRelacoes(
                                "tentativa.web." + contextoAtual.getSituacao().getId(),
                                contextoAtual.getSituacao());
            }
        }
        questionamento = tentativaClassificacao.aguardaConfirmacao()
                ? "A definição da categoria " + escolhida.name()
                        + " se aplica a esta situação?"
                : null;
        return resultadoClassificacao(registro);
    }

    public synchronized boolean possuiAtividadeModelagemAtiva() {
        return atividadeModelagem != null;
    }

    public synchronized boolean possuiAtividadeEscolhaOperacaoAtiva() {
        return atividadeEscolhaOperacao != null;
    }

    public synchronized Map<String, Object> proporValor(String papelId, int valor) {
        if (atividadeModelagem == null) {
            throw new IllegalStateException("a situação atual não possui modelagem web implementada");
        }
        Map<String, Object> resultado = atividadeModelagem.proporValor(papelId, valor);
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    /**
     * Posiciona um papel conhecido (nunca a incógnita) — a ação que soltar
     * qualquer elemento não-incógnita do enunciado sobre sua caixa dispara
     * (protocolo de mouse é posicionar). Só existe para Composição de
     * Medidas hoje (piloto).
     */
    public synchronized Map<String, Object> posicionarValorConhecido(String papelId) {
        if (!(atividadeModelagem instanceof ServicoAtividadeWebComposicao)) {
            throw new IllegalStateException(
                    "a situação atual não possui posicionamento de papel conhecido implementado");
        }
        Map<String, Object> resultado = ((ServicoAtividadeWebComposicao) atividadeModelagem)
                .posicionarValorConhecido(papelId);
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    /**
     * Ajusta a contagem de quadradinhos do material concreto (AG_EMCME) —
     * só existe para Composição de Medidas hoje (piloto); outras categorias
     * ainda não têm representação complementar portada.
     */
    public synchronized Map<String, Object> ajustarQuadradinho(String papelId, int delta) {
        if (!(atividadeModelagem instanceof ServicoAtividadeWebComposicao)) {
            throw new IllegalStateException(
                    "a situação atual não possui material concreto implementado");
        }
        Map<String, Object> resultado = ((ServicoAtividadeWebComposicao) atividadeModelagem)
                .ajustarQuadradinho(papelId, delta);
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    public synchronized Map<String, Object> escolherOperacao(String seletor, String operacao) {
        if (atividadeEscolhaOperacao == null) {
            throw new IllegalStateException(
                    "a situação atual não possui escolha de operação implementada");
        }
        Map<String, Object> resultado = atividadeEscolhaOperacao.escolherOperacao(seletor, operacao);
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    public synchronized Map<String, Object> reiniciarAtividadeAtual() {
        if (atividadeModelagem == null && atividadeEscolhaOperacao == null) {
            throw new IllegalStateException("a situação atual não possui modelagem web implementada");
        }
        if (atividadeModelagem != null) {
            atividadeModelagem.reiniciar();
        }
        if (atividadeEscolhaOperacao != null) {
            atividadeEscolhaOperacao.reiniciar();
        }
        return projetarEstado();
    }

    public synchronized Map<String, Object> confirmarCategoria(boolean concordou) {
        exigirClassificacaoAtiva();
        RegistroAcaoClassificacaoCategoria registro = tentativaClassificacao
                .avaliarConfirmacaoCategoriaDivergente(concordou,
                        contextoConfirmacao(concordou));
        questionamento = null;
        return resultadoClassificacao(registro);
    }

    private Map<String, Object> resultadoClassificacao(
            RegistroAcaoClassificacaoCategoria registro) {
        Map<String, Object> resultado = mapa();
        resultado.put("schema", "gerard.atividade-web.resultado-classificacao.v1");
        resultado.put("action_id", registro.getActionId());
        resultado.put("correta", Boolean.valueOf(registro.foiCorreta()));
        resultado.put("diagnostico", registro.getTipoDiagnosticoFactual().length() == 0
                ? null : registro.getTipoDiagnosticoFactual());
        resultado.put("desfecho", registro.getDesfecho().name());
        resultado.put("rejeicoes_consecutivas",
                Integer.valueOf(registro.getRejeicoesConsecutivas()));
        resultado.put("estado", projetarEstado());
        return resultado;
    }

    private void exigirClassificacaoAtiva() {
        if (contextoAtual == null || tentativaClassificacao == null) {
            throw new IllegalStateException("nenhuma situação sorteada aguarda classificação");
        }
    }

    private ContextoAcaoInstrumental contextoEscolha(TipoSituacaoAditiva escolhida) {
        return new ContextoAcaoInstrumental("Classificar situação-problema",
                "Selecionar um ícone de categoria", "Faixa de categorias web",
                "Classificar a estrutura da situação",
                TentativaClassificacaoCategoriaAditiva.ALVO_ESCOLHA,
                "SELECAO_CATEGORIA", "categoria_escolhida=" + escolhida.name(),
                "Categoria selecionada para validação", Collections.<String>emptyList());
    }

    private ContextoAcaoInstrumental contextoConfirmacao(boolean concordou) {
        return new ContextoAcaoInstrumental("Confirmar categoria escolhida",
                "Responder ao questionamento", "Diálogo web de confirmação",
                "Reconhecer ou reafirmar classificação divergente",
                TentativaClassificacaoCategoriaAditiva.ALVO_CONFIRMACAO,
                "CONFIRMACAO_CATEGORIA_DIVERGENTE", "concordou=" + concordou,
                "Resposta registrada", Collections.<String>emptyList());
    }

    private Map<String, Object> projetarEstado() {
        Map<String, Object> estado = projetar(contextoAtual, grupoAtual);
        estado.put("categoria_selecionada",
                categoriaSelecionada == null ? null : categoriaSelecionada.name());
        if (tentativaClassificacao.aguardaConfirmacao()) {
            estado.put("modo", "AGUARDANDO_CONFIRMACAO_CATEGORIA");
            estado.put("questionamento", questionamento);
            estado.put("acoes_disponiveis",
                    AcoesDisponiveisAtividadeWeb.confirmacaoCategoria());
        } else if (categoriaSelecionada != null) {
            estado.put("modo", "CATEGORIA_CLASSIFICADA");
            if (atividadeModelagem != null) {
                Map<String, Object> modelagem = atividadeModelagem.estadoAtual();
                estado.put("modelagem", modelagem);
                estado.put("concluida", modelagem.get("concluida"));
                estado.put("acoes_disponiveis", modelagem.get("acoes_disponiveis"));
            } else if (atividadeEscolhaOperacao != null) {
                Map<String, Object> modelagem = atividadeEscolhaOperacao.estadoAtual();
                estado.put("modelagem", modelagem);
                estado.put("concluida", modelagem.get("concluida"));
                estado.put("acoes_disponiveis", modelagem.get("acoes_disponiveis"));
            } else {
                estado.put("acoes_disponiveis", AcoesDisponiveisAtividadeWeb.sorteios());
            }
        } else if (tentativaClassificacao.estaEncerrada()) {
            estado.put("modo", "REEXPLICACAO_CATEGORIA");
            estado.put("categoria_revelada",
                    tentativaClassificacao.getCategoriaEsperada().name());
            estado.put("acoes_disponiveis", AcoesDisponiveisAtividadeWeb.sorteios());
        } else {
            estado.put("acoes_disponiveis",
                    AcoesDisponiveisAtividadeWeb.classificacaoCategoria());
        }
        boolean revelar = categoriaSelecionada != null
                || tentativaClassificacao.estaEncerrada();
        if (revelar) {
            estado.put("categoria", contextoAtual.getSituacao().getTipo().name());
            estado.put("cena", projetarCena(contextoAtual,
                    listaDeAcoes(estado.get("acoes_disponiveis"))));
            estado.put("elementos_texto", projetarElementosTexto(contextoAtual));
        } else {
            estado.remove("categoria");
            estado.remove("subtipo");
            estado.remove("termo_desconhecido");
            estado.remove("representacao_visual");
            estado.remove("operacao_relacao");
            estado.remove("operacao_estado_transformacao");
            estado.remove("papeis");
            estado.remove("diagrama");
            estado.remove("curadoria");
            estado.remove("elementos_texto");
        }
        // A curadoria bruta contém a resposta da incógnita e nunca integra o
        // contrato público do participante, mesmo após a classificação.
        estado.remove("curadoria");
        return estado;
    }

    /**
     * Elementos do enunciado marcados/arrastáveis após a categoria ser
     * aceita — portação de inicializarElementosTexto/
     * vincularPapeisSemanticosAosElementosTexto (Main.java), via
     * SegmentadorTextoSemantico (portátil, sem Swing). Lista completa e
     * ordenada de tokens (não só os vinculados a um papel) para o cliente
     * poder reconstruir o enunciado inteiro juntando com espaço — o
     * tokenizador usa \S+, então isso corresponde exatamente ao texto
     * original.
     */
    private static List<Object> projetarElementosTexto(ContextoCarregamentoAtividade contexto) {
        List<gerard.interpretacao.modelo.SegmentoTextoSemantico> segmentos =
                gerard.interpretacao.modelo.SegmentadorTextoSemantico.segmentar(
                        contexto.getEnunciadoExibido(), contexto.getInterpretacao());
        List<Object> resultado = new ArrayList<Object>();
        for (gerard.interpretacao.modelo.SegmentoTextoSemantico segmento : segmentos) {
            Map<String, Object> item = mapa();
            item.put("valor", segmento.getValor());
            item.put("papel_id", segmento.possuiVinculoSemantico()
                    ? segmento.getChavePapelSemantico() : null);
            item.put("incognita", Boolean.valueOf(segmento.representaIncognitaOriginal()));
            resultado.add(item);
        }
        return resultado;
    }

    private static Map<String, Object> projetarCena(
            ContextoCarregamentoAtividade contexto, List<Object> acoes) {
        CenaDiagramaAditivo cena = new GeradorCenaDiagramaAditivo().gerar(
                contexto.getSituacao().getTipo(), new AreaDiagrama(0, 0, 840, 480),
                contexto.getDefinicao(), new int[] {0, 0, 0});
        Map<String, Object> resultado = mapa();
        resultado.put("titulo", cena.getTitulo());
        resultado.put("descricao", cena.getDescricao());
        List<Object> figuras = new ArrayList<Object>();
        int indice = 0;
        for (FiguraDiagrama figura : cena.getFiguras()) {
            Map<String, Object> item = mapa();
            item.put("id", "figura." + indice++);
            item.put("tipo", figura.getTipo().name());
            item.put("x", Integer.valueOf(figura.getX()));
            item.put("y", Integer.valueOf(figura.getY()));
            item.put("largura", Integer.valueOf(figura.getLargura()));
            item.put("altura", Integer.valueOf(figura.getAltura()));
            item.put("rotulo", figura.getRotulo());
            item.put("posicao_rotulo", figura.getPosicaoRotulo().name());
            item.put("exibir_lupa", Boolean.valueOf(figura.isExibirLupa()));
            item.put("chave_papel_semantico", figura.getChavePapelSemantico());
            SemanticaCuradaSituacao.PapelCurado papel =
                    SemanticaCuradaSituacao.buscar(contexto.getSituacao(), null,
                            figura.getChavePapelSemantico());
            item.put("subtitulo", papel == null ? "" : papel.getParticipante());
            item.put("lupa_habilitada", Boolean.FALSE);
            item.put("interacoes_permitidas", projetarInteracoesPermitidas(
                    acoes, figura.getChavePapelSemantico()));
            figuras.add(item);
        }
        List<Object> conectores = new ArrayList<Object>();
        for (ConectorDiagrama conector : cena.getConectores()) {
            Map<String, Object> item = mapa();
            item.put("tipo", conector.getTipo().name());
            item.put("x1", Integer.valueOf(conector.getX1()));
            item.put("y1", Integer.valueOf(conector.getY1()));
            item.put("x2", Integer.valueOf(conector.getX2()));
            item.put("y2", Integer.valueOf(conector.getY2()));
            item.put("legenda", conector.getLegenda());
            if (conector.temAlvo()) {
                item.put("x_alvo", Integer.valueOf(conector.getXAlvo()));
                item.put("y_alvo", Integer.valueOf(conector.getYAlvo()));
            }
            conectores.add(item);
        }
        resultado.put("figuras", figuras);
        resultado.put("conectores", conectores);
        Map<String, Object> seletorOperacao = projetarSeletorOperacao(
                contexto.getSituacao().getTipo(), cena);
        if (seletorOperacao != null) {
            resultado.put("seletor_operacao", seletorOperacao);
        }
        resultado.put("viewport", projetarViewport(cena, seletorOperacao));
        // Decisão agregada da cena (não por figura): existe pelo menos um
        // papel com lupa, logo os painéis de eixo revelados por ela podem
        // ser oferecidos. Mesma regra usada pelo adaptador Swing (ver
        // DecisaoExibicaoPaineisEixo) — um único lugar decide isso, para
        // não obrigar um futuro consumidor web a recalculá-la sozinho.
        // Não decide qual mecanismo de eixo mostrar nem substitui
        // "lupa_habilitada" por figura, que segue como placeholder em
        // aberto (ver LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md).
        resultado.put("paineis_eixo_disponiveis",
                Boolean.valueOf(DecisaoExibicaoPaineisEixo.existeAlgumComLupa(
                        cena.getFiguras())));
        return resultado;
    }

    /**
     * Centro(s) onde o seletor Soma/Subtração se posiciona sobre o diagrama
     * — sempre os dois pontos de Composição de Transformações (mesmo antes
     * da segunda etapa liberar, para o viewport já reservar o espaço e o
     * diagrama não pular de tamanho quando ela aparecer) ou o ponto único de
     * Composição de Relações. Deriva de figuras/conectores já presentes na
     * cena (PosicaoSeletorOperacaoDiagrama), nunca de coordenada própria —
     * mesma fonte usada pelo cálculo do viewport logo abaixo.
     */
    private static Map<String, Object> projetarSeletorOperacao(
            TipoSituacaoAditiva tipo, CenaDiagramaAditivo cena) {
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {
            FiguraDiagrama t1 = buscarFigura(cena, "papel.transformacao1");
            FiguraDiagrama t2 = buscarFigura(cena, "papel.transformacao2");
            FiguraDiagrama tr = buscarFigura(cena, "papel.transformacaoFinal");
            if (t1 == null || t2 == null || tr == null) {
                return null;
            }
            Map<String, Object> resultado = mapa();
            resultado.put("entre_transformacoes", projetarCentro(
                    PosicaoSeletorOperacaoDiagrama.entreTransformacoes(t1, t2)));
            resultado.put("entre_estado_transformacao", projetarCentro(
                    PosicaoSeletorOperacaoDiagrama.entreEstadoETransformacao(tr)));
            return resultado;
        }
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            if (cena.getConectores().isEmpty()) {
                return null;
            }
            Map<String, Object> resultado = mapa();
            resultado.put("relacao", projetarCentro(
                    PosicaoSeletorOperacaoDiagrama.relacao(cena.getConectores().get(0))));
            return resultado;
        }
        return null;
    }

    private static FiguraDiagrama buscarFigura(CenaDiagramaAditivo cena, String chave) {
        for (FiguraDiagrama figura : cena.getFiguras()) {
            if (chave.equals(figura.getChavePapelSemantico())) {
                return figura;
            }
        }
        return null;
    }

    private static Map<String, Object> projetarCentro(PosicaoSeletorOperacaoDiagrama.Centro centro) {
        Map<String, Object> item = mapa();
        item.put("cx", Integer.valueOf(centro.cx));
        item.put("cy", Integer.valueOf(centro.cy));
        return item;
    }

    private static Map<String, Object> projetarViewport(CenaDiagramaAditivo cena,
            Map<String, Object> seletorOperacao) {
        double minimoX = Double.POSITIVE_INFINITY;
        double minimoY = Double.POSITIVE_INFINITY;
        double maximoX = Double.NEGATIVE_INFINITY;
        double maximoY = Double.NEGATIVE_INFINITY;
        for (FiguraDiagrama figura : cena.getFiguras()) {
            minimoX = Math.min(minimoX, figura.getX());
            minimoY = Math.min(minimoY, figura.getY());
            maximoX = Math.max(maximoX, figura.getX() + figura.getLargura());
            maximoY = Math.max(maximoY, figura.getY() + figura.getAltura());
        }
        for (ConectorDiagrama conector : cena.getConectores()) {
            minimoX = Math.min(minimoX, Math.min(conector.getX1(), conector.getX2()));
            minimoY = Math.min(minimoY, Math.min(conector.getY1(), conector.getY2()));
            maximoX = Math.max(maximoX, Math.max(conector.getX1(), conector.getX2()));
            maximoY = Math.max(maximoY, Math.max(conector.getY1(), conector.getY2()));
            if (conector.temAlvo()) {
                minimoX = Math.min(minimoX, conector.getXAlvo());
                minimoY = Math.min(minimoY, conector.getYAlvo());
                maximoX = Math.max(maximoX, conector.getXAlvo());
                maximoY = Math.max(maximoY, conector.getYAlvo());
            }
        }
        // O seletor Soma/Subtração é desenhado pelo cliente por cima da cena
        // (ver PosicaoSeletorOperacaoDiagrama), fora das figuras/conectores
        // acima — sem isto, a área calculada não sobra espaço para ele e o
        // cliente teria que recalcular/expandir o viewport por conta própria.
        if (seletorOperacao != null) {
            int meiaLargura = PosicaoSeletorOperacaoDiagrama.meiaLargura();
            int raio = PosicaoSeletorOperacaoDiagrama.raioBotao();
            int alturaReservadaAbaixo = raio + 28 + 100; // rótulos (2 linhas) + caixa de explicação
            for (Object valor : seletorOperacao.values()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> centro = (Map<String, Object>) valor;
                int cx = ((Integer) centro.get("cx")).intValue();
                int cy = ((Integer) centro.get("cy")).intValue();
                minimoX = Math.min(minimoX, cx - meiaLargura);
                maximoX = Math.max(maximoX, cx + meiaLargura);
                minimoY = Math.min(minimoY, cy - raio);
                maximoY = Math.max(maximoY, cy + alturaReservadaAbaixo);
            }
        }
        if (!Double.isFinite(minimoX)) {
            minimoX = 0; minimoY = 0; maximoX = 840; maximoY = 480;
        }
        double larguraConteudo = Math.max(1, maximoX - minimoX);
        double alturaConteudo = Math.max(1, maximoY - minimoY);
        double margemX = larguraConteudo / 6.0;
        double margemY = alturaConteudo / 6.0;
        Map<String, Object> viewport = mapa();
        viewport.put("x", Double.valueOf(minimoX - margemX));
        viewport.put("y", Double.valueOf(minimoY - margemY));
        viewport.put("largura", Double.valueOf(larguraConteudo + 2 * margemX));
        viewport.put("altura", Double.valueOf(alturaConteudo + 2 * margemY));
        return viewport;
    }

    private static Map<String, Object> projetar(ContextoCarregamentoAtividade contexto,
            Grupo grupo) {
        SituacaoProblemaAditiva s = contexto.getSituacao();
        if (s.getTipo() == null) {
            throw new IllegalStateException("situação curada sem categoria");
        }
        ResultadoInterpretacao interpretacao = contexto.getInterpretacao();
        if (interpretacao == null || interpretacao.getPapeis().isEmpty()) {
            throw new IllegalStateException("situação curada sem estrutura semântica: " + s.getId());
        }

        Map<String, Object> estado = mapa();
        estado.put("schema", SCHEMA);
        estado.put("modo", "CLASSIFICACAO_CATEGORIA");
        estado.put("grupo_sorteio", grupo.name());
        estado.put("situacao_id", s.getId());
        estado.put("situacao_grupo_id", s.getSituacaoGrupoId());
        estado.put("situacao_validada", Boolean.valueOf(s.isValidada()));
        estado.put("idioma", s.getCodigoIdioma());
        estado.put("fonte", s.getFonte());
        estado.put("contexto", s.getContexto());
        estado.put("categoria", s.getTipo().name());
        estado.put("categoria_selecionada", null);
        estado.put("enunciado", contexto.getEnunciadoExibido());
        estado.put("subtipo", s.getSubtipo());
        estado.put("termo_desconhecido", s.getTermoDesconhecido());
        estado.put("representacao_visual", s.getRepresentacaoVisual());
        estado.put("operacao_relacao", vazioComoNulo(s.getOperacaoRelacao()));
        estado.put("operacao_estado_transformacao",
                vazioComoNulo(s.getOperacaoEstadoTransformacao()));
        estado.put("papeis", projetarPapeis(interpretacao));
        estado.put("diagrama", projetarDiagrama(contexto.getDefinicao()));
        estado.put("curadoria", projetarCuradoria(s));
        estado.put("concluida", Boolean.FALSE);
        estado.put("acoes_disponiveis", AcoesDisponiveisAtividadeWeb.classificacaoCategoria());
        return estado;
    }

    private static List<Object> projetarPapeis(ResultadoInterpretacao interpretacao) {
        List<Object> papeis = new ArrayList<Object>();
        for (PapelElementoInterpretado papel : interpretacao.getPapeis()) {
            Map<String, Object> item = mapa();
            item.put("id", papel.getChavePapel());
            item.put("valor", papel.isConhecido() ? papel.getElemento() : null);
            item.put("conhecido", Boolean.valueOf(papel.isConhecido()));
            papeis.add(item);
        }
        return papeis;
    }

    private static Map<String, Object> projetarDiagrama(DefinicaoDiagramaAditivo d) {
        if (d == null) throw new IllegalStateException("definição de diagrama ausente");
        Map<String, Object> diagrama = mapa();
        diagrama.put("titulo", d.getTitulo());
        diagrama.put("rotulos", java.util.Arrays.asList(
                d.getRotulo1(), d.getRotulo2(), d.getRotulo3()));
        return diagrama;
    }

    private static Map<String, Object> projetarCuradoria(SituacaoProblemaAditiva s) {
        Map<String, Object> c = mapa();
        c.put("estado_inicial", vazioComoNulo(s.getEstadoInicial()));
        c.put("estado_intermediario", vazioComoNulo(s.getEstadoIntermediario()));
        c.put("transformacao", vazioComoNulo(s.getTransformacao()));
        c.put("sinal_transformacao", vazioComoNulo(s.getSinalTransformacao()));
        c.put("estado_final", vazioComoNulo(s.getEstadoFinal()));
        c.put("quantidade_1", vazioComoNulo(s.getQuantidade1()));
        c.put("quantidade_2", vazioComoNulo(s.getQuantidade2()));
        c.put("resultado", vazioComoNulo(s.getResultado()));
        c.put("referido", vazioComoNulo(s.getReferido()));
        c.put("referendo", vazioComoNulo(s.getReferendo()));
        c.put("valor_relativo", vazioComoNulo(s.getValorRelativo()));
        c.put("sinal_valor_relativo", vazioComoNulo(s.getSinalValorRelativo()));
        return c;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> listaDeAcoes(Object valor) {
        return valor instanceof List
                ? (List<Object>) valor : Collections.<Object>emptyList();
    }

    @SuppressWarnings("unchecked")
    private static List<Object> projetarInteracoesPermitidas(
            List<Object> acoes, String chavePapel) {
        List<Object> interacoes = new ArrayList<Object>();
        if (chavePapel == null || acoes == null) {
            return interacoes;
        }
        for (Object valor : acoes) {
            if (!(valor instanceof Map)) continue;
            Map<String, Object> acao = (Map<String, Object>) valor;
            String id = String.valueOf(acao.get("id"));
            String tipoInteracao;
            if ("PROPOR_VALOR_PAPEL".equals(id)) {
                tipoInteracao = "EDITAR_VALOR";
            } else if ("POSICIONAR_CONHECIDO".equals(id)) {
                tipoInteracao = "POSICIONAR_CONHECIDO";
            } else {
                continue;
            }
            Object corpoValor = acao.get("corpo");
            if (!(corpoValor instanceof Map)) continue;
            Map<String, Object> corpo = (Map<String, Object>) corpoValor;
            if (!chavePapel.equals(corpo.get("papel_id"))) continue;
            Map<String, Object> interacao = mapa();
            interacao.put("tipo", tipoInteracao);
            interacao.put("acao_id", id);
            interacao.put("fase_envio", "PROPOR_VALOR_PAPEL".equals(id) ? "CONFIRMACAO" : "IMEDIATA");
            interacao.put("papel_id", chavePapel);
            interacoes.add(interacao);
        }
        return interacoes;
    }

    private static String vazioComoNulo(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor;
    }

    private static Map<String, Object> mapa() {
        return new LinkedHashMap<String, Object>();
    }
}
