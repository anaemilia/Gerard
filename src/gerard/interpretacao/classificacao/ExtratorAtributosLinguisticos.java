package gerard.interpretacao.classificacao;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrai atributos linguísticos simples e explicáveis do enunciado.
 *
 * O objetivo não é substituir a interpretação linguística existente, mas fornecer
 * evidências estáveis para um classificador treinável e leve.
 */
public class ExtratorAtributosLinguisticos {
    private static final Pattern NUMERO_DIGITO = Pattern.compile("\\b\\d+[,.]?\\d*\\b");

    public List<String> extrair(String enunciado) {
        Set<String> atributos = new LinkedHashSet<String>();
        String texto = normalizar(enunciado);
        if (texto.length() == 0) {
            return new ArrayList<String>(atributos);
        }

        adicionarTokens(atributos, texto);
        adicionarNGramas(atributos, texto);
        adicionarPistasSemanticas(atributos, texto);
        adicionarAtributosNumericos(atributos, texto);

        return new ArrayList<String>(atributos);
    }

    public List<String> extrairPistasHumanas(String enunciado) {
        Set<String> pistas = new LinkedHashSet<String>();
        String texto = normalizar(enunciado);
        if (texto.length() == 0) {
            return new ArrayList<String>(pistas);
        }

        adicionarPistaSeContem(pistas, texto, "ao todo");
        adicionarPistaSeContem(pistas, texto, "em conjunto");
        adicionarPistaSeContem(pistas, texto, "total");
        adicionarPistaSeContem(pistas, texto, "juntos");
        adicionarPistaSeContem(pistas, texto, "a mais");
        adicionarPistaSeContem(pistas, texto, "a menos");
        adicionarPistaSeContem(pistas, texto, "menos que");
        adicionarPistaSeContem(pistas, texto, "mais que");
        adicionarPistaSeContem(pistas, texto, "diferenca");
        adicionarPistaSeContem(pistas, texto, "quanto falta");
        adicionarPistaSeContem(pistas, texto, "ontem");
        adicionarPistaSeContem(pistas, texto, "hoje");
        adicionarPistaSeContem(pistas, texto, "depois");
        adicionarPistaSeContem(pistas, texto, "em seguida");
        adicionarPistaSeContem(pistas, texto, "primeira");
        adicionarPistaSeContem(pistas, texto, "segunda");

        String[] verbosTransformacao = termosTransformacao();
        for (String verbo : verbosTransformacao) {
            if (contemTermo(texto, verbo)) {
                pistas.add(verbo);
            }
        }

        int qtdNumeros = contarNumeros(texto);
        pistas.add("numerais=" + qtdNumeros);
        return new ArrayList<String>(pistas);
    }

    public String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase()
                .replace('ç', 'c')
                .replaceAll("[^a-z0-9?/,.:;\\s-]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private void adicionarTokens(Set<String> atributos, String texto) {
        String[] tokens = texto.split("\\s+");
        for (String token : tokens) {
            String limpo = token.replaceAll("^[^a-z0-9]+|[^a-z0-9]+$", "");
            if (limpo.length() >= 2 && !ehStopword(limpo) && !ehNumero(limpo)) {
                atributos.add("tok:" + limpo);
            }
        }
    }

    private void adicionarNGramas(Set<String> atributos, String texto) {
        String[] tokens = texto.split("\\s+");
        for (int i = 0; i < tokens.length - 1; i++) {
            String a = limparToken(tokens[i]);
            String b = limparToken(tokens[i + 1]);
            if (a.length() > 0 && b.length() > 0) {
                String bigrama = a + " " + b;
                if (ehBigramaRelevante(bigrama)) {
                    atributos.add("bi:" + bigrama);
                }
            }
        }
    }

    private String limparToken(String token) {
        if (token == null) {
            return "";
        }
        return token.replaceAll("^[^a-z0-9]+|[^a-z0-9]+$", "");
    }

