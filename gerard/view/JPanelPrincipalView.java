/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.view;

import gerard.controller.ControllerInterface;
import gerard.model.GerardModelInterface;
import gerard.util.DesfazerRefazerObserver;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JSeparator;

public class JPanelPrincipalView extends javax.swing.JPanel implements DesfazerRefazerObserver {

    private GerardModelInterface model;
    private ControllerInterface controller;
    private Component component_pai;
    private boolean firstTime = true;
    private int estado = 0;//0 - ADITIVA 1 - MULTIPLICATIVA
    private JMenuItem jMenuItem_teoria;
    private JMenuItem jMenuItem_gerard;
    private boolean flagVisitante = true;

    public JPanelPrincipalView(Component component_pai, ControllerInterface controller, GerardModelInterface model) {
        this.component_pai = component_pai;
        this.controller = controller;
        this.model = model;

        this.model.registerObserver((DesfazerRefazerObserver) this);
        initComponents();
        this.menuBar = criarMenus();

        addBotoesListener();

    }

    public void modoMultiplicativas() {
        if (estado == 0) {
            estado = 1;
            //Passar para multiplicativa
            jButton_setarComposicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_multiplicacao.png"))); // NOI18N
            jButton_setarComposicao.setToolTipText("Multiplicação");
            jButton_setarComposicao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_multiplicacaoSelecionado.png"))); // NOI18N

            jButton_setTransformacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_dp.png"))); // NOI18N
            jButton_setTransformacao.setToolTipText("Divisão por partes");
            jButton_setTransformacao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_dpSelecionado.png"))); // NOI18N

            jButton_setComparacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_dc.png"))); // NOI18N
            jButton_setComparacao.setToolTipText("Divisão por cotas");
            jButton_setComparacao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_dcSelecionado.png"))); // NOI18N
            jRadioButtonMenuItem_multiplicativa.setSelected(true);
            
        }
    }

    public void modoAditivas() {
        if (estado == 1) {
            estado = 0;
            //Passar para aditiva
            jButton_setarComposicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_chave.png"))); // NOI18N
            jButton_setarComposicao.setToolTipText("composição de medidas");
            jButton_setarComposicao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_chaveSelecionado.png"))); // NOI18N

            jButton_setTransformacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_seta.png"))); // NOI18N
            jButton_setTransformacao.setToolTipText("Transformação de medidas");
            jButton_setTransformacao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_setaSelecionado.png"))); // NOI18N

            jButton_setComparacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_setaUp.png"))); // NOI18N
            jButton_setComparacao.setToolTipText("Comparação de medidas");
            jButton_setComparacao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_setaUpSelecionado.png"))); // NOI18N
            jRadioButtonMenuItem_aditiva.setSelected(true);


        }
    }

    public void setJMenuBar() {
        if (this.firstTime) {
            //this.menuBar = criarMenus();
            if (ehApplet()) {
                JApplet applet = (JApplet) this.component_pai;
                applet.setJMenuBar(menuBar);
            } else {
                JFrame frame = (JFrame) this.component_pai;
                frame.setJMenuBar(menuBar);
            }
            this.firstTime = false;
        } else {
            this.menuBar.setVisible(true);
        }
    }

    public void modoVisitante() {
        this.flagVisitante=true;
        jMenu_usuario.setEnabled(false);
        jMenuItem_editarDados.setEnabled(false);
        jMenuItem_exibirHistorico.setEnabled(false);
        jMenuItem_abrir.setEnabled(false);
        jMenuItem_salvar.setEnabled(false);
        this.jMenuItem_desfazer.setEnabled(false);
        this.jMenuItem_refazer.setEnabled(false);
    }

    public void modoUsuarioCadastrado() {
        this.flagVisitante=false;
        this.jMenuItem_desfazer.setEnabled(false);
        this.jMenuItem_refazer.setEnabled(false);
        jMenu_usuario.setEnabled(true);
        jMenuItem_editarDados.setEnabled(true);
        jMenuItem_exibirHistorico.setEnabled(true);
        jMenuItem_abrir.setEnabled(true);
        jMenuItem_salvar.setEnabled(false);
    }

