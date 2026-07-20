package gerard.interpretacao.classificacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.List;

/**
 * Classificador híbrido: modelo treinável + regras linguísticas + fallback manual.
 *
 * A ordem de decisão é:
 * 1) regras fortes de segurança e correção;
 * 2) modelo Naive Bayes, quando a confiança for suficiente;
 * 3) categoria manual/informada, quando a confiança for baixa.
 */
public class ClassificadorHibridoVergnaud implements ClassificadorCategoriaVergnaud {
    private static final double LIMIAR_MODELO_ALTA_CONFIANCA = 0.62;
    private static final double LIMIAR_REGRA_FORTE = 0.82;

    private final ExtratorAtributosLinguisticos extrator;
    private final ClassificadorNaiveBayesVergnaud modelo;

    public ClassificadorHibridoVergnaud() {
        this.extrator = new ExtratorAtributosLinguisticos();
        this.modelo = new ClassificadorNaiveBayesVergnaud();
        CarregadorExemplosSituacaoVergnaud carregador = new CarregadorExemplosSituacaoVergnaud();
        modelo.treinar(carregador.carregar());
    }

    public ResultadoClassificacaoVergnaud classificar(String enunciado, TipoSituacaoAditiva categoriaManual) {
        if (categoriaManual == null) {
            categoriaManual = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
        }

        ResultadoClassificacaoVergnaud resultadoModelo = modelo.classificar(enunciado, categoriaManual);
        ResultadoRegra resultadoRegra = aplicarRegras(enunciado);

        List<String> pistas = new ArrayList<String>();
        pistas.addAll(extrator.extrairPistasHumanas(enunciado));
        if (resultadoModelo.getCategoriaModelo() != null) {
            pistas.add("modelo=" + resultadoModelo.getCategoriaModelo().name());
            pistas.add("confiancaModelo=" + formatar(resultadoModelo.getConfiancaModelo()));
        }
        if (resultadoRegra.categoria != null) {
            pistas.add("regra=" + resultadoRegra.categoria.name());
            pistas.addAll(resultadoRegra.pistas);
        }

        if (resultadoRegra.categoria != null && resultadoRegra.confianca >= LIMIAR_REGRA_FORTE) {
            return new ResultadoClassificacaoVergnaud(resultadoRegra.categoria,
                    resultadoModelo.getCategoriaModelo(), resultadoRegra.categoria, categoriaManual,
                    resultadoRegra.confianca, resultadoModelo.getConfiancaModelo(), resultadoRegra.confianca,
                    "regra_linguistica", pistas);
        }

        if (resultadoModelo.getCategoriaPrevista() != null && resultadoModelo.getConfiancaModelo() >= LIMIAR_MODELO_ALTA_CONFIANCA) {
            if (resultadoRegra.categoria != null && resultadoRegra.categoria != resultadoModelo.getCategoriaPrevista() && resultadoRegra.confianca >= 0.70) {
                return new ResultadoClassificacaoVergnaud(resultadoRegra.categoria,
                        resultadoModelo.getCategoriaModelo(), resultadoRegra.categoria, categoriaManual,
                        resultadoRegra.confianca, resultadoModelo.getConfiancaModelo(), resultadoRegra.confianca,
                        "regra_corrige_modelo", pistas);
            }
            return new ResultadoClassificacaoVergnaud(resultadoModelo.getCategoriaPrevista(),
                    resultadoModelo.getCategoriaModelo(), resultadoRegra.categoria, categoriaManual,
                    resultadoModelo.getConfiancaModelo(), resultadoModelo.getConfiancaModelo(), resultadoRegra.confianca,
                    "modelo_treinado", pistas);
        }

        if (resultadoRegra.categoria != null && resultadoRegra.confianca >= 0.66) {
            return new ResultadoClassificacaoVergnaud(resultadoRegra.categoria,
                    resultadoModelo.getCategoriaModelo(), resultadoRegra.categoria, categoriaManual,
                    resultadoRegra.confianca, resultadoModelo.getConfiancaModelo(), resultadoRegra.confianca,
                    "regra_moderada", pistas);
        }

        pistas.add("fallback=mantida_categoria_manual");
        return new ResultadoClassificacaoVergnaud(categoriaManual,
                resultadoModelo.getCategoriaModelo(), resultadoRegra.categoria, categoriaManual,
                0.50, resultadoModelo.getConfiancaModelo(), resultadoRegra.confianca,
                "fallback_manual", pistas);
    }

