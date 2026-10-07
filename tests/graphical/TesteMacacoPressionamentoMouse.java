import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;

/**
 * Macaco do PRESSIONAMENTO do mouse (mousePressed / mouseReleased): pressiona e solta o mouse REAL em cada ponto
 * de uma grade sobre a área de trabalho (abaixo da faixa de ferramentas e categorias, para não sortear nada) e,
 * numa varredura fina, sobre a representação complementar. Em cada ponto registra: quem recebeu o pressionamento
 * (qual handler ficou ativo), cursor, foco, dica, diálogo/menu aberto, o que mudou no estado (assinatura das
 * posições e valores) logo após PRESSIONAR e depois de SOLTAR. Serve de "golden master": rodado antes e depois de
 * mover a ordem de prioridade dos elos do pressionamento para fora da Main, as tabelas têm de ser IGUAIS.
 *
 * Uso: TesteMacacoPressionamentoMouse <arquivo.tsv> [passoPx] [passoFino] [id1,id2,...]
 */
public class TesteMacacoPressionamentoMouse {
    static PrintWriter saida;
    static int linhas;
    static final int Y_INICIAL = 260;     // abaixo da faixa de ferramentas/categorias: nenhum clique sorteia ou troca categoria

    public static void main(String[] a) throws Exception {
        File tsv = new File(a.length > 0 ? a[0] : "pressionamento.tsv");
        int passo = a.length > 1 ? Integer.parseInt(a[1]) : 120;
        int passoFino = a.length > 2 ? Integer.parseInt(a[2]) : 16;   // fina = esse valor; o diagrama usa +20
        String[] ids = a.length > 3 ? a[3].split(",") : new String[]{
                "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114", "PO_TRANSFORMACAO_MEDIDAS_dinheiro_e_brinquedos_188709799",
                "PO_COMPARACAO_MEDIDAS_bolas_487868670", "PO_TRANSFORMACAO_RELACAO_bonecas_620955739",
                "PO_COMPOSICAO_RELACOES_idades_642701604", "PO_COMPOSICAO_MEDIDAS_?"};
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        gerard.ui.GerardTema.instalar();
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        TesteMacacoHistorinhaNumeroRelativo.janela = j[0];
        Thread.sleep(2500);
        Robot r = new Robot(); r.setAutoDelay(10);
        TesteMacacoHistorinhaNumeroRelativo.r = r;
        TesteMacacoHistorinhaNumeroRelativo.saida = tsv.getAbsoluteFile().getParentFile();
        TesteMacacoHistorinhaNumeroRelativo.sorte = new Random(7);
        TesteRobotExploracaoAposConclusao.fechar(r);
        TesteMacacoHistorinhaNumeroRelativo.t = TesteRobotExploracaoAposConclusao.tela(j[0]);

        saida = new PrintWriter(tsv, "UTF-8");
        saida.println("cenario\tfase\tx\ty\tapos_pressionar\tapos_soltar");
        for (String id : ids) cenario(id, passo, passoFino);
        saida.close();
        System.out.println("PONTOS registrados: " + linhas + " em " + tsv);
        System.exit(0);
    }

