package gerard.interpretacao.incognita;

import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.NumeroEncontrado;
import gerard.interpretacao.util.NormalizadorTexto;
import java.util.List;

/**
 * Centraliza a inferencia linguistica do papel da interrogacao.
 *
 * A interrogacao nao possui papel fixo: em composicao pode ser parte ou todo;
 * em transformacao pode ser estado inicial, transformacao ou estado final; em
 * comparacao pode ser referente, referido ou diferenca. Este modulo agrupa as
 * heuristicas comuns para que a interface use a mesma decisao em todos os
 * pontos: marcadores no texto, dicas, questionamento e alvo correto no diagrama.
 */
public class InferidorIncognitaLinguistica {

    public ResultadoIncognitaLinguistica inferir(String textoOriginal, CategoriaProblema categoria, List<NumeroEncontrado> numeros) {
        String texto = NormalizadorTexto.normalizar(textoOriginal == null ? "" : textoOriginal);
        String pergunta = extrairTrechoPergunta(texto);

        if (categoria == null) {
            categoria = CategoriaProblema.INDEFINIDA;
        }

        switch (categoria) {
            case COMPOSICAO_MEDIDAS:
                return inferirComposicaoMedidas(texto, pergunta, numeros);
            case TRANSFORMACAO_MEDIDAS:
                return inferirTransformacaoMedidas(texto, pergunta, numeros);
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
                return inferirComposicaoTransformacaoMedidas(texto, pergunta, numeros);
            case COMPARACAO_MEDIDAS:
                return inferirComparacaoMedidas(texto, pergunta, numeros);
            case COMPOSICAO_TRANSFORMACOES:
                return inferirComposicaoTransformacoes(texto, pergunta, numeros);
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                return inferirTransformacaoCompostaDoisPassos(texto, pergunta, numeros);
            case TRANSFORMACAO_RELACAO:
                return inferirTransformacaoRelacao(texto, pergunta, numeros);
            case COMPOSICAO_RELACOES:
                return inferirComposicaoRelacoes(texto, pergunta, numeros);
            default:
                return new ResultadoIncognitaLinguistica("papel.valor", false, "categoria indefinida");
        }
    }

    private ResultadoIncognitaLinguistica inferirComposicaoMedidas(String texto, String pergunta, List<NumeroEncontrado> numeros) {
        String base = pergunta.length() > 0 ? pergunta : texto;

        if (expressaoDeParteFaltante(base)) {
            return resultado("papel.parte2", "composicao: pergunta por parte faltante");
        }

        if (perguntaPedeTotal(base)) {
            return resultado("papel.todo", "composicao: pergunta por total");
        }

        if (totalConhecidoNoEnunciado(texto, numeros) && perguntaPedeMedidaDeComponente(base)) {
            String papelPorEntidade = papelPartePorEntidadePerguntada(texto, base, numeros);
            if (papelPorEntidade != null) {
                return resultado(papelPorEntidade, "composicao: entidade da pergunta associada a uma parte");
            }
            return resultado("papel.parte2", "composicao: total conhecido e uma parte desconhecida");
        }

        if (!totalConhecidoNoEnunciado(texto, numeros)) {
            return resultado("papel.todo", "composicao: pergunta por total");
        }

        return resultado("papel.todo", "composicao: padrao geral");
    }

    private ResultadoIncognitaLinguistica inferirTransformacaoMedidas(String texto, String pergunta, List<NumeroEncontrado> numeros) {
        String base = pergunta.length() > 0 ? pergunta : texto;

        if (perguntaPedeTransformacao(base)) {
            return resultado("papel.transformacao", "transformacao: pergunta pela mudanca ou pela quantidade transformada");
        }

        if (contemAlgum(base, "tinha antes", "possuia antes", "possuía antes", "quanto tinha antes",
                "inicialmente", "no inicio", "no início", "antes", "had before", "have before", "avant")) {
            return resultado("papel.estadoInicial", "transformacao: pergunta pelo estado inicial");
        }

        if (contemAlgum(base, "agora", "ficou", "ficaram", "restou", "restaram", "sobrou", "sobraram",
                "tem agora", "existem agora", "ao final", "final", "now", "left", "remain", "reste")) {
            return resultado("papel.estadoFinal", "transformacao: pergunta pelo estado final");
        }

        return resultado("papel.estadoFinal", "transformacao: padrao geral");
    }

