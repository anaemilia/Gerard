package gerard.ui.usuario;

import gerard.agente.modelousuario.Genero;
import gerard.agente.modelousuario.MidiaPreferida;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelEscolaridade;
import gerard.agente.modelousuario.PerfilAluno;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.i18n.ServicoLocalizacao;
import gerard.ui.UITemaGerard;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Diálogo modal de seleção/cadastro de usuário — dimensões 3 (Perfil do
 * aluno) e 4 (Perfil da aprendizagem) do Modelo do Usuário (Quadro 5.60, ver
 * gerard-modelo-usuario/SKILL.md). Adicionado em 2026-07-22.
 *
 * Suporta várias pessoas usando o mesmo app (decisão explícita do usuário):
 * ao selecionar um perfil existente ou cadastrar um novo, devolve o id
 * escolhido. O chamador deve repassar esse id para
 * LoggerInteracaoGerard.definirUsuario — os pontos que já leem
 * loggerInteracaoGerard.getUsuarioAtual() (Agente ZDP via
 * AgenteZDP.decidirEstrategia, Agente Modelador via
 * ConectorVereditoModelador) passam a usar esse id automaticamente, sem
 * nenhuma outra mudança.
 *
 * Só as dimensões 3 e 4 são coletadas aqui — as dimensões 1/2 (nível de
 * tarefa, domínio por categoria) são inferidas pelo uso, não digitadas; a
 * dimensão 5 (diagnóstico da tarefa) é escrita pelo Agente Modelador durante
 * o jogo, não neste cadastro.
 */
public final class DialogoUsuario extends JDialog {
    private final ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
    private final RepositorioModeloUsuario repositorio;

    private final DefaultListModel<ModeloUsuario> modeloLista = new DefaultListModel<ModeloUsuario>();
    private final JList<ModeloUsuario> listaUsuarios = new JList<ModeloUsuario>(modeloLista);

    private final JTextField campoNome = new JTextField(18);
    private final JSpinner campoIdade = new JSpinner(new SpinnerNumberModel(30, 0, 120, 1));
    private final JComboBox<Genero> campoSexo = new JComboBox<Genero>(Genero.values());
    private final JComboBox<MidiaPreferida> campoMidia = new JComboBox<MidiaPreferida>(MidiaPreferida.values());
    private final JComboBox<NivelEscolaridade> campoEscolaridade =
            new JComboBox<NivelEscolaridade>(NivelEscolaridade.values());
    private final JLabel rotuloPreviewFoto = new JLabel();

    private File fotoSelecionada;
    private String idSelecionado;

    public DialogoUsuario(Frame proprietario, RepositorioModeloUsuario repositorio) {
        super(proprietario, ServicoLocalizacao.getInstancia().texto("ui.userDialog.title"), true);
        this.repositorio = repositorio;
        getContentPane().setBackground(UITemaGerard.COR_FUNDO_CONTEUDO);
        montarInterface();
        carregarUsuarios();
        setMinimumSize(new Dimension(680, 480));
        setSize(680, 560);
        setLocationRelativeTo(proprietario);
    }

    /** Mostra o diálogo (bloqueante) e devolve o id escolhido, ou null se cancelado/fechado sem escolha. */
    public String mostrarESelecionar() {
        setVisible(true);
        return idSelecionado;
    }

