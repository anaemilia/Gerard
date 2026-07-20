package gerard.campoaditivo.semantica;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.papel.CatalogoPapeisSemanticos;

/**
 * Fonte única para o mapeamento entre índices visuais e papéis semânticos dos
 * diagramas aditivos. Evita que curadoria, questionamento e validação de
 * valores mantenham switches independentes e potencialmente divergentes.
 */
public final class CatalogoPapeisSemanticosAditivos {
    private final CatalogoPapeisSemanticos catalogoConceitual =
            new CatalogoPapeisSemanticos();


    public int obterIndiceElementoPorPapel(
            String chavePapel,
            TipoSituacaoAditiva tipo,
            boolean transformacaoCompostaEncadeada,
            int quantidadePassosTransformacaoComposta
    ) {
        if (chavePapel == null) {
            return -1;
        }

        if (transformacaoCompostaEncadeada) {
            if ("papel.estadoInicial".equals(chavePapel)) {
                return 0;
            }
            if ("papel.estadoFinal".equals(chavePapel)) {
                return Math.max(1, quantidadePassosTransformacaoComposta) * 3 - 1;
            }
            if (chavePapel.startsWith("papel.transformacao")) {
                String sufixo = chavePapel.substring("papel.transformacao".length());
                int passo = 1;
                if (sufixo.length() > 0 && sufixo.matches("\\d+")) {
                    passo = Integer.parseInt(sufixo);
                }
                return (passo - 1) * 3 + 1;
            }
        }

        if (tipo == null) {
            return -1;
        }

        switch (tipo) {
            case COMPOSICAO_MEDIDAS:
                if ("papel.parte1".equals(chavePapel)) return 0;
                if ("papel.parte2".equals(chavePapel)) return 1;
                if ("papel.todo".equals(chavePapel)) return 2;
                break;
            case TRANSFORMACAO_MEDIDAS:
                if ("papel.estadoInicial".equals(chavePapel)) return 0;
                if ("papel.transformacao".equals(chavePapel)) return 1;
                if ("papel.estadoFinal".equals(chavePapel)) return 2;
                break;
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
                if ("papel.parte1".equals(chavePapel)) return 0;
                if ("papel.parte2".equals(chavePapel)) return 1;
                if ("papel.todo".equals(chavePapel)) return 2;
                if ("papel.estadoInicial".equals(chavePapel)) return 3;
                if ("papel.transformacao".equals(chavePapel)
                        || chavePapel.startsWith("papel.transformacao")) return 4;
                if ("papel.estadoFinal".equals(chavePapel)) return 5;
                break;
            case COMPARACAO_MEDIDAS:
                if ("papel.referido".equals(chavePapel)) return 0;
                if ("papel.diferenca".equals(chavePapel)) return 1;
                if ("papel.referendo".equals(chavePapel)
                        || "papel.referente".equals(chavePapel)) return 2;
                break;
            case COMPOSICAO_TRANSFORMACOES:
                if ("papel.transformacao1".equals(chavePapel)) return 0;
                if ("papel.transformacao2".equals(chavePapel)) return 1;
                if ("papel.transformacaoFinal".equals(chavePapel)) return 2;
                break;
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                if ("papel.estadoInicial".equals(chavePapel)) return 0;
                if ("papel.transformacao1".equals(chavePapel)) return 1;
                if ("papel.estadoIntermediario".equals(chavePapel)) return 2;
                if ("papel.transformacao2".equals(chavePapel)) return 3;
                if ("papel.estadoFinal".equals(chavePapel)) return 4;
                break;
            case TRANSFORMACAO_RELACAO:
                if ("papel.relacaoInicial".equals(chavePapel)) return 0;
                if ("papel.transformacao".equals(chavePapel)) return 1;
                if ("papel.relacaoFinal".equals(chavePapel)) return 2;
                break;
            case COMPOSICAO_RELACOES:
                if ("papel.relacao1".equals(chavePapel)) return 0;
                if ("papel.relacao2".equals(chavePapel)) return 1;
                if ("papel.relacaoFinal".equals(chavePapel)) return 2;
                break;
            default:
                break;
        }

        return -1;
    }

