package gerard.agente.modelador;

/**
 * Cobre a heurística de sugestão de invariante operatório: categorias com
 * fórmula própria e inequívoca no catálogo devem sugerir; as demais devem
 * devolver null de propósito (sem forçar analogia).
 */
public final class TesteSugestorInvarianteOperatorio {
    private static int verificacoes;

    public static void main(String[] args) {
        SugestorInvarianteOperatorio sugestor = new SugestorInvarianteOperatorio();

        confirmar("COMP_CARDINAL_TODO".equals(sugestor.sugerirCodigo("COMPOSICAO_MEDIDAS", "papel.todo")),
                "Composição de Medidas, incógnita = todo");
        confirmar("COMP_CARDINAL_TODO".equals(sugestor.sugerirCodigo("COMPOSICAO_MEDIDAS", "papel.parte1")),
                "Composição de Medidas, incógnita = parte1");

        confirmar("TRANS_ADITIVA".equals(sugestor.sugerirCodigo("TRANSFORMACAO_MEDIDAS", "papel.estadoFinal")),
                "Transformação de Medidas, incógnita = estado final");
        confirmar("TRANS_DIFERENCA".equals(sugestor.sugerirCodigo("TRANSFORMACAO_MEDIDAS", "papel.transformacao")),
                "Transformação de Medidas, incógnita = transformação");
        confirmar("TRANS_INVERSA_ADITIVA".equals(sugestor.sugerirCodigo("TRANSFORMACAO_MEDIDAS", "papel.estadoInicial")),
                "Transformação de Medidas, incógnita = estado inicial");

        confirmar("COMPAR_ARTIGO_01".equals(sugestor.sugerirCodigo("COMPARACAO_MEDIDAS", "papel.referendo")),
                "Comparação de Medidas, incógnita = referendo");
        confirmar("COMPAR_RELACAO".equals(sugestor.sugerirCodigo("COMPARACAO_MEDIDAS", "papel.diferenca")),
                "Comparação de Medidas, incógnita = diferença");

        confirmar("COMP_CARDINAL_TODO".equals(sugestor.sugerirCodigo("COMPOSICAO_TRANSFORMACAO_MEDIDAS", "papel.todo")),
                "híbrido Composição+Transformação, incógnita = todo");
        confirmar("TRANS_ADITIVA".equals(sugestor.sugerirCodigo("COMPOSICAO_TRANSFORMACAO_MEDIDAS", "papel.estadoFinal")),
                "híbrido Composição+Transformação, incógnita = estado final");

        confirmar(sugestor.sugerirCodigo("COMPOSICAO_TRANSFORMACOES", "papel.transformacaoFinal") == null,
                "Composição de Transformações não tem fórmula própria — não deve sugerir");
        confirmar(sugestor.sugerirCodigo("TRANSFORMACAO_COMPOSTA_DOIS_PASSOS", "papel.estadoFinal") == null,
                "Transformação Composta em Dois Passos não tem fórmula própria — não deve sugerir");
        confirmar(sugestor.sugerirCodigo("TRANSFORMACAO_RELACAO", "papel.relacaoFinal") == null,
                "Transformação de uma Relação não tem fórmula própria — não deve sugerir");
        confirmar(sugestor.sugerirCodigo("COMPOSICAO_RELACOES", "papel.relacaoFinal") == null,
                "Composição de Relações não tem fórmula própria — não deve sugerir");

        confirmar(sugestor.sugerirCodigo(null, "papel.todo") == null, "categoria nula não deve sugerir");
        confirmar(sugestor.sugerirCodigo("COMPOSICAO_MEDIDAS", null) == null, "papel nulo não deve sugerir");
        confirmar(sugestor.sugerirCodigo("COMPOSICAO_MEDIDAS", "papel.inexistente") == null,
                "papel sem correspondência não deve sugerir");

        System.out.println("Teste do sugestor de invariante operatório aprovado: " + verificacoes + " verificações.");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
