package gerard.pesquisador;

import gerard.i18n.ServicoLocalizacao;
import gerard.pesquisador.log.EstatisticasLogGerard;
import gerard.pesquisador.log.EventoLogGerard;
import gerard.pesquisador.log.ExportadorLogGerard;
import gerard.pesquisador.log.LoggerInteracaoGerard;
import gerard.pesquisador.visualizacao.PainelD3WebView;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visão de pesquisador baseada em dados gerados em tempo de uso.
 *
 * O modelo de análise segue os quadros da pesquisa: agente da ação, problema,
 * tentativa, tarefa, C/E, instrumento, função do artefato e regras. Os dados
 * exibidos aqui são lidos dos arquivos TSV produzidos durante a interação com
 * o Gerard, e não de tabelas fixas do estudo.
 */
public class TelaVisaoPesquisador extends JDialog {

    private static final Color COR_FUNDO = new Color(246, 247, 248);
    private static final Color COR_SUPERFICIE = Color.WHITE;
    private static final Color COR_TEXTO = new Color(31, 41, 51);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(82, 97, 107);
    private static final Color COR_BORDA = new Color(213, 218, 224);
    private static final Color COR_PRIMARIA = new Color(37, 99, 235);
    private static final Color COR_DESTAQUE = new Color(234, 244, 255);
    private static final Color COR_ERRO = new Color(224, 87, 87);

    private final List<EventoLogGerard> eventos;
    private final EstatisticasLogGerard estatisticas;
    private final ServicoLocalizacao i18n;

    public TelaVisaoPesquisador(Window parent) {
        super(parent, ServicoLocalizacao.getInstancia().texto("pesq.title"), ModalityType.MODELESS);
        this.i18n = ServicoLocalizacao.getInstancia();
        this.eventos = LoggerInteracaoGerard.getInstancia().carregarEventos();
        this.estatisticas = new EstatisticasLogGerard(eventos);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(parent);
        setContentPane(criarConteudo());
    }

    public static void mostrar(Window parent) {
        TelaVisaoPesquisador tela = new TelaVisaoPesquisador(parent);
        tela.setVisible(true);
    }

    private String t(String chave) {
        return i18n.texto(chave);
    }

    private String f(String chave, Object... argumentos) {
        return i18n.formatar(chave, argumentos);
    }

    private JPanel criarConteudo() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel cabecalho = new JPanel(new BorderLayout(8, 2));
        cabecalho.setOpaque(false);
        JLabel titulo = new JLabel(t("pesq.title"));
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(COR_TEXTO);
        JLabel subtitulo = new JLabel(t("pesq.subtitle"));
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(COR_TEXTO_SECUNDARIO);
        cabecalho.add(titulo, BorderLayout.NORTH);
        cabecalho.add(subtitulo, BorderLayout.CENTER);

        JButton fechar = criarBotao(t("pesq.button.close"));
        fechar.addActionListener(e -> dispose());
        cabecalho.add(fechar, BorderLayout.EAST);
        raiz.add(cabecalho, BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Arial", Font.BOLD, 12));
        abas.addTab(t("pesq.tab.summary"), criarAbaSintese());
        abas.addTab(t("pesq.tab.table4"), criarAbaTabela4ProblemasEstudo());
        abas.addTab(t("pesq.tab.table6"), criarAbaTabela6UsuariosEstudo());
        abas.addTab(t("pesq.tab.table7"), criarAbaTabela7ObjetosEstudo());
        abas.addTab(t("pesq.tab.table13"), criarAbaTabela13AcoesNeutrasEstudo());
        abas.addTab(t("pesq.tab.table14"), criarAbaTabela14AcoesPorProblemaEstudo());
        abas.addTab(t("pesq.tab.table15"), criarAbaTabela15GruposQualitativosEstudo());
        abas.addTab(t("pesq.tab.visualizations"), criarAbaVisualizacoesTabela14());

        raiz.add(abas, BorderLayout.CENTER);
        return raiz;
    }

    private JPanel criarAbaSintese() {
        JPanel painel = painelBase(new BorderLayout(12, 12));

        JPanel cards = new JPanel();
        cards.setOpaque(false);
        cards.setLayout(new BoxLayout(cards, BoxLayout.X_AXIS));
        cards.add(criarCard(t("pesq.card.events"), String.valueOf(estatisticas.totalEventos()), t("pesq.card.logLines")));
        cards.add(Box.createHorizontalStrut(10));
        cards.add(criarCard(t("pesq.card.subjectActions"), String.valueOf(estatisticas.totalPorAgente("S")), t("pesq.card.agentS")));
        cards.add(Box.createHorizontalStrut(10));
        cards.add(criarCard(t("pesq.card.interfaceActions"), String.valueOf(estatisticas.totalPorAgente("C")), t("pesq.card.agentC")));
        cards.add(Box.createHorizontalStrut(10));
        cards.add(criarCard(t("pesq.card.errors"), String.valueOf(estatisticas.totalErros()), t("pesq.card.ceE")));
        cards.add(Box.createHorizontalStrut(10));
        cards.add(criarCard(t("pesq.card.problems"), String.valueOf(estatisticas.totalProblemas()), t("pesq.card.sessionProblem")));

        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.setOpaque(false);
        centro.add(new GraficoBarras(t("pesq.chart.errorsByCategory"), t("pesq.chart.errorsByCategory.subtitle"), estatisticas.contarErrosPorCategoria(), COR_ERRO), BorderLayout.CENTER);

        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font("Arial", Font.PLAIN, 13));
        texto.setForeground(COR_TEXTO);
        texto.setBackground(COR_SUPERFICIE);
        texto.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        texto.setText(f("pesq.summary.text", ExportadorLogGerard.arquivoSessaoAtual().getAbsolutePath()));

        painel.add(cards, BorderLayout.NORTH);
        painel.add(centro, BorderLayout.CENTER);
        painel.add(texto, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarAbaLogBruto() {
        return criarAbaTabela("Log bruto de interação", dadosLogBruto(), new int[]{135, 110, 95, 55, 80, 70, 260, 45, 190, 180, 250, 120, 330, 160, 360, 140, 260});
    }

    private JPanel criarAbaAcertosErros() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTabelaComTitulo(t("pesq.table.correctErrorsByCategory"), dadosAcertosErrosPorCategoria(), new int[]{260, 110, 110, 110}), BorderLayout.NORTH);
        painel.add(new GraficoBarras(t("pesq.chart.errorsByCategory"), t("pesq.chart.errorsByCategory.subtitle2"), estatisticas.contarErrosPorCategoria(), COR_ERRO), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaCategorias() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTabelaComTitulo("Eventos por categoria", dadosContagem(estatisticas.contarPorCategoria(), "Categoria", "Eventos"), new int[]{360, 120}), BorderLayout.NORTH);
        painel.add(new GraficoBarras("Eventos por categoria", "Distribuição das ações registradas", estatisticas.contarPorCategoria(), COR_PRIMARIA), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTarefas() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTabelaComTitulo("Eventos por tarefa", dadosContagem(estatisticas.contarPorTarefa(), "Tarefa", "Eventos"), new int[]{520, 120}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaInstrumentos() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTabelaComTitulo(t("pesq.table.eventsByArtifact"), dadosContagem(estatisticas.contarPorInstrumentoArtefato(), t("pesq.col.artifact"), t("pesq.col.events")), new int[]{420, 120}), BorderLayout.NORTH);
        painel.add(new GraficoBarras(t("pesq.chart.artifacts"), t("pesq.chart.artifacts.subtitle"), estatisticas.contarPorInstrumentoArtefato(), COR_PRIMARIA), BorderLayout.CENTER);
        return painel;
    }


