import gerard.interacao.unidades.AlvoControlesUnidades;
import gerard.interacao.unidades.HandlerInteracaoControlesUnidades;
import gerard.interacao.unidades.OperacaoControleUnidade;
import gerard.interacao.unidades.ResultadoAplicacaoControleUnidade;
import gerard.interacao.unidades.ResultadoControleUnidades;
import java.util.ArrayList;
import java.util.List;

/** Fase 7.10: sequência portátil dos controles +/− do material concreto. */
public final class TesteHandlerInteracaoControlesUnidades {

    public static void main(String[] args) {
        testarCliqueForaDosControlesNaoConsome();
        testarRemoverTemPrioridadeSobreAdicionar();
        testarBloqueioPelaModelagemAntesDoLimite();
        testarLimiteImpedeAplicacao();
        testarAplicacaoDevolveFatoDoProprietario();
        testarAdicionarQuandoSoHaControleDeAdicionar();
        testarAplicacaoSemFatoEhErro();
        System.out.println("Teste aprovado: controles de unidades preservam prioridade, liberação, limite e aplicação.");
    }

    private static void testarCliqueForaDosControlesNaoConsome() {
        AlvoFalso alvo = new AlvoFalso();
        exigir(novo().processar(alvo, 1, 1) == null, "Sem controle atingido o clique não é consumido.");
        exigir(alvo.chamadas.equals(lista("localizar:REMOVER", "localizar:ADICIONAR")),
                "Deve consultar remover e depois adicionar: " + alvo.chamadas);
    }

    private static void testarRemoverTemPrioridadeSobreAdicionar() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.controleRemover = "R";
        alvo.controleAdicionar = "A";
        ResultadoControleUnidades<String> r = novo().processar(alvo, 1, 1);
        exigir(r.getOperacao() == OperacaoControleUnidade.REMOVER && "R".equals(r.getControle()),
                "Remover deve ter prioridade.");
        exigir(!alvo.chamadas.contains("localizar:ADICIONAR"), "Adicionar não pode ser consultado.");
    }

    private static void testarBloqueioPelaModelagemAntesDoLimite() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.controleAdicionar = "A";
        alvo.liberado = false;
        ResultadoControleUnidades<String> r = novo().processar(alvo, 1, 1);
        exigir(r.getDesfecho() == ResultadoControleUnidades.Desfecho.BLOQUEADO_PELA_MODELAGEM,
                "Bloqueio da modelagem deve prevalecer.");
        exigir(!alvo.chamadas.contains("podeAplicar:ADICIONAR") && !alvo.chamadas.contains("aplicar:ADICIONAR"),
                "Limite e aplicação não podem ser consultados quando bloqueado.");
        exigir(r.getAplicacao() == null, "Sem aplicação quando bloqueado.");
    }

    private static void testarLimiteImpedeAplicacao() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.controleRemover = "R";
        alvo.podeAplicar = false;
        ResultadoControleUnidades<String> r = novo().processar(alvo, 1, 1);
        exigir(r.getDesfecho() == ResultadoControleUnidades.Desfecho.LIMITE_ATINGIDO, "Limite esperado.");
        exigir(!alvo.chamadas.contains("aplicar:REMOVER"), "Não pode aplicar além do limite.");
    }

    private static void testarAplicacaoDevolveFatoDoProprietario() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.controleRemover = "R";
        alvo.fato = new ResultadoAplicacaoControleUnidade(false, false);
        ResultadoControleUnidades<String> r = novo().processar(alvo, 1, 1);
        exigir(r.getDesfecho() == ResultadoControleUnidades.Desfecho.APLICADO, "Aplicação esperada.");
        exigir(r.getAplicacao() == alvo.fato, "O handler repassa o fato do proprietário sem recalcular.");
        exigir(alvo.chamadas.equals(lista("localizar:REMOVER", "liberado", "podeAplicar:REMOVER", "aplicar:REMOVER")),
                "Sequência: " + alvo.chamadas);
    }

    private static void testarAdicionarQuandoSoHaControleDeAdicionar() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.controleAdicionar = "A";
        alvo.fato = new ResultadoAplicacaoControleUnidade(true, true);
        ResultadoControleUnidades<String> r = novo().processar(alvo, 1, 1);
        exigir(r.getOperacao() == OperacaoControleUnidade.ADICIONAR
                && r.getAplicacao().isLimiteAtingidoAposAplicar(), "Adicionar aplicado com limite atingido após.");
    }

    private static void testarAplicacaoSemFatoEhErro() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.controleAdicionar = "A";
        alvo.fato = null;
        try {
            novo().processar(alvo, 1, 1);
            throw new AssertionError("Aplicar sem fato deveria falhar.");
        } catch (IllegalStateException esperado) {
            // ok
        }
    }

    private static HandlerInteracaoControlesUnidades novo() { return new HandlerInteracaoControlesUnidades(); }

    private static List<String> lista(String... s) {
        List<String> l = new ArrayList<String>();
        for (String x : s) l.add(x);
        return l;
    }

    private static void exigir(boolean c, String m) { if (!c) throw new AssertionError(m); }

    private static final class AlvoFalso implements AlvoControlesUnidades<String> {
        String controleRemover;
        String controleAdicionar;
        boolean liberado = true;
        boolean podeAplicar = true;
        ResultadoAplicacaoControleUnidade fato = new ResultadoAplicacaoControleUnidade(false, true);
        final List<String> chamadas = new ArrayList<String>();

        public String localizarControle(OperacaoControleUnidade op, int x, int y) {
            chamadas.add("localizar:" + op);
            return op == OperacaoControleUnidade.REMOVER ? controleRemover : controleAdicionar;
        }
        public boolean alteracaoLiberadaPelaModelagem() { chamadas.add("liberado"); return liberado; }
        public boolean podeAplicar(String c, OperacaoControleUnidade op) {
            chamadas.add("podeAplicar:" + op); return podeAplicar;
        }
        public ResultadoAplicacaoControleUnidade aplicar(String c, OperacaoControleUnidade op) {
            chamadas.add("aplicar:" + op); return fato;
        }
    }
}
