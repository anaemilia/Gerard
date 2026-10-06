import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;

/**
 * Macaco de FOCO, DICA e CURSOR do movimento do mouse (mouseMoved): move o mouse REAL por uma grade de pontos sobre
 * toda a tela, em vários cenários e fases, e registra a cada ponto o que a interface mostra: se há dica, qual texto,
 * qual cursor e qual alvo está em foco. Escreve a tabela em TSV. Serve de "golden master": rodado antes e depois de
 * mover a lógica de foco/dica/cursor para fora da Main, as duas tabelas têm de ser IGUAIS ponto a ponto.
 *
 * Fases por situação: "modelado" (dados posicionados no diagrama) e "limite" (3 rejeições: aparece a representação
 * complementar/historinha e o material concreto). Reaproveita os gestos reais do macaco da historinha.
 *
 * Uso: TesteMacacoDicaCursorMouse <arquivo.tsv> [passoPx] [id1,id2,...]
 */
public class TesteMacacoDicaCursorMouse {
    static PrintWriter saida;
    static int linhas;

    public static void main(String[] a) throws Exception {
        File tsv = new File(a.length > 0 ? a[0] : "dica_cursor.tsv");
        int passo = a.length > 1 ? Integer.parseInt(a[1]) : 48;
        String[] ids = a.length > 2 ? a[2].split(",") : new String[]{
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
        saida.println("cenario\tfase\tx\ty\tmostrar_dica\tabaixo\ttexto_dica\tcursor\tfoco");
        for (String id : ids) cenario(id, passo);
        saida.close();
        System.out.println("PONTOS registrados: " + linhas + " em " + tsv);
        System.exit(0);
    }

    static void cenario(String id, int passo) throws Exception {
        Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        TipoSituacaoAditiva tipo = null;
        if (id.endsWith("_?")) {
            tipo = TipoSituacaoAditiva.COMPOSICAO_MEDIDAS;
            for (SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
                if (s.getTipo() == tipo && "pt-BR".equals(s.getCodigoIdioma())) { id = s.getId(); break; }
            }
        } else {
            for (SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
                if (s.getId().equals(id)) { tipo = s.getTipo(); break; }
            }
        }
        if (tipo == null) { System.out.println("[PULADO] " + id); return; }
        boolean achou = false;
        for (int i = 0; i < 200 && !achou; i++) {
            TesteRobotExploracaoAposConclusao.selecionar(t, tipo);
            Thread.sleep(300);
            TesteRobotExploracaoAposConclusao.fechar(TesteMacacoHistorinhaNumeroRelativo.r);
            achou = id.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual());
        }
        System.out.println("== " + id + (achou ? "" : " (NÃO SORTEADA)"));
        if (!achou) return;
        TesteMacacoHistorinhaNumeroRelativo.atualizarOrigem();
        // diagrama vazio (nada posicionado)
        varrer(id, "vazio", passo);
        TesteMacacoHistorinhaNumeroRelativo.modelar();
        varrer(id, "modelado", passo);
        boolean porOperacao = TesteMacacoHistorinhaNumeroRelativo.seletorOperacaoAtivo();
        if (porOperacao) TesteMacacoHistorinhaNumeroRelativo.macacoOperacao();
        else TesteMacacoHistorinhaNumeroRelativo.macacoValor();
        Thread.sleep(2500);
        TesteRobotExploracaoAposConclusao.fechar(TesteMacacoHistorinhaNumeroRelativo.r);
        varrer(id, "limite", passo);
        varrerAreaComplementar(id, "limite_fino", 10);
    }

