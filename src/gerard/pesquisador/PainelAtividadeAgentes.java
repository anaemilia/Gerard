package gerard.pesquisador;

import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.OuvinteCasoAgenteModelador;
import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.monitor.AgenteMonitor;
import gerard.agente.monitor.OuvinteVeredictoAgenteMonitor;
import gerard.agente.zdp.AgenteZDP;
import gerard.agente.zdp.CamadaEstrategiaZDP;
import gerard.agente.zdp.OuvinteEstrategiaAgenteZDP;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.ui.UITemaGerard;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Aba ao vivo da Visão de Pesquisador (ver gerard-ajuda-adaptativa): mostra,
 * em ordem cronológica inversa, cada ação percebida pelos três agentes da
 * Ajuda Adaptativa enquanto o Gérard está em uso — Monitor, ZDP e Modelador.
 *
 * Complementa o IndicadorAgenteMonitor (o LED neutro visto pelo estudante):
 * este painel é só para o pesquisador e por isso pode usar vocabulário
 * técnico direto — o LED continua sendo o único sinal que o estudante vê.
 *
 * Se registra como ouvinte dos três agentes no construtor; quem instancia
 * este painel é responsável por chamar desconectar() quando a tela que o
 * contém for fechada, para não acumular ouvintes a cada reabertura.
 */
public final class PainelAtividadeAgentes extends JPanel
        implements OuvinteVeredictoAgenteMonitor, OuvinteEstrategiaAgenteZDP, OuvinteCasoAgenteModelador {

    /**
     * Uma cor discreta por agente, só pra escanear mais rápido qual linha é
     * de quem — tons contidos da mesma família neutra/quente do resto do
     * app (ver gerard-identidade-visual), sem usar COR_SUCESSO/COR_ERRO
     * (reservadas ao feedback pedagógico do estudante, não a esta tabela
     * técnica só do pesquisador).
     */
    private static final Map<String, Color> COR_POR_AGENTE = new HashMap<String, Color>();
    static {
        COR_POR_AGENTE.put("Monitor", new Color(0x8C, 0x5A, 0x3C));
        COR_POR_AGENTE.put("ZDP", new Color(0x8A, 0x7B, 0x3E));
        COR_POR_AGENTE.put("Modelador", new Color(0x4F, 0x6F, 0x64));
    }

    private final AgenteMonitor agenteMonitor;
    private final AgenteZDP agenteZDP;
    private final AgenteModelador agenteModelador;
    private final DefaultTableModel modeloTabela;
    private final SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");

    public PainelAtividadeAgentes(AgenteMonitor agenteMonitor, AgenteZDP agenteZDP, AgenteModelador agenteModelador) {
        super(new BorderLayout(10, 10));
        this.agenteMonitor = agenteMonitor;
        this.agenteZDP = agenteZDP;
        this.agenteModelador = agenteModelador;

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 10, 10, 10));

        JLabel subtitulo = new JLabel(
                "Atividade dos agentes Monitor, ZDP e Modelador em tempo real, enquanto o Gérard é usado.");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        add(subtitulo, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new Object[]{"Hora", "Agente", "Detalhe"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        JTable tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setGridColor(UITemaGerard.COR_BORDA);
        tabela.setShowVerticalLines(true);
        tabela.setShowHorizontalLines(true);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.getTableHeader().setBackground(UITemaGerard.COR_DESTAQUE);
        tabela.getTableHeader().setForeground(UITemaGerard.COR_TEXTO);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(700);
        tabela.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado,
                    boolean focoAtivo, int linha, int coluna) {
                Component componente = super.getTableCellRendererComponent(
                        tabela, valor, selecionado, focoAtivo, linha, coluna);
                Color cor = COR_POR_AGENTE.get(valor);
                componente.setForeground(cor != null ? cor : UITemaGerard.COR_TEXTO);
                setFont(getFont().deriveFont(Font.BOLD));
                return componente;
            }
        });

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA));
        add(rolagem, BorderLayout.CENTER);

        agenteMonitor.adicionarOuvinte(this);
        agenteZDP.adicionarOuvinte(this);
        agenteModelador.adicionarOuvinte(this);
    }

    public void desconectar() {
        agenteMonitor.removerOuvinte(this);
        agenteZDP.removerOuvinte(this);
        agenteModelador.removerOuvinte(this);
    }

    @Override
    public void aoAvaliar(boolean correto) {
        adicionarLinha("Monitor", "veredito=" + (correto ? "CORRETO" : "ERRADO"));
    }

    @Override
    public void aoDecidir(String idUsuario, TipoSituacaoAditiva categoria, String chavePapelAlvo,
                           boolean correto, CamadaEstrategiaZDP estrategia) {
        String tarefa = (categoria == null ? "?" : categoria.name()) + ":" + chavePapelAlvo;
        adicionarLinha("ZDP", "estrategia=" + estrategia + " tarefa=" + tarefa + " correto=" + correto);
    }

    @Override
    public void aoArmazenar(String idUsuario, DiagnosticoTarefa diagnostico) {
        adicionarLinha("Modelador", "armazenou caso tarefa=" + diagnostico.getTarefa()
                + " suporte=" + diagnostico.getSuporte());
    }

    private void adicionarLinha(String agente, String detalhe) {
        modeloTabela.insertRow(0, new Object[]{formatoHora.format(new Date()), agente, detalhe});
    }
}
