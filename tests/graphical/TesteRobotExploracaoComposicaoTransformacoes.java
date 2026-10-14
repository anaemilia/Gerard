import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.ui.vergnaud.PaineisEixosRelacoes;

/**
 * Desktop, com o mouse REAL (paridade com tests/web/e2e_exploracao_composicao_transformacoes_web.mjs): na Composição de
 * transformações com estados (guloseimas: inicial 35, T1 = -2, T2 = -5, resultante -7, intermediário 33, final 28), depois do
 * azul, mexer na transformação 1 pela reta do eixo faz a transformação resultante, o estado intermediário E o estado final
 * acompanharem; zero numa transformação não é aplicado. (As rosas, situação do relato, só se completam na web: o desktop não
 * tem as figuras "Parte do Estado Inicial" — lacuna registrada na matriz de paridade.)
 *
 * Uso: TesteRobotExploracaoComposicaoTransformacoes [pastaPng] [idSituacao] [valorDoEstadoFinal]
 */
public class TesteRobotExploracaoComposicaoTransformacoes {
    static String situacao = "PO_TRANSFORMACAO_COMPOSTA_DOIS_PASSOS_guloseimas_400916186";
    static String estadoFinalDigitado = "28";
    static int falhas;
    static Main.TelaGerard t;
    static Robot r;
    static Point o = new Point();
    static File pasta;

