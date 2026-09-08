import java.util.Arrays;
import java.util.Collections;

import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.DecisaoExibicaoPaineisEixo;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;

public final class TesteDecisaoExibicaoPaineisEixo {
    public static void main(String[] args) {
        FiguraDiagrama semLupa = new FiguraDiagrama(
                TipoFiguraDiagrama.RETANGULO, 0, 0, 10, 10, "", 0, false);
        FiguraDiagrama comLupa = new FiguraDiagrama(
                TipoFiguraDiagrama.ELIPSE, 0, 0, 10, 10, "", 0, false,
                gerard.campoaditivo.diagrama.modelo.PosicaoRotuloFigura.CENTRO, true);
        exigir(!DecisaoExibicaoPaineisEixo.existeAlgumComLupa(
                Collections.singletonList(semLupa)), "cena sem lupa");
        exigir(DecisaoExibicaoPaineisEixo.existeAlgumComLupa(
                Arrays.asList(semLupa, comLupa)), "cena portátil com lupa");

        ElementoVergnaud materializado = new ElementoVergnaud(
                0, 0, 10, 10, TipoFiguraDiagrama.ELIPSE, "", null, false, true);
        exigir(DecisaoExibicaoPaineisEixo.existeAlgumComLupa(
                Collections.singletonList(materializado)),
                "elemento materializado preserva a decisão da cena");

        System.out.println("APROVADO: decisão de lupa compartilhada sem vetor paralelo.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
