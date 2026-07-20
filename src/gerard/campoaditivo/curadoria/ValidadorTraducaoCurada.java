package gerard.campoaditivo.curadoria;

import gerard.interpretacao.simbolo.SimboloDesconhecido;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Funções puras usadas na validação e pontuação de traduções curadas:
 * comparação de textos, extração/normalização de números, e verificação de
 * que os valores numéricos curados aparecem em algarismos na tradução.
 *
 * Extraído de TelaCuradoriaSituacoes como parte da Fase 7 do plano de
 * refatoração — ver PLANO_REFATORACAO_ARQUITETURA_GERARD.md.
 *
 * Fica no mesmo pacote (gerard.campoaditivo.curadoria) porque
 * LinhaSituacao é uma classe aninhada de TelaCuradoriaSituacoes com campos
 * de visibilidade padrão (package-private); manter no mesmo pacote evita
 * precisar alterar essa visibilidade.
 *
 * Métodos originais em TelaCuradoriaSituacoes mantidos como wrappers de 1
 * linha; nenhum call site foi alterado.
 */
public final class ValidadorTraducaoCurada {

    private ValidadorTraducaoCurada() {
    }

    /** Compara dois textos ignorando null e espaços nas pontas. */
    public static boolean textosIguais(String a, String b) {
        return (a == null ? "" : a.trim()).equals(b == null ? "" : b.trim());
    }

    /**
     * Pontua uma linha de tradução para decidir qual versão preferir ao
     * consolidar duplicidades: validada > enunciado mais completo > tem id.
     */
    public static int pontuarTraducao(TelaCuradoriaSituacoes.LinhaSituacao l) {
        if (l == null) return Integer.MIN_VALUE;
        int pontos = Boolean.TRUE.equals(l.validada) ? 100000 : 0;
        String texto = l.enunciado == null ? "" : l.enunciado.trim();
        pontos += Math.min(50000, texto.length());
        if (l.id != null && !l.id.trim().isEmpty()) pontos += 10;
        return pontos;
    }

