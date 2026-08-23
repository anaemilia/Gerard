import gerard.dominio.campoaditivo.CatalogoExplicacoesConceituaisPapel;
import gerard.i18n.ServicoLocalizacao;
import gerard.idioma.IdiomaInterface;

/**
 * Harness executável de CatalogoExplicacoesConceituaisPapel — decisão da
 * usuária sobre o item 1 do levantamento de pendências de 2026-08-11
 * (AG_EME): a explicação conceitual do papel manipulado via protocolo de
 * mouse mora no objeto rico (PapelQuantitativo/FabricaPapeis*), Parte1 e
 * Parte2 compartilham o mesmo texto conceitual ("Parte"), e o conteúdo é
 * escrito nas 4 línguas da interface.
 *
 * Não toca em Main.java nem em nenhum caminho de produção — só exercita o
 * catálogo e a resolução i18n das chaves que ele devolve.
 */
public final class TesteCatalogoExplicacoesConceituaisPapel {

    public static void main(String[] args) {
        System.out.println("=== Cada papel vivo resolve para uma explicação específica (não a genérica) ===");
        String[] chavesVivasComExplicacaoEspecifica = {
                "papel.parte1", "papel.parte2", "papel.todo",
                "papel.estadoInicial", "papel.transformacao", "papel.estadoFinal",
                "papel.referido", "papel.referendo", "papel.referente", "papel.diferenca",
                "papel.transformacao1", "papel.transformacao2", "papel.transformacaoFinal",
                "papel.relacaoInicial", "papel.relacaoFinal", "papel.relacao1", "papel.relacao2",
        };
        for (String chave : chavesVivasComExplicacaoEspecifica) {
            String explicacao = CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao(chave);
            checar("chave de explicação de " + chave + " não é a genérica",
                    String.valueOf(!CatalogoExplicacoesConceituaisPapel.CHAVE_EXPLICACAO_GENERICA.equals(explicacao)),
                    "true");
        }

        System.out.println();
        System.out.println("=== Decisão da usuária (2026-08-16): Parte1 e Parte2 compartilham o mesmo texto conceitual ===");
        checar("Parte1 e Parte2 resolvem para a mesma chave de explicação",
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.parte1"),
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.parte2"));
        checar("Todo é conceitualmente diferente de Parte",
                String.valueOf(CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.todo")
                        .equals(CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.parte1"))),
                "false");

        System.out.println();
        System.out.println("=== Transformação é o mesmo conceito nos três esquemas que a usam ===");
        String explicacaoTransformacao = CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.transformacao");
        checar("Transformação (Transformação de Medidas/Relação) == Transformação1 (Composição de Transformações)",
                explicacaoTransformacao,
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.transformacao1"));
        checar("Transformação1 == Transformação2",
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.transformacao1"),
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.transformacao2"));
        checar("Transformação Final é conceitualmente diferente de Transformação (resultado da composição, não um insumo)",
                String.valueOf(CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.transformacaoFinal")
                        .equals(explicacaoTransformacao)),
                "false");

        System.out.println();
        System.out.println("=== Relação1/Relação2 compartilham texto; Relação Final é o resultado (diferente) ===");
        checar("Relação1 e Relação2 resolvem para a mesma chave de explicação",
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.relacao1"),
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.relacao2"));
        checar("Relação Final é conceitualmente diferente de Relação1",
                String.valueOf(CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.relacaoFinal")
                        .equals(CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.relacao1"))),
                "false");
        checar("Relação Final é o mesmo conceito venha de Transformação de Relação ou de Composição de Relações "
                        + "(mesma chave viva \"papel.relacaoFinal\" nos dois esquemas)",
                String.valueOf(CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.relacaoFinal") != null),
                "true");

        System.out.println();
        System.out.println("=== Sinônimos vivos sem fábrica própria resolvem para o mesmo conceito do papel real ===");
        checar("\"papel.referente\" resolve para a mesma explicação de \"papel.referendo\" (sinônimo histórico)",
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.referente"),
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.referendo"));
        checar("\"papel.diferenca\" (chave viva) tem explicação própria, diferente de Referido/Referendo",
                String.valueOf(CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.diferenca")
                        .equals(CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.referido"))),
                "false");

        System.out.println();
        System.out.println("=== Chave desconhecida ou nula cai no fallback genérico, nunca lança exceção ===");
        checar("chave nunca vista cai no fallback genérico",
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.isto_nao_existe"),
                CatalogoExplicacoesConceituaisPapel.CHAVE_EXPLICACAO_GENERICA);
        checar("chave nula cai no fallback genérico (não lança NullPointerException)",
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao(null),
                CatalogoExplicacoesConceituaisPapel.CHAVE_EXPLICACAO_GENERICA);
        checar("papel.valor (fallback histórico de obterPapelIncognitaAtual quando não há incógnita) "
                        + "também cai no fallback genérico",
                CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao("papel.valor"),
                CatalogoExplicacoesConceituaisPapel.CHAVE_EXPLICACAO_GENERICA);

        System.out.println();
        System.out.println("=== Toda chave de explicação devolvida resolve para texto real nas 4 línguas da interface ===");
        ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
        try {
            java.util.LinkedHashSet<String> chavesDeExplicacao = new java.util.LinkedHashSet<>();
            chavesDeExplicacao.add(CatalogoExplicacoesConceituaisPapel.CHAVE_EXPLICACAO_GENERICA);
            for (String chave : chavesVivasComExplicacaoEspecifica) {
                chavesDeExplicacao.add(CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao(chave));
            }
            checar("catálogo produz um número plausível de chaves de explicação distintas (entre 10 e 13)",
                    String.valueOf(chavesDeExplicacao.size() >= 10 && chavesDeExplicacao.size() <= 13), "true");

            for (IdiomaInterface idioma : IdiomaInterface.values()) {
                loc.definirIdioma(idioma);
                for (String chaveExplicacao : chavesDeExplicacao) {
                    String texto = loc.texto(chaveExplicacao);
                    checar("[" + idioma + "] " + chaveExplicacao + " resolve para texto não vazio e diferente da própria chave",
                            String.valueOf(texto != null && texto.trim().length() > 0 && !texto.equals(chaveExplicacao)),
                            "true");
                }
            }
        } finally {
            loc.definirIdioma(IdiomaInterface.PORTUGUES);
        }

        System.out.println();
        System.out.println("TODOS OS TESTES DE CatalogoExplicacoesConceituaisPapel PASSARAM.");
    }

    private static void checar(String rotulo, String obtido, String esperado) {
        if (!esperado.equals(obtido)) {
            throw new AssertionError(rotulo + ": esperado [" + esperado + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
