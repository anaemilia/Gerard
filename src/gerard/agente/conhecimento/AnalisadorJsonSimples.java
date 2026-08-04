package gerard.agente.conhecimento;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser JSON mínimo (objetos, arrays, string, number, boolean, null) —
 * suficiente para o formato raso das regras em
 * dados/base_conhecimento_gerard_jsons/base_conhecimento_gerard/*.jsonl.
 * Não há biblioteca JSON no classpath do projeto (ver lib/: só Weka e
 * Bounce), e o formato não justifica adicionar uma dependência nova.
 */
public final class AnalisadorJsonSimples {
    private final String texto;
    private int pos;

    private AnalisadorJsonSimples(String texto) {
        this.texto = texto;
        this.pos = 0;
    }

    public static Object analisar(String texto) {
        AnalisadorJsonSimples analisador = new AnalisadorJsonSimples(texto);
        return analisador.lerValor();
    }

    private Object lerValor() {
        pularEspacos();
        char c = texto.charAt(pos);
        if (c == '{') {
            return lerObjeto();
        }
        if (c == '[') {
            return lerArray();
        }
        if (c == '"') {
            return lerString();
        }
        if (c == 't' || c == 'f') {
            return lerBooleano();
        }
        if (c == 'n') {
            pos += 4;
            return null;
        }
        return lerNumero();
    }

    private Map<String, Object> lerObjeto() {
        Map<String, Object> mapa = new LinkedHashMap<String, Object>();
        pos++;
        pularEspacos();
        if (texto.charAt(pos) == '}') {
            pos++;
            return mapa;
        }
        while (true) {
            pularEspacos();
            String chave = lerString();
            pularEspacos();
            pos++; // ':'
            Object valor = lerValor();
            mapa.put(chave, valor);
            pularEspacos();
            char c = texto.charAt(pos);
            pos++;
            if (c == '}') {
                break;
            }
        }
        return mapa;
    }

    private List<Object> lerArray() {
        List<Object> lista = new ArrayList<Object>();
        pos++;
        pularEspacos();
        if (texto.charAt(pos) == ']') {
            pos++;
            return lista;
        }
        while (true) {
            lista.add(lerValor());
            pularEspacos();
            char c = texto.charAt(pos);
            pos++;
            if (c == ']') {
                break;
            }
        }
        return lista;
    }

    private String lerString() {
        StringBuilder sb = new StringBuilder();
        pos++; // abre aspas
        while (true) {
            char c = texto.charAt(pos++);
            if (c == '"') {
                break;
            }
            if (c == '\\') {
                char esc = texto.charAt(pos++);
                switch (esc) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'u':
                        String hex = texto.substring(pos, pos + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                        break;
                    default:
                        sb.append(esc);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Boolean lerBooleano() {
        if (texto.startsWith("true", pos)) {
            pos += 4;
            return Boolean.TRUE;
        }
        pos += 5;
        return Boolean.FALSE;
    }

    private Double lerNumero() {
        int inicio = pos;
        while (pos < texto.length() && "-+.eE0123456789".indexOf(texto.charAt(pos)) >= 0) {
            pos++;
        }
        return Double.valueOf(texto.substring(inicio, pos));
    }

    private void pularEspacos() {
        while (pos < texto.length() && Character.isWhitespace(texto.charAt(pos))) {
            pos++;
        }
    }
}
