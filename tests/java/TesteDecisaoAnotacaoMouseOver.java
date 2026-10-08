import gerard.ui.interacao.DecisaoAnotacaoMouseOver;
import gerard.ui.interacao.DecisaoAnotacaoMouseOver.Fonte;

/** A prioridade da anotação flutuante tem um dono só: confere as 32 combinações contra a ordem declarada. */
public class TesteDecisaoAnotacaoMouseOver {
    static int falhas;

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
            System.out.println("[FALHA] " + msg);
        }
    }

    public static void main(String[] a) {
        Fonte[] ordem = {Fonte.QUESTIONAMENTO_PERSISTENTE, Fonte.LIMITE_QUANTIDADE, Fonte.SINAL_DIVERGENTE,
                Fonte.DICA_POSICIONAMENTO, Fonte.MOUSE_OVER};
        for (int m = 0; m < 32; m++) {
            boolean[] v = new boolean[5];
            for (int k = 0; k < 5; k++) {
                v[k] = (m & (1 << k)) != 0;
            }
            Fonte esperada = Fonte.NENHUMA;
            for (int k = 0; k < 5; k++) {
                if (v[k]) {
                    esperada = ordem[k];
                    break;
                }
            }
            Fonte obtida = DecisaoAnotacaoMouseOver.decidir(v[0], v[1], v[2], v[3], v[4]);
            conferir(obtida == esperada, "combinação " + m + ": esperado " + esperada + ", obtido " + obtida);
        }
        conferir(DecisaoAnotacaoMouseOver.decidir(false, false, false, false, false) == Fonte.NENHUMA,
                "sem nenhuma fonte válida não há anotação");
        if (falhas > 0) {
            System.out.println("REPROVADO: " + falhas + " falha(s).");
            System.exit(1);
        }
        System.out.println("APROVADO: a prioridade da anotação flutuante tem um dono só (33 verificações).");
    }
}
