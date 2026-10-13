package gerard.infraestrutura.web;

import gerard.aplicacao.portabilidade.RotasTentativa;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * O contrato da API em OpenAPI 3.1. As rotas da tentativa saem da mesma tabela que as atende
 * ({@link RecursosTentativa#TABELA}) e os tipos de problema saem de {@link Problema}; as demais rotas são descritas
 * aqui, ao lado do código que as atende. Servido em {@code GET /api/v1/openapi.json}; o teste de contrato confere que o
 * documento servido é o do repositório ({@code documentacao/api/openapi.json}) e que cada rota documentada existe — mudar
 * a API sem mudar o contrato (ou o contrário) quebra o teste.
 */
final class DocumentoOpenApi {
    private DocumentoOpenApi() {
    }

    private static Map<String, Object> mapa(Object... paresChaveValor) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        for (int i = 0; i < paresChaveValor.length; i += 2) {
            m.put((String) paresChaveValor[i], paresChaveValor[i + 1]);
        }
        return m;
    }

    private static Map<String, Object> ref(String componente) {
        return mapa("$ref", "#/components/" + componente);
    }

    private static Map<String, Object> resposta(String descricao, Object conteudo) {
        Map<String, Object> r = mapa("description", descricao);
        if (conteudo != null) {
            r.put("content", conteudo);
        }
        return r;
    }

    private static Map<String, Object> hal(String esquema) {
        return mapa("application/hal+json", mapa("schema", ref("schemas/" + esquema)));
    }

    private static Map<String, Object> json(String esquema) {
        return mapa("application/json", mapa("schema", ref("schemas/" + esquema)));
    }

    private static Map<String, Object> problema(Problema p) {
        return resposta(p.status() + " " + p.slug(), mapa("application/problem+json", mapa("schema", ref("schemas/Problema"))));
    }

    private static Map<String, Object> respostasDeErro(Problema... problemas) {
        Map<String, Object> r = new LinkedHashMap<String, Object>();
        for (Problema p : problemas) {
            r.put(String.valueOf(p.status()), problema(p));
        }
        return r;
    }

    private static Map<String, Object> corpoJson(String[] campos) {
        Map<String, Object> propriedades = new LinkedHashMap<String, Object>();
        List<Object> obrigatorios = new ArrayList<Object>();
        for (String campo : campos) {
            boolean opcional = campo.endsWith("?");
            String[] partes = (opcional ? campo.substring(0, campo.length() - 1) : campo).split(":");
            propriedades.put(partes[0], mapa("type", opcional ? new Object[] {partes[1], "null"} : partes[1]));
            if (!opcional) {
                obrigatorios.add(partes[0]);
            }
        }
        Map<String, Object> esquema = mapa("type", "object", "properties", propriedades);
        if (!obrigatorios.isEmpty()) {
            esquema.put("required", obrigatorios);
        }
        return mapa("required", Boolean.TRUE, "content", mapa("application/json", mapa("schema", esquema)));
    }

    private static Map<String, Object> parametroDeCaminho(String nome, String descricao) {
        return mapa("name", nome, "in", "path", "required", Boolean.TRUE, "description", descricao,
                "schema", mapa("type", "string"));
    }

    private static Map<String, Object> parametroDeCabecalho(String nome, boolean obrigatorio, String descricao) {
        return mapa("name", nome, "in", "header", "required", Boolean.valueOf(obrigatorio), "description", descricao,
                "schema", mapa("type", "string"));
    }

    private static Map<String, Object> operacao(String resumo, String tag, Object parametros, Object corpo,
            Map<String, Object> respostas) {
        Map<String, Object> op = mapa("summary", resumo, "tags", new Object[] {tag});
        if (parametros != null) {
            op.put("parameters", parametros);
        }
        if (corpo != null) {
            op.put("requestBody", corpo);
        }
        op.put("responses", respostas);
        return op;
    }

    static Map<String, Object> gerar() {
        Map<String, Object> caminhos = new LinkedHashMap<String, Object>();
        String base = RotasTentativa.BASE;

        caminhos.put("/api/v1", mapa("get", operacao("Entrada da API: os links de onde tudo parte", "Entrada", null, null,
                mapa("200", resposta("Os links de entrada", hal("Raiz"))))));
        caminhos.put("/api/v1/openapi.json", mapa("get", operacao("Este contrato", "Entrada", null, null,
                mapa("200", resposta("O documento OpenAPI 3.1", mapa("application/json", mapa("schema",
                        mapa("type", "object"))))))));
        caminhos.put(Problema.BASE + "{slug}", mapa("get", operacao("Descreve um tipo de problema", "Problemas",
                new Object[] {parametroDeCaminho("slug", "O slug do tipo, o último segmento de \"type\"")}, null,
                mapa("200", resposta("O tipo de problema", json("Problema")), "404", problema(Problema.RECURSO_INEXISTENTE)))));

        Map<String, Object> criar = new LinkedHashMap<String, Object>();
        criar.put("post", operacao("Cria uma tentativa: sorteia uma situação", "Tentativa", null,
                corpoJson(new String[] {"grupo_sorteio:string?"}),
                combinar(mapa("201", resposta("A tentativa criada. Location aponta para ela; ETag é a versão inicial.",
                        hal("Estado"))), respostasDeErro(Problema.CORPO_INVALIDO, Problema.TIPO_DE_MIDIA_NAO_SUPORTADO,
                        Problema.DADOS_INVALIDOS, Problema.MUITAS_REQUISICOES, Problema.SERVICO_INDISPONIVEL))));
        caminhos.put(base, criar);

        for (RecursosTentativa.Rota rota : RecursosTentativa.TABELA) {
            String caminho = base + "/{id}" + (rota.modelo.isEmpty() ? "" : "/" + rota.modelo);
            List<Object> parametros = new ArrayList<Object>();
            parametros.add(parametroDeCaminho("id", "O id da tentativa, de \"tentativa_id\" do estado"));
            if (rota.temPapel()) {
                parametros.add(parametroDeCaminho("papel", "A chave do papel semântico (por exemplo, papel.parte1)"));
            }
            if (rota.altera) {
                parametros.add(parametroDeCabecalho("If-Match", true, "O ETag da última leitura da tentativa"));
            } else if ("GET".equals(rota.metodo)) {
                parametros.add(parametroDeCabecalho("If-None-Match", false, "Um ETag já conhecido; igual ao atual dá 304"));
            }
            Map<String, Object> respostas = new LinkedHashMap<String, Object>();
            respostas.put("200", resposta(rota.altera ? "Feito; o ETag novo vem no cabeçalho" : "Lido", hal("Estado")));
            if ("GET".equals(rota.metodo)) {
                respostas.put("304", resposta("Não mudou desde o ETag informado", null));
            }
            List<Problema> erros = new ArrayList<Problema>();
            erros.add(Problema.TENTATIVA_INEXISTENTE);
            erros.add(Problema.TENTATIVA_SUBSTITUIDA);
            erros.add(Problema.MUITAS_REQUISICOES);
            if (!"GET".equals(rota.metodo)) {
                erros.add(Problema.CORPO_INVALIDO);
                erros.add(Problema.TIPO_DE_MIDIA_NAO_SUPORTADO);
                erros.add(Problema.DADOS_INVALIDOS);
                erros.add(Problema.ACAO_INCOMPATIVEL_COM_O_ESTADO);
            }
            if (rota.altera) {
                erros.add(Problema.PRECONDICAO_OBRIGATORIA);
                erros.add(Problema.VERSAO_DESATUALIZADA);
            }
            respostas.putAll(respostasDeErro(erros.toArray(new Problema[0])));
            @SuppressWarnings("unchecked")
            Map<String, Object> item = (Map<String, Object>) caminhos.get(caminho);
            if (item == null) {
                item = new LinkedHashMap<String, Object>();
                caminhos.put(caminho, item);
            }
            Map<String, Object> op = operacao(rota.resumo, "Tentativa", parametros,
                    "GET".equals(rota.metodo) ? null : corpoJson(rota.corpo), respostas);
            op.put("x-altera-a-tentativa", Boolean.valueOf(rota.altera));
            op.put("x-idempotente", Boolean.valueOf("GET".equals(rota.metodo) || "PUT".equals(rota.metodo)));
            item.put(rota.metodo.toLowerCase(), op);
        }

        String[] perfil = {"nome:string", "idade:integer", "sexo:string", "midia_preferida:string",
            "nivel_escolaridade:string", "foto_data_url:string?"};
        Map<String, Object> usuarios = new LinkedHashMap<String, Object>();
        usuarios.put("get", operacao("Lista as pessoas cadastradas", "Pessoas", null, null,
                mapa("200", resposta("As pessoas", hal("ListaUsuarios")))));
        usuarios.put("post", operacao("Cadastra uma pessoa", "Pessoas", null, corpoJson(perfil),
                combinar(mapa("201", resposta("A pessoa criada, com o link \"editar\"", hal("Perfil"))),
                        respostasDeErro(Problema.DADOS_INVALIDOS))));
        caminhos.put("/api/v1/usuarios", usuarios);
        caminhos.put("/api/v1/usuarios/{id}", mapa("put", operacao("Edita uma pessoa", "Pessoas",
                new Object[] {parametroDeCaminho("id", "O id da pessoa")}, corpoJson(perfil),
                combinar(mapa("200", resposta("A pessoa editada", hal("Perfil"))),
                        respostasDeErro(Problema.DADOS_INVALIDOS, Problema.RECURSO_INEXISTENTE)))));

        Map<String, Object> sessao = new LinkedHashMap<String, Object>();
        sessao.put("get", operacao("Quem entrou nesta sessão", "Sessão", null, null,
                mapa("200", resposta("A pessoa da sessão, ou nenhuma", json("SessaoUsuario")))));
        sessao.put("post", operacao("Entra como uma pessoa cadastrada", "Sessão", null,
                corpoJson(new String[] {"usuario_id:string"}),
                combinar(mapa("200", resposta("Entrou", json("SessaoUsuario"))), respostasDeErro(Problema.DADOS_INVALIDOS))));
        caminhos.put("/api/v1/sessao/usuario", sessao);

        caminhos.put("/api/v1/relatos", mapa("post", operacao("Relata um problema encontrado na tela", "Suporte", null,
                corpoJson(new String[] {"descricao:string", "situacao_id:string", "categoria:string", "representacoes:string",
                    "idioma_interface:string", "idioma_situacao:string", "enunciado:string"}),
                combinar(mapa("200", resposta("O relato preparado", json("Resultado"))),
                        respostasDeErro(Problema.DADOS_INVALIDOS)))));
        caminhos.put("/api/v1/pesquisa/sincronizacoes", mapa("post", operacao(
                "Antecipa o envio do log (fechamento da aba)", "Pesquisa", null, null,
                mapa("200", resposta("Sincronizado", json("Resultado"))))));

        Map<String, Object> log = new LinkedHashMap<String, Object>();
        log.put("get", seguro(operacao("Consulta os eventos do log de pesquisa", "Pesquisa", new Object[] {
                    parametroDeConsulta("origem"), parametroDeConsulta("sessao"), parametroDeConsulta("usuario"),
                    parametroDeConsulta("limite")}, null,
                combinar(mapa("200", resposta("Os eventos", json("Resultado"))), respostasDeErro(Problema.NAO_AUTORIZADO,
                        Problema.SERVICO_INDISPONIVEL)))));
        log.put("post", seguro(operacao("Grava eventos do log de pesquisa (TSV ou JSON)", "Pesquisa", null,
                mapa("required", Boolean.TRUE, "content", mapa("text/tab-separated-values", mapa("schema", mapa("type", "string")),
                        "application/json", mapa("schema", mapa("type", "array", "items", mapa("type", "object"))))),
                combinar(mapa("200", resposta("Gravados", json("Resultado"))), respostasDeErro(Problema.CORPO_INVALIDO,
                        Problema.NAO_AUTORIZADO, Problema.SERVICO_INDISPONIVEL)))));
        caminhos.put("/api/v1/pesquisa/log", log);
        caminhos.put("/api/v1/curadoria/situacoes", mapa("post", seguro(operacao("Substitui as situações curadas", "Pesquisa",
                null, mapa("required", Boolean.TRUE, "content", mapa("text/plain", mapa("schema", mapa("type", "string")))),
                combinar(mapa("200", resposta("Aceito", json("Resultado"))), respostasDeErro(Problema.CORPO_INVALIDO,
                        Problema.NAO_AUTORIZADO, Problema.DADOS_INVALIDOS, Problema.SERVICO_INDISPONIVEL))))));
        caminhos.put("/api/v1/metricas", mapa("get", seguro(operacao("Contagem, duração e status por rota", "Operação",
                null, null, combinar(mapa("200", resposta("As métricas", json("Resultado"))),
                        respostasDeErro(Problema.NAO_AUTORIZADO, Problema.SERVICO_INDISPONIVEL))))));

        // Caminhos antigos, mantidos até a data em Sunset, com os mesmos métodos.
        for (String[] antigo : new String[][] {{"/api/pesquisa/log", "/api/v1/pesquisa/log"},
            {"/api/curadoria/situacoes", "/api/v1/curadoria/situacoes"}}) {
            @SuppressWarnings("unchecked")
            Map<String, Object> novo = (Map<String, Object>) caminhos.get(antigo[1]);
            Map<String, Object> copia = new LinkedHashMap<String, Object>();
            for (Map.Entry<String, Object> metodo : novo.entrySet()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> op = new LinkedHashMap<String, Object>((Map<String, Object>) metodo.getValue());
                op.put("deprecated", Boolean.TRUE);
                op.put("summary", op.get("summary") + " (depreciado: use " + antigo[1] + ")");
                copia.put(metodo.getKey(), op);
            }
            caminhos.put(antigo[0], copia);
        }

        Map<String, Object> esquemas = new LinkedHashMap<String, Object>();
        esquemas.put("Problema", mapa("type", "object", "description", "application/problem+json (RFC 9457)",
                "required", new Object[] {"type", "title", "status"}, "properties", mapa(
                        "type", mapa("type", "string", "description", "URI estável; GET nela descreve o problema"),
                        "title", mapa("type", "string"), "status", mapa("type", "integer"),
                        "detail", mapa("type", "string"), "instance", mapa("type", "string"),
                        "request_id", mapa("type", "string", "description", "O X-Request-Id da chamada"))));
        esquemas.put("Link", mapa("type", "object", "required", new Object[] {"href"}, "properties", mapa(
                "href", mapa("type", "string"),
                "metodo", mapa("type", "string", "enum", new Object[] {"POST", "PUT"},
                        "description", "Só vem quando não é GET (extensão ao HAL)"),
                "templated", mapa("type", "boolean"))));
        esquemas.put("Links", mapa("type", "object", "additionalProperties", ref("schemas/Link")));
        esquemas.put("Raiz", mapa("type", "object", "required", new Object[] {"_links"}, "properties",
                mapa("schema", mapa("type", "string"), "_links", ref("schemas/Links"))));
        esquemas.put("Estado", mapa("type", "object", "description",
                "O estado da tentativa. Além dos campos listados traz a cena, a modelagem e a ajuda; ver gerard.atividade-web.estado.v1.",
                "required", new Object[] {"tentativa_id", "situacao_id", "modo", "_links"}, "properties", mapa(
                        "schema", mapa("type", "string"), "modo", mapa("type", "string"),
                        "tentativa_id", mapa("type", "string"), "situacao_id", mapa("type", "string"),
                        "enunciado", mapa("type", "string"), "concluida", mapa("type", "boolean"),
                        "acoes_disponiveis", mapa("type", "array", "items", mapa("type", "object")),
                        "_links", ref("schemas/Links"))));
        esquemas.put("Perfil", mapa("type", "object", "required", new Object[] {"id", "nome"}, "properties", mapa(
                "id", mapa("type", "string"), "nome", mapa("type", "string"), "idade", mapa("type", "integer"),
                "_links", ref("schemas/Links"))));
        esquemas.put("ListaUsuarios", mapa("type", "object", "properties", mapa("usuarios",
                mapa("type", "array", "items", ref("schemas/Perfil")))));
        esquemas.put("SessaoUsuario", mapa("type", "object", "properties", mapa(
                "usuario_id", mapa("type", new Object[] {"string", "null"}), "perfil", ref("schemas/Perfil"))));
        esquemas.put("Resultado", mapa("type", "object", "description", "O resultado da ação"));

        Map<String, Object> componentes = mapa("schemas", esquemas, "securitySchemes", mapa("pesquisador",
                mapa("type", "http", "scheme", "bearer", "description",
                        "O token do pesquisador (PESQUISADOR_TOKEN). O visitante não tem credencial: a sessão por cookie "
                                + "(gerard_sessao) só o deixa alcançar a própria tentativa.")));

        Map<String, Object> raiz = new LinkedHashMap<String, Object>();
        raiz.put("openapi", "3.1.0");
        raiz.put("info", mapa("title", "Gérard API", "version", "1.0.0", "description",
                "A API do Gérard. Níveis de maturidade: recursos com identidade, verbos e status HTTP, hipermídia (HAL). "
                        + "O cliente conhece só GET /api/v1 e segue os links. Erros em application/problem+json. "
                        + "Alterar uma tentativa exige If-Match. Os caminhos fora de /api/v1 estão depreciados (cabeçalhos "
                        + "Deprecation, Sunset e Link rel=successor-version)."));
        raiz.put("servers", new Object[] {mapa("url", "/")});
        raiz.put("paths", caminhos);
        raiz.put("components", componentes);
        return raiz;
    }

    private static Map<String, Object> parametroDeConsulta(String nome) {
        return mapa("name", nome, "in", "query", "required", Boolean.FALSE, "schema", mapa("type", "string"));
    }

    private static Map<String, Object> seguro(Map<String, Object> operacao) {
        operacao.put("security", new Object[] {mapa("pesquisador", new Object[0])});
        return operacao;
    }

    private static Map<String, Object> combinar(Map<String, Object> a, Map<String, Object> b) {
        Map<String, Object> r = new LinkedHashMap<String, Object>(a);
        r.putAll(b);
        return r;
    }

    // ---- Escrita legível e estável (a ordem de inserção é a do contrato) -----------------------------------------

    static String escrever(Object valor) {
        StringBuilder sb = new StringBuilder();
        escrever(valor, sb, 0);
        return sb.append('\n').toString();
    }

    private static void indentar(StringBuilder sb, int nivel) {
        for (int i = 0; i < nivel; i++) {
            sb.append("  ");
        }
    }

    @SuppressWarnings("unchecked")
    private static void escrever(Object valor, StringBuilder sb, int nivel) {
        if (valor == null) {
            sb.append("null");
        } else if (valor instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) valor;
            if (m.isEmpty()) {
                sb.append("{}");
                return;
            }
            sb.append("{\n");
            int i = 0;
            for (Map.Entry<String, Object> e : m.entrySet()) {
                indentar(sb, nivel + 1);
                texto(e.getKey(), sb);
                sb.append(": ");
                escrever(e.getValue(), sb, nivel + 1);
                sb.append(++i < m.size() ? ",\n" : "\n");
            }
            indentar(sb, nivel);
            sb.append('}');
        } else if (valor instanceof Object[] || valor instanceof List) {
            List<Object> lista = valor instanceof List ? (List<Object>) valor : java.util.Arrays.asList((Object[]) valor);
            if (lista.isEmpty()) {
                sb.append("[]");
                return;
            }
            sb.append("[\n");
            for (int i = 0; i < lista.size(); i++) {
                indentar(sb, nivel + 1);
                escrever(lista.get(i), sb, nivel + 1);
                sb.append(i + 1 < lista.size() ? ",\n" : "\n");
            }
            indentar(sb, nivel);
            sb.append(']');
        } else if (valor instanceof Boolean || valor instanceof Number) {
            sb.append(valor);
        } else {
            texto(String.valueOf(valor), sb);
        }
    }

    private static void texto(String s, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }
}