    private ResultadoIncognitaLinguistica inferirComposicaoTransformacaoMedidas(String texto, String pergunta, List<NumeroEncontrado> numeros) {
        String base = pergunta.length() > 0 ? pergunta : texto;

        if (expressaoDeParteFaltante(base)) {
            return resultado("papel.parte2", "composicao-transformacao: parte inicial faltante");
        }
        if (contemAlgum(base, "ao todo", "no total", "juntos", "juntas", "total inicial")) {
            return resultado("papel.todo", "composicao-transformacao: total inicial");
        }
        if (contemAlgum(base, "deu", "perdeu", "ganhou", "recebeu", "colocou", "retirou", "tirou", "mudanca", "mudança")) {
            return resultado("papel.transformacao", "composicao-transformacao: transformacao");
        }
        return resultado("papel.estadoFinal", "composicao-transformacao: estado final");
    }

    private ResultadoIncognitaLinguistica inferirComparacaoMedidas(String texto, String pergunta, List<NumeroEncontrado> numeros) {
        String base = pergunta.length() > 0 ? pergunta : texto;

        if (contemAlgum(base, "a mais", "a menos", "diferenca", "diferença", "quanto mais", "quanto menos",
                "more than", "less than", "fewer than", "de plus", "de moins")) {
            return resultado("papel.diferenca", "comparacao: pergunta pela diferenca");
        }

        if (contemAlgum(base, "referente", "primeiro", "primeira")) {
            return resultado("papel.referente", "comparacao: pergunta pelo referente");
        }

        return resultado("papel.referido", "comparacao: pergunta pela medida referida");
    }

    private ResultadoIncognitaLinguistica inferirComposicaoTransformacoes(String texto, String pergunta, List<NumeroEncontrado> numeros) {
        String base = pergunta.length() > 0 ? pergunta : texto;

        if (contemAlgum(base, "primeira", "primeiro", "1a", "1ª", "1o", "1º")) {
            return resultado("papel.transformacao1", "composicao de transformacoes: primeira transformacao");
        }
        if (contemAlgum(base, "segunda", "segundo", "2a", "2ª", "2o", "2º")) {
            return resultado("papel.transformacao2", "composicao de transformacoes: segunda transformacao");
        }
        return resultado("papel.transformacaoFinal", "composicao de transformacoes: transformacao final");
    }

    private ResultadoIncognitaLinguistica inferirTransformacaoCompostaDoisPassos(String texto, String pergunta, List<NumeroEncontrado> numeros) {
        String base = pergunta.length() > 0 ? pergunta : texto;

        if (contemAlgum(base, "o que aconteceu", "que aconteceu", "qual foi a transformacao", "qual foi a transformação", "mudanca", "mudança")) {
            return resultado("papel.transformacaoFinal", "transformacao composta: pergunta pela transformacao total");
        }
        if (contemAlgum(base, "primeira", "primeiro", "ontem", "passo 1", "etapa 1")) {
            return resultado("papel.transformacao1", "transformacao composta: primeira transformacao");
        }
        if (contemAlgum(base, "segunda", "segundo", "hoje", "passo 2", "etapa 2")) {
            return resultado("papel.transformacao2", "transformacao composta: segunda transformacao");
        }
        if (contemAlgum(base, "antes", "inicial", "inicio", "início", "comecou", "começou")) {
            return resultado("papel.estadoInicial", "transformacao composta: estado inicial");
        }
        if (contemAlgum(base, "ainda", "agora", "ficou", "restou", "restaram", "sobrou", "sobraram", "tem agora", "ao final", "final")) {
            return resultado("papel.estadoFinal", "transformacao composta: estado final");
        }
        return resultado("papel.estadoFinal", "transformacao composta: padrao geral");
    }

