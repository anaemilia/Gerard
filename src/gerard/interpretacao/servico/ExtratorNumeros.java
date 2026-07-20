package gerard.interpretacao.servico;

import gerard.interpretacao.modelo.NumeroEncontrado;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtratorNumeros {
    private static final Pattern NUMERO = Pattern.compile("[+-]?\\d+(?:[.,]\\d+)?");
    private static final Pattern PALAVRA = Pattern.compile("[A-Za-zÀ-ÿ]+");
    private final AnalisadorNumeralContextual analisadorContextual = new AnalisadorNumeralContextual();
    private final Map<String, String> numeraisPorExtenso = criarMapaNumeraisPorExtenso();

    public List<NumeroEncontrado> extrair(String texto) {
        List<NumeroEncontrado> numeros = new ArrayList<NumeroEncontrado>();

        if (texto == null) {
            return numeros;
        }

        Matcher matcher = NUMERO.matcher(texto);

        while (matcher.find()) {
            String token = matcher.group();
            if (analisadorContextual.deveConsiderarComoQuantidade(texto, matcher.start(), matcher.end(), token)) {
                numeros.add(new NumeroEncontrado(token, matcher.start(), matcher.end(), token));
            }
        }

        Matcher palavras = PALAVRA.matcher(texto);
        while (palavras.find()) {
            String palavra = palavras.group();
            String valor = valorNumeralPorExtenso(palavra);
            if (valor != null && analisadorContextual.deveConsiderarComoQuantidade(texto, palavras.start(), palavras.end(), palavra)) {
                numeros.add(new NumeroEncontrado(palavra, palavras.start(), palavras.end(), valor));
            }
        }

        Collections.sort(numeros, new Comparator<NumeroEncontrado>() {
            public int compare(NumeroEncontrado a, NumeroEncontrado b) {
                return a.getPosicaoInicial() - b.getPosicaoInicial();
            }
        });

        return numeros;
    }

    private String valorNumeralPorExtenso(String palavra) {
        if (palavra == null) {
            return null;
        }
        String normalizada = java.text.Normalizer.normalize(palavra, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase();
        return numeraisPorExtenso.get(normalizada);
    }

    private Map<String, String> criarMapaNumeraisPorExtenso() {
        Map<String, String> mapa = new HashMap<String, String>();
        mapa.put("zero", "0");
        mapa.put("um", "1");
        mapa.put("uma", "1");
        mapa.put("dois", "2");
        mapa.put("duas", "2");
        mapa.put("tres", "3");
        mapa.put("três", "3");
        mapa.put("quatro", "4");
        mapa.put("cinco", "5");
        mapa.put("seis", "6");
        mapa.put("sete", "7");
        mapa.put("oito", "8");
        mapa.put("nove", "9");
        mapa.put("dez", "10");
        mapa.put("onze", "11");
        mapa.put("doze", "12");
        mapa.put("treze", "13");
        mapa.put("catorze", "14");
        mapa.put("quatorze", "14");
        mapa.put("quinze", "15");
        mapa.put("dezesseis", "16");
        mapa.put("dezasseis", "16");
        mapa.put("dezessete", "17");
        mapa.put("dezasete", "17");
        mapa.put("dezoito", "18");
        mapa.put("dezenove", "19");
        mapa.put("vinte", "20");
        return mapa;
    }
}
