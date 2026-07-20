package gerard.interpretacao.servico;

import gerard.interpretacao.simbolo.SimboloDesconhecido;

import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.incognita.InferidorIncognitaLinguistica;
import gerard.interpretacao.incognita.ResultadoIncognitaLinguistica;
import gerard.interpretacao.modelo.NumeroEncontrado;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.util.NormalizadorTexto;
import java.util.ArrayList;
import java.util.List;

public class InferidorPapeisNumericos {
    private final InferidorIncognitaLinguistica inferidorIncognita = new InferidorIncognitaLinguistica();

    public List<PapelElementoInterpretado> inferir(String textoOriginal, CategoriaProblema categoria, List<NumeroEncontrado> numeros) {
        List<PapelElementoInterpretado> papeis = new ArrayList<PapelElementoInterpretado>();
        String texto = NormalizadorTexto.normalizar(textoOriginal);

        if (categoria == null) {
            categoria = CategoriaProblema.INDEFINIDA;
        }

        switch (categoria) {
            case COMPOSICAO_MEDIDAS:
                inferirComposicaoMedidas(texto, numeros, papeis);
                break;
            case TRANSFORMACAO_MEDIDAS:
                inferirTransformacaoMedidas(texto, numeros, papeis);
                break;
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
                inferirComposicaoTransformacaoMedidas(texto, numeros, papeis);
                break;
            case COMPARACAO_MEDIDAS:
                inferirComparacaoMedidas(texto, numeros, papeis);
                break;
            case COMPOSICAO_TRANSFORMACOES:
                inferirComposicaoTransformacoes(numeros, papeis);
                break;
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                inferirTransformacaoCompostaDoisPassos(texto, numeros, papeis);
                break;
            case TRANSFORMACAO_RELACAO:
                inferirTransformacaoRelacao(numeros, papeis);
                break;
            case COMPOSICAO_RELACOES:
                inferirComposicaoRelacoes(numeros, papeis);
                break;
            default:
                inferirSequencialGenerico(numeros, papeis);
                break;
        }

        return papeis;
    }

    private void inferirComposicaoMedidas(String texto, List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis) {
        String papelIncognita = papelIncognita(texto, CategoriaProblema.COMPOSICAO_MEDIDAS, numeros, "papel.todo");

        if ("papel.parte1".equals(papelIncognita) && numeros.size() >= 2) {
            adicionarSeExiste(numeros, papeis, 0, "papel.parte2");
            adicionarSeExiste(numeros, papeis, 1, "papel.todo");
            adicionarDesconhecidoSeAusente(papeis, "papel.parte1");
            return;
        }

        if ("papel.parte2".equals(papelIncognita) && numeros.size() >= 2) {
            adicionarSeExiste(numeros, papeis, 0, "papel.parte1");
            adicionarSeExiste(numeros, papeis, 1, "papel.todo");
            adicionarDesconhecidoSeAusente(papeis, "papel.parte2");
            return;
        }

        adicionarSeExiste(numeros, papeis, 0, "papel.parte1");
        adicionarSeExiste(numeros, papeis, 1, "papel.parte2");
        if (numeros.size() >= 3) {
            adicionarSeExiste(numeros, papeis, 2, "papel.todo");
        }

        if (temInterrogacaoTextual(texto) || numeros.size() < 3 || perguntaTotal(texto)) {
            adicionarDesconhecidoSeAusente(papeis, papelIncognita);
        }
    }

    private void inferirTransformacaoMedidas(String texto, List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis) {
        String papelIncognita = papelIncognita(texto, CategoriaProblema.TRANSFORMACAO_MEDIDAS, numeros, "papel.estadoFinal");

        if ("papel.transformacao".equals(papelIncognita) && numeros.size() >= 2) {
            adicionarSeExiste(numeros, papeis, 0, "papel.estadoInicial");
            adicionarSeExiste(numeros, papeis, 1, "papel.estadoFinal");
            adicionarDesconhecidoSeAusente(papeis, "papel.transformacao");
            return;
        }

        if ("papel.estadoInicial".equals(papelIncognita) && numeros.size() >= 2) {
            adicionarSeExiste(numeros, papeis, 0, "papel.transformacao");
            adicionarSeExiste(numeros, papeis, 1, "papel.estadoFinal");
            adicionarDesconhecidoSeAusente(papeis, "papel.estadoInicial");
            return;
        }

        adicionarSeExiste(numeros, papeis, 0, "papel.estadoInicial");
        adicionarSeExiste(numeros, papeis, 1, "papel.transformacao");
        if (numeros.size() >= 3) {
            adicionarSeExiste(numeros, papeis, 2, "papel.estadoFinal");
        }

        if (temInterrogacaoTextual(texto) || numeros.size() < 3) {
            adicionarDesconhecidoSeAusente(papeis, papelIncognita);
        }
    }

