package gerard.campoaditivo.curadoria;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.campoaditivo.situacao.DiagnosticoSituacao;
import gerard.dominio.campoaditivo.situacao.EventoNarrativoCurado;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.dominio.campoaditivo.situacao.StatusCuradoriaSituacao;
import gerard.ui.UITemaGerard;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 * Adaptador Swing para declarações narrativas nominais do pesquisador.
 * Nenhuma ligação semântica é derivada da posição de uma linha ou coluna.
 */
public final class DialogoCuradoriaNarrativaRica extends JDialog {
    private final SituacaoProblemaAditiva situacaoTabular;
    private final RepositorioCuradoriaNarrativaRica repositorio;
    private final MontadorCuradoriaNarrativaRica montador =
            new MontadorCuradoriaNarrativaRica();

    private final JTextField campoId = new JTextField();
    private final JTextArea campoContexto = new JTextArea(2, 70);
    private final JTextField ordemInicial = new JTextField();
    private final JTextField chaveInicial = new JTextField();
    private final JTextField ordemFinal = new JTextField();
    private final JTextField chaveFinal = new JTextField();
    private final JLabel status = new JLabel(" ");
    private final JCheckBox validadaPeloPesquisador = new JCheckBox(
            "Validada pelo pesquisador");

    private final ModeloLinhas participantes = new ModeloLinhas(
            "id participante", "nome exibido");
    private final ModeloLinhas familias = new ModeloLinhas(
            "id família", "nome conceitual");
    private final ModeloLinhas objetos = new ModeloLinhas(
            "id objeto", "id família", "chave visual abstrata",
            "características: chave=valor;...");
    private final ModeloLinhas estadoInicial = new ModeloLinhas(
            "id participante", "id objeto", "quantidade");
    private final ModeloLinhas eventos = new ModeloLinhas(
            "tipo", "ordem", "chave", "id origem", "id destino",
            "id objeto", "quantidade");
    private final ModeloLinhas estadoFinal = new ModeloLinhas(
            "id participante", "id objeto", "quantidade");
    private final ModeloLinhas correspondencias = new ModeloLinhas(
            "papel", "tipo da referência", "id participante",
            "id participante comparado", "id família",
            "chave do evento", "id objeto");
    private final List<JTable> tabelas = new ArrayList<JTable>();
    private boolean salvou;

    public DialogoCuradoriaNarrativaRica(
            Window dono,
            SituacaoProblemaAditiva situacaoTabular,
            RepositorioCuradoriaNarrativaRica repositorio) {
        super(dono, "Narrativa rica da situação-problema",
                Dialog.ModalityType.APPLICATION_MODAL);
        if (situacaoTabular == null || repositorio == null) {
            throw new IllegalArgumentException(
                    "situação tabular e repositório narrativo são obrigatórios");
        }
        this.situacaoTabular = situacaoTabular;
        this.repositorio = repositorio;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construirInterface();
        carregar();
    }

