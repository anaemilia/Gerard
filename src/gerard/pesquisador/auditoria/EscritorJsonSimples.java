package gerard.pesquisador.auditoria;

import java.util.List;
import java.util.Map;

/**
 * Serializador JSON mínimo — irmão de
 * gerard.agente.conhecimento.AnalisadorJsonSimples (que faz o caminho
 * inverso, parsear). Mesma justificativa: sem lib JSON no classpath do
 * projeto. Serializa Map/List/String/Number/Boolean/null com escape
 * correto (aspas, barra invertida, controle) — usado por
 * JsonlAgentAuditWriter pra transformar AgentAuditEvent.paraMapa() numa
 * linha JSON válida.
 */
public final class EscritorJsonSimples {

    private EscritorJsonSimples() {
    }

    public static String escrever(Object valor) {
        StringBuilder saida = new StringBuilder();
        escreverValor(valor, saida);
        return saida.toString();
    }

    @SuppressWarnings("unchecked")
    private static void escreverValor(Object valor, StringBuilder saida) {
        if (valor == null) {
            saida.append("null");
        } else if (valor instanceof Map) {
            escreverObjeto((Map<String, Object>) valor, saida);
        } else if (valor instanceof List) {
            escreverArray((List<Object>) valor, saida);
        } else if (valor instanceof String) {
            escreverString((String) valor, saida);
        } else if (valor instanceof Boolean || valor instanceof Number) {
            saida.append(String.valueOf(valor));
        } else {
            escreverString(String.valueOf(valor), saida);
        }
    }

    private static void escreverObjeto(Map<String, Object> mapa, StringBuilder saida) {
        saida.append('{');
        boolean primeiro = true;
        for (Map.Entry<String, Object> entrada : mapa.entrySet()) {
            if (!primeiro) {
                saida.append(',');
            }
            primeiro = false;
            escreverString(entrada.getKey(), saida);
            saida.append(':');
            escreverValor(entrada.getValue(), saida);
        }
        saida.append('}');
    }

    private static void escreverArray(List<Object> lista, StringBuilder saida) {
        saida.append('[');
        boolean primeiro = true;
        for (Object item : lista) {
            if (!primeiro) {
                saida.append(',');
            }
            primeiro = false;
            escreverValor(item, saida);
        }
        saida.append(']');
    }

    private static void escreverString(String texto, StringBuilder saida) {
        saida.append('"');
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"': saida.append("\\\""); break;
                case '\\': saida.append("\\\\"); break;
                case '\n': saida.append("\\n"); break;
                case '\r': saida.append("\\r"); break;
                case '\t': saida.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        saida.append(String.format("\\u%04x", (int) c));
                    } else {
                        saida.append(c);
                    }
            }
        }
        saida.append('"');
    }
}