    private JPanel criarAbaTabela6Usuarios() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                "Tabela 6 - Quantidade de situações-problema analisadas e ações por usuário",
                "Adaptação da Tabela 6 do estudo. Os valores abaixo são calculados a partir dos arquivos de log: quantidade de problemas/sessões por usuário, ações do sujeito, ações da interface, acertos e erros."), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo("Quantidade de situações-problema e ações por usuário", dadosTabela6Usuarios(), new int[]{180, 180, 120, 130, 120, 100, 100}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela4Problemas() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                "Tabela 4 - Identificação dos problemas utilizados",
                "Adaptação da Tabela 4 do estudo. A lista é reconstruída a partir dos campos problema, categoria e enunciado presentes no log."), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo("Problemas presentes no log", dadosTabela4Problemas(), new int[]{150, 220, 620, 90, 90, 90, 90}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela5Formato() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                "Tabela 5 - Formato de codificação das ações instrumentais",
                "Adaptação da Tabela 5 do estudo. Cada linha agrupa uma tarefa registrada no log e mostra os instrumentos, funções, objetos e avaliação predominantes."), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo("Formato de codificação reconstruído a partir do log", dadosTabela5Formato(), new int[]{90, 320, 90, 360, 260, 360, 170, 100}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela7Objetos() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                "Tabela 7 - Objetos presentes no contexto da ação",
                "Adaptação da Tabela 7 do estudo. A coluna de eventos indica quantas vezes cada objeto aparece nos logs. Os objetos não identificados formalmente também são mantidos para inspeção."), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo("Objetos da ação no log", dadosTabela7Objetos(), new int[]{220, 520, 120}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela9Acoes() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                "Tabela 9 - Codificação das ações instrumentais",
                "Adaptação da Tabela 9 do estudo. Esta tabela mantém a granularidade de linha do log e reorganiza as colunas no formato da análise instrumental."), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo("Codificação das ações instrumentais", dadosTabela9AcoesInstrumentais(), new int[]{105, 75, 95, 60, 300, 330, 230, 360, 180, 420, 140, 180}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaGraficos() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        JTabbedPane graficos = new JTabbedPane();
        JPanel aba1 = painelBase(new BorderLayout(12, 12));
        aba1.add(criarTabelaComTitulo(t("pesq.table.correctErrorsByCategory"), dadosAcertosErrosPorCategoria(), new int[]{260, 110, 110, 110}), BorderLayout.NORTH);
        aba1.add(new GraficoBarras(t("pesq.chart.errorsByCategory"), t("pesq.chart.errorsByCategory.subtitle2"), estatisticas.contarErrosPorCategoria(), COR_ERRO), BorderLayout.CENTER);
        graficos.addTab(t("pesq.tab.correctErrors"), aba1);
        painel.add(graficos, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarTextoModeloTabela(String titulo, String texto) {
        JPanel painel = new JPanel(new BorderLayout(0, 4));
        painel.setBackground(COR_SUPERFICIE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 13));
        labelTitulo.setForeground(COR_TEXTO);
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 12));
        area.setForeground(COR_TEXTO_SECUNDARIO);
        area.setBackground(COR_SUPERFICIE);
        painel.add(labelTitulo, BorderLayout.NORTH);
        painel.add(area, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaExportacao() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        texto.setForeground(COR_TEXTO);
        texto.setBackground(COR_SUPERFICIE);
        texto.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        texto.setText(
                "Diretório de logs:\n" + ExportadorLogGerard.diretorioLogs().getAbsolutePath() +
                "\n\nArquivo da sessão atual:\n" + ExportadorLogGerard.arquivoSessaoAtual().getAbsolutePath() +
                "\n\nFormato TSV:\n" + EventoLogGerard.cabecalhoTsv() +
                "\n\nOs arquivos podem ser abertos em planilhas ou analisados posteriormente em Python/R/Java."
        );

        JPanel botoes = new JPanel();
        botoes.setOpaque(false);
        JButton abrirPasta = criarBotao("Abrir pasta de logs");
        abrirPasta.addActionListener(e -> abrirDiretorioLogs());
        botoes.add(abrirPasta);

        painel.add(texto, BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private void abrirDiretorioLogs() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(ExportadorLogGerard.diretorioLogs());
            }
         } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, f("pesq.error.openFolder", ex.getMessage()));
        }
    }


    private Object[][] dadosTabela6Usuarios() {
        Map<String, int[]> contagens = new LinkedHashMap<String, int[]>();
        Map<String, Set<String>> problemas = new LinkedHashMap<String, Set<String>>();
        for (EventoLogGerard e : eventos) {
            String usuario = normalizar(e.getUsuario());
            if (!contagens.containsKey(usuario)) {
                contagens.put(usuario, new int[]{0, 0, 0, 0, 0});
                problemas.put(usuario, new LinkedHashSet<String>());
            }
            int[] c = contagens.get(usuario);
            c[0]++;
            if ("S".equals(e.getAgenteDaAcao())) { c[1]++; }
            if ("C".equals(e.getAgenteDaAcao())) { c[2]++; }
            if ("C".equals(e.getCe())) { c[3]++; }
            if ("E".equals(e.getCe())) { c[4]++; }
            String problema = normalizar(e.getProblema());
            if (!t("pesq.value.notInformed").equals(problema)) {
                problemas.get(usuario).add(normalizar(e.getSessao()) + "::" + problema);
            }
        }
        Object[][] dados = new Object[contagens.size() + 1][7];
        dados[0] = new Object[]{t("pesq.col.user"), t("pesq.col.problemSituations"), t("pesq.col.subjectActions"), t("pesq.col.interfaceActions"), t("pesq.col.totalActions"), t("pesq.col.correct"), t("pesq.col.errors")};
        int i = 1;
        for (String usuario : contagens.keySet()) {
            int[] c = contagens.get(usuario);
            dados[i++] = new Object[]{usuario, Integer.valueOf(problemas.get(usuario).size()), Integer.valueOf(c[1]), Integer.valueOf(c[2]), Integer.valueOf(c[0]), Integer.valueOf(c[3]), Integer.valueOf(c[4])};
        }
        return dados;
    }

    private Object[][] dadosTabela4Problemas() {
        Map<String, String[]> base = new LinkedHashMap<String, String[]>();
        Map<String, int[]> contagens = new LinkedHashMap<String, int[]>();
        for (EventoLogGerard e : eventos) {
            String problema = normalizar(e.getProblema());
            String categoria = normalizar(e.getCategoria());
            String chave = problema + "::" + categoria;
            if (!base.containsKey(chave)) {
                base.put(chave, new String[]{problema, categoria, ""});
                contagens.put(chave, new int[]{0, 0, 0, 0});
            }
            String enunciado = e.getEnunciado() == null ? "" : e.getEnunciado().trim();
            if (enunciado.length() > 0 && base.get(chave)[2].length() == 0) {
                base.get(chave)[2] = enunciado;
            }
            int[] c = contagens.get(chave);
            c[0]++;
            if ("S".equals(e.getAgenteDaAcao())) { c[1]++; }
            if ("C".equals(e.getAgenteDaAcao())) { c[2]++; }
            if ("E".equals(e.getCe())) { c[3]++; }
        }
        Object[][] dados = new Object[base.size() + 1][7];
        dados[0] = new Object[]{t("pesq.col.problemLabel"), t("pesq.col.typeCategory"), t("pesq.col.statement"), t("pesq.col.events"), t("pesq.col.actionsS"), t("pesq.col.actionsC"), t("pesq.col.errors")};
        int i = 1;
        for (String chave : base.keySet()) {
            String[] b = base.get(chave);
            int[] c = contagens.get(chave);
            dados[i++] = new Object[]{b[0], b[1], b[2].length() == 0 ? t("pesq.value.notInformedInLog") : b[2], Integer.valueOf(c[0]), Integer.valueOf(c[1]), Integer.valueOf(c[2]), Integer.valueOf(c[3])};
        }
        return dados;
    }

    private Object[][] dadosTabela5Formato() {
        Map<String, String[]> base = new LinkedHashMap<String, String[]>();
        Map<String, int[]> contagens = new LinkedHashMap<String, int[]>();
        for (EventoLogGerard e : eventos) {
            String tarefa = normalizar(e.getTarefa());
            if (!base.containsKey(tarefa)) {
                base.put(tarefa, new String[]{"", "", "", "", ""});
                contagens.put(tarefa, new int[]{0, 0, 0, 0});
            }
            String[] b = base.get(tarefa);
            if (b[0].length() == 0) { b[0] = normalizar(e.getInstrumentoOrganizacao()); }
            if (b[1].length() == 0) { b[1] = normalizar(e.getInstrumentoArtefato()); }
            if (b[2].length() == 0) { b[2] = normalizar(e.getFuncaoDoArtefato()); }
            if (b[3].length() == 0) { b[3] = normalizar(e.getObjeto()); }
            int[] c = contagens.get(tarefa);
            c[0]++;
            if ("C".equals(e.getCe())) { c[1]++; }
            if ("E".equals(e.getCe())) { c[2]++; }
            if ("-".equals(e.getCe())) { c[3]++; }
        }
        Object[][] dados = new Object[base.size() + 1][8];
        dados[0] = new Object[]{"Etapa-modelo", "Tarefa", "C/E predominante", "Instrumento - organização", "Instrumento - artefato", "Função do artefato", "Objeto", "Eventos"};
        int i = 1;
        for (String tarefa : base.keySet()) {
            int[] c = contagens.get(tarefa);
            String ce = c[1] >= c[2] && c[1] >= c[3] ? "C" : (c[2] >= c[1] && c[2] >= c[3] ? "E" : "-");
            String[] b = base.get(tarefa);
            dados[i] = new Object[]{Integer.valueOf(i), tarefa, ce, b[0], b[1], b[2], b[3], Integer.valueOf(c[0])};
            i++;
        }
        return dados;
    }

    private Object[][] dadosTabela7Objetos() {
        Map<String, Integer> contagens = new LinkedHashMap<String, Integer>();
        for (EventoLogGerard e : eventos) {
            incrementar(contagens, normalizar(e.getObjeto()));
        }
        String[][] descricoes = new String[][]{
                {"OBJ1", "Cardinal do estado inicial"},
                {"OBJ2", "Cardinal do estado final"},
                {"OBJ3", "Cardinal do referente"},
                {"OBJ4", "Cardinal do número relativo"},
                {"OBJ5", "Cardinal do referido"},
                {"OBJ6", "Cardinal do todo"},
                {"OBJ7", "Cardinal da parte"},
                {"OBJ8", "O problema"}
        };
        List<Object[]> linhas = new ArrayList<Object[]>();
        for (String[] d : descricoes) {
            linhas.add(new Object[]{d[0], d[1], Integer.valueOf(valor(contagens.get(d[0])))});
        }
        for (Map.Entry<String, Integer> entrada : contagens.entrySet()) {
            String objeto = entrada.getKey();
            if (objeto.startsWith("OBJ")) { continue; }
            linhas.add(new Object[]{objeto, "Objeto registrado no log", entrada.getValue()});
        }
        Object[][] dados = new Object[linhas.size() + 1][3];
        dados[0] = new Object[]{t("pesq.col.identification"), t("pesq.col.description"), t("pesq.col.events")};
        for (int i = 0; i < linhas.size(); i++) {
            dados[i + 1] = linhas.get(i);
        }
        return dados;
    }

    private Object[][] dadosTabela9AcoesInstrumentais() {
        Object[][] dados = new Object[eventos.size() + 1][12];
        dados[0] = new Object[]{"Etapa/Tentativa", "Agente", "Problema", "C/E", "Tarefa", "Instrumento - organização", "Instrumento - artefato", "Função do artefato", "Objeto", "Regras", "Usuário", "Origem"};
        for (int i = 0; i < eventos.size(); i++) {
            EventoLogGerard e = eventos.get(i);
            dados[i + 1] = new Object[]{
                    e.getTentativa(), e.getAgenteDaAcao(), e.getProblema(), e.getCe(), e.getTarefa(),
                    e.getInstrumentoOrganizacao(), e.getInstrumentoArtefato(), e.getFuncaoDoArtefato(),
                    e.getObjeto(), e.getRegras(), e.getUsuario(), e.getOrigemEvento()
            };
        }
        return dados;
    }

    private JPanel criarAbaTabela4ProblemasEstudo() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                t("pesq.t4.title"),
                t("pesq.t4.desc")), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo(t("pesq.t4.tableTitle"), dadosTabela4ProblemasEstudo(), new int[]{150, 180, 760}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela5FormatoEstudo() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                "Tabela 5 - Formato de codificação das ações instrumentais",
                "Formato do estudo: etapa, tarefa, C/E, instrumento, função do artefato e objeto. As linhas abaixo sintetizam as ações registradas no log, sem listar ainda a transcrição completa."), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo("Formato de codificação das ações instrumentais", dadosTabela5FormatoEstudo(), new int[]{80, 310, 70, 430, 360, 220}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela6UsuariosEstudo() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                t("pesq.t6.title"),
                t("pesq.t6.desc")), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo(t("pesq.t6.tableTitle"), dadosTabela6UsuariosEstudo(), new int[]{220, 240, 300}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela7ObjetosEstudo() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                t("pesq.t7.title"),
                t("pesq.t7.desc")), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo(t("pesq.t7.tableTitle"), dadosTabela7ObjetosEstudo(), new int[]{150, 620, 160}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela9AcoesEstudo() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                "Tabela 9 - Codificação das ações instrumentais",
                "Formato do estudo: etapa, C/E, tarefa, instrumento, função do artefato, objeto e T.e.A. A tabela é gerada a partir das linhas do log, preservando a granularidade da ação."), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo("Codificação das ações instrumentais", dadosTabela9AcoesEstudo(), new int[]{90, 130, 80, 290, 390, 230, 320, 160, 130, 150}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela13AcoesNeutrasEstudo() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                t("pesq.t13.title"),
                t("pesq.t13.desc")), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo(t("pesq.t13.tableTitle"), dadosTabela13AcoesNeutrasEstudo(), new int[]{260, 260, 420, 520}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela14AcoesPorProblemaEstudo() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                t("pesq.t14.title"),
                t("pesq.t14.desc")), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo(
                t("pesq.t14.tableTitle"),
                dadosTabela14AcoesPorProblemaEstudo(),
                new int[]{160, 130, 130, 150, 90, 120, 100, 120},
                enunciadosPorProblemaAnalitico(),
                0
        ), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaTabela15GruposQualitativosEstudo() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                t("pesq.t15.title"),
                t("pesq.t15.desc")), BorderLayout.NORTH);
        painel.add(criarTabelaComTitulo(t("pesq.t15.tableTitle"), dadosTabela15GruposQualitativosEstudo(), new int[]{260, 560, 520}), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarAbaVisualizacoesTabela14() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                t("pesq.visualizations.title"),
                t("pesq.visualizations.desc")), BorderLayout.NORTH);

        PainelD3WebView webView = new PainelD3WebView(gerarHtmlD3Tabela14(), i18n);
        painel.add(webView, BorderLayout.CENTER);
        return painel;
    }

    private String gerarHtmlD3Tabela14() {
        String d3 = lerRecursoTexto("/gerard/pesquisador/visualizacao/d3.v3.min.js");
        String dadosJson = dadosLogBrutoD3Json();
        String labelsJson = labelsD3Json();
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\">");
        html.append("<style>");
        html.append("html,body{margin:0;padding:0;background:#f6f7f8;color:#1f2933;font-family:Arial,Helvetica,sans-serif;}");
        html.append("body{padding:14px;box-sizing:border-box;}");
        html.append(".intro{background:#fff;border:1px solid #d5dae0;border-radius:10px;padding:14px 16px;margin-bottom:12px;}");
        html.append(".intro h1{font-size:18px;margin:0 0 6px 0;}");
        html.append(".intro p{font-size:13px;color:#52616b;margin:0;line-height:1.45;}");
        html.append(".ai{margin-top:10px;background:#eaf4ff;border-left:4px solid #2563eb;border-radius:8px;padding:10px 12px;color:#1f2933;font-size:12px;line-height:1.45;}");
        html.append(".explanation{background:#fff;border:1px solid #d5dae0;border-radius:10px;padding:14px 16px;margin:-4px 0 12px 0;box-sizing:border-box;}");
        html.append(".explanation h3{font-size:14px;margin:0 0 8px 0;color:#1f2933;}");
        html.append(".explanation .label{font-weight:700;color:#1f2933;margin-top:8px;}");
        html.append(".explanation p{font-size:12px;color:#52616b;line-height:1.45;margin:4px 0;}");
        html.append(".explanation .example{background:#f8fafc;border:1px solid #e5e9ee;border-radius:8px;padding:9px 10px;margin:8px 0;}");
        html.append(".explanation .rule{font-weight:700;color:#1f2933;}");
        html.append(".explanation .statement{display:block;margin-top:4px;color:#52616b;}");
        html.append(".color-legend{background:#fff;border:1px solid #d5dae0;border-radius:10px;padding:10px 12px;margin:-4px 0 12px 0;box-sizing:border-box;font-size:12px;color:#52616b;line-height:1.45;}");
        html.append(".color-legend .legend-title{font-weight:700;color:#1f2933;margin-right:8px;}");
        html.append(".color-legend .legend-item{display:inline-flex;align-items:center;margin:3px 12px 3px 0;white-space:nowrap;cursor:help;}");
        html.append(".color-legend .legend-square{display:inline-block;width:11px;height:11px;border-radius:2px;border:1px solid rgba(0,0,0,.18);margin-right:5px;vertical-align:-1px;}");
        html.append(".card{background:#fff;border:1px solid #d5dae0;border-radius:10px;padding:14px 16px;margin-bottom:12px;box-sizing:border-box;min-height:300px;overflow:hidden;}");
        html.append(".chart-scroll{overflow:auto;max-height:560px;border:1px solid #e5e9ee;border-radius:8px;background:#fff;box-sizing:border-box;}");
        html.append(".chart-scroll svg{display:block;}");
        html.append(".chart-title{font-weight:700;font-size:15px;margin-bottom:2px;}");
        html.append(".chart-subtitle{font-size:12px;color:#52616b;margin-bottom:8px;line-height:1.35;}");
        html.append(".flow-grid{display:flex;flex-direction:column;gap:14px;align-items:stretch;overflow-x:hidden;padding-bottom:4px;}");
        html.append(".flow-panel{flex:1 1 auto;width:100%;min-width:0;background:#f8fafc;border:1px solid #e5e9ee;border-radius:10px;padding:10px 9px 8px 9px;box-sizing:border-box;overflow-x:auto;}");
        html.append(".flow-panel-title{font-weight:700;font-size:13px;color:#1f2933;margin-bottom:3px;}");
        html.append(".flow-panel-subtitle{font-size:11px;color:#52616b;line-height:1.35;min-height:30px;margin-bottom:4px;}");
        html.append(".flow-mini-node rect,.flow-mini-link,.eff-cell{cursor:pointer;}");
        html.append(".effectiveness-panel svg{min-width:760px;}");
        html.append(".axis path,.axis line{fill:none;stroke:#d5dae0;shape-rendering:crispEdges;}");
        html.append(".axis text{font-size:11px;fill:#52616b;}");
        html.append(".grid line{stroke:#e5e9ee;shape-rendering:crispEdges;}");
        html.append(".grid path{display:none;}");
        html.append(".bar,.cell,.dot,.node circle{cursor:pointer;}");
        html.append(".legend text{font-size:12px;fill:#52616b;}");
        html.append(".link{stroke:#b8c0cc;stroke-opacity:.55;}");
        html.append(".node text{font-size:12px;fill:#1f2933;pointer-events:none;}");
        html.append(".tooltip{position:absolute;display:none;background:#1f2933;color:#fff;border-radius:6px;padding:8px 10px;font-size:12px;max-width:520px;line-height:1.35;pointer-events:none;box-shadow:0 4px 14px rgba(0,0,0,.20);z-index:20;}");
        html.append(".tip-title{font-weight:700;margin-bottom:4px;}");
        html.append(".empty{padding:24px;color:#52616b;font-size:13px;}");
        html.append("</style>");
        html.append("<script>").append(d3).append("</script>");
        html.append("</head><body>");
        html.append("<div class=\"intro\"><h1>").append(htmlEscape(t("pesq.visualizations.title"))).append("</h1><p>").append(htmlEscape(t("pesq.visualizations.desc"))).append("</p>");
        html.append("<div class=\"ai\">").append(htmlEscape(t("pesq.visualizations.aiNote"))).append("</div></div>");
        html.append("<!-- D3_HTML_CURRENT: raw-log, agents-C-S, heatmap-flow-matrix-vertical-sequence -->");
        html.append("<div id=\"tooltip\" class=\"tooltip\"></div>");
        html.append("<div id=\"vis-heatmap\" class=\"card\"></div>");
        html.append(blocoLegendaCoresD3());
        html.append(blocoExplicacaoD3(
                "heatmap",
                t("pesq.d3.explain.heatmap.title"),
                t("pesq.d3.explain.heatmap.meaning"),
                t("pesq.d3.explain.heatmap.benefit")));
        html.append("<div id=\"vis-flow\" class=\"card\"></div>");
        html.append(blocoLegendaCoresD3());
        html.append(blocoExplicacaoD3(
                "flow",
                t("pesq.d3.explain.flow.title"),
                t("pesq.d3.explain.flow.meaning"),
                t("pesq.d3.explain.flow.benefit")));
        html.append("<div id=\"vis-sequence\" class=\"card scrollable-card\"></div>");
        html.append(blocoLegendaCoresD3());
        html.append(blocoExplicacaoD3(
                "sequence",
                t("pesq.d3.explain.sequence.title"),
                t("pesq.d3.explain.sequence.meaning"),
                t("pesq.d3.explain.sequence.benefit")));
        html.append("<script>");
        html.append("var RAW=").append(dadosJson).append(";\n");
        html.append("var TXT=").append(labelsJson).append(";\n");
        html.append(scriptD3Tabela14());
        html.append("</script>");
        html.append("</body></html>");
        return html.toString();
    }

    private String blocoLegendaCoresD3() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"color-legend\">");
        sb.append("<span class=\"legend-title\">").append(htmlEscape(t("pesq.d3.legend.title"))).append("</span>");
        sb.append(itemLegendaCorD3("correct", "#2e7d32", t("pesq.d3.raw.type.correct")));
        sb.append(itemLegendaCorD3("error", "#e05757", t("pesq.d3.raw.type.error")));
        sb.append(itemLegendaCorD3("feedback", "#f59e0b", t("pesq.d3.raw.type.feedback")));
        sb.append(itemLegendaCorD3("help", "#2563eb", t("pesq.d3.raw.type.help")));
        sb.append(itemLegendaCorD3("doubt", "#7c3aed", t("pesq.d3.raw.type.doubt")));
        sb.append(itemLegendaCorD3("neutral", "#9aa5b1", t("pesq.d3.legend.neutralAction")));
        sb.append(itemLegendaCorD3("interface", "#64748b", t("pesq.d3.legend.interfaceAction")));
        sb.append(itemLegendaCorD3("subject", "#1f2933", t("pesq.d3.legend.subjectAction")));
        sb.append("</div>");
        return sb.toString();
    }

    private String itemLegendaCorD3(String tipo, String cor, String texto) {
        return gerard.pesquisador.visualizacao.SerializacaoD3.itemLegendaCorD3(tipo, cor, texto);
    }

    private String blocoExplicacaoD3(String id, String titulo, String significado, String beneficio) {
        String seguro = id == null ? "" : id.replaceAll("[^A-Za-z0-9_-]", "");
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"explanation\">");
        sb.append("<h3>").append(htmlEscape(titulo)).append("</h3>");
        sb.append("<p class=\"example\"><span class=\"label\">").append(htmlEscape(t("pesq.d3.explain.exampleLabel"))).append(":</span> ");
        sb.append("<span id=\"explain-").append(htmlEscape(seguro)).append("-example\">").append(htmlEscape(t("pesq.d3.explain.noExample"))).append("</span></p>");
        sb.append("<p><span class=\"label\">").append(htmlEscape(t("pesq.d3.explain.meaningLabel"))).append(":</span> ").append(htmlEscape(significado)).append("</p>");
        sb.append("<p><span class=\"label\">").append(htmlEscape(t("pesq.d3.explain.benefitLabel"))).append(":</span> ").append(htmlEscape(beneficio)).append("</p>");
        sb.append("</div>");
        return sb.toString();
    }

    private String dadosLogBrutoD3Json() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < eventos.size(); i++) {
            if (i > 0) { sb.append(','); }
            EventoLogGerard e = eventos.get(i);
            String problema = problemaAnalitico(e);
            String enunciado = valorLimpo(e.getEnunciado());
            if (enunciado.length() == 0) { enunciado = enunciadoPadrao(problema); }
            sb.append('{');
            sb.append("\"idx\":").append(i + 1).append(',');
            sb.append("\"timestamp\":").append(jsonString(valorLimpo(e.getTimestamp()))).append(',');
            sb.append("\"sessao\":").append(jsonString(normalizar(e.getSessao()))).append(',');
            sb.append("\"usuario\":").append(jsonString(normalizar(e.getUsuario()))).append(',');
            sb.append("\"agente\":").append(jsonString(valorLimpo(e.getAgenteDaAcao()))).append(',');
            sb.append("\"problema\":").append(jsonString(problema)).append(',');
            sb.append("\"situacaoVersaoId\":").append(jsonString(valorLimpo(e.getSituacaoVersaoId()))).append(',');
            sb.append("\"situacaoGrupoId\":").append(jsonString(valorLimpo(e.getSituacaoGrupoId()))).append(',');
            sb.append("\"idiomaSituacao\":").append(jsonString(valorLimpo(e.getIdiomaSituacao()))).append(',');
            sb.append("\"tentativa\":").append(jsonString(normalizar(e.getTentativa()))).append(',');
            sb.append("\"tarefa\":").append(jsonString(normalizar(e.getTarefa()))).append(',');
            sb.append("\"ce\":").append(jsonString(ceParaTabela(e))).append(',');
            sb.append("\"instrumentoOrganizacao\":").append(jsonString(normalizar(e.getInstrumentoOrganizacao()))).append(',');
            sb.append("\"instrumentoArtefato\":").append(jsonString(normalizar(e.getInstrumentoArtefato()))).append(',');
            sb.append("\"funcao\":").append(jsonString(normalizar(e.getFuncaoDoArtefato()))).append(',');
            sb.append("\"objeto\":").append(jsonString(objetoInferido(e))).append(',');
            sb.append("\"regras\":").append(jsonString(normalizar(e.getRegras()))).append(',');
            sb.append("\"categoria\":").append(jsonString(categoriaEsperada(problema, e.getCategoria()))).append(',');
            sb.append("\"enunciado\":").append(jsonString(enunciado)).append(',');
            sb.append("\"origem\":").append(jsonString(normalizar(e.getOrigemEvento()))).append(',');
            sb.append("\"detalhes\":").append(jsonString(normalizar(e.getDetalhes()))).append(',');
            sb.append("\"tipoAcaoInteracao\":").append(jsonString(normalizar(e.getTipoAcaoInteracao()))).append(',');
            sb.append("\"propriedadeAcao\":").append(jsonString(normalizar(e.getPropriedadeAcao()))).append(',');
            sb.append("\"mudancaObservavel\":").append(jsonString(normalizar(e.getMudancaObservavel()))).append(',');
            sb.append("\"ocorrencia\":").append(jsonString(chaveOcorrencia(e)));
            sb.append('}');
        }
        sb.append("]");
        return sb.toString();
    }

    private String labelsD3Json() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"heatmapTitle\":").append(jsonString(t("pesq.d3.raw.heatmap.title"))).append(',');
        sb.append("\"heatmapSubtitle\":").append(jsonString(t("pesq.d3.raw.heatmap.subtitle"))).append(',');
        sb.append("\"flowTitle\":").append(jsonString(t("pesq.d3.raw.flow.title"))).append(',');
        sb.append("\"flowSubtitle\":").append(jsonString(t("pesq.d3.raw.flow.subtitle"))).append(',');
        sb.append("\"flowPanelRegulationTitle\":").append(jsonString(t("pesq.d3.flow.panel.regulation.title"))).append(',');
        sb.append("\"flowPanelRegulationSubtitle\":").append(jsonString(t("pesq.d3.flow.panel.regulation.subtitle"))).append(',');
        sb.append("\"flowPanelRecurrenceTitle\":").append(jsonString(t("pesq.d3.flow.panel.recurrence.title"))).append(',');
        sb.append("\"flowPanelRecurrenceSubtitle\":").append(jsonString(t("pesq.d3.flow.panel.recurrence.subtitle"))).append(',');
        sb.append("\"flowPanelEffectivenessTitle\":").append(jsonString(t("pesq.d3.flow.panel.effectiveness.title"))).append(',');
        sb.append("\"flowPanelEffectivenessSubtitle\":").append(jsonString(t("pesq.d3.flow.panel.effectiveness.subtitle"))).append(',');
        sb.append("\"flowBucketLegend\":").append(jsonString(t("pesq.d3.flow.bucket.legend"))).append(',');
        sb.append("\"flowBucketSign\":").append(jsonString(t("pesq.d3.flow.bucket.sign"))).append(',');
        sb.append("\"flowBucketResult\":").append(jsonString(t("pesq.d3.flow.bucket.result"))).append(',');
        sb.append("\"flowBucketValue\":").append(jsonString(t("pesq.d3.flow.bucket.value"))).append(',');
        sb.append("\"flowBucketDoubt\":").append(jsonString(t("pesq.d3.flow.bucket.doubt"))).append(',');
        sb.append("\"flowBucketOther\":").append(jsonString(t("pesq.d3.flow.bucket.other"))).append(',');
        sb.append("\"flowInterventionFeedback\":").append(jsonString(t("pesq.d3.flow.intervention.feedback"))).append(',');
        sb.append("\"flowInterventionHelp\":").append(jsonString(t("pesq.d3.flow.intervention.help"))).append(',');
        sb.append("\"flowInterventionInterface\":").append(jsonString(t("pesq.d3.flow.intervention.interface"))).append(',');
        sb.append("\"flowOutcomeCorrect\":").append(jsonString(t("pesq.d3.flow.outcome.correct"))).append(',');
        sb.append("\"flowOutcomeError\":").append(jsonString(t("pesq.d3.flow.outcome.error"))).append(',');
        sb.append("\"flowOutcomeDoubt\":").append(jsonString(t("pesq.d3.flow.outcome.doubt"))).append(',');
        sb.append("\"flowOutcomeNeutral\":").append(jsonString(t("pesq.d3.flow.outcome.neutral"))).append(',');
        sb.append("\"flowOutcomeOther\":").append(jsonString(t("pesq.d3.flow.outcome.other"))).append(',');
        sb.append("\"flowEffectRecovered\":").append(jsonString(t("pesq.d3.flow.effect.recovered"))).append(',');
        sb.append("\"flowEffectRepeated\":").append(jsonString(t("pesq.d3.flow.effect.repeated"))).append(',');
        sb.append("\"flowEffectDoubt\":").append(jsonString(t("pesq.d3.flow.effect.doubt"))).append(',');
        sb.append("\"flowEffectNeutral\":").append(jsonString(t("pesq.d3.flow.effect.neutral"))).append(',');
        sb.append("\"flowEffectOther\":").append(jsonString(t("pesq.d3.flow.effect.other"))).append(',');
        sb.append("\"flowThreeIntro\":").append(jsonString(t("pesq.d3.flow.three.intro"))).append(',');
        sb.append("\"flowThreeTotal\":").append(jsonString(t("pesq.d3.flow.three.total"))).append(',');
        sb.append("\"flowThreeTriplets\":").append(jsonString(t("pesq.d3.flow.three.triplets"))).append(',');
        sb.append("\"flowEffectivenessGuide\":").append(jsonString(t("pesq.d3.flow.effectiveness.guide"))).append(',');
        sb.append("\"flowEffectivenessPercent\":").append(jsonString(t("pesq.d3.flow.effectiveness.percent"))).append(',');
        sb.append("\"flowEffectivenessTotal\":").append(jsonString(t("pesq.d3.flow.effectiveness.total"))).append(',');
        sb.append("\"sequenceTitle\":").append(jsonString(t("pesq.d3.raw.sequence.title"))).append(',');
        sb.append("\"sequenceSubtitle\":").append(jsonString(t("pesq.d3.raw.sequence.subtitle"))).append(',');
        sb.append("\"problem\":").append(jsonString(t("pesq.col.problem"))).append(',');
        sb.append("\"statement\":").append(jsonString(t("pesq.col.statement"))).append(',');
        sb.append("\"events\":").append(jsonString(t("pesq.col.events"))).append(',');
        sb.append("\"event\":").append(jsonString(t("pesq.d3.raw.event"))).append(',');
        sb.append("\"occurrence\":").append(jsonString(t("pesq.d3.raw.occurrence"))).append(',');
        sb.append("\"order\":").append(jsonString(t("pesq.d3.raw.order"))).append(',');
        sb.append("\"user\":").append(jsonString(t("pesq.col.user"))).append(',');
        sb.append("\"task\":").append(jsonString(t("pesq.col.task"))).append(',');
        sb.append("\"agent\":").append(jsonString(t("pesq.col.agent"))).append(',');
        sb.append("\"assessment\":").append(jsonString(t("pesq.col.ce"))).append(',');
        sb.append("\"category\":").append(jsonString(t("pesq.col.category"))).append(',');
        sb.append("\"noData\":").append(jsonString(t("pesq.chart.noData"))).append(',');
        sb.append("\"typeCorrect\":").append(jsonString(t("pesq.d3.raw.type.correct"))).append(',');
        sb.append("\"typeError\":").append(jsonString(t("pesq.d3.raw.type.error"))).append(',');
        sb.append("\"typeNeutral\":").append(jsonString(t("pesq.d3.raw.type.neutral"))).append(',');
        sb.append("\"typeFeedback\":").append(jsonString(t("pesq.d3.raw.type.feedback"))).append(',');
        sb.append("\"typeHelp\":").append(jsonString(t("pesq.d3.raw.type.help"))).append(',');
        sb.append("\"typeDoubt\":").append(jsonString(t("pesq.d3.raw.type.doubt"))).append(',');
        sb.append("\"typeOtherInterface\":").append(jsonString(t("pesq.d3.raw.type.otherInterface"))).append(',');
        sb.append("\"typeOtherSubject\":").append(jsonString(t("pesq.d3.raw.type.otherSubject"))).append(',');
        sb.append("\"exampleLabel\":").append(jsonString(t("pesq.d3.explain.exampleLabel"))).append(',');
        sb.append("\"noExample\":").append(jsonString(t("pesq.d3.explain.noExample"))).append(',');
        sb.append("\"heatmapRule\":").append(jsonString(t("pesq.d3.explain.heatmap.rule"))).append(',');
        sb.append("\"flowRule\":").append(jsonString(t("pesq.d3.explain.flow.rule"))).append(',');
        sb.append("\"sequenceRule\":").append(jsonString(t("pesq.d3.explain.sequence.rule"))).append(',');
        sb.append("\"colorLegend\":").append(jsonString(t("pesq.d3.explain.colorLegend"))).append(',');
        sb.append("\"legendQuantity\":").append(jsonString(t("pesq.d3.legend.quantity"))).append(',');
        sb.append("\"legendElements\":").append(jsonString(t("pesq.d3.legend.elements"))).append(',');
        sb.append("\"asSeenHeatmap\":").append(jsonString(t("pesq.d3.explain.asSeenHeatmap"))).append(',');
        sb.append("\"thereAre\":").append(jsonString(t("pesq.d3.explain.thereAre"))).append(',');
        sb.append("\"registeredEvents\":").append(jsonString(t("pesq.d3.explain.registeredEvents"))).append(',');
        sb.append("\"and\":").append(jsonString(t("pesq.d3.explain.and"))).append(',');
        sb.append("\"researcherRelevance\":").append(jsonString(t("pesq.d3.explain.researcherRelevance"))).append(',');
        sb.append("\"heatmapRelevance\":").append(jsonString(t("pesq.d3.explain.heatmap.relevance"))).append(',');
        sb.append("\"flowRelevance\":").append(jsonString(t("pesq.d3.explain.flow.relevance"))).append(',');
        sb.append("\"sequenceRelevance\":").append(jsonString(t("pesq.d3.explain.sequence.relevance"))).append(',');
        sb.append("\"inNetwork\":").append(jsonString(t("pesq.d3.explain.inNetwork"))).append(',');
        sb.append("\"strongestTransition\":").append(jsonString(t("pesq.d3.explain.strongestTransition"))).append(',');
        sb.append("\"withWord\":").append(jsonString(t("pesq.d3.explain.withWord"))).append(',');
        sb.append("\"occurrences\":").append(jsonString(t("pesq.d3.explain.occurrences"))).append(',');
        sb.append("\"mostFrequentNode\":").append(jsonString(t("pesq.d3.explain.mostFrequentNode"))).append(',');
        sb.append("\"inTemporalSequence\":").append(jsonString(t("pesq.d3.explain.inTemporalSequence"))).append(',');
        sb.append("\"hasWord\":").append(jsonString(t("pesq.d3.explain.hasWord"))).append(',');
        sb.append("\"actions\":").append(jsonString(t("pesq.d3.explain.actions"))).append(',');
        sb.append("\"subjectActions\":").append(jsonString(t("pesq.d3.explain.subjectActions"))).append(',');
        sb.append("\"computerActions\":").append(jsonString(t("pesq.d3.explain.computerActions"))).append(',');
        sb.append("\"firstEvent\":").append(jsonString(t("pesq.d3.explain.firstEvent"))).append(',');
        sb.append("\"lastEvent\":").append(jsonString(t("pesq.d3.explain.lastEvent"))).append(',');
        sb.append("\"sequenceComposition\":").append(jsonString(t("pesq.d3.explain.sequenceComposition"))).append(',');
        sb.append("\"networkReadingTitle\":").append(jsonString(t("pesq.d3.network.reading.title"))).append(',');
        sb.append("\"networkExampleLabel\":").append(jsonString(t("pesq.d3.network.example.label"))).append(',');
        sb.append("\"networkRelevanceLabel\":").append(jsonString(t("pesq.d3.network.relevance.label"))).append(',');
        sb.append("\"networkMeaningLabel\":").append(jsonString(t("pesq.d3.network.meaning.label"))).append(',');
        sb.append("\"networkUseLabel\":").append(jsonString(t("pesq.d3.network.use.label"))).append(',');
        sb.append("\"flowRegulationReadingIntro\":").append(jsonString(t("pesq.d3.flow.regulation.readingIntro"))).append(',');
        sb.append("\"flowRegulationNodeRule\":").append(jsonString(t("pesq.d3.flow.regulation.nodeRule"))).append(',');
        sb.append("\"flowRegulationRelevance\":").append(jsonString(t("pesq.d3.flow.regulation.relevance"))).append(',');
        sb.append("\"flowRegulationMeaning\":").append(jsonString(t("pesq.d3.flow.regulation.meaning"))).append(',');
        sb.append("\"flowRegulationUse\":").append(jsonString(t("pesq.d3.flow.regulation.use"))).append(',');
        sb.append("\"flowRecurrenceReadingIntro\":").append(jsonString(t("pesq.d3.flow.recurrence.readingIntro"))).append(',');
        sb.append("\"flowRecurrenceTriplets\":").append(jsonString(t("pesq.d3.flow.recurrence.triplets"))).append(',');
        sb.append("\"flowRecurrenceNodeRule\":").append(jsonString(t("pesq.d3.flow.recurrence.nodeRule"))).append(',');
        sb.append("\"flowRecurrenceRelevance\":").append(jsonString(t("pesq.d3.flow.recurrence.relevance"))).append(',');
        sb.append("\"flowRecurrenceMeaning\":").append(jsonString(t("pesq.d3.flow.recurrence.meaning"))).append(',');
        sb.append("\"flowRecurrenceUse\":").append(jsonString(t("pesq.d3.flow.recurrence.use"))).append(',');
        sb.append("\"flowEffectivenessReadingIntro\":").append(jsonString(t("pesq.d3.flow.effectiveness.readingIntro"))).append(',');
        sb.append("\"flowEffectivenessInterventions\":").append(jsonString(t("pesq.d3.flow.effectiveness.interventions"))).append(',');
        sb.append("\"flowEffectivenessMatrixRule\":").append(jsonString(t("pesq.d3.flow.effectiveness.matrixRule"))).append(',');
        sb.append("\"flowEffectivenessRelevance\":").append(jsonString(t("pesq.d3.flow.effectiveness.relevance"))).append(',');
        sb.append("\"flowEffectivenessMeaning\":").append(jsonString(t("pesq.d3.flow.effectiveness.meaning"))).append(',');
        sb.append("\"flowEffectivenessUse\":").append(jsonString(t("pesq.d3.flow.effectiveness.use"))).append(',');
        sb.append("\"flowMostFrequentTransition\":").append(jsonString(t("pesq.d3.flow.mostFrequentTransition"))).append(',');
        sb.append("\"flowMostFrequentRelation\":").append(jsonString(t("pesq.d3.flow.mostFrequentRelation")));
        sb.append('}');
        return sb.toString();
    }

    private String scriptD3Tabela14() {
        return lerRecursoTexto("/gerard/pesquisador/visualizacao/gerard_d3_visualizacoes.js");
    }

    private String lerRecursoTexto(String caminho) {
        InputStream in = getClass().getResourceAsStream(caminho);
        if (in == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String linha;
            while ((linha = br.readLine()) != null) {
                sb.append(linha).append('\n');
            }
            br.close();
        } catch (Exception ex) {
            return "";
        }
        return sb.toString();
    }

    private String numeroJson(Object valor) {
        return gerard.pesquisador.visualizacao.SerializacaoD3.numeroJson(valor);
    }

    private String jsonString(String valor) {
        return gerard.pesquisador.visualizacao.SerializacaoD3.jsonString(valor);
    }

    private String htmlEscape(String valor) {
        return gerard.pesquisador.visualizacao.SerializacaoD3.htmlEscape(valor);
    }

    private JPanel criarAbaTabela16e17CamadasAgenteEstudo() {
        JPanel painel = painelBase(new BorderLayout(12, 12));
        painel.add(criarTextoModeloTabela(
                "Tabelas 16 e 17 - Camadas do comportamento do agente a partir da análise da tarefa",
                "Formato do estudo: camada, situação observada nas ações, comportamento esperado do agente e indício/decisão após a ajuda. As situações observadas e os indícios são calculados a partir do log."), BorderLayout.NORTH);
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Parte 1", criarTabelaComTitulo("Camadas do comportamento do agente - parte 1", dadosTabela16CamadasAgenteEstudo(), new int[]{230, 430, 520, 520}));
        abas.addTab("Parte 2", criarTabelaComTitulo("Camadas do comportamento do agente - parte 2", dadosTabela17CamadasAgenteEstudo(), new int[]{230, 430, 520, 520}));
        painel.add(abas, BorderLayout.CENTER);
        return painel;
    }

    private Object[][] dadosTabela4ProblemasEstudo() {
        Map<String, String[]> linhas = new LinkedHashMap<String, String[]>();
        for (EventoLogGerard e : eventos) {
            String problema = problemaAnalitico(e);
            if (!linhas.containsKey(problema)) {
                linhas.put(problema, new String[]{problema, e.getCategoria(), ""});
            }
            String enunciado = valorLimpo(e.getEnunciado());
            if (enunciado.length() > 0 && linhas.get(problema)[2].length() == 0) {
                linhas.get(problema)[2] = enunciado;
            }
        }
        Object[][] dados = new Object[linhas.size() + 1][3];
        dados[0] = new Object[]{t("pesq.col.problemLabel"), t("pesq.col.expectedType"), t("pesq.col.statement")};
        int i = 1;
        for (String problema : linhas.keySet()) {
            String[] l = linhas.get(problema);
            dados[i++] = new Object[]{
                    rotuloProblemaTabela4(l[0]),
                    categoriaEsperadaTabela4(l[0], l[1]),
                    enunciadoTabela4(l[0], l[2])
            };
        }
        return dados;
    }

    private Object[][] dadosTabela5FormatoEstudo() {
        Object[][] dados = new Object[5][6];
        dados[0] = new Object[]{"Etapa", "Tarefa", "C/E", "Instrumento", "Função do artefato", "Objeto"};
        dados[1] = new Object[]{"1", "Identificar o tipo de problema", cePredominante("tipo_categoria"), instrumentoMaisFrequente("tipo_categoria"), "Representar a categoria do problema. Eventos: " + contarEtapaModelo("tipo_categoria"), "Problema"};
        dados[2] = new Object[]{"2", "Exibir retorno", "C", instrumentoMaisFrequente("retorno"), "Orientar a revisão da ação. Eventos: " + contarEtapaModelo("retorno"), "Ação do usuário"};
        dados[3] = new Object[]{"3", "Posicionar elemento no diagrama", cePredominante("posicionamento"), instrumentoMaisFrequente("posicionamento"), "Representar relação matemática. Eventos: " + contarEtapaModelo("posicionamento"), "Número ou medida"};
        dados[4] = new Object[]{"4", "Corrigir ação após retorno", cePredominante("correcao_pos_retorno"), instrumentoMaisFrequente("correcao_pos_retorno"), "Ajustar o modelo construído. Eventos: " + contarEtapaModelo("correcao_pos_retorno"), "Diagrama"};
        return dados;
    }

    private Object[][] dadosTabela6UsuariosEstudo() {
        Map<String, Set<String>> ocorrencias = new LinkedHashMap<String, Set<String>>();
        Map<String, Integer> totalAcoes = new LinkedHashMap<String, Integer>();
        for (EventoLogGerard e : eventos) {
            String usuario = normalizar(e.getUsuario());
            if (!ocorrencias.containsKey(usuario)) {
                ocorrencias.put(usuario, new LinkedHashSet<String>());
                totalAcoes.put(usuario, Integer.valueOf(0));
            }
            ocorrencias.get(usuario).add(chaveOcorrencia(e));
            totalAcoes.put(usuario, Integer.valueOf(totalAcoes.get(usuario).intValue() + 1));
        }
        Object[][] dados = new Object[ocorrencias.size() + 2][3];
        dados[0] = new Object[]{t("pesq.col.user"), t("pesq.col.analyzedProblemSituations"), t("pesq.col.subjectPlusComputerActions")};
        int i = 1;
        int somaOcorrencias = 0;
        int somaAcoes = 0;
        for (String usuario : ocorrencias.keySet()) {
            int o = ocorrencias.get(usuario).size();
            int a = totalAcoes.get(usuario).intValue();
            somaOcorrencias += o;
            somaAcoes += a;
            dados[i++] = new Object[]{usuario, Integer.valueOf(o), Integer.valueOf(a)};
        }
        dados[i] = new Object[]{t("pesq.value.total"), Integer.valueOf(somaOcorrencias), Integer.valueOf(somaAcoes)};
        return dados;
    }

    private Object[][] dadosTabela7ObjetosEstudo() {
        Map<String, Integer> contagens = contarObjetosInferidos();
        String[][] base = new String[][]{
                {"OBJ1", t("pesq.obj.OBJ1")},
                {"OBJ2", t("pesq.obj.OBJ2")},
                {"OBJ3", t("pesq.obj.OBJ3")},
                {"OBJ4", t("pesq.obj.OBJ4")},
                {"OBJ5", t("pesq.obj.OBJ5")},
                {"OBJ6", t("pesq.obj.OBJ6")},
                {"OBJ7", t("pesq.obj.OBJ7")},
                {"OBJ8", t("pesq.obj.OBJ8")}
        };
        Object[][] dados = new Object[base.length + 1][3];
        dados[0] = new Object[]{t("pesq.col.identification"), t("pesq.col.description"), t("pesq.col.logOccurrences")};
        for (int i = 0; i < base.length; i++) {
            dados[i + 1] = new Object[]{base[i][0], base[i][1], Integer.valueOf(valor(contagens.get(base[i][0])))};
        }
        return dados;
    }

    private Object[][] dadosTabela9AcoesEstudo() {
        Object[][] dados = new Object[eventos.size() + 1][10];
        dados[0] = new Object[]{"Problema", "Etapa", "C/E", "Tarefa", "Instrumento - organização", "Instrumento - artefato", "Função do artefato", "Objeto", "T. e A.", "Usuário"};
        for (int i = 0; i < eventos.size(); i++) {
            EventoLogGerard e = eventos.get(i);
            dados[i + 1] = new Object[]{problemaAnalitico(e), e.getTentativa(), ceParaTabela(e), e.getTarefa(), e.getInstrumentoOrganizacao(), e.getInstrumentoArtefato(), e.getFuncaoDoArtefato(), objetoInferido(e), teoremaEmAtoInferido(e), e.getUsuario()};
        }
        return dados;
    }

    private Object[][] dadosTabela13AcoesNeutrasEstudo() {
        Map<String, String[]> exemplos = new LinkedHashMap<String, String[]>();
        Map<String, Set<String>> problemas = new LinkedHashMap<String, Set<String>>();
        Map<String, Integer> contagens = new LinkedHashMap<String, Integer>();
        for (EventoLogGerard e : eventos) {
            if (!"S".equals(e.getAgenteDaAcao()) || !"-".equals(e.getCe())) { continue; }
            String tipo = tipoAcaoNeutra(e);
            if (!exemplos.containsKey(tipo)) {
                exemplos.put(tipo, new String[]{exemploEvento(e), justificativaAcaoNeutra(tipo)});
                problemas.put(tipo, new LinkedHashSet<String>());
                contagens.put(tipo, Integer.valueOf(0));
            }
            problemas.get(tipo).add(problemaAnalitico(e));
            contagens.put(tipo, Integer.valueOf(contagens.get(tipo).intValue() + 1));
        }
        Object[][] dados = new Object[exemplos.size() + 1][4];
        dados[0] = new Object[]{t("pesq.col.neutralActionType"), t("pesq.col.problemOccurrence"), t("pesq.col.example"), t("pesq.col.justification")};
        int i = 1;
        for (String tipo : exemplos.keySet()) {
            String ocorrencia = juntarProblemas(problemas.get(tipo)) + " (" + contagens.get(tipo) + " " + t("pesq.value.occurrencesSuffix") + ")";
            dados[i++] = new Object[]{t(tipo), ocorrencia, exemplos.get(tipo)[0], exemplos.get(tipo)[1]};
        }
        return dados;
    }

    private Object[][] dadosTabela14AcoesPorProblemaEstudo() {
        Map<String, int[]> mapa = new LinkedHashMap<String, int[]>();
        Map<String, Set<String>> ocorrencias = new LinkedHashMap<String, Set<String>>();
        for (EventoLogGerard e : eventos) {
            String problema = problemaAnalitico(e);
            if (!mapa.containsKey(problema)) {
                mapa.put(problema, new int[]{0, 0, 0, 0, 0, 0, 0});
                ocorrencias.put(problema, new LinkedHashSet<String>());
            }
            int[] c = mapa.get(problema);
            c[3]++;
            if ("S".equals(e.getAgenteDaAcao())) { c[1]++; }
            if ("C".equals(e.getAgenteDaAcao())) { c[2]++; }
            if ("C".equals(e.getCe())) { c[4]++; }
            if ("E".equals(e.getCe())) { c[5]++; }
            if ("S".equals(e.getAgenteDaAcao()) && "-".equals(e.getCe())) { c[6]++; }
            ocorrencias.get(problema).add(chaveOcorrencia(e));
        }
        Object[][] dados = new Object[mapa.size() + 2][8];
        dados[0] = new Object[]{t("pesq.col.problem"), t("pesq.col.occurrenceCount"), t("pesq.col.subjectActions"), t("pesq.col.computerActions"), t("pesq.col.total"), t("pesq.col.subjectCorrect"), t("pesq.col.subjectErrors"), t("pesq.col.neutralActions")};
        int i = 1;
        int[] total = new int[]{0,0,0,0,0,0,0};
        for (String problema : mapa.keySet()) {
            int[] c = mapa.get(problema);
            c[0] = ocorrencias.get(problema).size();
            for (int j = 0; j < c.length; j++) { total[j] += c[j]; }
            dados[i++] = new Object[]{problema, Integer.valueOf(c[0]), Integer.valueOf(c[1]), Integer.valueOf(c[2]), Integer.valueOf(c[3]), Integer.valueOf(c[4]), Integer.valueOf(c[5]), Integer.valueOf(c[6])};
        }
        dados[i] = new Object[]{t("pesq.value.total"), Integer.valueOf(total[0]), Integer.valueOf(total[1]), Integer.valueOf(total[2]), Integer.valueOf(total[3]), Integer.valueOf(total[4]), Integer.valueOf(total[5]), Integer.valueOf(total[6])};
        return dados;
    }

    private Map<String, Integer> mapaTabela14Metrica(int indiceMetrica) {
        Map<String, int[]> base = contagensPorProblema();
        Map<String, Integer> mapa = new LinkedHashMap<String, Integer>();
        for (String problema : base.keySet()) {
            int[] c = base.get(problema);
            int valor = 0;
            if (indiceMetrica >= 0 && indiceMetrica < c.length) {
                valor = c[indiceMetrica];
            }
            mapa.put(problema, Integer.valueOf(valor));
        }
        return mapa;
    }

    private Map<String, String> enunciadosPorProblemaAnalitico() {
        Map<String, String> mapa = new LinkedHashMap<String, String>();
        for (EventoLogGerard e : eventos) {
            String problema = problemaAnalitico(e);
            if (!mapa.containsKey(problema)) {
                mapa.put(problema, "");
            }
            String enunciado = valorLimpo(e.getEnunciado());
            if (enunciado.length() > 0 && mapa.get(problema).length() == 0) {
                mapa.put(problema, enunciado);
            }
        }
        for (String problema : new ArrayList<String>(mapa.keySet())) {
            if (mapa.get(problema).length() == 0) {
                mapa.put(problema, enunciadoPadrao(problema));
            }
        }
        return mapa;
    }

    private Object[][] dadosTabela15GruposQualitativosEstudo() {
        Set<String> g1 = problemasAutonomiaOperacional();
        Set<String> g2 = problemasComErroRegraRepresentacao();
        Set<String> g3 = problemasComErroPosicionamento();
        Set<String> g4 = problemasComAjudaInsuficiente();
        Set<String> g5 = problemasComReorganizacaoAposAjuda();
        Object[][] dados = new Object[6][3];
        dados[0] = new Object[]{t("pesq.col.qualitativeGroup"), t("pesq.col.similarityCriterion"), t("pesq.col.associatedProblems")};
        dados[1] = new Object[]{t("pesq.group.g1"), t("pesq.group.g1.criteria"), juntarProblemas(g1)};
        dados[2] = new Object[]{t("pesq.group.g2"), t("pesq.group.g2.criteria"), juntarProblemas(g2)};
        dados[3] = new Object[]{t("pesq.group.g3"), t("pesq.group.g3.criteria"), juntarProblemas(g3)};
        dados[4] = new Object[]{t("pesq.group.g4"), t("pesq.group.g4.criteria"), juntarProblemas(g4)};
        dados[5] = new Object[]{t("pesq.group.g5"), t("pesq.group.g5.criteria"), juntarProblemas(g5)};
        return dados;
    }

    private Object[][] dadosTabela16CamadasAgenteEstudo() {
        Object[][] dados = new Object[8][4];
        dados[0] = new Object[]{"Camada", "Situação observada nas ações", "Comportamento esperado do agente", "Indício ou decisão após a ajuda"};
        dados[1] = new Object[]{"Camada operacional", "Condução da tarefa: " + contarConducaoTarefa() + " intervenções da interface.", "Apresentar a tarefa, solicitar categorização, orientar preenchimento, solicitar resultado e validar conclusão.", "A sequência é mantida, mas a condução isolada não garante superação de erro conceitual."};
        dados[2] = new Object[]{"Camada operacional", "Detecção de erro local: " + estatisticas.totalErros() + " ações do sujeito marcadas como erro.", "Identificar ação incompatível com a etapa atual: categoria, posição, entrada antecipada, omissão de sinal ou cálculo inadequado.", "A intervenção deve ocorrer logo após a ação inadequada para impedir avanço com representação incorreta."};
        dados[3] = new Object[]{"Camada operacional", "Correção local após feedback: " + contarCorrecoesAposFeedback() + " ocorrências.", "Apresentar mensagem de revisão quando a ação não corresponde à regra esperada.", "Se a ação seguinte é correta, há indício de suficiência local do feedback; se o erro se repete, a ajuda deve mudar de nível."};
        dados[4] = new Object[]{"Camada diagnóstico-conceitual", "Erro de categorização: " + contarErrosPorTipo("categoria") + " ocorrências.", "Interpretar confusão entre composição, transformação ou comparação.", "Acerto posterior sem justificativa pode indicar correção por eliminação; acerto com coerência de ação indica evidência mais forte."};
        dados[5] = new Object[]{"Camada diagnóstico-conceitual", "Erro de posicionamento no modelo: " + contarErrosPorTipo("posicionamento") + " ocorrências.", "Diferenciar erro de cálculo de erro na associação entre enunciado e função do elemento no diagrama.", "A ajuda deve nomear a função do espaço: parte, todo, referente, referido, estado inicial, estado final ou número relativo."};
        dados[6] = new Object[]{"Camada diagnóstico-conceitual", "Erro no número relativo ou no sinal: " + contarErrosPorTipo("sinal") + " ocorrências.", "Reconhecer que o sujeito pode ter identificado o valor, mas não o sentido da relação: perda, ganho, acréscimo ou redução.", "Correção para valor sinalizado indica avanço na representação formal do número relativo."};
        dados[7] = new Object[]{"Camada diagnóstico-conceitual", "Cálculo correto com representação incorreta: " + contarErrosPorTipo("calculo_representacao") + " indícios.", "Deslocar o foco do resultado numérico para a função do espaço no artefato.", "O agente não deve tratar o caso como simples erro de conta; deve explicitar a diferença entre resultado e papel no diagrama."};
        return dados;
    }

    private Object[][] dadosTabela17CamadasAgenteEstudo() {
        Object[][] dados = new Object[7][4];
        dados[0] = new Object[]{"Camada", "Situação observada nas ações", "Comportamento esperado do agente", "Indício ou decisão após a ajuda"};
        dados[1] = new Object[]{"Camada representacional", "Ajuda concreta ou manipulável: " + contarEventosTexto(new String[]{"ajuda", "quadradinho", "fábrica", "fabrica", "dica"}) + " eventos.", "Oferecer representação concreta, como quadradinhos, conjuntos ou fábrica, para apoiar retirada, ganho ou decomposição.", "Se o sujeito acerta na ajuda concreta e erra no modelo formal, o agente deve criar ponte explícita entre as representações."};
        dados[2] = new Object[]{"Camada representacional", "Ajuda com rótulos no modelo: " + contarEventosTexto(new String[]{"rótulo", "rotulo", "possuía", "restou", "gastou"}) + " eventos.", "Nomear explicitamente os elementos do diagrama e seus papéis conceituais.", "Correção posterior ao rótulo indica que o erro anterior estava ligado à ambiguidade do artefato."};
        dados[3] = new Object[]{"Camada representacional", "Articulação entre representações: " + contarEventosTexto(new String[]{"enunciado", "modelo", "diagrama"}) + " eventos relacionados.", "Relacionar enunciado verbal, representação concreta e modelo formal.", "A ajuda é mais efetiva quando o sujeito transfere o que realizou em uma representação para outra."};
        dados[4] = new Object[]{"Camada reguladora ou estratégica", "Ajuda insuficiente ou mal focalizada: " + contarErrosRepetidosAposFeedback() + " sequências com indício de repetição.", "Reconhecer que a mensagem anterior não atingiu a causa do erro e abandonar feedback genérico.", "Erro repetido exige ajuda mais específica, conceitual ou representacional."};
        dados[5] = new Object[]{"Camada reguladora ou estratégica", "Escolha progressiva da ajuda: " + contarProgressaoAjuda() + " sequências com escalonamento.", "Regular a intervenção em níveis: sinalizar erro, explicitar ponto problemático, oferecer rótulos/material concreto/orientação direta.", "A próxima ajuda deve depender da ação posterior: acerto, erro repetido, correção parcial, pedido de dica ou dúvida."};
        dados[6] = new Object[]{"Camada reguladora ou estratégica", "Avaliação da evidência de compreensão: " + contarAcoesCorretasAposAjuda() + " ações corretas posteriores a ajuda.", "Considerar não apenas se a ação foi correta, mas se houve justificativa ou coerência de sequência.", "Ação correta após muitas tentativas deve ser interpretada com cautela; ação correta estável sugere reorganização mais forte."};
        return dados;
    }

    private String problemaAnalitico(EventoLogGerard e) {
        return rotuloProblemaAnalitico(e == null ? "" : e.getProblema());
    }

    private String rotuloProblemaAnalitico(String valor) {
        String p = valorLimpo(valor).toLowerCase();
        if (p.contains("novo")) { return "Problema 06"; }
        if (p.contains("aj02")) { return "Problema 03"; }
        if (p.contains("p1") || p.equals("01") || p.equals("1")) { return "Problema 01"; }
        if (p.contains("p2") || p.equals("02") || p.equals("2")) { return "Problema 02"; }
        if (p.contains("p3") || p.equals("03") || p.equals("3")) { return "Problema 03"; }
        if (p.contains("p4") || p.equals("04") || p.equals("4")) { return "Problema 04"; }
        if (p.contains("p5") || p.equals("05") || p.equals("5")) { return "Problema 05"; }
        if (p.contains("p6") || p.equals("06") || p.equals("6")) { return "Problema 06"; }
        return normalizar(valor);
    }

    private String chaveOcorrencia(EventoLogGerard e) {
        return normalizar(e.getUsuario()) + "::" + problemaAnalitico(e);
    }

    private String categoriaEsperada(String problema, String categoriaLog) {
        if ("Problema 01".equals(problema)) { return "Composição"; }
        if ("Problema 02".equals(problema)) { return "Transformação"; }
        if ("Problema 03".equals(problema)) { return "Transformação"; }
        if ("Problema 04".equals(problema)) { return "Comparação"; }
        if ("Problema 05".equals(problema)) { return "Comparação"; }
        if ("Problema 06".equals(problema)) { return "Composição"; }
        return normalizar(categoriaLog);
    }

    private String enunciadoPadrao(String problema) {
        if ("Problema 01".equals(problema)) { return "Ao redor da mesa estão sentados 4 garotos e 7 garotas. Quantas pessoas estão sentadas ao redor da mesa?"; }
        if ("Problema 02".equals(problema)) { return "Ricardo saiu de casa com 6 bolas de gude. Ao voltar ele possuía duas bolas. O que aconteceu no jogo?"; }
        if ("Problema 03".equals(problema)) { return "Maria comprou uma caixa de bombons por 4 reais e ainda ficou com 4 reais. Quanto ela possuía antes de fazer a compra?"; }
        if ("Problema 04".equals(problema)) { return "Ricardo tem 6 anos, Carlos tem 4 anos a mais do que ele. Quantos anos tem Carlos?"; }
        if ("Problema 05".equals(problema)) { return "Carlos tem 7 reais e Luiz tem 6 reais a menos do que ele. Quantos reais tem Luiz?"; }
        if ("Problema 06".equals(problema)) { return "Ricardo tem 9 brinquedos dos quais uma parte são carrinhos e a outra parte são bonecos. Sabendo que existem 4 bonecos, quantos são os carrinhos?"; }
        return "Enunciado não registrado no log.";
    }

    private String rotuloProblemaTabela4(String problema) {
        String chave = chaveProblemaPadrao(problema);
        if (chave.length() > 0) {
            return t("pesq.t4.problem." + chave + ".label");
        }
        return normalizar(problema);
    }

    private String categoriaEsperadaTabela4(String problema, String categoriaLog) {
        if ("Problema 01".equals(problema) || "Problema 06".equals(problema)) {
            return t("pesq.t4.type.composition");
        }
        if ("Problema 02".equals(problema) || "Problema 03".equals(problema)) {
            return t("pesq.t4.type.transformation");
        }
        if ("Problema 04".equals(problema) || "Problema 05".equals(problema)) {
            return t("pesq.t4.type.comparison");
        }
        return traduzirCategoriaCurta(categoriaLog);
    }

    private String enunciadoTabela4(String problema, String enunciadoLog) {
        String chave = chaveProblemaPadrao(problema);
        if (chave.length() > 0) {
            return t("pesq.t4.problem." + chave + ".statement");
        }
        String enunciado = valorLimpo(enunciadoLog);
        return enunciado.length() == 0 ? t("pesq.t4.statement.notRegistered") : enunciado;
    }

    private String chaveProblemaPadrao(String problema) {
        if ("Problema 01".equals(problema)) { return "01"; }
        if ("Problema 02".equals(problema)) { return "02"; }
        if ("Problema 03".equals(problema)) { return "03"; }
        if ("Problema 04".equals(problema)) { return "04"; }
        if ("Problema 05".equals(problema)) { return "05"; }
        if ("Problema 06".equals(problema)) { return "06"; }
        return "";
    }

    private String traduzirCategoriaCurta(String categoriaLog) {
        String c = removerAcentos(valorLimpo(categoriaLog).toLowerCase());
        if (c.contains("composicao")) { return t("pesq.t4.type.composition"); }
        if (c.contains("transformacao")) { return t("pesq.t4.type.transformation"); }
        if (c.contains("comparacao")) { return t("pesq.t4.type.comparison"); }
        if (c.length() == 0) { return t("pesq.value.notInformed"); }
        return normalizar(categoriaLog);
    }

    private String removerAcentos(String valor) {
        if (valor == null) { return ""; }
        return valor.replace('á','a').replace('à','a').replace('ã','a').replace('â','a')
                .replace('é','e').replace('ê','e')
                .replace('í','i')
                .replace('ó','o').replace('ô','o').replace('õ','o')
                .replace('ú','u')
                .replace('ç','c');
    }

    private String valorLimpo(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String ceParaTabela(EventoLogGerard e) {
        String ce = valorLimpo(e.getCe());
        if (ce.length() == 0 && "C".equals(e.getAgenteDaAcao())) { return ""; }
        return ce.length() == 0 ? "-" : ce;
    }

    private String textoEvento(EventoLogGerard e) {
        return (valorLimpo(e.getTarefa()) + " " + valorLimpo(e.getInstrumentoOrganizacao()) + " " + valorLimpo(e.getInstrumentoArtefato()) + " " + valorLimpo(e.getFuncaoDoArtefato()) + " " + valorLimpo(e.getRegras()) + " " + valorLimpo(e.getDetalhes())).toLowerCase();
    }

    private boolean contem(EventoLogGerard e, String termo) {
        return textoEvento(e).contains(termo.toLowerCase());
    }

    private boolean contemAlgum(EventoLogGerard e, String[] termos) {
        String t = textoEvento(e);
        for (String termo : termos) {
            if (t.contains(termo.toLowerCase())) { return true; }
        }
        return false;
    }

    private String objetoInferido(EventoLogGerard e) {
        String objeto = valorLimpo(e.getObjeto());
        if (objeto.startsWith("OBJ")) { return objeto; }
        String t = textoEvento(e);
        if (t.contains("estado inicial") || t.contains("possuía") || t.contains("possuia")) { return "OBJ1"; }
        if (t.contains("estado final") || t.contains("restou") || t.contains("ficou")) { return "OBJ2"; }
        if (t.contains("referente")) { return "OBJ3"; }
        if (t.contains("número relativo") || t.contains("numero relativo") || t.contains("sinal") || t.contains("círculo") || t.contains("circulo")) { return "OBJ4"; }
        if (t.contains("referido")) { return "OBJ5"; }
        if (t.contains("todo") || t.contains("total")) { return "OBJ6"; }
        if (t.contains("parte") || t.contains("bonecos") || t.contains("carrinhos")) { return "OBJ7"; }
        if (t.contains("problema") || t.contains("legenda") || t.contains("categoria")) { return "OBJ8"; }
        return objeto.length() == 0 ? "-" : objeto;
    }

    private Map<String, Integer> contarObjetosInferidos() {
        Map<String, Integer> mapa = new LinkedHashMap<String, Integer>();
        for (EventoLogGerard e : eventos) {
            incrementar(mapa, objetoInferido(e));
        }
        return mapa;
    }

    private String teoremaEmAtoInferido(EventoLogGerard e) {
        String obj = objetoInferido(e);
        String cat = categoriaEsperada(problemaAnalitico(e), e.getCategoria());
        if ("OBJ1".equals(obj)) { return "Inv_5"; }
        if ("OBJ2".equals(obj)) { return "Inv_6"; }
        if ("OBJ3".equals(obj)) { return "Inv_11"; }
        if ("OBJ4".equals(obj) && "Comparação".equals(cat)) { return "Inv_12"; }
        if ("OBJ4".equals(obj)) { return "Inv_7"; }
        if ("OBJ5".equals(obj)) { return "Inv_14"; }
        if ("OBJ6".equals(obj)) { return "Inv_3"; }
        if ("OBJ7".equals(obj)) { return "Inv_1"; }
        if ("OBJ8".equals(obj)) { return categoriaParaInvariante(cat); }
        return "-";
    }

    private String categoriaParaInvariante(String cat) {
        if (cat == null) { return "-"; }
        String c = cat.toLowerCase();
        if (c.contains("composição") || c.contains("composicao")) { return "Inv_1"; }
        if (c.contains("transformação") || c.contains("transformacao")) { return "Inv_4"; }
        if (c.contains("comparação") || c.contains("comparacao")) { return "Inv_10"; }
        return "-";
    }

    private int contarEtapaModelo(String tipo) {
        int total = 0;
        for (int i = 0; i < eventos.size(); i++) {
            if (pertenceEtapaModelo(eventos.get(i), tipo, i)) { total++; }
        }
        return total;
    }

    private String cePredominante(String tipo) {
        int c = 0, e = 0, n = 0;
        for (int i = 0; i < eventos.size(); i++) {
            EventoLogGerard ev = eventos.get(i);
            if (!pertenceEtapaModelo(ev, tipo, i)) { continue; }
            if ("C".equals(ev.getCe())) { c++; }
            else if ("E".equals(ev.getCe())) { e++; }
            else { n++; }
        }
        if (e > c && e >= n) { return "E"; }
        if (c >= e && c >= n) { return "C"; }
        return "-";
    }

    private String instrumentoMaisFrequente(String tipo) {
        Map<String, Integer> mapa = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < eventos.size(); i++) {
            EventoLogGerard ev = eventos.get(i);
            if (!pertenceEtapaModelo(ev, tipo, i)) { continue; }
            String instrumento = normalizar(ev.getInstrumentoOrganizacao()) + " / " + normalizar(ev.getInstrumentoArtefato());
            incrementar(mapa, instrumento);
        }
        String melhor = "Não informado";
        int max = -1;
        for (Map.Entry<String, Integer> e : mapa.entrySet()) {
            if (e.getValue().intValue() > max) { max = e.getValue().intValue(); melhor = e.getKey(); }
        }
        return melhor;
    }

    private boolean pertenceEtapaModelo(EventoLogGerard e, String tipo, int indice) {
        String t = textoEvento(e);
        if ("tipo_categoria".equals(tipo)) {
            return "S".equals(e.getAgenteDaAcao()) && (t.contains("tipo de problema") || t.contains("legenda") || t.contains("composição") || t.contains("composicao") || t.contains("transformação") || t.contains("transformacao") || t.contains("comparação") || t.contains("comparacao"));
        }
        if ("retorno".equals(tipo)) {
            return "C".equals(e.getAgenteDaAcao()) && (t.contains("feedback") || t.contains("retorno") || t.contains("tem certeza") || t.contains("mensagem") || t.contains("dica"));
        }
        if ("posicionamento".equals(tipo)) {
            return "S".equals(e.getAgenteDaAcao()) && (t.contains("associar") || t.contains("arrast") || t.contains("posicion") || t.contains("informar valor") || t.contains("número relativo") || t.contains("numero relativo") || t.contains("resultado"));
        }
        if ("correcao_pos_retorno".equals(tipo)) {
            if (!"S".equals(e.getAgenteDaAcao())) { return false; }
            return indice > 0 && "C".equals(eventos.get(indice - 1).getAgenteDaAcao()) && contemAlgum(eventos.get(indice - 1), new String[]{"feedback", "tem certeza", "dica", "erro", "rótulo", "rotulo"});
        }
        return false;
    }

    private String tipoAcaoNeutra(EventoLogGerard e) {
        String texto = textoEvento(e);
        if (texto.contains("ler o enunciado") || texto.contains("lê") || texto.contains("leitura")) { return "pesq.neutral.reading"; }
        if (texto.contains("passagem") || texto.contains("próxima tarefa") || texto.contains("proxima tarefa")) { return "pesq.neutral.nextTask"; }
        if (texto.contains("retornar") || texto.contains("voltar") || texto.contains("retomo")) { return "pesq.neutral.returnAfterHelp"; }
        if (texto.contains("dica") || texto.contains("ajuda")) { return "pesq.neutral.helpRequest"; }
        if (texto.contains("sinal de menos") || texto.contains("como eu faço") || texto.contains("entrada")) { return "pesq.neutral.dataEntryQuestion"; }
        if (texto.contains("arrastar") || texto.contains("modelo") || texto.contains("diagrama") || texto.contains("para cá") || texto.contains("para ca")) { return "pesq.neutral.modelUseQuestion"; }
        if (texto.contains("esclarecimento") || texto.contains("pergunta") || texto.contains("tenho que")) { return "pesq.neutral.procedureQuestion"; }
        if (texto.contains("ok") || texto.contains("confirmar") || texto.contains("clicou")) { return "pesq.neutral.confirmInstruction"; }
        return "pesq.neutral.operational";
    }

    private String exemploEvento(EventoLogGerard e) {
        String org = valorLimpo(e.getInstrumentoOrganizacao());
        if (org.length() > 0) { return org; }
        String tarefa = valorLimpo(e.getTarefa());
        return tarefa.length() == 0 ? t("pesq.value.exampleNotRegistered") : tarefa;
    }

    private String justificativaAcaoNeutra(String tipo) {
        return t(tipo + ".justification");
    }

    private String juntarProblemas(Set<String> problemas) {
        if (problemas == null || problemas.isEmpty()) { return t("pesq.value.noSufficientOccurrence"); }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String p : problemas) {
            if (i > 0) { sb.append(", "); }
            sb.append(p);
            i++;
        }
        return sb.toString();
    }

    private Set<String> problemasAutonomiaOperacional() {
        Map<String, int[]> c = contagensPorProblema();
        Set<String> r = new LinkedHashSet<String>();
        for (String p : c.keySet()) {
            int[] v = c.get(p);
            int erros = v[5];
            int acertos = v[4];
            if (erros <= 1 || (acertos >= 2 && erros <= Math.max(1, acertos / 4))) { r.add(p); }
        }
        return r;
    }

    private Set<String> problemasComErroRegraRepresentacao() {
        return problemasPorErro(new String[]{"sinal", "número relativo", "numero relativo", "mais", "menos", "+", "-"});
    }

    private Set<String> problemasComErroPosicionamento() {
        return problemasPorErro(new String[]{"posicion", "associar", "arrast", "quadrado", "círculo", "circulo", "estado", "parte", "todo", "referente", "referido"});
    }

    private Set<String> problemasComAjudaInsuficiente() {
        Set<String> r = new LinkedHashSet<String>();
        Map<String, int[]> c = contagensPorProblema();
        for (String p : c.keySet()) {
            if (c.get(p)[5] >= 2) { r.add(p); }
        }
        for (EventoLogGerard e : eventos) {
            if ("E".equals(e.getCe()) && contemAlgum(e, new String[]{"repetir", "novamente", "mantém", "continua"})) { r.add(problemaAnalitico(e)); }
        }
        return r;
    }

    private Set<String> problemasComReorganizacaoAposAjuda() {
        Set<String> r = new LinkedHashSet<String>();
        for (int i = 1; i < eventos.size(); i++) {
            EventoLogGerard ant = eventos.get(i - 1);
            EventoLogGerard atual = eventos.get(i);
            if ("C".equals(ant.getAgenteDaAcao()) && "S".equals(atual.getAgenteDaAcao()) && "C".equals(atual.getCe()) && mesmoContexto(ant, atual) && contemAlgum(ant, new String[]{"feedback", "tem certeza", "dica", "ajuda", "rótulo", "rotulo", "sinal"})) {
                r.add(problemaAnalitico(atual));
            }
        }
        return r;
    }

    private Set<String> problemasPorErro(String[] termos) {
        Set<String> r = new LinkedHashSet<String>();
        for (EventoLogGerard e : eventos) {
            if ("E".equals(e.getCe()) && contemAlgum(e, termos)) { r.add(problemaAnalitico(e)); }
        }
        return r;
    }

    private Map<String, int[]> contagensPorProblema() {
        Map<String, int[]> mapa = new LinkedHashMap<String, int[]>();
        for (EventoLogGerard e : eventos) {
            String problema = problemaAnalitico(e);
            if (!mapa.containsKey(problema)) { mapa.put(problema, new int[]{0,0,0,0,0,0,0}); }
            int[] c = mapa.get(problema);
            c[3]++;
            if ("S".equals(e.getAgenteDaAcao())) { c[1]++; }
            if ("C".equals(e.getAgenteDaAcao())) { c[2]++; }
            if ("C".equals(e.getCe())) { c[4]++; }
            if ("E".equals(e.getCe())) { c[5]++; }
            if ("S".equals(e.getAgenteDaAcao()) && "-".equals(e.getCe())) { c[6]++; }
        }
        return mapa;
    }

    private int contarConducaoTarefa() {
        int n = 0;
        for (EventoLogGerard e : eventos) {
            if ("C".equals(e.getAgenteDaAcao()) && contemAlgum(e, new String[]{"apresentar", "solicitar", "confirmar conclusão", "validar", "orientar", "diagrama"})) { n++; }
        }
        return n;
    }

    private int contarCorrecoesAposFeedback() {
        int n = 0;
        for (int i = 1; i < eventos.size(); i++) {
            EventoLogGerard ant = eventos.get(i - 1);
            EventoLogGerard atual = eventos.get(i);
            if ("C".equals(ant.getAgenteDaAcao()) && "S".equals(atual.getAgenteDaAcao()) && "C".equals(atual.getCe()) && mesmoContexto(ant, atual) && contemAlgum(ant, new String[]{"feedback", "tem certeza", "erro", "dica", "rótulo", "rotulo"})) { n++; }
        }
        return n;
    }

    private int contarErrosPorTipo(String tipo) {
        int n = 0;
        for (EventoLogGerard e : eventos) {
            if (!"E".equals(e.getCe())) { continue; }
            if ("categoria".equals(tipo) && contemAlgum(e, new String[]{"legenda", "tipo de problema", "categoria", "composição", "composicao", "transformação", "transformacao", "comparação", "comparacao"})) { n++; }
            if ("posicionamento".equals(tipo) && contemAlgum(e, new String[]{"posicion", "associar", "arrast", "quadrado", "círculo", "circulo", "estado", "parte", "todo", "referente", "referido"})) { n++; }
            if ("sinal".equals(tipo) && contemAlgum(e, new String[]{"sinal", "número relativo", "numero relativo", "menos", "mais", "perda", "ganho"})) { n++; }
            if ("calculo_representacao".equals(tipo) && contemAlgum(e, new String[]{"resultado", "calcula", "calculo", "corretamente", "mantém", "continua", "associação inadequada"})) { n++; }
        }
        return n;
    }

    private int contarEventosTexto(String[] termos) {
        int n = 0;
        for (EventoLogGerard e : eventos) {
            if (contemAlgum(e, termos)) { n++; }
        }
        return n;
    }

    private int contarErrosRepetidosAposFeedback() {
        int n = 0;
        for (int i = 1; i < eventos.size(); i++) {
            EventoLogGerard ant = eventos.get(i - 1);
            EventoLogGerard atual = eventos.get(i);
            if ("C".equals(ant.getAgenteDaAcao()) && "S".equals(atual.getAgenteDaAcao()) && "E".equals(atual.getCe()) && mesmoContexto(ant, atual) && contemAlgum(ant, new String[]{"feedback", "tem certeza", "dica", "erro"})) { n++; }
        }
        return n;
    }

    private int contarProgressaoAjuda() {
        int n = 0;
        for (int i = 2; i < eventos.size(); i++) {
            EventoLogGerard e1 = eventos.get(i - 2);
            EventoLogGerard e2 = eventos.get(i - 1);
            EventoLogGerard e3 = eventos.get(i);
            if ("E".equals(e1.getCe()) && "C".equals(e2.getAgenteDaAcao()) && "C".equals(e3.getAgenteDaAcao()) && mesmoContexto(e1, e2) && mesmoContexto(e2, e3) && contemAlgum(e2, new String[]{"feedback", "tem certeza", "erro"}) && contemAlgum(e3, new String[]{"dica", "ajuda", "rótulo", "rotulo", "orientar"})) { n++; }
        }
        return n;
    }

    private int contarAcoesCorretasAposAjuda() {
        int n = 0;
        for (int i = 1; i < eventos.size(); i++) {
            EventoLogGerard ant = eventos.get(i - 1);
            EventoLogGerard atual = eventos.get(i);
            if ("S".equals(atual.getAgenteDaAcao()) && "C".equals(atual.getCe()) && "C".equals(ant.getAgenteDaAcao()) && mesmoContexto(ant, atual) && contemAlgum(ant, new String[]{"feedback", "dica", "ajuda", "rótulo", "rotulo", "orientar", "sinal"})) { n++; }
        }
        return n;
    }

    private boolean mesmoContexto(EventoLogGerard a, EventoLogGerard b) {
        if (a == null || b == null) { return false; }
        return normalizar(a.getUsuario()).equals(normalizar(b.getUsuario())) && problemaAnalitico(a).equals(problemaAnalitico(b));
    }

    private String normalizar(String valor) {
        if (valor == null || valor.trim().length() == 0) {
            return "Não informado";
        }
        return valor.trim();
    }

    private void incrementar(Map<String, Integer> mapa, String chave) {
        String normalizada = normalizar(chave);
        Integer valor = mapa.get(normalizada);
        mapa.put(normalizada, Integer.valueOf(valor == null ? 1 : valor.intValue() + 1));
    }

    private Object[][] dadosLogBruto() {
        Object[][] dados = new Object[eventos.size() + 1][EventoLogGerard.CABECALHO.length];
        for (int c = 0; c < EventoLogGerard.CABECALHO.length; c++) {
            dados[0][c] = EventoLogGerard.CABECALHO[c];
        }
        for (int i = 0; i < eventos.size(); i++) {
            EventoLogGerard e = eventos.get(i);
            dados[i + 1] = new Object[]{
                    e.getTimestamp(), e.getSessao(), e.getUsuario(), e.getAgenteDaAcao(), e.getProblema(),
                    e.getSituacaoVersaoId(), e.getSituacaoGrupoId(), e.getIdiomaSituacao(), e.getTentativa(),
                    e.getTarefa(), e.getCe(), e.getInstrumentoOrganizacao(), e.getInstrumentoArtefato(), e.getFuncaoDoArtefato(),
                    e.getObjeto(), e.getRegras(), e.getCategoria(), e.getEnunciado(), e.getOrigemEvento(), e.getDetalhes(),
                    e.getTipoAcaoInteracao(), e.getPropriedadeAcao(), e.getMudancaObservavel()
            };
        }
        return dados;
    }

    private Object[][] dadosAcertosErrosPorCategoria() {
        Map<String, Integer> acertos = estatisticas.contarAcertosPorCategoria();
        Map<String, Integer> erros = estatisticas.contarErrosPorCategoria();
        List<String> categorias = new ArrayList<String>();
        for (String chave : acertos.keySet()) {
            if (!categorias.contains(chave)) {
                categorias.add(chave);
            }
        }
        for (String chave : erros.keySet()) {
            if (!categorias.contains(chave)) {
                categorias.add(chave);
            }
        }
        Object[][] dados = new Object[categorias.size() + 1][4];
        dados[0] = new Object[]{t("pesq.col.category"), t("pesq.col.correct"), t("pesq.col.errors"), t("pesq.col.totalEvaluated")};
        for (int i = 0; i < categorias.size(); i++) {
            String categoria = categorias.get(i);
            int a = valor(acertos.get(categoria));
            int e = valor(erros.get(categoria));
            dados[i + 1] = new Object[]{categoria, Integer.valueOf(a), Integer.valueOf(e), Integer.valueOf(a + e)};
        }
        return dados;
    }

    private Object[][] dadosContagem(Map<String, Integer> mapa, String colunaChave, String colunaValor) {
        Object[][] dados = new Object[mapa.size() + 1][2];
        dados[0] = new Object[]{colunaChave, colunaValor};
        int i = 1;
        for (Map.Entry<String, Integer> entrada : mapa.entrySet()) {
            dados[i++] = new Object[]{entrada.getKey(), entrada.getValue()};
        }
        return dados;
    }

    private int valor(Integer inteiro) {
        return inteiro == null ? 0 : inteiro.intValue();
    }

    private JPanel painelBase(java.awt.LayoutManager layout) {
        JPanel painel = new JPanel(layout);
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(12, 10, 10, 10));
        return painel;
    }

    private JPanel criarCard(String titulo, String valor, String subtitulo) {
        JPanel card = new JPanel(new BorderLayout(2, 2));
        card.setBackground(COR_SUPERFICIE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        card.setPreferredSize(new Dimension(190, 94));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        labelTitulo.setForeground(COR_TEXTO_SECUNDARIO);
        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Arial", Font.BOLD, 28));
        labelValor.setForeground(COR_PRIMARIA);
        JLabel labelSubtitulo = new JLabel(subtitulo);
        labelSubtitulo.setFont(new Font("Arial", Font.PLAIN, 11));
        labelSubtitulo.setForeground(COR_TEXTO_SECUNDARIO);

        card.add(labelTitulo, BorderLayout.NORTH);
        card.add(labelValor, BorderLayout.CENTER);
        card.add(labelSubtitulo, BorderLayout.SOUTH);
        return card;
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setFocusPainted(false);
        botao.setBackground(new Color(243, 244, 246));
        botao.setForeground(COR_TEXTO);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 18, 6, 18)
        ));
        return botao;
    }

    private JPanel criarAbaTabela(String titulo, Object[][] dados, int[] larguras) {
        JPanel painel = painelBase(new BorderLayout());
        painel.add(criarTabelaComTitulo(titulo, dados, larguras), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarTabelaComTitulo(String titulo, Object[][] dados, int[] larguras) {
        return criarTabelaComTitulo(titulo, dados, larguras, null, -1);
    }

    private JPanel criarTabelaComTitulo(String titulo, Object[][] dados, int[] larguras, final Map<String, String> dicasPorValor, final int colunaDicaModelo) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(COR_FUNDO);

        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(COR_TEXTO);
        painel.add(label, BorderLayout.NORTH);

        String[] colunas = new String[dados.length == 0 ? 0 : dados[0].length];
        for (int i = 0; i < colunas.length; i++) {
            colunas[i] = String.valueOf(dados[0][i]);
        }
        Object[][] linhas = new Object[Math.max(0, dados.length - 1)][colunas.length];
        for (int i = 1; i < dados.length; i++) {
            System.arraycopy(dados[i], 0, linhas[i - 1], 0, colunas.length);
        }

        JTable tabela = new JTable(new ModeloTabelaNaoEditavel(linhas, colunas)) {
            public String getToolTipText(MouseEvent evento) {
                if (dicasPorValor != null && colunaDicaModelo >= 0 && evento != null) {
                    int linhaVisao = rowAtPoint(evento.getPoint());
                    int colunaVisao = columnAtPoint(evento.getPoint());
                    if (linhaVisao >= 0 && colunaVisao >= 0) {
                        int colunaModelo = convertColumnIndexToModel(colunaVisao);
                        if (colunaModelo == colunaDicaModelo) {
                            int linhaModelo = convertRowIndexToModel(linhaVisao);
                            Object valor = getModel().getValueAt(linhaModelo, colunaModelo);
                            String chave = valor == null ? "" : String.valueOf(valor);
                            String dica = dicasPorValor.get(chave);
                            if (dica != null && dica.trim().length() > 0) {
                                return formatarTooltipHtml(dica);
                            }
                        }
                    }
                }
                return super.getToolTipText(evento);
            }
        };
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.setRowHeight(26);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setGridColor(COR_BORDA);
        tabela.setShowVerticalLines(true);
        tabela.setShowHorizontalLines(true);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(COR_DESTAQUE);
        tabela.getTableHeader().setForeground(COR_TEXTO);
        tabela.setAutoCreateRowSorter(true);
        ajustarLarguras(tabela, larguras);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            String nome = tabela.getColumnName(i).toLowerCase();
            if (nome.equals("ce") || nome.contains("agente") || nome.contains("tentativa") || nome.contains("eventos") || nome.contains("erro") || nome.contains("acerto")) {
                tabela.getColumnModel().getColumn(i).setCellRenderer(centro);
            }
        }

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(COR_SUPERFICIE);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    private String formatarTooltipHtml(String texto) {
        String limpo = texto == null ? "" : texto.trim();
        if (limpo.length() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder("<html><div style='width:360px;'>");
        String[] palavras = escaparHtml(limpo).split(" ");
        int linha = 0;
        for (int i = 0; i < palavras.length; i++) {
            String palavra = palavras[i];
            if (linha > 0 && linha + palavra.length() > 70) {
                sb.append("<br>");
                linha = 0;
            } else if (i > 0) {
                sb.append(' ');
                linha++;
            }
            sb.append(palavra);
            linha += palavra.length();
        }
        sb.append("</div></html>");
        return sb.toString();
    }

    private String escaparHtml(String texto) {
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void ajustarLarguras(JTable tabela, int[] larguras) {
        if (larguras == null) {
            return;
        }
        TableColumnModel modelo = tabela.getColumnModel();
        for (int i = 0; i < larguras.length && i < modelo.getColumnCount(); i++) {
            modelo.getColumn(i).setPreferredWidth(larguras[i]);
        }
    }

    private static class ModeloTabelaNaoEditavel extends DefaultTableModel {
        ModeloTabelaNaoEditavel(Object[][] dados, Object[] colunas) {
            super(dados, colunas);
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public Class<?> getColumnClass(int columnIndex) {
            for (int i = 0; i < getRowCount(); i++) {
                Object valor = getValueAt(i, columnIndex);
                if (valor != null) {
                    return valor.getClass();
                }
            }
            return Object.class;
        }
    }

    private class GraficoBarrasDuplas extends JPanel {
        private final String titulo;
        private final String subtitulo;
        private final Map<String, Integer> dadosA;
        private final String legendaA;
        private final Map<String, Integer> dadosB;
        private final String legendaB;

        GraficoBarrasDuplas(String titulo, String subtitulo, Map<String, Integer> dadosA, String legendaA, Map<String, Integer> dadosB, String legendaB) {
            this.titulo = titulo;
            this.subtitulo = subtitulo;
            this.dadosA = dadosA;
            this.legendaA = legendaA;
            this.dadosB = dadosB;
            this.legendaB = legendaB;
            setBackground(COR_SUPERFICIE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)
            ));
            setPreferredSize(new Dimension(780, 260));
            setMinimumSize(new Dimension(580, 210));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margemEsq = 62;
            int margemDir = 24;
            int margemTopo = 58;
            int margemBaixo = 72;
            int areaW = Math.max(10, w - margemEsq - margemDir);
            int areaH = Math.max(10, h - margemTopo - margemBaixo);

            g2.setColor(COR_TEXTO);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(titulo, 18, 24);
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(COR_TEXTO_SECUNDARIO);
            g2.drawString(subtitulo, 18, 40);

            if ((dadosA == null || dadosA.isEmpty()) && (dadosB == null || dadosB.isEmpty())) {
                g2.setFont(new Font("Arial", Font.PLAIN, 13));
                g2.drawString(t("pesq.chart.noData"), 24, h / 2);
                g2.dispose();
                return;
            }

            int max = 1;
            List<String> chaves = new ArrayList<String>();
            if (dadosA != null) {
                for (String chave : dadosA.keySet()) {
                    if (!chaves.contains(chave)) { chaves.add(chave); }
                }
            }
            if (dadosB != null) {
                for (String chave : dadosB.keySet()) {
                    if (!chaves.contains(chave)) { chaves.add(chave); }
                }
            }
            for (String chave : chaves) {
                int a = valor(dadosA == null ? null : dadosA.get(chave));
                int b = valor(dadosB == null ? null : dadosB.get(chave));
                if (a > max) { max = a; }
                if (b > max) { max = b; }
            }

            g2.setColor(new Color(229, 233, 238));
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 4; i++) {
                int y = margemTopo + areaH - (areaH * i / 4);
                g2.drawLine(margemEsq, y, w - margemDir, y);
                int valorGrade = (int) Math.round(max * (i / 4.0));
                g2.setColor(COR_TEXTO_SECUNDARIO);
                g2.drawString(String.valueOf(valorGrade), 22, y + 4);
                g2.setColor(new Color(229, 233, 238));
            }

            int itens = Math.max(1, chaves.size());
            int espacamento = Math.max(1, areaW / itens);
            int larguraBarra = Math.max(12, Math.min(30, (espacamento - 22) / 2));
            int i = 0;
            for (String chave : chaves) {
                int baseX = margemEsq + i * espacamento + Math.max(0, (espacamento - (2 * larguraBarra + 6)) / 2);
                int a = valor(dadosA == null ? null : dadosA.get(chave));
                int b = valor(dadosB == null ? null : dadosB.get(chave));
                desenharBarra(g2, baseX, larguraBarra, margemTopo, areaH, max, a, COR_PRIMARIA);
                desenharBarra(g2, baseX + larguraBarra + 6, larguraBarra, margemTopo, areaH, max, b, COR_TEXTO_SECUNDARIO);

                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.setColor(COR_TEXTO_SECUNDARIO);
                String label = abreviar(chave, 16);
                int labelW = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, margemEsq + i * espacamento + Math.max(0, (espacamento - labelW) / 2), margemTopo + areaH + 24);
                i++;
            }

            int legendaY = h - 18;
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(COR_PRIMARIA);
            g2.fillRect(18, legendaY - 9, 10, 10);
            g2.setColor(COR_TEXTO_SECUNDARIO);
            g2.drawString(legendaA, 34, legendaY);
            int x2 = 180;
            g2.fillRect(x2, legendaY - 9, 10, 10);
            g2.drawString(legendaB, x2 + 16, legendaY);
            g2.dispose();
        }

        private void desenharBarra(Graphics2D g2, int x, int largura, int margemTopo, int areaH, int max, int valor, Color cor) {
            int barraH = (int) Math.round((valor / (double) max) * areaH);
            int y = margemTopo + areaH - barraH;
            g2.setColor(cor);
            g2.fillRoundRect(x, y, largura, barraH, 8, 8);
            g2.setColor(COR_TEXTO);
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            String textoValor = String.valueOf(valor);
            int textoW = g2.getFontMetrics().stringWidth(textoValor);
            g2.drawString(textoValor, x + (largura - textoW) / 2, Math.max(margemTopo + 10, y - 5));
        }

        private String abreviar(String texto, int limite) {
            if (texto == null) {
                return "";
            }
            if (texto.length() <= limite) {
                return texto;
            }
            return texto.substring(0, Math.max(0, limite - 1)) + "…";
        }
    }

    private class GraficoBarras extends JPanel {
        private final String titulo;
        private final String subtitulo;
        private final Map<String, Integer> dados;
        private final Color corBarra;

        GraficoBarras(String titulo, String subtitulo, Map<String, Integer> dados, Color corBarra) {
            this.titulo = titulo;
            this.subtitulo = subtitulo;
            this.dados = dados;
            this.corBarra = corBarra;
            setBackground(COR_SUPERFICIE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BORDA),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)
            ));
            setPreferredSize(new Dimension(780, 260));
            setMinimumSize(new Dimension(580, 210));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margemEsq = 62;
            int margemDir = 24;
            int margemTopo = 48;
            int margemBaixo = 66;
            int areaW = Math.max(10, w - margemEsq - margemDir);
            int areaH = Math.max(10, h - margemTopo - margemBaixo);

            g2.setColor(COR_TEXTO);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(titulo, 18, 24);
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(COR_TEXTO_SECUNDARIO);
            g2.drawString(subtitulo, 18, 40);

            if (dados == null || dados.isEmpty()) {
                g2.setFont(new Font("Arial", Font.PLAIN, 13));
                g2.drawString(t("pesq.chart.noData"), 24, h / 2);
                g2.dispose();
                return;
            }

            int max = 1;
            int itens = 0;
            for (Integer valor : dados.values()) {
                if (valor != null && valor.intValue() > max) {
                    max = valor.intValue();
                }
                itens++;
            }

            g2.setColor(new Color(229, 233, 238));
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i <= 4; i++) {
                int y = margemTopo + areaH - (areaH * i / 4);
                g2.drawLine(margemEsq, y, w - margemDir, y);
                int valor = (int) Math.round(max * (i / 4.0));
                g2.setColor(COR_TEXTO_SECUNDARIO);
                g2.drawString(String.valueOf(valor), 22, y + 4);
                g2.setColor(new Color(229, 233, 238));
            }

            int espacamento = Math.max(1, areaW / Math.max(1, itens));
            int larguraBarra = Math.max(18, Math.min(72, espacamento - 18));
            int i = 0;
            for (Map.Entry<String, Integer> entrada : dados.entrySet()) {
                int valor = entrada.getValue() == null ? 0 : entrada.getValue().intValue();
                int barraH = (int) Math.round((valor / (double) max) * areaH);
                int x = margemEsq + i * espacamento + Math.max(0, (espacamento - larguraBarra) / 2);
                int y = margemTopo + areaH - barraH;

                g2.setColor(corBarra);
                g2.fillRoundRect(x, y, larguraBarra, barraH, 10, 10);
                g2.setColor(COR_TEXTO);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String textoValor = String.valueOf(valor);
                int textoW = g2.getFontMetrics().stringWidth(textoValor);
                g2.drawString(textoValor, x + (larguraBarra - textoW) / 2, Math.max(margemTopo + 12, y - 6));

                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                g2.setColor(COR_TEXTO_SECUNDARIO);
                String label = abreviar(entrada.getKey(), 16);
                int labelW = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, x + (larguraBarra - labelW) / 2, margemTopo + areaH + 24);
                i++;
            }

            g2.dispose();
        }

        private String abreviar(String texto, int limite) {
            if (texto == null) {
                return "";
            }
            if (texto.length() <= limite) {
                return texto;
            }
            return texto.substring(0, Math.max(0, limite - 1)) + "…";
        }
    }
}
