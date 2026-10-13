import gerard.campoaditivo.sincronizacao.EstadosDaComposicaoDeTransformacoes;

import java.util.Map;

/** Os estados acompanham as transformações na exploração, e só enquanto continuam naturais. */
public class TesteEstadosDaComposicaoDeTransformacoes {
    static int falhas;

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "[OK] " : "[FALHA] ") + msg);
    }

    public static void main(String[] args) {
        Map<String, Integer> r = EstadosDaComposicaoDeTransformacoes.recalcular(5, -1, -2, false);
        conferir(r != null && r.get("papel.estadoFinal") == 3 && !r.containsKey("papel.estadoIntermediario"),
                "5 rosas, deu 1 e deu 1 (resultante -2): estado final 3");
        r = EstadosDaComposicaoDeTransformacoes.recalcular(5, 1, 0, false);
        conferir(r != null && r.get("papel.estadoFinal") == 5, "T1 = +1, T2 = -1 (resultante 0): o estado final volta a 5");
        r = EstadosDaComposicaoDeTransformacoes.recalcular(5, -1, -2, true);
        conferir(r != null && r.get("papel.estadoIntermediario") == 4 && r.get("papel.estadoFinal") == 3,
                "com o estado intermediário no diagrama, ele é o inicial mais a transformação 1");
        conferir(EstadosDaComposicaoDeTransformacoes.recalcular(5, -6, -7, false) == null,
                "uma transformação que deixaria o estado final negativo não é aceita");
        conferir(EstadosDaComposicaoDeTransformacoes.recalcular(5, -6, -1, true) == null,
                "nem a que deixaria o estado intermediário negativo");
        conferir(EstadosDaComposicaoDeTransformacoes.recalcular(null, -1, -2, true).isEmpty(),
                "sem estado inicial na situação, não há estado a acompanhar");
        System.out.println(falhas == 0 ? "APROVADO: os estados acompanham as transformações" : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
