package gerard.ui.usuario;

import gerard.agente.modelousuario.Genero;
import gerard.agente.modelousuario.MidiaPreferida;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelEscolaridade;
import gerard.agente.modelousuario.PerfilAluno;
import gerard.agente.modelousuario.PerfilAprendizagem;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import gerard.i18n.ServicoLocalizacao;
import gerard.ui.UITemaGerard;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
 * loggerInteracaoGerard.getUsuarioAtual() (seleção local de ajuda e Agente Modelador via
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
    // MidiaPreferida possui exatamente um valor. Botões de opção tornam essa
    // exclusividade explícita, inclusive para tecnologias assistivas.
    private final JRadioButton campoMidiaSom = new JRadioButton();
    private final JRadioButton campoMidiaGrafico = new JRadioButton();
    private final JRadioButton campoMidiaLinguagemNatural = new JRadioButton();
    private final JRadioButton campoMidiaVideo = new JRadioButton();
    private final JRadioButton campoMidiaHistoriaEmQuadrinhos = new JRadioButton();
    private final JComboBox<NivelEscolaridade> campoEscolaridade =
            new JComboBox<NivelEscolaridade>(NivelEscolaridade.values());
    private final JLabel rotuloPreviewFoto = new JLabel();

    private File fotoSelecionada;
    private String idSelecionado;
    private final ModeloUsuario perfilEmEdicao;
    // Diferente de perfilEmEdicao (modo de edição dedicado, sem lista — ver
    // construtor de 3 argumentos): este campo é preenchido ao clicar num
    // nome na lista "Usuários cadastrados" da tela inicial de dois painéis,
    // só para pré-visualizar os dados no formulário à direita (pedido da
    // usuária, 2026-07-29). Também usado por cadastrar() para decidir se o
    // clique em botaoCadastrar deve atualizar esse perfil em vez de criar um
    // duplicado — ver atualizarTextoBotaoCadastrar().
    private ModeloUsuario perfilPreVisualizado;
    private JButton botaoCadastrar;

    public DialogoUsuario(Frame proprietario, RepositorioModeloUsuario repositorio) {
        this(proprietario, repositorio, null);
    }

    /**
     * Modo de edição: quando perfilParaEditar não é null, o diálogo abre já
     * preenchido com os dados desse perfil, oculta a lista de "usuários
     * cadastrados" (trocar de usuário não é o objetivo aqui) e o botão salva
     * no mesmo id em vez de cadastrar um perfil novo — aberto ao clicar no
     * nome do usuário logado na barra superior (ver
     * Main.criarBotaoUsuarioMenuBar).
     */
    public DialogoUsuario(Frame proprietario, RepositorioModeloUsuario repositorio, ModeloUsuario perfilParaEditar) {
        super(proprietario, ServicoLocalizacao.getInstancia().texto("ui.userDialog.title"), true);
        this.repositorio = repositorio;
        this.perfilEmEdicao = perfilParaEditar;
        getContentPane().setBackground(UITemaGerard.COR_FUNDO_CONTEUDO);
        montarInterface();
        if (perfilEmEdicao != null) {
            preencherCampos(perfilEmEdicao);
        } else {
            carregarUsuarios();
        }
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

        boolean editando = perfilEmEdicao != null;
        JLabel titulo = new JLabel(localizacao.texto(editando ? "ui.userDialog.editSubtitle" : "ui.userDialog.subtitle"));
        titulo.setFont(new Font("Arial", Font.PLAIN, 13));
        titulo.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        add(titulo, BorderLayout.NORTH);

        if (editando) {
            JPanel envoltorio = new JPanel(new BorderLayout());
            envoltorio.setOpaque(false);
            envoltorio.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
            envoltorio.add(criarPainelCadastro(), BorderLayout.CENTER);
            add(envoltorio, BorderLayout.CENTER);
        } else {
            JSplitPane divisor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, criarPainelExistentes(), criarPainelCadastro());
            divisor.setResizeWeight(0.42);
            divisor.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
            divisor.setOpaque(false);
            add(divisor, BorderLayout.CENTER);
        }

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
        listaUsuarios.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ModeloUsuario selecionado = listaUsuarios.getSelectedValue();
                perfilPreVisualizado = selecionado;
                if (selecionado != null) {
                    preencherCampos(selecionado);
                }
                atualizarTextoBotaoCadastrar();
            }
        });
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

        JLabel rotulo = new JLabel(localizacao.texto(
                perfilEmEdicao != null ? "ui.userDialog.editUser" : "ui.userDialog.newUser"));
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

        campoMidiaSom.setText(localizacao.texto(chaveMidia(MidiaPreferida.SOM)));
        campoMidiaGrafico.setText(localizacao.texto(chaveMidia(MidiaPreferida.GRAFICO)));
        campoMidiaLinguagemNatural.setText(localizacao.texto(chaveMidia(MidiaPreferida.LINGUAGEM_NATURAL)));
        campoMidiaVideo.setText(localizacao.texto(chaveMidia(MidiaPreferida.VIDEO)));
        campoMidiaHistoriaEmQuadrinhos.setText(
                localizacao.texto(chaveMidia(MidiaPreferida.HISTORIA_EM_QUADRINHOS)));
        campoMidiaSom.setSelected(true);
        final JRadioButton[] opcoesMidia = {
            campoMidiaSom,
            campoMidiaGrafico,
            campoMidiaLinguagemNatural,
            campoMidiaVideo,
            campoMidiaHistoriaEmQuadrinhos
        };
        ButtonGroup grupoMidia = new ButtonGroup();
        for (JRadioButton opcao : opcoesMidia) {
            grupoMidia.add(opcao);
            opcao.setOpaque(false);
            opcao.setFont(new Font("Arial", Font.PLAIN, 13));
            opcao.setForeground(UITemaGerard.COR_TEXTO);
            opcao.setFocusPainted(false);
            opcao.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        JPanel painelMidia = new JPanel();
        painelMidia.setOpaque(false);
        painelMidia.setLayout(new BoxLayout(painelMidia, BoxLayout.Y_AXIS));
        for (JRadioButton opcao : opcoesMidia) {
            painelMidia.add(opcao);
        }
        campos.add(criarLinhaCampo(localizacao.texto("ui.userDialog.media"), painelMidia));
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

        botaoCadastrar = criarBotaoPrimario(localizacao.texto(
                perfilEmEdicao != null ? "ui.userDialog.save" : "ui.userDialog.register"));
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
        MidiaPreferida midia = campoMidiaGrafico.isSelected() ? MidiaPreferida.GRAFICO
                : campoMidiaLinguagemNatural.isSelected() ? MidiaPreferida.LINGUAGEM_NATURAL
                : campoMidiaVideo.isSelected() ? MidiaPreferida.VIDEO
                : campoMidiaHistoriaEmQuadrinhos.isSelected() ? MidiaPreferida.HISTORIA_EM_QUADRINHOS
                : MidiaPreferida.SOM;
        NivelEscolaridade escolaridade = (NivelEscolaridade) campoEscolaridade.getSelectedItem();

        ModeloUsuario perfilAlvoAtualizacao = perfilEmEdicao != null ? perfilEmEdicao : perfilPreVisualizado;
        if (perfilAlvoAtualizacao != null) {
            idSelecionado = perfilAlvoAtualizacao.getPerfilAluno().getId();
            repositorio.atualizarPerfil(idSelecionado, nome, idade, sexo, midia, escolaridade, fotoSelecionada);
        } else {
            idSelecionado = repositorio.cadastrarPerfil(nome, idade, sexo, midia, escolaridade, fotoSelecionada);
        }
        dispose();
    }

    /**
     * botaoCadastrar diz "Salvar" (e o clique atualiza, não duplica) sempre
     * que os campos estão mostrando um perfil já existente — seja o modo de
     * edição dedicado (perfilEmEdicao) ou uma pré-visualização por seleção
     * na lista (perfilPreVisualizado). Sem isso, clicar o botão depois de só
     * navegar pela lista criaria um cadastro duplicado (foi o que gerou
     * "ana_emilia_2" em ~/Gerard/perfis_usuario.tsv).
     */
    private void atualizarTextoBotaoCadastrar() {
        if (botaoCadastrar == null || perfilEmEdicao != null) {
            return;
        }
        botaoCadastrar.setText(localizacao.texto(
                perfilPreVisualizado != null ? "ui.userDialog.save" : "ui.userDialog.register"));
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

    /**
     * Preenche o formulário de cadastro com os dados de um perfil existente
     * — usado tanto no modo de edição dedicado (perfilEmEdicao, construtor)
     * quanto na pré-visualização por seleção na lista da tela inicial (ver
     * listener em criarPainelExistentes). A foto existente só aparece como
     * preview — fotoSelecionada continua null até o usuário escolher um
     * arquivo novo, para não recopiar a mesma foto a cada Salvar (ver
     * cadastrar(), que só passa fotoOrigem != null quando uma foto nova foi
     * escolhida).
     */
    private void preencherCampos(ModeloUsuario modelo) {
        PerfilAluno aluno = modelo.getPerfilAluno();
        PerfilAprendizagem aprendizagem = modelo.getPerfilAprendizagem();

        campoNome.setText(aluno.getNome() == null ? "" : aluno.getNome());
        campoIdade.setValue(aluno.getIdade() == null ? 30 : aluno.getIdade());
        if (aluno.getSexo() != null) {
            campoSexo.setSelectedItem(aluno.getSexo());
        }

        MidiaPreferida midia = aprendizagem.getMidiaPreferida();
        campoMidiaGrafico.setSelected(midia == MidiaPreferida.GRAFICO);
        campoMidiaLinguagemNatural.setSelected(midia == MidiaPreferida.LINGUAGEM_NATURAL);
        campoMidiaVideo.setSelected(midia == MidiaPreferida.VIDEO);
        campoMidiaHistoriaEmQuadrinhos.setSelected(midia == MidiaPreferida.HISTORIA_EM_QUADRINHOS);
        campoMidiaSom.setSelected(midia != MidiaPreferida.GRAFICO && midia != MidiaPreferida.LINGUAGEM_NATURAL
                && midia != MidiaPreferida.VIDEO && midia != MidiaPreferida.HISTORIA_EM_QUADRINHOS);

        if (aprendizagem.getNivelEscolaridade() != null) {
            campoEscolaridade.setSelectedItem(aprendizagem.getNivelEscolaridade());
        }

        // Reseta fotoSelecionada e o preview a cada troca de perfil mostrado
        // — sem isso, trocar de um usuário com foto para um sem foto (ou
        // vice-versa) deixaria a miniatura de um "vazando" para o outro.
        fotoSelecionada = null;
        ImageIcon miniatura = aluno.getFotoCaminho() == null ? null : carregarMiniatura(new File(aluno.getFotoCaminho()));
        if (miniatura != null) {
            rotuloPreviewFoto.setText("");
            rotuloPreviewFoto.setIcon(miniatura);
        } else {
            rotuloPreviewFoto.setIcon(null);
            rotuloPreviewFoto.setText(localizacao.texto("ui.userDialog.photo.placeholder"));
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
            case VIDEO: return "ui.userDialog.media.video";
            case HISTORIA_EM_QUADRINHOS: return "ui.userDialog.media.historiaEmQuadrinhos";
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
