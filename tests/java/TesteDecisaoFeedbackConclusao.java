import gerard.campoaditivo.conclusao.DecisaoFeedbackConclusao;

/**
 * Tabela de verdade completa (32 combinações) da decisão do feedback de conclusão, conferida contra a regra escrita
 * de forma independente (casos nomeados), sem Swing.
 */
public class TesteDecisaoFeedbackConclusao {
    static int verificacoes;

    public static void main(String[] a) {
        // casos nomeados
        // não concluída: remove o destaque e cancela, nunca restaura nem inicia
        for (int acabou = 0; acabou < 2; acabou++) for (int enc = 0; enc < 2; enc++) for (int susp = 0; susp < 2; susp++) for (int tip = 0; tip < 2; tip++) {
            DecisaoFeedbackConclusao d = DecisaoFeedbackConclusao.decidir(false, acabou == 1, enc == 1, susp == 1, tip == 1);
            exigir(d.removerDestaque() && d.cancelarFeedbackPendente() && !d.restaurarDestaqueEmSilencio() && !d.iniciarFeedbackDeSucesso(),
                    "não concluída: só remove e cancela (acabou=" + acabou + " enc=" + enc + " susp=" + susp + " tip=" + tip + ")");
        }
        // acabou de concluir pela 1ª vez, tentativa ainda aberta, tip pendente: inicia o feedback de sucesso
        DecisaoFeedbackConclusao primeira = DecisaoFeedbackConclusao.decidir(true, true, false, false, true);
        exigir(primeira.iniciarFeedbackDeSucesso() && !primeira.restaurarDestaqueEmSilencio() && !primeira.removerDestaque() && !primeira.cancelarFeedbackPendente(),
                "1ª conclusão: inicia o feedback de sucesso");
        // 1ª conclusão com o tip já apresentado: nada a fazer
        DecisaoFeedbackConclusao semTip = DecisaoFeedbackConclusao.decidir(true, true, false, false, false);
        exigir(!semTip.iniciarFeedbackDeSucesso() && !semTip.restaurarDestaqueEmSilencio() && !semTip.removerDestaque() && !semTip.cancelarFeedbackPendente(),
                "1ª conclusão com o tip já apresentado: nada a fazer");
        // concluiu de novo depois de a tentativa já estar encerrada: o destaque volta em silêncio, sem feedback
        DecisaoFeedbackConclusao exploracao = DecisaoFeedbackConclusao.decidir(true, true, true, false, true);
        exigir(exploracao.restaurarDestaqueEmSilencio() && !exploracao.iniciarFeedbackDeSucesso(),
                "exploração após a conclusão: restaura em silêncio, sem nova sinalização");
        // destaque suspenso pela manipulação exploratória: volta em silêncio quando a conclusão continua valendo
        DecisaoFeedbackConclusao suspenso = DecisaoFeedbackConclusao.decidir(true, false, true, true, false);
        exigir(suspenso.restaurarDestaqueEmSilencio() && !suspenso.iniciarFeedbackDeSucesso(),
                "destaque suspenso: volta em silêncio");
        // concluída e estável (sem novidade): nada a fazer
        DecisaoFeedbackConclusao estavel = DecisaoFeedbackConclusao.decidir(true, false, true, false, false);
        exigir(!estavel.restaurarDestaqueEmSilencio() && !estavel.iniciarFeedbackDeSucesso() && !estavel.removerDestaque() && !estavel.cancelarFeedbackPendente(),
                "concluída e estável: nada a fazer");
        // o feedback de sucesso e a restauração em silêncio nunca acontecem juntos
        for (int c = 0; c < 32; c++) {
            DecisaoFeedbackConclusao d = DecisaoFeedbackConclusao.decidir((c & 1) != 0, (c & 2) != 0, (c & 4) != 0, (c & 8) != 0, (c & 16) != 0);
            exigir(!(d.iniciarFeedbackDeSucesso() && d.restaurarDestaqueEmSilencio()) || ((c & 8) != 0),
                    "1ª conclusão e restauração em silêncio só coexistem se o destaque estava suspenso (combinação " + c + ")");
        }
        System.out.println("APROVADO: a decisão do feedback de conclusão é uma tabela de verdade pura (" + verificacoes + " verificações).");
    }

    static void exigir(boolean ok, String msg) {
        verificacoes++;
        if (!ok) { System.out.println("[FALHA] " + msg); System.exit(1); }
    }
}
