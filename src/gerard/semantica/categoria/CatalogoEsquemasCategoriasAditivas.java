package gerard.semantica.categoria;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.semantica.papel.CatalogoPapeisSemanticos;
import gerard.semantica.papel.PapelQuantitativo;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/** Fonte única dos composites semânticos das categorias aditivas. */
public final class CatalogoEsquemasCategoriasAditivas {
    private final CatalogoPapeisSemanticos papeis = new CatalogoPapeisSemanticos();
    private final Map<TipoSituacaoAditiva, EsquemaCategoriaAditiva> esquemas =
            new EnumMap<TipoSituacaoAditiva, EsquemaCategoriaAditiva>(TipoSituacaoAditiva.class);

    public CatalogoEsquemasCategoriasAditivas() {
        CategoriaSimples composicao = simples("papel.parte1", "papel.parte2", "papel.todo");
        CategoriaSimples transformacao = simples("papel.estadoInicial", "papel.transformacao", "papel.estadoFinal");
        CategoriaSimples comparacao = simples("papel.referido", "papel.diferenca", "papel.referendo");
        CategoriaSimples compTransformacoes = simples("papel.transformacao1", "papel.transformacao2", "papel.transformacaoFinal");
        CategoriaSimples transformacaoRelacao = simples("papel.relacaoInicial", "papel.transformacao", "papel.relacaoFinal");
        CategoriaSimples compRelacoes = simples("papel.relacao1", "papel.relacao2", "papel.relacaoFinal");

        registrar(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "Composição de medidas", composicao,
                lista("papel.parte1", "papel.parte2", "papel.todo"));
        registrar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "Transformação de medidas", transformacao,
                lista("papel.estadoInicial", "papel.transformacao", "papel.estadoFinal"));
        registrar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "Comparação de medidas", comparacao,
                lista("papel.referido", "papel.diferenca", "papel.referendo"));
        registrar(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, "Composição de transformações", compTransformacoes,
                lista("papel.transformacao1", "papel.transformacao2", "papel.transformacaoFinal"));
        registrar(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, "Transformação de relação", transformacaoRelacao,
                lista("papel.relacaoInicial", "papel.transformacao", "papel.relacaoFinal"));
        registrar(TipoSituacaoAditiva.COMPOSICAO_RELACOES, "Composição de relações", compRelacoes,
                lista("papel.relacao1", "papel.relacao2", "papel.relacaoFinal"));

        CategoriaComposta composicaoTransformacao = new CategoriaComposta(
                Arrays.<ComponenteCategoria>asList(composicao, transformacao));
        registrar(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACAO_MEDIDAS,
                "Composição seguida de transformação", composicaoTransformacao,
                lista("papel.estadoInicial", "papel.transformacao", "papel.estadoFinal"));

        CategoriaSimples passo1 = simples("papel.estadoInicial", "papel.transformacao1", "papel.estadoIntermediario");
        CategoriaSimples passo2 = simples("papel.estadoIntermediario", "papel.transformacao2", "papel.estadoFinal");
        CategoriaComposta doisPassos = new CategoriaComposta(
                Arrays.<ComponenteCategoria>asList(passo1, passo2));
        registrar(TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS,
                "Transformação composta em dois passos", doisPassos,
                lista("papel.estadoInicial", "papel.transformacao1", "papel.estadoIntermediario"));
    }

    public EsquemaCategoriaAditiva obter(TipoSituacaoAditiva tipo) {
        EsquemaCategoriaAditiva esquema = tipo == null ? null : esquemas.get(tipo);
        if (esquema != null) {
            return esquema;
        }
        CategoriaSimples vazio = simples("papel.parte1", "papel.parte2", "papel.todo");
        return new EsquemaCategoriaAditiva(tipo, "Categoria aditiva", vazio,
                lista("papel.parte1", "papel.parte2", "papel.todo"));
    }

    private CategoriaSimples simples(String a, String b, String c) {
        return new CategoriaSimples(lista(a, b, c),
                Arrays.asList(
                        new RestricaoSemantica("dominios", "Cada papel aceita apenas valores do seu universo numérico."),
                        new RestricaoSemantica("relacao_aditiva", "Os três papéis satisfazem A + B = C.")),
                new RelacaoSemantica(a + " + " + b + " = " + c));
    }

    private void registrar(TipoSituacaoAditiva tipo, String nome,
                           ComponenteCategoria raiz,
                           java.util.List<PapelQuantitativo> compartilhados) {
        esquemas.put(tipo, new EsquemaCategoriaAditiva(
                tipo, nome, raiz, Collections.unmodifiableList(compartilhados)));
    }

    private java.util.List<PapelQuantitativo> lista(String... chaves) {
        java.util.List<PapelQuantitativo> resultado =
                new java.util.ArrayList<PapelQuantitativo>();
        for (String chave : chaves) {
            resultado.add(papeis.obter(chave));
        }
        return resultado;
    }
}
