package gerard.campoaditivo.conclusao;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.i18n.ServicoLocalizacao;
import java.util.Arrays;
import java.util.List;

public final class TesteSeletorPapeisConclusaoModelagem {
    public static void main(String[] args) {
        SeletorPapeisConclusaoModelagem seletor =
                new SeletorPapeisConclusaoModelagem();
        List<String> cena = Arrays.asList("papel.parte1", "papel.valor",
                "papel.parte2", "papel.todo", " ", null);

        exigir(seletor.selecionar(cena, null, null),
                "papel.parte1", "papel.parte2", "papel.todo");

        SituacaoProblemaAditiva parcial = new SituacaoProblemaAditiva(
                "teste", true, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                IdiomaInterface.PORTUGUES, "Situacao parcial", "", "", "",
                "", "", "", "6", "", "?", "resultado", "", "");
        exigir(seletor.selecionar(cena, parcial,
                        ServicoLocalizacao.getInstancia()),
                "papel.parte1", "papel.todo");
    }

    private static void exigir(List<String> atual, String... esperado) {
        List<String> listaEsperada = Arrays.asList(esperado);
        if (!listaEsperada.equals(atual)) {
            throw new AssertionError("esperado=" + listaEsperada + ", atual=" + atual);
        }
    }
}
