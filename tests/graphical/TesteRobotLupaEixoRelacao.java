import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.ui.vergnaud.PaineisEixosRelacoes;

/**
 * Robot da Fase 7.13: clique real na lupa de um papel de Relações. O painel
 * do eixo é revelado e grava exatamente um log LUPA_EIXO_RELACAO; um segundo
 * clique no mesmo ponto não revela de novo nem grava outro log da lupa.
 * Uso: TesteRobotLupaEixoRelacao [CATEGORIA] [situacao_id_exigida]
 */
public class TesteRobotLupaEixoRelacao {
    static int falhas = 0;

    public static void main(String[] args) throws Exception {
        TipoSituacaoAditiva categoria = TipoSituacaoAditiva.valueOf(
                args.length > 0 ? args[0] : "TRANSFORMACAO_RELACAO");
        String exigida = args.length > 1 ? args[1] : null;
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setVisible(true); j[0].toFront(); }});
        Thread.sleep(2000);
        Robot r = new Robot(); r.setAutoDelay(12);
        TesteRobotExploracaoAposConclusao.fechar(r);
        final Main.TelaGerard t = TesteRobotExploracaoAposConclusao.tela(j[0]);
        for (int tentativa = 0; tentativa < 40; tentativa++) {
            TesteRobotExploracaoAposConclusao.selecionar(t, categoria);
            Thread.sleep(500); TesteRobotExploracaoAposConclusao.fechar(r);
            if (exigida == null || exigida.equals(TesteRobotControleComparacao.situacaoId(t))) break;
        }
        System.out.println("situacao=" + TesteRobotControleComparacao.situacaoId(t));
        if (exigida != null && !exigida.equals(TesteRobotControleComparacao.situacaoId(t))) {
            System.out.println("situação exigida não sorteada; execução descartada");
            System.exit(2);
        }
        final Point o = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); }});

        final List<PaineisEixosRelacoes.Painel> paineis = new ArrayList<PaineisEixosRelacoes.Painel>();
        final boolean[] ativo = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            ativo[0] = t.paineisEixosRelacoes.estaAtivo();
            paineis.addAll(t.paineisEixosRelacoes.obterPaineis()); }});
        verificar("painéis de eixo de Relações ativos (" + paineis.size() + ")", ativo[0] && !paineis.isEmpty());
        if (paineis.isEmpty()) { fim(); }
        PaineisEixosRelacoes.Painel alvo = null;
        for (PaineisEixosRelacoes.Painel p : paineis) if (!revelado(p)) { alvo = p; break; }
        verificar("existe papel com lupa ainda fechada", alvo != null);
        if (alvo == null) { fim(); }
        Rectangle lupa = areaLupa(t, alvo);
        System.out.println("lupa de " + alvo.elemento.chavePapelSemantico + " em " + lupa);
        int reveladosAntes = contarRevelados(paineis);
        int logsAntes = TesteRobotControleComparacao.contar("LUPA_EIXO_RELACAO");
        int cx = o.x + lupa.x + lupa.width / 2, cy = o.y + lupa.y + lupa.height / 2;
        TesteRobotControleComparacao.clicar(r, cx, cy);
        r.waitForIdle(); Thread.sleep(500); TesteRobotExploracaoAposConclusao.fechar(r);
        verificar("clique na lupa revela o painel do papel", revelado(alvo));
        verificar("só esse painel foi revelado (" + reveladosAntes + "->" + contarRevelados(paineis) + ")",
                contarRevelados(paineis) == reveladosAntes + 1);
        verificar("grava exatamente um log LUPA_EIXO_RELACAO (" + logsAntes + "->"
                        + TesteRobotControleComparacao.contar("LUPA_EIXO_RELACAO") + ")",
                TesteRobotControleComparacao.contar("LUPA_EIXO_RELACAO") == logsAntes + 1);

        int logsDepois = TesteRobotControleComparacao.contar("LUPA_EIXO_RELACAO");
        int reveladosDepois = contarRevelados(paineis);
        TesteRobotControleComparacao.clicar(r, cx, cy);
        r.waitForIdle(); Thread.sleep(500); TesteRobotExploracaoAposConclusao.fechar(r);
        verificar("segundo clique no mesmo ponto não grava outro log da lupa",
                TesteRobotControleComparacao.contar("LUPA_EIXO_RELACAO") == logsDepois);
        verificar("segundo clique não revela outro painel", contarRevelados(paineis) == reveladosDepois);
        fim();
    }

    static boolean revelado(final PaineisEixosRelacoes.Painel p) throws Exception {
        final boolean[] v = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { v[0] = p.estaRevelado(); }});
        return v[0];
    }

    static int contarRevelados(List<PaineisEixosRelacoes.Painel> ps) throws Exception {
        int n = 0; for (PaineisEixosRelacoes.Painel p : ps) if (revelado(p)) n++; return n;
    }

    static Rectangle areaLupa(final Main.TelaGerard t, final PaineisEixosRelacoes.Painel p) throws Exception {
        final Rectangle[] v = new Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                Method m = PaineisEixosRelacoes.class.getDeclaredMethod("obterAreaLupa", PaineisEixosRelacoes.Painel.class);
                m.setAccessible(true);
                v[0] = (Rectangle) m.invoke(t.paineisEixosRelacoes, p);
            } catch (Exception e) { throw new RuntimeException(e); }
        }});
        return v[0];
    }

    static void fim() {
        System.out.println("RESUMO falhas=" + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    static void verificar(String n, boolean ok) { if (!ok) falhas++; System.out.println((ok ? "[OK] " : "[FALHA] ") + n); }
}