    /** Converte um texto numérico (aceita vírgula decimal) para BigDecimal, ou null se inválido. */
    public static BigDecimal converterNumero(String valor) {
        if (valor == null) return null;
        String limpo = valor.trim().replace(" ", "").replace(',', '.');
        try {
            return new BigDecimal(limpo).abs().stripTrailingZeros();
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /** Normaliza um nome de papel semântico: minúsculo, sem acento, espaços viram "_". */
    public static String normalizarPapel(String valor) {
        if (valor == null) return "";
        return Normalizer.normalize(valor.trim().toLowerCase(java.util.Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replace(' ', '_');
    }

    /** Extrai todos os números (inteiros ou decimais, com sinal opcional) de um texto livre. */
    public static List<BigDecimal> extrairNumeros(String texto) {
        List<BigDecimal> numeros = new ArrayList<BigDecimal>();
        Matcher matcher = Pattern.compile("(?<![\\p{L}\\d])[+-]?\\d+(?:[.,]\\d+)?(?![\\p{L}\\d])")
                .matcher(texto == null ? "" : texto);
        while (matcher.find()) {
            BigDecimal numero = converterNumero(matcher.group());
            if (numero != null) numeros.add(numero);
        }
        return numeros;
    }

    /** Junta uma lista de mensagens em texto com marcadores, truncando em 20 itens. */
    public static String juntarMensagens(List<String> mensagens) {
        StringBuilder sb = new StringBuilder();
        int limite = Math.min(20, mensagens.size());
        for (int i = 0; i < limite; i++) sb.append("• ").append(mensagens.get(i)).append('\n');
        if (mensagens.size() > limite) sb.append("• ... e mais ").append(mensagens.size() - limite).append(" inconsistência(s).\n");
        return sb.toString().trim();
    }

    /**
     * Se o papel não é o desconhecido da situação e o valor curado não
     * aparece (em algarismos) entre os números já extraídos do texto,
     * adiciona uma mensagem de "ausente" à lista.
     */
    public static void adicionarSeAusente(List<String> ausentes, List<BigDecimal> numerosTexto,
            String rotulo, String valor, boolean papelDesconhecido) {
        if (papelDesconhecido || valor == null) return;
        String bruto = valor.trim();
        if (bruto.isEmpty() || SimboloDesconhecido.eh(bruto)) return;
        BigDecimal esperado = converterNumero(bruto);
        if (esperado == null) return;
        for (BigDecimal encontrado : numerosTexto) {
            if (encontrado.compareTo(esperado) == 0) return;
        }
        ausentes.add(rotulo + ": " + bruto);
    }

    /**
     * Verifica, para cada campo numérico curado da situação original (que
     * não seja o próprio termo desconhecido), se o valor aparece em
     * algarismos no texto traduzido. Retorna a lista de campos ausentes.
     */
    public static List<String> localizarValoresNumericosAusentes(
            TelaCuradoriaSituacoes.LinhaSituacao original, String texto) {
        List<String> ausentes = new ArrayList<String>();
        List<BigDecimal> numerosTexto = extrairNumeros(texto);
        String desconhecido = normalizarPapel(original.termoDesconhecido);
        adicionarSeAusente(ausentes, numerosTexto, "estado inicial", original.estadoInicial,
                desconhecido.equals("estado_inicial"));
        adicionarSeAusente(ausentes, numerosTexto, "transformação", original.transformacao,
                desconhecido.equals("transformacao"));
        adicionarSeAusente(ausentes, numerosTexto, "estado final", original.estadoFinal,
                desconhecido.equals("estado_final"));
        adicionarSeAusente(ausentes, numerosTexto, "quantidade 1", original.quantidade1,
                desconhecido.equals("quantidade_1") || desconhecido.equals("parte_1"));
        adicionarSeAusente(ausentes, numerosTexto, "quantidade 2", original.quantidade2,
                desconhecido.equals("quantidade_2") || desconhecido.equals("parte_2"));
        adicionarSeAusente(ausentes, numerosTexto, "resultado", original.resultado,
                desconhecido.equals("resultado") || desconhecido.equals("todo"));
        adicionarSeAusente(ausentes, numerosTexto, "referido", original.referido,
                desconhecido.equals("referido"));
        adicionarSeAusente(ausentes, numerosTexto, "referendo", original.referendo,
                desconhecido.equals("referendo"));
        adicionarSeAusente(ausentes, numerosTexto, "valor relativo", original.valorRelativo,
                desconhecido.equals("valor_relativo"));
        return ausentes;
    }

    /** Retorna o valor curado sem sinal explícito (+/-) na frente. */
    public static String valorSemSinal(String valor) {
        if (valor == null) return "";
        String v = valor.trim();
        if (v.startsWith("+") || v.startsWith("-")) return v.substring(1).trim();
        return v;
    }

    /** Retorna "positivo", "negativo" ou "" a partir do sinal (explícito ou implícito) do valor. */
    public static String sinalDoValor(String valor) {
        if (valor == null) return "";
        String v = valor.trim();
        if (v.startsWith("-")) return "negativo";
        if (v.startsWith("+")) return "positivo";
        return v.isEmpty() ? "" : "positivo";
    }

    /** Remove tabulações/quebras de linha de um campo de texto curado, aparando espaços nas pontas. */
    public static String campo(String valor) {
        return valor == null ? "" : valor.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ').trim();
    }

    /**
     * Gera um id padrão (determinístico) para uma linha de situação curada,
     * a partir do idioma, tipo, contexto e um hash do enunciado.
     */
    public static String gerarIdPadrao(int linha, String codigoIdioma,
            gerard.campoaditivo.modelo.TipoSituacaoAditiva tipo, String contexto, String enunciado) {
        String codigo = codigoIdioma == null ? "ID" : codigoIdioma.replaceAll("[^A-Za-z]", "").toUpperCase(java.util.Locale.ROOT);
        if (codigo.length() < 2) codigo = "ID";
        String base = codigo.substring(0, Math.min(3, codigo.length())) + "_" + (tipo == null ? "SITUACAO" : tipo.name())
                + "_" + normalizar(contexto)
                + "_" + Math.abs(enunciado == null ? linha : enunciado.hashCode());
        return base.replaceAll("[^A-Za-z0-9_\\-]", "_");
    }

    /** Normaliza um texto livre para uso em id: minúsculo, sem acento, não-alfanumérico vira "_". */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return normalizado;
    }
}
