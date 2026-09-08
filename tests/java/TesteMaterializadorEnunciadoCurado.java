import gerard.campoaditivo.curadoria.MaterializadorEnunciadoCurado;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.interpretacao.modelo.ResolvedorPapelInterpretado;

public class TesteMaterializadorEnunciadoCurado {
    public static void main(String[] args) {
        SituacaoProblemaAditiva situacao = new SituacaoProblemaAditiva(
                "teste", true, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                IdiomaInterface.PORTUGUES,
                "Um aluno tem seis bolas e recebe oito. Quantas bolas tem?",
                "", "", "", "", "", "", "6", "8", "?",
                "resultado", "", "");

        String texto = new MaterializadorEnunciadoCurado().materializar(situacao);
        String esperado = "Um aluno tem 6 bolas e recebe 8. Quantas bolas tem?";
        if (!esperado.equals(texto)) {
            throw new AssertionError("Materialização divergente: " + texto);
        }
        if (!texto.startsWith("Um aluno")) {
            throw new AssertionError("Numeral sem papel semântico não pode ser convertido");
        }
        ResultadoInterpretacao interpretacao =
                new ConstrutorResultadoCurado().construir(situacao, texto);
        if (interpretacao.getNumeros().size() != 2
                || !"papel.parte1".equals(interpretacao.getNumeros().get(0)
                        .getChavePapelSemantico())
                || !"papel.parte2".equals(interpretacao.getNumeros().get(1)
                        .getChavePapelSemantico())) {
            throw new AssertionError(
                    "Números curados devem transportar seus papéis nominais");
        }
        if (!"papel.todo".equals(
                ResolvedorPapelInterpretado.obterChavePapelIncognita(
                        interpretacao))) {
            throw new AssertionError(
                    "A interpretação deve fornecer a identidade da incógnita");
        }
        System.out.println("OK: enunciado materializado pelos papéis curados");
    }
}
