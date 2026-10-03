import java.awt.Dialog;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import gerard.aplicacao.portabilidade.ProjetorAjudaVisualWeb;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;

/**
 * Teste do macaco da regra "número relativo (ou transformação) + 3 rejeições consecutivas =>
 * historinha". Em vez de conduzir situações escolhidas à mão, percorre TODAS as situações
 * validadas que a regra alcança (no idioma da interface, português), sorteando-as pelo menu real
 * e, em cada uma, age como um macaco que nunca acerta: valores errados aleatórios, sinais
 * aleatórios, confirmações "Sim" e cliques perdidos, tudo por mouse e teclado reais (Robot),
 * até o limite de 3 rejeições consecutivas.
 *
 * Verifica, por situação: (1) antes do limite não há historinha no painel direito; (2) no limite
 * a historinha aparece e tem conteúdo desenhado na tela (captura real); (3) a tentativa não foi
 * concluída por acidente. Controle negativo: Composição de Medidas (sem número relativo) nunca
 * mostra historinha. Escreve a tabela aparece/não aparece e as capturas na pasta de saída.
 *
 * Uso: TesteMacacoHistorinhaNumeroRelativo [pastaSaida] [seed] [id1,id2,...|-] [pt-BR|en|fr]
 * Fica no pacote padrão porque TelaGerard tem visibilidade de pacote.
 */
public class TesteMacacoHistorinhaNumeroRelativo {
    static Main janela;
    static Main.TelaGerard t;
    static Robot r;
    static File saida;
    static Point o = new Point();
    static Random sorte;
    static final Map<String, String[]> resultado = new LinkedHashMap<String, String[]>();
    static int falhas;

    public static void main(String[] a) throws Exception {
        saida = new File(a.length > 0 ? a[0] : "macaco_historinhas");
        saida.mkdirs();
        long seed = a.length > 1 ? Long.parseLong(a[1]) : System.currentTimeMillis();
        sorte = new Random(seed);
        final java.util.List<String> filtro = a.length > 2 && !a[2].equals("-")
                ? java.util.Arrays.asList(a[2].split(",")) : null;
        final String idiomaFiltro = a.length > 3 ? a[3] : null;
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        gerard.ui.GerardTema.instalar();
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        janela = j[0];
        Thread.sleep(2500);
        r = new Robot(); r.setAutoDelay(12);
        TesteRobotExploracaoAposConclusao.fechar(r);
        t = TesteRobotExploracaoAposConclusao.tela(janela);

        gerard.idioma.IdiomaInterface[] idiomas = {gerard.idioma.IdiomaInterface.PORTUGUES,
                gerard.idioma.IdiomaInterface.INGLES, gerard.idioma.IdiomaInterface.FRANCES};
        String[] codigos = {"pt-BR", "en", "fr"};
        for (int k = 0; k < idiomas.length; k++) {
            if (idiomaFiltro != null && !idiomaFiltro.equals(codigos[k])) continue;
            trocarIdioma(idiomas[k]);
            percorrer(codigos[k], filtro);
        }
        if (filtro == null && (idiomaFiltro == null || "pt-BR".equals(idiomaFiltro))) {
            trocarIdioma(gerard.idioma.IdiomaInterface.PORTUGUES);
            controleNegativo();
        }

        int apareceu = 0, total = 0;
        PrintWriter w = new PrintWriter(new File(saida, "resultado_macaco.tsv"), "UTF-8");
        w.println("situacao\tcategoria\tcaminho\trejeicoes_no_limite\tstatus\tapoio");
        for (Map.Entry<String, String[]> e : resultado.entrySet()) {
            String[] v = e.getValue();
            w.println(e.getKey() + "\t" + v[0] + "\t" + v[1] + "\t" + v[2] + "\t" + v[3] + "\t" + v[4]);
            if (!e.getKey().startsWith("CONTROLE")) { total++; if ("APARECEU".equals(v[3])) apareceu++; }
        }
        w.close();
        System.out.println("RESUMO: historinha apareceu em " + apareceu + " de " + total + " situações; falhas=" + falhas);
        System.exit(falhas == 0 && apareceu == total ? 0 : 1);
    }


