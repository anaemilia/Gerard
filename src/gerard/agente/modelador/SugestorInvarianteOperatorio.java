package gerard.agente.modelador;

/**
 * Sugere um código de invariante operatório (do catálogo fechado, ver
 * TelaArtefatoExplicativo) a partir da categoria e do papel semântico da
 * incógnita da tentativa — nunca a partir de texto livre, nunca inventa um
 * código novo. Decisão do usuário em 2026-07-23, com base em Vergnaud
 * (1998, seção "The role of language and symbols in representation",
 * Seta 1): "a formação de conceitos implica a identificação de objetos,
 * com suas propriedades, relações e transformações — essa é a função
 * principal dos invariantes operatórios". No Gérard, os "objetos" são os
 * papéis semânticos e suas relações já estão estruturadas pela categoria;
 * por isso categoria + papel é uma base razoável para a sugestão.
 *
 * Sinal fraco, não uma associação garantida (Setas 2/2bis do mesmo artigo:
 * "a relação entre invariantes operatórios e o lado significado de uma
 * instância linguística ou semiótica não é uma correspondência um-para-um).
 * Só pré-seleciona o combo do pesquisador — nunca salva sozinho, nunca
 * impede o pesquisador de escolher outro item ou "Inserir nova forma
 * simbólica". Cobre só as categorias cujo catálogo tem fórmulas com
 * vocabulário específico e inequívoco (Composição/Transformação/Comparação
 * de Medidas e o híbrido Composição+Transformação): para as outras quatro
 * categorias (Composição de Transformações, Transformação Composta em Dois
 * Passos, Transformação de uma Relação, Composição de Relações) devolve
 * null de propósito — o catálogo atual não tem fórmula com vocabulário
 * próprio para elas, e forçar uma analogia com os códigos de "medidas"
 * seria inventar uma correspondência que não está clara.
 */
public final class SugestorInvarianteOperatorio {

    public String sugerirCodigo(String categoria, String chavePapelIncognita) {
        if (categoria == null || chavePapelIncognita == null) {
            return null;
        }
        String papel = chavePapelIncognita.trim();
        switch (categoria.trim()) {
            case "COMPOSICAO_MEDIDAS":
                if ("papel.todo".equals(papel) || "papel.parte1".equals(papel) || "papel.parte2".equals(papel)) {
                    return "COMP_CARDINAL_TODO";
                }
                return null;
            case "TRANSFORMACAO_MEDIDAS":
                if ("papel.estadoFinal".equals(papel)) return "TRANS_ADITIVA";
                if ("papel.transformacao".equals(papel)) return "TRANS_DIFERENCA";
                if ("papel.estadoInicial".equals(papel)) return "TRANS_INVERSA_ADITIVA";
                return null;
            case "COMPOSICAO_TRANSFORMACAO_MEDIDAS":
                if ("papel.todo".equals(papel) || "papel.parte1".equals(papel) || "papel.parte2".equals(papel)) {
                    return "COMP_CARDINAL_TODO";
                }
                if ("papel.transformacao".equals(papel)) return "TRANS_DIFERENCA";
                if ("papel.estadoFinal".equals(papel)) return "TRANS_ADITIVA";
                return null;
            case "COMPARACAO_MEDIDAS":
                if ("papel.referendo".equals(papel) || "papel.referido".equals(papel)) return "COMPAR_ARTIGO_01";
                if ("papel.diferenca".equals(papel)) return "COMPAR_RELACAO";
                return null;
            default:
                // COMPOSICAO_TRANSFORMACOES, TRANSFORMACAO_COMPOSTA_DOIS_PASSOS,
                // TRANSFORMACAO_RELACAO, COMPOSICAO_RELACOES: sem fórmula própria
                // no catálogo hoje — sem sugestão, de propósito.
                return null;
        }
    }
}