    /** Move o mouse por uma grade e registra, em cada ponto, dica, cursor e foco (lidos na EDT depois do evento). */
    static void varrer(String id, String fase, int passo) throws Exception {
        final Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        Robot r = TesteMacacoHistorinhaNumeroRelativo.r;
        final Point origem = new Point();
        final int[] dim = new int[2];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            origem.setLocation(t.getLocationOnScreen()); dim[0] = t.getWidth(); dim[1] = t.getHeight(); } });
        r.mouseMove(origem.x + 2, origem.y + 2);
        Thread.sleep(150);
        for (int y = 4; y < dim[1]; y += passo) {
            for (int x = 4; x < dim[0]; x += passo) {
                r.mouseMove(origem.x + x, origem.y + y);
                final String[] linha = new String[1];
                final int alvoX = x, alvoY = y;
                // Espera o evento de movimento chegar à tela (mouseMoved grava a posição como primeira ação) e só
                // então lê o estado, já na EDT: assim a leitura nunca antecede o tratamento do evento.
                for (int tentativa = 0; tentativa < 15; tentativa++) {
                    final boolean[] chegou = new boolean[1];
                    SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                        chegou[0] = t.mouseOverX == alvoX && t.mouseOverY == alvoY; } });
                    if (chegou[0]) break;
                    Thread.sleep(10);
                }
                SwingUtilities.invokeAndWait(new Runnable() { public void run() { linha[0] = estado(t); } });
                saida.println(id + "\t" + fase + "\t" + x + "\t" + y + "\t" + linha[0]);
                linhas++;
            }
        }
        saida.flush();
    }

    /**
     * Varredura FINA só sobre a área da representação complementar (onde ficam os controles de adicionar e remover
     * quadradinho e os próprios quadradinhos): os controles são pequenos e a grade grossa não os acerta.
     */
    static void varrerAreaComplementar(String id, String fase, int passo) throws Exception {
        final Main.TelaGerard t = TesteMacacoHistorinhaNumeroRelativo.t;
        Robot r = TesteMacacoHistorinhaNumeroRelativo.r;
        final Point origem = new Point();
        final java.awt.Rectangle[] area = new java.awt.Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                origem.setLocation(t.getLocationOnScreen());
                java.lang.reflect.Method m = Main.TelaGerard.class.getDeclaredMethod("obterAreaDiagramaAditivo");
                m.setAccessible(true);
                area[0] = new java.awt.Rectangle((java.awt.Rectangle) m.invoke(t));
            } catch (Exception e) { throw new IllegalStateException(e); } } });
        for (int y = area[0].y; y < area[0].y + area[0].height; y += passo) {
            for (int x = area[0].x; x < area[0].x + area[0].width; x += passo) {
                r.mouseMove(origem.x + x, origem.y + y);
                final String[] linha = new String[1];
                final int alvoX = x, alvoY = y;
                for (int tentativa = 0; tentativa < 15; tentativa++) {
                    final boolean[] chegou = new boolean[1];
                    SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                        chegou[0] = t.mouseOverX == alvoX && t.mouseOverY == alvoY; } });
                    if (chegou[0]) break;
                    Thread.sleep(10);
                }
                SwingUtilities.invokeAndWait(new Runnable() { public void run() { linha[0] = estado(t); } });
                saida.println(id + "	" + fase + "	" + x + "	" + y + "	" + linha[0]);
                linhas++;
            }
        }
        saida.flush();
    }

    /** O que a interface mostra agora: dica, cursor e alvo em foco (tudo lido de campos da tela). */
    static String estado(Main.TelaGerard t) {
        Cursor c = t.getCursor();
        String cursor = c == null ? "null" : (c.getName() == null ? "tipo" + c.getType() : c.getName());
        List<String> foco = new ArrayList<String>();
        if (t.elementoTextoFocado != null) foco.add("texto:" + t.elementoTextoFocado.valor);
        if (t.itemFocado != null) foco.add("item:" + t.itemFocado.getChavePapelSemantico() + "=" + t.itemFocado.valor);
        if (t.quadradinhoVennFocado != null) foco.add("quadradinho");
        if (t.agrupamentoAdicionarQuadradinhoFocado != null) foco.add("adicionar");
        if (t.agrupamentoRemoverQuadradinhoFocado != null) foco.add("remover");
        String texto = t.textoAnotacaoMouseOver == null ? "" : t.textoAnotacaoMouseOver.replace('\t', ' ').replace('\n', ' ');
        return t.mostrarAnotacaoMouseOver + "\t" + t.forcarAnotacaoMouseOverAbaixo + "\t" + texto + "\t" + cursor + "\t"
                + (foco.isEmpty() ? "-" : foco.toString());
    }
}