    public static void main(String[] a) throws Exception {
        pasta = a.length > 0 && a[0].length() > 0 ? new File(a[0]) : null;
        if (a.length > 1) situacao = a[1];
        if (a.length > 2) estadoFinalDigitado = a[2];
        if (pasta != null) pasta.mkdirs();
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        gerard.ui.GerardTema.instalar();
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        Thread.sleep(2500);
        r = new Robot(); r.setAutoDelay(12);
        TesteRobotExploracaoAposConclusao.fechar(r);
        t = TesteRobotExploracaoAposConclusao.tela(j[0]);
        TesteMacacoHistorinhaNumeroRelativo.janela = j[0];
        TesteMacacoHistorinhaNumeroRelativo.t = t;
        TesteMacacoHistorinhaNumeroRelativo.r = r;
        TesteMacacoHistorinhaNumeroRelativo.sorte = new java.util.Random(7);

        int i = 0;
        do {
            TesteRobotExploracaoAposConclusao.selecionar(t, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES);
            Thread.sleep(300);
        } while (++i < 400 && !situacao.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual()));
        if (!situacao.equals(TesteMacacoHistorinhaNumeroRelativo.idAtual())) {
            System.out.println("[NÃO] situação " + situacao + " não sorteada");
            System.exit(2);
        }
        Thread.sleep(800);
        TesteRobotExploracaoAposConclusao.fechar(r);
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); } });
        TesteMacacoHistorinhaNumeroRelativo.o = o;

        // Os dados do enunciado vão ao diagrama; as transformações pedem o sinal (a pessoa deu rosas: negativo).
        final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
        final List<ElementoVergnaud> els = new ArrayList<ElementoVergnaud>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            textos.addAll(t.elementosTexto); els.addAll(t.elementosVergnaud); } });
        boolean gateTestado = false;
        for (ElementoTextoMovel tx : textos) {
            if (!tx.possuiVinculoSemantico()) continue;
            ElementoVergnaud alvo = null;
            for (ElementoVergnaud e : els) if (e.chavePapelSemantico.equals(tx.chavePapelSemantico)) alvo = e;
            if (alvo == null) continue;
            TesteRobotExploracaoAposConclusao.arrastar(r,
                    new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                    new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
            Thread.sleep(700);
            Point negativo = itemDeMenu("negativo");
            if (negativo != null && !gateTestado) {
                gateTestado = true;
                // A modelagem não continua com número relativo esperando sinal: pegar outra peça só reabre o menu.
                ElementoTextoMovel outro = null;
                for (ElementoTextoMovel c : textos) if (c != tx && c.possuiVinculoSemantico() && !c.chavePapelSemantico.equals(tx.chavePapelSemantico)
                        && !c.chavePapelSemantico.contains("transformacao") && !c.chavePapelSemantico.equals("papel.estadoFinal")) { outro = c; break; }
                if (outro != null) {
                    ElementoVergnaud alvo2 = null;
                    for (ElementoVergnaud e : els) if (e.chavePapelSemantico.equals(outro.chavePapelSemantico)) alvo2 = e;
                    final int antes = contarNoDiagrama(t);
                    TesteRobotExploracaoAposConclusao.arrastar(r,
                            new Point(o.x + outro.x + outro.largura / 2, o.y + outro.y - outro.altura / 2),
                            new Point(o.x + alvo2.x + alvo2.largura / 2, o.y + alvo2.y + alvo2.altura / 2));
                    Thread.sleep(700);
                    verificar("com o sinal pendente, outra peça não entra no diagrama", contarNoDiagrama(t) == antes);
                    negativo = itemDeMenu("negativo");
                    verificar("com o sinal pendente, o menu de sinal reaparece", negativo != null);
                }
            }
            if (negativo != null) { clicar(negativo); Thread.sleep(900); }
            if (Boolean.getBoolean("depurar")) {
                int[] v = valores();
                System.out.println("arrastou " + tx.valor + " -> " + tx.chavePapelSemantico + (negativo != null ? " (sinal -)" : "")
                        + " | T1=" + v[0] + " T2=" + v[1] + " res=" + v[2] + " ini=" + v[3] + " fin=" + v[4] + " int=" + v[5]);
                capturar("passo_" + tx.chavePapelSemantico.replace("papel.", ""));
                final StringBuilder dump = new StringBuilder();
                SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                    for (ElementoVergnaud e : t.elementosVergnaud) {
                        dump.append(e.chavePapelSemantico.replace("papel.", "")).append("[textoEditavel='").append(e.textoEditavel).append("'");
                        for (ItemTextoArrastavel it : t.itensArrastaveis) {
                            if (it.estaNoDiagrama() && e.x <= it.x + it.largura / 2 && it.x + it.largura / 2 <= e.x + e.largura
                                    && e.y <= it.y + it.altura / 2 && it.y + it.altura / 2 <= e.y + e.altura) dump.append(" item='").append(it.valor).append("'");
                        }
                        dump.append("] ");
                    } } });
                System.out.println("   elementos: " + dump);
            }
        }
        // A operação entre as transformações (quando a situação tem o seletor) é a que o domínio diz ser a correta.
        clicarOperacaoCorreta();
        // O estado final é a incógnita: a pessoa a digita.
        TesteMacacoHistorinhaNumeroRelativo.digitarNaIncognita(estadoFinalDigitado);
        Thread.sleep(2500);
        TesteRobotExploracaoAposConclusao.fechar(r);
        for (int k = 0; k < 4 && !TesteRobotExploracaoAposConclusao.atingida(t); k++) {
            TesteMacacoHistorinhaNumeroRelativo.cliquePerdido(); // tira o foco da caixa de texto
            Thread.sleep(1500);
        }
        verificar("modelagem concluída (azul) pelos protocolos reais", TesteRobotExploracaoAposConclusao.atingida(t));
        if (Boolean.getBoolean("depurar")) System.out.println("   fase=" + faseConclusao());
        capturar("azul");
        int[] antes = valores();
        System.out.println("no azul: T1=" + antes[0] + " T2=" + antes[1] + " resultante=" + antes[2]
                + " inicial=" + antes[3] + " final=" + antes[4] + " intermediário=" + antes[5]);
        verificar("no azul, o diagrama é consistente (resultante = T1 + T2; final = inicial + resultante)", consistente(antes));

        // Lupa da transformação 1 e arraste do ponto de controle da reta, passo a passo, até o valor mudar.
        final PaineisEixosRelacoes.Painel painel = painelDe("papel.transformacao1");
        verificar("a transformação 1 tem painel de eixo", painel != null);
        if (painel == null) fim();
        Rectangle lupa = TesteRobotLupaEixoRelacao.areaLupa(t, painel);
        clicar(new Point(o.x + lupa.x + lupa.width / 2, o.y + lupa.y + lupa.height / 2));
        Thread.sleep(600);
        TesteRobotExploracaoAposConclusao.fechar(r);
        verificar("clique na lupa revela o eixo da transformação 1", TesteRobotLupaEixoRelacao.revelado(painel));
        mover(painel, +1);
        int[] depois = valores();
        System.out.println("depois de arrastar a reta: T1=" + depois[0] + " T2=" + depois[1] + " resultante=" + depois[2]
                + " inicial=" + depois[3] + " final=" + depois[4] + " intermediário=" + depois[5]);
        capturar("depois_do_arraste");
        verificar("a reta mudou a transformação 1", depois[0] != antes[0]);
        verificar("a transformação resultante acompanhou (T1 + T2)", depois[2] == depois[0] + depois[1]);
        verificar("o estado final acompanhou (inicial + resultante)", depois[4] == depois[3] + depois[2]);
        verificar("o estado intermediário acompanhou (inicial + T1)", depois[5] == depois[3] + depois[0]);

        // Zero numa transformação (inteiros não nulos): a reta não o aplica.
        mover(painel, +1);
        int[] noZero = valores();
        System.out.println("depois de levar a reta ao zero: T1=" + noZero[0] + " resultante=" + noZero[2] + " final=" + noZero[4]);
        capturar("zero_recusado");
        verificar("zero numa transformação não é aplicado", noZero[0] != 0);
        verificar("o diagrama segue consistente depois da tentativa de zero", consistente(noZero));
        fim();
    }

    /** Diagnóstico: por que a avaliação de conclusão ainda não dá CONCLUIDA (papéis esperados e o estado de cada posicionamento). */
    static String faseConclusao() throws Exception {
        final String[] f = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try {
                java.lang.reflect.Method m1 = Main.TelaGerard.class.getDeclaredMethod("capturarPapeisEsperadosConclusao");
                java.lang.reflect.Method m2 = Main.TelaGerard.class.getDeclaredMethod("capturarPosicionamentosConclusao");
                m1.setAccessible(true); m2.setAccessible(true);
                java.util.Collection<String> esperados = (java.util.Collection<String>) m1.invoke(t);
                java.util.List<gerard.campoaditivo.conclusao.EstadoPosicionamentoModelagem> estados =
                        (java.util.List<gerard.campoaditivo.conclusao.EstadoPosicionamentoModelagem>) m2.invoke(t);
                StringBuilder sb = new StringBuilder("concluida=" + t.controladorConclusaoModelagem.isConcluida()
                        + " esperados=" + esperados + " avaliacao="
                        + t.avaliadorConclusaoModelagem.avaliar(esperados, estados));
                for (gerard.campoaditivo.conclusao.EstadoPosicionamentoModelagem e : estados) {
                    sb.append('\n').append("      ").append(e.getPapelItem()).append("->").append(e.getPapelAlvo()).append(" valor=")
                            .append(e.getValorMatematico()).append(" noDiagrama=").append(e.isNoDiagrama())
                            .append(" incog=").append(e.isIncognitaOriginal()).append(" protocolo=").append(e.isPreenchidoPeloProtocoloMouseTexto())
                            .append(" corresp=").append(e.getValorCorrespondeAoCurado()).append(" modificado=").append(e.getEstadoModificado());
                }
                f[0] = sb.toString();
            } catch (Exception ex) { f[0] = "erro: " + ex; }
        } });
        return f[0];
    }

    static int contarNoDiagrama(final Main.TelaGerard t) throws Exception {
        final int[] n = new int[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (ItemTextoArrastavel it : t.itensArrastaveis) if (it.estaNoDiagrama()) n[0]++; } });
        return n[0];
    }

    static boolean consistente(int[] v) {
        return v[2] == v[0] + v[1] && v[4] == v[3] + v[2] && v[5] == v[3] + v[0];
    }

    /** Clica, no seletor Soma/Subtração, na operação que o domínio diz ser a correta (se o seletor está ativo). */
    static void clicarOperacaoCorreta() throws Exception {
        final gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno[] sel = new gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno[1];
        final boolean[] ativo = new boolean[1];
        final boolean[] soma = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            sel[0] = t.seletorOperacaoRelacaoAluno;
            ativo[0] = sel[0].estaAtivo();
            soma[0] = gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                    t.tipoSituacaoSelecionada, t.situacaoProblemaAtual,
                    gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor.ENTRE_TRANSFORMACOES)
                    == gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria.SOMA; } });
        if (!ativo[0]) {
            return;
        }
        java.lang.reflect.Field f = gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno.class.getDeclaredField(
                soma[0] ? "areaSoma" : "areaSubtracao");
        f.setAccessible(true);
        Rectangle area = new Rectangle((Rectangle) f.get(sel[0]));
        clicar(new Point(o.x + area.x + area.width / 2, o.y + area.y + area.height / 2));
        Thread.sleep(900);
        TesteRobotExploracaoAposConclusao.fechar(r);
    }

    /** Arrasta o ponto de controle, um pixel por vez, na direção dada, até o valor navegável mudar (no máximo 600 px). */
    static void mover(final PaineisEixosRelacoes.Painel painel, int direcao) throws Exception {
        mover(painel, direcao, Integer.MIN_VALUE);
    }

    /** Se {@code alvo} não é MIN_VALUE, continua até o valor navegável chegar a ele (ou 600 px). */
    static void mover(final PaineisEixosRelacoes.Painel painel, int direcao, int atual) throws Exception {
        final Rectangle[] ponto = new Rectangle[1];
        final int[] inicio = new int[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            ponto[0] = painel.grafico.obterAreaVisualPontoControle(); inicio[0] = painel.grafico.getValorNavegavel(); } });
        int x = o.x + ponto[0].x + ponto[0].width / 2;
        int y = o.y + ponto[0].y + ponto[0].height / 2;
        r.mouseMove(x, y); Thread.sleep(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); Thread.sleep(150);
        int meta = atual == Integer.MIN_VALUE ? Integer.MIN_VALUE : 0;
        for (int px = 1; px <= 600; px++) {
            r.mouseMove(x + direcao * px, y);
            if (px % 3 == 0) {
                final int[] valor = new int[1];
                SwingUtilities.invokeAndWait(new Runnable() { public void run() { valor[0] = painel.grafico.getValorNavegavel(); } });
                if (meta == Integer.MIN_VALUE ? valor[0] != inicio[0] : valor[0] == meta) break;
            }
        }
        Thread.sleep(200);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        Thread.sleep(900);
        TesteRobotExploracaoAposConclusao.fechar(r);
    }

    static PaineisEixosRelacoes.Painel painelDe(final String chave) throws Exception {
        final PaineisEixosRelacoes.Painel[] p = new PaineisEixosRelacoes.Painel[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (PaineisEixosRelacoes.Painel x : t.paineisEixosRelacoes.obterPaineis()) {
                if (chave.equals(x.elemento.chavePapelSemantico)) p[0] = x;
            } } });
        return p[0];
    }

    /** Os números que a tela mostra: T1, T2, resultante, estado inicial e estado final. */
    static int[] valores() throws Exception {
        final int[] v = new int[6];
        final String[] chaves = {"papel.transformacao1", "papel.transformacao2", "papel.transformacaoFinal",
            "papel.estadoInicial", "papel.estadoFinal", "papel.estadoIntermediario"};
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (int i = 0; i < chaves.length; i++) {
                for (ElementoVergnaud e : t.elementosVergnaud) {
                    if (!chaves[i].equals(e.chavePapelSemantico)) continue;
                    String texto = e.textoEditavel == null ? "" : e.textoEditavel.trim();
                    if (texto.isEmpty()) {
                        for (ItemTextoArrastavel it : t.itensArrastaveis) {
                            if (it.estaNoDiagrama() && e.x <= it.x + it.largura / 2 && it.x + it.largura / 2 <= e.x + e.largura
                                    && e.y <= it.y + it.altura / 2 && it.y + it.altura / 2 <= e.y + e.altura) {
                                texto = it.valor == null ? "" : it.valor.trim();
                            }
                        }
                    }
                    String limpo = texto.replace('−', '-').replaceAll("[^0-9-]", "");
                    v[i] = limpo.isEmpty() || "-".equals(limpo) ? 0 : Integer.parseInt(limpo);
                }
            } } });
        return v;
    }

    static Point itemDeMenu(final String prefixo) throws Exception {
        final Point[] p = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (Window w : Window.getWindows()) {
                java.awt.Component c = achar(w, prefixo);
                if (c != null) { Point l = c.getLocationOnScreen(); p[0] = new Point(l.x + c.getWidth() / 2, l.y + c.getHeight() / 2); return; }
            } } });
        return p[0];
    }

    static java.awt.Component achar(java.awt.Component c, String prefixo) {
        if (c instanceof javax.swing.JRadioButtonMenuItem && c.isShowing()
                && ((javax.swing.JRadioButtonMenuItem) c).getText().startsWith(prefixo)) return c;
        if (c instanceof java.awt.Container) for (java.awt.Component f : ((java.awt.Container) c).getComponents()) {
            java.awt.Component x = achar(f, prefixo); if (x != null) return x; }
        return null;
    }

    static void clicar(Point p) {
        r.mouseMove(p.x, p.y); r.delay(150);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.delay(40); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        r.waitForIdle();
    }

    static void capturar(String nome) throws Exception {
        if (pasta == null) return;
        final Rectangle[] rc = new Rectangle[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            rc[0] = new Rectangle(t.getLocationOnScreen().x, t.getLocationOnScreen().y, t.getWidth(), t.getHeight()); } });
        ImageIO.write(r.createScreenCapture(rc[0]), "png", new File(pasta, nome + ".png"));
    }

    static void fim() {
        System.out.println(falhas == 0 ? "APROVADO: a exploração mantém o diagrama consistente (mouse real)" : "FALHAS: " + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    static void verificar(String nome, boolean ok) {
        if (!ok) falhas++;
        System.out.println((ok ? "[OK] " : "[FALHA] ") + nome);
    }
}
