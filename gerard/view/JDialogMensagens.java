
package gerard.view;

import gerard.controller.ControllerInterface;
import gerard.model.GerardModelInterface;
import gerard.banco.ModelBanco;
import gerard.util.Aplicativo1.ControllerAplicativo1;
import gerard.util.Aplicativo1.Aplicativo1MensagemObserver;
import gerard.util.Aplicativo1.Nucleo;
import gerard.util.MensagensObserver;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author Kecia
 */
public class JDialogMensagens extends JDialog implements MensagensObserver, Aplicativo1MensagemObserver {

    private ControllerInterface controller;
    private GerardModelInterface model;
    private Component component_pai = null;
    private boolean flagBotaoOk = false;
    private boolean flagBotaoSIMeNAO = true;
    private boolean flagBotaoMAISeMENOS = false;
    private ModelBanco modelBanco;
    private Nucleo nucleo;

    public JDialogMensagens(Component componente_pai, ControllerInterface cont, GerardModelInterface model, ModelBanco modelBanco,ControllerAplicativo1 controllerAplicativo1) {
        this.setModal(true);
        this.component_pai = componente_pai;
        this.controller = cont;
        this.model = model;
        this.modelBanco = modelBanco;
        this.model.registerObserver((MensagensObserver) this);
        this.modelBanco.registerObserver((MensagensObserver) this);
        this.nucleo = controllerAplicativo1.getNucleo();
        this.nucleo.registerObserver((Aplicativo1MensagemObserver)this);
        
        
        initComponents();
        addBotoesListener();
       
    }
    public JDialogMensagens(Component componente_pai, ControllerInterface cont, GerardModelInterface model,ControllerAplicativo1 controllerAplicativo1) {
        this.setModal(true);
        this.component_pai = componente_pai;
        this.controller = cont;
        this.model = model;
        this.model.registerObserver((MensagensObserver) this);
        this.nucleo = controllerAplicativo1.getNucleo();
        this.nucleo.registerObserver((Aplicativo1MensagemObserver)this);
        

        initComponents();
        addBotoesListener();

    }

    public void mostrarMensagemOk() {
        String mensagem = this.model.getJOptionPaneTexto();
        this.setBotaoOk();
        this.setDialog(mensagem);
    }

    public void mensagemDigitarSinal() {
        String mensagem = this.model.getJOptionPaneTexto();
        this.setBotaoMAISeMENOS();
        this.setDialog(mensagem);
    }

    public void mostrarMensagemSimNao() {
        String mensagem = this.model.getJOptionPaneTexto();
        this.setBotaoSIMeNAO();
        this.setDialog(mensagem);
    }

  
    public void mostrarMensagemBanco(String mensagem) {
            setBotaoOk();
        
        setDialog(mensagem);
    }

    private void setDialog(String mensagem) {
        this.jLabel_mensagem.setText(mensagem);
        this.setLocationRelativeTo(component_pai);
        this.setVisible(true);
    }

    private void setBotaoOk() {
        if (this.flagBotaoOk == false) {
            jButton_ok_nao_menos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoOK.png"))); // NOI18N
            jButton_ok_nao_menos.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoOK_selecionado.png"))); // NOI18N
            jButton_sim_mais.setVisible(false);
            this.flagBotaoOk = true;
            this.flagBotaoMAISeMENOS = false;
            this.flagBotaoSIMeNAO = false;
        }
    }

    private void setBotaoSIMeNAO() {
        if (this.flagBotaoSIMeNAO == false) {
            //botao SIM
            jButton_sim_mais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoSIM.png"))); // NOI18N
            jButton_sim_mais.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoSIM_Selecionado.png"))); // NOI18N
            jButton_sim_mais.setVisible(true);
            //botao NAO
            jButton_ok_nao_menos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoNAO.png"))); // NOI18N
            jButton_ok_nao_menos.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoNAO_Selecionado.png"))); // NOI18N
            this.flagBotaoOk = false;
            this.flagBotaoMAISeMENOS = false;
            this.flagBotaoSIMeNAO = true;
        }
    }

