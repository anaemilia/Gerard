package gerard.interpretacao.servico;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filtra ocorrencias numericas que parecem artigo indefinido em portugues,
 * e nao quantidade matematica a ser modelada no diagrama.
 */
public class AnalisadorNumeralContextual {
    private static final Pattern PALAVRA = Pattern.compile("[A-Za-zÀ-ÿ]+|\\d+");

    private static final Set<String> PREPOSICOES_LOCATIVAS = new HashSet<String>(Arrays.asList(
            "em", "no", "na", "num", "numa", "dentro", "sobre"
    ));

    private static final Set<String> NUCLEOS_RECIPIENTE_OU_LOCAL = new HashSet<String>(Arrays.asList(
            "caixa", "cesta", "cesto", "sacola", "saco", "pote", "vaso", "balde", "garrafa",
            "prato", "mesa", "gaveta", "armario", "armário", "estante", "bandeja", "panela",
            "copo", "jarra", "quadro", "cartaz", "sala", "classe", "turma", "estojo", "mochila",
            "chaveiro", "colecao", "coleção", "conjunto", "pacote", "caixinha", "pasta"
    ));

    private static final Set<String> NUCLEOS_CONTEXTO_NAO_QUANTITATIVO = new HashSet<String>(Arrays.asList(
            "brincadeira", "jogo", "partida", "rodada", "aula", "atividade", "prova", "competicao",
            "competição", "corrida", "festa", "viagem", "passeio", "dia", "tarde", "manha", "manhã",
            "noite", "vez", "ocasião", "ocasiao", "historia", "história", "situacao", "situação"
    ));

    public boolean deveConsiderarComoQuantidade(String texto, int inicio, int fim, String tokenNumerico) {
        if (texto == null || tokenNumerico == null) {
            return false;
        }

        if (ehUsoDeUmComoArtigoIndefinidoPortugues(texto, inicio, fim, tokenNumerico)) {
            return false;
        }

        return true;
    }

    private boolean ehUsoDeUmComoArtigoIndefinidoPortugues(String texto, int inicio, int fim, String tokenNumerico) {
        String tokenNormalizado = normalizar(tokenNumerico);
        if (!("1".equals(tokenNormalizado) || "um".equals(tokenNormalizado) || "uma".equals(tokenNormalizado))) {
            return false;
        }

        String anterior = palavraAnterior(texto, inicio);
        String proxima = palavraProxima(texto, fim);
        String seguinte = palavraDepoisDaProxima(texto, fim);

        if (ehUsoGeralDeArtigoIndefinidoComConteudo(texto, fim)) {
            return true;
        }

        if (!ehNucleoDeRecipienteOuLocal(proxima) && !ehNucleoDeContextoNaoQuantitativo(proxima)) {
            return false;
        }

        if (ehPreposicaoLocativa(anterior)) {
            return true;
        }

        String duasAntes = duasPalavrasAntes(texto, inicio);
        if (duasAntes.endsWith("dentro de") || duasAntes.endsWith("dentro da") || duasAntes.endsWith("dentro do")) {
            return true;
        }

        // Caso comum: "tinha uma caixa com 35 chocolates". A unidade recipiente
        // apenas localiza/agrupa o objeto contado; o valor matematico esta no conteudo.
        if (ehNucleoDeRecipienteOuLocal(proxima) && ehPreposicaoDeConteudo(seguinte)) {
            return true;
        }

        // Caso comum em enunciados: "1 caixa guarda...", "1 cesta tem...".
        // Aqui o foco quantitativo geralmente esta no conteudo, nao na unidade recipiente.
        if (inicio == primeiroIndiceNaoEspaco(texto) && ehVerboDeConteudoOuExistencia(seguinte)) {
            return true;
        }

        return false;
    }

    private boolean ehPreposicaoLocativa(String palavra) {
        return PREPOSICOES_LOCATIVAS.contains(normalizar(palavra));
    }

    private boolean ehNucleoDeRecipienteOuLocal(String palavra) {
        String p = normalizar(palavra);
        return NUCLEOS_RECIPIENTE_OU_LOCAL.contains(p);
    }

    private boolean ehNucleoDeContextoNaoQuantitativo(String palavra) {
        String p = normalizar(palavra);
        return NUCLEOS_CONTEXTO_NAO_QUANTITATIVO.contains(p);
    }

