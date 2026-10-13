package gerard.infraestrutura.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.aplicacao.portabilidade.RotasTentativa;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A tentativa como recurso: a coleção {@code /api/v1/tentativas} (criar = sortear uma situação) e tudo o que mora sob
 * {@code /api/v1/tentativas/{id}} (ver {@link RotasTentativa} para o mapa). Reconhece o caminho, confere que o {id} é
 * a tentativa em curso do visitante e chama o serviço da aplicação; não decide nada do domínio.
 *
 * <p>Semântica HTTP, uma regra por linha da tabela em {@link #reconhecer}:
 * <ul>
 * <li>{@code GET} só lê (seguro, com ETag e {@code If-None-Match}); {@code PUT} grava um estado e repetir dá o mesmo
 * resultado; {@code POST} registra um evento (cada chamada conta) ou cria.</li>
 * <li>Alterar a tentativa exige {@code If-Match} com o ETag da última leitura: 428 se faltar, 412 se outra aba ou
 * chamada a alterou antes. O ETag muda a cada alteração.</li>
 * <li>Erros são {@code application/problem+json} (ver {@link Problema}): 404 tentativa inexistente, 410 substituída por
 * outro sorteio, 409 ação incompatível com o estado, 422 dados que o domínio recusa, 400 JSON inválido, 415 tipo de
 * mídia, 405 com {@code Allow}, 429 com {@code Retry-After}.</li>
 * </ul>
 */
final class RecursosTentativa implements HttpHandler {
    /** Como o servidor responde; a classe do servidor é quem sabe serializar e registrar atividade. */
    interface Respondedor {
        void responder(HttpExchange troca, int status, Map<String, Object> corpo) throws IOException;
    }

    /** Uma operação sobre a tentativa: recebe o serviço, o papel do caminho (ou null) e o corpo JSON. */
    private interface Operacao {
        Map<String, Object> executar(ServicoSorteioAtividade sorteios, String papel, Map<String, Object> corpo);
    }

    /** Uma linha da tabela de rotas: o método do recurso, se altera a tentativa, e o que faz. */
    private static final class Rota {
        final String metodo;
        final boolean altera;
        final Operacao operacao;

        Rota(String metodo, boolean altera, Operacao operacao) {
            this.metodo = metodo;
            this.altera = altera;
            this.operacao = operacao;
        }
    }

    private final SessoesWeb sessoes;
    private final Respondedor respondedor;
    private final int limiteRequisicoes;
    private final long janelaMs;

    RecursosTentativa(SessoesWeb sessoes, Respondedor respondedor) {
        this.sessoes = sessoes;
        this.respondedor = respondedor;
        this.limiteRequisicoes = Integer.parseInt(System.getenv().getOrDefault("LIMITE_REQUISICOES_POR_JANELA", "100"));
        this.janelaMs = Long.parseLong(System.getenv().getOrDefault("JANELA_REQUISICOES_SEGUNDOS", "10")) * 1000L;
    }

    private void problema(HttpExchange troca, Problema problema, String detalhe) throws IOException {
        respondedor.responder(troca, problema.status(),
                problema.corpo(detalhe, troca.getRequestURI().getPath()));
    }

    @Override
    public void handle(HttpExchange troca) throws IOException {
        SessaoWeb sessao = sessoes.de(troca);
        long espera = sessao.segundosParaPoderRequisitar(limiteRequisicoes, janelaMs);
        if (espera > 0) {
            troca.getResponseHeaders().set("Retry-After", String.valueOf(espera));
            problema(troca, Problema.MUITAS_REQUISICOES, null);
            return;
        }
        String caminho = troca.getRequestURI().getPath();
        String resto = caminho.length() > RotasTentativa.BASE.length()
                ? caminho.substring(RotasTentativa.BASE.length()) : "";
        if (resto.startsWith("/")) {
            resto = resto.substring(1);
        }
        if (resto.endsWith("/")) {
            resto = resto.substring(0, resto.length() - 1);
        }
        if (resto.isEmpty()) {
            criarTentativa(troca, sessao);
            return;
        }
        List<String> segmentos = Arrays.asList(resto.split("/"));
        String id = segmentos.get(0);
        Rota rota = reconhecer(segmentos.subList(1, segmentos.size()));
        if (rota == null) {
            problema(troca, Problema.RECURSO_INEXISTENTE, null);
            return;
        }
        if (!rota.metodo.equals(troca.getRequestMethod())) {
            troca.getResponseHeaders().set("Allow", rota.metodo);
            problema(troca, Problema.METODO_NAO_PERMITIDO, null);
            return;
        }
        ServicoSorteioAtividade sorteios = sessao.sorteios();
        if (!id.equals(sorteios.getTentativaId())) {
            problema(troca, sessao.foiSubstituida(id) ? Problema.TENTATIVA_SUBSTITUIDA : Problema.TENTATIVA_INEXISTENTE,
                    null);
            return;
        }
        String papel = segmentos.size() >= 3 && "papeis".equals(segmentos.get(1)) ? segmentos.get(2) : null;
        Map<String, Object> corpo;
        if ("GET".equals(rota.metodo)) {
            corpo = new LinkedHashMap<String, Object>();
        } else {
            if (!tipoDeMidiaAceito(troca)) {
                problema(troca, Problema.TIPO_DE_MIDIA_NAO_SUPORTADO, null);
                return;
            }
            try {
                corpo = corpo(troca);
            } catch (RuntimeException invalido) {
                problema(troca, Problema.CORPO_INVALIDO, null);
                return;
            }
        }
        // A conferência da versão e a alteração são um passo só: duas abas não passam juntas pelo mesmo ETag.
        synchronized (sessao) {
            String etagAtual = sessao.etag(id);
            if (rota.altera) {
                String ifMatch = troca.getRequestHeaders().getFirst("If-Match");
                if (ifMatch == null) {
                    problema(troca, Problema.PRECONDICAO_OBRIGATORIA, null);
                    return;
                }
                if (!etagAtual.equals(ifMatch)) {
                    troca.getResponseHeaders().set("ETag", etagAtual);
                    problema(troca, Problema.VERSAO_DESATUALIZADA, null);
                    return;
                }
            } else if ("GET".equals(rota.metodo)
                    && etagAtual.equals(troca.getRequestHeaders().getFirst("If-None-Match"))) {
                troca.getResponseHeaders().set("ETag", etagAtual);
                troca.sendResponseHeaders(304, -1);
                troca.close();
                return;
            }
            Map<String, Object> resposta;
            try {
                resposta = rota.operacao.executar(sorteios, papel, corpo);
            } catch (RuntimeException erro) {
                responderErroDeDominio(troca, erro);
                return;
            }
            if (rota.altera) {
                sessao.avancarVersao();
            }
            troca.getResponseHeaders().set("ETag", sessao.etag(id));
            respondedor.responder(troca, 200, resposta);
        }
    }

    /** Criar uma tentativa: sortear uma situação (do grupo pedido, ou de um grupo ao acaso). */
    private void criarTentativa(HttpExchange troca, SessaoWeb sessao) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            troca.getResponseHeaders().set("Allow", "POST");
            problema(troca, Problema.METODO_NAO_PERMITIDO, null);
            return;
        }
        if (!tipoDeMidiaAceito(troca)) {
            problema(troca, Problema.TIPO_DE_MIDIA_NAO_SUPORTADO, null);
            return;
        }
        Map<String, Object> corpo;
        try {
            corpo = corpo(troca);
        } catch (RuntimeException invalido) {
            problema(troca, Problema.CORPO_INVALIDO, null);
            return;
        }
        Object grupo = corpo.get("grupo_sorteio");
        if (grupo != null && !"MEDIDAS".equals(grupo) && !"RELACOES".equals(grupo)) {
            problema(troca, Problema.DADOS_INVALIDOS, "grupo_sorteio deve ser MEDIDAS ou RELACOES");
            return;
        }
        synchronized (sessao) {
            ServicoSorteioAtividade sorteios = sessao.sorteios();
            String anterior = sorteios.getTentativaId();
            Map<String, Object> estado;
            try {
                estado = "MEDIDAS".equals(grupo) ? sorteios.sortearMedidas()
                        : "RELACOES".equals(grupo) ? sorteios.sortearRelacoes() : sorteios.estadoInicial();
            } catch (IllegalStateException semSituacao) {
                problema(troca, Problema.SERVICO_INDISPONIVEL, semSituacao.getMessage());
                return;
            }
            sessao.trocarDeTentativa(anterior);
            String id = String.valueOf(estado.get("tentativa_id"));
            troca.getResponseHeaders().set("Location", RotasTentativa.tentativa(id));
            troca.getResponseHeaders().set("ETag", sessao.etag(id));
            respondedor.responder(troca, 201, estado);
        }
    }

    private void responderErroDeDominio(HttpExchange troca, RuntimeException erro) throws IOException {
        if (erro instanceof IllegalStateException) {
            problema(troca, Problema.ACAO_INCOMPATIVEL_COM_O_ESTADO, erro.getMessage());
        } else if (erro instanceof IllegalArgumentException || erro instanceof ClassCastException
                || erro instanceof NullPointerException) {
            problema(troca, Problema.DADOS_INVALIDOS, erro instanceof IllegalArgumentException
                    ? erro.getMessage() : "campo ausente ou com tipo errado");
        } else {
            System.err.println("Falha inesperada em " + troca.getRequestURI().getPath() + ": " + erro);
            erro.printStackTrace();
            problema(troca, Problema.ERRO_INTERNO, null);
        }
    }

    /** Com corpo, tem que ser JSON; sem corpo (POST de ação sem dados), qualquer um serve. */
    private static boolean tipoDeMidiaAceito(HttpExchange troca) {
        String tipo = troca.getRequestHeaders().getFirst("Content-Type");
        if (tipo != null && tipo.toLowerCase().startsWith("application/json")) {
            return true;
        }
        String comprimento = troca.getRequestHeaders().getFirst("Content-Length");
        return tipo == null && (comprimento == null || "0".equals(comprimento));
    }

    /** As linhas da tabela: caminho abaixo de /tentativas/{id}, método, se altera a tentativa, e o que faz. */
    private static Rota reconhecer(List<String> s) {
        int n = s.size();
        if (n == 0) {
            return new Rota("GET", false, (sv, p, c) -> sv.consultarEstado());
        }
        if (n == 1) {
            switch (s.get(0)) {
                case "classificacao":
                    return new Rota("POST", true, (sv, p, c) -> sv.escolherCategoria(String.valueOf(c.get("categoria"))));
                case "operacoes":
                    return new Rota("POST", true, (sv, p, c) -> {
                        if (!sv.possuiAtividadeEscolhaOperacaoAtiva()) {
                            throw new IllegalStateException("a situação atual não possui escolha de operação implementada");
                        }
                        return sv.escolherOperacao(String.valueOf(c.get("seletor")), String.valueOf(c.get("operacao")));
                    });
                case "conclusao":
                    return new Rota("POST", true, (sv, p, c) -> sv.escolherConclusao(String.valueOf(c.get("escolha"))));
                case "ajudas":
                    return new Rota("POST", true, (sv, p, c) ->
                            sv.ajudaContextual(String.valueOf(c.get("area")), String.valueOf(c.get("intencao"))));
                case "reinicio":
                    return new Rota("POST", true, (sv, p, c) -> sv.reiniciarAtividadeAtual());
                case "idioma":
                    return new Rota("PUT", true, (sv, p, c) -> sv.trocarIdiomaSituacao(String.valueOf(c.get("codigo"))));
                default:
                    return null;
            }
        }
        if (n == 2 && "classificacao".equals(s.get(0)) && "confirmacao".equals(s.get(1))) {
            return new Rota("POST", true, (sv, p, c) -> sv.confirmarCategoria(Boolean.TRUE.equals(c.get("concordou"))));
        }
        if (n >= 3 && "papeis".equals(s.get(0))) {
            String recurso = String.join("/", s.subList(2, n));
            switch (recurso) {
                case "posicionamento":
                    return new Rota("POST", true, (sv, p, c) -> {
                        Object origem = c.get("origem_papel_id");
                        return sv.soltarNumero(p, origem == null ? null : String.valueOf(origem));
                    });
                case "valor":
                    return new Rota("POST", true, (sv, p, c) -> sv.proporValor(p, ((Number) c.get("valor")).intValue()));
                case "valor/confirmacao":
                    return new Rota("POST", true, (sv, p, c) -> sv.responderConfirmacaoValor(p,
                            Boolean.TRUE.equals(c.get("confirmou")), ((Number) c.get("valor")).intValue()));
                case "quadradinhos":
                    return new Rota("POST", true, (sv, p, c) -> sv.ajustarQuadradinho(p, ((Number) c.get("delta")).intValue()));
                case "sinal":
                    return new Rota("POST", true, (sv, p, c) -> sv.escolherSinalNumeroRelativo(p, String.valueOf(c.get("sinal"))));
                case "eixo/visibilidade":
                    return new Rota("PUT", true, (sv, p, c) ->
                            Boolean.TRUE.equals(c.get("visivel")) ? sv.revelarEixo(p) : sv.ocultarEixo(p));
                case "eixo/projecao":
                    // Cálculo puro: não grava nada na tentativa, então não exige nem avança a versão.
                    return new Rota("POST", false, (sv, p, c) -> sv.projetarAlteracaoEixo(p, ((Number) c.get("valor")).intValue()));
                default:
                    return null;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> corpo(HttpExchange troca) throws IOException {
        String texto = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).trim();
        if (texto.isEmpty()) {
            return new LinkedHashMap<String, Object>();
        }
        Object analisado = AnalisadorJsonSimples.analisar(texto);
        if (!(analisado instanceof Map)) {
            throw new IllegalArgumentException("o corpo precisa ser um objeto JSON");
        }
        return (Map<String, Object>) analisado;
    }
}
