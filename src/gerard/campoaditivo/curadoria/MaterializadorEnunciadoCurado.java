package gerard.campoaditivo.curadoria;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.i18n.ServicoLocalizacao;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Materializa a representação numérica de um enunciado a partir dos papéis
 * da situação curada. Não interpreta nem classifica texto livre: somente
 * substitui um numeral quando seu valor já pertence a um papel conhecido.
 */
public final class MaterializadorEnunciadoCurado {
    private static final Map<String, String> ALGARISMOS = criarAlgarismos();

    public String materializar(SituacaoProblemaAditiva situacao) {
        return materializar(situacao, situacao == null ? "" : situacao.getEnunciado());
    }

    public String materializar(SituacaoProblemaAditiva situacao, String textoOriginal) {
        String texto = textoOriginal == null ? "" : textoOriginal;
        if (situacao == null || texto.trim().length() == 0) {
            return texto;
        }

        Set<String> valoresCurados = new LinkedHashSet<String>();
        for (SemanticaCuradaSituacao.PapelCurado papel :
                SemanticaCuradaSituacao.mapear(situacao, ServicoLocalizacao.getInstancia())) {
            if (papel == null || papel.isDesconhecido()) continue;
            String magnitude = magnitude(papel.getValor());
            if (magnitude.length() > 0) valoresCurados.add(magnitude);
        }

        String[] tokens = texto.split(" ", -1);
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = materializarToken(tokens[i], valoresCurados);
        }
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) resultado.append(' ');
            resultado.append(tokens[i]);
        }
        return resultado.toString();
    }

    private String materializarToken(String token, Set<String> valoresCurados) {
        if (token == null || token.length() == 0) return token == null ? "" : token;
        int inicio = 0;
        int fim = token.length();
        while (inicio < fim && !Character.isLetter(token.charAt(inicio))) inicio++;
        while (fim > inicio && !Character.isLetter(token.charAt(fim - 1))) fim--;
        if (inicio >= fim) return token;

        String palavra = normalizar(token.substring(inicio, fim));
        String algarismo = ALGARISMOS.get(palavra);
        if (algarismo == null || !valoresCurados.contains(algarismo)) return token;
        return token.substring(0, inicio) + algarismo + token.substring(fim);
    }

    private String magnitude(String valor) {
        if (valor == null) return "";
        String v = valor.trim().replace(',', '.').replaceAll("[^0-9.]", "");
        if (v.length() == 0 || ".".equals(v)) return "";
        try {
            double numero = Double.parseDouble(v);
            if (numero == Math.rint(numero)) return String.valueOf((long) numero);
            return String.valueOf(numero);
        } catch (NumberFormatException ex) {
            return "";
        }
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
    }

    private static Map<String, String> criarAlgarismos() {
        Map<String, String> mapa = new LinkedHashMap<String, String>();
        mapa.put("zero", "0"); mapa.put("um", "1"); mapa.put("uma", "1");
        mapa.put("dois", "2"); mapa.put("duas", "2"); mapa.put("tres", "3");
        mapa.put("quatro", "4"); mapa.put("cinco", "5"); mapa.put("seis", "6");
        mapa.put("sete", "7"); mapa.put("oito", "8"); mapa.put("nove", "9");
        mapa.put("dez", "10"); mapa.put("onze", "11"); mapa.put("doze", "12");
        mapa.put("treze", "13"); mapa.put("catorze", "14"); mapa.put("quatorze", "14");
        mapa.put("quinze", "15"); mapa.put("dezesseis", "16"); mapa.put("dezessete", "17");
        mapa.put("dezoito", "18"); mapa.put("dezenove", "19"); mapa.put("vinte", "20");
        mapa.put("trinta", "30"); mapa.put("quarenta", "40"); mapa.put("cinquenta", "50");
        mapa.put("sessenta", "60"); mapa.put("setenta", "70"); mapa.put("oitenta", "80");
        mapa.put("noventa", "90"); mapa.put("cem", "100"); mapa.put("cento", "100");
        return mapa;
    }
}