    private void inferirComposicaoTransformacaoMedidas(String texto, List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis) {
        String papelIncognita = papelIncognita(texto, CategoriaProblema.COMPOSICAO_TRANSFORMACAO_MEDIDAS, numeros, "papel.estadoFinal");

        adicionarSeExiste(numeros, papeis, 0, "papel.parte1");
        adicionarSeExiste(numeros, papeis, 1, "papel.parte2");
        if (numeros.size() >= 3) {
            adicionarSeExiste(numeros, papeis, 2, "papel.transformacao");
        }
        if (numeros.size() >= 4) {
            adicionarSeExiste(numeros, papeis, 3, "papel.transformacao");
        }
        if (numeros.size() >= 5) {
            adicionarSeExiste(numeros, papeis, 4, "papel.estadoFinal");
        }

        if (temInterrogacaoTextual(texto) || numeros.size() < 5) {
            adicionarDesconhecidoSeAusente(papeis, papelIncognita);
        }
    }

    private void inferirComparacaoMedidas(String texto, List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis) {
        if (comparacaoPorDiferencaDeclarada(texto) && numeros.size() >= 2) {
            int indiceDiferenca = indiceNumeroDaDiferencaDeclarada(texto, numeros);

            if (indiceDiferenca >= 0) {
                int indiceMedidaConhecida = primeiroIndiceDiferente(numeros, indiceDiferenca);

                if (indiceMedidaConhecida >= 0) {
                    String papelMedidaConhecida = papelDaMedidaConhecidaNaComparacao(texto, numeros, indiceDiferenca, indiceMedidaConhecida);
                    String papelIncognita = "papel.referente".equals(papelMedidaConhecida) ? "papel.referido" : "papel.referente";
                    adicionarPapeisComparacaoNaOrdemDoTexto(numeros, papeis, indiceDiferenca, indiceMedidaConhecida, papelMedidaConhecida);
                    adicionarDesconhecido(papeis, papelIncognita);
                    return;
                }
            }
        }

        adicionarSeExiste(numeros, papeis, 0, "papel.referente");
        adicionarSeExiste(numeros, papeis, 1, "papel.referido");
        if (numeros.size() >= 3) {
            adicionarSeExiste(numeros, papeis, 2, "papel.diferenca");
        }

        if (temInterrogacaoTextual(texto) || numeros.size() < 3) {
            adicionarDesconhecidoSeAusente(papeis, papelIncognita(texto, CategoriaProblema.COMPARACAO_MEDIDAS, numeros, "papel.referido"));
        }
    }

    private int indiceNumeroDaDiferencaDeclarada(String texto, List<NumeroEncontrado> numeros) {
        int melhorIndice = -1;
        int melhorDistancia = Integer.MAX_VALUE;
        int indiceExpressao = indicePrimeiraExpressaoComparativa(texto);

        if (indiceExpressao < 0) {
            return -1;
        }

        for (int i = 0; i < numeros.size(); i++) {
            NumeroEncontrado numero = numeros.get(i);

            if (numero.getPosicaoFinal() <= indiceExpressao) {
                int distancia = indiceExpressao - numero.getPosicaoFinal();

                if (distancia < melhorDistancia) {
                    melhorDistancia = distancia;
                    melhorIndice = i;
                }
            }
        }

        return melhorIndice;
    }

    private int primeiroIndiceDiferente(List<NumeroEncontrado> numeros, int indiceIgnorado) {
        for (int i = 0; i < numeros.size(); i++) {
            if (i != indiceIgnorado) {
                return i;
            }
        }
        return -1;
    }

    private String papelDaMedidaConhecidaNaComparacao(String texto, List<NumeroEncontrado> numeros, int indiceDiferenca, int indiceMedidaConhecida) {
        if (indiceDiferenca == 0) {
            return "papel.referente";
        }

        if (referenteTextualJaApareceuAntesDaDiferenca(texto, numeros, indiceDiferenca)) {
            return "papel.referente";
        }

        return "papel.referido";
    }

    private boolean referenteTextualJaApareceuAntesDaDiferenca(String texto, List<NumeroEncontrado> numeros, int indiceDiferenca) {
        int indiceExpressao = indicePrimeiraExpressaoComparativa(texto);
        String expressao = expressaoComparativaEncontrada(texto);

        if (indiceExpressao < 0 || expressao == null || indiceDiferenca < 0 || indiceDiferenca >= numeros.size()) {
            return false;
        }

        int inicioNome = indiceExpressao + expressao.length();
        String nome = primeiraPalavraDepois(texto, inicioNome);

        if (nome.length() < 2) {
            return false;
        }

        int limite = numeros.get(indiceDiferenca).getPosicaoInicial();
        if (limite <= 0 || limite > texto.length()) {
            limite = Math.min(Math.max(limite, 0), texto.length());
        }

        String prefixo = texto.substring(0, limite);
        return contemPalavra(prefixo, nome);
    }