    private ResultadoIncognitaLinguistica inferirTransformacaoRelacao(String texto, String pergunta, List<NumeroEncontrado> numeros) {
        String base = pergunta.length() > 0 ? pergunta : texto;

        if (contemAlgum(base, "relacao inicial", "relação inicial", "antes")) {
            return resultado("papel.relacaoInicial", "transformacao de relacao: relacao inicial");
        }
        if (contemAlgum(base, "mudanca", "mudança", "transformacao", "transformação", "passou", "alterou")) {
            return resultado("papel.transformacao", "transformacao de relacao: transformacao");
        }
        return resultado("papel.relacaoFinal", "transformacao de relacao: relacao final");
    }

    private ResultadoIncognitaLinguistica inferirComposicaoRelacoes(String texto, String pergunta, List<NumeroEncontrado> numeros) {
        String base = pergunta.length() > 0 ? pergunta : texto;

        if (contemAlgum(base, "primeira relacao", "primeira relação", "relacao 1", "relação 1")) {
            return resultado("papel.relacao1", "composicao de relacoes: primeira relacao");
        }
        if (contemAlgum(base, "segunda relacao", "segunda relação", "relacao 2", "relação 2")) {
            return resultado("papel.relacao2", "composicao de relacoes: segunda relacao");
        }
        return resultado("papel.relacaoFinal", "composicao de relacoes: relacao final");
    }

    private ResultadoIncognitaLinguistica resultado(String chavePapel, String justificativa) {
        return new ResultadoIncognitaLinguistica(chavePapel, true, justificativa);
    }

    private String extrairTrechoPergunta(String texto) {
        if (texto == null || texto.length() == 0) {
            return "";
        }

        int fim = texto.lastIndexOf('?');
        if (fim < 0) {
            fim = texto.length();
        }

        int inicio = 0;
        for (int i = fim - 1; i >= 0; i--) {
            char c = texto.charAt(i);
            if (c == '.' || c == '!' || c == ';' || c == ':') {
                inicio = i + 1;
                break;
            }
        }

        return texto.substring(inicio, fim).trim();
    }

    private boolean perguntaPedeTransformacao(String texto) {
        return contemAlgum(texto,
                "o que aconteceu", "que aconteceu", "quanto ganhou", "quantos ganhou", "quantas ganhou",
                "quanto perdeu", "quantos perdeu", "quantas perdeu", "qual foi a transformacao",
                "qual foi a transformação", "mudanca", "mudança",
                "quanto gastou", "quantos gastou", "quantas gastou", "quanto pagou",
                "quanto comeu", "quantos comeu", "quantas comeu", "quanto bebeu",
                "quantos bebeu", "quantas bebeu", "quanto pegou", "quantos pegou",
                "quantas pegou", "quanto retirou", "quantos retirou", "quantas retirou",
                "quanto tirou", "quantos tirou", "quantas tirou", "quanto deu",
                "quantos deu", "quantas deu", "quanto vendeu", "quantos vendeu",
                "quantas vendeu", "quanto emprestou", "quantos emprestou", "quantas emprestou",
                "quanto usou", "quantos usou", "quantas usou", "quanto utilizou",
                "quantos utilizou", "quantas utilizou", "quantos chegaram", "quantas chegaram",
                "comeu", "bebeu", "pegou", "gastou", "pagou", "perdeu", "ganhou",
                "recebeu", "retirou", "tirou", "deu", "vendeu", "emprestou", "usou",
                "utilizou", "chegaram", "sairam", "saíram",
                "what happened", "how many did", "how much did", "how many were added",
                "how many were removed", "how many were eaten", "how many were spent",
                "que s'est-il passe", "que sest il passe", "combien a-t-il pris",
                "combien a-t-elle pris", "combien a-t-il mange", "combien a-t-elle mange",
                "combien a-t-il depense", "combien a-t-elle depense", "combien ont ete retires");
    }