    public String obterChavePapelDoElemento(
            TipoSituacaoAditiva tipo,
            int indiceElemento,
            boolean transformacaoCompostaEncadeada,
            int quantidadePassosTransformacaoComposta
    ) {
        if (indiceElemento < 0) {
            return "papel.valor";
        }

        if (transformacaoCompostaEncadeada) {
            int passo = indiceElemento / 3;
            int indiceLocal = indiceElemento % 3;
            int totalPassos = Math.max(1, quantidadePassosTransformacaoComposta);

            if (indiceLocal == 0) {
                return passo == 0 ? "papel.estadoInicial" : "papel.estadoIntermediario";
            }
            if (indiceLocal == 1) {
                return "papel.transformacao" + (passo + 1);
            }
            if (indiceLocal == 2) {
                return passo == totalPassos - 1
                        ? "papel.estadoFinal"
                        : "papel.estadoIntermediario";
            }
            return "papel.valor";
        }

        if (tipo == null) {
            return "papel.valor";
        }

        switch (tipo) {
            case COMPOSICAO_MEDIDAS:
                if (indiceElemento == 0) return "papel.parte1";
                if (indiceElemento == 1) return "papel.parte2";
                if (indiceElemento == 2) return "papel.todo";
                break;
            case TRANSFORMACAO_MEDIDAS:
                if (indiceElemento == 0) return "papel.estadoInicial";
                if (indiceElemento == 1) return "papel.transformacao";
                if (indiceElemento == 2) return "papel.estadoFinal";
                break;
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
                if (indiceElemento == 0) return "papel.parte1";
                if (indiceElemento == 1) return "papel.parte2";
                if (indiceElemento == 2) return "papel.todo";
                if (indiceElemento == 3) return "papel.estadoInicial";
                if (indiceElemento == 4) return "papel.transformacao";
                if (indiceElemento == 5) return "papel.estadoFinal";
                break;
            case COMPARACAO_MEDIDAS:
                if (indiceElemento == 0) return "papel.referido";
                if (indiceElemento == 1) return "papel.diferenca";
                if (indiceElemento == 2) return "papel.referendo";
                break;
            case COMPOSICAO_TRANSFORMACOES:
                if (indiceElemento == 0) return "papel.transformacao1";
                if (indiceElemento == 1) return "papel.transformacao2";
                if (indiceElemento == 2) return "papel.transformacaoFinal";
                break;
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                if (indiceElemento == 0) return "papel.estadoInicial";
                if (indiceElemento == 1) return "papel.transformacao1";
                if (indiceElemento == 2) return "papel.estadoIntermediario";
                if (indiceElemento == 3) return "papel.transformacao2";
                if (indiceElemento == 4) return "papel.estadoFinal";
                break;
            case TRANSFORMACAO_RELACAO:
                if (indiceElemento == 0) return "papel.relacaoInicial";
                if (indiceElemento == 1) return "papel.transformacao";
                if (indiceElemento == 2) return "papel.relacaoFinal";
                break;
            case COMPOSICAO_RELACOES:
                if (indiceElemento == 0) return "papel.relacao1";
                if (indiceElemento == 1) return "papel.relacao2";
                if (indiceElemento == 2) return "papel.relacaoFinal";
                break;
            default:
                break;
        }

        return "papel.valor";
    }

    public NaturezaPapelAditivo obterNatureza(String chavePapel) {
        String chave = chavePapel == null ? "" : chavePapel.trim();
        if (chave.length() == 0 || "papel.valor".equals(chave)) {
            return NaturezaPapelAditivo.INDEFINIDO;
        }
        return obterDominioNumerico(chave) == DominioNumerico.INTEIROS
                ? NaturezaPapelAditivo.TRANSFORMACAO_OU_RELACAO
                : NaturezaPapelAditivo.QUANTIDADE;
    }

    public DominioNumerico obterDominioNumerico(String chavePapel) {
        return catalogoConceitual.dominioDoPapel(chavePapel);
    }

    public boolean papelRepresentaQuantidade(String chavePapel) {
        return obterDominioNumerico(chavePapel) == DominioNumerico.NATURAIS;
    }

    public boolean papelPodeReceberSinal(String chavePapel) {
        return catalogoConceitual.papelPermiteSinal(chavePapel);
    }
}
