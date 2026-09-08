import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Protege a cópia representacional do valor já vinculado no enunciado. */
public class TesteCopiaSemanticaElementoTexto {

    public static void main(String[] args) throws Exception {
        ElementoTextoMovel elemento =
                new ElementoTextoMovel("Antes 12 depois", 0);
        elemento.vincularSemantica("papel.parte1", 6, 8, "12");
        exigir("12".equals(elemento.getValorSemanticoAtual()),
                "a cópia deve usar o trecho semanticamente vinculado");

        elemento.atualizarValorSemantico("14");
        exigir("Antes 14 depois".equals(elemento.valor),
                "a atualização deve preservar o texto ao redor do valor");
        exigir("14".equals(elemento.getValorSemanticoAtual()),
                "a cópia deve observar o valor semântico atualizado");

        String main = new String(Files.readAllBytes(
                Paths.get("src/Main.java")), StandardCharsets.UTF_8);
        exigir(!main.contains("private String extrairValorArrastavel"),
                "Main não deve reconstruir o valor do elemento textual");

        String inicio =
                "private void converterElementoTextoEmItemDiagrama";
        String fim = "private MarcadorTexto encontrarMarcadorFixoTexto";
        int posicaoInicio = main.indexOf(inicio);
        int posicaoFim = main.indexOf(fim, posicaoInicio);
        exigir(posicaoInicio >= 0 && posicaoFim > posicaoInicio,
                "fluxo de cópia textual deve permanecer localizável");
        String fluxo = main.substring(posicaoInicio, posicaoFim);
        exigir(fluxo.contains(
                        "String valor = elemento.getValorSemanticoAtual();")
                        && !fluxo.contains("Pattern.compile")
                        && !fluxo.contains("SimboloDesconhecido.regexClasse"),
                "a tela deve copiar o valor fornecido pela representação");

        System.out.println(
                "OK: cópia do valor semanticamente vinculado ao texto");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