    private boolean perguntaPedeTotal(String texto) {
        return contemAlgum(texto, "ao todo", "no total", "total", "juntos", "juntas", "existem", "existe",
                "in all", "altogether", "en tout", "au total", "ensemble");
    }

    private boolean perguntaPedeMedidaDeComponente(String texto) {
        return contemAlgum(texto, "quantos", "quantas", "quanto", "quanta", "qual", "alguns", "algumas");
    }

    private boolean expressaoDeParteFaltante(String texto) {
        return contemAlgum(texto, "quanto falta", "quantos faltam", "quantas faltam", "faltam", "falta",
                "precisa", "necessita", "para completar", "para formar", "para chegar", "para ficar com",
                "completar", "missing", "needed", "reste a", "reste à", "manque");
    }

    private boolean totalConhecidoNoEnunciado(String texto, List<NumeroEncontrado> numeros) {
        if (numeros == null || numeros.size() < 2) {
            return false;
        }
        return contemAlgum(texto, "ao todo", "no total", "total", "juntos", "juntas", "somando", "somadas", "somados",
                "existem", "existe", "sao", "são", "ha", "há");
    }

    private String papelPartePorEntidadePerguntada(String texto, String pergunta, List<NumeroEncontrado> numeros) {
        if (texto == null || pergunta == null || numeros == null || numeros.size() < 2) {
            return null;
        }

        String entidade = extrairEntidadePerguntada(pergunta);
        if (entidade.length() < 3) {
            return null;
        }

        int primeiraOcorrencia = texto.indexOf(entidade);
        if (primeiraOcorrencia < 0) {
            return null;
        }

        int posNumero0 = numeros.get(0).getPosicaoInicial();
        int posNumero1 = numeros.get(1).getPosicaoInicial();

        if (primeiraOcorrencia <= Math.min(posNumero0, posNumero1)) {
            return "papel.parte1";
        }
        if (primeiraOcorrencia > Math.min(posNumero0, posNumero1) && primeiraOcorrencia <= Math.max(posNumero0, posNumero1)) {
            return "papel.parte2";
        }

        return null;
    }

    private String extrairEntidadePerguntada(String pergunta) {
        if (pergunta == null) {
            return "";
        }

        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("[a-zA-ZçÇáàãâéêíóôõúüñ]+").matcher(pergunta);
        while (matcher.find()) {
            String p = matcher.group();
            if (p == null || p.length() < 3) {
                continue;
            }
            if (ehPalavraFuncional(p)) {
                continue;
            }
            return p;
        }
        return "";
    }

    private boolean ehPalavraFuncional(String palavra) {
        if (palavra == null) {
            return true;
        }
        String p = palavra.toLowerCase();
        return p.equals("quantos") || p.equals("quantas") || p.equals("quanto") || p.equals("quanta") ||
               p.equals("qual") || p.equals("quais") || p.equals("tem") || p.equals("têm") || p.equals("agora") ||
               p.equals("existem") || p.equals("existe") || p.equals("faltam") || p.equals("falta") ||
               p.equals("para") || p.equals("completar") || p.equals("ficar") || p.equals("com") ||
               p.equals("ao") || p.equals("todo") || p.equals("total") || p.equals("juntos") || p.equals("juntas") ||
               p.equals("as") || p.equals("os") || p.equals("o") || p.equals("a") || p.equals("de") || p.equals("da") ||
               p.equals("do") || p.equals("das") || p.equals("dos") || p.equals("um") || p.equals("uma");
    }

    private boolean contemAlgum(String texto, String... expressoes) {
        if (texto == null) {
            return false;
        }
        for (String expressao : expressoes) {
            if (expressao != null && expressao.length() > 0 && texto.contains(expressao)) {
                return true;
            }
        }
        return false;
    }
}