    public boolean exibir(java.awt.Component componenteReferencia) {
        pack();
        setMinimumSize(new Dimension(980, 660));
        if (componenteReferencia == null) {
            setLocationRelativeTo(getOwner());
        } else {
            setLocationRelativeTo(componenteReferencia);
        }
        setVisible(true);
        return salvou;
    }

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(UITemaGerard.COR_FUNDO_CONTEUDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topo = new JPanel(new BorderLayout(6, 6));
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Declarações da narrativa rica");
        titulo.setFont(UITemaGerard.FONTE_TITULO_SUBMENU);
        titulo.setForeground(UITemaGerard.COR_TEXTO);
        topo.add(titulo, BorderLayout.NORTH);
        JTextArea explicacao = new JTextArea(
                "Declare nominalmente participantes, famílias, objetos, estados, eventos e "
                + "correspondências. Nada será inferido dos campos personagem, do texto, "
                + "da ordem das linhas ou da posição visual.");
        explicacao.setEditable(false);
        explicacao.setFocusable(false);
        explicacao.setLineWrap(true);
        explicacao.setWrapStyleWord(true);
        explicacao.setOpaque(false);
        explicacao.setFont(UITemaGerard.FONTE_TEXTO_SUBMENU);
        explicacao.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        topo.add(explicacao, BorderLayout.CENTER);
        topo.add(criarCabecalho(), BorderLayout.SOUTH);
        raiz.add(topo, BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(UITemaGerard.FONTE_TEXTO_SUBMENU);
        abas.addTab("Participantes", painelTabela(participantes,
                "Pessoas, grupos, animais ou outros participantes, com identidade explícita."));
        abas.addTab("Famílias", painelTabela(familias,
                "Famílias conceituais de objetos contáveis, como morangos, rosas ou bilas."));
        abas.addTab("Objetos", painelTabela(objetos,
                "Objetos contados e suas características. A chave visual é abstrata e não contém caminho de imagem."));
        abas.addTab("Estado inicial", painelTabela(estadoInicial,
                "Inventários iniciais. Para declarar inventário vazio, informe apenas o participante."));
        abas.addTab("Eventos", painelTabela(eventos,
                "Mudanças quantitativas da história, em ordem temporal explícita."));
        abas.addTab("Estado final", painelTabela(estadoFinal,
                "Inventários finais declarados pelo pesquisador; não são recalculados pela tela."));
        abas.addTab("Correspondências", painelTabela(correspondencias,
                "Vínculos nominais entre cada papel formal e um fato da narrativa."));
        configurarEditoresEnumerados();
        raiz.add(abas, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout(8, 0));
        rodape.setOpaque(false);
        validadaPeloPesquisador.setOpaque(false);
        validadaPeloPesquisador.setFont(UITemaGerard.FONTE_TEXTO_SUBMENU);
        validadaPeloPesquisador.setForeground(UITemaGerard.COR_TEXTO);
        validadaPeloPesquisador.setToolTipText(
                "Promove esta representação rica somente após revisão humana.");
        validadaPeloPesquisador.getAccessibleContext().setAccessibleName(
                "Validar narrativa rica pelo pesquisador");
        validadaPeloPesquisador.getAccessibleContext().setAccessibleDescription(
                "Marca a narrativa rica como validada depois da revisão humana.");
        rodape.add(validadaPeloPesquisador, BorderLayout.WEST);
        status.setFont(UITemaGerard.FONTE_TEXTO_SUBMENU);
        status.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        rodape.add(status, BorderLayout.CENTER);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botoes.setOpaque(false);
        JButton cancelar = new JButton("Cancelar");
        JButton salvar = new JButton("Salvar narrativa rica");
        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> salvar());
        cancelar.getAccessibleContext().setAccessibleName(
                "Cancelar edição da narrativa rica");
        salvar.getAccessibleContext().setAccessibleName(
                "Salvar narrativa rica");
        botoes.add(cancelar);
        botoes.add(salvar);
        rodape.add(botoes, BorderLayout.EAST);
        raiz.add(rodape, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private JPanel criarCabecalho() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        adicionarCampo(painel, gbc, 0, 0, "id da situação", campoId);
        campoId.setEditable(false);
        campoId.setBackground(UITemaGerard.COR_CAMPO_DESABILITADO);
        adicionarCampo(painel, gbc, 0, 1, "contexto narrativo", campoContexto);
        campoContexto.setLineWrap(true);
        campoContexto.setWrapStyleWord(true);
        JScrollPane rolagem = new JScrollPane(campoContexto);
        substituirComponente(painel, campoContexto, rolagem, gbc, 1);

        JPanel marcadores = new JPanel(new GridBagLayout());
        marcadores.setOpaque(false);
        GridBagConstraints m = new GridBagConstraints();
        m.insets = new Insets(3, 3, 3, 3);
        m.anchor = GridBagConstraints.WEST;
        m.fill = GridBagConstraints.HORIZONTAL;
        adicionarCampo(marcadores, m, 0, 0,
                "ordem inicial", ordemInicial);
        adicionarCampo(marcadores, m, 0, 1,
                "chave inicial", chaveInicial);
        adicionarCampo(marcadores, m, 2, 0,
                "ordem final", ordemFinal);
        adicionarCampo(marcadores, m, 2, 1,
                "chave final", chaveFinal);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        painel.add(marcadores, gbc);
        return painel;
    }

    private static void adicionarCampo(
            JPanel painel,
            GridBagConstraints gbc,
            int coluna,
            int linha,
            String rotulo,
            java.awt.Component campo) {
        JLabel label = new JLabel(rotulo);
        label.setFont(UITemaGerard.FONTE_TEXTO_SUBMENU);
        label.setForeground(UITemaGerard.COR_TEXTO);
        gbc.gridx = coluna;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        painel.add(label, gbc);
        gbc.gridx = coluna + 1;
        gbc.weightx = 1;
        painel.add(campo, gbc);
    }

    private static void substituirComponente(
            JPanel painel,
            java.awt.Component antigo,
            java.awt.Component novo,
            GridBagConstraints gbc,
            int linha) {
        painel.remove(antigo);
        gbc.gridx = 1;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        painel.add(novo, gbc);
    }

    private JPanel painelTabela(ModeloLinhas modelo, String explicacao) {
        JPanel painel = new JPanel(new BorderLayout(6, 6));
        painel.setBackground(UITemaGerard.COR_SUPERFICIE);
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JLabel texto = new JLabel(explicacao);
        texto.setFont(UITemaGerard.FONTE_TEXTO_SUBMENU);
        texto.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        painel.add(texto, BorderLayout.NORTH);

        JTable tabela = new JTable(modelo);
        tabela.setFont(UITemaGerard.FONTE_TEXTO_SUBMENU);
        tabela.getTableHeader().setFont(UITemaGerard.FONTE_TEXTO_SUBMENU);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabelas.add(tabela);
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA));
        painel.add(rolagem, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botoes.setOpaque(false);
        JButton adicionar = new JButton("Adicionar linha");
        JButton remover = new JButton("Remover seleção");
        adicionar.addActionListener(e -> modelo.adicionarVazia());
        remover.addActionListener(e -> removerSelecao(tabela, modelo));
        adicionar.getAccessibleContext().setAccessibleName(
                "Adicionar linha em " + explicacao);
        remover.getAccessibleContext().setAccessibleName(
                "Remover linhas selecionadas em " + explicacao);
        botoes.add(adicionar);
        botoes.add(remover);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private void configurarEditoresEnumerados() {
        JTable tabelaEventos = tabelaDe(eventos);
        JComboBox<EventoNarrativoCurado.Tipo> tiposEventos =
                new JComboBox<EventoNarrativoCurado.Tipo>(
                        EventoNarrativoCurado.Tipo.values());
        tabelaEventos.getColumnModel().getColumn(0).setCellEditor(
                new DefaultCellEditor(tiposEventos));

        JTable tabelaCorrespondencias = tabelaDe(correspondencias);
        JComboBox<ReferenciaValorNarrativo.Tipo> tiposReferencias =
                new JComboBox<ReferenciaValorNarrativo.Tipo>(
                        ReferenciaValorNarrativo.Tipo.values());
        tabelaCorrespondencias.getColumnModel().getColumn(1).setCellEditor(
                new DefaultCellEditor(tiposReferencias));
    }

    private JTable tabelaDe(ModeloLinhas modelo) {
        for (JTable tabela : tabelas) {
            if (tabela.getModel() == modelo) return tabela;
        }
        throw new IllegalStateException("tabela do modelo não localizada");
    }

    private static void removerSelecao(
            JTable tabela, ModeloLinhas modelo) {
        int[] linhas = tabela.getSelectedRows();
        for (int i = linhas.length - 1; i >= 0; i--) {
            modelo.remover(tabela.convertRowIndexToModel(linhas[i]));
        }
    }

    private void carregar() {
        campoId.setText(situacaoTabular.getId());
        try {
            Optional<RegistroCuradoriaNarrativaRica> existente =
                    repositorio.carregar(situacaoTabular.getId());
            if (existente.isPresent()) {
                RegistroCuradoriaNarrativaRica registro = existente.get();
                preencher(montador.decompor(registro));
                validadaPeloPesquisador.setSelected(
                        registro.getStatusCuradoria()
                        == StatusCuradoriaSituacao.VALIDADA_PELO_PESQUISADOR);
                status.setText("Narrativa rica existente carregada para revisão humana.");
            } else {
                validadaPeloPesquisador.setSelected(false);
                campoContexto.setText(situacaoTabular.getContexto());
                status.setText("Ainda não existe narrativa rica para esta situação.");
            }
        } catch (IOException | IllegalArgumentException ex) {
            status.setText("Não foi possível ler a narrativa rica existente.");
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar a narrativa rica: " + ex.getMessage(),
                    "Curadoria narrativa",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencher(RascunhoCuradoriaNarrativaRica rascunho) {
        campoId.setText(rascunho.getIdSituacao());
        campoContexto.setText(rascunho.getContexto());
        ordemInicial.setText(rascunho.getOrdemEstadoInicial());
        chaveInicial.setText(rascunho.getChaveEstadoInicial());
        ordemFinal.setText(rascunho.getOrdemEstadoFinal());
        chaveFinal.setText(rascunho.getChaveEstadoFinal());
        for (RascunhoCuradoriaNarrativaRica.Participante linha
                : rascunho.getParticipantes()) {
            participantes.adicionar(linha.getId(), linha.getNome());
        }
        for (RascunhoCuradoriaNarrativaRica.Familia linha
                : rascunho.getFamilias()) {
            familias.adicionar(linha.getId(), linha.getNome());
        }
        for (RascunhoCuradoriaNarrativaRica.Objeto linha
                : rascunho.getObjetos()) {
            objetos.adicionar(
                    linha.getId(), linha.getFamiliaId(),
                    linha.getChaveVisual(), linha.getCaracteristicas());
        }
        preencherEstado(estadoInicial, rascunho.getEstadoInicial());
        for (RascunhoCuradoriaNarrativaRica.Evento linha
                : rascunho.getEventos()) {
            eventos.adicionar(
                    linha.getTipo(), linha.getOrdem(), linha.getChave(),
                    linha.getOrigemId(), linha.getDestinoId(),
                    linha.getObjetoId(), linha.getQuantidade());
        }
        preencherEstado(estadoFinal, rascunho.getEstadoFinal());
        for (RascunhoCuradoriaNarrativaRica.Correspondencia linha
                : rascunho.getCorrespondencias()) {
            correspondencias.adicionar(
                    linha.getPapel(), linha.getTipo(),
                    linha.getParticipanteId(),
                    linha.getParticipanteComparadoId(),
                    linha.getFamiliaId(), linha.getEventoChave(),
                    linha.getObjetoId());
        }
    }

    private static void preencherEstado(
            ModeloLinhas modelo,
            List<RascunhoCuradoriaNarrativaRica.ItemEstado> linhas) {
        for (RascunhoCuradoriaNarrativaRica.ItemEstado linha : linhas) {
            modelo.adicionar(
                    linha.getParticipanteId(),
                    linha.getObjetoId(),
                    linha.getQuantidade());
        }
    }

    private void salvar() {
        encerrarEdicoes();
        try {
            RegistroCuradoriaNarrativaRica candidata =
                    montador.montar(coletar());
            ResultadoConversaoSituacaoProblemaRica resultado =
                    new ConversorSituacaoProblemaRica().converter(
                            situacaoTabular,
                            candidata.getNarrativa(),
                            candidata.getCorrespondencias());
            if (validadaPeloPesquisador.isSelected()
                    && !resultado.ehValida()) {
                status.setText(
                        "A promoção foi bloqueada pelos diagnósticos da situação.");
                JOptionPane.showMessageDialog(
                        this,
                        mensagemDiagnosticos(resultado,
                                "A narrativa não pode ser validada enquanto houver diagnósticos:"),
                        "Validação da narrativa rica",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!resultado.ehValida()
                    && !confirmarPersistenciaDaCandidata(resultado)) {
                status.setText(
                        "A narrativa permanece aberta para revisão do pesquisador.");
                return;
            }
            StatusCuradoriaSituacao statusEscolhido =
                    validadaPeloPesquisador.isSelected()
                    ? StatusCuradoriaSituacao.VALIDADA_PELO_PESQUISADOR
                    : StatusCuradoriaSituacao.CANDIDATA_NAO_CURADA;
            RegistroCuradoriaNarrativaRica registro =
                    new RegistroCuradoriaNarrativaRica(
                            candidata.getIdSituacao(), statusEscolhido,
                            candidata.getNarrativa(),
                            candidata.getCorrespondencias());
            repositorio.salvar(registro);
            salvou = true;
            dispose();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            status.setText("Revise as declarações obrigatórias.");
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Narrativa rica incompleta",
                    JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            status.setText("A narrativa rica não foi salva.");
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao salvar a narrativa rica: " + ex.getMessage(),
                    "Curadoria narrativa",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean confirmarPersistenciaDaCandidata(
            ResultadoConversaoSituacaoProblemaRica resultado) {
        StringBuilder mensagem = new StringBuilder(mensagemDiagnosticos(
                resultado,
                "A declaração foi preservada, mas ainda possui diagnósticos:"));
        mensagem.append(
                "\nDeseja salvá-la como candidata para revisão humana, sem correção automática?");
        return JOptionPane.showConfirmDialog(
                this,
                mensagem.toString(),
                "Salvar candidata diagnosticada",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private static String mensagemDiagnosticos(
            ResultadoConversaoSituacaoProblemaRica resultado,
            String introducao) {
        StringBuilder mensagem = new StringBuilder(introducao).append("\n\n");
        for (DiagnosticoSituacao diagnostico : resultado.getDiagnosticos()) {
            mensagem.append("• ").append(diagnostico.toString()).append('\n');
        }
        return mensagem.toString();
    }

    private void encerrarEdicoes() {
        for (JTable tabela : tabelas) {
            if (tabela.isEditing()) tabela.getCellEditor().stopCellEditing();
        }
    }

    private RascunhoCuradoriaNarrativaRica coletar() {
        List<RascunhoCuradoriaNarrativaRica.Participante> listaParticipantes =
                new ArrayList<>();
        for (String[] linha : participantes.copiarLinhas()) {
            listaParticipantes.add(
                    new RascunhoCuradoriaNarrativaRica.Participante(
                            linha[0], linha[1]));
        }
        List<RascunhoCuradoriaNarrativaRica.Familia> listaFamilias =
                new ArrayList<>();
        for (String[] linha : familias.copiarLinhas()) {
            listaFamilias.add(new RascunhoCuradoriaNarrativaRica.Familia(
                    linha[0], linha[1]));
        }
        List<RascunhoCuradoriaNarrativaRica.Objeto> listaObjetos =
                new ArrayList<>();
        for (String[] linha : objetos.copiarLinhas()) {
            listaObjetos.add(new RascunhoCuradoriaNarrativaRica.Objeto(
                    linha[0], linha[1], linha[2], linha[3]));
        }
        List<RascunhoCuradoriaNarrativaRica.Evento> listaEventos =
                new ArrayList<>();
        for (String[] linha : eventos.copiarLinhas()) {
            listaEventos.add(new RascunhoCuradoriaNarrativaRica.Evento(
                    linha[0], linha[1], linha[2], linha[3],
                    linha[4], linha[5], linha[6]));
        }
        List<RascunhoCuradoriaNarrativaRica.Correspondencia>
                listaCorrespondencias = new ArrayList<>();
        for (String[] linha : correspondencias.copiarLinhas()) {
            listaCorrespondencias.add(
                    new RascunhoCuradoriaNarrativaRica.Correspondencia(
                            linha[0], linha[1], linha[2], linha[3],
                            linha[4], linha[5], linha[6]));
        }
        return new RascunhoCuradoriaNarrativaRica(
                campoId.getText(), campoContexto.getText(),
                ordemInicial.getText(), chaveInicial.getText(),
                ordemFinal.getText(), chaveFinal.getText(),
                listaParticipantes, listaFamilias, listaObjetos,
                coletarEstado(estadoInicial), listaEventos,
                coletarEstado(estadoFinal), listaCorrespondencias);
    }

    private static List<RascunhoCuradoriaNarrativaRica.ItemEstado>
            coletarEstado(ModeloLinhas modelo) {
        List<RascunhoCuradoriaNarrativaRica.ItemEstado> resultado =
                new ArrayList<>();
        for (String[] linha : modelo.copiarLinhas()) {
            resultado.add(new RascunhoCuradoriaNarrativaRica.ItemEstado(
                    linha[0], linha[1], linha[2]));
        }
        return resultado;
    }

    static final class ModeloLinhas extends AbstractTableModel {
        private final String[] colunas;
        private final List<String[]> linhas = new ArrayList<String[]>();

        ModeloLinhas(String... colunas) {
            this.colunas = colunas.clone();
        }

        void adicionarVazia() {
            adicionar(new String[colunas.length]);
        }

        void adicionar(String... valores) {
            String[] linha = new String[colunas.length];
            for (int i = 0; i < linha.length; i++) {
                linha[i] = i < valores.length && valores[i] != null
                        ? valores[i] : "";
            }
            linhas.add(linha);
            int indice = linhas.size() - 1;
            fireTableRowsInserted(indice, indice);
        }

        void remover(int indice) {
            if (indice < 0 || indice >= linhas.size()) return;
            linhas.remove(indice);
            fireTableRowsDeleted(indice, indice);
        }

        List<String[]> copiarLinhas() {
            List<String[]> copia = new ArrayList<>();
            for (String[] linha : linhas) copia.add(linha.clone());
            return copia;
        }

        public int getRowCount() { return linhas.size(); }
        public int getColumnCount() { return colunas.length; }
        public String getColumnName(int coluna) { return colunas[coluna]; }
        public boolean isCellEditable(int linha, int coluna) { return true; }
        public Object getValueAt(int linha, int coluna) {
            return linhas.get(linha)[coluna];
        }
        public void setValueAt(Object valor, int linha, int coluna) {
            linhas.get(linha)[coluna] = valor == null ? "" : valor.toString();
            fireTableCellUpdated(linha, coluna);
        }
    }
}