    public void removeJMenuBar() {
        if(!this.firstTime)
        this.menuBar.setVisible(false);
    }

    private boolean ehApplet() {
        if (this.component_pai instanceof JApplet) {
            return true;
        }
        return false;
    }

    private JMenuBar criarMenus() {
        // criar menubar
        buttonGroup1 = new javax.swing.ButtonGroup();
        menuBar = new javax.swing.JMenuBar();
        jMenu_aplicativo = new javax.swing.JMenu();
        jMenuItem_novo = new javax.swing.JMenuItem();
        jMenuItem_recomecar = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenu_estruturas = new javax.swing.JMenu();
        jRadioButtonMenuItem_aditiva = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem_multiplicativa = new javax.swing.JRadioButtonMenuItem();

        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem_voltar = new javax.swing.JMenuItem();
        jMenu_sobre = new javax.swing.JMenu();
        jMenuItem_teoria = new javax.swing.JMenuItem();
        jMenuItem_gerard = new javax.swing.JMenuItem();

        jMenu_editar = new javax.swing.JMenu();
        jMenuItem_abrir = new javax.swing.JMenuItem();
        jMenuItem_salvar = new javax.swing.JMenuItem();
        jMenuItem_desfazer = new javax.swing.JMenuItem();
        jMenuItem_refazer = new javax.swing.JMenuItem();

        jMenu_usuario = new javax.swing.JMenu();
        jMenuItem_editarDados = new javax.swing.JMenuItem();
        jMenuItem_exibirHistorico = new javax.swing.JMenuItem();



        menuBar.setBackground(new java.awt.Color(255, 255, 255));
        menuBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        menuBar.setToolTipText("");
        menuBar.setPreferredSize(new java.awt.Dimension(56, 20));

        jMenu_aplicativo.setText("Aplicativo");
        jMenu_aplicativo.setToolTipText("aplicativo");
        jMenu_aplicativo.setForeground(new java.awt.Color(0, 102, 0));

        jMenuItem_novo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem_novo.setText("Novo");
        jMenuItem_novo.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_novo.setForeground(new java.awt.Color(0, 102, 0));
        jMenu_aplicativo.add(jMenuItem_novo);

        jMenuItem_recomecar.setText("Recomeçar");
        jMenuItem_recomecar.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_recomecar.setForeground(new java.awt.Color(0, 102, 0));

        jMenu_aplicativo.add(jMenuItem_recomecar);

        jMenu_aplicativo.add(new Separator());

        jMenu_estruturas.setText("Estruturas..");
        jMenu_estruturas.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_estruturas.setForeground(new java.awt.Color(0, 102, 0));

        jRadioButtonMenuItem_aditiva.setText("Aditivas");
        jRadioButtonMenuItem_aditiva.setToolTipText("selecionar estruturas adtivas");
        jRadioButtonMenuItem_aditiva.setSelected(true);
        jRadioButtonMenuItem_aditiva.setForeground(new java.awt.Color(0, 102, 0));
        jRadioButtonMenuItem_aditiva.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_estruturas.add(jRadioButtonMenuItem_aditiva);

        jRadioButtonMenuItem_multiplicativa.setText("Multiplicativas");
        jRadioButtonMenuItem_multiplicativa.setToolTipText("selecionar estruturas multiplicativas");
        jRadioButtonMenuItem_multiplicativa.setForeground(new java.awt.Color(0, 102, 0));
        jRadioButtonMenuItem_multiplicativa.setBackground(new java.awt.Color(255, 255, 255));
        jMenu_estruturas.add(jRadioButtonMenuItem_multiplicativa);

        jMenu_aplicativo.add(jMenu_estruturas);

        jMenu_aplicativo.add(new Separator());

        jMenuItem_voltar.setText("Voltar para tela login");
        jMenu_aplicativo.add(jMenuItem_voltar);
        jMenuItem_voltar.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_voltar.setForeground(new java.awt.Color(0, 102, 0));

        menuBar.add(jMenu_aplicativo);

        //menuEditar        
        jMenu_editar.setText("Editar");
        jMenu_editar.setToolTipText("Editar");
        jMenu_editar.setForeground(new java.awt.Color(0, 102, 0));

        jMenuItem_abrir.setText("Abrir");
        jMenuItem_abrir.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_abrir.setForeground(new java.awt.Color(0, 102, 0));
        jMenu_editar.add(jMenuItem_abrir);
        jMenuItem_salvar.setText("Salvar");
        jMenuItem_salvar.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_salvar.setForeground(new java.awt.Color(0, 102, 0));
        jMenu_editar.add(jMenuItem_salvar);

        jMenu_editar.add(new JSeparator());

        jMenuItem_desfazer.setText("Desfazer");

        jMenuItem_desfazer.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_desfazer.setForeground(new java.awt.Color(0, 102, 0));
        jMenuItem_desfazer.setEnabled(false);
        jMenu_editar.add(jMenuItem_desfazer);

        jMenuItem_refazer.setText("Refazer");
        jMenuItem_refazer.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_refazer.setForeground(new java.awt.Color(0, 102, 0));
        jMenuItem_refazer.setEnabled(false);
        jMenu_editar.add(jMenuItem_refazer);

        menuBar.add(this.jMenu_editar);

        //menu Usuário
        jMenu_usuario = new javax.swing.JMenu();
        jMenu_usuario.setText("Usuário");
        jMenu_usuario.setToolTipText("dados do usuário");
        jMenu_usuario.setForeground(new java.awt.Color(0, 102, 0));

        jMenuItem_editarDados.setText("Editar Cadastro");
        jMenuItem_editarDados.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_editarDados.setForeground(new java.awt.Color(0, 102, 0));
        jMenu_usuario.add(jMenuItem_editarDados);

        jMenuItem_exibirHistorico.setText("Exibir histórico");
        jMenuItem_exibirHistorico.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_exibirHistorico.setForeground(new java.awt.Color(0, 102, 0));
        jMenu_usuario.add(jMenuItem_exibirHistorico);

        menuBar.add(jMenu_usuario);

        //menu sobre
        jMenu_sobre.setText("Sobre");
        jMenu_sobre.setToolTipText("sobre");
        jMenu_sobre.setForeground(new java.awt.Color(0, 102, 0));
        jMenuItem_teoria.setText("Teoria");
        jMenuItem_teoria.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_teoria.setForeground(new java.awt.Color(0, 102, 0));
        jMenu_sobre.add(jMenuItem_teoria);

        jMenu_sobre.add(new JSeparator());
        jMenuItem_gerard.setText("GERARD 1.0");
        jMenuItem_gerard.setBackground(new java.awt.Color(255, 255, 255));
        jMenuItem_gerard.setForeground(new java.awt.Color(0, 102, 0));


        jMenu_sobre.add(jMenuItem_gerard);


        menuBar.add(jMenu_sobre);


        buttonGroup1.add(jRadioButtonMenuItem_aditiva);
        buttonGroup1.add(jRadioButtonMenuItem_multiplicativa);

        return this.menuBar;
    }

