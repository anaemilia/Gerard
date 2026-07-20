package gerard.Scaffolding.venn;

/**
 * Regra pura que limita a cardinalidade visual de um agrupamento ao valor
 * semântico curado. Também resolve o terceiro valor quando a curadoria mantém
 * um dos três papéis como incógnita e os outros dois são conhecidos.
 */
public final class ScaffoldingLimiteQuantidadeVenn {

    public boolean atingiuLimite(int quantidadeAtual, Integer limiteCurado) {
        return limiteCurado != null
                && quantidadeAtual >= Math.max(0, Math.abs(limiteCurado.intValue()));
    }

    public Integer resolverLimite(String[] chaves, Integer[] valores, int indice) {
        if (chaves == null || valores == null || chaves.length < 3
                || valores.length < 3 || indice < 0 || indice >= 3) {
            return null;
        }

        Integer[] resolvidos = new Integer[] { valores[0], valores[1], valores[2] };
        String k0 = normalizar(chaves[0]);
        String k1 = normalizar(chaves[1]);
        String k2 = normalizar(chaves[2]);

        boolean composicao = eh(k0, "papel.parte1")
                && eh(k1, "papel.parte2")
                && eh(k2, "papel.todo");
        boolean transformacao = (eh(k0, "papel.estadoInicial") || eh(k0, "papel.relacaoInicial")
                || eh(k0, "papel.estadoIntermediario"))
                && k1.startsWith("papel.transformacao")
                && (eh(k2, "papel.estadoFinal") || eh(k2, "papel.relacaoFinal")
                || eh(k2, "papel.estadoIntermediario"));
        boolean comparacao = eh(k0, "papel.referido")
                && eh(k1, "papel.diferenca")
                && eh(k2, "papel.referendo");
        boolean composicaoTransformacoes = eh(k0, "papel.transformacao1")
                && eh(k1, "papel.transformacao2")
                && eh(k2, "papel.transformacaoFinal");
        boolean composicaoRelacoes = eh(k0, "papel.relacao1")
                && eh(k1, "papel.relacao2")
                && eh(k2, "papel.relacaoFinal");

        if (composicao || transformacao || comparacao
                || composicaoTransformacoes || composicaoRelacoes) {
            resolverSoma(resolvidos);
        }

        Integer valor = resolvidos[indice];
        return valor == null ? null : Integer.valueOf(Math.abs(valor.intValue()));
    }

    private void resolverSoma(Integer[] valores) {
        Integer a = valores[0];
        Integer b = valores[1];
        Integer c = valores[2];

        if (a == null && b != null && c != null) {
            valores[0] = Integer.valueOf(c.intValue() - b.intValue());
        } else if (b == null && a != null && c != null) {
            valores[1] = Integer.valueOf(c.intValue() - a.intValue());
        } else if (c == null && a != null && b != null) {
            valores[2] = Integer.valueOf(a.intValue() + b.intValue());
        }
    }

    private boolean eh(String atual, String esperado) {
        return esperado.equals(atual);
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
