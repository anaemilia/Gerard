package gerard.campoaditivo.categoria;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.PoliticaValoresAditivos;

public final class TestePerfisCategoriasAditivas {
    private static int verificacoes;

    public static void main(String[] args) {
        CatalogoPerfisCategoriasAditivas catalogo =
                new CatalogoPerfisCategoriasAditivas();

        PerfilInteracaoCategoria composicao =
                catalogo.obter(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
        confirmar(composicao.getGrupo()
                        == GrupoCategoriaAditiva.SEM_RELACAO_ASSINADA,
                "composição de medidas deve ficar sem relação assinada");
        confirmar(!composicao.possuiRelacaoAssinada(),
                "composição não deve expor controles de sinal");
        confirmar(!composicao.indiceCompartilhadoPermiteSinal(0)
                        && !composicao.indiceCompartilhadoPermiteSinal(1)
                        && !composicao.indiceCompartilhadoPermiteSinal(2),
                "parte 1, parte 2 e todo devem permanecer cardinais");

        PerfilInteracaoCategoria transformacao =
                catalogo.obter(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        confirmar(transformacao.getGrupo()
                        == GrupoCategoriaAditiva.COM_RELACAO_ASSINADA,
                "transformação deve ficar no grupo com relação assinada");
        confirmar(transformacao.papelPermiteSinal("papel.transformacao"),
                "o papel transformação deve permitir sinal");
        confirmar(!transformacao.papelPermiteSinal("papel.estadoInicial")
                        && !transformacao.papelPermiteSinal("papel.estadoFinal"),
                "estados devem permanecer cardinais");
        confirmar(transformacao.indiceCompartilhadoPermiteSinal(1),
                "o índice central da transformação deve ser assinado");

        PerfilInteracaoCategoria comparacao =
                catalogo.obter(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
        confirmar(comparacao.papelPermiteSinal("papel.diferenca")
                        && comparacao.papelPermiteSinal("papel.valorRelativo"),
                "comparação deve reconhecer os aliases do valor relativo");
        confirmar(!comparacao.papelPermiteSinal("papel.referido")
                        && !comparacao.papelPermiteSinal("papel.referendo"),
                "referido e referendo devem permanecer cardinais");

        PerfilInteracaoCategoria composta = catalogo.obter(
                TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS);
        confirmar(composta.papelPermiteSinal("papel.transformacao1")
                        && composta.papelPermiteSinal("papel.transformacao2"),
                "transformações compostas devem generalizar papéis numerados");

        PoliticaValoresAditivos politica = new PoliticaValoresAditivos();
        confirmar(!politica.valorEhValidoNoEstadoCompartilhado(
                        TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, 1,
                        Integer.valueOf(-1)),
                "composição não deve aceitar parte negativa");
        confirmar(politica.valorEhValidoNoEstadoCompartilhado(
                        TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, 1,
                        Integer.valueOf(-1)),
                "transformação deve aceitar relação negativa");
        confirmar(!politica.valorEhValidoNoEstadoCompartilhado(
                        TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, 2,
                        Integer.valueOf(-1)),
                "estado final negativo deve continuar bloqueado");

        System.out.println("Teste de perfis das categorias aprovado: "
                + verificacoes + " verificações.");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
