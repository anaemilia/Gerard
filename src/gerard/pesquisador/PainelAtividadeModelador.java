package gerard.pesquisador;

import gerard.agente.modelador.AgenteModelador;
import gerard.agente.modelador.OuvinteCasoAgenteModelador;
import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.ui.UITemaGerard;
import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Atividade factual do Modelador exibida exclusivamente à pesquisadora. */
public final class PainelAtividadeModelador extends JPanel
        implements OuvinteCasoAgenteModelador {

    private final AgenteModelador agenteModelador;
    private final DefaultTableModel modeloTabela;
    private final SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");

    public PainelAtividadeModelador(AgenteModelador agenteModelador) {
        super(new BorderLayout(10, 10));
        if (agenteModelador == null) {
            throw new IllegalArgumentException("Modelador é obrigatório");
        }
        this.agenteModelador = agenteModelador;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 10, 10, 10));

        JLabel subtitulo = new JLabel(
                "Casos factuais recebidos pelo Modelador durante o uso do Gérard.");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        add(subtitulo, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(
                new Object[]{"Hora", "Componente", "Detalhe"}, 0) {
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

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA));
        add(rolagem, BorderLayout.CENTER);
        agenteModelador.adicionarOuvinte(this);
    }

    public void desconectar() {
        agenteModelador.removerOuvinte(this);
    }

    @Override
    public void aoArmazenar(String idUsuario, DiagnosticoTarefa diagnostico) {
        modeloTabela.insertRow(0, new Object[]{
            formatoHora.format(new Date()),
            "Modelador",
            "armazenou caso tarefa=" + diagnostico.getTarefa()
                    + " suporte=" + diagnostico.getSuporte()
        });
    }
}
