package gerard.pesquisador.auditoria;

import gerard.agente.conhecimento.AnalisadorJsonSimples;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Validador REAL de um subconjunto de JSON Schema (rodada 4, 2026-07-31) —
 * sem biblioteca externa no classpath, implementa manualmente {@code $ref}
 * (resolvido contra {@code #/$defs/X}), {@code oneOf}, {@code required},
 * {@code type} (com arrays de tipos e {@code null}), {@code enum},
 * {@code const}, {@code additionalProperties}, {@code pattern} e
 * {@code format: date-time}. Não é o validador completo do draft
 * 2020-12 (sem suporte a {@code allOf}/{@code anyOf}/{@code not}/
 * {@code $dynamicRef}/formatos exóticos) — cobre exatamente o que
 * {@code schema_agentes_execucao_gerard.json} usa de verdade, e o que a
 * rodada 4 pediu explicitamente ($ref, oneOf, required, tipos, enums,
 * formatos, propriedades extras).
 */
public final class SchemaValidator {
    private static final Pattern PADRAO_DATE_TIME = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?(Z|[+-]\\d{2}:\\d{2})$");

    private final Map<String, Object> schemaRaiz;
    private final Map<String, Object> defs;

    @SuppressWarnings("unchecked")
    public SchemaValidator(Map<String, Object> schemaRaiz) {
        this.schemaRaiz = schemaRaiz;
        Object d = schemaRaiz.get("$defs");
        this.defs = d instanceof Map ? (Map<String, Object>) d : Collections.<String, Object>emptyMap();
    }

    @SuppressWarnings("unchecked")
    public static SchemaValidator carregarDe(File arquivoSchema) throws IOException {
        StringBuilder conteudo = new StringBuilder();
        BufferedReader leitor = new BufferedReader(new FileReader(arquivoSchema));
        try {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                conteudo.append(linha).append('\n');
            }
        } finally {
            leitor.close();
        }
        Object raiz = AnalisadorJsonSimples.analisar(conteudo.toString());
        if (!(raiz instanceof Map)) {
            throw new IOException("Schema raiz nao e um objeto JSON: " + arquivoSchema);
        }
        return new SchemaValidator((Map<String, Object>) raiz);
    }

    /** Valida contra o schema raiz completo (tipicamente com oneOf evento/resumoEpisodio). */
    public List<String> validar(Object dado) {
        List<String> erros = new ArrayList<String>();
        validarContraSchema(dado, schemaRaiz, "$", erros);
        return erros;
    }

    /** Valida contra uma definicao especifica de $defs (ex.: "evento"), sem passar pelo oneOf raiz. */
    @SuppressWarnings("unchecked")
    public List<String> validarContraDefinicao(Object dado, String nomeDefinicao) {
        List<String> erros = new ArrayList<String>();
        Object def = defs.get(nomeDefinicao);
        if (!(def instanceof Map)) {
            erros.add("Definicao '" + nomeDefinicao + "' nao encontrada em $defs");
            return erros;
        }
        validarContraSchema(dado, (Map<String, Object>) def, "$", erros);
        return erros;
    }

    @SuppressWarnings("unchecked")
    private void validarContraSchema(Object dado, Map<String, Object> schemaOriginal, String caminho,
            List<String> erros) {
        Map<String, Object> schema = resolverRef(schemaOriginal);

        if (schema.containsKey("oneOf")) {
            List<Object> variantes = (List<Object>) schema.get("oneOf");
            int passaram = 0;
            List<String> errosVariantes = new ArrayList<String>();
            for (Object v : variantes) {
                List<String> errosLocais = new ArrayList<String>();
                if (v instanceof Map) {
                    validarContraSchema(dado, (Map<String, Object>) v, caminho, errosLocais);
                }
                if (errosLocais.isEmpty()) {
                    passaram++;
                } else {
                    errosVariantes.addAll(errosLocais);
                }
            }
            if (passaram != 1) {
                erros.add(caminho + ": oneOf falhou (" + passaram + " variante(s) valida(s), esperado exatamente 1)"
                        + (errosVariantes.isEmpty() ? "" : " — detalhes: " + errosVariantes));
            }
            return;
        }

        if (schema.containsKey("const")) {
            Object esperado = schema.get("const");
            if (!Objects.equals(normalizarNumero(dado), normalizarNumero(esperado))) {
                erros.add(caminho + ": esperado const=" + esperado + ", encontrado " + dado);
            }
        }

        if (schema.containsKey("enum")) {
            List<Object> validos = (List<Object>) schema.get("enum");
            boolean ok = false;
            for (Object v : validos) {
                if (Objects.equals(normalizarNumero(v), normalizarNumero(dado))) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                erros.add(caminho + ": valor " + dado + " nao esta no enum " + validos);
            }
        }

        if (schema.containsKey("type")) {
            if (!tipoBate(dado, schema.get("type"))) {
                erros.add(caminho + ": tipo esperado " + schema.get("type") + ", encontrado " + tipoReal(dado));
            }
        }

        if (schema.containsKey("pattern") && dado instanceof String) {
            String padrao = String.valueOf(schema.get("pattern"));
            if (!Pattern.compile(padrao).matcher((String) dado).find()) {
                erros.add(caminho + ": \"" + dado + "\" nao bate com pattern " + padrao);
            }
        }

        if ("date-time".equals(schema.get("format")) && dado instanceof String) {
            if (!PADRAO_DATE_TIME.matcher((String) dado).matches()) {
                erros.add(caminho + ": \"" + dado + "\" nao e um date-time ISO-8601 valido");
            }
        }

        if (dado instanceof Map) {
            Map<String, Object> mapaDado = (Map<String, Object>) dado;
            if (schema.containsKey("required")) {
                for (Object campoObj : (List<Object>) schema.get("required")) {
                    String campo = String.valueOf(campoObj);
                    if (!mapaDado.containsKey(campo) || mapaDado.get(campo) == null) {
                        erros.add(caminho + ": campo obrigatorio ausente ou nulo: \"" + campo + "\"");
                    }
                }
            }
            Object propsObj = schema.get("properties");
            Map<String, Object> props = propsObj instanceof Map ? (Map<String, Object>) propsObj : null;
            if (props != null) {
                for (Map.Entry<String, Object> entrada : props.entrySet()) {
                    if (mapaDado.containsKey(entrada.getKey()) && entrada.getValue() instanceof Map) {
                        validarContraSchema(mapaDado.get(entrada.getKey()), (Map<String, Object>) entrada.getValue(),
                                caminho + "." + entrada.getKey(), erros);
                    }
                }
            }
            boolean permiteExtras = !Boolean.FALSE.equals(schema.get("additionalProperties"));
            if (!permiteExtras && props != null) {
                for (String chave : mapaDado.keySet()) {
                    if (!props.containsKey(chave)) {
                        erros.add(caminho + ": propriedade extra nao permitida (additionalProperties=false): \""
                                + chave + "\"");
                    }
                }
            }
        }

        if (dado instanceof List && schema.get("items") instanceof Map) {
            List<Object> lista = (List<Object>) dado;
            Map<String, Object> itemSchema = (Map<String, Object>) schema.get("items");
            for (int i = 0; i < lista.size(); i++) {
                validarContraSchema(lista.get(i), itemSchema, caminho + "[" + i + "]", erros);
            }
        }
    }

    private Object normalizarNumero(Object valor) {
        if (valor instanceof Number) {
            return Double.valueOf(((Number) valor).doubleValue());
        }
        return valor;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> resolverRef(Map<String, Object> schema) {
        if (schema.containsKey("$ref")) {
            String ref = String.valueOf(schema.get("$ref"));
            String nome = ref.substring(ref.lastIndexOf('/') + 1);
            Object alvo = defs.get(nome);
            if (alvo instanceof Map) {
                return (Map<String, Object>) alvo;
            }
        }
        return schema;
    }

    private boolean tipoBate(Object dado, Object tipoDecl) {
        if (tipoDecl instanceof List) {
            for (Object t : (List<?>) tipoDecl) {
                if (bateUmTipo(dado, String.valueOf(t))) {
                    return true;
                }
            }
            return false;
        }
        return bateUmTipo(dado, String.valueOf(tipoDecl));
    }

    private boolean bateUmTipo(Object dado, String tipo) {
        if ("null".equals(tipo)) {
            return dado == null;
        }
        if (dado == null) {
            return false;
        }
        if ("string".equals(tipo)) {
            return dado instanceof String;
        }
        if ("integer".equals(tipo)) {
            if (dado instanceof Integer || dado instanceof Long) {
                return true;
            }
            if (dado instanceof Double) {
                double v = ((Double) dado).doubleValue();
                return v == Math.floor(v) && !Double.isInfinite(v);
            }
            return false;
        }
        if ("number".equals(tipo)) {
            return dado instanceof Number;
        }
        if ("boolean".equals(tipo)) {
            return dado instanceof Boolean;
        }
        if ("object".equals(tipo)) {
            return dado instanceof Map;
        }
        if ("array".equals(tipo)) {
            return dado instanceof List;
        }
        return true;
    }

    private String tipoReal(Object dado) {
        if (dado == null) {
            return "null";
        }
        if (dado instanceof String) {
            return "string";
        }
        if (dado instanceof Boolean) {
            return "boolean";
        }
        if (dado instanceof Number) {
            return "number";
        }
        if (dado instanceof Map) {
            return "object";
        }
        if (dado instanceof List) {
            return "array";
        }
        return dado.getClass().getName();
    }
}
