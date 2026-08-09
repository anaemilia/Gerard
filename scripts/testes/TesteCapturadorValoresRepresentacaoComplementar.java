package gerard.campoaditivo.sincronizacao.representacoes;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.transformacao.processo.PoliticaSinalTransformacaoComplementar;
import gerard.campoaditivo.venn.mapeamento.FabricaMapeamentosPapeisComplementares;
import java.util.Arrays;

public final class TesteCapturadorValoresRepresentacaoComplementar {

    public static void main(String[] args) {
        CapturadorValoresRepresentacaoComplementar capturador =
                new CapturadorValoresRepresentacaoComplementar(
                        new PoliticaSinalTransformacaoComplementar());
        CirculoVenn referido = no(true, 0, "");
        CirculoVenn referendo = no(true, 0, "");
        CirculoVenn relativo = no(false, -3, "-3");

        ValoresCapturadosRepresentacaoComplementar captura = capturador.capturar(
                Arrays.asList(referido, referendo, relativo),
                new FabricaMapeamentosPapeisComplementares().obter(
                        TipoSituacaoAditiva.COMPARACAO_MEDIDAS),
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                false, null, 1, true,
                (indice, no) -> indice.intValue() == 0 ? 5 : 8,
                texto -> texto == null || texto.length() == 0
                        ? null : Integer.valueOf(texto));

        Integer[] valores = captura.getValores();
        exigir(Integer.valueOf(5).equals(valores[0]), "referido não mapeado");
        exigir(Integer.valueOf(-3).equals(valores[1]), "valor relativo não mapeado");
        exigir(Integer.valueOf(8).equals(valores[2]), "referendo não mapeado");
        exigir(captura.getConhecidos()[0] && captura.getConhecidos()[1]
                && captura.getConhecidos()[2], "valores conhecidos não preservados");
        exigir(captura.getIndiceAlteradoSemantico() == 2,
                "índice visual alterado não foi convertido");

        System.out.println("Teste aprovado: captura complementar preserva mapeamento e valores.");
    }

    private static CirculoVenn no(boolean unidades, int referencia, String texto) {
        CirculoVenn no = new CirculoVenn(0, 0, 10, 10, "", referencia, unidades);
        no.textoEditavel = texto;
        return no;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