    private void adicionarPapeisComparacaoNaOrdemDoTexto(
            List<NumeroEncontrado> numeros,
            List<PapelElementoInterpretado> papeis,
            int indiceDiferenca,
            int indiceMedidaConhecida,
            String papelMedidaConhecida
    ) {
        for (int i = 0; i < numeros.size(); i++) {
            if (i == indiceDiferenca) {
                adicionarSeExiste(numeros, papeis, i, "papel.diferenca");
            } else if (i == indiceMedidaConhecida) {
                adicionarSeExiste(numeros, papeis, i, papelMedidaConhecida);
            }
        }
    }

    private int indicePrimeiraExpressaoComparativa(String texto) {
        int melhor = -1;

        for (String expressao : expressoesComparativasDeclaradas()) {
            int indice = texto.indexOf(expressao);

            if (indice >= 0 && (melhor < 0 || indice < melhor)) {
                melhor = indice;
            }
        }

        return melhor;
    }

    private String expressaoComparativaEncontrada(String texto) {
        int melhorIndice = -1;
        String melhorExpressao = null;

        for (String expressao : expressoesComparativasDeclaradas()) {
            int indice = texto.indexOf(expressao);

            if (indice >= 0 && (melhorIndice < 0 || indice < melhorIndice)) {
                melhorIndice = indice;
                melhorExpressao = expressao;
            }
        }

        return melhorExpressao;
    }

    private String[] expressoesComparativasDeclaradas() {
        return new String[] {
                "a mais que", "a menos que", "mais velho que", "mais velha que", "mais novo que", "mais nova que", "mais que", "menos que",
                "more than", "less than", "fewer than", "older than", "younger than",
                "de plus que", "de moins que", "plus vieux que", "plus vieille que", "plus jeune que", "plus que", "moins que"
        };
    }

    private String primeiraPalavraDepois(String texto, int inicio) {
        if (inicio < 0 || inicio >= texto.length()) {
            return "";
        }

        String trecho = texto.substring(inicio).trim();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("[a-zA-ZçÇáàãâéêíóôõúüñÁÀÃÂÉÊÍÓÔÕÚÜÑ]+|[a-zA-Z]+(?:'[a-zA-Z]+)?").matcher(trecho);

        if (matcher.find()) {
            return matcher.group().toLowerCase();
        }

        return "";
    }

    private boolean contemPalavra(String texto, String palavra) {
        if (palavra == null || palavra.length() == 0) {
            return false;
        }

        java.util.regex.Pattern padrao = java.util.regex.Pattern.compile("(^|[^a-zA-ZçÇáàãâéêíóôõúüñÁÀÃÂÉÊÍÓÔÕÚÜÑ])" + java.util.regex.Pattern.quote(palavra) + "([^a-zA-ZçÇáàãâéêíóôõúüñÁÀÃÂÉÊÍÓÔÕÚÜÑ]|$)");
        return padrao.matcher(texto).find();
    }

    private void inferirComposicaoTransformacoes(List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis) {
        adicionarSeExiste(numeros, papeis, 0, "papel.transformacao1");
        adicionarSeExiste(numeros, papeis, 1, "papel.transformacao2");
        if (numeros.size() >= 3) {
            adicionarSeExiste(numeros, papeis, 2, "papel.transformacaoFinal");
        } else {
            adicionarDesconhecidoSeAusente(papeis, "papel.transformacaoFinal");
        }
    }

    private void inferirTransformacaoCompostaDoisPassos(String texto, List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis) {
        adicionarSeExiste(numeros, papeis, 0, "papel.estadoInicial");
        adicionarSeExiste(numeros, papeis, 1, "papel.transformacao1");
        adicionarSeExiste(numeros, papeis, 2, "papel.transformacao2");
        if (numeros.size() >= 4) {
            adicionarSeExiste(numeros, papeis, 3, "papel.estadoFinal");
        }

        if (temInterrogacaoTextual(texto) || numeros.size() < 4) {
            adicionarDesconhecidoSeAusente(papeis, papelIncognita(texto, CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, numeros, "papel.estadoFinal"));
        }
    }

    private void inferirTransformacaoRelacao(List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis) {
        adicionarSeExiste(numeros, papeis, 0, "papel.relacaoInicial");
        adicionarSeExiste(numeros, papeis, 1, "papel.transformacao");
        if (numeros.size() >= 3) {
            adicionarSeExiste(numeros, papeis, 2, "papel.relacaoFinal");
        } else {
            adicionarDesconhecidoSeAusente(papeis, "papel.relacaoFinal");
        }
    }

