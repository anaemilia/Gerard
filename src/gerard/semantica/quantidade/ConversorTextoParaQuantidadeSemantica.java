package gerard.semantica.quantidade;

import gerard.semantica.papel.PapelQuantitativo;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Conversão exata e contextual de texto para quantidade semântica. */
public final class ConversorTextoParaQuantidadeSemantica {
    private static final Pattern TRECHO_NUMERICO = Pattern.compile(
            "[+\\-]?(?:\\d[\\d.,]*|[.,]\\d+)");

    public QuantidadeSemantica converter(String texto,
            PerfilQuantidadeSituacao perfil, PapelQuantitativo papel) {
        BigDecimal valor = converterDecimal(texto);
        if (valor == null) {
            return null;
        }
        try {
            return new QuantidadeSemantica(valor, perfil, papel);
        } catch (IllegalArgumentException invalido) {
            return null;
        }
    }

    public BigDecimal converterDecimal(String texto) {
        if (texto == null) {
            return null;
        }
        String normalizado = normalizarSinais(texto).trim();
        if (normalizado.length() == 0 || "?".equals(normalizado)) {
            return null;
        }
        Matcher matcher = TRECHO_NUMERICO.matcher(normalizado.replace(" ", ""));
        if (!matcher.find()) {
            return null;
        }
        String numero = matcher.group();
        if (matcher.find()) {
            return null;
        }
        String canonico = canonizarSeparadores(numero);
        try {
            return canonico == null ? null : new BigDecimal(canonico);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizarSinais(String texto) {
        return texto.replace('\u2212', '-').replace('\u2013', '-')
                .replace('\u2014', '-').replace('\u00A0', ' ')
                .replace('\u202F', ' ');
    }

    private String canonizarSeparadores(String numero) {
        int ultimaVirgula = numero.lastIndexOf(',');
        int ultimoPonto = numero.lastIndexOf('.');
        if (ultimaVirgula >= 0 && ultimoPonto >= 0) {
            char decimal = ultimaVirgula > ultimoPonto ? ',' : '.';
            char milhar = decimal == ',' ? '.' : ',';
            String semMilhar = numero.replace(String.valueOf(milhar), "");
            return decimal == ',' ? semMilhar.replace(',', '.') : semMilhar;
        }
        if (ultimaVirgula >= 0) {
            return canonizarSeparadorUnico(numero, ',');
        }
        if (ultimoPonto >= 0) {
            return canonizarSeparadorUnico(numero, '.');
        }
        return numero;
    }

    private String canonizarSeparadorUnico(String numero, char separador) {
        int ultima = numero.lastIndexOf(separador);
        int ocorrencias = contar(numero, separador);
        int casasFinais = numero.length() - ultima - 1;
        boolean decimal = casasFinais >= 1 && casasFinais <= 2;
        if (ocorrencias > 1 && !decimal) {
            return numero.replace(String.valueOf(separador), "");
        }
        if (!decimal) {
            return numero.replace(String.valueOf(separador), "");
        }
        String antes = numero.substring(0, ultima)
                .replace(String.valueOf(separador), "");
        return antes + "." + numero.substring(ultima + 1);
    }

    private int contar(String texto, char caractere) {
        int quantidade = 0;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) == caractere) quantidade++;
        }
        return quantidade;
    }
}
