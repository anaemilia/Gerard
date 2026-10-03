import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.diagrama.elementos.*;
import gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno;

/** Mouse/teclado reais. Reflexão somente para ler geometria e estado, nunca para mudá-los. */
public class TesteRobotHistorinhasCategoriasRelativas extends TesteRobotExploracaoAposConclusao {
    static File evidencias;
    public static void main(String[] args) throws Exception {
        evidencias = new File(args[0]); evidencias.mkdirs();
        new gerard.agente.modelousuario.RepositorioModeloUsuario().cadastrarPerfil(
                "Teste historinhas", 10, gerard.agente.modelousuario.Genero.OUTRO,
                gerard.agente.modelousuario.MidiaPreferida.VIDEO,
                gerard.agente.modelousuario.NivelEscolaridade.PRIMEIRO_GRAU, null);
        final Main[] janela = new Main[1];
        SwingUtilities.invokeAndWait(() -> {
            janela[0] = new Main(); janela[0].setAlwaysOnTop(true);
            janela[0].setVisible(true); janela[0].toFront();
        });
        Robot r = new Robot(); r.setAutoDelay(20); Thread.sleep(2000); fechar(r);
        capturar(r, "00_inicio");
        Main.TelaGerard t = tela(janela[0]);
        entrar(r, t);
        for (TipoSituacaoAditiva tipo : new TipoSituacaoAditiva[] {
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, TipoSituacaoAditiva.COMPOSICAO_RELACOES }) {
            for (int i = 0; i < 60; i++) {
                selecionarMouse(r, t, tipo); Thread.sleep(600); fechar(r);
                if (t.situacaoProblemaAtual != null && t.tipoSituacaoSelecionada == tipo
                        && t.seletorOperacaoRelacaoAluno.estaAtivo()) break;
            }
            verificar(tipo + ": sem historinha antes de rejeitar", !historinhaVisivel(t));
            System.out.println("SITUACAO=" + t.situacaoProblemaAtual.getId());
            Point o = t.getLocationOnScreen();
            List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>(t.elementosTexto);
            // Papéis dados primeiro; incógnita por último.
            for (int passada = 0; passada < 2; passada++) for (ElementoTextoMovel tx : textos) {
                if (!tx.possuiVinculoSemantico()) continue;
                SemanticaCuradaSituacao.PapelCurado pc = SemanticaCuradaSituacao.buscar(t.situacaoProblemaAtual, null, tx.chavePapelSemantico);
                if (pc == null || pc.isDesconhecido() != (passada == 1)) continue;
                ElementoVergnaud alvo = null;
                for (ElementoVergnaud e : t.elementosVergnaud) if (e.chavePapelSemantico.equals(tx.chavePapelSemantico)) alvo = e;
                if (alvo == null) continue;
                arrastar(r, new Point(o.x + tx.x + tx.largura / 2, o.y + tx.y - tx.altura / 2),
                        new Point(o.x + alvo.x + alvo.largura / 2, o.y + alvo.y + alvo.altura / 2));
                escolherSinal(r, pc.getValor().startsWith("-"));
            }
            OpcaoOperacaoCuradoria correta = AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(
                    tipo, t.situacaoProblemaAtual, TipoOperacaoSeletor.ENTRE_TRANSFORMACOES);
            OpcaoOperacaoCuradoria errada = correta == OpcaoOperacaoCuradoria.SOMA
                    ? OpcaoOperacaoCuradoria.SUBTRACAO : OpcaoOperacaoCuradoria.SOMA;
            for (int n = 1; n <= 3; n++) {
                clicarOperacao(r, t, t.seletorOperacaoRelacaoAluno, errada);
                verificar(tipo + ": historinha após " + n + " rejeição(ões)", historinhaVisivel(t) == (n == 3));
                capturar(r, tipo + "_rejeicao_" + n);
            }
            String identificador = null;
            for (gerard.dominio.campoaditivo.ajuda.HistorinhaAjudaVisual h : tipo.selecionarRepertorioAjudaVisual().getHistorinhas())
                if (h.getIdSituacaoCurada().equals(t.situacaoProblemaAtual.getId())) identificador = h.getIdentificador();
            verificar(tipo + ": ilustração própria projetada", identificador != null
                    && t.painelHistorinhasComplementar instanceof gerard.ui.ajuda.PainelAjudaNarrativaVisualCategoria
                    && ("projecao:" + identificador).equals(((gerard.ui.ajuda.PainelAjudaNarrativaVisualCategoria)
                            t.painelHistorinhasComplementar).getChaveRepertorio()));
            clicarOperacao(r, t, t.seletorOperacaoRelacaoAluno, correta);
            if (t.seletorOperacaoEstadoTransformacaoAluno.estaAtivo()) {
                clicarOperacao(r, t, t.seletorOperacaoEstadoTransformacaoAluno,
                        AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta(tipo,
                                t.situacaoProblemaAtual, TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO));
            }
            for (SemanticaCuradaSituacao.PapelCurado pc : SemanticaCuradaSituacao.mapear(t.situacaoProblemaAtual, null)) {
                if (!pc.isDesconhecido() || pc.getValorInteiro() == null) continue;
                digitarComFoco(r, t, o, Integer.toString(Math.abs(pc.getValorInteiro())));
                escolherSinal(r, pc.getValorInteiro() < 0);
                fechar(r);
            }
            Thread.sleep(1700); fechar(r);
            verificar(tipo + ": conclusão real", atingida(t));
            verificar(tipo + ": historinha permanece na conclusão", historinhaVisivel(t));
            capturar(r, tipo + "_concluida");
        }
        selecionarMouse(r, t, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        Thread.sleep(1000); fechar(r);
        verificar("nova situação limpa historinha antes de rejeitar", !historinhaVisivel(t));
        capturar(r, "transformacao_nova_situacao");
        System.out.println("RESUMO falhas=" + falhas); janela[0].dispose(); System.exit(falhas == 0 ? 0 : 1);
    }
    static boolean historinhaVisivel(Main.TelaGerard t) {
        return t.painelHistorinhasComplementar != null && t.painelHistorinhasComplementar.isShowing();
    }
    static void digitarComFoco(Robot r, Main.TelaGerard t, Point o, String valor) throws Exception {
        ItemTextoArrastavel item=null;
        for(ItemTextoArrastavel it:t.itensArrastaveis)if(it.estaNoDiagrama()&&it.editavel)item=it;
        if(item==null)throw new IllegalStateException("incógnita não engatada");
        r.mouseMove(o.x+item.x+item.largura/2,o.y+item.y+item.altura/2);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);r.delay(40);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);r.delay(350);
        for(Window w:Window.getWindows())if(w instanceof JDialog && w.isShowing()) {
            Component campo=acharCampo(w);if(campo==null)continue;
            clicar(r,campo);r.keyPress(java.awt.event.KeyEvent.VK_CONTROL);r.keyPress(java.awt.event.KeyEvent.VK_A);
            r.keyRelease(java.awt.event.KeyEvent.VK_A);r.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
            for(char ch:valor.toCharArray()){int k=java.awt.event.KeyEvent.VK_0+ch-'0';r.keyPress(k);r.keyRelease(k);}
            r.keyPress(java.awt.event.KeyEvent.VK_ENTER);r.keyRelease(java.awt.event.KeyEvent.VK_ENTER);r.delay(500);return;
        }
        throw new IllegalStateException("editor não aberto");
    }
    static Component acharCampo(Component c) {
        if(c instanceof JTextField && c.isShowing())return c;
        if(c instanceof Container)for(Component f:((Container)c).getComponents()){Component a=acharCampo(f);if(a!=null)return a;}
        return null;
    }
    static void entrar(Robot r, Main.TelaGerard t) throws Exception {
        Component botao=acharBotao(SwingUtilities.getWindowAncestor(t),
                gerard.i18n.ServicoLocalizacao.getInstancia().texto("ui.tooltip.user"), true);
        clicar(r,botao); Thread.sleep(600);
        for(Window w:Window.getWindows()) if(w instanceof gerard.ui.usuario.DialogoUsuario && w.isShowing()) {
            JList<?> lista=(JList<?>)acharLista(w);
            Point p=lista.getLocationOnScreen(); Rectangle celula=lista.getCellBounds(0,0);
            r.mouseMove(p.x+celula.x+celula.width/2,p.y+celula.y+celula.height/2);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); r.delay(250);
            clicar(r,acharBotao(w,gerard.i18n.ServicoLocalizacao.getInstancia().texto("ui.userDialog.enter"),false));
            Thread.sleep(600); capturar(r,"01_login");
            if(w.isShowing())throw new IllegalStateException("login não concluído"); return;
        }
        throw new IllegalStateException("diálogo de login não aberto");
    }
    static Component acharLista(Component c) {
        if(c instanceof JList && c.isShowing())return c;
        if(c instanceof Container)for(Component f:((Container)c).getComponents()){Component a=acharLista(f);if(a!=null)return a;}
        return null;
    }
    static Component acharBotao(Component c,String texto,boolean tooltip) {
        if(c instanceof JButton && c.isShowing() && texto.equals(tooltip?((JButton)c).getToolTipText():((JButton)c).getText()))return c;
        if(c instanceof Container)for(Component f:((Container)c).getComponents()){Component a=acharBotao(f,texto,tooltip);if(a!=null)return a;}
        return null;
    }
    static void clicar(Robot r, Component c) throws Exception {
        final Point p = new Point();
        SwingUtilities.invokeAndWait(() -> { p.setLocation(c.getLocationOnScreen()); p.translate(c.getWidth()/2, c.getHeight()/2); });
        r.mouseMove(p.x,p.y); r.delay(150); r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK); r.delay(250);
    }
    static void abrirPais(Robot r, Component c) throws Exception {
        if (c.getParent() instanceof JPopupMenu) {
            Component pai = ((JPopupMenu)c.getParent()).getInvoker();
            abrirPais(r,pai); clicar(r,pai);
        }
    }
    static void selecionarMouse(Robot r, Main.TelaGerard t, TipoSituacaoAditiva tipo) throws Exception {
        JButton botao = tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES ? t.botaoAtalhoComposicaoTransformacoes
                : tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO ? t.botaoAtalhoTransformacaoRelacao
                : tipo == TipoSituacaoAditiva.COMPOSICAO_RELACOES ? t.botaoAtalhoComposicaoRelacoes
                : t.botaoAtalhoTransformacao;
        SwingUtilities.invokeAndWait(() -> { Window w=SwingUtilities.getWindowAncestor(t); w.toFront(); w.requestFocus(); });
        JButton sortear=tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                ? t.botaoFerramentaSortearMedidas : t.botaoFerramentaSortearRelacoes;
        for(int i=0;i<150;i++) {
            clicar(r,sortear); r.waitForIdle(); Thread.sleep(300); fechar(r);
            if(t.situacaoProblemaAtual != null && t.situacaoProblemaAtual.getTipo()==tipo
                    && (tipo != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES
                    || "PO_COMPOSICAO_TRANSFORMACOES_bolas_509261012".equals(t.situacaoProblemaAtual.getId())))break;
        }
        System.out.println("CLIQUE_CATEGORIA=" + tipo + " em " + botao.getLocationOnScreen());
        clicar(r,botao); r.waitForIdle();
    }
    static void clicarOperacao(Robot r, Main.TelaGerard t, SeletorOperacaoRelacaoAluno seletor,
            OpcaoOperacaoCuradoria opcao) throws Exception {
        Field f = SeletorOperacaoRelacaoAluno.class.getDeclaredField(opcao == OpcaoOperacaoCuradoria.SOMA ? "areaSoma" : "areaSubtracao");
        f.setAccessible(true); Rectangle area = (Rectangle)f.get(seletor); Point o = t.getLocationOnScreen();
        r.mouseMove(o.x+area.x+area.width/2,o.y+area.y+area.height/2);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK); r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        r.waitForIdle(); Thread.sleep(600); fechar(r);
    }
    static void escolherSinal(Robot r, boolean negativo) throws Exception {
        for (Window w : Window.getWindows()) {
            Component opcao = acharSinal(w, negativo);
            if (opcao != null) { clicar(r,opcao); r.waitForIdle(); Thread.sleep(300); return; }
        }
    }
    static Component acharSinal(Component c, boolean negativo) {
        if (c instanceof JRadioButtonMenuItem && c.isShowing()
                && ((JRadioButtonMenuItem)c).getText().startsWith(negativo ? "negativo" : "positivo")) return c;
        if (c instanceof Container) for (Component filho : ((Container)c).getComponents()) {
            Component achado = acharSinal(filho,negativo); if (achado != null) return achado;
        }
        return null;
    }
    static void capturar(Robot r, String nome) throws Exception {
        ImageIO.write(r.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())),
                "png",new File(evidencias,nome+".png"));
    }
}