    private boolean ehVerboDeConteudoOuExistencia(String palavra) {
        String p = normalizar(palavra);
        return p.equals("tem") || p.equals("possui") || p.equals("guarda") || p.equals("havia") ||
               p.equals("existem") || p.equals("existe") || p.equals("contem") || p.equals("contém") ||
               p.equals("recebe") || p.equals("fica") || p.equals("estao") || p.equals("estão");
    }

    private boolean ehPreposicaoDeConteudo(String palavra) {
        String p = normalizar(palavra);
        return p.equals("com") || p.equals("de") || p.equals("contendo");
    }

    /**
     * Trata casos como "um chaveiro com 3 chaves". Nessa construção,
     * "um" funciona como artigo indefinido que introduz o objeto/recipiente
     * da cena, e não como quantidade matemática a ser modelada. A quantidade
     * efetivamente relevante aparece depois da preposição de conteúdo.
     *
     * A regra é deliberadamente contextual: ela não remove ocorrências como
     * "um livro e dois cadernos", nas quais o "um" participa da contagem.
     */
    private boolean ehUsoGeralDeArtigoIndefinidoComConteudo(String texto, int fim) {
        java.util.List<String> proximas = proximasPalavras(texto, fim, 8);
        if (proximas.size() < 3) {
            return false;
        }

        for (int i = 1; i < proximas.size(); i++) {
            if (ehPreposicaoDeConteudo(proximas.get(i))) {
                for (int j = i + 1; j < proximas.size(); j++) {
                    if (ehTokenQuantidadeExplicita(proximas.get(j))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean ehTokenQuantidadeExplicita(String palavra) {
        String p = normalizar(palavra);
        if (p.matches("[+-]?\\d+(?:[.,]\\d+)?")) {
            return true;
        }
        return p.equals("zero") || p.equals("dois") || p.equals("duas") || p.equals("tres")
                || p.equals("quatro") || p.equals("cinco") || p.equals("seis") || p.equals("sete")
                || p.equals("oito") || p.equals("nove") || p.equals("dez") || p.equals("onze")
                || p.equals("doze") || p.equals("treze") || p.equals("catorze") || p.equals("quatorze")
                || p.equals("quinze") || p.equals("dezesseis") || p.equals("dezasseis")
                || p.equals("dezessete") || p.equals("dezasete") || p.equals("dezoito")
                || p.equals("dezenove") || p.equals("vinte");
    }

    private java.util.List<String> proximasPalavras(String texto, int fim, int limite) {
        java.util.List<String> resposta = new java.util.ArrayList<String>();
        Matcher matcher = PALAVRA.matcher(texto == null ? "" : texto);
        while (matcher.find()) {
            if (matcher.start() >= fim) {
                resposta.add(matcher.group());
                if (resposta.size() >= limite) {
                    break;
                }
            }
        }
        return resposta;
    }

    private String palavraAnterior(String texto, int inicio) {
        String anterior = "";
        Matcher matcher = PALAVRA.matcher(texto == null ? "" : texto);
        while (matcher.find()) {
            if (matcher.end() <= inicio) {
                anterior = matcher.group();
            } else {
                break;
            }
        }
        return anterior;
    }

    private String palavraProxima(String texto, int fim) {
        Matcher matcher = PALAVRA.matcher(texto == null ? "" : texto);
        while (matcher.find()) {
            if (matcher.start() >= fim) {
                return matcher.group();
            }
        }
        return "";
    }

    private String palavraDepoisDaProxima(String texto, int fim) {
        Matcher matcher = PALAVRA.matcher(texto == null ? "" : texto);
        boolean achouProxima = false;
        while (matcher.find()) {
            if (matcher.start() >= fim) {
                if (!achouProxima) {
                    achouProxima = true;
                } else {
                    return matcher.group();
                }
            }
        }
        return "";
    }

    private String duasPalavrasAntes(String texto, int inicio) {
        String penultima = "";
        String ultima = "";
        Matcher matcher = PALAVRA.matcher(texto == null ? "" : texto);
        while (matcher.find()) {
            if (matcher.end() <= inicio) {
                penultima = ultima;
                ultima = matcher.group();
            } else {
                break;
            }
        }
        String resultado = (penultima + " " + ultima).trim();
        return normalizar(resultado);
    }

    private int primeiroIndiceNaoEspaco(String texto) {
        if (texto == null) {
            return 0;
        }
        for (int i = 0; i < texto.length(); i++) {
            if (!Character.isWhitespace(texto.charAt(i))) {
                return i;
            }
        }
        return 0;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase();
    }
}
