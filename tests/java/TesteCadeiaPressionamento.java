import gerard.ui.interacao.CadeiaPressionamento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** A prioridade do pressionamento: o primeiro elo que atende encerra a cadeia; os seguintes não são consultados. */
public class TesteCadeiaPressionamento {
    static int verificacoes;

    public static void main(String[] a) {
        final List<String> consultados = new ArrayList<String>();
        CadeiaPressionamento cadeia = new CadeiaPressionamento()
                .adicionar("A", elo(consultados, "A", false))
                .adicionar("B", elo(consultados, "B", true))
                .adicionar("C", elo(consultados, "C", true));
        exigir(Arrays.asList("A", "B", "C").equals(cadeia.ordem()), "a ordem de prioridade é a de adição");
        String atendeu = cadeia.pressionar(10, 20, null);
        exigir("B".equals(atendeu), "o primeiro elo que atende (B) vence: " + atendeu);
        exigir(Arrays.asList("A", "B").equals(consultados), "o elo C nem é consultado (consultados: " + consultados + ")");

        // ninguém atende: devolve nulo e consulta todos, na ordem
        consultados.clear();
        CadeiaPressionamento vazia = new CadeiaPressionamento()
                .adicionar("X", elo(consultados, "X", false))
                .adicionar("Y", elo(consultados, "Y", false));
        exigir(vazia.pressionar(1, 2, null) == null && Arrays.asList("X", "Y").equals(consultados),
                "nenhum atende: nulo e todos consultados em ordem");

        // um elo que só BLOQUEIA (devolve true sem fazer nada) também encerra a cadeia
        consultados.clear();
        CadeiaPressionamento bloqueio = new CadeiaPressionamento()
                .adicionar("bloqueia", elo(consultados, "bloqueia", true))
                .adicionar("depois", elo(consultados, "depois", true));
        exigir("bloqueia".equals(bloqueio.pressionar(0, 0, null)) && Arrays.asList("bloqueia").equals(consultados),
                "o elo que bloqueia encerra a cadeia");

        // as coordenadas chegam intactas ao elo
        final int[] visto = new int[2];
        new CadeiaPressionamento().adicionar("coords", new CadeiaPressionamento.Elo() {
            public boolean tentar(int x, int y, java.awt.event.MouseEvent e) { visto[0] = x; visto[1] = y; return true; }
        }).pressionar(33, 44, null);
        exigir(visto[0] == 33 && visto[1] == 44, "as coordenadas chegam ao elo");

        boolean recusou = false;
        try { new CadeiaPressionamento().adicionar(" ", elo(consultados, "z", true)); } catch (IllegalArgumentException e) { recusou = true; }
        exigir(recusou, "elo sem nome é recusado");
        System.out.println("APROVADO: a prioridade do pressionamento tem um dono só (" + verificacoes + " verificações).");
    }

    static CadeiaPressionamento.Elo elo(final List<String> consultados, final String nome, final boolean atende) {
        return new CadeiaPressionamento.Elo() {
            public boolean tentar(int x, int y, java.awt.event.MouseEvent e) { consultados.add(nome); return atende; }
        };
    }

    static void exigir(boolean ok, String msg) {
        verificacoes++;
        if (!ok) { System.out.println("[FALHA] " + msg); System.exit(1); }
    }
}