    private boolean ehBigramaRelevante(String bigrama) {
        return bigrama.contains("ao todo")
                || bigrama.contains("em conjunto")
                || bigrama.contains("a mais")
                || bigrama.contains("a menos")
                || bigrama.contains("mais que")
                || bigrama.contains("menos que")
                || bigrama.contains("em seguida")
                || bigrama.contains("mais tarde")
                || bigrama.contains("no final")
                || bigrama.contains("tem agora")
                || bigrama.contains("ficou com")
                || bigrama.contains("ainda tem")
                || bigrama.contains("foram tiradas")
                || bigrama.contains("foram vendidas")
                || bigrama.contains("foram vendidos")
                || bigrama.contains("foram postas")
                || bigrama.contains("foi tirada")
                || bigrama.contains("qual total")
                || bigrama.contains("qual foi");
    }

    private void adicionarPistasSemanticas(Set<String> atributos, String texto) {
        if (contemAlgum(texto, "ao todo", "em conjunto", "total", "juntos", "juntas", "somando", "colecao", "possui", "tem")) {
            atributos.add("sem:composicao");
        }
        if (contemAlgum(texto, "perdeu", "perderam", "ganhou", "ganharam", "comeu", "comeram", "deu", "deram", "gastou", "comprou", "recebeu", "receberam", "foram postas", "foram tiradas", "foram vendidos", "chegaram", "voltou", "restavam", "sobraram")) {
            atributos.add("sem:transformacao");
        }
        if (contemAlgum(texto, "a mais", "a menos", "mais que", "menos que", "diferenca", "quanto falta", "quantos a menos", "quantos a mais")) {
            atributos.add("sem:comparacao");
        }
        if (contemAlgum(texto, "ontem", "hoje", "depois", "em seguida", "mais tarde", "primeira", "segunda", "primeiro", "segundo", "manha", "tarde", "noite")) {
            atributos.add("sem:temporal");
        }
        if (contemAlgum(texto, "primeira partida", "segunda partida", "ao final das duas", "duas partidas", "transformacao total", "ganhado ao todo", "perdido ao todo")) {
            atributos.add("sem:composicao_transformacoes");
        }
        if (contemAlgum(texto, "a mae de", "passou a ter", "ficou a mais", "ficou a menos") && contemAlgum(texto, "a mais", "a menos")) {
            atributos.add("sem:transformacao_relacao");
        }
        if (contemAlgum(texto, "mais velho", "mais velha", "relacao entre", "diferenca de idade") && contemAlgum(texto, "joao", "pedro", "carla", "maria", "carlos")) {
            atributos.add("sem:composicao_relacoes");
        }

        int positivos = contarTermos(texto, termosTransformacaoPositiva());
        int negativos = contarTermos(texto, termosTransformacaoNegativa());
        int temporais = contarTermos(texto, termosTemporais());
        if (positivos + negativos >= 2) {
            atributos.add("qtd_transformacoes:2_ou_mais");
        }
        if (temporais >= 2) {
            atributos.add("qtd_temporais:2_ou_mais");
        }
        if (positivos > 0 && negativos > 0) {
            atributos.add("sinais:mistos");
        } else if (positivos >= 2) {
            atributos.add("sinais:positivos_repetidos");
        } else if (negativos >= 2) {
            atributos.add("sinais:negativos_repetidos");
        }
    }

    private void adicionarAtributosNumericos(Set<String> atributos, String texto) {
        int qtd = contarNumeros(texto);
        if (qtd == 0) {
            atributos.add("num:zero");
        } else if (qtd == 1) {
            atributos.add("num:um");
        } else if (qtd == 2) {
            atributos.add("num:dois");
        } else if (qtd == 3) {
            atributos.add("num:tres");
        } else {
            atributos.add("num:quatro_ou_mais");
        }
    }

    public int contarNumeros(String textoNormalizado) {
        int total = 0;
        Matcher m = NUMERO_DIGITO.matcher(textoNormalizado);
        while (m.find()) {
            total++;
        }
        String texto = " " + textoNormalizado + " ";
        String[] palavras = new String[] {
            "zero", "um", "uma", "dois", "duas", "tres", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez",
            "onze", "doze", "treze", "catorze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove",
            "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa", "cem"
        };
        for (String palavra : palavras) {
            total += contarOcorrenciasPalavra(texto, palavra);
        }
        return total;
    }

