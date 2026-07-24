import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;

/**
 * Teste de estresse "semi-guiado": em vez de cliques cegos em coordenadas
 * aleatorias da tela (que raramente exercitam o fluxo real do Gerard), sorteia
 * a cada iteracao uma acao entre as que fazem sentido no estado atual da
 * TelaGerard (arrastar um item para um alvo do diagrama, digitar um valor
 * numa incognita editavel, ou pedir uma nova situacao via botaoSortear) e
 * executa via java.awt.Robot, entao passa pelos mesmos MouseListener/
 * KeyListener reais que um usuario humano acionaria.
 *
 * Fica no pacote padrao (sem "package") de proposito, igual a Main.java:
 * TelaGerard e seus campos (itensArrastaveis, elementosVergnaud,
 * botaoSortear...) tem visibilidade de pacote, entao esta classe precisa
 * estar no mesmo pacote para le-los sem reflexao.
 *
 * Nao faz parte do app entregue ao usuario final - e so uma ferramenta de
 * teste local. Ao empacotar o instalador com jpackage, esta classe fica de
 * fora (nao e o main-class do modulo).
 */
public class TesteMonkeySemiGuiado {

    public static void main(String[] args) throws Exception {
        long duracaoMs = (args.length > 0 ? Long.parseLong(args[0]) : 300) * 1000L;
        long seed = args.length > 1 ? Long.parseLong(args[1]) : System.currentTimeMillis();
        final Random random = new Random(seed);

        File diretorioLogs = new File(new File(System.getProperty("user.home"), "Gerard"), "logs");
        diretorioLogs.mkdirs();
        File arquivoLog = new File(diretorioLogs,
                "monkey_test_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".log");
        final PrintWriter log = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoLog), "UTF-8")), true);
        log.println("Teste monkey semi-guiado — seed=" + seed + " duracaoMs=" + duracaoMs);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable ex) {
                log.println("[" + new Date() + "] EXCECAO NAO TRATADA na thread " + t.getName() + ":");
                ex.printStackTrace(log);
            }
        });

        final Main[] janelaRef = new Main[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                janelaRef[0] = new Main();
                janelaRef[0].setVisible(true);
                janelaRef[0].toFront();
            }
        });
        Thread.sleep(1500);

        Main.TelaGerard tela = encontrarTelaGerard(janelaRef[0]);
        if (tela == null) {
            log.println("Nao foi possivel localizar a TelaGerard dentro da janela. Abortando.");
            log.close();
            return;
        }

        System.out.println("Teste iniciado (seed=" + seed + "). NAO mexa no mouse/teclado durante o teste.");
        System.out.println("Log: " + arquivoLog.getAbsolutePath());

        Robot robot = new Robot();
        robot.setAutoDelay(12);

        garantirCategoriaSelecionada(robot, tela, random, log);

        long fim = System.currentTimeMillis() + duracaoMs;
        int iteracao = 0;
        int erros = 0;
        while (System.currentTimeMillis() < fim) {
            iteracao++;
            try {
                executarIteracao(robot, tela, random, log, iteracao);
                // Digitar um valor aleatorio na incognita agora pode abrir a
                // pergunta de confirmacao "Tem certeza que esse e o valor
                // do X?" (e, se ENTER aceitar, a dica "Escolha soma ou
                // subtracao..." em seguida) — sem isto, o robot ficava preso
                // atras de um dialogo modal que o script nao sabia fechar.
                varrerDialogosAbertos(robot, log);
            } catch (Exception ex) {
                erros++;
                log.println("[" + new Date() + "] Falha na iteracao " + iteracao + ":");
                ex.printStackTrace(log);
            }
            Thread.sleep(200 + random.nextInt(400));
        }

        log.println("Fim: " + iteracao + " iteracoes, " + erros + " erros capturados.");
        log.close();
        System.out.println("Teste concluido: " + iteracao + " iteracoes, " + erros + " erros. Log em " + arquivoLog.getAbsolutePath());
    }

    private static Main.TelaGerard encontrarTelaGerard(final Main janela) throws Exception {
        final Main.TelaGerard[] resultado = new Main.TelaGerard[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                for (Component c : janela.getContentPane().getComponents()) {
                    if (c instanceof JTabbedPane) {
                        JTabbedPane abas = (JTabbedPane) c;
                        Component primeira = abas.getComponentAt(0);
                        if (primeira instanceof Main.TelaGerard) {
                            resultado[0] = (Main.TelaGerard) primeira;
                        }
                    }
                }
            }
        });
        return resultado[0];
    }

    /**
     * botaoSortear so randomiza a situacao *dentro* de uma categoria ja
     * escolhida (ver Main.iniciarNovaAtividade: sem categoriaSelecionadaPara
     * Atividade, so mostra a tela de instrucao). Sem este passo, o teste
     * fica preso clicando Sortear sem nenhum efeito (foi o que aconteceu na
     * primeira rodada de fumaca). As tres categorias reais (Composicao,
     * Transformacao, Comparacao de medidas) ficam dentro do submenu
     * "Medidas" do botao Tipo — os outros dois grupos do menu (Transformacoes,
     * Relacoes) sao placeholders desabilitados ("em construcao").
     */
    private static void garantirCategoriaSelecionada(Robot robot, final Main.TelaGerard tela,
            Random random, PrintWriter log) throws Exception {
        final boolean[] jaSelecionada = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                jaSelecionada[0] = tela.categoriaSelecionadaParaAtividade;
            }
        });
        if (jaSelecionada[0]) {
            return;
        }

        log.println("Categoria ainda nao selecionada — abrindo menu Tipo.");
        clicarComponente(robot, tela.botaoTipo);
        Thread.sleep(300);

        final Component[] grupoMedidas = new Component[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                if (tela.menuTipo != null && tela.menuTipo.getComponentCount() > 0) {
                    grupoMedidas[0] = tela.menuTipo.getComponent(0);
                }
            }
        });
        if (grupoMedidas[0] == null) {
            log.println("Grupo 'Medidas' nao encontrado dentro de menuTipo — abortando selecao de categoria.");
            return;
        }
        clicarComponente(robot, grupoMedidas[0]);
        Thread.sleep(300);

        final List<Component> opcoes = new ArrayList<Component>();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                if (tela.submenuTipoMedidas != null) {
                    for (Component c : tela.submenuTipoMedidas.getComponents()) {
                        if (c.isEnabled()) {
                            opcoes.add(c);
                        }
                    }
                }
            }
        });
        if (opcoes.isEmpty()) {
            log.println("submenuTipoMedidas sem opcoes habilitadas — abortando selecao de categoria.");
            return;
        }
        Component escolhida = opcoes.get(random.nextInt(opcoes.size()));
        log.println("Selecionando categoria inicial: " + escolhida.getName());
        clicarComponente(robot, escolhida);
        Thread.sleep(800);
    }

    /**
     * Fecha, com ENTER, qualquer java.awt.Dialog visivel deixado por uma
     * iteracao (ex.: a pergunta de confirmacao do valor da incognita e a
     * dica que pode aparecer em seguida). ENTER aceita a opcao padrao —
     * "Sim" na pergunta de confirmacao, "OK" na dica — o suficiente para o
     * teste seguir em frente em vez de travar atras do modal. Limite de
     * tentativas evita loop infinito se algum dialogo nao fechar com ENTER.
     */
    private static void varrerDialogosAbertos(Robot robot, PrintWriter log) throws Exception {
        for (int tentativa = 0; tentativa < 3; tentativa++) {
            Thread.sleep(200);
            if (!existeDialogoVisivel()) {
                return;
            }
            log.println("  dialogo modal detectado apos a iteracao — enviando ENTER (tentativa "
                    + (tentativa + 1) + ")");
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }
    }

    private static boolean existeDialogoVisivel() throws Exception {
        final boolean[] resultado = new boolean[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                for (Window w : Window.getWindows()) {
                    if (w.isVisible() && w instanceof java.awt.Dialog) {
                        resultado[0] = true;
                        return;
                    }
                }
            }
        });
        return resultado[0];
    }

    private static Point centroNaTela(final Component c) throws Exception {
        final Point[] resultado = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                if (c != null && c.isShowing()) {
                    Point p = c.getLocationOnScreen();
                    resultado[0] = new Point(p.x + c.getWidth() / 2, p.y + c.getHeight() / 2);
                }
            }
        });
        return resultado[0];
    }

    private static void clicarComponente(Robot robot, final Component c) throws Exception {
        Point centro = centroNaTela(c);
        if (centro == null) {
            throw new IllegalStateException("Componente nao esta visivel para clique: " + c);
        }
        robot.mouseMove(centro.x, centro.y);
        robot.delay(60);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static void executarIteracao(Robot robot, final Main.TelaGerard tela, Random random,
            PrintWriter log, int iteracao) throws Exception {
        final List<ItemTextoArrastavel> itens = new ArrayList<ItemTextoArrastavel>();
        final List<ElementoTextoMovel> textos = new ArrayList<ElementoTextoMovel>();
        final List<ElementoVergnaud> elementos = new ArrayList<ElementoVergnaud>();
        final Point[] origemTela = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                itens.addAll(tela.itensArrastaveis);
                textos.addAll(tela.elementosTexto);
                elementos.addAll(tela.elementosVergnaud);
                origemTela[0] = tela.isShowing() ? tela.getLocationOnScreen() : null;
            }
        });

        if (origemTela[0] == null) {
            log.println("[iter " + iteracao + "] TelaGerard nao esta visivel (aba trocada?) — pulando.");
            return;
        }

        // So os elementos com vinculo semantico (numero ou incognita) podem
        // ser enviados ao diagrama - ver Main.ehNumeroOuInterrogacaoDoTexto,
        // que e privado mas cuja logica e so isto (elemento.possuiVinculoSemantico()
        // e nao estarmos numa mensagem de sistema em vez de um problema real).
        List<ElementoTextoMovel> candidatosArrastar = new ArrayList<ElementoTextoMovel>();
        for (ElementoTextoMovel texto : textos) {
            if (texto.possuiVinculoSemantico()) {
                candidatosArrastar.add(texto);
            }
        }

        List<ItemTextoArrastavel> editaveisNoDiagrama = new ArrayList<ItemTextoArrastavel>();
        for (ItemTextoArrastavel item : itens) {
            if (item.estaNoDiagrama() && item.editavel) {
                editaveisNoDiagrama.add(item);
            }
        }

        double sorteio = random.nextDouble();

        if (!candidatosArrastar.isEmpty() && sorteio < 0.55) {
            ElementoTextoMovel texto = candidatosArrastar.get(random.nextInt(candidatosArrastar.size()));
            // ElementoTextoMovel.contem() trata (x,y) como a linha de base do
            // texto: valido em [y-altura, y+6] verticalmente. O centro dessa
            // faixa fica dentro da margem com folga.
            Point origemItem = new Point(
                    origemTela[0].x + texto.x + texto.largura / 2,
                    origemTela[0].y + texto.y - texto.altura / 2);
            Point destino;
            if (!elementos.isEmpty()) {
                ElementoVergnaud alvo = elementos.get(random.nextInt(elementos.size()));
                destino = new Point(
                        origemTela[0].x + alvo.x + alvo.largura / 2,
                        origemTela[0].y + alvo.y + alvo.altura / 2);
            } else {
                destino = new Point(origemTela[0].x + 400 + random.nextInt(400),
                        origemTela[0].y + 300 + random.nextInt(300));
            }
            log.println("[iter " + iteracao + "] arrastar valor=" + texto.valor
                    + " papel=" + texto.chavePapelSemantico + " de " + origemItem + " para " + destino);
            arrastar(robot, origemItem, destino);
            return;
        }

        if (!editaveisNoDiagrama.isEmpty() && sorteio < 0.85) {
            ItemTextoArrastavel item = editaveisNoDiagrama.get(random.nextInt(editaveisNoDiagrama.size()));
            Point centro = new Point(
                    origemTela[0].x + item.x + item.largura / 2,
                    origemTela[0].y + item.y + item.altura / 2);
            int valorDigitado = random.nextInt(100);
            log.println("[iter " + iteracao + "] digitar valor=" + valorDigitado
                    + " na incognita papel=" + item.chavePapel + " em " + centro);
            digitarNaIncognita(robot, centro, valorDigitado);
            return;
        }

        Point botaoSortearNaTela = localizarBotaoSortear(tela);
        if (botaoSortearNaTela != null) {
            log.println("[iter " + iteracao + "] clicar Sortear (novo problema)");
            robot.mouseMove(botaoSortearNaTela.x, botaoSortearNaTela.y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    private static Point localizarBotaoSortear(final Main.TelaGerard tela) throws Exception {
        final Point[] resultado = new Point[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                if (tela.botaoSortear != null && tela.botaoSortear.isShowing()) {
                    Point p = tela.botaoSortear.getLocationOnScreen();
                    resultado[0] = new Point(
                            p.x + tela.botaoSortear.getWidth() / 2,
                            p.y + tela.botaoSortear.getHeight() / 2);
                }
            }
        });
        return resultado[0];
    }

    private static void arrastar(Robot robot, Point origem, Point destino) {
        robot.mouseMove(origem.x, origem.y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        int passos = 14;
        for (int i = 1; i <= passos; i++) {
            int x = origem.x + (destino.x - origem.x) * i / passos;
            int y = origem.y + (destino.y - origem.y) * i / passos;
            robot.mouseMove(x, y);
            robot.delay(10);
        }
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static void digitarNaIncognita(Robot robot, Point centro, int valor) throws InterruptedException {
        robot.mouseMove(centro.x, centro.y);
        clicarDuplo(robot);
        Thread.sleep(350);
        String texto = Integer.toString(valor);
        for (int i = 0; i < texto.length(); i++) {
            int codigo = KeyEvent.VK_0 + (texto.charAt(i) - '0');
            robot.keyPress(codigo);
            robot.keyRelease(codigo);
        }
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(250);
    }

    private static void clicarDuplo(Robot robot) {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(35);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
