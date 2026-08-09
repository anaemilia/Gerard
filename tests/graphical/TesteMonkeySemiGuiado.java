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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import gerard.agente.modelador.OuvinteCasoAgenteModelador;
import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.monitor.OuvinteVeredictoAgenteMonitor;
import gerard.agente.zdp.CamadaEstrategiaZDP;
import gerard.agente.zdp.OuvinteEstrategiaAgenteZDP;
import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Teste de estresse "semi-guiado": em vez de cliques cegos em coordenadas
 * aleatorias da tela (que raramente exercitam o fluxo real do Gerard), sorteia
 * a cada iteracao uma acao entre as que fazem sentido no estado atual da
 * TelaGerard (arrastar um item para um alvo do diagrama, digitar um valor
 * numa incognita editavel, ou pedir uma nova situacao via itemNovaSituacao)
 * e executa via java.awt.Robot/doClick(), entao passa pelos mesmos
 * MouseListener/KeyListener/ActionListener reais que um usuario humano
 * acionaria.
 *
 * Fica no pacote padrao (sem "package") de proposito, igual a Main.java:
 * TelaGerard e seus campos (itensArrastaveis, elementosVergnaud,
 * itemNovaSituacao, menuCategoria...) tem visibilidade de pacote, entao
 * esta classe precisa estar no mesmo pacote para le-los sem reflexao.
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

        File arquivoAgentes = new File(diretorioLogs,
                "monkey_agentes_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".tsv");
        final PrintWriter logAgentes = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivoAgentes), "UTF-8")), true);
        logAgentes.println("seq\ttMs\tagente\tdetalhe");
        GravadorAtividadeAgentes gravador = new GravadorAtividadeAgentes(logAgentes, System.currentTimeMillis());
        tela.agenteMonitor.adicionarOuvinte(gravador);
        tela.agenteZDP.adicionarOuvinte(gravador);
        tela.agenteModelador.adicionarOuvinte(gravador);
        System.out.println("Log de atividade dos agentes: " + arquivoAgentes.getAbsolutePath());

        Robot robot = new Robot();
        robot.setAutoDelay(12);

        garantirCategoriaSelecionada(robot, tela, random, log);

        long fim = System.currentTimeMillis() + duracaoMs;
        int iteracao = 0;
        int erros = 0;
        while (System.currentTimeMillis() < fim) {
            iteracao++;
            try {
                talvezTrocarCategoria(robot, tela, random, log, iteracao);
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
        tela.agenteMonitor.removerOuvinte(gravador);
        tela.agenteZDP.removerOuvinte(gravador);
        tela.agenteModelador.removerOuvinte(gravador);
        logAgentes.close();
        System.out.println("Teste concluido: " + iteracao + " iteracoes, " + erros + " erros. Log em " + arquivoLog.getAbsolutePath());
        System.out.println("Eventos de agentes gravados: " + gravador.totalEventos() + " em " + arquivoAgentes.getAbsolutePath());
    }

    /**
     * Grava, em ordem cronologica, cada acao percebida pelos tres agentes de
     * Ajuda Adaptativa durante o teste monkey — mesmas tres interfaces de
     * observador ja usadas por PainelAtividadeAgentes (ver
     * gerard-ajuda-adaptativa), so que aqui persistidas em TSV para analise
     * posterior (ex.: grafo de transicoes entre estados dos agentes).
     */
    private static final class GravadorAtividadeAgentes
            implements OuvinteVeredictoAgenteMonitor, OuvinteEstrategiaAgenteZDP, OuvinteCasoAgenteModelador {
        private final PrintWriter destino;
        private final long inicioMs;
        private int sequencia;

        GravadorAtividadeAgentes(PrintWriter destino, long inicioMs) {
            this.destino = destino;
            this.inicioMs = inicioMs;
        }

        int totalEventos() {
            return sequencia;
        }

        @Override
        public void aoAvaliar(boolean correto) {
            registrar("Monitor", correto ? "CORRETO" : "ERRADO");
        }

        @Override
        public void aoDecidir(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo,
                               boolean correto, CamadaEstrategiaZDP estrategia) {
            registrar("ZDP", String.valueOf(estrategia));
        }

        @Override
        public void aoArmazenar(String idUsuario, DiagnosticoTarefa diagnostico) {
            registrar("Modelador", String.valueOf(diagnostico.getSuporte()));
        }

        private void registrar(String agente, String detalhe) {
            sequencia++;
            destino.println(sequencia + "\t" + (System.currentTimeMillis() - inicioMs) + "\t" + agente + "\t" + detalhe);
        }
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
     * "Nova situacao-problema" (item de menu, Arquivo > Nova situacao-
     * problema) so randomiza a situacao *dentro* de uma categoria ja
     * escolhida (ver Main.iniciarNovaAtividade: sem categoriaSelecionadaPara
     * Atividade, so mostra a tela de instrucao). Sem este passo, o teste
     * fica preso sem nenhum efeito (foi o que aconteceu na primeira rodada
     * de fumaca). Desde a migracao para JMenuBar (2026-07-28), a categoria
     * e selecionada via tela.menuCategoria (JMenu real, Arquivo > Categoria)
     * chamando doClick() diretamente nos itens — mais robusto que dirigir o
     * Robot por cima de um menu nativo do SO, que e sensivel a timing/tema.
     * Ate 2026-08-07 restringia as opcoes ao grupo "Medidas" (indice 0). A
     * partir desta data usa todasOpcoesDeCategoriaHabilitadas — cobre todos
     * os grupos (Medidas e Relacoes; os dois itens "Em construcao" do grupo
     * Transformacoes ficam de fora naturalmente, por estarem desabilitados)
     * a pedido da usuaria, para que o teste monkey exercite Relacoes tambem,
     * nao so a categoria sorteada uma unica vez no inicio.
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

        log.println("Categoria ainda nao selecionada — selecionando via menuCategoria (JMenu).");

        List<JMenuItem> opcoes = todasOpcoesDeCategoriaHabilitadas(tela);
        if (opcoes.isEmpty()) {
            log.println("Nenhuma opcao habilitada em menuCategoria — abortando selecao de categoria.");
            return;
        }
        final JMenuItem opcaoEscolhida = opcoes.get(random.nextInt(opcoes.size()));
        final String textoEscolhido = opcaoEscolhida.getText();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                opcaoEscolhida.doClick();
            }
        });
        log.println("Categoria inicial selecionada: " + textoEscolhido);
        Thread.sleep(800);
    }

    /**
     * Todos os JMenuItem folha, de todos os subgrupos de tela.menuCategoria
     * (Medidas, Transformacoes, Relacoes...), que estao habilitados agora.
     * Generico de proposito — nao assume quais grupos existem nem quantos,
     * so que cada grupo e um JMenu com itens folha dentro. Usado tanto para
     * a selecao inicial de categoria (garantirCategoriaSelecionada) quanto
     * para trocas de categoria no meio do teste (talvezTrocarCategoria) —
     * um unico lugar define "categoria valida para o teste", em vez de
     * duplicar o filtro em dois pontos.
     */
    private static List<JMenuItem> todasOpcoesDeCategoriaHabilitadas(final Main.TelaGerard tela) throws Exception {
        final List<JMenuItem> opcoes = new ArrayList<JMenuItem>();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                if (tela.menuCategoria == null) {
                    return;
                }
                for (int g = 0; g < tela.menuCategoria.getItemCount(); g++) {
                    JMenuItem grupo = tela.menuCategoria.getItem(g);
                    if (!(grupo instanceof JMenu)) {
                        continue;
                    }
                    JMenu subMenu = (JMenu) grupo;
                    for (int i = 0; i < subMenu.getItemCount(); i++) {
                        JMenuItem item = subMenu.getItem(i);
                        if (item != null && item.isEnabled()) {
                            opcoes.add(item);
                        }
                    }
                }
            }
        });
        return opcoes;
    }

    /**
     * Com baixa probabilidade a cada iteracao, troca a categoria atual por
     * outra sorteada entre TODAS as opcoes habilitadas (nao so a categoria
     * escolhida no inicio) — sem isto, uma rodada cuja selecao inicial caisse
     * em "Medidas" nunca exercitaria "Relacoes" (e vice-versa) durante todo o
     * teste, deixando a cobertura incompleta mesmo com Relacoes habilitada.
     * Troca via doClick() no JMenuItem real, o mesmo caminho que
     * selecionarCategoria usa para um clique humano — cancela qualquer
     * adivinhacao pendente e comeca uma nova atividade na categoria
     * escolhida.
     */
    private static void talvezTrocarCategoria(Robot robot, final Main.TelaGerard tela, Random random,
            PrintWriter log, int iteracao) throws Exception {
        if (random.nextDouble() >= 0.08) {
            return;
        }
        List<JMenuItem> opcoes = todasOpcoesDeCategoriaHabilitadas(tela);
        if (opcoes.isEmpty()) {
            return;
        }
        final JMenuItem escolhida = opcoes.get(random.nextInt(opcoes.size()));
        final String texto = escolhida.getText();
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                escolhida.doClick();
            }
        });
        log.println("[iter " + iteracao + "] trocar categoria -> " + texto);
        Thread.sleep(500);
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

        // "Nova situacao-problema" agora e um JMenuItem dentro da JMenuBar
        // (Arquivo > Nova situacao-problema), nao mais um botao sempre
        // visivel na tela — doClick() dispara a mesma acao sem precisar
        // abrir o menu visualmente via Robot.
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                if (tela.itemNovaSituacao != null && tela.itemNovaSituacao.isEnabled()) {
                    log.println("[iter " + iteracao + "] clicar Nova situacao-problema (menu Arquivo)");
                    tela.itemNovaSituacao.doClick();
                }
            }
        });
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