    public JPanelGrafico getJPanelGrafico() {
        return this.panelGrafico;
    }

    private void addBotoesListener() {
        this.jButton_setarComposicao.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.setDiagramaComposicaoMultiplicacao();
            }
        });
        this.jButton_setTransformacao.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.setDiagramaTransformacaoDPartes();
            }
        });
        this.jButton_setComparacao.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.setDiagramaComparacaoDCotas();
            }
        });
        this.jButton_proximoPasso.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.setProximoPasso();
            }
        });
        this.jButton_maisDica.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.maisDica();
            }
        });
        this.jRadioButtonMenuItem_aditiva.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                modoAditivas();
                controller.setAplicativoAditivas();
            }
        });
        this.jRadioButtonMenuItem_multiplicativa.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                modoMultiplicativas();
                controller.setAplicativoMultiplicativas();
            }
        });
        this.jMenuItem_voltar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.setTelaLogin();
            }
        });
        this.jMenuItem_exibirHistorico.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.exibirHistorico();
            }
        });
        this.jMenuItem_novo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.novoEstado();
            }
        });
        this.jMenuItem_recomecar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.recomecarEstado();
            }
        });
        this.jButton_desfazer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.desfazer();
            }
        });
        this.jButton_refazer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.refazer();
            }
        });
        this.jMenuItem_salvar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.salvar();
            }
        });
        this.jMenuItem_abrir.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.abrir();
            }
        });
         this.jMenuItem_editarDados.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.editarCadastro();
            }
        });
       this.jMenuItem_gerard.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.sobre();
            }

        });
        this.jMenuItem_teoria.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.teoria();
            }

        });
        this.jMenuItem_desfazer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.desfazer();
            }
        });
        this.jMenuItem_refazer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.refazer();
            }
        });
    }

    public void atualizarDesfazer(boolean desfazer) {
        this.jButton_desfazer.setEnabled(desfazer);
        this.jMenuItem_desfazer.setEnabled(desfazer);
    }

    public void atualizarRefazer(boolean refazer) {
        this.jButton_refazer.setEnabled(refazer);
        this.jMenuItem_refazer.setEnabled(refazer);
    }

    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel_botoesGraficos = new javax.swing.JPanel();
        jButton_setarComposicao = new javax.swing.JButton();
        jButton_setTransformacao = new javax.swing.JButton();
        jButton_setComparacao = new javax.swing.JButton();
        jPanel_botoesGraficos1 = new javax.swing.JPanel();
        jButton_desfazer = new javax.swing.JButton();
        jButton_refazer = new javax.swing.JButton();
        jPanelBotoes = new javax.swing.JPanel();
        jButton_proximoPasso = new javax.swing.JButton();
        jButton_maisDica = new javax.swing.JButton();
        panelGrafico = new JPanelGrafico(model, controller);

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setForeground(new java.awt.Color(0, 102, 0));
        jLabel1.setText("Questão");
        jLabel1.setToolTipText("Tente resolver a questão abaixo..");

        jPanel_botoesGraficos.setMaximumSize(new java.awt.Dimension(150, 32767));
        jPanel_botoesGraficos.setMinimumSize(new java.awt.Dimension(170, 69));
        jPanel_botoesGraficos.setPreferredSize(new java.awt.Dimension(150, 60));

        jButton_setarComposicao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_chave.png"))); // NOI18N
        jButton_setarComposicao.setToolTipText("composição de medidas");
        jButton_setarComposicao.setBorderPainted(false);
        jButton_setarComposicao.setPreferredSize(new java.awt.Dimension(52, 50));
        jButton_setarComposicao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_chaveSelecionado.png"))); // NOI18N
        jPanel_botoesGraficos.add(jButton_setarComposicao);

        jButton_setTransformacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_seta.png"))); // NOI18N
        jButton_setTransformacao.setToolTipText("Transformação de medidas");
        jButton_setTransformacao.setBorderPainted(false);
        jButton_setTransformacao.setPreferredSize(new java.awt.Dimension(52, 50));
        jButton_setTransformacao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_setaSelecionado.png"))); // NOI18N
        jPanel_botoesGraficos.add(jButton_setTransformacao);

        jButton_setComparacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_setaUp.png"))); // NOI18N
        jButton_setComparacao.setToolTipText("Comparação de medidas");
        jButton_setComparacao.setBorderPainted(false);
        jButton_setComparacao.setPreferredSize(new java.awt.Dimension(50, 50));
        jButton_setComparacao.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botao_setaUpSelecionado.png"))); // NOI18N
        jPanel_botoesGraficos.add(jButton_setComparacao);

        jPanel_botoesGraficos1.setMaximumSize(new java.awt.Dimension(110, 32767));
        jPanel_botoesGraficos1.setMinimumSize(new java.awt.Dimension(130, 69));
        jPanel_botoesGraficos1.setPreferredSize(new java.awt.Dimension(110, 60));

        jButton_desfazer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoDESFAZER.png"))); // NOI18N
        jButton_desfazer.setToolTipText("desfazer");
        jButton_desfazer.setBorderPainted(false);
        jButton_desfazer.setEnabled(false);
        jButton_desfazer.setPreferredSize(new java.awt.Dimension(52, 50));
        jButton_desfazer.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoDESFAZER_Selecionado.png"))); // NOI18N
        jPanel_botoesGraficos1.add(jButton_desfazer);

        jButton_refazer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoREFAZER.png"))); // NOI18N
        jButton_refazer.setToolTipText("refazer");
        jButton_refazer.setBorderPainted(false);
        jButton_refazer.setEnabled(false);
        jButton_refazer.setPreferredSize(new java.awt.Dimension(52, 50));
        jButton_refazer.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoREFAZER_Selecionado.png"))); // NOI18N
        jPanel_botoesGraficos1.add(jButton_refazer);

        jPanelBotoes.setLayout(new javax.swing.BoxLayout(jPanelBotoes, javax.swing.BoxLayout.LINE_AXIS));

        jButton_proximoPasso.setForeground(new java.awt.Color(0, 102, 0));
        jButton_proximoPasso.setText("Qual o próximo passo?");
        jPanelBotoes.add(jButton_proximoPasso);

        jButton_maisDica.setForeground(new java.awt.Color(0, 102, 0));
        jButton_maisDica.setText("Mais Dica");
        jPanelBotoes.add(jButton_maisDica);

        javax.swing.GroupLayout panelGraficoLayout = new javax.swing.GroupLayout(panelGrafico);
        panelGrafico.setLayout(panelGraficoLayout);
        panelGraficoLayout.setHorizontalGroup(
                panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 596, Short.MAX_VALUE));
        panelGraficoLayout.setVerticalGroup(
                panelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 232, Short.MAX_VALUE));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel_botoesGraficos1, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel_botoesGraficos, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE).addGap(18, 18, 18).addComponent(jPanelBotoes, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE).addContainerGap()).addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(panelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap())));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(jLabel1)).addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(jPanel_botoesGraficos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jPanel_botoesGraficos1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addComponent(jPanelBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(236, Short.MAX_VALUE)).addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGap(73, 73, 73).addComponent(panelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(10, 10, 10)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
    }
    private javax.swing.JButton jButton_desfazer;
    private javax.swing.JButton jButton_refazer;
    private javax.swing.JPanel jPanel_botoesGraficos1;
    private javax.swing.JButton jButton_proximoPasso;
    private javax.swing.JButton jButton_maisDica;
    private javax.swing.JButton jButton_setComparacao;
    private javax.swing.JButton jButton_setTransformacao;
    private javax.swing.JButton jButton_setarComposicao;
    private javax.swing.JLabel jLabel1;
    //private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelBotoes;
    // private javax.swing.JToolBar jToolBar1;
    private JPanelGrafico panelGrafico;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenu jMenu_aplicativo;
    private javax.swing.JMenu jMenu_usuario;
    private javax.swing.JMenu jMenu_sobre;
    private javax.swing.JMenu jMenu_estruturas;
    private javax.swing.JMenu jMenu_editar;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem jMenuItem_novo;
    private javax.swing.JMenuItem jMenuItem_editarDados;
    private javax.swing.JMenuItem jMenuItem_exibirHistorico;
    private javax.swing.JMenuItem jMenuItem_voltar;
    private javax.swing.JMenuItem jMenuItem_abrir;
    private javax.swing.JMenuItem jMenuItem_salvar;
    private javax.swing.JMenuItem jMenuItem_desfazer;
    private javax.swing.JMenuItem jMenuItem_refazer;
    private javax.swing.JMenuItem jMenuItem_recomecar;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem_aditiva;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem_multiplicativa;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPanel jPanel_botoesGraficos;

    public void atualizarBotaoSalvar(boolean salvar) {
       if(!this.flagVisitante)
        this.jMenuItem_salvar.setEnabled(salvar);
    }



    
}


