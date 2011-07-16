package gerard.view;

import gerard.banco.TelaBancoObserver;
import gerard.controller.ControllerInterface;
import gerard.banco.ModelBanco;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JPanel;

public class JPanelLogin extends JPanel implements TelaBancoObserver{

    private Component component_pai;
    private ControllerInterface controller;
    private ModelBanco modelBanco;
    private boolean botaoCadastrar=true;
 public JPanelLogin(Component pai, ControllerInterface cont) {
         this.component_pai = pai;
        this.controller = cont;
        initComponents();
         this.jButton_aplicativo1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.setAplicativo1();
            }
        });

        this.jButton_aplicativo2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.setAplicativoAditivas();
            }
        });
 }

    public JPanelLogin(Component pai, ControllerInterface cont,ModelBanco modelBanco) {
        this.component_pai = pai;
        this.controller = cont;
        this.modelBanco=modelBanco;
        this.modelBanco.registerObserver((TelaBancoObserver)this);
        initComponents();

        this.jButton_aplicativo1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                 controller.setAplicativo1();
            }
        });

        this.jButton_aplicativo2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                controller.setAplicativoAditivas();
            }
        });
      addBotoesListener();
    }
   public void addBotoesListener(){
         this.jButton_ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //verficar se o login e a senha existem
                controller.conectarUsuario(jTextField_login.getText(),jPasswordField_senha.getPassword());
            }
        });

        this.jButton_cadastrar_sair.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(botaoCadastrar==true){
                    //cadastrar usuario
                    controller.cadastrarUsuario();
                }
                else{
                    //logout
                    controller.desconectarUsuario();
                }
            }
        });
   }
    public void modoApplet(){
        this.jButton_cadastrar_sair.setEnabled(false);
        this.jButton_ok.setEnabled(false);
        this.jTextField_login.setEnabled(false);
        this.jPasswordField_senha.setEnabled(false);
        this.jLabel_login.setEnabled(false);
        this.jLabel_senha.setEnabled(false);
        this.jButton_aplicativo1.setEnabled(false);

    }
    public void updateTela(String texto) {
      this.setLabelMensagem1(texto);
       this.setBotaoSairVisible();
    }
    public void setLabelMensagem1(String texto) {
        this.jLabel_mensagem1.setText(texto);
    }
  
    public void setBotaoSairVisible() {
        this.botaoCadastrar=false;
        this.jButton_ok.setVisible(false);
        jButton_cadastrar_sair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoSAIR.png"))); // NOI18N
        jButton_cadastrar_sair.setToolTipText("Desconectar");
        jButton_cadastrar_sair.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoSAIR_selecionado.png"))); // NOI18N
        this.jTextField_login.setEnabled(false);
        this.jPasswordField_senha.setEnabled(false);
    }

    public void setBotaoCadastrarVisible() {
        this.botaoCadastrar=true;
        this.jButton_ok.setVisible(true);
        jButton_cadastrar_sair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCadastrar.png"))); // NOI18N
        jButton_cadastrar_sair.setToolTipText("Criar Login");
        jButton_cadastrar_sair.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCadastrar_selecionado.png"))); // NOI18N
        this.jTextField_login.setEnabled(true);
        this.jTextField_login.setText("");
        this.jPasswordField_senha.setEnabled(true);
        this.jPasswordField_senha.setText("");
    }

    private void initComponents() {

        jLabel_senha = new javax.swing.JLabel();
        jLabel_login = new javax.swing.JLabel();
        jTextField_login = new javax.swing.JTextField();
        jPasswordField_senha = new javax.swing.JPasswordField();
        jButton_ok = new javax.swing.JButton();
        jButton_cadastrar_sair = new javax.swing.JButton();
        jLabel_mensagem1 = new javax.swing.JLabel();
        jLabel_mensagem2 = new javax.swing.JLabel();
        jButton_aplicativo1 = new javax.swing.JButton();
        jButton_aplicativo2 = new javax.swing.JButton();
        jLabel_aplicativo1 = new javax.swing.JLabel();
        jLabel_aplicativo2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        jLabel_senha.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14));
        jLabel_senha.setText("Senha: ");

        jLabel_login.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14));
        jLabel_login.setText("Login (CPF): ");

        jPasswordField_senha.setText("");

        jButton_ok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoOK.png"))); // NOI18N
        jButton_ok.setToolTipText("Fazer login");
        jButton_ok.setBorderPainted(false);
        jButton_ok.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_ok.setPreferredSize(new java.awt.Dimension(52, 50));
        jButton_ok.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoOK_selecionado.png"))); // NOI18N

        jButton_cadastrar_sair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCadastrar.png"))); // NOI18N
        jButton_cadastrar_sair.setToolTipText("Criar Login");
        jButton_cadastrar_sair.setBorderPainted(false);
        jButton_cadastrar_sair.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_cadastrar_sair.setPreferredSize(new java.awt.Dimension(104, 50));
        jButton_cadastrar_sair.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCadastrar_selecionado.png"))); // NOI18N

        jLabel_mensagem1.setFont(new java.awt.Font("Lucida Calligraphy", 0, 18));
        jLabel_mensagem1.setForeground(new java.awt.Color(0, 102, 0));
        jLabel_mensagem1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_mensagem1.setText("Olá! Seja bem vindo! ");

        jLabel_mensagem2.setFont(new java.awt.Font("Lucida Calligraphy", 0, 18));
        jLabel_mensagem2.setForeground(new java.awt.Color(0, 102, 0));
        jLabel_mensagem2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_mensagem2.setText("Escolha o aplicativo a ser utilizado ao lado!");

        jButton_aplicativo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoMultiplicativas_pretoBranco.png"))); // NOI18N
        jButton_aplicativo1.setToolTipText("Compor a situação-problema a partir do diagrama..");
        jButton_aplicativo1.setBorderPainted(false);
        jButton_aplicativo1.setPreferredSize(new java.awt.Dimension(355, 227));
        jButton_aplicativo1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoMultiplicativas.png"))); // NOI18N

        jButton_aplicativo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoAplicativo2_.png"))); // NOI18N
        jButton_aplicativo2.setToolTipText("Compor o diagrama a partir da situação-problema..");
        jButton_aplicativo2.setBorderPainted(false);
        jButton_aplicativo2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoAplicativo2.png"))); // NOI18N

        jLabel_aplicativo1.setFont(new java.awt.Font("Lucida Calligraphy", 0, 12));
        jLabel_aplicativo1.setForeground(new java.awt.Color(0, 102, 0));
        jLabel_aplicativo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_aplicativo1.setText("Aplicativo 1");

        jLabel_aplicativo2.setFont(new java.awt.Font("Lucida Calligraphy", 0, 12));
        jLabel_aplicativo2.setForeground(new java.awt.Color(0, 102, 0));
        jLabel_aplicativo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_aplicativo2.setText("Aplicativo 2");

        jSeparator1.setForeground(new java.awt.Color(0, 102, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel_mensagem2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .addComponent(jLabel_mensagem1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton_ok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_cadastrar_sair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel_senha)
                                    .addComponent(jLabel_login))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPasswordField_senha)
                                    .addComponent(jTextField_login, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel_aplicativo1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                    .addComponent(jLabel_aplicativo2, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                    .addComponent(jButton_aplicativo2, javax.swing.GroupLayout.PREFERRED_SIZE, 313, Short.MAX_VALUE)
                    .addComponent(jButton_aplicativo1, 0, 313, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
                    .addGap(402, 402, 402)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(204, 204, 204)
                                .addComponent(jLabel_aplicativo1)
                                .addGap(26, 26, 26)
                                .addComponent(jButton_aplicativo2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel_aplicativo2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel_mensagem1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel_mensagem2)
                                .addGap(170, 170, 170)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel_login)
                                    .addComponent(jTextField_login, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jPasswordField_senha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel_senha))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton_cadastrar_sair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton_ok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(98, 98, 98))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton_aplicativo1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(259, 259, 259)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(281, Short.MAX_VALUE)))
        );
    }// </editor-fold>





    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = component_pai.getWidth();
        int height = component_pai.getHeight();

        java.net.URL caminho = getClass().getResource("/gerard/imagens/telaLogin.png");

        Image img = null;
        try {
            img = javax.imageio.ImageIO.read(caminho);
        } catch (IOException ex) {
            // Logger.getLogger(JPanelLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (img != null) {
            g.drawImage(img, 0, 0, width, height, this);
        }
    }
    // Variables declaration - do not modify
    private javax.swing.JButton jButton_aplicativo1;
    private javax.swing.JButton jButton_aplicativo2;
    private javax.swing.JButton jButton_cadastrar_sair;
    private javax.swing.JButton jButton_ok;
    private javax.swing.JLabel jLabel_aplicativo1;
    private javax.swing.JLabel jLabel_aplicativo2;
    private javax.swing.JLabel jLabel_login;
    private javax.swing.JLabel jLabel_mensagem1;
    private javax.swing.JLabel jLabel_mensagem2;
    private javax.swing.JLabel jLabel_senha;
    private javax.swing.JPasswordField jPasswordField_senha;
    private javax.swing.JTextField jTextField_login;
    private javax.swing.JSeparator jSeparator1;





    // End of variables declaration
}
