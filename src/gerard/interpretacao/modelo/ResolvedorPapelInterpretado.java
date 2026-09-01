package gerard.interpretacao.modelo;

import gerard.interpretacao.simbolo.SimboloDesconhecido;

import java.util.List;

/**
 * Resolve a chave de papel semântico associada a um elemento textual do
 * enunciado, a partir de {@link ResultadoInterpretacao} — por valor
 * (comparando com o texto do elemento), por índice posicional na lista de
 * papéis interpretados, ou reduzindo uma chave já obtida à sua forma
 * canônica (sem sufixo numérico de instância, ex.: "papel.parte1" →
 * "papel.parte").
 *
 * Extraído de {@code Main.TelaGerard} em 2026-09-01 (ver
 * {@code LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md}, item P0
 * "resolução de papéis por posição, índice ou valor textual"): esta lógica
 * não dependia de Swing, geometria ou qualquer estado de instância de
 * {@code TelaGerard} além do próprio {@link ResultadoInterpretacao} —
 * apenas não tinha, até então, um proprietário fora da tela. `Main`
 * preserva wrappers de mesmo nome que apenas repassam
 * {@code resultadoInterpretacao} para estes métodos estáticos; nenhum
 * ponto de chamada existente foi alterado.
 *
 * Não decide qual índice ou valor consultar — isso continua sendo decidido
 * por quem chama (ex.: a posição de um elemento na lista de elementos de
 * texto arrastáveis), que pode depender do desenho ou da categoria ativa.
 */
public final class ResolvedorPapelInterpretado {

    private ResolvedorPapelInterpretado() {
    }

    /**
     * A interrogação representa o item desconhecido, mas não constitui um
     * papel semântico próprio. Quando a associação direta resultar em
     * "papel.valor", recupera-se da curadoria o papel efetivamente ocupado
     * pela incógnita (estado inicial, transformação, estado final, parte 1,
     * parte 2, referido, referendo, valor relativo ou todo).
     */
    public static String aplicarFallbackCuradoItemDesconhecido(
            ResultadoInterpretacao resultadoInterpretacao, String chavePapel) {
        if (!"papel.valor".equals(chavePapel)) {
            return chavePapel;
        }
        if (resultadoInterpretacao != null && resultadoInterpretacao.getPapeis() != null) {
            List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();
            for (int i = 0; i < papeis.size(); i++) {
                PapelElementoInterpretado papel = papeis.get(i);
                if (papel != null && !papel.isConhecido()
                        && papel.getChavePapel() != null
                        && papel.getChavePapel().trim().length() > 0) {
                    return converterParaPapelCanonico(papel.getChavePapel());
                }
            }
        }
        return chavePapel;
    }

    public static String obterChavePapelExataPorValor(
            ResultadoInterpretacao resultadoInterpretacao, String valor) {
        if (resultadoInterpretacao == null || valor == null) {
            return "papel.valor";
        }

        List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();

        if (SimboloDesconhecido.eh(valor)) {
            for (int i = 0; i < papeis.size(); i++) {
                PapelElementoInterpretado papel = papeis.get(i);
                if (!papel.isConhecido()) {
                    return papel.getChavePapel();
                }
            }
        }

        for (int i = 0; i < papeis.size(); i++) {
            PapelElementoInterpretado papel = papeis.get(i);
            if (valor.equals(papel.getElemento())) {
                return papel.getChavePapel();
            }
        }

        return "papel.valor";
    }

    public static String obterChavePapelExataPorIndice(
            ResultadoInterpretacao resultadoInterpretacao, int indice) {
        if (resultadoInterpretacao == null || indice < 0) {
            return "papel.valor";
        }

        List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();

        if (indice >= 0 && indice < papeis.size()) {
            return papeis.get(indice).getChavePapel();
        }

        return "papel.valor";
    }

    public static String obterChavePapelCanonicoPorValor(
            ResultadoInterpretacao resultadoInterpretacao, String valor) {
        if (resultadoInterpretacao == null || valor == null) {
            return "papel.valor";
        }

        List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();

        for (int i = 0; i < papeis.size(); i++) {
            PapelElementoInterpretado papel = papeis.get(i);

            if (valor.equals(papel.getElemento())) {
                return converterParaPapelCanonico(papel.getChavePapel());
            }
        }

        if (SimboloDesconhecido.eh(valor)) {
            for (int i = 0; i < papeis.size(); i++) {
                PapelElementoInterpretado papel = papeis.get(i);

                if (!papel.isConhecido()) {
                    return converterParaPapelCanonico(papel.getChavePapel());
                }
            }
        }

        return "papel.valor";
    }

    public static String obterChavePapelCanonicoPorIndice(
            ResultadoInterpretacao resultadoInterpretacao, int indice) {
        if (resultadoInterpretacao == null || indice < 0) {
            return "papel.valor";
        }

        List<PapelElementoInterpretado> papeis = resultadoInterpretacao.getPapeis();

        if (indice >= 0 && indice < papeis.size()) {
            return converterParaPapelCanonico(papeis.get(indice).getChavePapel());
        }

        return "papel.valor";
    }

    /** Reduz uma chave de papel específica de instância à sua forma canônica (sem sufixo numérico). */
    public static String converterParaPapelCanonico(String chavePapel) {
        if (chavePapel == null) {
            return "papel.valor";
        }

        if (chavePapel.indexOf("parte") >= 0) {
            return "papel.parte";
        }

        if (chavePapel.indexOf("todo") >= 0) {
            return "papel.todo";
        }

        if (chavePapel.indexOf("estadoInicial") >= 0) {
            return "papel.estadoInicial";
        }

        if (chavePapel.indexOf("estadoFinal") >= 0) {
            return "papel.estadoFinal";
        }

        if (chavePapel.indexOf("transformacao") >= 0) {
            return "papel.transformacao";
        }

        if (chavePapel.indexOf("referendo") >= 0 || chavePapel.indexOf("referente") >= 0) {
            return "papel.referendo";
        }

        if (chavePapel.indexOf("referido") >= 0) {
            return "papel.referido";
        }

        if (chavePapel.indexOf("diferenca") >= 0) {
            return "papel.diferenca";
        }

        if (chavePapel.indexOf("relacao") >= 0) {
            return "papel.relacao";
        }

        return "papel.valor";
    }
}