    static void cenario(String id, int passo, int passoFino) throws Exception {
        Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        TipoSituacaoAditiva tipo = null;
        for (SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
            if (id.endsWith("_?") ? (s.getTipo() == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS && "pt-BR".equals(s.getCodigoIdioma()))
                    : s.getId().equals(id)) { tipo = s.getTipo(); id = s.getId(); break; }
        }
        if (tipo == null) { System.out.println("[PULADO] " + id); return; }
        boolean achou = false;
        for (int i = 0; i < 200 && !achou; i++) {
            TesteRobotExploracaoAposConclusao.selecionar(t, tipo);
            Thread.sleep(300);
            limpar();
            achou = id.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual());
        }
        System.out.println("== " + id + (achou ? "" : " (NÃO SORTEADA)"));
        if (!achou) return;
        TesteMacacoHistorinhaNumeroRelativo.atualizarOrigem();
        // fase "vazio": nada posicionado ainda, os números estão no enunciado (elo do elemento de texto móvel)
        varrerAlvos(id, "vazio_alvos");
        if (System.getProperty("sovazio") != null) return;
        TesteMacacoHistorinhaNumeroRelativo.modelar();
        limpar();
        boolean soAlvos = System.getProperty("soalvos") != null;     // só o centro de cada peça (rápido)
        if (!soAlvos) varrer(id, "modelado", passo, null);
        varrerAlvos(id, "modelado_alvos");
        if (!soAlvos) varrer(id, "modelado_diagrama", passoFino + 20, areaPorMetodo("obterAreaVisivelDiagramasVergnaud"));
        boolean porOperacao = TesteMacacoHistorinhaNumeroRelativo.seletorOperacaoAtivo();
        if (porOperacao) TesteMacacoHistorinhaNumeroRelativo.macacoOperacao();
        else TesteMacacoHistorinhaNumeroRelativo.macacoValor();
        Thread.sleep(2500);
        limpar();
        if (!soAlvos) varrer(id, "limite", passo, null);
        varrerAlvos(id, "limite_alvos");
        if (soAlvos) return;
        varrer(id, "limite_diagrama", passoFino + 20, areaPorMetodo("obterAreaVisivelDiagramasVergnaud"));
        if (tipo != TipoSituacaoAditiva.TRANSFORMACAO_RELACAO && tipo != TipoSituacaoAditiva.COMPOSICAO_RELACOES) {
            varrer(id, "limite_fino", passoFino, areaComplementar());
        }
    }

    static java.awt.Rectangle areaPorMetodo(final String metodo) throws Exception {
        final Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        final java.awt.Rectangle[] area = new java.awt.Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                java.lang.reflect.Method m = Main.TelaGerard.class.getDeclaredMethod(metodo);
                m.setAccessible(true);
                area[0] = new java.awt.Rectangle((java.awt.Rectangle) m.invoke(t));
            } catch (Exception e) { throw new IllegalStateException(e); } } });
        return area[0];
    }

    /** Pressiona e solta no CENTRO de cada peça (itens, textos do enunciado, elementos e conectores do diagrama). */
    static void varrerAlvos(String id, String fase) throws Exception {
        final Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        final List<Point> centros = new ArrayList<Point>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (ItemTextoArrastavel i : t.itensArrastaveis) centros.add(new Point(i.x + i.largura / 2, i.y + i.altura / 2));
            for (gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel x : t.elementosTexto) centros.add(new Point(x.x + x.largura / 2, x.y - x.altura / 2));   // o y do texto é a linha de base
            for (gerard.campoaditivo.diagrama.elementos.ElementoVergnaud x : t.elementosVergnaud) centros.add(new Point(x.x + x.largura / 2, x.y + x.altura / 2));
            for (gerard.campoaditivo.diagrama.elementos.ConectorVergnaud c : t.conectoresVergnaud) centros.add(new Point((c.x1 + c.x2) / 2, (c.y1 + c.y2) / 2));
        } });
        pressionarEm(id, fase, centros);
    }

    static void pressionarEm(String id, String fase, List<Point> pontos) throws Exception {
        final Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        Robot r = TesteMacacoHistorinhaNumeroRelativo.r;
        final Point origem = new Point();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { origem.setLocation(t.getLocationOnScreen()); } });
        for (Point p : pontos) {
            r.mouseMove(origem.x + p.x, origem.y + p.y);
            Thread.sleep(40);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(110);
            String apos = assinatura(t);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(160);
            String solto = assinatura(t);
            saida.println(id + "	" + fase + "	" + p.x + "	" + p.y + "	" + apos + "	" + solto);
            linhas++;
            limpar();
        }
        saida.flush();
    }

    static java.awt.Rectangle areaComplementar() throws Exception {
        final Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        final java.awt.Rectangle[] area = new java.awt.Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                java.lang.reflect.Method m = Main.TelaGerard.class.getDeclaredMethod("obterAreaDiagramaAditivo");
                m.setAccessible(true);
                area[0] = new java.awt.Rectangle((java.awt.Rectangle) m.invoke(t));
            } catch (Exception e) { throw new IllegalStateException(e); } } });
        return area[0];
    }

    /** Pressiona e solta em cada ponto da grade (na área dada ou na área de trabalho inteira) e registra os dois instantes. */
    static void varrer(String id, String fase, int passo, java.awt.Rectangle area) throws Exception {
        final Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        Robot r = TesteMacacoHistorinhaNumeroRelativo.r;
        final Point origem = new Point();
        final int[] dim = new int[2];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            origem.setLocation(t.getLocationOnScreen()); dim[0] = t.getWidth(); dim[1] = t.getHeight(); } });
        int x0 = area == null ? 4 : area.x, x1 = area == null ? dim[0] : area.x + area.width;
        int y0 = area == null ? Y_INICIAL : Math.max(Y_INICIAL, area.y), y1 = area == null ? dim[1] : area.y + area.height;
        for (int y = y0; y < y1; y += passo) {
            for (int x = x0; x < x1; x += passo) {
                r.mouseMove(origem.x + x, origem.y + y);
                Thread.sleep(40);
                r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                Thread.sleep(110);
                String apos = assinatura(t);
                r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                Thread.sleep(160);
                String solto = assinatura(t);
                saida.println(id + "\t" + fase + "\t" + x + "\t" + y + "\t" + apos + "\t" + solto);
                linhas++;
                limpar();
            }
        }
        saida.flush();
    }

    /** Fecha diálogos e menus abertos por um clique, sem confirmar nada (Esc). */
    static void limpar() throws Exception {
        Robot r = TesteMacacoHistorinhaNumeroRelativo.r;
        for (int i = 0; i < 5; i++) {
            final boolean[] aberto = new boolean[1];
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                for (Window w : Window.getWindows()) {
                    if (w.isShowing() && ((w instanceof JDialog && ((JDialog) w).isModal()) || w.getClass().getName().contains("Popup"))) aberto[0] = true;
                } } });
            if (!aberto[0]) return;
            r.keyPress(KeyEvent.VK_ESCAPE); r.keyRelease(KeyEvent.VK_ESCAPE);
            Thread.sleep(180);
        }
    }

    /** Quem está ativo, cursor, foco, dica, diálogo/menu aberto e uma assinatura do estado das peças. */
    static String assinatura(final Main.TelaGerard t) throws Exception {
        final String[] s = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                StringBuilder b = new StringBuilder();
                List<String> ativos = new ArrayList<String>();
                if ((Boolean) chamar(campo(t, "handlerItemTextoArrastavel"), "estaAtivo")) ativos.add("item");
                if (chamar(campo(t, "handlerElementoTextoMovel"), "obterElementoAtivo") != null) ativos.add("texto");
                if ((Boolean) chamar(campo(t, "handlerQuadradinhoVenn"), "estaAtivo")) ativos.add("quadradinho");
                if ((Boolean) chamar(campo(t, "handlerConectorVergnaud"), "estaAtivo")) ativos.add("conector");
                if ((Boolean) chamar(campo(t, "handlerPaineisEixosRelacoes"), "estaAtivo")) ativos.add("paineisEixo");
                if ((Boolean) chamar(campo(t, "handlerControleComparacao"), "estaAtivo")) ativos.add("controleComparacao");
                if ((Boolean) chamar(campo(t, "controladorArrasteElastico"), "estaAtivo")) ativos.add("elastico");
                b.append(ativos.isEmpty() ? "-" : ativos.toString());
                Cursor c = t.getCursor();
                b.append('|').append(c == null ? "null" : (c.getName() == null ? "tipo" + c.getType() : c.getName()));
                b.append('|').append(t.elementoTextoFocado != null ? "T" : "").append(t.itemFocado != null ? "I" : "")
                        .append(t.quadradinhoVennFocado != null ? "Q" : "");
                b.append('|').append(t.mostrarAnotacaoMouseOver ? "dica" : "");
                int dialogos = 0, menus = 0;
                for (Window w : Window.getWindows()) {
                    if (!w.isShowing()) continue;
                    if (w instanceof JDialog && ((JDialog) w).isModal()) dialogos++;
                    else if (w.getClass().getName().contains("Popup")) menus++;
                }
                b.append('|').append("d").append(dialogos).append("m").append(menus);
                // estado das peças: itens do diagrama, textos do enunciado, valor dos elementos, painéis e conclusão
                StringBuilder e = new StringBuilder();
                for (ItemTextoArrastavel i : t.itensArrastaveis) e.append(i.getChavePapelSemantico()).append('=').append(i.valor).append('@').append(i.x).append(',').append(i.y).append(';');
                for (gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel x : t.elementosTexto) e.append(x.valor).append('@').append(x.x).append(',').append(x.y).append(';');
                for (gerard.campoaditivo.diagrama.elementos.ElementoVergnaud x : t.elementosVergnaud) e.append(x.chavePapelSemantico).append(':').append(x.textoEditavel).append(';');
                b.append('|').append(resumo(e.toString()));
                b.append('|').append(t.painelHistorinhasComplementar != null && t.painelHistorinhasComplementar.isShowing() ? "historinha" : "");
                s[0] = b.toString();
            } catch (Exception ex) { throw new IllegalStateException(ex); } } });
        return s[0];
    }

    static String resumo(String texto) throws Exception {
        byte[] h = MessageDigest.getInstance("SHA-1").digest(texto.getBytes("UTF-8"));
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 4; i++) b.append(String.format("%02x", h[i]));
        return b.toString();
    }

    static Object campo(Object o, String nome) throws Exception {
        Class<?> k = o.getClass();
        while (k != null) {
            try { java.lang.reflect.Field f = k.getDeclaredField(nome); f.setAccessible(true); return f.get(o); }
            catch (NoSuchFieldException e) { k = k.getSuperclass(); }
        }
        throw new NoSuchFieldException(nome);
    }

    static Object chamar(Object o, String metodo) throws Exception {
        java.lang.reflect.Method m = o.getClass().getMethod(metodo);
        m.setAccessible(true);
        return m.invoke(o);
    }
}