    private void inferirComposicaoRelacoes(List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis) {
        adicionarSeExiste(numeros, papeis, 0, "papel.relacao1");
        adicionarSeExiste(numeros, papeis, 1, "papel.relacao2");
        if (numeros.size() >= 3) {
            adicionarSeExiste(numeros, papeis, 2, "papel.relacaoFinal");
        } else {
            adicionarDesconhecidoSeAusente(papeis, "papel.relacaoFinal");
        }
    }

    private void inferirSequencialGenerico(List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis) {
        for (int i = 0; i < numeros.size(); i++) {
            adicionarSeExiste(numeros, papeis, i, "papel.valor" + (i + 1));
        }
    }


    private String papelIncognita(String texto, CategoriaProblema categoria, List<NumeroEncontrado> numeros, String fallback) {
        ResultadoIncognitaLinguistica resultado = inferidorIncognita.inferir(texto, categoria, numeros);
        if (resultado != null && resultado.getChavePapel() != null && resultado.getChavePapel().trim().length() > 0 && !"papel.valor".equals(resultado.getChavePapel())) {
            return resultado.getChavePapel();
        }
        return fallback;
    }
    private boolean temInterrogacaoTextual(String texto) {
        return texto != null && texto.indexOf('?') >= 0;
    }

    private String papelIncognitaComposicao(String texto) {
        if (contemAlgum(texto, "ao todo", "no total", "juntos", "juntas", "existem", "quantos", "quantas", "in all", "altogether", "total", "en tout", "au total", "ensemble")) {
            return "papel.todo";
        }
        return "papel.todo";
    }

    private String papelIncognitaTransformacao(String texto) {
        if (perguntaTransformacao(texto) || contemAlgum(texto, "quanto ganhou", "quanto perdeu", "qual foi a transformacao", "qual foi a transformação", "mudanca", "mudança")) {
            return "papel.transformacao";
        }
        if (perguntaEstadoInicial(texto) || contemAlgum(texto, "inicialmente", "no inicio", "no início", "antes")) {
            return "papel.estadoInicial";
        }
        if (contemAlgum(texto, "agora", "ficou", "ficaram", "restou", "restaram", "sobrou", "sobraram", "tem agora", "existem agora", "ao final", "final")) {
            return "papel.estadoFinal";
        }
        return "papel.estadoFinal";
    }

    private String papelIncognitaComparacao(String texto) {
        if (contemAlgum(texto, "a mais", "a menos", "diferenca", "diferença", "quanto mais", "quanto menos")) {
            return "papel.diferenca";
        }
        return "papel.referido";
    }

    private boolean perguntaTotal(String texto) {
        return contemAlgum(texto, "ao todo", "no total", "juntos", "juntas", "in all", "altogether", "total", "en tout", "au total", "ensemble");
    }

    private boolean perguntaTransformacao(String texto) {
        return contemAlgum(texto, "o que aconteceu", "que aconteceu", "what happened", "que s'est-il passe", "que sest il passe", "s'est-il passe", "happened");
    }

    private boolean perguntaEstadoInicial(String texto) {
        return contemAlgum(texto, "tinha antes", "possuia antes", "quanto tinha antes", "how many did", "have before", "had before", "combien avait", "avant");
    }

    private boolean comparacaoPorDiferencaDeclarada(String texto) {
        return contemAlgum(texto,
                "a mais que", "a menos que", "mais velho que", "mais velha que", "mais novo que", "mais nova que",
                "more than", "less than", "fewer than", "older than", "younger than",
                "de plus que", "de moins que", "plus vieux que", "plus vieille que", "plus jeune que", "plus que", "moins que");
    }

    private boolean contemAlgum(String texto, String... expressoes) {
        for (String expressao : expressoes) {
            if (texto.contains(expressao)) {
                return true;
            }
        }
        return false;
    }

    private void adicionarSeExiste(List<NumeroEncontrado> numeros, List<PapelElementoInterpretado> papeis, int indice, String chavePapel) {
        if (indice >= 0 && indice < numeros.size()) {
            papeis.add(new PapelElementoInterpretado(numeros.get(indice).getValorCanonico(), chavePapel, true));
        }
    }

    private void adicionarDesconhecido(List<PapelElementoInterpretado> papeis, String chavePapel) {
        papeis.add(new PapelElementoInterpretado("?", chavePapel, false));
    }

    private void adicionarDesconhecidoSeAusente(List<PapelElementoInterpretado> papeis, String chavePapel) {
        for (int i = 0; i < papeis.size(); i++) {
            PapelElementoInterpretado papel = papeis.get(i);
            if (!papel.isConhecido() && SimboloDesconhecido.eh(papel.getElemento())) {
                return;
            }
        }
        adicionarDesconhecido(papeis, chavePapel);
    }
}