    private void setBotaoMAISeMENOS() {
        if (this.flagBotaoMAISeMENOS == false) {
            //botao MAIS
            jButton_sim_mais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoMAIS.png"))); // NOI18N
            jButton_sim_mais.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoMAIS_Selecionado.png"))); // NOI18N
            jButton_sim_mais.setVisible(true);
            //botao mMENOS
            jButton_ok_nao_menos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoMENOS.png"))); // NOI18N
            jButton_ok_nao_menos.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoMENOS_Selecionado.png"))); // NOI18N
            this.flagBotaoOk = false;
            this.flagBotaoMAISeMENOS = true;
            this.flagBotaoSIMeNAO = false;
        }
    }

    private void addBotoesListener() {
         this.jButton_sim_mais.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
                if (flagBotaoSIMeNAO) {
                    controller.verificarSIMeNAO("sim");
                } else if (flagBotaoMAISeMENOS) {
                    controller.verificarSinal("+");
                }


            }
        });
        this.jButton_ok_nao_menos.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                           dispose();
                if (flagBotaoSIMeNAO) {
                    controller.verificarSIMeNAO("nao");
                } else if (flagBotaoMAISeMENOS) {
                    controller.verificarSinal("-");
                }

            }
        });
    }



    public void updateMensagem(String mensagem) {
        this.setBotaoOk();
        this.setDialog(mensagem);
    }



    private class TelaFundo extends JPanel {

        private Image img = null;

        public TelaFundo() {
            java.net.URL caminho = getClass().getResource("/gerard/imagens/telaLogin.png");
            try {
                img = javax.imageio.ImageIO.read(caminho);
            } catch (IOException ex) {
                //Logger.getLogger(JPanelLogin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void paintComponent(Graphics g) {
            if (img != null && component_pai != null) {
                int width = component_pai.getWidth();
                int height = component_pai.getHeight();
                g.drawImage(img, 0, 0, width, height, this);

            }

        }
    }

    private void initComponents() {

        jPanel_objetos = new TelaFundo();
        jLabel_mensagem = new javax.swing.JLabel();
        jButton_ok_nao_menos = new javax.swing.JButton();
        jButton_sim_mais = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel_mensagem.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_mensagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jButton_ok_nao_menos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoNAO.png"))); // NOI18N
        jButton_ok_nao_menos.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoNAO_Selecionado.png"))); // NOI18N
        jButton_ok_nao_menos.setBorderPainted(false);
        jButton_ok_nao_menos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_ok_nao_menos.setPreferredSize(new java.awt.Dimension(52, 50));


        jButton_sim_mais.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoSIM.png"))); // NOI18N
        jButton_sim_mais.setBorderPainted(false);
        jButton_sim_mais.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_sim_mais.setPreferredSize(new java.awt.Dimension(52, 50));
        jButton_sim_mais.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoSIM_Selecionado.png"))); // NOI18N

        javax.swing.GroupLayout jPanel_objetosLayout = new javax.swing.GroupLayout(jPanel_objetos);
        jPanel_objetos.setLayout(jPanel_objetosLayout);
        jPanel_objetosLayout.setHorizontalGroup(
                jPanel_objetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel_objetosLayout.createSequentialGroup().addContainerGap().addGroup(jPanel_objetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_objetosLayout.createSequentialGroup().addComponent(jButton_sim_mais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jButton_ok_nao_menos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(jLabel_mensagem, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)).addContainerGap()));
        jPanel_objetosLayout.setVerticalGroup(
                jPanel_objetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_objetosLayout.createSequentialGroup().addContainerGap().addComponent(jLabel_mensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE).addGroup(jPanel_objetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jButton_ok_nao_menos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jButton_sim_mais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel_objetos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel_objetos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        this.setTitle("Olá");

        pack();
    }

    private javax.swing.JButton jButton_ok_nao_menos;
    private javax.swing.JButton jButton_sim_mais;
    private javax.swing.JLabel jLabel_mensagem;
    private javax.swing.JPanel jPanel_objetos;
}
