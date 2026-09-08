import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.campoaditivo.sincronizacao.representacoes.CapturadorValoresVergnaud;
import gerard.campoaditivo.sincronizacao.representacoes.ValoresCapturadosVergnaud;
import java.util.Arrays;
import java.awt.Rectangle;

public final class TesteCapturadorValoresVergnaud {
    public static void main(String[] args) {
        ElementoVergnaud a = elemento("6");
        ElementoVergnaud b = elemento("");
        ElementoVergnaud c = elemento("14");
        ElementoVergnaud d = elemento("8");
        CapturadorValoresVergnaud capturador = new CapturadorValoresVergnaud();

        ValoresCapturadosVergnaud captura = capturador.capturar(
                Arrays.asList(a, b, c, d), new int[] {0, 3, 2}, 3,
                elemento -> inteiro(elemento.textoEditavel));
        Integer[] valores = captura.getValores();
        boolean[] conhecidos = captura.getConhecidos();
        exigir(Integer.valueOf(6).equals(valores[0])
                        && Integer.valueOf(8).equals(valores[1])
                        && Integer.valueOf(14).equals(valores[2]),
                "A janela deslocada deve ser traduzida para a ordem semântica.");
        exigir(conhecidos[0] && conhecidos[1] && conhecidos[2],
                "Valores presentes devem ser conhecidos.");
        exigir(captura.getIndiceAlteradoSemantico() == 1,
                "O índice real alterado deve virar posição semântica.");

        ValoresCapturadosVergnaud incompleta = capturador.capturar(
                Arrays.asList(a, b, c), new int[] {0, 1, 9}, 8,
                elemento -> inteiro(elemento.textoEditavel));
        exigir(!incompleta.getConhecidos()[1]
                        && incompleta.getValores()[2] == null,
                "Ausência de valor e índice inválido devem permanecer desconhecidos.");
        exigir(incompleta.getIndiceAlteradoSemantico() == -1,
                "Alteração fora da janela não deve ganhar posição inventada.");

        System.out.println("TesteCapturadorValoresVergnaud: OK");
    }

    private static ElementoVergnaud elemento(String valor) {
        ElementoVergnaud elemento = new ElementoVergnaud(
                0, 0, 10, 10, TipoFiguraDiagrama.RETANGULO, "",
                new Rectangle(0, 0, 10, 10), false);
        elemento.textoEditavel = valor;
        return elemento;
    }

    private static Integer inteiro(String texto) {
        return texto == null || texto.trim().length() == 0
                ? null : Integer.valueOf(texto);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