    private int contarOcorrenciasPalavra(String texto, String palavra) {
        int total = 0;
        Matcher m = Pattern.compile("\\b" + Pattern.quote(palavra) + "\\b").matcher(texto);
        while (m.find()) {
            total++;
        }
        return total;
    }

    private boolean ehNumero(String token) {
        return token.matches("\\d+[,.]?\\d*");
    }

    private boolean ehStopword(String token) {
        return token.length() <= 1
                || token.equals("de") || token.equals("da") || token.equals("do") || token.equals("das") || token.equals("dos")
                || token.equals("em") || token.equals("na") || token.equals("no") || token.equals("nas") || token.equals("nos")
                || token.equals("com") || token.equals("para") || token.equals("por") || token.equals("que") || token.equals("qual")
                || token.equals("quais") || token.equals("quanto") || token.equals("quantos") || token.equals("quantas")
                || token.equals("ele") || token.equals("ela") || token.equals("eles") || token.equals("elas")
                || token.equals("sua") || token.equals("seu") || token.equals("suas") || token.equals("seus")
                || token.equals("um") || token.equals("uma") || token.equals("e") || token.equals("o") || token.equals("a")
                || token.equals("os") || token.equals("as");
    }

    public boolean contemAlgum(String texto, String... termos) {
        for (String termo : termos) {
            if (contemTermo(texto, normalizar(termo))) {
                return true;
            }
        }
        return false;
    }

    public boolean contemTermo(String texto, String termo) {
        if (texto == null || termo == null || termo.length() == 0) {
            return false;
        }
        String t = normalizar(termo);
        if (t.length() == 0) {
            return false;
        }
        if (t.indexOf(' ') >= 0 || t.indexOf('-') >= 0) {
            return (" " + texto + " ").contains(" " + t + " ");
        }
        return Pattern.compile("\\b" + Pattern.quote(t) + "\\b").matcher(texto).find();
    }

    private void adicionarPistaSeContem(Set<String> pistas, String texto, String termo) {
        String normalizado = normalizar(termo);
        if (contemTermo(texto, normalizado)) {
            pistas.add(termo);
        }
    }

    private int contarTermos(String texto, String[] termos) {
        int total = 0;
        for (String termo : termos) {
            if (contemTermo(texto, termo)) {
                total++;
            }
        }
        return total;
    }

    public String[] termosTransformacao() {
        String[] positivos = termosTransformacaoPositiva();
        String[] negativos = termosTransformacaoNegativa();
        String[] todos = new String[positivos.length + negativos.length];
        System.arraycopy(positivos, 0, todos, 0, positivos.length);
        System.arraycopy(negativos, 0, todos, positivos.length, negativos.length);
        return todos;
    }

    public String[] termosTransformacaoPositiva() {
        return new String[] {
            "ganhou", "ganharam", "ganhado", "recebeu", "receberam", "presenteou", "comprou", "compraram",
            "colocou", "colocaram", "postas", "foram postas", "chegaram", "acrescentou", "adicionou", "achou", "apareceram"
        };
    }

    public String[] termosTransformacaoNegativa() {
        return new String[] {
            "perdeu", "perderam", "perdido", "comeu", "comeram", "tomou", "pegou", "gastou", "gastaram", "deu", "deram",
            "emprestou", "tirou", "tiraram", "tirada", "tiradas", "foram tiradas", "vendeu", "venderam", "vendidos", "foram vendidos",
            "utilizados", "pagou", "restavam", "sobraram", "quebradas"
        };
    }

    public String[] termosTemporais() {
        return new String[] {"ontem", "hoje", "depois", "em seguida", "mais tarde", "primeira", "segunda", "primeiro", "segundo", "manha", "tarde", "noite"};
    }
}