    private void montarInterface() {
        setLayout(new BorderLayout(0, 0));

        JLabel titulo = new JLabel(localizacao.texto("ui.userDialog.subtitle"));
        titulo.setFont(new Font("Arial", Font.PLAIN, 13));
        titulo.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        add(titulo, BorderLayout.NORTH);

        JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, criarPainelExistentes(), criarPainelCadastro());
        divisor.setResizeWeight(0.42);
        divisor.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
        divisor.setOpaque(false);
        add(divisor, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        rodape.setOpaque(false);
        JButton botaoCancelar = criarBotaoSecundario(localizacao.texto("ui.userDialog.cancel"));
        botaoCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                idSelecionado = null;
                dispose();
            }
        });
        rodape.add(botaoCancelar);
        add(rodape, BorderLayout.SOUTH);
    }

    private JPanel criarPainelExistentes() {
        JPanel painel = criarCard();
        painel.setLayout(new BorderLayout(0, 8));

        JLabel rotulo = new JLabel(localizacao.texto("ui.userDialog.existing"));
        rotulo.setFont(new Font("Arial", Font.BOLD, 14));
        rotulo.setForeground(UITemaGerard.COR_TEXTO);
        painel.add(rotulo, BorderLayout.NORTH);

        listaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaUsuarios.setBackground(UITemaGerard.COR_SUPERFICIE);
        listaUsuarios.setCellRenderer(new RenderizadorUsuario());
        painel.add(new JScrollPane(listaUsuarios), BorderLayout.CENTER);

        JButton botaoEntrar = criarBotaoPrimario(localizacao.texto("ui.userDialog.enter"));
        botaoEntrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ModeloUsuario selecionado = listaUsuarios.getSelectedValue();
                if (selecionado == null) {
                    JOptionPane.showMessageDialog(DialogoUsuario.this,
                            localizacao.texto("ui.userDialog.selectFirst"));
                    return;
                }
                idSelecionado = selecionado.getPerfilAluno().getId();
                dispose();
            }
        });
        JPanel rodapeLista = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        rodapeLista.setOpaque(false);
        rodapeLista.add(botaoEntrar);
        painel.add(rodapeLista, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelCadastro() {
        JPanel painel = criarCard();
        painel.setLayout(new BorderLayout(0, 10));

        JLabel rotulo = new JLabel(localizacao.texto("ui.userDialog.newUser"));
        rotulo.setFont(new Font("Arial", Font.BOLD, 14));
        rotulo.setForeground(UITemaGerard.COR_TEXTO);
        painel.add(rotulo, BorderLayout.NORTH);

        // Campos num painel à parte, dentro de JScrollPane: o cadastro tem
        // campos demais para caber sempre na altura fixa do diálogo (idioma
        // mais longo, DPI maior, fonte do SO) — sem isso, o rodapé do
        // diálogo (botão Cancelar) sobrepõe os últimos campos em vez de
        // simplesmente exigir rolagem.
        JPanel campos = new JPanel();
        campos.setOpaque(false);
        campos.setLayout(new BoxLayout(campos, BoxLayout.Y_AXIS));

        JPanel linhaNomeFoto = new JPanel(new BorderLayout(10, 0));
        linhaNomeFoto.setOpaque(false);
        linhaNomeFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaNomeFoto.setMaximumSize(new Dimension(420, 90));
        linhaNomeFoto.add(criarLinhaCampo(localizacao.texto("ui.userDialog.name"), campoNome), BorderLayout.CENTER);
        linhaNomeFoto.add(criarPainelFoto(), BorderLayout.EAST);
        campos.add(linhaNomeFoto);
        campos.add(Box.createVerticalStrut(6));
        campos.add(criarLinhaCampo(localizacao.texto("ui.userDialog.age"), campoIdade));
        campos.add(Box.createVerticalStrut(6));

        campoSexo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Genero) {
                    label.setText(localizacao.texto(chaveGenero((Genero) value)));
                }
                return label;
            }
        });
        campos.add(criarLinhaCampo(localizacao.texto("ui.userDialog.gender"), campoSexo));
        campos.add(Box.createVerticalStrut(6));

        campoMidia.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MidiaPreferida) {
                    label.setText(localizacao.texto(chaveMidia((MidiaPreferida) value)));
                }
                return label;
            }
        });
        campos.add(criarLinhaCampo(localizacao.texto("ui.userDialog.media"), campoMidia));
        campos.add(Box.createVerticalStrut(6));

        campoEscolaridade.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NivelEscolaridade) {
                    label.setText(localizacao.texto(chaveEscolaridade((NivelEscolaridade) value)));
                }
                return label;
            }
        });
        campos.add(criarLinhaCampo(localizacao.texto("ui.userDialog.schooling"), campoEscolaridade));
        campos.add(Box.createVerticalGlue());

        JScrollPane scrollCampos = new JScrollPane(campos);
        scrollCampos.setOpaque(false);
        scrollCampos.getViewport().setOpaque(false);
        scrollCampos.setBorder(BorderFactory.createEmptyBorder());
        scrollCampos.getVerticalScrollBar().setUnitIncrement(14);
        painel.add(scrollCampos, BorderLayout.CENTER);

        JButton botaoCadastrar = criarBotaoPrimario(localizacao.texto("ui.userDialog.register"));
        JPanel rodapeCadastro = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rodapeCadastro.setOpaque(false);
        rodapeCadastro.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        rodapeCadastro.add(botaoCadastrar);
        botaoCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrar();
            }
        });
        painel.add(rodapeCadastro, BorderLayout.SOUTH);

        return painel;
    }

    private void cadastrar() {
        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        if (nome.length() == 0) {
            JOptionPane.showMessageDialog(this, localizacao.texto("ui.userDialog.nameRequired"));
            return;
        }
        Integer idade = (Integer) campoIdade.getValue();
        Genero sexo = (Genero) campoSexo.getSelectedItem();
        MidiaPreferida midia = (MidiaPreferida) campoMidia.getSelectedItem();
        NivelEscolaridade escolaridade = (NivelEscolaridade) campoEscolaridade.getSelectedItem();

        idSelecionado = repositorio.cadastrarPerfil(nome, idade, sexo, midia, escolaridade, fotoSelecionada);
        dispose();
    }

    /** Espaço de upload de foto ao lado do nome — pedido do usuário em 2026-07-22 (não está no Quadro 5.60 original). */
    private JPanel criarPainelFoto() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        Dimension tamanho = new Dimension(72, 72);
        rotuloPreviewFoto.setText(localizacao.texto("ui.userDialog.photo.placeholder"));
        rotuloPreviewFoto.setFont(new Font("Arial", Font.PLAIN, 10));
        rotuloPreviewFoto.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        rotuloPreviewFoto.setHorizontalAlignment(SwingConstants.CENTER);
        rotuloPreviewFoto.setVerticalAlignment(SwingConstants.CENTER);
        rotuloPreviewFoto.setOpaque(true);
        rotuloPreviewFoto.setBackground(UITemaGerard.COR_SUPERFICIE_SUAVE);
        rotuloPreviewFoto.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA));
        rotuloPreviewFoto.setPreferredSize(tamanho);
        rotuloPreviewFoto.setMinimumSize(tamanho);
        rotuloPreviewFoto.setMaximumSize(tamanho);
        rotuloPreviewFoto.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botaoEscolherFoto = criarBotaoSecundario(localizacao.texto("ui.userDialog.photo.choose"));
        botaoEscolherFoto.setFont(new Font("Arial", Font.PLAIN, 11));
        botaoEscolherFoto.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        botaoEscolherFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        botaoEscolherFoto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                escolherFoto();
            }
        });

        painel.add(rotuloPreviewFoto);
        painel.add(Box.createVerticalStrut(6));
        painel.add(botaoEscolherFoto);
        return painel;
    }

    private void escolherFoto() {
        JFileChooser seletor = new JFileChooser();
        seletor.setFileFilter(new FileNameExtensionFilter(
                localizacao.texto("ui.userDialog.photo.filter"), "jpg", "jpeg", "png", "gif", "bmp"));
        int resultado = seletor.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File escolhido = seletor.getSelectedFile();
        ImageIcon miniatura = carregarMiniatura(escolhido);
        if (miniatura == null) {
            JOptionPane.showMessageDialog(this, localizacao.texto("ui.userDialog.photo.invalid"));
            return;
        }
        fotoSelecionada = escolhido;
        rotuloPreviewFoto.setText("");
        rotuloPreviewFoto.setIcon(miniatura);
    }

    private ImageIcon carregarMiniatura(File arquivo) {
        try {
            Image imagem = ImageIO.read(arquivo);
            if (imagem == null) {
                return null;
            }
            Image escalada = imagem.getScaledInstance(68, 68, Image.SCALE_SMOOTH);
            return new ImageIcon(escalada);
        } catch (IOException ex) {
            return null;
        }
    }

    private void carregarUsuarios() {
        modeloLista.clear();
        for (ModeloUsuario modelo : repositorio.listarPerfisCadastrados()) {
            modeloLista.addElement(modelo);
        }
    }

    private String chaveGenero(Genero genero) {
        switch (genero) {
            case MASCULINO: return "ui.userDialog.gender.masculino";
            case FEMININO: return "ui.userDialog.gender.feminino";
            default: return "ui.userDialog.gender.outro";
        }
    }

    private String chaveMidia(MidiaPreferida midia) {
        switch (midia) {
            case SOM: return "ui.userDialog.media.som";
            case GRAFICO: return "ui.userDialog.media.grafico";
            default: return "ui.userDialog.media.linguagemNatural";
        }
    }

    private String chaveEscolaridade(NivelEscolaridade nivel) {
        switch (nivel) {
            case PRIMEIRO_GRAU: return "ui.userDialog.schooling.primeiro";
            case SEGUNDO_GRAU: return "ui.userDialog.schooling.segundo";
            case GRADUACAO: return "ui.userDialog.schooling.graduacao";
            default: return "ui.userDialog.schooling.posGraduacao";
        }
    }

    private JPanel criarLinhaCampo(String rotuloTexto, JComponent campo) {
        JPanel linha = new JPanel();
        linha.setOpaque(false);
        linha.setLayout(new BoxLayout(linha, BoxLayout.Y_AXIS));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel rotulo = new JLabel(rotuloTexto);
        rotulo.setFont(new Font("Arial", Font.PLAIN, 12));
        rotulo.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(320, campo.getPreferredSize().height + 6));
        linha.add(rotulo);
        linha.add(Box.createVerticalStrut(2));
        linha.add(campo);
        return linha;
    }

    private JPanel criarCard() {
        JPanel painel = new JPanel();
        painel.setBackground(UITemaGerard.COR_SUPERFICIE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITemaGerard.COR_BORDA),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        return painel;
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
        botao.setForeground(Color.WHITE);
        botao.setBackground(UITemaGerard.COR_PRIMARIA);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 13, 8, 13));
        return botao;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(UITemaGerard.FONTE_BOTAO_MENU_PRINCIPAL);
        botao.setForeground(UITemaGerard.COR_TEXTO);
        botao.setBackground(UITemaGerard.COR_FUNDO_CONTEUDO);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITemaGerard.COR_BORDA),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        return botao;
    }

    private final class RenderizadorUsuario extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                        boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ModeloUsuario) {
                PerfilAluno perfil = ((ModeloUsuario) value).getPerfilAluno();
                label.setText(perfil.getNome());
            }
            label.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            if (!isSelected) {
                label.setBackground(UITemaGerard.COR_SUPERFICIE);
                label.setForeground(UITemaGerard.COR_TEXTO);
            }
            return label;
        }
    }
}
