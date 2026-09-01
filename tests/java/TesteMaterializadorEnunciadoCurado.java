import gerard.campoaditivo.curadoria.MaterializadorEnunciadoCurado;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;

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
        System.out.println("OK: enunciado materializado pelos papéis curados");
    }
}
