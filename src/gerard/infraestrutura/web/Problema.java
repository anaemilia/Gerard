package gerard.infraestrutura.web;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Os problemas que a API devolve, no formato do RFC 9457 (application/problem+json). O {@code type} é estável e
 * dereferenciável: GET /api/v1/problemas/{slug} descreve o problema, então o cliente decide pelo tipo e nunca pelo
 * texto de {@code detail}.
 */
enum Problema {
    CORPO_INVALIDO("corpo-invalido", 400, "Corpo da requisição inválido",
            "O corpo não é um JSON válido."),
    DADOS_INVALIDOS("dados-invalidos", 422, "Dados inválidos",
            "O corpo é um JSON válido, mas um campo falta, tem o tipo errado ou um valor que o domínio recusa."),
    NAO_AUTORIZADO("nao-autorizado", 401, "Não autorizado", "A credencial exigida está ausente ou é inválida."),
    RECURSO_INEXISTENTE("recurso-inexistente", 404, "Recurso inexistente", "Não há recurso neste endereço."),
    TENTATIVA_INEXISTENTE("tentativa-inexistente", 404, "Tentativa inexistente",
            "O id não é o de nenhuma tentativa deste visitante."),
    METODO_NAO_PERMITIDO("metodo-nao-permitido", 405, "Método não permitido",
            "O recurso não aceita este método; o cabeçalho Allow lista os aceitos."),
    ACAO_INCOMPATIVEL_COM_O_ESTADO("acao-incompativel-com-o-estado", 409, "Ação incompatível com o estado",
            "A tentativa não está num estado em que esta ação faça sentido (por exemplo, responder a uma confirmação "
                    + "que não foi pedida)."),
    TENTATIVA_SUBSTITUIDA("tentativa-substituida", 410, "Tentativa substituída",
            "A tentativa existiu, mas outro sorteio a substituiu; ela não volta."),
    VERSAO_DESATUALIZADA("versao-desatualizada", 412, "Versão desatualizada",
            "O If-Match não corresponde à versão atual da tentativa: outra aba ou chamada a alterou. Leia o recurso "
                    + "de novo antes de agir."),
    TIPO_DE_MIDIA_NAO_SUPORTADO("tipo-de-midia-nao-suportado", 415, "Tipo de mídia não suportado",
            "O corpo precisa ser application/json."),
    PRECONDICAO_OBRIGATORIA("precondicao-obrigatoria", 428, "Precondição obrigatória",
            "Alterar uma tentativa exige o cabeçalho If-Match com o ETag da última leitura."),
    MUITAS_REQUISICOES("muitas-requisicoes", 429, "Muitas requisições",
            "A sessão excedeu o limite de requisições; tente de novo depois do tempo em Retry-After."),
    ERRO_INTERNO("erro-interno", 500, "Erro interno",
            "O servidor falhou ao atender a requisição; o erro foi registrado no servidor."),
    SERVICO_INDISPONIVEL("servico-indisponivel", 503, "Serviço indisponível",
            "O recurso não está configurado neste servidor.");

    static final String BASE = "/api/v1/problemas/";

    private final String slug;
    private final int status;
    private final String titulo;
    private final String descricao;

    Problema(String slug, int status, String titulo, String descricao) {
        this.slug = slug;
        this.status = status;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    int status() {
        return status;
    }

    String slug() {
        return slug;
    }

    /** O corpo application/problem+json desta ocorrência. */
    Map<String, Object> corpo(String detalhe, String instancia) {
        Map<String, Object> corpo = new LinkedHashMap<String, Object>();
        corpo.put("type", BASE + slug);
        corpo.put("title", titulo);
        corpo.put("status", Integer.valueOf(status));
        corpo.put("detail", detalhe == null || detalhe.isEmpty() ? descricao : detalhe);
        if (instancia != null) {
            corpo.put("instance", instancia);
        }
        return corpo;
    }

    /** A descrição do tipo, servida em GET /api/v1/problemas/{slug}. */
    Map<String, Object> descricaoDoTipo() {
        Map<String, Object> corpo = new LinkedHashMap<String, Object>();
        corpo.put("type", BASE + slug);
        corpo.put("title", titulo);
        corpo.put("status", Integer.valueOf(status));
        corpo.put("description", descricao);
        return corpo;
    }

    static Problema porSlug(String slug) {
        for (Problema p : values()) {
            if (p.slug.equals(slug)) {
                return p;
            }
        }
        return null;
    }
}
