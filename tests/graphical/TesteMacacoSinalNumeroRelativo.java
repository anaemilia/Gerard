import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.TentativaEscolhaSinalPapelQuantitativo;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.OpcaoSinalNumeroInteiro;

/**
 * Macaco da escolha de sinal do número relativo (fluxo único em FluxoEscolhaSinalNumeroRelativo; aviso em
 * AvisoSinalDivergente): para situações com número relativo, arrasta cada dado do enunciado até sua figura com o
 * mouse real, abre o menu de sinal e escolhe uma opção ERRADA nos números de posição par e CERTA nos de posição
 * ímpar. Desdobramentos conferidos a cada escolha, contra o critério curado lido da própria tentativa:
 *   - escolha errada: a tentativa do papel soma 1 rejeição e o aviso de sinal divergente aparece;
 *   - escolha certa: a sequência de rejeições do papel zera e o aviso some.
 *
 * Uso: TesteMacacoSinalNumeroRelativo [pastaSaida]
 */
public class TesteMacacoSinalNumeroRelativo {
    static Main janela;
    static Main.TelaGerard t;
    static Robot r;
    static Point o = new Point();
    static int falhas;
    static int verificacoes;
    static int erradas, certas;

    public static void main(String[] a) throws Exception {
        java.util.Locale.setDefault(new java.util.Locale("pt", "BR"));
        gerard.ui.GerardTema.instalar();
        final Main[] j = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            j[0] = new Main(); j[0].setAlwaysOnTop(true); j[0].setVisible(true); j[0].toFront(); }});
        janela = j[0];
        Thread.sleep(2500);
        r = new Robot(); r.setAutoDelay(15);
        TesteRobotExploracaoAposConclusao.fechar(r);
        t = TesteRobotExploracaoAposConclusao.tela(janela);

        String[] alvos = {"PO_TRANSFORMACAO_RELACAO_bonecas_620955739", "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114",
                "PO_TRANSFORMACAO_MEDIDAS_dinheiro_e_brinquedos_188709799"};
        TipoSituacaoAditiva[] tipos = {TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS};
        for (int i = 0; i < alvos.length; i++) situacao(alvos[i], tipos[i]);

        conferir(erradas > 0 && certas > 0, "a rodada exercitou os dois desfechos (erradas=" + erradas + ", certas=" + certas + ")");
        System.out.println("VERIFICAÇÕES de desdobramentos: " + verificacoes + "; RESUMO: falhas=" + falhas);
        System.exit(falhas == 0 ? 0 : 1);
    }

    static void situacao(String id, TipoSituacaoAditiva tipo) throws Exception {
        boolean achou = false;
        for (int i = 0; i < 200 && !achou; i++) {
            TesteRobotExploracaoAposConclusao.selecionar(t, tipo);
            Thread.sleep(300);
            TesteRobotExploracaoAposConclusao.fechar(r);
            achou = id.equals(idAtual());
        }
        System.out.println("== " + id + (achou ? "" : " (NÃO SORTEADA)"));
        if (!achou) { falhas++; return; }
        SwingUtilities.invokeAndWait(new Runnable() { public void run() { o.setLocation(t.getLocationOnScreen()); } });

        final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
        final List<ElementoVergnaud> els = new ArrayList<ElementoVergnaud>();
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            textos.addAll(t.elementosTexto); els.addAll(t.elementosVergnaud); }});
        int ordem = 0;
        for (ElementoTextoMovel tx : textos) {
            if (!tx.possuiVinculoSemantico()) continue;
            ElementoVergnaud alvo = null;
            for (ElementoVergnaud e : els) if (e.chavePapelSemantico.equals(tx.chavePapelSemantico)) alvo = e;
            if (alvo == null) continue;
            final String papel = tx.chavePapelSemantico;
            TesteRobotExploracaoAposConclusao.arrastar(r,
                    new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                    new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
            Thread.sleep(900);
            TentativaEscolhaSinalPapelQuantitativo tentativa = tentativa(papel);
            if (tentativa == null || !haMenuDeSinal()) { TesteRobotExploracaoAposConclusao.fechar(r); continue; }

            NumeroInteiro esperado = (NumeroInteiro) campo(tentativa, "numeroEsperado");
            boolean querErrada = ordem++ % 2 == 0;
            OpcaoSinalNumeroInteiro certa = esperado.correspondeAoSinalRepresentado(OpcaoSinalNumeroInteiro.MAIS)
                    ? OpcaoSinalNumeroInteiro.MAIS : OpcaoSinalNumeroInteiro.MENOS;
            boolean escolherPositivo = (certa == OpcaoSinalNumeroInteiro.MAIS) != querErrada;
            int antes = tentativa.getRejeicoesConsecutivas();
            clicarRadio(escolherPositivo ? "positivo" : "negativo");
            Thread.sleep(1300);
            TesteRobotExploracaoAposConclusao.fechar(r);

            int depois = tentativa.getRejeicoesConsecutivas();
            boolean avisoVisivel = avisoVisivel();
            if (querErrada) {
                erradas++;
                conferir(depois == antes + 1 && avisoVisivel,
                        papel + ": sinal errado -> +1 rejeição (" + antes + "->" + depois + ") e aviso de sinal divergente visível=" + avisoVisivel);
            } else {
                certas++;
                conferir(depois == 0 && !avisoVisivel,
                        papel + ": sinal certo -> rejeições zeradas (" + depois + ") e aviso oculto (visível=" + avisoVisivel + ")");
            }
        }
    }

    // ------------------------------------------------------------------------------------------------ infraestrutura
    @SuppressWarnings("unchecked")
    static TentativaEscolhaSinalPapelQuantitativo tentativa(final String papel) throws Exception {
        final Object[] v = new Object[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            try { v[0] = ((Map<String, TentativaEscolhaSinalPapelQuantitativo>) campo(t, "tentativasEscolhaSinalAtual")).get(papel); }
            catch (Exception e) { throw new IllegalStateException(e); } }});
        return (TentativaEscolhaSinalPapelQuantitativo) v[0];
    }

    static boolean avisoVisivel() throws Exception {
        final boolean[] v = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            v[0] = t.avisoSinalDivergente.estaVisivel(t.itensArrastaveis, t.elementosVergnaud); }});
        return v[0];
    }

    static String idAtual() throws Exception {
        final String[] id = new String[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            id[0] = t.situacaoProblemaAtual == null ? null : t.situacaoProblemaAtual.getId(); }});
        return id[0];
    }

    static boolean haMenuDeSinal() throws Exception {
        return radio("positivo") != null;
    }

    static Point radio(final String prefixo) throws Exception {
        final Point[] p = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() { public void run() {
            for (Window w : Window.getWindows()) { Point q = acharRadio(w, prefixo); if (q != null) p[0] = q; } }});
        return p[0];
    }

    static Point acharRadio(Component c, String prefixo) {
        if (c instanceof JRadioButtonMenuItem && c.isShowing() && ((JRadioButtonMenuItem) c).getText().startsWith(prefixo)) {
            Point l = c.getLocationOnScreen();
            return new Point(l.x + c.getWidth() / 2, l.y + c.getHeight() / 2);
        }
        if (c instanceof Container) for (Component f : ((Container) c).getComponents()) {
            Point q = acharRadio(f, prefixo);
            if (q != null) return q;
        }
        return null;
    }

    static void clicarRadio(String prefixo) throws Exception {
        Point p = radio(prefixo);
        if (p == null) { falhas++; System.out.println("[FALHA] opção de sinal não encontrada: " + prefixo); return; }
        r.mouseMove(p.x, p.y); r.delay(200);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    static Object campo(Object o, String nome) throws Exception {
        Class<?> k = o.getClass();
        while (k != null) {
            try { Field f = k.getDeclaredField(nome); f.setAccessible(true); return f.get(o); }
            catch (NoSuchFieldException e) { k = k.getSuperclass(); }
        }
        throw new NoSuchFieldException(nome);
    }

    static void conferir(boolean ok, String msg) {
        verificacoes++;
        if (!ok) falhas++;
        System.out.println((ok ? "[OK]    " : "[FALHA] ") + msg);
    }
}