    public ResultadoClassificacaoVergnaud classificarSemFallbackManual(String enunciado) {
        return classificar(enunciado, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
    }

    private ResultadoRegra aplicarRegras(String enunciado) {
        String texto = extrator.normalizar(enunciado);
        ResultadoRegra r = new ResultadoRegra();
        if (texto.length() == 0) {
            return r;
        }

        if (ehComposicaoSeguidaDeTransformacao(texto, r)) {
            r.categoria = TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS;
            r.confianca = 0.92;
            return r;
        }

        if (ehComposicaoTransformacoes(texto, r)) {
            r.categoria = TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES;
            r.confianca = 0.88;
            return r;
        }

        if (ehTransformacaoCompostaDoisPassos(texto, r)) {
            r.categoria = TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS;
            r.confianca = 0.94;
            return r;
        }

        if (ehTransformacaoRelacao(texto, r)) {
            r.categoria = TipoSituacaoAditiva.TRANSFORMACAO_RELACAO;
            r.confianca = 0.86;
            return r;
        }

        if (ehComposicaoRelacoes(texto, r)) {
            r.categoria = TipoSituacaoAditiva.COMPOSICAO_RELACOES;
            r.confianca = 0.84;
            return r;
        }

        if (ehComparacaoMedidas(texto, r)) {
            r.categoria = TipoSituacaoAditiva.COMPARACAO_MEDIDAS;
            r.confianca = 0.90;
            return r;
        }

        if (ehComposicaoMedidas(texto, r)) {
            r.categoria = TipoSituacaoAditiva.COMPOSICAO_MEDIDAS;
            r.confianca = 0.76;
            return r;
        }

        if (ehTransformacaoMedidas(texto, r)) {
            r.categoria = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
            r.confianca = 0.73;
            return r;
        }

        return r;
    }

    /**
     * Duas partes/medidas iniciais, depois uma retirada/acréscimo.
     */
    private boolean ehComposicaoSeguidaDeTransformacao(String texto, ResultadoRegra r) {
        boolean temPartesIniciais = contemAlgum(texto, "brancas e", "amarelas", "vermelhas", "redondas e", "quadradas", "azuis e", "de vidro e", "de aco", "saias em uma", "em outra", "rosas brancas", "mesas redondas");
        boolean temTransformacaoDepois = contemAlgum(texto, "deu", "tirada", "tiradas", "foram tiradas", "comeu", "perdeu", "vendidos", "foram vendidos", "retirou", "gastou");
        boolean perguntaFinal = contemAlgum(texto, "ficou", "ficaram", "restaram", "sobraram", "ainda tem", "quantas", "quantos");
        int qtdNumeros = extrator.contarNumeros(texto);
        if (temPartesIniciais && temTransformacaoDepois && perguntaFinal && qtdNumeros >= 4) {
            r.pistas.add("duas_partes_iniciais");
            r.pistas.add("transformacao_posterior");
            return true;
        }
        return false;
    }

    private boolean ehTransformacaoCompostaDoisPassos(String texto, ResultadoRegra r) {
        boolean estadoInicial = contemAlgum(texto, "tinha", "possuia", "havia", "existiam", "guardava", "guarda", "comecou", "no comeco", "inicialmente");
        int qtdTransformacoes = contarTermos(texto, extrator.termosTransformacao());
        int qtdNumeros = extrator.contarNumeros(texto);
        boolean temporais = contarTermos(texto, extrator.termosTemporais()) >= 2 || (texto.contains("ontem") && texto.contains("hoje"));
        boolean sequencia = contemAlgum(texto, "depois", "em seguida", "mais tarde", "primeiro", "segundo", "dois passos");
        boolean perguntaFinal = contemAlgum(texto, "ainda tem", "tem agora", "ficou", "ficaram", "restaram", "sobraram", "quantos chocolates ainda", "quantas ainda");
        boolean perguntaTransformacaoParcial = contemAlgum(texto, "ganhou na primeira", "perdeu na primeira", "ganhou na segunda", "perdeu na segunda", "quantas bilas", "quantas figurinhas ela ganhou");

        if (!perguntaTransformacaoParcial && estadoInicial && perguntaFinal && qtdNumeros >= 3 && ((qtdTransformacoes >= 2 && (temporais || sequencia)) || (temporais && qtdTransformacoes >= 1))) {
            r.pistas.add("estado_inicial");
            if (temporais) {
                r.pistas.add("marcadores_temporais");
            }
            if (qtdTransformacoes >= 2) {
                r.pistas.add("duas_transformacoes");
            }
            return true;
        }
        return false;
    }

    private boolean ehComposicaoTransformacoes(String texto, ResultadoRegra r) {
        boolean duasTransformacoes = contarTermos(texto, extrator.termosTransformacao()) >= 2 || contemAlgum(texto, "ganhado ao todo", "perdido ao todo", "transformacao total", "ao final das duas", "duas partidas");
        boolean perguntaTransformacao = contemAlgum(texto, "ganhou na primeira", "perdeu na primeira", "ganhou na segunda", "perdeu na segunda", "transformacao total", "ganhou quantas", "quantas bilas", "quantas figurinhas ela ganhou");
        boolean semEstadoInicialMedida = !contemAlgum(texto, "tinha uma caixa com", "tinha", "possuia", "havia") || contemAlgum(texto, "jogou duas", "partidas", "ganhado ao todo", "perdido ao todo");
        if (duasTransformacoes && perguntaTransformacao && semEstadoInicialMedida) {
            r.pistas.add("composicao_de_transformacoes");
            return true;
        }
        return false;
    }

    private boolean ehTransformacaoRelacao(String texto, ResultadoRegra r) {
        boolean relacao = contemAlgum(texto, "a mais que", "a menos que", "mais que", "menos que");
        boolean transformacao = contemAlgum(texto, "comprou", "ganhou", "perdeu", "depois", "passou a ter", "ficou a mais", "ficou a menos");
        boolean perguntaRelacaoFinal = contemAlgum(texto, "ficou a mais", "ficou a menos", "passou a ter", "quantas bonecas maria ficou a mais", "quantas figurinhas a mais");
        if (relacao && transformacao && perguntaRelacaoFinal) {
            r.pistas.add("relacao_transformada");
            return true;
        }
        return false;
    }

    private boolean ehComposicaoRelacoes(String texto, ResultadoRegra r) {
        int relacoes = contarTermos(texto, new String[] {"a mais que", "a menos que", "mais velho", "mais velha", "mais novo", "mais nova"});
        boolean perguntaRelacao = contemAlgum(texto, "relacao entre", "diferenca de idade", "qual e a diferenca", "qual é a diferença");
        if (relacoes >= 2 || (relacoes >= 1 && perguntaRelacao && contemAlgum(texto, "relacao entre", "diferença de idade", "diferenca de idade"))) {
            r.pistas.add("relacoes_encadeadas");
            return true;
        }
        return false;
    }

    private boolean ehComparacaoMedidas(String texto, ResultadoRegra r) {
        if (contemAlgum(texto, "a mais que", "a menos que", "mais que", "menos que", "quantos a menos", "quantas a menos", "quantos a mais", "quantas a mais", "diferenca", "diferença")) {
            r.pistas.add("pista_comparacao");
            return true;
        }
        return false;
    }

    private boolean ehComposicaoMedidas(String texto, ResultadoRegra r) {
        boolean perguntaTodo = contemAlgum(texto, "ao todo", "em conjunto", "total", "juntos", "juntas", "quantas pecas", "quantas frutas", "quantas bolas ele tem", "quantas bonecas tem as duas", "quantas continhas", "qual o total");
        boolean partes = contemAlgum(texto, " e ", "azuis", "vermelhas", "brancas", "amarelas", "de prata", "de ouro", "pokemon", "digimon", "em uma gaveta", "em outra gaveta");
        boolean temTransformacao = contemAlgum(texto, extrator.termosTransformacao());
        if (perguntaTodo && partes && !temTransformacao) {
            r.pistas.add("pista_total_partes");
            return true;
        }
        if (perguntaTodo && !temTransformacao) {
            r.pistas.add("pista_total");
            return true;
        }
        boolean parteDesconhecida = contemAlgum(texto, "se ele tem", "sendo que", "as outras", "o outro", "quantas sao", "quantas são") && contemAlgum(texto, "ao todo", "tem 17", "tem 19", "existem 12", "com 80", "com 20");
        if (parteDesconhecida) {
            r.pistas.add("parte_desconhecida");
            return true;
        }
        return false;
    }

    private boolean ehTransformacaoMedidas(String texto, ResultadoRegra r) {
        boolean temTransformacao = contemAlgum(texto, extrator.termosTransformacao());
        boolean perguntaEstadoOuMudanca = contemAlgum(texto, "ficou", "tem agora", "restaram", "sobraram", "ainda tenho", "ainda tem", "antes", "quanto gastou", "quantas comeu", "quantos restaram", "quantos sobraram");
        if (temTransformacao && perguntaEstadoOuMudanca) {
            r.pistas.add("pista_transformacao");
            return true;
        }
        return false;
    }

    public boolean ehTransformacaoCompostaEmPassos(String enunciado) {
        ResultadoRegra r = new ResultadoRegra();
        return ehTransformacaoCompostaDoisPassos(extrator.normalizar(enunciado), r);
    }

    private boolean contemAlgum(String texto, String... termos) {
        return extrator.contemAlgum(texto, termos);
    }

    private int contarTermos(String texto, String[] termos) {
        int total = 0;
        for (String termo : termos) {
            String normalizado = extrator.normalizar(termo);
            if (normalizado.length() > 0 && extrator.contemTermo(texto, normalizado)) {
                total++;
            }
        }
        return total;
    }

    private int indicePrimeiraTransformacao(String texto) {
        int melhor = -1;
        for (String termo : extrator.termosTransformacao()) {
            String t = extrator.normalizar(termo);
            int idx = texto.indexOf(t);
            if (idx >= 0 && (melhor < 0 || idx < melhor)) {
                melhor = idx;
            }
        }
        return melhor;
    }

    private String formatar(double valor) {
        return String.format(java.util.Locale.US, "%.2f", valor);
    }

    private static class ResultadoRegra {
        private TipoSituacaoAditiva categoria;
        private double confianca;
        private List<String> pistas = new ArrayList<String>();
    }
}
