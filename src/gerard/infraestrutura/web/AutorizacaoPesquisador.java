package gerard.infraestrutura.web;

import com.sun.net.httpserver.HttpExchange;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Os dois perfis da API. O <b>visitante</b> não tem credencial: a sessão (cookie) o identifica e só deixa alcançar a
 * própria tentativa. O <b>pesquisador</b> apresenta um token — {@code Authorization: Bearer <token>} — e só ele lê o log, envia
 * curadoria e consulta as métricas. O token vem de {@code PESQUISADOR_TOKEN} (ou, por compatibilidade,
 * {@code CURADORIA_TOKEN}); sem nenhum configurado, as rotas de pesquisador respondem 503 em vez de ficarem abertas.
 *
 * <p>O cabeçalho antigo {@code X-Curadoria-Token} segue aceito, marcado como depreciado ({@code Deprecation: true}).
 * A comparação é em tempo constante e o token nunca vai para o log.
 */
final class AutorizacaoPesquisador {
    private AutorizacaoPesquisador() {
    }

    /** null se a requisição é de pesquisador autenticado; senão o problema a devolver (e os cabeçalhos já postos). */
    static Problema verificar(HttpExchange troca) {
        String esperado = System.getProperty("gerard.pesquisador.token", System.getenv("PESQUISADOR_TOKEN"));
        if (esperado == null || esperado.trim().isEmpty()) {
            esperado = System.getenv("CURADORIA_TOKEN");
        }
        if (esperado == null || esperado.trim().isEmpty()) {
            return Problema.SERVICO_INDISPONIVEL;
        }
        String recebido = null;
        String autorizacao = troca.getRequestHeaders().getFirst("Authorization");
        if (autorizacao != null && autorizacao.regionMatches(true, 0, "Bearer ", 0, 7)) {
            recebido = autorizacao.substring(7).trim();
        } else if (troca.getRequestHeaders().getFirst("X-Curadoria-Token") != null) {
            recebido = troca.getRequestHeaders().getFirst("X-Curadoria-Token");
            troca.getResponseHeaders().set("Deprecation", "true");
        }
        if (recebido == null || !MessageDigest.isEqual(recebido.getBytes(StandardCharsets.UTF_8),
                esperado.getBytes(StandardCharsets.UTF_8))) {
            troca.getResponseHeaders().set("WWW-Authenticate", "Bearer realm=\"gerard\"");
            return Problema.NAO_AUTORIZADO;
        }
        return null;
    }
}
