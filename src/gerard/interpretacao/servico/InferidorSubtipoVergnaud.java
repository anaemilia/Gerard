package gerard.interpretacao.servico;

import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.modelo.SubtipoVergnaud;
import java.util.List;

public class InferidorSubtipoVergnaud {
    public SubtipoVergnaud inferir(CategoriaProblema categoria, List<PapelElementoInterpretado> papeis) {
        String incognita = obterChaveIncognita(papeis);

        if (categoria == null) {
            categoria = CategoriaProblema.INDEFINIDA;
        }

        switch (categoria) {
            case COMPOSICAO_MEDIDAS:
                return subtipoComposicaoMedidas(incognita);
            case TRANSFORMACAO_MEDIDAS:
                return subtipoTransformacaoMedidas(incognita);
            case COMPOSICAO_TRANSFORMACAO_MEDIDAS:
                return subtipoComposicaoTransformacaoMedidas(incognita);
            case COMPARACAO_MEDIDAS:
                return subtipoComparacaoMedidas(incognita);
            case COMPOSICAO_TRANSFORMACOES:
                return subtipoComposicaoTransformacoes(incognita);
            case TRANSFORMACAO_COMPOSTA_DOIS_PASSOS:
                return subtipoTransformacaoCompostaDoisPassos(incognita);
            case TRANSFORMACAO_RELACAO:
                return subtipoTransformacaoRelacao(incognita);
            case COMPOSICAO_RELACOES:
                return subtipoComposicaoRelacoes(incognita);
            case INDEFINIDA:
            default:
                return new SubtipoVergnaud("subtipo.indefinido", null, -1);
        }
    }

    private String obterChaveIncognita(List<PapelElementoInterpretado> papeis) {
        if (papeis == null) {
            return null;
        }
        for (int i = 0; i < papeis.size(); i++) {
            PapelElementoInterpretado papel = papeis.get(i);
            if (!papel.isConhecido()) {
                return papel.getChavePapel();
            }
        }
        return null;
    }

    private SubtipoVergnaud subtipoComposicaoMedidas(String incognita) {
        if ("papel.parte1".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_medidas.parte1", "papel.parte1", 0);
        if ("papel.parte2".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_medidas.parte2", "papel.parte2", 1);
        if ("papel.todo".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_medidas.todo", "papel.todo", 2);
        return new SubtipoVergnaud("subtipo.composicao_medidas.geral", null, -1);
    }

    private SubtipoVergnaud subtipoTransformacaoMedidas(String incognita) {
        if ("papel.estadoInicial".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_medidas.estadoInicial", "papel.estadoInicial", 0);
        if ("papel.transformacao".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_medidas.transformacao", "papel.transformacao", 1);
        if ("papel.estadoFinal".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_medidas.estadoFinal", "papel.estadoFinal", 2);
        return new SubtipoVergnaud("subtipo.transformacao_medidas.geral", null, -1);
    }


    private SubtipoVergnaud subtipoComposicaoTransformacaoMedidas(String incognita) {
        if ("papel.parte1".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_medidas.parte1", "papel.parte1", 0);
        if ("papel.parte2".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_medidas.parte2", "papel.parte2", 1);
        if ("papel.todo".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_medidas.todo", "papel.todo", 2);
        if ("papel.transformacao".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_medidas.transformacao", "papel.transformacao", 4);
        if ("papel.estadoFinal".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_medidas.estadoFinal", "papel.estadoFinal", 5);
        return new SubtipoVergnaud("subtipo.composicao_transformacao_medidas.geral", null, -1);
    }
    private SubtipoVergnaud subtipoComparacaoMedidas(String incognita) {
        if ("papel.referido".equals(incognita)) return new SubtipoVergnaud("subtipo.comparacao_medidas.referido", "papel.referido", 0);
        if ("papel.diferenca".equals(incognita)) return new SubtipoVergnaud("subtipo.comparacao_medidas.diferenca", "papel.diferenca", 1);
        if ("papel.referendo".equals(incognita) || "papel.referente".equals(incognita)) return new SubtipoVergnaud("subtipo.comparacao_medidas.referendo", "papel.referendo", 2);
        return new SubtipoVergnaud("subtipo.comparacao_medidas.geral", null, -1);
    }

    private SubtipoVergnaud subtipoComposicaoTransformacoes(String incognita) {
        if ("papel.transformacao1".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_transformacoes.transformacao1", "papel.transformacao1", 0);
        if ("papel.transformacao2".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_transformacoes.transformacao2", "papel.transformacao2", 1);
        if ("papel.transformacaoFinal".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_transformacoes.transformacaoFinal", "papel.transformacaoFinal", 2);
        return new SubtipoVergnaud("subtipo.composicao_transformacoes.geral", null, -1);
    }

    private SubtipoVergnaud subtipoTransformacaoCompostaDoisPassos(String incognita) {
        if ("papel.estadoInicial".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_medidas.estadoInicial", "papel.estadoInicial", 0);
        if ("papel.transformacao1".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_transformacoes.transformacao1", "papel.transformacao1", 1);
        if ("papel.transformacao2".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_transformacoes.transformacao2", "papel.transformacao2", 4);
        if ("papel.transformacaoFinal".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_transformacoes.transformacaoFinal", "papel.transformacaoFinal", 2);
        if ("papel.estadoFinal".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_medidas.estadoFinal", "papel.estadoFinal", 5);
        return new SubtipoVergnaud("subtipo.transformacao_composta_dois_passos.geral", null, -1);
    }

    private SubtipoVergnaud subtipoTransformacaoRelacao(String incognita) {
        if ("papel.relacaoInicial".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_relacao.relacaoInicial", "papel.relacaoInicial", 0);
        if ("papel.transformacao".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_relacao.transformacao", "papel.transformacao", 1);
        if ("papel.relacaoFinal".equals(incognita)) return new SubtipoVergnaud("subtipo.transformacao_relacao.relacaoFinal", "papel.relacaoFinal", 2);
        return new SubtipoVergnaud("subtipo.transformacao_relacao.geral", null, -1);
    }

    private SubtipoVergnaud subtipoComposicaoRelacoes(String incognita) {
        if ("papel.relacao1".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_relacoes.relacao1", "papel.relacao1", 0);
        if ("papel.relacao2".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_relacoes.relacao2", "papel.relacao2", 1);
        if ("papel.relacaoFinal".equals(incognita)) return new SubtipoVergnaud("subtipo.composicao_relacoes.relacaoFinal", "papel.relacaoFinal", 2);
        return new SubtipoVergnaud("subtipo.composicao_relacoes.geral", null, -1);
    }
}
