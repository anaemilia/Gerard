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
 */
final class RecursosTentativa implements HttpHandler {
    /** Como o servidor responde; a classe do servidor é quem sabe serializar e registrar atividade. */
    interface Respondedor {
        void responder(HttpExchange troca, int status, Map<String, Object> corpo) throws IOException;

        Map<String, Object> erro(String mensagem);
    }

    /** Uma operação sobre a tentativa: recebe o serviço, o papel do caminho (ou null) e o corpo JSON. */
    private interface Operacao {
        Map<String, Object> executar(ServicoSorteioAtividade sorteios, String papel, Map<String, Object> corpo);
    }

    private final SessoesWeb sessoes;
    private final Respondedor respondedor;

    RecursosTentativa(SessoesWeb sessoes, Respondedor respondedor) {
        this.sessoes = sessoes;
        this.respondedor = respondedor;
    }

    @Override
    public void handle(HttpExchange troca) throws IOException {
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
            tentativas(troca);
            return;
        }
        List<String> segmentos = Arrays.asList(resto.split("/"));
        String id = segmentos.get(0);
        List<String> subrecurso = segmentos.subList(1, segmentos.size());
        Operacao operacao = reconhecer(subrecurso);
        if (operacao == null) {
            respondedor.responder(troca, 404, respondedor.erro("Recurso inexistente"));
            return;
        }
        if (!"POST".equals(troca.getRequestMethod())) {
            respondedor.responder(troca, 405, respondedor.erro("Método não permitido"));
            return;
        }
        ServicoSorteioAtividade sorteios = sessoes.de(troca).sorteios();
        if (!id.equals(sorteios.getTentativaId())) {
            respondedor.responder(troca, 404, respondedor.erro("Tentativa inexistente ou substituída por outro sorteio"));
            return;
        }
        String papel = subrecurso.size() >= 2 && "papeis".equals(subrecurso.get(0)) ? subrecurso.get(1) : null;
        try {
            Map<String, Object> corpo = corpo(troca);
            respondedor.responder(troca, 200, operacao.executar(sorteios, papel, corpo));
        } catch (RuntimeException erro) {
            respondedor.responder(troca, erro instanceof SemOperacaoAtiva ? 422 : 400,
                    respondedor.erro(erro.getMessage()));
        }
    }

    /** Criar uma tentativa: sortear uma situação (do grupo pedido, ou de um grupo ao acaso). */
    private void tentativas(HttpExchange troca) throws IOException {
        if (!"POST".equals(troca.getRequestMethod())) {
            respondedor.responder(troca, 405, respondedor.erro("Método não permitido"));
            return;
        }
        try {
            Map<String, Object> corpo = corpo(troca);
            Object grupo = corpo.get("grupo_sorteio");
            ServicoSorteioAtividade sorteios = sessoes.de(troca).sorteios();
            Map<String, Object> estado;
            if ("MEDIDAS".equals(grupo)) {
                estado = sorteios.sortearMedidas();
            } else if ("RELACOES".equals(grupo)) {
                estado = sorteios.sortearRelacoes();
            } else if (grupo == null) {
                estado = sorteios.estadoInicial();
            } else {
                respondedor.responder(troca, 400, respondedor.erro("grupo_sorteio inválido: " + grupo));
                return;
            }
            troca.getResponseHeaders().add("Location", RotasTentativa.tentativa(String.valueOf(estado.get("tentativa_id"))));
            respondedor.responder(troca, 200, estado);
        } catch (RuntimeException erro) {
            respondedor.responder(troca, 422, respondedor.erro(erro.getMessage()));
        }
    }

    private static final class SemOperacaoAtiva extends IllegalStateException {
        SemOperacaoAtiva(String mensagem) {
            super(mensagem);
        }
    }

    private static Operacao reconhecer(List<String> s) {
        int n = s.size();
        if (n == 1) {
            switch (s.get(0)) {
                case "classificacao":
                    return (sv, p, c) -> sv.escolherCategoria(String.valueOf(c.get("categoria")));
                case "operacoes":
                    return (sv, p, c) -> {
                        if (!sv.possuiAtividadeEscolhaOperacaoAtiva()) {
                            throw new SemOperacaoAtiva("a situação atual não possui escolha de operação implementada");
                        }
                        return sv.escolherOperacao(String.valueOf(c.get("seletor")), String.valueOf(c.get("operacao")));
                    };
                case "conclusao":
                    return (sv, p, c) -> sv.escolherConclusao(String.valueOf(c.get("escolha")));
                case "ajudas":
                    return (sv, p, c) -> sv.ajudaContextual(String.valueOf(c.get("area")), String.valueOf(c.get("intencao")));
                case "reinicio":
                    return (sv, p, c) -> sv.reiniciarAtividadeAtual();
                case "idioma":
                    return (sv, p, c) -> sv.trocarIdiomaSituacao(String.valueOf(c.get("codigo")));
                default:
                    return null;
            }
        }
        if (n == 2 && "classificacao".equals(s.get(0)) && "confirmacao".equals(s.get(1))) {
            return (sv, p, c) -> sv.confirmarCategoria(Boolean.TRUE.equals(c.get("concordou")));
        }
        if (n >= 3 && "papeis".equals(s.get(0))) {
            String recurso = String.join("/", s.subList(2, n));
            switch (recurso) {
                case "posicionamento":
                    return (sv, p, c) -> {
                        Object origem = c.get("origem_papel_id");
                        return sv.soltarNumero(p, origem == null ? null : String.valueOf(origem));
                    };
                case "valor":
                    return (sv, p, c) -> sv.proporValor(p, ((Number) c.get("valor")).intValue());
                case "valor/confirmacao":
                    return (sv, p, c) -> sv.responderConfirmacaoValor(p, Boolean.TRUE.equals(c.get("confirmou")),
                            ((Number) c.get("valor")).intValue());
                case "quadradinhos":
                    return (sv, p, c) -> sv.ajustarQuadradinho(p, ((Number) c.get("delta")).intValue());
                case "sinal":
                    return (sv, p, c) -> sv.escolherSinalNumeroRelativo(p, String.valueOf(c.get("sinal")));
                case "eixo/visibilidade":
                    return (sv, p, c) -> Boolean.TRUE.equals(c.get("visivel")) ? sv.revelarEixo(p) : sv.ocultarEixo(p);
                case "eixo/projecao":
                    return (sv, p, c) -> sv.projetarAlteracaoEixo(p, ((Number) c.get("valor")).intValue());
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
        return (Map<String, Object>) AnalisadorJsonSimples.analisar(texto);
    }
}
