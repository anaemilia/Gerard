import gerard.ui.interacao.DicaControleQuadradinho;
import gerard.ui.interacao.DicaControleQuadradinho.Controle;

/** Os quatro estados da dica do controle de quadradinho, para adicionar e remover, com e sem valor inteiro. */
public class TesteDicaControleQuadradinho {
    static int verificacoes;

    public static void main(String[] a) {
        // modelagem não iniciada: bloqueio, nunca mão, sem chave própria
        for (Controle c : Controle.values()) for (int li = 0; li < 2; li++) for (int vi = 0; vi < 2; vi++) {
            DicaControleQuadradinho d = DicaControleQuadradinho.decidir(c, false, li == 1, vi == 1);
            exigir(d.bloqueadaPelaModelagem() && !d.cursorMao() && d.getChaveMensagem() == null,
                    "bloqueada: " + c + " liberada=" + li + " inteiro=" + vi);
        }
        confere(Controle.ADICIONAR, true, false, "ui.tooltip.venn.addSquare", true);
        confere(Controle.REMOVER, true, false, "ui.tooltip.venn.removeSquare", true);
        confere(Controle.ADICIONAR, true, true, "ui.tooltip.venn.increaseIntegerValue", true);
        confere(Controle.REMOVER, true, true, "ui.tooltip.venn.decreaseIntegerValue", true);
        confere(Controle.ADICIONAR, false, false, "ui.tooltip.venn.semanticLimitReached", false);
        confere(Controle.REMOVER, false, false, "ui.tooltip.venn.minimumReached", false);
        confere(Controle.ADICIONAR, false, true, "ui.tooltip.venn.integerLimitReached", false);
        confere(Controle.REMOVER, false, true, "ui.tooltip.venn.integerLimitReached", false);
        System.out.println("APROVADO: a dica do controle de quadradinho é uma tabela pura (" + verificacoes + " verificações).");
    }

    static void confere(Controle c, boolean liberada, boolean inteiro, String chave, boolean mao) {
        DicaControleQuadradinho d = DicaControleQuadradinho.decidir(c, true, liberada, inteiro);
        exigir(!d.bloqueadaPelaModelagem() && chave.equals(d.getChaveMensagem()) && d.cursorMao() == mao,
                c + " liberada=" + liberada + " inteiro=" + inteiro + " -> " + d.getChaveMensagem() + " mao=" + d.cursorMao());
    }

    static void exigir(boolean ok, String msg) {
        verificacoes++;
        if (!ok) { System.out.println("[FALHA] " + msg); System.exit(1); }
    }
}