    /** Sorteia e conduz, no idioma atual da interface, todas as situações que a regra alcança. */
    static void percorrer(String codigoIdioma, java.util.List<String> filtro) throws Exception {
        Map<TipoSituacaoAditiva, Set<String>> alvos = new LinkedHashMap<TipoSituacaoAditiva, Set<String>>();
        Map<String, SituacaoProblemaAditiva> porId = new LinkedHashMap<String, SituacaoProblemaAditiva>();
        for (SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
            if (!codigoIdioma.equals(s.getCodigoIdioma())) continue;
            if (filtro != null && !filtro.contains(s.getId())) continue;
            boolean alcancada = SemanticaCuradaSituacao.possuiNumeroRelativo(s) || s.getTipo().envolveTransformacao();
            if (!alcancada) continue;
            Set<String> ids = alvos.get(s.getTipo());
            if (ids == null) { ids = new LinkedHashSet<String>(); alvos.put(s.getTipo(), ids); }
            ids.add(s.getId());
            porId.put(s.getId(), s);
        }
        System.out.println("##### idioma=" + codigoIdioma + " situações-alvo=" + porId.size());
        for (Map.Entry<TipoSituacaoAditiva, Set<String>> e : alvos.entrySet()) {
            Set<String> faltam = new LinkedHashSet<String>(e.getValue());
            for (int tentativa = 0; tentativa < 150 && !faltam.isEmpty(); tentativa++) {
                TesteRobotExploracaoAposConclusao.selecionar(t, e.getKey());
                Thread.sleep(300);
                TesteRobotExploracaoAposConclusao.fechar(r);
                String id = idAtual();
                if (id == null || !faltam.contains(id)) continue;
                faltam.remove(id);
                atualizarOrigem();
                conduzir(porId.get(id));
            }
            for (String id : faltam) resultado.put(id, new String[]{e.getKey().name(), "-", "-", "NAO_SORTEADA", "-"});
        }
    }

