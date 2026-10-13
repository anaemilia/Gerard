package gerard.infraestrutura.web;

import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Uma {@link SessaoWeb} por visitante, identificada por um cookie opaco emitido pelo servidor. Sem cookie válido
 * (primeira visita, cookie de outra instância, sessão expirada), cria uma sessão nova e devolve o cookie. Sessões
 * ociosas expiram e o total é limitado: ao estourar o limite sai a mais ociosa.
 */
final class SessoesWeb {
    static final String COOKIE = "gerard_sessao";
    // HttpExchange.setAttribute guarda por contexto (compartilhado entre requisições), por isso a sessão da
    // requisição em curso fica neste mapa fraco, chaveado pela própria troca.
    private final Map<HttpExchange, SessaoWeb> daRequisicao =
            java.util.Collections.synchronizedMap(new java.util.WeakHashMap<HttpExchange, SessaoWeb>());

    private final Map<String, SessaoWeb> sessoes = new ConcurrentHashMap<String, SessaoWeb>();
    private final Supplier<SessaoWeb> fabrica;
    private final long ociosidadeMaximaMs;
    private final int limite;

    SessoesWeb(Supplier<SessaoWeb> fabrica, long ociosidadeMaximaMs, int limite) {
        this.fabrica = fabrica;
        this.ociosidadeMaximaMs = ociosidadeMaximaMs;
        this.limite = limite;
    }

    /** A sessão de quem fez a requisição; cria e envia o cookie quando ainda não existe. */
    SessaoWeb de(HttpExchange troca) {
        SessaoWeb ja = daRequisicao.get(troca);
        if (ja != null) {
            return ja;
        }
        String id = lerCookie(troca);
        SessaoWeb sessao = id == null ? null : sessoes.get(id);
        if (sessao == null) {
            sessao = criar();
            emitirCookie(troca, sessao.id());
        }
        sessao.registrarAcesso();
        daRequisicao.put(troca, sessao);
        return sessao;
    }

    boolean contem(String id) {
        return sessoes.containsKey(id);
    }

    int quantidade() {
        return sessoes.size();
    }

    /** Remove as sessões ociosas além do tempo máximo. */
    void expirar() {
        long limiteMs = System.currentTimeMillis() - ociosidadeMaximaMs;
        for (SessaoWeb sessao : sessoes.values()) {
            if (sessao.ultimoAcessoEmMs() < limiteMs) {
                sessoes.remove(sessao.id());
            }
        }
    }

    synchronized SessaoWeb criar() {
        while (sessoes.size() >= limite) {
            SessaoWeb maisOciosa = null;
            for (SessaoWeb sessao : sessoes.values()) {
                if (maisOciosa == null || sessao.ultimoAcessoEmMs() < maisOciosa.ultimoAcessoEmMs()) {
                    maisOciosa = sessao;
                }
            }
            if (maisOciosa == null) {
                break;
            }
            sessoes.remove(maisOciosa.id());
        }
        SessaoWeb nova = fabrica.get();
        sessoes.put(nova.id(), nova);
        return nova;
    }

    static String novoId() {
        return UUID.randomUUID().toString();
    }

    private static String lerCookie(HttpExchange troca) {
        List<String> cabecalhos = troca.getRequestHeaders().get("Cookie");
        if (cabecalhos == null) {
            return null;
        }
        for (String cabecalho : cabecalhos) {
            for (String par : cabecalho.split(";")) {
                String p = par.trim();
                if (p.startsWith(COOKIE + "=")) {
                    return p.substring(COOKIE.length() + 1);
                }
            }
        }
        return null;
    }

    private void emitirCookie(HttpExchange troca, String id) {
        boolean https = "https".equalsIgnoreCase(troca.getRequestHeaders().getFirst("X-Forwarded-Proto"));
        troca.getResponseHeaders().add("Set-Cookie", COOKIE + "=" + id + "; Path=/; HttpOnly; SameSite=Lax; Max-Age="
                + (ociosidadeMaximaMs / 1000) + (https ? "; Secure" : ""));
    }
}