    /** Troca o idioma da interface pelo menu Exibir > Idioma (o mesmo item que a usuária clicaria). */
    static void trocarIdioma(final gerard.idioma.IdiomaInterface idioma) throws Exception {
        final boolean[] ok = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            javax.swing.JMenuBar barra = janela.getJMenuBar();
            for (int i = 0; barra != null && i < barra.getMenuCount() && !ok[0]; i++) {
                javax.swing.JMenu topo = barra.getMenu(i);
                if (topo == null || topo.getMnemonic() != KeyEvent.VK_X || topo.getItemCount() == 0) continue;
                javax.swing.JMenuItem sub = topo.getItem(0);
                if (!(sub instanceof javax.swing.JMenu)) continue;
                javax.swing.JMenu menuIdioma = (javax.swing.JMenu) sub;
                javax.swing.JMenuItem alvo = menuIdioma.getItem(idioma.ordinal());
                if (alvo != null && alvo.isEnabled()) { alvo.doClick(); ok[0] = true; }
                else if (alvo != null) ok[0] = true;   // já é o idioma atual
            }
        }});
        if (!ok[0]) throw new IllegalStateException("menu de idioma não encontrado");
        Thread.sleep(1500);
        TesteRobotExploracaoAposConclusao.fechar(r);
        Thread.sleep(1500);
        atualizarOrigem();
        System.out.println("idioma da interface = " + idioma);
    }

    // ---------------------------------------------------------------- condução por situação

    static void conduzir(SituacaoProblemaAditiva s) throws Exception {
        String id = s.getId();
        String tipo = s.getTipo().name();
        String esperado = ProjetorAjudaVisualWeb.projetar(s, true).isEmpty() ? "-"
                : String.valueOf(((Map<?, ?>) ProjetorAjudaVisualWeb.projetar(s, true).get(0)).get("tipo"));
        boolean porOperacao = seletorOperacaoAtivo();
        String caminho = porOperacao ? "operacao" : "valor";
        System.out.println("== " + id + " (" + caminho + ")");
        boolean acidente = false;
        if (porOperacao) {
            acidente = macacoOperacao();
        } else {
            modelar();
            if (!incognitaEditavelPresente()) {          // relayout após troca de idioma: repete uma vez
                Thread.sleep(1000);
                atualizarOrigem();
                modelar();
            }
            acidente = macacoValor();
        }
        int rej = rejeicoes();
        boolean limite = noLimite();
        String status;
        if (acidente) {
            Thread.sleep(1500);
            capturar(id);
            status = "CONCLUIU_POR_ACIDENTE";
        } else if (!limite) {
            status = "LIMITE_NAO_ATINGIDO";
        } else {
            Thread.sleep(2200);   // abertura gradual da historinha (~1,1 s)
            int tinta = tintaDoPainelDireito();
            capturar(id);
            status = tinta > 0 ? "APARECEU" : "NAO_APARECEU";
        }
        if (!"APARECEU".equals(status)) { falhas++; System.out.println("[FALHA] " + id + " => " + status); }
        else System.out.println("[OK] " + id);
        resultado.put(id, new String[]{tipo, caminho, String.valueOf(rej), status, esperado});
    }

    /** Macaco no caminho de valor/sinal: nunca acerta; devolve true se concluiu por acidente. */
    static boolean macacoValor() throws Exception {
        for (int rodada = 0; rodada < 16; rodada++) {
            if (TesteRobotExploracaoAposConclusao.atingida(t)) return true;
            if (noLimite()) return false;
            if (painelVisivel()) {
                falhas++;
                System.out.println("[FALHA] historinha visível antes do limite (rej=" + rejeicoes() + ")");
            }
            if (sorte.nextInt(4) == 0) cliquePerdido();
            if (TesteRobotExploracaoAposConclusao.haDialogo()) {
                tecla(KeyEvent.VK_ENTER);                       // "Sim": confirma o valor rejeitado
                Thread.sleep(1500);
                if (TesteRobotExploracaoAposConclusao.haDialogo()) digitarNoDialogo(valorErrado());
                continue;
            }
            Point menu = opcaoDeSinalNaTela();
            if (menu != null) { clicar(menu); Thread.sleep(1500); continue; }
            digitarNaIncognita(valorErrado());
            Thread.sleep(1300);
            Point menu2 = opcaoDeSinalNaTela();
            if (menu2 != null) { clicar(menu2); Thread.sleep(1500); }
        }
        return TesteRobotExploracaoAposConclusao.atingida(t);
    }

    /** Macaco no seletor Soma/Subtração: só cliques na opção errada. */
    static boolean macacoOperacao() throws Exception {
        final gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno[] sel = new gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { sel[0] = t.seletorOperacaoRelacaoAluno; }});
        final gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria[] certa =
                new gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            certa[0] = gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                    t.tipoSituacaoSelecionada, t.situacaoProblemaAtual,
                    gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor.ENTRE_TRANSFORMACOES); }});
        boolean erradaESoma = certa[0] != gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria.SOMA;
        for (int i = 0; i < 8 && !noLimite(); i++) {
            if (painelVisivel()) {
                falhas++;
                System.out.println("[FALHA] historinha visível antes do limite (rej=" + rejeicoes() + ")");
            }
            if (sorte.nextInt(4) == 0) cliquePerdido();
            java.lang.reflect.Field f = gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno.class.getDeclaredField(
                    erradaESoma ? "areaSoma" : "areaSubtracao");
            f.setAccessible(true);
            Rectangle area = new Rectangle((Rectangle) f.get(sel[0]));
            clicar(new Point(o.x + area.x + area.width / 2, o.y + area.y + area.height / 2));
            Thread.sleep(900);
            TesteRobotExploracaoAposConclusao.fechar(r);
        }
        return TesteRobotExploracaoAposConclusao.atingida(t);
    }

    static void controleNegativo() throws Exception {
        for (int tentativa = 0; tentativa < 40; tentativa++) {
            TesteRobotExploracaoAposConclusao.selecionar(t, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
            Thread.sleep(300);
            TesteRobotExploracaoAposConclusao.fechar(r);
            if (t.situacaoProblemaAtual != null && !SemanticaCuradaSituacao.possuiNumeroRelativo(t.situacaoProblemaAtual)) break;
        }
        atualizarOrigem();
        String id = "CONTROLE_" + idAtual();
        modelar();
        macacoValorSemExigirLimite(5);
        Thread.sleep(2200);
        boolean visivel = painelVisivel();
        capturar(id);
        if (visivel) { falhas++; System.out.println("[FALHA] controle negativo mostrou historinha"); }
        else System.out.println("[OK] controle negativo (sem número relativo) não mostra historinha");
        resultado.put(id, new String[]{"COMPOSICAO_MEDIDAS", "valor", String.valueOf(rejeicoes()),
                visivel ? "APARECEU_INDEVIDAMENTE" : "NAO_APARECEU_COMO_ESPERADO", "-"});
    }

    static void macacoValorSemExigirLimite(int rodadas) throws Exception {
        for (int rodada = 0; rodada < rodadas && !TesteRobotExploracaoAposConclusao.atingida(t); rodada++) {
            if (TesteRobotExploracaoAposConclusao.haDialogo()) {
                tecla(KeyEvent.VK_ENTER);
                Thread.sleep(1500);
                if (TesteRobotExploracaoAposConclusao.haDialogo()) digitarNoDialogo(valorErrado());
                continue;
            }
            digitarNaIncognita(valorErrado());
            Thread.sleep(1300);
        }
    }

    // ---------------------------------------------------------------- observação do estado

    /** Valor que não pode ser o certo: bem acima de qualquer resposta curada. */
    static String valorErrado() { return String.valueOf(200 + sorte.nextInt(799)); }

    static String idAtual() throws Exception {
        final String[] v = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            v[0] = t.situacaoProblemaAtual == null ? null : t.situacaoProblemaAtual.getId(); }});
        return v[0];
    }

    static boolean seletorOperacaoAtivo() throws Exception {
        final boolean[] b = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            b[0] = t.seletorOperacaoRelacaoAluno != null && t.seletorOperacaoRelacaoAluno.estaAtivo(); }});
        return b[0];
    }

    static boolean noLimite() throws Exception { return tentativa().estaNoLimiteAjudaVisual(); }

    static int rejeicoes() throws Exception {
        final int[] n = new int[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                java.lang.reflect.Field g = Main.TelaGerard.class.getDeclaredField("tentativasIncognitaAtual");
                g.setAccessible(true);
                Object papel = g.get(t);
                n[0] = papel == null ? 0 : ((gerard.dominio.campoaditivo.PapelQuantitativo) papel).getTentativasRejeitadasConsecutivas();
            } catch (Exception e) { n[0] = -1; }
        }});
        return n[0];
    }

    static gerard.dominio.campoaditivo.TentativaModelagemAditiva tentativa() throws Exception {
        final Object[] v = new Object[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                java.lang.reflect.Field f = Main.TelaGerard.class.getDeclaredField("tentativaModelagemAtual");
                f.setAccessible(true);
                v[0] = f.get(t);
            } catch (Exception e) { throw new RuntimeException(e); }
        }});
        return (gerard.dominio.campoaditivo.TentativaModelagemAditiva) v[0];
    }

    static boolean painelVisivel() throws Exception {
        final boolean[] b = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            JComponent p = t.painelHistorinhasComplementar;
            b[0] = p != null && p.isVisible() && p.isShowing(); }});
        return b[0];
    }

    /** Pixels diferentes do fundo no painel direito, em captura REAL da tela. */
    static int tintaDoPainelDireito() throws Exception {
        final Rectangle[] rc = new Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            JComponent p = t.painelHistorinhasComplementar;
            if (p != null && p.isShowing()) {
                Point l = p.getLocationOnScreen();
                rc[0] = new Rectangle(l.x, l.y, p.getWidth(), p.getHeight());
            }
        }});
        if (rc[0] == null || rc[0].width < 10 || rc[0].height < 10) return 0;
        BufferedImage img = r.createScreenCapture(rc[0]);
        int fundo = img.getRGB(2, 2) & 0xFFFFFF;
        int tinta = 0;
        for (int y = 0; y < img.getHeight(); y += 2)
            for (int x = 0; x < img.getWidth(); x += 2)
                if (distancia(img.getRGB(x, y) & 0xFFFFFF, fundo) > 24) tinta++;
        return tinta > (img.getWidth() * img.getHeight()) / 4 / 200 ? tinta : 0;   // > 0,5% da área
    }

    static int distancia(int a, int b) {
        return Math.abs(((a >> 16) & 255) - ((b >> 16) & 255)) + Math.abs(((a >> 8) & 255) - ((b >> 8) & 255))
                + Math.abs((a & 255) - (b & 255));
    }

    // ---------------------------------------------------------------- ações reais (Robot)

    static void atualizarOrigem() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); }});
    }

    static void modelar() throws Exception {
        final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
        final List<ElementoVergnaud> els = new ArrayList<ElementoVergnaud>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            textos.addAll(t.elementosTexto); els.addAll(t.elementosVergnaud); }});
        for (ElementoTextoMovel tx : textos) {
            if (!tx.possuiVinculoSemantico()) continue;
            ElementoVergnaud alvo = null;
            for (ElementoVergnaud e : els) if (e.chavePapelSemantico.equals(tx.chavePapelSemantico)) alvo = e;
            if (alvo == null) continue;
            TesteRobotExploracaoAposConclusao.arrastar(r,
                    new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                    new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
        }
        Thread.sleep(800);
    }

    static void digitarNoDialogo(String v) throws Exception {
        Thread.sleep(600);
        for (char ch : v.toCharArray()) tecla(KeyEvent.VK_0 + (ch - '0'));
        Thread.sleep(300);
        tecla(KeyEvent.VK_ENTER);
        Thread.sleep(1500);
    }

    /** Clique perdido numa região vazia do canto inferior esquerdo da área de trabalho. */
    static void cliquePerdido() throws Exception {
        final Point[] p = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            p[0] = new Point(o.x + 40 + sorte.nextInt(120), o.y + t.getHeight() - 30 - sorte.nextInt(40)); }});
        clicar(p[0]);
        Thread.sleep(300);
    }

    static void clicar(Point p) {
        r.mouseMove(p.x, p.y); r.delay(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.delay(40); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        r.waitForIdle();
    }


    static boolean incognitaEditavelPresente() throws Exception {
        final boolean[] ok = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel it : t.itensArrastaveis)
                if (it.estaNoDiagrama() && it.editavel && it.representaIncognitaOriginal()) ok[0] = true; }});
        return ok[0];
    }

    /** Edita SEMPRE a incógnita original; os dados do enunciado nunca são tocados pelo macaco. */
    static void digitarNaIncognita(String v) throws Exception {
        final int[] c = new int[2];
        final boolean[] ok = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel it : t.itensArrastaveis)
                if (it.estaNoDiagrama() && it.editavel && it.representaIncognitaOriginal()) {
                    c[0] = it.x + it.largura / 2; c[1] = it.y + it.altura / 2; ok[0] = true; }
        }});
        if (!ok[0]) { System.out.println("incógnita editável não encontrada"); return; }
        System.out.println("digitando " + v);
        r.mouseMove(o.x + c[0], o.y + c[1]); r.delay(100);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); r.delay(35);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(400);
        for (char ch : v.toCharArray()) tecla(KeyEvent.VK_0 + (ch - '0'));
        tecla(KeyEvent.VK_ENTER);
    }

    static void tecla(int k) { r.keyPress(k); r.keyRelease(k); }

    static java.awt.Component achar(java.awt.Component c, String prefixo) {
        if (c instanceof javax.swing.AbstractButton && ((javax.swing.AbstractButton) c).getText() != null
                && ((javax.swing.AbstractButton) c).getText().startsWith(prefixo) && c.isShowing()) return c;
        if (c instanceof java.awt.Container) for (java.awt.Component f : ((java.awt.Container) c).getComponents()) {
            java.awt.Component x = achar(f, prefixo); if (x != null) return x; }
        return null;
    }

    /** Centro, na tela, do item de menu visível com o texto dado; null se não há menu aberto. */
    static Point itemNaTela(final String prefixo) throws Exception {
        final Point[] p = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (Window w : Window.getWindows()) {
                java.awt.Component c = achar(w, prefixo);
                if (c != null) { Point l = c.getLocationOnScreen(); p[0] = new Point(l.x + c.getWidth() / 2, l.y + c.getHeight() / 2); return; }
            } }});
        return p[0];
    }


    /** Centro de uma das duas opções de sinal (radio) do menu aberto, sorteada; null se não há menu. */
    static Point opcaoDeSinalNaTela() throws Exception {
        final java.util.List<Point> pontos = new ArrayList<Point>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (Window w : Window.getWindows()) coletarRadios(w, pontos); }});
        return pontos.isEmpty() ? null : pontos.get(sorte.nextInt(pontos.size()));
    }

    static void coletarRadios(java.awt.Component c, java.util.List<Point> pontos) {
        if (c instanceof javax.swing.JRadioButtonMenuItem && c.isShowing()) {
            Point l = c.getLocationOnScreen(); pontos.add(new Point(l.x + c.getWidth() / 2, l.y + c.getHeight() / 2));
        }
        if (c instanceof java.awt.Container) for (java.awt.Component f : ((java.awt.Container) c).getComponents()) coletarRadios(f, pontos);
    }

    static void capturar(String nome) throws Exception {
        final Rectangle[] rc = new Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            Point p = janela.getRootPane().getLocationOnScreen();
            rc[0] = new Rectangle(p.x, p.y, janela.getRootPane().getWidth(), janela.getRootPane().getHeight()); }});
        r.mouseMove(rc[0].x + rc[0].width - 5, rc[0].y + rc[0].height - 5);
        r.delay(300);
        ImageIO.write(r.createScreenCapture(rc[0]), "png", new File(saida, nome + ".png"));
    }
}
